package common.tag;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import common.cfg.CfgProductionRule;

/** A non-recursive representation of a context-free tree consisting of
 * vertexes, directed edges and in the context of TAG may have a special foot
 * node. */
public class Tree {

  protected final List<Vertex> vertexes = new LinkedList<Vertex>();
  private final List<Edge> edges = new LinkedList<Edge>();
  protected Vertex root = null;
  protected Vertex foot = null;
  private final List<Vertex> NA = new LinkedList<Vertex>();
  private final List<Vertex> OA = new LinkedList<Vertex>();

  /** Takes a string in bracket format, tokenizes it and parses the actual tree
   * from it. */
  public Tree(String tree) throws ParseException {
    String[] tokens = tokenize(tree);
    List<Vertex> vertexpath = new LinkedList<Vertex>();
    List<Integer> children = new LinkedList<Integer>();
    for (int i = 0; i < tokens.length; i++) {
      switch (tokens[i]) {
      case "(": {
        i = openSubTree(tokens, vertexpath, children, i);
        break;
      }
      case ")":
        if (vertexpath.size() == 0) {
          throw new ParseException(
            "Closing bracket encountered when no nodes in tree were found.", 0);
        }
        vertexpath.remove(vertexpath.size() - 1);
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
      default: {
        handleChildNode(tokens, vertexpath, children, i);
        break;
      }
      }
    }
  }

  protected int handleSubscript(String[] tokens, int i) throws ParseException {
    i++;
    switch (tokens[i]) {
    case "NA":
      this.NA.add(this.vertexes.get(this.vertexes.size() - 1));
      break;
    case "OA":
      this.OA.add(this.vertexes.get(this.vertexes.size() - 1));
      break;
    default:
      throw new ParseException("Unknown subscript " + tokens[i], 0);
    }
    return i;
  }

  protected void handleChildNode(String[] tokens, List<Vertex> vertexpath,
    List<Integer> children, int i) {
    Vertex vertex;
    if (tokens[i].equals("ε")) {
      vertex = new Vertex("");
    } else {
      vertex = new Vertex(tokens[i]);
    }
    children.set(children.size() - 1, children.get(children.size() - 1) + 1);
    vertex.setGornaddress(vertexpath.get(vertexpath.size() - 1).getGornaddress()
      + "." + children.get(children.size() - 1).toString());

    Edge edge = new Edge(vertexpath.get(vertexpath.size() - 1), vertex);
    this.edges.add(edge);
    this.vertexes.add(vertex);
  }

  protected int openSubTree(String[] tokens, List<Vertex> vertexpath,
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
        .setGornaddress(vertexpath.get(vertexpath.size() - 1).getGornaddress()
          + "." + children.get(children.size() - 1).toString());
      Edge edge = new Edge(vertexpath.get(vertexpath.size() - 1), vertex);
      this.edges.add(edge);
    }
    vertexpath.add(vertex);
    children.add(0);
    this.vertexes.add(vertex);
    return i;
  }

  /** Creates a tree from a cfg rule. Hence S -> A B would become (S A B). */
  public Tree(CfgProductionRule rule) throws ParseException {
    this("(" + rule.getLhs() + " " + String.join(" ", rule.getRhs()) + ")");
  }

  protected Tree() {
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
        || tree.charAt(i) == '*') || tree.charAt(i) == '_') {
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
      + (isInOA(this.root.getGornaddress()) ? "_OA" : "")
      + (isInNA(this.root.getGornaddress()) ? "_NA" : "") + " "
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
        .append(isInOA(child.getGornaddress()) ? "_OA" : "")
        .append(isInNA(child.getGornaddress()) ? "_NA" : "").append(" ")
        .append(toStringAllChildren(child)).append(")");
    }
    return representation.toString();
  }

  /** Returns a list of all child nodes of the given node. */
  public List<Vertex> getChildren(Vertex node) {
    List<Vertex> children = new LinkedList<Vertex>();
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
  public Vertex getNodeByGornAdress(String gornaddress) {
    for (Vertex p : this.vertexes) {
      if (p.getGornaddress().equals(gornaddress)) {
        return p;
      }
    }
    return null;
  }

  /** Returns true if node is marked with null adjoin. */
  public boolean isInNA(String gornaddress) {
    for (Vertex p : this.NA) {
      if (p.getGornaddress().equals(gornaddress)) {
        return true;
      }
    }
    return false;
  }

  /** Returns true if node is marked with obligatory adjoin. */
  public boolean isInOA(String gornaddress) {
    for (Vertex p : this.OA) {
      if (p.getGornaddress().equals(gornaddress)) {
        return true;
      }
    }
    return false;
  }

  /** Returns the height of the tree, that is the path from the root to the
   * deepest child, that is the longest gorn address. */
  public int getHeight() {
    int maxheight = 0;
    for (Vertex p : this.vertexes) {
      String[] psplit = p.getGornaddress().split("[.]");
      if (psplit.length > maxheight) {
        maxheight = psplit.length;
      }
    }
    return maxheight;
  }

  /** return the max width, that is the number of nodes in a layer. The layer is
   * the number of parts when splitting the gorn address at '.', hence root is
   * in layer 1, its children are layer 2 etc. */
  private int getWidthInLayer(int layer) {
    int width = 0;
    for (Vertex p : this.vertexes) {
      String[] psplit = p.getGornaddress().split("[.]");
      if (psplit.length == layer) {
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
  public int getWidthBelowNode(Vertex p) {
    int width = 0;
    int nodes = 1;
    for (int i = p.getGornaddress().split("[.]").length + 1; nodes != 0; i++) {
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
      if (p.dominates(node.getGornaddress())) {
        String[] nodesplit = node.getGornaddress().split("[.]");
        if (nodesplit.length == layer) {
          width++;
        }
      }
    }
    return width;
  }

  /** Substitutes the given initaltree into this tree at the node at that gorn
   * address and returns the result as new Tree object. Clones all nodes for
   * doing so. */
  public Tree substitute(String gorn, Tree initialtree) {
    Tree newtree = new Tree();
    Vertex substnode = null;
    for (int i = 0; i < vertexes.size(); i++) {
      if (vertexes.get(i).getGornaddress().equals(gorn)) {
        substnode = vertexes.get(i);
        for (Vertex p : initialtree.vertexes) {
          newtree.vertexes.add(p.clone());
          newtree.vertexes.get(newtree.vertexes.size() - 1)
            .setGornaddress(gorn + p.getGornaddress());
        }
      } else {
        newtree.vertexes.add(vertexes.get(i).clone());
      }
      if (vertexes.get(i).getGornaddress().equals("")) {
        newtree.root = newtree.vertexes.get(newtree.vertexes.size() - 1);
      } else if (vertexes.get(i).equals(this.foot)) {
        newtree.foot = newtree.vertexes.get(newtree.vertexes.size() - 1);
      }
    }
    for (Edge edge : this.edges) {
      if (edge.getTo().equals(substnode)) {
        Vertex newfrom =
          newtree.getNodeByGornAdress(edge.getFrom().getGornaddress());
        Vertex newto = newtree.getNodeByGornAdress(gorn);
        newtree.edges.add(new Edge(newfrom, newto));
      } else {
        Vertex newfrom =
          newtree.getNodeByGornAdress(edge.getFrom().getGornaddress());
        Vertex newto =
          newtree.getNodeByGornAdress(edge.getTo().getGornaddress());
        newtree.edges.add(new Edge(newfrom, newto));
      }
    }
    for (Edge edge : initialtree.edges) {
      Vertex newfrom =
        newtree.getNodeByGornAdress(gorn + edge.getFrom().getGornaddress());
      Vertex newto =
        newtree.getNodeByGornAdress(gorn + edge.getTo().getGornaddress());
      newtree.edges.add(new Edge(newfrom, newto));
    }
    return newtree;
  }

  /** Adjoins the given auxiliary tree into this tree at the node at that gorn
   * address and returns the result as new Tree object. Clones all nodes for
   * doing so. */
  public Tree adjoin(String gorn, Tree auxtree) {
    Tree newtree = new Tree();
    Vertex adjnode = null;
    for (int i = 0; i < vertexes.size(); i++) {
      if (vertexes.get(i).getGornaddress().equals(gorn)) {
        adjnode = vertexes.get(i);
      } else {
        newtree.vertexes.add(vertexes.get(i).clone());
      }
    }
    for (Vertex vertex : vertexes) {
      if (vertex.equals(this.foot)) {
        newtree.foot = newtree.getNodeByGornAdress(vertex.getGornaddress());
      }
      if (adjnode.dominates(vertex.getGornaddress())) {
        Vertex p = newtree.getNodeByGornAdress(vertex.getGornaddress());
        p.setGornaddress(gorn + auxtree.getFoot().getGornaddress() + vertex
          .getGornaddress().substring(adjnode.getGornaddress().length()));
      }
    }
    for (Vertex p : auxtree.vertexes) {
      newtree.vertexes.add(p.clone());
      newtree.vertexes.get(newtree.vertexes.size() - 1)
        .setGornaddress(gorn + p.getGornaddress());
    }
    newtree.root = newtree.getNodeByGornAdress("");
    for (Edge edge : this.edges) {
      if (edge.getFrom().equals(adjnode)) {
        Vertex newfrom = newtree.getNodeByGornAdress(
          gorn + auxtree.getFoot().getGornaddress() + edge.getFrom()
            .getGornaddress().substring(adjnode.getGornaddress().length()));
        Vertex newto = newtree.getNodeByGornAdress(
          gorn + auxtree.getFoot().getGornaddress() + edge.getTo()
            .getGornaddress().substring(adjnode.getGornaddress().length()));
        newtree.edges.add(new Edge(newfrom, newto));
      } else if (adjnode.dominates(edge.getFrom().getGornaddress())) {
        Vertex newfrom = newtree.getNodeByGornAdress(
          gorn + auxtree.getFoot().getGornaddress() + edge.getFrom()
            .getGornaddress().substring(adjnode.getGornaddress().length()));
        Vertex newto = newtree.getNodeByGornAdress(
          gorn + auxtree.getFoot().getGornaddress() + edge.getTo()
            .getGornaddress().substring(adjnode.getGornaddress().length()));
        newtree.edges.add(new Edge(newfrom, newto));
      } else {
        Vertex newfrom =
          newtree.getNodeByGornAdress(edge.getFrom().getGornaddress());
        Vertex newto =
          newtree.getNodeByGornAdress(edge.getTo().getGornaddress());
        newtree.edges.add(new Edge(newfrom, newto));
      }
    }
    for (Edge edge : auxtree.edges) {
      Vertex newfrom =
        newtree.getNodeByGornAdress(gorn + edge.getFrom().getGornaddress());
      Vertex newto =
        newtree.getNodeByGornAdress(gorn + edge.getTo().getGornaddress());
      newtree.edges.add(new Edge(newfrom, newto));
    }
    return newtree;
  }
}
