package com.sismon.vista.utilities;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jgcastillo
 */
public class MacollasTableModel extends AbstractTableModel{

    String[] columnTitles;

    Object[][] dataEntries;

    int rowCount;

    public MacollasTableModel(String[] columnTitles, Object[][] dataEntries) {
        this.columnTitles = columnTitles;
        this.dataEntries = dataEntries;
    }

    @Override
    public int getRowCount() {
        return dataEntries.length;
    }

    @Override
    public int getColumnCount() {
        return columnTitles.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        return dataEntries[row][column];
    }

    @Override
    public String getColumnName(int column) {
        return columnTitles[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

//    @Override
//    public void setValueAt(Object value, int row, int column) {
//        dataEntries[row][column] = value;
//        fireTableCellUpdated(row, column);
//    }

    public Object[][] getDataEntries() {
        return dataEntries;
    }

    @Override
    public void fireTableCellUpdated(int row, int column) {
        super.fireTableCellUpdated(row, column); 
    }
    
    
}
