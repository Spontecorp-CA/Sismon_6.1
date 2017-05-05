package com.sismon.vista.utilities;

import java.awt.Color;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author jgcastillo
 */
public final class ExcelStyles {

    public static XSSFCellStyle fechaLarga(XSSFWorkbook wb){
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        return cellStyle;
    }
    
    public static XSSFCellStyle mesYearLargoAzul(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        return cellStyle;
    }
    
    public static XSSFCellStyle mesYearCorto(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setColor(IndexedColors.BLUE.getIndex());
        font.setFontHeightInPoints((short) 12);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMMM-yyyy"));
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle mesYearCortoColorAgua(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(204, 255, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle mesYearCortoAmarillo(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle sinDecimalesCentradoClaro(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle sinDecimalesCentradoAmarillo(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle sinDecimalesOculto(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle sinDecimales(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        return cellStyle;
    }
    
    public static XSSFCellStyle sinDecimalesBold(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle unDecimal(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        return cellStyle;
    }
    
    public static XSSFCellStyle unDecimalBold(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle unDecimalAzulMarino(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(153, 204, 255)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle dosDecimales(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.00"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        return cellStyle;
    }
    
    public static XSSFCellStyle dosDecimalesBold(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.00"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle dosDecimalesAzul(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.BLUE.getIndex());
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.00"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle tresDecimales(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        return cellStyle;
    }
    
    public static XSSFCellStyle tresDecimalesBold(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle tresDecimalesBoldAmarillo(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle tresDecimalesNegativo(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("[Red](#,##0.000)"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        return cellStyle;
    }
    
    public static XSSFCellStyle cuatroDecimales(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        return cellStyle;
    }
    
    public static XSSFCellStyle porcentajeSinDecimal(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0%"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        return cellStyle;
    }
    
    public static XSSFCellStyle porcentaje(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00%"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        return cellStyle;
    }
    
    public static XSSFCellStyle porcentajeAzul(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.BLUE.getIndex());
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00%"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle AlineadoAlCentroAzul(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.BLUE.getIndex());
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle AlineadoAlCentroColorAgua(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(204, 255, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        return cellStyle;
    }
    
    public static XSSFCellStyle AlineadoIzquierda(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        return cellStyle;
    }
    
    public static XSSFCellStyle AlineadoDerecha(XSSFWorkbook wb) {
        XSSFCreationHelper createHelper = wb.getCreationHelper();
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        return cellStyle;
    }
}
