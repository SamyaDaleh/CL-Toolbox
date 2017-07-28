package chartparsing.tag;

import chartparsing.AbstractItem;
import chartparsing.Item;

public class TagEarleyPrefixValidItem extends AbstractItem implements Item {

  public TagEarleyPrefixValidItem(String tree, String p, String pos,
    String iGamma2, int i, Integer j, Integer k, int l, boolean adj) {
    String footFrom = (j == null) ? "-" : String.valueOf(j);
    String footTo = (k == null) ? "-" : String.valueOf(k);
    String adjRep = (adj) ? "1" : "0";
    itemForm = new String[] {tree, p, pos, iGamma2, String.valueOf(i), footFrom,
      footTo, String.valueOf(l), adjRep};
  }

  public TagEarleyPrefixValidItem(String treeName, String p, String pos,
    String iGamma, int i, String j, String k, int l, boolean adj) {
    String footFrom = (j == null) ? "-" : String.valueOf(j);
    String footTo = (k == null) ? "-" : String.valueOf(k);
    String adjRep = (adj) ? "1" : "0";
    itemForm = new String[] {treeName, p, pos, String.valueOf(iGamma),
      String.valueOf(i), footFrom, footTo, String.valueOf(l), adjRep};
  }

  public TagEarleyPrefixValidItem(String treeName, String node, String pos,
    String iGamma, String i, String j, String k, String l, boolean adj) {
    String adjRep = (adj) ? "1" : "0";
    itemForm = new String[] {treeName, node, pos, iGamma, i, j, k, l, adjRep};
  }

}
