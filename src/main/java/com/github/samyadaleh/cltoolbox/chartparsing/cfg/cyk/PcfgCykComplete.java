package com.github.samyadaleh.cltoolbox.chartparsing.cfg.cyk;

import com.github.samyadaleh.cltoolbox.chartparsing.dynamicdeductionrule.AbstractDynamicDecutionRuleTwoAntecedences;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ProbabilisticChartItemInterface;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.cfg.PcfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.github.samyadaleh.cltoolbox.common.Constants.DEDUCTION_RULE_PCFG_CYK_COMPLETE;

/**
 * If two items match the rhs of a rule, get a new item that represents the lhs.
 * Decides based on the rule it is instantiated with if it behaves weighted.
 */
public class PcfgCykComplete
    extends AbstractDynamicDecutionRuleTwoAntecedences {

  protected final CfgProductionRule pRule;

  private static final Logger log = LogManager.getLogger();

  public PcfgCykComplete(CfgProductionRule pRule) {
    this.pRule = pRule;
    this.name = DEDUCTION_RULE_PCFG_CYK_COMPLETE + " " + pRule.toString();
    this.antNeeded = 2;
  }

  protected void calculateConsequences(String[] itemForm1, String[] itemForm2) {
    String nt1 = itemForm1[0];
    String i1 = itemForm1[1];
    int i1Int = Integer.parseInt(i1);
    String j1 = itemForm1[2];
    int j1Int = Integer.parseInt(j1);

    String nt2 = itemForm2[0];
    String i2 = itemForm2[1];
    int i2Int = Integer.parseInt(i2);
    String j2 = itemForm2[2];
    int j2Int = Integer.parseInt(j2);


      ChartItemInterface consequence;
      if (pRule instanceof  PcfgProductionRule) {
        if (nt1.equals(pRule.getRhs()[0]) && nt2.equals(pRule.getRhs()[1])
            && j1Int == i2Int) {
          Double x1 = ((ProbabilisticChartItemInterface) antecedences.get(0))
              .getProbability();
          Double x2 = ((ProbabilisticChartItemInterface) antecedences.get(1))
              .getProbability();
          consequence =
              new PcfgCykItem(x1 + x2
                  + -Math.log(((PcfgProductionRule) pRule).getP()), pRule.getLhs(),
                  i1Int, j2Int);
          addTreesToConsequence(i1, consequence);
          logItemGeneration(consequence);
          consequences.add(consequence);
        }
      } else {
        if (nt1.equals(pRule.getRhs()[0]) && nt2.equals(pRule.getRhs()[1])
            && i1Int + j1Int == i2Int) {
          consequence =
              new DeductionChartItem(pRule.getLhs(), String.valueOf(i1Int),
                  String.valueOf(j1Int + j2Int));
          addTreesToConsequence(i1, consequence);
          logItemGeneration(consequence);
          consequences.add(consequence);
        }
      }
  }

  protected void addTreesToConsequence(String i1, ChartItemInterface consequence) {
    CfgProductionRule rule =
        new CfgProductionRule(pRule.getLhs(), pRule.getRhs());
    List<Tree> derivedTrees =
        CfgCykUtils.generateDerivedTrees(i1, this.getAntecedences(), rule);
    consequence.setTrees(derivedTrees);
  }

  @Override public String toString() {
    if (pRule instanceof PcfgProductionRule) {
      return "x1 : [" + pRule.getRhs()[0] + ", i, j], x2 : [" + pRule.getRhs()[1]
          + ", j, k]" + "\n______ \n" + "x1 + x2 + |log(" + ((PcfgProductionRule) pRule).getP()
          + ")| : [" + pRule.getLhs() + ", i, k]";
    } else {
      return "[" + pRule.getRhs()[0] + ",i,l1], [" + pRule.getRhs()[1] + ",i+l1,l2]"
          + "\n______ \n" + "[" + pRule.getLhs() + ",i,l1+l2]";
    }
  }

  protected void logItemGeneration(ChartItemInterface item) {
    if (log.isDebugEnabled()) {
      StringBuilder out = new StringBuilder("generated: ");
      out.append(item).append(" with trees:");
      for (Tree tree : item.getTrees()) {
        out.append(' ').append(tree);
      }
      out.append(" from:");
      for (ChartItemInterface antecedence : antecedences) {
        out.append(' ').append(antecedence);
      }
      out.append(" with rule ").append(name);
      log.debug(out.toString());
    }
  }

}
