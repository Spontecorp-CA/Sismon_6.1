package com.sismon.vista.reporte;

import com.sismon.controller.Constantes;
import com.sismon.model.Escenario;
import com.sismon.model.Explotacion;
import com.sismon.model.Perforacion;
import com.sismon.vista.Contexto;
import com.sismon.vista.controller.ReportesController;
import com.sismon.vista.reportetest.MakePerforacion;
import com.sismon.vista.reportetest.MakePerforacionReport;
import com.sismon.vista.reportetest.PerforacionReport;
import com.sismon.vista.utilities.SismonLog;
import com.sismon.vista.utilities.Utils;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;


public class ReportePerforacionTotalDialog extends javax.swing.JDialog {

    private final List<Perforacion> lista;
    private final List<Explotacion> listaExp;
    private final int reporteFiltro;
    private final int tipoReporte;
    private final ReportesController reportesController;
    private final Escenario escenario;
    private final MakePerforacion makePerforacionReport;

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
    public ReportePerforacionTotalDialog(java.awt.Frame parent,
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
        fillDataTable();
        this.progressBar.setVisible(false);
        showVentanaTiempo();
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
            tipoPerfilPanel.setVisible(true);
        } else {
            tipoPerfilPanel.setVisible(false);
        }
    }

    private void getReporteType() {
        int i = 0;
        LocalDate fechaIn;
        LocalDate fechaOut;
        int dias;
        switch (tipoReporte) {
            case Constantes.REPORTE_PERFORACION_TOTAL:
            case Constantes.REPORTE_INVERSION:
                titles = new String[]{"Bloque", "Yacimiento", "Campo", "Plan",
                    "Macolla", "Taladro", "Pozo", "Fase", "Días", "Fecha In", "Fecha Out",
                    "Bs", "US$", "US$ Equiv."};
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
                    "Macolla", "Pozo", "Fecha", "Prod Diaria", "Prod Acum",
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

    private int numeroSemana(LocalDate ld) {
        if (ld.getDayOfYear() == 7) {
            return 1;
        } else {
            return ((ld.getDayOfYear() - 6) / 7) + ((ld.getDayOfYear() - 6) % 7);
        }
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

    private void prepareNewReport(File excelFile) {
        
        SwingWorker<Void,Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);
                List<PerforacionReport> perforaciones = makePerforacionReport
                        .makePerforacionData(escenario);

                try (InputStream is = getClass().getResourceAsStream("/resources/files/PerforacionTotal_template.xls")) {
                    try (OutputStream os = new FileOutputStream(excelFile)) {
//                        Context context = new Context();
//                        context.putVar("perforaciones", perforaciones);
//                        JxlsHelper.getInstance().processTemplate(is, os, context);
                        
//                        Context context = PoiTransformer.createInitialContext();
//                        context.putVar("perforaciones", perforaciones);
//                        context.putVar("sheetNames", Arrays.asList(
//                                "Arrastre",
//                                "Actual"));
//                        JxlsHelper.getInstance().setUseFastFormulaProcessor(false)
//                                .processTemplate(is, os, context);


                        // lo hecho de último
//                        Transformer transformer = TransformerFactory.createTransformer(is, os);
//                        System.out.println("Creating area");
//                        XlsArea arrastreArea = new XlsArea("Arrastre!B3:AJ3", transformer);
//                        XlsArea actualArea = new XlsArea("Actual!B3:AJ3", transformer);
//                        
//                        System.out.println("Va a crear los comandos");
//                        EachCommand arrastreEachCommand = new EachCommand(
//                                "pr", "perforaciones", arrastreArea, 
//                                new SimpleCellRefGenerator());
//                        
//                        EachCommand actualEachCommand = new EachCommand(
//                                "pr", "perforaciones", actualArea,
//                                new SimpleCellRefGenerator());
//
//                        System.out.println("va a crear el comando if");
//                        IfCommand ifCommand = new IfCommand("pr.yearSup < pr.actualYear",
//                                arrastreArea,
//                                actualArea);
//                        System.out.println("Agrega los comandos a la hoja Arrastre");
//                        arrastreArea.addCommand(new AreaRef("Arrastre!B3:AJ3"),
//                                arrastreEachCommand);
//                        arrastreArea.addCommand(new AreaRef("Arrastre!B3:AJ3"), 
//                                ifCommand);
//                        System.out.println("Agrega los comando a la hoja Actual");
//                        actualArea.addCommand(new AreaRef("Actual!B3:AJ3"),
//                                actualEachCommand);
//                        actualArea.addCommand(new AreaRef("Actual!B3:AJ3"), 
//                                ifCommand);
//                        
//                        System.out.println("va a crear el contexto");
//                        Context context = new Context();
//                        context.putVar("perforaciones", perforaciones);
//                        LOGGER.logger.log(Level.INFO,"Applying at cell Sheet!A1");
//                        Size arrastreSize = arrastreArea.applyAt(new CellRef("Arrastre!A1"), context);
//                        System.out.println("Size es " + arrastreSize.getWidth());
//                        
//                        Size actualSize = actualArea.applyAt(new CellRef("Actual!A1"), context);
//                        LOGGER.logger.log(Level.INFO,"pasó el método applyAt");
////                        arrastreArea.setFormulaProcessor(new StandardFormulaProcessor());
////                        actualArea.setFormulaProcessor(new StandardFormulaProcessor());
//                        System.out.println("va a procesar las formulas");
//                        arrastreArea.processFormulas();
//                        actualArea.processFormulas();
//                        LOGGER.logger.log(Level.INFO,"Complete");
//                        transformer.write();
//                        LOGGER.logger.log(Level.INFO,"written to file");

                        LOGGER.logger.log(Level.INFO, "Archivo de perfortacion generado con éxito");
                    }
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                } catch (IOException e) {
                    LOGGER.logger.log(Level.SEVERE, "Problemas buscando el archivo", e);
                }
                return null;
            }
        };
        worker.execute();
        
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

        javax.swing.GroupLayout toolBarPanelLayout = new javax.swing.GroupLayout(toolBarPanel);
        toolBarPanel.setLayout(toolBarPanelLayout);
        toolBarPanelLayout.setHorizontalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolBarPanelLayout.createSequentialGroup()
                .addComponent(tipoPerfilPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 342, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );
        toolBarPanelLayout.setVerticalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolBarPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolBarPanelLayout.createSequentialGroup()
                        .addComponent(tipoPerfilPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolBarPanelLayout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))))
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
        if (respuesta == JFileChooser.APPROVE_OPTION) {
            excelFile = fc.getSelectedFile();
            if (!excelFile.getAbsolutePath().endsWith(".xls")) {
                excelFile = new File(excelFile + ".xls");
            }
            try {
                if (!excelFile.exists()) {
                    if (!excelFile.createNewFile()) {
                        JOptionPane.showMessageDialog(Contexto.getActiveFrame(), "No se puede crear "
                                + "el archivo indicado", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else {
                        prepareNewReport(excelFile);
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
                        prepareNewReport(excelFile);
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
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JTable dataTable;
    private javax.swing.JRadioButton dlnteRadioButton;
    private javax.swing.JButton excelButton;
    private javax.swing.JRadioButton gasRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton petroleoRadioButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel tipoPerfilPanel;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolBarPanel;
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
