package chartparsing.lcfrsrules;

import common.lcfrs.Clause;
import common.lcfrs.Predicate;

/**
 * Functions commonly used by several sRCG Rules.
 */
public class SrcgDeductionUtils {

  /** Resume:
   * [S(X1 •X2) -> A(X1,X2), 1, <1,1>, (<0,1>)]
   * [A(a •,b) -> ε, 2, <1,1>, (<1,2>, <?,?>)]
   * Item 1 has a list of vectors for the arguments of A, in this case <0,1> for
   *  X1, the first argument.
   * Vectors for A in item 1 have to match vectors in item2.
   * In this case <0,1> has to match <1,2> (which may has a longer span, 
   * <1 ...2> but doesn't.
   * 
   * Suspend:
   *[S(•X1 X2) -> A(X1,X2), 0, <1,0>, (<?,?>)]]
   * [[A(a •,b) -> ε, 1, <1,1>, (<0,1>, <?,?>)]
   * all previously seen arguments in item1 have to match with the ones in 
   * item 2.
   * */
  public static boolean ifRhsVectorMatchesLhsVector(Clause clause1parsed,
    String[] itemform1, Predicate rhs1, int iint1, Clause clause2parsed,
    String[] itemform2) {
    boolean vectorsmatch = true;
    for (int argnum = 0; argnum < iint1-1; argnum++) {
      String[] arg = rhs1.getSymbols()[argnum];
      String[] lhssym = clause1parsed.getLhs().getSymbolsAsPlainArray();
      int argfromabspos = clause2parsed.getLhs().getAbsolutePos(argnum + 1, 0);
      String argfrom =
        itemform2[argfromabspos * 2 + 4];
      int argtoabspos = clause2parsed.getLhs().getAbsolutePos(argnum + 1,
        clause2parsed.getLhs().getSymbols()[argnum].length-1);
      try { //DEBUG
      String argto = itemform2[argtoabspos * 2 + 5];
      for (int il = 0; il < lhssym.length; il++) {
        if (lhssym[il].equals(arg[0])) {
          if (!itemform1[il * 2 + 4].equals(argfrom)
            || !itemform1[il * 2 + 5].equals(argto)) {
            vectorsmatch = false;
          }
        }
      }
      } catch (ArrayIndexOutOfBoundsException e) { //DEBUG
        System.out.println(e.getLocalizedMessage());
        return false;
      }
    }
    return vectorsmatch;
  }

  public static boolean ifRhsVectorMatchesLhsVectorResume(Clause clause1parsed,
    String[] itemform1, Predicate rhs1, int iint1, Clause clause2parsed,
    String[] itemform2) {
    boolean vectorsmatch = true;
    for (int argnum = 0; argnum < iint1; argnum++) {
      String[] arg = rhs1.getSymbols()[argnum];
      String[] lhssym = clause1parsed.getLhs().getSymbolsAsPlainArray();
      int argfromabspos = clause2parsed.getLhs().getAbsolutePos(argnum + 1, 0);
      String argfrom =
        itemform2[argfromabspos * 2 + 4];
      int argtoabspos = clause2parsed.getLhs().getAbsolutePos(argnum + 1,
        clause2parsed.getLhs().getSymbols()[argnum].length-1);
      try { //DEBUG
      String argto = itemform2[argtoabspos * 2 + 5];
      for (int il = 0; il < lhssym.length; il++) {
        if (lhssym[il].equals(arg[0])) {
          if (!itemform1[il * 2 + 4].equals(argfrom)
            || !itemform1[il * 2 + 5].equals(argto)) {
            vectorsmatch = false;
          }
        }
      }
      } catch (ArrayIndexOutOfBoundsException e) { //DEBUG
        System.out.println(e.getLocalizedMessage());
        return false;
      }
    }
    return vectorsmatch;
  }
}
