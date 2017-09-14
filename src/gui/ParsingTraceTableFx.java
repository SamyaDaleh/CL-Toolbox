package gui;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ParsingTraceTableFx extends Application {

  private String[][] rowData = new String[][] {{"1", "[]", "axiom", "{}"}};

  private TableView<ParsingStep> table = new TableView<ParsingStep>();

  private String[] columnNames =
    new String[] {"Id", "Item", "Rules", "Backpointers"};

  public static void main(String[] args) {
    launch(args);
  }
  
  public void init() {
   Parameters paras = this.getParameters();
   ArrayList<String[]> allData = new ArrayList<String[]>();
   ArrayList<String> line = new ArrayList<String>();
   for (String entry : paras.getRaw()) {
     line.add(entry);
     if (line.size() == 4) {
       allData.add(line.toArray(new String[4]));
       line = new ArrayList<String>();
     }
   }
   this.rowData = allData.toArray(new String[allData.size()][]);
  }

  @Override public void start(Stage stage) {
    Scene scene = new Scene(new Group());
    stage.setTitle("Parsing Trace Table");
    stage.setWidth(450);
    stage.setHeight(500);

    for (String columnName : columnNames) {
      TableColumn<ParsingStep, String> col =
        new TableColumn<ParsingStep, String>(columnName);
      col.setCellValueFactory(
        new PropertyValueFactory<ParsingStep, String>(columnName));
      col.setMinWidth(100);
      table.getColumns().add(col);
    }
    ObservableList<ParsingStep> data = FXCollections.observableArrayList();
    for (String[] date : rowData) {
      data.add(new ParsingStep(date));
    }
    table.setItems(data);
 //   table.prefHeightProperty().bind(stage.heightProperty());
 //   table.prefWidthProperty().bind(stage.widthProperty());

    final VBox vbox = new VBox();
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    vbox.getChildren().addAll(table);

    ((Group) scene.getRoot()).getChildren().addAll(vbox);

    stage.setScene(scene);
    stage.show();
  }

}
