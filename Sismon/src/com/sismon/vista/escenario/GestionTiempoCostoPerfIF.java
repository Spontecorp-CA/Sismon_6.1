package com.sismon.vista.escenario;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.jpamanager.FilaManager;
import com.sismon.jpamanager.MacollaManager;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Perforacion;
import com.sismon.model.Taladro;
import com.sismon.vista.Contexto;
import com.sismon.vista.controller.EditableCostosTableModel;
import com.sismon.vista.controller.PerforacionEscenarioController;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class GestionTiempoCostoPerfIF extends javax.swing.JInternalFrame {

    private static GestionTiempoCostoPerfIF instance = null;
    private Escenario escenarioSelected;
    private Macolla macollaSelected;
    private Fila filaSelected;
    private Map<Integer, Perforacion> perfMap;
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private final EscenarioManager escenarioManager;
    private final MacollaManager macollaManager;
    private final FilaManager filaManager;
    private final PerforacionManager perforacionManager;

    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form GestionTiempoCostoPerf
     */
    private GestionTiempoCostoPerfIF() {
        initComponents();
        this.escenarioManager = new EscenarioManager();
        this.macollaManager = new MacollaManager();
        this.filaManager = new FilaManager();
        this.perforacionManager = new PerforacionManager();

        init();
    }

    public static GestionTiempoCostoPerfIF getInstance() {
        if (instance == null) {
            instance = new GestionTiempoCostoPerfIF();
        }
        return instance;
    }

    private void init() {
        this.setTitle("Gestión de Tiempos y Costos de Perforación");
        this.setFrameIcon(icon);

    }

    private void fillEscenarioComboBox() {
        escenarioComboBox.removeAllItems();
        List<Escenario> escenarios = escenarioManager.findAllMV(false);
        escenarioComboBox.addItem("... seleccione escenario");
        for (Escenario escenario : escenarios) {
            escenarioComboBox.addItem(escenario);
        }
    }

    private void fillMacollaComboBox() {
        macollaComboBox.removeAllItems();
        List<Macolla> macollas = macollaManager.findAll();
        macollaComboBox.addItem("... seleccione macolla");
        for (Macolla macolla : macollas) {
            macollaComboBox.addItem(macolla);
        }
    }

    private void fillFilaComboBox() {
        filaComboBox.removeAllItems();
        List<Fila> filas = filaManager.findAll(macollaSelected);
        filaComboBox.addItem("... seleccione fila");
        for (Fila fila : filas) {
            filaComboBox.addItem(fila);
        }
    }

    private void fillPozosTable() {
        perfMap = new LinkedHashMap<>();
        List<Perforacion> perforaciones = getPerforacionTotalTaladro(escenarioSelected, filaSelected);
        if (!perforaciones.isEmpty()) {
            String[] titles = {"Pozo", "Taladro", "Fase", "Fecha In", "Dias Activos",
                "Dias Inactivos", "Dias Total", "Fecha Out", "Bs.", "US$"};
            Object[][] data = new Object[perforaciones.size()][titles.length];
            int i = 0;
            for (Perforacion perf : perforaciones) {
                if (perf.getFilaId().equals(filaSelected)) {
                    data[i][0] = perf.getPozoId() != null ? perf.getPozoId().toString() : "";
                    data[i][1] = perf.getTaladroId() != null ? perf.getTaladroId().toString() : "";
                    data[i][2] = perf.getFase() != null ? perf.getFase() : "";
                    data[i][3] = perf.getFase() != null ? dateFormat.format(perf.getFechaIn()) : "";
                    data[i][4] = perf.getDiasActivos() != null ? perf.getDiasActivos() : 0.0;
                    data[i][5] = perf.getDiasInactivos() != null ? perf.getDiasInactivos() : 0.0;
                    data[i][6] = perf.getDias() != null ? perf.getDias() : 0.0;
                    data[i][7] = perf.getFase() != null ? dateFormat.format(perf.getFechaOut()) : "";
                    data[i][8] = perf.getBs() != null ? perf.getBs() : 0.0;
                    data[i][9] = perf.getUsd() != null ? perf.getUsd() : 0.0;
                }
                perfMap.put(i, perf);
                i++;
            }
            TableModel model = new EditableCostosTableModel(data, titles);
            dataTable.setModel(model);

            dataTable.getModel().addTableModelListener((TableModelEvent e) -> {
                Integer row = e.getFirstRow();
                int column = e.getColumn();

                TableModel tableModel = (TableModel) e.getSource();
                Object dato = tableModel.getValueAt(row, column);
                Perforacion perf = perfMap.get(row);

                if (isValidDateModification(perf.getFechaIn())) {
                    switch (column) {
                        case 4:
                            perf.setDiasActivos((Double) dato);
                            double inactivos = (Double) tableModel.getValueAt(row, 5);
                            tableModel.setValueAt(perf.getDiasActivos() + inactivos, row, 6);
                            break;
                        case 5:
                            perf.setDiasInactivos((Double) dato);
                            double activos = (Double) tableModel.getValueAt(row, 4);
                            tableModel.setValueAt(perf.getDiasInactivos() + activos, row, 6);
                            break;
                        case 8:
                            perf.setBs((Double) dato);
                            break;
                        case 9:
                            perf.setUsd((Double) dato);
                            break;
                    }
                    perf.setDias(perf.getDiasActivos() + perf.getDiasInactivos());
                    perfMap.put(row, perf);
                    guardarButton.setEnabled(true);
                } else {
                    showCierreWarning();
                }
            });

        }
    }

    private void guardarCambios() {
        Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
        List<Perforacion> perforaciones = new ArrayList<>(perfMap.values());
        perforacionManager.batchEdit(perforaciones);
        Contexto.showMessage("Cambios guardados ", Constantes.MENSAJE_INFO);
    }

    /**
     * Método para traer la perforacion de un taladro
     *
     * @param escenario
     * @param fila
     * @return
     */
    public List<Perforacion> getPerforacionTotalTaladro(Escenario escenario, Fila fila) {
        List<Perforacion> perforaciones = perforacionManager.findAllByDate(escenarioSelected, fila);
        return perforaciones;
    }

    private boolean isValidDateModification(Date fecha) {
        if (escenarioSelected.getFechaCierre() != null) {
            return fecha.after(escenarioSelected.getFechaCierre());
        } else {
            return true;
        }
    }

    private void showCierreWarning() {
        String fecha = dateFormat.format(escenarioSelected.getFechaCierre());
        JOptionPane.showMessageDialog(this,
                "No puede hacer cambios en este escenario antes de esta fecha: " + fecha,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        guardarButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        escenarioComboBox = new javax.swing.JComboBox();
        backPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        macollaComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        filaComboBox = new javax.swing.JComboBox();

        setClosable(true);
        setIconifiable(true);
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
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        guardarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconguardar26.png"))); // NOI18N
        guardarButton.setText("Guardar");
        guardarButton.setEnabled(false);
        guardarButton.setFocusable(false);
        guardarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        guardarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        guardarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarButtonActionPerformed(evt);
            }
        });
        toolBar.add(guardarButton);

        escenarioComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escenarioComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(521, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        toolBar.add(jPanel1);

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        dataTable.setAutoCreateRowSorter(true);
        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(dataTable);

        jLabel1.setText("Macolla:");

        macollaComboBox.setEnabled(false);
        macollaComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                macollaComboBoxActionPerformed(evt);
            }
        });

        jLabel2.setText("Fila:");

        filaComboBox.setEnabled(false);
        filaComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filaComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(macollaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(macollaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(filaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void escenarioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escenarioComboBoxActionPerformed
        if (escenarioComboBox.getSelectedItem() instanceof Escenario) {
            escenarioSelected = (Escenario) escenarioComboBox.getSelectedItem();
            fillMacollaComboBox();
            macollaComboBox.setEnabled(true);
            dataTable.setModel(new DefaultTableModel());
        } else {
            macollaComboBox.setEnabled(false);
            filaComboBox.setEnabled(false);
            dataTable.setModel(new DefaultTableModel());
        }
    }//GEN-LAST:event_escenarioComboBoxActionPerformed

    private void macollaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_macollaComboBoxActionPerformed
        if (macollaComboBox.getSelectedItem() instanceof Macolla) {
            macollaSelected = (Macolla) macollaComboBox.getSelectedItem();
            fillFilaComboBox();
            filaComboBox.setEnabled(true);
        } else {
            filaComboBox.setEnabled(false);
        }
    }//GEN-LAST:event_macollaComboBoxActionPerformed

    private void filaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filaComboBoxActionPerformed
        if (filaComboBox.getSelectedItem() instanceof Fila) {
            filaSelected = (Fila) filaComboBox.getSelectedItem();
            fillPozosTable();
        } else {
            dataTable.setModel(new DefaultTableModel());
        }
    }//GEN-LAST:event_filaComboBoxActionPerformed

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        guardarCambios();
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        fillEscenarioComboBox();
        Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
        Contexto.setActiveFrame(instance);
    }//GEN-LAST:event_onActivated

    private void onClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClosed
        Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
    }//GEN-LAST:event_onClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JTable dataTable;
    private javax.swing.JComboBox escenarioComboBox;
    private javax.swing.JComboBox filaComboBox;
    private javax.swing.JButton guardarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox macollaComboBox;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

}
