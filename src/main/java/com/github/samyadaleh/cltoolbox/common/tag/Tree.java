package com.github.samyadaleh.cltoolbox.common.tag;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.samyadaleh.cltoolbox.common.ArrayUtils.tokenize;
import static com.github.samyadaleh.cltoolbox.common.Constants.EPSILON;

/**
 * A non-recursive representation of a context-free tree consisting of
 * vertexes, directed edges and in the context of TAG may have a special foot
 * node.
 */
public class Tree {

  private final List<Vertex> vertexes = new ArrayList<>();
  private final Map<String, Vertex> gornAddressMap = new HashMap<>();
  private final List<Edge> edges = new ArrayList<>();
  private Vertex root = null;
  private Vertex foot = null;
  private List<Vertex> nA = new ArrayList<>();
  private List<Vertex> oA = new ArrayList<>();

  private final ArrayList<String> leafOrder = new ArrayList<>();
  private final ArrayList<String> leafGorns = new ArrayList<>();

  private String stringRepresentation;

  /**
   * Takes a string in bracket format, tokenizes it and parses the actual tree
   * from it.
   */
  public Tree(String tree) throws ParseException {
    Character[] specialChars = new Character[] {'(', ')', '*', '_', '<', '>'};
    List<String> tokenList = tokenize(tree, specialChars);
    String[] tokens = tokenList.toArray(new String[0]);
    List<Vertex> vertexPath = new ArrayList<>();
    List<Integer> children = new ArrayList<>();
    for (int i = 0; i < tokens.length; i++) {
      switch (tokens[i]) {
      case "(": {
        i = openSubTree(tokens, vertexPath, children, i);
        break;
      }
      case ")":
        if (vertexPath.isEmpty()) {
          throw new ParseException(
              "Closing bracket encountered when no nodes in tree were found.",
              0);
        }
        vertexPath.remove(vertexPath.size() - 1);
        children.remove(children.size() - 1);
        break;
      case "*":
        if (this.foot != null) {
          throw new ParseException(
              "Tried to set foot node twice. Only one foot node is allowed.",
              0);
        }
        this.foot = this.vertexes.get(this.vertexes.size() - 1);
        break;
      case "_":
        i = handleSubscript(tokens, i);
        break;
      case "<":
        leafOrder.add(tokens[i + 1]);
        leafGorns
            .add(this.vertexes.get(this.vertexes.size() - 1).getGornAddress());
        i += 2;
        break;
      default:
        handleChildNode(tokens, vertexPath, children, i);
        break;
      }
    }
  }

  private int handleSubscript(String[] tokens, int i) throws ParseException {
    i++;
    switch (tokens[i]) {
    case "NA":
      this.nA.add(this.vertexes.get(this.vertexes.size() - 1));
      break;
    case "OA":
      this.oA.add(this.vertexes.get(this.vertexes.size() - 1));
      break;
    default:
      throw new ParseException("Unknown subscript " + tokens[i], 0);
    }
    return i;
  }

  private void handleChildNode(String[] tokens, List<Vertex> vertexPath,
      List<Integer> children, int i) {
    Vertex vertex;
    if (tokens[i].equals(EPSILON)) {
      vertex = new Vertex("");
    } else {
      vertex = new Vertex(tokens[i]);
    }
    children.set(children.size() - 1, children.get(children.size() - 1) + 1);
    String gornAddress = vertexPath.get(vertexPath.size() - 1).getGornAddress()
        + "." + children.get(children.size() - 1).toString();
    vertex.setGornAddress(gornAddress);

    Edge edge = new Edge(vertexPath.get(vertexPath.size() - 1), vertex);
    this.edges.add(edge);
    addVertex(vertex);
  }

  private int openSubTree(String[] tokens, List<Vertex> vertexPath,
      List<Integer> children, int i) throws ParseException {
    i++;
    if (tokens[i].equals("(") || tokens[i].equals(")") || tokens[i]
        .equals("*")) {
      throw new ParseException(
          "Expecting label of root of subtree, found " + tokens[i]
              + " instead.", 0);
    }
    Vertex vertex;
    if (EPSILON.equals(tokens[i])) {
      vertex = new Vertex("");
    } else {
      vertex = new Vertex(tokens[i]);
    }
    if (this.root == null) {
      this.root = vertex;
      vertex.setGornAddress("");
    } else {
      children.set(children.size() - 1, children.get(children.size() - 1) + 1);
      vertex.setGornAddress(
          vertexPath.get(vertexPath.size() - 1).getGornAddress() + "."
              + children.get(children.size() - 1).toString());
      Edge edge = new Edge(vertexPath.get(vertexPath.size() - 1), vertex);
      this.edges.add(edge);
    }
    vertexPath.add(vertex);
    children.add(0);
    addVertex(vertex);
    return i;
  }

  /**
   * Creates a tree from a cfg rule. Hence, S -> A B would become (S A B).
   */
  public Tree(CfgProductionRule rule) throws ParseException {
      this("(" + rule.getLhs() + " " + String.join(" ",
          ("".equals(rule.getRhs()[0]) ? new String[] {EPSILON} : rule.getRhs()))
          + ")");
  }

  private Tree() {
    super();
  }

  public Vertex getRoot() {
    return this.root;
  }

  public Vertex getFoot() {
    return this.foot;
  }

  @Override public String toString() {
    if (stringRepresentation != null) {
      return stringRepresentation;
    }
    if (this.root == null) {
      stringRepresentation = "()";
      return stringRepresentation;
    }
    stringRepresentation = "("
        + (this.root.getLabel().isEmpty() ? EPSILON : this.root.getLabel())
        + (this.root.equals(foot) ? "*" : "") + (
        isInOA(this.root.getGornAddress()) ? "_OA" : "") + (
        isInNA(this.root.getGornAddress()) ? "_NA" : "") + " "
        + toStringAllChildren(this.root) + ")";
    return stringRepresentation;
  }

  /**
   * Retrieves all child nodes of a node. Each one is printed with its label
   * and its children in brackets and returned. For each child this function is
   * recursively called.
   */
  private String toStringAllChildren(Vertex node) {
    StringBuilder representation = new StringBuilder();
    List<Vertex> children = getChildren(node);
    for (Vertex child : children) {
      representation.append("(")
          .append(child.getLabel().isEmpty() ? EPSILON : child.getLabel())
          .append(child.equals(foot) ? "*" : "")
          .append(isInOA(child.getGornAddress()) ? "_OA" : "")
          .append(isInNA(child.getGornAddress()) ? "_NA" : "");
      if (leafGorns.contains(child.getGornAddress())) {
        representation.append('<');
        representation
            .append(leafOrder.get(leafGorns.indexOf(child.getGornAddress())));
        representation.append('>');
      }
      representation.append(" ").append(toStringAllChildren(child)).append(")");
    }
    return representation.toString();
  }

  /**
   * Returns a list of all child nodes of the given node.
   */
  public List<Vertex> getChildren(Vertex node) {
    List<Vertex> children = new ArrayList<>();
    for (Edge edge : this.edges) {
      if (edge.getFrom().equals(node)) {
        children.add(edge.getTo());
      }
    }
    children.sort(new PrecedenceComparator());
    return children;
  }

  public List<Vertex> getVertexes() {
    return this.vertexes;
  }

  /**
   * Returns true if a given node has at least one child.
   */
  boolean hasChildren(Vertex p) {
    return !getChildren(p).isEmpty();
  }

  /**
   * Takes a gorn address and returns the vertex the address belongs to.
   */
  public Vertex getNodeByGornAddress(String gornAddress) {
    if (gornAddressMap.containsKey(gornAddress)) {
      return gornAddressMap.get(gornAddress);
    }
    return null;
  }

  /**
   * Returns true if node is marked with null adjoin.
   */
  public boolean isInNA(String gornAddress) {
    for (Vertex p : this.nA) {
      if (p.getGornAddress().equals(gornAddress)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if node is marked with obligatory adjoin.
   */
  public boolean isInOA(String gornAddress) {
    for (Vertex p : this.oA) {
      if (p.getGornAddress().equals(gornAddress)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the height of the tree, that is the path from the root to the
   * deepest child, that is the longest gorn address.
   */
  public int getHeight() {
    int maxHeight = 0;
    for (Vertex p : this.vertexes) {
      String[] pSplit = p.getGornAddress().split("[.]");
      if (pSplit.length > maxHeight) {
        maxHeight = pSplit.length;
      }
    }
    return maxHeight;
  }

  /**
   * return the max width, that is the number of nodes in a layer. The layer is
   * the number of parts when splitting the gorn address at '.', hence root is
   * in layer 1, its children are layer 2 etc.
   */
  private int getWidthInLayer(int layer) {
    int width = 0;
    for (Vertex p : this.vertexes) {
      String[] pSplit = p.getGornAddress().split("[.]");
      if (pSplit.length == layer) {
        width++;
      }
    }
    return width;
  }

  /**
   * Returns the max width of the tree, that is the most nodes in a layer.
   */
  public int getWidth() {
    int width = 0;
    int nodes = 1;
    for (int i = 1; nodes != 0; i++) {
      nodes = getWidthInLayer(i);
      if (nodes > width) {
        width = nodes;
      }
    }
    return width;
  }

  /**
   * Returns the max width of the tree, that is the most number of characters in
   * any layer.
   */
  public int getWidthInChars() {
    return getWidthInChars(this.getRoot());
  }

  /**
   * Returns the max width of the subtree below the vertex. If it has children,
   * it sums up the width of its children and returns their width or its own in
   * chars, whatever is bigger. If it has no children, it returns its own width.
   */
  private int getWidthInChars(Vertex v) {
    int ownWidth = v.getLabel().length() + 1;
    int childWidth = 0;
    for (Vertex c : this.getChildren(v)) {
      childWidth += getWidthInChars(c) + 1;
    }
    return Math.max(ownWidth, childWidth);
  }

  /**
   * Returns the max width of the subtree below node p, that is the most nodes
   * in one layer where all nodes are dominated by p.
   */
  public int getWidthBelowNodeInNodes(Vertex p) {
    int width = 0;
    int nodes = 1;
    for (int i = p.getGornAddress().split("[.]").length + 1; nodes != 0; i++) {
      nodes = getWidthInLayerBelowNode(p, i);
      if (nodes > width) {
        width = nodes;
      }
    }
    return width;
  }

  /**
   * Returns the number of nodes in one layer that are dominated by p.
   */
  private int getWidthInLayerBelowNode(Vertex p, int layer) {
    int width = 0;
    for (Vertex node : this.vertexes) {
      if (!p.dominates(node.getGornAddress())) {
        continue;
      }
      String[] nodeSplit = node.getGornAddress().split("[.]");
      if (nodeSplit.length == layer) {
        width++;
      }
    }
    return width;
  }

  /**
   * Substitutes the given initalTree into this tree at the node at that gorn
   * address and returns the result as new Tree object. Clones all nodes for
   * doing so.
   * @param gorn The gorn address of the substitution node of this tree.
   * @param initialTree The tree to be substituted into this one.
   */
  public Tree substitute(String gorn, Tree initialTree) {
    Tree resultTree = new Tree();
    Vertex substitutionNode = null;
    for (int i = 0; i < vertexes.size(); i++) {
      if (vertexes.get(i).getGornAddress().equals(gorn)) {
        substitutionNode = vertexes.get(i);
        for (Vertex p : initialTree.vertexes) {
          Vertex newVertex = p.copy();
          newVertex.setGornAddress(gorn + p.getGornAddress());
          resultTree.addVertex(newVertex);
          markRootOrFootNode(resultTree);
        }
      } else {
        resultTree.addVertex(vertexes.get(i).copy());
        markRootOrFootNode(resultTree);
      }
    }
    for (Edge edge : this.edges) {
      Vertex newFrom =
          resultTree.getNodeByGornAddress(edge.getFrom().getGornAddress());
      Vertex newTo;
      if (edge.getTo().equals(substitutionNode)) {
        newTo = resultTree.getNodeByGornAddress(gorn);
      } else {
        newTo = resultTree.getNodeByGornAddress(edge.getTo().getGornAddress());
      }
      resultTree.edges.add(new Edge(newFrom, newTo));
    }
    return addEdgesToNewTree(gorn, initialTree, resultTree);
  }

  private void markRootOrFootNode(Tree newTree) {
    if (newTree.vertexes.get(newTree.vertexes.size() - 1).getGornAddress().isEmpty()) {
      newTree.root = newTree.vertexes.get(newTree.vertexes.size() - 1);
    } else if (newTree.vertexes.get(newTree.vertexes.size() - 1)
        .equals(this.foot)) {
      newTree.foot = newTree.vertexes.get(newTree.vertexes.size() - 1);
    }
  }

  /**
   * Adjoins the given auxiliary tree into this tree at the node at that gorn
   * address and returns the result as new Tree object. Clones all nodes for
   * doing so.
   */
  public Tree adjoin(String gorn, Tree auxTree) {
    Tree newTree = new Tree();
    Vertex adjNode = null;
    for (Vertex value : vertexes) {
      if (value.getGornAddress().equals(gorn)) {
        adjNode = value;
      } else {
        newTree.addVertex(value.copy());
      }
    }
    for (Vertex vertex : vertexes) {
      if (vertex.equals(this.foot)) {
        newTree.foot = newTree.getNodeByGornAddress(vertex.getGornAddress());
      }
      assert adjNode != null;
      if (adjNode.dominates(vertex.getGornAddress())) {
        Vertex p = newTree.getNodeByGornAddress(vertex.getGornAddress());
        String oldGorn = p.getGornAddress();
        p.setGornAddress(
            gorn + auxTree.getFoot().getGornAddress() + vertex.getGornAddress()
                .substring(adjNode.getGornAddress().length()));
        newTree.updateVertex(oldGorn, p);
      }
    }
    for (Vertex p : auxTree.vertexes) {
      Vertex newVertex = p.copy();
      newVertex.setGornAddress(gorn + p.getGornAddress());
      newTree.addVertex(newVertex);
    }
    newTree.root = newTree.getNodeByGornAddress("");
    for (Edge edge : this.edges) {
      if (edge.getFrom().equals(adjNode)) {
        copyEdgeFromAuxTreeToNewTree(gorn, auxTree, newTree, adjNode, edge);
      } else {
        assert adjNode != null;
        if (adjNode.dominates(edge.getFrom().getGornAddress())) {
          copyEdgeFromAuxTreeToNewTree(gorn, auxTree, newTree, adjNode, edge);
        } else {
          Vertex newFrom =
              newTree.getNodeByGornAddress(edge.getFrom().getGornAddress());
          Vertex newTo =
              newTree.getNodeByGornAddress(edge.getTo().getGornAddress());
          newTree.edges.add(new Edge(newFrom, newTo));
        }
      }
    }
    return addEdgesToNewTree(gorn, auxTree, newTree);
  }

  private void copyEdgeFromAuxTreeToNewTree(String gorn, Tree auxTree,
      Tree newTree, Vertex adjNode, Edge edge) {
    Vertex newFrom = newTree.getNodeByGornAddress(
        gorn + auxTree.getFoot().getGornAddress() + edge.getFrom()
            .getGornAddress().substring(adjNode.getGornAddress().length()));
    Vertex newTo = newTree.getNodeByGornAddress(
        gorn + auxTree.getFoot().getGornAddress() + edge.getTo()
            .getGornAddress().substring(adjNode.getGornAddress().length()));
    newTree.edges.add(new Edge(newFrom, newTo));
  }

  private Tree addEdgesToNewTree(String gorn, Tree edgeTree, Tree newTree) {
    for (Edge edge : edgeTree.edges) {
      Vertex newFrom =
          newTree.getNodeByGornAddress(gorn + edge.getFrom().getGornAddress());
      Vertex newTo =
          newTree.getNodeByGornAddress(gorn + edge.getTo().getGornAddress());
      newTree.edges.add(new Edge(newFrom, newTo));
    }
    return newTree;
  }

  /**
   * Returns an equivalent Tree where all nodes have at most 2 children. For
   * creating new nodes it creates labels not in the list and adds them to the
   * list.
   */
  public Tree getBinarizedTree(List<String> newNonterminals) {
    Tree newTree = this;
    boolean changed;
    do {
      changed = false;
      for (Vertex p : newTree.getVertexes()) {
        String gornAddress = p.getGornAddress();
        if (!gornAddress.isEmpty()
            && gornAddress.charAt(gornAddress.length() - 1) == '3') {
          int i = 1;
          String newNonterminal = "X" + i;
          i++;
          while (newNonterminals.contains(newNonterminal)) {
            newNonterminal = "X" + i;
            i++;
          }
          Tree newestTree = new Tree();
          newestTree.foot = newTree.foot;
          newestTree.root = newTree.root;
          newestTree.oA = newTree.oA;
          newestTree.nA = newTree.nA;
          for (Vertex q : newTree.getVertexes()) {
            newestTree.addVertex(q);
          }
          Vertex parent =
              newTree.getNodeByGornAddress(p.getGornAddressOfParent());
          Vertex firstChild =
              newTree.getNodeByGornAddress(parent.getGornAddress() + ".1");
          Vertex newNode = new Vertex(newNonterminal);
          newNode.setGornAddress(parent.getGornAddress() + ".2");
          newestTree.addVertex(newNode);
          newestTree.edges.add(new Edge(parent, newNode));
          for (Edge edge : newTree.edges) {
            if (edge.getFrom().equals(parent)) {
              if (edge.getTo().getGornAddress().endsWith(".1")) {
                newestTree.edges.add(edge);
              } else {
                newestTree.edges.add(new Edge(newNode, edge.getTo()));
                String oldGorn = edge.getTo().getGornAddress();
                String[] oldGornSplit = oldGorn.split("[.]");
                String newLastDigit = String.valueOf(
                    Integer.parseInt(oldGornSplit[oldGornSplit.length - 1])
                        - 1);
                edge.getTo().setGornAddress(
                    newNode.getGornAddress() + "." + newLastDigit);
              }
            } else {
              newestTree.edges.add(edge);
              Vertex nodeCheckGornAddress = edge.getTo();
              if (parent.dominates(nodeCheckGornAddress.getGornAddress())
                  && !firstChild
                  .dominates(nodeCheckGornAddress.getGornAddress())) {
                String[] oldGornSplit =
                    nodeCheckGornAddress.getGornAddress().split("[.]");
                String[] parentGornSplit = parent.getGornAddress().split("[.]");
                String newLastDigit = String.valueOf(
                    Integer.parseInt(oldGornSplit[parentGornSplit.length + 1])
                        - 1);
                String newGornAddress =
                    newNode.getGornAddress() + "." + newLastDigit + "." + String
                        .join(".", ArrayUtils
                            .getSubSequenceAsArray(oldGornSplit,
                                parentGornSplit.length + 1,
                                oldGornSplit.length));
                nodeCheckGornAddress.setGornAddress(newGornAddress);
              }
            }
          }
          // bonus points for allowing the same for trees with crossing edges,
          // for that edit LeafGorns accordingly.
          newNonterminals.add(newNonterminal);
          changed = true;
          newTree = newestTree;
          break;
        }
      }
    } while (changed);
    return newTree;
  }

  public ArrayList<String> getLeafOrder() {
    return this.leafOrder;
  }

  public ArrayList<String> getLeafGorns() {
    return this.leafGorns;
  }

  public boolean isBinarized() {
    for (Vertex p : this.getVertexes()) {
      String gornAddress = p.getGornAddress();
      if (!gornAddress.isEmpty()
          && gornAddress.charAt(gornAddress.length() - 1) == '3') {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Tree){
      return o.toString().equals(this.toString());
    }
    if (o instanceof String) {
      return o.equals(this.toString());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  /**
   * Returns true if structure tree1 is a subtree of this tree, meaning there
   * is a node constellation with the same labels.
   */
  public boolean contains(Tree tree1) {
    return contains(root, tree1);
  }

  private boolean contains(Vertex node, Tree tree1) {
    boolean sameStructure =
        sameStructure(this, node, tree1, tree1.getRoot());
    if (sameStructure) {
      return true;
    }
    boolean containsInChildren = false;
    for (Vertex child : getChildren(node)) {
      containsInChildren = containsInChildren || contains(child, tree1);
    }
    return containsInChildren;
  }

  private static boolean sameStructure(
      Tree tree1, Vertex node1, Tree tree2, Vertex node2) {
    if (!node1.getLabel().equals(node2.getLabel())){
      return false;
    }
    // or does this node just need to have more children than the root of tree1?
    if (tree2.getChildren(node2).isEmpty()) {
      return true;
    }
    if (tree1.getChildren(node1).size() != tree2.getChildren(node2).size()) {
      return false;
    }
    boolean sameStructure = true;
    for (int i = 0; i < tree1.getChildren(node1).size(); i++) {
      Vertex child1 = tree1.getChildren(node1).get(i);
      Vertex child2 = tree2.getChildren(node2).get(i);
      sameStructure =
          sameStructure && sameStructure(tree1, child1, tree2, child2);
    }
    return sameStructure;
  }

  /**
   * Returns true if all leaves are epsilon nodes
   */
  public boolean allLeavesAreEpsilon() {
    return allLeavesAreEpsilon(root);
  }

  private boolean allLeavesAreEpsilon(Vertex node) {
    if (!hasChildren(node)) {
      return "".equals(node.getLabel());
    } else  {
      boolean allLeavesAreEpsilon = true;
      for (Vertex child : this.getChildren(node)){
        allLeavesAreEpsilon = allLeavesAreEpsilon && allLeavesAreEpsilon(child);
      }
      return allLeavesAreEpsilon;
    }
  }

  /**
   * Return true if the tree has one substitution node and all other leaves are
   * epsilon.
   */
  public boolean oneSubstitutionNodeRestEpsilon(List<String> nonterminals) {
    List<String> types = getTypesOfLeaves(root, nonterminals);
    int substitutionNodes = 0;
    int terminals = 0;
    for (String type: types) {
      switch (type) {
      case "nt":
        substitutionNodes++;
        break;
      case "t":
        terminals++;
      }
    }
    return substitutionNodes == 1 && terminals == 0;
  }

  private List<String> getTypesOfLeaves(Vertex node, List<String> nonterminals) {
    List<String> types = new ArrayList<>();
    if (!hasChildren(node)) {
      String label = node.getLabel();
      if ("".equals(label)) {
        types.add("epsilon");
      } else if (nonterminals.contains(label)) {
        types.add("nt");
      } else  {
        types.add("t");
      }
    } else  {
      for (Vertex child : this.getChildren(node)){
        types.addAll(getTypesOfLeaves(child, nonterminals));
      }
    }
    return types;
  }

  public void addVertex(Vertex vertex) {
    this.vertexes.add(vertex);
    this.gornAddressMap.put(vertex.getGornAddress(), vertex);
  }

  public void updateVertex(String oldGorn, Vertex vertex) {
    this.gornAddressMap.remove(oldGorn);
    this.gornAddressMap.put(vertex.getGornAddress(), vertex);
  }
}
