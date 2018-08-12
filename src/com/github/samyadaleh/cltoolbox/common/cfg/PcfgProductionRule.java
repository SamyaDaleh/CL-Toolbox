package com.github.samyadaleh.cltoolbox.common.cfg;

import java.text.ParseException;

/**
 * Representation of a context-free rule where the lhs is only allowed to
 * contain one symbol and the rule has a probability.
 */
public class PcfgProductionRule {
  private Double p = 0.0;
  private final String lhs;
  private final String[] rhs;

  /**
   * Construction of an array of length 3 where the elements are lhs, rhs and
   * probability.
   */
  PcfgProductionRule(String[] rule) {
    this.lhs = rule[0];
    this.rhs = rule[1].split(" ");
    this.p = Double.parseDouble(rule[2]);
  }

  PcfgProductionRule(String lhs, String[] rhs, double p) {
    this.lhs = lhs;
    this.rhs = rhs;
    this.p = p;
  }

  /**
   * Creates a rule from a String representation like 0.5 : S -> A B
   * @throws ParseException
   */
  PcfgProductionRule(String ruleString) throws ParseException {
    if (!ruleString.contains("->")) {
      throw new ParseException("Separator -> missing in rule " + ruleString, 0);
    }
    if (!ruleString.contains(":")) {
      throw new ParseException("Separator : missing in rule " + ruleString, 0);
    }
    if (ruleString.indexOf("->") < ruleString.indexOf(':')) {
      throw new ParseException(": has to be left of -> in rule " + ruleString,
        0);
    }
    String[] ruleSplit = ruleString.split(":", 2);
    this.p = Double.parseDouble(ruleSplit[0].trim());
    String[] ruleSplit2 = ruleSplit[1].split("->", 2);
    this.lhs = ruleSplit2[0].trim();
    if (ruleSplit2[1].trim().equals("") || ruleSplit2[1].trim().equals("ε")) {
      this.rhs = new String[] {""};
    } else {
      this.rhs = ruleSplit2[1].trim().split(" ");
    }
  }

  public String getLhs() {
    return this.lhs;
  }

  public String[] getRhs() {
    return this.rhs;
  }

  public Double getP() {
    return this.p;
  }

  @Override public String toString() {
    if (rhs[0].equals("")) {
      return String.valueOf(p) + " : " + lhs + " -> ε";
    } else {
      return String.valueOf(p) + " : " + lhs + " -> " + String.join(" ", rhs);
    }
  }
}
