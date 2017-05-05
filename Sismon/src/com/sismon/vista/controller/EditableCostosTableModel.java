package com.sismon.vista.controller;

import javax.swing.table.AbstractTableModel;

public class EditableCostosTableModel extends AbstractTableModel{
    private String[] columnTitles;
    private Object[][] dataEntries;
    private int rowCount;

    private final Object[] longValues = {"Pozo12345678", "Taladro1234", "Fase1234567890",
        9999.99, 9999.99, 9999.99, 123123456789012345.99, 123123456789012345.99};

    public EditableCostosTableModel(Object[][] dataEntries, String[] columnTitles) {
        this.columnTitles = columnTitles;
        this.dataEntries = dataEntries;
    }

    public Object[] getLongValues() {
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
        boolean isEditable = false;
        if(column == 4 || column == 5 || column == 8 || column == 9){
            isEditable = true;
        }
        return isEditable;
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
