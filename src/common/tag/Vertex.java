package common.tag;

import common.ArrayUtils;

/** A node in a tree, also called vertex. */
public class Vertex implements Cloneable {
  private final String label;
  private String gornaddress;

  /** Constructs a node with the given label. */
  Vertex(String label) {
    this.label = label;
  }

  public String getLabel() {
    return this.label;
  }

  public String getGornaddress() {
    return this.gornaddress;
  }

  void setGornaddress(String gornaddress) {
    this.gornaddress = gornaddress;
  }

  /** If this node is not a root node (with empty gorn address) the gorn address
   * of a possible right sibling is returned, that has gorn address +1. It is
   * not checked if a sibling exists at all. */
  public String getGornAddressOfPotentialRightSibling() {
    if (gornaddress == null || gornaddress.length() == 0)
      return null;
    String[] gornsplit = gornaddress.split("[.]");
    // node is child of root
    if (gornsplit.length == 0) {
      int nthchild =
        Integer.parseInt(gornaddress.substring(1, gornaddress.length()));
      return "." + String.valueOf(nthchild + 1);
    } // */
    int nthchild = Integer.parseInt(gornsplit[gornsplit.length - 1]);
    return String.join(".",
      ArrayUtils.getSubSequenceAsArray(gornsplit, 0, gornsplit.length - 1))
      + "." + String.valueOf(nthchild + 1);
  }

  /** If this node is not a root node the gorn address of the parent is
   * returned. */
  public String getGornAddressOfParent() {
    if (gornaddress == null || gornaddress.length() == 0)
      return null;
    String[] gornsplit = gornaddress.split("[.]");
    // node is child of root
    if (gornsplit.length == 0) {
      return "";
    }
    return String.join(".",
      ArrayUtils.getSubSequenceAsArray(gornsplit, 0, gornsplit.length - 1));
  }

  /**
   * Returns true if this node dominates the given node, that means if it is a 
   * parent or an ancestor. The root domintes all other nodes.
   */
  public boolean dominates(String gornaddress) {
    if (gornaddress.equals("") || gornaddress.equals(this.gornaddress)) {
      return false;
    }
    if (this.gornaddress.equals("")) {
      return true;
    }
    String[] gorn1split = this.gornaddress.split("[.]");
    String[] gorn2split = gornaddress.split("[.]");
    
    int l1 = gorn1split.length;
    int l2 = gorn2split.length;
    
    if(l2 < l1) {
      return false;
    }
    
    for (int i = 1; i < l1; i++) {
      if (!gorn1split[i].equals(gorn2split[i])) {
        return false;
      }
    }
    return true;
  }
  
  @Override protected Vertex clone() {
    Vertex p = new Vertex(this.label);
    p.gornaddress = this.gornaddress;
    return p;
  }
}
