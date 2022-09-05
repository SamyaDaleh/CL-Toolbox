package com.github.samyadaleh.cltoolbox.common.tag;

import java.io.FileNotFoundException;
import java.text.ParseException;

import com.github.samyadaleh.cltoolbox.common.GrammarLoader;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import org.junit.Test;

import static org.junit.Assert.*;

public class TagTest {

  @Test public void testBinarization()
      throws ParseException, FileNotFoundException {
    Tag tag = GrammarLoader.readTag("binarize.tag");
    assertFalse(tag.isBinarized());
    Tag binarizedTag = tag.getBinarizedTag();
    assertTrue(binarizedTag.isBinarized());
    assertEquals("(S_NA (a )(X2 (S (b )(X1 (S* )(c )))(d )))",
      binarizedTag.getAuxiliaryTree("β1").toString());
    assertEquals("(S (ε ))",
      binarizedTag.getInitialTree("α").toString());
  }

  @Test public void testConvertEpsilonCfgToTag()
      throws ParseException, FileNotFoundException {
    Cfg cfg = GrammarLoader.readCfg("anbnepsilon.cfg");
    Tag tagfromCfg = new Tag(cfg);
    assertEquals("G = <N, T, I, A, S>\n" + "N = {S}\n" + "T = {a, b}\n"
        + "I = {α1 : (S (a )(S )(b )), α2 : (S (ε ))}\n" + "A = {}\n"
        + "S = S\n", tagfromCfg.toString());
  }
}
