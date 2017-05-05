package com.sismon.vista.controller;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.jpamanager.ParidadManager;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.model.Escenario;
import com.sismon.model.Paridad;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.Year;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author jgcastillo
 */
public class HojaOperacionalController {

    private final EscenarioManager escenarioManager;
    private final PerforacionManager perforacionManager;
    private final ReportesController reportesController;

    private Map<String, XSSFCellStyle> styles;
    private Paridad paridad;

    private static final DateFormat DATEFORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final NumberFormat NUMBERFORMAT = new DecimalFormat("###,###,###,###,##0.0000");
    private static final SismonLog SISMONLOG = SismonLog.getInstance();

    public HojaOperacionalController() {
        this.escenarioManager = new EscenarioManager();
        this.perforacionManager = new PerforacionManager();
        this.reportesController = new ReportesController();

        try {
            ParidadManager paridadManager = new ParidadManager();
            paridad = paridadManager.find(Constantes.PARIDAD_ACTIVA);
        } catch (Exception e) {
            StringBuilder celdasage = new StringBuilder();
            celdasage.append("<html>");
            celdasage.append("No pudo localizar la paridad activa, ");
            celdasage.append("<br/>");
            celdasage.append("corrija y vuelva a intentarlo.");
            celdasage.append("</html>");
            JOptionPane.showMessageDialog(null, celdasage.toString());
        }
    }

    public List<Escenario> getEscenarios() {
        return escenarioManager.findAll();
    }

    public List<Perforacion> getPerforacionList(Escenario escenario) {
        return perforacionManager.findAllOrderedByPozo(escenario);
    }

    public int makeOperationalExcelSheet(File excelFile, List<Perforacion> perforacionList,
            Map<String, Object> valorManualMap) {

        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {

                Map<Pozo, Object[]> mapa = reportesController.prepareDataPerforacionXExcel2(perforacionList);
                SXSSFWorkbook workbook = reportesController.makeExcelPerforacionFile(mapa);

                // se escriben los datos al archivo
                try (FileOutputStream out = new FileOutputStream(excelFile)) {
                    workbook.write(out);
                    out.close();
                } catch (FileNotFoundException e) {
                    return Constantes.HOJAOPERACIONAL_EN_USO;
                }

                createResumen(mapa, valorManualMap, excelFile);

                return Constantes.HOJAOPERACIONAL_OK;
            }
        };
        worker.execute();

        try {
            return worker.get();
        } catch (InterruptedException | ExecutionException ex) {
            SISMONLOG.logger.log(Level.SEVERE, null, ex);
            return Constantes.HOJAOPERACIONAL_ERROR;
        }
    }

    private Map<Pozo, Object[]> prepareDataOperacional(List<Perforacion> perforacionList) {
        Map<Pozo, Object[]> mapaOperacional = new LinkedHashMap<>();

        Pozo pozo;
        Pozo pozoTemp = null;
        double costoPerfBs = 0.0;
        double costoPerfUsd = 0.0;
        double costoPerfEquiv = 0.0;
        Object[] datos = null;
        int i = 0;
        for (Perforacion perf : perforacionList) {
            pozo = perf.getPozoId();
            if (!pozo.equals(pozoTemp)) {
                datos = new Object[90];
                pozoTemp = pozo;

                datos[0] = perf.getMacollaId(); //macolla
                datos[1] = perf.getPozoId(); //pozo
                datos[2] = perf.getPozoId().getUbicacion(); //localizacion
                datos[3] = perf.getPozoId().getTipoPozo(); //tipo de pozo
                datos[4] = perf.getMacollaId().getCampoId().getNombre(); //campo
                datos[5] = perf.getPozoId().getPi(); //pi
                datos[6] = perf.getPozoId().getRgp(); //rgp
                datos[7] = perf.getPozoId().getDeclinacion(); //declinación

                switch (perf.getFase()) {
                    case Constantes.FASE_MUDANZA_ENTRE_MACOLLAS:
                        datos[8] = perf.getTaladroId(); // taladro
                        datos[9] = perf.getFase(); // fase
                        datos[10] = perf.getFechaIn(); // fechaIn
                        datos[11] = perf.getFechaOut(); // fechaOut 
                        datos[12] = perf.getDiasActivos(); // dias activo
                        datos[13] = perf.getDiasInactivos(); // dias inactivos
                        datos[14] = perf.getDias(); // dias total
                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        break;
                    case Constantes.FASE_SUPERFICIAL:
                        datos[15] = perf.getTaladroId(); // taladro
                        datos[16] = perf.getFase(); // fase
                        datos[17] = perf.getFechaIn(); // fechaIn
                        datos[18] = perf.getFechaOut(); // fechaOut 
                        datos[19] = perf.getDiasActivos(); // dias activo
                        datos[20] = perf.getDiasInactivos(); // dias inactivos
                        datos[21] = perf.getDias(); // dias total
                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        break;
                }
            } else {
                switch (perf.getFase()) {
                    case Constantes.FASE_SUPERFICIAL:
                        datos[15] = perf.getTaladroId(); // taladro
                        datos[16] = perf.getFase(); // fase
                        datos[17] = perf.getFechaIn(); // fechaIn
                        datos[18] = perf.getFechaOut(); // fechaOut 
                        datos[19] = perf.getDiasActivos(); // dias activo
                        datos[20] = perf.getDiasInactivos(); // dias inactivos
                        datos[21] = perf.getDias(); // dias total
                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        break;
                    case Constantes.FASE_SLANT:
                        datos[22] = perf.getTaladroId(); // taladro
                        datos[23] = perf.getFase(); // fase
                        datos[24] = perf.getFechaIn(); // fechaIn
                        datos[25] = perf.getFechaOut(); // fechaOut 
                        datos[26] = perf.getDiasActivos(); // dias activo
                        datos[27] = perf.getDiasInactivos(); // dias inactivos
                        datos[28] = perf.getDias(); // dias total
                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        datos[57] = costoPerfBs;  // costo perforación Bs
                        datos[58] = costoPerfUsd; // costo perforación USd
                        datos[59] = costoPerfEquiv; // costo perforación Equiv
                        costoPerfBs = 0.0;
                        costoPerfUsd = 0.0;
                        costoPerfEquiv = 0.0;
                        mapaOperacional.put(pozo, datos);
                        datos = new Object[90];
                        break;
                    case Constantes.FASE_VERTICAL:
                        datos[29] = perf.getTaladroId(); // taladro
                        datos[30] = perf.getFase(); // fase
                        datos[31] = perf.getFechaIn(); // fechaIn
                        datos[32] = perf.getFechaOut(); // fechaOut 
                        datos[33] = perf.getDiasActivos(); // dias activo
                        datos[34] = perf.getDiasInactivos(); // dias inactivos
                        datos[35] = perf.getDias(); // dias total
                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        datos[57] = costoPerfBs;  // costo perforación Bs
                        datos[58] = costoPerfUsd; // costo perforación USd
                        datos[59] = costoPerfEquiv; // costo perforación Equiv
                        costoPerfBs = 0.0;
                        costoPerfUsd = 0.0;
                        costoPerfEquiv = 0.0;
                        mapaOperacional.put(pozo, datos);
                        datos = new Object[90];
                        break;
                    case Constantes.FASE_PILOTO:
                        datos[36] = perf.getTaladroId(); // taladro
                        datos[37] = perf.getFase(); // fase
                        datos[38] = perf.getFechaIn(); // fechaIn
                        datos[39] = perf.getFechaOut(); // fechaOut
                        datos[40] = perf.getDiasActivos(); // dias activo
                        datos[41] = perf.getDiasInactivos(); // dias inactivos
                        datos[42] = perf.getDias(); // dias total
                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        datos[57] = costoPerfBs;  // costo perforación Bs
                        datos[58] = costoPerfUsd; // costo perforación USd
                        datos[59] = costoPerfEquiv; // costo perforación Equiv
                        costoPerfBs = 0.0;
                        costoPerfUsd = 0.0;
                        costoPerfEquiv = 0.0;
                        mapaOperacional.put(pozo, datos);
                        datos = new Object[90];
                        break;
                    case Constantes.FASE_INTERMEDIO:
                        datos[43] = perf.getTaladroId(); // taladro
                        datos[44] = perf.getFase(); // fase
                        datos[45] = perf.getFechaIn(); // fechaIn
                        datos[46] = perf.getFechaOut(); // fechaOut
                        datos[47] = perf.getDiasActivos(); // dias activo
                        datos[48] = perf.getDiasInactivos(); // dias inactivos
                        datos[49] = perf.getDias(); // dias total
                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        break;
                    case Constantes.FASE_PRODUCTOR:
                        datos[50] = perf.getTaladroId(); // taladro
                        datos[51] = perf.getFase(); // fase
                        datos[52] = perf.getFechaIn(); // fechaIn
                        datos[53] = perf.getFechaOut(); // fechaOut 
                        datos[54] = perf.getDiasActivos(); // dias activo
                        datos[55] = perf.getDiasInactivos(); // dias inactivos
                        datos[56] = perf.getDias(); // dias total
                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        datos[57] = costoPerfBs;  // costo perforación Bs
                        datos[58] = costoPerfUsd; // costo perforación USd
                        datos[59] = costoPerfEquiv; // costo perforación Equiv
                        costoPerfBs = 0.0;
                        costoPerfUsd = 0.0;
                        costoPerfEquiv = 0.0;
                        break;
                    case Constantes.FASE_COMPLETACION:
                        datos[60] = perf.getTaladroId(); // taladro
                        datos[61] = perf.getFase(); // fase
                        datos[62] = perf.getFechaIn(); // fechaIn
                        datos[63] = perf.getFechaOut(); // fechaOut 
                        datos[64] = perf.getDiasActivos(); // dias activo
                        datos[65] = perf.getDiasInactivos(); // dias inactivos
                        datos[66] = perf.getDias(); // dias total
                        datos[67] = perf.getBs(); // costos de completación Bs
                        datos[68] = perf.getUsd(); // costos de completación USD
                        datos[69] = perf.getEquiv(); // costos de completación Equiv
                        break;
                    case Constantes.FASE_CONEXION:
                        datos[70] = perf.getTaladroId(); // taladro
                        datos[71] = perf.getFase(); // fase
                        datos[72] = perf.getFechaIn(); // fechaIn
                        datos[73] = perf.getFechaOut(); // fechaOut
                        datos[74] = perf.getDiasActivos(); // dias activo
                        datos[75] = perf.getDiasInactivos(); // dias inactivos
                        datos[76] = perf.getDias(); // dias total
                        datos[77] = perf.getBs(); // costos de completación Bs
                        datos[78] = perf.getUsd(); // costos de completación USD
                        datos[79] = perf.getEquiv(); // costos de completación Equiv
                        break;
                    case Constantes.FASE_EVALUACION:
                        datos[80] = perf.getTaladroId(); // taladro
                        datos[81] = perf.getFase(); // fase
                        datos[82] = perf.getFechaIn(); // fechaIn
                        datos[83] = perf.getFechaOut(); // fechaOut 
                        datos[84] = perf.getDiasActivos(); // dias activo
                        datos[85] = perf.getDiasInactivos(); // dias inactivos
                        datos[86] = perf.getDias(); // dias total
                        datos[87] = perf.getBs(); // costo de evaluacion Bs
                        datos[88] = perf.getUsd(); // costo de evaluacion USD
                        datos[89] = perf.getEquiv(); // costo de evaluacion Equiv
                        mapaOperacional.put(pozo, datos);
                        datos = new Object[90];
                        break;
                }
            }
            i++;
            if (i < perforacionList.size() && perforacionList.get(i).getPozoId().equals(pozo)) {
                mapaOperacional.put(pozo, datos);
            }
        }

        return mapaOperacional;
    }

    private void createResumen(Map<Pozo, Object[]> dataOperacionalMap,
            Map<String, Object> valorManualMap,
            File excelFile) {

        XSSFWorkbook workbook = null;
        XSSFSheet sheetResumen = null;

        try (FileInputStream fis = new FileInputStream(excelFile)) {
            workbook = new XSSFWorkbook(fis);
            sheetResumen = workbook.getSheet("Resumen");
            createStyles(workbook);
        } catch (Exception e) {
        }

        ResourceBundle rb = ResourceBundle.getBundle("com.sismon.vista.controller.TitulosResumenXLS");

        // Crea los titulos del lado izquierdo
        int rowNumber = 0;
        StringBuilder builder;
        String key;
        String value;
        Row row;
        Cell cell;
        for (int fila = 0; fila < 793; fila++) {
            row = sheetResumen.createRow(rowNumber);
            for (int col = 0; col < 2; col++) {
                builder = new StringBuilder();
                cell = row.createCell(col);

                builder.append("Fila");
                builder.append(String.valueOf(fila));
                builder.append("c");
                builder.append(String.valueOf(col));
                key = builder.toString();
                value = rb.getString(key);
                if (!value.equals("0")) {
                    cell.setCellValue(value);
                }
            }
            rowNumber++;
        }

        Double porcDeclinacionAnual = (Double) valorManualMap.get("porcDeclinacionAnual");
        Double potencialCA = (Double) valorManualMap.get("potencialCA");
        Integer yearReport = (Integer) valorManualMap.get("yearReport");
        String plan = (String) valorManualMap.get("plan");

        makeMergeColumns(sheetResumen);

        makeBulkLeftAligment(sheetResumen);

        LocalDate ldYearReport = LocalDate.of(yearReport, 1, 1);//LocalDate.now();

        row = sheetResumen.getRow(0);
        // Coloca la paridad cambiaria usada, la tasa de declinación y el año del resumen
        cell = row.createCell(3);
        cell.setCellValue(paridad.getValor());
        cell.setCellStyle(styles.get("dosDecimalesAzul"));

        cell = row.createCell(11);
        cell.setCellValue(porcDeclinacionAnual / 100);
        cell.setCellStyle(styles.get("porcentajeAzul"));

        // determina el primer celda del año en curso
        //LocalDate ldFirstMonth = LocalDate.of(configuracion.getCurrentYear(), Month.JANUARY, 1);
        //LocalDate ldFirstMonth = LocalDate.of(localDate.getYear(), Month.JANUARY, 1);
        Instant inst = ldYearReport.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Date fechaResumen = Date.from(inst);

        cell = row.createCell(12);
        cell.setCellValue(fechaResumen);
        cell.setCellStyle(styles.get("celdaYearLargoAzul"));

        // calculo de los dias de cada celda(fila 4) y el acumulado (fila 2)
        int currentYear = ldYearReport.getYear();   //configuracion.getCurrentYear();
        Year crrntYear = Year.of(currentYear);
        int acumulaDias = 0;
        for (int celda = 1; celda < 13; celda++) {
            LocalDate ld = LocalDate.of(currentYear, celda, 1);
            Month month = ld.getMonth();
            int diasMonth = month.length(crrntYear.isLeap());
            acumulaDias += diasMonth;
            row = sheetResumen.getRow(1);
            cell = row.createCell(celda + 1);
            cell.setCellValue(acumulaDias);
            cell.setCellStyle(styles.get("sinDecimalesCentradoClaro"));

            row = sheetResumen.getRow(3);
            cell = row.createCell(celda + 1);
            cell.setCellValue(diasMonth);
            cell.setCellStyle(styles.get("sinDecimalesCentradoClaro"));
        }

        row = sheetResumen.getRow(2);
        sheetResumen.addMergedRegion(new CellRangeAddress(
                2, // comienzo de fila
                2, // fin de fila 
                0, // comienzo columna
                1)); // fin de columna
        cell = row.getCell(0);
        cell.setCellValue(ldYearReport.getYear());
        cell.setCellStyle(styles.get("AlineadoAlCentroColorAgua"));

        // título de celdas en fila 3
        for (int celda = 1; celda < 13; celda++) {
            cell = row.createCell(celda + 1);
            cell.setCellValue(getFirstDayOfMonth(ldYearReport.getYear(), celda));
            cell.setCellStyle(styles.get("celdaYearCortoColorAgua"));
        }

        // Potencial C/A (MBD)
        row = sheetResumen.getRow(4);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            cell.setCellValue(potencialCA);
            cell.setCellStyle(styles.get("unDecimal"));
        }

        // Declinación y porcentaje de declinación
        double decl = 0.0;
        for (int celda = 2; celda < 14; celda++) {
            row = sheetResumen.getRow(5);
            cell = row.createCell(celda);
            decl += -potencialCA * (porcDeclinacionAnual / 100) / 12.0;
            cell.setCellValue(decl);
            cell.setCellStyle(styles.get("tresDecimalesNegativo"));

            row = sheetResumen.getRow(6);
            cell = row.createCell(celda);
            cell.setCellValue(-decl / potencialCA);
            cell.setCellStyle(styles.get("porcentajeSinDecimal"));
        }

        // crea las formulas desde la fila 10 hasta la fila 793
        createCellFormulas(sheetResumen);

        createQueryCellFormulas(sheetResumen);

        // Cálculos Arrastre y Actual
        // cambiar el 24 luego por una variable, pozosArrastre plan batalla carabobo
        // Pozos terminados en este año que vienen de arrastre
        int contadorPozosArrastre;
        int contadorPozosActual;
        boolean[] contadoArrastrePlan = new boolean[12];
        boolean[] contadoArrastre = new boolean[12];
        double prodPozo = 0.0;
        double acumulada;

        // Producción declinada
        Map<Integer, Double> prodDeclinadaArrastrePlanMap = new HashMap<>();
        Map<Integer, Double> prodDeclinadaArrastreMap = new HashMap<>();
        Map<Integer, Double> prodDeclinadaPlanMap = new HashMap<>();
        Map<Integer, Double> prodDeclinadaMap = new HashMap<>();
        Map<Integer, Double> sumaDeclinadaArrastreMap = new HashMap<>();
        Map<Integer, Double> sumaDclinadaActualMap = new HashMap<>();

        // Potencial inicial
        Map<Integer, Double> piArrastrePlanMap = new HashMap<>();
        Map<Integer, Double> piArrastreMap = new HashMap<>();
        Map<Integer, Double> piActualPlanMap = new HashMap<>();
        Map<Integer, Double> piActualMap = new HashMap<>();
        Map<Integer, Double> sumaPiArrastreMap = new HashMap<>();
        Map<Integer, Double> sumaPiActualMap = new HashMap<>();

        // Pozos Trabajados
        // información de pozos en progreso y terminados
        Map[] arregloMap = contadorProgresoTerminados(dataOperacionalMap, ldYearReport, plan);
        Map<Integer, Integer> progresoArrastrePlanMap = arregloMap[0];
        Map<Integer, Integer> progresoArrastreMap = arregloMap[1];
        Map<Integer, Integer> progresoActualPlanMap = arregloMap[2];
        Map<Integer, Integer> progresoActualMap = arregloMap[3];
        Map<Integer, Integer> terminadosArrastrePlanMap = arregloMap[4];
        Map<Integer, Integer> terminadosArrastreMap = arregloMap[5];
        Map<Integer, Integer> terminadosActualPlanMap = arregloMap[6];
        Map<Integer, Integer> terminadosActualMap = arregloMap[7];

        // T/A Pozos Trabajados Prod. Generadores
        Map[] arregloTAMap = contadorTaladroAgno(dataOperacionalMap, ldYearReport, plan);
        Map<Integer, Double> progresoArrastreTAPlanMap = arregloTAMap[0];
        Map<Integer, Double> progresoArrastreTAMap = arregloTAMap[1];
        Map<Integer, Double> progresoActualTAPlanMap = arregloTAMap[2];
        Map<Integer, Double> progresoActualTAMap = arregloTAMap[3];
        Map<Integer, Double> terminadosArrastreTAPlanMap = arregloTAMap[4];
        Map<Integer, Double> terminadosArrastreTAMap = arregloTAMap[5];
        Map<Integer, Double> terminadosActualTAPlanMap = arregloTAMap[6];
        Map<Integer, Double> terminadosActualTAMap = arregloTAMap[7];

        // MMBs Pozos Trabajados Productores
        Map[] arregloMMBsMap = contadorMMBs(dataOperacionalMap, ldYearReport, plan);
        Map<Integer, Double> progresoArrastreMMBsPlanMap = arregloMMBsMap[0];
        Map<Integer, Double> progresoArrastreMMBsMap = arregloMMBsMap[1];
        Map<Integer, Double> progresoActualMMBsPlanMap = arregloMMBsMap[2];
        Map<Integer, Double> progresoActualMMBsMap = arregloMMBsMap[3];
        Map<Integer, Double> terminadosArrastreMMBsPlanMap = arregloMMBsMap[4];
        Map<Integer, Double> terminadosArrastreMMBsMap = arregloMMBsMap[5];
        Map<Integer, Double> terminadosActualMMBsPlanMap = arregloMMBsMap[6];
        Map<Integer, Double> terminadosActualMMBsMap = arregloMMBsMap[7];

        // No. Pozos Completados (MEM) Prod. Gen.
        Map[] arregloCompletadoMEMMap = contadorCompletadosMem(dataOperacionalMap, ldYearReport, plan);
        Map<Integer, Integer> aceptadosMemArrastrePlanMap = arregloCompletadoMEMMap[0];
        Map<Integer, Integer> aceptadosMemArrastreMap = arregloCompletadoMEMMap[1];
        Map<Integer, Integer> aceptadosMemActualPlanMap = arregloCompletadoMEMMap[2];
        Map<Integer, Integer> aceptadosMemActualMap = arregloCompletadoMEMMap[3];
        Map<Integer, Double> taproductoresMemArrastrePlanMap = arregloCompletadoMEMMap[4];
        Map<Integer, Double> taproductoresMemArrastreMap = arregloCompletadoMEMMap[5];
        Map<Integer, Double> taproductoresMemActualPlanMap = arregloCompletadoMEMMap[6];
        Map<Integer, Double> taproductoresMemActualMap = arregloCompletadoMEMMap[7];
        Map<Integer, Double> mmbscompletadosMemArrastrePlanMap = arregloCompletadoMEMMap[8];
        Map<Integer, Double> mmbscompletadosMemArrastreMap = arregloCompletadoMEMMap[9];
        Map<Integer, Double> mmbscompletadosMemActualPlanMap = arregloCompletadoMEMMap[10];
        Map<Integer, Double> mmbscompletadosMemActualMap = arregloCompletadoMEMMap[11];

        Map[] arregloConectadosMap = contadorConectados(dataOperacionalMap, ldYearReport, plan);
        Map<Integer, Integer> conectadosArrastrePlanMap = arregloConectadosMap[0];
        Map<Integer, Integer> conectadosArrastreMap = arregloConectadosMap[1];
        Map<Integer, Integer> conectadosActualPlanMap = arregloConectadosMap[2];
        Map<Integer, Integer> conectadosActualMap = arregloConectadosMap[3];

        Map[] arregloIncorporadosMap = contadorIncorporados(dataOperacionalMap, ldYearReport, plan);
        Map<Integer, Double> incorporadosArrastrePlanMap = arregloIncorporadosMap[0];
        Map<Integer, Double> incorporadosArrastreMap = arregloIncorporadosMap[1];
        Map<Integer, Double> incorporadosTerminadosPlanMap = arregloIncorporadosMap[2];
        Map<Integer, Double> incorporadosTerminadosMap = arregloIncorporadosMap[3];

        Map<Integer, Integer> terminadosMecanicamenteMap
                = contadorTerminadosMecanicamente(dataOperacionalMap, ldYearReport);

        Map<Integer, Double> potencialTerminadosMecanicamenteMap
                = contadorPotencialTerminadosMecanicamente(dataOperacionalMap, ldYearReport);

        // inicializaciones
        for (int celda = 1; celda < 13; celda++) {
            prodDeclinadaArrastrePlanMap.put(celda, 0.0);
            prodDeclinadaArrastreMap.put(celda, 0.0);
            prodDeclinadaPlanMap.put(celda, 0.0);
            prodDeclinadaMap.put(celda, 0.0);
            sumaDeclinadaArrastreMap.put(celda, 0.0);
            sumaDclinadaActualMap.put(celda, 0.0);

            piArrastrePlanMap.put(celda, 0.0);
            piArrastreMap.put(celda, 0.0);
            piActualPlanMap.put(celda, 0.0);
            piActualMap.put(celda, 0.0);
            sumaPiArrastreMap.put(celda, 0.0);
            sumaPiActualMap.put(celda, 0.0);
        }

        boolean isArrastre;
        for (Map.Entry<Pozo, Object[]> mapa : dataOperacionalMap.entrySet()) {
            Pozo pozo = mapa.getKey();

            Object[] datos = mapa.getValue();

            Date fechaInicio = (Date) datos[17];
            Instant instant = Instant.ofEpochMilli(fechaInicio.getTime());
            LocalDate ldInicio = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
            Month monthInicio = ldInicio.getMonth();
            int yearInicio = ldInicio.getYear();

            Date fechaFinProductor = (Date) datos[53];
            if (fechaFinProductor == null) {
                continue;
            }

            instant = Instant.ofEpochMilli(fechaFinProductor.getTime());
            LocalDate ldFinPrd = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
            Month monthFinProductor = ldFinPrd.getMonth();
            int yearFinProductor = ldFinPrd.getYear();

            Date fechaAceptacion = (Date) datos[83];
            instant = Instant.ofEpochMilli(fechaAceptacion.getTime());
            LocalDate ldAcept = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
            Month monthAcep = ldAcept.getMonth();
            int yearAceptacion = ldAcept.getYear();

            isArrastre = yearInicio < ldYearReport.getYear();//configuracion.getCurrentYear();

            // calculos de generación
            double decl_anual = Double.valueOf(datos[7].toString());
            double dec_diaria = 1 - Math.pow(1 - decl_anual, 1.0 / 365);
            int[] diasProdDecl = calculoDiasProdDecl(fechaAceptacion);
            boolean firstMes = true;

            for (int celda = 1; celda < 13; celda++) {
                LocalDate ldYearReportMes = LocalDate.of(
                        ldYearReport.getYear() // configuracion.getCurrentYear() // año actual
                        ,
                         celda // celda actual
                        ,
                         1);   // primer día del celda
                LocalDate ldFinActualMes = ldYearReportMes.with(TemporalAdjusters.lastDayOfMonth());

                if (monthAcep.getValue() == celda && firstMes) {
                    prodPozo = pozo.getPi() * Math.pow(1 - dec_diaria, diasProdDecl[1]);
                    firstMes = false;
                } else if (!firstMes) {
                    prodPozo = prodPozo * Math.pow(1 - dec_diaria, diasProdDecl[1]);
                }

                if (isArrastre) {
                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                        acumulada = prodDeclinadaArrastrePlanMap.get(celda) + prodPozo;
                        prodDeclinadaArrastrePlanMap.put(celda, acumulada);
                        if (monthAcep.getValue() == celda) {
                            acumulada = piArrastrePlanMap.get(celda) + pozo.getPi();
                            piArrastrePlanMap.put(celda, acumulada);
                        }
                    } else {
                        acumulada = prodDeclinadaArrastreMap.get(celda) + prodPozo;
                        prodDeclinadaArrastreMap.put(celda, acumulada);
                        if (monthAcep.getValue() == celda) {
                            acumulada = piArrastreMap.get(celda) + pozo.getPi();
                            piArrastreMap.put(celda, acumulada);
                        }
                    }
                    sumaDeclinadaArrastreMap.put(celda, prodDeclinadaArrastrePlanMap.get(celda)
                            + prodDeclinadaArrastreMap.get(celda));
                    sumaPiArrastreMap.put(celda, piArrastrePlanMap.get(celda)
                            + piArrastreMap.get(celda));
                } else {
                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                        acumulada = prodDeclinadaPlanMap.get(celda) + prodPozo;
                        prodDeclinadaPlanMap.put(celda, acumulada);
                        if (monthAcep.getValue() == celda) {
                            acumulada = piActualPlanMap.get(celda) + pozo.getPi();
                            piActualPlanMap.put(celda, acumulada);
                        }
                    } else {
                        acumulada = prodDeclinadaMap.get(celda) + prodPozo;
                        prodDeclinadaMap.put(celda, acumulada);
                        if (monthAcep.getValue() == celda) {
                            acumulada = piActualMap.get(celda) + pozo.getPi();
                            piActualMap.put(celda, acumulada);
                        }
                    }
                    sumaDclinadaActualMap.put(celda, prodDeclinadaPlanMap.get(celda)
                            + prodDeclinadaMap.get(celda));
                    sumaPiArrastreMap.put(celda, piActualPlanMap.get(celda)
                            + piActualMap.get(celda));
                }

            }

        }

        // produccion fiscalizada titulos
        row = sheetResumen.getRow(95);
        for (int celda = 1; celda < 13; celda++) {
            cell = row.createCell(celda + 1);
            cell.setCellValue(getFirstDayOfMonth(ldYearReport.getYear(), celda));
            cell.setCellStyle(styles.get("celdaYearCortoColorAgua"));
        }

        // pozos Arrastre y Actual plan batalla carabobo
        int[] cantPAP = new int[12];
        int[] cantPAnoP = new int[12];
        int[] cantPP = new int[12];
        int[] cantPnoP = new int[12];

        for (int i = 0; i < 12; i++) {
            cantPAP[i] = 0;
            cantPAnoP[i] = 0;
            cantPP[i] = 0;
            cantPnoP[i] = 0;
        }

        for (int celda = 1; celda < 13; celda++) {
            row = sheetResumen.getRow(21);
            cell = row.createCell(celda + 1);
            StringBuilder formula = new StringBuilder();
            formula.append("SUM(").append((char) ('A' + (celda + 1))).append("30:");
            formula.append((char) ('A' + (celda + 1))).append("37)");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("23");
            cell.setCellFormula(formula.toString());
            //cell.setCellValue((sumaDeclinadaArrastreMap.get(celda) + sumaDclinadaActualMap.get(celda)) / 1000);
            cell.setCellStyle(styles.get("dosDecimales"));

            row = sheetResumen.getRow(22);
            cell = row.createCell(celda + 1);
            formula = new StringBuilder();
            formula.append((char) ('A' + (celda + 1))).append("24");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("27");
            //cell.setCellValue((sumaDeclinadaArrastreMap.get(celda) + sumaDclinadaActualMap.get(celda)) / 1000);
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("dosDecimales"));

            row = sheetResumen.getRow(23);
            cell = row.createCell(celda + 1);
            formula = new StringBuilder();
            formula.append((char) ('A' + (celda + 1))).append("25");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("26");
            cell.setCellFormula(formula.toString());
            //cell.setCellValue(sumaDeclinadaArrastreMap.get(celda) / 1000);
            cell.setCellStyle(styles.get("tresDecimalesBold"));

            // Perforacion Arrastre Plan
            row = sheetResumen.getRow(24);
            cell = row.createCell(celda + 1);
            cell.setCellValue(prodDeclinadaArrastrePlanMap.get(celda) / 1000);
            cell.setCellStyle(styles.get("tresDecimales"));

            // Potencial Arrastre Plan
            row = sheetResumen.getRow(73);
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(piArrastrePlanMap.get(celda) / 1000);
            } else {
                String valor = String.valueOf(piArrastrePlanMap.get(celda) / 1000);
                formula = new StringBuilder();
                formula.append((char) ('A' + (celda))).append("74");
                formula.append(" + ").append(valor);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            // Perforacion Arrastre fuera de Plan
            row = sheetResumen.getRow(25);
            cell = row.createCell(celda + 1);
            cell.setCellValue(prodDeclinadaArrastreMap.get(celda) / 1000);
            cell.setCellStyle(styles.get("tresDecimales"));

            // Potencial Arrastre fuera de Plan
            row = sheetResumen.getRow(74);
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(piArrastreMap.get(celda) / 1000);
            } else {
                String valor = String.valueOf(piArrastreMap.get(celda) / 1000);
                formula = new StringBuilder();
                formula.append((char) ('A' + (celda))).append("75");
                formula.append(" + ").append(valor);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(26);
            cell = row.createCell(celda + 1);
            formula = new StringBuilder();
            formula.append((char) ('A' + (celda + 1))).append("28");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("29");
            cell.setCellFormula(formula.toString());
            //cell.setCellValue(sumaDclinadaActualMap.get(celda) / 1000);
            cell.setCellStyle(styles.get("tresDecimalesBold"));

            // Perforacion Terminados Plan
            row = sheetResumen.getRow(27);
            cell = row.createCell(celda + 1);
            cell.setCellValue(prodDeclinadaPlanMap.get(celda) / 1000);
            cell.setCellStyle(styles.get("tresDecimales"));

            // Potencial Terminados Plan
            row = sheetResumen.getRow(76);
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(piActualPlanMap.get(celda) / 1000);
            } else {
                String valor = String.valueOf(piActualPlanMap.get(celda) / 1000);
                formula = new StringBuilder();
                formula.append((char) ('A' + (celda))).append("77");
                formula.append(" + ").append(valor);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            // Perforacion Terminados fuera de Plan
            row = sheetResumen.getRow(28);
            cell = row.createCell(celda + 1);
            cell.setCellValue(prodDeclinadaMap.get(celda) / 1000);
            cell.setCellStyle(styles.get("tresDecimales"));

            // Potencial Terminados fuera de Plan
            row = sheetResumen.getRow(77);
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(piActualMap.get(celda) / 1000);
            } else {
                String valor = String.valueOf(piActualMap.get(celda) / 1000);
                formula = new StringBuilder();
                formula.append((char) ('A' + (celda))).append("78");
                formula.append(" + ").append(valor);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            // Pozos en progreso arrastre 
            row = sheetResumen.getRow(122);   // en el Plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cantPAP[celda - 1] = progresoArrastrePlanMap.get(0) + progresoArrastrePlanMap.get(celda)
                        - (terminadosArrastrePlanMap.get(celda) + terminadosActualPlanMap.get(celda));
            } else {
                cantPAP[celda - 1] = cantPAP[celda - 2] + progresoArrastrePlanMap.get(celda)
                        - (terminadosArrastrePlanMap.get(celda) + terminadosActualPlanMap.get(celda));
            }
            cell.setCellValue(cantPAP[celda - 1]);
            cell.setCellStyle(styles.get("sinDecimales"));

            row = sheetResumen.getRow(123);   // Complementarios
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cantPAnoP[celda - 1] = progresoArrastreMap.get(0) + progresoArrastreMap.get(celda)
                        - (terminadosArrastreMap.get(celda) + terminadosActualMap.get(celda));
            } else {
                cantPAnoP[celda - 1] = cantPAnoP[celda - 2] + progresoArrastreMap.get(celda)
                        - (terminadosArrastreMap.get(celda) + terminadosActualMap.get(celda));
            }
            cell.setCellValue(cantPAnoP[celda - 1]);
            cell.setCellStyle(styles.get("sinDecimales"));

            // Pozos en progreso año actual
//            row = sheetResumen.getRow(786);   // en el Plan por defecto de excel
            row = sheetResumen.getRow(125);   // en el Plan por defecto de excel
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cantPP[celda - 1] = progresoActualPlanMap.get(celda) - terminadosActualPlanMap.get(celda);
            } else {
                cantPP[celda - 1] = cantPP[celda - 2] + progresoActualPlanMap.get(celda)
                        - terminadosActualPlanMap.get(celda);
            }
            cell.setCellValue(cantPP[celda - 1]);
            cell.setCellStyle(styles.get("sinDecimales"));

            row = sheetResumen.getRow(126);   // Complementarios
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cantPnoP[celda - 1] = progresoActualMap.get(celda) - terminadosActualMap.get(celda);
            } else {
                cantPnoP[celda - 1] = cantPnoP[celda - 2] + progresoActualMap.get(celda)
                        - terminadosActualMap.get(celda);
            }
            cell.setCellValue(cantPnoP[celda - 1]);
            cell.setCellStyle(styles.get("sinDecimales"));

            // Pozos terminados arrastre 
            row = sheetResumen.getRow(128);   // en el Plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosArrastrePlanMap.get(celda) + terminadosArrastrePlanMap.get(0));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("129");
                formula.append(" + ").append(terminadosArrastrePlanMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));

            row = sheetResumen.getRow(129);   // Complementarios
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosArrastreMap.get(celda) + terminadosArrastreMap.get(0));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("130");
                formula.append(" + ").append(terminadosArrastreMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));

            // Pozos terminados año actual
            row = sheetResumen.getRow(131);   // en el Plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosActualPlanMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("132");
                formula.append(" + ").append(terminadosActualPlanMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));

            row = sheetResumen.getRow(132);   // Complementarios
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosActualMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("133");
                formula.append(" + ").append(terminadosActualMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));

            // T/A Pozos trabajados Prod. Generadores
            row = sheetResumen.getRow(189); // ta progreso arrastre en Plan
            cell = row.createCell(celda + 1);
            cell.setCellValue(progresoArrastreTAPlanMap.get(celda));
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(190); // ta progreso arrastre complementaria
            cell = row.createCell(celda + 1);
            cell.setCellValue(progresoArrastreTAMap.get(celda));
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(192); // ta progreso actual en Plan
            cell = row.createCell(celda + 1);
            cell.setCellValue(progresoActualTAPlanMap.get(celda));
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(193); // ta progreso actual complementaria
            cell = row.createCell(celda + 1);
            cell.setCellValue(progresoActualTAMap.get(celda));
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(195); // ta terminados arrastre Plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosArrastreTAPlanMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("196");
                formula.append(" + ").append(terminadosArrastreTAPlanMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(196); // ta terminados arrastre complementaria
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosArrastreTAMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("197");
                formula.append(" + ").append(terminadosArrastreTAMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(198); // ta terminados actual plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosActualTAPlanMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("199");
                formula.append(" + ").append(terminadosActualTAPlanMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(199); // ta terminados actual complementaria
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosActualTAMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("200");
                formula.append(" + ").append(terminadosActualTAMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            // MMBs Pozos Trabajados Productores (celda 245 a 310 del resumen)
            row = sheetResumen.getRow(250); // MMBs progreso arrastre en Plan
            cell = row.createCell(celda + 1);
            cell.setCellValue(progresoArrastreMMBsPlanMap.get(celda) / 1000.0);
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(251); // MMBs progreso arrastre complementarias
            cell = row.createCell(celda + 1);
            cell.setCellValue(progresoArrastreMMBsMap.get(celda) / 1000.0);
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(253); // MMBs progreso actual plan 
            cell = row.createCell(celda + 1);
            cell.setCellValue(progresoActualMMBsPlanMap.get(celda) / 1000.0);
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(254); // MMBs progreso actual complementarias 
            cell = row.createCell(celda + 1);
            cell.setCellValue(progresoActualMMBsMap.get(celda) / 1000.0);
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(256); // MMBs terminados arrastre plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosArrastreMMBsPlanMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("257");
                formula.append(" + ").append(terminadosArrastreMMBsPlanMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(257); // MMBs terminados arrastre complementarias
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosArrastreMMBsMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("258");
                formula.append(" + ").append(terminadosArrastreMMBsMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(259); // MMBs terminados actual plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosActualMMBsPlanMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("260");
                formula.append(" + ").append(terminadosActualMMBsPlanMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(260); // MMBs terminados actual complementarias
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosActualMMBsMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("261");
                formula.append(" + ").append(terminadosActualMMBsMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));
            
            // No. Pozos Completados (MEM) Prod. Gen.
            row = sheetResumen.getRow(314); // Aceptados MEM Arrastre en Plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(aceptadosMemArrastrePlanMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("315");
                formula.append(" + ").append(aceptadosMemArrastrePlanMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));
            
            row = sheetResumen.getRow(315); // Aceptados MEM Arrastre complementarios
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(aceptadosMemArrastreMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("316");
                formula.append(" + ").append(aceptadosMemArrastreMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));
            
            row = sheetResumen.getRow(317); // Aceptados MEM Actual Plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(aceptadosMemActualPlanMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("318");
                formula.append(" + ").append(aceptadosMemActualPlanMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));
            
            row = sheetResumen.getRow(318); // Aceptados MEM Actual Complementarios
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(aceptadosMemActualMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("319");
                formula.append(" + ").append(aceptadosMemActualMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));
            
            row = sheetResumen.getRow(340); // TA MEM Arrastre en Plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(taproductoresMemArrastrePlanMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("341");
                formula.append(" + ").append(taproductoresMemArrastrePlanMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));
            
            row = sheetResumen.getRow(341); // TA MEM Arrastre Complementarios
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(taproductoresMemArrastreMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("342");
                formula.append(" + ").append(taproductoresMemArrastreMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));
            
            row = sheetResumen.getRow(343); // TA MEM Actual en Plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(taproductoresMemActualPlanMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("344");
                formula.append(" + ").append(taproductoresMemActualPlanMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(344); // TA MEM Actual Complementarios
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(taproductoresMemActualMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("345");
                formula.append(" + ").append(taproductoresMemActualMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));
            
            row = sheetResumen.getRow(364); //MMBs Completados MEM Arrastre en Plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(mmbscompletadosMemArrastrePlanMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("365");
                formula.append(" + ").append(mmbscompletadosMemArrastrePlanMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));
            
            row = sheetResumen.getRow(365); //MMBs Completados MEM Arrastre Complementos
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(mmbscompletadosMemArrastreMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("366");
                formula.append(" + ").append(mmbscompletadosMemArrastreMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));
            
            row = sheetResumen.getRow(367); //MMBs Completados MEM Arrastre en Plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(mmbscompletadosMemActualPlanMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("368");
                formula.append(" + ").append(mmbscompletadosMemActualPlanMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(368); //MMBs Completados MEM Arrastre Complementos
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(mmbscompletadosMemActualMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("369");
                formula.append(" + ").append(mmbscompletadosMemActualMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("tresDecimales"));

            // Conectados
            row = sheetResumen.getRow(730); // conectdos arrastre plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(conectadosArrastrePlanMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("731");
                formula.append(" + ").append(conectadosArrastrePlanMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));

            row = sheetResumen.getRow(731); // conectdos arrastre complementarios
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(conectadosArrastreMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("732");
                formula.append(" + ").append(conectadosArrastreMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));

            row = sheetResumen.getRow(733); // conectdos actual plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(conectadosActualPlanMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("734");
                formula.append(" + ").append(conectadosActualPlanMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));

            row = sheetResumen.getRow(734); // conectdos actual complementarios
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(conectadosActualMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("735");
                formula.append(" + ").append(conectadosActualMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));

            // MBD (INCORPORACIÓN), linea: 758
            row = sheetResumen.getRow(757); // Arrastre Plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(incorporadosArrastrePlanMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("758");
                formula.append(" + ").append(incorporadosArrastrePlanMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("unDecimal"));

            row = sheetResumen.getRow(758); // Arrastre complementarios
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(incorporadosArrastreMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("759");
                formula.append(" + ").append(incorporadosArrastreMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("unDecimal"));

            row = sheetResumen.getRow(760); // Terminados plan
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(incorporadosTerminadosPlanMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("761");
                formula.append(" + ").append(incorporadosTerminadosPlanMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("unDecimal"));

            row = sheetResumen.getRow(761); // Terminados complementarios
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(incorporadosTerminadosMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("762");
                formula.append(" + ").append(incorporadosTerminadosMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("unDecimal"));

            // N° Pozos Completados Mecanicamente, linea: 781
            row = sheetResumen.getRow(781); // Perforación
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(terminadosMecanicamenteMap.get(celda));
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("782");
                formula.append(" + ").append(terminadosMecanicamenteMap.get(celda));
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("sinDecimales"));

            //Potencial Pozos Completados Mecanicamente, linea: 789
            row = sheetResumen.getRow(788); // Perforación
            cell = row.createCell(celda + 1);
            if (celda == 1) {
                cell.setCellValue(potencialTerminadosMecanicamenteMap.get(celda) / 1000.0);
            } else {
                formula = new StringBuilder();
                formula.append((char) ('A' + celda)).append("789");
                formula.append(" + ").append(potencialTerminadosMecanicamenteMap.get(celda) / 1000.0);
                cell.setCellFormula(formula.toString());
            }
            cell.setCellStyle(styles.get("unDecimal"));
        }

        // título de celdas en fila 47
        row = sheetResumen.getRow(46);
        for (int celda = 1; celda < 13; celda++) {
            cell = row.createCell(celda + 1);
            cell.setCellValue(getFirstDayOfMonth(ldYearReport.getYear(), celda));
            cell.setCellStyle(styles.get("celdaYearCortoColorAgua"));
        }

        // título de celdas en fila 117
        row = sheetResumen.getRow(116);
        for (int celda = 1; celda < 13; celda++) {
            cell = row.createCell(celda + 1);
            cell.setCellValue(getFirstDayOfMonth(ldYearReport.getYear(), celda));
            cell.setCellStyle(styles.get("celdaYearCortoAmarillo"));
        }

        // título de celdas en fila 184
        row = sheetResumen.getRow(183);
        for (int celda = 1; celda < 13; celda++) {
            cell = row.createCell(celda + 1);
            cell.setCellValue(getFirstDayOfMonth(ldYearReport.getYear(), celda));
            cell.setCellStyle(styles.get("celdaYearCortoAmarillo"));
        }

        // Anchos de columna (las columnas son medidas en 1/256 ava parte de un caracter
        sheetResumen.setColumnWidth(0, 34 * 256);
        sheetResumen.setColumnWidth(1, 56 * 256);
        for (int celda = 2; celda < 14; celda++) {
            sheetResumen.setColumnWidth(celda, 12 * 256);
        }

        try {
            FileOutputStream out = new FileOutputStream(excelFile);
            workbook.write(out);
            out.close();
        } catch (Exception e) {
        }
    }

    private void createCellFormulas(XSSFSheet sheetResumen) {
        Row row;
        Cell cell;

        row = sheetResumen.getRow(10);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("12");
            formula.append("+").append((char) ('A' + celda)).append("13");
            formula.append("+").append((char) ('A' + celda)).append("16");
            formula.append("+").append((char) ('A' + celda)).append("17");
            formula.append("+").append((char) ('A' + celda)).append("18");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("unDecimal"));
        }

        row = sheetResumen.getRow(11);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("23");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));
        }

        row = sheetResumen.getRow(12);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("14");
            formula.append("+").append((char) ('A' + celda)).append("15");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));
        }

        row = sheetResumen.getRow(13);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("30");
            formula.append("+").append((char) ('A' + celda)).append("32");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));
        }

        row = sheetResumen.getRow(14);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("31");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));
        }

        row = sheetResumen.getRow(15);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("33");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));
        }

        row = sheetResumen.getRow(16);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("34");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));
        }

        row = sheetResumen.getRow(17);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("19");
            formula.append("+").append((char) ('A' + celda)).append("20");
            formula.append("+").append((char) ('A' + celda)).append("37");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));
        }

        row = sheetResumen.getRow(18);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("35");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));
        }

        row = sheetResumen.getRow(19);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("36");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));
        }

        row = sheetResumen.getRow(36);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append("SUM(").append((char) ('A' + (celda + 1))).append("38:");
            formula.append((char) ('A' + celda)).append("45)");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimalesBold"));
        }

        row = sheetResumen.getRow(50);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append("SUM(").append((char) ('A' + (celda + 1))).append("52:");
            formula.append((char) ('A' + celda)).append("56)");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("unDecimalBold"));
        }

        row = sheetResumen.getRow(47);
        cell = row.createCell(2);
        String formulaExt = (char) ('C') + "49";
        cell.setCellFormula(formulaExt);
        cell.setCellStyle(styles.get("unDecimal"));
        for (int celda = 3; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append("(").append((char) ('A' + (celda - 1))).append("48");
            formula.append("*").append((char) ('A' + (celda - 1))).append("2");
            formula.append("+").append((char) ('A' + celda)).append("49");
            formula.append("*").append((char) ('A' + celda)).append("4");
            formula.append(") / ").append((char) ('A' + celda)).append("2");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("unDecimal"));
        }

        // Potencial promedio celda
        row = sheetResumen.getRow(48);
        cell = row.createCell(2);
        formulaExt = "(C5 + C50) / 2";
        cell.setCellFormula(formulaExt);
        cell.setCellStyle(styles.get("unDecimal"));
        for (int celda = 3; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append("(").append((char) ('A' + (celda - 1))).append("50");
            formula.append("+").append((char) ('A' + celda)).append("50");
            formula.append(") / 2");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("unDecimal"));
        }

        // Potencial Fin de Período
        row = sheetResumen.getRow(49);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("5");
            formula.append("+").append((char) ('A' + celda)).append("6");
            formula.append("+").append((char) ('A' + celda)).append("22");
            formula.append("+").append((char) ('A' + celda)).append("8");
            formula.append("+").append((char) ('A' + celda)).append("9");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("unDecimal"));
        }

        // Potencial Fin de Periodo por tipo de crudo (MBD) Extrapesado
        row = sheetResumen.getRow(55);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("50");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("unDecimal"));
        }

        row = sheetResumen.getRow(56);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("106");
            formula.append("/").append((char) ('A' + celda)).append("49");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("porcentajeBoldColorAgua"));
        }

        row = sheetResumen.getRow(57);
        for (int celda = 2; celda < 14; celda++) {
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("107");
            formula.append("/").append((char) ('A' + celda)).append("48");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("porcentajeBoldColorAgua"));
        }

        // generación de potencial inicial
        for (int celda = 2; celda < 14; celda++) {
            row = sheetResumen.getRow(59);
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append((char) ('A' + (celda + 1))).append("61");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("62");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("65");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("66");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("67");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("unDecimalAzulMarino"));

            row = sheetResumen.getRow(60);
            cell = row.createCell(celda);
            formula = new StringBuilder();
            formula.append((char) ('A' + (celda + 1))).append("72");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(61);
            cell = row.createCell(celda);
            formula = new StringBuilder();
            formula.append((char) ('A' + (celda + 1))).append("63");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("64");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(62);
            cell = row.createCell(celda);
            formula = new StringBuilder();
            formula.append((char) ('A' + (celda + 1))).append("79");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("81");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(63);
            cell = row.createCell(celda);
            formula = new StringBuilder();
            formula.append((char) ('A' + (celda + 1))).append("80");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(64);
            cell = row.createCell(celda);
            formula = new StringBuilder();
            formula.append((char) ('A' + (celda + 1))).append("82");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(65);
            cell = row.createCell(celda);
            formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("83");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(66);
            cell = row.createCell(celda + 1);
            formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("68");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("69");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("86");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(67);
            cell = row.createCell(celda);
            formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("84");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(68);
            cell = row.createCell(celda + 1);
            formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("85");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));
        }

        // subtotales y totales de potencial
        for (int celda = 2; celda < 14; celda++) {
            row = sheetResumen.getRow(70);
            cell = row.createCell(celda);
            StringBuilder formula = new StringBuilder();
            formula.append("SUM(");
            formula.append((char) ('A' + celda)).append("79");
            formula.append(":").append((char) ('A' + celda)).append("86, ");
            formula.append((char) ('A' + celda)).append("72)");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("unDecimalAzulMarino"));

            row = sheetResumen.getRow(71);
            cell = row.createCell(celda);
            formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("76");
            formula.append(" + ").append((char) ('A' + celda)).append("73");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimales"));

            row = sheetResumen.getRow(72);
            cell = row.createCell(celda);
            formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("74");
            formula.append(" + ").append((char) ('A' + celda)).append("75");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimalesBold"));

            row = sheetResumen.getRow(75);
            cell = row.createCell(celda);
            formula = new StringBuilder();
            formula.append((char) ('A' + celda)).append("77");
            formula.append(" + ").append((char) ('A' + celda)).append("78");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("tresDecimalesBold"));

//            row = sheetResumen.getRow(85);
//            cell = row.createCell(celda);
//            formula = new StringBuilder();
//            formula.append("SUM(");
//            formula.append((char) ('A' + celda)).append("87:");
//            formula.append((char) ('A' + celda)).append("94)");
//            cell.setCellFormula(formula.toString());
//            cell.setCellStyle(styles.get("tresDecimales"));
        }

        // Producción fiscalizada
        for (int celda = 1; celda < 13; celda++) {
            // tecnologías
            makeSumCellFormula(sheetResumen, 86, celda, "tresDecimalesBold",
                    87, 94);

            // Fiscalizada / Operada
            makeSumCellFormula(sheetResumen, 97, celda, "unDecimalBoldRojo",
                    99, 103);

            // promedio producción fiscalizada
            createCellPromedioFormula(sheetResumen, celda, 98, "unDecimalBold");

            createSumMultiCellFormula(sheetResumen, 103, celda,
                    "unDecimal", 106);

            // produccion disponible
            row = sheetResumen.getRow(105);
            cell = row.createCell(celda + 1);
            StringBuilder formula = new StringBuilder();
            formula.append("(");
            formula.append((char) ('A' + (celda + 1))).append("49 + (");
            formula.append((char) ('A' + (celda + 1))).append("537");
            formula.append(" + ").append((char) ('A' + (celda + 1))).append("540))");
            formula.append(" * ").append((char) ('A' + (celda + 1))).append("544");
            cell.setCellFormula(formula.toString());
            cell.setCellStyle(styles.get("unDecimalBold"));

            // promedio produccion disponible
            createCellPromedioFormula(sheetResumen, celda, 107, "unDecimalBold");

            // producción gas
            // promedio producción gas
            createCellPromedioFormula(sheetResumen, celda, 111, "unDecimalBold");

            // promedio producción agua
            createCellPromedioFormula(sheetResumen, celda, 115, "unDecimalBold");

            createSumMultiCellFormula(sheetResumen, 116, celda,
                    "sinDecimalesBold", 122, 128);

            // Conteo de pozos trabajados
            // Total trabajados
            createSumMultiCellFormula(sheetResumen, 118, celda,
                    "sinDecimales", 119, 128, 131);

            // Totalizaciones
            createSumMultiCellFormula(sheetResumen, 119, celda, // en progreso
                    "sinDecimales", 120, 121);

            createSumMultiCellFormula(sheetResumen, 120, celda, // en progreso del Plan
                    "sinDecimales", 123, 126);

            createSumMultiCellFormula(sheetResumen, 121, celda, // en progreso fuera del Plan
                    "sinDecimales", 124, 127);

            createSumMultiCellFormula(sheetResumen, 122, celda, // totalización
                    "sinDecimales", 123, 124);

            // en progreso
            createSumMultiCellFormula(sheetResumen, 125, celda, // totalización
                    "sinDecimales", 126, 127);

            // pozos terminados arraste
            createSumMultiCellFormula(sheetResumen, 128, celda, // totalización
                    "sinDecimales", 129, 130);

            // pozos terminados actual
            createSumMultiCellFormula(sheetResumen, 131, celda, // totalización
                    "sinDecimales", 132, 133);

            // No. Pozos Trabajados Prod. Generadores
            createSumMultiCellFormula(sheetResumen, 134, celda,
                    "sinDecimalesBold", 135, 136);

            createSumMultiCellFormula(sheetResumen, 137, celda,
                    "sinDecimalesBold", 138, 139);

            createSumMultiCellFormula(sheetResumen, 140, celda,
                    "sinDecimalesBold", 141, 142);

            createSumMultiCellFormula(sheetResumen, 143, celda,
                    "sinDecimalesBold", 144, 145);

            createSumMultiCellFormula(sheetResumen, 146, celda,
                    "sinDecimalesBold", 147, 148);

            createSumMultiCellFormula(sheetResumen, 149, celda,
                    "sinDecimalesBold", 150, 151);

            createSumMultiCellFormula(sheetResumen, 152, celda,
                    "sinDecimalesBold", 153, 154);

            createSumMultiCellFormula(sheetResumen, 155, celda,
                    "sinDecimalesBold", 156, 157);

            createSumMultiCellFormula(sheetResumen, 156, celda,
                    "sinDecimalesBold", 159, 162, 165, 168, 171, 174, 177, 180);

            createSumMultiCellFormula(sheetResumen, 157, celda,
                    "sinDecimalesBold", 160, 163, 166, 169, 172, 175, 178, 181);

            createSumMultiCellFormula(sheetResumen, 158, celda,
                    "sinDecimales", 159, 160);

            createSumMultiCellFormula(sheetResumen, 161, celda,
                    "sinDecimales", 162, 163);

            createSumMultiCellFormula(sheetResumen, 164, celda,
                    "sinDecimales", 165, 166);

            createSumMultiCellFormula(sheetResumen, 167, celda,
                    "sinDecimales", 168, 169);

            createSumMultiCellFormula(sheetResumen, 170, celda,
                    "sinDecimales", 171, 172);

            createSumMultiCellFormula(sheetResumen, 173, celda,
                    "sinDecimales", 174, 175);

            createSumMultiCellFormula(sheetResumen, 176, celda,
                    "sinDecimales", 177, 178);

            createSumMultiCellFormula(sheetResumen, 179, celda,
                    "sinDecimales", 180, 181);

            // Total N° Pozos Trabaj. Prod. Generadores
            createSumMultiCellFormula(sheetResumen, 182, celda,
                    "sinDecimalesCentradoAmarillo", 118, 134, 137, 140, 143, 146,
                    149, 152, 155);

            // T/A pozos trabajados Prod. Generadores
            // perforación
            createSumMultiCellFormula(sheetResumen, 185, celda, "unDecimalBold",
                    195, 186, 198);

            createSumMultiCellFormula(sheetResumen, 186, celda, "tresDecimales",
                    187, 188);

            createSumMultiCellFormula(sheetResumen, 187, celda, "tresDecimales",
                    190, 193);

            createSumMultiCellFormula(sheetResumen, 188, celda, "tresDecimales",
                    191, 194);

            // en progreso arrastre
            createSumMultiCellFormula(sheetResumen, 189, celda, "tresDecimales",
                    190, 191);

            // en progreso año actual
            createSumMultiCellFormula(sheetResumen, 192, celda, "tresDecimales",
                    193, 194);

            // pozos terminados arrastre
            createSumMultiCellFormula(sheetResumen, 195, celda, "tresDecimales",
                    196, 197);

            // pozos terminados actual
            createSumMultiCellFormula(sheetResumen, 198, celda, "tresDecimales",
                    199, 200);

            // ra/rc C/T
            createSumMultiCellFormula(sheetResumen, 201, celda, "tresDecimales",
                    202, 203);

            // reeentradas
            createSumMultiCellFormula(sheetResumen, 204, celda, "tresDecimalesBold",
                    205, 206);

            // IAV
            createSumMultiCellFormula(sheetResumen, 207, celda, "tresDecimalesBold",
                    208, 209);

            // estimulaciones / fracturas
            createSumMultiCellFormula(sheetResumen, 210, celda, "tresDecimalesBold",
                    211, 212);

            // otras actividades
            createSumMultiCellFormula(sheetResumen, 213, celda, "tresDecimalesBold",
                    214, 215);

            // tecnologias
            createSumMultiCellFormula(sheetResumen, 216, celda, "tresDecimalesBold",
                    217, 218);

            // progreso
            createSumMultiCellFormula(sheetResumen, 217, celda, "tresDecimales",
                    220, 223, 226, 229, 232, 235, 238, 241);

            // terminados
            createSumMultiCellFormula(sheetResumen, 218, celda, "tresDecimales",
                    221, 224, 227, 230, 233, 236, 239, 242);

            // completaciones especiales
            createSumMultiCellFormula(sheetResumen, 219, celda, "tresDecimales",
                    220, 221);

            // calentamiento electrico en fondo
            createSumMultiCellFormula(sheetResumen, 222, celda, "sinDecimales",
                    223, 224);

            // intesurf
            createSumMultiCellFormula(sheetResumen, 225, celda, "sinDecimales",
                    226, 227);

            // inyección contínua a vapor
            createSumMultiCellFormula(sheetResumen, 228, celda, "sinDecimales",
                    229, 230);

            // inyección de agua
            createSumMultiCellFormula(sheetResumen, 231, celda, "sinDecimales",
                    232, 233);

            // sand aid
            createSumMultiCellFormula(sheetResumen, 234, celda, "sinDecimales",
                    235, 236);

            // sagd
            createSumMultiCellFormula(sheetResumen, 237, celda, "sinDecimales",
                    238, 239);

            // sw sagd
            createSumMultiCellFormula(sheetResumen, 240, celda, "sinDecimales",
                    241, 242);

            // total t/a pozos trabajados prod generadores
            createSumMultiCellFormula(sheetResumen, 243, celda, "unDecimalBoldAmarillo",
                    185, 201, 204, 210, 213, 216, 207);

            // titulos celdas fila 245
            createSumMultiCellFormula(sheetResumen, 245, celda, "celdaYearCortoAmarillo",
                    117);

            // perforación
            createSumMultiCellFormula(sheetResumen, 246, celda, "tresDecimalesBoldCeleste",
                    256, 247, 259);

            // perforación en progreso
            createSumMultiCellFormula(sheetResumen, 247, celda, "tresDecimales",
                    248, 249);

            // perforación plan
            createSumMultiCellFormula(sheetResumen, 248, celda, "tresDecimales",
                    251, 254);

            // perforación act complementaria
            createSumMultiCellFormula(sheetResumen, 249, celda, "tresDecimales",
                    252, 255);

            // perforación en progreso años anteriores
            createSumMultiCellFormula(sheetResumen, 250, celda, "tresDecimales",
                    251, 252);

            // perforación en progreso año actual
            createSumMultiCellFormula(sheetResumen, 253, celda, "tresDecimales",
                    254, 255);

            // perforación terminados arastre
            createSumMultiCellFormula(sheetResumen, 256, celda, "tresDecimales",
                    257, 258);

            // perforación terminados actual
            createSumMultiCellFormula(sheetResumen, 259, celda, "tresDecimales",
                    260, 261);

            // ra/rc C/T
            createSumMultiCellFormula(sheetResumen, 262, celda, "tresDecimalesBoldCeleste",
                    263, 264);

            // ra/rc S/t
            createSumMultiCellFormula(sheetResumen, 265, celda, "tresDecimalesBoldCeleste",
                    266, 267);

            // perforación reentradas
            createSumMultiCellFormula(sheetResumen, 268, celda, "tresDecimalesBoldCeleste",
                    269, 270);

            // perforación iav
            createSumMultiCellFormula(sheetResumen, 271, celda, "tresDecimalesBoldCeleste",
                    272, 273);

            // estimulacion / fracturas
            createSumMultiCellFormula(sheetResumen, 274, celda, "tresDecimalesBoldCeleste",
                    275, 276);

            // Otras actividades c/T
            createSumMultiCellFormula(sheetResumen, 277, celda, "tresDecimalesBoldCeleste",
                    278, 279);

            // otras actividades s/T
            createSumMultiCellFormula(sheetResumen, 280, celda, "tresDecimalesBoldCeleste",
                    281, 282);

            // tecnologías
            createSumMultiCellFormula(sheetResumen, 283, celda, "sinDecimalBoldCeleste",
                    284, 285);

            // tecnologías en progreso
            createSumMultiCellFormula(sheetResumen, 284, celda, "sinDecimal",
                    287, 290, 293, 296, 299, 302, 305, 308);

            // tecnologías terminados
            createSumMultiCellFormula(sheetResumen, 285, celda, "sinDecimal",
                    288, 291, 294, 297, 300, 303, 306, 309);

            // completaciones especiales
            createSumMultiCellFormula(sheetResumen, 286, celda, "sinDecimalBoldCeleste",
                    287, 288);

            // tecnologías calentamiento electrico en fondo
            createSumMultiCellFormula(sheetResumen, 289, celda, "sinDecimalBoldCeleste",
                    290, 291);

            // tecnologías intesurf
            createSumMultiCellFormula(sheetResumen, 292, celda, "sinDecimalBoldCelest;",
                    293, 294);

            // tecnologías inyección continua de vapor
            createSumMultiCellFormula(sheetResumen, 295, celda, "sinDecimalBoldCeleste",
                    296, 297);

            // tecnologías inyección de agua
            createSumMultiCellFormula(sheetResumen, 298, celda, "sinDecimalBoldCeleste",
                    299, 300);

            // tecnologías sand aid
            createSumMultiCellFormula(sheetResumen, 301, celda, "sinDecimalBoldCeleste",
                    302, 303);

            // tecnologías sagd
            createSumMultiCellFormula(sheetResumen, 304, celda, "sinDecimalBoldCeleste",
                    305, 306);

            // tecnologías sw sagd
            createSumMultiCellFormula(sheetResumen, 307, celda, "sinDecimalBoldCeleste",
                    308, 309);

            // Total MMBs Pozos Trabaj. Prod. Generadores
            createSumMultiCellFormula(sheetResumen, 310, celda, "tresDecimalesBoldAmarillo",
                    283, 280, 277, 274, 271, 268, 265, 262, 246);

            //No. Pozos Completados (MEM) Prod. Gen.
            createSumMultiCellFormula(sheetResumen, 312, celda, "celdaYearCortoRosado",
                    117);

            //Aceptados perforación
            createSumMultiCellFormula(sheetResumen, 313, celda, "sinDecimalBoldAzulMarino",
                    314, 317);

            // arrastre
            createSumMultiCellFormula(sheetResumen, 314, celda, "sinDecimal",
                    315, 316);

            // terminados
            createSumMultiCellFormula(sheetResumen, 317, celda, "sinDecimal",
                    318, 319);

            // tecnologías
            makeSumCellFormula(sheetResumen, 327, celda, "sinDecimalBoldCeleste",
                    328, 335);

            // terminados
            createSumMultiCellFormula(sheetResumen, 336, celda, "sinDecimalBoldRosado",
                    313, 320, 321, 323, 324, 325, 326, 327, 322);

            //T/A Pozos Completados Prod. Gen.
            createSumMultiCellFormula(sheetResumen, 338, celda, "celdaYearCortoRosado",
                    117);

            // perforacion
            createSumMultiCellFormula(sheetResumen, 339, celda, "unDecimalBoldCeleste",
                    340, 343);

            // arrastre
            createSumMultiCellFormula(sheetResumen, 340, celda, "tresDecimales",
                    341, 342);

            // terminados
            createSumMultiCellFormula(sheetResumen, 343, celda, "tresDecimales",
                    344, 345);

            // arrastre
            makeSumCellFormula(sheetResumen, 351, celda, "tresDecimalesBoldCeleste",
                    352, 359);

            // Total T/A Pozos Productores Generadores
            createSumMultiCellFormula(sheetResumen, 360, celda, "tresDecimalesBoldRosado",
                    339, 346, 350, 349, 347, 351, 348);

            // MMBs Pozos Completados Productores
            createSumMultiCellFormula(sheetResumen, 362, celda, "celdaYearCortoRosado",
                    117);

            // Perforacion
            createSumMultiCellFormula(sheetResumen, 363, celda, "tresDecimalesBold",
                    364, 367);

            // Arrastre
            createSumMultiCellFormula(sheetResumen, 364, celda, "tresDecimales",
                    365, 366);

            // terminados
            createSumMultiCellFormula(sheetResumen, 367, celda, "tresDecimales",
                    368, 369);

            // tecnologías
            makeSumCellFormula(sheetResumen, 377, celda, "tresDecimalesBold",
                    378, 385);

            // total pozos productores generadores
            makeSumMultiCellFormula(sheetResumen, 386, celda, "tresDecimalesBoldRosado",
                    370, 377, 363);

            // No. Pozos Trabajados Inyectores de Disposición de Agua
            createSumMultiCellFormula(sheetResumen, 388, celda, "celdaYearCortoVerde",
                    117);

            //Total Pozos Trabajados Inyectores de Disposición de Agua
            createSumMultiCellFormula(sheetResumen, 392, celda, "sinDecimalBoldVerde",
                    389, 390, 391);

            // T/A Pozos Trabajados Inyectores de Disposición de Agua
            createSumMultiCellFormula(sheetResumen, 394, celda, "celdaYearCortoVerde",
                    388);

            // Total T/A Trabajados Inyectores de Disposición de Agua
            createSumMultiCellFormula(sheetResumen, 397, celda, "tresDecimalesBoldVerde",
                    395, 396);

            // MMBs PozosTrabajados Inyectores de Disposición de Agua
            createSumMultiCellFormula(sheetResumen, 399, celda, "celdaYearCortoVerde",
                    394);

            // Total MMBs Pozos Trabajados Inyectores de Disposición de Agua
            createSumMultiCellFormula(sheetResumen, 403, celda, "sinDecimalBoldVerde",
                    400, 401, 402);

            // Volumen de Agua de Disposición Inyectada (MBDA)
            createSumMultiCellFormula(sheetResumen, 405, celda, "celdaYearCortoVerde",
                    399);

            // Total Volumen de Agua de Disposición Inyectada (MBDA)
            createSumMultiCellFormula(sheetResumen, 409, celda, "sinDecimalBoldVerde",
                    406, 407, 408);

            // VVolumen de Agua de Disposición Inyectada del Negocio (MBDA)
            createSumMultiCellFormula(sheetResumen, 411, celda, "celdaYearCortoVerde",
                    405);

            // Total Volumen de Agua de Disposición Inyectada del Negocio (MBDA)
            createSumMultiCellFormula(sheetResumen, 415, celda, "sinDecimalBoldVerde",
                    412, 413, 414);

            // Servicios a Pozos Prod. C/T
            createSumMultiCellFormula(sheetResumen, 417, celda, "celdaYearCortoVerdeClaro",
                    411);

            // Servicios a Pozos Prod. S/T
            createSumMultiCellFormula(sheetResumen, 429, celda, "celdaYearCortoVioleta",
                    417);

            // Servicios a Pozos Inyec.S/T
            createSumMultiCellFormula(sheetResumen, 434, celda, "celdaYearCortoVioleta",
                    429);

            // Captura de Información Pozos Prod.
            createSumMultiCellFormula(sheetResumen, 438, celda, "celdaYearCortoVioleta",
                    434);

            // Captura de Información Pozos Inyect.
            createSumMultiCellFormula(sheetResumen, 441, celda, "celdaYearCortoVioleta",
                    438);

            // Abandono de Pozos Con Taladro.
            createSumMultiCellFormula(sheetResumen, 445, celda, "celdaYearCortoNaranja",
                    429);

            // Abandono de Pozos Sin Taladro.
            createSumMultiCellFormula(sheetResumen, 450, celda, "celdaYearCortoNaranja",
                    445);

            // Equipos disponible.
            createSumMultiCellFormula(sheetResumen, 454, celda, "celdaYearCortoCrema",
                    450);

            // Perforación
            createSumMultiCellFormula(sheetResumen, 455, celda, "sinDecimalesBold",
                    456, 459);

            // perforación generadora
            createSumMultiCellFormula(sheetResumen, 456, celda, "sinDecimales",
                    457, 458);

            // total equipos activos
            createSumMultiCellFormula(sheetResumen, 462, celda, "sinDecimalBoldCrema",
                    455, 460, 461);

            // Equipos activos fin de período.
            createSumMultiCellFormula(sheetResumen, 464, celda, "celdaYearCortoCrema",
                    445);

            // Perforación
            createSumMultiCellFormula(sheetResumen, 465, celda, "sinDecimalesBold",
                    466, 469);

            // perforación generadora
            createSumMultiCellFormula(sheetResumen, 466, celda, "sinDecimales",
                    467, 468);

            // total equipos activos
            createSumMultiCellFormula(sheetResumen, 472, celda, "sinDecimalBoldCrema",
                    465, 470, 471);

            // dias efectivos poe equipo
            createSumMultiCellFormula(sheetResumen, 474, celda, "celdaYearCortoCrema",
                    464);
            // Perforación
            createSumMultiCellFormula(sheetResumen, 475, celda, "unDecimalBoldCremaClaro",
                    476, 479);

            // perforación generadora
            createSumMultiCellFormula(sheetResumen, 476, celda, "unDecimalCremaClaro",
                    477, 478);

            // total taladro mes
            createSumMultiCellFormula(sheetResumen, 482, celda, "sinDecimalBoldCrema",
                    475, 480, 481);

            // taladro/mes
            createSumMultiCellFormula(sheetResumen, 484, celda, "celdaYearCortoCrema",
                    474);
            // Perforación
            createSumMultiCellFormula(sheetResumen, 485, celda, "unDecimalBoldCremaClaro",
                    486, 489);

            // perforación generadora
            createSumMultiCellFormula(sheetResumen, 486, celda, "unDecimalCremaClaro",
                    487, 488);

            // plan
            createDivCellFormula(sheetResumen, 487, celda, "unDecimalCremaClaro",
                    477, 4);

            // actividad complementaria
            createDivCellFormula(sheetResumen, 488, celda, "unDecimalCremaClaro",
                    478, 4);

            // perforacion no generadora
            createDivCellFormula(sheetResumen, 489, celda, "unDecimalCremaClaro",
                    479, 4);

            // ra/rc
            createDivCellFormula(sheetResumen, 490, celda, "unDecimalCremaClaro",
                    480, 4);

            // servicios
            createDivCellFormula(sheetResumen, 491, celda, "unDecimalCremaClaro",
                    481, 4);

            // total taladro mes
            createSumMultiCellFormula(sheetResumen, 492, celda, "sinDecimalBoldCrema",
                    485, 490, 491);

            // Total Prod. Diferida No Programada
            makeSumCellFormula(sheetResumen, 523, celda, "tresDecimalesNegativoBoldVerde",
                    496, 522);

            // Promedio año no Programado
            createCellPromedioFormula(sheetResumen, celda, 524,
                    "tresDecimalesNegativoBoldVerde");

            // Total Prod. Diferida Prog. (MES)
            makeSumCellFormula(sheetResumen, 534, celda, "tresDecimalesNegativoBoldVerde",
                    527, 533);

            // Promedio Año Prog. 
            createCellPromedioFormula(sheetResumen, celda, 535,
                    "tresDecimalesNegativoBoldVerde");

            // Total Prod. Diferida Prog. y No Prog. (MES)
            createSumMultiCellFormula(sheetResumen, 537, celda, "tresDecimalesNegativoBoldVerde",
                    523, 534);

            // Promedio Año Prog. Y No Prog. (MES)
            createSumMultiCellFormula(sheetResumen, 538, celda, "tresDecimalesNegativoBoldVerde",
                    524, 535);

            // valores positivos
            makeValoresPositivosCellFormula(sheetResumen, 541, celda, "dosDecimalesBoldNegativo",
                    104, 544, 540, 49);

            // factor de campo (fechas)
            createSumMultiCellFormula(sheetResumen, 543, celda, "celdaYearCorto",
                    484);

            //No. Pozos Trabajados Inyectores de Gas (RS)
            createSumMultiCellFormula(sheetResumen, 547, celda, "celdaYearCortoRojo",
                    543);

            // Total Pozos Trabajados Inyectores (RS)
            createSumMultiCellFormula(sheetResumen, 551, celda, "sinDecimalBoldRojo",
                    548, 549, 550);

            // T/A Pozos Trabajados Inyectores (RS)
            createSumMultiCellFormula(sheetResumen, 553, celda, "celdaYearCortoRojo",
                    547);

            // Total T/A Trabajados Inyectores (RS)
            createSumMultiCellFormula(sheetResumen, 556, celda, "tresDecimalesBoldRojo",
                    554, 555);

            // MMBsF Pozos Trabajados Inyectores (RS)
            createSumMultiCellFormula(sheetResumen, 558, celda, "celdaYearCortoRojo",
                    553);

            // Inyección de Gas (MMPCND) (RS)
            createSumMultiCellFormula(sheetResumen, 563, celda, "celdaYearCortoRojo",
                    558);

            // Inyección de Gas Total del Negocio (MMPCND) (RS)
            createSumMultiCellFormula(sheetResumen, 567, celda, "celdaYearCortoRojo",
                    563);

            // No. Pozos Trabajados Inyectores de Agua (RS)
            createSumMultiCellFormula(sheetResumen, 571, celda, "celdaYearCortoTurquesa",
                    567);

            // Total PozosTrabajados Inyectores de Agua (RS)
            createSumMultiCellFormula(sheetResumen, 575, celda, "sinDecimalesBoldTurquesa",
                    572, 573, 574);

            // T/A Pozos Trabajados Inyectores de Agua (RS)
            createSumMultiCellFormula(sheetResumen, 577, celda, "celdaYearCortoTurquesa",
                    571);

            // Total T/A Trabajados Inyectores de Agua (RS)
            createSumMultiCellFormula(sheetResumen, 580, celda, "tresDecimalesBoldTurquesa",
                    578, 579);

            // MMBsF Pozos Trabajados Inyectores de Agua (RS)
            createSumMultiCellFormula(sheetResumen, 582, celda, "celdaYearCortoTurquesa",
                    577);

            // Inyección de Agua (MBDA)
            createSumMultiCellFormula(sheetResumen, 587, celda, "celdaYearCortoTurquesa",
                    582);

            // Inyección Total de Agua del Negocio (MBDA)
            createSumMultiCellFormula(sheetResumen, 591, celda, "celdaYearCortoTurquesa",
                    587);

            // Pozos Activos Productores categ. 1
            createSumMultiCellFormula(sheetResumen, 595, celda, "celdaYearCortoVerdeOliva",
                    582);

            createSumMultiCellFormula(sheetResumen, 600, celda, "sinDecimalesBold",
                    596, 597, 598, 599);

            // Pozos Productores categ. 2
            createSumMultiCellFormula(sheetResumen, 602, celda, "celdaYearCortoVerdeOliva",
                    595);

            createSumMultiCellFormula(sheetResumen, 607, celda, "sinDecimalesBold",
                    603, 604, 605, 606);

            // Total Pozos Activos Cat 1 y 2
            createSumMultiCellFormula(sheetResumen, 609, celda, "sinDecimalesBoldVerdeOliva",
                    600, 607);

            // Pozos Productores Inactivos Categ 3, 5,7,8
            createSumMultiCellFormula(sheetResumen, 611, celda, "celdaYearCortoVerdeOliva",
                    602);

            createSumMultiCellFormula(sheetResumen, 616, celda, "sinDecimalesBold",
                    612, 613, 614, 615);

            // No. Pozos Trabajados Proyectos Tecnologicos Pilotos
            createSumMultiCellFormula(sheetResumen, 618, celda, "celdaYearCortoCyan",
                    611);

            // Total No. Pozos Trabajados Proyectos Tecnologicos Pilotos
            createSumMultiCellFormula(sheetResumen, 622, celda, "sinDecimalesBoldCyan",
                    619, 620, 621);

            // T/A Pozos Trabajados Proyectos Tecnologicos Pilotos
            createSumMultiCellFormula(sheetResumen, 624, celda, "celdaYearCortoCyan",
                    618);

            // Total T/A Pozos Trabajados Proyectos Tecnologicos Pilotos
            createSumMultiCellFormula(sheetResumen, 627, celda, "tresDecimalesBoldCyan",
                    625, 626);

            // MMBsF No. Pozos Trabajados Proyectos Tecnologicos Pilotos
            createSumMultiCellFormula(sheetResumen, 629, celda, "celdaYearCortoCyan",
                    624);

            // Pozos Trabajados Estratigraficos / Exploratorios / Observadores
            createSumMultiCellFormula(sheetResumen, 634, celda, "celdaYearCortoRosaClaro",
                    629);

            // Pozos Trabajados Proyectos de Infraestructura
            createSumMultiCellFormula(sheetResumen, 639, celda, "celdaYearCortoVioletaClaro",
                    634);

            // INDICADORES
            // BD/POZO
            createIfFormulaWDiv(sheetResumen, 646, celda, "tresDecimalesBoldCeleste",
                    313, 23, 1000, 313);
            // BD/POZO ARRASTRE
            createIfFormulaWDiv(sheetResumen, 647, celda, "tresDecimales",
                    314, 24, 1000, 314);
            // Plan
            createIfFormulaWDiv(sheetResumen, 648, celda, "tresDecimales",
                    315, 25, 1000, 315);
            // Actividad Complementaria
            createIfFormulaWDiv(sheetResumen, 649, celda, "tresDecimales",
                    316, 26, 1000, 316);
            // BD/POZOS TERMINADOS
            createIfFormulaWDiv(sheetResumen, 650, celda, "tresDecimales",
                    317, 27, 1000, 317);
            // Plan
            createIfFormulaWDiv(sheetResumen, 651, celda, "tresDecimales",
                    318, 28, 1000, 318);
            // Actividad Complementaria
            createIfFormulaWDiv(sheetResumen, 652, celda, "tresDecimales",
                    319, 29, 1000, 319);

            // DIAS/POZO
            createIfFormulaWDiv(sheetResumen, 654, celda, "tresDecimalesBoldCeleste",
                    313, 339, 365, 313);
            // DIAS/POZO ARRASTRE
            createIfFormulaWDiv(sheetResumen, 655, celda, "tresDecimales",
                    314, 340, 365, 314);
            // Plan
            createIfFormulaWDiv(sheetResumen, 656, celda, "tresDecimales",
                    315, 341, 365, 315);
            // Actividad Complementaria
            createIfFormulaWDiv(sheetResumen, 657, celda, "tresDecimales",
                    316, 342, 365, 316);
            // DIAS/POZOS TERMINADOS
            createIfFormulaWDiv(sheetResumen, 658, celda, "tresDecimales",
                    317, 343, 365, 317);
            // Plan
            createIfFormulaWDiv(sheetResumen, 659, celda, "tresDecimales",
                    318, 344, 365, 318);
            // Actividad Complementaria
            createIfFormulaWDiv(sheetResumen, 660, celda, "tresDecimales",
                    319, 345, 365, 319);

            // M$/BPD
            createIfFormulaWParidadCambiaria(sheetResumen, 662, celda, "tresDecimalesBoldCeleste",
                    23, 363, 23);
            // M$/BPD POZO ARRASTRE
            createIfFormulaWParidadCambiaria(sheetResumen, 663, celda, "tresDecimales",
                    24, 364, 24);
            // Plan
            createIfFormulaWParidadCambiaria(sheetResumen, 664, celda, "tresDecimales",
                    25, 365, 25);
            // Actividad Complementaria
            createIfFormulaWParidadCambiaria(sheetResumen, 665, celda, "tresDecimales",
                    26, 366, 26);
            // M$/BPD POZOS TERMINADOS
            createIfFormulaWParidadCambiaria(sheetResumen, 666, celda, "tresDecimales",
                    27, 367, 27);
            // Plan
            createIfFormulaWParidadCambiaria(sheetResumen, 667, celda, "tresDecimales",
                    28, 368, 28);
            // Actividad Complementaria
            createIfFormulaWParidadCambiaria(sheetResumen, 668, celda, "tresDecimales",
                    29, 369, 29);

            // MM$/POZO
            createIfFormulaWParidadCambiaria(sheetResumen, 670, celda, "tresDecimalesBoldCeleste",
                    313, 363, 313);
            // MM$/POZO POZO ARRASTRE
            createIfFormulaWParidadCambiaria(sheetResumen, 671, celda, "tresDecimales",
                    314, 364, 314);
            // Plan
            createIfFormulaWParidadCambiaria(sheetResumen, 672, celda, "tresDecimales",
                    315, 365, 315);
            // Actividad Complementaria
            createIfFormulaWParidadCambiaria(sheetResumen, 673, celda, "tresDecimales",
                    316, 366, 316);
            // MM$/POZO POZOS TERMINADOS
            createIfFormulaWParidadCambiaria(sheetResumen, 674, celda, "tresDecimales",
                    317, 367, 317);
            // Plan
            createIfFormulaWParidadCambiaria(sheetResumen, 675, celda, "tresDecimales",
                    318, 368, 318);
            // Actividad Complementaria
            createIfFormulaWParidadCambiaria(sheetResumen, 676, celda, "tresDecimales",
                    319, 369, 319);

            // COSTO DE GENERACIÓN  POR PERFORACIÓN M$/BPD
            createIfFormulaWParidadCambiaria(sheetResumen, 677, celda, "tresDecimalesBoldAmarillo",
                    61, 363, 61);

            // Ra/Rc - C/T
            // BD/POZO
            createMultDivFormula(sheetResumen, 680, celda, "tresDecimales",
                    30, 1000, 320);
            // DIAS/POZO
            createMultDivFormula(sheetResumen, 681, celda, "tresDecimales",
                    346, 365, 320);
            // M$/BPD
            createIfFormulaWParidadCambiaria(sheetResumen, 682, celda, "tresDecimales",
                    30, 370, 30);
            // MM$/POZO
            createDivWParidadCambiariaFormula(sheetResumen, 683, celda, "tresdecimales",
                    370, 320);
            // COSTO DE GENERACIÓN  POR RA/RC CT M$/BPD
            createIfFormulaWSumAndDiv(sheetResumen, 684, celda, "tresDecimalesBoldAmarillo",
                    62, 370, 371, 372, 62);

            // Ra/Rc - S/T
            // BD / POZO 
            createIfFormulaWDiv(sheetResumen, 687, celda, "tresDecimales",
                    321, 31, 1000, 321);
            // M$ / BPD 
            createIfFormulaWParidadCambiaria(sheetResumen, 688, celda, "tresDecimales",
                    31, 371, 31);
            // MM$ / POZO 
            createIfFormulaWParidadCambiaria(sheetResumen, 689, celda, "tresDecimales",
                    321, 371, 321);

            // Reentradas
            // BD/POZO
            createIfFormulaWDiv(sheetResumen, 692, celda, "tresDecimales",
                    322, 32, 1000, 322);
            // M$/BPD
            createIfFormulaWDiv(sheetResumen, 693, celda, "tresDecimales",
                    322, 347, 365, 322);
            // MM$/POZO
            createIfFormulaWParidadCambiaria(sheetResumen, 694, celda, "tresDecimales",
                    32, 372, 32);
            // COSTO DE GENERACIÓN  POR  IAV M$/BPD
            createDivWParidadCambiariaFormula(sheetResumen, 695, celda, "tresdecimales",
                    372, 322);

            // IAV
            // BD/POZO
            createMultDivFormula(sheetResumen, 698, celda, "tresDecimales",
                    33, 1000, 323);
            // DIAS/POZO
            createIfFormulaWParidadCambiaria(sheetResumen, 699, celda, "tresDecimales",
                    33, 373, 33);
            // M$/BPD
            createIfFormulaWParidadCambiaria(sheetResumen, 700, celda, "tresDecimales",
                    323, 373, 323);
            // MM$/POZO
            createIfFormulaWParidadCambiaria(sheetResumen, 701, celda, "tresDecimalesBoldAmarillo",
                    65, 373, 65);

            // Estimulación/Fractura
            // BD/POZO
            createIfFormulaWDiv(sheetResumen, 704, celda, "tresDecimales",
                    324, 34, 1000, 324);
            // M$/BPD
            createIfFormulaWParidadCambiaria(sheetResumen, 705, celda, "tresDecimales",
                    34, 374, 34);
            // MM$/POZO
            createIfFormulaWParidadCambiaria(sheetResumen, 706, celda, "tresDecimales",
                    324, 374, 324);
            // COSTO DE GENERACIÓN  POR  EST.M$/BPD
            createIfFormulaWParidadCambiaria(sheetResumen, 707, celda, "tresDecimalesBoldAmarillo",
                    66, 374, 66);

            // Otros - C/T
            // BD/POZO
            createIfFormulaWDiv(sheetResumen, 710, celda, "tresDecimales",
                    325, 35, 1000, 325);
            // DIAS/POZO
            createIfFormulaWDiv(sheetResumen, 711, celda, "tresDecimales",
                    325, 350, 365, 325);
            // M$/BPD
            createIfFormulaWParidadCambiaria(sheetResumen, 712, celda, "tresDecimales",
                    35, 375, 35);
            // MM$/POZO
            createIfFormulaWParidadCambiaria(sheetResumen, 713, celda, "tresDecimales",
                    325, 375, 325);
            // COSTO DE GENERACIÓN  POR  OTROS C/T.M$/BPD
            createIfFormulaWTwoSumAndDiv(sheetResumen, 714, celda, "tresDecimalesBoldAmarillo",
                    67, 375, 376, 67);

            //Otros - S/T
            // BD/POZO
            createMultDivFormula(sheetResumen, 717, celda, "tresDecimales",
                    36, 1000, 326);
            // M$/BPD
            createIfFormulaWParidadCambiaria(sheetResumen, 718, celda, "tresDecimales",
                    36, 376, 36);
            // MM$/POZO
            createIfFormulaWParidadCambiaria(sheetResumen, 719, celda, "tresDecimales",
                    326, 376, 326);

            // SERVICIOS
            // BD/POZO
            createMultDivFormula(sheetResumen, 723, celda, "tresDecimales",
                    422, 365, 420);
            // MM$/POZO
            createDivWParidadCambiariaFormula(sheetResumen, 724, celda, "tresdecimales",
                    423, 420);

            //Incorporación
            createSumMultiCellFormula(sheetResumen, 728, celda, "celdaYearCortoRosaClaro",
                    639);
            // conectados
            createSumMultiCellFormula(sheetResumen, 729, celda, "sinDecimalBoldVerdeOscuro",
                    730, 733);
            // arrastre
            createSumMultiCellFormula(sheetResumen, 730, celda, "sinDecimales",
                    731, 732);
            // terminados
            createSumMultiCellFormula(sheetResumen, 733, celda, "sinDecimales",
                    734, 735);
            // tecnologías
            makeSumMultiCellFormula(sheetResumen, 743, celda, "sinDecimalBoldCeleste",
                    744, 751);
            // Total Pozos Complet. (MEM) Prod. Gen.
            createSumMultiCellFormula(sheetResumen, 752, celda, "sinDecimalesBoldRosaClaro",
                    729, 736, 737, 739, 740, 741, 742, 743, 738);

            // MBD (INCORPORACIÓN)
            createSumMultiCellFormula(sheetResumen, 755, celda, "celdaYearCortoRosaClaro",
                    728);
            // perforación
            createSumMultiCellFormula(sheetResumen, 756, celda, "unDecimalBoldCeleste",
                    757, 760);
            // arrastre
            createSumMultiCellFormula(sheetResumen, 757, celda, "unDecimal",
                    758, 759);
            // terminados
            createSumMultiCellFormula(sheetResumen, 760, celda, "unDecimal",
                    761, 762);
            // tecnologías
            makeSumMultiCellFormula(sheetResumen, 770, celda, "sinDecimalBoldCeleste",
                    771, 778);
            // Total Pozos Complet. (MEM) Prod. Gen.
            createSumMultiCellFormula(sheetResumen, 779, celda, "sinDecimalesBoldRosaClaro",
                    756, 763, 764, 766, 767, 768, 769, 770, 765);

            // N° Pozos Completados Mecanicamente
            createSumMultiCellFormula(sheetResumen, 781, celda, "celdaYearCortoEsmeralda",
                    755);
            // perforación
            createSumMultiCellFormula(sheetResumen, 786, celda, "sinDecimalesBoldCremaClaro",
                    782, 783, 784, 785);

            // Potencial Pozos Completados Mecanicamente
            createSumMultiCellFormula(sheetResumen, 788, celda, "celdaYearCortoGuayaba",
                    781);
            // Total
            createSumMultiCellFormula(sheetResumen, 793, celda, "sinDecimalesBoldCremaClaro",
                    789, 790, 791, 792);
        }
    }

    private void createQueryCellFormulas(XSSFSheet sheetResumen) {
        Row row;
        Cell cell;

        MakeQueriesToDb mqtdb = new MakeQueriesToDb();
        //mqtdb.generacionPotencialDeclinada(escenario);
    }

    private Map[] contadorProgresoTerminados(Map<Pozo, Object[]> dataOperacionalMap,
            LocalDate ldYearReport, String plan) {
        Map<Integer, Integer> progresoArrastrePlanMap = new HashMap<>();
        Map<Integer, Integer> progresoArrastreMap = new HashMap<>();
        Map<Integer, Integer> progresoPlanMap = new HashMap<>();
        Map<Integer, Integer> progresoMap = new HashMap<>();
        Map<Integer, Integer> terminadosArrastrePlanMap = new HashMap<>();
        Map<Integer, Integer> terminadosArrastreMap = new HashMap<>();
        Map<Integer, Integer> terminadosPlanMap = new HashMap<>();
        Map<Integer, Integer> terminadosMap = new HashMap<>();
        Map[] arregloMap = new Map[8];

        // inicia los mapas
        for (int i = 0; i < 13; i++) {
            progresoArrastrePlanMap.put(i, 0);
            progresoArrastreMap.put(i, 0);
            progresoPlanMap.put(i, 0);
            progresoMap.put(i, 0);
            terminadosArrastrePlanMap.put(i, 0);
            terminadosArrastreMap.put(i, 0);
            terminadosPlanMap.put(i, 0);
            terminadosMap.put(i, 0);
        }

        int contadorProgreso;
        int contadorTerminados;

        for (Map.Entry<Pozo, Object[]> mapa : dataOperacionalMap.entrySet()) {
            Pozo pozo = mapa.getKey();
            Object[] datos = mapa.getValue();

            Date fechaInicio = (Date) datos[17];
            Date fechaFinPrd = (Date) datos[53];

            if (fechaFinPrd == null) {
                continue;
            }

            int currentYear = ldYearReport.getYear();

            LocalDate ldInicio = parseDate(fechaInicio);
            LocalDate ldFinPrd = parseDate(fechaFinPrd);

            // pozos en progreso
            if (ldInicio.getYear() < currentYear) {
                if (pozo.getPlan().equalsIgnoreCase(plan)) {
                    contadorProgreso = progresoArrastrePlanMap.get(0) + 1;
                    progresoArrastrePlanMap.put(0, contadorProgreso);
                } else {
                    contadorProgreso = progresoArrastreMap.get(0) + 1;
                    progresoArrastreMap.put(0, contadorProgreso);
                }
            } else if (ldInicio.getYear() == currentYear) {
                if (pozo.getPlan().equalsIgnoreCase(plan)) {
                    contadorProgreso = progresoPlanMap.get(ldInicio.getMonth().getValue()) + 1;
                    progresoPlanMap.put(ldInicio.getMonth().getValue(), contadorProgreso);
                } else {
                    contadorProgreso = progresoMap.get(ldInicio.getMonth().getValue()) + 1;
                    progresoMap.put(ldInicio.getMonth().getValue(), contadorProgreso);
                }
            }

            // pozos terminados
            if (ldFinPrd.getYear() < currentYear) {
                if (pozo.getPlan().equalsIgnoreCase(plan)) {
                    contadorProgreso = progresoArrastrePlanMap.get(0) - 1;
                    progresoArrastrePlanMap.put(0, contadorProgreso);
                    contadorTerminados = terminadosArrastrePlanMap.get(0) + 1;
                    terminadosArrastrePlanMap.put(0, contadorTerminados);
                } else {
                    contadorProgreso = progresoArrastreMap.get(0) - 1;
                    progresoArrastreMap.put(0, contadorProgreso);
                    contadorTerminados = terminadosMap.get(0) + 1;
                    terminadosMap.put(0, contadorTerminados);
                }
            } else if (ldFinPrd.getYear() == currentYear) {
                if (pozo.getPlan().equalsIgnoreCase(plan)) {
                    if (ldInicio.getYear() < currentYear) {
                        contadorTerminados = terminadosArrastrePlanMap.get(ldFinPrd.getMonth().getValue()) + 1;
                        terminadosArrastrePlanMap.put(ldFinPrd.getMonth().getValue(), contadorTerminados);
                    } else if (ldInicio.getYear() == currentYear) {
                        contadorTerminados = terminadosPlanMap.get(ldFinPrd.getMonth().getValue()) + 1;
                        terminadosPlanMap.put(ldFinPrd.getMonth().getValue(), contadorTerminados);
                    }
                } else if (ldInicio.getYear() < currentYear) {
                    contadorTerminados = terminadosArrastreMap.get(ldFinPrd.getMonth().getValue()) + 1;
                    terminadosArrastreMap.put(ldFinPrd.getMonth().getValue(), contadorTerminados);
                } else if (ldInicio.getYear() == currentYear) {
                    contadorTerminados = terminadosMap.get(ldFinPrd.getMonth().getValue()) + 1;
                    terminadosMap.put(ldFinPrd.getMonth().getValue(), contadorTerminados);
                }
            }
        }

        arregloMap[0] = progresoArrastrePlanMap;
        arregloMap[1] = progresoArrastreMap;
        arregloMap[2] = progresoPlanMap;
        arregloMap[3] = progresoMap;
        arregloMap[4] = terminadosArrastrePlanMap;
        arregloMap[5] = terminadosArrastreMap;
        arregloMap[6] = terminadosPlanMap;
        arregloMap[7] = terminadosMap;

        return arregloMap;
    }

    /**
     * Contador de T/A (Taladro año), devuelve un mapa para cada tipo de
     * estadistica
     */
    private Map[] contadorTaladroAgno(Map<Pozo, Object[]> dataOperacionalMap,
            LocalDate ldYearReport, String plan) {
        Map[] arregloMap = new Map[8];

        // la clave es el mes, comenzando en cero
        // el valor es calculo de T/A para cada mes 
        // T/A =  (cantidad de dias del taladro en el mes) / 365 
        Map<Integer, Double> progresoArrastrePlanMap = new HashMap<>();
        Map<Integer, Double> progresoArrastreMap = new HashMap<>();
        Map<Integer, Double> progresoPlanMap = new HashMap<>();
        Map<Integer, Double> progresoMap = new HashMap<>();
        Map<Integer, Double> terminadosArrastrePlanMap = new HashMap<>();
        Map<Integer, Double> terminadosArrastreMap = new HashMap<>();
        Map<Integer, Double> terminadosPlanMap = new HashMap<>();
        Map<Integer, Double> terminadosMap = new HashMap<>();

        for (int i = 1; i < 13; i++) {
            progresoArrastrePlanMap.put(i, 0.0);
            progresoArrastreMap.put(i, 0.0);
            progresoPlanMap.put(i, 0.0);
            progresoMap.put(i, 0.0);
            terminadosArrastrePlanMap.put(i, 0.0);
            terminadosArrastreMap.put(i, 0.0);
            terminadosPlanMap.put(i, 0.0);
            terminadosMap.put(i, 0.0);
        }

        for (Map.Entry<Pozo, Object[]> mapa : dataOperacionalMap.entrySet()) {
            Pozo pozo = mapa.getKey();
            Object[] datos = mapa.getValue();

            Date fechaInicioSup = (Date) datos[17];
            Date fechaFinSup = (Date) datos[18];
            int diasActSup = (Integer) datos[19];

            Date fechaInicioInter = (Date) datos[45];
            Date fechaFinInter = (Date) datos[46];
            int diasActInter = 0;
            if (fechaInicioInter != null) {
                diasActInter = (Integer) datos[47];
            } else {
                continue;
            }

            Date fechaInicioPrd = (Date) datos[52];
            Date fechaFinPrd = (Date) datos[53];
            int diasActPrd = (Integer) datos[54];

            if (fechaFinPrd == null) {
                continue;
            }

            int currentYear = ldYearReport.getYear();

            LocalDate ldInicioSup = parseDate(fechaInicioSup);
            LocalDate ldFinSup = parseDate(fechaFinSup);
            LocalDate ldInicioInter = parseDate(fechaInicioInter);
            LocalDate ldFinInter = parseDate(fechaFinInter);
            LocalDate ldInicioPrd = parseDate(fechaInicioPrd);
            LocalDate ldFinPrd = parseDate(fechaFinPrd);

            // pozos en progreso
            Double valor = 0.0;
            int diasTranscurridos = 0;
            int diasAgnoActual = ldYearReport.lengthOfYear();;

            // arrastre
            if (ldInicioSup.getYear() < currentYear) {
                if (pozo.getPlan().equalsIgnoreCase(plan)) {
                    // en el plan
                    if (ldFinPrd.getYear() == currentYear) {
                        //termina año actual
                        for (int mes = 1; mes < 13; mes++) {
                            if (ldFinPrd.getMonthValue() == mes) {
                                diasTranscurridos = ldFinPrd.getDayOfMonth();
                                valor = Double.valueOf(diasTranscurridos * 1.0 / diasAgnoActual);
                                valor += progresoArrastrePlanMap.get(mes);
                                progresoArrastrePlanMap.put(mes, valor);
                            } else if (mes < ldFinPrd.getMonthValue()) {
                                boolean leap = ldFinPrd.isLeapYear();
                                Month month = Month.of(mes);
                                diasTranscurridos = month.length(leap);
                                valor = Double.valueOf(diasTranscurridos * 1.0 / diasAgnoActual);
                                valor += progresoArrastrePlanMap.get(mes);
                                progresoArrastrePlanMap.put(mes, valor);
                            } else if (mes > ldFinPrd.getMonthValue()) {
                                break;
                            }
                        }
                    } else {
                        //termina año próximo año
                        for (int mes = 1; mes < 13; mes++) {
                            boolean leap = ldFinPrd.isLeapYear();
                            Month month = Month.of(mes);
                            diasTranscurridos = month.length(leap);
                            valor = diasTranscurridos * 1.0 / diasAgnoActual;
                            valor += progresoArrastrePlanMap.get(mes);
                            progresoArrastrePlanMap.put(mes, valor);
                        }
                    }
                } else {
                    // actividad complementaria
                    if (ldFinPrd.getYear() == currentYear) {
                        //termina año actual
                        for (int mes = 1; mes < 13; mes++) {
                            if (ldFinPrd.getMonthValue() == mes) {
                                diasTranscurridos = ldFinPrd.getDayOfMonth();
                                valor = diasTranscurridos * 1.0 / diasAgnoActual;
                                valor += progresoArrastreMap.get(mes);
                                progresoArrastreMap.put(mes, valor);
                            } else if (mes < ldFinPrd.getMonthValue()) {
                                boolean leap = ldFinPrd.isLeapYear();
                                Month month = Month.of(mes);
                                diasTranscurridos = month.length(leap);
                                valor = diasTranscurridos * 1.0 / diasAgnoActual;
                                valor += progresoArrastreMap.get(mes);
                                progresoArrastreMap.put(mes, valor);
                            } else if (mes > ldFinPrd.getMonthValue()) {
                                break;
                            }
                        }
                    } else {
                        //termina año próximo año
                        for (int mes = 1; mes < 13; mes++) {
                            boolean leap = ldFinPrd.isLeapYear();
                            Month month = Month.of(mes);
                            diasTranscurridos = month.length(leap);
                            diasAgnoActual = ldYearReport.lengthOfYear();
                            valor = diasTranscurridos * 1.0 / diasAgnoActual;
                            valor += progresoArrastrePlanMap.get(mes);
                            progresoArrastrePlanMap.put(mes, valor);
                        }
                    }
                }

                // año actual
            } else if (ldInicioSup.getYear() == currentYear) {
                boolean leap = ldInicioSup.isLeapYear();
                diasAgnoActual = ldInicioSup.lengthOfYear();
                if (pozo.getPlan().equalsIgnoreCase(plan)) {
                    // en el plan
//                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                    for (int mes = 1; mes < 13; mes++) {
                        // empieza en este mes                            
                        if (ldInicioSup.getMonthValue() == mes) {
                            Month month = Month.of(mes);
                            int dias = Period
                                    .between(ldInicioSup,
                                            LocalDate.of(currentYear,
                                                    month,
                                                    month.length(leap)))
                                    .getDays();
                            valor = dias * 1.0 / diasAgnoActual;
                            valor += progresoPlanMap.get(mes);
                            progresoPlanMap.put(mes, valor);
                            continue;
                        }
                        // termina el mismo año
                        if (ldFinPrd.getYear() == currentYear) {
                            if (mes < ldFinPrd.getMonthValue()) {
                                Month month = Month.of(mes);
                                valor = month.length(leap) * 1.0 / diasAgnoActual;
                                valor += progresoPlanMap.get(mes);
                                progresoPlanMap.put(mes, valor);
                            } else if (mes == ldFinPrd.getMonthValue()) {
                                valor = ldFinPrd.getDayOfMonth() * 1.0 / diasAgnoActual;
                                valor += progresoPlanMap.get(mes);
                                progresoPlanMap.put(mes, valor);
                                break;
                            }
                            // termina el año siguiente
                        } else if (ldFinPrd.getYear() > currentYear) {
                            Month month = Month.of(mes);
                            valor = month.length(leap) * 1.0 / diasAgnoActual;
                            valor += progresoPlanMap.get(mes);
                            progresoPlanMap.put(mes, valor);
                        }
                    }
//                    }
                } else {
                    // actividad complementaria
                    for (int mes = 1; mes < 13; mes++) {
                        // empieza en este mes                            
                        if (ldInicioSup.getMonthValue() == mes) {
                            Month month = Month.of(mes);
                            int dias = Period
                                    .between(ldInicioSup,
                                            LocalDate.of(currentYear,
                                                    month,
                                                    month.length(leap)))
                                    .getDays();
                            valor = dias * 1.0 / diasAgnoActual;
                            valor += progresoMap.get(mes);
                            progresoMap.put(mes, valor);
                            continue;
                        }
                        // termina el mismo año
                        if (ldFinPrd.getYear() == currentYear) {
                            if (mes < ldFinPrd.getMonthValue()) {
                                Month month = Month.of(mes);
                                valor = month.length(leap) * 1.0 / diasAgnoActual;
                                valor += progresoMap.get(mes);
                                progresoMap.put(mes, valor);
                            } else if (mes == ldFinPrd.getMonthValue()) {
                                valor = ldFinPrd.getDayOfMonth() * 1.0 / diasAgnoActual;
                                valor += progresoMap.get(mes);
                                progresoMap.put(mes, valor);
                                break;
                            }
                            // termina el año siguiente
                        } else if (ldFinPrd.getYear() > currentYear) {
                            Month month = Month.of(mes);
                            valor = month.length(leap) * 1.0 / diasAgnoActual;
                            valor += progresoMap.get(mes);
                            progresoMap.put(mes, valor);
                        }
                    }
                }
            }

            // calculo de terminados
            int diasTerminados = 0;
            double taTerminadoMes = 0.0;
            for (int mes = 1; mes < 13; mes++) {
                if (pozo.getPlan().equalsIgnoreCase(plan)) {
                    // en el plan
                    // arrastre
                    if (ldInicioSup.getYear() < currentYear) {
                        diasTerminados = taDiasTerminados(mes,
                                ldInicioSup, ldFinSup, diasActSup,
                                ldInicioInter, ldFinInter, diasActInter,
                                ldInicioPrd, ldFinPrd, diasActPrd,
                                currentYear);
                        taTerminadoMes = diasTerminados * 1.0 / ldYearReport.lengthOfYear()
                                + terminadosArrastrePlanMap.get(mes);
                        terminadosArrastrePlanMap.put(mes, taTerminadoMes);
                    } else if (ldInicioSup.getYear() == currentYear) {
                        // actual
                        diasTerminados = taDiasTerminados(mes,
                                ldInicioSup, ldFinSup, diasActSup,
                                ldInicioInter, ldFinInter, diasActInter,
                                ldInicioPrd, ldFinPrd, diasActPrd,
                                currentYear);
                        taTerminadoMes = diasTerminados * 1.0 / ldYearReport.lengthOfYear()
                                + terminadosPlanMap.get(mes);
                        terminadosPlanMap.put(mes, taTerminadoMes);
                    }
                } else {
                    // complementarias
                    // arrastre
                    if (ldInicioSup.getYear() < currentYear) {
                        diasTerminados = taDiasTerminados(mes,
                                ldInicioSup, ldFinSup, diasActSup,
                                ldInicioInter, ldFinInter, diasActInter,
                                ldInicioPrd, ldFinPrd, diasActPrd,
                                currentYear);
                        taTerminadoMes = diasTerminados * 1.0 / ldYearReport.lengthOfYear()
                                + terminadosArrastreMap.get(mes);
                        terminadosArrastreMap.put(mes, taTerminadoMes);
                    } else if (ldInicioSup.getYear() == currentYear) {
                        // actual
                        diasTerminados = taDiasTerminados(mes,
                                ldInicioSup, ldFinSup, diasActSup,
                                ldInicioInter, ldFinInter, diasActInter,
                                ldInicioPrd, ldFinPrd, diasActPrd,
                                currentYear);
                        taTerminadoMes = diasTerminados * 1.0 / ldYearReport.lengthOfYear()
                                + terminadosMap.get(mes);
                        terminadosMap.put(mes, taTerminadoMes);
                    }
                }
            }
        }
        arregloMap[0] = progresoArrastrePlanMap;
        arregloMap[1] = progresoArrastreMap;
        arregloMap[2] = progresoPlanMap;
        arregloMap[3] = progresoMap;
        arregloMap[4] = terminadosArrastrePlanMap;
        arregloMap[5] = terminadosArrastreMap;
        arregloMap[6] = terminadosPlanMap;
        arregloMap[7] = terminadosMap;

        return arregloMap;
    }

    private Map[] contadorMMBs(Map<Pozo, Object[]> dataOperacionalMap,
            LocalDate ldYearReport, String plan ) {
        Map[] arregloMap = new Map[8];

        // la clave es el mes, comenzando en cero
        // el valor es calculo de T/A para cada mes 
        // T/A =  (cantidad de dias del taladro en el mes) / 365 
        Map<Integer, Double> progresoArrastrePlanMap = new HashMap<>();
        Map<Integer, Double> progresoArrastreMap = new HashMap<>();
        Map<Integer, Double> progresoPlanMap = new HashMap<>();
        Map<Integer, Double> progresoMap = new HashMap<>();
        Map<Integer, Double> terminadosArrastrePlanMap = new HashMap<>();
        Map<Integer, Double> terminadosArrastreMap = new HashMap<>();
        Map<Integer, Double> terminadosPlanMap = new HashMap<>();
        Map<Integer, Double> terminadosMap = new HashMap<>();

        for (int i = 1; i < 13; i++) {
            progresoArrastrePlanMap.put(i, 0.0);
            progresoArrastreMap.put(i, 0.0);
            progresoPlanMap.put(i, 0.0);
            progresoMap.put(i, 0.0);
            terminadosArrastrePlanMap.put(i, 0.0);
            terminadosArrastreMap.put(i, 0.0);
            terminadosPlanMap.put(i, 0.0);
            terminadosMap.put(i, 0.0);
        }

        for (Map.Entry<Pozo, Object[]> mapa : dataOperacionalMap.entrySet()) {
            Pozo pozo = mapa.getKey();
            Object[] datos = mapa.getValue();

            Date fechaInicioSup = (Date) datos[17];
            Date fechaFinSup = (Date) datos[18];
            int diasActSup = (Integer) datos[19];

            Date fechaInicioInter = (Date) datos[45];
            Date fechaFinInter = (Date) datos[46];
            int diasActInter = 0;
            if (fechaInicioInter != null) {
                diasActInter = (Integer) datos[47];
            } else {
                continue;
            }

            Date fechaInicioPrd = (Date) datos[52];
            Date fechaFinPrd = (Date) datos[53];
            int diasActPrd = (Integer) datos[54];

            double costoPerf = (Double) datos[57];
            double costoLoc = (Double) datos[90];

            if (fechaFinPrd == null) {
                continue;
            }

            int currentYear = ldYearReport.getYear();

            LocalDate ldInicioSup = parseDate(fechaInicioSup);
            LocalDate ldFinSup = parseDate(fechaFinSup);
            LocalDate ldInicioInter = parseDate(fechaInicioInter);
            LocalDate ldFinInter = parseDate(fechaFinInter);
            LocalDate ldInicioPrd = parseDate(fechaInicioPrd);
            LocalDate ldFinPrd = parseDate(fechaFinPrd);

            for (int mes = 1; mes < 13; mes++) {
                int dias = 0;
                double monto = 0.0;
                // progreso arrastre
                if (ldInicioSup.getYear() < currentYear) {
                    dias = taDiasProgreso(mes, ldInicioSup, ldFinSup,
                            diasActSup, ldInicioInter, ldFinInter,
                            diasActInter, ldInicioPrd, ldFinPrd,
                            diasActPrd, currentYear);
                    // en el plan
                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                        if (dias != 0) {
                            dias += taProductoresTerminados(mes, ldInicioSup,
                                    ldFinSup, diasActSup, ldInicioInter,
                                    ldFinInter, diasActInter, ldInicioPrd,
                                    ldFinPrd, diasActPrd, currentYear);
                            monto = (dias * 58.399) + progresoArrastrePlanMap.get(mes);
                            progresoArrastrePlanMap.put(mes, monto);
                        }
                    } else { // fuera del plan
                        if (dias != 0) {
                            dias += taProductoresTerminados(mes, ldInicioSup,
                                    ldFinSup, diasActSup, ldInicioInter,
                                    ldFinInter, diasActInter, ldInicioPrd,
                                    ldFinPrd, diasActPrd, currentYear);
                            monto = (dias * 58.399) + progresoArrastreMap.get(mes);
                            progresoArrastreMap.put(mes, monto);
                        }
                    }
                } else if (ldInicioSup.getYear() == currentYear) { // progreso actual
                    dias = taDiasProgreso(mes, ldInicioSup, ldFinSup,
                            diasActSup, ldInicioInter, ldFinInter,
                            diasActInter, ldInicioPrd, ldFinPrd,
                            diasActPrd, currentYear);
                    // en el plan
                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                        if (dias != 0) {
                            dias += taProductoresTerminados(mes, ldInicioSup,
                                    ldFinSup, diasActSup, ldInicioInter,
                                    ldFinInter, diasActInter, ldInicioPrd,
                                    ldFinPrd, diasActPrd, currentYear);
                            monto = (dias * 58.399) + progresoPlanMap.get(mes);
                            progresoArrastrePlanMap.put(mes, monto);
                        }
                    } else { // fuera del plan
                        if (dias != 0) {
                            dias += taProductoresTerminados(mes, ldInicioSup,
                                    ldFinSup, diasActSup, ldInicioInter,
                                    ldFinInter, diasActInter, ldInicioPrd,
                                    ldFinPrd, diasActPrd, currentYear);
                            monto = (dias * 58.399) + progresoMap.get(mes);
                            progresoArrastreMap.put(mes, monto);
                        }
                    }
                }

                // terminados
                //arrastre
                if (ldInicioSup.getYear() < currentYear) {
                    // en el plan
                    dias = taProductoresTerminados(mes, ldInicioSup,
                            ldFinSup, diasActSup, ldInicioInter,
                            ldFinInter, diasActInter, ldInicioPrd,
                            ldFinPrd, diasActPrd, currentYear);
                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                        if (dias != 0) {
                            monto = (costoPerf + costoLoc) + terminadosArrastrePlanMap.get(mes);
                            terminadosArrastrePlanMap.put(mes, monto);
                        }
                    } else { // complementarias
                        if (dias != 0) {
                            monto = (costoPerf + costoLoc) + terminadosArrastreMap.get(mes);
                            terminadosArrastreMap.put(mes, monto);
                        }
                    }
                } else if (ldInicioSup.getYear() == currentYear) { // actual
                    // en el plan
                    dias = taProductoresTerminados(mes, ldInicioSup,
                            ldFinSup, diasActSup, ldInicioInter,
                            ldFinInter, diasActInter, ldInicioPrd,
                            ldFinPrd, diasActPrd, currentYear);
                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                        if (dias != 0) {
                            monto = (costoPerf + costoLoc) + terminadosPlanMap.get(mes);
                            terminadosPlanMap.put(mes, monto);
                        }
                    } else { // complementarias
                        if (dias != 0) {
                            monto = (costoPerf + costoLoc) + terminadosMap.get(mes);
                            terminadosMap.put(mes, monto);
                        }
                    }
                }

            }
        }

        arregloMap[0] = progresoArrastrePlanMap;
        arregloMap[1] = progresoArrastreMap;
        arregloMap[2] = progresoPlanMap;
        arregloMap[3] = progresoMap;
        arregloMap[4] = terminadosArrastrePlanMap;
        arregloMap[5] = terminadosArrastreMap;
        arregloMap[6] = terminadosPlanMap;
        arregloMap[7] = terminadosMap;

        return arregloMap;
    }

    private Map[] contadorCompletadosMem(Map<Pozo, Object[]> dataOperacionalMap,
            LocalDate ldYearReport, String plan) {
        Map[] arregloMap = new Map[12];

        Map<Integer, Integer> aceptadosMemArrastrePlanMap = new HashMap<>();
        Map<Integer, Integer> aceptadosMemArrastreMap = new HashMap<>();
        Map<Integer, Integer> aceptadosMemActualPlanMap = new HashMap<>();
        Map<Integer, Integer> aceptadosMemActualMap = new HashMap<>();
        Map<Integer, Double> taproductoresMemArrastrePlanMap = new HashMap<>();
        Map<Integer, Double> taproductoresMemArrastreMap = new HashMap<>();
        Map<Integer, Double> taproductoresMemActualPlanMap = new HashMap<>();
        Map<Integer, Double> taproductoresMemActualMap = new HashMap<>();
        Map<Integer, Double> mmbscompletadosMemArrastrePlanMap = new HashMap<>();
        Map<Integer, Double> mmbscompletadosMemArrastreMap = new HashMap<>();
        Map<Integer, Double> mmbscompletadosMemActualPlanMap = new HashMap<>();
        Map<Integer, Double> mmbscompletadosMemActualMap = new HashMap<>();

        for (int i = 1; i < 13; i++) {
            aceptadosMemArrastrePlanMap.put(i, 0);
            aceptadosMemArrastreMap.put(i, 0);
            aceptadosMemActualPlanMap.put(i, 0);
            aceptadosMemActualMap.put(i, 0);
            taproductoresMemArrastrePlanMap.put(i, 0.0);
            taproductoresMemArrastreMap.put(i, 0.0);
            taproductoresMemActualPlanMap.put(i, 0.0);
            taproductoresMemActualMap.put(i, 0.0);
            mmbscompletadosMemArrastrePlanMap.put(i, 0.0);
            mmbscompletadosMemArrastreMap.put(i, 0.0);
            mmbscompletadosMemActualPlanMap.put(i, 0.0);
            mmbscompletadosMemActualMap.put(i, 0.0);
        }

        for (Map.Entry<Pozo, Object[]> mapa : dataOperacionalMap.entrySet()) {

            Object[] datos = mapa.getValue();
            Optional<Date> opFechaIntermedio = Optional.ofNullable((Date) datos[45]);
            

            // se determina que es pozo productor
            if (opFechaIntermedio.isPresent()) {
                int cuenta = 0;
                int diasTaladro = 0;
                double monto = 0.0;
                
                Pozo pozo = mapa.getKey();
                Date fechaInicioSup = (Date) datos[17];
                
                Date fechaAceptacion = (Date) datos[83];
                LocalDate ldInicioSup = parseDate(fechaInicioSup);
                LocalDate ldAceptacion = parseDate(fechaAceptacion);

                int currentYear = ldYearReport.getYear();
                LocalDate firstDayCurrentYear = LocalDate.of(currentYear, 1, 1);
                
                String nombreMem = (String) datos[2];
                
                // determina que tiene nombre del MEM
                if (null != nombreMem && !nombreMem.isEmpty()) {
                    
                    int mesAceptacion = ldAceptacion.getMonthValue();
                    diasTaladro = (Integer) datos[21] + (Integer) datos[49]
                            + (Integer) datos[56] + (Integer) datos[66];
                    
                    double costTotal = (Double)datos[59] + (Double)datos[92]
                            + (Double)datos[69] + (Double)datos[79];
                    
                    // en arrastre
                    if (ldInicioSup.isBefore(firstDayCurrentYear)) {
                        // si fue aceptado en el año actual
                        if (ldAceptacion.getYear() == currentYear) {
                            // en el plan
                            if (pozo.getPlan().equalsIgnoreCase(plan)) {
                                // cuenta de pozos
                                cuenta = aceptadosMemArrastrePlanMap.get(mesAceptacion) + 1;
                                aceptadosMemArrastrePlanMap.put(mesAceptacion, cuenta);
                                
                                // ta
                                monto = taproductoresMemArrastrePlanMap.get(mesAceptacion)
                                        + diasTaladro * 1.0/365.0;
                                taproductoresMemArrastrePlanMap.put(mesAceptacion, monto);
                                
                                // mmbs
                                monto = mmbscompletadosMemArrastrePlanMap.get(mesAceptacion)
                                        + costTotal / 1000;
                                mmbscompletadosMemArrastrePlanMap.put(mesAceptacion, monto);
                                
                            } else { // complementarias
                                cuenta = aceptadosMemArrastreMap.get(mesAceptacion) + 1;
                                aceptadosMemArrastreMap.put(mesAceptacion, cuenta);
                                
                                monto = taproductoresMemArrastreMap.get(mesAceptacion)
                                        + diasTaladro * 1.0 / 365.0;
                                taproductoresMemArrastreMap.put(mesAceptacion, monto);
                                
                                monto = mmbscompletadosMemArrastreMap.get(mesAceptacion)
                                        + costTotal / 1000;
                                mmbscompletadosMemArrastreMap.put(mesAceptacion, monto);
                            }
                        } else if(ldAceptacion.getYear() < currentYear){
                            // plan
                            if(pozo.getPlan().equalsIgnoreCase(plan)){
                                // cuenta pozos
                                cuenta = aceptadosMemArrastrePlanMap.get(1) + 1;
                                aceptadosMemArrastrePlanMap.put(1, cuenta);

                                // ta
                                monto = taproductoresMemArrastrePlanMap.get(1)
                                        + diasTaladro * 1.0 / 365.0;
                                taproductoresMemArrastrePlanMap.put(1, monto);

                                // mmbs
                                monto = mmbscompletadosMemArrastrePlanMap.get(1)
                                        + costTotal / 1000;
                                mmbscompletadosMemArrastrePlanMap.put(1, monto);
                            } else {  //complementarias
                                cuenta = aceptadosMemArrastreMap.get(1) + 1;
                                aceptadosMemArrastreMap.put(1, cuenta);

                                monto = taproductoresMemArrastreMap.get(1)
                                        + diasTaladro * 1.0 / 365.0;
                                taproductoresMemArrastreMap.put(1, monto);

                                monto = mmbscompletadosMemArrastreMap.get(1)
                                        + costTotal / 1000;
                                mmbscompletadosMemArrastreMap.put(1, monto);
                            }
                        }
                    } else if (ldInicioSup.getYear() == currentYear) { // actual
                        // si fue aceptado en el año actual
                        if (ldAceptacion.getYear() == currentYear) {
                            // en el plan
                            if (pozo.getPlan().equalsIgnoreCase(plan)) {
                                cuenta = aceptadosMemActualPlanMap.get(ldAceptacion.getMonthValue()) + 1;
                                aceptadosMemActualPlanMap.put(ldAceptacion.getMonthValue(), cuenta);
                                
                                monto = taproductoresMemActualPlanMap.get(mesAceptacion)
                                        + diasTaladro * 1.0 / 365.0;
                                taproductoresMemActualPlanMap.put(mesAceptacion, monto);
                                
                                monto = mmbscompletadosMemActualPlanMap.get(mesAceptacion)
                                        + costTotal / 1000;
                                mmbscompletadosMemActualPlanMap.put(mesAceptacion, monto);
                            } else { // complementarias
                                cuenta = aceptadosMemActualMap.get(ldAceptacion.getMonthValue()) + 1;
                                aceptadosMemActualMap.put(ldAceptacion.getMonthValue(), cuenta);
                                
                                monto = taproductoresMemActualMap.get(mesAceptacion)
                                        + diasTaladro * 1.0 / 365.0;
                                taproductoresMemActualMap.put(mesAceptacion, monto);
                                
                                monto = mmbscompletadosMemActualMap.get(mesAceptacion)
                                        + costTotal / 1000;
                                mmbscompletadosMemActualMap.put(mesAceptacion, monto);
                            }
                        }
                    }
                }
            }
        }

        arregloMap[0] = aceptadosMemArrastrePlanMap;
        arregloMap[1] = aceptadosMemArrastreMap;
        arregloMap[2] = aceptadosMemActualPlanMap;
        arregloMap[3] = aceptadosMemActualMap;
        arregloMap[4] = taproductoresMemArrastrePlanMap;
        arregloMap[5] = taproductoresMemArrastreMap;
        arregloMap[6] = taproductoresMemActualPlanMap;
        arregloMap[7] = taproductoresMemActualMap;
        arregloMap[8] = mmbscompletadosMemArrastrePlanMap;
        arregloMap[9] = mmbscompletadosMemArrastreMap;
        arregloMap[10] = mmbscompletadosMemActualPlanMap;
        arregloMap[11] = mmbscompletadosMemActualMap;

        return arregloMap;
    }

    private Map[] contadorConectados(Map<Pozo, Object[]> dataOperacionalMap,
            LocalDate ldYearReport, String plan) {
        Map[] arregloMap = new Map[4];

        // la clave es el mes, comenzando en cero
        // el valor es calculo de T/A para cada mes 
        // T/A =  (cantidad de dias del taladro en el mes) / 365 
        Map<Integer, Integer> conectadosArrastrePlanMap = new HashMap<>();
        Map<Integer, Integer> conectadosArrastreMap = new HashMap<>();
        Map<Integer, Integer> conectadosActualPlanMap = new HashMap<>();
        Map<Integer, Integer> conectadosActualMap = new HashMap<>();

        for (int i = 1; i < 13; i++) {
            conectadosArrastrePlanMap.put(i, 0);
            conectadosArrastreMap.put(i, 0);
            conectadosActualPlanMap.put(i, 0);
            conectadosActualMap.put(i, 0);
        }

        for (Map.Entry<Pozo, Object[]> mapa : dataOperacionalMap.entrySet()) {
            Pozo pozo = mapa.getKey();
            Object[] datos = mapa.getValue();

            Date fechaInicioSup = (Date) datos[17];
            Optional<Date> optFechaIniInter = Optional.ofNullable((Date) datos[45]);
            Date fechaConex = (Date) datos[72];
            int diasActInter = 0;
            if (!optFechaIniInter.isPresent()) {
                continue;
            }

            int currentYear = ldYearReport.getYear();

            LocalDate ldInicioSup = parseDate(fechaInicioSup);
            LocalDate ldFechaConex = parseDate(fechaConex);
            int conectadosAntesPlan = 0;
            int conectadosAntes = 0;

            for (int mes = 1; mes < 13; mes++) {
                int counter = 0;

                // Arrastre
                if (ldInicioSup.getYear() < ldYearReport.getYear()) {
                    // en el plan
                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                        if (ldFechaConex.getYear() < ldYearReport.getYear()) {
                            conectadosAntesPlan++;
                        } else if (ldFechaConex.getYear() == ldYearReport.getYear()
                                && ldFechaConex.getMonthValue() == mes) {
                            if (mes == 1) {
                                counter = conectadosAntesPlan++;
                                conectadosArrastrePlanMap.put(mes, counter);
                            } else {
                                counter = conectadosArrastrePlanMap.get(mes) + 1;
                                conectadosArrastrePlanMap.put(mes, counter);
                            }
                        }
                    } else { // complementarios
                        if (ldFechaConex.getYear() < ldYearReport.getYear()) {
                            conectadosAntes++;
                        } else if (ldFechaConex.getYear() == ldYearReport.getYear()
                                && ldFechaConex.getMonthValue() == mes) {
                            if (mes == 1) {
                                counter = conectadosAntes++;
                                conectadosArrastreMap.put(mes, counter);
                            } else {
                                counter = conectadosArrastreMap.get(mes) + 1;
                                conectadosArrastreMap.put(mes, counter);
                            }
                        }
                    }

                    // Conectados actual
                } else if (ldInicioSup.getYear() == ldYearReport.getYear()) {
                    // en el plan
                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                        if (ldFechaConex.getYear() < ldYearReport.getYear()) {
                            conectadosAntesPlan++;
                        } else if (ldFechaConex.getYear() == ldYearReport.getYear()
                                && ldFechaConex.getMonthValue() == mes) {
                            if (mes == 1) {
                                counter = conectadosAntesPlan++;
                                conectadosActualPlanMap.put(mes, counter);
                            } else {
                                counter = conectadosActualPlanMap.get(mes) + 1;
                                conectadosActualPlanMap.put(mes, counter);
                            }
                        }
                    } else { // complementarios
                        if (ldFechaConex.getYear() < ldYearReport.getYear()) {
                            conectadosAntes++;
                        } else if (ldFechaConex.getYear() == ldYearReport.getYear()
                                && ldFechaConex.getMonthValue() == mes) {
                            if (mes == 1) {
                                counter = conectadosAntes++;
                                conectadosActualMap.put(mes, counter);
                            } else {
                                counter = conectadosActualMap.get(mes) + 1;
                                conectadosActualMap.put(mes, counter);
                            }
                        }
                    }
                } else {
                    break;
                }

            }
        }

        arregloMap[0] = conectadosArrastrePlanMap;
        arregloMap[1] = conectadosArrastreMap;
        arregloMap[2] = conectadosActualPlanMap;
        arregloMap[3] = conectadosActualMap;

        return arregloMap;
    }

    private Map[] contadorIncorporados(
            Map<Pozo, Object[]> dataOperacionalMap,
            LocalDate ldYearReport, String plan) {

        Map[] incorporadosMap = new HashMap[4];

        Map<Integer, Double> incorporadosArrastrePlanMap = new HashMap<>();
        Map<Integer, Double> incorporadosArrastreMap = new HashMap<>();
        Map<Integer, Double> incorporadosTerminadosPlanMap = new HashMap<>();
        Map<Integer, Double> incorporadosTerminadosMap = new HashMap<>();

        for (int i = 1; i < 13; i++) {
            incorporadosArrastrePlanMap.put(i, 0.0);
            incorporadosArrastreMap.put(i, 0.0);
            incorporadosTerminadosPlanMap.put(i, 0.0);
            incorporadosTerminadosMap.put(i, 0.0);
        }

        double mbdIncorporados = 0.0;
        for (Map.Entry<Pozo, Object[]> mapa : dataOperacionalMap.entrySet()) {
            Pozo pozo = mapa.getKey();
            Object[] datos = mapa.getValue();

            Optional<Date> opInter = Optional.ofNullable((Date) datos[45]);
            if (!opInter.isPresent()) {
                continue;
            }

            LocalDate ldInicioSup = parseDate((Date) datos[17]);
            LocalDate ldConex = parseDate((Date) datos[72]);

            //Arrastre
            if (ldInicioSup.getYear() < ldYearReport.getYear()) {
                if (ldConex.getYear() == ldYearReport.getYear()) {
                    // en plan
                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                        mbdIncorporados = incorporadosArrastrePlanMap.get(ldConex.getMonthValue());
                        mbdIncorporados += (Double) datos[5];
                        incorporadosArrastrePlanMap.put(ldConex.getMonthValue(), mbdIncorporados);
                    } else {
                        mbdIncorporados = incorporadosArrastreMap.get(ldConex.getMonthValue());
                        mbdIncorporados += (Double) datos[5];
                        incorporadosArrastreMap.put(ldConex.getMonthValue(), mbdIncorporados);
                    }
                } else if (ldConex.getYear() < ldYearReport.getYear()) {
                    // en plan
                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                        mbdIncorporados = incorporadosArrastrePlanMap.get(1);
                        mbdIncorporados += (Double) datos[5];
                        incorporadosArrastrePlanMap.put(1, mbdIncorporados);
                    } else {
                        mbdIncorporados = incorporadosArrastreMap.get(1);
                        mbdIncorporados += (Double) datos[5];
                        incorporadosArrastreMap.put(1, mbdIncorporados);
                    }
                }
            } else if (ldInicioSup.getYear() == ldYearReport.getYear()) {
                if (ldConex.getYear() == ldYearReport.getYear()) {
                    // en plan
                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                        mbdIncorporados = incorporadosTerminadosPlanMap.get(ldConex.getMonthValue());
                        mbdIncorporados += (Double) datos[5];
                        incorporadosTerminadosPlanMap.put(ldConex.getMonthValue(), mbdIncorporados);
                    } else {
                        mbdIncorporados = incorporadosTerminadosMap.get(ldConex.getMonthValue());
                        mbdIncorporados += (Double) datos[5];
                        incorporadosTerminadosMap.put(ldConex.getMonthValue(), mbdIncorporados);
                    }
                } else if (ldConex.getYear() < ldYearReport.getYear()) {
                    // en plan
                    if (pozo.getPlan().equalsIgnoreCase(plan)) {
                        mbdIncorporados = incorporadosTerminadosPlanMap.get(1);
                        mbdIncorporados += (Double) datos[5];
                        incorporadosTerminadosPlanMap.put(1, mbdIncorporados);
                    } else {
                        mbdIncorporados = incorporadosTerminadosMap.get(1);
                        mbdIncorporados += (Double) datos[5];
                        incorporadosTerminadosMap.put(1, mbdIncorporados);
                    }
                }
            }

        }

        incorporadosMap[0] = incorporadosArrastrePlanMap;
        incorporadosMap[1] = incorporadosArrastreMap;
        incorporadosMap[2] = incorporadosTerminadosPlanMap;
        incorporadosMap[3] = incorporadosTerminadosMap;

        return incorporadosMap;

    }

    private Map<Integer, Integer> contadorTerminadosMecanicamente(
            Map<Pozo, Object[]> dataOperacionalMap,
            LocalDate ldYearReport) {

        Map<Integer, Integer> terminadosMecMap = new HashMap<>();

        for (int i = 1; i < 13; i++) {
            terminadosMecMap.put(i, 0);
        }

        int terminadosMec = 0;
        for (Map.Entry<Pozo, Object[]> mapa : dataOperacionalMap.entrySet()) {
            Object[] datos = mapa.getValue();

            Optional<Date> optFechaIniInter = Optional.ofNullable((Date) datos[45]);
            if (!optFechaIniInter.isPresent()) {
                continue;
            }

            LocalDate ldInicioCompl = parseDate((Date) datos[62]);

            if (ldInicioCompl.getYear() == ldYearReport.getYear()) {
                terminadosMec = terminadosMecMap.get(ldInicioCompl.getMonthValue()) + 1;
                terminadosMecMap.put(ldInicioCompl.getMonthValue(), terminadosMec);
            } else if (ldInicioCompl.getYear() < ldYearReport.getYear()) {
                terminadosMec = terminadosMecMap.get(1) + 1;
                terminadosMecMap.put(1, terminadosMec);
            }

        }
        return terminadosMecMap;
    }

    private Map<Integer, Double> contadorPotencialTerminadosMecanicamente(
            Map<Pozo, Object[]> dataOperacionalMap,
            LocalDate ldYearReport) {

        Map<Integer, Double> potencialTermMecMap = new HashMap<>();

        for (int i = 1; i < 13; i++) {
            potencialTermMecMap.put(i, 0.0);
        }

        double potencial = 0.0;
        for (Map.Entry<Pozo, Object[]> mapa : dataOperacionalMap.entrySet()) {
            Object[] datos = mapa.getValue();

            Optional<Date> optFechaIniInter = Optional.ofNullable((Date) datos[45]);
            if (!optFechaIniInter.isPresent()) {
                continue;
            }

            LocalDate ldInicioCompl = parseDate((Date) datos[62]);
            double pi = (Double) datos[5];

            if (ldInicioCompl.getYear() == ldYearReport.getYear()) {
                potencial = potencialTermMecMap.get(ldInicioCompl.getMonthValue()) + pi;
                potencialTermMecMap.put(ldInicioCompl.getMonthValue(), potencial);
            } else if (ldInicioCompl.getYear() < ldYearReport.getYear()) {
                potencial = potencialTermMecMap.get(1) + potencial;
                potencialTermMecMap.put(1, potencial);
            }

        }
        return potencialTermMecMap;

    }

    private int taDiasProgreso(int mes, LocalDate ldInicioSup,
            LocalDate ldFinSup,
            int diasActSup,
            LocalDate ldInicioInter,
            LocalDate ldFinInter,
            int diasActInter,
            LocalDate ldInicioPrd,
            LocalDate ldFinPrd, int diasActPrd, int currentYear) {

        int diasProgreso = 0;
        boolean leap = LocalDate.of(currentYear, Month.JANUARY, 1).isLeapYear();
        LocalDate currentMonth = LocalDate.of(currentYear, mes, 1);
        LocalDate nextMonth = currentMonth.plusMonths(1L);

        LocalDate lastDayCurrentMonth = LocalDate.of(currentMonth.getYear(),
                currentMonth.getMonth(),
                currentMonth.getMonth().length(leap));
        LocalDate firstDayNextMonth = LocalDate.of(nextMonth.getYear(),
                nextMonth.getMonth(),
                1);

        LocalDate ldS14 = calculoS14(ldInicioSup, currentYear);

        if (ldS14.getYear() == currentYear) {
            if (ldFinPrd.getYear() == (currentYear + 1)) {
                if (ldS14 == lastDayCurrentMonth) {
                    if (ldS14.getMonth().getValue() == mes) {
                        diasProgreso = Period.between(ldS14, firstDayNextMonth).getDays();
                    } else if (ldS14.getMonth().getValue() < mes) {
                        diasProgreso = currentMonth.getMonth().length(leap);
                    }
                } else {
                    if (ldS14.getMonth().getValue() == mes) {
                        diasProgreso = Period.between(ldS14, lastDayCurrentMonth).getDays();
                    } else if (ldS14.getMonth().getValue() < mes) {
                        diasProgreso = currentMonth.getMonth().length(leap);
                    }
                }
            } else {
                if (ldS14 == lastDayCurrentMonth) {
                    if (ldFinPrd.getMonthValue() > mes) {
                        if (ldS14.getMonthValue() == mes) {
                            diasProgreso = Period.between(ldS14, firstDayNextMonth).getDays();
                        } else if (ldS14.getMonthValue() < mes) {
                            diasProgreso = currentMonth.getMonth().length(leap);
                        }
                    }
                } else {
                    if (ldFinPrd.getMonthValue() > mes) {
                        if (ldS14.getMonthValue() == mes) {
                            diasProgreso = Period.between(ldS14, lastDayCurrentMonth).getDays();
                        } else if (ldS14.getMonthValue() < mes) {
                            diasProgreso = currentMonth.getMonth().length(leap);
                        }
                    }
                }
            }
        }

        return diasProgreso;
    }

    private int taProductoresTerminados(int mes, LocalDate ldInicioSup,
            LocalDate ldFinSup,
            int diasActSup,
            LocalDate ldInicioInter,
            LocalDate ldFinInter,
            int diasActInter,
            LocalDate ldInicioPrd,
            LocalDate ldFinPrd, int diasActPrd, int currentYear) {

        int diasTerminados = 0;

        LocalDate ldS14 = calculoS14(ldInicioSup, currentYear);
        LocalDate currentMonth = LocalDate.of(currentYear, mes, 1);
        LocalDate lastDayLastYear = LocalDate.of((currentYear - 1), 12, 31);
        if (ldS14.getYear() == currentYear && ldS14.getMonth() == currentMonth.getMonth()) {
            if (ldFinSup.getYear() == currentYear && ldInicioSup.getYear() == currentYear) {
                diasTerminados += diasActSup;
            } else if (ldFinSup.getYear() == currentYear && ldInicioSup.getYear() == (currentYear - 1)) {
                diasTerminados += Period.between(ldFinSup, lastDayLastYear).getDays();
            }

            if (ldFinInter.getYear() == currentYear && ldInicioInter.getYear() == currentYear) {
                diasTerminados += diasActInter;
            } else if (ldFinInter.getYear() == currentYear && ldInicioInter.getYear() == (currentYear - 1)) {
                diasTerminados += Period.between(ldFinInter, lastDayLastYear).getDays();
            }

            if (ldFinPrd.getYear() == currentYear && ldInicioPrd.getYear() == currentYear) {
                diasTerminados += diasActPrd;
            } else if (ldFinPrd.getYear() == currentYear && ldInicioPrd.getYear() == (currentYear - 1)) {
                diasTerminados += Period.between(ldFinPrd, lastDayLastYear).getDays();
            }
        }

        return diasTerminados;
    }

    private LocalDate calculoS14(LocalDate ldInicioSup, int currentYear) {
        LocalDate ldS14 = null;
        if (ldInicioSup.getYear() < currentYear) {
            ldS14 = LocalDate.of(currentYear, Month.JANUARY, 1);
        } else {
            ldS14 = ldInicioSup;
        }
        return ldS14;
    }

    private int taDiasTerminados(int mes, LocalDate ldInicioSup,
            LocalDate ldFinSup,
            int diasActSup,
            LocalDate ldInicioInter,
            LocalDate ldFinInter,
            int diasActInter,
            LocalDate ldInicioPrd,
            LocalDate ldFinPrd, int diasActPrd, int currentYear) {

        int diasTerminados = 0;
        LocalDate ldLastDayLastYear = LocalDate.of((currentYear - 1), Month.DECEMBER, 31);
        Period period;
        if (ldFinPrd.getYear() == currentYear && ldFinPrd.getMonthValue() == mes) {
            if (ldFinSup.getYear() == currentYear && ldInicioSup.getYear() == currentYear) {
                diasTerminados += diasActSup;
            } else if (ldFinSup.getYear() == currentYear
                    && ldInicioSup.getYear() == (currentYear - 1)) {
                period = Period.between(ldLastDayLastYear, ldFinSup);
                diasTerminados += period.getDays();
            }

            if (ldFinInter.getYear() == currentYear && ldInicioInter.getYear() == currentYear) {
                diasTerminados += diasActInter;
            } else if (ldFinInter.getYear() == currentYear
                    && ldInicioInter.getYear() == (currentYear - 1)) {
                period = Period.between(ldLastDayLastYear, ldFinInter);
                diasTerminados += period.getDays();
            }

            if (ldFinPrd.getYear() == currentYear && ldInicioInter.getYear() == currentYear) {
                diasTerminados += diasActPrd;
            } else if (ldFinPrd.getYear() == currentYear
                    && ldInicioPrd.getYear() == (currentYear - 1)) {
                period = Period.between(ldLastDayLastYear, ldFinPrd);
                diasTerminados += period.getDays();
            }

        }
        return diasTerminados;
    }

    private LocalDate parseDate(Date fecha) {
        Instant instant = fecha.toInstant();
        LocalDate ld = LocalDateTime
                .ofInstant(instant, ZoneId.systemDefault())
                .toLocalDate();
        return ld;
    }

    /**
     * Devuelve un arreglo con los siguientes valores : 0 -> cantidad de dias en
     * el celda 1 -> cantidad de dias entre el dia de aceptación y e fin de
     * celda
     *
     * @param fechaAceptacion
     * @
     *
     * return
     */
    private int[] calculoDiasProdDecl(Date fechaAceptacion) {
        Instant inst = Instant.ofEpochMilli(fechaAceptacion.getTime());
        LocalDate fechaAcptLD = LocalDateTime.ofInstant(inst, ZoneId.systemDefault()).toLocalDate();
        LocalDate finMes = fechaAcptLD.with(TemporalAdjusters.lastDayOfMonth());
        Period period = Period.between(fechaAcptLD, finMes);
        Month celda = fechaAcptLD.getMonth();

        int[] dias = {celda.maxLength(), period.getDays()};
        return dias;
    }

    private void makeMergeColumns(XSSFSheet sheetResumen) {
        Row row = sheetResumen.getRow(0);
        // coloca las celdas que estan agrupadas
        sheetResumen.addMergedRegion(new CellRangeAddress(
                0, // comienzo de fila
                0, // fin de fila 
                0, // comienzo columna
                1)); // fin de columna
        Cell cell = row.getCell(0);
        cell.setCellStyle(styles.get("AlineadoAlCentroAzul"));

        sheetResumen.addMergedRegion(new CellRangeAddress(
                0, // comienzo de fila
                0, // fin de fila 
                12, // comienzo columna
                13)); // fin de columna

        row = sheetResumen.getRow(6);
        sheetResumen.addMergedRegion(new CellRangeAddress(6, 6, 0, 1));
        cell = row.getCell(0);
        cell.setCellStyle(styles.get("AlineadoDerecha"));
    }

    private Date getFirstDayOfMonth(int year, int celda) {
        LocalDate ld = LocalDate.of(year, celda, 1);
        Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    private void makeBulkLeftAligment(XSSFSheet sheetResumen) {
        int[] rowNumbers = {13, 14, 18, 19, 23, 24, 25, 26, 27, 28, 29, 37, 38,
            39, 40, 41, 42, 43, 44, 62, 63, 67, 68, 72, 73, 74, 75,
            76, 77, 86, 87, 88, 89, 90, 91, 92, 93, 118, 119,
            120, 121, 122, 123, 124, 125, 126, 127, 128, 129,
            130, 131, 132, 134, 135, 137, 138, 140, 141, 143,
            144, 146, 147, 149, 150, 152, 153, 155, 156, 157,
            158, 159, 160, 161, 162, 163, 164, 165, 166, 167,
            168, 169, 170, 171, 172, 173, 174, 175, 176, 177,
            178, 179, 180, 185, 186, 187, 188, 189, 190, 191,
            192, 193, 194, 195, 196, 197, 198, 199, 201, 202,
            204, 205, 207, 208, 210, 211, 213, 214, 216, 217,
            218, 219, 220, 221, 222, 223, 224, 225, 226, 227,
            228, 229, 230, 231, 232, 233, 240, 241, 246, 247,
            248, 249, 250, 251, 252, 253, 254, 255, 256, 257,
            258, 259, 260, 262, 263, 265, 266, 268, 269, 271,
            272, 274, 275, 277, 278, 280, 281, 283, 284, 285,
            286, 287, 288, 289, 290, 291, 292, 293, 294, 295,
            296, 297, 298, 299, 300, 301, 302, 303, 304, 305,
            306, 307, 308, 313, 314, 315, 316, 317, 318, 327,
            328, 239, 330, 331, 332, 333, 334, 339, 340, 341,
            342, 343, 344, 351, 352, 353, 354, 355, 356, 357,
            358, 363, 364, 365, 366, 367, 368, 377, 378, 378,
            379, 380, 381, 382, 383, 384, 455, 456, 457, 458,
            465, 466, 467, 468, 475, 476, 477, 478, 485, 486,
            487, 488, 646, 647, 648, 649, 650, 651, 652, 654,
            655, 656, 657, 658, 659, 662, 663, 664, 665, 666,
            667, 670, 671, 672, 673, 674, 675, 729, 730, 731,
            732, 733, 734, 743, 744, 745, 746, 747, 748, 749,
            750, 756, 757, 758, 759, 760, 761, 770, 771, 772,
            773, 774, 775, 776, 777};

        for (int i = 0; i < rowNumbers.length; i++) {
            Row row = sheetResumen.getRow(rowNumbers[i]);
            Cell cell = row.getCell(1);
            cell.setCellStyle(styles.get("AlineadoDerecha"));
        }
    }

    private Map<String, XSSFCellStyle> createStyles(XSSFWorkbook wb) {
        styles = new HashMap<>();

        // Los formatos en Excel
        XSSFCreationHelper createHelper = (XSSFCreationHelper) wb.getCreationHelper();

        XSSFCellStyle cellStyle;
        Font font;

        cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        styles.put("fechaLarga", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setColor(IndexedColors.BLUE.getIndex());
        font.setFontHeightInPoints((short) 12);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMMM-yyyy"));
        cellStyle.setFont(font);
        styles.put("celdaYearLargoAzul", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFont(font);
        styles.put("celdaYearCorto", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(204, 255, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoColorAgua", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 153, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoRosado", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoAmarillo", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(153, 204, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoVerde", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(204, 255, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoVerdeClaro", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(204, 153, 255)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoVioleta", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 102, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoNaranja", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 153)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoCrema", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 0, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoRojo", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(0, 204, 255)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoTurquesa", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(49, 134, 155)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoEsmeralda", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(218, 150, 148)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoGuayaba", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(153, 204, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoVerdeOliva", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(51, 204, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoCyan", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 153, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoRosaClaro", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(204, 153, 255)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("celdaYearCortoVioletaClaro", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        styles.put("sinDecimalesCentradoClaro", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("sinDecimalesCentradoAmarillo", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setFontHeightInPoints((short) 9);
        font.setColor(IndexedColors.WHITE.getIndex());
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setFont(font);
        styles.put("sinDecimalesOculto", cellStyle);

        cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styles.put("sinDecimales", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        styles.put("sinDecimalesBold", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(204, 255, 255)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("sinDecimalBoldCeleste", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("sinDecimalesBoldCremaClaro", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 153, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("sinDecimalBoldRosado", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(51, 51, 153)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("sinDecimalBoldAzulMarino", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(153, 204, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("sinDecimalBoldVerde", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(0, 128, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("sinDecimalBoldVerdeOscuro", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 0, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("sinDecimalBoldRojo", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 153)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("sinDecimalBoldCrema", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(0, 204, 255)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("sinDecimalesBoldTurquesa", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(153, 204, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("sinDecimalesBoldVerdeOliva", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(51, 204, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("sinDecimalesBoldCyan", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 153, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("sinDecimalesBoldRosaClaro", cellStyle);

        cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styles.put("unDecimal", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        styles.put("unDecimalBold", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 0, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("unDecimalBoldRojo", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("unDecimalBoldAmarillo", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(204, 255, 255)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("unDecimalBoldCeleste", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(153, 204, 255)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("unDecimalAzulMarino", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("unDecimalCremaClaro", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("unDecimalBoldCremaClaro", cellStyle);

        cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.00"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styles.put("dosDecimales", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.BLUE.getIndex());
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.00"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        styles.put("dosDecimalesAzul", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00; [Red](#,##0.00)"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        styles.put("dosDecimalesBoldNegativo", cellStyle);

        cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styles.put("tresDecimales", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        styles.put("tresDecimalesBold", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 153, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("tresDecimalesBoldRosado", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(204, 255, 255)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("tresDecimalesBoldCeleste", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 255, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("tresDecimalesBoldAmarillo", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(153, 204, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("tresDecimalesBoldVerde", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(255, 0, 0)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("tresDecimalesBoldRojo", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(0, 204, 255)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("tresDecimalesBoldTurquesa", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(51, 204, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("tresDecimalesBoldCyan", cellStyle);

        cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("[Red](#,##0.000)"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styles.put("tresDecimalesNegativo", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("[Black]#,##0.0000; [Red](#,##0.000)"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(204, 255, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("tresDecimalesNegativoBoldVerde", cellStyle);

        cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("###,###,##0.0000"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styles.put("cuatroDecimales", cellStyle);

        cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0%"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styles.put("porcentajeSinDecimal", cellStyle);

        cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00%"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styles.put("porcentaje", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.0%"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(204, 255, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put("porcentajeBoldColorAgua", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.BLUE.getIndex());
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("0.00%"));
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setFont(font);
        styles.put("porcentajeAzul", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.BLUE.getIndex());
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFont(font);
        styles.put("AlineadoAlCentroAzul", cellStyle);

        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(204, 255, 204)));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        styles.put("AlineadoAlCentroColorAgua", cellStyle);

        cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        styles.put("AlineadoIzquierda", cellStyle);

        cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        styles.put("AlineadoDerecha", cellStyle);

        return styles;
    }

    private void createCellPromedioFormula(XSSFSheet sheetResumen, int celda,
            int fila, String cellStyle) {
        fila--;
        Row row = sheetResumen.getRow(fila);
        Cell cell = row.createCell(celda + 1);
        StringBuilder formula = new StringBuilder();
        switch (celda) {
            case 1:
                formula.append("(").append((char) ('A' + (celda + 1))).append(fila);
                formula.append(" * ").append((char) ('A' + (celda + 1))).append("4)");
                break;
            case 2:
                formula.append("(").append((char) ('A' + celda)).append(fila);
                formula.append("*").append((char) ('A' + celda)).append("4");
                formula.append(" + ").append((char) ('A' + (celda + 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda + 1))).append("4)");
                break;
            case 3:
                formula.append("(").append((char) ('A' + (celda - 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 1))).append("4");
                formula.append("+").append((char) ('A' + celda)).append(fila);
                formula.append("*").append((char) ('A' + celda)).append("4");
                formula.append("+").append((char) ('A' + (celda + 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda + 1))).append("4)");
                break;
            case 4:
                formula.append("(").append((char) ('A' + (celda - 2))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 2))).append("4");
                formula.append("+").append((char) ('A' + (celda - 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 1))).append("4");
                formula.append("+").append((char) ('A' + celda)).append(fila);
                formula.append("*").append((char) ('A' + celda)).append("4");
                formula.append("+").append((char) ('A' + (celda + 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda + 1))).append("4)");
                break;
            case 5:
                formula.append("(").append((char) ('A' + (celda - 3))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 3))).append("4");
                formula.append("+").append((char) ('A' + (celda - 2))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 2))).append("4");
                formula.append("+").append((char) ('A' + (celda - 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 1))).append("4");
                formula.append("+").append((char) ('A' + celda)).append(fila);
                formula.append("*").append((char) ('A' + celda)).append("4");
                formula.append("+").append((char) ('A' + (celda + 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda + 1))).append("4)");
                break;
            case 6:
                formula.append("(").append((char) ('A' + (celda - 4))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 4))).append("4");
                formula.append("+").append((char) ('A' + (celda - 3))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 3))).append("4");
                formula.append("+").append((char) ('A' + (celda - 2))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 2))).append("4");
                formula.append("+").append((char) ('A' + (celda - 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 1))).append("4");
                formula.append("+").append((char) ('A' + celda)).append(fila);
                formula.append("*").append((char) ('A' + celda)).append("4");
                formula.append("+").append((char) ('A' + (celda + 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda + 1))).append("4)");
                break;
            case 7:
                formula.append("(").append((char) ('A' + (celda - 5))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 5))).append("4");
                formula.append("+").append((char) ('A' + (celda - 4))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 4))).append("4");
                formula.append("+").append((char) ('A' + (celda - 3))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 3))).append("4");
                formula.append("+").append((char) ('A' + (celda - 2))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 2))).append("4");
                formula.append("+").append((char) ('A' + (celda - 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 1))).append("4");
                formula.append("+").append((char) ('A' + celda)).append(fila);
                formula.append("*").append((char) ('A' + celda)).append("4");
                formula.append("+").append((char) ('A' + (celda + 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda + 1))).append("4)");
                break;
            case 8:
                formula.append("(").append((char) ('A' + (celda - 6))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 6))).append("4");
                formula.append("+").append((char) ('A' + (celda - 5))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 5))).append("4");
                formula.append("+").append((char) ('A' + (celda - 4))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 4))).append("4");
                formula.append("+").append((char) ('A' + (celda - 3))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 3))).append("4");
                formula.append("+").append((char) ('A' + (celda - 2))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 2))).append("4");
                formula.append("+").append((char) ('A' + (celda - 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 1))).append("4");
                formula.append("+").append((char) ('A' + celda)).append(fila);
                formula.append("*").append((char) ('A' + celda)).append("4");
                formula.append("+").append((char) ('A' + (celda + 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda + 1))).append("4)");
                break;
            case 9:
                formula.append("(").append((char) ('A' + (celda - 7))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 7))).append("4");
                formula.append("+").append((char) ('A' + (celda - 6))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 6))).append("4");
                formula.append("+").append((char) ('A' + (celda - 5))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 5))).append("4");
                formula.append("+").append((char) ('A' + (celda - 4))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 4))).append("4");
                formula.append("+").append((char) ('A' + (celda - 3))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 3))).append("4");
                formula.append("+").append((char) ('A' + (celda - 2))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 2))).append("4");
                formula.append("+").append((char) ('A' + (celda - 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 1))).append("4");
                formula.append("+").append((char) ('A' + celda)).append(fila);
                formula.append("*").append((char) ('A' + celda)).append("4");
                formula.append("+").append((char) ('A' + (celda + 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda + 1))).append("4)");
                break;
            case 10:
                formula.append("(").append((char) ('A' + (celda - 8))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 8))).append("4");
                formula.append("+").append((char) ('A' + (celda - 7))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 7))).append("4");
                formula.append("+").append((char) ('A' + (celda - 6))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 6))).append("4");
                formula.append("+").append((char) ('A' + (celda - 5))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 5))).append("4");
                formula.append("+").append((char) ('A' + (celda - 4))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 4))).append("4");
                formula.append("+").append((char) ('A' + (celda - 3))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 3))).append("4");
                formula.append("+").append((char) ('A' + (celda - 2))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 2))).append("4");
                formula.append("+").append((char) ('A' + (celda - 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 1))).append("4");
                formula.append("+").append((char) ('A' + celda)).append(fila);
                formula.append("*").append((char) ('A' + celda)).append("4");
                formula.append("+").append((char) ('A' + (celda + 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda + 1))).append("4)");
                break;
            case 11:
                formula.append("(").append((char) ('A' + (celda - 9))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 9))).append("4");
                formula.append("+").append((char) ('A' + (celda - 8))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 8))).append("4");
                formula.append("+").append((char) ('A' + (celda - 7))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 7))).append("4");
                formula.append("+").append((char) ('A' + (celda - 6))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 6))).append("4");
                formula.append("+").append((char) ('A' + (celda - 5))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 5))).append("4");
                formula.append("+").append((char) ('A' + (celda - 4))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 4))).append("4");
                formula.append("+").append((char) ('A' + (celda - 3))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 3))).append("4");
                formula.append("+").append((char) ('A' + (celda - 2))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 2))).append("4");
                formula.append("+").append((char) ('A' + (celda - 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 1))).append("4");
                formula.append("+").append((char) ('A' + celda)).append(fila);
                formula.append("*").append((char) ('A' + celda)).append("4");
                formula.append("+").append((char) ('A' + (celda + 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda + 1))).append("4)");
                break;
            case 12:
                formula.append("(").append((char) ('A' + (celda - 10))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 10))).append("4");
                formula.append("+").append((char) ('A' + (celda - 9))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 9))).append("4");
                formula.append("+").append((char) ('A' + (celda - 8))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 8))).append("4");
                formula.append("+").append((char) ('A' + (celda - 7))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 7))).append("4");
                formula.append("+").append((char) ('A' + (celda - 6))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 6))).append("4");
                formula.append("+").append((char) ('A' + (celda - 5))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 5))).append("4");
                formula.append("+").append((char) ('A' + (celda - 4))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 4))).append("4");
                formula.append("+").append((char) ('A' + (celda - 3))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 3))).append("4");
                formula.append("+").append((char) ('A' + (celda - 2))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 2))).append("4");
                formula.append("+").append((char) ('A' + (celda - 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda - 1))).append("4");
                formula.append("+").append((char) ('A' + celda)).append(fila);
                formula.append("*").append((char) ('A' + celda)).append("4");
                formula.append("+").append((char) ('A' + (celda + 1))).append(fila);
                formula.append("*").append((char) ('A' + (celda + 1))).append("4)");
                break;
        }
        formula.append(" / ").append((char) ('A' + (celda + 1))).append("2");
        cell.setCellFormula(formula.toString());
        cell.setCellStyle(styles.get(cellStyle));
    }

    private void createSumMultiCellFormula(XSSFSheet sheetResumen, int actualRow,
            int celda, String cellStyle, int... operando) {
        Row row = sheetResumen.getRow(actualRow - 1);
        Cell cell = row.createCell(celda + 1);
        StringBuilder formula = new StringBuilder();
        formula.append((char) ('A' + (celda + 1))).append(operando[0]);
        for (int i = 1; i < operando.length; i++) {
            formula.append(" + ").append((char) ('A' + (celda + 1)))
                    .append(operando[i]);
        }
        cell.setCellFormula(formula.toString());
        cell.setCellStyle(styles.get(cellStyle));
    }

    private void createDivCellFormula(XSSFSheet sheetResumen, int actualRow,
            int celda, String cellStyle, int dividendo, int divisor) {
        Row row = sheetResumen.getRow(actualRow - 1);
        Cell cell = row.createCell(celda + 1);
        StringBuilder formula = new StringBuilder();
        formula.append((char) ('A' + (celda + 1))).append(dividendo);
        formula.append(" / ").append((char) ('A' + (celda + 1))).append(divisor);
        cell.setCellFormula(formula.toString());
        cell.setCellStyle(styles.get(cellStyle));
    }

    private void makeSumCellFormula(XSSFSheet sheetResumen, int actualRow,
            int celda, String cellStyle, int celIni, int celEnd) {
        Row row = sheetResumen.getRow(actualRow - 1);
        Cell cell = row.createCell(celda + 1);
        StringBuilder formula = new StringBuilder();
        formula.append("SUM(").append((char) ('A' + (celda + 1))).append(celIni).append(":");
        formula.append((char) ('A' + (celda + 1))).append(celEnd).append(")");
        cell.setCellFormula(formula.toString());
        cell.setCellStyle(styles.get(cellStyle));
    }

    private void makeSumMultiCellFormula(XSSFSheet sheetResumen, int actualRow,
            int celda, String cellStyle, int celIni, int celEnd, int... morecells) {
        Row row = sheetResumen.getRow(actualRow - 1);
        Cell cell = row.createCell(celda + 1);
        StringBuilder formula = new StringBuilder();
        formula.append("SUM(").append((char) ('A' + (celda + 1))).append(celIni).append(":");
        formula.append((char) ('A' + (celda + 1))).append(celEnd).append(",");
        for (int i = 0; i < morecells.length; i++) {
            formula.append((char) ('A' + (celda + 1))).append(morecells[i]);
        }
        formula.append(")");
        cell.setCellFormula(formula.toString());
        cell.setCellStyle(styles.get(cellStyle));
    }

    private void makeValoresPositivosCellFormula(XSSFSheet sheetResumen, int actualRow,
            int celda, String cellStyle, int... operandos) {
        Row row = sheetResumen.getRow(actualRow - 1);
        Cell cell = row.createCell(celda + 1);
        StringBuilder formula = new StringBuilder();
        formula.append("(").append((char) ('A' + (celda + 1))).append(operandos[0]);
        formula.append(" / ").append((char) ('A' + (celda + 1))).append(operandos[1]);
        formula.append(") - ").append((char) ('A' + (celda + 1))).append(operandos[2]);
        formula.append(" - ").append((char) ('A' + (celda + 1))).append(operandos[3]);
        cell.setCellFormula(formula.toString());
        cell.setCellStyle(styles.get(cellStyle));
    }

    private void createIfFormulaWDiv(XSSFSheet sheetResumen, int actualRow,
            int celda, String cellStyle, int... operandos) {
        Row row = sheetResumen.getRow(actualRow - 1);
        Cell cell = row.createCell(celda + 1);
        StringBuilder formula = new StringBuilder();
        formula.append("IF(").append((char) ('A' + (celda + 1))).append(operandos[0]).append("=0,,(");
        formula.append((char) ('A' + (celda + 1))).append(operandos[1]);
        formula.append(" * ").append(operandos[2]);
        formula.append(" / ").append((char) ('A' + (celda + 1))).append(operandos[3]);
        formula.append("))");
        cell.setCellFormula(formula.toString());
        cell.setCellStyle(styles.get(cellStyle));
    }

    private void createIfFormulaWParidadCambiaria(XSSFSheet sheetResumen, int actualRow,
            int celda, String cellStyle, int... operandos) {
        Row row = sheetResumen.getRow(actualRow - 1);
        Cell cell = row.createCell(celda + 1);
        StringBuilder formula = new StringBuilder();
        formula.append("IF(").append((char) ('A' + (celda + 1))).append(operandos[0]).append("=0,,");
        formula.append((char) ('A' + (celda + 1))).append(operandos[1]);
        formula.append(" / ").append("$D$1");
        formula.append(" / ").append((char) ('A' + (celda + 1))).append(operandos[2]);
        formula.append(" )");
        cell.setCellFormula(formula.toString());
        cell.setCellStyle(styles.get(cellStyle));
    }

    private void createMultDivFormula(XSSFSheet sheetResumen, int actualRow,
            int celda, String cellStyle, int... operandos) {
        Row row = sheetResumen.getRow(actualRow - 1);
        Cell cell = row.createCell(celda + 1);
        StringBuilder formula = new StringBuilder();
        formula.append((char) ('A' + (celda + 1))).append(operandos[0]);
        formula.append(" * ").append(operandos[1]);
        formula.append(" / ").append((char) ('A' + (celda + 1))).append(operandos[2]);
        cell.setCellFormula(formula.toString());
        cell.setCellStyle(styles.get(cellStyle));
    }

    private void createDivWParidadCambiariaFormula(XSSFSheet sheetResumen, int actualRow,
            int celda, String cellStyle, int... operandos) {
        Row row = sheetResumen.getRow(actualRow - 1);
        Cell cell = row.createCell(celda + 1);
        StringBuilder formula = new StringBuilder();
        formula.append((char) ('A' + (celda + 1))).append(operandos[0]);
        formula.append(" / ").append("$D$1");
        formula.append(" / ").append((char) ('A' + (celda + 1))).append(operandos[1]);
        cell.setCellFormula(formula.toString());
        cell.setCellStyle(styles.get(cellStyle));
    }

    private void createIfFormulaWSumAndDiv(XSSFSheet sheetResumen, int actualRow,
            int celda, String cellStyle, int... operandos) {
        Row row = sheetResumen.getRow(actualRow - 1);
        Cell cell = row.createCell(celda + 1);
        StringBuilder formula = new StringBuilder();
        formula.append("IF(").append((char) ('A' + (celda + 1))).append(operandos[0]).append("=0,,(");
        formula.append((char) ('A' + (celda + 1))).append(operandos[1]);
        formula.append(" + ").append((char) ('A' + (celda + 1))).append(operandos[2]);
        formula.append(" + ").append((char) ('A' + (celda + 1))).append(operandos[3]);
        formula.append(") / ").append("$D$1 / ");
        formula.append((char) ('A' + (celda + 1))).append(operandos[4]).append(")");
        cell.setCellFormula(formula.toString());
        cell.setCellStyle(styles.get(cellStyle));
    }

    private void createIfFormulaWTwoSumAndDiv(XSSFSheet sheetResumen, int actualRow,
            int celda, String cellStyle, int... operandos) {
        Row row = sheetResumen.getRow(actualRow - 1);
        Cell cell = row.createCell(celda + 1);
        StringBuilder formula = new StringBuilder();
        formula.append("IF(").append((char) ('A' + (celda + 1))).append(operandos[0]).append("=0,,(");
        formula.append((char) ('A' + (celda + 1))).append(operandos[1]);
        formula.append(" + ").append((char) ('A' + (celda + 1))).append(operandos[2]);
        formula.append(") / ").append("$D$1 / ");
        formula.append((char) ('A' + (celda + 1))).append(operandos[3]).append(")");
        cell.setCellFormula(formula.toString());
        cell.setCellStyle(styles.get(cellStyle));
    }
}
