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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Parses different grammars from text files. */
public class GrammarParser {
  private static final Pattern p = Pattern.compile("\"(.*?)\"");
  private static List<Exception> errors;
  private static final Logger log = LogManager.getLogger();

  /** Parses a CFG from a file and returns it as Cfg. */
  public static Cfg parseCfgFile(String grammarFile) throws IOException {
    Cfg cfg = new Cfg();
    errors = new ArrayList<>();
    Map<String, List<String>> declarations = parseDeclarations(grammarFile);
    for (Entry<String, List<String>> entry : declarations.entrySet()) {
      switch (entry.getKey()) {
      case "N":
        String[] nts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        cfg.setNonterminals(nts);
        break;
      case "T":
        String[] ts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        cfg.setTerminals(ts);
        break;
      case "S":
        if (entry.getValue().size() > 1) {
          errors.add(new ParseException("Too many start symbols declared", 0));
          continue;
        }
        cfg.setStartSymbol(entry.getValue().get(0));
        break;
      case "P":
        for (String rule : entry.getValue()) {
          try {
            cfg.addProductionRule(rule);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case "G":
        log.info("Grammar declaration detected. Nothing to do.");
        break;
      default:
        errors.add(new ParseException(
          "Unknown declaration symbol: " + entry.getKey(), 0));
      }
    }
    checkForGrammarProblems(cfg);
    if(printErrors()) return null;
    return cfg;
  }

  /** Parses a PCFG from a file and returns it as Pcfg. */
  public static Pcfg parsePcfgFile(String grammarFile) throws IOException {
    Pcfg pcfg = new Pcfg();
    errors = new ArrayList<>();
    Map<String, List<String>> declarations = parseDeclarations(grammarFile);
    for (Entry<String, List<String>> entry : declarations.entrySet()) {
      switch (entry.getKey()) {
      case "N":
        String[] nts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        pcfg.setNonterminals(nts);
        break;
      case "T":
        String[] ts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        pcfg.setTerminals(ts);
        break;
      case "S":
        if (entry.getValue().size() > 1) {
          errors.add(new ParseException("Too many start symbols declared", 0));
          continue;
        }
        pcfg.setStartSymbol(entry.getValue().get(0));
        break;
      case "P":
        for (String rule : entry.getValue()) {
          try {
            pcfg.addProductionRule(rule);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case "G":
        log.info("Grammar declaration detected. Nothing to do.");
        break;
      default:
        errors.add(new ParseException(
          "Unknown declaration symbol: " + entry.getKey(), 0));
      }
    }
    checkForGrammarProblems(pcfg);
    if(printErrors()) return null;
    return pcfg;
  }

  /** Parses a TAG from a text file and returns it as a Tag object. */
  public static Tag parseTagFile(String grammarFile) throws IOException {
    Tag tag = new Tag();
    errors = new ArrayList<>();
    Map<String, List<String>> declarations = parseDeclarations(grammarFile);
    for (Entry<String, List<String>> entry : declarations.entrySet()) {
      switch (entry.getKey()) {
      case "N":
        String[] nts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        tag.setNonterminals(nts);
        break;
      case "T":
        String[] ts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
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
        errors.add(new ParseException(
          "Unknown declaration symbol: " + entry.getKey(), 0));
      }
    }
    checkForGrammarProblems(tag);
    if(printErrors()) return null;
    return tag;
  }

  /** Parses a sRCG from a file and returns it as Srcg. */
  public static Srcg parseSrcgFile(String grammarFile) throws IOException {
    Srcg srcg = new Srcg();
    errors = new ArrayList<>();
    Map<String, List<String>> declarations = parseDeclarations(grammarFile);
    for (Entry<String, List<String>> entry : declarations.entrySet()) {
      switch (entry.getKey()) {
      case "N":
        String[] nts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        srcg.setNonterminals(nts);
        break;
      case "V":
        String[] vs =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        srcg.setVariables(vs);
        break;
      case "T":
        String[] ts =
          entry.getValue().toArray(new String[entry.getValue().size()]);
        srcg.setTerminals(ts);
        break;
      case "S":
        if (entry.getValue().size() > 1) {
          errors.add(new ParseException("Too many start symbols declared", 0));
          continue;
        }
        srcg.setStartSymbol(entry.getValue().get(0));
        break;
      case "P":
        for (String clauseDec : entry.getValue()) {
          try {
            srcg.addClause(clauseDec);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case "G":
        log.info("Grammar declaration detected. Nothing to do.");
        break;
      default:
        errors.add(new ParseException(
          "Unknown declaration symbol: " + entry.getKey(), 0));
      }
    }
    checkForGrammarProblems(srcg);
    if(printErrors()) return null;
    return srcg;
  }

  private static boolean printErrors() {
    if (errors.size() > 0) {
      log.error(
        "The following errors occurred while reading the grammar file:");
      for (Exception e : errors) {
        log.error(e.getMessage(), e);
      }
      return true;
    }
    return false;
  }

  private static void checkForGrammarProblems(Cfg cfg) {
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
  }

  private static void checkForGrammarProblems(Pcfg pcfg) {
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

  private static void checkForGrammarProblems(Srcg srcg) {
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
  }

  private static void checkAuxiliaryTrees(Tag tag) {
    Iterator<String> treeNameIter;
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

  /**
   * For a line like "A", "S" it gets the content of each quote and makes each
   * an element of the returned array.
   */
  private static List<String> parseNT(String lineTrim) {
    Matcher m = p.matcher(lineTrim);
    List<String> nList = new ArrayList<>();
    while (m.find()) {
      String n = m.group();
      nList.add(n.substring(1, n.length() - 1));
    }
    return nList;
  }

  private static Map<String, List<String>> parseDeclarations(String grammarFile)
    throws IOException {
    Map<String, List<String>> declarations = new LinkedHashMap<>();
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
}
