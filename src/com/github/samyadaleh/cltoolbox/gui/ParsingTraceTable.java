package com.github.samyadaleh.cltoolbox.gui;

import com.github.samyadaleh.cltoolbox.common.parser.Token;
import com.github.samyadaleh.cltoolbox.common.tag.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.ParseException;
import java.util.List;

public class ParsingTraceTable extends AbstractParsingTraceTable {

  private final Timer showTimer;
  private final Timer disposeTimer;
  private final JTable table;
  private Point hintCell;
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
          if ((row > -1 && row < table.getRowCount()) && (col > -1
              && col < table.getColumnCount()) && (hintCell == null
              || hintCell.x != col || hintCell.y != row)) {
            hintCell = new Point(col, row);
            showTimer.restart();
          }
        }
      });
    }
  }

  protected DisplayTree generateBackpointerPopup(Token token, int xCorrect,
      int yCorrect) {
    String itemForm =
        (String) table.getValueAt(Integer.parseInt(token.getString()) - 1, 1);
    int widthOfFirstThreeColumns = -30;
    widthOfFirstThreeColumns += table.getCellRect(0, 0, true).getWidth();
    widthOfFirstThreeColumns += table.getCellRect(0, 1, true).getWidth();
    widthOfFirstThreeColumns += table.getCellRect(0, 2, true).getWidth();
    return generateItemPopup((xCorrect - widthOfFirstThreeColumns), yCorrect,
        itemForm);
  }

  protected DisplayTree generateItemPopup(int xCorrect, int yCorrect,
      String itemForm) {
    String treeName = itemForm.substring(1, itemForm.indexOf(','));
    DisplayTree popup = null;
    try {
      popup = new DisplayTree(
          new String[] {tag.getTree(treeName).toString(), itemForm});
      popup.setVisible(false);
      Rectangle bounds = table.getCellRect(hintCell.y, hintCell.x, true);
      bounds.setSize(popup.getWidth(), popup.getHeight());
      bounds.setLocation(
          (int) Math.round(bounds.getX() + table.getWidth() + xCorrect),
          (int) Math.round(MouseInfo.getPointerInfo().getLocation().getY())
              + yCorrect);
      popup.setBounds(bounds);
      popup.setAlwaysOnTop(true);
      popup.setVisible(true);
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
    }
    return popup;
  }

  private class ShowPopupActionHandler implements ActionListener {
    @Override public void actionPerformed(ActionEvent e) {
      if (hintCell != null) {
        assert disposeTimer != null;
        disposeTimer.stop();
        List<DisplayTreeInterface> popups = getTreePopups();
        if (popups.size() > 0) {
          disposeTimer.start();
        }
      }
    }
  }

  private class DisposePopupActionHandler implements ActionListener {
    @Override public void actionPerformed(ActionEvent e) {
      for (DisplayTreeInterface popup : getTreePopups()) {
        ((DisplayTree) popup).setVisible(false);
      }
    }
  }

  protected String getHoverCellContent() {
    return (String) table.getValueAt(hintCell.y, hintCell.x);
  }
}
