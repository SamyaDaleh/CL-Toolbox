package gui;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import common.tag.Tree;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class DisplayTreeFx implements DisplayTreeInterface {

  private Tree tree;
  private Map<String, Integer[]> nodesDrawn = new HashMap<String, Integer[]>();
  private String[] itemForm;
  private int x = 100;
  private int y = 500;
  private GraphicsContext gc;
  private Stage stage;

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
    stage = new Stage();
    stage.setTitle("Tree");
    Group root = new Group();
    Canvas canvas = new Canvas(80 * tree.getWidth(), 80 * tree.getHeight());
    GraphicsContext gc = canvas.getGraphicsContext2D();
    paint(gc);
    root.getChildren().add(canvas);
    stage.setScene(new Scene(root));
    stage.setX(this.x);
    stage.setY(this.y);
    stage.show();
  }

  /** Initiates the drawing of the tree. */
  public void paint(GraphicsContext gc) {
    this.gc = gc;
    AbstractDisplayTree.paint(this);
  }

  @Override public Tree getTree() {
    return this.tree;
  }

  @Override public String[] getItemForm() {
    return this.itemForm;
  }

  @Override public void drawText(String label, int x, int y) {
    gc.fillText(label, x, y);
  }

  @Override public void drawLine(int x1, int y1, int x2, int y2) {
    gc.strokeLine(x1, y1, x2, y2);
  }

  @Override public Map<String, Integer[]> getNodesDrawn() {
    return this.nodesDrawn;
  }

  @Override public void clearRect(int i, int j, int width, int height) {
    gc.clearRect(i, j, width, height);
    
  }

  @Override public int getWidth() {
    return (int) gc.getCanvas().getWidth();
  }

  @Override public int getHeight() {
    return (int) gc.getCanvas().getHeight();
  }

  public void dispose() {
    stage.hide();
  }
}
