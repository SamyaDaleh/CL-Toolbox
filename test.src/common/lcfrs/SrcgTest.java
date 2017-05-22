package common.lcfrs;

import java.text.ParseException;

public class SrcgTest {
  public static void main(String[] args) throws ParseException {
  Srcg srcg1 = new Srcg();
  srcg1.setNonterminals(new String[] {"S", "A"});
  srcg1.setTerminals(new String[] {"a"});
  srcg1.setVariables(new String[] {"X1", "X2"});
  srcg1.setStartSymbol("S");
  srcg1.addClause("S (X1 X2)", "A(X2, X1)");
  srcg1.addClause("A (a X1, b X2)", "A(X1, X2)");
  srcg1.addClause("A (ε,ε)", "ε");
  
  if(srcg1.isOrdered()){
    System.out.println("Wrong, it's not ordered.");
  } else {
    System.out.println("Correct, it's not ordered.");
  }
  if(srcg1.hasEpsilonProductions()){
    System.out.println("Correct, has empty productions.");
  } else {
    System.out.println("Wrong, contains empty productions.");
  }
  }

}
