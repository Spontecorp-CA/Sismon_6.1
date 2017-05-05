package com.sismon.vista.mejorvision;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.model.Escenario;
import com.sismon.vista.Contexto;
import com.sismon.vista.controller.GestionMejorVisionController;
import com.sismon.vista.utilities.DocFilter;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class GestionMejorVisionIF extends javax.swing.JInternalFrame {

    private static GestionMejorVisionIF instance = null;

    private final EscenarioManager escenarioManager;

    private Escenario escenarioSelected;
    private Escenario escenarioEntrada;
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form CrearEscenarioIF
     */
    private GestionMejorVisionIF() {
        initComponents();
        this.escenarioManager = new EscenarioManager();
        init();
    }

    public static GestionMejorVisionIF getInstance() {
        if (instance == null) {
            instance = new GestionMejorVisionIF();
        }
        return instance;
    }

    private void init() {
        this.setFrameIcon(icon);
        progressBar.setVisible(false);
        fillDataTable();
        fillEscenariosComboBox();
    }

    private void configureListeners() {
        nombreTextField.getDocument().addDocumentListener(docListener);
        premisasDocTextField.getDocument().addDocumentListener(docListener);
    }

    private void removeListeners() {
        nombreTextField.getDocument().removeDocumentListener(docListener);
        premisasDocTextField.getDocument().removeDocumentListener(docListener);
    }

    private void clearForm() {
        nombreTextField.setText(null);
        fechaDateChooser.setDate(null);
        fechaCierreDateChooser.setDate(null);
        descripcionTextField.setText(null);
        guardarButton.setEnabled(false);
        eliminarButton.setEnabled(false);
        archivoButton.setEnabled(false);
        premisasButton.setEnabled(false);
        escenarioRadioButton.setSelected(true);
        dataTable.clearSelection();
        premisasDocTextField.setText(null);
        escenarioSelected = null;
        clearComboBox();
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
    }
    
    private void clearComboBox() {
        escenariosComboBox.removeAllItems();
        escenariosComboBox.addItem("... seleccione");
    }

    private void fillDataTable() {
        String[] titles = {"Id", "Nombre", "Fecha Creación", "Fecha Cierre","Descripción"};
        List<Escenario> escenarios = escenarioManager.findAllMV(true);
        if (escenarios != null && !escenarios.isEmpty()) {
            Object[][] data = new Object[escenarios.size()][titles.length];
            int i = 0;
            for (Escenario escenario : escenarios) {
                data[i][0] = escenario.getId();
                data[i][1] = escenario.getNombre();
                data[i][2] = dateFormat.format(escenario.getFecha());
                data[i][3] = dateFormat.format(escenario.getFechaCierre());
                data[i][4] = escenario.getComentario();
                i++;
            }
            DefaultTableModel model = new DefaultTableModel(data, titles);
            dataTable.setModel(model);
            dataTable.removeColumn(dataTable.getColumnModel().getColumn(0));
        } else {
            dataTable.setModel(new DefaultTableModel());
        }
        
        // esto es para variar el ancho de cada columna
        int columns = dataTable.getModel().getColumnCount();
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = {makeEmptyString("X", 40),
            makeEmptyString("X", 15),
            makeEmptyString("X", 15),
            makeEmptyString("X", 100)};
        TableCellRenderer headerRenderer = dataTable.getTableHeader().getDefaultRenderer();
        for (int i = 0; i < columns - 1; i++) {
            column = dataTable.getColumnModel().getColumn(i);
            comp = headerRenderer.getTableCellRendererComponent(
                    null, column.getHeaderValue(),
                    false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = dataTable.getDefaultRenderer(model.getColumnClass(i)).
                    getTableCellRendererComponent(
                            dataTable, longValues[i],
                            false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }

    private String makeEmptyString(String charToFill, int length) {
        return Stream.generate(() -> charToFill).limit(length).collect(Collectors.joining());
    }

    private void modify() {
        if (fechaDateChooser.getDate() != null 
                && fechaCierreDateChooser.getDate() != null) {
            guardarButton.setEnabled(true);
            eliminarButton.setEnabled(true);
            archivoButton.setEnabled(true);
            //premisasButton.setEnabled(true);
        }
    }

    private void fillEscenariosComboBox() {
        List<Escenario> escenarios = null;
        if (escenarioRadioButton.isSelected()) {
            escenarios = escenarioManager.findAllMV(false);
        } else if (mvRadioButton.isSelected()) {
            escenarios = escenarioManager.findAllMV(true);
        }
        clearComboBox();
        escenarios.stream().forEach(esc -> {
            escenariosComboBox.addItem(esc);
        });
    }

    private DocumentListener docListener = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            modify();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            modify();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            modify();
        }

    };
    
    private void getEscenarioEntrada() {
        if (escenariosComboBox.getSelectedItem() instanceof Escenario) {
            escenarioEntrada = (Escenario) escenariosComboBox.getSelectedItem();
        } else {
            Contexto.showMessage("Debe seleccionar un escenario a copiar", Constantes.MENSAJE_ERROR);
            return;
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

        escenariosButtonGroup = new javax.swing.ButtonGroup();
        fileChooser = new javax.swing.JFileChooser();
        backPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        nombreTextField = new javax.swing.JTextField();
        fechaDateChooser = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        escenarioRadioButton = new javax.swing.JRadioButton();
        mvRadioButton = new javax.swing.JRadioButton();
        escenariosComboBox = new javax.swing.JComboBox();
        descripcionTextField = new javax.swing.JTextField();
        premisasDocTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        fechaCierreDateChooser = new com.toedter.calendar.JDateChooser();
        toolBar = new javax.swing.JToolBar();
        guardarButton = new javax.swing.JButton();
        limpiarButton = new javax.swing.JButton();
        archivoButton = new javax.swing.JButton();
        premisasButton = new javax.swing.JButton();
        eliminarButton = new javax.swing.JButton();
        toolBarPanel = new javax.swing.JPanel();
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

        jLabel1.setText("Nombre:");

        jLabel2.setText("Fecha:");

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Escenario", "Fecha Creación", "Fecha Cierre", "Descripción"
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

        jLabel3.setText("Descripción:");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Generar desde"));

        escenariosButtonGroup.add(escenarioRadioButton);
        escenarioRadioButton.setSelected(true);
        escenarioRadioButton.setText("Otro Escenario");
        escenarioRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        escenarioRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escenarioRadioButtonActionPerformed(evt);
            }
        });

        escenariosButtonGroup.add(mvRadioButton);
        mvRadioButton.setText("Mejor Visión");
        mvRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        mvRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mvRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(escenarioRadioButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(mvRadioButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addComponent(escenariosComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(escenarioRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mvRadioButton)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(escenariosComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setText("Archivo premisas:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Lista de Mejor Visión registrados");

        jLabel6.setText("FechaCierre:");

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(backPanelLayout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(descripcionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel5)
                                .addGroup(backPanelLayout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(premisasDocTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 433, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(backPanelLayout.createSequentialGroup()
                            .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(backPanelLayout.createSequentialGroup()
                                    .addComponent(fechaDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fechaCierreDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(nombreTextField))
                            .addGap(145, 145, 145))))
                .addGap(284, 284, 284))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(fechaDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fechaCierreDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nombreTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(descripcionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(premisasDocTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                .addContainerGap())
        );

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        guardarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconguardar26.png"))); // NOI18N
        guardarButton.setText("Guardar");
        guardarButton.setEnabled(false);
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
        limpiarButton.setFocusable(false);
        limpiarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        limpiarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        limpiarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limpiarButtonActionPerformed(evt);
            }
        });
        toolBar.add(limpiarButton);

        archivoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconbuscar26.png"))); // NOI18N
        archivoButton.setText("Buscar");
        archivoButton.setEnabled(false);
        archivoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        archivoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        archivoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                archivoButtonActionPerformed(evt);
            }
        });
        toolBar.add(archivoButton);

        premisasButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconloadarchivo26.png"))); // NOI18N
        premisasButton.setText("Cargar");
        premisasButton.setEnabled(false);
        premisasButton.setFocusable(false);
        premisasButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        premisasButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        premisasButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                premisasButtonActionPerformed(evt);
            }
        });
        toolBar.add(premisasButton);

        eliminarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icondelete26.png"))); // NOI18N
        eliminarButton.setText("Eliminar");
        eliminarButton.setEnabled(false);
        eliminarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        eliminarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        eliminarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarButtonActionPerformed(evt);
            }
        });
        toolBar.add(eliminarButton);

        javax.swing.GroupLayout toolBarPanelLayout = new javax.swing.GroupLayout(toolBarPanel);
        toolBarPanel.setLayout(toolBarPanelLayout);
        toolBarPanelLayout.setHorizontalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolBarPanelLayout.createSequentialGroup()
                .addContainerGap(603, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        toolBarPanelLayout.setVerticalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolBarPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        toolBar.add(toolBarPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        Contexto.setActiveFrame(instance);
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        configureListeners();
        init();
    }//GEN-LAST:event_onActivated

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        if (!nombreTextField.getText().isEmpty() && fechaDateChooser.getDate() != null) {
            if (escenarioSelected == null) {
                try {
                    if (escenarioManager.find(nombreTextField.getText().toUpperCase()) == null) {
                        guardarButton.setEnabled(false);
                        progressBar.setVisible(true);
                        progressBar.setIndeterminate(false);
                        progressBar.setStringPainted(true);
                        progressBar.setValue(0);
                        String archivo = premisasDocTextField.getText();

                        GestionMejorVisionController generador;
                        if (escenarioRadioButton.isSelected()) {
                            getEscenarioEntrada();
                            generador = new GestionMejorVisionController(
                                    nombreTextField.getText().toUpperCase(),
                                    fechaDateChooser.getDate(),
                                    descripcionTextField.getText(),
                                    progressBar,
                                    archivo,
                                    Constantes.ESCENARIO_MEJOR_VISION,
                                    escenarioEntrada);
                        } else {
                            getEscenarioEntrada();
                            generador = new GestionMejorVisionController(
                                    nombreTextField.getText().toUpperCase(),
                                    fechaDateChooser.getDate(),
                                    descripcionTextField.getText(),
                                    progressBar,
                                    archivo,
                                    Constantes.ESCENARIO_MEJOR_VISION,
                                    escenarioEntrada);
                        }
                        generador.execute();
                        if (generador.get()) {
                            clearForm();
                            fillDataTable();
                            progressBar.setVisible(false);
                            progressBar.setIndeterminate(true);
                            progressBar.setStringPainted(false);
                        }
                    } else {
                        Contexto.showMessage("Mejor Visión existente, debe usar otro nombre", Constantes.MENSAJE_ERROR);
                        nombreTextField.grabFocus();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    sismonlog.logger.log(Level.SEVERE, "Error de ejecución", ex);
                }
            } else {
                escenarioSelected.setNombre(nombreTextField.getText().toUpperCase());
                escenarioSelected.setFecha(fechaDateChooser.getDate());
                escenarioSelected.setFechaCierre(fechaCierreDateChooser.getDate());
                escenarioSelected.setComentario(descripcionTextField.getText());
                escenarioSelected.setStatus(Constantes.ESCENARIO_PRUEBA);
                if (!premisasDocTextField.getText().isEmpty()) {
                    escenarioSelected.setArchivo(premisasDocTextField.getText());
                } else {
                    escenarioSelected.setArchivo(null);
                }
                escenarioManager.edit(escenarioSelected);

                Contexto.showMessage("Mejor Visión actualizado con éxito", Constantes.MENSAJE_INFO);
                fillDataTable();
                clearForm();
            }
        } else {
            Contexto.showMessage("Debe completar todos los campos", Constantes.MENSAJE_ERROR);
        }
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void dataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataTableMouseClicked
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        int id = (int) dataTable.getModel().getValueAt(dataTable.getSelectedRow(), 0);
        escenarioSelected = escenarioManager.find(id);
        fechaDateChooser.setDate(escenarioSelected.getFecha());
        fechaCierreDateChooser.setDate(escenarioSelected.getFechaCierre());
        nombreTextField.setText(escenarioSelected.getNombre());
        descripcionTextField.setText(escenarioSelected.getComentario());
        premisasDocTextField.setText(escenarioSelected.getArchivo());
        
        eliminarButton.setEnabled(true);
    }//GEN-LAST:event_dataTableMouseClicked

    private void eliminarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarButtonActionPerformed
        int id = (int) dataTable.getModel().getValueAt(dataTable.getSelectedRow(), 0);
        escenarioSelected = escenarioManager.find(id);
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Desea eliminar la Mejor Visión "
                + escenarioSelected.getNombre() + "?",
                "Advertencia", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                GestionMejorVisionController gestor = new GestionMejorVisionController(escenarioSelected, progressBar);
                gestor.execute();
                if (gestor.get()) {
                    clearForm();
                    fillDataTable();
                    escenarioSelected = null;
                }
            } catch (InterruptedException | ExecutionException ex) {
                sismonlog.logger.log(Level.SEVERE, "No pudo eliminar la Mejor Visión", ex);
                Contexto.showMessage("Error. No pudo eliminar la Mejor Visión " + escenarioSelected.getNombre(),
                        Constantes.MENSAJE_ERROR);
            }
        }
    }//GEN-LAST:event_eliminarButtonActionPerformed

    private void onDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onDeactivated
        clearForm();
        removeListeners();
        Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
    }//GEN-LAST:event_onDeactivated

    private void archivoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_archivoButtonActionPerformed
        fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new DocFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        int answer = fileChooser.showOpenDialog(this);
        if (answer == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            premisasDocTextField.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_archivoButtonActionPerformed
    private void cargaArchivo (){
        try {
            String ruta = premisasDocTextField.getText();
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(ruta));
            }
        } catch (IOException ex) {
            sismonlog.logger.log(Level.SEVERE, null, ex);
        }
    }
    private void premisasButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_premisasButtonActionPerformed
        try {
            String ruta = premisasDocTextField.getText();
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(ruta));
            }
        } catch (IOException ex) {
            sismonlog.logger.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_premisasButtonActionPerformed

    private void escenarioRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escenarioRadioButtonActionPerformed
        fillEscenariosComboBox();
    }//GEN-LAST:event_escenarioRadioButtonActionPerformed

    private void mvRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mvRadioButtonActionPerformed
        fillEscenariosComboBox();
    }//GEN-LAST:event_mvRadioButtonActionPerformed

    private void limpiarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limpiarButtonActionPerformed
        clearForm();
    }//GEN-LAST:event_limpiarButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton archivoButton;
    private javax.swing.JPanel backPanel;
    private javax.swing.JTable dataTable;
    private javax.swing.JTextField descripcionTextField;
    private javax.swing.JButton eliminarButton;
    private javax.swing.JRadioButton escenarioRadioButton;
    private javax.swing.ButtonGroup escenariosButtonGroup;
    private javax.swing.JComboBox escenariosComboBox;
    private com.toedter.calendar.JDateChooser fechaCierreDateChooser;
    private com.toedter.calendar.JDateChooser fechaDateChooser;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JButton guardarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton limpiarButton;
    private javax.swing.JRadioButton mvRadioButton;
    private javax.swing.JTextField nombreTextField;
    private javax.swing.JButton premisasButton;
    private javax.swing.JTextField premisasDocTextField;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolBarPanel;
    // End of variables declaration//GEN-END:variables

}
