package common.lcfrs;

import java.util.LinkedList;
import java.util.List;

/** Representation of a sRCG - simple Range Concatenation Grammar. */
public class Srcg {
  String[] nonterminals;
  String[] terminals;
  String[] variables;
  String startsymbol;
  List<Clause> clauses = new LinkedList<Clause>();

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
    repr.append("N = {" + String.join(", ", nonterminals) + "}\n");
    repr.append("T = {" + String.join(", ", terminals) + "}\n");
    repr.append("V = {" + String.join(", ", variables) + "}\n");
    repr.append("P = {");
    for (int i = 0; i < clauses.size(); i++) {
      if (i > 0) {
        repr.append(", ");
      }
      repr.append(clauses.get(i).toString());
    }
    repr.append("}\n");
    repr.append("S = " + startsymbol + "\n");
    return repr.toString();
  }
}
