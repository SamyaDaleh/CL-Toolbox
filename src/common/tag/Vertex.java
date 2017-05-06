package common.tag;

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
}
