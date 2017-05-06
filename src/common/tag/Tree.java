package common.tag;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Tree {

  private List<Vertex> vertexes = new LinkedList<Vertex>();
  private List<Edge> edges = new LinkedList<Edge>();
  private Vertex root = null;
  private Vertex foot = null;

  public Tree(String tree) throws ParseException {
    String[] tokens = tokenize(tree);
    List<Vertex> vertexpath = new LinkedList<Vertex>();
    List<Integer> children = new LinkedList<Integer>();
    for (int i = 0; i < tokens.length; i++) {
      if (tokens[i].equals("(")) {
        i++;
        if (tokens[i].equals("(") || tokens[i].equals(")") || tokens[i].equals("*")) {
          throw new ParseException(
            "Expecting label of root of subtree, found " + tokens[i] + " instead.",
            0);
        }
        Vertex vertex = new Vertex(tokens[i]);
        if (this.root == null) {
          this.root = vertex;
          vertex.setGornaddress("");
        } else {
          children.set(children.size()-1, children.get(children.size()-1)+1);
          vertex.setGornaddress(vertexpath.get(vertexpath.size() - 1).getGornaddress() + "." + children.get(children.size()-1).toString());
          Edge edge = new Edge(vertexpath.get(vertexpath.size() - 1),
            vertex);
          this.edges.add(edge);
        }
        vertexpath.add(vertex);
        children.add(0);
        this.vertexes.add(vertex);
      } else if (tokens[i].equals(")")) {
        if (vertexpath.size() == 0) {
          throw new ParseException(
            "Closing bracket encountered when no nodes in tree were found.",
            0);
        }
        vertexpath.remove(vertexpath.size() - 1);
        children.remove(children.size() -1);
      } else if (tokens[i].equals("*")) {
        if (this.foot != null) {
          throw new ParseException(
            "Tried to set foot node twice. Only one foot node is allowed.", 0);
        }
        this.foot = this.vertexes.get(this.vertexes.size() - 1);
      } else {
        // now this can only be children of the last vertex in path
        Vertex vertex = new Vertex(tokens[i]);
          children.set(children.size()-1, children.get(children.size()-1)+1);
          vertex.setGornaddress(vertexpath.get(vertexpath.size() - 1).getGornaddress() + "." + children.get(children.size()-1).toString());
     
          Edge edge = new Edge(vertexpath.get(vertexpath.size() - 1),
            vertex);
          this.edges.add(edge);
          this.vertexes.add(vertex);
      }
    }
  }

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
        || tree.charAt(i) == '*')) {
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

  public String l(Vertex v) {
    return v.getLabel();
  }
  
  public Vertex getRoot() {
    return this.root;
  }
  
  public Vertex getFoot() {
    return this.foot;
  }

  @Override public String toString() {
    StringBuilder representation = new StringBuilder();
    representation.append(
      "(" + this.root.getLabel() + " " + toStringAllChildren(this.root) + ")");
    return representation.toString();
  }

  private String toStringAllChildren(Vertex node) {
    StringBuilder representation = new StringBuilder();
    List<Vertex> children = getChildren(node);
    for (Vertex child : children) {
      representation
        .append("(" + child.getLabel() + (child.equals(foot) ? "*" : "") + " "
          + toStringAllChildren(child) + ")");
    }
    return representation.toString();
  }

  private List<Vertex> getChildren(Vertex node) {
    List<Vertex> children = new LinkedList<Vertex>();
    for (Edge edge : this.edges) {
      if (edge.getFrom().equals(node)) {
        children.add(edge.getTo());
      }
    }
    return children;
  }

  public List<Vertex> getVertexes() {
    return this.vertexes;
  }

  public boolean hasChildren(Vertex p) {
    if (getChildren(p).isEmpty()) return false;
    else return true;
  }

  public Vertex getNodeByGornAdress(String gornaddress) {
    for (Vertex p : this.vertexes) {
      if (p.getGornaddress().equals(gornaddress)) {
        return p;
      }
    }
    return null;
  }
}
