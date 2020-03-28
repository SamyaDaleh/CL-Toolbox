package com.github.samyadaleh.cltoolbox.common.lcfrs;

import com.github.samyadaleh.cltoolbox.common.AbstractNTSGrammar;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;
import com.github.samyadaleh.cltoolbox.common.cfg.CfgProductionRule;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.Binarization;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.EmptyProductions;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.Order;
import com.github.samyadaleh.cltoolbox.common.lcfrs.util.UselessRules;
import com.github.samyadaleh.cltoolbox.common.parser.inner.InnerSrcgGrammarParser;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

import java.io.BufferedReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a sRCG - simple Range Concatenation Grammar.
 */
public class Srcg extends AbstractNTSGrammar {
  private String[] variables;
  private final List<Clause> clauses = new ArrayList<>();

  /**
   * Converts a CFG to a sRCG with dimension = 1.
   */
  public Srcg(Cfg cfg) throws ParseException {
    this.setNonterminals(cfg.getNonterminals());
    this.setTerminals(cfg.getTerminals());
    this.setStartSymbol(cfg.getStartSymbol());
    ArrayList<String> newVariables = new ArrayList<>();
    for (CfgProductionRule rule : cfg.getProductionRules()) {
      StringBuilder lhs = new StringBuilder();
      StringBuilder rhs = new StringBuilder();
      int i = 0;
      lhs.append(rule.getLhs()).append("(");
      for (String rhsSym : rule.getRhs()) {
        if (rhsSym.length() > 0) {
          if (cfg.terminalsContain(rhsSym)) {
            lhs.append(" ").append(rhsSym);
          } else {
            i++;
            String newVar = "X" + i;
            while (cfg.nonterminalsContain(newVar)) {
              i++;
              newVar = "X" + i;
            }
            if (!newVariables.contains(newVar)) {
              newVariables.add(newVar);
            }
            lhs.append(" ").append(newVar);
            rhs.append(rhsSym).append("(").append(newVar).append(")");
          }
        } else {
          rhs.append("ε");
          lhs.append("ε");
        }
      }
      lhs.append(")");
      this.addClause(lhs.toString(), rhs.toString());
    }
    this.variables = newVariables.toArray(new String[0]);
  }

  /**
   * Converts a TAG to a sRCG. With auxiliary trees dimension will be 2, else 1.
   */
  public Srcg(Tag tag) {
    this.setNonterminals(tag.getTreeNames().toArray(new String[0]));
    this.setTerminals(tag.getTerminals());
    List<String> newVariables = new ArrayList<>();
    addStartSymbolAndMayGenerateStartClauses(tag, newVariables);
    List<Vertex> adjunctionVertexStack = new ArrayList<>();
    List<List<StringBuilder>> adjunctionPredicateStack = new ArrayList<>();
    for (String treeName : tag.getTreeNames()) {
      int i = 1;
      Tree tree = tag.getTree(treeName);
      List<StringBuilder> clauseStrings = new ArrayList<>();
      clauseStrings.add(new StringBuilder());
      clauseStrings.get(0).append(treeName).append("(");
      List<StringBuilder> rhsStrings = new ArrayList<>();
      rhsStrings.add(new StringBuilder());
      for (Vertex v : tree.getVertexes()) {
        if (tag.terminalsContain(v.getLabel())) {
          for (StringBuilder clauseString : clauseStrings) {
            clauseString.append(" ").append(v.getLabel());
          }
        } else {
          i = handleSecondHalfOfAdjunctionNode(tag, newVariables, clauseStrings,
              rhsStrings, i, adjunctionVertexStack, adjunctionPredicateStack, v,
              tree);
          HandleFirstHalfOfAdjunctionNode handleFirstHalfOfAdjunctionNode =
              new HandleFirstHalfOfAdjunctionNode(tag, newVariables, treeName,
                  tree, clauseStrings, rhsStrings, i, adjunctionVertexStack,
                  adjunctionPredicateStack, v).invoke();
          clauseStrings = handleFirstHalfOfAdjunctionNode.getClauseStrings();
          rhsStrings = handleFirstHalfOfAdjunctionNode.getRhsStrings();
          i = handleFirstHalfOfAdjunctionNode.getI();

          HandleSubstitutionNode handleSubstitutionNode =
              new HandleSubstitutionNode(tag, newVariables, treeName,
                  clauseStrings, rhsStrings, i, v).invoke();
          clauseStrings = handleSubstitutionNode.getClauseStrings();
          rhsStrings = handleSubstitutionNode.getRhsStrings();
          i = handleSubstitutionNode.getI();
          if (tree.getFoot() != null && tree.getFoot() == v) {
            for (StringBuilder clauseString : clauseStrings) {
              clauseString.append(",");
            }
          }
        }
      }
      handleSecondHalfOfAdjunctionNode(tag, newVariables, clauseStrings,
          rhsStrings, i, adjunctionVertexStack, adjunctionPredicateStack, null,
          tree);

      for (StringBuilder clauseString : clauseStrings) {
        clauseString.append(")");
      }
      for (int j = 0; j < clauseStrings.size(); j++) {
        try {
          this.addClause(clauseStrings.get(j).toString(),
              rhsStrings.get(j).toString());
        } catch (ParseException e) {
          throw new RuntimeException(e);
        }
      }
    }
    this.setVariables(newVariables.toArray(new String[0]));
  }

  private int handleSecondHalfOfAdjunctionNode(Tag tag,
      List<String> newVariables, List<StringBuilder> clauseStrings,
      List<StringBuilder> rhsStrings, int i, List<Vertex> adjunctionVertexStack,
      List<List<StringBuilder>> adjunctionPredicateStack, Vertex v, Tree tree) {
    if (adjunctionVertexStack.size() > 0 && (v == null || !v.getGornAddress()
        .startsWith(adjunctionVertexStack.get(adjunctionVertexStack.size() - 1)
            .getGornAddress()) || !tree.getVertexes().contains(v))) {
      String var = null;
      List<StringBuilder> predicateList =
          adjunctionPredicateStack.get(adjunctionPredicateStack.size() - 1);
      String predicateVar =
          predicateList.get(0).substring(predicateList.get(0).indexOf("(") + 1);

      List<StringBuilder> newClauseStrings = new ArrayList<>();
      List<StringBuilder> newRhsStrings = new ArrayList<>();
      List<Integer> toDelete = new ArrayList<>();
      for (int i1 = 0; i1 < clauseStrings.size(); i1++) {
        StringBuilder clauseString = clauseStrings.get(i1);
        if (clauseString.indexOf(predicateVar) >= 0) {
          toDelete.add(i1);
          GenerateNewVar generateNewVar =
              new GenerateNewVar(tag, newVariables, i, var).invoke();
          i = generateNewVar.getI();
          var = generateNewVar.getVar();
          StringBuilder rhsString = rhsStrings.get(i1);
          for (StringBuilder aPredicateList : predicateList) {
            StringBuilder newClauseString = new StringBuilder(clauseString);
            StringBuilder newRhsString = new StringBuilder(rhsString);
            newClauseString.append(" ").append(var);
            newRhsString.append(aPredicateList).append(",").append(var)
                .append(")");
            newClauseStrings.add(newClauseString);
            newRhsStrings.add(newRhsString);
          }
        }
      }
      clauseStrings.addAll(newClauseStrings);
      for (int j = toDelete.size() - 1; j >= 0; j--) {
        clauseStrings.remove(toDelete.get(j).intValue());
        rhsStrings.remove(toDelete.get(j).intValue());
      }
      rhsStrings.addAll(newRhsStrings);
      adjunctionVertexStack.remove(adjunctionVertexStack.size() - 1);
      adjunctionPredicateStack.remove(adjunctionPredicateStack.size() - 1);
    }
    return i;
  }

  private void addStartSymbolAndMayGenerateStartClauses(Tag tag,
      List<String> newVariables) {
    try {
      int startTrees = 0;
      for (String treeName : tag.getInitialTreeNames()) {
        if (tag.getInitialTree(treeName).getRoot().getLabel()
            .equals(tag.getStartSymbol())) {
          startTrees++;
        }
      }
      String startSymbol = tag.getStartSymbol();
      if (startTrees > 1) {
        int i = 1;
        String newStartSymbol;
        do {
          newStartSymbol = startSymbol + i;
          i++;
        } while (tag.getTreeNames().contains(newStartSymbol));
        startSymbol = newStartSymbol;
        i = 1;
        String newVar;
        do {
          newVar = "X" + i;
          i++;
        } while (this.terminalsContain(newVar));
        newVariables.add(newVar);
        for (String treeName : tag.getInitialTreeNames()) {
          if (tag.getInitialTree(treeName).getRoot().getLabel()
              .equals(tag.getStartSymbol())) {
            this.addClause(newStartSymbol + "(" + newVar + ")",
                treeName + "(" + newVar + ")");
          }
        }
      }
      this.setStartSymbol(startSymbol);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public Srcg() {
    super();
  }

  public Srcg(BufferedReader in) throws ParseException {
    new InnerSrcgGrammarParser(this, in).invoke();
  }

  public void setVariables(String[] variables) {
    this.variables = variables;
  }

  public void addClause(String lhs, String rhs) throws ParseException {
    this.clauses.add(new Clause(lhs, rhs));
  }

  public void addClause(String string) throws ParseException {
    this.clauses.add(new Clause(string));
  }

  public void addClause(Clause newClause) {
    this.clauses.add(newClause);
  }

  @Override public String toString() {
    StringBuilder repr = new StringBuilder();
    repr.append("G = <N, T, V, P, S>\n").append("N = {");
    if(getNonterminals() != null) {
      repr.append(String.join(", ", getNonterminals()));
    }
    repr.append("}\n").append("T = {");
    if (getTerminals() != null) {
      repr.append(String.join(", ", getTerminals()));
    }
    repr.append("}\n").append("V = {");
    if (variables != null) {
      repr.append(String.join(", ", variables));
    }
    repr.append("}\n").append("P = {");
    for (int i = 0; i < clauses.size(); i++) {
      if (i > 0) {
        repr.append(", ");
      }
      repr.append(clauses.get(i).toString());
    }
    repr.append("}\n");
    repr.append("S = ").append(getStartSymbol()).append("\n");
    return repr.toString();
  }

  public List<Clause> getClauses() {
    return this.clauses;
  }

  public String[] getVariables() {
    return this.variables;
  }

  /**
   * Returns true if each rhs contains at most two predicates.
   */
  public boolean isBinarized() {
    return Binarization.isBinarized(this);
  }

  /**
   * Returns true if there is at least one clause that contains the empty string
   * in one of its lhs arguments, except if it is the start symbol in which case
   * it must not occur on any rhs.
   */
  public boolean hasEpsilonProductions() {
    return EmptyProductions.hasEpsilonProductions(this);
  }

  /**
   * Returns true if all variables in rhs predicates appear in the same order as
   * in the lhs predicate.
   */
  public boolean isOrdered() {
    return Order.isOrdered(this);
  }

  /**
   * Returns an equivalent sRCG where the variables are ordered in each rule for
   * each predicate. Might leave useless nonterminals behind.
   */
  public Srcg getOrderedSrcg() throws ParseException {
    return Order.getOrderedSrcg(this);
  }

  /**
   * Return an equivalent sRCG without epsilon as any lhs argument.
   * Nongenerating symbols should be removed first.
   */
  public Srcg getSrcgWithoutEmptyProductions() throws ParseException {
    return EmptyProductions.getSrcgWithoutEmptyProductions(this);
  }

  public Srcg getBinarizedSrcg() throws ParseException {
    return Binarization.getBinarizedSrcg(this);
  }

  /**
   * Returns a sRCG equivalent to this one but with only useful rules.
   */
  public Srcg getSrcgWithoutUselessRules() {
    return UselessRules.getSrcgWithoutUselessRules(this);
  }

  private static class HandleSubstitutionNode {
    private Tag tag;
    private List<String> newVariables;
    private String treeName;
    private List<StringBuilder> clauseStrings;
    private List<StringBuilder> rhsStrings;
    private int i;
    private Vertex v;

    HandleSubstitutionNode(Tag tag, List<String> newVariables, String treeName,
        List<StringBuilder> clauseStrings, List<StringBuilder> rhsStrings,
        int i, Vertex v) {
      this.tag = tag;
      this.newVariables = newVariables;
      this.treeName = treeName;
      this.clauseStrings = clauseStrings;
      this.rhsStrings = rhsStrings;
      this.i = i;
      this.v = v;
    }

    List<StringBuilder> getClauseStrings() {
      return clauseStrings;
    }

    List<StringBuilder> getRhsStrings() {
      return rhsStrings;
    }

    public int getI() {
      return i;
    }

    public HandleSubstitutionNode invoke() {
      if (tag.isSubstitutionNode(v, treeName)) {
        String var;
        do {
          var = "X" + i;
          i++;
        } while (tag.terminalsContain(var));
        if (!newVariables.contains(var)) {
          newVariables.add(var);
        }
        for (StringBuilder clauseString : clauseStrings) {
          clauseString.append(" ").append(var);
        }
        List<StringBuilder> newClauseStrings = new ArrayList<>();
        List<StringBuilder> newRhsStrings = new ArrayList<>();
        for (String iniTreeName : tag.getInitialTreeNames()) {
          Tree iniTree = tag.getInitialTree(iniTreeName);
          if (iniTree.getRoot().getLabel().equals(v.getLabel())) {
            for (int j = 0; j < clauseStrings.size(); j++) {
              newClauseStrings.add(clauseStrings.get(j));
              StringBuilder newRhs = new StringBuilder(rhsStrings.get(j));
              newRhs.append(" ").append(iniTreeName).append("(").append(var)
                  .append(")");
              newRhsStrings.add(newRhs);
            }
          }
        }
        if (newClauseStrings.size() > 0) {
          clauseStrings = newClauseStrings;
          rhsStrings = newRhsStrings;
        }
      }
      return this;
    }
  }

  private static class HandleFirstHalfOfAdjunctionNode {
    private Tag tag;
    private List<String> newVariables;
    private String treeName;
    private Tree tree;
    private List<StringBuilder> clauseStrings;
    private List<StringBuilder> rhsStrings;
    private int i;
    private List<Vertex> adjunctionVertexStack;
    private List<List<StringBuilder>> adjunctionPredicateStack;
    private Vertex v;

    HandleFirstHalfOfAdjunctionNode(Tag tag, List<String> newVariables,
        String treeName, Tree tree, List<StringBuilder> clauseStrings,
        List<StringBuilder> rhsStrings, int i,
        List<Vertex> adjunctionVertexStack,
        List<List<StringBuilder>> adjunctionPredicateStack, Vertex v) {
      this.tag = tag;
      this.newVariables = newVariables;
      this.treeName = treeName;
      this.tree = tree;
      this.clauseStrings = clauseStrings;
      this.rhsStrings = rhsStrings;
      this.i = i;
      this.adjunctionVertexStack = adjunctionVertexStack;
      this.adjunctionPredicateStack = adjunctionPredicateStack;
      this.v = v;
    }

    List<StringBuilder> getClauseStrings() {
      return clauseStrings;
    }

    List<StringBuilder> getRhsStrings() {
      return rhsStrings;
    }

    public int getI() {
      return i;
    }

    public HandleFirstHalfOfAdjunctionNode invoke() {
      List<StringBuilder> newClauseStrings = new ArrayList<>();
      List<StringBuilder> newRhsStrings = new ArrayList<>();
      if (tree.getFoot() == null || tree.getFoot() != v) {
        if (!tree.isInNA(v.getGornAddress())) {
          List<StringBuilder> predicateList = new ArrayList<>();
          String var = null;
          boolean adjoinPosFound = false;
          for (String auxTreeName : tag.getAuxiliaryTreeNames()) {
            if (tag.isAdjoinable(auxTreeName, treeName, v.getGornAddress())) {
              StringBuilder newPredicate = new StringBuilder();
              GenerateNewVar generateNewVar =
                  new GenerateNewVar(tag, newVariables, i, var).invoke();
              i = generateNewVar.getI();
              var = generateNewVar.getVar();
              newPredicate.append(auxTreeName).append("(").append(var);
              predicateList.add(newPredicate);
              adjoinPosFound = true;
              for (int i1 = 0; i1 < clauseStrings.size(); i1++) {
                StringBuilder clauseString = clauseStrings.get(i1);
                StringBuilder newClauseString = new StringBuilder(clauseString);
                newClauseString.append(" ").append(var);
                newClauseStrings.add(newClauseString);
                newRhsStrings.add(rhsStrings.get(i1));
              }
            }
          }
          if (adjoinPosFound) {
            adjunctionPredicateStack.add(predicateList);
            adjunctionVertexStack.add(v);
          }
        }
        if (!tree.isInOA(v.getGornAddress())) {
          newClauseStrings.addAll(clauseStrings);
          newRhsStrings.addAll(rhsStrings);
        }
        clauseStrings = newClauseStrings;
        rhsStrings = newRhsStrings;
      }
      return this;
    }
  }

  private static class GenerateNewVar {
    private Tag tag;
    private List<String> newVariables;
    private int i;
    private String var;

    GenerateNewVar(Tag tag, List<String> newVariables, int i, String var) {
      this.tag = tag;
      this.newVariables = newVariables;
      this.i = i;
      this.var = var;
    }

    public int getI() {
      return i;
    }

    String getVar() {
      return var;
    }

    public GenerateNewVar invoke() {
      if (var == null) {
        do {
          var = "X" + i;
          i++;
        } while (tag.terminalsContain(var));
        if (!newVariables.contains(var)) {
          newVariables.add(var);
        }
      }
      return this;
    }
  }
}
