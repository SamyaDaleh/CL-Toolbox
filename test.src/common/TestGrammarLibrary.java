package common;

import java.text.ParseException;

import common.cfg.Cfg;
import common.cfg.Pcfg;
import common.lcfrs.Srcg;
import common.tag.Tag;

public class TestGrammarLibrary {

  public static Cfg anBnCfg() {
    Cfg cfg = new Cfg();

    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S"});
    cfg.addProductionRule("S -> a S b");
    cfg.addProductionRule("S -> a b");
    cfg.setStartSymbol("S");

    return cfg;
  }

  public static Cfg wwRCfg() {
    Cfg cfg = new Cfg();

    cfg.setTerminals(new String[] {"a", "b", "c"});
    cfg.setNonterminals(new String[] {"S"});
    cfg.addProductionRule("S -> a S a");
    cfg.addProductionRule("S -> b S b");
    cfg.addProductionRule("S -> c");
    cfg.setStartSymbol("S");

    return cfg;
  }

  public static Tag anCBTag() throws ParseException {
    Tag g = new Tag();
    g.setNonterminals(new String[] {"S", "T"});
    g.setTerminals(new String[] {"a", "b", "c"});
    g.setStartsymbol("S");
    g.addInitialTree("α1", "(S T b)");
    g.addInitialTree("α2", "(T c)");
    g.addAuxiliaryTree("β", "(T a T*)");
    return g;
  }

  public static Srcg anBnSrcg() throws ParseException {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[] {"S", "A"});
    srcg.setTerminals(new String[] {"a", "b"});
    srcg.setVariables(new String[] {"X1", "X2"});
    srcg.setStartSymbol("S");
    srcg.addClause("S (X1 X2) -> A(X1, X2)");
    srcg.addClause("A (a X1, b X2) -> A(X1, X2)");
    srcg.addClause("A (a,b) -> ε");
    return srcg;
  }

  public static Cfg longRhsCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S"});
    cfg.addProductionRule("S -> a S b S S a S b a b");
    cfg.addProductionRule("S -> a b");
    cfg.setStartSymbol("S");
    return cfg;
  }

  public static Cfg epsCfg() {
    Cfg cfgeps = new Cfg();
    cfgeps.setTerminals(new String[] {"a", "b"});
    cfgeps.setNonterminals(new String[] {"S", "A", "B", "C"});
    cfgeps.addProductionRule("A -> ε");
    cfgeps.addProductionRule("S -> ");
    cfgeps.addProductionRule("C -> ");
    cfgeps.addProductionRule("S -> b A a S b C");
    cfgeps.addProductionRule("A -> a");
    cfgeps.addProductionRule("A -> b B");
    cfgeps.addProductionRule("B -> b");
    cfgeps.setStartSymbol("S");
    return cfgeps;
  }

  public static Cfg eftCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "0", "1", "(", ")", "*", "+"});
    cfg.setNonterminals(new String[] {"I", "F", "T", "E"});
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
  }

  public static Cfg leftRecursionCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b", "c", "d"});
    cfg.setNonterminals(new String[] {"S"});
    cfg.addProductionRule("S -> S");
    cfg.addProductionRule("S -> S a");
    cfg.addProductionRule("S -> S b");
    cfg.addProductionRule("S -> c");
    cfg.addProductionRule("S -> d");
    cfg.setStartSymbol("S");
    return cfg;
  }

  public static Cfg nonReachableSymbolsCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a"});
    cfg.setNonterminals(new String[] {"S", "G"});
    cfg.setStartSymbol("S");
    cfg.addProductionRule("S -> a");
    cfg.addProductionRule("G -> b");
    return cfg;
  }

  public static Cfg nonGeneratingSymbolsCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a"});
    cfg.setNonterminals(new String[] {"S", "G"});
    cfg.setStartSymbol("S");
    cfg.addProductionRule("S -> a");
    cfg.addProductionRule("S -> G");
    cfg.addProductionRule("G -> G b");
    return cfg;
  }

  public static Pcfg banPcfg() {
    Pcfg pcfg = new Pcfg();
    pcfg.setTerminals(new String[] {"a", "b"});
    pcfg.setNonterminals(new String[] {"S", "A", "B"});
    pcfg
      .setProductionRules(new String[][] {{"S", "A B", "1"}, {"A", "b", "0.7"},
        {"A", "a", "0.3"}, {"B", "B B", "0.6"}, {"B", "a", "0.4"}});
    pcfg.setStartSymbol("S");
    return pcfg;
  }

  public static Cfg anbnCnfCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S", "A", "B", "X1"});
    cfg.addProductionRule("S -> A X1");
    cfg.addProductionRule("S -> A B");
    cfg.addProductionRule("A -> a");
    cfg.addProductionRule("B -> b");
    cfg.addProductionRule("X1 -> S B");
    cfg.setStartSymbol("S");
    return cfg;
  }

  public static Cfg anbnC2fCfg() {
    Cfg cfg = new Cfg();
    cfg.setTerminals(new String[] {"a", "b"});
    cfg.setNonterminals(new String[] {"S", "A", "B", "C", "X1"});
    cfg.addProductionRule("S -> A X1");
    cfg.addProductionRule("S -> A B");
    cfg.addProductionRule("C -> a");
    cfg.addProductionRule("B -> b");
    cfg.addProductionRule("X1 -> S B");
    cfg.addProductionRule("A -> C");
    cfg.setStartSymbol("S");
    return cfg;
  }

  public static Pcfg niceUglyCarPcfg() {
    Pcfg pcfg = new Pcfg();
    pcfg.setNonterminals(new String[] {"N", "A"});
    pcfg.setTerminals(new String[] {"camping", "car", "nice", "red", "ugly",
      "green", "house", "bike"});
    pcfg.setStartSymbol("N");
    pcfg.setProductionRules(new String[][] {{"N", "N N", "0.1"},
      {"N", "red", "0.1"}, {"N", "car", "0.1"}, {"N", "camping", "0.2"},
      {"A", "nice", "0.3"}, {"A", "red", "0.2"}, {"N", "A N", "0.2"},
      {"N", "green", "0.1"}, {"N", "bike", "0.1"}, {"N", "house", "0.1"},
      {"A", "ugly", "0.25"}, {"A", "green", "0.25"}});
    return pcfg;
  }

  public static Srcg longStringsSrcg() throws ParseException {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[] {"S", "A", "B", "C"});
    srcg.setTerminals(new String[] {"a", "b", "c"});
    srcg.setVariables(new String[] {"U", "V", "W", "X", "Y", "Z"});
    srcg.setStartSymbol("S");
    srcg.addClause("S(V Y W Z X ) -> A(V,W,X) B(Y,Z)");
    srcg.addClause("A(a,a,a) -> ε");
    srcg.addClause("A(X U, Y V, Z W) -> A(X,Y,Z) C(U,V,W)");
    srcg.addClause("B(b,b) -> ε");
    srcg.addClause("B(X V,W Y) -> B(X,Y) B(V,W)");
    srcg.addClause("C(a,c,c) -> ε");
    return srcg;
  }

  public static Srcg anbnUnorderedEpsSrcg() throws ParseException {
    Srcg srcg1 = new Srcg();
    srcg1.setNonterminals(new String[] {"S", "A"});
    srcg1.setTerminals(new String[] {"a"});
    srcg1.setVariables(new String[] {"X1", "X2"});
    srcg1.setStartSymbol("S");
    srcg1.addClause("S (X1 X2) -> A(X2, X1)");
    srcg1.addClause("A (a X1, b X2) -> A(X1, X2)");
    srcg1.addClause("A (ε,ε) -> ε");
    return srcg1;
  }

  public static Cfg anbnCnfProbCfg() {
    Cfg cfgeps = new Cfg();
    cfgeps.setTerminals(new String[] {"a", "b"});
    cfgeps.setNonterminals(new String[] {"S", "X1", "Y1", "Y2"});
    cfgeps.addProductionRule("Y1 -> a");
    cfgeps.addProductionRule("S -> Y1 X1");
    cfgeps.addProductionRule("Y2 -> b");
    cfgeps.addProductionRule("X1 -> S Y2");
    cfgeps.addProductionRule("S -> Y1 Y2");
    cfgeps.setStartSymbol("S");
    return cfgeps;
  }
  
  public static Srcg unorderedSrcg() throws ParseException {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[]{"S", "A"});
    srcg.setTerminals(new String[]{"a", "b"});
    srcg.setVariables(new String[]{"X", "Y"});
    srcg.addClause("S(X Y) -> A(X,Y)");
    srcg.addClause("A(X, Y) -> A(Y,X)");
    srcg.addClause("A(a X,b Y) -> A(X,Y)");
    srcg.addClause("A(a,b) -> ε");
    srcg.setStartSymbol("S");
    return srcg;
  }
  
  public static Srcg withEmptyProductionsSrcg() throws ParseException {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[]{"S", "A"});
    srcg.setTerminals(new String[]{"a", "b"});
    srcg.setVariables(new String[]{"X", "Y"});
    srcg.addClause("S(X Y) -> A(X,Y)");
    srcg.addClause("A(a, ε) -> ε");
    srcg.addClause("A(ε, b) -> ε");
    srcg.addClause("A(a, b) -> ε");
    srcg.setStartSymbol("S");
    return srcg;
  }
  
  public static Srcg testBinarizationSrcg() throws ParseException {
    Srcg srcg = new Srcg();
    srcg.setNonterminals(new String[]{"S", "A", "B", "C"});
    srcg.setTerminals(new String[]{"a", "b", "c"});
    srcg.setVariables(new String[]{"X", "Y", "Z", "U", "V", "W"});
    srcg.addClause("S(X Y Z U V W) -> A(X,U) B(Y,V) C(Z,W)");
    srcg.addClause("A(a X, a Y) -> A(X, Y)");
    srcg.addClause("B(b X, b Y) -> B(X, Y)");
    srcg.addClause("C(c X, c Y) -> C(X, Y)");
    srcg.addClause("A(a, a) -> ε");
    srcg.addClause("B(b, b) -> ε");
    srcg.addClause("C(c, c) -> ε");
    srcg.setStartSymbol("S");
    return srcg;
  }
}
