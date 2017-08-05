package chartparsing.lcfrs;

import java.text.ParseException;
import java.util.List;

import chartparsing.AbstractItem;
import chartparsing.Item;
import common.lcfrs.Clause;
import common.lcfrs.RangeVector;

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
    String[] pRanges = p.getRangesAsPlainArray();
    this.itemForm = new String[pRanges.length + 4];
    this.itemForm[0] = clause;
    this.itemForm[1] = String.valueOf(pos);
    this.itemForm[2] = String.valueOf(i);
    this.itemForm[3] = String.valueOf(j);
    System.arraycopy(pRanges, 0, this.itemForm, 4, pRanges.length);
    this.p = p;
  }

  /** Constructor with string array instead of a range vector to spare me a lot
   * of conversions. */
  SrcgEarleyActiveItem(String clause, int pos, int i, int j,
    List<String> rangeVector) {
    this.itemForm = new String[rangeVector.size() + 4];
    this.itemForm[0] = clause;
    this.itemForm[1] = String.valueOf(pos);
    this.itemForm[2] = String.valueOf(i);
    this.itemForm[3] = String.valueOf(j);
    int k = 4;
    for (String range : rangeVector) {
      itemForm[k] = range;
      k++;
    }
  }

  @Override public String toString() {
    String[] clauseSplit = itemForm[0].split("->");
    try {
      Clause clause = new Clause(clauseSplit[0], clauseSplit[1]);
      String dottedClause = clause.setDotAt(Integer.parseInt(itemForm[2]),
        Integer.parseInt(itemForm[3]));
      if (p != null) {
        return "[" + dottedClause + ", " + itemForm[1] + ", <" + itemForm[2]
          + "," + itemForm[3] + ">, " + p.toString() + "]";
      } else {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int i = 0; i * 2 + 5 < itemForm.length; i++) {
          if (i > 0) {
            builder.append(", ");
          }
          builder.append("<").append(itemForm[i * 2 + 4]).append(",")
            .append(itemForm[i * 2 + 5]).append(">");
        }
        builder.append(")");
        return "[" + dottedClause + ", " + itemForm[1] + ", <" + itemForm[2]
          + "," + itemForm[3] + ">, " + builder.toString() + "]";
      }
    } catch (ParseException e) {
      if (p != null) {
        return "[A(ɸ) -> A_1(ɸ_1) ... A_m(ɸ_m), " + itemForm[1] + ", <"
          + itemForm[2] + "," + itemForm[3] + ">, " + p.toString() + "]";
      } else {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int i = 0; i * 2 + 5 < itemForm.length; i++) {
          if (i > 0) {
            builder.append(", ");
          }
          builder.append("<").append(itemForm[i * 2 + 4]).append(",")
            .append(itemForm[i * 2 + 5]).append(">");
        }
        builder.append(")");
        return "[A(ɸ) -> A_1(ɸ_1) ... A_m(ɸ_m), " + itemForm[1] + ", <"
          + itemForm[2] + "," + itemForm[3] + ">, " + builder.toString() + "]";
      }
    }
  }
}
