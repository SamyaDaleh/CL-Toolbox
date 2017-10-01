package cli;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class) public class MainTest {

  private final String algorithm;

  @Parameters public static Collection<String[]> browsers() {
    return Arrays.asList(new String[][] {{"cfg-neverheardofthis"},
      {"cfg-topdown"}, {"cfg-shiftreduce"}, {"cfg-earley"}, {"cfg-leftcorner"},
      {"cfg-leftcorner-chart"}, {"cfg-cyk"}, {"cfg-cyk-extended"},
      {"cfg-cyk-general"}, {"cfg-unger"}, {"pcfg-astar"}, {"pcfg-cyk"},
      {"tag-earley"}, {"tag-cyk-extended"}, {"tag-earley-prefixvalid"},
      {"srcg-cyk-extended"}, {"srcg-cyk-general"}, {"srcg-earley"}});
  }

  public MainTest(String algorithm) {
    this.algorithm = algorithm;
  }

  @Test public void testEmptyCall() throws Exception {
    Main.main(new String[] {});
    Main.main(new String[] {".\\resources\\grammars\\anbn.cfg", "a a b b",
      "itssupposedtobenothing"});
  }

  @Test public void testCfgCalls() throws Exception {
    callWithGrammar(".\\resources\\grammars\\anbn.cfg", "a a b b");
  }

  @Test public void testPcfgCalls() throws Exception {
    callWithGrammar(".\\resources\\grammars\\a0n.pcfg", "1 0 0");
  }

  @Test public void testTagCalls() throws Exception {
    callWithGrammar(".\\resources\\grammars\\anbncndn.tag", "a a b b c c d d");
  }

  @Test public void testSrcgCalls() throws Exception {
    callWithGrammar(".\\resources\\grammars\\anbmcndm.srcg", "a a b c c d");
  }

  private void callWithGrammar(String grammarfile, String w) throws Exception {
    Main.main(new String[] {grammarfile, w, algorithm, "--please"});
    Main.main(new String[] {grammarfile, w, algorithm, "--javafx"});
  }
}
