package gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.ParseException;

import javax.swing.*;

import common.tag.Tag;

public class ParsingTraceTable {
  public static void displayTrace(String[][] rowData, String[] columnNames) {

    JFrame f = new JFrame();
    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    JTable table = new JTable(rowData, columnNames);
    table.setEnabled(false);
    f.add(new JScrollPane(table));

    f.pack();
    f.setVisible(true);
  }

  private Timer showTimer;
  private Timer disposeTimer;
  private JTable table;
  private Point hintCell;
  private DisplayTree popup;
  private Tag tag;

  public ParsingTraceTable(String[][] rowData, String[] columnNames, Tag tag) {

    JFrame f = new JFrame();
    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    table = new JTable(rowData, columnNames);
    table.setEnabled(false);
    f.add(new JScrollPane(table));

    this.tag = tag;

    f.pack();
    f.setVisible(true);

    showTimer = new Timer(200, new ShowPopupActionHandler());
    showTimer.setRepeats(false);
    showTimer.setCoalesce(true);

    disposeTimer = new Timer(5000, new DisposePopupActionHandler());
    disposeTimer.setRepeats(false);
    disposeTimer.setCoalesce(true);

    table.addMouseMotionListener(new MouseMotionAdapter() {

      @Override public void mouseMoved(MouseEvent e) {

        Point p = e.getPoint();
        int row = table.rowAtPoint(p);
        int col = table.columnAtPoint(p);

        if ((row > -1 && row < table.getRowCount())
          && (col > -1 && col < table.getColumnCount())) {
          
          if (hintCell == null || (hintCell.x != col || hintCell.y != row)) {
            hintCell = new Point(col, row);
            showTimer.restart();
          }
        }
      }
    });
  }

  protected DisplayTree getTreePopup() {
    if (popup != null) {
      popup.dispose();
      popup = null;
    }
    String value = (String) table.getValueAt(hintCell.y, hintCell.x);
    if (value.charAt(0) == '[') {
      String treename = value.substring(1, value.indexOf(','));
      try {
        popup = new DisplayTree(
          new String[] {tag.getTree(treename).toString(), value});
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    return popup;

  }

  public class ShowPopupActionHandler implements ActionListener {

    @Override public void actionPerformed(ActionEvent e) {

      if (hintCell != null) {

        disposeTimer.stop();

        DisplayTree popup = getTreePopup();
        if (popup != null) {
          popup.setVisible(false);

          Rectangle bounds = table.getCellRect(hintCell.y, hintCell.x, true);
          bounds.setSize(popup.getWidth(), popup.getHeight());
          bounds.setLocation((int) Math.round(bounds.getX() + table.getWidth()),
            (int) Math.round(bounds.getY()));

          popup.setBounds(bounds);
          popup.setAlwaysOnTop(true);
          popup.setVisible(true);

          disposeTimer.start();
        }

      }

    }
  }

  public class DisposePopupActionHandler implements ActionListener {

    @Override public void actionPerformed(ActionEvent e) {

      DisplayTree popup = getTreePopup();
      popup.setVisible(false);

    }
  }
}
