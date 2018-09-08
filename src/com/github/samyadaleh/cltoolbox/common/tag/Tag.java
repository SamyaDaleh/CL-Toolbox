package com.github.samyadaleh.cltoolbox.common.tag;

import com.github.samyadaleh.cltoolbox.common.AbstractNTSGrammar;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.parser.CollectSetContentsTag;
import com.github.samyadaleh.cltoolbox.common.parser.Token;
import com.github.samyadaleh.cltoolbox.common.parser.TokenReader;
import com.github.samyadaleh.cltoolbox.common.tag.util.Binarization;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils.addSymbolToCategory;

/**
 * Tree adjoining grammar that consists of terminals, nonterminals, a start
 * symbol, some initial trees and auxiliary trees.
 */
public class Tag extends AbstractNTSGrammar {
  private final Map<String, Tree> initialTrees = new HashMap<>();
  private final Map<String, Tree> auxiliaryTrees = new HashMap<>();

  public Tag() {
    super();
  }

  /**
   * Creates a TAG from a context free grammar by converting all rules to
   * trees.
   */
  public Tag(Cfg cfg) throws ParseException {
    this.setNonterminals(cfg.getNonterminals());
    this.setTerminals(cfg.getTerminals());
    this.setStartSymbol(cfg.getStartSymbol());
    int i = 1;
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      String treeString =
          "(" + rule.getLhs() + " " + String.join(" ", rule.getRhs()) + ")";
      this.addInitialTree("α" + String.valueOf(i), treeString);
      i++;
    }
  }

  public Tag(BufferedReader in) throws IOException, ParseException {
    Character[] specialChars = new Character[] {'>', '{', '}', ',', ':', '='};
    TokenReader reader = new TokenReader(in, specialChars);
    Set<String> validCategories = new HashSet<>();
    validCategories.add("N");
    validCategories.add("T");
    validCategories.add("S");
    validCategories.add("I");
    validCategories.add("A");
    validCategories.add("G");
    List<String> category = new ArrayList<>();
    String lhs = null;
    StringBuilder rhs = null;
    List<String> symbols = new ArrayList<>();
    Token token;
    while ((token = reader.getNextToken()) != null) {
      String tokenString = token.getString();
      switch (category.size()) {
      case 0:
        handleMainCategory(validCategories, category, token);
        break;
      case 1:
        addSymbolToCategory(category, token, "=");
        break;
      case 2:
        category = addStartsymbolOrAddCategory(category, token);
        break;
      case 3:
        CollectSetContentsTag collectSetContents =
            (CollectSetContentsTag) new CollectSetContentsTag(this, category,
                lhs, rhs, symbols, token).invoke();
        category = collectSetContents.getCategory();
        lhs = collectSetContents.getLhs();
        rhs = collectSetContents.getRhs();
        symbols = collectSetContents.getSymbols();
        break;
      default:
        CollectTreeTokens collectTreeTokens =
            new CollectTreeTokens(category, lhs, rhs, tokenString).invoke();
        category = collectTreeTokens.getCategory();
        lhs = collectTreeTokens.getLhs();
        rhs = collectTreeTokens.getRhs();
      }
    }
  }

  private List<String> addStartsymbolOrAddCategory(List<String> category,
      Token token) throws ParseException {
    String tokenString = token.getString();
    switch (category.get(0)) {
    case "I":
      addSymbolToCategory(category, token, "{");
      break;
    case "A":
      addSymbolToCategory(category, token, "{");
      break;
    case "N":
      addSymbolToCategory(category, token, "{");
      break;
    case "T":
      addSymbolToCategory(category, token, "{");
      break;
    case "S":
      if (this.getStartSymbol() != null) {
        throw new ParseException(
            "Startsymbol was declared twice: " + tokenString,
            token.getLineNumber());
      }
      this.setStartSymbol(tokenString);
      category = new ArrayList<>();
      break;
    case "G":
      if (tokenString.equals(">")) {
        category = new ArrayList<>();
      }
    default:
    }
    return category;
  }

  private void handleMainCategory(Set<String> validCategories,
      List<String> category, Token token) throws ParseException {
    String tokenString = token.getString();
    if (validCategories.contains(tokenString)) {
      if ((tokenString.equals("N") && this.getNonterminals() != null) || (
          tokenString.equals("T") && this.getTerminals() != null) || (
          tokenString.equals("S") && this.getStartSymbol() != null) || (
          tokenString.equals("A") && this.getAuxiliaryTreeNames().size() > 0)
          || (tokenString.equals("I")
          && this.getInitialTreeNames().size() > 0)) {
        throw new ParseException("Category " + tokenString + " is already set.",
            token.getLineNumber());
      }
      category.add(tokenString);
    } else {
      throw new ParseException("Unknown declaration symbol " + tokenString,
          token.getLineNumber());
    }
  }

  /**
   * Parses a tree string, performs some basic validation and adds it to its
   * initial trees.
   */
  public void addInitialTree(String name, String tree) throws ParseException {
    this.initialTrees.put(name, new Tree(tree));
    for (Vertex p : getInitialTree(name).getVertexes()) {
      if (isInTerminals(p.getLabel()) && getInitialTree(name).hasChildren(p)) {
        throw new ParseException(
            "Terminal nodes are not allowed to have children", 0);
      }
    }
  }

  /**
   * Returns a set of all names of inital trees.
   */
  public Set<String> getInitialTreeNames() {
    return this.initialTrees.keySet();
  }

  /**
   * Returns a set of all names of auxiliary trees.
   */
  public Set<String> getAuxiliaryTreeNames() {
    return this.auxiliaryTrees.keySet();
  }

  public Tree getInitialTree(String name) {
    return this.initialTrees.get(name);
  }

  public Tree getAuxiliaryTree(String name) {
    return this.auxiliaryTrees.get(name);
  }

  /**
   * Parses a tree string, performs some basic validation and adds it to its
   * auxiliary trees.
   */
  public void addAuxiliaryTree(String name, String tree) throws ParseException {
    this.auxiliaryTrees.put(name, new Tree(tree));
    if (!isFootAndRootSameLabel(getAuxiliaryTree(name))) {
      throw new ParseException(
          "Root and Foot of auxiliary tree must have the same label", 0);
    }
    for (Vertex p : getAuxiliaryTree(name).getVertexes()) {
      if (isInTerminals(p.getLabel()) && getAuxiliaryTree(name)
          .hasChildren(p)) {
        throw new ParseException(
            "Terminal nodes are not allowed to have children", 0);
      }
    }
  }

  /**
   * Returns true if root node and foot node of an auxiliary tree have the same
   * label.
   */
  private boolean isFootAndRootSameLabel(Tree auxiliaryTree) {
    return (auxiliaryTree.getFoot().getLabel()
        .equals(auxiliaryTree.getRoot().getLabel()));
  }

  /**
   * Returns a set of all names of inital and auxiliary trees.
   */
  public Set<String> getTreeNames() {
    Set<String> c = new HashSet<>();
    c.addAll(this.initialTrees.keySet());
    c.addAll(this.auxiliaryTrees.keySet());
    return c;
  }

  public Tree getTree(String name) {
    if (this.initialTrees.containsKey(name)) {
      return this.initialTrees.get(name);
    } else {
      return this.auxiliaryTrees.get(name);
    }
  }

  /**
   * Returns true if the passed vertex in the named tree is a substitution
   * node, that means: it has a nonterminal label, it has no child nodes and it
   * is no foot node.
   */
  public boolean isSubstitutionNode(Vertex p, String treeName) {
    boolean nonterminalLabel = false;
    for (String nt : getNonterminals()) {
      if (nt.equals(p.getLabel())) {
        nonterminalLabel = true;
        break;
      }
    }
    Tree tree;
    if (initialTrees.containsKey(treeName)) {
      tree = initialTrees.get(treeName);
    } else {
      tree = auxiliaryTrees.get(treeName);
    }
    boolean isFootNode = p.equals(tree.getFoot());
    return (nonterminalLabel && !tree.hasChildren(p) && !isFootNode);
  }

  /**
   * Returns true if the TAG is binaried, that means all nodes have at most 2
   * child nodes.
   */
  public boolean isBinarized() {
    return Binarization.isBinarized(this);
  }

  /**
   * Return equivalent TAG were all nodes have at most 2 child nodes.
   */
  public Tag getBinarizedTag() throws ParseException {
    return Binarization.getBinarizedTag(this);
  }

  /**
   * Returns true if the passed label is one of the nonterminals.
   */
  public boolean isInNonterminals(String label) {
    for (String nt : getNonterminals()) {
      if (label.equals(nt))
        return true;
    }
    return false;
  }

  /**
   * Returns true if the passed label is one of the terminals.
   */
  private boolean isInTerminals(String label) {
    for (String t : getTerminals()) {
      if (label.equals(t))
        return true;
    }
    return false;
  }

  /**
   * Returns true if auxtree is adjoinable in tree at node with gorn address.
   * That is the case if the label of the node and the auxiliary tree are the
   * same and the node is no leaf.
   */
  public boolean isAdjoinable(String auxTreeName, String treeName,
      String gornAddress) {
    Tree tree = getTree(treeName);
    Tree auxTree = getAuxiliaryTree(auxTreeName);
    boolean labelCheck = auxTree != null && auxTree.getRoot().getLabel()
        .equals(tree.getNodeByGornAdress(gornAddress).getLabel());
    boolean isLeaf =
        tree.getChildren(tree.getNodeByGornAdress(gornAddress)).isEmpty();
    boolean nullAdjoin = tree.isInNA(gornAddress);
    return (labelCheck && !isLeaf && !nullAdjoin);
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    Set<String> iniTreesNameSet = getInitialTreeNames();
    String[] iniTreeNames = iniTreesNameSet.toArray(new String[0]);
    Set<String> auxTreesNameSet = getAuxiliaryTreeNames();
    String[] auxTreeNames = auxTreesNameSet.toArray(new String[0]);
    builder.append("G = <N, T, I, A, S>\n");
    builder.append("N = {").append(String.join(", ", getNonterminals()))
        .append("}\n");
    builder.append("T = {").append(String.join(", ", getTerminals()))
        .append("}\n");
    builder.append("I = {");
    for (int i = 0; i < initialTrees.size(); i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append(iniTreeNames[i]).append(" : ")
          .append(getInitialTree(iniTreeNames[i]).toString());
    }
    builder.append("}\n");
    builder.append("A = {");
    for (int i = 0; i < auxiliaryTrees.size(); i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append(auxTreeNames[i]).append(" : ")
          .append(getAuxiliaryTree(auxTreeNames[i]).toString());
    }
    builder.append("}\n");
    builder.append("S = ").append(getStartSymbol()).append("\n");
    return builder.toString();
  }

  private class CollectTreeTokens {
    private List<String> category;
    private String lhs;
    private StringBuilder rhs;
    private String tokenString;

    CollectTreeTokens(List<String> category, String lhs, StringBuilder rhs,
        String tokenString) {
      this.category = category;
      this.lhs = lhs;
      this.rhs = rhs;
      this.tokenString = tokenString;
    }

    List<String> getCategory() {
      return category;
    }

    public String getLhs() {
      return lhs;
    }

    public StringBuilder getRhs() {
      return rhs;
    }

    CollectTreeTokens invoke() throws ParseException {
      switch (tokenString) {
      case "}":
        if (category.get(0).equals("I")) {
          Tag.this.addInitialTree(lhs, rhs.toString());
        } else {
          Tag.this.addAuxiliaryTree(lhs, rhs.toString());
        }
        category = new ArrayList<>();
        lhs = null;
        break;
      case ",":
        if (category.get(0).equals("I")) {
          Tag.this.addInitialTree(lhs, rhs.toString());
        } else {
          Tag.this.addAuxiliaryTree(lhs, rhs.toString());
        }
        rhs = new StringBuilder();
        lhs = null;
        category.remove(4);
        category.remove(3);
        break;
      default:
        if (rhs.length() > 0) {
          rhs.append(' ');
        }
        rhs.append(tokenString);
      }
      return this;
    }
  }

}
