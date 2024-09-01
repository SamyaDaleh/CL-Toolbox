package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

import java.io.BufferedReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TagParser {

  /**
   * Parses a TAG from a text file and returns it as a Tag object.
   */
  public static Tag parseTagReader(Reader reader)
      throws ParseException {
    BufferedReader in = new BufferedReader(reader);
    Tag tag = new Tag(in);
    List<Exception> errors = checkForGrammarProblems(tag);
    if (!errors.isEmpty()) {
      GrammarParserUtils.printErrors(errors);
      throw (ParseException) errors.get(0);
    }
    return tag;

  }

  private static List<Exception> checkForGrammarProblems(Tag tag) {
    List<Exception> errors = new ArrayList<>();
    if (tag.getNonterminals() == null) {
      errors.add(new ParseException(
          "Nonterminals are null, check grammar format.", 0));
      return errors;
    }
    if (tag.getTerminals() == null) {
      errors.add(new ParseException(
          "Terminals are null, check grammar format.", 0));
      return errors;
    }
    if (tag.getTreeNames()== null) {
      errors.add(new ParseException(
          "No trees defined, check grammar format.", 0));
      return errors;
    }
    for (String nt : tag.getNonterminals()) {
      for (String t : tag.getTerminals()) {
        if (t.equals(nt)) {
          errors.add(new ParseException(
              t + " declared in both terminals and nonterminals.", 0));
        }
      }
    }
    checkInitialTrees(tag, errors);
    checkAuxiliaryTrees(tag, errors);
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
    if (!tag.nonterminalsContain(tag.getStartSymbol())) {
      errors.add(new ParseException(
          "The start symbol is not one of the nonterminals.", 0));
    }
    return errors;
  }

  private static void checkAuxiliaryTrees(Tag tag, List<Exception> errors) {
    for (String treeName : tag.getAuxiliaryTreeNames()) {
      checkTree(tag, treeName, errors);
    }
  }

  private static void checkTree(
      Tag tag, String treeName, List<Exception> errors) {
    Tree tree = tag.getTree(treeName);
    for (Vertex v : tree.getVertexes()) {
      if (!v.getLabel().isEmpty()
              && !tag.getNonterminals().contains(v.getLabel())
              && !tag.getTerminals().contains(v.getLabel())) {
        errors.add(new ParseException(
            "Label of vertex " + v.getLabel() + " of tree " + treeName
                + " is neither declared nonterminal nor terminal "
                + "and is not epsilon.", 0));
      }
      if (tree.getNodeByGornAddress(v.getGornAddress() + ".1") != null
          && tag.getTerminals().contains(v.getLabel())) {
        errors.add(new ParseException(
            "Node with gorn address " + v.getGornAddress() + " in tree "
                + treeName
                + " is not a leaf, but its label is declared terminal.", 0));
      }
    }
  }

  private static void checkInitialTrees(
      Tag tag, List<Exception> errors) {
    for (String treeName : tag.getInitialTreeNames()) {
      checkTree(tag, treeName, errors);
    }
  }
}
