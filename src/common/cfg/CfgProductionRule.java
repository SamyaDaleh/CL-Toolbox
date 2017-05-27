package common.cfg;

/** Representation of a CFG production rule where the lhs consists of one
 * nonterminal and the rhs can be any length. */
public class CfgProductionRule {
  final String lhs;
  final String[] rhs;

  /** Construction with an array of length 2 which contains lhs and rhs. */
  CfgProductionRule(String[] rule) {
    this.lhs = rule[0];
    String[] rulesplit = rule[1].split(" ");
    if (rulesplit.length == 1 && rulesplit[0].equals("ε")) {
      this.rhs = new String[] {""};
    } else {
      this.rhs = rulesplit;
    }
  }

  

  /** Lhs and Rhs passed separately, used when converting one rule format to
   * another. */
  CfgProductionRule(String lhs, String[] rhs) {
    this.lhs = lhs;
    if (rhs.length == 1 && rhs[0].equals("ε")) {
      this.rhs = new String[] {""};
    } else {
      this.rhs = rhs;
    }
  }

  public String getLhs() {
    return this.lhs;
  }

  public String[] getRhs() {
    return this.rhs;
  }

  @Override public String toString() {
    if (rhs[0].equals("")) {
      return lhs + " -> ε";
    } else {
      return lhs + " -> " + String.join(" ", rhs);
    }
  }
}
