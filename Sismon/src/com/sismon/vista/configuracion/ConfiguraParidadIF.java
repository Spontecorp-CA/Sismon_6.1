package com.sismon.vista.configuracion;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.ParidadManager;
import com.sismon.model.Paridad;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import com.sismon.vista.utilities.Utils;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ConfiguraParidadIF extends javax.swing.JInternalFrame {

    private static ConfiguraParidadIF instance = null;
    
    private final ParidadManager paridadManager;
    private Paridad laParidad;
    private boolean changeOk = false;
    private boolean noUpdate = true;
    private DecimalFormat decf = new DecimalFormat("###,##0.00");
    
    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));
    
    private ConfiguraParidadIF() {
        initComponents();
        setTitle("Paridad Cambiaria");
        setFrameIcon(icon);
        
        this.paridadManager = new ParidadManager();
    }

    public static ConfiguraParidadIF getInstance() {
        if(instance == null){
            instance = new ConfiguraParidadIF();
        }
        return instance;
    }
    
    private void configureComponentListeners() {
        paridadTextField.getDocument().addDocumentListener(docListener);
        fechaVigenciaDateChooser.getDateEditor().addPropertyChangeListener(compListener);
        fechaVencimientoDateChooser.getDateEditor().addPropertyChangeListener(compListener);
    }

    private void removeComponentListeners() {
        paridadTextField.getDocument().removeDocumentListener(docListener);
        fechaVigenciaDateChooser.getDateEditor().removePropertyChangeListener(compListener);
        fechaVencimientoDateChooser.getDateEditor().removePropertyChangeListener(compListener);
    }
    
    private void clearForm() {
        paridadTextField.setText(null);
        fechaVigenciaDateChooser.setDate(null);
        fechaVencimientoDateChooser.setDate(null);
        enableButtons(false);
    }
    
    private void updateForm() {
        changeOk = false;
        noUpdate = true;

        paridadTextField.setText(decf.format(laParidad.getValor()));
        fechaVigenciaDateChooser.setDate(laParidad.getFechaIn());
        if (laParidad.getFechaOut() != null) {
            fechaVencimientoDateChooser.setDate(laParidad.getFechaOut());
        }

        changeOk = true;
        noUpdate = false;
    }
    
    private void enableButtons(boolean enable) {
        guardarButton.setEnabled(enable);
        updateButton.setEnabled(enable);
        deleteButton.setEnabled(enable);
    }
    
    private void modify() {
        enableButtons(true);
    }
    
    private void updateModel() {
        laParidad.setValor(parseDouble(paridadTextField.getText()));
        laParidad.setFechaIn(fechaVigenciaDateChooser.getDate());
        if (fechaVencimientoDateChooser.getDate() != null) {
            laParidad.setFechaOut(fechaVencimientoDateChooser.getDate());
            laParidad.setStatus(Constantes.PARIDAD_VENCIDA);
        } else {
            laParidad.setFechaOut(null);
            laParidad.setStatus(Constantes.PARIDAD_ACTIVA);
        }

    }
    
    private boolean checkExist() {
        boolean result = true;
        try {
            double valor = Utils.parseDouble(paridadTextField.getText());
            Date fecha = fechaVencimientoDateChooser.getDate();
            Paridad paridad = paridadManager.find(valor, fecha);
            if (paridad == null) {
                result = false;
            }
        } catch (NumberFormatException e) {
            Contexto.showMessage("Debe colocar un valor válido en la paridad", Constantes.MENSAJE_ERROR);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
    
    private void fillDataTable() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        dataTable.removeAll();
        List<Paridad> paridadList = paridadManager.findAll();
        String[] encabezado = {"Paridad(Bs/US$)", "Fecha vigencia", "Fecha vencimiento", "Status"};
        Object[][] data = new Object[paridadList.size()][encabezado.length];

        int i = 0;
        for (Paridad item : paridadList) {
            data[i][0] = decf.format(item.getValor());
            data[i][1] = df.format(item.getFechaIn());
            if (item.getFechaOut() != null) {
                data[i][2] = df.format(item.getFechaOut());
            } else {
                data[i][2] = "";
            }
            data[i][3] = parseStatus(item.getStatus());
            i++;
        }
        TableModel model = new DefaultTableModel(data, encabezado);
        dataTable.setModel(model);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int j = 0; j < dataTable.getColumnCount(); j++) {
            dataTable.getColumnModel().getColumn(j).setCellRenderer(centerRenderer);
        }
    }
    
    private String parseStatus(int status) {
        String value = "";
        switch (status) {
            case 0:
                value = "VENCIDA";
                break;
            case 1:
                value = "ACTIVA";
                break;
        }
        return value;
    }
    
    private Double parseDouble(String numero) {
        double newNumero = 0.0;
        if (numero.contains(",")) {
            numero = numero.replace(",", ".");
        }
        newNumero = Double.parseDouble(numero);
        return newNumero;
    }
    
    private Date parseFecha(String fecha) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaRetorno = null;
        try {
            fechaRetorno = df.parse(fecha);
        } catch (ParseException ex) {
            sismonlog.logger.log(Level.SEVERE, "Error convirtiendo la fecha", ex);
        }
        return fechaRetorno;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        guardarButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        backPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        paridadTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        fechaVigenciaDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        fechaVencimientoDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();

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

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setMinimumSize(new java.awt.Dimension(182, 182));

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
        jToolBar1.add(guardarButton);

        updateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconupdate26.png"))); // NOI18N
        updateButton.setText("Actualizar");
        updateButton.setFocusable(false);
        updateButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(updateButton);

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icondelete26.png"))); // NOI18N
        deleteButton.setText("Eliminar");
        deleteButton.setFocusable(false);
        deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(deleteButton);

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
        jToolBar1.add(clearButton);

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dataTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(dataTable);

        jLabel1.setText("Paridad cambiaria:");

        jLabel2.setText("Fecha vigencia:");

        jLabel3.setText("fecha vencimiento:");

        jLabel4.setText("Bs/US$");

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(fechaVigenciaDateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fechaVencimientoDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(backPanelLayout.createSequentialGroup()
                                .addComponent(paridadTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE))
                .addContainerGap())
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(paridadTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2))
                    .addComponent(fechaVigenciaDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(fechaVencimientoDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        if (checkExist()) {
            Contexto.showMessage("El valor y/o fecha ingresado ya existe, "
                    + "por favor intente nuevamente", Constantes.MENSAJE_ERROR);
            paridadTextField.setText(null);
            fechaVencimientoDateChooser.cleanup();
            return;
        }

        if (!paridadManager.findAll().isEmpty()) {
            try {
                Paridad oldParidad = paridadManager.find(Constantes.PARIDAD_ACTIVA);
                oldParidad.setStatus(Constantes.PARIDAD_VENCIDA);
                oldParidad.setFechaOut(new Date(fechaVigenciaDateChooser.getDate().getTime() - (1000 * 60 * 60 * 24)));
                paridadManager.edit(oldParidad);
            } catch (Exception e) {
                sismonlog.logger.log(Level.SEVERE, "Error buscando la paridad activa", e);
            }
        }

        laParidad = new Paridad();
        updateModel();
        paridadManager.create(laParidad);
        Contexto.showMessage("Paridad cambiaria creada con éxito", Constantes.MENSAJE_INFO);
        fillDataTable();
        clearForm();
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clearForm();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        configureComponentListeners();
        enableButtons(false);
        clearForm();
        fillDataTable();
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        Contexto.setActiveFrame(instance);
        changeOk = true;
    }//GEN-LAST:event_onActivated

    private void onDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onDeactivated
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        removeComponentListeners();
    }//GEN-LAST:event_onDeactivated

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        updateModel();
        paridadManager.edit(laParidad);
        fillDataTable();
        clearForm();
        enableButtons(false);
    }//GEN-LAST:event_updateButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        updateModel();
        paridadManager.remove(laParidad);
        fillDataTable();
        clearForm();
        enableButtons(false);
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void dataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataTableMouseClicked
        TableModel tableModel = dataTable.getModel();
        double valor = parseDouble((String) tableModel.getValueAt(dataTable.getSelectedRow(), 0));
        Date fechaVigencia = parseFecha((String) tableModel.getValueAt(dataTable.getSelectedRow(), 1));
        try {
            laParidad = paridadManager.find(valor, fechaVigencia);
        } catch (Exception e) {
            // do nothing
        }
        updateForm();
        enableButtons(true);
    }//GEN-LAST:event_dataTableMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JButton clearButton;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton deleteButton;
    private com.toedter.calendar.JDateChooser fechaVencimientoDateChooser;
    private com.toedter.calendar.JDateChooser fechaVigenciaDateChooser;
    private javax.swing.JButton guardarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField paridadTextField;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables

    private final DocumentListener docListener = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            if (changeOk) {
                modify();
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (changeOk) {
                modify();
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            if (changeOk) {
                modify();
            }
        }

    };

    private final PropertyChangeListener compListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("date".equals(evt.getPropertyName())) {
                if (changeOk) {
                    modify();
                }
            }
        }

    };
}
