package com.github.samyadaleh.cltoolbox.gui;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParsingTraceTable {

  private final Timer showTimer;
  private final Timer disposeTimer;
  private final JTable table;
  private Point hintCell;
  private List<DisplayTree> popups = new ArrayList<>();
  private final Tag tag;
  private static final Logger log = LogManager.getLogger();

  public ParsingTraceTable(String[][] rowData, String[] columnNames, Tag tag) {
    JFrame f = new JFrame();
    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    table = new JTable(rowData, columnNames);
    table.setEnabled(false);
    table.getTableHeader().setReorderingAllowed(false);
    f.add(new JScrollPane(table));
    this.tag = tag;
    f.pack();
    f.setTitle("Parsing Trace Table");
    f.setVisible(true);
    if (tag == null) {
      showTimer = null;
      disposeTimer = null;
    } else {
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
            && (col > -1 && col < table.getColumnCount())
            && (hintCell == null || hintCell.x != col || hintCell.y != row)) {
            hintCell = new Point(col, row);
            showTimer.restart();
          }
        }
      });
    }
  }

  private List<DisplayTree> getTreePopups() {
    for( int i = popups.size()-1; i>=0; i--) {
      popups.get(i).dispose();
      popups.remove(i);
    }
    String value = (String) table.getValueAt(hintCell.y, hintCell.x);
    if (value.charAt(0) == '[') {
      String treeName = value.substring(1, value.indexOf(','));
      try {
        DisplayTree popup = new DisplayTree(
            new String[] {tag.getTree(treeName).toString(), value});
        popups.add(popup);
        popup.setVisible(false);
        Rectangle bounds = table.getCellRect(hintCell.y, hintCell.x, true);
        bounds.setSize(popup.getWidth(), popup.getHeight());
        bounds.setLocation((int) Math.round(bounds.getX() + table.getWidth()),
            (int) Math.round(MouseInfo.getPointerInfo().getLocation().getY()));
        popup.setBounds(bounds);
        popup.setAlwaysOnTop(true);
        popup.setVisible(true);
      } catch (ParseException e) {
        log.error(e.getMessage(), e);
      }
    }
    return popups;
  }

  private class ShowPopupActionHandler implements ActionListener {
    @Override public void actionPerformed(ActionEvent e) {
      if (hintCell != null) {
        assert disposeTimer != null;
        disposeTimer.stop();
        List<DisplayTree> popups = getTreePopups();
        if (popups.size() > 0) {
          disposeTimer.start();
        }
      }
    }
  }

  private class DisposePopupActionHandler implements ActionListener {
    @Override public void actionPerformed(ActionEvent e) {
      for(DisplayTree popup : getTreePopups()) {
        popup.setVisible(false);
      }
    }
  }
}
