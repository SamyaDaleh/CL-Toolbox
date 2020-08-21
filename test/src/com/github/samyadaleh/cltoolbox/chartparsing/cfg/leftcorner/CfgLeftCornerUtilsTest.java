package com.github.samyadaleh.cltoolbox.chartparsing.cfg.leftcorner;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class CfgLeftCornerUtilsTest {

  @Test public void testGenerateDerivedChartTrees() throws ParseException {
    List<ChartItemInterface> antecedences = new ArrayList<>();
    antecedences.add(new DeductionChartItem("N1", "0", "1"));
    antecedences.get(0).getTrees()
        .add(new Tree("(N1 (t0 )(N0 (ε ))(N0 (ε )))"));
    antecedences.get(0).getTrees()
        .add(new Tree("(N1 (t0 )(N0 (N1 (ε ))(N1 (ε )))(N0 (ε )))"));
    antecedences.get(0).getTrees()
        .add(new Tree("(N1 (t0 )(N0 (ε ))(N0 (N1 (ε ))(N1 (ε ))))"));
    antecedences.get(0).getTrees().add(
        new Tree("(N1 (t0 )(N0 (N1 (ε ))(N1 (ε )))(N0 (N1 (ε ))(N1 (ε ))))"));
    DeductionChartItem consequence =
        new DeductionChartItem("N0 -> N1 •N1", "0", "1");
    CfgProductionRule rule = new CfgProductionRule("N0 -> N1 N1");
    CfgLeftCornerUtils.generateDerivedChartTrees(rule, antecedences, consequence);
    List<Tree> derivedTrees = consequence.getTrees();
    assertEquals(4, derivedTrees.size());
    assertEquals("(N0 (N1 (t0 )(N0 (ε ))(N0 (ε )))(N1 ))",
        derivedTrees.get(0).toString());
    assertEquals("(N0 (N1 (t0 )(N0 (N1 (ε ))(N1 (ε )))(N0 (ε )))(N1 ))",
        derivedTrees.get(1).toString());
    assertEquals("(N0 (N1 (t0 )(N0 (ε ))(N0 (N1 (ε ))(N1 (ε ))))(N1 ))",
        derivedTrees.get(2).toString());
    assertEquals(
        "(N0 (N1 (t0 )(N0 (N1 (ε ))(N1 (ε )))(N0 (N1 (ε ))(N1 (ε ))))(N1 ))",
        derivedTrees.get(3).toString());
  }

  @Test public void testGenerateDerivedTreesScan() throws ParseException {
    List<ChartItemInterface> antecedences = new ArrayList<>();
    antecedences.add(new DeductionChartItem("", "0", "0"));
    DeductionChartItem consequence =
        new DeductionChartItem("N0 ->  •", "0", "0");
    CfgProductionRule rule = new CfgProductionRule("N0 -> ");
    CfgLeftCornerUtils.generateDerivedChartTrees(rule, antecedences, consequence);
    List<Tree> derivedTrees = consequence.getTrees();
    assertEquals(1, derivedTrees.size());
    assertEquals("(N0 (ε ))",
        derivedTrees.get(0).toString());
  }
}
