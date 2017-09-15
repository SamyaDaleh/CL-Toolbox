package gui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.tag.Tree;
import common.tag.Vertex;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class DisplayTreeFx {

  private Tree tree;
  private Map<String, Integer[]> nodesDrawn = new HashMap<String, Integer[]>();
  private String[] itemForm;
  private int x = 100;
  private int y = 500;

  public DisplayTreeFx(String[] args) throws ParseException {
    this.tree = new Tree(args[0]);
    if (args.length > 1) {
      this.itemForm = args[1].substring(1, args[1].length() - 1).split(",");
    } else {
      itemForm = new String[] {};
    }
    displayTree();
  }

  private void displayTree() {
    Stage stage = new Stage();
    stage.setTitle("Tree");
    Group root = new Group();
    Canvas canvas = new Canvas(80 * tree.getWidth(), 80 * tree.getHeight());
    GraphicsContext gc = canvas.getGraphicsContext2D();
    paint(gc);
    root.getChildren().add(canvas);
    stage.setScene(new Scene(root));
    stage.show();
  }

  /** Initiates the drawing of the tree. */
  public void paint(GraphicsContext gc) {
    gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    switch (itemForm.length) {
    case 0:
      break;
    case 6:
      gc.fillText(itemForm[2], 30, 60);
      gc.fillText(itemForm[5], gc.getCanvas().getWidth() - 30, 60);
      break;
    case 8:
      gc.fillText(itemForm[3], 30, 60);
      gc.fillText(itemForm[6], gc.getCanvas().getWidth() - 30, 60);
      break;
    case 9:
      gc.fillText(itemForm[4], 30, 60);
      gc.fillText(itemForm[7], gc.getCanvas().getWidth() - 30, 60);
      break;
    default:
      System.err
        .println("Unexpected item length " + String.valueOf(itemForm.length));
    }
    drawSubTree(gc, tree.getNodeByGornAdress(""), 60, 0, (int) gc.getCanvas().getWidth());
    for (int i = 0; i < tree.getLeafOrder().size(); i++) {
      int index = tree.getLeafOrder().indexOf(String.valueOf(i));
      Vertex p = tree.getNodeByGornAdress(tree.getLeafGorns().get(index));
      int nodex = (int) ((i + 1) * gc.getCanvas().getWidth() / (tree.getLeafOrder().size() + 1));
      int height = (int) (gc.getCanvas().getHeight() - 50);
      gc.fillText(p.getLabel(), nodex, height);
      Integer[] xyParent = nodesDrawn.get(p.getGornAddressOfParent());
      gc.strokeLine(nodex, height - 10, xyParent[0], xyParent[1] + 10);
    }
  }

  /** Draws the root of a subtree in the middle, divides its space by the number
   * of its children's width, triggers to draw the children. */
  private void drawSubTree(GraphicsContext g, Vertex p, int height, int widthFrom,
    int widthDelta) {
    if (tree.getLeafGorns().contains(p.getGornAddress())) {
      return;
    }
    int nodeX = widthFrom + widthDelta / 2;
    String label = createTreeLabel(p);
    if (tree.getFoot() != null && tree.getFoot().equals(p)) {
      drawFootIndices(g, height, nodeX, label);
    }
    g.fillText(label, nodeX, height);
    switch (itemForm.length) {
    case 0:
      break;
    case 6:
      String gorn = itemForm[1].substring(0, itemForm[1].length() - 1);
      if (p.getGornAddress().equals(gorn)
        || (p.getGornAddress().equals("") && gorn.equals("ε"))) {
        drawTagCykDot(g, height, nodeX);
      }
      break;
    case 8:
    case 9:
      if (p.getGornAddress().equals(itemForm[1])
        || (p.getGornAddress().equals("") && itemForm[1].equals("ε"))) {
        drawTagEarleyDot(g, height, nodeX, label);
      }
      break;
    default:
      System.err
        .println("Unexpected item length " + String.valueOf(itemForm.length));
    }
    nodesDrawn.put(p.getGornAddress(), new Integer[] {nodeX, height});
    if (!p.getGornAddress().equals("")) {
      Integer[] xyParent = nodesDrawn.get(p.getGornAddressOfParent());
      g.strokeLine(nodeX, height - 10, xyParent[0], xyParent[1] + 10);
    }
    int widthSum = 0;
    List<Vertex> children = tree.getChildren(p);
    ArrayList<Integer> widths = new ArrayList<Integer>();
    for (Vertex child : children) {
      int width = tree.getWidthBelowNode(child);
      if (width == 0) {
        widthSum += 1;
        widths.add(1);
      } else {
        widthSum += width;
        widths.add(width);
      }
    }

    int drawWidth = widthFrom;
    for (int i = 0; i < children.size(); i++) {
      if (widthSum > 0) {
        int newWidthDelta = widthDelta * widths.get(i) / widthSum;
        drawSubTree(g, children.get(i),
          (int) (height + g.getCanvas().getHeight() / tree.getHeight()), drawWidth,
          newWidthDelta);
        drawWidth += newWidthDelta;
      } else {
        drawSubTree(g, children.get(i),
          (int) (height + g.getCanvas().getHeight() / tree.getHeight()), drawWidth,
          widthDelta / children.size());
        drawWidth += widthDelta / children.size();
      }
    }
  }

  private void drawTagCykDot(GraphicsContext g, int height, int nodeX) {
    char pos = itemForm[1].charAt(itemForm[1].length() - 1);
    switch (pos) {
    case '⊤':
      g.fillText("•", nodeX, height - 8);
      break;
    case '⊥':
      g.fillText("•", nodeX, height + 8);
      break;
    default:
      System.out.println("Unknown pos: " + pos);
    }
  }

  private void drawTagEarleyDot(GraphicsContext g, int height, int nodeX,
    String label) {
    int halfLabelWidth = label.length() * 8 / 2;
    switch (itemForm[2]) {
    case "la":
      g.fillText("•", nodeX - halfLabelWidth, height - 5);
      break;
    case "lb":
      g.fillText("•", nodeX - halfLabelWidth, height + 8);
      break;
    case "rb":
      g.fillText("•", nodeX + halfLabelWidth, height + 8);
      break;
    case "ra":
      g.fillText("•", nodeX + halfLabelWidth, height - 5);
      break;
    default:
      System.out.println("Unknown pos: " + itemForm[2]);
    }
  }

  private void drawFootIndices(GraphicsContext g, int height, int nodeX,
    String label) {
    int halfLabelWidth = label.length() * 10 / 2;
    if (itemForm.length == 6) {
      g.fillText(itemForm[3], nodeX - halfLabelWidth - 10, height);
      g.fillText(itemForm[4], nodeX + halfLabelWidth + 10, height);
    } else if (itemForm.length == 8) {
      g.fillText(itemForm[4], nodeX - halfLabelWidth - 10, height);
      g.fillText(itemForm[5], nodeX + halfLabelWidth + 10, height);
    } else if (itemForm.length == 9) {
      g.fillText(itemForm[5], nodeX - halfLabelWidth - 10, height);
      g.fillText(itemForm[6], nodeX + halfLabelWidth + 10, height);
    }
  }

  private String createTreeLabel(Vertex p) {
    StringBuilder label = new StringBuilder();
    label.append(p.getLabel());
    if (tree.isInOA(p.getGornAddress())) {
      label.append("_OA");
    }
    if (tree.isInNA(p.getGornAddress())) {
      label.append("_NA");
    }
    if (tree.getFoot() != null && tree.getFoot().equals(p)) {
      label.append("*");
    }
    return label.toString();
  }
}
