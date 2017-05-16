package common.tag;

import java.text.ParseException;

public class VertexTest {
  public static void main(String[] args) throws ParseException {
    Vertex v1 = new Vertex("S");
    v1.setGornaddress("");
    if (v1.getGornAddressOfPotentialRightSibling() == null) {
      System.out.println("sibling test 1 ok");
    } else {
      System.out.println("sibling test 1 fail");
    }
    Vertex v2 = new Vertex("A");
    v2.setGornaddress(".1");
    if (v2.getGornAddressOfPotentialRightSibling().equals(".2")) {
      System.out.println("sibling test 2 ok");
    } else {
      System.out.println("sibling test 2 fail");
    }
    Vertex v3 = new Vertex("B");
    v3.setGornaddress(".1.1");
    if (v3.getGornAddressOfPotentialRightSibling().equals(".1.2")) {
      System.out.println("sibling test 3 ok");
    } else {
      System.out.println("sibling test 3 fail");
    }
  }

}
