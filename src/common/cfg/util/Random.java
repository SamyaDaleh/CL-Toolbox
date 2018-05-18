package common.cfg.util;

import java.text.ParseException;
import java.util.ArrayList;

import common.cfg.Cfg;

/**
 * Generates a random context free grammar
 */
public class Random {
  private static java.util.Random r = new java.util.Random();

  /**
   * Returns a random context free grammar containing from 1 to infinite
   * terminals and nonterminals. One nonterminal is randomly picked as start
   * symbol. A production rule contains of a random nonterminal als lhs and an
   * arbitrary long sequence of terminals and nonterminals including length
   * zero.
   */
  public static Cfg getRandomCfg() throws ParseException {
    Cfg cfg = new Cfg();
    String[] terminalArray = getRandomArray("t");
    cfg.setTerminals(terminalArray);
    String[] nonterminalArray = getRandomArray("N");
    cfg.setNonterminals(nonterminalArray);
    String s = getRandomArrayElement(nonterminalArray);
    cfg.setStartSymbol(s);
    setRandomProductionRules(cfg);
    return cfg;
  }

  /**
   * Returns a random element of the passed string array.
   */
  public static String getRandomArrayElement(String[] array) {
    int pos = r.nextInt(array.length);
    return array[pos];
  }

  /**
   * Generates random production rules for the cfg. The first production rule
   * has the start symbol as lhs. An arbitrary number of other production rules
   * is added, even zero.
   */
  private static void setRandomProductionRules(Cfg cfg) throws ParseException {
    String rhs = getRandomRhs(cfg);
    cfg.addProductionRule(cfg.getStartSymbol() + " -> " + rhs);
    while (r.nextBoolean()) {
      rhs = getRandomRhs(cfg);
      String lhs = getRandomArrayElement(cfg.getNonterminals());
      cfg.addProductionRule(lhs + " -> " + rhs);
    }
  }

  /**
   * Returns the string representation of a rule's rhs. It contains an arbitrary
   * sequence of terminals and nonterminals of that grammar, even zero. The
   * occurence of terminals and nonterminals is equally likely.
   */
  public static String getRandomRhs(Cfg cfg) {
    StringBuilder rhs = new StringBuilder();
    while (r.nextBoolean()) {
      String symbol;
      if (r.nextBoolean()) {
        symbol = getRandomArrayElement(cfg.getTerminals());
      } else {
        symbol = getRandomArrayElement(cfg.getNonterminals());
      }
      if (rhs.length() > 0) {
        rhs.append(' ');
      }
      rhs.append(symbol);
    }
    return rhs.toString();
  }

  /**
   * Generates an arbitrary long array of at least length 1. The array consists
   * of elements starting with the passed prefix followed by an incrementing
   * number.
   */
  private static String[] getRandomArray(String prefix) {
    int numt = 0;
    ArrayList<String> terminals = new ArrayList<String>();
    terminals.add(prefix + numt);
    while (r.nextBoolean()) {
      numt++;
      terminals.add(prefix + numt);
    }
    String[] terminalArray = terminals.toArray(new String[terminals.size()]);
    return terminalArray;
  }
}
