package common.lcfrs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

/** Representation of a sRCG - simple Range Concatenation Grammar. */
public class Srcg {
  private String[] nonterminals;
  private String[] terminals;
  private String[] variables;
  private String startsymbol;
  private final List<Clause> clauses = new LinkedList<Clause>();

  public Srcg(Cfg cfg) {
    this.nonterminals = cfg.getVars();
    this.terminals = cfg.getTerminals();
    this.startsymbol = cfg.getStart_var();
    ArrayList<String> newvariables = new ArrayList<String>();
    for (CfgProductionRule rule : cfg.getR()) {
      StringBuilder lhs = new StringBuilder();
      StringBuilder rhs = new StringBuilder();
      int i = 1;
      lhs.append(rule.getLhs()).append("(");
      for (String rhssym : rule.getRhs()) {
        if (rhssym.length() > 0) {
          if (cfg.terminalsContain(rhssym)) {
            lhs.append(" ").append(rhssym);
          } else {
            String newvar = "X" + String.valueOf(i);
            while (cfg.nonterminalsContain(newvar)) {
              i++;
              newvar = "X" + String.valueOf(i);
            }
            if (!newvariables.contains(newvar)) {
              newvariables.add(newvar);
            }
            lhs.append(" ").append(newvar);
            rhs.append(rhssym).append("(").append(newvar).append(")");
          }
        } else {
          rhs.append("ε");
          lhs.append("ε");
        }
      }
      lhs.append(")");
      this.addClause(lhs.toString(), rhs.toString());
    }
    this.variables = newvariables.toArray(new String[newvariables.size()]);
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

  public void setStartSymbol(String startsymbol) {
    this.startsymbol = startsymbol;
  }

  public void addClause(String lhs, String rhs) {
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
    repr.append("S = ").append(startsymbol).append("\n");
    return repr.toString();
  }

  public List<Clause> getClauses() {
    return this.clauses;
  }

  public String getStartSymbol() {
    return this.startsymbol;
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
    boolean hasepsilon = false;
    for (Clause clause : this.clauses) {
      for (String[] argument : clause.getLhs().getSymbols()) {
        if (argument.length == 1 && argument[0].equals("")) {
          hasepsilon = true;
        }
      }
    }
    return hasepsilon;
  }

  /** Returns true if all variables in rhs predicates appear in the same order
   * as in the lhs predicate. */
  public boolean isOrdered() {
    for (Clause clause : this.clauses) {
      for (Predicate rhspred : clause.getRhs()) {
        ArrayList<Integer> posinlhs = new ArrayList<Integer>();
        for (String symbol : rhspred.getSymbolsAsPlainArray()) {
          int[] indices = clause.getLhs().find(symbol);
          int abspos = clause.getLhs().getAbsolutePos(indices[0], indices[1]);
          posinlhs.add(abspos);
        }
        for (int i = 1; i < posinlhs.size(); i++) {
          if (!(posinlhs.get(i - 1) < posinlhs.get(i))) {
            return false;
          }
        }
      }
    }
    return true;
  }
}
