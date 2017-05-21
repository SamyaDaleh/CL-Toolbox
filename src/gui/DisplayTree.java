package gui;

import java.awt.Graphics;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import common.tag.Tree;
import common.tag.Vertex;

public class DisplayTree extends JFrame {
  Tree tree;

  public DisplayTree(String treestring) throws ParseException {
    super();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.tree = new Tree(treestring);
  }

  public void paint(Graphics g) {
    Map<String, Integer> nodesdrawn = new HashMap<String, Integer>();
    int height = tree.getHeight();
    int drawheight = 60;
    int drawwidth = 0;
    for (int i = 1; i <= height; i++) {
      int width = tree.getWidthInLayer(i);
      drawwidth = 400 * 1 / (width + 1);
      for (Vertex p : tree.getVertexes()) {
        if (p.getGornaddress().split("[.]").length == i) {
          g.drawString(p.getLabel(), drawwidth, drawheight);
          nodesdrawn.put(p.getGornaddress(), drawwidth);
          if (i > 1) {
            int xparent = nodesdrawn.get(p.getGornAddressOfParent());
            g.drawLine(drawwidth, drawheight - 10, xparent,
              drawheight - 400 / height + 10);
          }
          drawwidth += 400 / width;
        }
      }
      drawheight += 400 / height;
    }
  }

  public static void main(String[] args) throws ParseException {
    DisplayTree dt = new DisplayTree(args[0]);
    dt.setSize(400, 400);
    dt.setVisible(true);
  }
}
