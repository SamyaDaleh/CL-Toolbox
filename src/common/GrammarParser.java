package common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.cfg.Cfg;
import common.cfg.Pcfg;
import common.lcfrs.Srcg;
import common.tag.Tag;

/** Parses different grammars from text files. */
public class GrammarParser {
  private static final Pattern p = Pattern.compile("\"(.*?)\"");

  /** Parses a CFG from a file and returns it as Cfg. */
  public static Cfg parseCfgFile(String grammarFile) throws IOException {
    Cfg cfg = new Cfg();
    BufferedReader in = new BufferedReader(new FileReader(grammarFile));
    String line = in.readLine().trim();
    while (line != null) {
      String lineTrim = line.trim();
      if (lineTrim.charAt(0) == 'N') {
        if (cfg.getNonterminals() != null) {
          System.out.println("Declaring N twice is not allowed");
          in.close();
          return null;
        }
        cfg.setNonterminals(parseNT(lineTrim));
      } else if (lineTrim.charAt(0) == 'T') {
        if (cfg.getTerminals() != null) {
          System.out.println("Declaring T twice is not allowed");
          in.close();
          return null;
        }
        cfg.setTerminals(parseNT(lineTrim));
      } else if (lineTrim.charAt(0) == 'S') {
        if (cfg.getStartSymbol() != null) {
          System.out.println("Declaring S twice is not allowed");
          in.close();
          return null;
        }
        cfg.setStartSymbol(parseS(lineTrim));
      } else if (lineTrim.charAt(0) == 'P') {
        if (cfg.getProductionRules().size() > 0) {
          System.out.println("Declaring P twice is not allowed");
          in.close();
          return null;
        }
        for (String rule : parseNT(lineTrim)) {
          cfg.addProductionRule(rule);
        }
      }
      line = in.readLine();
    }
    in.close();
    return cfg;
  }

  /** Parses a PCFG from a file and returns it as Pcfg. */
  public static Pcfg parsePcfgFile(String grammarFile) throws IOException {
    Pcfg pcfg = new Pcfg();
    BufferedReader in = new BufferedReader(new FileReader(grammarFile));
    String line = in.readLine().trim();
    while (line != null) {
      String lineTrim = line.trim();
      if (lineTrim.charAt(0) == 'N') {
        if (pcfg.getNonterminals() != null) {
          System.out.println("Declaring N twice is not allowed");
          in.close();
          return null;
        }
        pcfg.setNonterminals(parseNT(lineTrim));
      } else if (lineTrim.charAt(0) == 'T') {
        if (pcfg.getTerminals() != null) {
          System.out.println("Declaring T twice is not allowed");
          in.close();
          return null;
        }
        pcfg.setTerminals(parseNT(lineTrim));
      } else if (lineTrim.charAt(0) == 'S') {
        if (pcfg.getStartSymbol() != null) {
          System.out.println("Declaring S twice is not allowed");
          in.close();
          return null;
        }
        pcfg.setStartSymbol(parseS(lineTrim));
      } else if (lineTrim.charAt(0) == 'P') {
        if (pcfg.getProductionRules().size() > 0) {
          System.out.println("Declaring P twice is not allowed");
          in.close();
          return null;
        }
        pcfg.setProductionRules(parseProbabilisticRules(lineTrim, ":", "->"));
      }
      line = in.readLine();
    }
    in.close();
    return pcfg;
  }

  /** For a line like "S -> a b", "S -> A B" it gets the content of each quote,
   * separates it by delimiter, in this case '->' and returns 2d array where
   * each entry represents a rule and each sub array consists of lhs and rhs */
  private static String[][] parseRules(String lineTrim, String delimiter) {
    Matcher m = p.matcher(lineTrim);
    ArrayList<String[]> ruleList = new ArrayList<String[]>();
    while (m.find()) {
      String rawRule = m.group();
      String lhs = rawRule.substring(0, rawRule.indexOf(delimiter)).trim();
      String rhs = rawRule
        .substring(rawRule.indexOf(delimiter) + delimiter.length()).trim();
      ruleList.add(
        new String[] {lhs.substring(1), rhs.substring(0, rhs.length() - 1)});
    }

    return ruleList.toArray(new String[ruleList.size()][]);
  }

  /** For a line like "1 : S -> a b", "1 : S -> A B" it gets the content of each
   * quote, separates it by delimiters, in this case ':' and '->' and returns 2d
   * array where each entry represents a rule and each sub array consists of
   * lhs, rhs and p */
  private static String[][] parseProbabilisticRules(String lineTrim,
    String leftDelimiter, String rightDelimiter) {
    Matcher m = p.matcher(lineTrim);
    ArrayList<String[]> ruleList = new ArrayList<String[]>();
    while (m.find()) {
      String rawRule = m.group();
      String p = rawRule.substring(0, rawRule.indexOf(leftDelimiter)).trim();
      String lhs = rawRule
        .substring(rawRule.indexOf(leftDelimiter) + leftDelimiter.length(),
          rawRule.indexOf(rightDelimiter))
        .trim();
      String rhs = rawRule
        .substring(rawRule.indexOf(rightDelimiter) + rightDelimiter.length())
        .trim();
      ruleList.add(
        new String[] {lhs, rhs.substring(0, rhs.length() - 1), p.substring(1)});
    }
    return ruleList.toArray(new String[ruleList.size()][]);
  }

  /** Parses a TAG from a text file and returns it as a Tag object. */
  public static Tag parseTagFile(String grammarFile)
    throws IOException, ParseException {
    Tag tag = new Tag();
    BufferedReader in = new BufferedReader(new FileReader(grammarFile));
    String line = in.readLine().trim();
    while (line != null) {
      String lineTrim = line.trim();
      if (lineTrim.charAt(0) == 'N') {
        if (tag.getNonterminals() != null) {
          System.out.println("Declaring N twice is not allowed");
          in.close();
          return null;
        }
        tag.setNonterminals(parseNT(lineTrim));
      } else if (lineTrim.charAt(0) == 'T') {
        if (tag.getTerminals() != null) {
          System.out.println("Declaring T twice is not allowed");
          in.close();
          return null;
        }
        tag.setTerminals(parseNT(lineTrim));
      } else if (lineTrim.charAt(0) == 'S') {
        if (tag.getStartSymbol() != null) {
          System.out.println("Declaring S twice is not allowed");
          in.close();
          return null;
        }
        tag.setStartsymbol(parseS(lineTrim));
      } else if (lineTrim.charAt(0) == 'I') {
        if (tag.getInitialTreeNames().size() > 0) {
          System.out.println("Declaring I twice is not allowed");
          in.close();
          return null;
        }
        for (String[] treedec : parseRules(lineTrim, ":")) {
          tag.addInitialTree(treedec[0], treedec[1]);
        }
      } else if (lineTrim.charAt(0) == 'A') {
        if (tag.getAuxiliaryTreeNames().size() > 0) {
          System.out.println("Declaring A twice is not allowed");
          in.close();
          return null;
        }
        for (String[] treeDec : parseRules(lineTrim, ":")) {
          tag.addAuxiliaryTree(treeDec[0], treeDec[1]);
        }
      }
      line = in.readLine();
    }
    in.close();
    return tag;
  }

  /** For a line like "A", "S" it gets the content of each quote and makes each
   * an element of the returned array. */
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
    m.find();
    String s = m.group();
    return s.substring(1, s.length() - 1);
  }

  /** Parses a sRCG from a file and returns it as Srcg. */
  public static Srcg parseSrcgFile(String grammarFile)
    throws IOException, ParseException {
    Srcg srcg = new Srcg();
    BufferedReader in = new BufferedReader(new FileReader(grammarFile));
    String line = in.readLine().trim();
    while (line != null) {
      String lineTrim = line.trim();
      if (lineTrim.charAt(0) == 'N') {
        if (srcg.getNonterminals() != null) {
          System.out.println("Declaring N twice is not allowed");
          in.close();
          return null;
        }
        srcg.setNonterminals(parseNT(lineTrim));
      } else if (lineTrim.charAt(0) == 'V') {
        if (srcg.getVariables() != null) {
          System.out.println("Declaring V twice is not allowed");
          in.close();
          return null;
        }
        srcg.setVariables(parseNT(lineTrim));
      } else if (lineTrim.charAt(0) == 'T') {
        if (srcg.getTerminals() != null) {
          System.out.println("Declaring T twice is not allowed");
          in.close();
          return null;
        }
        srcg.setTerminals(parseNT(lineTrim));
      } else if (lineTrim.charAt(0) == 'S') {
        if (srcg.getStartSymbol() != null) {
          System.out.println("Declaring S twice is not allowed");
          in.close();
          return null;
        }
        srcg.setStartSymbol(parseS(lineTrim));
      } else if (lineTrim.charAt(0) == 'P') {
        if (srcg.getClauses().size() > 0) {
          System.out.println("Declaring P twice is not allowed");
          in.close();
          return null;
        }
        for (String[] clauseDec : parseRules(lineTrim, "->")) {
          srcg.addClause(clauseDec[0], clauseDec[1]);
        }
      }
      line = in.readLine();
    }
    in.close();
    return srcg;
  }
}
