package common;

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

  /** Returns all arbitrary concatenations of the set elements up to a length of
   * maxlength, including the empty string. */
  public static Set<String> star(Set<String> a, int maxLength) {
    Set<String> c = new HashSet<String>();
    c.add("");
    if (maxLength == 0)
      return c;
    else {
      return union(concatenation(a, star(a, maxLength - 1)),
        star(a, maxLength - 1));
    }
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

}
