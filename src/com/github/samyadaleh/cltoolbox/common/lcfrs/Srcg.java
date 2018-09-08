package com.github.samyadaleh.cltoolbox.common.lcfrs;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.Binarization;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.EmptyProductions;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.Order;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.UselessRules;
import com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils;
import com.github.samyadaleh.cltoolbox.common.parser.Token;
import com.github.samyadaleh.cltoolbox.common.parser.TokenReader;

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
    TokenReader reader = new TokenReader(in, specialChars);
    Set<String> validCategories = new HashSet<>();
    validCategories.add("N");
    validCategories.add("T");
    validCategories.add("V");
    validCategories.add("S");
    validCategories.add("P");
    validCategories.add("G");
    List<String> category = new ArrayList<>();
    String lhsNT = null;
    StringBuilder lhs = new StringBuilder();
    String currentRhsNt = null;
    StringBuilder rhs = new StringBuilder();
    List<String> symbols = new ArrayList<>();
    Token token;
    while ((token = reader.getNextToken()) != null) {
      String tokenString = token.getString();
      switch (category.size()) {
      case 0:
        handleMainCategory(validCategories, category, token);
        break;
      case 1:
        addSymbolToCategory(category, token, "=");
        break;
      case 2:
        category = addStartsymbolOrAddCategory(category, token);
        break;
      case 3:
        CollectSetContents collectSetContents =
            new CollectSetContents(category, lhsNT, lhs, symbols, token,
                tokenString).invoke();
        category = collectSetContents.getCategory();
        lhsNT = collectSetContents.getLhsNT();
        symbols = collectSetContents.getSymbols();
        break;
      case 4:
        if (category.get(3).equals("-")) {
          GrammarParserUtils.addSymbolToCategory(category, token, ">");
        } else {
          lhs.append(" ").append(tokenString);
          if (tokenString.equals(")")) {
            category.remove(3);
          }
        }
        break;
      case 5:
        CollectOuterRhsSymbols collectOuterRhsSymbols =
            new CollectOuterRhsSymbols(category, lhsNT, lhs, currentRhsNt, rhs,
                token, tokenString).invoke();
        category = collectOuterRhsSymbols.getCategory();
        lhsNT = collectOuterRhsSymbols.getLhsNT();
        lhs = collectOuterRhsSymbols.getLhs();
        currentRhsNt = collectOuterRhsSymbols.getCurrentRhsNt();
        rhs = collectOuterRhsSymbols.getRhs();
        break;
      default:
        switch (tokenString) {
        case ")":
          category.remove(5);
          currentRhsNt = null;
        default:
          rhs.append(" ").append(tokenString);
          break;
        }
        break;
      }
    }
  }

  private String findLhsNTOrAddCategory(List<String> category, String lhs,
      Token token) throws ParseException {
    String tokenString = token.getString();
    if (lhs == null || !tokenString.equals("(")) {
      lhs = tokenString;
    } else if (tokenString.equals("(")) {
      category.add(tokenString);
    } else {
      throw new ParseException("Unexpected situation with token " + tokenString,
          token.getLineNumber());
    }
    return lhs;
  }

  private String findRhsNTOrAddCategory(List<String> category, String rhsnt,
      Token token) throws ParseException {
    String tokenString = token.getString();
    if (rhsnt == null || !tokenString.equals("(")) {
      rhsnt = tokenString;
    } else if (tokenString.equals("(")) {
      category.add(tokenString);
    } else {
      throw new ParseException("Unexpected situation with token " + tokenString,
          token.getLineNumber());
    }
    return rhsnt;
  }

  private List<String> addStartsymbolOrAddCategory(List<String> category,
      Token token) throws ParseException {
    String tokenString = token.getString();
    switch (category.get(0)) {
    case "P":
    case "N":
    case "T":
    case "V":
      addSymbolToCategory(category, token, "{");
      break;
    case "S":
      if (this.getStartSymbol() != null) {
        throw new ParseException(
            "Startsymbol was declared twice: " + tokenString,
            token.getLineNumber());
      }
      this.setStartSymbol(tokenString);
      category = new ArrayList<>();
      break;
    case "G":
      if (tokenString.equals(">")) {
        category = new ArrayList<>();
      }
    default:
    }
    return category;
  }

  private void handleMainCategory(Set<String> validCategories,
      List<String> category, Token token) throws ParseException {
    String tokenString = token.getString();
    if (validCategories.contains(tokenString)) {
      if ((tokenString.equals("N") && this.getNonterminals() != null) || (
          tokenString.equals("T") && this.getTerminals() != null) || (
          tokenString.equals("S") && this.getStartSymbol() != null) || (
          tokenString.equals("V") && this.getVariables() != null)) {
        throw new ParseException("Category " + token + " is already set.",
            token.getLineNumber());
      }
      category.add(tokenString);
    } else {
      throw new ParseException("Unknown declaration symbol " + token,
          token.getLineNumber());
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

  private class CollectOuterRhsSymbols {
    private List<String> category;
    private String lhsNT;
    private StringBuilder lhs;
    private String currentRhsNt;
    private StringBuilder rhs;
    private Token token;
    private String tokenString;

    CollectOuterRhsSymbols(List<String> category, String lhsNT,
        StringBuilder lhs, String currentRhsNt, StringBuilder rhs, Token token,
        String tokenString) {
      this.category = category;
      this.lhsNT = lhsNT;
      this.lhs = lhs;
      this.currentRhsNt = currentRhsNt;
      this.rhs = rhs;
      this.token = token;
      this.tokenString = tokenString;
    }

    List<String> getCategory() {
      return category;
    }

    String getLhsNT() {
      return lhsNT;
    }

    public StringBuilder getLhs() {
      return lhs;
    }

    String getCurrentRhsNt() {
      return currentRhsNt;
    }

    public StringBuilder getRhs() {
      return rhs;
    }

    CollectOuterRhsSymbols invoke() throws ParseException {
      switch (tokenString) {
      case ",":
        Srcg.this.addClause(lhs.toString(), rhs.toString());
        currentRhsNt = null;
        rhs = new StringBuilder();
        lhsNT = null;
        lhs = new StringBuilder();
        category.remove(4);
        category.remove(3);
        break;
      case "|":
        Srcg.this.addClause(lhs.toString(), rhs.toString());
        currentRhsNt = null;
        rhs = new StringBuilder();
        break;
      case "}":
        Srcg.this.addClause(lhs.toString(), rhs.toString());
        category = new ArrayList<>();
        currentRhsNt = null;
        rhs = new StringBuilder();
        lhsNT = null;
        lhs = new StringBuilder();
        break;
      default:
        currentRhsNt = findRhsNTOrAddCategory(category, currentRhsNt, token);
        if (tokenString.equals("(")) {
          rhs.append(currentRhsNt).append("(");
        }
      }
      return this;
    }
  }

  private class CollectSetContents {
    private List<String> category;
    private String lhsNT;
    private StringBuilder lhs;
    private List<String> symbols;
    private Token token;
    private String tokenString;

    CollectSetContents(List<String> category, String lhsNT, StringBuilder lhs,
        List<String> symbols, Token token, String tokenString) {
      this.category = category;
      this.lhsNT = lhsNT;
      this.lhs = lhs;
      this.symbols = symbols;
      this.token = token;
      this.tokenString = tokenString;
    }

    List<String> getCategory() {
      return category;
    }

    String getLhsNT() {
      return lhsNT;
    }

    public List<String> getSymbols() {
      return symbols;
    }

    CollectSetContents invoke() throws ParseException {
      switch (category.get(0)) {
      case "N":
        switch (tokenString) {
        case "}":
          Srcg.this.nonterminals = symbols.toArray(new String[0]);
          category = new ArrayList<>();
          symbols = new ArrayList<>();
          break;
        case ",":
          break;
        default:
          symbols.add(tokenString);
        }
        break;
      case "T":
        switch (tokenString) {
        case "}":
          Srcg.this.terminals = symbols.toArray(new String[0]);
          category = new ArrayList<>();
          symbols = new ArrayList<>();
          break;
        case ",":
          break;
        default:
          symbols.add(tokenString);
        }
        break;
      case "V":
        switch (tokenString) {
        case "}":
          Srcg.this.variables = symbols.toArray(new String[0]);
          category = new ArrayList<>();
          symbols = new ArrayList<>();
          break;
        case ",":
          break;
        default:
          symbols.add(tokenString);
        }
        break;
      case "P":
        if (lhs.toString().endsWith(")")) {
          GrammarParserUtils.addSymbolToCategory(category, token, "-");
        } else {
          lhsNT = findLhsNTOrAddCategory(category, lhsNT, token);
          if (tokenString.equals("(")) {
            lhs.append(lhsNT).append("(");
          }
        }
        break;
      default:
        if (lhsNT != null) {
          throw new ParseException("Expected ( but found " + tokenString,
              token.getLineNumber());
        }
        if (!tokenString.equals(",")) {
          lhsNT = tokenString;
        }
      }
      return this;
    }
  }
}
