package com.github.samyadaleh.cltoolbox.gui;

import com.github.samyadaleh.cltoolbox.common.tag.Tree;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class DisplayTreeFx implements DisplayTreeInterface {

  private Tree tree;
  private final Map<String, Integer[]> nodesDrawn = new HashMap<>();
  private String[] itemForm;
  private GraphicsContext gc;
  private Stage stage;
  private JfxWindowHolder parent;
  private Canvas canvas;

  DisplayTreeFx(JfxWindowHolder parent, String[] args) throws ParseException {
    this.tree = new Tree(args[0]);
    this.parent = parent;
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
    StackPane stackPane = new StackPane();
    canvas = new Canvas(80 * tree.getWidth(), 80 * tree.getHeight());
    canvas.widthProperty().bind(stackPane.widthProperty());
    canvas.heightProperty().bind(stackPane.heightProperty());
    stackPane.getChildren().add(canvas);
    Scene scene = new Scene(stackPane);
    scene.widthProperty().addListener(observable -> draw());
    scene.heightProperty().addListener(observable -> draw());
    stage.setScene(scene);
    stage.setWidth(80 * tree.getWidth());
    stage.setHeight(80 * tree.getHeight());
    stage.setX(X);
    stage.setY(Y);
    stage.setOnCloseRequest(e -> parent.close());
    draw();
    stage.show();
  }

  private void draw() {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    paint(gc);
  }

  /**
   * Initiates the drawing of the tree.
   */
  private void paint(GraphicsContext gc) {
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

  @Override public void clearRect(int width, int height) {
    gc.clearRect(0, 0, width, height);

  }

  @Override public int getWidth() {
    return (int) gc.getCanvas().getWidth();
  }

  @Override public int getHeight() {
    return (int) gc.getCanvas().getHeight();
  }

  void dispose() {
    stage.hide();
  }

  void setLocation(double x, double y) {
    stage.setX(x);
    stage.setY(y);
  }

  void close() {
    stage.close();
  }
}
