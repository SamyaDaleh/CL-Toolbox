package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.ArrayUtils.contains;

public class TagGrammarParser {
  private static List<Exception> errors;

  /**
   * Parses a TAG from a text file and returns it as a Tag object.
   */
  public static Tag parseTagReader(Reader reader)
      throws ParseException, IOException {
    errors = new ArrayList<>();
    BufferedReader in = new BufferedReader(reader);
    Tag tag = new Tag(in);
    checkForGrammarProblems(tag);
    if (GrammarParserUtils.printErrors(errors))
      return null;
    return tag;

  }

  private static void checkForGrammarProblems(Tag tag) {
    for (String nt : tag.getNonterminals()) {
      for (String t : tag.getTerminals()) {
        if (t.equals(nt)) {
          errors.add(new ParseException(
              t + " declared in both terminals and nonterminals.", 0));
        }
      }
    }
    checkInitialTrees(tag);
    checkAuxiliaryTrees(tag);
    if (tag.getNonterminals() == null) {
      errors.add(new ParseException("No nonterminals declared in grammar.", 0));
    }
    if (tag.getTerminals() == null) {
      errors.add(new ParseException("No terminals declared in grammar.", 0));
    }
    if (tag.getStartSymbol() == null) {
      errors.add(new ParseException("No start symbol declared in grammar.", 0));
    }
    if (tag.getInitialTreeNames() == null) {
      errors.add(
          new ParseException("No initial trees rules declared in grammar.", 0));
    }
  }

  private static void checkAuxiliaryTrees(Tag tag) {
    Iterator<String> treeNameIter;
    treeNameIter = tag.getAuxiliaryTreeNames().iterator();
    while (treeNameIter.hasNext()) {
      String treeName = treeNameIter.next();
      Tree tree = tag.getAuxiliaryTree(treeName);
      for (Vertex v : tree.getVertexes()) {
        if (!v.getLabel().equals("") && !contains(tag.getNonterminals(),
            v.getLabel()) && !contains(tag.getTerminals(), v.getLabel())) {
          errors.add(new ParseException(
              "Label of vertex " + v.getLabel() + " of tree " + treeName
                  + " is neither declared nonterminal nor terminal "
                  + "and is not epsilon.", 0));
        }
      }
    }
  }

  private static void checkInitialTrees(Tag tag) {
    for (String treeName : tag.getInitialTreeNames()) {
      Tree tree = tag.getInitialTree(treeName);
      for (Vertex v : tree.getVertexes()) {
        if (!v.getLabel().equals("") && !contains(tag.getNonterminals(),
            v.getLabel()) && !contains(tag.getTerminals(), v.getLabel())) {
          errors.add(new ParseException(
              "Label of vertex " + v.getLabel() + " of tree " + treeName
                  + " is neither declared nonterminal nor terminal "
                  + "and is not epsilon.", 0));
        }
        if (tree.getNodeByGornAdress(v.getGornAddress() + ".1") != null
            && contains(tag.getTerminals(), v.getLabel())) {
          errors.add(new ParseException(
              "Node with gorn address " + v.getGornAddress() + " in tree "
                  + treeName
                  + " is not a leaf, but its label is declared terminal.", 0));
        }
      }
    }
  }
}
