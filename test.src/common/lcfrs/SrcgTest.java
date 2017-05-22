package common.lcfrs;

import java.text.ParseException;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SrcgTest {

  @Test public void testOrder() {
    Srcg srcg1 = new Srcg();
    srcg1.setNonterminals(new String[] {"S", "A"});
    srcg1.setTerminals(new String[] {"a"});
    srcg1.setVariables(new String[] {"X1", "X2"});
    srcg1.setStartSymbol("S");
    srcg1.addClause("S (X1 X2)", "A(X2, X1)");
    srcg1.addClause("A (a X1, b X2)", "A(X1, X2)");
    srcg1.addClause("A (ε,ε)", "ε");
    assertTrue(!srcg1.isOrdered());

  }

  @Test public void testEmptyProductions() {
    Srcg srcg1 = new Srcg();
    srcg1.setNonterminals(new String[] {"S", "A"});
    srcg1.setTerminals(new String[] {"a"});
    srcg1.setVariables(new String[] {"X1", "X2"});
    srcg1.setStartSymbol("S");
    srcg1.addClause("S (X1 X2)", "A(X2, X1)");
    srcg1.addClause("A (a X1, b X2)", "A(X1, X2)");
    srcg1.addClause("A (ε,ε)", "ε");
    assertTrue(srcg1.hasEpsilonProductions());

  }

}
