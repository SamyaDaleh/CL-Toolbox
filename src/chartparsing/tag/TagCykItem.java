package chartparsing.tag;

import chartparsing.AbstractItem;
import chartparsing.Item;

/** Item of length 6 used by TAG CYK parsing. */
public class TagCykItem extends AbstractItem implements Item {

  /** Constructor that replaces the foot node indices by '-' if they are
   * null. */
  public TagCykItem(String tree, String p, int i, Integer f1, Integer f2,
    int j) {
    String footFrom = (f1 == null) ? "-" : String.valueOf(f1);
    String footTo = (f2 == null) ? "-" : String.valueOf(f2);
    itemForm = new String[] {tree, p, String.valueOf(i), footFrom, footTo,
      String.valueOf(j)};
  }

  @Override public String toString(){
    StringBuilder representation = new StringBuilder();
    representation.append("[");
    for (String element : itemForm) {
      if (representation.length() > 1) {
        representation.append(",");
      }
      switch (element) {
      case "⊤":
        representation.append("ε⊤");
        break;
      case "⊥":
        representation.append("ε⊥");
        break;
      case "":
        representation.append("ε");
        break;
      default:
        representation.append(element);
        break;
      }
    }
    representation.append("]");
    return representation.toString();
  }
}
