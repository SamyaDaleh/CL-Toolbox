package com.github.samyadaleh.cltoolbox.common.cfg;

import com.github.samyadaleh.cltoolbox.common.cfg.util.*;
import com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils;
import com.github.samyadaleh.cltoolbox.common.parser.Token;
import com.github.samyadaleh.cltoolbox.common.parser.TokenReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.samyadaleh.cltoolbox.common.parser.GrammarParserUtils.addSymbolToCategory;

/**
 * Representation of a context-free grammar consisting of nonterminals,
 * terminals, production rules and a start symbol.
 */
public class Cfg extends AbstractCfg {
  private final List<CfgProductionRule> productionRules = new ArrayList<>();

  public Cfg() {
    super();
  }

  /**
   * Creates a CFG from a PCFG by throwing away all probabilities.
   */
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

  public Cfg(BufferedReader in) throws IOException, ParseException {
    Character[] specialChars =
        new Character[] {'-', '>', '{', '}', ',', '|', '='};
    TokenReader reader = new TokenReader(in, specialChars);
    Set<String> validCategories = new HashSet<>();
    validCategories.add("N");
    validCategories.add("T");
    validCategories.add("S");
    validCategories.add("P");
    validCategories.add("G");
    List<String> category = new ArrayList<>();
    String lhs = null;
    StringBuilder rhs = null;
    List<String> symbols = new ArrayList<>();
    Token token;
    while ((token = reader.getNextToken()) != null) {
      String tokenString = token.getString();
      switch (category.size()) {
      case 0:
        GrammarParserUtils
            .handleMainCategory(this, validCategories, category, token);
        break;
      case 1:
        addSymbolToCategory(category, token, "=");
        break;
      case 2:
        category = GrammarParserUtils
            .addStartsymbolOrAddCategory(this, category, token);
        break;
      case 3:
        switch (category.get(0)) {
        case "N":
          switch (tokenString) {
          case "}":
            this.nonterminals = symbols.toArray(new String[0]);
            category = new ArrayList<>();
            symbols = new ArrayList<>();
            break;
          case ",":
            break;
          default:
            symbols.add(tokenString);
          }
          break;
        case "T":
          switch (tokenString) {
          case "}":
            this.terminals = symbols.toArray(new String[0]);
            category = new ArrayList<>();
            symbols = new ArrayList<>();
            break;
          case ",":
            break;
          default:
            symbols.add(tokenString);
          }
          break;
        case "P":
          lhs = GrammarParserUtils.findLhsOrAddCategory(category, lhs, token);
          break;
        default:
          if (lhs != null) {
            throw new ParseException("Expected - but found " + tokenString,
                token.getLineNumber());
          }
          if (!tokenString.equals(",")) {
            lhs = tokenString;
          }
        }
        break;
      case 4:
        if (tokenString.equals(">")) {
          category.add(tokenString);
          rhs = new StringBuilder();
        } else {
          throw new ParseException("Expected > but found " + token,
              token.getLineNumber());
        }
        break;
      default:
        switch (tokenString) {
        case "}":
          category = new ArrayList<>();
          this.addProductionRule(lhs + " -> " + rhs.toString());
          lhs = null;
          break;
        case "|":
          this.addProductionRule(lhs + " -> " + rhs.toString());
          rhs = new StringBuilder();
          break;
        case ",":
          this.addProductionRule(lhs + " -> " + rhs.toString());
          rhs = new StringBuilder();
          lhs = null;
          category.remove(4);
          category.remove(3);
          break;
        default:
          if (rhs.length() > 0) {
            rhs.append(' ');
          }
          rhs.append(tokenString);
        }
        break;
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
      if (rule.getLhs().equals(startSymbol) && rule.getRhs()[0].equals("")) {
        for (CfgProductionRule rule2 : this.productionRules) {
          if (rule2.getRhs()[0].equals(startSymbol) || rule2.getRhs()[1]
              .equals(startSymbol)) {
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
      if (rule.getRhs().length == 1) {
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

}
