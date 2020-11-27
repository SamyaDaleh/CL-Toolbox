package com.github.samyadaleh.cltoolbox.chartparsing.converter.cfg;

import com.github.samyadaleh.cltoolbox.chartparsing.item.DeductionChartItem;
import com.github.samyadaleh.cltoolbox.chartparsing.ParsingSchema;
import com.github.samyadaleh.cltoolbox.chartparsing.StaticDeductionRule;
import com.github.samyadaleh.cltoolbox.chartparsing.cfg.shiftreduce.CfgLrKRule;
import com.github.samyadaleh.cltoolbox.common.ArrayUtils;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.*;

import static com.github.samyadaleh.cltoolbox.common.Constants.*;

public class CfgToLrKRulesConverter {
  private static final Logger log = LogManager.getLogger();

  public static ParsingSchema cfgToLrKRules(Cfg cfg, String w, int k)
      throws ParseException {
    String[] wSplit = w.split(" ");
    ParsingSchema schema = new ParsingSchema();
    StaticDeductionRule axiom = new StaticDeductionRule();
    axiom.setName(DEDUCTION_RULE_CFG_LRK_AXIOM);
    axiom.addConsequence(new DeductionChartItem("q0", "0"));
    schema.addAxiom(axiom);
    String[] initialState = computeInitialState(cfg, k);
    List<List<String[]>> states = computeStates(cfg, initialState, k);
    printStates(states);
    Map<String, String> parseTable =
        computeParseTable(states, initialState, wSplit.length, schema, cfg, k);
    for (Map.Entry<String, String> entry : parseTable.entrySet()) {
      if (entry.getValue().split(", ").length > 1) {
        throw new ParseException(
            "Shift-Reduce conflict for state " + entry.getKey()
                + ", grammar cannot be parsed with LR(" + k + ").", 1);
      }
    }
    printParseTable(parseTable, states.size());
    List<String> statesWithShifts = new ArrayList<>();
    List<String> statesWithReduces = new ArrayList<>();
    for (Map.Entry<String, String> entry : parseTable.entrySet()) {
      String state = entry.getKey().split(" ")[0];
      String action = entry.getValue().substring(0, 1);
      if (action.equals(DEDUCTION_ACTION_CFG_LRK_SHIFT)) {
        if (statesWithReduces.contains(state)) {
          throw new ParseException("Shift-Reduce conflict for state " + state
              + ", grammar cannot be parsed with LR(" + k + ").", 1);
        }
        statesWithShifts.add(state);

      } else if (action.equals(DEDUCTION_ACTION_CFG_LRK_REDUCE)) {
        if (statesWithShifts.contains(state)) {
          throw new ParseException("Shift-Reduce conflict for state " + state
              + ", grammar cannot be parsed with LR(" + k + ").", 1);
        }
        statesWithReduces.add(state);
      }
    }
    CfgLrKRule rule =
        new CfgLrKRule(wSplit, cfg.getProductionRules(), parseTable);
    schema.addRule(rule);
    for (int i = 0; i < states.size(); i++) {
      if (states.get(i).contains(initialState)) {
        schema.addGoal(new DeductionChartItem("q" + i));
        break;
      }
    }
    return schema;
  }

  public static String[] computeInitialState(Cfg cfg, int k) {
    String[] initialState;
    if (k > 0) {
      initialState =
          new String[] {cfg.getStartSymbol() + "' -> •" + cfg.getStartSymbol(),
              "$"};
    } else {
      initialState =
          new String[] {cfg.getStartSymbol() + "' -> •" + cfg.getStartSymbol()};
    }
    return initialState;
  }

  private static void printStates(List<List<String[]>> states) {
    if (log.isDebugEnabled()) {
      log.debug("Generated states: ");
      for (List<String[]> state : states) {
        StringBuilder line = new StringBuilder("{");
        for (String[] entry : state) {
          line.append(' ').append(ArrayUtils.toString(entry));
        }
        line.append(" }");
        log.debug(line.toString());
      }
    }
  }

  private static void printParseTable(Map<String, String> parseTable,
      int statesSize) {
    if (log.isDebugEnabled()) {
      List<String> keysWithoutStates = new ArrayList<>();
      int columnWidth = 0;
      for (String key : parseTable.keySet()) {
        if (!key.endsWith(" $")) {
          String[] keySplit = key.split(" ", 2);
          keysWithoutStates.add(keySplit[1]);
          int thisWidth = keySplit[1].length();
          if (thisWidth > columnWidth) {
            columnWidth = thisWidth;
          }
        }
      }
      keysWithoutStates.add("$");
      columnWidth += 3;
      printParseTableHeader(keysWithoutStates, columnWidth);
      for (int i = 0; i < statesSize; i++) {
        printParseTableLine(parseTable, String.valueOf(i), keysWithoutStates,
            columnWidth);
      }
    }
  }

  private static void printParseTableLine(Map<String, String> parseTable,
      String state, List<String> keysWithoutStates, int columnWidth) {
    StringBuilder line = new StringBuilder();
    line.append(printParseTableCell(state, columnWidth));
    for (String column : keysWithoutStates) {
      String key = state + " " + column;
      String entry = parseTable.get(key);
      if (entry == null) {
        line.append(printParseTableCell("", columnWidth));
      } else {
        line.append(printParseTableCell(entry, columnWidth));
      }
    }
    log.debug(line.toString());
  }

  private static String printParseTableCell(String content, int columnWidth) {
    StringBuilder cell = new StringBuilder(content);
    for (int i = 0; i < columnWidth - content.length(); i++) {
      cell.append(' ');
    }
    return cell.toString();
  }

  private static void printParseTableHeader(List<String> keysWithoutStates,
      int columnWidth) {
    StringBuilder line = new StringBuilder();
    line.append(printParseTableCell("", columnWidth));
    for (String column : keysWithoutStates) {
      line.append(printParseTableCell(column, columnWidth));
    }
    log.debug(line.toString());
  }

  public static Map<String, String> computeParseTable(
      List<List<String[]>> states, String[] initialState, int wSplitLength,
      ParsingSchema schema, Cfg cfg, int k) {
    Map<String, String> parseTable = new HashMap<>();
    String[] finalState = initialState.clone();
    finalState[0] = initialState[0].replaceFirst("•", "") + " •";
    for (int i = 0; i < states.size(); i++) {
      addShiftActionToParseTable(states, cfg, k, parseTable, i);
      addReduceActionToParseTable(states, cfg, k, parseTable, i);
      if (contains(states.get(i), finalState)) {
        parseTable.put(i + " $", DEDUCTION_ACTION_CFG_LRK_ACCEPT);
        schema.addGoal(
            new DeductionChartItem("q0 " + cfg.getStartSymbol() + " q" + i,
                String.valueOf(wSplitLength)));
      }
    }
    addGotoActionToParseTable(states, cfg, k, parseTable);
    return parseTable;
  }

  private static void addGotoActionToParseTable(List<List<String[]>> states,
      Cfg cfg, int k, Map<String, String> parseTable) {
    for (int i = 0; i < states.size(); i++) {
      for (String nt : cfg.getNonterminals()) {
        List<String[]> gotoState = computeGotoStates(states.get(i), nt, cfg, k);
        if (gotoState.size() == 0) {
          continue;
        }
        for (int j = 0; j < states.size(); j++) {
          if (equals(gotoState, states.get(j))) {
            parseTable.put(i + " " + nt, String.valueOf(j));
            break;
          }
        }
      }
    }
  }

  private static void addReduceActionToParseTable(List<List<String[]>> states,
      Cfg cfg, int k, Map<String, String> parseTable, int i) {
    for (String[] stateEntry : states.get(i)) {
      if (stateEntry[0].endsWith("•")) {
        for (int j = 0; j < cfg.getProductionRules().size(); j++) {
          if (stateEntry[0].substring(0, stateEntry[0].length() - 2)
              .equals(cfg.getProductionRules().get(j).toString())) {
            String key = i + " $";
            if (parseTable.containsKey(key)) {
              parseTable.put(key,parseTable.get(key) + ", " + "r" + (j + 1));
            } else {
              parseTable.put(key, "r" + (j + 1));
            }
          }
        }
      }
    }
  }

  private static void addShiftActionToParseTable(List<List<String[]>> states,
      Cfg cfg, int k, Map<String, String> parseTable, int i) {
    for (String t : cfg.getTerminals()) {
      List<String[]> gotoState = computeGotoStates(states.get(i), t, cfg, k);
      for (int j = 0; j < states.size(); j++) {
        if (equals(gotoState, states.get(j))) {
          String key = i + " " + t;
          if (parseTable.containsKey(key)) {
            parseTable.put(key,
                parseTable.get(key) + ", " + DEDUCTION_ACTION_CFG_LRK_SHIFT
                    + j);
          }
          parseTable.put(key, DEDUCTION_ACTION_CFG_LRK_SHIFT + j);
          break;
        }
      }
    }
  }

  /**
   * Returns true if state is one of the entries in states by string comparison.
   */
  private static boolean contains(List<String[]> states, String[] state) {
    for (String[] stateEntry : states) {
      if (stateEntry.length != state.length) {
        return false;
      }
      boolean same = true;
      for (int i = 0; i < state.length; i++) {
        if (!state[i].equals(stateEntry[i])) {
          same = false;
          break;
        }
      }
      if (same) {
        return true;
      }
    }
    return false;
  }

  public static List<List<String[]>> computeStates(Cfg cfg,
      String[] initialState, int k) {
    List<List<String[]>> states = new ArrayList<>();
    List<String[]> initialClosure = new ArrayList<>();
    initialClosure.add(initialState);
    states.add(computeClosure(initialClosure, cfg, k));
    boolean changed = true;
    while (changed) {
      changed = false;
      String[] nUT =
          new String[cfg.getNonterminals().length + cfg.getTerminals().length];
      System.arraycopy(cfg.getNonterminals(), 0, nUT, 0,
          cfg.getNonterminals().length);
      System.arraycopy(cfg.getTerminals(), 0, nUT, cfg.getNonterminals().length,
          cfg.getTerminals().length);
      for (String x : nUT) {
        List<List<String[]>> statesCopy = new ArrayList<>(states);
        for (List<String[]> q : statesCopy) {
          List<String[]> gotoStates = computeGotoStates(q, x, cfg, k);
          if (Union(states, gotoStates)) {
            changed = true;
          }
        }
      }
    }
    return states;
  }

  /**
   * Returns true if goToStates is not in states yet.
   */
  private static boolean Union(List<List<String[]>> states,
      List<String[]> gotoStates) {
    if (gotoStates.size() == 0) {
      return false;
    }
    for (List<String[]> state : states) {
      if (equals(state, gotoStates)) {
        return false;
      }
    }
    states.add(gotoStates);
    return true;
  }

  /**
   * Returns true if lists contain the same arrays, order in list doesn't matter.
   */
  private static boolean equals(List<String[]> states,
      List<String[]> gotoStates) {
    if (states.size() != gotoStates.size()) {
      return false;
    }
    for (String[] state1 : states) {
      boolean found = false;
      for (String[] state2 : gotoStates) {
        if (Arrays.equals(state1, state2)) {
          found = true;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

  private static List<String[]> computeGotoStates(List<String[]> q, String x,
      Cfg cfg, int k) {
    List<String[]> gotoStates = new ArrayList<>();
    for (String[] rule : q) {
      String[] newState = getGotoState(rule, x);
      if (newState != null) {
        gotoStates.add(newState);
      }
    }
    return computeClosure(gotoStates, cfg, k);
  }

  private static String[] getGotoState(String[] rule, String x) {
    String[] ruleSplit = rule[0].split(" ");
    for (String aRuleSplit : ruleSplit) {
      if (aRuleSplit.startsWith("•")) {
        if (aRuleSplit.length() > 1) {
          String sym = aRuleSplit.substring(1);
          if (sym.equals(x)) {
            String[] newState = rule.clone();
            int dotPos = rule[0].indexOf('•');
            int afterDot = dotPos + sym.length() + 2;
            if (afterDot < rule[0].length()) {
              newState[0] = rule[0].substring(0, dotPos) + sym + " •" + rule[0]
                  .substring(dotPos + sym.length() + 2);
            } else {
              newState[0] = rule[0].substring(0, dotPos) + sym + " •";
            }
            return newState;
          } else {
            return null;
          }
        } else {
          return null;
        }
      }
    }
    return null;
  }

  private static List<String[]> computeClosure(List<String[]> closure, Cfg cfg,
      int k) {
    boolean changed = true;
    while (changed) {
      changed = false;
      List<String[]> closureCopy = new ArrayList<>(closure);
      for (String[] stateEntry : closureCopy) {
        String[] ruleSplit = stateEntry[0].split(" ");
        for (String sym : ruleSplit) {
          if (sym.startsWith("•")) {
            if (sym.length() > 1) {
              String interestingSymbol = sym.substring(1);
              String stuffafterdot = stateEntry[0].split("•")[1];
              List<String[]> firstSet;
              String[] heritage = ArrayUtils
                  .getSubSequenceAsArray(stateEntry, 1, stateEntry.length);
              if (stuffafterdot.length() > sym.length()) {
                firstSet = getFirstSet(cfg,
                    stateEntry[0].split("•\\S* ")[1].split(" "), heritage, k);
              } else {
                firstSet = getFirstSet(cfg, new String[] {}, heritage, k);
              }
              for (CfgProductionRule rule : cfg.getProductionRules()) {
                if (rule.getLhs().equals(interestingSymbol)) {
                  if (k > 0) {
                    for (String[] lookahead : firstSet) {
                      String[] newState = new String[lookahead.length + 1];
                      newState[0] = rule.getLhs() + " -> •" + ArrayUtils
                          .getSubSequenceAsString(rule.getRhs(), 0,
                              rule.getRhs().length);
                      System.arraycopy(lookahead, 0, newState, 1,
                          lookahead.length);
                      if (!listContainsArray(closure, newState)) {
                        closure.add(newState);
                        changed = true;
                      }
                    }
                  } else {
                    String[] newState = new String[] {
                        rule.getLhs() + " -> •" + ArrayUtils
                            .getSubSequenceAsString(rule.getRhs(), 0,
                                rule.getRhs().length)};
                    if (!listContainsArray(closure, newState)) {
                      closure.add(newState);
                      changed = true;
                    }
                  }
                }
              }
            }
            break;
          }
        }
      }
    }
    return closure;
  }

  private static List<String[]> getFirstSet(Cfg cfg, String[] ruleRest,
      String[] heritage, int k) {
    List<String[]> firstSet = new ArrayList<>();
    if (k == 0) {
      return firstSet;
    }
    List<String[]> firstSetExpansions = new ArrayList<>();
    String[] ruleRestAndHeritage =
        new String[ruleRest.length + heritage.length];
    System.arraycopy(ruleRest, 0, ruleRestAndHeritage, 0, ruleRest.length);
    System.arraycopy(heritage, 0, ruleRestAndHeritage, ruleRest.length,
        heritage.length);
    firstSetExpansions.add(ruleRestAndHeritage);
    while (firstSetExpansions.size() > 0) {
      String[] underExamination = firstSetExpansions.get(0);
      firstSetExpansions.remove(0);
      boolean ntFound = false;
      for (int i = 0; i < k && i < underExamination.length; i++) {
        if (cfg.nonterminalsContain(underExamination[i])) {
          ntFound = true;
          for (CfgProductionRule rule : cfg.getProductionRules()) {
            if (rule.getLhs().equals(underExamination[i])) {
              String[] newExpansion;
              if (rule.getRhs()[0].equals("")) {
                newExpansion =
                    ArrayUtils.getSequenceWithoutIAsArray(underExamination, i);
              } else {
                newExpansion =
                    new String[underExamination.length + rule.getRhs().length
                        - 1];
                System.arraycopy(underExamination, 0, newExpansion, 0, i);
                for (int j = i; j < rule.getRhs().length; j++) {
                  newExpansion[j] = rule.getRhs()[j - i];
                }
                for (int j = i + rule.getRhs().length; j
                    < underExamination.length + rule.getRhs().length - 1; j++) {
                  newExpansion[j] =
                      underExamination[j - rule.getRhs().length + 1];
                }
              }
              firstSetExpansions.add(newExpansion);
            }
          }
          break;
        }
      }
      if (ntFound) {
        continue;
      }
      if (underExamination.length < k) {
        firstSet.add(underExamination);
      } else {
        firstSet.add(ArrayUtils.getSubSequenceAsArray(underExamination, 0, k));
      }
    }
    return firstSet;
  }

  private static boolean listContainsArray(List<String[]> closure,
      String[] newState) {
    for (String[] state : closure) {
      if (state.length == newState.length) {
        boolean same = true;
        for (int i = 0; i < state.length; i++) {
          if (!state[i].equals(newState[i])) {
            same = false;
            break;
          }
        }
        if (same) {
          return true;
        }
      }
    }
    return false;
  }
}
