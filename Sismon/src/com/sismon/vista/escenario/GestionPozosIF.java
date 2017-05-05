package com.sismon.vista.escenario;

import com.sismon.controller.Constantes;
import com.sismon.exceptions.SismonException;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.jpamanager.MacollaManager;
import com.sismon.jpamanager.MacollaSecuenciaManager;
import com.sismon.jpamanager.PozoManager;
import com.sismon.jpamanager.RampeoManager;
import com.sismon.model.Escenario;
import com.sismon.model.Macolla;
import com.sismon.model.Pozo;
import com.sismon.model.Rampeo;
import com.sismon.vista.Contexto;
import com.sismon.vista.controller.EditablePozoTableModel;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Component;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class GestionPozosIF extends javax.swing.JInternalFrame {

    private static GestionPozosIF instance = null;
    private Escenario escenarioSelected = null;
    private Macolla macollaSelected = null;
    private Pozo pozoSelected = null;
    private Map<Integer, Pozo> pozosMap;

    private final EscenarioManager escenarioManager;
    private final MacollaManager macollaManager;
    private final MacollaSecuenciaManager macollaSecuenciaManager;
    private final PozoManager pozoManager;
    private final RampeoManager rampeoManager;

    private static final SismonLog sismonlog = SismonLog.getInstance();
    private static final NumberFormat nFormat = new DecimalFormat("###,###,###,##0.000");
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form GestionPozosIF
     */
    private GestionPozosIF() {
        initComponents();
        escenarioManager = new EscenarioManager();
        macollaManager = new MacollaManager();
        pozoManager = new PozoManager();
        macollaSecuenciaManager = new MacollaSecuenciaManager();
        rampeoManager = new RampeoManager();

        init();
    }

    public static GestionPozosIF getInstance() {
        if (instance == null) {
            instance = new GestionPozosIF();
        }
        return instance;
    }

    private void init() {
        setFrameIcon(icon);
    }

    private void fillEscenariosComboBox() {
        escenarioComboBox.removeAllItems();
        List<Escenario> escenarios = escenarioManager.findAllMV(false);
        escenarioComboBox.removeAllItems();
        escenarioComboBox.addItem("... seleccione Escenario");
        if (!escenarios.isEmpty()) {
            escenarios.stream().forEach(esc -> {
                escenarioComboBox.addItem(esc);
            });
        }
    }

    private void clearComboBOx(JComboBox combo, String tipo) {
        combo.removeAllItems();
        combo.addItem("... seleccione " + tipo);
        combo.setEnabled(false);
    }

    private void fillMacollasComboBox() {
        if (escenarioSelected != null) {
            try {
                List<Macolla> macollas = macollaSecuenciaManager.findAll(escenarioSelected);
                clearComboBOx(macollasComboBox, "Macolla");
                macollas.stream().forEach(mac -> {
                    macollasComboBox.addItem(mac);
                });
            } catch (SismonException ex) {
                Contexto.showMessage(ex.getMessage(), Constantes.MENSAJE_ERROR);
            }
        }
    }

    private void fillPozosTable() {
        pozosMap = new HashMap<>();
        NumberFormat porcFormat = NumberFormat.getPercentInstance(Locale.FRENCH);
        porcFormat.setMinimumFractionDigits(2);
        try {
            List<Pozo> pozos = pozoManager.findAll(macollaSelected, escenarioSelected);
            if (!pozos.isEmpty()) {
                String[] titles = {"Localización", "Número", "Nombre", "Plan", "Clase Pozo",
                    "Yacimiento", "Bloque", "PI", "Decl Anual %", "Plateau Prod (días)",
                    "RGP", "Incr Anual RGP %", "Plateau RGP", "AyS", "Incr Anual AyS %",
                    "Plateau AyS", "Exp Hip", "Tasa Abandono", "Reserva Max",
                    "Grado API XP", "Grado API Dilnt", "Grado API Mezcla",
                    "Rampeo 1 dias", "Rampeo 1 rpm", "Rampeo 2 dias", "Rampeo 2 rpm",
                    "Rampeo 3 dias", "Rampeo 3 rpm", "Rampeo 4 dias", "Rampeo 4 rpm",
                    "Rampeo 5 dias", "Rampeo 5 rpm", "Rampeo 6 dias", "Rampeo 6 rpm"};
                Object[][] data = new Object[pozos.size()][titles.length];
                int i = 0;
                for (Pozo p : pozos) {
                    data[i][0] = p.getUbicacion() != null ? p.getUbicacion() : "";
                    data[i][1] = p.getNumero() != null ? p.getNumero() : "";
                    data[i][2] = p.getNombre() != null ? p.getNombre() : "";
                    data[i][3] = p.getPlan() != null ? p.getPlan() : "";
                    data[i][4] = p.getClasePozo() != null ? p.getClasePozo() : "";
                    data[i][5] = p.getYacimiento() != null ? p.getYacimiento() : "";
                    data[i][6] = p.getBloque() != null ? p.getBloque() : "";
                    data[i][7] = p.getPi() != null ? p.getPi() : "";
                    data[i][8] = p.getDeclinacion() != null ? porcFormat.format(p.getDeclinacion()) : "";
                    data[i][9] = p.getInicioDecl() != null ? p.getInicioDecl() : "";
                    data[i][10] = p.getRgp() != null ? p.getRgp() : "";
                    data[i][11] = p.getIncremAnualRgp() != null ? porcFormat.format(p.getIncremAnualRgp()) : "";
                    data[i][12] = p.getInicioDeclRgp() != null ? p.getInicioDeclRgp() : "";
                    data[i][13] = p.getAys() != null ? p.getAys() : "";
                    data[i][14] = p.getIncremAnualAys() != null ? porcFormat.format(p.getIncremAnualAys()) : "";
                    data[i][15] = p.getInicioDeclAys() != null ? p.getInicioDeclAys() : "";
                    data[i][16] = p.getExpHiperb() != null ? p.getExpHiperb() : "";
                    data[i][17] = p.getTasaAbandono() != null ? p.getTasaAbandono() : "";
                    data[i][18] = p.getReservaMax() != null ? nFormat.format(p.getReservaMax()) : "";
                    data[i][19] = p.getGradoApiXp() != null ? p.getGradoApiXp() : "";
                    data[i][20] = p.getGradoApiDiluente() != null ? p.getGradoApiDiluente() : "";
                    data[i][21] = p.getGradoApiMezcla() != null ? p.getGradoApiMezcla() : "";

                    List<Rampeo> rampeos = rampeoManager.findAll(p, escenarioSelected);
                    if (!rampeos.isEmpty()) {
                        int j = 0;
                        for (Rampeo r : rampeos) {
                            data[i][22 + j] = r.getDias();
                            data[i][23 + j] = r.getRpm();
                            j += 2;
                        }
                        p.setRampeoCollection(rampeos);
                    }
                    pozosMap.put(i, p);
                    i++;
                }
                EditablePozoTableModel model = new EditablePozoTableModel(data, titles);
                pozosTable.setModel(model);

                pozosTable.getModel().addTableModelListener(new TableModelListener() {

                    @Override
                    public void tableChanged(TableModelEvent e) {
                        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
                        Integer row = e.getFirstRow();
                        int column = e.getColumn();
                        TableModel model = (TableModel) e.getSource();
                        Object data = model.getValueAt(row, column);
                        Optional<Rampeo> ramp;
                        Rampeo rampeo;
                        Pozo pozo = pozosMap.get(row);
                        List<Rampeo> rampeos = (List<Rampeo>) pozo.getRampeoCollection();
                        List<Rampeo> newRampeos = new ArrayList();
                        String dato;
                        switch (column) {
                            case 2:
                                pozo.setNombre((String) data);
                                break;
                            case 3:
                                pozo.setPlan((String) data);
                                break;
                            case 4:
                                pozo.setClasePozo((String) data);
                                break;
                            case 5:
                                pozo.setYacimiento((String) data);
                                break;
                            case 6:
                                pozo.setBloque((String) data);
                                break;
                            case 7:
                                pozo.setPi((Double) data);
                                break;
                            case 8:
                                dato = (String) data;
                                dato = dato.substring(0, dato.length() - 1).replace(',', '.');
                                pozo.setDeclinacion(Double.parseDouble(dato) / 100);
                                break;
                            case 9:
                                pozo.setInicioDecl((Integer) data);
                                break;
                            case 10:
                                pozo.setRgp((Double) data);
                                break;
                            case 11:
                                dato = (String) data;
                                dato = dato.substring(0, dato.length() - 1).replace(',', '.');
                                pozo.setIncremAnualRgp(Double.parseDouble(dato) / 100);
                                break;
                            case 12:
                                pozo.setInicioDeclRgp((Integer) data);
                                break;
                            case 13:
                                pozo.setAys((Double) data);
                                break;
                            case 14:
                                dato = (String) data;
                                dato = dato.substring(0, dato.length() - 1).replace(',', '.');
                                pozo.setIncremAnualAys(Double.parseDouble(dato) / 100);
                                break;
                            case 15:
                                pozo.setInicioDeclAys((Integer) data);
                                break;
                            case 16:
                                pozo.setExpHiperb((Double) data);
                                break;
                            case 17:
                                pozo.setTasaAbandono((Double) data);
                                break;
                            case 18:
                                Double value = 0.0;
                                try {
                                    Number num = nFormat.parse((String) data);
                                    value = num.doubleValue();
                                } catch (ParseException ex) {
                                    sismonlog.logger.log(Level.SEVERE, null, ex);
                                }
                                pozo.setReservaMax(value);
                                break;
                            case 19:
                                pozo.setGradoApiXp((Double) data);
                                break;
                            case 20:
                                pozo.setGradoApiDiluente((Double) data);
                                break;
                            case 21:
                                pozo.setGradoApiMezcla((Double) data);
                                break;
                            case 22:
                                ramp = rampeos.stream()
                                        .filter(ram -> ram.getNumero() == 1)
                                        .findFirst();
                                rampeo = ramp.get();
                                rampeo.setDias((Double) data);
                                newRampeos.add(rampeo);
                                break;
                            case 23:
                                ramp = rampeos.stream()
                                        .filter(ram -> ram.getNumero() == 1)
                                        .findFirst();
                                rampeo = ramp.get();
                                rampeo.setRpm((Double) data);
                                newRampeos.add(rampeo);
                                break;
                            case 24:
                                ramp = rampeos.stream()
                                        .filter(ram -> ram.getNumero() == 2)
                                        .findFirst();
                                rampeo = ramp.get();
                                rampeo.setDias((Double) data);
                                newRampeos.add(rampeo);
                                break;
                            case 25:
                                ramp = rampeos.stream()
                                        .filter(ram -> ram.getNumero() == 2)
                                        .findFirst();
                                rampeo = ramp.get();
                                rampeo.setRpm((Double) data);
                                newRampeos.add(rampeo);
                                break;
                            case 26:
                                ramp = rampeos.stream()
                                        .filter(ram -> ram.getNumero() == 3)
                                        .findFirst();
                                rampeo = ramp.get();
                                rampeo.setDias((Double) data);
                                newRampeos.add(rampeo);
                                break;
                            case 27:
                                ramp = rampeos.stream()
                                        .filter(ram -> ram.getNumero() == 3)
                                        .findFirst();
                                rampeo = ramp.get();
                                rampeo.setRpm((Double) data);
                                newRampeos.add(rampeo);
                                break;
                            case 28:
                                ramp = rampeos.stream()
                                        .filter(ram -> ram.getNumero() == 4)
                                        .findFirst();
                                rampeo = ramp.get();
                                rampeo.setDias((Double) data);
                                newRampeos.add(rampeo);
                                break;
                            case 29:
                                ramp = rampeos.stream()
                                        .filter(ram -> ram.getNumero() == 4)
                                        .findFirst();
                                rampeo = ramp.get();
                                rampeo.setRpm((Double) data);
                                newRampeos.add(rampeo);
                                break;
                            case 30:
                                ramp = rampeos.stream()
                                        .filter(ram -> ram.getNumero() == 5)
                                        .findFirst();
                                rampeo = ramp.get();
                                rampeo.setDias((Double) data);
                                newRampeos.add(rampeo);
                                break;
                            case 31:
                                ramp = rampeos.stream()
                                        .filter(ram -> ram.getNumero() == 5)
                                        .findFirst();
                                rampeo = ramp.get();
                                rampeo.setRpm((Double) data);
                                newRampeos.add(rampeo);
                                break;
                            case 32:
                                ramp = rampeos.stream()
                                        .filter(ram -> ram.getNumero() == 6)
                                        .findFirst();
                                rampeo = ramp.get();
                                rampeo.setDias((Double) data);
                                newRampeos.add(rampeo);
                                break;
                            case 33:
                                ramp = rampeos.stream()
                                        .filter(ram -> ram.getNumero() == 6)
                                        .findFirst();
                                rampeo = ramp.get();
                                rampeo.setRpm((Double) data);
                                newRampeos.add(rampeo);
                                break;
                        }
                        pozosMap.put(row, pozo);
                        guardarButton.setEnabled(true);
                    }
                });

                setupPozosTable();
            }
        } catch (SismonException ex) {
            Contexto.showMessage(ex.getMessage(), Constantes.MENSAJE_ERROR);
        }
    }

    private void setupPozosTable() {
        int columns = pozosTable.getModel().getColumnCount();

        EditablePozoTableModel model = (EditablePozoTableModel) pozosTable.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.getLongValues();
        TableCellRenderer headerRenderer = pozosTable.getTableHeader().getDefaultRenderer();
        for (int i = 0; i < columns; i++) {
            column = pozosTable.getColumnModel().getColumn(i);
            comp = headerRenderer.getTableCellRendererComponent(
                    null, column.getHeaderValue(),
                    false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = pozosTable.getDefaultRenderer(model.getColumnClass(i)).
                    getTableCellRendererComponent(
                            pozosTable, longValues[i],
                            false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }

        TableColumn clasePozoColumn = pozosTable.getColumnModel().getColumn(4);
        JComboBox combo = new JComboBox();
        combo.addItem("Productor");
        combo.addItem("Inyector");
        combo.addItem("Estratigráfico");
        combo.addItem("Observador");
        clasePozoColumn.setCellEditor(new DefaultCellEditor(combo));
    }

    private void guardarCambios() {
        List<Pozo> pozos = new ArrayList<>();
        for (Map.Entry<Integer, Pozo> dataMap : pozosMap.entrySet()) {
            pozos.add(dataMap.getValue());
            List<Rampeo> rampeos = (List<Rampeo>) dataMap.getValue().getRampeoCollection();
            rampeoManager.batchEdit(rampeos);
        }
        pozoManager.batchEdit(pozos);
        guardarButton.setEnabled(false);
        Contexto.showMessage("Pozos actualizados con éxito", Constantes.MENSAJE_INFO);
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
        jPanel1 = new javax.swing.JPanel();
        escenarioComboBox = new javax.swing.JComboBox();
        macollasComboBox = new javax.swing.JComboBox();
        backPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pozosTable = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Editar Pozos");
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

        guardarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconguardar26.png"))); // NOI18N
        guardarButton.setText("Guardar");
        guardarButton.setToolTipText("Guardar");
        guardarButton.setEnabled(false);
        guardarButton.setFocusable(false);
        guardarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        guardarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        guardarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(guardarButton);

        escenarioComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escenarioComboBoxActionPerformed(evt);
            }
        });

        macollasComboBox.setEnabled(false);
        macollasComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                macollasComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(macollasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(475, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {escenarioComboBox, macollasComboBox});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(macollasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jToolBar1.add(jPanel1);

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        pozosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8", "Title 9", "Title 10", "Title 11", "Title 12", "Title 13", "Title 14", "Title 15", "Title 16", "Title 17", "Title 18", "Title 19", "Title 20", "Title 21", "Title 22", "Title 23", "Title 24", "Title 25", "Title 26", "Title 27", "Title 28", "Title 29", "Title 30", "Title 31", "Title 32", "Title 33", "Title 34"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        pozosTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(pozosTable);

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        fillEscenariosComboBox();
        Contexto.setActiveFrame(instance);
    }//GEN-LAST:event_onActivated

    private void onDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onDeactivated
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
    }//GEN-LAST:event_onDeactivated

    private void escenarioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escenarioComboBoxActionPerformed
        if (escenarioComboBox.getSelectedItem() instanceof Escenario) {
            escenarioSelected = (Escenario) escenarioComboBox.getSelectedItem();
            fillMacollasComboBox();
            macollasComboBox.setEnabled(true);
        } else {
            clearComboBOx(macollasComboBox, "Macolla");
        }
    }//GEN-LAST:event_escenarioComboBoxActionPerformed

    private void macollasComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_macollasComboBoxActionPerformed
        if (macollasComboBox.getSelectedItem() instanceof Macolla) {
            macollaSelected = (Macolla) macollasComboBox.getSelectedItem();
            fillPozosTable();
        } else {
            pozosTable.setModel(new DefaultTableModel());
        }
    }//GEN-LAST:event_macollasComboBoxActionPerformed

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        guardarCambios();
    }//GEN-LAST:event_guardarButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JComboBox escenarioComboBox;
    private javax.swing.JButton guardarButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JComboBox macollasComboBox;
    private javax.swing.JTable pozosTable;
    // End of variables declaration//GEN-END:variables
}
