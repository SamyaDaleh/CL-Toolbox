package gui;

import javax.swing.*;

public class ParsingTraceTable
{
  public static void displayTrace( String[][] rowData, String[] columnNames )
  {

    JFrame f = new JFrame();
    f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

    JTable table = new JTable( rowData, columnNames );
    f.add( new JScrollPane( table ) );

    f.pack();
    f.setVisible( true );
  }
}
