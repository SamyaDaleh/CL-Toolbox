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
  }
}
