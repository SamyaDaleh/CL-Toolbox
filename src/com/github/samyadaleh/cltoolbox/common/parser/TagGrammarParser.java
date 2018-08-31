package com.github.samyadaleh.cltoolbox.common.parser;

import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.samyadaleh.cltoolbox.common.ArrayUtils.contains;

public class TagGrammarParser {
  private static List<Exception> errors;
  private static final Logger log = LogManager.getLogger();

  /**
   * Parses a TAG from a text file and returns it as a Tag object.
   */
  public static Tag parseTagFile(String grammarFile) throws IOException {
    Tag tag = new Tag();
    errors = new ArrayList<>();
    Map<String, List<String>> declarations =
        GrammarParserUtils.parseDeclarations(grammarFile, errors);
    for (Map.Entry<String, List<String>> entry : declarations.entrySet()) {
      switch (entry.getKey()) {
      case "N":
        String[] nts = entry.getValue().toArray(new String[0]);
        tag.setNonterminals(nts);
        break;
      case "T":
        String[] ts = entry.getValue().toArray(new String[0]);
        tag.setTerminals(ts);
        break;
      case "S":
        if (entry.getValue().size() > 1) {
          errors.add(new ParseException("Too many start symbols declared", 0));
          continue;
        }
        tag.setStartSymbol(entry.getValue().get(0));
        break;
      case "I":
        for (String treeDec : entry.getValue()) {
          try {
            tag.addInitialTree(treeDec);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case "A":
        for (String treeDec : entry.getValue()) {
          try {
            tag.addAuxiliaryTree(treeDec);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case "G":
        log.info("Grammar declaration detected. Nothing to do.");
        break;
      default:
        errors.add(
            new ParseException("Unknown declaration symbol: " + entry.getKey(),
                0));
      }
    }
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
