package com.github.samyadaleh.cltoolbox.chartparsing;

import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class StaticDeductionRuleTest {

  @Test
  public void testAddConsequence() {
    StaticDeductionRule rule = new StaticDeductionRule();
    ChartItemInterface consequence = new DeductionChartItem("S", "0");
    rule.addConsequence(consequence);
    List<ChartItemInterface> consequences = rule.getConsequences();
    assertEquals(1, consequences.size());
    assertEquals(consequence, consequences.get(0));
  }

  @Test
  public void testGetAntecedences() {
    StaticDeductionRule rule = new StaticDeductionRule();
    List<ChartItemInterface> antecedences = new ArrayList<>();
    antecedences.add(new DeductionChartItem("S", "0"));
    rule.setAntecedences(antecedences);
    List<ChartItemInterface> retrievedAntecedences = rule.getAntecedences();
    assertEquals(antecedences, retrievedAntecedences);
  }

  @Test
  public void testGetName() {
    StaticDeductionRule rule = new StaticDeductionRule();
    String name = "Test Rule";
    rule.setName(name);
    assertEquals(name, rule.getName());
  }

  @Test
  public void testToString() {
    StaticDeductionRule rule = new StaticDeductionRule();
    ChartItemInterface antecedence1 = new DeductionChartItem("S", "0");
    ChartItemInterface antecedence2 = new DeductionChartItem("A", "1");
    ChartItemInterface consequence1 = new DeductionChartItem("B", "2");
    ChartItemInterface consequence2 = new DeductionChartItem("C", "3");
    rule.getAntecedences().add(antecedence1);
    rule.getAntecedences().add(antecedence2);
    rule.getConsequences().add(consequence1);
    rule.getConsequences().add(consequence2);
    String expectedRepresentation = antecedence1.toString() + antecedence2 +
        "\n______\n" + consequence1 + consequence2;
    assertEquals(expectedRepresentation, rule.toString());
  }
}
