package com.github.samyadaleh.cltoolbox.common;

import com.github.samyadaleh.cltoolbox.common.ccg.Ccg;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.Pcfg;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Srcg;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

public class TestGrammarLibrary {

  public static Cfg anBnCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S"});
    try {
      cfg.addProductionRule("S -> a S b");
      cfg.addProductionRule("S -> a b");
      cfg.setStartSymbol("S");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg earleyBottomUpMissingAxiomsCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"t0", "t1"});
    cfg.setNonterminals(new String[] {"N0"});
    try {
      cfg.addProductionRule("N0 -> ε");
      cfg.addProductionRule("N0 -> t0 N0 t1 N0 N0");
      cfg.setStartSymbol("N0");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg anBnEpsilonCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S"});
    try {
      cfg.addProductionRule("S -> a S b");
      cfg.addProductionRule("S -> ε");
      cfg.setStartSymbol("S");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg wwRCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "c"});
    cfg.setNonterminals(new String[] {"S"});
    try {
      cfg.addProductionRule("S -> a S a");
      cfg.addProductionRule("S -> b S b");
      cfg.addProductionRule("S -> c");
      cfg.setStartSymbol("S");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg lrCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"John", "the", "apple"});
    cfg.setNonterminals(new String[] {"NP", "N", "Det"});
    try {
      cfg.addProductionRule("NP -> Det N");
      cfg.addProductionRule("NP -> John");
      cfg.addProductionRule("Det -> the");
      cfg.addProductionRule("N -> apple");
      cfg.setStartSymbol("NP");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg diffTreeAmountCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S", "A", "B"});
    try {
      cfg.addProductionRule("S -> a b");
      cfg.addProductionRule("S -> a B");
      cfg.addProductionRule("S -> b A");
      cfg.addProductionRule("A -> a");
      cfg.addProductionRule("A -> a S");
      cfg.addProductionRule("A -> b A A");
      cfg.addProductionRule("B -> b");
      cfg.addProductionRule("B -> b S");
      cfg.addProductionRule("B -> a B B");
      cfg.setStartSymbol("S");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg longRhsCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S"});
    try {
      cfg.addProductionRule("S -> a S b S S a S b a b");
      cfg.addProductionRule("S -> a b");
      cfg.setStartSymbol("S");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg epsCfg() {
    Cfg cfgeps = new Cfg();
    cfgeps.setTerminals(new String[] {"a", "b"});
    cfgeps.setNonterminals(new String[] {"S", "A", "B", "C"});
    try {
      cfgeps.addProductionRule("A -> ε");
      cfgeps.addProductionRule("S -> ");
      cfgeps.addProductionRule("C -> ");
      cfgeps.addProductionRule("S -> b A a S b C");
      cfgeps.addProductionRule("A -> a");
      cfgeps.addProductionRule("A -> b B");
      cfgeps.addProductionRule("B -> b");
      cfgeps.setStartSymbol("S");
      return cfgeps;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg eftCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "0", "1", "(", ")", "*", "+"});
    cfg.setNonterminals(new String[] {"I", "F", "T", "E"});
    try {
      cfg.addProductionRule("I -> a");
      cfg.addProductionRule("I -> b");
      cfg.addProductionRule("I -> I a");
      cfg.addProductionRule("I -> I b");
      cfg.addProductionRule("I -> I 0");
      cfg.addProductionRule("I -> I 1");
      cfg.addProductionRule("F -> I");
      cfg.addProductionRule("F -> ( E )");
      cfg.addProductionRule("T -> F");
      cfg.addProductionRule("T -> T * F");
      cfg.addProductionRule("E -> T");
      cfg.addProductionRule("E -> E + T");
      cfg.setStartSymbol("E");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg directLeftRecursionCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "c", "d"});
    cfg.setNonterminals(new String[] {"S"});
    try {
      cfg.addProductionRule("S -> S");
      cfg.addProductionRule("S -> S a");
      cfg.addProductionRule("S -> S b");
      cfg.addProductionRule("S -> c");
      cfg.addProductionRule("S -> d");
      cfg.setStartSymbol("S");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg indirectLeftRecursionCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S", "A"});
    try {
      cfg.addProductionRule("S -> A a");
      cfg.addProductionRule("A -> S a");
      cfg.addProductionRule("S -> b");
      cfg.setStartSymbol("S");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg leftRecursionNoTerminationCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a"});
    cfg.setNonterminals(new String[] {"S", "A"});
    try {
      cfg.addProductionRule("S -> A");
      cfg.addProductionRule("A -> A a");
      cfg.setStartSymbol("S");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg earleyPassiveCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"t0", "t1"});
    cfg.setNonterminals(new String[] {"N1"});
    try {
      cfg.addProductionRule("N1 -> t0 t1");
      cfg.addProductionRule("N1 -> t0");
      cfg.setStartSymbol("N1");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg nonReachableSymbolsCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a"});
    cfg.setNonterminals(new String[] {"S", "G"});
    cfg.setStartSymbol("S");
    try {
      cfg.addProductionRule("S -> a");
      cfg.addProductionRule("G -> b");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg nonGeneratingSymbolsCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a"});
    cfg.setNonterminals(new String[] {"S", "G"});
    cfg.setStartSymbol("S");
    try {
      cfg.addProductionRule("S -> a");
      cfg.addProductionRule("S -> G");
      cfg.addProductionRule("G -> G b");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg nonGeneratingSymbolsEpsilonCfg() {
    Cfg cfg = new Cfg();
    cfg.setStartSymbol("S");
    cfg.setNonterminals(new String[] {"S", "A", "B"});
    cfg.setTerminals(new String[] {"a", "b", "c"});
    try {
      cfg.addProductionRule("S -> A B");
      cfg.addProductionRule("A -> ");
      cfg.addProductionRule("B -> ε");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg anbnCnfCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S", "A", "B", "X1"});
    try {
      cfg.addProductionRule("S -> A X1");
      cfg.addProductionRule("S -> A B");
      cfg.addProductionRule("A -> a");
      cfg.addProductionRule("B -> b");
      cfg.addProductionRule("X1 -> S B");
      cfg.setStartSymbol("S");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg anbnC2fCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S", "A", "B", "C", "X1"});
    try {
      cfg.addProductionRule("S -> A X1");
      cfg.addProductionRule("S -> A B");
      cfg.addProductionRule("C -> a");
      cfg.addProductionRule("B -> b");
      cfg.addProductionRule("X1 -> S B");
      cfg.addProductionRule("A -> C");
      cfg.setStartSymbol("S");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg anbnCnfProbCfg() {
    Cfg cfgeps = new Cfg();
    cfgeps.setTerminals(new String[] {"a", "b"});
    cfgeps.setNonterminals(new String[] {"S", "X1", "Y1", "Y2"});
    try {
      cfgeps.addProductionRule("Y1 -> a");
      cfgeps.addProductionRule("S -> Y1 X1");
      cfgeps.addProductionRule("Y2 -> b");
      cfgeps.addProductionRule("X1 -> S Y2");
      cfgeps.addProductionRule("S -> Y1 Y2");
      cfgeps.setStartSymbol("S");
      return cfgeps;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg noUsefulNonterminalCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S"});
    try {
      cfg.addProductionRule("S -> a S b");
      cfg.setStartSymbol("S");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg leftCornerBreak() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(
        new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i"});
    cfg.setNonterminals(
        new String[] {"S", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L"});
    try {
      cfg.addProductionRule("S -> A B C");
      cfg.addProductionRule("A -> D E F");
      cfg.addProductionRule("D -> a");
      cfg.addProductionRule("E -> b");
      cfg.addProductionRule("F -> c");
      cfg.addProductionRule("B -> G H I");
      cfg.addProductionRule("G -> d");
      cfg.addProductionRule("H -> e");
      cfg.addProductionRule("I -> f");
      cfg.addProductionRule("C -> J K L");
      cfg.addProductionRule("J -> g");
      cfg.addProductionRule("K -> h");
      cfg.addProductionRule("L -> i");
      cfg.setStartSymbol("S");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Cfg ungerWrongGoalTreesCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"t0", "t1"});
    cfg.setNonterminals(new String[] {"N1"});
    try {
      cfg.addProductionRule("N1 -> t0");
      cfg.addProductionRule("N1 -> t0 t1");
      cfg.addProductionRule("N1 -> ε");
      cfg.setStartSymbol("N1");
      return cfg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Pcfg niceUglyCarPcfg() {
    Pcfg pcfg = new Pcfg();
    pcfg.setNonterminals(new String[] {"N", "A"});
    pcfg.setTerminals(
        new String[] {"camping", "car", "nice", "red", "ugly", "green", "house",
            "bike"});
    pcfg.setStartSymbol("N");
    pcfg.setProductionRules(
        new String[][] {{"N", "N N", "0.1"}, {"N", "red", "0.1"},
            {"N", "car", "0.1"}, {"N", "camping", "0.2"}, {"A", "nice", "0.3"},
            {"A", "red", "0.2"}, {"N", "A N", "0.2"}, {"N", "green", "0.1"},
            {"N", "bike", "0.1"}, {"N", "house", "0.1"}, {"A", "ugly", "0.25"},
            {"A", "green", "0.25"}});
    return pcfg;
  }

  public static Pcfg banPcfg() {
    Pcfg pcfg = new Pcfg();
    pcfg.setTerminals(new String[] {"a", "b"});
    pcfg.setNonterminals(new String[] {"S", "A", "B"});
    pcfg.setProductionRules(
        new String[][] {{"S", "A B", "1"}, {"A", "b", "0.7"}, {"A", "a", "0.3"},
            {"B", "B B", "0.6"}, {"B", "a", "0.4"}});
    pcfg.setStartSymbol("S");
    return pcfg;
  }

  public static Tag anCBTag() {
    try {
      Tag g = new Tag();
      g.setNonterminals(new String[] {"S", "T"});
      g.setTerminals(new String[] {"a", "b", "c"});
      g.setStartSymbol("S");
      g.addInitialTree("α1", "(S T b)");
      g.addInitialTree("α2", "(T c)");
      g.addAuxiliaryTree("β", "(T a T*)");
      return g;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Tag binarizeTag() {
    try {
      Tag g = new Tag();
      g.setNonterminals(new String[] {"S"});
      g.setTerminals(new String[] {"a", "b", "c", "d"});
      g.setStartSymbol("S");
      g.addInitialTree("α", "(S ε)");
      g.addAuxiliaryTree("β", "(S_NA a (S b S* c) d)");
      return g;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Tag convertToLcfrsTag() {
    try {
      Tag g = new Tag();
      g.setNonterminals(new String[] {"S", "A", "D"});
      g.setTerminals(new String[] {"a", "b", "c", "d"});
      g.setStartSymbol("S");
      g.addInitialTree("α", "(S a (A b c) D)");
      g.addInitialTree("γ", "(D d)");
      g.addAuxiliaryTree("β", "(A a A* a)");
      return g;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Srcg anBnSrcg() {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[] {"S", "A"});
    srcg.setTerminals(new String[] {"a", "b"});
    srcg.setVariables(new String[] {"X1", "X2"});
    srcg.setStartSymbol("S");
    try {
      srcg.addClause("S (X1 X2) -> A(X1, X2)");
      srcg.addClause("A (a X1, b X2) -> A(X1, X2)");
      srcg.addClause("A (a,b) -> ε");

    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    return srcg;
  }

  public static Srcg longStringsSrcg() {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[] {"S", "A", "B", "C"});
    srcg.setTerminals(new String[] {"a", "b", "c"});
    srcg.setVariables(new String[] {"U", "V", "W", "X", "Y", "Z"});
    srcg.setStartSymbol("S");
    try {
      srcg.addClause("S(V Y W Z X ) -> A(V,W,X) B(Y,Z)");
      srcg.addClause("A(a,a,a) -> ε");
      srcg.addClause("A(X U, Y V, Z W) -> A(X,Y,Z) C(U,V,W)");
      srcg.addClause("B(b,b) -> ε");
      srcg.addClause("B(X V,W Y) -> B(X,Y) B(V,W)");
      srcg.addClause("C(a,c,c) -> ε");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    return srcg;
  }

  public static Srcg anbnUnorderedEpsSrcg() {
    Srcg srcg1 = new Srcg();
    srcg1.setNonterminals(new String[] {"S", "A"});
    srcg1.setTerminals(new String[] {"a"});
    srcg1.setVariables(new String[] {"X1", "X2"});
    srcg1.setStartSymbol("S");
    try {
      srcg1.addClause("S (X1 X2) -> A(X2, X1)");
      srcg1.addClause("A (a X1, b X2) -> A(X1, X2)");
      srcg1.addClause("A (ε,ε) -> ε");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    return srcg1;
  }

  public static Srcg unorderedSrcg() {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[] {"S", "A"});
    srcg.setTerminals(new String[] {"a", "b"});
    srcg.setVariables(new String[] {"X", "Y"});
    try {
      srcg.addClause("S(X Y) -> A(X,Y)");
      srcg.addClause("A(X, Y) -> A(Y,X)");
      srcg.addClause("A(a X,b Y) -> A(X,Y)");
      srcg.addClause("A(a,b) -> ε");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    srcg.setStartSymbol("S");
    return srcg;
  }

  public static Srcg withEmptyProductionsSrcg() {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[] {"S", "A"});
    srcg.setTerminals(new String[] {"a", "b"});
    srcg.setVariables(new String[] {"X", "Y"});
    try {
      srcg.addClause("S(X Y) -> A(X,Y)");
      srcg.addClause("A(a, ε) -> ε");
      srcg.addClause("A(ε, b) -> ε");
      srcg.addClause("A(a, b) -> ε");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    srcg.setStartSymbol("S");
    return srcg;
  }

  public static Srcg testBinarizationSrcg() {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[] {"S", "A", "B", "C"});
    srcg.setTerminals(new String[] {"a", "b", "c"});
    srcg.setVariables(new String[] {"X", "Y", "Z", "U", "V", "W"});
    try {
      srcg.addClause("S(X Y Z U V W) -> A(X,U) B(Y,V) C(Z,W)");
      srcg.addClause("A(a X, a Y) -> A(X, Y)");
      srcg.addClause("B(b X, b Y) -> B(X, Y)");
      srcg.addClause("C(c X, c Y) -> C(X, Y)");
      srcg.addClause("A(a, a) -> ε");
      srcg.addClause("B(b, b) -> ε");
      srcg.addClause("C(c, c) -> ε");

    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    srcg.setStartSymbol("S");
    return srcg;
  }

  public static Srcg testOptimalBinarizationSrcg() {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[] {"S", "A", "B", "C", "D"});
    srcg.setTerminals(new String[] {"a", "c", "d"});
    srcg.setVariables(new String[] {"X", "Y", "Z", "U"});
    try {
      srcg.addClause("S(X Y Z) -> A(X, Y, Z)");
      srcg.addClause("A(a X Y, c Z, d U) -> B(X) C(Y, Z) D(U)");
      srcg.addClause("B(a) -> ε");
      srcg.addClause("C(c, c) -> ε");
      srcg.addClause("D(d) -> ε");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    srcg.setStartSymbol("S");
    return srcg;
  }

  public static Srcg testOptimostBinarizationSrcg() {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[] {"S", "A", "B", "C", "D"});
    srcg.setTerminals(new String[] {"a", "b", "c", "d"});
    srcg.setVariables(new String[] {"X", "Y", "V", "U"});
    try {
      srcg.addClause("S(X Y) -> A(X, Y)");
      srcg.addClause("A(U V, X Y) -> A(U) B(V) C(X) D(Y)");
      srcg.addClause("A(a) -> ε");
      srcg.addClause("B(b) -> ε");
      srcg.addClause("C(c) -> ε");
      srcg.addClause("D(d) -> ε");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    srcg.setStartSymbol("S");
    return srcg;
  }

  public static Srcg testSrcgWUselessRules() {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[] {"S", "A", "B", "C"});
    srcg.setTerminals(new String[] {"a", "b", "c"});
    srcg.setVariables(new String[] {"X", "Y", "Z", "U", "V", "W"});
    try {
      srcg.addClause("S(X Y Z U V W) -> A(X,U)");
      srcg.addClause("A(a X, a Y) -> A(X, Y)");
      srcg.addClause("A(b X, b Y) -> B(X, Y)");
      srcg.addClause("A(a, a) -> ε");
      srcg.addClause("C(c, c) -> ε");

    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    srcg.setStartSymbol("S");
    return srcg;
  }

  public static Srcg anbmcndmSrcg() {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[] {"S", "A", "B"});
    srcg.setTerminals(new String[] {"a", "b", "c", "d"});
    srcg.setVariables(new String[] {"X", "Y", "V", "W"});
    try {
      srcg.addClause("S( X V Y W ) -> A(X,Y) B(V,W)");
      srcg.addClause("B(ε,ε) -> ε");
      srcg.addClause("B(b X,d Y) -> B(X,Y)");
      srcg.addClause("A(a,c) -> ε");
      srcg.addClause("A(a X,c Y) -> A(X,Y)");
      srcg.setStartSymbol("S");
      return srcg;
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public static Ccg dedCcg() throws IOException {
    String ccgString = "Trip\tNP\n" + "merengue\tNP\n" + "likes\t(S\\NP)/NP\n"
        + "certainly\t(S\\NP)/(S\\NP)\n";
    return new Ccg(new BufferedReader(new StringReader(ccgString)));
  }
}
