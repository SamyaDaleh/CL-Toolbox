package common;

import java.util.HashSet;
import java.util.Set;

public class SetUtilsTest {
	public static void main(String[] args) {
		Set<String> a = new HashSet<String>();
		a.add("a");
		a.add("b");
		
		Set<String> c = SetUtils.star(a, 2);
		if(!(c.size() == 7)) {
			System.out.println("Error, size of c is " + c.size());
		}
		
		Set<String> d = SetUtils.star(a, 3);
		if(!(d.size() == 15)) {
			System.out.println("Error, size of d is " + d.size());
		}
	}

}
