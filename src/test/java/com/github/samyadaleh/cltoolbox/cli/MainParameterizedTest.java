package com.github.samyadaleh.cltoolbox.cli;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class) public class MainParameterizedTest {

  private final String algorithm;

  @Parameters public static Collection<String[]> browsers() {
    return Arrays.asList(
        new String[][] {{"cfg-neverheardofthis"}, {"cfg-topdown"},
            {"cfg-shiftreduce"}, {"cfg-lr-0"}, {"cfg-lr-1"}, {"cfg-earley"},
            {"cfg-earley-bottomup"}, {"cfg-earley-passive"}, {"cfg-leftcorner"},
            {"cfg-leftcorner-bottomup"}, {"cfg-leftcorner-chart"}, {"cfg-cyk"},
            {"cfg-cyk-extended"}, {"cfg-cyk-general"}, {"cfg-unger"},
            {"pcfg-astar"}, {"pcfg-cyk"}, {"tag-earley"}, {"tag-cyk-extended"},
            {"tag-cyk-general"}, {"tag-earley-prefixvalid"},
            {"srcg-cyk-extended"}, {"srcg-cyk-general"}, {"srcg-earley"}});
  }

  public MainParameterizedTest(String algorithm) {
    this.algorithm = algorithm;
  }

  @Test public void testCfgCalls() {
    try {
      callWithGrammar(".\\resources\\grammars\\anbn.cfg", "a a b b");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test public void testPcfgCalls() {
    try {
      callWithGrammar(".\\resources\\grammars\\a0n.pcfg", "1 0 0");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test public void testTagCalls() {
    try {
      callWithGrammar(".\\resources\\grammars\\anbncndn.tag",
          "a a b b c c d d");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test public void testSrcgCalls() {
    try {
      callWithGrammar(".\\resources\\grammars\\anbmcndm.srcg", "a a b c c d");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  private void callWithGrammar(String grammarfile, String w) {
    Main.main(new String[] {grammarfile, w, algorithm, "--please"});
  }
}
