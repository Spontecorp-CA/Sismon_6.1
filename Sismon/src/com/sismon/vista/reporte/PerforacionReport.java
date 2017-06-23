package com.sismon.vista.reporte;

import com.sismon.controller.Constantes;
import com.sismon.model.Perforacion;
import com.sismon.vista.utilities.Utils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class PerforacionReport extends SwingWorker<SXSSFWorkbook, Void> {

    private JProgressBar progressBar;
    private final List<Perforacion> lista;
    private final String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun",
        "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
    private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    public PerforacionReport(JProgressBar progressBar, List<Perforacion> lista) {
        this.progressBar = progressBar;
        this.lista = lista;
    }

    @Override
    protected SXSSFWorkbook doInBackground() throws Exception {
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        // Se crea un libro de excel en blanco
        SXSSFWorkbook workbook = new SXSSFWorkbook();

        // Se extrae la fecha min y max del reporte para generar las hojas del reporte
        LocalDate ldMin = getLdMinMax(lista, true);
        LocalDate ldMax = getLdMinMax(lista, false);

        SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet("Conteo de Pozos");
        Row row0 = sheet.createRow(0);
        Row row = sheet.createRow(1);
        Cell cell;

        String[] titulos = {"En Progreso", "Terminados", "Conectados",
            "Aceptados", "Superficial", "Intermedio", "Productor", "Completacion",
            "Conexión", "Evaluación"};

        // Colocación de lo meses de cada año
        int firstYear = ldMin.getYear();
        int lastYear = ldMax.getYear();
        int monthFirstYear = ldMin.getMonthValue();
        int years = (lastYear - firstYear) + 1;
        int mesesFirstYear = 13 - monthFirstYear;

        int yearUpset = 1;
        int mesUpset = 1;
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

        // se colocan los titulo de la hoja
        int rowNumber = 2;
        for (String title : titulos) {
            row = sheet.createRow(rowNumber++);
            cell = row.createCell(0);
            cell.setCellValue(title);
        }

//        // se calcula los datos mes a mes y por fase
        int[][][] cuenta = getPhasesOutCount(lista, ldMax, ldMin);
        int[][][] cuentaCont = getPhasesCount(lista, ldMax, ldMin);
        
        for (int i = 0; i < cuenta.length; i++) {
            for (int j = 0; j < cuenta[i].length; j++) {
                for (int k = 0; k < cuenta[i][j].length; k++) {
                    String fase = "";
                    switch (i) {
                        case 0:
                            fase = "Super";
                            break;
                        case 1:
                            fase = "Inter";
                            break;
                        case 2:
                            fase = "Produ";
                            break;
                        case 3:
                            fase = "Compl";
                            break;
                        case 4:
                            fase = "Conex";
                            break;
                        case 5:
                            fase = "Evalu";
                            break;
                    }
                }
            }
        }

        int[][] enProgreso = new int[ldMax.getYear() - ldMin.getYear() + 1][12];

        for (int a = 0; a < enProgreso.length; a++) {
            for (int m = 0; m < enProgreso[a].length; m++) {
                enProgreso[a][m] = cuentaCont[0][a][m] + cuentaCont[1][a][m] + cuentaCont[2][a][m]
                        + cuentaCont[3][a][m] + cuentaCont[4][a][m];

            }
        }
        rowNumber = 6; // inicio de los superficiales
        int[][] terminados = getPostPerforacion(Constantes.FASE_PRODUCTOR, lista, ldMin, ldMax);
        int[][] conectados = getPostPerforacion(Constantes.FASE_CONEXION, lista, ldMin, ldMax);
        int[][] aceptados = getPostPerforacion(Constantes.FASE_EVALUACION, lista, ldMin, ldMax);

        // se colocan los pozos en progreso
        rowNumber = 2;
        row = sheet.getRow(rowNumber++);
        for (int a = 0; a < enProgreso.length; a++) {
            for (int m = 0; m < enProgreso[a].length; m++) {
                cell = row.createCell(a * 12 + m + 1, Cell.CELL_TYPE_NUMERIC);
                cell.setCellValue(enProgreso[a][m]);
            }
        }
        // los terminados
        row = sheet.getRow(rowNumber++);
        for (int a = 0; a < terminados.length; a++) {
            for (int m = 0; m < terminados[a].length; m++) {
                cell = row.createCell(a * 12 + m + 1, Cell.CELL_TYPE_NUMERIC);
                cell.setCellValue(terminados[a][m]);
            }
        }

        // los conectados
        row = sheet.getRow(rowNumber++);
        for (int a = 0; a < conectados.length; a++) {
            for (int m = 0; m < conectados[a].length; m++) {
                cell = row.createCell(a * 12 + m + 1, Cell.CELL_TYPE_NUMERIC);
                cell.setCellValue(conectados[a][m]);
            }
        }

        // los aceptados
        row = sheet.getRow(rowNumber++);
        for (int a = 0; a < aceptados.length; a++) {
            for (int m = 0; m < aceptados[a].length; m++) {
                cell = row.createCell(a * 12 + m + 1, Cell.CELL_TYPE_NUMERIC);
                cell.setCellValue(aceptados[a][m]);
            }
        }

        // las fases
        for (int f = 0; f < cuenta.length; f++) {
            row = sheet.getRow(rowNumber++);
            for (int a = 0; a < cuenta[f].length; a++) {
                for (int m = 0; m < cuenta[f][a].length; m++) {
                    cell = row.createCell(a * 12 + m + 1, Cell.CELL_TYPE_NUMERIC);
                    cell.setCellValue(cuenta[f][a][m]);
                }
            }
        }
        return workbook;
    }

    private LocalDate getLdMinMax(List<Perforacion> lista, boolean min) {
        Optional<Date> fecha;
        LocalDate ld;
        // Se extrae la fecha min y max del reporte para generar las hojas del reporte
        if (min) {
            fecha = lista.stream()
                    .map(p -> p.getFechaIn())
                    .max((p1, p2) -> p2.compareTo(p1));
            ld = Utils.parseToLocalDate(fecha.get());
        } else {
            fecha = lista.stream()
                    .map(p -> p.getFechaOut())
                    .max((p1, p2) -> p1.compareTo(p2));
            ld = Utils.parseToLocalDate(fecha.get());
        }
        return ld;
    }

    private int[][][] getPhasesCount(List<Perforacion> list, LocalDate ldMax, LocalDate ldMin) {

        String[] fases = {"Superficial", "Intermedio", "Productor", "Completación",
            "Conexión", "Evaluación"};
        // se calcula los datos mes a mes y por fase
        int[][][] cuenta = new int[fases.length][ldMax.getYear() - ldMin.getYear() + 1][12];

        for (Perforacion perf : lista) {
            LocalDate ldIn = Utils.parseToLocalDate(perf.getFechaIn());
            LocalDate ldOut = Utils.parseToLocalDate(perf.getFechaOut());

            if (perf.getFase().equals(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS)
                    || perf.getFase().equals(Constantes.FASE_MUDANZA_ENTRE_POZOS)
                    || perf.getFase().equals(Constantes.FASE_PILOTO)
                    || perf.getFase().equals(Constantes.FASE_SLANT)) {
                continue;
            }

            int f = 0; // indice de fase
            switch (perf.getFase()) {
                case Constantes.FASE_SUPERFICIAL:
                    f = 0;
                    break;
                case Constantes.FASE_INTERMEDIO:
                    f = 1;
                    break;
                case Constantes.FASE_PRODUCTOR:
                    f = 2;
                    break;
                case Constantes.FASE_COMPLETACION:
                    f = 3;
                    break;
                case Constantes.FASE_CONEXION:
                    f = 4;
                    break;
                case Constantes.FASE_EVALUACION:
                    f = 5;
                    break;
            }

            int a = (ldIn.getYear() - ldMin.getYear()); // indice de año
            int m = (ldIn.getMonthValue() - 1); // indice de mes

            cuenta[f][a][m]++;
            if (ldIn.getYear() == ldOut.getYear()) { // termina en el mismo año
                if (ldIn.getMonthValue() != ldOut.getMonthValue()) {
                    if (ldIn.getMonthValue() < 12) {
                        // agrega la cuenta a cada uno de los meses en que se encuentra
                        for (int mes = (ldIn.getMonthValue() + 1);
                                mes <= ldOut.getMonthValue();
                                mes++) {
                            m = mes - 1;
                            cuenta[f][a][m]++;
                        }
                    }
                }                
            } else { // termina al año siguiente o después
                Period period = Period.between(ldIn.plusMonths(1), ldOut);
                int meses = period.getMonths();
                ldIn = ldIn.plusMonths(1);
                while (ldIn.isBefore(ldOut) || ldIn.equals(ldOut)) {
                    a = ldIn.getYear() - ldMin.getYear();
                    m = ldIn.getMonthValue() - 1;
                    cuenta[f][a][m]++;
                    ldIn = ldIn.plusMonths(1);
                }
           }
        }

        return cuenta;
    }
    
    private int[][][] getPhasesOutCount(List<Perforacion> list, LocalDate ldMax, LocalDate ldMin) {

        String[] fases = {"Superficial", "Intermedio", "Productor", "Completación",
            "Conexión", "Evaluación"};
        // se calcula los datos mes a mes y por fase
        int[][][] cuenta = new int[fases.length][ldMax.getYear() - ldMin.getYear() + 1][12];

        for (Perforacion perf : lista) {
            LocalDate ldOut = Utils.parseToLocalDate(perf.getFechaOut());

            if (perf.getFase().equals(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS)
                    || perf.getFase().equals(Constantes.FASE_MUDANZA_ENTRE_POZOS)
                    || perf.getFase().equals(Constantes.FASE_PILOTO)
                    || perf.getFase().equals(Constantes.FASE_SLANT)) {
                continue;
            }

            int f = 0; // indice de fase
            switch (perf.getFase()) {
                case Constantes.FASE_SUPERFICIAL:
                    f = 0;
                    break;
                case Constantes.FASE_INTERMEDIO:
                    f = 1;
                    break;
                case Constantes.FASE_PRODUCTOR:
                    f = 2;
                    break;
                case Constantes.FASE_COMPLETACION:
                    f = 3;
                    break;
                case Constantes.FASE_CONEXION:
                    f = 4;
                    break;
                case Constantes.FASE_EVALUACION:
                    f = 5;
                    break;
            }

            int a = (ldOut.getYear() - ldMin.getYear()); // indice de año de salida
            int m = (ldOut.getMonthValue() - 1); // indice de mes de salida

            cuenta[f][a][m]++;
        }

        return cuenta;
    }

    private int[][] getPostPerforacion(String fase, List<Perforacion> lista,
            LocalDate ldMin, LocalDate ldMax) {

        int[][] cuenta = new int[(ldMax.getYear() - ldMin.getYear()) + 1][12];

        for (Perforacion perf : lista) {
            if (perf.getFase().equals(fase)) {
                LocalDate ldOut = Utils.parseToLocalDate(perf.getFechaOut());
                int a = ldOut.getYear() - ldMin.getYear();
                int m = ldOut.getMonthValue() - 1;
                cuenta[a][m]++;
            }
        }
        return cuenta;
    }

    @Override
    protected void done() {
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
        super.done(); //To change body of generated methods, choose Tools | Templates.
    }

}
