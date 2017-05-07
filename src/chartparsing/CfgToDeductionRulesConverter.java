/* Based on the slides from Laura Kallmeyer about Parsing as Deduction. Top
 * Down: https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf Shift Reduce:
 * https://user.phil.hhu.de/~kallmeyer/Parsing/shift-reduce.pdf Earley:
 * https://user.phil.hhu.de/~kallmeyer/Parsing/earley.pdf Left corner:
 * https://user.phil.hhu.de/~kallmeyer/Parsing/left-corner.pdf */

package chartparsing;

import common.SetUtils;
import common.cfg.Cfg;
import common.cfg.CfgDollarItem;
import common.cfg.CfgDottedItem;
import common.cfg.CfgItem;
import common.cfg.CfgProductionRule;

public class CfgToDeductionRulesConverter {

  public static ParsingSchema CfgToParsingSchema(Cfg cfg, String w,
    String schema) {
    switch (schema) {
    case "topdown":
      return CfgToTopDownRules(cfg, w);
    case "shiftreduce":
      return CfgToShiftReduceRules(cfg, w);
    case "earley":
      return CfgToEarleyRules(cfg, w);
    case "leftcorner":
      return CfgToLeftCornerRules(cfg, w);
    default:
      return null;
    }
  }

  public static ParsingSchema CfgToTopDownRules(Cfg cfg, String w) {
    if (cfg.hasEpsilonProductions()) {
      System.out
        .println("CFG must not contain empty productions for TopDown parsing.");
      return null;
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    for (int i = 0; i < wsplit.length; i++) {
      for (String sequence : SetUtils.star(
        SetUtils.union(cfg.getTerminals(), cfg.getVars()), wsplit.length - i)) {
        String[] seqsplit = sequence.split(" ");
        if (seqsplit[0].equals(wsplit[i])) {
          DeductionRule scan = new DeductionRule();
          scan.addAntecedence(new CfgItem(sequence, i));
          scan.addConsequence(
            new CfgItem(getSubSequence(seqsplit, 1, seqsplit.length), i + 1));
          scan.setName("scan " + wsplit[i]);
          schema.addRule(scan);
          // System.out.println(scan.toString()); //DEBUG
        } else if (cfg.varsContain(seqsplit[0])) {
          for (CfgProductionRule rule : cfg.getR()) {
            if (rule.getLhs().equals(seqsplit[0])) {
              String[] gammaalpha = append(rule.getRhs(),
                getSubSequence(seqsplit, 1, wsplit.length - i).split(" "));
              if (gammaalpha.length <= wsplit.length - i) {
                DeductionRule predict = new DeductionRule();
                predict.addAntecedence(new CfgItem(sequence, i));
                predict
                  .addConsequence(new CfgItem(String.join(" ", gammaalpha), i));
                predict.setName("predict " + rule.getLhs() + " -> "
                  + String.join(" ", rule.getRhs()));
                schema.addRule(predict);
                // System.out.println(predict.toString()); //DEBUG
              }
            }
          }
        }
      }

    }
    DeductionRule axiom = new DeductionRule();
    axiom.addConsequence(new CfgItem(cfg.getStart_var(), 0));
    axiom.setName("axiom");
    schema.addRule(axiom);
    schema.addGoal(new CfgItem("", wsplit.length));
    return schema;
  }

  public static ParsingSchema CfgToShiftReduceRules(Cfg cfg, String w) {
    if (cfg.hasEpsilonProductions()) {
      System.out
        .println("CFG must not contain empty productions for ShiftReduce parsing.");
      return null;
    }
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    for (int i = 0; i <= wsplit.length; i++) {
      for (String sequence : SetUtils.star(
        SetUtils.union(cfg.getTerminals(), cfg.getVars()), wsplit.length)) {
        String[] seqsplit = sequence.split(" ");
        if (((i == 0 && sequence.length() == 0)
          || (sequence.length() > 0 && seqsplit.length <= i))
          && i < wsplit.length) {
          DeductionRule shift = new DeductionRule();
          shift.addAntecedence(new CfgItem(sequence, i));
          if (sequence.length() > 0) {
            shift
              .addConsequence(new CfgItem(sequence + " " + wsplit[i], i + 1));
          } else {
            shift.addConsequence(new CfgItem(wsplit[i], i + 1));
          }
          shift.setName("shift " + wsplit[i]);
          schema.addRule(shift);
          // System.out.println(shift.toString()); //DEBUG
        }
        if (seqsplit.length <= i) {
          for (CfgProductionRule rule : cfg.getR()) {
            String gamma = getStringHeadIfEndsWith(seqsplit, rule.getRhs());
            if (gamma != null) {
              DeductionRule reduce = new DeductionRule();
              reduce.addAntecedence(new CfgItem(sequence, i));
              if (gamma.length() > 0) {
                reduce
                  .addConsequence(new CfgItem(gamma + " " + rule.getLhs(), i));
              } else {
                reduce.addConsequence(new CfgItem(rule.getLhs(), i));
              }
              reduce.setName("reduce " + rule.getLhs() + " -> "
                + String.join(" ", rule.getRhs()));
              schema.addRule(reduce);
              // System.out.println(reduce.toString()); //DEBUG
            }
          }
        }
      }
    }

    DeductionRule axiom = new DeductionRule();
    axiom.addConsequence(new CfgItem("", 0));
    axiom.setName("axiom");
    schema.addRule(axiom);
    schema.addGoal(new CfgItem(cfg.getStart_var(), wsplit.length));
    return schema;
  }

  public static ParsingSchema CfgToEarleyRules(Cfg cfg, String w) {
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    for (CfgProductionRule rule : cfg.getR()) {
      if (rule.getLhs().equals(cfg.getStart_var())) {
        DeductionRule axiom = new DeductionRule();
        axiom.addConsequence(
          new CfgDottedItem("S -> •" + String.join(" ", rule.getRhs()), 0, 0));
        axiom.setName("axiom");
        schema.addRule(axiom);
        schema.addGoal(new CfgDottedItem(
          "S -> " + String.join(" ", rule.getRhs()) + " •", 0, wsplit.length));
      }
      if (w.length() > 0) {
        for (int i = 0; i <= wsplit.length; i++) {
          for (int j = i; j <= wsplit.length; j++) {
            for (int k = 0; k < rule.getRhs().length; k++) {
              if (j < wsplit.length && rule.getRhs()[k].equals(wsplit[j])) {
                DeductionRule scan = new DeductionRule();
                if (k == 0) {
                  scan.addAntecedence(new CfgDottedItem(
                    rule.getLhs() + " -> •"
                      + getSubSequence(rule.getRhs(), k, rule.getRhs().length),
                    i, j));
                } else {
                  scan.addAntecedence(new CfgDottedItem(
                    rule.getLhs() + " -> " + getSubSequence(rule.getRhs(), 0, k)
                      + " •"
                      + getSubSequence(rule.getRhs(), k, rule.getRhs().length),
                    i, j));
                }
                scan.addConsequence(new CfgDottedItem(rule.getLhs() + " -> "
                  + getSubSequence(rule.getRhs(), 0, k + 1) + " •"
                  + getSubSequence(rule.getRhs(), k + 1, rule.getRhs().length),
                  i, j + 1));
                scan.setName("scan " + wsplit[j]);
                schema.addRule(scan);
                // System.out.println(scan.toString()); // DEBUG
              }
            }

            for (CfgProductionRule rule2 : cfg.getR()) {
              for (int k = 0; k < rule.getRhs().length; k++) {
                if (rule.getRhs()[k].equals(rule2.getLhs())) {
                  DeductionRule predict = new DeductionRule();
                  predict.addAntecedence(new CfgDottedItem(
                    rule.getLhs() + " -> " + getSubSequence(rule.getRhs(), 0, k)
                      + " •"
                      + getSubSequence(rule.getRhs(), k, rule.getRhs().length),
                    i, j));
                  for (int l = j; l <= wsplit.length; l++) {
                    DeductionRule complete = new DeductionRule();
                    if (rule.getRhs()[0].length() == 0 ) {
                      complete.addAntecedence(new CfgDottedItem(rule.getLhs()
                        + " -> •"
                        + getSubSequence(rule.getRhs(), k, rule.getRhs().length),
                        i, j));
                    } else {
                      complete.addAntecedence(new CfgDottedItem(rule.getLhs()
                        + " -> " + getSubSequence(rule.getRhs(), 0, k) + " •"
                        + getSubSequence(rule.getRhs(), k, rule.getRhs().length),
                        i, j));
                    }
                    if (rule2.getRhs()[0].length() == 0) {
                        complete.addAntecedence(
                          new CfgDottedItem(rule2.getLhs() + " -> •", j, l));
                    } else {
                      complete.addAntecedence(
                        new CfgDottedItem(rule2.getLhs() + " -> "
                          + String.join(" ", rule2.getRhs()) + " •", j, l));
                    }
                    complete.addConsequence(new CfgDottedItem(rule.getLhs()
                      + " -> " + getSubSequence(rule.getRhs(), 0, k + 1) + " •"
                      + getSubSequence(rule.getRhs(), k + 1,
                        rule.getRhs().length),
                      i, l));
                    complete.setName("complete " + rule2.getLhs());
                    schema.addRule(complete);
                    // System.out.println(complete.toString()); // DEBUG
                  }
                  if (rule2.getRhs().length > 0) {
                    predict.addConsequence(new CfgDottedItem(rule2.getLhs()
                      + " -> •" + String.join(" ", rule2.getRhs()), j, j));
                  } else {
                    predict.addConsequence(
                      new CfgDottedItem(rule2.getLhs() + " -> •", j, j));
                  }
                  predict.setName("predict " + rule2.getLhs() + " -> "
                    + String.join(" ", rule2.getRhs()));
                  schema.addRule(predict);
                  // System.out.println(predict.toString()); // DEBUG
                }
              }
            }
          }
        }
      }
    }
    return schema;
  }

  // TODO too slow
  public static ParsingSchema CfgToLeftCornerRules(Cfg cfg, String w) {
    String[] wsplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    DeductionRule axiom = new DeductionRule();
    axiom.addConsequence(new CfgDollarItem(w, cfg.getStart_var(), ""));
    axiom.setName("axiom");
    schema.addRule(axiom);

    for (String stackcompleted : SetUtils
      .star(SetUtils.union(cfg.getTerminals(), cfg.getVars()), wsplit.length)) {
      String[] stackcompletedsplit = stackcompleted.split(" ");
      for (String stackpredicted : SetUtils.star(
        SetUtils.union(cfg.getTerminals(), cfg.getVars(), new String[] {"$"}),
        wsplit.length + 1)) { // TODO not enough, estimate automatically=?
        String[] stackpredictedsplit = stackpredicted.split(" ");
        for (String stacklhs : SetUtils.star(cfg.getVars(), wsplit.length)) {
          if (!stackpredictedsplit[0].equals("$")) {
            for (CfgProductionRule rule : cfg.getR()) {
              if (stackcompletedsplit[0].equals(rule.getRhs()[0])) {
                DeductionRule reduce = new DeductionRule();
                reduce.addAntecedence(
                  new CfgDollarItem(stackcompleted, stackpredicted, stacklhs));
                String newstackpredicted = "";
                if (stackpredicted.length() == 0) {
                  newstackpredicted =
                    getSubSequence(rule.getRhs(), 1, rule.getRhs().length)
                      + " $";
                } else {
                  newstackpredicted =
                    getSubSequence(rule.getRhs(), 1, rule.getRhs().length)
                      + " $ " + stackpredicted;
                }
                String newstacklhs = "";
                if (stacklhs.length() == 0) {
                  newstacklhs = rule.getLhs();
                } else {
                  newstacklhs = rule.getLhs() + " " + stacklhs;
                }
                reduce.addConsequence(new CfgDollarItem(
                  getSubSequence(stackcompletedsplit, 1,
                    stackcompletedsplit.length),
                  newstackpredicted, newstacklhs));
                reduce.setName("reduce " + rule.getLhs() + " -> "
                  + String.join(" ", rule.getRhs()));
                schema.addRule(reduce);
                // System.out.println(reduce.toString()); // DEBUG
              }
            }
          }

          if (stackpredictedsplit[0].equals("$") && stacklhs.length() > 0) {
            String[] stacklhssplit = stacklhs.split(" ");
            DeductionRule move = new DeductionRule();
            move.addAntecedence(
              new CfgDollarItem(stackcompleted, stackpredicted, stacklhs));
            if (stackcompleted.length() > 0) {
              move.addConsequence(
                new CfgDollarItem(stacklhssplit[0] + " " + stackcompleted,
                  getSubSequence(stackpredictedsplit, 1,
                    stackpredictedsplit.length),
                  getSubSequence(stacklhssplit, 1, stacklhssplit.length)));
            } else {
              move.addConsequence(new CfgDollarItem(stacklhssplit[0],
                getSubSequence(stackpredictedsplit, 1,
                  stackpredictedsplit.length),
                getSubSequence(stacklhssplit, 1, stacklhssplit.length)));
            }
            move.setName("move " + stacklhssplit[0]);
            schema.addRule(move);
            // System.out.println(move.toString()); // DEBUG
          }
          if (stackcompleted.length() > 0 && stackpredicted.length() > 0
            && stackcompletedsplit[0].equals(stackpredictedsplit[0])) {
            DeductionRule remove = new DeductionRule();
            remove.addAntecedence(
              new CfgDollarItem(stackcompleted, stackpredicted, stacklhs));
            remove.addConsequence(new CfgDollarItem(
              getSubSequence(stackcompletedsplit, 1,
                stackcompletedsplit.length),
              getSubSequence(stackpredictedsplit, 1,
                stackpredictedsplit.length),
              stacklhs));
            remove.setName("remove " + stackcompletedsplit[0]);
            schema.addRule(remove);
            // System.out.println(remove.toString()); // DEBUG
          }
        }
      }
    }
    schema.addGoal(new CfgDollarItem("", "", ""));
    return schema;
  }

  private static String getStringHeadIfEndsWith(String[] seqsplit,
    String[] rhs) {
    if (seqsplit.length < rhs.length)
      return null;
    for (int i = 0; i < rhs.length; i++) {
      if (!(seqsplit[seqsplit.length - rhs.length + i].equals(rhs[i]))) {
        return null;
      }
    }
    return getSubSequence(seqsplit, 0, seqsplit.length - rhs.length);
  }

  private static String[] append(String[] split, String[] rhs) {
    StringBuilder subseq = new StringBuilder();
    for (int i = 0; i < split.length; i++) {
      if (i > 0)
        subseq.append(" ");
      subseq.append(split[i]);
    }
    for (int i = 0; i < rhs.length; i++) {
      if (subseq.length() > 0)
        subseq.append(" ");
      subseq.append(rhs[i]);
    }
    return subseq.toString().split(" ");
  }

  private static String getSubSequence(String[] sequence, int from, int to) {
    StringBuilder subseq = new StringBuilder();
    for (int i = from; i < to && i < sequence.length; i++) {
      if (i > from)
        subseq.append(" ");
      subseq.append(sequence[i]);
    }
    return subseq.toString();
  }
}
