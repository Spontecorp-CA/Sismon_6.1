package com.sismon.vista.configuracion;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.CampoManager;
import com.sismon.jpamanager.FilaHasTaladroManager;
import com.sismon.jpamanager.FilaManager;
import com.sismon.jpamanager.MacollaManager;
import com.sismon.jpamanager.TaladroManager;
import com.sismon.jpamanager.TaladroStatusManager;
import com.sismon.model.Campo;
import com.sismon.model.Fila;
import com.sismon.model.FilaHasTaladro;
import com.sismon.model.Macolla;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroStatus;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import com.sismon.vista.utilities.StatusTaladroEnum;
import com.sismon.vista.utilities.UtilitiesManager;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class AsignaTaladrosaFilaIF extends javax.swing.JInternalFrame {

    private static AsignaTaladrosaFilaIF instance = null;

    private final CampoManager campoManager;
    private final TaladroManager taladroManager;
    private final TaladroStatusManager taladroStatusManager;
    private final MacollaManager macollaManager;
    private final FilaManager filaManager;
    private final FilaHasTaladroManager filaHasTaladroManager;

    private final FilaHasTaladro fht;
    private List<Taladro> taladroList;
    private List<Macolla> macollaList;
    private List<FilaHasTaladro> dataList;
    private boolean taladroIsSelected = false;

    private Campo campoSelected;
    private Taladro taladroRemoved = null;
    private FilaHasTaladro fhtRemoved = null;
    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    private AsignaTaladrosaFilaIF() {
        initComponents();
        setTitle("Asignar Taladros a Filas");
        setFrameIcon(icon);

        this.campoManager = new CampoManager();
        this.taladroManager = new TaladroManager();
        this.taladroStatusManager = new TaladroStatusManager();
        this.macollaManager = new MacollaManager();
        this.filaManager = new FilaManager();
        this.filaHasTaladroManager = new FilaHasTaladroManager();
        this.fht = new FilaHasTaladro();

        init();
    }

    public static AsignaTaladrosaFilaIF getInstance() {
        if (instance == null) {
            instance = new AsignaTaladrosaFilaIF();
        }
        return instance;
    }

    private void init() {
        taladroList = taladroManager.findAll();
        loadCampoComboBox();
        loadTaladrosComboBox();
        dataList = filaHasTaladroManager.findAllBase();
    }

    private void loadTaladrosComboBox() {
        taladrosComboBox.removeAllItems();
        taladrosComboBox.addItem("...seleccione taladro");

        List<FilaHasTaladro> fhtList = filaHasTaladroManager.findAllBase();
        Map<Taladro, FilaHasTaladro> taladroMap = new HashMap<>();
        for(FilaHasTaladro fht : fhtList){
            taladroMap.put(fht.getTaladroId(), fht);
        }
        
        List<Taladro> taladrosAvailables = new ArrayList<>();
        for (Taladro tal : taladroList) {
            if(!taladroMap.containsKey(tal)){
                taladrosAvailables.add(tal);
            }
        }

        taladrosAvailables.stream().forEach(tal -> {
            taladrosComboBox.addItem(tal);
        });
    }

    private void loadCampoComboBox() {
        camposComboBox.removeAllItems();
        List<Campo> campos = campoManager.findAll();
        camposComboBox.addItem("... seleccione campo");
        for(Campo campo : campos){
            camposComboBox.addItem(campo);
        }
    }

    private void loadMacollasComboBox(Campo campo) {
        macollasComboBox.removeAllItems();
        macollaList = macollaManager.findAll(campo);
        macollasComboBox.addItem("... seleccione macolla");

        List<FilaHasTaladro> fhtList = filaHasTaladroManager.findAllBase();
        Map<Fila, FilaHasTaladro> filasMap = new HashMap<>();
        for(FilaHasTaladro fht :fhtList){
            filasMap.put(fht.getFilaId(), fht);
        }
        
        Set<Macolla> macollas = new LinkedHashSet<>();
        for(Macolla macolla : macollaList){
            List<Fila> filas = filaManager.findAll(macolla);
            for(Fila fila : filas){
                if(!filasMap.containsKey(fila)){
                    macollas.add(macolla);
                }
            }
        }
        
        macollas.stream().forEach(item -> {
            macollasComboBox.addItem(item);
        });
    }

    private void loadFilasComboBox(Macolla macolla) {
        filasComboBox.removeAllItems();
        filasComboBox.addItem("... seleccione una fila");
        List<Fila> pozoFilaList = filaManager.findAll(macolla);
        List<FilaHasTaladro> fhtList = filaHasTaladroManager.findAll();
        if (!fhtList.isEmpty()) {
            for (FilaHasTaladro fhast : fhtList) {
                if (pozoFilaList.contains(fhast.getFilaId())) {
                    pozoFilaList.remove(fhast.getFilaId());
                }
            }
        }
        pozoFilaList.stream().forEach(item -> {
            filasComboBox.addItem(item);
        });
    }

    private void loadDataTable() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        dataList = filaHasTaladroManager.findAllBase();
        dataTable.setModel(new DefaultTableModel());

        if (!dataList.isEmpty()) {
            String[] titles = {"Taladro", "Macolla", "Fila", "Fecha Inicial"};
            Object[][] data = new Object[dataList.size()][titles.length];

            int i = 0;
            for (FilaHasTaladro fht : dataList) {
                data[i][0] = fht.getTaladroId();
                data[i][1] = fht.getFilaId().getMacollaId();
                data[i][2] = fht.getFilaId().getNombre();
                data[i][3] = df.format(fht.getTaladroId().getFechaInicial());
                i++;
            }

            TableModel model = new DefaultTableModel(data, titles);
            dataTable.setModel(model);
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

        toolBar = new javax.swing.JToolBar();
        toolBarPanel = new javax.swing.JPanel();
        asignarButton = new javax.swing.JButton();
        quitarButton = new javax.swing.JButton();
        backPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        camposComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        macollasComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        filasComboBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        taladrosComboBox = new javax.swing.JComboBox();

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
                onDeactivate(evt);
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

        asignarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconguardar26.png"))); // NOI18N
        asignarButton.setText("Guardar");
        asignarButton.setEnabled(false);
        asignarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        asignarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        asignarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asignarButtonActionPerformed(evt);
            }
        });

        quitarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icondelete26.png"))); // NOI18N
        quitarButton.setText("Eliminar");
        quitarButton.setEnabled(false);
        quitarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        quitarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        quitarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitarButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout toolBarPanelLayout = new javax.swing.GroupLayout(toolBarPanel);
        toolBarPanel.setLayout(toolBarPanelLayout);
        toolBarPanelLayout.setHorizontalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolBarPanelLayout.createSequentialGroup()
                .addComponent(asignarButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(quitarButton)
                .addGap(0, 738, Short.MAX_VALUE))
        );
        toolBarPanelLayout.setVerticalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolBarPanelLayout.createSequentialGroup()
                .addGroup(toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(asignarButton)
                    .addComponent(quitarButton))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        toolBar.add(toolBarPanel);

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Taladro", "Macolla", "Fila", "Fecha Inicial"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dataTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(dataTable);

        camposComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                camposComboBoxActionPerformed(evt);
            }
        });

        jLabel1.setText("Campo:");

        jLabel2.setText("Macolla:");

        macollasComboBox.setEnabled(false);
        macollasComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                macollasComboBoxActionPerformed(evt);
            }
        });

        jLabel3.setText("Filas:");

        filasComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filasComboBoxActionPerformed(evt);
            }
        });

        jLabel4.setText("Taladro:");

        taladrosComboBox.setEnabled(false);
        taladrosComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                taladrosComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(camposComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(macollasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(filasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(taladrosComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        backPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {camposComboBox, filasComboBox, macollasComboBox, taladrosComboBox});

        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(camposComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(macollasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(filasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(taladrosComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 890, Short.MAX_VALUE)
            .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onDeactivate(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onDeactivate
        if (filasComboBox.getModel().getSize() > 0) {
            filasComboBox.setSelectedIndex(0);
        }
        if (macollasComboBox.getModel().getSize() > 0) {
            macollasComboBox.setSelectedIndex(0);
        }
        if (taladrosComboBox.getModel().getSize() > 0) {
            taladrosComboBox.setSelectedIndex(0);
        }

        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
    }//GEN-LAST:event_onDeactivate

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        init();
        List<FilaHasTaladro> mhtList = filaHasTaladroManager.findAllBase();
        if (!mhtList.isEmpty()) {
            loadDataTable();
        }
        Contexto.setActiveFrame(instance);
    }//GEN-LAST:event_onActivated

    private void asignarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_asignarButtonActionPerformed
        try {
            Fila fila = (Fila) filasComboBox.getSelectedItem();

            Taladro taladro = (Taladro) taladrosComboBox.getSelectedItem();

            // Cambia el TaladroStatus de este Taladro de Disponible a Ocupado
            // en la fecha de asignación
            TaladroStatus talStatus = new TaladroStatus();
            talStatus.setTaladroId(taladro);
            talStatus.setFechaIn(taladro.getFechaInicial());
            talStatus.setNombre(Constantes.TALADRO_STATUS_OCUPADO);
            talStatus.setStatus(Constantes.TALADRO_STATUS_ACTIVO);

            FilaHasTaladro fht = new FilaHasTaladro();
            fht.setTaladroId(taladro);
            fht.setFilaId(fila);
//            fht.setFechaIn(fechaCambio);
            filaHasTaladroManager.edit(fht);
            taladroStatusManager.create(talStatus);

            dataList = filaHasTaladroManager.findAllBase();
            loadDataTable();
            loadTaladrosComboBox();
            taladroIsSelected = false;
            filasComboBox.setSelectedIndex(0);
            macollasComboBox.setSelectedIndex(0);
            camposComboBox.setSelectedIndex(0);
            asignarButton.setEnabled(false);
            macollasComboBox.setEnabled(false);
            filasComboBox.setEnabled(false);
            taladrosComboBox.setEnabled(false);
        } catch (ClassCastException e) {
            Contexto.showMessage("Debe seleccionar una Fila", Constantes.MENSAJE_ERROR);
            sismonlog.logger.log(Level.INFO, "Error capturado", e);
        }
    }//GEN-LAST:event_asignarButtonActionPerformed

    private void quitarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitarButtonActionPerformed
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Va a quitar un Taladro de la asignación de perforación"
                + "\n¿Desea Continuar?", "Importante",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            UtilitiesManager.changeTaladroStatus(taladroRemoved,
                    StatusTaladroEnum.OCUPADO.toString(),
                    StatusTaladroEnum.DISPONIBLE.toString(),
                    taladroRemoved.getFechaInicial());
            filaHasTaladroManager.remove(fhtRemoved);

            // arreglar esto:
            //taladroList = taladroManager.findAllDisponible();
            taladroList = taladroManager.findAll();
            loadTaladrosComboBox();
            loadDataTable();
            quitarButton.setEnabled(false);
        }
    }//GEN-LAST:event_quitarButtonActionPerformed

    private void macollasComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_macollasComboBoxActionPerformed
        if (macollasComboBox.getSelectedItem() instanceof Macolla) {
            Macolla macolla = (Macolla) macollasComboBox.getSelectedItem();
            loadFilasComboBox(macolla);
            filasComboBox.setEnabled(true);
            Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        }
    }//GEN-LAST:event_macollasComboBoxActionPerformed

    private void camposComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_camposComboBoxActionPerformed
        if (camposComboBox.getSelectedItem() instanceof Campo) {
            campoSelected = (Campo) camposComboBox.getSelectedItem();
            loadMacollasComboBox(campoSelected);
            macollasComboBox.setEnabled(true);
        } else {
            macollasComboBox.setEnabled(false);
            filasComboBox.setEnabled(false);
            taladrosComboBox.setEnabled(false);
        }
    }//GEN-LAST:event_camposComboBoxActionPerformed

    private void filasComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filasComboBoxActionPerformed
        if (filasComboBox.getSelectedItem() instanceof Fila) {
            taladrosComboBox.setEnabled(true);
        }
    }//GEN-LAST:event_filasComboBoxActionPerformed

    private void dataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataTableMouseClicked
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        taladroRemoved = (Taladro) model.getValueAt(dataTable.getSelectedRow(), 0);

        for (FilaHasTaladro item : dataList) {
            if (item.getTaladroId().equals(taladroRemoved)) {
                fhtRemoved = item;
                break;
            }
        }

        quitarButton.setEnabled(true);
    }//GEN-LAST:event_dataTableMouseClicked

    private void taladrosComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_taladrosComboBoxActionPerformed
        asignarButton.setEnabled(true);
    }//GEN-LAST:event_taladrosComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton asignarButton;
    private javax.swing.JPanel backPanel;
    private javax.swing.JComboBox camposComboBox;
    private javax.swing.JTable dataTable;
    private javax.swing.JComboBox filasComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox macollasComboBox;
    private javax.swing.JButton quitarButton;
    private javax.swing.JComboBox taladrosComboBox;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolBarPanel;
    // End of variables declaration//GEN-END:variables
}
