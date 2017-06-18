package common.lcfrs;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import common.cfg.Cfg;
import common.cfg.CfgProductionRule;

/** Representation of a sRCG - simple Range Concatenation Grammar. */
public class Srcg {
  private String[] nonterminals;
  private String[] terminals;
  private String[] variables;
  private String startSymbol;
  private final List<Clause> clauses = new ArrayList<Clause>();

  /** Converts a CFG to a sRCG with dimension = 1. */
  public Srcg(Cfg cfg) throws ParseException {
    this.nonterminals = cfg.getNonterminals();
    this.terminals = cfg.getTerminals();
    this.startSymbol = cfg.getStartSymbol();
    ArrayList<String> newVariables = new ArrayList<String>();
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
            String newVar = "X" + String.valueOf(i);
            while (cfg.nonterminalsContain(newVar)) {
              i++;
              newVar = "X" + String.valueOf(i);
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
    this.variables = newVariables.toArray(new String[newVariables.size()]);
  }

  public Srcg() {
    super();
  }

  public void setNonterminals(String[] nonterminals) {
    this.nonterminals = nonterminals;
  }

  public void setTerminals(String[] terminals) {
    this.terminals = terminals;
  }

  public void setVariables(String[] variables) {
    this.variables = variables;
  }

  public void setStartSymbol(String startSymbol) {
    this.startSymbol = startSymbol;
  }

  public void addClause(String lhs, String rhs) throws ParseException {
    this.clauses.add(new Clause(lhs, rhs));
  }

  public void addClause(String string) throws ParseException {
    this.clauses.add(new Clause(string));
  }

  @Override public String toString() {
    StringBuilder repr = new StringBuilder();
    repr.append("G = <N, T, V, P, S>\n");
    repr.append("N = {").append(String.join(", ", nonterminals)).append("}\n");
    repr.append("T = {").append(String.join(", ", terminals)).append("}\n");
    repr.append("V = {").append(String.join(", ", variables)).append("}\n");
    repr.append("P = {");
    for (int i = 0; i < clauses.size(); i++) {
      if (i > 0) {
        repr.append(", ");
      }
      repr.append(clauses.get(i).toString());
    }
    repr.append("}\n");
    repr.append("S = ").append(startSymbol).append("\n");
    return repr.toString();
  }

  public List<Clause> getClauses() {
    return this.clauses;
  }

  public String getStartSymbol() {
    return this.startSymbol;
  }

  public String[] getVariables() {
    return this.variables;
  }

  public String[] getTerminals() {
    return this.terminals;
  }

  public String[] getNonterminals() {
    return this.nonterminals;
  }

  /** Returns true if each rhs contains at most two predicates. */
  public boolean isBinarized() {
    boolean binarized = true;
    for (Clause clause : this.clauses) {
      if (clause.getRhs().size() > 2) {
        binarized = false;
      }
    }
    return binarized;
  }

  /** Returns true if there is at least one clause that contains the empty
   * string in one of its lhs arguments, except if it is the start symbol in
   * which case it must not occur on any rhs. */
  public boolean hasEpsilonProductions() {
    boolean hasEpsilon = false;
    for (Clause clause : this.clauses) {
      for (String[] argument : clause.getLhs().getSymbols()) {
        if (argument.length == 1 && argument[0].equals("")) {
          if (clause.getLhs().getNonterminal().equals(this.startSymbol)) {
            for (Clause clause2 : this.clauses) {
              for (Predicate rhsPred : clause2.getRhs()) {
                if (rhsPred.getNonterminal().equals(this.startSymbol)) {
                  return true;
                }
              }
            }
          } else {
            return true;
          }
        }
      }
    }
    return hasEpsilon;
  }

  /** Returns true if all variables in rhs predicates appear in the same order
   * as in the lhs predicate. */
  public boolean isOrdered() {
    for (Clause clause : this.clauses) {
      boolean clauseIsOrdered = isOrdered(clause);
      if (!clauseIsOrdered) {
        return false;
      }
    }
    return true;
  }

  /** Returns true if all predicates in rhs of clause are ordered regarding the
   * lhs predicate. */
  private boolean isOrdered(Clause clause) {
    for (Predicate rhsPred : clause.getRhs()) {
      boolean predicateIsOrdered = isOrdered(clause, rhsPred);
      if (!predicateIsOrdered) {
        return false;
      }
    }
    return true;
  }

  /** Return true if variables in rhs predicate occur in same order as in lhs
   * predicate of clause. */
  private boolean isOrdered(Clause clause, Predicate rhsPred) {
    ArrayList<Integer> posInLhs = getOrderInLhs(clause, rhsPred);
    for (int i = 1; i < posInLhs.size(); i++) {
      if (!(posInLhs.get(i - 1) < posInLhs.get(i))) {
        return false;
      }
    }
    return true;
  }

  private ArrayList<Integer> getOrderInLhs(Clause clause, Predicate rhsPred) {
    ArrayList<Integer> posInLhs = new ArrayList<Integer>();
    for (String symbol : rhsPred.getSymbolsAsPlainArray()) {
      int[] indices = clause.getLhs().find(symbol);
      int abspos = clause.getLhs().getAbsolutePos(indices[0], indices[1]);
      posInLhs.add(abspos);
    }
    return posInLhs;
  }

  /** Returns true if grammar has chain rules, that are rules with exactly one
   * rhs predicate. */
  public boolean hasChainRules() {
    for (Clause clause : this.clauses) {
      if (clause.getRhs().size() == 1) {
        return true;
      }
    }
    return false;
  }

  /** Returns an equivalent sRCG where the variables are ordered in each rule
   * for each predicate. Might leave useless nonterminals behind. */
  public Srcg getOrderedSrcg() throws ParseException {
    Srcg newSrcg = new Srcg();
    newSrcg.setTerminals(this.getTerminals());
    newSrcg.setVariables(this.getVariables());
    newSrcg.setStartSymbol(this.getStartSymbol());
    newSrcg.setNonterminals(this.getNonterminals());
    for (Clause clause : this.getClauses()) {
      newSrcg.addClause(clause.toString());
    }
    boolean change = true;
    while (change) {
      change = false;
      for (int j = 0; j < newSrcg.getClauses().size(); j++) {
        Clause clause = newSrcg.getClauses().get(j);
        for (int i = 0; i < newSrcg.getClauses().get(j).getRhs().size(); i++) {
          Predicate rhsPred = clause.getRhs().get(i);
          String oldNt = rhsPred.getNonterminal();
          if (!isOrdered(clause, rhsPred)) {
            change = true;
            ArrayList<Integer> orderVector =
              getNormalizedPos(getOrderInLhs(clause, rhsPred));
            StringBuilder newNt = new StringBuilder();
            newNt.append(oldNt).append("^<");
            for (int k = 0; k < orderVector.size(); k++) {
              if (k > 0) {
                newNt.append(',');
              }
              newNt.append(String.valueOf(orderVector.get(k)));
            }
            newNt.append(">");

            clause.getRhs().set(i,
              orderedPredicate(rhsPred, newNt.toString(), orderVector));
            if (!newSrcg.nonTerminalsContain(newNt.toString())) {
              ArrayList<String> newNts = new ArrayList<String>();
              for (String copyNt : newSrcg.getNonterminals()) {
                newNts.add(copyNt);
              }
              newNts.add(newNt.toString());
              newSrcg
                .setNonterminals(newNts.toArray(new String[newNts.size()]));
            }
            for (int l = 0; l < newSrcg.getClauses().size(); l++) {
              Clause clause2 = newSrcg.getClauses().get(l);
              if (clause2.getLhs().getNonterminal().equals(oldNt)) {
                String clause2String = clause2.toString();
                int ibrack = clause2String.indexOf('(');
                String newClause = newNt
                  + clause2String.substring(ibrack, clause2String.length());
                newSrcg.addClause(newClause);
              }
            }
          }
        }
      }
    }
    return newSrcg;
  }

  /** Returns a Predicate where the nonterminal is replaced by newNt and the
   * arguments swapped places according to orderVector. */
  private Predicate orderedPredicate(Predicate rhsPred, String newNt,
    ArrayList<Integer> orderVector) throws ParseException {
    StringBuilder newPred = new StringBuilder();
    newPred.append(newNt);
    newPred.append('(');
    for (int i = 1; i <= rhsPred.getDim(); i++) {
      for (int j = 0; j < orderVector.size(); j++) {
        if (orderVector.get(j) == i) {
          if (i > 1) {
            newPred.append(',');
          }
          newPred.append(rhsPred.getArgumentByIndex(j + 1)[0]);
        }
      }
    }
    newPred.append(')');
    return new Predicate(newPred.toString());
  }

  /** Normalizes the position vector to consecutive numbers starting with 1.
   * Example: 5,0,6 becomes 2,1,3 */
  private ArrayList<Integer> getNormalizedPos(ArrayList<Integer> posInLhs) {
    @SuppressWarnings("unchecked") ArrayList<Integer> posNormalized =
      (ArrayList<Integer>) posInLhs.clone();
    int searchInt = 0;
    int argInt = 1;
    while (argInt <= posInLhs.size()) {
      for (int i = 0; i < posInLhs.size(); i++) {
        if (posInLhs.get(i) == searchInt) {
          posNormalized.set(i, argInt);
          argInt++;
          break;
        }
      }
      searchInt++;
    }
    return posNormalized;
  }

  /** Returns true if mayNt is in set of nonterminals. */
  private boolean nonTerminalsContain(String mayNt) {
    for (String nt : this.nonterminals) {
      if (mayNt.equals(nt)) {
        return true;
      }
    }
    return false;
  }

  public Srcg getSrcgWithoutEmptyProductions() throws ParseException {
    ArrayList<String[]> epsilonCandidates = getEpsilonCandidates();
    Srcg newSrcg = new Srcg();
    newSrcg.setVariables(this.variables);
    newSrcg.setTerminals(this.terminals);
    newSrcg.setStartSymbol(this.startSymbol);
    ArrayList<String> newNts = new ArrayList<String>();
    for (String oldNt : this.getNonterminals()) {
      newNts.add(oldNt);
    }
    for (String[] candidate : epsilonCandidates) {
      if (candidate[0].equals(this.startSymbol)) {
        StringBuilder newS = new StringBuilder("S'");
        while (this.nonTerminalsContain(newS.toString())) {
          newS.append('\'');
        }
        newSrcg.setStartSymbol(newS.toString());
        newNts.add(newS.toString());
        newSrcg.addClause(newS + "(ε) -> ε");
        newSrcg.addClause(newS + "(" + this.getVariables()[0] + ") -> "
          + this.startSymbol + "(" + this.getVariables()[0] + ")");
      }
    }
    // for every clause in this.clauses add all ε-reductions of this rule to
    // clauses:
    // for all combinations of candidates that apply to rhs
    // y'know like S -> A B and there are 2 possibilities how A can have epsilon
    // and 3 possibilities
    // how B can have epsilon, consider 6 combinations
    // replace (in new clause) rhs by new nt name. if a place in jota is 0,
    // remove respective component in rhs and delete variable in lhs.
    // if jota consists of only 0, remove whole predicate from rhs.

    // if the new rule just obtained has epsilon in lhs:
    // if jota for lhs nt contains at least one 1, remove epsilon components
    // in lhs and replace nt with name from list of epsilon candidates
    // add new rule to set of rules in newSrcg
    return newSrcg;
  }

  private ArrayList<String[]> getEpsilonCandidates() {
    ArrayList<String[]> epsilonCandidates = new ArrayList<String[]>();
    for (Clause clause : this.clauses) {
      if (clause.getRhs().isEmpty()) {
        for (int i = 1; i <= clause.getLhs().getDim(); i++) {
          StringBuilder jota = new StringBuilder();
          if (clause.getLhsSymAt(i, 0).equals("")) {
            jota.append('0');
          } else {
            jota.append('1');
          }
          String[] newPair =
            new String[] {clause.getLhs().getNonterminal(), jota.toString()};
          boolean found = false;
          for (String[] oldPair : epsilonCandidates) {
            if (oldPair[0].equals(newPair[0])) {
              if (oldPair[1].equals(newPair[1])) {
                found = true;
                break;
              }
            }
          }
          if (!found) {
            epsilonCandidates.add(newPair);
          }
        }
      }
    }
    boolean changed = true;
    while (changed) {
      changed = false;
      for (Clause clause : this.clauses) {
        for (int i = 0; i < epsilonCandidates.size(); i++) {
          String[] candidate = epsilonCandidates.get(i);
          ArrayList<String> lhsVector = new ArrayList<String>();
          Predicate clauseLhs = clause.getLhs();
          for (int j = 0; j < clauseLhs.getDim(); j++) {
            String[] argument = clauseLhs.getArgumentByIndex(j);
            StringBuilder newArgument = new StringBuilder();
            for (int k = 0; k < argument.length; k++) {
              String element = argument[k];
              if (this.terminalsContain(element)) {
                newArgument.append(element);
              } else {
                for (Predicate rhsPred : clause.getRhs()) {
                  int[] indices = rhsPred.find(element);
                  if (indices[0] >= 0) {
                    if (rhsPred.getNonterminal().equals(candidate[0])
                      && candidate[1].charAt(indices[0] - 1) == '0') {
                      newArgument.append("");
                    } else {
                      newArgument.append(element);
                    }
                  }
                }
              }
            }
          }
          StringBuilder jota = new StringBuilder();
          for (int j = 0; j < lhsVector.size(); j++) {
            if (lhsVector.get(j).equals("")) {
              jota.append('0');
              changed = true;
            } else {
              jota.append('1');
            }
          }
          if (changed) {
            epsilonCandidates.add(
              new String[] {clause.getLhs().getNonterminal(), jota.toString()});
          }
        }
      }
    }
    return epsilonCandidates;
  }

  private boolean terminalsContain(String mayT) {
    for (int i = 0; i < this.terminals.length; i++) {
      if (this.terminals[i].equals(mayT)) {
        return true;
      }
    }
    return false;
  }
}
