package chartparsing.lcfrsrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.lcfrs.Clause;

/** Similar to the binary complete rule in CYK for CFG. If there is a clause and
 * the vectors of two items that represent the rhs match, combine them to a new item that represents
 * the lhs with span over both. */
public class SrcgCykBinary extends AbstractDynamicDeductionRule {
  
  private final Clause clause;
  
  public SrcgCykBinary(Clause clause) {
    this.name = "Binary";
    this.antneeded = 2;
    this.clause = clause;
  }

  @Override public List<Item> getConsequences() {
    // TODO Auto-generated method stub
    return null;
  }

}
