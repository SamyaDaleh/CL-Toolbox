package chartparsing;

import java.util.LinkedList;
import java.util.List;

import common.Item;

public class GenerateConsequencesItem implements Item {
  
  List<String> itemform = new LinkedList<String>();

  @Override public void setItemform(List<String> itemform) {
    this.itemform = itemform;
  }

  @Override public List<String> getItemform() {
    return this.itemform;
  }

}
