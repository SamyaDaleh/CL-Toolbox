package com.github.samyadaleh.cltoolbox.common.tag;

import java.text.ParseException;

import org.junit.Test;

import com.github.samyadaleh.cltoolbox.common.TestGrammarLibrary;

import static org.junit.Assert.*;

public class TagTest {

  @Test public void testBinarization() throws ParseException {
    assertFalse(TestGrammarLibrary.binarizeTag().isBinarized());
    Tag binarizedTag = TestGrammarLibrary.binarizeTag().getBinarizedTag();
    assertTrue(binarizedTag.isBinarized());
    assertEquals("(S_NA (a )(X2 (S (b )(X1 (S* )(c )))(d )))",
      binarizedTag.getAuxiliaryTree("β1").toString());
    assertEquals("(S (ε ))",
      binarizedTag.getInitialTree("α").toString());
  }

  @Test public void testConvertEpsilonCfgToTag() throws ParseException {
    Tag tagfromCfg = new Tag(TestGrammarLibrary.anBnEpsilonCfg());
    assertEquals("G = <N, T, I, A, S>\n" + "N = {S}\n" + "T = {a, b}\n"
        + "I = {α1 : (S (a )(S )(b )), α2 : (S (ε ))}\n" + "A = {}\n"
        + "S = S\n", tagfromCfg.toString());
  }
}
