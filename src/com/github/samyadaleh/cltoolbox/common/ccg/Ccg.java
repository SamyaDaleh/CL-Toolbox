package com.github.samyadaleh.cltoolbox.common.ccg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Combinatory Categorial Grammar that contains a lexicon, a mapping from
 * words to their categories.
 */
public class Ccg {
  private Map<String, List<String>> lexicon = new HashMap<>();

  public Ccg(BufferedReader in) throws IOException {
    String line;
    while ((line = in.readLine()) != null) {
      String[] lineSplit = line.split("\t");
      if (!lexicon.containsKey(lineSplit[0])) {
        lexicon.put(lineSplit[0], new ArrayList<>());
      }
      lexicon.get(lineSplit[0]).add(lineSplit[1]);
    }
  }

  public Map<String, List<String>> getLexicon() {
    return lexicon;
  }
}
