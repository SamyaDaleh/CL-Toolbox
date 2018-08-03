package common;

import static common.ArrayUtils.contains;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.cfg.Cfg;
import common.cfg.CfgProductionRule;
import common.cfg.Pcfg;
import common.cfg.PcfgProductionRule;
import common.lcfrs.Clause;
import common.lcfrs.Predicate;
import common.lcfrs.Srcg;
import common.tag.Tag;
import common.tag.Tree;
import common.tag.Vertex;

/** Parses different grammars from text files. */
public class GrammarParser {
  private static final Pattern p = Pattern.compile("\"(.*?)\"");
  private static List<Exception> errors;

  /** Parses a CFG from a file and returns it as Cfg. */
  public static Cfg parseCfgFile(String grammarFile) throws IOException {
    errors = new ArrayList<Exception>();
    Cfg cfg = new Cfg();
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
      }
      switch (lineTrim.charAt(0)) {
      case 'N':
        if (cfg.getNonterminals() != null) {
          errors.add(new ParseException("N was declared twice.", 0));
          break;
        }
        cfg.setNonterminals(parseNT(lineTrim));
        break;
      case 'T':
        if (cfg.getTerminals() != null) {
          errors.add(new ParseException("T was declared twice.", 0));
          break;
        }
        cfg.setTerminals(parseNT(lineTrim));
        break;
      case 'S':
        if (cfg.getStartSymbol() != null) {
          errors.add(new ParseException("S was declared twice.", 0));
          break;
        }
        cfg.setStartSymbol(parseS(lineTrim));
        break;
      case 'P':
        if (cfg.getProductionRules().size() > 0) {
          errors.add(new ParseException("P was declared twice.", 0));
          break;
        }
        for (String rule : parseNT(lineTrim)) {
          try {
            cfg.addProductionRule(rule);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case 'G':
        System.out.println("Grammar declaration detected. Nothing to do.");
        break;
      default:
        errors.add(new ParseException(
          "Unknown declaration symbol: " + lineTrim.charAt(0), 0));
      }
      line = in.readLine();
    }
    in.close();
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

  /** Parses a PCFG from a file and returns it as Pcfg. */
  public static Pcfg parsePcfgFile(String grammarFile) throws IOException {
    errors = new ArrayList<Exception>();
    Pcfg pcfg = new Pcfg();
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
      }
      switch (lineTrim.charAt(0)) {
      case 'N':
        if (pcfg.getNonterminals() != null) {
          errors.add(new ParseException("N was declared twice.", 0));
          in.close();
          return null;
        }
        pcfg.setNonterminals(parseNT(lineTrim));
        break;
      case 'T':
        if (pcfg.getTerminals() != null) {
          errors.add(new ParseException("T was declared twice.", 0));
          in.close();
          return null;
        }
        pcfg.setTerminals(parseNT(lineTrim));
        break;
      case 'S':
        if (pcfg.getStartSymbol() != null) {
          errors.add(new ParseException("S was declared twice.", 0));
          in.close();
          return null;
        }
        pcfg.setStartSymbol(parseS(lineTrim));
        break;
      case 'P':
        if (pcfg.getProductionRules().size() > 0) {
          errors.add(new ParseException("P was declared twice.", 0));
          in.close();
          return null;
        }
        for (String rule : parseNT(lineTrim)) {
          try {
            pcfg.addProductionRule(rule);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case 'G':
        System.out.println("Grammar declaration detected. Nothing to do.");
        break;
      default:
        System.err.println("Unknown declaration symbol: " + lineTrim.charAt(0));
      }
      line = in.readLine();
    }
    in.close();
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
    errors = new ArrayList<Exception>();
    Tag tag = new Tag();
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
      }
      switch (lineTrim.charAt(0)) {
      case 'N':
        if (tag.getNonterminals() != null) {
          System.out.println("Declaring N twice is not allowed");
          in.close();
          return null;
        }
        tag.setNonterminals(parseNT(lineTrim));
        break;
      case 'T':
        if (tag.getTerminals() != null) {
          System.out.println("Declaring T twice is not allowed");
          in.close();
          return null;
        }
        tag.setTerminals(parseNT(lineTrim));
        break;
      case 'S':
        if (tag.getStartSymbol() != null) {
          System.out.println("Declaring S twice is not allowed");
          in.close();
          return null;
        }
        tag.setStartSymbol(parseS(lineTrim));
        break;
      case 'I':
        if (tag.getInitialTreeNames().size() > 0) {
          System.out.println("Declaring I twice is not allowed");
          in.close();
          return null;
        }
        for (String treeDec : parseNT(lineTrim)) {
          try {
            tag.addInitialTree(treeDec);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case 'A':
        if (tag.getAuxiliaryTreeNames().size() > 0) {
          System.out.println("Declaring A twice is not allowed");
          in.close();
          return null;
        }
        for (String treeDec : parseNT(lineTrim)) {
          try {
            tag.addAuxiliaryTree(treeDec);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case 'G':
        System.out.println("Grammar declaration detected. Nothing to do.");
        break;
      default:
        System.err.println("Unknown declaration symbol: " + lineTrim.charAt(0));
      }
      line = in.readLine();
    }
    in.close();
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
        if (tree.getNodeByGornAdress(v.getGornAddress()+ ".1") != null
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
  private static String[] parseNT(String lineTrim) {
    Matcher m = p.matcher(lineTrim);
    ArrayList<String> nList = new ArrayList<String>();
    while (m.find()) {
      String n = m.group();
      nList.add(n.substring(1, n.length() - 1));
    }
    return nList.toArray(new String[nList.size()]);
  }

  /** Takes a line like "S" and returns the string inside the quotes. */
  private static String parseS(String lineTrim) {
    Matcher m = p.matcher(lineTrim);
    if (m.find()) {
      String s = m.group();
      return s.substring(1, s.length() - 1);
    }
    errors.add(new ParseException(
      "No declaration of start symbol found in line " + lineTrim, 0));
    return null;
  }

  /** Parses a sRCG from a file and returns it as Srcg. */
  public static Srcg parseSrcgFile(String grammarFile) throws IOException {
    errors = new ArrayList<Exception>();
    Srcg srcg = new Srcg();
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
      }
      switch (lineTrim.charAt(0)) {
      case 'N':
        if (srcg.getNonterminals() != null) {
          System.out.println("Declaring N twice is not allowed");
          in.close();
          return null;
        }
        srcg.setNonterminals(parseNT(lineTrim));
        break;
      case 'V':
        if (srcg.getVariables() != null) {
          System.out.println("Declaring V twice is not allowed");
          in.close();
          return null;
        }
        srcg.setVariables(parseNT(lineTrim));
        break;
      case 'T':
        if (srcg.getTerminals() != null) {
          System.out.println("Declaring T twice is not allowed");
          in.close();
          return null;
        }
        srcg.setTerminals(parseNT(lineTrim));
        break;
      case 'S':
        if (srcg.getStartSymbol() != null) {
          System.out.println("Declaring S twice is not allowed");
          in.close();
          return null;
        }
        srcg.setStartSymbol(parseS(lineTrim));
        break;
      case 'P':
        if (srcg.getClauses().size() > 0) {
          System.out.println("Declaring P twice is not allowed");
          in.close();
          return null;
        }
        for (String clauseDec : parseNT(lineTrim)) {
          try {
            srcg.addClause(clauseDec);
          } catch (ParseException e) {
            errors.add(e);
          }
        }
        break;
      case 'G':
        System.out.println("Grammar declaration detected. Nothing to do.");
        break;
      default:
        System.err.println("Unknown declaration symbol: " + lineTrim.charAt(0));
      }
      line = in.readLine();
    }
    in.close();
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
