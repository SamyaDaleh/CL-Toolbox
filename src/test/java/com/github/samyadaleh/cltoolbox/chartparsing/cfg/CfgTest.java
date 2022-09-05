package com.github.samyadaleh.cltoolbox.chartparsing.cfg;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.ParseException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CfgTest {

  @Test public void testConvertToCnf() throws ParseException {
    String grammar = "G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a, b}\n"
        + "S = S\n" + "P = {S -> a S b, S -> a b}\n";
    Cfg cfg = new Cfg(new BufferedReader(new StringReader(grammar)));
    assertFalse(cfg.isInChomskyNormalForm());
    cfg = cfg.getCfgWithoutEmptyProductions()
        .getCfgWithoutNonGeneratingSymbols()
        .getCfgWithoutNonReachableSymbols().getBinarizedCfg()
        .getCfgWithEitherOneTerminalOrNonterminalsOnRhs()
        .getCfgWithoutLoops();
    assertTrue(cfg.isInChomskyNormalForm());
  }

  @Test public void testConvertToGnf() throws ParseException {
    String grammar = "G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a, b}\n"
        + "S = S\n" + "P = {S -> a S b, S -> a b}\n";
    Cfg cfg = new Cfg(new BufferedReader(new StringReader(grammar)));
    assertFalse(cfg.isInGreibachNormalForm());
    // Step 1 Convert to CNF
    cfg = cfg.getCfgWithoutEmptyProductions()
        .getCfgWithoutNonGeneratingSymbols()
        .getCfgWithoutNonReachableSymbols().getBinarizedCfg()
        .getCfgWithEitherOneTerminalOrNonterminalsOnRhs()
        .getCfgWithoutLoops();
    // Step 2: Remove Left Recursion
    cfg = cfg.getCfgWithoutLeftRecursion();
    // Step 3: Convert every Production to GNF by expanding the first
    // Nonterminal to all possible terminals.
    cfg = cfg.getCfgWithProductionsInGnf();
    assertTrue(cfg.isInGreibachNormalForm());
    System.out.println(cfg);
  }
}
