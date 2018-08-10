package com.github.samyadaleh.cltoolbox.common.tag;

import java.util.Comparator;

class PrecedenceComparator implements Comparator<Vertex> {

  @Override public int compare(Vertex o1, Vertex o2) {

    if (o1.getGornAddress().equals("") || o2.getGornAddress().equals("")) {
      return 0;
    }
    String[] gorn1 = o1.getGornAddress().split("[.]");
    String[] gorn2 = o2.getGornAddress().split("[.]");

    int l1 = gorn1.length;
    int l2 = gorn2.length;

    for (int i = 1; i < l1 && i < l2; i++) {
      if (Integer.parseInt(gorn1[i]) < Integer.parseInt(gorn2[i])) {
        return -1;
      } else if (Integer.parseInt(gorn2[i]) < Integer.parseInt(gorn1[i])) {
        return 1;
      }
    }
    return 0;
  }

}
