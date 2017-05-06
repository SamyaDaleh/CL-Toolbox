package common.cfg;

public class PcfgProductionRule {
  Double p = 0.0;
  private String lhs;
  private String[] rhs;
  
  PcfgProductionRule(String[] rule) {
	  this.lhs = rule[0];
	  this.rhs = rule[1].split(" ");
		this.p = Double.parseDouble(rule[2]);
  }
  
  PcfgProductionRule(String lhs, String rhs, Double p) {
	  this.lhs = lhs;
	  this.rhs = rhs.split(" ");
	  this.p = p;
  }
  
  public String getLhs() {
	 return this.lhs;
  }
  
  public String[] getRhs(){
	  return this.rhs;
  }
  
  public Double getP() {
	  return this.p;
  }
}
