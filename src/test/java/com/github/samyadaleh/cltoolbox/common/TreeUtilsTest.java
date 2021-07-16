package com.github.samyadaleh.cltoolbox.common;

import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TreeUtilsTest {

  @Test public void testAddsNothing() throws ParseException {
    Tree tree1 = new Tree("(N1 (S )(S ))");
    Tree tree2 =
        new Tree("(S (N1 (S (N1 (S (ε ))(S (t2 )))(S (ε )))(S (ε )))(S (ε )))");
    assertTrue(TreeUtils.addsNothing(tree1, tree2, new String[] {"S", "N1"}));
    assertFalse(TreeUtils.addsNothing(tree2, tree1, new String[] {"S", "N1"}));
  }
}
