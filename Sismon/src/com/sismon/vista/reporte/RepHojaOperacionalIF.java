package com.sismon.vista.reporte;

import com.sismon.controller.Constantes;
import com.sismon.model.Escenario;
import com.sismon.model.Perforacion;
import com.sismon.vista.Contexto;
import com.sismon.vista.controller.HojaOperacionalController;
import com.sismon.vista.utilities.ExtensionFileFilter;
import com.sismon.vista.utilities.SismonLog;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class RepHojaOperacionalIF extends javax.swing.JInternalFrame {

    private static RepHojaOperacionalIF instance = null;

    private Escenario escenarioSelected;
    private List<Perforacion> perforacionList;
    private final HojaOperacionalController controller;

    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));
    private static final SismonLog SISMONLOG = SismonLog.getInstance();
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###,#00.00");
    private final static String OPCION_SELECCIONE = "... seleccione";

    /**
     * Creates new form RepHojaOperacionalIF
     */
    private RepHojaOperacionalIF() {
        initComponents();
        init();
        this.controller = new HojaOperacionalController();
    }

    public static RepHojaOperacionalIF getInstance() {
        if (instance == null) {
            instance = new RepHojaOperacionalIF();
        }
        return instance;
    }

    private void init() {
        this.setFrameIcon(icon);
    }

    private void fillEscenarioComboBox() {
        escenarioComboBox.removeAllItems();
        escenarioComboBox.addItem(OPCION_SELECCIONE);
        controller.getEscenarios().forEach(esc -> {
            escenarioComboBox.addItem(esc);
        });
    }

    private void fillDataTable() {
        perforacionList = controller.getPerforacionList(escenarioSelected);
        String[] titles = {"Macolla", "Fila", "Pozo", "Taladro", "Fase", "Fecha In",
            "Días Act", "Días Inact", "Días Total",
            "Fecha Out", "Bs.", "Us$", "US$ Equiv."};
        Object[][] datos = new Object[perforacionList.size()][titles.length];

        int i = 0;
        for (Perforacion p : perforacionList) {
            datos[i][0] = p.getMacollaId().getNombre();
            datos[i][1] = p.getFilaId().getNombre();
            datos[i][2] = p.getPozoId().getUbicacion();
            datos[i][3] = p.getTaladroId().getNombre();
            datos[i][4] = p.getFase();
            datos[i][5] = dateFormat.format(p.getFechaIn());
            datos[i][6] = p.getDiasActivos();
            datos[i][7] = p.getDiasInactivos();
            datos[i][8] = p.getDias();
            datos[i][9] = dateFormat.format(p.getFechaOut());
            datos[i][10] = decimalFormat.format(p.getBs());
            datos[i][11] = decimalFormat.format(p.getUsd());
            datos[i][12] = decimalFormat.format(p.getEquiv());
            i++;
        }

        TableModel model = new DefaultTableModel(datos, titles);
        dataTable.setModel(model);
    }

    private void makeExcelFile(Map<String, Object> valorManualMap) {
        File excelFile;
        int resultado = 0;
        try {
            JFileChooser chooser = new JFileChooser();
            FileFilter filter = new ExtensionFileFilter("Archivos xlsx (excel)", "xlsx");
            chooser.setFileFilter(filter);
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                excelFile = chooser.getSelectedFile();
                if (!excelFile.getAbsolutePath().endsWith(".xlsx")) {
                    excelFile = new File(excelFile + ".xlsx");
                }
                try {
                    if (!excelFile.exists()) {
                        if (!excelFile.createNewFile()) {
                            Contexto.showMessage("No se puede crear "
                                    + "el archivo indicado",
                                    Constantes.MENSAJE_ERROR);
                            return;
                        } else {
                            resultado = controller.makeOperationalExcelSheet(
                                    excelFile, perforacionList, valorManualMap);
                        }
                    } else {
                        int userChoice = JOptionPane.showConfirmDialog(this,
                                "El archivo indicado ya existe, ¿Desea sobreescribirlo?",
                                "Información", JOptionPane.INFORMATION_MESSAGE);
                        if (userChoice == JOptionPane.NO_OPTION) {
                            Contexto.showMessage("Acción cancelada por el usuario",
                                    Constantes.MENSAJE_INFO);
                            return;
                        } else {
                            resultado = controller.makeOperationalExcelSheet(
                                    excelFile, perforacionList, valorManualMap);
                        }
                    }
                } catch (IOException | HeadlessException e) {
                    SISMONLOG.logger.log(Level.SEVERE, "Error generando la hoja excel. Error: ", e);
                }
            }
        } catch (Exception e) {
            SISMONLOG.logger.log(Level.SEVERE, "Error procesando la hoja operacional", e);
        }

        switch (resultado) {
            case Constantes.HOJAOPERACIONAL_OK:
                Contexto.showMessage("Hoja Operacional generada con éxito",
                        Constantes.MENSAJE_INFO);
                break;
            case Constantes.HOJAOPERACIONAL_ERROR:
                Contexto.showMessage("Ocurrió un problema al generar la Hoja Operacional",
                        Constantes.MENSAJE_ERROR);
                break;
            case Constantes.HOJAOPERACIONAL_EN_USO:
                Contexto.showMessage("Archivo está en uso por otra aplicación, "
                            + "cancelela y vuelva a intentarlo",
                        Constantes.MENSAJE_ERROR);
                break;
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

        backPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        potencialCATextField = new javax.swing.JTextField();
        declinacionAnualTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        yearReportTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        planTextField = new javax.swing.JTextField();
        toolBar = new javax.swing.JToolBar();
        excelFileButton = new javax.swing.JButton();
        toolbarPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        escenarioComboBox = new javax.swing.JComboBox<>();
        progressBar = new javax.swing.JProgressBar();

        setClosable(true);
        setIconifiable(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                onActivate(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(dataTable);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Valores manuales"));

        jLabel2.setText("Potencial C/A (MBD):");

        jLabel3.setText("% Declinación Anual:");

        potencialCATextField.setText("0.923");

        declinacionAnualTextField.setText("8");

        jLabel4.setText("Año reporte:");

        yearReportTextField.setText("2017");

        jLabel5.setText("Plan:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(potencialCATextField, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                    .addComponent(yearReportTextField))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(declinacionAnualTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(planTextField)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(potencialCATextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(declinacionAnualTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(yearReportTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(planTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        excelFileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/MS Excel-26.png"))); // NOI18N
        excelFileButton.setText("Generar MVisión");
        excelFileButton.setEnabled(false);
        excelFileButton.setFocusable(false);
        excelFileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        excelFileButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        excelFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excelFileButtonActionPerformed(evt);
            }
        });
        toolBar.add(excelFileButton);

        jLabel1.setText("Mejor Visión / Escenario: ");

        escenarioComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escenarioComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout toolbarPanelLayout = new javax.swing.GroupLayout(toolbarPanel);
        toolbarPanel.setLayout(toolbarPanelLayout);
        toolbarPanelLayout.setHorizontalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 219, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
        );
        toolbarPanelLayout.setVerticalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        toolBar.add(toolbarPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onActivate(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivate
        Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
        fillEscenarioComboBox();
    }//GEN-LAST:event_onActivate

    private void escenarioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escenarioComboBoxActionPerformed
        if (escenarioComboBox.getSelectedItem() instanceof Escenario) {
            escenarioSelected = (Escenario) escenarioComboBox.getSelectedItem();
            fillDataTable();
            excelFileButton.setEnabled(true);
        }
    }//GEN-LAST:event_escenarioComboBoxActionPerformed

    private void excelFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excelFileButtonActionPerformed
        Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
        Map<String, Object> valorManualMap = new HashMap<>();
        try {
            Double potencialCA = Double.parseDouble(potencialCATextField.getText());
            valorManualMap.put("potencialCA", potencialCA);
            Double porcDeclinacionAnual = Double.parseDouble(declinacionAnualTextField.getText());
            valorManualMap.put("porcDeclinacionAnual", porcDeclinacionAnual);
            Integer yearReport = Integer.parseInt(yearReportTextField.getText());
            valorManualMap.put("yearReport", yearReport);
            String plan = planTextField.getText().trim();
            valorManualMap.put("plan", plan);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Sólo puede colocar valores numéricos usando el punto(.) como separador",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        makeExcelFile(valorManualMap);
    }//GEN-LAST:event_excelFileButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JTable dataTable;
    private javax.swing.JTextField declinacionAnualTextField;
    private javax.swing.JComboBox<Object> escenarioComboBox;
    private javax.swing.JButton excelFileButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField planTextField;
    private javax.swing.JTextField potencialCATextField;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JTextField yearReportTextField;
    // End of variables declaration//GEN-END:variables
}
