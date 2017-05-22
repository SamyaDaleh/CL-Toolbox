package common.cfg;

public class CfgTest {
  static Cfg gen_cfgbintest()
  { 
      Cfg G = new Cfg();

      G.setTerminals(new String[]{"a", "b"});
      G.setVars(new String[]{"S"});
      G.setR(new String[][]{
          { "S", "a S b S S a S b a b" },
          { "S", "a b" }
      });
      G.setStart_var("S");

      return G;
  }
  
  public static void main(String[] args) {
   if (gen_cfgbintest().isBinarized()) {
     System.out.println("Something is wrong here. CFG can't be binarized yet.");
   }
   Cfg cfgbin = gen_cfgbintest().binarize();
   if (cfgbin.isBinarized()) {
     System.out.println("CFG successfully binarized.");
   } else{
     System.out.println("CFG binarization fail.");
   }
   
   Cfg cfgeps = new Cfg();
   cfgeps.setTerminals(new String[]{"a", "b"});
   cfgeps.setVars(new String[]{"S", "A", "B"});
   cfgeps.setR(new String[][]{
       { "A", "ε" },
       { "S", "" },
       { "S", "b A a S b" },
       { "A", "a" },
       { "A", "b B" },
       { "B", "b" }
   });
   cfgeps.setStart_var("S");
   
   Cfg epsfree = cfgeps.removeEmptyProductions();
   if (cfgeps.hasEpsilonProductions() && !epsfree.hasEpsilonProductions()) {
     System.out.println("Cfg successfully got rid of epsilon productions.");
   } else {
     System.out.println("You messed up");
   }
  }
}
