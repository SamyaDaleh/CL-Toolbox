package common.lcfrs;

import java.util.ArrayList;
import java.util.Collections;

import common.ArrayUtils;

/** Representation of a predicate of the form A(ɑ1,...,ɑ_dim(A)). */
public class Predicate {
  private final String nonterminal;
  private final String[][] symbols;

  /** Constructor that creates the predicate from a string representation. */
  public Predicate(String predicate) {
    int lbrack = predicate.indexOf('(');
    int rbrack = predicate.indexOf(')');
    this.nonterminal = predicate.substring(0, lbrack).trim();
    String rightover = predicate.substring(lbrack + 1, rbrack).trim();
    if (rightover.length() == 0 || rightover.equals("ε")) {
      symbols = new String[][] {new String[] {""}};
    } else {
      String[] subgroups = rightover.split(",");
      ArrayList<String[]> subgroupcol = new ArrayList<String[]>();
      for (String subgroup : subgroups) {
        if (subgroup.trim().equals("ε")) {
          subgroupcol.add(new String[] {""});
        } else {
          subgroupcol.add(subgroup.trim().split(" "));
        }
      }
      symbols = subgroupcol.toArray(new String[subgroupcol.size()][]);
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
        repr.append("ε");
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
    ArrayList<String> symbolsarray = new ArrayList<String>();
    for (String[] symset : symbols) {
      Collections.addAll(symbolsarray, symset);
    }
    return symbolsarray.toArray(new String[symbolsarray.size()]);
  }

  /** Returns a string representation where the dot is at the ith argument at
   * the jth element. */
  public String setDotAt(int i, int j) {
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

  public String getSymAt(int i, int j) {
    return this.symbols[i - 1][j];
  }

  public String[] getArgumentByIndex(int i) {
    return symbols[i - 1];
  }

  public int getDim() {
    return symbols.length;
  }

  /** If all elements were a plain list, return the index the specified item
   * would have. */
  public int getAbsolutePos(int iint, int jint) {
    int index = 0;
    for (int i = 1; i < iint; i++) {
      index += symbols[i - 1].length;
    }
    return index + jint;
  }

  public boolean ifSymExists(int i, int j) {
    return symbols.length >= i && symbols[i - 1].length > j;
  }

  /** Looks for a symbol and returns its indices. */
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
