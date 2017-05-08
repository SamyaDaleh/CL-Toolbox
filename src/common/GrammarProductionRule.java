package common;

/** Representation of an arbitrary production rule of a grammar. */
public class GrammarProductionRule {
  String[] lhs;
  String[] rhs;
  Double p = 0.0;

  /**
   * Creates rule from an array that is either 2 or 3 elements long. The 
   * elements are left rule side, right rule side and optional probabilities.
   */
  GrammarProductionRule(String[] rule) {
    this.lhs = rule[0].split(" ");
    this.rhs = rule[1].split(" ");
    if (rule.length == 3) {
      this.p = Double.parseDouble(rule[2]);
    }
  }

  /**
   * Creates a production rule from left and right rule sides where spaces
   * separate the tokens.
   */
  GrammarProductionRule(String lhs, String rhs) {
    this.lhs = lhs.split(" ");
    this.rhs = rhs.split(" ");
  }

  /**
   * Creates a production rule from left and right rule sides where spaces
   * separate the tokens and p is the probability of the rule.
   */
  GrammarProductionRule(String lhs, String rhs, Double p) {
    this.lhs = lhs.split(" ");
    this.rhs = rhs.split(" ");
    this.p = p;
  }

  public String[] getLhs() {
    return this.lhs;
  }

  public String[] getRhs() {
    return this.rhs;
  }

  public Double getP() {
    return this.p;
  }
}
