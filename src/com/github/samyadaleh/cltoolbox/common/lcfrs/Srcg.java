package com.github.samyadaleh.cltoolbox.common.lcfrs;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.samyadaleh.cltoolbox.common.AbstractNTSGrammar;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.Binarization;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.EmptyProductions;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.Order;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.UselessRules;
import com.github.samyadaleh.cltoolbox.common.parser.CollectSetContentsSrcg;
import com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils;
import com.github.samyadaleh.cltoolbox.common.parser.Token;
import com.github.samyadaleh.cltoolbox.common.parser.TokenReader;

import static com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils.addSymbolToCategory;

/**
 * Representation of a sRCG - simple Range Concatenation Grammar.
 */
public class Srcg extends AbstractNTSGrammar {
  private String[] variables;
  private final List<Clause> clauses = new ArrayList<>();

  /**
   * Converts a CFG to a sRCG with dimension = 1.
   */
  public Srcg(Cfg cfg) throws ParseException {
    this.setNonterminals(cfg.getNonterminals());
    this.setTerminals(cfg.getTerminals());
    this.setStartSymbol(cfg.getStartSymbol());
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
        category = GrammarParserUtils
            .addStartsymbolOrAddCategory(this, category, token,
                validCategories);
        break;
      case 3:
        CollectSetContentsSrcg collectSetContents =
            (CollectSetContentsSrcg) new CollectSetContentsSrcg(this, category,
                lhsNT, lhs, symbols, token).invoke();
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

  public void setVariables(String[] variables) {
    this.variables = variables;
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
    repr.append("N = {").append(String.join(", ", getNonterminals()))
        .append("}\n");
    repr.append("T = {").append(String.join(", ", getTerminals()))
        .append("}\n");
    repr.append("V = {").append(String.join(", ", variables)).append("}\n");
    repr.append("P = {");
    for (int i = 0; i < clauses.size(); i++) {
      if (i > 0) {
        repr.append(", ");
      }
      repr.append(clauses.get(i).toString());
    }
    repr.append("}\n");
    repr.append("S = ").append(getStartSymbol()).append("\n");
    return repr.toString();
  }

  public List<Clause> getClauses() {
    return this.clauses;
  }

  public String[] getVariables() {
    return this.variables;
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
   * Return an equivalent sRCG without epsilon as any lhs argument.
   */
  public Srcg getSrcgWithoutEmptyProductions() throws ParseException {
    return EmptyProductions.getSrcgWithoutEmptyProductions(this);
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
    private final Token token;
    private final String tokenString;

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

    StringBuilder getLhs() {
      return lhs;
    }

    String getCurrentRhsNt() {
      return currentRhsNt;
    }

    StringBuilder getRhs() {
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

}
