package common.lcfrs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a range vector for w if all ranges are ranges over w.
 *
 */
public class RangeVector {
  
  private List<Range> ranges = new LinkedList<Range>();

  /**
   * Creates a new vector of numranges ranges filled with placeholders. 
   */
  public RangeVector(int numranges) {
    for (int i = 0; i < numranges; i++) {
      ranges.add(new Range("?","?"));
    }
  }

  /**
   * Returns ranges as 1d array.
   */
  public String[] getRangesAsPlainArray() {
    ArrayList<String> rangesarray = new ArrayList<String>();
    for (Range range : ranges) {
      rangesarray.add(range.getRange()[0]);
      rangesarray.add(range.getRange()[1]);
    }
    return rangesarray.toArray(new String[rangesarray.size()]);
  }

  @Override public String toString() {
    StringBuilder repr = new StringBuilder();
    repr.append("(");
    for (int i = 0; i < ranges.size(); i++) {
      if (i > 0) {
        repr.append(", ");
      }
      repr.append(ranges.get(i).toString());
    }
    repr.append(")");
    return repr.toString();
  }
}
