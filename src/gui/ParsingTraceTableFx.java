package gui;

import common.tag.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ParsingTraceTableFx {

  private String[][] rowData;
  private Tag tag;

  private TableView<ParsingStep> table = new TableView<ParsingStep>();

  private String[] columnNames =
    new String[] {"Id", "Item", "Rules", "Backpointers"};

  public ParsingTraceTableFx(String[][] rowData, String[] columnNames,
    Tag tag) {
    this.rowData = rowData;
    this.columnNames = columnNames;
    this.tag = tag;
    if (tag == null) {
      displayTable();
    } else {
      displayTableWithHover();
    }
  }

  private void displayTableWithHover() {
    // TODO Auto-generated method stub
    displayTable();
  }

  private void displayTable() {
    Stage stage = new Stage();
    final VBox vbox = new VBox();
    Scene scene = new Scene(vbox);
    stage.setTitle("Parsing Trace Table");
    stage.setWidth(850);
    stage.setHeight(750);

    for (String columnName : columnNames) {
      TableColumn<ParsingStep, String> col =
        new TableColumn<ParsingStep, String>(columnName);
      col.setCellValueFactory(
        new PropertyValueFactory<ParsingStep, String>(columnName));
      col.setMinWidth(200);
      table.getColumns().add(col);
    }
    ObservableList<ParsingStep> data = FXCollections.observableArrayList();
    for (String[] date : rowData) {
      data.add(new ParsingStep(date));
    }
    table.setItems(data);
    table.setPrefHeight(stage.getHeight() - 40);
    table.setPrefWidth(stage.getWidth() - 40);

    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    vbox.getChildren().addAll(table);
    
    VBox.setVgrow(table, Priority.ALWAYS);

    stage.setScene(scene);
    stage.show();
  }

}