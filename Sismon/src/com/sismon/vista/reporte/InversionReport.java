package com.sismon.vista.reporte;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.ParidadManager;
import com.sismon.model.Escenario;
import com.sismon.model.Paridad;
import com.sismon.model.Pozo;
import com.sismon.model.Taladro;
import com.sismon.vista.controller.ReportesController;
import com.sismon.vista.utilities.Utils;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


public class InversionReport extends SwingWorker<SXSSFWorkbook, Object[]> {

    private final ReportesController reportesController;
    private final ParidadManager paridadManager;
    private final JProgressBar progressBar;
    private final JLabel messageLabel;
    private final Escenario escenario;
    private Map<Pozo, Object[]> mapa;
    private SXSSFWorkbook workbook;
    private final String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun",
        "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
    private String moneda;
    private Paridad paridad;

    protected InversionReport(JProgressBar progressBar, JLabel messageLabel,
            Map<Pozo, Object[]> mapa, String moneda, Escenario escenario) {
        this.reportesController = new ReportesController();
        this.progressBar = progressBar;
        this.messageLabel = messageLabel;
        this.mapa = mapa;
        this.moneda = moneda;
        this.escenario = escenario;
        this.paridadManager = new ParidadManager();
    }

    @Override
    protected SXSSFWorkbook doInBackground() throws Exception {
        progressBar.setVisible(true);
        messageLabel.setText("Procesando...");
        workbook = new SXSSFWorkbook();

        paridad = paridadManager.find(Constantes.PARIDAD_ACTIVA);

        // se obtienen las fechas min y max de la data para extraer los años
        Date fechaMin = null;
        Date fechaMax = null;
        List<Object[]> fechasList = reportesController.getFechaMinMax(escenario);
        for (Object[] fechas : fechasList) {
            fechaMin = (Date) fechas[0];
            fechaMax = (Date) fechas[1];
        }

        LocalDate ldMin = Utils.parseToLocalDate(fechaMin);
        LocalDate ldMax = Utils.parseToLocalDate(fechaMax);

        String[] titulos1 = {"Bloque", "Yacimiento", "Campo", "Plan", "Macolla",
            "Taladro", "Pozo", "Inicio Sup", "Fin Conex"};

        SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet("Millones " + moneda);
        
        Row row0 = sheet.createRow(0);
        Row row = sheet.createRow(1);
        Cell cell;

        // Colocación de lo meses de cada año
        int firstYear = ldMin.getYear();
        int lastYear = ldMax.getYear();
        int monthFirstYear = ldMin.getMonthValue();
        int years = (lastYear - firstYear) + 1;
        int mesesFirstYear = 13 - monthFirstYear;

        int yearUpset = 9;
        int mesUpset = 9;
        for (int i = 0; i < years; i++) {
            cell = row0.createCell(yearUpset);
            cell.setCellValue(firstYear++);

            if (i == 0) {
                for (int m = monthFirstYear; m <= 12; m++) {
                    cell = row.createCell(mesUpset++);
                    cell.setCellValue(meses[m - 1]);
                }
                yearUpset += mesesFirstYear;
            } else {
                for (String mese : meses) {
                    cell = row.createCell(mesUpset++);
                    cell.setCellValue(mese);
                }
                yearUpset += 12;
            }

        }

//            Row row = sheet.createRow(2);
        for (int i = 0; i < titulos1.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(titulos1[i]);
        }

        CreationHelper createHelper = (CreationHelper) workbook.getCreationHelper();
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        int rownum = 2;
        firstYear = ldMin.getYear();
        for (Map.Entry<Pozo, Object[]> data : mapa.entrySet()) {
            Map<Integer, Double> excelMap = new HashMap<>();

            Pozo pozo = data.getKey();
            Object[] datos = data.getValue();

            row = sheet.createRow(rownum);
            cell = row.createCell(0);
            cell.setCellValue(pozo.getBloque());

            cell = row.createCell(1);
            cell.setCellValue(pozo.getYacimiento());

            cell = row.createCell(2);
            cell.setCellValue(pozo.getMacollaId().getCampoId().getNombre());

            cell = row.createCell(3);
            cell.setCellValue(pozo.getPlan());

            cell = row.createCell(4);
            cell.setCellValue(pozo.getMacollaId().getNombre());

            cell = row.createCell(5);
            cell.setCellValue(((Taladro) datos[0]).getNombre());

            cell = row.createCell(6);
            cell.setCellValue(pozo.getUbicacion());
            rownum++;

            // Superficial
            Date iniSup = (Date) datos[16];
            Date endSup = (Date) datos[18];
            Long diasSup = (Long) datos[17];
            Date finConexion = (Date) datos[36];
            Double montoSup;
            Double montoInter;
            Double montoProd;
            Double montoCompl;
            
            // costos
            montoSup = (Double) datos[33];
            montoInter = (Double) datos[34];
            montoProd = (Double) datos[35];
            montoCompl = (Double) datos[13];
            
//            if (moneda.equals(Constantes.BOLIVARES)) {
//                montoSup = (Double) datos[33];
//                montoInter = (Double) datos[34];
//                montoProd = (Double) datos[35];
//                montoCompl = (Double) datos[13];
//            } else {
//                montoSup = ((Double) datos[33]) / paridad.getValor();
//                montoInter = ((Double) datos[34]) / paridad.getValor();
//                montoProd = ((Double) datos[35]) / paridad.getValor();
//                montoCompl = ((Double) datos[13]) / paridad.getValor();
//            }
            cell = row.createCell(7);
            cell.setCellValue(iniSup);
            cell.setCellStyle(cellStyle);
            
            // se agrega la feca de fin de conexion
            cell = row.createCell(8);
            cell.setCellValue(finConexion);
            cell.setCellStyle(cellStyle);
            
            calculaCelda(firstYear, monthFirstYear, iniSup, endSup, diasSup,
                    montoSup, row, excelMap);

            // Intermedio
            Date iniInter = (Date) datos[19];
            Date endInter = (Date) datos[21];
            Long diasInter = (Long) datos[20];
            calculaCelda(firstYear, monthFirstYear, iniInter, endInter,
                    diasInter, montoInter, row, excelMap);

            // Productor
            Date iniProd = (Date) datos[22];
            Date endProd = (Date) datos[24];
            Long diasProd = (Long) datos[23];
            calculaCelda(firstYear, monthFirstYear, iniProd, endProd,
                    diasProd, montoProd, row, excelMap);

            // Completación
            Date iniCompl = (Date) datos[26];
            Date endCompl = (Date) datos[28];
            Long diasCompl = (Long) datos[27];
            calculaCelda(firstYear, monthFirstYear, iniCompl, endCompl,
                    diasCompl, montoCompl, row, excelMap);
        }

        return workbook;
    }

    @Override
    protected void process(List<Object[]> chunks) {
        Object[] datos = chunks.get(chunks.size() - 1);
        messageLabel.setText((String) datos[0]);
        progressBar.setValue((Integer) datos[1] * 10);
    }

    @Override
    protected void done() {
        progressBar.setVisible(true);
        messageLabel.setText("Listo!");
        super.done();
    }

    private void calculaCelda(int firstYear, int monthFirstYear, Date fechaIniFase,
            Date fechaEndFase, long diasFase, Double monto, Row row, Map<Integer, Double> excelMap) {

        LocalDate ldIniSup = Utils.parseToLocalDate(fechaIniFase);
        LocalDate ldEndSup = Utils.parseToLocalDate(fechaEndFase);
        int yearInitSup = ldIniSup.getYear();
        int yearEndSup = ldEndSup.getYear();
        int monthInitSup = ldIniSup.getMonthValue();
        int monthEndSup = ldEndSup.getMonthValue();
        Cell cell;
        double oldValue = 0.0;
        
        int desplazamiento = 9;

        // los que comienzan desde el primer año
        if (yearInitSup == firstYear) {
            if (monthFirstYear == monthInitSup) {
                if (monthInitSup == monthEndSup) {
                    cell = row.createCell((12 - monthInitSup) + desplazamiento);
                    cell.setCellValue(monto / 1000000.0);
                    excelMap.put(cell.getColumnIndex(), monto / 1000000.0);
                } else {
                    // se calcula el costo de dias de la etapa
                    //long diasFase = (Long) datos[17];
                    double costoDia = monto / diasFase;

                    // se calcula cuanto dias del siguiente mes se le coloca costo
                    int diasEndMonth = ldEndSup.getDayOfMonth();
                    long diasIniMonth = diasFase - diasEndMonth;
                    // se coloca el costo de los dias del mes de inicio
                    cell = row.createCell(12 - monthInitSup + desplazamiento);
                    if (excelMap.containsKey(cell.getColumnIndex())) {
                        oldValue = excelMap.get(cell.getColumnIndex());
                        cell.setCellValue((costoDia * diasIniMonth) / 1000000.0
                                + oldValue);
                        oldValue = 0.0;
                    } else {
                        cell.setCellValue((costoDia * diasIniMonth) / 1000000.0);
                    }
                    excelMap.put(cell.getColumnIndex(), cell.getNumericCellValue());
                    // se coloca el costo de los dias del mes de fin
                    cell = row.createCell(12 - monthInitSup + desplazamiento + 1);
                    if (excelMap.containsKey(cell.getColumnIndex())) {
                        oldValue = excelMap.get(cell.getColumnIndex());
                        cell.setCellValue((costoDia * diasEndMonth) / 1000000.0
                                + oldValue);
                        oldValue = 0.0;
                    } else {
                        cell.setCellValue((costoDia * diasEndMonth) / 1000000.0);
                    }
                    excelMap.put(cell.getColumnIndex(), cell.getNumericCellValue());
                }
            } else {
                // hay que poner aqui lo que corresponda cuando el mes de 
                // inicio sea distinto al mes de diciembre
            }
        }

        // los que comienzan después del primer año
        if (yearInitSup != firstYear) {
            if (monthInitSup == monthEndSup) {
                int cellYear = yearInitSup - firstYear - 1;
                cell = row.createCell(cellYear * 12 + monthInitSup + desplazamiento);
                if (excelMap.containsKey(cell.getColumnIndex())) {
                    oldValue = excelMap.get(cell.getColumnIndex());
                    cell.setCellValue(monto / 1000000.0 + oldValue);
                    oldValue = 0.0;
                } else {
                    cell.setCellValue(monto / 1000000.0);
                }
                excelMap.put(cell.getColumnIndex(), cell.getNumericCellValue());
            } else if (yearInitSup == yearEndSup) {
                // se calcula el costo de dias de la etapa
                //long diasSup = (Long) datos[17];
                double costoDia = monto / diasFase;

                // se calcula cuanto dias del siguiente mes se le coloca costo
                int diasEndMonth = ldEndSup.getDayOfMonth();
                long diasIniMonth = diasFase - diasEndMonth;

                int cellYear = yearInitSup - firstYear - 1;
                // se coloca el costo de los dias del mes de inicio
                cell = row.createCell(cellYear * 12 + monthInitSup + desplazamiento);
                if (excelMap.containsKey(cell.getColumnIndex())) {
                    oldValue = excelMap.get(cell.getColumnIndex());
                    cell.setCellValue((costoDia * diasIniMonth) / 1000000.0
                            + oldValue);
                    oldValue = 0.0;
                } else {
                    cell.setCellValue((costoDia * diasIniMonth) / 1000000.0);
                }
                excelMap.put(cell.getColumnIndex(), cell.getNumericCellValue());
                // se coloca el costo de los dias del mes de fin
                cell = row.createCell(cellYear * 12 + monthInitSup + desplazamiento + 1);
                if (excelMap.containsKey(cell.getColumnIndex())) {
                    oldValue = excelMap.get(cell.getColumnIndex());
                    cell.setCellValue((costoDia * diasIniMonth) / 1000000.0
                            + oldValue);
                    oldValue = 0.0;
                } else {
                    cell.setCellValue((costoDia * diasEndMonth) / 1000000.0);
                }
                excelMap.put(cell.getColumnIndex(), cell.getNumericCellValue());
            } else {
                // se calcula el costo de dias de la etapa
                //long diasSup = (Long) datos[17];
                double costoDia = monto / diasFase;

                // se calcula cuanto dias del siguiente mes se le coloca costo
                int diasEndMonth = ldEndSup.getDayOfMonth();
                long diasIniMonth = diasFase - diasEndMonth;

                //se coloca el año de inicio y el año de fin
                int cellIniYear = yearInitSup - firstYear - 1;
                int cellEndYear = yearInitSup - firstYear;
                // se coloca el costo de los dias del año de inicio
                cell = row.createCell(cellIniYear * 12 + monthInitSup + desplazamiento);
                if (excelMap.containsKey(cell.getColumnIndex())) {
                    oldValue = excelMap.get(cell.getColumnIndex());
                    cell.setCellValue((costoDia * diasIniMonth) / 1000000.0
                            + oldValue);
                    oldValue = 0.0;
                } else {
                    cell.setCellValue((costoDia * diasIniMonth) / 1000000.0);
                }
                excelMap.put(cell.getColumnIndex(), cell.getNumericCellValue());
                // se coloca el costo de los dias del año de fin
                cell = row.createCell(cellIniYear * 12 + monthInitSup + desplazamiento);
                if (excelMap.containsKey(cell.getColumnIndex())) {
                    oldValue = excelMap.get(cell.getColumnIndex());
                    cell.setCellValue((costoDia * diasIniMonth) / 1000000.0
                            + oldValue);
                    oldValue = 0.0;
                } else {
                    cell.setCellValue((costoDia * diasEndMonth) / 1000000.0);
                }
                excelMap.put(cell.getColumnIndex(), cell.getNumericCellValue());
            }
        }

    }
}
