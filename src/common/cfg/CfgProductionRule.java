package common.cfg;

public class CfgProductionRule {
  String lhs;
  String[] rhs;
  
  CfgProductionRule(String[] rule) {
	  this.lhs = rule[0];
	  this.rhs = rule[1].split(" ");
  }
  
  CfgProductionRule(String lhs, String rhs) {
	  this.lhs = lhs;
	  this.rhs = rhs.split(" ");
  }
  
  public String getLhs() {
	 return this.lhs;
  }
  
  public String[] getRhs(){
	  return this.rhs;
  }
}
