package com.github.samyadaleh.cltoolbox.chartparsing.lcfrs;

import java.util.ArrayList;

import com.github.samyadaleh.cltoolbox.common.lcfrs.Clause;
import com.github.samyadaleh.cltoolbox.common.lcfrs.Predicate;

/** Functions commonly used by several sRCG Rules. */
public class SrcgDeductionUtils {

  /** Resume: [S(X1 •X2) -> A(X1,X2), 1, <1,1>, (<0,1>)] [A(a •,b) -> ε, 2,
   * <1,1>, (<1,2>, <?,?>)] Item 1 has a list of vectors for the arguments of A,
   * in this case <0,1> for X1, the first argument. Vectors for A in item 1 have
   * to match vectors in item2. In this case <0,1> has to match <1,2> (which may
   * has a longer span, <1 ...2> but doesn't.
   * 
   * Suspend: [S(•X1 X2) -> A(X1,X2), 0, <1,0>, (<?,?>)]] [[A(a •,b) -> ε, 1,
   * <1,1>, (<0,1>, <?,?>)] all previously seen arguments in item1 have to match
   * with the ones in item 2. */
  public static boolean ifRhsVectorMatchesLhsVector(Clause clause1Parsed,
    String[] itemForm1, Predicate rhs1, int iInt1, Clause clause2Parsed,
    String[] itemform2) {
    boolean vectorsMatch = true;
    for (int argNum = 0; argNum < iInt1 - 1; argNum++) {
      String[] arg = rhs1.getSymbols()[argNum];
      String[] lhsSym = clause1Parsed.getLhs().getSymbolsAsPlainArray();
      int argFromAbsPos = clause2Parsed.getLhs().getAbsolutePos(argNum + 1, 0);
      String argFrom = itemform2[argFromAbsPos * 2 + 4];
      int argToAbsPos = clause2Parsed.getLhs().getAbsolutePos(argNum + 1,
        clause2Parsed.getLhs().getSymbols()[argNum].length - 1);
      String argTo = itemform2[argToAbsPos * 2 + 5];
      for (int il = 0; il < lhsSym.length; il++) {
        if (lhsSym[il].equals(arg[0]) && (!itemForm1[il * 2 + 4].equals(argFrom)
          || !itemForm1[il * 2 + 5].equals(argTo))) {
          vectorsMatch = false;
          break;
        }
      }
    }
    return vectorsMatch;
  }

  public static boolean ifRhsVectorMatchesLhsVectorResume(Clause clause1Parsed,
    String[] itemForm1, Predicate rhs1, int iInt1, Clause clause2Parsed,
    String[] itemform2) {
    boolean vectorsMatch = true;
    for (int argNum = 0; argNum < iInt1; argNum++) {
      String[] arg = rhs1.getSymbols()[argNum];
      String[] lhsSym = clause1Parsed.getLhs().getSymbolsAsPlainArray();
      int argFromAbsPos = clause2Parsed.getLhs().getAbsolutePos(argNum + 1, 0);
      String argFrom = itemform2[argFromAbsPos * 2 + 4];
      int argToAbsPos = clause2Parsed.getLhs().getAbsolutePos(argNum + 1,
        clause2Parsed.getLhs().getSymbols()[argNum].length - 1);
      String argTo = itemform2[argToAbsPos * 2 + 5];
      for (int il = 0; il < lhsSym.length; il++) {
        if (lhsSym[il].equals(arg[0])) {
          if (!itemForm1[il * 2 + 4].equals(argFrom)
            || !itemForm1[il * 2 + 5].equals(argTo)) {
            vectorsMatch = false;
          }
        }
      }
    }
    return vectorsMatch;
  }

  /** If you pass it a list of vectors/ranges and the predicate of a rule
   * (mostly a lhs) it returns ranges for the whole arguments. */
  public static <T> ArrayList<?>
    getRangesForArguments(ArrayList<T> rangeOverElements, Predicate lhs) {
    ArrayList<T> rangeOverArguments = new ArrayList<>();
    for (int i = 0; i < lhs.getDim(); i++) {
      rangeOverArguments
        .add(rangeOverElements.get(lhs.getAbsolutePos(i + 1, 0) * 2));
      if (i == lhs.getDim() - 1) {
        rangeOverArguments
          .add(rangeOverElements.get(rangeOverElements.size() - 1));
      } else {
        rangeOverArguments
          .add(rangeOverElements.get(lhs.getAbsolutePos(i + 2, 0) * 2 - 1));
      }
    }
    return rangeOverArguments;
  }

   public static void addIndicesToVectorRanges(String[] itemForm,
       ArrayList<String> vectorRanges, int index) {
    if (index == -1) {
      vectorRanges.add("?");
      vectorRanges.add("?");
    } else {
      vectorRanges.add(itemForm[(index - 1) * 2 + 1]);
      vectorRanges.add(itemForm[(index - 1) * 2 + 2]);
    }
  }
}
