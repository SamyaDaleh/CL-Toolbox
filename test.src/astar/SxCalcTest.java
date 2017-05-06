package astar;

import java.util.Map;

import common.cfg.Pcfg;

public class SxCalcTest {
	
    static Pcfg gen_pcfg0()
    { 
        Pcfg G = new Pcfg();

        G.setTerminals(new String[]{"a", "b"});
        G.setVars(new String[]{ "S", "A", "B" });
        G.setR(new String[][]{
            { "S", "A B", "1" },
            { "A", "b", "0.7" },
            { "A", "a", "0.3" },
            { "B", "B B", "0.6" },
            { "B", "a", "0.4" }
        });
        G.setStart_var("S");

        return G;
    }
    
	public static void main(String[] args) {
		int errors = 0;
		Map<String,Double> insides = SxCalc.getInsides(gen_pcfg0(), 4);
		for (String in : insides.keySet()) {
			if (in.equals("in(A,1)") && !(insides.get(in) == 0.35667494393873245)
		     || in.equals("in(A,2)") && !(insides.get(in) == 1.7976931348623157E308)
		     || in.equals("in(A,3)") && !(insides.get(in) == 1.7976931348623157E308)
		     || in.equals("in(A,4)") && !(insides.get(in) == 1.7976931348623157E308)
		     || in.equals("in(B,1)") && !(insides.get(in) == 0.916290731874155)
		     || in.equals("in(B,2)") && !(insides.get(in) == 2.3434070875143007)
		     || in.equals("in(B,3)") && !(insides.get(in) == 3.7705234431544463)
		     || in.equals("in(B,4)") && !(insides.get(in) == 5.197639798794592)
		     || in.equals("in(S,1)") && !(insides.get(in) == 1.7976931348623157E308)
		     || in.equals("in(S,2)") && !(insides.get(in) == 1.2729656758128876)
		     || in.equals("in(S,3)") && !(insides.get(in) == 2.700082031453033)
		     || in.equals("in(S,4)") && !(insides.get(in) == 4.127198387093179) ) {
				System.out.println("error for " + in + ", got " + insides.get(in));
				errors++;
			}
        
        }
		Map<String,Double> outsides = SxCalc.getOutsides(insides, 4, gen_pcfg0());
		for (String out : outsides.keySet()) {
			if (out.equals("out(S,0,4,0)") && !(outsides.get(out) == 0.0)
		     || out.equals("out(A,0,4,0)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(B,0,4,0)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(S,0,3,1)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(S,1,3,0)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(A,0,3,1)") && !(outsides.get(out) == 0.916290731874155)
		     || out.equals("out(A,1,3,0)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(B,0,3,1)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(B,1,3,0)") && !(outsides.get(out) == 0.35667494393873245)
		     || out.equals("out(S,0,2,2)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(S,1,2,1)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(S,2,2,0)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(A,0,2,2)") && !(outsides.get(out) == 2.3434070875143007)
		     || out.equals("out(A,1,2,1)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(A,2,2,0)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(B,0,2,2)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(B,1,2,1)") && !(outsides.get(out) == 1.783791299578878)
		     || out.equals("out(B,2,2,0)") && !(outsides.get(out) == 1.783791299578878)
		     || out.equals("out(S,0,1,3)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(S,1,1,2)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(S,2,1,1)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(S,3,1,0)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(A,0,1,3)") && !(outsides.get(out) == 3.7705234431544463)
		     || out.equals("out(A,1,1,2)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(A,2,1,1)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(A,3,1,0)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(B,0,1,3)") && !(outsides.get(out) == 1.7976931348623157E308)
		     || out.equals("out(B,1,1,2)") && !(outsides.get(out) == 3.2109076552190237)
		     || out.equals("out(B,2,1,1)") && !(outsides.get(out) == 3.2109076552190237)
		     || out.equals("out(B,3,1,0)") && !(outsides.get(out) == 3.2109076552190237)) {
				System.out.println("error for " + out + ", got " + outsides.get(out));
				errors++;
			}
        }
		if (errors == 0) {
			System.out.println("SxCalcTest successful");
		} else {
			System.out.println("SxCalcTest returned errors");
		}
	}

}
