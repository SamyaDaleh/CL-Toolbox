package gui;

import javafx.beans.property.SimpleStringProperty;

class ParsingStep {
  private final SimpleStringProperty id;
  private final SimpleStringProperty item;
  private final SimpleStringProperty rules;
  private final SimpleStringProperty backpointers;

  public ParsingStep(String[] date) {
    this.id = new SimpleStringProperty(date[0]);
    this.item = new SimpleStringProperty(date[1]);
    this.rules = new SimpleStringProperty(date[2]);
    this.backpointers = new SimpleStringProperty(date[3]);
  }

}
