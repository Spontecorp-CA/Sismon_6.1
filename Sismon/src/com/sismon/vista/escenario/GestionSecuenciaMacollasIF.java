package com.sismon.vista.escenario;

import com.sismon.controller.Constantes;
import com.sismon.controller.SortedListModel;
import com.sismon.exceptions.SismonException;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.jpamanager.MacollaManager;
import com.sismon.jpamanager.MacollaSecuenciaManager;
import com.sismon.model.Escenario;
import com.sismon.model.Macolla;
import com.sismon.model.MacollaSecuencia;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Toolkit;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

public class GestionSecuenciaMacollasIF extends javax.swing.JInternalFrame {

    private static GestionSecuenciaMacollasIF instance = null;

    private Escenario escenarioSelected;
    private Set<Macolla> macollas;
    private final SortedListModel originalListModel = new SortedListModel();
    private final DefaultListModel finalListModel = new DefaultListModel();
    private boolean ordenMacollasClear = true;

    private final EscenarioManager escenarioManager;
    private final MacollaSecuenciaManager macollaSecuenciaManager;
    private final MacollaManager macollaManager;

    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form GestionSecuenciaMacollasIF
     */
    private GestionSecuenciaMacollasIF() {
        initComponents();
        setFrameIcon(icon);

        this.escenarioManager = new EscenarioManager();
        this.macollaSecuenciaManager = new MacollaSecuenciaManager();
        this.macollaManager = new MacollaManager();
    }

    public static GestionSecuenciaMacollasIF getInstance() {
        if (instance == null) {
            instance = new GestionSecuenciaMacollasIF();
        }
        return instance;
    }

    private void fillEscenariosComboBox() {
        List<Escenario> escenarios = escenarioManager.findAllMV(false);
        escenarioComboBox.removeAllItems();
        escenarioComboBox.addItem("... seleccione Escenario"); //JAP
        if (escenarioComboBox.getSelectedItem() instanceof Escenario) {  //JAP
            originalListModel.clear();                      // JAP
            originalList.setModel(originalListModel);       // JAP
            finalListModel.clear();                         // JAP
            finalList.setModel(finalListModel);             // JAP
        }
        escenarios.stream().forEach(esc -> {
            escenarioComboBox.addItem(esc);
        });
    }

    private void fillMacollaList() {
        macollas = new HashSet<>(findMacollasXEscenario());
        List<Macolla> macollasOrdered = findMacollasXEscenario();
        
        originalListModel.clear();
        originalList.setModel(originalListModel);
        finalListModel.clear();
        finalList.setModel(finalListModel);
        //macollas.stream().forEach((macolla) -> {
        macollasOrdered.stream().forEach((macolla) -> {
            finalListModel.addElement(macolla);
        });
        finalList.setModel(finalListModel);
        
        //colocar las macollas que no esten en la lista de perforación
        List<Macolla> macollasEnCampo = macollaManager.findAll();
        macollasEnCampo.removeAll(macollas);
        
        macollasEnCampo.forEach(mac -> {
            originalListModel.addElement(mac);
        });
        originalList.setModel(originalListModel);
        
    }

    private List<Macolla> findMacollasXEscenario() {
        List<Macolla> macollaList = null;
        try {
            macollaList = macollaSecuenciaManager.findAll(escenarioSelected);   
        } catch (SismonException ex) {
            sismonlog.logger.log(Level.SEVERE, null, ex);
            Contexto.showMessage(ex.getMessage(), Constantes.MENSAJE_ERROR);
        }
        return macollaList;
    }

    private void setGuardarButtonDisable() {
        if (guardarButton.isEnabled()) {
            guardarButton.setEnabled(false);
        }
        if (!ordenMacollasClear) {
            clearOrdenMacollas();
        }
    }
    
    private void setGuardarButtonEnable(){
        if (!guardarButton.isEnabled()) {
            guardarButton.setEnabled(true);
        }
        if (!ordenMacollasClear) {
            clearOrdenMacollas();
        }
    }

    private void clearOrdenMacollas() {
        List<MacollaSecuencia> omList = macollaSecuenciaManager.findAllOrdered(escenarioSelected);
        if (!omList.isEmpty()) {
            for (MacollaSecuencia sec : omList) {
                sec.setSecuencia(null);
                macollaSecuenciaManager.remove(sec);
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

        toolBar = new javax.swing.JToolBar();
        guardarButton = new javax.swing.JButton();
        toolBarPanel = new javax.swing.JPanel();
        escenarioComboBox = new javax.swing.JComboBox();
        backPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        originalList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        agregarButton = new javax.swing.JButton();
        agregarAllButton = new javax.swing.JButton();
        removerButton = new javax.swing.JButton();
        removerAllButton = new javax.swing.JButton();
        subirButton = new javax.swing.JButton();
        bajarButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        finalList = new javax.swing.JList();

        setClosable(true);
        setIconifiable(true);
        setTitle("Secuencia de Perforación de las Macollas");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
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

        escenarioComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escenarioComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout toolBarPanelLayout = new javax.swing.GroupLayout(toolBarPanel);
        toolBarPanel.setLayout(toolBarPanelLayout);
        toolBarPanelLayout.setHorizontalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolBarPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(252, Short.MAX_VALUE))
        );
        toolBarPanelLayout.setVerticalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolBarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        toolBar.add(toolBarPanel);

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Macollas sin Asignar Orden:");

        jScrollPane2.setViewportView(originalList);

        jLabel2.setText(" Macollas en Orden de Perforación:");

        agregarButton.setText("Agregar >");
        agregarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarButtonActionPerformed(evt);
            }
        });

        agregarAllButton.setText("+ Todos >>");
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

        removerAllButton.setText("<< Todos -");
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

        jScrollPane1.setViewportView(finalList);

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(agregarButton)
                            .addComponent(agregarAllButton)
                            .addComponent(removerButton)
                            .addComponent(removerAllButton)
                            .addComponent(subirButton)
                            .addComponent(bajarButton))))
                .addGap(18, 18, 18)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        backPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {agregarAllButton, agregarButton, bajarButton, removerAllButton, removerButton, subirButton});

        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        Macolla mackie;
        Set<Macolla> macollasSelected = new LinkedHashSet<>();
        if (finalList.getSelectedIndices().length == 0) {
            Contexto.showMessage("Debe primero seleccionar una macolla para remover",
                    Constantes.MENSAJE_ERROR);
        } else {
            if (finalList.getSelectedIndices().length > 1) {
                int[] selectedIndices = finalList.getSelectedIndices();
                for (int i = 0; i < selectedIndices.length; i++) {
                    mackie = (Macolla) finalListModel.getElementAt(selectedIndices[i]);
                    macollasSelected.add(mackie);
                }
                // Agregar a la lista nueva
                macollasSelected.stream().forEach((macolla) -> {
                    originalListModel.addElement(macolla);
                    finalListModel.removeElement(macolla);
                });
                
                System.out.println("Llego al if");
                
            } else {
                setGuardarButtonDisable();
                Macolla value = (Macolla) finalList.getSelectedValue();
                originalListModel.addElement(value);
                originalList.setModel(originalListModel);
                finalListModel.removeElement(value);
                finalList.setModel(finalListModel);
                
                System.out.println("Llegó al else");
                
            }
            setGuardarButtonEnable();
    }//GEN-LAST:event_removerButtonActionPerformed
    
    }
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

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        guardarButton.setEnabled(false);
        fillEscenariosComboBox();
        Contexto.setActiveFrame(instance);
    }//GEN-LAST:event_formInternalFrameActivated

    private void escenarioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escenarioComboBoxActionPerformed
        if (escenarioComboBox.getSelectedItem() instanceof Escenario) {
            escenarioSelected = (Escenario) escenarioComboBox.getSelectedItem();
            List<MacollaSecuencia> macollaSecList = macollaSecuenciaManager.findAllOrdered(escenarioSelected);
            if (!macollaSecList.isEmpty()) {
                fillMacollaList();
            }
        }
    }//GEN-LAST:event_escenarioComboBoxActionPerformed

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                System.out.println("LLegó a guardar los cambios");
                
                clearOrdenMacollas();
                for (int i = 0; i < finalListModel.getSize(); i++) {
                    Macolla macolla = (Macolla) finalListModel.getElementAt(i);
                    MacollaSecuencia secuencia = new MacollaSecuencia();
                    secuencia.setEscenarioId(escenarioSelected);
                    secuencia.setSecuencia(i + 1);
                    secuencia.setMacollaId(macolla);
                    
                    macollaSecuenciaManager.edit(secuencia);
                }
                return null;
            }

            @Override
            protected void done() {
                ordenMacollasClear = false;
                guardarButton.setEnabled(false);
                Contexto.showMessage("Secuencia de perforación guardada con éxito", Constantes.MENSAJE_INFO);
            }

        };
        worker.execute();
    }//GEN-LAST:event_guardarButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregarAllButton;
    private javax.swing.JButton agregarButton;
    private javax.swing.JPanel backPanel;
    private javax.swing.JButton bajarButton;
    private javax.swing.JComboBox escenarioComboBox;
    private javax.swing.JList finalList;
    private javax.swing.JButton guardarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList originalList;
    private javax.swing.JButton removerAllButton;
    private javax.swing.JButton removerButton;
    private javax.swing.JButton subirButton;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolBarPanel;
    // End of variables declaration//GEN-END:variables
}
