package com.github.samyadaleh.cltoolbox.cli;

import org.junit.Test;

public class MainTest {

  @Test public void testNonexistantAlgorithm() {
    try {
      Main.main(new String[] {".\\resources\\grammars\\anbn.cfg", "a a b b",
          "itssupposedtobenothing"});
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}
