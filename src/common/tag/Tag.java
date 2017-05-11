package common.tag;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

/** Tree adjoining grammar that consists of terminals, nonterminals, a start
 * symbol, some initial trees and auxiliary trees. */
public class Tag {
  String[] nonterminals;
  String[] terminals;
  String startsymbol;
  Map<String, Tree> initialtrees = new HashMap<String, Tree>();
  Map<String, Tree> auxiliarytrees = new HashMap<String, Tree>();

  public Tag() {
    super();
  }

  /** Creates a TAG from a context free grammar by converting all rules to
   * trees. */
  public Tag(Cfg cfg) throws ParseException {
    this.nonterminals = cfg.getVars();
    this.terminals = cfg.getTerminals();
    this.startsymbol = cfg.getStart_var();
    int i = 1;
    for (CfgProductionRule rule : cfg.getR()) {
      String treestring =
        "(" + rule.getLhs() + " " + String.join(" ", rule.getRhs()) + ")";
      this.addInitialTree("α" + String.valueOf(i), treestring);
      i++;
    }
  }

  public void setNonterminals(String[] nonterminals) {
    this.nonterminals = nonterminals;
  }

  public String[] getNonterminals() {
    return this.nonterminals;
  }

  public void setTerminals(String[] terminals) {
    this.terminals = terminals;
  }

  public String[] getTerminals() {
    return this.terminals;
  }

  /** Parses a tree string, performs some basic validation and adds it to its
   * initial trees. */
  public void addInitialTree(String name, String tree) throws ParseException {
    this.initialtrees.put(name, new Tree(tree));
    for (Vertex p : getInitialTree(name).getVertexes()) {
      if (isInTerminals(p.getLabel()) && getInitialTree(name).hasChildren(p)) {
        throw new ParseException(
          "Terminal nodes are not allowed to have children", 0);
      }
    }
  }

  /** Returns a set of all names of inital trees. */
  public Set<String> getInitialTreeNames() {
    return this.initialtrees.keySet();
  }

  /** Returns a set of all names of auxiliary trees. */
  public Set<String> getAuxiliaryTreeNames() {
    return this.auxiliarytrees.keySet();
  }

  public Tree getInitialTree(String name) {
    return this.initialtrees.get(name);
  }

  public Tree getAuxiliaryTree(String name) {
    return this.auxiliarytrees.get(name);
  }

  /** Parses a tree string, performs some basic validation and adds it to its
   * auxiliary trees. */
  public void addAuxiliaryTree(String name, String tree) throws ParseException {
    this.auxiliarytrees.put(name, new Tree(tree));
    if (!isFootAndRootSameLabel(getAuxiliaryTree(name))) {
      throw new ParseException(
        "Root and Foot of auxiliary tree must have the same label", 0);
    }
    for (Vertex p : getAuxiliaryTree(name).getVertexes()) {
      if (isInTerminals(p.getLabel())
        && getAuxiliaryTree(name).hasChildren(p)) {
        throw new ParseException(
          "Terminal nodes are not allowed to have children", 0);
      }
    }
  }

  /** Returns true if root node and foot node of an auxiliary tree have the same
   * label. */
  private boolean isFootAndRootSameLabel(Tree auxiliaryTree) {
    return (auxiliaryTree.getFoot().getLabel()
      .equals(auxiliaryTree.getRoot().getLabel()));
  }

  /** Returns a set of all names of inital and auxiliary trees. */
  public Set<String> getTreeNames() {
    Set<String> c = new HashSet<String>();
    c.addAll(this.initialtrees.keySet());
    c.addAll(this.auxiliarytrees.keySet());
    return c;
  }

  public Tree getTree(String name) {
    if (this.initialtrees.containsKey(name)) {
      return this.initialtrees.get(name);
    } else {
      return this.auxiliarytrees.get(name);
    }
  }

  public void setStartsymbol(String startsymbol) {
    this.startsymbol = startsymbol;
  }

  public String getStartSymbol() {
    return this.startsymbol;
  }

  /** Returns true if the passed vertex in the named tree is a substitution
   * node, that means: it has a nonterminal label, it has no child nodes and it
   * is no foot node. */
  public boolean isSubstitutionNode(Vertex p, String treename) {
    boolean nonterminallabel = false;
    for (String nt : nonterminals) {
      if (nt.equals(p.getLabel())) {
        nonterminallabel = true;
        break;
      }
    }
    Tree tree = null;
    if (initialtrees.containsKey(treename)) {
      tree = initialtrees.get(treename);
    } else {
      tree = auxiliarytrees.get(treename);
    }
    boolean isfootnode = p.equals(tree.getFoot());
    return (nonterminallabel && !tree.hasChildren(p) && !isfootnode);
  }

  /** Returns true if the TAG is binaried, that means all nodes have at most 2
   * child nodes. */
  public boolean isBinarized() {
    for (Tree tree : initialtrees.values()) {
      for (Vertex p : tree.getVertexes()) {
        String gornaddress = p.getGornaddress();
        if (gornaddress.length() > 0
          && gornaddress.charAt(gornaddress.length() - 1) == '3') {
          return false;
        }
      }
    }
    for (Tree tree : auxiliarytrees.values()) {
      for (Vertex p : tree.getVertexes()) {
        String gornaddress = p.getGornaddress();
        if (gornaddress.length() > 0
          && gornaddress.charAt(gornaddress.length() - 1) == '3') {
          return false;
        }
      }
    }
    return true;
  }

  /** Returns true if the passed label is one of the nonterminals. */
  public boolean isInNonterminals(String label) {
    for (String nt : nonterminals) {
      if (label.equals(nt))
        return true;
    }
    return false;
  }

  /** Returns true if the passed label is one of the terminals. */
  public boolean isInTerminals(String label) {
    for (String t : terminals) {
      if (label.equals(t))
        return true;
    }
    return false;
  }

  /** Returns true if auxtree is adjoinable in tree at node with gorn address.
   * That is the case if the label of the node and the auxiliary tree are the
   * same and the node is no substitution node.
   * @return */
  public boolean isAdjoinable(String auxtreename, String treename,
    String gornaddress) {
    boolean labelcheck = getAuxiliaryTree(auxtreename).getRoot().getLabel()
      .equals(getTree(treename).getNodeByGornAdress(gornaddress).getLabel());
    boolean issubstnode = isSubstitutionNode(
      getTree(treename).getNodeByGornAdress(gornaddress), treename);
    return (labelcheck && !issubstnode);
  }
}
