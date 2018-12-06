package com.github.samyadaleh.cltoolbox.common;

/**
 * Class holding constant strings used all around the toolbox.
 */
public class Constants {
  /* Static rules*/
  public final static String DEDUCTION_RULE_CCG_DEDUCTION_AXIOM = "axiom";
  public final static String DEDUCTION_RULE_CFG_CYK_AXIOM = "scan";
  public final static String DEDUCTION_RULE_CFG_CYK_AXIOM_EPSILON = "scan ε";
  public final static String DEDUCTION_RULE_CFG_EARLEY_AXIOM = "axiom";
  public final static String DEDUCTION_RULE_CFG_EARLEY_BOTTOMUP_AXIOM = "initialize";
  public final static String DEDUCTION_RULE_CFG_LEFTCORNER_AXIOM = "axiom";
  public final static String DEDUCTION_RULE_CFG_LEFTCORNER_CHART_AXIOM = "scan";
  public final static String DEDUCTION_RULE_CFG_LEFTCORNER_CHART_AXIOM_EPSILON = "scan-ε";
  public final static String DEDUCTION_RULE_CFG_LRK_AXIOM = "initialize";
  public final static String DEDUCTION_RULE_CFG_SHIFTREDUCE_AXIOM = "axiom";
  public final static String DEDUCTION_RULE_CFG_TOPDOWN_AXIOM = "axiom";
  public final static String DEDUCTION_RULE_CFG_UNGER_AXIOM = "axiom";
  public final static String DEDUCTION_RULE_LCFRS_CYK_AXIOM = "scan";
  public final static String DEDUCTION_RULE_LCFRS_EARLEY_AXIOM = "initialize";
  public final static String DEDUCTION_RULE_PCFG_CYK_AXIOM = "scan";
  public final static String DEDUCTION_RULE_TAG_CYK_AXIOM_FOOTPREDICT = "foot-predict";
  public final static String DEDUCTION_RULE_TAG_CYK_AXIOM_LEXSCAN = "lex-scan";
  public final static String DEDUCTION_RULE_TAG_CYK_AXIOM_EPSSCAN = "eps-scan";
  public final static String DEDUCTION_RULE_TAG_EARLEY_AXIOM = "initialize";

  /* Dynamic rules*/
  public final static String DEDUCTION_RULE_CCG_DEDUCTION_BACKWARDAPPLICATION = "Backward Application";
  public final static String DEDUCTION_RULE_CCG_DEDUCTION_BACKWARDCOMPOSITION1 = "Backward Composition 1";
  public final static String DEDUCTION_RULE_CCG_DEDUCTION_BACKWARDCOMPOSITION2 = "Backward Composition 2";
  public final static String DEDUCTION_RULE_CCG_DEDUCTION_FORWARDAPPLICATION = "Forward Application";
  public final static String DEDUCTION_RULE_CCG_DEDUCTION_FORWARDCOMPOSITION1 = "Forward Composition 1";
  public final static String DEDUCTION_RULE_CCG_DEDUCTION_FORWARDCOMPOSITION2 = "Forward Composition 2";
  public final static String DEDUCTION_RULE_CFG_CYK_COMPLETE = "complete";
  public final static String DEDUCTION_RULE_CFG_EARLEY_COMPLETE = "complete";
  public final static String DEDUCTION_RULE_CFG_EARLEY_CONVERT = "convert";
  public final static String DEDUCTION_RULE_CFG_EARLEY_PREDICT = "predict";
  public final static String DEDUCTION_RULE_CFG_EARLEY_SCAN = "scan";
  public final static String DEDUCTION_RULE_CFG_LEFTCORNER_MOVE = "move";
  public final static String DEDUCTION_RULE_CFG_LEFTCORNER_REDUCE = "reduce";
  public final static String DEDUCTION_RULE_CFG_LEFTCORNER_REMOVE = "remove";
  public final static String DEDUCTION_RULE_CFG_SHIFTREDUCE_REDUCE = "reduce";
  public final static String DEDUCTION_RULE_CFG_SHIFTREDUCE_SHIFT = "shift";
  public final static String DEDUCTION_RULE_CFG_LRK_REDUCE = "reduce";
  public final static String DEDUCTION_RULE_CFG_LRK_SHIFT = "shift";
  public final static String DEDUCTION_RULE_CFG_TOPDOWN_PREDICT = "predict";
  public final static String DEDUCTION_RULE_CFG_TOPDOWN_SCAN = "scan";
  public final static String DEDUCTION_RULE_CFG_UNGER_COMPLETE = "complete";
  public final static String DEDUCTION_RULE_CFG_UNGER_PREDICT = "predict";
  public final static String DEDUCTION_RULE_CFG_UNGER_SCAN = "scan";
  public final static String DEDUCTION_RULE_PCFG_CYK_COMPLETE = "complete";
  public final static String DEDUCTION_RULE_LCFRS_CYK_COMPLETE_BINARY = "complete binary";
  public final static String DEDUCTION_RULE_LCFRS_CYK_COMPLETE_GENERAL = "complete general";
  public final static String DEDUCTION_RULE_LCFRS_CYK_COMPLETE_UNARY = "complete unary";
  public final static String DEDUCTION_RULE_LCFRS_EARLEY_COMPLETE = "complete";
  public final static String DEDUCTION_RULE_LCFRS_EARLEY_CONVERT = "convert";
  public final static String DEDUCTION_RULE_LCFRS_EARLEY_PREDICT = "predict";
  public final static String DEDUCTION_RULE_LCFRS_EARLEY_RESUME = "resume";
  public final static String DEDUCTION_RULE_LCFRS_EARLEY_SCAN = "scan";
  public final static String DEDUCTION_RULE_LCFRS_EARLEY_SUSPEND = "suspend";
  public final static String DEDUCTION_RULE_TAG_ADJOIN = "adjoin";
  public final static String DEDUCTION_RULE_TAG_MOVE_BINARY = "move-binary";
  public final static String DEDUCTION_RULE_TAG_MOVE_GENERAL = "move-general";
  public final static String DEDUCTION_RULE_TAG_MOVE_UNARY = "move-unary";
  public final static String DEDUCTION_RULE_TAG_NULLADJOIN = "null-adjoin";
  public final static String DEDUCTION_RULE_TAG_EARLEY_ADJOIN = "adjoin";
  public final static String DEDUCTION_RULE_TAG_EARLEY_COMPLETEFOOT = "complete foot";
  public final static String DEDUCTION_RULE_TAG_EARLEY_COMPLETENODE = "complete node";
  public final static String DEDUCTION_RULE_TAG_EARLEY_MOVE_DOWN = "move down";
  public final static String DEDUCTION_RULE_TAG_EARLEY_MOVE_RIGHT = "move right";
  public final static String DEDUCTION_RULE_TAG_EARLEY_MOVE_UP = "move up";
  public final static String DEDUCTION_RULE_TAG_EARLEY_PREDICTADJOINABLE = "predict adjoinable with";
  public final static String DEDUCTION_RULE_TAG_EARLEY_PREDICTADJOINED = "predict adjoined in";
  public final static String DEDUCTION_RULE_TAG_EARLEY_PREDICTNOADJOIN = "predict no adjoin";
  public final static String DEDUCTION_RULE_TAG_EARLEY_PREDICTSUBST = "predict substitution of";
  public final static String DEDUCTION_RULE_TAG_EARLEY_SCANEPS = "scan ε";
  public final static String DEDUCTION_RULE_TAG_EARLEY_SCANTERM = "scan";
  public final static String DEDUCTION_RULE_TAG_EARLEY_SUBSTITUTE = "substitute in";
  public final static String DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_CONVERTLA1 = "convert la1";
  public final static String DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_CONVERTLA2 = "convert la2";
  public final static String DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_CONVERTRB = "convert rb";
  public final static String DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTADJOINABLE = "predict adjoinable";
  public final static String DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTADJOINED = "predict adjoined";
  public final static String DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTNOADJOIN = "predict no adjoin";
  public final static String DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_PREDICTSUBSTITUTED = "predict substituted";
  public final static String DEDUCTION_RULE_TAG_EARLEY_PREFIXVALID_SUBSTITUTE = "substitute";

  /**/
  public final static String DEDUCTION_ACTION_CFG_LRK_SHIFT = "s";
  public final static String DEDUCTION_ACTION_CFG_LRK_REDUCE = "r";
  public final static String DEDUCTION_ACTION_CFG_LRK_ACCEPT = "acc";

}
