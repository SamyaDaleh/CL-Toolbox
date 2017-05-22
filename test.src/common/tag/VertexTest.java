package common.tag;

import java.text.ParseException;

import static org.junit.Assert.assertTrue;

public class VertexTest {
  public void testSiblings() throws ParseException {
    Vertex v1 = new Vertex("S");
    v1.setGornaddress("");
    assertTrue(v1.getGornAddressOfPotentialRightSibling() == null);

    Vertex v2 = new Vertex("A");
    v2.setGornaddress(".1");
    assertTrue(v2.getGornAddressOfPotentialRightSibling().equals(".2"));

    Vertex v3 = new Vertex("B");
    v3.setGornaddress(".1.1");
    assertTrue(v3.getGornAddressOfPotentialRightSibling().equals(".1.2"));
  }

}
