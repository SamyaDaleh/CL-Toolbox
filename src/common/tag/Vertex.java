package common.tag;

import common.ArrayUtils;

public class Vertex {
  String label;
  String gornaddress;

  Vertex(String label) {
    this.label = label;
  }

  public String getLabel() {
    return this.label;
  }

  public String getGornaddress() {
    return this.gornaddress;
  }

  protected void setGornaddress(String gornaddress) {
    this.gornaddress = gornaddress;
  }

  public String getGornAddressOfPotentialRightSibling() {
    if (gornaddress == null || gornaddress.length() == 0)
      return null;
    String[] gornsplit = gornaddress.split(".");
    // node is child of root
    if (gornsplit.length == 0) {
      int nthchild = Integer.parseInt(gornaddress.substring(1, gornaddress.length()));
      return "." + String.valueOf(nthchild + 1);
    } //*/
    int nthchild = Integer.parseInt(gornsplit[gornsplit.length - 1]);
    return String.join(".",
      ArrayUtils.getSubSequenceAsArray(gornsplit, 0, gornsplit.length - 1))
      + "." + String.valueOf(nthchild + 1);
  }

  public String getGornAddressOfParent() {
    if (gornaddress == null || gornaddress.length() == 0)
      return null;
    String[] gornsplit = gornaddress.split(".");
    // node is child of root
    if (gornsplit.length == 0) {
      return "";
    }
    return String.join(".",
      ArrayUtils.getSubSequenceAsArray(gornsplit, 0, gornsplit.length - 1));
  }
}
