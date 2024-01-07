package com.github.samyadaleh.cltoolbox.common.cfg;

import java.text.ParseException;

import static com.github.samyadaleh.cltoolbox.common.Constants.ARROW_RIGHT;
import static com.github.samyadaleh.cltoolbox.common.Constants.EPSILON;

/**
 * Representation of a context-free rule where the lhs is only allowed to
 * contain one symbol and the rule has a probability.
 */
public class PcfgProductionRule extends CfgProductionRule {
  private final Double p;

  /**
   * Construction of an array of length 3 where the elements are lhs, rhs and
   * probability.
   */
  PcfgProductionRule(String[] rule) {
    super(rule[0], rule[1].split(" "));
    this.p = Double.parseDouble(rule[2]);
  }

  PcfgProductionRule(String lhs, String[] rhs, double p) {
    super(lhs, rhs);
    this.p = p;
  }

  /**
   * Creates a rule from a String representation like 0.5 : S -> A B
   */
  PcfgProductionRule(String ruleString) throws ParseException {
    super(ruleString.split(":", 2)[1]);
    if (ruleString.indexOf(ARROW_RIGHT) < ruleString.indexOf(':')) {
      throw new ParseException(": has to be left of " + ARROW_RIGHT + " in rule " + ruleString,
        0);
    }
    String[] ruleSplit = ruleString.split(":", 2);
    this.p = Double.parseDouble(ruleSplit[0].trim());
    }

  public Double getP() {
    return this.p;
  }

  @Override public String toString() {
    double roundedP = Math.round(p * 100.0) / 100.0;
    if (this.getRhs()[0].equals("")) {
      return roundedP + " : " + this.getLhs() + " " + ARROW_RIGHT + " " + EPSILON;
    } else {
      return roundedP + " : " + this.getLhs() + " " + ARROW_RIGHT + " "
          + String.join(" ", this.getRhs());
    }
  }
}
