package common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.cfg.Cfg;
import common.tag.Tag;

public class GrammarParser {
  static Pattern p = Pattern.compile("\"(.*?)\"");

  public static Cfg parseCfgFile(String grammarfile) throws IOException {
    Cfg cfg = new Cfg();
    BufferedReader in = new BufferedReader(new FileReader(grammarfile));
    String line = in.readLine().trim();
    while (line != null) {
      String linetrim = line.trim();
      if (linetrim.charAt(0) == 'N') {
        if (cfg.getVars() != null) {
          System.out.println("Declaring N twice is not allowed");
          in.close();
          return null;
        }
        cfg.setVars(parseNT(linetrim));
      } else if (linetrim.charAt(0) == 'T') {
        if (cfg.getTerminals() != null) {
          System.out.println("Declaring T twice is not allowed");
          in.close();
          return null;
        }
        cfg.setTerminals(parseNT(linetrim));
      } else if (linetrim.charAt(0) == 'S') {
        if (cfg.getStart_var() != null) {
          System.out.println("Declaring S twice is not allowed");
          in.close();
          return null;
        }
        cfg.setStart_var(parseS(linetrim));
      } else if (linetrim.charAt(0) == 'P') {
        if (cfg.getR().size() > 0) {
          System.out.println("Declaring P twice is not allowed");
          in.close();
          return null;
        }
        cfg.setR(parseRule(linetrim, "->"));
      }
      line = in.readLine();
    }
    in.close();
    return cfg;
  }

  private static String[][] parseRule(String linetrim, String delimiter) {
    Matcher m = p.matcher(linetrim);
    m.find();
    String rawrule = m.group();
    ArrayList<String[]> rulelist = new ArrayList<String[]>();
    try {
      while (true) {
        String lhs = rawrule.substring(0, rawrule.indexOf(delimiter)).trim();
        String rhs = rawrule.substring(rawrule.indexOf(delimiter) + delimiter.length()).trim();
        rulelist.add(new String[] {lhs.substring(1),
          rhs.substring(0, rhs.length() - 1)});
        m.find();
        rawrule = m.group();
      }
    } catch (IllegalStateException e) {
      //
    }
    return rulelist.toArray(new String[rulelist.size()][]);
  }

  public static Tag parseTagFile(String grammarfile) throws IOException, ParseException {
    Tag tag = new Tag();
    BufferedReader in = new BufferedReader(new FileReader(grammarfile));
    String line = in.readLine().trim();
    while (line != null) {
      String linetrim = line.trim();
      if (linetrim.charAt(0) == 'N') {
        if (tag.getNonterminals() != null) {
          System.out.println("Declaring N twice is not allowed");
          in.close();
          return null;
        }
        tag.setNonterminals(parseNT(linetrim));
      } else if (linetrim.charAt(0) == 'T') {
        if (tag.getTerminals() != null) {
          System.out.println("Declaring T twice is not allowed");
          in.close();
          return null;
        }
        tag.setTerminals(parseNT(linetrim));
      } else if (linetrim.charAt(0) == 'S') {
        if (tag.getStartSymbol() != null) {
          System.out.println("Declaring S twice is not allowed");
          in.close();
          return null;
        }
        tag.setStartsymbol(parseS(linetrim));
      } else if (linetrim.charAt(0) == 'I') {
        if (tag.getInitialTreeNames().size() > 0) {
          System.out.println("Declaring I twice is not allowed");
          in.close();
          return null;
        }
        for (String[] treedec : parseRule(linetrim, ":")) {
          tag.addInitialTree(treedec[0], treedec[1]);
        }
      } else if (linetrim.charAt(0) == 'A') {
        if (tag.getAuxiliaryTreeNames().size() > 0) {
          System.out.println("Declaring A twice is not allowed");
          in.close();
          return null;
        }
        for (String[] treedec : parseRule(linetrim, ":")) {
          tag.addAuxiliaryTree(treedec[0], treedec[1]);
        }
      }
      line = in.readLine();
    }
    in.close();
    return tag;
  }
  
  private static String[] parseNT(String linetrim) {
    Matcher m = p.matcher(linetrim);
    m.find();
    String n = m.group();
    ArrayList<String> nlist = new ArrayList<String>();
    try {
      while (true) {
        nlist.add(n.substring(1, n.length() - 1));
        m.find();
        n = m.group();
      }
    } catch (IllegalStateException e) {
      //
    }
    return nlist.toArray(new String[nlist.size()]);
  }
  
  private static String parseS(String linetrim) {
    Matcher m = p.matcher(linetrim);
    m.find();
    String s = m.group();
    return s.substring(1, s.length() - 1);
  }
}
