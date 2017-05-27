package common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Collection of functions to work with Sets. */
class SetUtils {

  /** Returns a new set that contains all items that one or both input sets
   * contain. */
  private static Set<String> union(Set<String> a, Set<String> b) {
    Set<String> c = new HashSet<String>();
    c.addAll(a);
    c.addAll(b);
    return c;
  }

  /** Returns a new set that only contains items that are elements of both
   * sets. */
  public static Set<String> intersection(Set<String> a, Set<String> b) {
    Set<String> c = new HashSet<String>();
    for (String element : a.toArray(new String[a.size()])) {
      if (b.contains(element))
        c.add(element);
    }
    return c;
  }

  /** Returns all arbitrary concatenations of the set elements up to a length of
   * maxlength, including the empty string. */
  public static Set<String> star(Set<String> a, int maxlength) {
    Set<String> c = new HashSet<String>();
    c.add("");
    if (maxlength == 0)
      return c;
    else {
      return union(concatenation(a, star(a, maxlength - 1)),
        star(a, maxlength - 1));
    }
  }

  /** Returns all arbitrary concatenations of the array elements up to a length
   * of maxlength, including the empty string. */
  public static Set<String> star(String[] a, int maxlength) {
    Set<String> c = new HashSet<String>();
    Collections.addAll(c, a);
    return star(c, maxlength);
  }

  /** Returns a new set that contains all elements of a concatenated with all
   * elements of b. */
  private static Set<String> concatenation(Set<String> a, Set<String> b) {
    Set<String> c = new HashSet<String>();
    for (String element : a.toArray(new String[a.size()])) {
      for (String element2 : b.toArray(new String[b.size()])) {
        if (element.isEmpty() || element2.isEmpty()) {
          c.add(element + element2);
        } else {
          c.add(element + " " + element2);
        }
      }

    }
    return c;
  }

  /** Treats the passed arrays as sets and returns a set containing all elements
   * that occur in one of the arrays. */
  public static Set<String> union(String[]... sets) {
    Set<String> c = new HashSet<String>();
    for (String[] set : sets) {
      Collections.addAll(c, set);
    }
    return c;
  }
}
