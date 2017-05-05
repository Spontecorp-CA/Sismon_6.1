package com.sismon.vista.configuracion;

import com.sismon.controller.Constantes;
import com.sismon.controller.SortedListModel;
import com.sismon.jpamanager.CampoManager;
import com.sismon.jpamanager.MacollaManager;
import com.sismon.jpamanager.MacollaSecuenciaManager;
import com.sismon.model.Campo;
import com.sismon.model.Macolla;
import com.sismon.model.MacollaSecuencia;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

public class ConfiguraSecuenciaMacollasIF extends javax.swing.JInternalFrame 
            implements PropertyChangeListener{

    private static ConfiguraSecuenciaMacollasIF instance = null;

    private Set<Macolla> macollas;
    private final SortedListModel originalListModel = new SortedListModel();
    private final DefaultListModel finalListModel = new DefaultListModel();
    private Campo campoSelected;
    private boolean ordenMacollasClear = true;

    private final CampoManager campoManager;
    private final MacollaManager macollaManager;
    private final MacollaSecuenciaManager secuenciaManager;
    
    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form ConfiguraSecuenciaMacollasIF
     */
    private ConfiguraSecuenciaMacollasIF() {
        initComponents();
        setTitle("Orden de Perforación de Macollas");
        setFrameIcon(icon);

        campoManager = new CampoManager();
        macollaManager = new MacollaManager();
        secuenciaManager = new MacollaSecuenciaManager();

        macollas = new HashSet<>();
        progressBar.setVisible(false);
    }

    public static ConfiguraSecuenciaMacollasIF getInstance() {
        if (instance == null) {
            instance = new ConfiguraSecuenciaMacollasIF();
        }
        return instance;
    }

    private void init() {
        guardarButton.setEnabled(false);
        campoSelected = (Campo)campoComboBox.getItemAt(0);
        
        List<MacollaSecuencia> omList = secuenciaManager.findAll();
        if (omList.isEmpty()) {
            fillMacollasList();
            finalListModel.clear();
            finalList.setModel(finalListModel);
        } else {
            finalListModel.clear();
            List<Macolla> macollasList = findMacollaXCampo(campoSelected);
//            Collections.sort(macollasList, Macolla.COMPARE_BY_ORDEN);
            Map<Integer, Macolla> macollaOrdered = sortMacollasBySequence(macollasList);
//            macollasList.stream().forEach(item -> {
//                finalListModel.addElement(item);
//            });
            for(Map.Entry<Integer, Macolla> map : macollaOrdered.entrySet()){
                finalListModel.addElement(map.getValue());
            }
            finalList.setModel(finalListModel);
            originalListModel.clear();
            originalList.setModel(originalListModel);
        }
    }

    private void fillCampoComboBox() {
        List<Campo> campos = campoManager.findAll();
        Campo[] items = campos.toArray(new Campo[campos.size()]);
        DefaultComboBoxModel model = new DefaultComboBoxModel(items);
        campoComboBox.setModel(model);
    }

    private void fillMacollasList() {
        macollas = new HashSet<>(findMacollaXCampo(campoSelected));
        macollas.stream().forEach((macolla) -> {
            originalListModel.addElement(macolla);
        });
        originalList.setModel(originalListModel);
    }

    private List<Macolla> findMacollaXCampo(Campo campo) {
        List<Macolla> macollaList = macollaManager.findAll(campo);
        return macollaList;
    }

    private Map<Integer, Macolla> sortMacollasBySequence(List<Macolla> macollaList) {
        Map<Integer, Macolla> macollasOrdered = new TreeMap<>();
        
        for (Macolla mac : macollaList) {
            List<MacollaSecuencia> secList = secuenciaManager.findAll(mac);
            if(!secList.isEmpty()){
                macollasOrdered.put(secList.get(0).getSecuencia(), secList.get(0).getMacollaId());
            }
        }
        
        return macollasOrdered;
    }
    
    private void setGuardarButtonDisable() {
        if (guardarButton.isEnabled()) {
            guardarButton.setEnabled(false);
        }
        if (!ordenMacollasClear) {
            clearOrdenMacollas();
        }
    }
    
    private void clearOrdenMacollas() {
        List<MacollaSecuencia> omList = secuenciaManager.findAll();
        if (!omList.isEmpty()) {
            for(MacollaSecuencia sec : omList){
                if(sec.getEscenarioId() == null){
                    sec.setSecuencia(null);
                    secuenciaManager.remove(sec);
                }
            }

        }
        ordenMacollasClear = true;
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
        finalList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        originalList = new javax.swing.JList();
        agregarButton = new javax.swing.JButton();
        agregarAllButton = new javax.swing.JButton();
        removerButton = new javax.swing.JButton();
        removerAllButton = new javax.swing.JButton();
        subirButton = new javax.swing.JButton();
        bajarButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        campoComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        toolBar = new javax.swing.JToolBar();
        guardarButton = new javax.swing.JButton();
        progressPanel = new javax.swing.JPanel();
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

        jScrollPane1.setViewportView(finalList);

        jScrollPane2.setViewportView(originalList);

        agregarButton.setText("Agregar >");
        agregarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarButtonActionPerformed(evt);
            }
        });

        agregarAllButton.setText("+ Todas >>");
        agregarAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarAllButtonActionPerformed(evt);
            }
        });

        removerButton.setText("< Remover");
        removerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removerButtonActionPerformed(evt);
            }
        });

        removerAllButton.setText("<< Todas -");
        removerAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removerAllButtonActionPerformed(evt);
            }
        });

        subirButton.setText("Subir");
        subirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subirButtonActionPerformed(evt);
            }
        });

        bajarButton.setText("Bajar");
        bajarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajarButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Macollas en el campo:");

        jLabel2.setText("Orden de Perforación:");

        campoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoComboBoxActionPerformed(evt);
            }
        });

        jLabel3.setText("Campo:");

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(backPanelLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(campoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(agregarButton)
                            .addComponent(agregarAllButton)
                            .addComponent(removerButton)
                            .addComponent(removerAllButton)
                            .addComponent(subirButton)
                            .addComponent(bajarButton))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        backPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {agregarAllButton, agregarButton, bajarButton, removerAllButton, removerButton, subirButton});

        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(campoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(agregarButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(agregarAllButton)
                        .addGap(27, 27, 27)
                        .addComponent(removerButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removerAllButton)
                        .addGap(34, 34, 34)
                        .addComponent(subirButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bajarButton)))
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

        javax.swing.GroupLayout progressPanelLayout = new javax.swing.GroupLayout(progressPanel);
        progressPanel.setLayout(progressPanelLayout);
        progressPanelLayout.setHorizontalGroup(
            progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, progressPanelLayout.createSequentialGroup()
                .addContainerGap(313, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        progressPanelLayout.setVerticalGroup(
            progressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(progressPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        toolBar.add(progressPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(toolBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        fillCampoComboBox();
        init();
        Contexto.setActiveFrame(instance);
    }//GEN-LAST:event_onActivated

    private void campoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoComboBoxActionPerformed
        campoSelected = (Campo) campoComboBox.getSelectedItem();
        fillMacollasList();
    }//GEN-LAST:event_campoComboBoxActionPerformed

    private void agregarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarButtonActionPerformed
        Macolla mackie;// = null;
        Set<Macolla> macollasSelected = new LinkedHashSet<>();
        if (originalList.getSelectedIndices().length == 0) {
            Contexto.showMessage("Debe primero seleccionar una macolla para agregar", 
                    Constantes.MENSAJE_ERROR);
        } else {
            if (originalList.getSelectedIndices().length > 1) {
                int[] selectedIndices = originalList.getSelectedIndices();
                for (int i = 0; i < selectedIndices.length; i++) {
                    mackie = (Macolla) originalListModel.getElementAt(selectedIndices[i]);
                    macollasSelected.add(mackie);
                }
                // Agregar a la lista nueva
                macollasSelected.stream().forEach((macolla) -> {
                    finalListModel.addElement(macolla);
                });
                // Remover de la lista vieja
                macollasSelected.stream().forEach((macolla) -> {
                    originalListModel.removeElement(macolla);
                });

            } else {
                mackie = (Macolla) originalList.getSelectedValue();
                finalListModel.addElement(mackie);
                originalListModel.removeElement(mackie);
            }
        }
        finalList.setModel(finalListModel);
        originalList.setModel(originalListModel);
        if (originalListModel.getSize() == 0) {
            guardarButton.setEnabled(true);
        }
    }//GEN-LAST:event_agregarButtonActionPerformed

    private void agregarAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarAllButtonActionPerformed
        Macolla[] macollaArray = new Macolla[originalListModel.getSize()];
        for (int i = 0; i < originalListModel.getSize(); i++) {
            macollaArray[i] = (Macolla) originalListModel.getElementAt(i);
        }
        for (Macolla macolla : macollaArray) {
            originalListModel.removeElement(macolla);
            finalListModel.addElement(macolla);
        }

        finalList.setModel(finalListModel);
        originalList.setModel(originalListModel);
        if (originalListModel.getSize() == 0) {
            guardarButton.setEnabled(true);
        }
    }//GEN-LAST:event_agregarAllButtonActionPerformed

    private void removerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removerButtonActionPerformed
        if (finalList.getSelectedIndices().length == 0) {
            Contexto.showMessage("Debe primero seleccionar una macolla para remover", 
                    Constantes.MENSAJE_ERROR);
        } else {
            setGuardarButtonDisable();
            Macolla value = (Macolla) finalList.getSelectedValue();
            originalListModel.addElement(value);
            originalList.setModel(originalListModel);
            finalListModel.removeElement(value);
            finalList.setModel(finalListModel);
        }
    }//GEN-LAST:event_removerButtonActionPerformed

    private void removerAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removerAllButtonActionPerformed
        Macolla[] macollaArray = new Macolla[finalListModel.getSize()];
        for (int i = 0; i < finalListModel.getSize(); i++) {
            macollaArray[i] = (Macolla) finalListModel.getElementAt(i);
        }
        for (Macolla macolla : macollaArray) {
            finalListModel.removeElement(macolla);
            originalListModel.addElement(macolla);
        }

        finalList.setModel(finalListModel);
        originalList.setModel(originalListModel);
        guardarButton.setEnabled(false);
    }//GEN-LAST:event_removerAllButtonActionPerformed

    private void subirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subirButtonActionPerformed
        setGuardarButtonDisable();
        Macolla value = (Macolla) finalList.getSelectedValue();
        int index = finalList.getSelectedIndex();
        if (index != 0) {
            finalListModel.remove(index--);
            finalListModel.add(index, value);
            finalList.setModel(finalListModel);
            finalList.setSelectedValue(value, false);
        }
        guardarButton.setEnabled(true);
    }//GEN-LAST:event_subirButtonActionPerformed

    private void bajarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajarButtonActionPerformed
        setGuardarButtonDisable();
        int listSize = finalListModel.getSize();
        Macolla value = (Macolla) finalList.getSelectedValue();
        int index = finalList.getSelectedIndex();
        if ((index + 1) != listSize) {
            if (index < listSize) {
                finalListModel.remove(index++);
                finalListModel.add(index, value);
                finalList.setModel(finalListModel);
                finalList.setSelectedValue(value, false);
            }
        }
        guardarButton.setEnabled(true);
    }//GEN-LAST:event_bajarButtonActionPerformed

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                setProgress(0);
                progressBar.setVisible(true);
                clearOrdenMacollas();
                int maxCount = finalListModel.getSize();
                int count = 0;
                int progress = 0;
                progressBar.setIndeterminate(false);
                for (int i = 0; i < finalListModel.getSize(); i++) {
                    Macolla macolla = (Macolla) finalListModel.getElementAt(i);
                    MacollaSecuencia secuencia = new MacollaSecuencia();
                    //OrdenMacollas ordenMacollas = new OrdenMacollas();
                    secuencia.setEscenarioId(null);
                    secuencia.setSecuencia(i + 1);
                    secuencia.setMacollaId(macolla);
                    count++;
                    progress = 100 * count / maxCount;
                    setProgress(progress);
                    //macollaManager.edit(macolla);
                    secuenciaManager.edit(secuencia);
                }
                return null;
            }

            @Override
            protected void done() {
                ordenMacollasClear = false;
                guardarButton.setEnabled(false);
                progressBar.setVisible(false);
                Contexto.showMessage("Secuencia de perforación guardada con éxito", Constantes.MENSAJE_INFO);
            }
            
        };
        worker.addPropertyChangeListener(this);
        worker.execute();
    }//GEN-LAST:event_guardarButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregarAllButton;
    private javax.swing.JButton agregarButton;
    private javax.swing.JPanel backPanel;
    private javax.swing.JButton bajarButton;
    private javax.swing.JComboBox campoComboBox;
    private javax.swing.JList finalList;
    private javax.swing.JButton guardarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList originalList;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel progressPanel;
    private javax.swing.JButton removerAllButton;
    private javax.swing.JButton removerButton;
    private javax.swing.JButton subirButton;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }
    }
}
