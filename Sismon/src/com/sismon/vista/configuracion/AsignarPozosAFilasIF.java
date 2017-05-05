package com.sismon.vista.configuracion;

import com.sismon.controller.Constantes;
import com.sismon.controller.SortedListModel;
import com.sismon.jpamanager.CampoManager;
import com.sismon.jpamanager.FilaManager;
import com.sismon.jpamanager.MacollaManager;
import com.sismon.jpamanager.MacollaSecuenciaManager;
import com.sismon.jpamanager.PozoManager;
import com.sismon.model.Campo;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Pozo;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class AsignarPozosAFilasIF extends javax.swing.JInternalFrame {

    private static AsignarPozosAFilasIF instance = null;
    private Campo campo;
    private Macolla macollaSelected;
    private SortedListModel listModel = new SortedListModel();
    private List<Fila> filaList = new ArrayList<>();
    private Set<Fila> filaSet = new LinkedHashSet<>();
    private List<Pozo> pozosSelectedList;

    private final CampoManager campoManager;
    private final MacollaManager macollaManager;
    private final PozoManager pozoManager;
    private final FilaManager filaManager;
    private final MacollaSecuenciaManager macollaSecuenciaManager;
    
    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form AsignarPozosAFilasIF
     */
    private AsignarPozosAFilasIF() {
        initComponents();
        setTitle("Asignar Pozos a Filas");
        setFrameIcon(icon);

        this.campoManager = new CampoManager();
        this.macollaManager = new MacollaManager();
        this.pozoManager = new PozoManager();
        this.filaManager = new FilaManager();
        this.macollaSecuenciaManager = new MacollaSecuenciaManager();

        init();
    }

    public static AsignarPozosAFilasIF getInstance() {
        if (instance == null) {
            instance = new AsignarPozosAFilasIF();
        }
        return instance;
    }

    private void init() {
        fillCampoComboBox();
        fillMacollasProcesadasTable();
    }

    private void clearForm() {
        DefaultTableModel model = (DefaultTableModel) pozosTable.getModel();
        model.setRowCount(0);
        fillCampoComboBox();
        if (campoComboBox.getModel().getSize() == 1) {
            fillMacollasComboBox((Campo) campoComboBox.getItemAt(0));
        }
        filaTextField.setText(null);
        listModel.clear();
        guardarButton.setEnabled(false);
        quitarButton.setEnabled(false);
        agregarButton.setEnabled(false);
        filaList.clear();
        this.repaint();
    }

    private void fillCampoComboBox() {
        List<Campo> campos = campoManager.findAll();
        Campo[] items = campos.toArray(new Campo[campos.size()]);
        DefaultComboBoxModel model = new DefaultComboBoxModel(items);
        campoComboBox.setModel(model);
    }

    private void fillMacollasComboBox(Campo campo) {

        macollaComboBox.removeAllItems();
        List<Macolla> macollas = macollaManager.findAll(campo);

        Set<Macolla> macollasNoConfigSet = new HashSet<>();
        Set<Macolla> macollasConfigSet = new HashSet<>();

        // Macollas no configuradas 
        macollas.stream().forEach(mac -> {
            macollasNoConfigSet.add(mac);
        });

        // Macollas configuradas
        List<Fila> filaConfigList = filaManager.findAll();
        filaConfigList.stream().forEach((fil) -> {
            macollasConfigSet.add(fil.getMacollaId());
        });

        // Elimina las macollas ya configuradas del ComboBox
        macollasConfigSet.stream().forEach(item -> {
            if (macollasNoConfigSet.contains(item)) {
                macollasNoConfigSet.remove(item);
            }
        });

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement("... seleccione una macolla");
        for (Macolla mac : macollasNoConfigSet) {
            model.addElement(mac);
        }
        macollaComboBox.setModel(model);
    }

    private void fillPozosList() {
        listModel.clear();
        pozosList.setModel(listModel);
        pozosList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        pozosList.setLayoutOrientation(JList.VERTICAL);
        if (macollaSelected != null) {
            List<Pozo> pozoList = pozoManager.findAll(macollaSelected);
            Pozo[] pozoArray = pozoList.toArray(new Pozo[pozoList.size()]);
            listModel.addAll(pozoArray);
            pozosList.setModel(listModel);
            agregarButton.setEnabled(true);
        }
    }

    private void fillPozosTable(List<Fila> lista) {
        pozosTable.removeAll();

        String[] titles = {"Macolla", "Fila", "Pozo"};
        int count = 0;
        for (Fila item : lista) {
            count += item.getPozoCollection().size();
        }
        Object[][] data = new Object[count][titles.length];
        int i = 0;
        for (Fila fila : lista) {
            for (Pozo well : fila.getPozoCollection()) {
                data[i][0] = fila.getMacollaId();
                data[i][1] = fila.getNombre();
                data[i][2] = well;
                if (listModel.contains(well)) {
                    listModel.removeElement(well);
                }
                i++;
            }
        }

        TableModel model = new DefaultTableModel(data, titles);
        pozosTable.setModel(model);
    }

    private void doSeleccion(boolean impar) {
        agregarButton.setEnabled(true);
        ListModel<Pozo> model = pozosList.getModel();
        List<Integer> indexesList = new ArrayList<>();
        for (int i = 0; i < model.getSize(); i++) {
            Pozo pozoImpar = model.getElementAt(i);
            String ubicacion = pozoImpar.getUbicacion();
            int posicion = Integer.parseInt(ubicacion.substring(ubicacion.lastIndexOf('-') + 1));
            if (impar) {
                if (posicion % 2 != 0) {
                    indexesList.add(i);
                }
            } else {
                if (posicion % 2 == 0) {
                    indexesList.add(i);
                }
            }
        }
        int[] indexes = new int[indexesList.size()];
        int i = 0;
        for (Integer objInt : indexesList) {
            indexes[i++] = objInt;
        }
        pozosList.setSelectedIndices(indexes);
    }

    private void fillMacollasProcesadasTable() {
        macollasProcesadasTable.removeAll();
        List<Fila> macollasProcesadasList = filaManager.findAll();
        Set<Macolla> macollasProcesadasSet = new HashSet<>();
        macollasProcesadasList.stream().forEach(item -> {
            macollasProcesadasSet.add(item.getMacollaId());
        });
        String[] titles = {"Macollas Configuradas"};
        Object[][] data = new Object[macollasProcesadasSet.size()][titles.length];
        int i = 0;
        for (Macolla item : macollasProcesadasSet) {
            data[i][0] = item;
            i++;
        }
        TableModel model = new DefaultTableModel(data, titles);
        macollasProcesadasTable.setModel(model);
    }

    private boolean checkCantFilas() {
        return (pozosList.getModel().getSize() <= 0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pozosButtonGroup = new javax.swing.ButtonGroup();
        backPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        campoComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        macollaComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pozosList = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        pozosImparesRadioButton = new javax.swing.JRadioButton();
        pozosParesRadioButton = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        pozosTable = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        macollasProcesadasTable = new javax.swing.JTable();
        filaTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        toolBar = new javax.swing.JToolBar();
        guardarButton = new javax.swing.JButton();
        limpiarButton = new javax.swing.JButton();
        agregarButton = new javax.swing.JButton();
        quitarButton = new javax.swing.JButton();
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
                onDeactivated(evt);
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Campo:");

        campoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoComboBoxActionPerformed(evt);
            }
        });

        jLabel2.setText("Macolla:");

        macollaComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                macollaComboBoxActionPerformed(evt);
            }
        });

        jLabel3.setText("Pozos:");

        jScrollPane1.setViewportView(pozosList);

        jLabel4.setText("Filas:");

        jLabel5.setText("Pozos:");

        pozosButtonGroup.add(pozosImparesRadioButton);
        pozosImparesRadioButton.setText("Impares");
        pozosImparesRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pozosImparesRadioButtonActionPerformed(evt);
            }
        });

        pozosButtonGroup.add(pozosParesRadioButton);
        pozosParesRadioButton.setText("Pares");
        pozosParesRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pozosParesRadioButtonActionPerformed(evt);
            }
        });

        pozosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Macolla", "Fila", "Pozo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(pozosTable);

        macollasProcesadasTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Macollas Configuradas"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        macollasProcesadasTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                macollasProcesadasTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(macollasProcesadasTable);

        jLabel6.setText("Selección:");

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(campoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2))
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(pozosImparesRadioButton)
                            .addComponent(pozosParesRadioButton)
                            .addComponent(filaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(backPanelLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(86, 86, 86)))
                        .addGap(17, 17, 17)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(macollaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(campoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(macollaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(filaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pozosImparesRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pozosParesRadioButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
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

        limpiarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconlimpiar26.png"))); // NOI18N
        limpiarButton.setText("Limpiar");
        limpiarButton.setFocusPainted(false);
        limpiarButton.setFocusable(false);
        limpiarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        limpiarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        limpiarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limpiarButtonActionPerformed(evt);
            }
        });
        toolBar.add(limpiarButton);

        agregarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconagregar.png"))); // NOI18N
        agregarButton.setText("Agregar");
        agregarButton.setBorder(null);
        agregarButton.setBorderPainted(false);
        agregarButton.setFocusable(false);
        agregarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        agregarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        agregarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarButtonActionPerformed(evt);
            }
        });
        toolBar.add(agregarButton);

        quitarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconquitar26.png"))); // NOI18N
        quitarButton.setText("Quitar");
        quitarButton.setFocusable(false);
        quitarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        quitarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        quitarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitarButtonActionPerformed(evt);
            }
        });
        toolBar.add(quitarButton);

        javax.swing.GroupLayout toolbarPanelLayout = new javax.swing.GroupLayout(toolbarPanel);
        toolbarPanel.setLayout(toolbarPanelLayout);
        toolbarPanelLayout.setHorizontalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolbarPanelLayout.createSequentialGroup()
                .addContainerGap(463, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );
        toolbarPanelLayout.setVerticalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        toolBar.add(toolbarPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 805, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 1, Short.MAX_VALUE))
                    .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        ComboBoxModel model = campoComboBox.getModel();
        if (model.getSize() == 1) {
            campo = (Campo) campoComboBox.getItemAt(0);
            fillMacollasComboBox(campo);
        }
        guardarButton.setEnabled(false);
        quitarButton.setEnabled(false);
        agregarButton.setEnabled(false);
        progressBar.setVisible(false);
        fillMacollasProcesadasTable();
        Contexto.setActiveFrame(instance);
    }//GEN-LAST:event_onActivated

    private void onDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onDeactivated
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        macollasProcesadasTable.setModel(new DefaultTableModel());
        clearForm();
    }//GEN-LAST:event_onDeactivated

    private void campoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoComboBoxActionPerformed
        campo = (Campo) campoComboBox.getSelectedItem();
        fillMacollasComboBox(campo);
    }//GEN-LAST:event_campoComboBoxActionPerformed

    private void macollaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_macollaComboBoxActionPerformed
        if (macollaComboBox.getSelectedItem() instanceof Macolla) {
            macollaSelected = (Macolla) macollaComboBox.getSelectedItem();
            fillPozosList();
        } else {
            clearForm();
        }
        Contexto.showMessage(null, Constantes.MENSAJE_ERROR);
    }//GEN-LAST:event_macollaComboBoxActionPerformed

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private boolean exito = false;

            @Override
            protected Void doInBackground() throws Exception {
                setProgress(0);
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);
                try {
                    if (!checkCantFilas()) {
                        Contexto.showMessage("Posiblemente no ha completado todas las filas",
                                Constantes.MENSAJE_ERROR);
                        done();
                    } else {
                        Set<Fila> filasTemp = new LinkedHashSet<>();
                        Map<String, Collection<Pozo>> pozosColl = new HashMap<>();
                        
                        for (Fila fil : filaSet) {
                            Fila fila = new Fila();
                            fila.setNombre(fil.getNombre());
                            fila.setMacollaId(fil.getMacollaId());
                            filasTemp.add(fila);
                            pozosColl.put(fila.getNombre(), fil.getPozoCollection());
                        }
                        
                        filaManager.batchSave(filasTemp);
                        filasTemp = new LinkedHashSet(filaManager.findAll(macollaSelected));
                        
                        List<Pozo> pozosEnDb = pozoManager.findAll(macollaSelected);
                        Map<String, Pozo> mapPozosEnDb = new HashMap<>();
                        for (Pozo pozo : pozosEnDb) {
                            mapPozosEnDb.put(pozo.getUbicacion(), pozo);
                        }
                                                
                        List<Pozo> pozosActualizados = new ArrayList<>();
                        for (Fila fila : filasTemp) {
                            for (Pozo pozo : pozosColl.get(fila.getNombre())) {
                                Pozo pozoAct = mapPozosEnDb.get(pozo.getUbicacion());
                                pozoAct.setFilaId(fila);
                                pozosActualizados.add(pozoAct);
                            }
                        }
                        
                        pozoManager.batchEdit(pozosActualizados);
                        
                        filaSet.clear();

                        fillMacollasProcesadasTable();
                        clearForm();
                        filaList.clear();
                        exito = true;
                    }
                } catch (Exception ex) {
                    sismonlog.logger.log(Level.SEVERE, "Error asignando los pozos a filas", ex);
                }
                return null;
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                guardarButton.setEnabled(false);
            }
        };
        worker.execute();
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void quitarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitarButtonActionPerformed
        List<Fila> filaConfList = filaManager.findAll(macollaSelected);
        List<Pozo> pozosEnDb = pozoManager.findAll(macollaSelected);

        pozosEnDb.stream().forEach((p) -> {
            p.setFilaId(null);
        });
        pozoManager.batchEdit(pozosEnDb);

        filaConfList.stream().forEach((f) -> {
            filaManager.remove(f);
        });
        fillMacollasProcesadasTable();
        clearForm();
        Contexto.showMessage("Pozos desasignados con éxito de las filas de la macolla "
                + macollaSelected.toString(), Constantes.MENSAJE_INFO);
    }//GEN-LAST:event_quitarButtonActionPerformed

    private void agregarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarButtonActionPerformed
        // Prepara la data para la tabla
        Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
        Fila filaSelected = null;
        if (filaTextField.getText().isEmpty()) {
            Contexto.showMessage("Debe colocar la fila", Constantes.MENSAJE_ERROR);
            return;
        } else {
            filaSelected = new Fila();
            filaSelected.setNombre("Fila - " + filaTextField.getText());
        }

        pozosSelectedList = pozosList.getSelectedValuesList();

        if (!pozosSelectedList.isEmpty()) {
            Fila otherFila;
            if (filaManager.find(filaSelected.getNombre(), macollaSelected) != null) {
                otherFila = filaManager.find(filaSelected.getNombre(), macollaSelected);
            } else {
                otherFila = new Fila();
            }
            otherFila.setMacollaId(macollaSelected);
            otherFila.setNombre(filaSelected.getNombre());
            otherFila.setPozoCollection(pozosSelectedList);
            filaList.add(otherFila);

            fillPozosTable(filaList);
            filaSet.addAll(filaList);

            pozosList.clearSelection();
            guardarButton.setEnabled(true);

            pozosButtonGroup.clearSelection();
            filaTextField.setText(null);
        } else {
            Contexto.showMessage("Debe seleccionar los pozos a asignar", Constantes.MENSAJE_ERROR);
        }
    }//GEN-LAST:event_agregarButtonActionPerformed

    private void limpiarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limpiarButtonActionPerformed
        clearForm();
    }//GEN-LAST:event_limpiarButtonActionPerformed

    private void pozosImparesRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pozosImparesRadioButtonActionPerformed
        doSeleccion(true);
    }//GEN-LAST:event_pozosImparesRadioButtonActionPerformed

    private void pozosParesRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pozosParesRadioButtonActionPerformed
        doSeleccion(false);
    }//GEN-LAST:event_pozosParesRadioButtonActionPerformed

    private void macollasProcesadasTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_macollasProcesadasTableMouseClicked
        clearForm();

        try {
            Object macollaObj = macollasProcesadasTable.getModel().getValueAt(
                    macollasProcesadasTable.getSelectedRow(), 0);
            if (macollaObj instanceof Macolla) {
                macollaSelected = (Macolla) macollaObj;
                List<Fila> pozoFilaConfList = filaManager.findAll(macollaSelected);
                List<Fila> filas = new ArrayList<>();
                for(Fila fil : pozoFilaConfList){
                    List<Pozo> pozos = pozoManager.findAll(fil.getMacollaId(), fil);
                    Fila fila = fil;
                    fila.setPozoCollection(pozos);
                    filas.add(fil);
                }
                
                fillPozosTable(filas);
                quitarButton.setEnabled(true);
            } else {
                sismonlog.logger.log(Level.SEVERE, "el objeto no es instancia de macolla");
            }

        } catch (ClassCastException e) {
            sismonlog.logger.log(Level.SEVERE, "Error convirtiendo el objeto ", e);
        }
    }//GEN-LAST:event_macollasProcesadasTableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregarButton;
    private javax.swing.JPanel backPanel;
    private javax.swing.JComboBox campoComboBox;
    private javax.swing.JTextField filaTextField;
    private javax.swing.JButton guardarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton limpiarButton;
    private javax.swing.JComboBox macollaComboBox;
    private javax.swing.JTable macollasProcesadasTable;
    private javax.swing.ButtonGroup pozosButtonGroup;
    private javax.swing.JRadioButton pozosImparesRadioButton;
    private javax.swing.JList pozosList;
    private javax.swing.JRadioButton pozosParesRadioButton;
    private javax.swing.JTable pozosTable;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton quitarButton;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolbarPanel;
    // End of variables declaration//GEN-END:variables

}
