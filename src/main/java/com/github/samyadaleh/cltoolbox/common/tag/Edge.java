package com.github.samyadaleh.cltoolbox.common.tag;

/** A directed edge between two nodes. */
class Edge {
  private final Vertex from;
  private final Vertex to;

  Edge(Vertex from, Vertex to) {
    this.from = from;
    this.to = to;
  }

  public Vertex getFrom() {
    return this.from;
  }

  public Vertex getTo() {
    return this.to;
  }
}
