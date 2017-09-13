package common.cfg;

import java.util.ArrayList;
import java.util.List;

import common.cfg.util.Binarization;
import common.cfg.util.ChainRules;
import common.cfg.util.EmptyProductions;
import common.cfg.util.LeftRecursion;
import common.cfg.util.MixedRhs;
import common.cfg.util.UselessSymbols;

/** Representation of a context-free grammar consisting of nonterminals,
 * terminals, production rules and a start symbol. */
public class Cfg extends AbstractCfg {
  private final List<CfgProductionRule> productionRules =
    new ArrayList<CfgProductionRule>();

  public Cfg() {
    super();
  }

  /** Creates a CFG from a PCFG by throwing away all probabilities. */
  public Cfg(Pcfg pcfg) {
    this.nonterminals = pcfg.getNonterminals();
    this.terminals = pcfg.getTerminals();
    this.startSymbol = pcfg.getStartSymbol();
    for (PcfgProductionRule rule : pcfg.getProductionRules()) {
      CfgProductionRule newRule =
        new CfgProductionRule(rule.getLhs(), rule.getRhs());
      this.productionRules.add(newRule);
    }
  }

  public List<CfgProductionRule> getProductionRules() {
    return productionRules;
  }

  /** Returns true if there is at least one rule with an empty right side,
   * except it's a start symbol rule and the start symbol never occurs on any
   * rhs. */
  public boolean hasEpsilonProductions() {
    return EmptyProductions.hasEpsilonProductions(this);
  }

  /** Returns true if grammar is in Canonical Two Form. C2F is like Chomsky
   * Normal form, but chain rules are also allowed. */
  public boolean isInCanonicalTwoForm() {
    for (CfgProductionRule rule : this.productionRules) {
      if (rule.getLhs().equals(startSymbol) && rule.getRhs()[0].equals("")) {
        for (CfgProductionRule rule2 : this.productionRules) {
          if (rule2.getRhs()[0].equals(startSymbol)
            || rule2.getRhs()[1].equals(startSymbol)) {
            return false;
          }
        }
      } else if (rule.getRhs().length == 2
        && (!nonterminalsContain(rule.getRhs()[0])
          || !nonterminalsContain(rule.getRhs()[1]))
        || rule.getRhs().length > 2) {
        return false;
      }
    }
    return true;
  }

  /** Returns true if the grammar is in Chomsky Normal Form. A grammar is in CNF
   * if all rules are either of the form A -> t or A -> BC. S -> ε is allowed,
   * in which case S must not appear on any right hand side. */
  public boolean isInChomskyNormalForm() {
    if (!isInCanonicalTwoForm()) {
      return false;
    }
    for (CfgProductionRule rule : this.productionRules) {
      if (rule.getRhs().length == 1) {
        if (!terminalsContain(rule.getRhs()[0])) {
          return false;
        }
      }
    }
    return true;
  }

  /** Returns true if grammar is in Greibach Normal Form. All right hand sides
   * must start with a terminal, followed by arbitrary many nonterminals
   * including none. */
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

  /** Returns an equivalent CFG where all rhs have at most length 2. */
  public Cfg getBinarizedCfg() {
    return Binarization.binarize(this);
  }

  /** Returns true if all rhs' have at most length 2. */
  public boolean isBinarized() {
    return Binarization.isBinarized(this);
  }

  /** Returns an equivalent grammar without non-generating symbols. Call this
   * before removing non-reachable symbols. */
  public Cfg getCfgWithoutNonGeneratingSymbols() {
    return UselessSymbols.removeNonGeneratingSymbols(this);
  }

  /** Returns an equivalent grammar without non-reachable symbols. Before
   * calling this, remove all non-generating symbols. */
  public Cfg getCfgWithoutNonReachableSymbols() {
    return UselessSymbols.removeNonReachableSymbols(this);
  }

  /** Returns an equivalent CFG without empty productions, only S -> ε is
   * allowed in which case it is removed from all rhs'. May leaves non
   * generating symbols behind. */
  public Cfg getCfgWithoutEmptyProductions() {
    return EmptyProductions.removeEmptyProductions(this);
  }

  /** Returns an equivalent grammar without chain rules, that are rules of the
   * form A -> B. Remove epsilon productions beforehand. */
  public Cfg getCfgWithoutChainRules() {
    return ChainRules.removeChainRules(this);
  }

  /** Returns true if grammar has rules of the form A -> B. */
  boolean hasChainRules() {
    return ChainRules.hasChainRules(this);
  }

  /** Returns a new grammar where in all rhs > 1 terminals are replaced by
   * nonterminals and new rules A -> a are added. */
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

  /** Returns true if CFG has one rule with direct left recursion, of the form A
   * -> A.... Remove epsilon productions to make sure no indirect left recursion
   * is left. */
  public boolean hasDirectLeftRecursion() {
    return LeftRecursion.hasDirectLeftRecursion(this);
  }

  /** Removes direct left recursion. S -> S is ignored. S -> S a | b are
   * replaced by S -> b S1, S1 -> a S1 | ε Adds empty productions to the grammar
   * and maybe chain rules. Remove empty productions first to make sure grammar
   * does not contain indirect left recursion. */
  public Cfg getCfgWithoutLeftRecursion() {
    return LeftRecursion.removeLeftRecursion(this);
  }

  /** Returns true if there is at least one production rule that contains
   * terminals and nonterminals as rhs symbols. */
  public boolean hasMixedRhs() {
    return MixedRhs.hasMixedRhs(this);
  }

  /** Creates a CfgProductionRule from the string representation and adds it to
   * its set of rules. */
  public void addProductionRule(String rule) {
    this.productionRules.add(new CfgProductionRule(rule));
  }
}
