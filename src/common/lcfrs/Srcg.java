package common.lcfrs;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import common.cfg.Cfg;
import common.cfg.CfgProductionRule;
import common.lcfrs.util.EmptyProductions;
import common.lcfrs.util.Order;

/** Representation of a sRCG - simple Range Concatenation Grammar. */
public class Srcg {
  private String[] nonterminals;
  private String[] terminals;
  private String[] variables;
  private String startSymbol;
  private final List<Clause> clauses = new ArrayList<Clause>();

  /** Converts a CFG to a sRCG with dimension = 1. */
  public Srcg(Cfg cfg) throws ParseException {
    this.nonterminals = cfg.getNonterminals();
    this.terminals = cfg.getTerminals();
    this.startSymbol = cfg.getStartSymbol();
    ArrayList<String> newVariables = new ArrayList<String>();
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
    this.variables = newVariables.toArray(new String[newVariables.size()]);
  }

  public Srcg() {
    super();
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

  public void addClause(String lhs, String rhs) throws ParseException {
    this.clauses.add(new Clause(lhs, rhs));
  }

  public void addClause(String string) throws ParseException {
    this.clauses.add(new Clause(string));
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

  /** Returns true if each rhs contains at most two predicates. */
  public boolean isBinarized() {
    boolean binarized = true;
    for (Clause clause : this.clauses) {
      if (clause.getRhs().size() > 2) {
        binarized = false;
      }
    }
    return binarized;
  }

  /** Returns true if there is at least one clause that contains the empty
   * string in one of its lhs arguments, except if it is the start symbol in
   * which case it must not occur on any rhs. */
  public boolean hasEpsilonProductions() {
    return EmptyProductions.hasEpsilonProductions(this);
  }

  /** Returns true if all variables in rhs predicates appear in the same order
   * as in the lhs predicate. */
  public boolean isOrdered() {
    return Order.isOrdered(this);
  }

  /** Returns true if grammar has chain rules, that are rules with exactly one
   * rhs predicate. */
  public boolean hasChainRules() {
    for (Clause clause : this.clauses) {
      if (clause.getRhs().size() == 1) {
        return true;
      }
    }
    return false;
  }

  /** Returns an equivalent sRCG where the variables are ordered in each rule
   * for each predicate. Might leave useless nonterminals behind. */
  public Srcg getOrderedSrcg() throws ParseException {
    return Order.getOrderedSrcg(this);
  }

  /** Returns true if mayNt is in set of nonterminals. */
  public boolean nonTerminalsContain(String mayNt) {
    for (String nt : this.nonterminals) {
      if (mayNt.equals(nt)) {
        return true;
      }
    }
    return false;
  }

  /** Return an equivalent sRCG without epsilon as any lhs argument. */
  public Srcg getSrcgWithoutEmptyProductions() throws ParseException {
    return EmptyProductions.getSrcgWithoutEmptyProductions(this);
  }

  public void addClause(Clause newClause) {
    this.clauses.add(newClause);
  }

  /** Returns true if mayT is one of the terminals. */
  public boolean terminalsContain(String mayT) {
    for (int i = 0; i < this.terminals.length; i++) {
      if (this.terminals[i].equals(mayT)) {
        return true;
      }
    }
    return false;
  }
}
