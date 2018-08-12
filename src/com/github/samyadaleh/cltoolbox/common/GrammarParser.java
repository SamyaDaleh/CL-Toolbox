package com.github.samyadaleh.cltoolbox.common;

import static com.github.samyadaleh.cltoolbox.common.ArrayUtils.contains;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

/** Parses different grammars from text files. */
public class GrammarParser {
  private static final Pattern p = Pattern.compile("\"(.*?)\"");
  private static List<Exception> errors;

  /** Parses a CFG from a file and returns it as Cfg. */
  public static Cfg parseCfgFile(String grammarFile) throws IOException {
    Cfg cfg = new Cfg();
    errors = new ArrayList<Exception>();
    Map<String, List<String>> declarations = parseDeclarations(grammarFile);
    for (Entry<String, List<String>> entry : declarations.entrySet()) {
      switch (entry.getKey()) {
      case "N":
        if (cfg.getNonterminals() != null) {
          errors.add(new ParseException("N was declared twice.", 0));
          continue;
        }
        String[] nts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        cfg.setNonterminals(nts);
        break;
      case "T":
        if (cfg.getTerminals() != null) {
          errors.add(new ParseException("T was declared twice.", 0));
          continue;
        }
        String[] ts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        cfg.setTerminals(ts);
        break;
      case "S":
        if (cfg.getStartSymbol() != null) {
          errors.add(new ParseException("S was declared twice.", 0));
          continue;
        }
        if (entry.getValue().size() > 1) {
          errors.add(new ParseException("Too many start symbols declared", 0));
          continue;
        }
        cfg.setStartSymbol(entry.getValue().get(0));
        break;
      case "P":
        if (cfg.getProductionRules().size() > 0) {
          errors.add(new ParseException("P was declared twice.", 0));
          break;
        }
        for (String rule : entry.getValue()) {
          try {
            cfg.addProductionRule(rule);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case "G":
        System.out.println("Grammar declaration detected. Nothing to do.");
        break;
      default:
        errors.add(new ParseException(
          "Unknown declaration symbol: " + entry.getKey(), 0));
      }
    }
    for (String nt : cfg.getNonterminals()) {
      for (String t : cfg.getTerminals()) {
        if (t.equals(nt)) {
          errors.add(new ParseException(
            t + " declared in both terminals and nonterminals.", 0));
        }
      }
    }
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      if (!contains(cfg.getNonterminals(), rule.getLhs())) {
        errors.add(new ParseException("LHS " + rule.getLhs() + " in rule "
          + rule.toString() + " not declared as nonterminal.", 0));
      }
      for (String rhsSym : rule.getRhs()) {
        if (!contains(cfg.getTerminals(), rhsSym)
          && !contains(cfg.getNonterminals(), rhsSym) && !rhsSym.equals("")) {
          errors.add(new ParseException(rhsSym + " in rule " + rule.toString()
            + " is neither declared as terminal nor as nonterminal.", 0));
        }
      }
    }
    if (cfg.getNonterminals() == null) {
      errors.add(new ParseException("No nonterminals declared in grammar.", 0));
    }
    if (cfg.getTerminals() == null) {
      errors.add(new ParseException("No terminals declared in grammar.", 0));
    }
    if (cfg.getStartSymbol() == null) {
      errors.add(new ParseException("No start symbol declared in grammar.", 0));
    }
    if (cfg.getProductionRules() == null) {
      errors
        .add(new ParseException("No production rules declared in grammar.", 0));
    }
    if (errors.size() > 0) {
      System.err.println(
        "The following errors occurred while reading the grammar file:");
      for (Exception e : errors) {
        System.err.println(e.getMessage());
      }
      return null;
    }
    return cfg;
  }

  private static Map<String, List<String>> parseDeclarations(String grammarFile)
    throws IOException {
    Map<String, List<String>> declarations =
      new LinkedHashMap<String, List<String>>();
    BufferedReader in = new BufferedReader(new FileReader(grammarFile));
    String line = in.readLine().trim();
    while (line != null) {
      String lineTrim = line.trim();
      int quotes = lineTrim.length() - lineTrim.replace("\"", "").length();
      if (quotes % 2 > 0) {
        errors.add(
          new ParseException("Uneven number of quotes in line " + lineTrim, 0));
      }
      String[] splitEquals = lineTrim.split("=");
      if (splitEquals.length == 1) {
        errors.add(
          new ParseException("No equals symbol found in line " + lineTrim, 0));
        continue;
      }
      String symbol = splitEquals[0].trim();
      if (declarations.containsKey(symbol)) {
        errors.add(new ParseException(symbol + " was declared twice.", 0));
      }
      declarations.put(symbol, parseNT(lineTrim));
      line = in.readLine();
    }
    in.close();
    return declarations;
  }

  /** Parses a PCFG from a file and returns it as Pcfg. */
  public static Pcfg parsePcfgFile(String grammarFile) throws IOException {
    Pcfg pcfg = new Pcfg();
    errors = new ArrayList<Exception>();
    Map<String, List<String>> declarations = parseDeclarations(grammarFile);
    for (Entry<String, List<String>> entry : declarations.entrySet()) {
      switch (entry.getKey()) {
      case "N":
        if (pcfg.getNonterminals() != null) {
          errors.add(new ParseException("N was declared twice.", 0));
          continue;
        }
        String[] nts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        pcfg.setNonterminals(nts);
        break;
      case "T":
        if (pcfg.getTerminals() != null) {
          errors.add(new ParseException("T was declared twice.", 0));
          continue;
        }
        String[] ts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        pcfg.setTerminals(ts);
        break;
      case "S":
        if (pcfg.getStartSymbol() != null) {
          errors.add(new ParseException("S was declared twice.", 0));
          continue;
        }
        if (entry.getValue().size() > 1) {
          errors.add(new ParseException("Too many start symbols declared", 0));
          continue;
        }
        pcfg.setStartSymbol(entry.getValue().get(0));
        break;
      case "P":
        if (pcfg.getProductionRules().size() > 0) {
          errors.add(new ParseException("P was declared twice.", 0));
          continue;
        }
        for (String rule : entry.getValue()) {
          try {
            pcfg.addProductionRule(rule);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case "G":
        System.out.println("Grammar declaration detected. Nothing to do.");
        break;
      default:
        errors.add(new ParseException(
          "Unknown declaration symbol: " + entry.getKey(), 0));
      }
    }
    for (String nt : pcfg.getNonterminals()) {
      for (String t : pcfg.getTerminals()) {
        if (t.equals(nt)) {
          errors.add(new ParseException(
            t + " declared in both terminals and nonterminals.", 0));
        }
      }
    }
    for (PcfgProductionRule rule : pcfg.getProductionRules()) {
      if (!contains(pcfg.getNonterminals(), rule.getLhs())) {
        errors.add(new ParseException("LHS " + rule.getLhs() + " in rule "
          + rule.toString() + " not declared as nonterminal.", 0));
      }
      for (String rhsSym : rule.getRhs()) {
        if (!contains(pcfg.getTerminals(), rhsSym)
          && !contains(pcfg.getNonterminals(), rhsSym) && !rhsSym.equals("")) {
          errors.add(new ParseException(rhsSym + " in rule " + rule.toString()
            + " is neither declared as terminal nor as nonterminal.", 0));
        }
      }
    }
    if (pcfg.getNonterminals() == null) {
      errors.add(new ParseException("No nonterminals declared in grammar.", 0));
    }
    if (pcfg.getTerminals() == null) {
      errors.add(new ParseException("No terminals declared in grammar.", 0));
    }
    if (pcfg.getStartSymbol() == null) {
      errors.add(new ParseException("No start symbol declared in grammar.", 0));
    }
    if (pcfg.getProductionRules() == null) {
      errors
        .add(new ParseException("No production rules declared in grammar.", 0));
    }
    if (errors.size() > 0) {
      System.err.println(
        "The following errors occurred while reading the grammar file:");
      for (Exception e : errors) {
        System.err.println(e.getMessage());
      }
      return null;
    }
    return pcfg;
  }

  /** Parses a TAG from a text file and returns it as a Tag object. */
  public static Tag parseTagFile(String grammarFile) throws IOException {
    Tag tag = new Tag();
    errors = new ArrayList<Exception>();
    Map<String, List<String>> declarations = parseDeclarations(grammarFile);
    for (Entry<String, List<String>> entry : declarations.entrySet()) {
      switch (entry.getKey()) {
      case "N":
        if (tag.getNonterminals() != null) {
          errors.add(new ParseException("Declaring N twice is not allowed", 0));
          continue;
        }
        String[] nts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        tag.setNonterminals(nts);
        break;
      case "T":
        if (tag.getTerminals() != null) {
          errors.add(new ParseException("Declaring T twice is not allowed", 0));
          continue;
        }
        String[] ts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        tag.setTerminals(ts);
        break;
      case "S":
        if (tag.getStartSymbol() != null) {
          errors.add(new ParseException("Declaring S twice is not allowed", 0));
          continue;
        }
        if (entry.getValue().size() > 1) {
          errors.add(new ParseException("Too many start symbols declared", 0));
          continue;
        }
        tag.setStartSymbol(entry.getValue().get(0));
        break;
      case "I":
        if (tag.getTerminals() == null) {
          errors.add(new ParseException(
            "You have to declare terminals before trees can be parsed.", 0));
          continue;
        }
        if (tag.getInitialTreeNames().size() > 0) {
          errors.add(new ParseException("Declaring I twice is not allowed", 0));
          continue;
        }
        for (String treeDec : entry.getValue()) {
          try {
            tag.addInitialTree(treeDec);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case "A":
        if (tag.getTerminals() == null) {
          errors.add(new ParseException(
            "You have to declare terminals before trees can be parsed.", 0));
          continue;
        }
        if (tag.getAuxiliaryTreeNames().size() > 0) {
          errors.add(new ParseException("Declaring A twice is not allowed", 0));
          continue;
        }
        for (String treeDec : entry.getValue()) {
          try {
            tag.addAuxiliaryTree(treeDec);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case "G":
        System.out.println("Grammar declaration detected. Nothing to do.");
        break;
      default:
        errors.add(new ParseException(
          "Unknown declaration symbol: " + entry.getKey(), 0));
      }
    }
    for (String nt : tag.getNonterminals()) {
      for (String t : tag.getTerminals()) {
        if (t.equals(nt)) {
          errors.add(new ParseException(
            t + " declared in both terminals and nonterminals.", 0));
        }
      }
    }
    Iterator<String> treeNameIter = tag.getInitialTreeNames().iterator();
    while (treeNameIter.hasNext()) {
      String treeName = treeNameIter.next();
      Tree tree = tag.getInitialTree(treeName);
      for (Vertex v : tree.getVertexes()) {
        if (!v.getLabel().equals("")
          && !contains(tag.getNonterminals(), v.getLabel())
          && !contains(tag.getTerminals(), v.getLabel())) {
          errors.add(
            new ParseException("Label of vertex " + v.getLabel() + " of tree "
              + treeName + " is neither declared nonterminal nor terminal "
              + "and is not epsilon.", 0));
        }
        if (tree.getNodeByGornAdress(v.getGornAddress() + ".1") != null
          && contains(tag.getTerminals(), v.getLabel())) {
          errors.add(new ParseException("Node with gorn address "
            + v.getGornAddress() + " in tree " + treeName
            + " is not a leaf, but its label is declared terminal.", 0));
        }
      }
    }
    treeNameIter = tag.getAuxiliaryTreeNames().iterator();
    while (treeNameIter.hasNext()) {
      String treeName = treeNameIter.next();
      Tree tree = tag.getAuxiliaryTree(treeName);
      for (Vertex v : tree.getVertexes()) {
        if (!v.getLabel().equals("")
          && !contains(tag.getNonterminals(), v.getLabel())
          && !contains(tag.getTerminals(), v.getLabel())) {
          errors.add(
            new ParseException("Label of vertex " + v.getLabel() + " of tree "
              + treeName + " is neither declared nonterminal nor terminal "
              + "and is not epsilon.", 0));
        }
      }
    }
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
    if (errors.size() > 0) {
      System.err.println(
        "The following errors occurred while reading the grammar file:");
      for (Exception e : errors) {
        System.err.println(e.getMessage());
      }
      return null;
    }
    return tag;
  }

  /**
   * For a line like "A", "S" it gets the content of each quote and makes each
   * an element of the returned array.
   */
  private static List<String> parseNT(String lineTrim) {
    Matcher m = p.matcher(lineTrim);
    List<String> nList = new ArrayList<String>();
    while (m.find()) {
      String n = m.group();
      nList.add(n.substring(1, n.length() - 1));
    }
    return nList;
  }

  /** Parses a sRCG from a file and returns it as Srcg. */
  public static Srcg parseSrcgFile(String grammarFile) throws IOException {
    Srcg srcg = new Srcg();
    errors = new ArrayList<Exception>();
    Map<String, List<String>> declarations = parseDeclarations(grammarFile);
    for (Entry<String, List<String>> entry : declarations.entrySet()) {
      switch (entry.getKey()) {
      case "N":
        if (srcg.getNonterminals() != null) {
          errors.add(new ParseException("Declaring N twice is not allowed", 0));
          continue;
        }
        String[] nts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        srcg.setNonterminals(nts);
        break;
      case "V":
        if (srcg.getVariables() != null) {
          errors.add(new ParseException("Declaring V twice is not allowed", 0));
          continue;
        }
        String[] vs =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        srcg.setVariables(vs);
        break;
      case "T":
        if (srcg.getTerminals() != null) {
          errors.add(new ParseException("Declaring T twice is not allowed", 0));
          continue;
        }
        String[] ts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        srcg.setTerminals(ts);
        break;
      case "S":
        if (srcg.getStartSymbol() != null) {
          errors.add(new ParseException("Declaring S twice is not allowed", 0));
          continue;
        }
        if (entry.getValue().size() > 1) {
          errors.add(new ParseException("Too many start symbols declared", 0));
          continue;
        }
        srcg.setStartSymbol(entry.getValue().get(0));
        break;
      case "P":
        if (srcg.getClauses().size() > 0) {
          errors.add(new ParseException("Declaring P twice is not allowed", 0));
          continue;
        }
        for (String clauseDec : entry.getValue()) {
          try {
            srcg.addClause(clauseDec);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case "G":
        System.out.println("Grammar declaration detected. Nothing to do.");
        break;
      default:
        errors.add(new ParseException(
          "Unknown declaration symbol: " + entry.getKey(), 0));
      }
    }
    for (String nt : srcg.getVariables()) {
      for (String t : srcg.getTerminals()) {
        if (t.equals(nt)) {
          errors.add(new ParseException(
            t + " declared in both terminals and variables.", 0));
        }
      }
    }
    for (Clause clause : srcg.getClauses()) {
      Predicate lhsPred = clause.getLhs();
      checkPredicateFormat(srcg, clause, lhsPred);
      for (Predicate rhsPred : clause.getRhs()) {
        checkPredicateFormat(srcg, clause, rhsPred);
      }
    }
    if (srcg.getNonterminals() == null) {
      errors.add(new ParseException("No nonterminals declared in grammar.", 0));
    }
    if (srcg.getTerminals() == null) {
      errors.add(new ParseException("No terminals declared in grammar.", 0));
    }
    if (srcg.getStartSymbol() == null) {
      errors.add(new ParseException("No start symbol declared in grammar.", 0));
    }
    if (srcg.getVariables() == null) {
      errors.add(new ParseException("No variables declared in grammar.", 0));
    }
    if (srcg.getClauses() == null) {
      errors.add(new ParseException("No clauses declared in grammar.", 0));
    }
    if (errors.size() > 0) {
      System.err.println(
        "The following errors occurred while reading the grammar file:");
      for (Exception e : errors) {
        System.err.println(e.getMessage());
      }
      return null;
    }
    return srcg;
  }

  private static void checkPredicateFormat(Srcg srcg, Clause clause,
    Predicate pred) {
    if (!contains(srcg.getNonterminals(), pred.getNonterminal())) {
      errors.add(new ParseException("Nonterminal " + pred.getNonterminal()
        + " in clause " + clause.toString() + " not declared as nonterminal.",
        0));
    }
    for (String symbol : pred.getSymbolsAsPlainArray()) {
      if (!symbol.equals("") && !contains(srcg.getTerminals(), symbol)
        && !contains(srcg.getVariables(), symbol)) {
        errors.add(new ParseException("Symbol " + symbol + " in clause "
          + clause.toString() + " is neither declared terminal nor variable.",
          0));
      }
    }
  }
}
