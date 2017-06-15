package cli;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import cli.Main;

public class MainTest {

  @Test public void testEmptyCall() throws ParseException, IOException {
    Main.main(new String[] {});
    Main.main(new String[] {".\\resources\\grammars\\anbn.cfg", "a a b b",
      "itssupposedtobenothing"});
  }

  @Test public void testCfgCalls() throws ParseException, IOException {
    callWithGrammar(".\\resources\\grammars\\anbn.cfg", "a a b b");
  }

  @Test public void testPcfgCalls() throws ParseException, IOException {
    callWithGrammar(".\\resources\\grammars\\a0n.pcfg", "1 0 0");
  }

  @Test public void testTagCalls() throws ParseException, IOException {
    callWithGrammar(".\\resources\\grammars\\anbncndn.tag", "a a b b c c d d");
  }

  @Test public void testSrcgCalls() throws ParseException, IOException {
    callWithGrammar(".\\resources\\grammars\\anbmcndm.srcg", "a a b c c d");
  }

  private void callWithGrammar(String grammarfile, String w)
    throws ParseException, IOException {
    String[] algorithms = new String[] {"cfg-neverheardofthis", "cfg-topdown",
      "cfg-shiftreduce", "cfg-earley", "cfg-leftcorner", "cfg-leftcorner-chart",
      "cfg-cyk", "cfg-cyk-extended", "cfg-unger", "pcfg-astar", "tag-earley", "tag-cyk",
      "srcg-cyk", "srcg-cyk-extended", "srcg-earley"};
    for (String algorithm : algorithms) {
      Main.main(new String[] {grammarfile, w, algorithm});
      Main.main(new String[] {grammarfile, w, algorithm, "--success"});
      Main.main(new String[] {grammarfile, w, algorithm, "--please"});
    }
  }
}
