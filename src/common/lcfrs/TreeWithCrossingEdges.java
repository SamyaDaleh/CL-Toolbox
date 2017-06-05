package common.lcfrs;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import common.tag.Tree;
import common.tag.Vertex;

public class TreeWithCrossingEdges extends Tree {

  private ArrayList<String> leaforder;
  private ArrayList<String> leafgorns;

  private TreeWithCrossingEdges() {
    super();
  }

  /** Constructor for a format like "(S (Comp dat<0>) (VP (NP Jan<1>) (VP (NP
   * Piet<2>) (VP (NP de-kinderen<3>) (V zwemmen<6>)) (V helpen<5>)) (V
   * zag<4>)))" */
  public TreeWithCrossingEdges(String tree) throws ParseException {
    leaforder = new ArrayList<String>();
    leafgorns = new ArrayList<String>();
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
      case "<":
        leaforder.add(tokens[i + 1]);
        leafgorns
          .add(this.vertexes.get(this.vertexes.size() - 1).getGornaddress());
        i += 2;
        break;
      default: {
        handleChildNode(tokens, vertexpath, children, i);
        break;
      }
      }
    }
  }

  /** Constructor for passing tree and sentence separately, like "(S (Comp <0>)
   * (VP (NP <1>) (VP (NP <2>) (VP (NP <3>) (V <6>)) (V <5>)) (V <4>)))" "dat
   * Jan Piet de_kinderen zag helpen zwemmen" */
  public TreeWithCrossingEdges(String tree, String input)
    throws ParseException {
    super(tree);
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

  @Override public String toString() {
    return "("
      + (this.getRoot().getLabel().equals("") ? "ε" : this.getRoot().getLabel())
      + (this.getRoot().equals(foot) ? "*" : "")
      + (isInOA(this.getRoot().getGornaddress()) ? "_OA" : "")
      + (isInNA(this.getRoot().getGornaddress()) ? "_NA" : "") + " "
      + toStringAllChildren(this.getRoot()) + ")";
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
        .append(isInNA(child.getGornaddress()) ? "_NA" : "");
      if (leafgorns.contains(child.getGornaddress())) {
        representation.append('<');
        representation
          .append(leaforder.get(leafgorns.indexOf(child.getGornaddress())));
        representation.append('>');
      }
        representation.append(" ")
        .append(toStringAllChildren(child)).append(")");
    }
    return representation.toString();
  }
  
  public ArrayList<String> getLeafOrder() {
    return this.leaforder;
  }
  
  public ArrayList<String> getLeafGorns() {
    return this.leafgorns;
  }
}
