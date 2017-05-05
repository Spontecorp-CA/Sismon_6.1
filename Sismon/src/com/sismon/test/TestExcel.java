package com.sismon.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStream;
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author jgcastillo
 */
public class TestExcel {
    public static void main(String[] args) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet1 = (SXSSFSheet) workbook.createSheet("Hoja 1");
        
        Row row0 = sheet1.createRow(0);
        Cell cell0 = row0.createCell(0);
        
        cell0.setCellValue("esta es una prueba");
        File excel = null;
        try {
            excel = new File("C:\\Users\\jgcastillo\\Desktop\\test.xlsx");
            FileOutputStream out = new FileOutputStream(excel);
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            System.out.println("dio " + e.getMessage());
        }
        
        try {
            File archivo = new File("C:\\Users\\jgcastillo\\Desktop\\test.xlsx");
            FileInputStream fis = new FileInputStream(archivo);
            XSSFWorkbook wk = new XSSFWorkbook(fis);
            XSSFSheet sheet2 = wk.getSheet("Hoja 1");
            
            Row row1 = sheet2.getRow(0);
            Cell cell1 = row1.getCell(0);
            
            if("esta es una prueba".equals(cell1.getStringCellValue())){
                System.out.println("Paso");
            } else {
                System.out.println("Error");
            }
            
            XSSFSheet sheet3 = wk.createSheet("Hoja 2");
            Row row3 = sheet3.createRow(0);
            Cell cell3 = row3.createCell(0);
            cell3.setCellValue("Una prueba en otra hoja");
            
            fis.close();
            excel = new File("C:\\Users\\jgcastillo\\Desktop\\test.xlsx");
            FileOutputStream fos = new FileOutputStream(excel);
            if(wk != null && fos != null){
                wk.write(fos);
                System.out.println("La escribió");
            } else {
                System.out.println(wk.getNumberOfSheets());
                System.out.println(fos.toString());
            }
            
            fos.close();
            System.out.println("FIN");
        } catch (IOException e) {
            System.out.println("dio en el segundo catch " + e.getMessage());
        }
        
    }
}
