package com.github.samyadaleh.cltoolbox.common;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Open collection of functions to work with arrays.
 */
public class ArrayUtils {

  /**
   * Retrieves the subsequence of an array from (inclusive) index to (exclusive)
   * index and returns it as string, entries separated by space.
   */
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

  /**
   * Retrieves a subsequence of an array from (inclusive) to (excusive) and
   * returns it as array.
   */
  public static String[] getSubSequenceAsArray(String[] sequence, int from,
      int to) {
    ArrayList<String> newSequence = new ArrayList<>();
    for (int i = from; i < to && i < sequence.length; i++) {
      newSequence.add(sequence[i]);
    }
    return newSequence.toArray(new String[0]);
  }

  /**
   * Retrieves a subsequence of an array from (inclusive) to (excusive) and
   * returns it as list.
   */
  public static List<String> getSubSequenceAsList(String[] sequence, int from,
      int to) {
    ArrayList<String> newSequence = new ArrayList<>();
    for (int i = from; i < to && i < sequence.length; i++) {
      newSequence.add(sequence[i]);
    }
    return newSequence;
  }

  /**
   * Returns true if the arrays are equal, that means all strings at the same
   * index have to be equal. Also the special character '?' is equal to
   * everything.
   */
  public static boolean match(String[] itemForm1, String[] itemForm2) {
    if (itemForm1.length != itemForm2.length) {
      return false;
    }
    for (int i = 0; i < itemForm1.length; i++) {
      if (!(itemForm1[i].equals("?") || itemForm2[i].equals("?") || itemForm1[i]
          .equals(itemForm2[i]))) {
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

  /**
   * If seqsplit ends with rhs, the first part of seqsplit without rhs is
   * returned, else null.
   */
  public static String getStringHeadIfEndsWith(String[] seqSplit,
      String[] rhs) {
    if (seqSplit.length < rhs.length)
      return null;
    for (int i = 0; i < rhs.length; i++) {
      if (!(seqSplit[seqSplit.length - rhs.length + i].equals(rhs[i]))) {
        return null;
      }
    }
    return ArrayUtils
        .getSubSequenceAsString(seqSplit, 0, seqSplit.length - rhs.length);
  }

  /**
   * Returns a new array without the element at index i.
   */
  public static String[] getSequenceWithoutIAsArray(String[] array, int i) {
    ArrayList<String> newArray = new ArrayList<>();
    for (int j = 0; j < array.length; j++) {
      if (j != i) {
        newArray.add(array[j]);
      }
    }
    return newArray.toArray(new String[0]);
  }

  /**
   * Returns true if the element is to be found somewhere in the array.
   */
  public static <T> boolean contains(T[] array, T element) {
    for (T el : array) {
      if (el.equals(element)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Actually does the tokenization by throwing away spaces, returning special
   * symbols as single tokens and all other continuous string concatenations
   * each as a token.
   */
  public static List<String> tokenize(String tree, Character[] specialSymbols) {
    List<String> tokens = new ArrayList<>();
    StringBuilder builder = new StringBuilder();
    String treeTrim = tree.trim();
    for (int i = 0; i < treeTrim.length(); i++) {
      if (treeTrim.charAt(i) == ' ') {
        if (builder.length() > 0) {
          tokens.add(builder.toString());
          builder = new StringBuilder();
        }
      } else {
        int finalI = i;
        if (Stream.of(specialSymbols).anyMatch(
            s -> treeTrim.substring(finalI).startsWith(String.valueOf(s)))) {
          if (builder.length() > 0) {
            tokens.add(builder.toString());
            builder = new StringBuilder();
          }
          tokens.add(String.valueOf(treeTrim.charAt(i)));
        } else {
          builder.append(treeTrim.charAt(i));
        }
      }
    }
    if (builder.length() > 0) {
      tokens.add(builder.toString());
    }
    return tokens;
  }

  /**
   * Fake concat that returns a new array with the elements of the two arrays
   * concated to each other
   */
  public static String[] concat(String[] array1, String[] array2) {
    return Stream.concat(Arrays.stream(array1), Arrays.stream(array2))
        .toArray(size -> (String[]) Array
            .newInstance(array1.getClass().getComponentType(), size));
  }

  /**
   * Returns true if the list contains an array where all elements are equal
   * to strings.
   */
  public static boolean contains(List<String[]> arrayList, String[] array) {
    for (String[] array1 : arrayList) {
      if (array1.length != array.length) {
        continue;
      }
      boolean equal = true;
      for (int i = 0; i < array1.length; i++) {
        if (array1[i] != array[i]) {
          equal = false;
          break;
        }
      }
      if(equal) {
        return true;
      }
    }
    return false;
  }
}
