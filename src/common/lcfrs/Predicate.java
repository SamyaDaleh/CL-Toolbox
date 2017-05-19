package common.lcfrs;

import java.util.ArrayList;

import common.ArrayUtils;

/** Representation of a predicate of the form A(ɑ1,...,ɑ_dim(A)). */
public class Predicate {
  String nonterminal;
  String[][] symbols;

  /** Constructor that creates the predicate from a string representation. */
  Predicate(String predicate) {
    int lbrack = predicate.indexOf('(');
    int rbrack = predicate.indexOf(')');
    this.nonterminal = predicate.substring(0, lbrack).trim();
    String rightover = predicate.substring(lbrack + 1, rbrack).trim();
    if (rightover.length() == 0 || rightover.equals("ε")) {
      symbols = new String[][] {};
    } else {
      String[] subgroups = rightover.split(",");
      ArrayList<String[]> subgroupcol = new ArrayList<String[]>();
      for (String subgroup : subgroups) {
        subgroupcol.add(subgroup.split(" "));
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
      repr.append(
        ArrayUtils.getSubSequenceAsString(symbols[i], 0, symbols[i].length));
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
      for (String symbol : symset) {
        symbolsarray.add(symbol);
      }
    }
    return symbolsarray.toArray(new String[symbolsarray.size()]);
  }

  /** Returns a string representation where the dot is at position i of the
   * variables. */
  public String setDotAt(int i) {
    StringBuilder repr = new StringBuilder();
    repr.append(nonterminal);
    repr.append('(');
    int l = 0;
    for (int j = 0; j < symbols.length; j++) {
      if (j > 0 ) {
        repr.append(",");
      }
      for (int k = 0; k < symbols[j].length; k++){
        if (k > 0) {
          repr.append(" ");
        }
        if (i == l) {
          repr.append("•");
        }
        repr.append(symbols[j][k]);
        l++;
      }
    }

    if (i == l) {
      repr.append(" •");
    }
    repr.append(')');
    return repr.toString();
  }
}
