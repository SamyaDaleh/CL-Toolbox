package chartparsing;

import chartparsing.Item;
import common.ArrayUtils;
import common.tag.Tree;

/** Abstract class that holds the functions commonly used by all items. */
public abstract class AbstractItem implements Item {

  protected String[] itemForm;
  protected Tree tree;

  @Override public String[] getItemform() {
    return this.itemForm;
  }

  @Override public boolean equals(Object o) {
    return ArrayUtils.match(this.itemForm, ((Item) o).getItemform());
  }

  @Override public int hashCode() {
    int hashCode = 0;
    int i = 2;
    for (String item : this.itemForm) {
      for (char chara : item.toCharArray()) {
        int value = (int) chara;
        hashCode += value * i;
        i += 2;
      }
    }
    return hashCode;
  }

  @Override public String toString() {
    return ArrayUtils.toString(this.itemForm);
  }

  @Override public Tree getTree() {
    return this.tree;
  }

  @Override public void setTree(Tree tree) {
    this.tree = tree;
  }

}
