package chartparsing.tag;

import chartparsing.AbstractItem;
import chartparsing.Item;

/** Item of length 8 used by TAG Earley parsing. */
public class TagEarleyItem extends AbstractItem implements Item {

  /** Constructor that replaces the foot node indices by '-' if they are
   * null. */
  public TagEarleyItem(String tree, String p, String pos, int i, Integer j,
    Integer k, int l, boolean adj) {
    String footFrom = (j == null) ? "-" : String.valueOf(j);
    String footTo = (k == null) ? "-" : String.valueOf(k);
    String adjRep = (adj) ? "1" : "0";
    itemForm = new String[] {tree, p, pos, String.valueOf(i), footFrom, footTo,
      String.valueOf(l), adjRep};
  }

  /**
   * Constructor that takes Strings instead of Integers, so I can pass in '?'.
   */
  public TagEarleyItem(String treeName, String gornAddress, String pos, int i,
      String j, String k, int l) {
    String footFrom = (j == null) ? "-" : String.valueOf(j);
    String footTo = (k == null) ? "-" : String.valueOf(k);
    String adjRep = "0";
    itemForm = new String[] {treeName, gornAddress, pos, String.valueOf(i), footFrom, footTo,
      String.valueOf(l), adjRep};
  }

}
