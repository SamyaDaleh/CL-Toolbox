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

  /**
   * Return an equivalent sRCG without epsilon as any lhs argument.
   */
  public Srcg getSrcgWithoutEmptyProductions() throws ParseException {
    ArrayList<String[]> epsilonCandidates = getEpsilonCandidates();
    Srcg newSrcg = new Srcg();
    newSrcg.setVariables(this.variables);
    newSrcg.setTerminals(this.terminals);
    newSrcg.setStartSymbol(this.startSymbol);
    ArrayList<String> newNts = new ArrayList<String>();
    for (String[] candidate : epsilonCandidates) {
      newNts.add(candidate[0] + "^" + candidate[1]);
    }
    for (String[] candidate : epsilonCandidates) {
      if (candidate[0].equals(this.startSymbol) && candidate[1].length() == 1) {
        StringBuilder newS = new StringBuilder("S'");
        while (this.nonTerminalsContain(newS.toString())) {
          newS.append('\'');
        }
        newSrcg.setStartSymbol(newS.toString());
        if (!newNts.contains(newS)) {
          newNts.add(newS.toString());
        }
        if (candidate[1].equals("0")) {
          newSrcg.addClause(newS + "(ε) -> ε");
        } else {
          newSrcg.addClause(newS + "(" + this.getVariables()[0] + ") -> "
            + this.startSymbol + "^1" + "(" + this.getVariables()[0] + ")");
        }
      }
    }
    for (Clause clause : this.clauses) {
      for (ArrayList<String[]> combination : getCombinationsForRhs(
        epsilonCandidates, clause)) {
        Clause newClause = new Clause(clause.toString());
        int predDeleted = 0;
        for (int i = 0; i < newClause.getRhs().size(); i++) {
          Predicate oldRhs = newClause.getRhs().get(i);
          StringBuilder newRhsPred = new StringBuilder();
          newRhsPred.append(combination.get(i)[0]).append('^')
            .append(combination.get(i)[1]).append('(');
          String jota = combination.get(i)[1];
          boolean oneEncountered = false;
          for (int j = 0; j < jota.length(); j++) {
            if (jota.charAt(j) == '0') {
              String epsilonVariable =
                oldRhs.getSymAt(j + 1, 0);
              Predicate oldLhs = newClause.getLhs();
              Predicate newLhs =
                removeVariableFromPredicate(epsilonVariable, oldLhs);
              newClause.setLhs(newLhs);
            } else {
              if (oneEncountered) {
                newRhsPred.append(',');
              }
              newRhsPred
                .append(oldRhs.getArgumentByIndex(j + 1)[0]);
              oneEncountered = true;
            }
          }
          newRhsPred.append(')');
          Predicate newRhs = new Predicate(newRhsPred.toString());
          if (oneEncountered) {
            newClause.getRhs().set(i, newRhs);
          } else {
            newClause.getRhs().remove(i - predDeleted);
            predDeleted++;
          }
        }

        Predicate oldLhs = newClause.getLhs();
        String jotaLhs = getJotaForPredicate(oldLhs);
        if (jotaLhs.contains("1")) {
          StringBuilder newLhsPred = new StringBuilder();
          newLhsPred.append(newClause.getLhs().getNonterminal()).append('^')
            .append(jotaLhs).append('(');
          boolean argAdded = false;
          for (int i = 0; i < newClause.getLhs().getDim(); i++) {
            if (newClause.getLhs().getArgumentByIndex(i + 1)[0] != "") {
              if (argAdded) {
                newLhsPred.append(',');
              }
              argAdded = true;
              for (int j = 0; j < newClause.getLhs()
                .getArgumentByIndex(i + 1).length; j++) {
                if (j > 0) {
                  newLhsPred.append(' ');
                }
                newLhsPred.append(newClause.getLhs().getSymAt(i + 1, j));
              }
            }
          }
          newLhsPred.append(')');
          newClause.setLhs(new Predicate(newLhsPred.toString()));
          newSrcg.addClause(newClause);
        }
      }
      if (clause.getRhs().isEmpty()) {
        Clause newClause = new Clause(clause.toString());
        String jotaLhs = getJotaForPredicate(newClause.getLhs());
        if (jotaLhs.contains("1")) {
          StringBuilder newLhsPred = new StringBuilder();
          newLhsPred.append(newClause.getLhs().getNonterminal()).append('^')
            .append(jotaLhs).append('(');
          boolean argAdded = false;
          for (int i = 0; i < newClause.getLhs().getDim(); i++) {
            if (newClause.getLhs().getArgumentByIndex(i + 1)[0] != "") {
              if (argAdded) {
                newLhsPred.append(',');
              }
              argAdded = true;
              for (int j = 0; j < newClause.getLhs()
                .getArgumentByIndex(i + 1).length; j++) {
                if (j > 0) {
                  newLhsPred.append(' ');
                }
                newLhsPred.append(newClause.getLhs().getSymAt(i + 1, j));
              }
            }
          }
          newLhsPred.append(')');
          newClause.setLhs(new Predicate(newLhsPred.toString()));
          newSrcg.addClause(newClause);
        }
      }
    }
    newSrcg.setNonterminals(newNts.toArray(new String[newNts.size()]));
    return newSrcg;
  }

  private String getJotaForPredicate(Predicate oldLhs) {
    StringBuilder jotaLhs = new StringBuilder();
    for (int i = 0; i < oldLhs.getDim(); i++) {
      if (oldLhs.getArgumentByIndex(i + 1)[0].equals("")) {
        jotaLhs.append('0');
      } else {
        jotaLhs.append('1');
      }
    }
    String jotaLhsString = jotaLhs.toString();
    return jotaLhsString;
  }

  private Predicate removeVariableFromPredicate(String epsilonVariable,
    Predicate oldLhs) throws ParseException {
    StringBuilder newLhsPred = new StringBuilder();
    int[] indices = oldLhs.find(epsilonVariable);
    newLhsPred.append(oldLhs.getNonterminal())
      .append('(');
    for (int k = 0; k < oldLhs.getDim(); k++) {
      if (k > 0) {
        newLhsPred.append(',');
      }
      for (int l = 0; l < oldLhs
        .getArgumentByIndex(k + 1).length; l++) {
        if (k + 1 != indices[0] || l != indices[1]) {
          if (l > 0) {
            newLhsPred.append(' ');
          }
          newLhsPred.append(oldLhs.getSymAt(k + 1, l));
        }
      }
    }
    newLhsPred.append(')');
    return new Predicate(newLhsPred.toString());
  }

  private void addClause(Clause newClause) {
    this.clauses.add(newClause);
  }

  /**
   * Take a list of epsilonCandidates and a clause, returns a list of 
   * combinations of candidates for the rhs of the clause. Let's say there is
   * a clause like S -> A B C. A has 2 possibilities of containing epsilon,
   * B has 3, C has 0. Returns 6 lists considering all combinations which 
   * contain an empty placeholder for C.
   */
  @SuppressWarnings({"serial", "unchecked"}) private
    ArrayList<ArrayList<String[]>> getCombinationsForRhs(
      ArrayList<String[]> epsilonCandidates, Clause clause) {
    ArrayList<ArrayList<String[]>> combinations =
      new ArrayList<ArrayList<String[]>>();
    for (int i = 0; i < clause.getRhs().size(); i++) {
      if (i == 0) {
        boolean somethingAppended = false;
        for (String[] candidate : epsilonCandidates) {
          if (candidate[0].equals(clause.getRhs().get(i).getNonterminal())
            && candidate[1].length() == clause.getRhs().get(i).getDim()) {
            somethingAppended = true;
            combinations.add(new ArrayList<String[]>() {
              {
                add(candidate);
              }
            });
          }
        }
        if (!somethingAppended) {
          combinations.add(new ArrayList<String[]>() {
            {
              add(new String[] {});
            }
          });
        }
      } else {
        boolean somethingAppended = false;
        ArrayList<ArrayList<String[]>> newCombinations =
          new ArrayList<ArrayList<String[]>>();
        for (String[] candidate : epsilonCandidates) {
          if (candidate[0].equals(clause.getRhs().get(i).getNonterminal())) {
            somethingAppended = true;
            for (ArrayList<String[]> combination : combinations) {
              newCombinations.add((ArrayList<String[]>) combination.clone());
              newCombinations.get(newCombinations.size() - 1).add(candidate);
            }
          }
        }

        if (!somethingAppended) {
          for (ArrayList<String[]> combination : combinations) {
            combination.add(new String[] {});
          }
        } else {
          combinations = newCombinations;
        }
      }
    }
    return combinations;
  }

  /** Returns a list of nonterminals whose predicates can have arguments. The
   * list consists of arrays of length 2. The first entry contains the
   * respective nonterminal. The second entry contains a vector (String) of 0
   * and 1 that specifies which arguments can become empty. */
  private ArrayList<String[]> getEpsilonCandidates() {
    ArrayList<String[]> epsilonCandidates = getDirectEpsilonCandidates();
    boolean changed = true;
    while (changed) {
      changed = false;
      for (Clause clause : this.clauses) {
        for (int i = 0; i < epsilonCandidates.size(); i++) {
          String[] candidate = epsilonCandidates.get(i);
          Predicate clauseLhs = clause.getLhs();
          String[] newCandidate = getEpsilonCandidateForLhsWithoutCandidate(
            clause, candidate, clauseLhs);
          boolean found = false;
          for (String[] oldCandidate : epsilonCandidates) {
            if (oldCandidate[0].equals(newCandidate[0])
              && oldCandidate[1].equals(newCandidate[1])) {
              found = true;
              break;
            }
          }
          if (!found) {
            epsilonCandidates.add(newCandidate);
            changed = true;
          }
        }
      }
    }
    return epsilonCandidates;
  }

  private String[] getEpsilonCandidateForLhsWithoutCandidate(Clause clause,
    String[] candidate, Predicate clauseLhs) {
    ArrayList<String> lhsVector = new ArrayList<String>();
    for (int j = 0; j < clauseLhs.getDim(); j++) {
      String[] argument = clauseLhs.getArgumentByIndex(j + 1);
      StringBuilder newArgument = new StringBuilder();
      for (int k = 0; k < argument.length; k++) {
        if (k > 0) {
          newArgument.append(' ');
        }
        String element = argument[k];
        if (this.terminalsContain(element)) {
          newArgument.append(element);
        } else {
          for (Predicate rhsPred : clause.getRhs()) {
            int[] indices = rhsPred.find(element);
            if (indices[0] >= 0) {
              if (rhsPred.getNonterminal().equals(candidate[0])
                && candidate[1].length() == rhsPred.getDim()
                && candidate[1].charAt(indices[0] - 1) == '0') {
                newArgument.append("");
              } else {
                newArgument.append(element);
              }
            }
          }
        }
      }
      lhsVector.add(newArgument.toString());
    }
    StringBuilder jota = new StringBuilder();
    for (int j = 0; j < lhsVector.size(); j++) {
      if (lhsVector.get(j).equals("")) {
        jota.append('0');
      } else {
        jota.append('1');
      }
    }
    String[] newCandidate =
      new String[] {clause.getLhs().getNonterminal(), jota.toString()};
    return newCandidate;
  }

  private ArrayList<String[]> getDirectEpsilonCandidates() {
    ArrayList<String[]> epsilonCandidates = new ArrayList<String[]>();
    for (Clause clause : this.clauses) {
      if (clause.getRhs().isEmpty()) {
        String jota = getJotaForPredicate(clause.getLhs());

        String[] newPair =
          new String[] {clause.getLhs().getNonterminal(), jota};
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
    return epsilonCandidates;
  }

  /**
   * Returns true if mayT is one of the terminals.
   */
  private boolean terminalsContain(String mayT) {
    for (int i = 0; i < this.terminals.length; i++) {
      if (this.terminals[i].equals(mayT)) {
        return true;
      }
    }
    return false;
  }
}
