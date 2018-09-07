package com.github.samyadaleh.cltoolbox.common.tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.util.Binarization;

import static com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils.addSymbolToCategory;

/**
 * Tree adjoining grammar that consists of terminals, nonterminals, a start
 * symbol, some initial trees and auxiliary trees.
 */
public class Tag {
  private String[] nonterminals;
  private String[] terminals;
  private String startSymbol;
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
    this.nonterminals = cfg.getNonterminals();
    this.terminals = cfg.getTerminals();
    this.startSymbol = cfg.getStartSymbol();
    int i = 1;
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      String treeString =
          "(" + rule.getLhs() + " " + String.join(" ", rule.getRhs()) + ")";
      this.addInitialTree("α" + String.valueOf(i), treeString);
      i++;
    }
  }

  public Tag(BufferedReader in) throws IOException, ParseException {
    Character[] specialChars =
        new Character[] {'>', '{', '}', ',', ':', '=', '<'};
    Set<String> validCategories = new HashSet<>();
    validCategories.add("N");
    validCategories.add("T");
    validCategories.add("S");
    validCategories.add("I");
    validCategories.add("A");
    validCategories.add("G");
    List<String> tokens = new ArrayList<>();
    String line = null;
    List<String> category = new ArrayList<>();
    int lineNumber = 0;
    String lhs = null;
    StringBuilder rhs = null;
    List<String> symbols = new ArrayList<>();
    while (tokens.size() > 0 || (line = in.readLine()) != null) {
      if (tokens.size() == 0) {
        tokens = ArrayUtils.tokenize(line, specialChars);
        lineNumber++;
      }
      String token = tokens.get(0);
      tokens.remove(0);
      switch (category.size()) {
      case 0:
        handleMainCategory(validCategories, category, lineNumber, token);
        break;
      case 1:
        addSymbolToCategory(category, lineNumber, token, "=");
        break;
      case 2:
        category = addStartsymbolOrAddCategory(category, lineNumber, token);
        break;
      case 3:
        switch (category.get(0)) {
        case "N":
          switch (token) {
          case "}":
            this.nonterminals = symbols.toArray(new String[0]);
            category = new ArrayList<>();
            symbols = new ArrayList<>();
            break;
          case ",":
            break;
          default:
            symbols.add(token);
          }
          break;
        case "T":
          switch (token) {
          case "}":
            this.terminals = symbols.toArray(new String[0]);
            category = new ArrayList<>();
            symbols = new ArrayList<>();
            break;
          case ",":
            break;
          default:
            symbols.add(token);
          }
          break;
        case "I":
        case "A":
          lhs = findLhsOrAddCategory(category, lineNumber, lhs, token);
          rhs = new StringBuilder();
          break;
        default:
          if (lhs != null) {
            throw new ParseException("Expected : but found " + token,
                lineNumber);
          }
          if (!token.equals(",")) {
            lhs = token;
          }
        }
        break;
      default:
        switch (token) {
        case "}":
          if (category.get(0).equals("I")) {
            this.addInitialTree(lhs, rhs.toString());
          } else {
            this.addAuxiliaryTree(lhs, rhs.toString());
          }
          category = new ArrayList<>();
          lhs = null;
          break;
        case ",":
          if (category.get(0).equals("I")) {
            this.addInitialTree(lhs, rhs.toString());
          } else {
            this.addAuxiliaryTree(lhs, rhs.toString());
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
          rhs.append(token);
        }
      }
    }
  }

  private String findLhsOrAddCategory(List<String> category, int lineNumber,
      String lhs, String token) throws ParseException {
    if (lhs == null || !token.equals(":")) {
      lhs = token;
    } else if (token.equals(":")) {
      category.add(token);
    } else {
      throw new ParseException("Unexpected situation with token " + token,
          lineNumber);
    }
    return lhs;
  }

  private List<String> addStartsymbolOrAddCategory(List<String> category,
      int lineNumber, String token) throws ParseException {
    switch (category.get(0)) {
    case "I":
      addSymbolToCategory(category, lineNumber, token, "{");
      break;
    case "A":
      addSymbolToCategory(category, lineNumber, token, "{");
      break;
    case "N":
      addSymbolToCategory(category, lineNumber, token, "{");
      break;
    case "T":
      addSymbolToCategory(category, lineNumber, token, "{");
      break;
    case "S":
      if (this.getStartSymbol() != null) {
        throw new ParseException("Startsymbol was declared twice: " + token,
            lineNumber);
      }
      this.startSymbol = token;
      category = new ArrayList<>();
      break;
    case "G":
      if (token.equals(">")) {
        category = new ArrayList<>();
      }
    default:
    }
    return category;
  }

  private void handleMainCategory(Set<String> validCategories,
      List<String> category, int lineNumber, String token)
      throws ParseException {
    if (validCategories.contains(token)) {
      if ((token.equals("N") && this.getNonterminals() != null) || (
          token.equals("T") && this.getTerminals() != null) || (
          token.equals("S") && this.startSymbol != null)) {
        throw new ParseException("Category " + token + " is already set.",
            lineNumber);
      }
      category.add(token);
    } else {
      throw new ParseException("Unknown declaration symbol " + token,
          lineNumber);
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

  public void setStartSymbol(String startSymbol) {
    this.startSymbol = startSymbol;
  }

  public String getStartSymbol() {
    return this.startSymbol;
  }

  /**
   * Returns true if the passed vertex in the named tree is a substitution
   * node, that means: it has a nonterminal label, it has no child nodes and it
   * is no foot node.
   */
  public boolean isSubstitutionNode(Vertex p, String treeName) {
    boolean nonterminalLabel = false;
    for (String nt : nonterminals) {
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
    for (String nt : nonterminals) {
      if (label.equals(nt))
        return true;
    }
    return false;
  }

  /**
   * Returns true if the passed label is one of the terminals.
   */
  private boolean isInTerminals(String label) {
    for (String t : terminals) {
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
    builder.append("N = {").append(String.join(", ", nonterminals))
        .append("}\n");
    builder.append("T = {").append(String.join(", ", terminals)).append("}\n");
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
    builder.append("S = ").append(startSymbol).append("\n");
    return builder.toString();
  }
}
