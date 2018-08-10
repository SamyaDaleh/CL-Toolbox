package com.github.samyadaleh.cltoolbox.gui;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import com.github.samyadaleh.cltoolbox.common.GrammarParser;
import com.github.samyadaleh.cltoolbox.common.cfg.Cfg;

public class ParsingTaskSwing {
  
  /**
   * Prepares the UI fo the exercise with table, task description and rule
   * definition.
   */
  public ParsingTaskSwing(Cfg cfg, String input, String algorithm) {
    JFrame f = new JFrame();
    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    JTable table = new JTable(new String[][] {{"", "", "", ""}},
      new String[] {"Id", "Item", "Rule", "Backpointers"});
    table.setEnabled(false);
    table.setDragEnabled(false);
    table.setCellSelectionEnabled(false);
    table.getTableHeader().setReorderingAllowed(false);
    f.getContentPane().add(new JScrollPane(table), BorderLayout.WEST);
    JTextArea textTask = new JTextArea();
    textTask.setEditable(false);
    textTask.setDragEnabled(false);
    textTask.setText("Perform " + algorithm
      + " parsing with the following input and grammar.\n" + "w = \"" + input
      + "\"\n" + cfg.toString() + "\n");
    JPanel textPart = new JPanel();
    textPart.setLayout(new BoxLayout(textPart, BoxLayout.PAGE_AXIS));
    textPart.add(textTask);
    JTextArea rules = new JTextArea();
    rules.setEditable(false);
    rules.setText("          \n" + "Axiom: _________\n               [S, 0]\n\n"
      + "               [aα, i]\nScan:   ______   wi+1 = a\n"
      + "              [α, i + 1]\n\n                [Aα, i]\n"
      + "Predict:  ______A → γ ∈ P, |γα| ≤ n − i\n                [γα, i]");
    textPart.add(rules);
    f.getContentPane().add(textPart, BorderLayout.EAST);
    f.pack();
    f.setTitle("Parsing Exercise");
    f.setVisible(true);
  }
  
  /**
   * Entry point for testing.
   * @throws IOException 
   */
  public static void main(String... args) throws IOException {
    String grammarFile = args[0];
    String w = args[1];
    String algorithm = "topdown";
    Cfg cfg = GrammarParser.parseCfgFile(grammarFile);
    new ParsingTaskSwing(cfg, w, algorithm);
  }
}
