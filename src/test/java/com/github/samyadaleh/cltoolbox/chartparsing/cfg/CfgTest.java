package com.github.samyadaleh.cltoolbox.chartparsing.cfg;

import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.ParseException;

import static org.junit.Assert.*;

public class CfgTest {

  @Test public void testConvertToCnf() throws ParseException {
    String grammar = "N = {N0}\n" + "T = {t0, t1, t2}\n" + "S = N0\n"
        + "P = {N0 -> ε, N0 -> t2 N0 N0, N0 -> t0 t2 t2, N0 -> t2 N0, N0 -> t0 N0, N0 -> t0 N0 t1}\n";
    Cfg cfg = new Cfg(new BufferedReader(new StringReader(grammar)));
    assertFalse(cfg.isInChomskyNormalForm());
    cfg = cfg.getCfgInChomskyNormalForm();
    assertTrue(cfg.isInChomskyNormalForm());
  }

  @Test public void testConvertFromCnfToGnf() throws ParseException {
    String grammar = "N = {S, A, B, X}\n" + "T = {a, b}\n" + "S = S\n"
        + "P = {S -> A B, S -> A X, X -> S B, A -> a, B -> b}\n";
    Cfg cfg = new Cfg(new BufferedReader(new StringReader(grammar)));
    assertFalse(cfg.isInGreibachNormalForm());
    // Step 2: Remove Left Recursion
    cfg = cfg.getCfgWithoutLeftRecursion();
    // Step 3: Convert every Production to GNF by expanding the first
    // Nonterminal to all possible terminals.
    cfg = cfg.getCfgWithProductionsInGnf();
    assertTrue(cfg.isInGreibachNormalForm());
    String goal = "G = <N, T, S, P>\n" + "N = {S, A, B, X}\n" + "T = {a, b}\n"
        + "S = S\n"
        + "P = {S -> a B, S -> a X, X -> a B B, X -> a X B, A -> a, B -> b}\n";
    assertEquals(goal, cfg.toString());
    System.out.println(cfg);
  }

  @Test public void testConvertToGnf() throws ParseException {
    String grammar = "G = <N, T, S, P>\n" + "N = {S}\n" + "T = {a, b}\n"
        + "S = S\n" + "P = {S -> a S b, S -> a b}\n";
    Cfg cfg = new Cfg(new BufferedReader(new StringReader(grammar)));
    assertFalse(cfg.isInGreibachNormalForm());
    // Step 1 Convert to CNF
    cfg = cfg.getCfgInChomskyNormalForm();
    /*
    cfg = cfg.getCfgWithoutEmptyProductions()
        .getCfgWithoutNonGeneratingSymbols()
        .getCfgWithoutNonReachableSymbols().getBinarizedCfg()
        .getCfgWithEitherOneTerminalOrNonterminalsOnRhs()
        .getCfgWithoutLoops(); //*/
    // Step 2: Remove Left Recursion
    cfg = cfg.getCfgWithoutLeftRecursion().getCfgWithoutEmptyProductions();
    // Step 3: Convert every Production to GNF by expanding the first
    // Nonterminal to all possible terminals.
    cfg = cfg.getCfgWithProductionsInGnf();
    assertTrue(cfg.isInGreibachNormalForm());
    System.out.println(cfg);
  }
}
