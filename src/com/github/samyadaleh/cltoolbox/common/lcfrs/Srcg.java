package com.github.samyadaleh.cltoolbox.common.lcfrs;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.Binarization;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.EmptyProductions;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.Order;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.UselessRules;
import com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils;

import static com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils.addSymbolToCategory;

/**
 * Representation of a sRCG - simple Range Concatenation Grammar.
 */
public class Srcg {
  private String[] nonterminals;
  private String[] terminals;
  private String[] variables;
  private String startSymbol;
  private final List<Clause> clauses = new ArrayList<>();

  /**
   * Converts a CFG to a sRCG with dimension = 1.
   */
  public Srcg(Cfg cfg) throws ParseException {
    this.nonterminals = cfg.getNonterminals();
    this.terminals = cfg.getTerminals();
    this.startSymbol = cfg.getStartSymbol();
    ArrayList<String> newVariables = new ArrayList<>();
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      StringBuilder lhs = new StringBuilder();
      StringBuilder rhs = new StringBuilder();
      int i = 0;
      lhs.append(rule.getLhs()).append("(");
      for (String rhsSym : rule.getRhs()) {
        if (rhsSym.length() > 0) {
          if (cfg.terminalsContain(rhsSym)) {
            lhs.append(" ").append(rhsSym);
          } else {
            i++;
            String newVar = "X" + String.valueOf(i);
            while (cfg.nonterminalsContain(newVar)) {
              i++;
              newVar = "X" + String.valueOf(i);
            }
            if (!newVariables.contains(newVar)) {
              newVariables.add(newVar);
            }
            lhs.append(" ").append(newVar);
            rhs.append(rhsSym).append("(").append(newVar).append(")");
          }
        } else {
          rhs.append("ε");
          lhs.append("ε");
        }
      }
      lhs.append(")");
      this.addClause(lhs.toString(), rhs.toString());
    }
    this.variables = newVariables.toArray(new String[0]);
  }

  public Srcg() {
    super();
  }

  public Srcg(BufferedReader in) throws IOException, ParseException {
    Character[] specialChars =
        new Character[] {'-', '>', '{', '}', ',', '|', '=', '<', '(', ')'};
    Set<String> validCategories = new HashSet<>();
    validCategories.add("N");
    validCategories.add("T");
    validCategories.add("V");
    validCategories.add("S");
    validCategories.add("P");
    validCategories.add("G");
    List<String> tokens = new ArrayList<>();
    String line = null;
    List<String> category = new ArrayList<>();
    int lineNumber = 0;
    String lhsNT = null;
    StringBuilder lhs = new StringBuilder();
    String currentRhsNt = null;
    StringBuilder rhs = new StringBuilder();
    List<String> symbols = new ArrayList<>();
    while (tokens.size() > 0 || (line = in.readLine()) != null) {
      if (tokens.size() == 0) {
        tokens = ArrayUtils.tokenize(line, specialChars);
        lineNumber++;
      }
      String token = tokens.get(0);
      tokens.remove(0);
      switch (category.size()) {
      case 0:
        handleMainCategory(validCategories, category, lineNumber, token);
        break;
      case 1:
        addSymbolToCategory(category, lineNumber, token, "=");
        break;
      case 2:
        category = addStartsymbolOrAddCategory(category, lineNumber, token);
        break;
      case 3:
        switch (category.get(0)) {
        case "N":
          switch (token) {
          case "}":
            this.nonterminals = symbols.toArray(new String[0]);
            category = new ArrayList<>();
            symbols = new ArrayList<>();
            break;
          case ",":
            break;
          default:
            symbols.add(token);
          }
          break;
        case "T":
          switch (token) {
          case "}":
            this.terminals = symbols.toArray(new String[0]);
            category = new ArrayList<>();
            symbols = new ArrayList<>();
            break;
          case ",":
            break;
          default:
            symbols.add(token);
          }
          break;
        case "V":
          switch (token) {
          case "}":
            this.variables = symbols.toArray(new String[0]);
            category = new ArrayList<>();
            symbols = new ArrayList<>();
            break;
          case ",":
            break;
          default:
            symbols.add(token);
          }
          break;
        case "P":
          if (lhs.toString().endsWith(")")) {
            GrammarParserUtils
                .addSymbolToCategory(category, lineNumber, token, "-");
          } else {
            lhsNT = findLhsNTOrAddCategory(category, lineNumber, lhsNT, token);
            if (token.equals("(")) {
              lhs.append(lhsNT).append("(");
            }
          }
          break;
        default:
          if (lhsNT != null) {
            throw new ParseException("Expected ( but found " + token,
                lineNumber);
          }
          if (!token.equals(",")) {
            lhsNT = token;
          }
        }
        break;
      case 4:
        if (category.get(3).equals("-")) {
          GrammarParserUtils
              .addSymbolToCategory(category, lineNumber, token, ">");
        } else {
          lhs.append(" ").append(token);
          if (token.equals(")")) {
            category.remove(3);
          }
        }
        break;
      case 5:
        switch (token) {
        case ",":
          this.addClause(lhs.toString(), rhs.toString());
          currentRhsNt = null;
          rhs = new StringBuilder();
          lhsNT = null;
          lhs = new StringBuilder();
          category.remove(4);
          category.remove(3);
          break;
        case "|":
          this.addClause(lhs.toString(), rhs.toString());
          currentRhsNt = null;
          rhs = new StringBuilder();
          break;
        case "}":
          this.addClause(lhs.toString(), rhs.toString());
          category = new ArrayList<>();
          currentRhsNt = null;
          rhs = new StringBuilder();
          lhsNT = null;
          lhs = new StringBuilder();
          break;
        default:
          currentRhsNt =
              findRhsNTOrAddCategory(category, lineNumber, currentRhsNt, token);
          if (token.equals("(")) {
            rhs.append(currentRhsNt).append("(");
          }
        }
        break;
      default:
        switch (token) {
        case ")":
          category.remove(5);
          currentRhsNt = null;
        default:
          rhs.append(" ").append(token);
          break;
        }
        break;
      }
    }
  }

  private String findLhsNTOrAddCategory(List<String> category, int lineNumber,
      String lhs, String token) throws ParseException {
    if (lhs == null || !token.equals("(")) {
      lhs = token;
    } else if (token.equals("(")) {
      category.add(token);
    } else {
      throw new ParseException("Unexpected situation with token " + token,
          lineNumber);
    }
    return lhs;
  }

  private String findRhsNTOrAddCategory(List<String> category, int lineNumber,
      String rhsnt, String token) throws ParseException {
    if (rhsnt == null || !token.equals("(")) {
      rhsnt = token;
    } else if (token.equals("(")) {
      category.add(token);
    } else {
      throw new ParseException("Unexpected situation with token " + token,
          lineNumber);
    }
    return rhsnt;
  }

  private List<String> addStartsymbolOrAddCategory(List<String> category,
      int lineNumber, String token) throws ParseException {
    switch (category.get(0)) {
    case "P":
    case "N":
    case "T":
    case "V":
      addSymbolToCategory(category, lineNumber, token, "{");
      break;
    case "S":
      if (this.getStartSymbol() != null) {
        throw new ParseException("Startsymbol was declared twice: " + token,
            lineNumber);
      }
      this.setStartSymbol(token);
      category = new ArrayList<>();
      break;
    case "G":
      if (token.equals(">")) {
        category = new ArrayList<>();
      }
    default:
    }
    return category;
  }

  private void handleMainCategory(Set<String> validCategories,
      List<String> category, int lineNumber, String token)
      throws ParseException {
    if (validCategories.contains(token)) {
      if ((token.equals("N") && this.getNonterminals() != null) || (
          token.equals("T") && this.getTerminals() != null) || (
          token.equals("S") && this.getStartSymbol() != null) || (
          token.equals("V") && this.getVariables() != null)) {
        throw new ParseException("Category " + token + " is already set.",
            lineNumber);
      }
      category.add(token);
    } else {
      throw new ParseException("Unknown declaration symbol " + token,
          lineNumber);
    }
  }

  public void setNonterminals(String[] nonterminals) {
    this.nonterminals = nonterminals;
  }

  public void setTerminals(String[] terminals) {
    this.terminals = terminals;
  }

  public void setVariables(String[] variables) {
    this.variables = variables;
  }

  public void setStartSymbol(String startSymbol) {
    this.startSymbol = startSymbol;
  }

  private void addClause(String lhs, String rhs) throws ParseException {
    this.clauses.add(new Clause(lhs, rhs));
  }

  public void addClause(String string) throws ParseException {
    this.clauses.add(new Clause(string));
  }

  public void addClause(Clause newClause) {
    this.clauses.add(newClause);
  }

  @Override public String toString() {
    StringBuilder repr = new StringBuilder();
    repr.append("G = <N, T, V, P, S>\n");
    repr.append("N = {").append(String.join(", ", nonterminals)).append("}\n");
    repr.append("T = {").append(String.join(", ", terminals)).append("}\n");
    repr.append("V = {").append(String.join(", ", variables)).append("}\n");
    repr.append("P = {");
    for (int i = 0; i < clauses.size(); i++) {
      if (i > 0) {
        repr.append(", ");
      }
      repr.append(clauses.get(i).toString());
    }
    repr.append("}\n");
    repr.append("S = ").append(startSymbol).append("\n");
    return repr.toString();
  }

  public List<Clause> getClauses() {
    return this.clauses;
  }

  public String getStartSymbol() {
    return this.startSymbol;
  }

  public String[] getVariables() {
    return this.variables;
  }

  public String[] getTerminals() {
    return this.terminals;
  }

  public String[] getNonterminals() {
    return this.nonterminals;
  }

  /**
   * Returns true if each rhs contains at most two predicates.
   */
  public boolean isBinarized() {
    return Binarization.isBinarized(this);
  }

  /**
   * Returns true if there is at least one clause that contains the empty string
   * in one of its lhs arguments, except if it is the start symbol in which case
   * it must not occur on any rhs.
   */
  public boolean hasEpsilonProductions() {
    return EmptyProductions.hasEpsilonProductions(this);
  }

  /**
   * Returns true if all variables in rhs predicates appear in the same order as
   * in the lhs predicate.
   */
  public boolean isOrdered() {
    return Order.isOrdered(this);
  }

  /**
   * Returns an equivalent sRCG where the variables are ordered in each rule for
   * each predicate. Might leave useless nonterminals behind.
   */
  public Srcg getOrderedSrcg() throws ParseException {
    return Order.getOrderedSrcg(this);
  }

  /**
   * Returns true if mayNt is in set of nonterminals.
   */
  public boolean nonTerminalsContain(String mayNt) {
    for (String nt : this.nonterminals) {
      if (mayNt.equals(nt)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return an equivalent sRCG without epsilon as any lhs argument.
   */
  public Srcg getSrcgWithoutEmptyProductions() throws ParseException {
    return EmptyProductions.getSrcgWithoutEmptyProductions(this);
  }

  /**
   * Returns true if mayT is one of the terminals.
   */
  public boolean terminalsContain(String mayT) {
    for (String terminal : this.terminals) {
      if (terminal.equals(mayT)) {
        return true;
      }
    }
    return false;
  }

  public Srcg getBinarizedSrcg() throws ParseException {
    return Binarization.getBinarizedSrcg(this);
  }

  /**
   * Returns a sRCG equivalent to this one but with only useful rules.
   */
  public Srcg getSrcgWithoutUselessRules() {
    return UselessRules.getSrcgWithoutUselessRules(this);
  }
}
