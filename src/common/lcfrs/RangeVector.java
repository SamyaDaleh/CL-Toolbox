package common.lcfrs;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a range vector for w if all ranges are ranges over w.
 *
 */
public class RangeVector {
  
  private final List<Range> ranges = new ArrayList<Range>();

  /**
   * Creates a new vector of numranges ranges filled with placeholders. 
   */
  public RangeVector(int numRanges) {
    for (int i = 0; i < numRanges; i++) {
      ranges.add(new Range("?","?"));
    }
  }

  /**
   * Returns ranges as 1d array.
   */
  public String[] getRangesAsPlainArray() {
    ArrayList<String> rangesArray = new ArrayList<String>();
    for (Range range : ranges) {
      rangesArray.add(range.getRange()[0]);
      rangesArray.add(range.getRange()[1]);
    }
    return rangesArray.toArray(new String[rangesArray.size()]);
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
