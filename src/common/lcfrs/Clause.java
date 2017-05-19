package common.lcfrs;

import java.util.LinkedList;
import java.util.List;

/** Representation of a clause of the form A(ɑ1,...,ɑ_dim(A)) ->
 * A1(X1,...,X_dim(A1)) ... */
public class Clause {

  Predicate lhs;
  List<Predicate> rhs = new LinkedList<Predicate>();

  /** Constructor that creates the lhs Predicate and splits the rhs to make
   * every part a Predicate */
  public Clause(String lhs, String rhs) {
    this.lhs = new Predicate(lhs);
    int start = 0;
    for (int i = 1; i < rhs.length(); i++) {
      if (rhs.charAt(i) == ')') {
        this.rhs.add(new Predicate(rhs.substring(start, i + 1)));
        start = i + 1;
      }
    }
  }

  @Override public String toString() {
    StringBuilder repr = new StringBuilder();
    repr.append(lhs.toString());
    repr.append(" -> ");
    if (rhs.isEmpty()) {
      repr.append("ε");
    } else {
      for (int i = 0; i < rhs.size(); i++) {
        if (i > 0)
          repr.append(" ");
        repr.append(rhs.get(i).toString());
      }
    }
    return repr.toString();
  }

  /** Return the nonterminal of the left predicate. */
  public String getLhsNonterminal() {
    return lhs.getNonterminal();
  }

  public int getLhsDim() {
    return lhs.getSymbols().length;
  }

  /** Returns its string representation with a dot at the ith position of the
   * variables in the lhs. */
  public String setDotAt(int i) {
    StringBuilder repr = new StringBuilder();
    repr.append(lhs.setDotAt(i));
    repr.append(" -> ");
    if (rhs.isEmpty()) {
      repr.append("ε");
    } else {
      for (int j = 0; j < rhs.size(); j++) {
        if (j > 0)
          repr.append(" ");
        repr.append(rhs.get(j).toString());
      }
    }
    return repr.toString();
  }
}
