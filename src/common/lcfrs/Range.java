package common.lcfrs;

/**
 * A pair <l,r> ∈ Pos(w) x Pos(w) with l <= r is a range in w,
 */
public class Range {
  String l;
  String r;
  
  /**
   * Constructor with the indices of start and end of the substring. As string
   * so they can contain '?' needed for parsing while I don't know the spans.
   */
  public Range(String l, String r) {
    this.l = l;
    this.r = r;
  }

  @Override public String toString() {
    return "<" + l + "," + r + ">";
  }
  
  public String[] getRange(){
    return new String[]{l,r};
  }
}
