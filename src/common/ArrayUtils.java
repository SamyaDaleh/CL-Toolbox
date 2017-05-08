package common;

import java.util.ArrayList;

public class ArrayUtils {
  public static String getSubSequenceAsString(String[] sequence, int from, int to) {
    StringBuilder subseq = new StringBuilder();
    for (int i = from; i < to && i < sequence.length; i++) {
      if (i > from)
        subseq.append(" ");
      subseq.append(sequence[i]);
    }
    return subseq.toString();
  }
  public static String[] getSubSequenceAsArray(String[] sequence, int from, int to) {
    ArrayList<String> newsequence = new ArrayList<String>();
    for (int i = from; i < to && i < sequence.length; i++) {
      newsequence.add(sequence[i]);
    }
    return newsequence.toArray(new String[newsequence.size()]);
  }
}
