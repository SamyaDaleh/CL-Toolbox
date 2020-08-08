package com.github.samyadaleh.cltoolbox.common.cfg;

import java.text.ParseException;

/**
 * Representation of a CFG production rule where the lhs consists of one
 * nonterminal and the rhs can be any length.
 */
public class CfgProductionRule {
  private final String lhs;
  private final String[] rhs;

  /** Construction with an array of length 2 which contains lhs and rhs. */
  public CfgProductionRule(String[] rule) {
    this.lhs = rule[0];
    String[] ruleSplit = rule[1].split(" ");
    if ((ruleSplit.length == 1 && ruleSplit[0].equals("ε"))) {
      this.rhs = new String[] {""};
    } else {
      this.rhs = ruleSplit;
    }
  }

  /**
   * Lhs and Rhs passed separately, used when converting one rule format to
   * another.
   */
  public CfgProductionRule(String lhs, String[] rhs) {
    this.lhs = lhs;
    if (rhs.length == 1 && rhs[0].equals("ε")) {
      this.rhs = new String[] {""};
    } else {
      this.rhs = rhs;
    }
  }

  /**
   * Creates a rule from a String representation like S -> A B
   */
  public CfgProductionRule(String ruleString) throws ParseException {
    if (!ruleString.contains("->")) {
      throw new ParseException("Separator -> missing in rule " + ruleString, 0);
    }
    String[] ruleSplit = ruleString.split("->", 2);
    this.lhs = ruleSplit[0].trim();
    if (ruleSplit[1].trim().equals("") || ruleSplit[1].trim().equals("ε")) {
      this.rhs = new String[] {""};
    } else {
      this.rhs = ruleSplit[1].trim().split(" ");
    }
  }

  public String getLhs() {
    return this.lhs;
  }

  public String[] getRhs() {
    return this.rhs;
  }

  @Override public String toString() {
    if (rhs.length == 0 || rhs[0].equals("")) {
      return lhs + " -> ε";
    } else {
      return lhs + " -> " + String.join(" ", rhs);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof CfgProductionRule){
      return o.toString().equals(this.toString());
    }
    if (o instanceof String) {
      return o.equals(this.toString());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }
}
