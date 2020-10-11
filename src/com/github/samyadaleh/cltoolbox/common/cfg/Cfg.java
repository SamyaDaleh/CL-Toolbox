package com.github.samyadaleh.cltoolbox.common.cfg;

import com.github.samyadaleh.cltoolbox.common.cfg.util.*;
import com.github.samyadaleh.cltoolbox.common.finiteautomata.NondeterministicFiniteAutomaton;
import com.github.samyadaleh.cltoolbox.common.parser.inner.InnerCfgGrammarParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.*;

/**
 * Representation of a context-free grammar consisting of nonterminals,
 * terminals, production rules and a start symbol.
 */
public class Cfg extends AbstractCfg {
  private final List<CfgProductionRule> productionRules = new ArrayList<>();
  private static final Logger log = LogManager.getLogger();

  public Cfg() {
    super();
  }

  /**
   * Creates a CFG from a PCFG by throwing away all probabilities.
   */
  public Cfg(Pcfg pcfg) {
    this.setNonterminals(pcfg.getNonterminals());
    this.setTerminals(pcfg.getTerminals());
    this.setStartSymbol(pcfg.getStartSymbol());
    for (PcfgProductionRule rule : pcfg.getProductionRules()) {
      CfgProductionRule newRule =
          new CfgProductionRule(rule.getLhs(), rule.getRhs());
      this.productionRules.add(newRule);
    }
  }

  public Cfg(BufferedReader in) throws ParseException {
    new InnerCfgGrammarParser(this, in).invoke();
  }

  public Cfg(NondeterministicFiniteAutomaton nfa) {
    setTerminals(nfa.getTerminals());
    setStartSymbol(nfa.getInitialState());
    setNonterminals(nfa.getStates());
    for (Map.Entry<String[], String[]> entry : nfa.getTransitionFunction()
        .entrySet()) {
      for (String transitionState : entry.getValue()) {
        try {
          productionRules.add(new CfgProductionRule(
              entry.getKey()[0] + " -> " + entry.getKey()[1] + " "
                  + transitionState));
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public List<CfgProductionRule> getProductionRules() {
    return productionRules;
  }

  /**
   * Returns true if there is at least one rule with an empty right side,
   * except it's a start symbol rule and the start symbol never occurs on any
   * rhs.
   */
  public boolean hasEpsilonProductions() {
    return EmptyProductions.hasEpsilonProductions(this);
  }

  /**
   * Returns true if grammar is in Canonical Two Form. C2F is like Chomsky
   * Normal form, but chain rules are also allowed.
   */
  public boolean isInCanonicalTwoForm() {
    for (CfgProductionRule rule : this.productionRules) {
      if (rule.getLhs().equals(this.getStartSymbol()) && rule.getRhs()[0]
          .equals("")) {
        for (CfgProductionRule rule2 : this.productionRules) {
          if (rule2.getRhs().length == 2 && (
              rule2.getRhs()[0].equals(this.getStartSymbol()) || rule2
                  .getRhs()[1].equals(this.getStartSymbol()))) {
            return false;
          }
        }
      } else if (
          rule.getRhs().length == 2 && (!nonterminalsContain(rule.getRhs()[0])
              || !nonterminalsContain(rule.getRhs()[1]))
              || rule.getRhs().length > 2) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if the grammar is in Chomsky Normal Form. A grammar is in CNF
   * if all rules are either of the form A -> t or A -> BC. S -> ε is allowed,
   * in which case S must not appear on any right hand side.
   */
  public boolean isInChomskyNormalForm() {
    if (!isInCanonicalTwoForm()) {
      return false;
    }
    for (CfgProductionRule rule : this.productionRules) {
      if (rule.getRhs().length == 1 && !"".equals(rule.getRhs()[0])) {
        if (!terminalsContain(rule.getRhs()[0])) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Returns true if grammar is in Greibach Normal Form. All right hand sides
   * must start with a terminal, followed by arbitrary many nonterminals
   * including none.
   */
  public boolean isInGreibachNormalForm() {
    for (CfgProductionRule rule : this.productionRules) {
      if (rule.getRhs()[0].equals("")) {
        return false;
      }
      if (!terminalsContain(rule.getRhs()[0])) {
        return false;
      }
      for (int i = 1; i < rule.getRhs().length; i++) {
        if (!nonterminalsContain(rule.getRhs()[i])) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Returns an equivalent CFG where all rhs have at most length 2.
   */
  public Cfg getBinarizedCfg() {
    return Binarization.binarize(this);
  }

  /**
   * Returns true if all rhs' have at most length 2.
   */
  public boolean isBinarized() {
    return Binarization.isBinarized(this);
  }

  /**
   * Returns an equivalent grammar without non-generating symbols. Call this
   * before removing non-reachable symbols.
   */
  public Cfg getCfgWithoutNonGeneratingSymbols() {
    return UselessSymbols.removeNonGeneratingSymbols(this);
  }

  /**
   * Returns an equivalent grammar without non-reachable symbols. Before
   * calling this, remove all non-generating symbols.
   */
  public Cfg getCfgWithoutNonReachableSymbols() {
    return UselessSymbols.removeNonReachableSymbols(this);
  }

  /**
   * Returns an equivalent CFG without empty productions, only S -> ε is
   * allowed in which case it is removed from all rhs'. May leaves non
   * generating symbols behind.
   */
  public Cfg getCfgWithoutEmptyProductions() {
    return EmptyProductions.removeEmptyProductions(this);
  }

  /**
   * Returns an equivalent grammar without chain rules, that are rules of the
   * form A -> B. Remove epsilon productions beforehand.
   */
  public Cfg getCfgWithoutChainRules() {
    return ChainRules.removeChainRules(this);
  }

  /**
   * Returns a new grammar where in all rhs > 1 terminals are replaced by
   * nonterminals and new rules A -> a are added.
   */
  public Cfg getCfgWithEitherOneTerminalOrNonterminalsOnRhs() {
    return MixedRhs.replaceTerminals(this);
  }

  protected void appendRuleRepresentation(StringBuilder builder) {
    for (int i = 0; i < productionRules.size(); i++) {
      if (i > 0) {
        builder.append(", ");
      }
      builder.append(productionRules.get(i).toString());
    }
  }

  /**
   * Returns true if CFG has one rule with direct left recursion, of the form A
   * -> A.... Remove epsilon productions to make sure no indirect left recursion
   * is left.
   */
  public boolean hasDirectLeftRecursion() {
    return LeftRecursion.hasDirectLeftRecursion(this);
  }

  /**
   * Returns true if CFG has one rule with direct left recursion, of the form A
   * -> A.... Remove epsilon productions to make sure no indirect left recursion
   * is left.
   */
  public boolean hasLeftRecursion() {
    return LeftRecursion.hasLeftRecursion(this);
  }

  /**
   * Removes direct left recursion. S -> S is ignored. S -> S a | b are
   * replaced by S -> b S1, S1 -> a S1 | ε Adds empty productions to the grammar
   * and maybe chain rules. Remove empty productions first to make sure grammar
   * does not contain indirect left recursion.
   */
  Cfg getCfgWithoutDirectLeftRecursion() {
    return LeftRecursion.removeDirectLeftRecursion(this);
  }

  /**
   * Removes left recursion. S -> S is ignored. S -> S a | b are
   * replaced by S -> b S1, S1 -> a S1 | ε Adds empty productions to the grammar
   * and maybe chain rules. Remove empty productions first to make sure grammar
   * does not contain indirect left recursion.
   */
  public Cfg getCfgWithoutLeftRecursion() throws ParseException {
    return LeftRecursion.removeLeftRecursion(this);
  }

  /**
   * Returns true if there is at least one production rule that contains
   * terminals and nonterminals as rhs symbols.
   */
  public boolean hasMixedRhs() {
    return MixedRhs.hasMixedRhs(this);
  }

  /**
   * Creates a CfgProductionRule from the string representation and adds it to
   * its set of rules.
   */
  public void addProductionRule(String rule) throws ParseException {
    this.productionRules.add(new CfgProductionRule(rule));
  }

  /**
   * Returns true if grammar has generating symbols.
   */
  public boolean hasGeneratingSymbols() {
    return UselessSymbols.hasGeneratingSymbols(this);
  }

  public Cfg getLeftFactoredCfg() {
    return Factoring.getLeftFactoredCfg(this);
  }

  public Cfg getCfgWithoutDuplicates() {
    Cfg cfg = new Cfg();
    cfg.setStartSymbol(this.getStartSymbol());
    Set<CfgProductionRule> newProductionRulesSet = new LinkedHashSet<>();
    try {
      for (CfgProductionRule rule : this.getProductionRules()) {
        newProductionRulesSet.add(new CfgProductionRule(rule.toString()));
      }
      List<CfgProductionRule> newProductionRules =
          new ArrayList<>(newProductionRulesSet);
      for (CfgProductionRule rule : newProductionRules) {
        cfg.addProductionRule(rule.toString());
      }
    } catch (ParseException e) {
      log.debug("Should never happen", e);
    }
    Set<String> setTerminals =
        new LinkedHashSet<>(Arrays.asList(this.getTerminals()));
    cfg.setTerminals(setTerminals.toArray(new String[0]));
    Set<String> setNonterminals =
        new LinkedHashSet<>(Arrays.asList(this.getNonterminals()));
    cfg.setNonterminals(setNonterminals.toArray(new String[0]));
    return cfg;
  }

  /**
   * Returns a Cfg where no nonterminal appears both as lc and in other places
   * by doubling respective nonterminals and their rules.
   * Separates terminals from nonterminals beforehand.
   */
  public Cfg getCfgWithDoubledRules() throws ParseException {
    return Doubling.doubleSymbols(this);
  }

  /**
   * Returns true if all rules are of the format T*N?
   */
  public boolean isRightLinear() {
    for (CfgProductionRule rule : productionRules) {
      boolean nSawn = false;
      for (String rhsSym : rule.getRhs()) {
        if (nSawn) {
          return false;
        }
        if (nonterminalsContain(rhsSym)) {
          nSawn = true;
        }
      }
    }
    return true;
  }

  public Cfg getCfgWithReversedProductionRules() {
    return ReverseProductionRules.getCfgWithReversedProductionRules(this);
  }
}
