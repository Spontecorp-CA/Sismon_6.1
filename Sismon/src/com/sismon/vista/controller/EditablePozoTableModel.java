package com.sismon.vista.controller;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jgcastillo
 */
public class EditablePozoTableModel extends AbstractTableModel{

    private String[] columnTitles;
    private Object[][] dataEntries;
    private int rowCount;
    
    private final Object[] longValues = {"UbicacionXXXX", "Numero", "XXXXXXXXXXXXXXX", 
        "1234567890123456789012345", "EstatigraficoXXXX", "XXXXXXXXXXXXXXX", "XXXXXXXXXXXXXXX",
        12345.34, 23.12, 999, 999.99, 99.99, 9999, 999.99, 99.99, 9999.99, 999.99, 
        123.99, 123123456789.00, 999.99, 999.99, 999.99, 99, 9999.99, 99, 9999.99,
        99, 9999.99, 99, 9999.99, 99, 9999.99, 99, 9999.99};

    public EditablePozoTableModel(Object[][] dataEntries, String[] columnTitles) {
        this.columnTitles = columnTitles;
        this.dataEntries = dataEntries;
    }
    
    public Object[] getLongValues(){
        return longValues;
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
        return (column > 1);
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        dataEntries[row][column] = value;
        fireTableCellUpdated(row, column);
    }

    public Object[][] getDataEntries() {
        return dataEntries;
    }

    @Override
    public void fireTableCellUpdated(int row, int column) {
        super.fireTableCellUpdated(row, column); 
    }
    
    
}
