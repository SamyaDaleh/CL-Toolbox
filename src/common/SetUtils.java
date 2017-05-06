package common;

import java.util.HashSet;
import java.util.Set;

public class SetUtils {
  public static Set<String> union(Set<String> a, Set<String> b) {
    Set<String> c = new HashSet<String>();
    c.addAll(a);
    c.addAll(b);
    return c;
  }

  public static Set<String> intersection(Set<String> a, Set<String> b) {
    Set<String> c = new HashSet<String>();
    for (String element : a.toArray(new String[a.size()])) {
      if (b.contains((String) element))
        c.add((String) element);
    }
    return c;
  }

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
  
  public static Set<String> star(String[] a, int maxlength){
    Set<String> c = new HashSet<String>();
    for (String b : a){
      c.add(b);
    }
    return star(c, maxlength);
  }

  public static Set<String> concatenation(Set<String> a, Set<String> b) {
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

  // TODO change data structures in CFG to Sets
  public static Set<String> union(String[]... sets) {
    Set<String> c = new HashSet<String>();
    for (String[] set : sets) {
      for (String element : set) {
        c.add(element);
      }
    }
    return c;
  }
}
