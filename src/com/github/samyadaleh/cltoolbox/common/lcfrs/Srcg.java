package com.github.samyadaleh.cltoolbox.common.lcfrs;

import com.github.samyadaleh.cltoolbox.common.AbstractNTSGrammar;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.Binarization;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.EmptyProductions;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.Order;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.UselessRules;
import com.github.samyadaleh.cltoolbox.common.parser.inner.InnerSrcgGrammarParser;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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

  public Srcg(BufferedReader in) throws ParseException {
    new InnerSrcgGrammarParser(this, in).invoke();
  }

  public void setVariables(String[] variables) {
    this.variables = variables;
  }

  public void addClause(String lhs, String rhs) throws ParseException {
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

}
