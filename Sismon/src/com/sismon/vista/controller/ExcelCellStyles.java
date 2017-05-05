package com.sismon.vista.controller;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author jgcastillo
 */
public class ExcelCellStyles {

    private final XSSFCreationHelper createHelper;
    private final XSSFWorkbook wb;
    private final SXSSFWorkbook swb;
    private XSSFCellStyle cellStyle;
    private Font font;
    
    public ExcelCellStyles(XSSFWorkbook wb){
        this.wb = wb;
        this.swb = null;
        this.createHelper = wb.getCreationHelper();
    }
    
    public ExcelCellStyles(SXSSFWorkbook swb) {
        this.swb = swb;
        this.wb = null;
        this.createHelper = (XSSFCreationHelper) swb.getCreationHelper();
    }
    
    public XSSFCellStyle getDateCellStyle(){
        if(wb != null){
            cellStyle = wb.createCellStyle();
        }
        
        if(swb != null){
            cellStyle = (XSSFCellStyle) swb.createCellStyle();
        }
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        return cellStyle;
    }
    
    public XSSFCellStyle getNoDecimalsStyle(){
        if (wb != null) {
            cellStyle = wb.createCellStyle();
        }

        if (swb != null) {
            cellStyle = (XSSFCellStyle) swb.createCellStyle();
        }
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        return cellStyle;
    }
    
    public XSSFCellStyle getTwoDecimalsStyle(){
        if (wb != null) {
            cellStyle = wb.createCellStyle();
        }

        if (swb != null) {
            cellStyle = (XSSFCellStyle) swb.createCellStyle();
        }
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.00"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        return cellStyle;
    }
    
    public XSSFCellStyle getFourDecimalsStyle() {
        if (wb != null) {
            cellStyle = wb.createCellStyle();
        }

        if (swb != null) {
            cellStyle = (XSSFCellStyle) swb.createCellStyle();
        }
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        return cellStyle;
    }
}
