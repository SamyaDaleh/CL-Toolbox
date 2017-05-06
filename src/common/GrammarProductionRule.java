package common;

public class GrammarProductionRule {
  String[] lhs;
  String[] rhs;
  Double p = 0.0;
  
  GrammarProductionRule(String[] rule) {
	  this.lhs = rule[0].split(" ");
	  this.rhs = rule[1].split(" ");
	  if (rule.length == 3) {
		  this.p = Double.parseDouble(rule[2]);
	  }
  }
  
  GrammarProductionRule(String lhs, String rhs) {
	  this.lhs = lhs.split(" ");
	  this.rhs = rhs.split(" ");
  }
  
  GrammarProductionRule(String lhs, String rhs, Double p) {
	  this.lhs = lhs.split(" ");
	  this.rhs = rhs.split(" ");
	  this.p = p;
  }
  
  public String[] getLhs() {
	 return this.lhs;
  }
  
  public String[] getRhs(){
	  return this.rhs;
  }
  
  public Double getP() {
	  return this.p;
  }
}
