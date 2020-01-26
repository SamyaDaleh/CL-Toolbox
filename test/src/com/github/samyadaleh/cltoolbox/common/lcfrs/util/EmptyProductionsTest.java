package com.github.samyadaleh.cltoolbox.common.lcfrs.util;

import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class EmptyProductionsTest {

  @Test public void testEveryArgumentBecomesEmpty() throws ParseException {
    Clause clause = new Clause("N0(t0 t1 X1 X2) -> N1(X1) N1(X2)");
    List<String[]> combinations = new ArrayList<>();
    combinations.add(new String[]{"N0", "0"});
    combinations.add(new String[]{"N0", "0"});
    // TODO for some reason the removal is only called for the first argument,
    //  need to rethink, reread literature
    Clause newClause = EmptyProductions.getClauseForEpsilonCombination(clause, combinations);
    assertEquals("N0(t0 t1) -> ε",newClause.toString());
  }

}
