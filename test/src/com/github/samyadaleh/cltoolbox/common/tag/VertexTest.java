package com.github.samyadaleh.cltoolbox.common.tag;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.samyadaleh.cltoolbox.common.tag.Vertex;

public class VertexTest {
  @Test public void testSiblings() {
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
