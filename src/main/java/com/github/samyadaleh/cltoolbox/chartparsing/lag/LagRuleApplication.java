package com.github.samyadaleh.cltoolbox.chartparsing.lag;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.lag.LagRule;

import java.util.List;

/**
 * Applies a rule of a rule package, that must be part of the rule package the
 * item allows.
 */
public class LagRuleApplication extends AbstractDynamicDeductionRule {
  private final LagRule rule;

  public LagRuleApplication(String ruleName, LagRule lagRule) {
    super();
    this.name = ruleName;
    this.rule = lagRule;
  }

  @Override public List<ChartItemInterface> getConsequences() {
    // TODO implement
    return null;
  }
}
