package common.lcfrs;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

/** Representation of a sRCG - simple Range Concatenation Grammar. */
public class Srcg {
  private String[] nonterminals;
  private String[] terminals;
  private String[] variables;
  private String startSymbol;
  private final List<Clause> clauses = new ArrayList<Clause>();

  /**
   * Converts a CFG to a sRCG with dimension = 1.
   */
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

  /** Returns true if there is some rhs predicate that contains the empty string
   * in one of its lhs arguments. */
  public boolean hasEpsilonProductions() {
    boolean hasEpsilon = false;
    for (Clause clause : this.clauses) {
      for (String[] argument : clause.getLhs().getSymbols()) {
        if (argument.length == 1 && argument[0].equals("")) {
          hasEpsilon = true;
        }
      }
    }
    return hasEpsilon;
  }

  /** Returns true if all variables in rhs predicates appear in the same order
   * as in the lhs predicate. */
  public boolean isOrdered() {
    for (Clause clause : this.clauses) {
      for (Predicate rhsPred : clause.getRhs()) {
        ArrayList<Integer> posInLhs = new ArrayList<Integer>();
        for (String symbol : rhsPred.getSymbolsAsPlainArray()) {
          int[] indices = clause.getLhs().find(symbol);
          int abspos = clause.getLhs().getAbsolutePos(indices[0], indices[1]);
          posInLhs.add(abspos);
        }
        for (int i = 1; i < posInLhs.size(); i++) {
          if (!(posInLhs.get(i - 1) < posInLhs.get(i))) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /** Returns true if grammar has chain rules, that are rules with exactly one
   * rhs predicate. */
  public boolean hasChainRules() {
    for(Clause clause : this.clauses) {
      if (clause.getRhs().size() == 1) {
        return true;
      }
    }
    return false;
  }
}
