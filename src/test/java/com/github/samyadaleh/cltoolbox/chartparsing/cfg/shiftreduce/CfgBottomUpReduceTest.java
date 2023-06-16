package com.github.samyadaleh.cltoolbox.chartparsing.cfg.shiftreduce;

import com.github.samyadaleh.cltoolbox.chartparsing.item.BottomUpChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.item.ChartItemInterface;
import com.github.samyadaleh.cltoolbox.chartparsing.item.Pair;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.junit.Test;

import java.text.ParseException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class CfgBottomUpReduceTest {

  @Test
  public void testTreeGeneration() throws ParseException {
    CfgBottomUpReduce rule = new CfgBottomUpReduce(new CfgProductionRule("S -> S N2"));
    BottomUpChartItem antecedence = new BottomUpChartItem("S1 S N2", "2");
    List<Pair<String, Map<Integer, List<Tree>>>> stackState = new ArrayList<>();
    // 0 = {Pair@3650} "N2 : {1=[(N2 (t0 ))]}"
    Map<Integer, List<Tree>> map1 = new HashMap<>();
    map1.put(1, Collections.singletonList(new Tree("(N2 (t0 ))")));
    Pair<String, Map<Integer, List<Tree>>> stack1 = new Pair<>("N2", map1);
    stackState.add(stack1);
    // 1 = {Pair@3651} "S : {1=[(S (t0 )), (S (N2 (t0 ))), (S (N1 (S (t0 )))), (S (N1 (S (N2 (t0 )))))]}"
    Map<Integer, List<Tree>> map2 = new HashMap<>();
    map2.put(1, Arrays.asList(new Tree("(S (t0 ))"),
        new Tree("(S (N2 (t0 )))"),
        new Tree("(S (N1 (S (t0 ))))"),
        new Tree("(S (N1 (S (N2 (t0 ))))")));
    Pair<String, Map<Integer, List<Tree>>> stack2 = new Pair<>("S", map2);
    stackState.add(stack2);
    // 2 = {Pair@3652} "S1 : {0=[(S1 (ε ))], 1=[(S1 (S (t0 ))), (S1 (S (N2 (t0 )))), (S1 (S (N1 (S (t0 ))))), (S1 (S (N1 (S (N2 (t0 ))))))]}"
    Map<Integer, List<Tree>> map3 = new HashMap<>();
    map3.put(0, Collections.singletonList(new Tree("(S1 (ε ))")));
    map3.put(1, Arrays.asList(new Tree("(S1 (S (t0 )))"),
        new Tree("(S1 (S (N2 (t0 )))"),
        new Tree("(S1 (S (N1 (S (t0 ))))"),
        new Tree("(S1 (S (N1 (S (N2 (t0 )))))")));
    Pair<String, Map<Integer, List<Tree>>> stack3 = new Pair<>("S1", map3);
    stackState.add(stack3);
    antecedence.setStackState(stackState);
    rule.clearItems();
    rule.setAntecedences(Collections.singletonList(antecedence));
    ChartItemInterface consequence = rule.getConsequences().get(0);
    assertEquals(9, consequence.getTrees().size());
  }

}
