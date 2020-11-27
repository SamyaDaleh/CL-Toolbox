package com.github.samyadaleh.cltoolbox.common.lcfrs;

/**
 * A pair <l,r> ∈ Pos(w) x Pos(w) with l <= r is a range in w,
 */
class Range {
  private final String l;
  private final String r;
  
  /**
   * Constructor with the indices of start and end of the substring. As string
   * so they can contain '?' needed for parsing while I don't know the spans.
   * Initializes it with unknown indices as that is the only use case for now.
   */
  Range() {
    this.l = "?";
    this.r = "?";
  }

  @Override public String toString() {
    return "<" + l + "," + r + ">";
  }
  
  public String[] getRange(){
    return new String[]{l,r};
  }
}
