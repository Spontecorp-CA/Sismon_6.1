package com.sismon.controller;

import com.sismon.jpamanager.TipoPozoManager;
import com.sismon.model.TipoPozo;
import com.sismon.vista.utilities.SismonLog;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Clase soporte que permite crear los tipos de pozo a ser usados por la 
 * aplicación. Los pozos los carga de un archivo usado para tal fin y el
 * cual se encuentra embuido dentro de la misma. Esta operación se realiza
 * ezclusivamente al arrancar la aplicación por primera vez.
 * 
 * @author jgcastillo
 */
public class TipoPozoAPI {
    private TipoPozo tipoPozo;
    private static final SismonLog SISMONLOG = SismonLog.getInstance();
    
    public TipoPozoAPI() {
        creaDataTipoPozo();
    }
    
    private void creaDataTipoPozo() {
        TipoPozoManager tipoPozoManager = new TipoPozoManager();
        List<TipoPozo> tipoPozoList = tipoPozoManager.findAll();

        if (tipoPozoList.isEmpty()) {
            try {
                InputStream fis = getClass().getClassLoader().getResourceAsStream(
                        "resources/files/POZO_TIPOS_FAJA_V23.xlsx");
                XSSFWorkbook workbook = new XSSFWorkbook(fis);
                XSSFSheet sheet = workbook.getSheetAt(0);

                Iterator<Row> rows = sheet.rowIterator();

                List<Object[]> data = new ArrayList<>();
                Cell cell;
                //Row row;
                while (rows.hasNext()) {
                    Row row = rows.next();
                    if (row.getRowNum() == 0) {
                        continue;
                    }

                    Object[] cellsData = new Object[44];
                    for (int colIndex = 0; colIndex < cellsData.length; colIndex++) {
                        cell = row.getCell(colIndex);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_STRING:
                                    cellsData[colIndex] = cell.getStringCellValue();
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
                                    cellsData[colIndex] = cell.getNumericCellValue();
                                    break;
                            }
                        }
                    }

                    data.add(cellsData);
                }

                for (Object[] item : data) {
                    tipoPozo = new TipoPozo();
                    tipoPozo.setCodigo(item[0].toString());
                    tipoPozo.setImagen(getPozoImage(item[0].toString()));
                    tipoPozo.setTipo(item[1].toString());
                    tipoPozoManager.create(tipoPozo);

                }
                fis.close();

            } catch (IOException e) {
                SISMONLOG.logger.log(Level.SEVERE, "Error", e);
            } catch (Exception e) {
                SISMONLOG.logger.log(Level.SEVERE, "Error", e);
            }
        }
    }
    
    /**
     * Este método devuelve una imagen del tipo de pozo seleccionado. En caso 
     * que en futuros usos del sistema se desee obtener esta imagen.
     * 
     * @param imageName
     * @return 
     */
    public String getPozoImage(String imageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("resources/images/");
        sb.append(imageName);
        sb.append(".png");
        return sb.toString();
    }
}
