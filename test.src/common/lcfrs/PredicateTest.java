package common.lcfrs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PredicateTest {
  @Test public void testGetAbsPos(){
    Predicate lhs = new Predicate("A ( a b c, d, e f )");
    assertEquals(0, lhs.getAbsolutePos(1, 0));
    assertEquals(1, lhs.getAbsolutePos(1, 1));
    assertEquals(2, lhs.getAbsolutePos(1, 2));
    assertEquals(3, lhs.getAbsolutePos(2, 0));
    assertEquals(4, lhs.getAbsolutePos(3, 0));
    assertEquals(5, lhs.getAbsolutePos(3, 1));
  }
}
