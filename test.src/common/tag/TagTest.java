package common.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import common.TestGrammarLibrary;

public class TagTest {


  @Test public void testBinarization() throws ParseException {
    assertTrue(!TestGrammarLibrary.binarizeTag().isBinarized());
    Tag binarizedTag = TestGrammarLibrary.binarizeTag().getBinarizedTag();
    assertTrue(binarizedTag.isBinarized());
    assertEquals("(S_NA (a )(X2 (S (b )(X1 (S* )(c )))(d )))",
      binarizedTag.getAuxiliaryTree("β1").toString());
    assertEquals("(S (ε ))",
      binarizedTag.getInitialTree("α").toString());
  }
}
