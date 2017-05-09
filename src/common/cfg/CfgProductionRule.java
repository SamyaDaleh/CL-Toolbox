package common.cfg;

/** Representation of a CFG production rule where the lhs consists of one
 * nonterminal and the rhs can be any length. */
public class CfgProductionRule {
  String lhs;
  String[] rhs;

  /** Construction with an array of length 2 which contains lhs and rhs. */
  CfgProductionRule(String[] rule) {
    this.lhs = rule[0];
    this.rhs = rule[1].split(" ");
  }

  /** Constructor where left and right rule side are passed separately. */
  CfgProductionRule(String lhs, String rhs) {
    this.lhs = lhs;
    this.rhs = rhs.split(" ");
  }

  /** Lhs and Rhs passed separately, used when converting one rule format to
   * another. */
  public CfgProductionRule(String lhs, String[] rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public String getLhs() {
    return this.lhs;
  }

  public String[] getRhs() {
    return this.rhs;
  }
}
