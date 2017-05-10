package common;

import java.util.ArrayList;

/** Open collection of functions to work with arrays. */
public class ArrayUtils {

  /** Retrieves the subsequence of an array from (inclusive) index to
   * (exclusive) index and returns it as string. */
  public static String getSubSequenceAsString(String[] sequence, int from,
    int to) {
    StringBuilder subseq = new StringBuilder();
    for (int i = from; i < to && i < sequence.length; i++) {
      if (i > from)
        subseq.append(" ");
      subseq.append(sequence[i]);
    }
    return subseq.toString();
  }

  /** Retrieves a subsequence of an array from (inclusive) to (excusive) and
   * returns it as array. */
  public static String[] getSubSequenceAsArray(String[] sequence, int from,
    int to) {
    ArrayList<String> newsequence = new ArrayList<String>();
    for (int i = from; i < to && i < sequence.length; i++) {
      newsequence.add(sequence[i]);
    }
    return newsequence.toArray(new String[newsequence.size()]);
  }

  /** Returns true if the arrays are equal, that means all strings at the same
   * index has to be equal. Also the special character '?' is equal to
   * everything. */
  public static boolean match(String[] itemform1, String[] itemform2) {
    if (itemform1.length != itemform2.length) {
      return false;
    }
    for (int i = 0; i < itemform1.length; i++) {
      if (!(itemform1[i].equals("?") || itemform2[i].equals("?")
        || itemform1[i].equals(itemform2[i]))) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Returns a string representation of an array, here used for items.
   */
  public static String toString(String[] item) {
    StringBuilder representation = new StringBuilder();
    representation.append("[");
    for (String entry : item) {
      if (representation.length() > 1) {
        representation.append(",");
      }
      representation.append(entry);
    }
    representation.append("]");
    return representation.toString();
  }
}
