package com.github.samyadaleh.cltoolbox.common.cfg;

import com.github.samyadaleh.cltoolbox.common.parser.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Representation of a context free grammar where the rules have
 * probabilities.
 */
public class Pcfg extends AbstractCfg {
  private final List<PcfgProductionRule> productionRules = new ArrayList<>();

  public Pcfg() {
    super();
  }

  /**
   * Create a PCFG from a CFG where all rules have the same probability.
   */
  public Pcfg(Cfg cfg) {
    this.setNonterminals(cfg.getNonterminals());
    this.setTerminals(cfg.getTerminals());
    this.setStartSymbol(cfg.getStartSymbol());
    for (String nt : this.getNonterminals()) {
      int ruleCount = 0;
      for (CfgProductionRule rule : cfg.getProductionRules()) {
        if (rule.getLhs().equals(nt)) {
          ruleCount++;
        }
      }
      for (CfgProductionRule rule : cfg.getProductionRules()) {
        if (rule.getLhs().equals(nt)) {
          this.productionRules.add(
              new PcfgProductionRule(rule.getLhs(), rule.getRhs(),
                  1.0 / ruleCount));
        }
      }
    }
  }

  public Pcfg(BufferedReader in) throws IOException, ParseException {
    Character[] specialChars =
        new Character[] {'-', '>', '{', '}', ',', '|', '=', ':'};
    TokenReader reader = new TokenReader(in, specialChars);
    Set<String> validCategories = getValidCategories();
    List<String> category = new ArrayList<>();
    int lineNumber = 0;
    String prob = null;
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
        GrammarParserUtils.addSymbolToCategory(category, token, "=");
        break;
      case 2:
        category = GrammarParserUtils
            .addStartsymbolOrAddCategory(this, category, token,
                validCategories);
        break;
      case 3:
        CollectSetContentsPcfg collectSetContents =
            (CollectSetContentsPcfg) new CollectSetContentsPcfg(this, category,
                prob, lhs, symbols, token).invoke();
        category = collectSetContents.getCategory();
        prob = collectSetContents.getProb();
        lhs = collectSetContents.getLhs();
        symbols = collectSetContents.getSymbols();
        break;
      case 4:
        lhs = GrammarParserUtils.findLhsOrAddCategory(category, lhs, token);
        break;
      case 5:
        if (tokenString.equals(">")) {
          category.add(tokenString);
          rhs = new StringBuilder();
        } else {
          throw new ParseException("Expected > but found " + token, lineNumber);
        }
        break;
      default:
        CollectRhsSymbolsPcfg collectRhsSymbols =
            (CollectRhsSymbolsPcfg) new CollectRhsSymbolsPcfg(this, category,
                prob, lhs, rhs, tokenString).invoke();
        category = collectRhsSymbols.getCategory();
        prob = collectRhsSymbols.getProb();
        lhs = collectRhsSymbols.getLhs();
        rhs = collectRhsSymbols.getRhs();
        break;
      }
    }
  }

  public List<PcfgProductionRule> getProductionRules() {
    return productionRules;
  }

  public void setProductionRules(String[][] rules) {
    for (String[] rule : rules) {
      this.productionRules.add(new PcfgProductionRule(rule));
    }
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
   * Creates a PcfgProductionRule from the string representation and adds it to
   * its set of rules.
   */
  public void addProductionRule(String rule) throws ParseException {
    this.productionRules.add(new PcfgProductionRule(rule));
  }

}
