package common;

import java.util.ArrayList;
import java.util.List;

/** Open collection of functions to work with arrays. */
public class ArrayUtils {

  /** Retrieves the subsequence of an array from (inclusive) index to
   * (exclusive) index and returns it as string. */
  public static String getSubSequenceAsString(String[] sequence, int from,
    int to) {
    StringBuilder subSeq = new StringBuilder();
    for (int i = from; i < to && i < sequence.length; i++) {
      if (i > from)
        subSeq.append(" ");
      subSeq.append(sequence[i]);
    }
    return subSeq.toString();
  }

  /** Retrieves a subsequence of an array from (inclusive) to (excusive) and
   * returns it as array. */
  public static String[] getSubSequenceAsArray(String[] sequence, int from,
    int to) {
    ArrayList<String> newSequence = new ArrayList<String>();
    for (int i = from; i < to && i < sequence.length; i++) {
      newSequence.add(sequence[i]);
    }
    return newSequence.toArray(new String[newSequence.size()]);
  }

  /** Retrieves a subsequence of an array from (inclusive) to (excusive) and
   * returns it as list. */
  public static List<String> getSubSequenceAsList(String[] sequence, int from,
    int to) {
    ArrayList<String> newSequence = new ArrayList<String>();
    for (int i = from; i < to && i < sequence.length; i++) {
      newSequence.add(sequence[i]);
    }
    return newSequence;
  }

  /** Returns true if the arrays are equal, that means all strings at the same
   * index have to be equal. Also the special character '?' is equal to
   * everything. */
  public static boolean match(String[] itemForm1, String[] itemForm2) {
    if (itemForm1.length != itemForm2.length) {
      return false;
    }
    for (int i = 0; i < itemForm1.length; i++) {
      try { // DEBUG
      if (!(itemForm1[i].equals("?") || itemForm2[i].equals("?")
        || itemForm1[i].equals(itemForm2[i]))) {
        return false;
      }
      } catch (NullPointerException e) {
        System.out.println("gotcha");
      }
    }
    return true;
  }

  /** Returns a string representation of an array, here used for items. */
  public static String toString(String[] item) {
    StringBuilder representation = new StringBuilder();
    representation.append("[");
    for (String element : item) {
      if (representation.length() > 1) {
        representation.append(",");
      }
      if (element.equals("")) {
        representation.append("ε");
      } else {
        representation.append(element);
      }
    }
    representation.append("]");
    return representation.toString();
  }

  /** If seqsplit ends with rhs, the first part of seqsplit without rhs is
   * returned, else null. */
  public static String getStringHeadIfEndsWith(String[] seqSplit,
    String[] rhs) {
    if (seqSplit.length < rhs.length)
      return null;
    for (int i = 0; i < rhs.length; i++) {
      if (!(seqSplit[seqSplit.length - rhs.length + i].equals(rhs[i]))) {
        return null;
      }
    }
    return ArrayUtils.getSubSequenceAsString(seqSplit, 0,
      seqSplit.length - rhs.length);
  }

  /** Returns a new array without the element at index i. */
  public static String[] getSequenceWithoutIAsArray(String[] array, int i) {
    ArrayList<String> newArray = new ArrayList<String>();
    for (int j = 0; j < array.length; j++) {
      if (j != i) {
        newArray.add(array[j]);
      }

    }
    return newArray.toArray(new String[newArray.size()]);
  }

}
