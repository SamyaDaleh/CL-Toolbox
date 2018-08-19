package com.github.samyadaleh.cltoolbox.cli;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.github.samyadaleh.cltoolbox.cli.Main;

@RunWith(Parameterized.class) public class MainTest {

  private final String algorithm;

  @Parameters public static Collection<String[]> browsers() {
    return Arrays.asList(
        new String[][] {{"cfg-neverheardofthis"}, {"cfg-topdown"},
            {"cfg-shiftreduce"}, {"cfg-lr-0"}, {"cfg-lr-1"}, {"cfg-earley"},
            {"cfg-passive"}, {"cfg-leftcorner"}, {"cfg-leftcorner-chart"},
            {"cfg-cyk"}, {"cfg-cyk-extended"}, {"cfg-cyk-general"},
            {"cfg-unger"}, {"pcfg-astar"}, {"pcfg-cyk"}, {"tag-earley"},
            {"tag-cyk-extended"}, {"tag-cyk-general"},
            {"tag-earley-prefixvalid"}, {"srcg-cyk-extended"},
            {"srcg-cyk-general"}, {"srcg-earley"}});
  }

  public MainTest(String algorithm) {
    this.algorithm = algorithm;
  }

  @Test public void testEmptyCall() throws Exception {
    try {
      Main.main(new String[] {});
      Main.main(new String[] {".\\resources\\grammars\\anbn.cfg", "a a b b",
          "itssupposedtobenothing"});
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test public void testCfgCalls() throws Exception {
    try {
      callWithGrammar(".\\resources\\grammars\\anbn.cfg", "a a b b");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test public void testPcfgCalls() throws Exception {
    try {
      callWithGrammar(".\\resources\\grammars\\a0n.pcfg", "1 0 0");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test public void testTagCalls() throws Exception {
    try {
      callWithGrammar(".\\resources\\grammars\\anbncndn.tag",
          "a a b b c c d d");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test public void testSrcgCalls() throws Exception {
    try {
      callWithGrammar(".\\resources\\grammars\\anbmcndm.srcg", "a a b c c d");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }

  private void callWithGrammar(String grammarfile, String w) throws Exception {
    Main.main(new String[] {grammarfile, w, algorithm, "--please"});
    Main.main(new String[] {grammarfile, w, algorithm, "--javafx"});
  }
}
