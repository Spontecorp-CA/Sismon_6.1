package com.sismon.vista.reporte;

import com.sismon.controller.Constantes;
import com.sismon.exceptions.SismonException;
import com.sismon.model.Campo;
import com.sismon.model.Escenario;
import com.sismon.model.Macolla;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.model.Taladro;
import com.sismon.vista.Contexto;
import com.sismon.vista.controller.ReportesController;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RepInversionIF extends javax.swing.JInternalFrame {

    private static RepInversionIF instance = null;
    private Escenario escenarioSelected;
    private Campo campoSelected;
    private Macolla macollaSelected;
    private String bloqueSelected;
    private String yacimientoSelected;
    private String planSelected;
    private Taladro taladroSelected;
    private Pozo pozoSelected;
    private Date inicialDate;
    private Date finalDate;
    private int tipoReporte;

    private final DefaultListModel taladroModel;
    private final DefaultListModel macollaModel;
    private final DefaultListModel pozoModel;
    private final ListSelectionModel macollasSelectionModel;
    private final ListSelectionModel taladrosSelectionModel;
    private final ListSelectionModel pozosSelectionModel;
    private final Set<Integer> macollasSelectedSet;
    private final Set<Integer> taladrosSelectedSet;
    private final Set<Integer> pozosSelectedSet;

    private final ReportesController controller;
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final String VALOR_INICIAL = "... seleccione";
    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form RepInversionIF
     */
    private RepInversionIF() {
        initComponents();
        setFrameIcon(icon);
        
        controller = new ReportesController();
        this.taladroModel = new DefaultListModel();
        this.macollaModel = new DefaultListModel();
        this.pozoModel = new DefaultListModel();

        this.macollasSelectionModel = macollasList.getSelectionModel();
        this.taladrosSelectionModel = taladrosList.getSelectionModel();
        this.pozosSelectionModel = pozosList.getSelectionModel();
        macollasSelectionModel.addListSelectionListener(new MacollasListSelectionHandler());
        taladrosSelectionModel.addListSelectionListener(new TaladrosListSelectionHandler());
        pozosSelectionModel.addListSelectionListener(new PozosListSelectionHandler());
        this.macollasSelectedSet = new HashSet<>();
        this.taladrosSelectedSet = new HashSet<>();
        this.pozosSelectedSet = new HashSet<>();
    }

    public static RepInversionIF getInstance() {
        if (instance == null) {
            instance = new RepInversionIF();
        }
        return instance;
    }

    private void fillEscenarioComboBox() {
        resetComboBox(escenarioComboBox);
        controller.getEscenarioList().stream().forEach((escenario) -> {
            escenarioComboBox.addItem(escenario);
        });
    }
    
    public void setTipoReporte(int tipoReporte){
        this.tipoReporte = tipoReporte;
        
        if(tipoReporte == Constantes.REPORTE_PERFORACION){
            conteosPanel.setVisible(true);
        } else {
            conteosPanel.setVisible(false);
        }
    }

    private void fillBaseComboBoxes() {
        resetComboBox(bloqueComboBox);
        resetComboBox(yacimientoComboBox);
        resetComboBox(planComboBox);

        Set<String> bloques = new HashSet<>();
        Set<String> yacimientos = new HashSet<>();
        Set<String> planes = new HashSet<>();
        List<Pozo> pozos = controller.getPozoList(escenarioSelected);
        pozos.stream().forEach(pz -> {
            bloques.add(pz.getBloque());
            yacimientos.add(pz.getYacimiento());
            planes.add(pz.getPlan());
        });

        bloques.stream().forEach(bloqueComboBox::addItem);
        yacimientos.stream().forEach(yacimientoComboBox::addItem);
        planes.stream().forEach(planComboBox::addItem);
    }

    private void fillCampoComboBox() {
        resetComboBox(campoComboBox);
        controller.getCampoList().stream().forEach(campoComboBox::addItem);
    }

    private void resetComboBox(JComboBox comboBox) {
        comboBox.removeAllItems();
        comboBox.addItem(VALOR_INICIAL);
    }

    private void resetListas() {
        taladroModel.removeAllElements();
        macollaModel.removeAllElements();
        pozoModel.removeAllElements();

        taladrosList.setModel(taladroModel);
        macollasList.setModel(macollaModel);
        pozosList.setModel(pozoModel);
    }

    private void enableComboBoxes(boolean interruptor) {
        bloqueComboBox.setEnabled(interruptor);
        yacimientoComboBox.setEnabled(interruptor);
        planComboBox.setEnabled(interruptor);
        campoComboBox.setEnabled(interruptor);
    }

    private void fillMacollaList() {
        DefaultListModel model = new DefaultListModel();
        List<Macolla> macollas = controller.getMacollaList(campoSelected);
        macollas.stream().forEach(model::addElement);
        macollasList.setModel(model);
    }

    private void fillTaladroList() {
        taladroModel.removeAllElements();
        taladrosList.setModel(taladroModel);

        List<Taladro> taladros = controller.getTaladroList(escenarioSelected);
        taladros.stream().forEach(taladroModel::addElement);
        taladrosList.setModel(taladroModel);
    }

    private void fillPozoList() {
        try {
            pozoModel.removeAllElements();
            List<Pozo> pozos = controller.getPozoList(escenarioSelected, macollaSelected);
            pozos.stream().forEach(pozoModel::addElement);
            pozosList.setModel(pozoModel);
        } catch (SismonException e) {
        }
    }

    private void generarReporte() {
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);

        StringBuilder query = new StringBuilder();
        List<String> paramNameList = new ArrayList<>();
        List<Object> paramList = new ArrayList<>();
        List<Perforacion> perforacionList;
        int reporteFiltro;

        String[] paramName;
        Object[] param;

        if (tipoReporte == Constantes.REPORTE_INVERSION
                || tipoReporte == Constantes.REPORTE_PERFORACION
                || tipoReporte == Constantes.REPORTE_PERFORACION_TOTAL) {
            query.append("SELECT p FROM Perforacion p WHERE p.escenarioId = :");
        }
        if (escenarioSelected != null) {
            query.append("escenario");
            paramNameList.add("escenario");
            paramList.add(escenarioSelected);
            String paraName;

            reporteFiltro = Constantes.REPORTE_SIN_FILTRO;
            String reporteTitle = "";
            switch(tipoReporte){
                case Constantes.REPORTE_PERFORACION_TOTAL:
                    reporteTitle = "Perforación Total";
                    break;
                case Constantes.REPORTE_INVERSION:
                    reporteTitle = "Inversión en Perforación";
                    break;
                case Constantes.REPORTE_PERFORACION:
                    reporteTitle = "Perforación (Conteo)";
                    break;
            }
            // selección de taladro
            if (taladroSelected != null && taladrosSelectedSet.size() == 1) {
                query.append(" AND p.taladroId = :taladro");
                paramNameList.add("taladro");
                paramList.add(taladroSelected);
            }

            if (!taladrosSelectedSet.isEmpty() && taladrosSelectedSet.size() > 1) {
                boolean primeravez = true;
                for (Integer indice : taladrosSelectedSet) {
                    Taladro tal = (Taladro) taladrosList.getModel().getElementAt(indice);
                    if (primeravez) {
                        query.append(" AND (p.taladroId = :taladro").append(indice);
                        paraName = "taladro" + String.valueOf(indice);
                        paramNameList.add(paraName);
                        paramList.add(tal);
                        primeravez = false;
                    } else {
                        query.append(" OR p.taladroId = :taladro").append(indice);
                        paraName = "taladro" + String.valueOf(indice);
                        paramNameList.add(paraName);
                        paramList.add(tal);
                    }
                }
                query.append(") ");
                reporteTitle = "Perforación por Taladro";
                reporteFiltro = Constantes.REPORTE_POR_TALADRO;
            }

            // selección de macolla
            if (macollaSelected != null && macollasSelectedSet.size() == 1
                    && pozoSelected == null) {
                query.append(" AND p.macollaId = :macolla");
                paramNameList.add("macolla");
                paramList.add(macollaSelected);

                reporteTitle = "Perforación por Macolla";
                reporteFiltro = Constantes.REPORTE_POR_MACOLLA;
            }

            if (!macollasSelectedSet.isEmpty() && macollasSelectedSet.size() > 1) {
                boolean primeravez = true;
                for (Integer indice : macollasSelectedSet) {
                    Macolla mac = (Macolla) macollasList.getModel().getElementAt(indice);
                    if (primeravez) {
                        query.append(" AND (p.macollaId = :macolla").append(indice);
                        paraName = "macolla" + String.valueOf(indice);
                        paramNameList.add(paraName);
                        paramList.add(mac);
                        primeravez = false;
                    } else {
                        query.append(" OR p.macollaId = :macolla").append(indice);
                        paraName = "macolla" + String.valueOf(indice);
                        paramNameList.add(paraName);
                        paramList.add(mac);
                    }
                }
                query.append(") ");
                reporteTitle = "Perforación por Macollas";
                reporteFiltro = Constantes.REPORTE_POR_MACOLLA;
            }

            // selección de pozo
            if (pozoSelected != null && pozosSelectedSet.size() == 1) {
                query.append(" AND p.pozoId = :pozo");
                paramNameList.add("pozo");
                paramList.add(pozoSelected);

                reporteTitle = "Perforación por Pozo";
                reporteFiltro = Constantes.REPORTE_POR_POZO;
            }

            if (!pozosSelectedSet.isEmpty() && pozosSelectedSet.size() > 1) {
                boolean primeravez = true;
                for (Integer indice : pozosSelectedSet) {
                    Pozo poz = (Pozo) pozosList.getModel().getElementAt(indice);
                    if (primeravez) {
                        query.append(" AND (p.pozoId = :pozo").append(indice);
                        paraName = "pozo" + String.valueOf(indice);
                        paramNameList.add(paraName);
                        paramList.add(poz);
                        primeravez = false;
                    } else {
                        query.append(" OR p.pozoId = :pozo").append(indice);
                        paraName = "pozo" + String.valueOf(indice);
                        paramNameList.add(paraName);
                        paramList.add(poz);
                    }
                }
                query.append(") ");
                reporteTitle = "Perforación por Pozos";
                reporteFiltro = Constantes.REPORTE_POR_POZO;
            }

            // selección de fechas
            if (fechaInIniDateChooser.getDate() != null) {
                Date fecha = fechaInIniDateChooser.getDate();
                query.append(" AND p.fechaIn >= :fechaInIni");
                paraName = "fechaInIni";
                paramNameList.add(paraName);
                paramList.add(fecha);
            }

            if (fechaInFinDateChooser.getDate() != null) {
                Date fecha = fechaInFinDateChooser.getDate();
                query.append(" AND p.fechaIn <= :fechaInFin");
                paraName = "fechaInFin";
                paramNameList.add(paraName);
                paramList.add(fecha);
            }

            if (fechaOutIniDateChooser.getDate() != null) {
                Date fecha = fechaOutIniDateChooser.getDate();
                query.append(" AND p.fechaOut >= :fechaOutIni");
                paraName = "fechaOutIni";
                paramNameList.add(paraName);
                paramList.add(fecha);
            }

            if (fechaOutFinDateChooser.getDate() != null) {
                Date fecha = fechaOutFinDateChooser.getDate();
                query.append(" AND p.fechaOut <= :fechaOutFin");
                paraName = "fechaOutFin";
                paramNameList.add(paraName);
                paramList.add(fecha);
            }

            if (fechaInIniDateChooser.getDate() != null
                    || fechaInFinDateChooser.getDate() != null
                    || fechaOutIniDateChooser.getDate() != null
                    || fechaOutFinDateChooser.getDate() != null) {
                reporteTitle = "Perforación con filtro de fechas";
                reporteFiltro = Constantes.REPORTE_POR_FECHAS;
            }

            query.append(" ORDER BY p.taladroId, p.fechaIn");
            paramName = paramNameList.toArray(new String[paramNameList.size()]);
            param = paramList.toArray(new Object[paramList.size()]);

            perforacionList = controller
                    .getPerforacionList(query.toString(), paramName, param);
                      
            if (!perforacionList.isEmpty()) {
                showReportDialog(perforacionList, reporteFiltro, reporteTitle);
            } else {
                Contexto.showMessage("La selección realizada no produjo resultados", Constantes.MENSAJE_WARNING);
            }

            taladrosList.clearSelection();
            macollasList.clearSelection();
            pozosList.clearSelection();
        } else {
            Contexto.showMessage("Debe seleccionar al menos un escenario", Constantes.MENSAJE_ERROR);
        }
    }

    private void showReportDialog(List<Perforacion> lista, int reporteFiltro,
            String reportTitle) {
        SwingUtilities.invokeLater(() -> {
            ReporteResultsDialog dialog = new ReporteResultsDialog(
                    Contexto.getMainFrame(), true, lista, null, reporteFiltro,
                    reportTitle, tipoReporte, escenarioSelected);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    dialog.dispose();
                }
            });
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
    }
    
    private void showReportPerforacionTotalDialog(List<Perforacion> lista, int reporteFiltro,
            String reportTitle) {
        java.awt.EventQueue.invokeLater(() -> {
            ReportePerforacionTotalDialog dialog = new ReportePerforacionTotalDialog(
                    Contexto.getMainFrame(), true, lista, null, reporteFiltro,
                    reportTitle, tipoReporte, escenarioSelected);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    dialog.dispose();
                }
            });
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
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

        conteoButtonGroup = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        procesarButton = new javax.swing.JButton();
        toolbarPanel = new javax.swing.JPanel();
        escenarioComboBox = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        backPanel = new javax.swing.JPanel();
        bloqueComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        yacimientoComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        campoComboBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        planComboBox = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        taladrosList = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        macollasList = new javax.swing.JList();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        pozosList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        fechaInIniDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        fechaInFinDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        fechaOutIniDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        fechaOutFinDateChooser = new com.toedter.calendar.JDateChooser();
        conteosPanel = new javax.swing.JPanel();
        conteoCompletoRadioButton = new javax.swing.JRadioButton();

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
                onOpen(evt);
            }
        });

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

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

        escenarioComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escenarioComboBoxActionPerformed(evt);
            }
        });

        jLabel10.setText("Escenario:");

        javax.swing.GroupLayout toolbarPanelLayout = new javax.swing.GroupLayout(toolbarPanel);
        toolbarPanel.setLayout(toolbarPanelLayout);
        toolbarPanelLayout.setHorizontalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(549, Short.MAX_VALUE))
        );
        toolbarPanelLayout.setVerticalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        toolBar.add(toolbarPanel);

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        bloqueComboBox.setEnabled(false);
        bloqueComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bloqueComboBoxActionPerformed(evt);
            }
        });

        jLabel1.setText("Bloque:");

        yacimientoComboBox.setEnabled(false);
        yacimientoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yacimientoComboBoxActionPerformed(evt);
            }
        });

        jLabel2.setText("Yacimiento");

        jLabel3.setText("Campo");

        campoComboBox.setEnabled(false);
        campoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoComboBoxActionPerformed(evt);
            }
        });

        jLabel4.setText("Plan");

        planComboBox.setEnabled(false);
        planComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planComboBoxActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(taladrosList);

        jLabel5.setText("Taladros:");

        jLabel7.setText("Macollas:");

        jScrollPane3.setViewportView(macollasList);

        jLabel8.setText("Pozos:");

        jScrollPane4.setViewportView(pozosList);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Por rango de fechas"));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Fecha entrada a la fase:");

        jLabel11.setText("Inicio:");

        jLabel9.setText("Fin:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Fecha salida de la fase:");

        jLabel13.setText("Inicio:");

        jLabel14.setText("Fin:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(fechaInIniDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(fechaInFinDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel6)
                    .addComponent(jLabel12)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(fechaOutIniDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(fechaOutFinDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(62, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fechaInIniDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fechaInFinDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fechaOutIniDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fechaOutFinDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        conteoCompletoRadioButton.setSelected(true);
        conteoCompletoRadioButton.setText("Completo");

        javax.swing.GroupLayout conteosPanelLayout = new javax.swing.GroupLayout(conteosPanel);
        conteosPanel.setLayout(conteosPanelLayout);
        conteosPanelLayout.setHorizontalGroup(
            conteosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conteosPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(conteoCompletoRadioButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        conteosPanelLayout.setVerticalGroup(
            conteosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conteosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(conteoCompletoRadioButton)
                .addContainerGap(70, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(conteosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(backPanelLayout.createSequentialGroup()
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, backPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bloqueComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yacimientoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(campoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(planComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(23, 23, 23))
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bloqueComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(yacimientoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(campoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(planComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(conteosPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onOpen(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onOpen
        switch (tipoReporte) {
            case Constantes.REPORTE_INVERSION:
                this.title = "Reportes de Inversión";
                break;
            case Constantes.REPORTE_PERFORACION:
                this.title = "Reportes de Perforación";
                break;
            case Constantes.REPORTE_PERFORACION_TOTAL:
                this.title = "Reportes de Perforación Total";
                break;
        }
        fillEscenarioComboBox();
    }//GEN-LAST:event_onOpen

    private void escenarioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escenarioComboBoxActionPerformed
        if (escenarioComboBox.getSelectedItem() instanceof Escenario) {
            escenarioSelected = (Escenario) escenarioComboBox.getSelectedItem();

            fillBaseComboBoxes();
            fillCampoComboBox();
            enableComboBoxes(true);
            fillTaladroList();
        } else {
            resetComboBox(bloqueComboBox);
            resetComboBox(yacimientoComboBox);
            resetComboBox(planComboBox);
            resetComboBox(campoComboBox);
            resetListas();
            enableComboBoxes(false);
        }
    }//GEN-LAST:event_escenarioComboBoxActionPerformed

    private void bloqueComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bloqueComboBoxActionPerformed
        if (bloqueComboBox.getSelectedIndex() != 0) {
            bloqueSelected = (String) bloqueComboBox.getSelectedItem();
        }
    }//GEN-LAST:event_bloqueComboBoxActionPerformed

    private void yacimientoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yacimientoComboBoxActionPerformed
        if (yacimientoComboBox.getSelectedIndex() != 0) {
            yacimientoSelected = (String) yacimientoComboBox.getSelectedItem();
        }
    }//GEN-LAST:event_yacimientoComboBoxActionPerformed

    private void campoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoComboBoxActionPerformed
        if (campoComboBox.getSelectedItem() instanceof Campo) {
            campoSelected = (Campo) campoComboBox.getSelectedItem();
            fillMacollaList();
        }
    }//GEN-LAST:event_campoComboBoxActionPerformed

    private void planComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planComboBoxActionPerformed
        if (planComboBox.getSelectedIndex() != 0) {
            planSelected = (String) planComboBox.getSelectedItem();
        }
    }//GEN-LAST:event_planComboBoxActionPerformed

    private void procesarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_procesarButtonActionPerformed
        generarReporte();
    }//GEN-LAST:event_procesarButtonActionPerformed

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        fillEscenarioComboBox();
    }//GEN-LAST:event_onActivated


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JComboBox bloqueComboBox;
    private javax.swing.JComboBox campoComboBox;
    private javax.swing.ButtonGroup conteoButtonGroup;
    private javax.swing.JRadioButton conteoCompletoRadioButton;
    private javax.swing.JPanel conteosPanel;
    private javax.swing.JComboBox escenarioComboBox;
    private com.toedter.calendar.JDateChooser fechaInFinDateChooser;
    private com.toedter.calendar.JDateChooser fechaInIniDateChooser;
    private com.toedter.calendar.JDateChooser fechaOutFinDateChooser;
    private com.toedter.calendar.JDateChooser fechaOutIniDateChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JList macollasList;
    private javax.swing.JComboBox planComboBox;
    private javax.swing.JList pozosList;
    private javax.swing.JButton procesarButton;
    private javax.swing.JList taladrosList;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JComboBox yacimientoComboBox;
    // End of variables declaration//GEN-END:variables

    class MacollasListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            macollasSelectedSet.clear();
            if (!lsm.isSelectionEmpty()) {
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        macollasSelectedSet.add(i);
                    }
                }

                int[] selectedIndexes = macollasList.getSelectedIndices();
                if (!macollasSelectedSet.isEmpty()) {
                    if (macollasSelectedSet.size() == 1) {
                        macollaSelected = (Macolla) macollasList.getModel().getElementAt(selectedIndexes[0]);
                        fillPozoList();
                    } else {
                        pozoModel.removeAllElements();
                        pozosList.setModel(pozoModel);
                    }
                }
            }
        }
    }

    class TaladrosListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            taladrosSelectedSet.clear();
            if (!lsm.isSelectionEmpty()) {
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        taladrosSelectedSet.add(i);
                    }
                }

                int[] selectedIndexes = taladrosList.getSelectedIndices();
                if (!taladrosSelectedSet.isEmpty()) {
                    if (taladrosSelectedSet.size() == 1) {
                        taladroSelected = (Taladro) taladrosList.getModel().getElementAt(selectedIndexes[0]);
                    }
                }
            }
        }
    }

    class PozosListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            pozosSelectedSet.clear();
            if (!lsm.isSelectionEmpty()) {
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        pozosSelectedSet.add(i);
                    }
                }

                int[] selectedIndexes = pozosList.getSelectedIndices();
                if (!pozosSelectedSet.isEmpty()) {
                    if (pozosSelectedSet.size() == 1) {
                        pozoSelected = (Pozo) pozosList.getModel().getElementAt(selectedIndexes[0]);
                    }
                }
            }
        }
    }
}
