package common.lcfrs;

import java.text.ParseException;

import common.AbstractItem;
import common.Item;

/** Active item that has not been fully seen yet. */
public class SrcgEarleyActiveItem extends AbstractItem implements Item {

  private RangeVector p;

  /** Constructor with a clause, where a dot marked up until which point the
   * clause has been macthed with the input. p is the position up to which we
   * have processed the input. We have processed up to the jith element of the
   * ith argument. RangeVector contains the bindings of the variables of the
   * lhs. */
  public SrcgEarleyActiveItem(String clause, int pos, int i, int j,
    RangeVector p) {
    String[] pranges = p.getRangesAsPlainArray();
    this.itemform = new String[pranges.length + 4];
    this.itemform[0] = clause;
    this.itemform[1] = String.valueOf(pos);
    this.itemform[2] = String.valueOf(i);
    this.itemform[3] = String.valueOf(j);
    System.arraycopy(pranges, 0, this.itemform, 4, pranges.length);
    this.p = p;
  }

  /** Constructor with string array instead of a range vector to spare me a lot
   * of conversions. */
  public SrcgEarleyActiveItem(String clause, int pos, int i, int j,
    String[] rangevector) {
    this.itemform = new String[rangevector.length + 4];
    this.itemform[0] = clause;
    this.itemform[1] = String.valueOf(pos);
    this.itemform[2] = String.valueOf(i);
    this.itemform[3] = String.valueOf(j);
    System.arraycopy(rangevector, 0, this.itemform, 4, rangevector.length);
  }

  @Override public String toString() {
    String[] clausesplit = itemform[0].split("->");
    try {
      Clause clause = new Clause(clausesplit[0], clausesplit[1]);
      String dottedclause = clause.setDotAt(Integer.parseInt(itemform[2]),
        Integer.parseInt(itemform[3]));
      if (p != null) {
        return "[" + dottedclause + ", " + itemform[1] + ", <" + itemform[2]
          + "," + itemform[3] + ">, " + p.toString() + "]";
      } else {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int i = 0; i * 2 + 5 < itemform.length; i++) {
          if (i > 0) {
            builder.append(", ");
          }
          builder.append("<").append(itemform[i * 2 + 4]).append(",")
            .append(itemform[i * 2 + 5]).append(">");
        }
        builder.append(")");
        return "[" + dottedclause + ", " + itemform[1] + ", <" + itemform[2]
          + "," + itemform[3] + ">, " + builder.toString() + "]";
      }
    } catch (ParseException e) {
      if (p != null) {
        return "[A(ɸ) -> A_1(ɸ_1) ... A_m(ɸ_m), " + itemform[1] + ", <"
          + itemform[2] + "," + itemform[3] + ">, " + p.toString() + "]";
      } else {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int i = 0; i * 2 + 5 < itemform.length; i++) {
          if (i > 0) {
            builder.append(", ");
          }
          builder.append("<").append(itemform[i * 2 + 4]).append(",")
            .append(itemform[i * 2 + 5]).append(">");
        }
        builder.append(")");
        return "[A(ɸ) -> A_1(ɸ_1) ... A_m(ɸ_m), " + itemform[1] + ", <"
          + itemform[2] + "," + itemform[3] + ">, " + builder.toString() + "]";
      }
    }
  }
}
