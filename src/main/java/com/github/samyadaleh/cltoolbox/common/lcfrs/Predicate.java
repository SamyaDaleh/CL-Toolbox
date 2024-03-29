package com.github.samyadaleh.cltoolbox.common.lcfrs;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import com.github.samyadaleh.cltoolbox.common.ArrayUtils;

import static com.github.samyadaleh.cltoolbox.common.Constants.EPSILON;

/** Representation of a predicate of the form A(ɑ1,...,ɑ_dim(A)). */
public class Predicate {
  private final String nonterminal;
  private final String[][] symbols;

  /** Constructor that creates the predicate from a string representation. */
  public Predicate(String predicate) throws ParseException {
    int lBrackNum = 0;
    int rBrackNum = 0;
    for (int i = 0; i < predicate.length(); i++) {
      if (predicate.charAt(i) == '(')
        lBrackNum++;
      if (predicate.charAt(i) == ')')
        rBrackNum++;
    }
    if (lBrackNum != 1 || rBrackNum != 1) {
      throw new ParseException(
        "Amounts of opening or closing brackets is not correct.", 0);
    }
    int lBrack = predicate.indexOf('(');
    int rBrack = predicate.indexOf(')');
    if (rBrack < lBrack) {
      throw new ParseException("Closing bracket is left of opening bracket.",
        0);
    }
    this.nonterminal = predicate.substring(0, lBrack).trim();
    String rightOver = predicate.substring(lBrack + 1, rBrack).trim();
    if (rightOver.length() == 0 || rightOver.equals(EPSILON)) {
      symbols = new String[][] {new String[] {""}};
    } else {
      String[] subgroups = rightOver.split(",");
      ArrayList<String[]> subgroupCol = new ArrayList<>();
      for (String subgroup : subgroups) {
        if (subgroup.trim().equals(EPSILON)) {
          subgroupCol.add(new String[] {""});
        } else {
          subgroupCol.add(subgroup.trim().split(" "));
        }
      }
      symbols = subgroupCol.toArray(new String[subgroupCol.size()][]);
    }
  }

  @Override public String toString() {
    StringBuilder repr = new StringBuilder();
    repr.append(nonterminal);
    repr.append('(');
    for (int i = 0; i < symbols.length; i++) {
      if (i > 0)
        repr.append(",");
      if (symbols[i].length == 1 && symbols[i][0].equals("")) {
        repr.append(EPSILON);
      } else {
        repr.append(
          ArrayUtils.getSubSequenceAsString(symbols[i], 0, symbols[i].length));
      }
    }
    repr.append(')');
    return repr.toString();
  }

  public String getNonterminal() {
    return this.nonterminal;
  }

  public String[][] getSymbols() {
    return this.symbols;
  }

  public String[] getSymbolsAsPlainArray() {
    ArrayList<String> symbolsArray = new ArrayList<>();
    for (String[] symSet : symbols) {
      Collections.addAll(symbolsArray, symSet);
    }
    return symbolsArray.toArray(new String[0]);
  }

  /**
   * Returns a string representation where the dot is at the ith argument at the
   * jth element.
   */
  String setDotAt(int i, int j) {
    StringBuilder repr = new StringBuilder();
    repr.append(nonterminal);
    repr.append('(');
    for (int l = 0; l < symbols.length; l++) {
      if (l > 0) {
        repr.append(",");
      }
      for (int k = 0; k < symbols[l].length; k++) {
        if (k > 0) {
          repr.append(" ");
        }
        if (i - 1 == l && j == k) {
          repr.append("•");
        }
        repr.append(symbols[l][k]);
      }
      if (i - 1 == l && j == symbols[l].length) {
        repr.append(" •");
      }
    }
    repr.append(')');
    return repr.toString();
  }

  /**
   * Returns from argument i the jth symbol. According to the definition the
   * indices for i start at 1.
   */
  public String getSymAt(int i, int j) {
    return this.symbols[i - 1][j];
  }

  /**
   * Returns true if from argument i the jth symbol exists. According to the
   * definition the indices for i start at 1.
   */
  public boolean hasSymAt(int i, int j) {
    if (this.symbols.length >= i) {
      return symbols[i - 1].length > j;
    }
    return false;
  }

  /**
   * According to the definition argument indices start at 1.
   */
  public String[] getArgumentByIndex(int i) {
    return symbols[i - 1];
  }

  public int getDim() {
    return symbols.length;
  }

  /**
   * If all elements were a plain list, return the index the specified item
   * would have.
   */
  public int getAbsolutePos(int iInt, int jInt) {
    int index = 0;
    for (int i = 1; i < iInt; i++) {
      index += symbols[i - 1].length;
    }
    return index + jInt;
  }

  public boolean ifSymExists(int i, int j) {
    return symbols.length >= i && symbols[i - 1].length > j;
  }

  /**
   * Looks for a symbol and returns its indices. Returns [-1, -1] if not found.
   */
  public int[] find(String symbol) {
    for (int i = 0; i < symbols.length; i++) {
      for (int j = 0; j < symbols[i].length; j++) {
        if (symbols[i][j].equals(symbol)) {
          return new int[] {i + 1, j};
        }
      }
    }
    return new int[] {-1, -1};
  }
}
