package com.sismon.vista.escenario;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.jpamanager.ExplotacionManager;
import com.sismon.model.Escenario;
import com.sismon.model.Explotacion;
import com.sismon.vista.Contexto;
import com.sismon.vista.controller.ExplotacionEscenarioController;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ExplotacionEscenarioIF extends javax.swing.JInternalFrame {

    private static ExplotacionEscenarioIF instance = null;
    private Escenario escenarioSelected;
    private Map<Integer, Object[]> produccionMap;

    private final EscenarioManager escenarioManager;
    private final ExplotacionManager explotacionManager;
    private final DecimalFormat decFormat;

    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    private DocumentListener docListener = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            verificaInputs();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            verificaInputs();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            verificaInputs();
        }
    };

    /**
     * Creates new form ExplotacionEscenarioIF
     */
    private ExplotacionEscenarioIF() {
        initComponents();
        setFrameIcon(icon);

        this.escenarioManager = new EscenarioManager();
        this.explotacionManager = new ExplotacionManager();
        this.decFormat = new DecimalFormat("###,##0.00");
        this.produccionMap = new TreeMap<>();
        init();
    }

    public static ExplotacionEscenarioIF getInstance() {
        if (instance == null) {
            instance = new ExplotacionEscenarioIF();
        }
        return instance;
    }

    private void init() {
        fillEscenarioComboBox();
        progressBar.setVisible(false);
        fechaInicioLabel.setVisible(false);
        fechaInicioDateChooser.setVisible(false);
    }

    private void configureListener() {
        yearsTextField.getDocument().addDocumentListener(docListener);
    }

    private void removeListener() {
        yearsTextField.getDocument().removeDocumentListener(docListener);
    }

    private void fillEscenarioComboBox() {
        escenarioComboBox.removeAllItems();
        escenarioComboBox.addItem("... seleccione Escenario");
        List<Escenario> escenarios = escenarioManager.findAll();
        escenarios.stream().forEach(esc -> {
            escenarioComboBox.addItem(esc);
        });
    }

    private void procesarProduccion() {
        try {
            double years = Double.parseDouble(yearsTextField.getText());
            boolean hiperbolico = hiperbolicRadioButton.isSelected();

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                        long startTime = System.nanoTime();

                        @Override
                        protected Void doInBackground() throws Exception {

                            progressBar.setVisible(true);
                            progressBar.setIndeterminate(true);

                            ExplotacionEscenarioController expEscController
                                    = new ExplotacionEscenarioController(escenarioSelected, produccionMap);
                            if (fechaInicioDateChooser.getDate() == null) {
                                expEscController.generarProduccion(years, true, hiperbolico, null);
                            } else {
                                expEscController.generarProduccion(years, true, hiperbolico, 
                                        fechaInicioDateChooser.getDate());
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                            fillDataProduccionTable(explotacionManager.findAll());
                            progressBar.setVisible(false);

                            long endTime = System.nanoTime();
                            double elapseTime = (endTime - startTime) / 1000000000.0;
                            Contexto.showMessage("El proceso se ejecutó en "
                                    + decFormat.format(elapseTime) + " segundos", Constantes.MENSAJE_INFO);
                        }
                    };
            worker.execute();
        } catch (NumberFormatException e) {
            Contexto.showMessage("Los años deben contener números", Constantes.MENSAJE_ERROR);
        }
    }

    private void fillDataProduccionTable(List<Explotacion> explotacionList) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                NumberFormat nf = new DecimalFormat("###,###,###,###,##0.00");
                DateFormat dformat = new SimpleDateFormat("dd/MM/yyyy");
                String[] header = {"Fecha", "Macolla", "Fila", "Pozo", "Prod Mes", "Prod Acum",
                    "Prod Gas", "Prod Gas Acum", "Prod AyS", "Prod AyS Acum",
                    "Dlnt Necesitado", "Dlnt Nec Acum"};

                int i = 0;
                Object[][] data = new Object[explotacionList.size()][header.length];
                for (Explotacion explt : explotacionList) {
                    data[i][0] = dformat.format(explt.getFecha());
                    data[i][1] = explt.getPozoId().getMacollaId();
                    data[i][2] = explt.getPozoId().getFilaId();
                    data[i][3] = explt.getPozoId();
                    data[i][4] = nf.format(explt.getProdDiaria());
                    data[i][5] = nf.format(explt.getProdAcum());
                    data[i][6] = nf.format(explt.getProdGas());
                    data[i][7] = nf.format(explt.getProdGasAcum());
                    data[i][8] = nf.format(explt.getProdAyS());
                    data[i][9] = nf.format(explt.getProdAySAcum());
                    data[i][10] = nf.format(explt.getProdDlnt());
                    data[i][11] = nf.format(explt.getProdDlntAcum());
                    i++;
                }

                TableModel model = new DefaultTableModel(data, header);
                explotacionTable.setModel(model);
            }
        });

    }

    private void verificaInputs() {
        if (!yearsTextField.getText().isEmpty()
                && (escenarioComboBox.getSelectedItem() instanceof Escenario)) {
            procesarButton.setEnabled(true);
        } else {
            procesarButton.setEnabled(false);
        }
    }

    private void clearForm() {
        explotacionTable.setModel(new DefaultTableModel());
        yearsTextField.setText("");
        escenarioComboBox.setSelectedIndex(0);
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        declinancionButtonGroup = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        procesarButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        toolPanel = new javax.swing.JPanel();
        escenarioComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        yearsTextField = new javax.swing.JTextField();
        exponencialRadioButton = new javax.swing.JRadioButton();
        hiperbolicRadioButton = new javax.swing.JRadioButton();
        progressBar = new javax.swing.JProgressBar();
        fechaInicioLabel = new javax.swing.JLabel();
        fechaInicioDateChooser = new com.toedter.calendar.JDateChooser();
        backPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        explotacionTable = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setTitle("Explotación de Escenario");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                onActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                onClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
                onDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                onOpen(evt);
            }
        });

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        procesarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconprocesar26.png"))); // NOI18N
        procesarButton.setText("Procesar");
        procesarButton.setEnabled(false);
        procesarButton.setFocusable(false);
        procesarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        procesarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        procesarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                procesarButtonActionPerformed(evt);
            }
        });
        toolBar.add(procesarButton);

        clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconlimpiar26.png"))); // NOI18N
        clearButton.setText("Limpiar");
        clearButton.setFocusable(false);
        clearButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        toolBar.add(clearButton);

        escenarioComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escenarioComboBoxActionPerformed(evt);
            }
        });

        jLabel1.setText("Años");

        jLabel2.setText("Formula de declinación:");

        declinancionButtonGroup.add(exponencialRadioButton);
        exponencialRadioButton.setSelected(true);
        exponencialRadioButton.setText("exponencial");

        declinancionButtonGroup.add(hiperbolicRadioButton);
        hiperbolicRadioButton.setText("exponente hiperbólico");

        fechaInicioLabel.setText("Fecha Inicio:");

        javax.swing.GroupLayout toolPanelLayout = new javax.swing.GroupLayout(toolPanel);
        toolPanel.setLayout(toolPanelLayout);
        toolPanelLayout.setHorizontalGroup(
            toolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(toolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(toolPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(exponencialRadioButton)
                        .addGap(2, 2, 2)
                        .addComponent(hiperbolicRadioButton))
                    .addGroup(toolPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yearsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(fechaInicioLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fechaInicioDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75))
        );
        toolPanelLayout.setVerticalGroup(
            toolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolPanelLayout.createSequentialGroup()
                .addGroup(toolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(toolPanelLayout.createSequentialGroup()
                        .addGroup(toolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(toolPanelLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(toolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fechaInicioDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(toolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(yearsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(fechaInicioLabel)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(toolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(exponencialRadioButton)
                            .addComponent(hiperbolicRadioButton)))
                    .addGroup(toolPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        toolBar.add(toolPanel);

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        explotacionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(explotacionTable);

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void procesarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_procesarButtonActionPerformed
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        explotacionTable.setModel(new DefaultTableModel());
        procesarProduccion();
    }//GEN-LAST:event_procesarButtonActionPerformed

    private void escenarioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escenarioComboBoxActionPerformed
        if (escenarioComboBox.getSelectedItem() instanceof Escenario) {
            escenarioSelected = (Escenario) escenarioComboBox.getSelectedItem();
        } else {
            escenarioSelected = null;
        }

        verificaInputs();
    }//GEN-LAST:event_escenarioComboBoxActionPerformed

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        configureListener();
    }//GEN-LAST:event_onActivated

    private void onDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onDeactivated
        removeListener();
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
    }//GEN-LAST:event_onDeactivated

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clearForm();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void onOpen(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onOpen
        List<Explotacion> lista = explotacionManager.findAll();
        if (lista.size() > 0) {
            fillDataProduccionTable(lista);
        }
        lista = null;
    }//GEN-LAST:event_onOpen

    private void onClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClosed
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
    }//GEN-LAST:event_onClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JButton clearButton;
    private javax.swing.ButtonGroup declinancionButtonGroup;
    private javax.swing.JComboBox escenarioComboBox;
    private javax.swing.JTable explotacionTable;
    private javax.swing.JRadioButton exponencialRadioButton;
    private com.toedter.calendar.JDateChooser fechaInicioDateChooser;
    private javax.swing.JLabel fechaInicioLabel;
    private javax.swing.JRadioButton hiperbolicRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton procesarButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolPanel;
    private javax.swing.JTextField yearsTextField;
    // End of variables declaration//GEN-END:variables
}
