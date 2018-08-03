package common.tag;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import common.ArrayUtils;
import common.cfg.CfgProductionRule;

/** A non-recursive representation of a context-free tree consisting of
 * vertexes, directed edges and in the context of TAG may have a special foot
 * node. */
public class Tree {

  private final List<Vertex> vertexes = new ArrayList<Vertex>();
  private final List<Edge> edges = new ArrayList<Edge>();
  private Vertex root = null;
  private Vertex foot = null;
  private List<Vertex> nA = new ArrayList<Vertex>();
  private List<Vertex> oA = new ArrayList<Vertex>();

  private final ArrayList<String> leafOrder = new ArrayList<String>();
  private final ArrayList<String> leafGorns = new ArrayList<String>();

  /** Takes a string in bracket format, tokenizes it and parses the actual tree
   * from it. */
  public Tree(String tree) throws ParseException {
    String[] tokens = tokenize(tree);
    List<Vertex> vertexPath = new ArrayList<Vertex>();
    List<Integer> children = new ArrayList<Integer>();
    for (int i = 0; i < tokens.length; i++) {
      switch (tokens[i]) {
      case "(": {
        i = openSubTree(tokens, vertexPath, children, i);
        break;
      }
      case ")":
        if (vertexPath.size() == 0) {
          throw new ParseException(
            "Closing bracket encountered when no nodes in tree were found.", 0);
        }
        vertexPath.remove(vertexPath.size() - 1);
        children.remove(children.size() - 1);
        break;
      case "*":
        if (this.foot != null) {
          throw new ParseException(
            "Tried to set foot node twice. Only one foot node is allowed.", 0);
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
    if (tokens[i].equals("ε")) {
      vertex = new Vertex("");
    } else {
      vertex = new Vertex(tokens[i]);
    }
    children.set(children.size() - 1, children.get(children.size() - 1) + 1);
    vertex.setGornaddress(vertexPath.get(vertexPath.size() - 1).getGornAddress()
      + "." + children.get(children.size() - 1).toString());

    Edge edge = new Edge(vertexPath.get(vertexPath.size() - 1), vertex);
    this.edges.add(edge);
    this.vertexes.add(vertex);
  }

  private int openSubTree(String[] tokens, List<Vertex> vertexPath,
    List<Integer> children, int i) throws ParseException {
    i++;
    if (tokens[i].equals("(") || tokens[i].equals(")")
      || tokens[i].equals("*")) {
      throw new ParseException(
        "Expecting label of root of subtree, found " + tokens[i] + " instead.",
        0);
    }
    Vertex vertex = new Vertex(tokens[i]);
    if (this.root == null) {
      this.root = vertex;
      vertex.setGornaddress("");
    } else {
      children.set(children.size() - 1, children.get(children.size() - 1) + 1);
      vertex
        .setGornaddress(vertexPath.get(vertexPath.size() - 1).getGornAddress()
          + "." + children.get(children.size() - 1).toString());
      Edge edge = new Edge(vertexPath.get(vertexPath.size() - 1), vertex);
      this.edges.add(edge);
    }
    vertexPath.add(vertex);
    children.add(0);
    this.vertexes.add(vertex);
    return i;
  }

  /** Creates a tree from a cfg rule. Hence S -> A B would become (S A B). */
  public Tree(CfgProductionRule rule) throws ParseException {
    this("(" + rule.getLhs() + " "
      + String.join(" ",
        ("".equals(rule.getRhs()[0]) ? new String[] {"ε"} : rule.getRhs()))
      + ")");
  }

  private Tree() {
    super();
  }

  /** Actually does the tokenization by throwing away spaces, returning '(', ')'
   * '*' and '_' as single char tokens and all other continuous string
   * concatenations each as a token. */
  private String[] tokenize(String tree) {
    ArrayList<String> tokens = new ArrayList<String>();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < tree.length(); i++) {
      if (tree.charAt(i) == ' ') {
        if (builder.length() > 0) {
          tokens.add(builder.toString());
          builder = new StringBuilder();
        }
      } else if ((tree.charAt(i) == '(' || tree.charAt(i) == ')'
        || tree.charAt(i) == '*') || tree.charAt(i) == '_'
        || tree.charAt(i) == '<' || tree.charAt(i) == '>') {
        if (builder.length() > 0) {
          tokens.add(builder.toString());
          builder = new StringBuilder();
        }
        tokens.add(String.valueOf(tree.charAt(i)));
      } else {
        builder.append(String.valueOf(tree.charAt(i)));
      }
    }
    return tokens.toArray(new String[tokens.size()]);
  }

  public Vertex getRoot() {
    return this.root;
  }

  public Vertex getFoot() {
    return this.foot;
  }

  @Override public String toString() {
    return "(" + (this.root.getLabel().equals("") ? "ε" : this.root.getLabel())
      + (this.root.equals(foot) ? "*" : "")
      + (isInOA(this.root.getGornAddress()) ? "_OA" : "")
      + (isInNA(this.root.getGornAddress()) ? "_NA" : "") + " "
      + toStringAllChildren(this.root) + ")";
  }

  /** Retrieves all child nodes of a node. Each one is printed with its label
   * and its children in brackets and returned. For each child this function is
   * recursively called. */
  private String toStringAllChildren(Vertex node) {
    StringBuilder representation = new StringBuilder();
    List<Vertex> children = getChildren(node);
    for (Vertex child : children) {
      representation.append("(")
        .append(child.getLabel().equals("") ? "ε" : child.getLabel())
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

  /** Returns a list of all child nodes of the given node. */
  public List<Vertex> getChildren(Vertex node) {
    List<Vertex> children = new ArrayList<Vertex>();
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

  /** Returns true if a given node has at least one child. */
  boolean hasChildren(Vertex p) {
    return !getChildren(p).isEmpty();
  }

  /** Takes a gorn address and returns the vertex the address belongs to. */
  public Vertex getNodeByGornAdress(String gornAddress) {
    for (Vertex p : this.vertexes) {
      if (p.getGornAddress().equals(gornAddress)) {
        return p;
      }
    }
    return null;
  }

  /** Returns true if node is marked with null adjoin. */
  public boolean isInNA(String gornAddress) {
    for (Vertex p : this.nA) {
      if (p.getGornAddress().equals(gornAddress)) {
        return true;
      }
    }
    return false;
  }

  /** Returns true if node is marked with obligatory adjoin. */
  public boolean isInOA(String gornAddress) {
    for (Vertex p : this.oA) {
      if (p.getGornAddress().equals(gornAddress)) {
        return true;
      }
    }
    return false;
  }

  /** Returns the height of the tree, that is the path from the root to the
   * deepest child, that is the longest gorn address. */
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

  /** return the max width, that is the number of nodes in a layer. The layer is
   * the number of parts when splitting the gorn address at '.', hence root is
   * in layer 1, its children are layer 2 etc. */
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

  /** Returns the max width of the tree, that is the most nodes in a layer. */
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

  /** Returns the max width of the subtree below node p, that is the most nodes
   * in one layer where all nodes are dominated by p. */
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

  /** Returns the number of nodes in one layer that are dominated by p. */
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

  /** Substitutes the given initaltree into this tree at the node at that gorn
   * address and returns the result as new Tree object. Clones all nodes for
   * doing so. */
  public Tree substitute(String gorn, Tree initialTree) {
    Tree newTree = new Tree();
    Vertex substNode = null;
    for (int i = 0; i < vertexes.size(); i++) {
      if (vertexes.get(i).getGornAddress().equals(gorn)) {
        substNode = vertexes.get(i);
        for (Vertex p : initialTree.vertexes) {
          newTree.vertexes.add(p.clone());
          newTree.vertexes.get(newTree.vertexes.size() - 1)
            .setGornaddress(gorn + p.getGornAddress());
        }
      } else {
        newTree.vertexes.add(vertexes.get(i).clone());
      }
      if (vertexes.get(i).getGornAddress().equals("")) {
        newTree.root = newTree.vertexes.get(newTree.vertexes.size() - 1);
      } else if (vertexes.get(i).equals(this.foot)) {
        newTree.foot = newTree.vertexes.get(newTree.vertexes.size() - 1);
      }
    }
    for (Edge edge : this.edges) {
      if (edge.getTo().equals(substNode)) {
        Vertex newFrom =
          newTree.getNodeByGornAdress(edge.getFrom().getGornAddress());
        Vertex newTo = newTree.getNodeByGornAdress(gorn);
        newTree.edges.add(new Edge(newFrom, newTo));
      } else {
        Vertex newFrom =
          newTree.getNodeByGornAdress(edge.getFrom().getGornAddress());
        Vertex newTo =
          newTree.getNodeByGornAdress(edge.getTo().getGornAddress());
        newTree.edges.add(new Edge(newFrom, newTo));
      }
    }
    for (Edge edge : initialTree.edges) {
      Vertex newFrom =
        newTree.getNodeByGornAdress(gorn + edge.getFrom().getGornAddress());
      Vertex newTo =
        newTree.getNodeByGornAdress(gorn + edge.getTo().getGornAddress());
      newTree.edges.add(new Edge(newFrom, newTo));
    }
    return newTree;
  }

  /** Adjoins the given auxiliary tree into this tree at the node at that gorn
   * address and returns the result as new Tree object. Clones all nodes for
   * doing so. */
  public Tree adjoin(String gorn, Tree auxTree) {
    Tree newTree = new Tree();
    Vertex adjNode = null;
    for (int i = 0; i < vertexes.size(); i++) {
      if (vertexes.get(i).getGornAddress().equals(gorn)) {
        adjNode = vertexes.get(i);
      } else {
        newTree.vertexes.add(vertexes.get(i).clone());
      }
    }
    for (Vertex vertex : vertexes) {
      if (vertex.equals(this.foot)) {
        newTree.foot = newTree.getNodeByGornAdress(vertex.getGornAddress());
      }
      if (adjNode.dominates(vertex.getGornAddress())) {
        Vertex p = newTree.getNodeByGornAdress(vertex.getGornAddress());
        p.setGornaddress(gorn + auxTree.getFoot().getGornAddress() + vertex
          .getGornAddress().substring(adjNode.getGornAddress().length()));
      }
    }
    for (Vertex p : auxTree.vertexes) {
      newTree.vertexes.add(p.clone());
      newTree.vertexes.get(newTree.vertexes.size() - 1)
        .setGornaddress(gorn + p.getGornAddress());
    }
    newTree.root = newTree.getNodeByGornAdress("");
    for (Edge edge : this.edges) {
      if (edge.getFrom().equals(adjNode)) {
        Vertex newFrom = newTree.getNodeByGornAdress(
          gorn + auxTree.getFoot().getGornAddress() + edge.getFrom()
            .getGornAddress().substring(adjNode.getGornAddress().length()));
        Vertex newTo = newTree.getNodeByGornAdress(
          gorn + auxTree.getFoot().getGornAddress() + edge.getTo()
            .getGornAddress().substring(adjNode.getGornAddress().length()));
        newTree.edges.add(new Edge(newFrom, newTo));
      } else if (adjNode.dominates(edge.getFrom().getGornAddress())) {
        Vertex newFrom = newTree.getNodeByGornAdress(
          gorn + auxTree.getFoot().getGornAddress() + edge.getFrom()
            .getGornAddress().substring(adjNode.getGornAddress().length()));
        Vertex newTo = newTree.getNodeByGornAdress(
          gorn + auxTree.getFoot().getGornAddress() + edge.getTo()
            .getGornAddress().substring(adjNode.getGornAddress().length()));
        newTree.edges.add(new Edge(newFrom, newTo));
      } else {
        Vertex newFrom =
          newTree.getNodeByGornAdress(edge.getFrom().getGornAddress());
        Vertex newTo =
          newTree.getNodeByGornAdress(edge.getTo().getGornAddress());
        newTree.edges.add(new Edge(newFrom, newTo));
      }
    }
    for (Edge edge : auxTree.edges) {
      Vertex newFrom =
        newTree.getNodeByGornAdress(gorn + edge.getFrom().getGornAddress());
      Vertex newTo =
        newTree.getNodeByGornAdress(gorn + edge.getTo().getGornAddress());
      newTree.edges.add(new Edge(newFrom, newTo));
    }
    return newTree;
  }

  /** Returns an equivalent Tree where all nodes have at most 2 children. For
   * creating new nodes it creates labels not in the list and adds them to the
   * list. */
  public Tree getBinarizedTree(ArrayList<String> newNonterminals) {
    Tree newTree = this;
    boolean changed;
    do {
      changed = false;
      for (Vertex p : newTree.getVertexes()) {
        String gornAddress = p.getGornAddress();
        if (gornAddress.length() > 0
          && gornAddress.charAt(gornAddress.length() - 1) == '3') {
          int i = 1;
          String newNonterminal = "X" + String.valueOf(i);
          i++;
          while (newNonterminals.contains(newNonterminal)) {
            newNonterminal = "X" + String.valueOf(i);
            i++;
          }
          Tree newestTree = new Tree();
          newestTree.foot = newTree.foot;
          newestTree.root = newTree.root;
          newestTree.oA = newTree.oA;
          newestTree.nA = newTree.nA;
          newestTree.vertexes.addAll(newTree.getVertexes());
          Vertex parent =
            newTree.getNodeByGornAdress(p.getGornAddressOfParent());
          Vertex firstChild =
            newTree.getNodeByGornAdress(parent.getGornAddress() + ".1");
          Vertex newNode = new Vertex(newNonterminal);
          newNode.setGornaddress(parent.getGornAddress() + ".2");
          newestTree.vertexes.add(newNode);
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
                  Integer.parseInt(oldGornSplit[oldGornSplit.length - 1]) - 1);
                edge.getTo().setGornaddress(
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
                  newNode.getGornAddress() + "." + newLastDigit + "."
                    + String.join(".",
                      ArrayUtils.getSubSequenceAsArray(oldGornSplit,
                        parentGornSplit.length + 1, oldGornSplit.length));
                nodeCheckGornAddress.setGornaddress(newGornAddress);
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
      if (gornAddress.length() > 0
        && gornAddress.charAt(gornAddress.length() - 1) == '3') {
        return false;
      }
    }
    return true;
  }
}
