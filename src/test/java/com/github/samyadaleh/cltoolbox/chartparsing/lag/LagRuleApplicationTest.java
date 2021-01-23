package com.github.samyadaleh.cltoolbox.chartparsing.lag;

import com.github.samyadaleh.cltoolbox.common.lag.LagRule;
import com.github.samyadaleh.cltoolbox.common.lag.LagState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LagRuleApplicationTest {

  @Test public void testReplaceX() {
    LagState lagState =
        new LagState(new String[] {"r1", "r2"}, new String[] {"b", "X", "c"});
    LagRule lagRule =
        new LagRule(new String[] {"X"}, new String[] {"b", "c"}, lagState);
    LagRuleApplication rule = new LagRuleApplication("r1", lagRule);
    String[] collextX = new String[]{"b", "c"};
    String[] categories = new String[]{"b", "X", "c"};
    String[] xReplaced = rule.replaceX(categories,collextX);
    assertEquals(4, xReplaced.length);
  }
}
