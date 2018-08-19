package com.github.samyadaleh.cltoolbox.common.tag;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;

/** A node in a tree, also called vertex. */
public class Vertex implements Cloneable {
  private final String label;
  private String gornAddress;

  /** Constructs a node with the given label. */
  Vertex(String label) {
    this.label = label;
  }

  public String getLabel() {
    return this.label;
  }

  public String getGornAddress() {
    return this.gornAddress;
  }

  void setGornaddress(String gornaddress) {
    this.gornAddress = gornaddress;
  }

  /** If this node is not a root node (with empty gorn address) the gorn address
   * of a possible right sibling is returned, that has gorn address +1. It is
   * not checked if a sibling exists at all. */
  public String getGornAddressOfPotentialRightSibling() {
    if (gornAddress == null || gornAddress.length() == 0)
      return null;
    String[] gornsplit = gornAddress.split("[.]");
    // node is child of root
    if (gornsplit.length == 0) {
      int nthchild =
        Integer.parseInt(gornAddress.substring(1));
      return "." + String.valueOf(nthchild + 1);
    }
    int nthchild = Integer.parseInt(gornsplit[gornsplit.length - 1]);
    return String.join(".",
      ArrayUtils.getSubSequenceAsArray(gornsplit, 0, gornsplit.length - 1))
      + "." + String.valueOf(nthchild + 1);
  }

  /** If this node is not a root node the gorn address of the parent is
   * returned. */
  public String getGornAddressOfParent() {
    if (gornAddress == null || gornAddress.length() == 0)
      return null;
    String[] gornsplit = gornAddress.split("[.]");
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
  boolean dominates(String gornaddress) {
    if (gornaddress.equals("") || gornaddress.equals(this.gornAddress)) {
      return false;
    }
    if (this.gornAddress.equals("")) {
      return true;
    }
    String[] gorn1split = this.gornAddress.split("[.]");
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
    p.gornAddress = this.gornAddress;
    return p;
  }
}
