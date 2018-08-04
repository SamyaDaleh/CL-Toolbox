package common.cfg.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import chartparsing.Deduction;
import chartparsing.ParsingSchema;
import chartparsing.converter.GrammarToDeductionRulesConverter;
import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

/**
 * Different methods to search for grammars with specific properties.
 */
public class GrammarSearcher {
  private static Deduction deduction = new Deduction();
  private static GrammarToDeductionRulesConverter gdrc =
    new GrammarToDeductionRulesConverter();
  private static ParsingSchema schema;
  private static java.util.Random r = new java.util.Random();
  private static int populationSize = 200;
  private static int generationSize = 1000;

  /**
   * Entry point to call any searcher method from.
   */
  public static void main(String... args) throws ParseException {
    evolutionarySearch();
  }

  /**
   * Uses evolutionary search: Starts with random grammars that are checked for
   * the criteria. Worst half is thrown away, remaining ones are altered and
   * mutated until a goal grammar is found or the maximum number of generations
   * is reached.
   */
  public static void evolutionarySearch() throws ParseException {
    List<Cfg> population = new ArrayList<Cfg>();
    List<Float> scores = new ArrayList<Float>();
    int generation = 0;
    for (int i = 0; i < populationSize; i++) {
      Cfg newCfg = Random.getRandomCfg();
      population.add(newCfg);
      scores.add(evaluateGrammar(newCfg));
    }
    Cfg bestGrammar = null;
    while ((bestGrammar = findMaxScoreGrammar(population, scores)) == null) {
      deleteWorstHalf(population, scores);
      if (generation == generationSize) {
        System.out.println("No target grammar found after " + generationSize
          + " generations. These are the grammars:");
        for (int i = 0; i < population.size(); i++) {
          System.out.println(population.get(i));
          System.out.println("Score: " + scores.get(i));
        }
        return;
      }
      while (population.size() < populationSize) {
        breedGrammarsAndApplyMutation(population, scores);
      }
      generation++;
    }
    System.out.println(bestGrammar);
  }

  /**
   * creates a new grammar from the existing ones that is added to the
   * population.
   */
  private static void breedGrammarsAndApplyMutation(List<Cfg> population,
    List<Float> scores) throws ParseException {
    Cfg m1 = getRandomElement(population);
    Cfg m2 = getRandomElement(population);
    Cfg child = new Cfg();
    List<String> recombinedRules = recombineRules(m1, m2);
    for (int i = 0; i < recombinedRules.size(); i++) {
      child.addProductionRule(recombinedRules.get(i));
    }
    child.setTerminals(combineArrays(m1.getTerminals(), m2.getTerminals()));
    child.setNonterminals(
      combineArrays(m1.getNonterminals(), m2.getNonterminals()));
    if (r.nextBoolean()) {
      child.setStartSymbol(m1.getStartSymbol());
    } else {
      child.setStartSymbol(m2.getStartSymbol());
    }
    if (r.nextInt(1000) < 1000.0 / (populationSize * 10)) {
      mutateGrammar(child);
    }
    if (!alreadyInPopulation(population, child)) {
      population.add(child);
      scores.add(evaluateGrammar(child));
    }
  }

  /**
   * Returns true if the same grammar regarding string representation already
   * exists in population.
   */
  private static boolean alreadyInPopulation(List<Cfg> population, Cfg child) {
    for (Cfg cfg : population) {
      if (cfg.toString().equals(child.toString())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Applys one of a range of possible mutations: add a new nonterminal or
   * terminal, add a new production rule or alter an existing production rule.
   */
  private static void mutateGrammar(Cfg child) throws ParseException {
    int decision = r.nextInt(3);
    if (decision == 0) {
      int rulePos = r.nextInt(child.getProductionRules().size());
      CfgProductionRule alteredRule =
        alterRule(child.getProductionRules().get(rulePos), child);
      child.getProductionRules().set(rulePos, alteredRule);
    }
    if (decision == 1) {
      child
        .addProductionRule(Random.getRandomArrayElement(child.getNonterminals())
          + " -> " + Random.getRandomRhs(child));
    }
    if (decision == 2) {
      if (r.nextBoolean()) {
        String newNt = "N" + child.getNonterminals().length;
        List<String> newNts = new ArrayList<String>();
        for (String oldNt : child.getNonterminals()) {
          newNts.add(oldNt);
        }
        newNts.add(newNt);
        child.setNonterminals(newNts.toArray(new String[newNts.size()]));
      } else {
        String newT = "t" + child.getTerminals().length;
        List<String> newTs = new ArrayList<String>();
        for (String oldT : child.getTerminals()) {
          newTs.add(oldT);
        }
        newTs.add(newT);
        child.setTerminals(newTs.toArray(new String[newTs.size()]));
      }
    }
  }

  /**
   * alters the given production rule and returns the altered one: It can add,
   * alter or remove any symbol, except the lhs which can only be altered.
   */
  private static CfgProductionRule alterRule(
    CfgProductionRule cfgProductionRule, Cfg cfg) throws ParseException {
    String[] ruleSplit = cfgProductionRule.toString().split(" ");
    int mutPos = r.nextInt(ruleSplit.length - 1);
    if (mutPos > 0) {
      mutPos++;
      int decision = r.nextInt(3);
      StringBuilder newRule = new StringBuilder();
      newRule.append(cfgProductionRule.getLhs()).append(" ->");
      int i = 0;
      for (; i < mutPos - 2; i++) {
        newRule.append(" ").append(cfgProductionRule.getRhs()[i]);
      }
      if (decision == 0) {
        String newSym = Random.getRandomArrayElement(
          combineArrays(cfg.getNonterminals(), cfg.getTerminals()));
        newRule.append(" ").append(newSym);
      }
      if (decision == 1) {
        String newSym = Random.getRandomArrayElement(
          combineArrays(cfg.getNonterminals(), cfg.getTerminals()));
        newRule.append(" ").append(newSym);
        i++;
      }
      if (decision == 2) {
        i++;
      }
      for (; i < cfgProductionRule.getRhs().length; i++) {
        newRule.append(" ").append(cfgProductionRule.getRhs()[i]);
      }
      return new CfgProductionRule(newRule.toString());
    } else {
      String newNt = Random.getRandomArrayElement(cfg.getNonterminals());
      String newRule =
        newNt + " -> " + cfgProductionRule.toString().split(" -> ")[1];
      return new CfgProductionRule(newRule);

    }
  }

  /**
   * Pass it two lists of terminals and nonterminals and it returns a new array
   * that contains all symbols. Because those lists were created with
   * incrementing symbols it just return the longest array, which automatically
   * contains all symbols.
   */
  private static String[] combineArrays(String[] array1, String[] array2) {
    if (array1.length > array2.length) {
      return array1;
    } else {
      return array2;
    }
  }

  /**
   * Defines a random split point. Before the point production rules from the
   * first cfg are added, then from the second one.
   */
  private static List<String> recombineRules(Cfg m1, Cfg m2) {
    List<String> newRules = new ArrayList<String>();
    int splitPoint = 1;
    if (m1.getProductionRules().size() > 1) {
      splitPoint = r.nextInt(m1.getProductionRules().size() - 1) + 1;
    }
    for (int i = 0; i < splitPoint; i++) {
      newRules.add(m1.getProductionRules().get(i).toString());
    }
    for (int i = splitPoint; i < m2.getProductionRules().size(); i++) {
      newRules.add(m2.getProductionRules().get(i).toString());
    }
    return newRules;
  }

  /**
   * Returns a random cfg from the list.
   */
  private static Cfg getRandomElement(List<Cfg> population) {
    return population.get(r.nextInt(population.size()));
  }

  /**
   * Deletes the worst element n/2 times.
   */
  private static void deleteWorstHalf(List<Cfg> population,
    List<Float> scores) {
    int halfSize = population.size() / 2;
    for (int i = 0; i < halfSize; i++) {
      deleteMin(population, scores);
    }
  }

  /**
   * Deletes either the next found element of score 0 or the element with the
   * lowest score from both lists.
   */
  private static void deleteMin(List<Cfg> population, List<Float> scores) {
    float min = Float.MAX_VALUE;
    int minPos = 0;
    for (int i = 0; i < scores.size(); i++) {
      if (scores.get(i) == 0.0) {
        population.remove(i);
        scores.remove(i);
        return;
      }
      if (scores.get(i) < min) {
        min = scores.get(i);
        minPos = i;
      }
    }
    population.remove(minPos);
    scores.remove(minPos);
  }

  /**
   * Look for a grammar with score higher than 0.99. I don't trust float
   * comparison with == 1.0
   */
  private static Cfg findMaxScoreGrammar(List<Cfg> population,
    List<Float> scores) {
    for (int i = 0; i < scores.size(); i++) {
      if (1.0 - scores.get(i) < 0.01) {
        return population.get(i);
      }
    }
    return null;
  }

  /**
   * Return the fitness score of the passed grammar according to the current
   * search criteria.
   * @throws ParseException
   */
  private static float evaluateGrammar(Cfg cfg) throws ParseException {
    float actualScore = 0;
    float maxScore = 0;
    int tdLength = 0;
    int lcLength = 0;
    // int srLength = 0;

    maxScore++;
    actualScore = propertyHasGeneratingSymbols(cfg, actualScore);

    maxScore++;
    actualScore = propertyHasNoDirectLeftRecursion(cfg, actualScore);

    maxScore++;
    actualScore = propertyCfgTopdownParseable(cfg, actualScore);
    tdLength = deduction.getChart().size();

    maxScore++;
    actualScore = propertyHasNoEpsilonProductions(cfg, actualScore);

    /* maxScore++; actualScore = propertyCfgShiftReduceParseable(cfg,
     * actualScore); srLength = deduction.getChart().size(); // */

    maxScore++;
    actualScore = propertyCfgLeftCornerParseable(cfg, actualScore);
    lcLength = deduction.getChart().size();

    maxScore++;
    if (lcLength < tdLength) {
      actualScore++;
    }
    // must not fulfill any criteria that leads to Exception
    return actualScore / maxScore;
  }

  private static float propertyHasNoDirectLeftRecursion(Cfg cfg,
    float actualScore) {
    if (!cfg.hasDirectLeftRecursion()) {
      actualScore++;
    }
    return actualScore;
  }

  private static float propertyCfgTopdownParseable(Cfg cfg, float actualScore)
    throws ParseException {
    schema = gdrc.convertToSchema(cfg, "t0 t1", "cfg-topdown");
    if (deduction.doParse(schema, false)) {
      actualScore++;
    }
    return actualScore;
  }

  /* private static float propertyCfgShiftReduceParseable(Cfg cfg, float
   * actualScore) { schema = gdrc.convertToSchema(cfg, "t0 t1",
   * "cfg-shiftreduce"); if (deduction.doParse(schema, false)) { actualScore++;
   * } return actualScore; } // */

  private static float propertyCfgLeftCornerParseable(Cfg cfg,
    float actualScore) throws ParseException {
    schema = gdrc.convertToSchema(cfg, "t0 t1", "cfg-leftcorner");
    if (deduction.doParse(schema, false)) {
      actualScore++;
    }
    return actualScore;
  }

  private static float propertyHasNoEpsilonProductions(Cfg cfg,
    float actualScore) {
    if (!cfg.hasEpsilonProductions()) {
      actualScore++;
    }
    return actualScore;
  }

  private static Float propertyHasGeneratingSymbols(Cfg cfg,
    Float actualScore) {
    if (cfg.hasGeneratingSymbols()) {
      actualScore++;
    }
    return actualScore;
  }
}
