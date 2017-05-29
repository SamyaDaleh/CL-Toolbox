package common.lcfrs;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

/** Representation of a clause of the form A(ɑ1,...,ɑ_dim(A)) ->
 * A1(X1,...,X_dim(A1)) ... */
public class Clause {

  private final Predicate lhs;
  private final List<Predicate> rhs = new LinkedList<Predicate>();

  /** Constructor that creates the lhs Predicate and splits the rhs to make
   * every part a Predicate  */
  Clause(String lhs, String rhs) throws ParseException {
    this.lhs = new Predicate(lhs);
    int start = 0;
    for (int i = 1; i < rhs.length(); i++) {
      if (rhs.charAt(i) == ')') {
        this.rhs.add(new Predicate(rhs.substring(start, i + 1)));
        start = i + 1;
      }
    }
  }

  /**
   * Does the split at "->" for you. 
   */
  public Clause(String clause) throws ParseException {
    String[] clausesplit = clause.split("->");
    this.lhs = new Predicate(clausesplit[0]);
    String rhs = clausesplit[1];
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

  /** Returns a string representation where the dot is at the ith argument at
   * the jth element of the lhs. */
  public String setDotAt(int i, int j) {
    StringBuilder repr = new StringBuilder();
    repr.append(lhs.setDotAt(i,j));
    repr.append(" -> ");
    if (rhs.isEmpty()) {
      repr.append("ε");
    } else {
      for (int k = 0; k < rhs.size(); k++) {
        if (k > 0)
          repr.append(" ");
        repr.append(rhs.get(k).toString());
      }
    }
    return repr.toString();
  }

  /**
   * Returns a symbol of the lhs, from the ith argument the jth element.
   */
  public String getLhsSymAt(int i, int j) {
    return lhs.getSymAt(i,j);
  }

  public List<Predicate> getRhs() {
    return this.rhs;
  }

  public Predicate getLhs() {
    return this.lhs;
  }
}
