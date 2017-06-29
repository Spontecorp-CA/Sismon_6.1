package com.sismon.vista.reporte;

import com.sismon.controller.Constantes;
import com.sismon.model.Escenario;
import com.sismon.model.Explotacion;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.model.Taladro;
import com.sismon.vista.Contexto;
import com.sismon.vista.controller.ReportesController;
import com.sismon.vista.reportetest.MakePerforacion;
import com.sismon.vista.reportetest.MakePerforacionReport;
import com.sismon.vista.utilities.SismonLog;
import com.sismon.vista.utilities.Utils;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReporteResultsDialog extends javax.swing.JDialog {

    private final List<Perforacion> lista;
    private final List<Explotacion> listaExp;
    private final int reporteFiltro;
    private final int tipoReporte;
    private final ReportesController reportesController;
    private final Escenario escenario;
    private final MakePerforacion makePerforacionReport;
    private String moneda = "bs";

    private String[] titles = null;
    private Object[][] data = null;

    private final NumberFormat decFormat = new DecimalFormat("###,###,###,###,##0.00");
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final SismonLog LOGGER = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form ReporteResultsDialog
     *
     * @param parent
     * @param modal
     * @param lista
     * @param listaExp
     * @param reporteFiltro
     * @param reportTitle
     * @param tipoReporte
     * @param escenario
     */
    public ReporteResultsDialog(java.awt.Frame parent,
            boolean modal, List<Perforacion> lista, List<Explotacion> listaExp,
            int reporteFiltro,
            String reportTitle, int tipoReporte, Escenario escenario) {
        super(parent, modal);
        initComponents();
        this.reportesController = new ReportesController();
        this.makePerforacionReport = new MakePerforacionReport();
        this.escenario = escenario;

        this.lista = lista;
        this.listaExp = listaExp;
        this.reporteFiltro = reporteFiltro;
        this.tipoReporte = tipoReporte;

        init();
    }

    private void init() {
        this.setIconImage(icon.getImage());
        setTitulo(tipoReporte);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        procesoLabel.setVisible(false);
        bsRadioButton.setVisible(false);
        usdRadioButton.setVisible(false);
        showVentanaTiempo();

        fillDataTable();
    }

    private void setTitulo(int tipoReporte) {
        String titulo = "";
        switch (tipoReporte) {
            case Constantes.REPORTE_PERFORACION_TOTAL:
                titulo = "Perforación Total";
                break;
            case Constantes.REPORTE_INVERSION:
                titulo = "Reporte Inversión";
                break;
            case Constantes.REPORTE_PERFORACION:
                titulo = "Reportes Perforación (Conteo)";
                break;
        }
        this.setTitle(titulo);
    }

    private void fillDataTable() {
        getReporteType();
        DefaultTableModel model = new DefaultTableModel(data, titles);
        dataTable.setModel(model);
    }

    private void showVentanaTiempo() {
        if (tipoReporte == Constantes.REPORTE_EXPLOTACION) {
            this.setTitle("Reporte de Producción");
            tipoPerfilPanel.setVisible(true);
        } else {
            tipoPerfilPanel.setVisible(false);
        }
    }

    private void getReporteType() {
        int i;// 0;
        LocalDate fechaIn;
        LocalDate fechaOut;
        int dias;
        switch (tipoReporte) {
            case Constantes.REPORTE_PERFORACION_TOTAL:
            case Constantes.REPORTE_INVERSION:
                titles = new String[]{"Bloque", "Yacimiento", "Campo", "Plan",
                    "Macolla", "Taladro", "Pozo", "Fase", "Días", "Fecha In", "Fecha Out",
                    "Bs", "US$", "US$ Equiv."};
                bsRadioButton.setVisible(true);
                usdRadioButton.setVisible(true);
                data = new Object[lista.size()][titles.length];
                i = 0;
                for (Perforacion perf : lista) {
                    data[i][0] = perf.getPozoId().getBloque();
                    data[i][1] = perf.getPozoId().getYacimiento();
                    data[i][2] = perf.getMacollaId().getCampoId().getNombre();
                    data[i][3] = perf.getPozoId().getPlan();
                    data[i][4] = perf.getPozoId().getMacollaId().getNombre();
                    data[i][5] = perf.getTaladroId();
                    data[i][6] = perf.getPozoId();
                    data[i][7] = perf.getFase();
                    fechaIn = LocalDateTime
                            .ofInstant(perf.getFechaIn().toInstant(), ZoneId.systemDefault())
                            .toLocalDate();
                    fechaOut = LocalDateTime
                            .ofInstant(perf.getFechaOut().toInstant(), ZoneId.systemDefault())
                            .toLocalDate();
                    dias = (int) ChronoUnit.DAYS.between(fechaIn, fechaOut);
                    data[i][8] = dias;
                    data[i][9] = dateFormat.format(perf.getFechaIn());
                    data[i][10] = dateFormat.format(perf.getFechaOut());
                    data[i][11] = decFormat.format(perf.getBs());
                    data[i][12] = decFormat.format(perf.getUsd());
                    data[i][13] = decFormat.format(perf.getEquiv());
                    i++;
                }
                break;
            case Constantes.REPORTE_PERFORACION:
                titles = new String[]{"Bloque", "Yacimiento", "Campo", "Plan",
                    "Macolla", "Taladro", "Pozo", "Fase", "Días", "Fecha In", "Fecha Out"};
                data = new Object[lista.size()][titles.length];
                i = 0;
                for (Perforacion perf : lista) {
                    data[i][0] = perf.getPozoId().getBloque();
                    data[i][1] = perf.getPozoId().getYacimiento();
                    data[i][2] = perf.getMacollaId().getCampoId().getNombre();
                    data[i][3] = perf.getPozoId().getPlan();
                    data[i][4] = perf.getPozoId().getMacollaId();
                    data[i][5] = perf.getTaladroId();
                    data[i][6] = perf.getPozoId();
                    data[i][7] = perf.getFase();
                    fechaIn = LocalDateTime
                            .ofInstant(perf.getFechaIn().toInstant(), ZoneId.systemDefault())
                            .toLocalDate();
                    fechaOut = LocalDateTime
                            .ofInstant(perf.getFechaOut().toInstant(), ZoneId.systemDefault())
                            .toLocalDate();
                    dias = (int) ChronoUnit.DAYS.between(fechaIn, fechaOut);
                    data[i][8] = dias;
                    data[i][9] = dateFormat.format(perf.getFechaIn());
                    data[i][10] = dateFormat.format(perf.getFechaOut());
                    i++;
                }
                break;
            case Constantes.REPORTE_EXPLOTACION:
                titles = new String[]{"Bloque", "Yacimiento", "Campo", "Plan",
                    "Macolla", "Pozo", "Fecha", "Prod Mes", "Prod Acum",
                    "Prod Gas", "Gas Acum", "Prod AyS", "AyS Acum",
                    "Diluente", "Dlnt Acum"};
                data = new Object[listaExp.size()][titles.length];
                i = 0;
                for (Explotacion exp : listaExp) {
                    data[i][0] = exp.getPozoId().getBloque();
                    data[i][1] = exp.getPozoId().getYacimiento();
                    data[i][2] = exp.getPozoId().getMacollaId().getCampoId().getNombre();
                    data[i][3] = exp.getPozoId().getPlan();
                    data[i][4] = exp.getPozoId().getMacollaId();
                    data[i][5] = exp.getPozoId();                   
                    data[i][6] = dateFormat.format(exp.getFecha());
                    data[i][7] = decFormat.format(exp.getProdDiaria());
                    data[i][8] = decFormat.format(exp.getProdAcum());
                    data[i][9] = decFormat.format(exp.getProdGas());
                    data[i][10] = decFormat.format(exp.getProdGasAcum());
                    data[i][11] = decFormat.format(exp.getProdAyS());
                    data[i][12] = decFormat.format(exp.getProdAySAcum());
                    data[i][13] = decFormat.format(exp.getProdDlnt());
                    data[i][14] = decFormat.format(exp.getProdDlntAcum());
                    i++;
                }
                break;
        }
    }

    private SXSSFWorkbook makeExcelPerforacionFileThread2(Map<Pozo, Object[]> mapa) {
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        SwingWorker<SXSSFWorkbook, Void> worker = new SwingWorker<SXSSFWorkbook, Void>() {

            @Override
            protected SXSSFWorkbook doInBackground() throws Exception {
                return reportesController.makeExcelPerforacionFile(mapa);
            }
        };
        worker.execute();
        SXSSFWorkbook workbook = null;
        try {
            workbook = worker.get();
            progressBar.setIndeterminate(false);
            progressBar.setVisible(false);
        } catch (ExecutionException | InterruptedException ex) {
            Logger.getLogger(ReporteResultsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return workbook;
    }

    private SXSSFWorkbook makeExcelInversionFileThread2(final Map<Pozo, Object[]> mapa, String moneda) {
        InversionReport inversionReportWorker = new InversionReport(progressBar,
                procesoLabel, mapa, moneda, escenario);
        SXSSFWorkbook workbook = null;

        inversionReportWorker.execute();
        try {
            workbook = inversionReportWorker.get();
            progressBar.setVisible(false);
            procesoLabel.setText("listo");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("capturó " + e);
        }

        return workbook;
    }

    private SXSSFWorkbook makeExcelConteoPerforacionFile2(List<Perforacion> lista) {
        PerforacionReport perforacionReportWorker = new PerforacionReport(
                progressBar, lista);
        SXSSFWorkbook workbook = null;

        perforacionReportWorker.execute();
        try {
            workbook = perforacionReportWorker.get();
            progressBar.setVisible(false);
            procesoLabel.setText("listo");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("capturó " + e);
        }

        return workbook;
    }

    private SXSSFWorkbook makeExcelExplotacionFile2(List<Explotacion> listaExp) {
        SwingWorker<SXSSFWorkbook, Void> worker = new SwingWorker<SXSSFWorkbook, Void>() {
            @Override
            protected SXSSFWorkbook doInBackground() throws Exception {
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);

                SXSSFWorkbook workbook; // = null;

                // Se crea un libro de excel en blanco
                workbook = new SXSSFWorkbook();

                // Aqui va el código que genera el reporte
                // Se extrae la fecha min y max del reporte para generar las hojas del reporte
                Optional<Date> fechaOpMin = listaExp.stream()
                        .map(p -> p.getFecha())
                        .max((p1, p2) -> p2.compareTo(p1));

                Optional<Date> fechaOpMax = listaExp.stream()
                        .map(p -> p.getFecha())
                        .max((p1, p2) -> p1.compareTo(p2));

                Date fechaMin = fechaOpMin.get();
                Date fechaMax = fechaOpMax.get();

                LocalDate ldMin = LocalDateTime
                        .ofInstant(fechaMin.toInstant(), ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate ldMax = LocalDateTime
                        .ofInstant(fechaMax.toInstant(), ZoneId.systemDefault())
                        .toLocalDate();

                String[] titulos1 = {"Bloque", "Yacimiento", "Campo", "Plan", "Macolla",
                    "Pozo", "Taladro", "Fecha Conex", "PI"};

                int cellNumber = 0;
                Row row;
                Cell cell;

                StringBuilder sb = new StringBuilder();
                sb.append("Producción ");
                sb.append(ldMin.getYear()).append(" - ");
                sb.append(ldMax.getYear());

                SXSSFSheet sheet = workbook.createSheet(sb.toString());
                // se recorre todas las fechas del reporte
                // en esta fila van los años

                int minYear = ldMin.getYear();
                int maxYear = ldMax.getYear();

                // coloca el encabezado de cada pozo
                row = sheet.createRow(1);
                for (String title : titulos1) {
                    cell = row.createCell(cellNumber++);
                    cell.setCellValue(title);
                }
                Row rowYear = sheet.createRow(0);
                // se colocan los encabezados de año y mes
                int cellCounter = 9;
                for (int year = minYear; year <= maxYear; year++) {
                    cell = rowYear.createCell(cellCounter);
                    cell.setCellValue(year);
                    if (year == minYear) {
                        // el primer año
                        for (int mes = ldMin.getMonthValue(); mes < 13; mes++) {
                            cell = row.createCell(cellCounter);
                            cell.setCellValue(getMes(mes));
                            cellCounter++;
                        }
                    } else {
                        // el resto de los años
                        for (int mes = 1; mes < 13; mes++) {
                            cell = row.createCell(cellCounter);
                            cell.setCellValue(getMes(mes));
                            cellCounter++;
                        }
                    }
                }
                
                int rowNumber = 2;
                Pozo pozo = new Pozo();
                int mesIniYearCero = 6;
                int mesFinYearCero = mesIniYearCero + (12 - ldMin.getMonthValue());
                double acumPozo = 0.0;
                
                // Aqui comienza a colocar en el excel la data de cada pozo
                for (Explotacion exp : listaExp) {
                    
                    
                    LocalDate ld = LocalDateTime.ofInstant(exp.getFecha()
                            .toInstant(), ZoneId.systemDefault()).toLocalDate();
                    // para evitar el NullPointerException
                    cell = row.getCell(cellNumber);
                    
                    if (!pozo.equals(exp.getPozoId())) { // primera aparición del pozo
                        pozo = exp.getPozoId();
                        cellNumber = 0;
                        row = sheet.createRow(rowNumber++);
                        cell = row.createCell(cellNumber++);
                        cell.setCellValue(exp.getPozoId().getBloque());
                        cell = row.createCell(cellNumber++);
                        cell.setCellValue(exp.getPozoId().getYacimiento());
                        cell = row.createCell(cellNumber++);
                        cell.setCellValue(exp.getPozoId().getMacollaId()
                                .getCampoId().getNombre());
                        cell = row.createCell(cellNumber++);
                        cell.setCellValue(exp.getPozoId().getPlan());
                        cell = row.createCell(cellNumber++);
                        cell.setCellValue(exp.getPozoId().getMacollaId().getNombre());
                        cell = row.createCell(cellNumber++);
                        cell.setCellValue(exp.getPozoId().getUbicacion());
                        
                        Taladro taladro = reportesController
                                .getProduccionTaladro(pozo);
                        cell = row.createCell(cellNumber++);
                        cell.setCellValue(taladro.getNombre());
                        
                        Date fechaInicio = reportesController
                                .getFechaInicioProduccion(pozo);
                        cell = row.createCell(cellNumber++);
                        cell.setCellValue(dateFormat.format(fechaInicio));
                        cell = row.createCell(cellNumber);
                        cell.setCellValue(exp.getPozoId().getPi());
                        
                        if(ld.getYear() == ldMin.getYear()){
                            cellNumber += ld.getMonthValue() - mesIniYearCero;
                        } else { 
                            cellNumber = mesFinYearCero 
                                            + 12 * (ld.getYear()- 1 - ldMin.getYear()) 
                                            + ld.getMonthValue() + 3;
                        }
                        
                        cell = row.createCell(cellNumber);

                        if(isLastDayOfMonth(ld)){
                            acumPozo += exp.getProdDiaria();
                            cell.setCellValue(acumPozo);
                            cellNumber++;
                            acumPozo = 0.0;
                        } else {
                            acumPozo += exp.getProdDiaria();
                        }
                    } else { // mas datos del mismo pozo
                        if (isLastDayOfMonth(ld)) {
                            cell = row.createCell(cellNumber++);
                            acumPozo += exp.getProdDiaria();
                            cell.setCellValue(acumPozo);
                            acumPozo = 0.0;
                        } else {
                            acumPozo += exp.getProdDiaria();
                        }
                    }
                }

                return workbook;
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                super.done();
            }
        };

        SXSSFWorkbook workbook = null;
        worker.execute();
        try {
            workbook = worker.get();
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.logger.log(Level.SEVERE, null, ex);
        }
        return workbook;
    }
    
    private boolean isLastDayOfMonth(LocalDate ld){
        boolean isLastDay = false;
        Month mes = ld.getMonth();
        boolean isLeap = ld.isLeapYear();
        int lastDay = mes.length(isLeap);
        
        if(ld.getDayOfMonth() == lastDay){
            isLastDay = true;
        }
        
        return isLastDay;
    }

    private int isBisiesto(int year) {
        LocalDate ld = LocalDate.of(year, Month.FEBRUARY, 1);
        if (ld.isLeapYear()) {
            return 366;
        } else {
            return 365;
        }
    }

    private String getMes(int numMes) {
        switch (numMes) {
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";
        }
        return null;
    }

    private void saveExcelFile(File excelFile, XSSFWorkbook workbook) {
        try (FileOutputStream out = new FileOutputStream(excelFile)) {
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            Contexto.showMessage("Archivo está en uso por otra aplicación, "
                    + "cancelela y vuelva a intentarlo", Constantes.MENSAJE_ERROR);
        }
    }

    private void saveExcelFile(File excelFile, SXSSFWorkbook workbook) {
        try (FileOutputStream out = new FileOutputStream(excelFile)) {
            workbook.write(out);
            out.close();
            workbook.dispose();
            Contexto.showMessage("Archivo Excel creado", Constantes.MENSAJE_INFO);
        } catch (IOException e) {
            Contexto.showMessage("Archivo está en uso por otra aplicación, "
                    + "cancelela y vuelva a intentarlo", Constantes.MENSAJE_ERROR);
        }
    }

    private void prepareReport(File excelFile, String moneda) {
        Map<Pozo, Object[]> mapa;
        XSSFWorkbook workbook = null;
        SXSSFWorkbook sworkbook = null;
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        switch (tipoReporte) {
            case Constantes.REPORTE_PERFORACION_TOTAL:
                mapa = reportesController.prepareDataPerforacionXExcel2(lista);
                sworkbook = makeExcelPerforacionFileThread2(mapa);
                break;
            case Constantes.REPORTE_INVERSION:
                mapa = reportesController.prepareDataPerforacionXExcel(lista, moneda);
                sworkbook = makeExcelInversionFileThread2(mapa, moneda);
                break;
            case Constantes.REPORTE_PERFORACION:
                sworkbook = makeExcelConteoPerforacionFile2(lista);
                break;
            case Constantes.REPORTE_EXPLOTACION: {
                try {
                    sworkbook = makeExcelExplotacionFile2(listaExp);
                } catch (Exception ex) {
                    Logger.getLogger(ReporteResultsDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
        }
        if (workbook != null) {
            saveExcelFile(excelFile, workbook);
        }

        if (sworkbook != null) {
            saveExcelFile(excelFile, sworkbook);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        monedaButtonGroup = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        excelButton = new javax.swing.JButton();
        toolBarPanel = new javax.swing.JPanel();
        tipoPerfilPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        petroleoRadioButton = new javax.swing.JRadioButton();
        gasRadioButton = new javax.swing.JRadioButton();
        aysRadioButton = new javax.swing.JRadioButton();
        dlnteRadioButton = new javax.swing.JRadioButton();
        progressBar = new javax.swing.JProgressBar();
        procesoLabel = new javax.swing.JLabel();
        bsRadioButton = new javax.swing.JRadioButton();
        usdRadioButton = new javax.swing.JRadioButton();
        backPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        excelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/MS Excel-26.png"))); // NOI18N
        excelButton.setText("Exportar");
        excelButton.setFocusable(false);
        excelButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        excelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        excelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excelButtonActionPerformed(evt);
            }
        });
        toolBar.add(excelButton);

        jLabel1.setText("Tipo de perfil:");

        buttonGroup.add(petroleoRadioButton);
        petroleoRadioButton.setSelected(true);
        petroleoRadioButton.setText("Petróleo");

        buttonGroup.add(gasRadioButton);
        gasRadioButton.setText("Gas");

        buttonGroup.add(aysRadioButton);
        aysRadioButton.setText("Agua y Sedimento");

        buttonGroup.add(dlnteRadioButton);
        dlnteRadioButton.setText("Diluente");

        javax.swing.GroupLayout tipoPerfilPanelLayout = new javax.swing.GroupLayout(tipoPerfilPanel);
        tipoPerfilPanel.setLayout(tipoPerfilPanelLayout);
        tipoPerfilPanelLayout.setHorizontalGroup(
            tipoPerfilPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tipoPerfilPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(petroleoRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(gasRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(aysRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dlnteRadioButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tipoPerfilPanelLayout.setVerticalGroup(
            tipoPerfilPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tipoPerfilPanelLayout.createSequentialGroup()
                .addContainerGap(11, Short.MAX_VALUE)
                .addGroup(tipoPerfilPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(petroleoRadioButton)
                    .addComponent(gasRadioButton)
                    .addComponent(aysRadioButton)
                    .addComponent(dlnteRadioButton))
                .addContainerGap())
        );

        progressBar.setStringPainted(true);

        procesoLabel.setText(" ");

        monedaButtonGroup.add(bsRadioButton);
        bsRadioButton.setSelected(true);
        bsRadioButton.setText("Bs");

        monedaButtonGroup.add(usdRadioButton);
        usdRadioButton.setText("US$");

        javax.swing.GroupLayout toolBarPanelLayout = new javax.swing.GroupLayout(toolBarPanel);
        toolBarPanel.setLayout(toolBarPanelLayout);
        toolBarPanelLayout.setHorizontalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolBarPanelLayout.createSequentialGroup()
                .addComponent(tipoPerfilPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(bsRadioButton)
                .addGap(18, 18, 18)
                .addComponent(usdRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(procesoLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
        );

        toolBarPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {bsRadioButton, usdRadioButton});

        toolBarPanelLayout.setVerticalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolBarPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(toolBarPanelLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bsRadioButton)
                            .addComponent(usdRadioButton)))
                    .addComponent(tipoPerfilPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(toolBarPanelLayout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(procesoLabel)))
                .addGap(18, 18, 18))
        );

        toolBar.add(toolBarPanel);

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(dataTable);

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1036, Short.MAX_VALUE)
                .addContainerGap())
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void excelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excelButtonActionPerformed
        final JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ExcelFiles());
        File excelFile = null;
        int respuesta = fc.showSaveDialog(this);

        if (bsRadioButton.isSelected()) {
            moneda = Constantes.BOLIVARES;
        } else if (usdRadioButton.isSelected()) {
            moneda = Constantes.DOLARES;
        }

        if (respuesta == JFileChooser.APPROVE_OPTION) {
            excelFile = fc.getSelectedFile();
            if (!excelFile.getAbsolutePath().endsWith(".xlsx")) {
                excelFile = new File(excelFile + ".xlsx");
            }
            try {
                if (!excelFile.exists()) {
                    if (!excelFile.createNewFile()) {
                        JOptionPane.showMessageDialog(Contexto.getActiveFrame(), "No se puede crear "
                                + "el archivo indicado", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else {
                        prepareReport(excelFile, moneda);
                    }
                } else {
                    int answer = JOptionPane.showConfirmDialog(this, "El archivo "
                            + "indicado ya existe, ¿Desea sobreescribirlo?",
                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                    if (answer == JOptionPane.NO_OPTION) {
                        JOptionPane.showMessageDialog(this, "Acción cancelada por "
                                + "el usuario", "Información",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    } else {
                        prepareReport(excelFile, moneda);
                    }
                }
            } catch (IOException | HeadlessException e) {
                LOGGER.logger.log(Level.FINER, "Error: ", e);
            }

        }
    }//GEN-LAST:event_excelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton aysRadioButton;
    private javax.swing.JPanel backPanel;
    private javax.swing.JRadioButton bsRadioButton;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JTable dataTable;
    private javax.swing.JRadioButton dlnteRadioButton;
    private javax.swing.JButton excelButton;
    private javax.swing.JRadioButton gasRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.ButtonGroup monedaButtonGroup;
    private javax.swing.JRadioButton petroleoRadioButton;
    private javax.swing.JLabel procesoLabel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel tipoPerfilPanel;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolBarPanel;
    private javax.swing.JRadioButton usdRadioButton;
    // End of variables declaration//GEN-END:variables

    private class ExcelFiles extends FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = Utils.getExtension(f);
            if (extension != null) {
                if (extension.equals(Utils.xls)
                        || extension.equals(Utils.xlsx)) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Archivos Excel";
        }
    }

}
