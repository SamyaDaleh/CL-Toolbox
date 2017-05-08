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
}
