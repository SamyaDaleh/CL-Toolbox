package com.github.samyadaleh.cltoolbox.common;

import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import org.junit.Test;

import java.text.ParseException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class TreeUtilsTest {

  @Test public void testAddsNothing() throws ParseException {
    Tree tree1 = new Tree("(N1 (S )(S ))");
    Tree tree2 =
        new Tree("(S (N1 (S (N1 (S (ε ))(S (t2 )))(S (ε )))(S (ε )))(S (ε )))");
    assertTrue(TreeUtils.addsNothing(tree1, tree2, Arrays.asList("S", "N1")));
    assertFalse(TreeUtils.addsNothing(tree2, tree1, Arrays.asList("S", "N1")));
  }

  @Test public void testMergeTrees() throws ParseException {
    Tree tree1 = new Tree("(S (N1 (S (ε )))(N1 ))");
    Tree tree2 = new Tree("(S (N1 )(N1 (S (ε ))))");
    Tree treeMerged = TreeUtils.mergeTrees(tree1, tree2);
    assertEquals("(S (N1 (S (ε )))(N1 (S (ε ))))", treeMerged.toString());
  }

  @Test public void testMergeTrees2() throws ParseException {
    Tree tree1 = new Tree("(S (N1 (t0 )(S (ε )))(N1 ))");
    Tree tree2 = new Tree("(S (N1 )(N1 (S (ε ))))");
    Tree treeMerged = TreeUtils.mergeTrees(tree2, tree1);
    assertEquals("(S (N1 (t0 )(S (ε )))(N1 (S (ε ))))", treeMerged.toString());
  }
}
