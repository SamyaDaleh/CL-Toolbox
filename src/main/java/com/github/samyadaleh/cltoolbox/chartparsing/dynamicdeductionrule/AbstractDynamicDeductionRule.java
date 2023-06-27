package com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/** Class to hold the methods commonly used by all DeductionRules. */
public abstract class AbstractDynamicDeductionRule
  implements DynamicDeductionRuleInterface {

  protected List<ChartItemInterface> antecedences;
  protected List<ChartItemInterface> consequences;
  protected String name;
  protected static final Logger log = LogManager.getLogger();

  protected int antNeeded;

  @Override public List<ChartItemInterface> getAntecedences() {
    return new ArrayList<>(this.antecedences);
  }

  @Override public void setAntecedences(List<ChartItemInterface> antecedences) {
    this.antecedences = new ArrayList<>(antecedences);
  }
  @Override public String getName() {
    return this.name;
  }

  @Override public int getAntecedencesNeeded() {
    return this.antNeeded;
  }

  @Override public void clearItems() {
    antecedences = new ArrayList<>();
    consequences = new ArrayList<>();
  }

  protected void logItemGeneration(ChartItemInterface item) {
    if(log.isDebugEnabled()) {
      StringBuilder out = new StringBuilder("generated: ");
      out.append(item).append(" with trees:");
      for (Tree tree : item.getTrees()) {
        out.append(' ').append(tree).append(',');
      }
      out.append(" from:");
      for(ChartItemInterface antecedence : antecedences) {
        out.append(' ').append(antecedence);
      }
      out.append(" with rule ").append(name);
      log.debug(out.toString() );
    }
  }

}
