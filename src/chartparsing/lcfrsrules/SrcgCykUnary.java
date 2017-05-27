package chartparsing.lcfrsrules;

import java.util.List;

import chartparsing.AbstractDynamicDeductionRule;
import common.Item;
import common.lcfrs.Clause;

/**
 * Similar to the Unary rule in extended CYK for CFG. If there is a chain rule and an item for the rhs, get an lhs item with the same span.
 */
public class SrcgCykUnary extends AbstractDynamicDeductionRule {
  
  private final Clause clause;
  private final String[] wsplit;
  
  public SrcgCykUnary(Clause clause, String[] wsplit) {
    this.name = "Unary";
    this.antneeded = 1;
    this.clause = clause;
    this.wsplit = wsplit;
  }

  @Override public List<Item> getConsequences() {
    if (antecedences.size() == antneeded) {
      String[] itemform = antecedences.get(0).getItemform();
      String nt = itemform[0];
      // rest is vector
      
      if (nt.equals(clause.getRhs().get(0).getNonterminal())) {
        // wir haben V rechts und V + T links und Vektoren für die V
        // Argumente können aus mehreren V unt T bestehe
        // für jedes Argument, bilde Vektorenliste, belege V mit bekannten Vektoren,
        // T sind jeweils + oder -1.
        // für jedes T, check ob T = wsplit an der Stelle
        // für jedes V, Grenzen müssen zusammenpassen.
        
        // erstelle neues Item aus clause lhs nt und einem vektor pro Argument,
        // gebildet ähnlich wie das im Converter für Scan zusammengebstelt wird.
        // Ich glaube, ich hab das für Earley schon mehrmals gemacht??
      }
      
    }
    return this.consequences;
  }

}
