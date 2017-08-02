package common.tag.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import common.tag.Tag;
import common.tag.Tree;

public class Binarization {

  /** Returns true if the TAG is binaried, that means all nodes have at most 2
   * child nodes. */
  public static boolean isBinarized(Tag tag) {
    for (String treeName : tag.getTreeNames()) {
      Tree tree = tag.getTree(treeName);
      if (!tree.isBinarized()) {
        return false;
      }
    }
    return true;
  }

  /** Return equivalent TAG were all nodes have at most 2 child nodes. */
  public static Tag getBinarizedTag(Tag oldTag) throws ParseException {
    Tag newTag = new Tag();
    newTag.setStartsymbol(oldTag.getStartSymbol());
    newTag.setTerminals(oldTag.getTerminals());
    ArrayList<String> newNonterminals = new ArrayList<String>();
    newNonterminals.addAll(Arrays.asList(oldTag.getNonterminals()));
    for (String treeName : oldTag.getInitialTreeNames()) {
      Tree tree = oldTag.getTree(treeName);
      if (tree.isBinarized()) {
        newTag.addInitialTree(treeName, tree.toString());
      } else {
        int i = 1;
        String newTreeName = treeName + String.valueOf(i);
        i++;
        while (newTag.getTreeNames().contains(newTreeName)
          || oldTag.getTreeNames().contains(newTreeName)) {
          newTreeName = treeName + String.valueOf(i);
          i++;
        }
        newTag.addInitialTree(newTreeName,
          tree.getBinarizedTree(newNonterminals).toString());
      }
    }
    for (String treeName : oldTag.getAuxiliaryTreeNames()) {
      Tree tree = oldTag.getTree(treeName);
      if (tree.isBinarized()) {
        newTag.addInitialTree(treeName, tree.toString());
      } else {
        int i = 1;
        String newTreeName = treeName + String.valueOf(i);
        i++;
        while (newTag.getTreeNames().contains(newTreeName)
          || oldTag.getTreeNames().contains(newTreeName)) {
          newTreeName = treeName + String.valueOf(i);
          i++;
        }
        newTag.addAuxiliaryTree(newTreeName,
          tree.getBinarizedTree(newNonterminals).toString());
      }
    }
    newTag.setNonterminals(
      newNonterminals.toArray(new String[newNonterminals.size()]));
    return newTag;
  }
}
