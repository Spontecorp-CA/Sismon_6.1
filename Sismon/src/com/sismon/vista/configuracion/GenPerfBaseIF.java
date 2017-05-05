package com.sismon.vista.configuracion;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.model.Taladro;
import com.sismon.vista.Contexto;
import com.sismon.vista.controller.PerforacionController;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class GenPerfBaseIF extends javax.swing.JInternalFrame {

    private static GenPerfBaseIF instance = null;

    private final PerforacionManager perforacionManager;

    // Mapa para el almacenamiento temporal de los resultados
    private Map<Integer, Object[]> estrategiaPerforacionMap;
    private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private final NumberFormat decimalFormat = new DecimalFormat("###,###,##0.00");

    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form GenPerfBaseIF
     */
    private GenPerfBaseIF() {
        initComponents();
        setTitle("Generación de Perforación Base");
        setFrameIcon(icon);

        this.perforacionManager = new PerforacionManager();
        this.estrategiaPerforacionMap = new TreeMap<>();
        this.progressBar.setVisible(false);
    }

    public static GenPerfBaseIF getInstance() {
        if (instance == null) {
            instance = new GenPerfBaseIF();
        }
        return instance;
    }

    private void init() {
        fillPerforacionMap();
        List<Perforacion> perforaciones = perforacionManager.findAllBase();
        if (perforaciones != null || !perforaciones.isEmpty()) {
            fillDataTable();
        }
    }

    private void fillPerforacionMap() {
        List<Perforacion> perforaciones = perforacionManager.findAllBase();
        if (perforaciones != null || !perforaciones.isEmpty()) {
            estrategiaPerforacionMap = new TreeMap<>();
            int i = 1;
            for (Perforacion perf : perforaciones) {
                Object[] items = new Object[13];
                items[0] = perf.getTaladroId();
                items[1] = perf.getMacollaId();
                items[2] = perf.getFilaId();
                items[3] = perf.getPozoId();
                items[4] = perf.getFase();
                items[5] = perf.getFechaIn();
                items[6] = perf.getFechaOut();
                items[7] = perf.getBs();
                items[8] = perf.getUsd();
                items[9] = perf.getEquiv();
                items[10] = perf.getDiasActivos() == null ? 0.0 : perf.getDiasActivos();
                items[11] = perf.getDiasInactivos() == null ? 0.0 : perf.getDiasInactivos();
                items[12] = perf.getDias() == null ? 0.0 : perf.getDias();
                estrategiaPerforacionMap.put(i++, items);
            }
        } else {
            dataTable.setModel(new DefaultTableModel());
        }
    }

    private void fillDataTable() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                String[] header = {"Taladro", "Macolla", "Fila", "Pozo", "Fase",
                    "Fecha In", "Dias Activos", "Dias Inactivos", "Dias Totales",
                    "Fecha Out", "Bs.", "US$", "US$ Equiv."};
                Object[][] data = new Object[estrategiaPerforacionMap.size()][header.length];

                int i = 0;
                for (Map.Entry<Integer, Object[]> mapa : estrategiaPerforacionMap.entrySet()) {
                    Object[] elementos = mapa.getValue();
                    data[i][0] = ((Taladro) elementos[0]).getNombre();
                    data[i][1] = ((Macolla) elementos[1]).getNombre() + "(" + ((Macolla) elementos[1]).getNumero() + ")";
                    data[i][2] = ((Fila) elementos[2]).getNombre();
                    data[i][3] = ((Pozo) elementos[3]).getUbicacion();
                    data[i][4] = elementos[4];
                    data[i][5] = df.format((Date) elementos[5]);
                    data[i][6] = decimalFormat.format((double) elementos[10]);
                    data[i][7] = decimalFormat.format((double) elementos[11]);
                    data[i][8] = decimalFormat.format((double) elementos[12]);
                    data[i][9] = df.format((Date) elementos[6]);
                    data[i][10] = decimalFormat.format((double) elementos[7]);
                    data[i][11] = decimalFormat.format((double) elementos[8]);
                    data[i][12] = decimalFormat.format((double) elementos[9]);
                    i++;
                }

                TableModel model = new DefaultTableModel(data, header);
                dataTable.setModel(model);
                messageLabel.setText("Procesados " + dataTable.getModel().getRowCount() + " registros");
            }
        });

    }

    private void fillDataTableAfterSave() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                String[] header = {"Taladro", "Macolla", "Fila", "Pozo", "Fase", "Fecha In",
                    "Dias Activos", "Dias Inactivos", "Dias Total",
                    "Fecha Out", "Bs.", "US$", "US$ Equiv."};
                List<Perforacion> perforaciones = perforacionManager.findAll(null);

                Object[][] data = new Object[perforaciones.size()][header.length];
                int i = 0;
                for (Perforacion perf : perforaciones) {
                    data[i][0] = perf.getTaladroId().getNombre();
                    data[i][1] = perf.getMacollaId().getNombre() + "(" + perf.getMacollaId().getNumero() + ")";
                    data[i][2] = perf.getFilaId().getNombre();
                    data[i][3] = perf.getPozoId().getUbicacion();
                    data[i][4] = perf.getFase();
                    data[i][5] = df.format(perf.getFechaIn());
                    data[i][6] = perf.getDiasActivos();
                    data[i][7] = perf.getDiasInactivos();
                    data[i][8] = perf.getDias();
                    data[i][9] = df.format(perf.getFechaOut());
                    data[i][10] = decimalFormat.format(perf.getBs());
                    data[i][11] = decimalFormat.format(perf.getUsd());
                    data[i][12] = decimalFormat.format(perf.getEquiv());
                    i++;
                }

                TableModel model = new DefaultTableModel(data, header);
                dataTable.setModel(model);
                messageLabel.setText("Procesados " + dataTable.getModel().getRowCount() + " registros");
            }
        });
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
        messageLabel = new javax.swing.JLabel();
        toolBar = new javax.swing.JToolBar();
        guardarButton = new javax.swing.JButton();
        procesarButton = new javax.swing.JButton();
        toolbarPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();

        setClosable(true);
        setIconifiable(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                onActivated(evt);
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
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(messageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 626, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        guardarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconguardar26.png"))); // NOI18N
        guardarButton.setText("Guardar");
        guardarButton.setFocusable(false);
        guardarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        guardarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        guardarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarButtonActionPerformed(evt);
            }
        });
        toolBar.add(guardarButton);

        procesarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconprocesar26.png"))); // NOI18N
        procesarButton.setText("Procesar");
        procesarButton.setFocusable(false);
        procesarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        procesarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        procesarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                procesarButtonActionPerformed(evt);
            }
        });
        toolBar.add(procesarButton);

        javax.swing.GroupLayout toolbarPanelLayout = new javax.swing.GroupLayout(toolbarPanel);
        toolbarPanel.setLayout(toolbarPanelLayout);
        toolbarPanelLayout.setHorizontalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarPanelLayout.createSequentialGroup()
                .addContainerGap(569, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        toolbarPanelLayout.setVerticalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        toolBar.add(toolbarPanel);

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
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            boolean salvado = false;

            @Override
            protected Void doInBackground() throws Exception {
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);
                progressBar.setStringPainted(true);
                setProgress(0);
                List<Perforacion> perforacionList = new ArrayList<>();
                Perforacion perforacion;

                // elimina la perforación base anterior
                perforacionManager.removeBase();

                for (Map.Entry<Integer, Object[]> datosMap : estrategiaPerforacionMap.entrySet()) {
                    Object[] items = datosMap.getValue();
                    perforacion = new Perforacion();
                    perforacion.setTaladroId((Taladro) items[0]);
                    perforacion.setMacollaId((Macolla) items[1]);
                    perforacion.setFilaId((Fila) items[2]);
                    perforacion.setPozoId((Pozo) items[3]);
                    perforacion.setFase((String) items[4]);
                    perforacion.setFechaIn((Date) items[5]);
                    perforacion.setFechaOut((Date) items[6]);

                    perforacion.setDiasActivos((double) items[10]);
                    perforacion.setDiasInactivos((double) items[11]);
                    perforacion.setDias((double) items[12]);

                    perforacion.setBs((Double) items[7]);
                    perforacion.setUsd((Double) items[8]);
                    perforacion.setEquiv((Double) items[9]);
                    perforacion.setEscenarioId(null);
                    perforacionList.add(perforacion);
                }
                perforacionManager.batchSave(perforacionList);
                salvado = true;
                return null;
            }

            @Override
            protected void done() {
                try {
                    if (salvado) {
                        progressBar.setVisible(false);
                        fillDataTableAfterSave();
                        Contexto.showMessage("Datos de perforación base guardados con éxito",
                                Constantes.MENSAJE_INFO);
                    } else {
                        Contexto.showMessage("Ha ocurrido un error guardando los datos de perforación",
                                Constantes.MENSAJE_ERROR);
                    }
                    get();
                } catch (ExecutionException | InterruptedException e) {
                    sismonlog.logger.log(Level.SEVERE, "Error guardando los datos de perforación", e);
                }
            }
        };
        worker.execute();

    }//GEN-LAST:event_guardarButtonActionPerformed

    private void procesarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_procesarButtonActionPerformed
        try {
            PerforacionController perfController
                    = new PerforacionController(this.estrategiaPerforacionMap, progressBar);;
            perfController.execute();
            estrategiaPerforacionMap = perfController.get();
            fillDataTable();

        } catch (InterruptedException | ExecutionException ex) {
            sismonlog.logger.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_procesarButtonActionPerformed

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        Contexto.setActiveFrame(instance);
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        messageLabel.setText(null);
        init();
    }//GEN-LAST:event_onActivated


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton guardarButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton procesarButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolbarPanel;
    // End of variables declaration//GEN-END:variables
}
