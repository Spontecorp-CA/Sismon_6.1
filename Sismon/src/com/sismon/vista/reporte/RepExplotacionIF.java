package com.sismon.vista.reporte;

import com.sismon.controller.Constantes;   
import com.sismon.model.Campo;
import com.sismon.model.Escenario;
import com.sismon.model.Explotacion;
import com.sismon.model.Macolla;
import com.sismon.model.Pozo;
import com.sismon.vista.Contexto;
import com.sismon.vista.controller.ReportesController;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RepExplotacionIF extends javax.swing.JInternalFrame {

    private static RepExplotacionIF instance = null;
    private Escenario escenarioSelected;
    private Campo campoSelected;
    private Macolla macollaSelected;
    private String bloqueSelected;
    private String yacimientoSelected;
    private String planSelected;
    private Pozo pozoSelected;
    private Date inicialDate;
    private Date finalDate;
    private int tipoReporte;

    private final DefaultListModel taladroModel;
    private final DefaultListModel macollaModel;
    private final DefaultListModel pozoModel;
    private final ListSelectionModel macollasSelectionModel;
    private final ListSelectionModel pozosSelectionModel;
    private final Set<Integer> macollasSelectedSet;
    private final Set<Integer> pozosSelectedSet;
    private List<Pozo> pozoList;

    private final ReportesController controller;
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final String VALOR_INICIAL = "... seleccione";
    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form RepInversionIF
     */
    private RepExplotacionIF(int tipoReporte) {
        initComponents();
        init();
        
        this.tipoReporte = tipoReporte;

        switch (tipoReporte) {
            case Constantes.REPORTE_INVERSION:
                this.title = "Reportes de Inversión";
                break;
            case Constantes.REPORTE_PERFORACION:
                this.title = "Reportes de Perforación";
                break;
            case Constantes.REPORTE_EXPLOTACION:
                this.title = "Reportes de Producción";
                break;
        }

        controller = new ReportesController();
        this.taladroModel = new DefaultListModel();
        this.macollaModel = new DefaultListModel();
        this.pozoModel = new DefaultListModel();

        this.macollasSelectionModel = macollasList.getSelectionModel();
        this.pozosSelectionModel = pozosList.getSelectionModel();
        macollasSelectionModel.addListSelectionListener(new MacollasListSelectionHandler());
        pozosSelectionModel.addListSelectionListener(new PozosListSelectionHandler());
        this.macollasSelectedSet = new HashSet<>();
        this.pozosSelectedSet = new HashSet<>();
    }
    
    private void init(){
        setFrameIcon(icon);
    }

    public static RepExplotacionIF getInstance(int tipoReporte) {
        if (instance == null) {
            instance = new RepExplotacionIF(tipoReporte);
        }
        return instance;
    }
    
    private void setPozoList(){
        new BackgroundWorker().execute();
    }
    
    private void fillBaseComboBoxes() {
        resetComboBox(bloqueComboBox);
        resetComboBox(yacimientoComboBox);
        resetComboBox(planComboBox);

        Set<String> bloques = new HashSet<>();
        Set<String> yacimientos = new HashSet<>();
        Set<String> planes = new HashSet<>();
        List<Pozo> pozos = fillPozos();//controller.getPozoList();
        pozos.stream().forEach(pz -> {
            bloques.add(pz.getBloque());
            yacimientos.add(pz.getYacimiento());
            planes.add(pz.getPlan());
        });

        bloques.stream().forEach(bloqueComboBox::addItem);
        yacimientos.stream().forEach(yacimientoComboBox::addItem);
        planes.stream().forEach(planComboBox::addItem);
    }
    
    private List<Pozo> fillPozos(){
        List<Pozo> pozos = new ArrayList<>();
        Contexto.showMessage("Cargando datos desde la base, puede tardar unos segundos", Color.blue);
        SwingWorker<List<Pozo>, Void> worker = new SwingWorker() {
            @Override
            protected List<Pozo> doInBackground() throws Exception {
                
                List<Pozo> pozos = controller.getPozoList();
                return pozos;
            }
        };
        try {
            worker.execute();
            pozos =  worker.get();
            Contexto.showMessage("", Color.blue);
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(RepExplotacionIF.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pozos;
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
        List<Macolla> macollas = controller.getMacollasExplotadas();
        macollas.stream()
                .sorted((Macolla m1, Macolla m2) -> 
                        m1.getNumero().compareTo(m2.getNumero())
                )
                .forEach(model::addElement);
        macollasList.setModel(model);
    }

    private void fillPozoList() {
            pozoModel.removeAllElements();
            List<Pozo> pozos = controller.getPozosExplotados(macollaSelected);
            pozos.stream()
                 .forEach(pozoModel::addElement);
            pozosList.setModel(pozoModel);
    }

    private void generarReporte() {
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);

        StringBuilder query = new StringBuilder();
        List<String> paramNameList = new ArrayList<>();
        List<Object> paramList = new ArrayList<>();
        List<Explotacion> explotacionList;
        
        boolean macollaFilter = false;
        boolean pozoFilter = false;
        int reporteFiltro;

        String[] paramName;
        Object[] param;

        query.append("SELECT e FROM Explotacion e");
        String paraName;

        reporteFiltro = Constantes.REPORTE_SIN_FILTRO;
        String reporteTitle = "Explotación Total";

        // selección de macolla
        if (macollaSelected != null && macollasSelectedSet.size() == 1){
            query.append(" JOIN e.pozoId p WHERE p.macollaId = :macolla");
            paramNameList.add("macolla");
            paramList.add(macollaSelected);

            reporteTitle = "Explotación por Macolla";
            reporteFiltro = Constantes.REPORTE_POR_MACOLLA;
            macollaFilter = true;
        }

        if (!macollasSelectedSet.isEmpty() && macollasSelectedSet.size() > 1) {
            boolean primeravez = true;
            for (Integer indice : macollasSelectedSet) {
                Macolla mac = (Macolla) macollasList.getModel().getElementAt(indice);
                if (primeravez) {
                    query.append(" JOIN e.pozoId p WHERE p.macollaId = :macolla").append(indice);
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

            reporteTitle = "Explotación por Macollas";
            reporteFiltro = Constantes.REPORTE_POR_MACOLLA;
            macollaFilter = true;
        }

        // selección de pozo
        if (pozoSelected != null && pozosSelectedSet.size() == 1) {
            query.append(" AND e.pozoId = :pozo");
            paramNameList.add("pozo");
            paramList.add(pozoSelected);

            reporteTitle = "Explotación por Pozo";
            reporteFiltro = Constantes.REPORTE_POR_POZO;
            pozoFilter = true;
        }

        if (!pozosSelectedSet.isEmpty() && pozosSelectedSet.size() > 1) {
            boolean primeravez = true;
            for (Integer indice : pozosSelectedSet) {
                Pozo poz = (Pozo) pozosList.getModel().getElementAt(indice);
                if (primeravez) {
                    query.append(" AND e.pozoId = :pozo").append(indice);
                    paraName = "pozo" + String.valueOf(indice);
                    paramNameList.add(paraName);
                    paramList.add(poz);
                    primeravez = false;
                } else {
                    query.append(" OR e.pozoId = :pozo").append(indice);
                    paraName = "pozo" + String.valueOf(indice);
                    paramNameList.add(paraName);
                    paramList.add(poz);
                }
            }
            
            reporteTitle = "Explotación por Pozos";
            reporteFiltro = Constantes.REPORTE_POR_POZO;
            pozoFilter = true;
        }
        
        paramName = paramNameList.toArray(new String[paramNameList.size()]);
        param = paramList.toArray(new Object[paramList.size()]);

        if(macollaFilter || pozoFilter){
            query.append(" ORDER BY p.macollaId, e.pozoId, e.fecha");
            explotacionList = controller
                    .getExplotacionList(query.toString(), paramName, param);
        } else {
            query.append(" JOIN e.pozoId p ORDER BY p.macollaId, e.pozoId, e.fecha");
            explotacionList = controller
                    .getExplotacionList(query.toString());
        }
        
        if (!explotacionList.isEmpty()) {
            showReportDialog(explotacionList, reporteFiltro, reporteTitle);
        } else {
            Contexto.showMessage("La selección realizada no produjo resultados", Constantes.MENSAJE_WARNING);
        }

        macollasList.clearSelection();
        pozosList.clearSelection();
        macollaFilter = false;
        pozoFilter = false;
    }

    private void showReportDialog(List<Explotacion> lista, int reporteFiltro,
            String reportTitle) {
        java.awt.EventQueue.invokeLater(() -> {
            ReporteResultsDialog dialog = new ReporteResultsDialog(
                    Contexto.getMainFrame(), true, null, lista, reporteFiltro,
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

        agrupacionButtonGroup = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        procesarButton = new javax.swing.JButton();
        toolbarPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        backPanel = new javax.swing.JPanel();
        bloqueComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        yacimientoComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        campoComboBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        planComboBox = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        macollasList = new javax.swing.JList();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        pozosList = new javax.swing.JList();

        setClosable(true);
        setIconifiable(true);
        setTitle("Reportes de Explotación");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
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

        javax.swing.GroupLayout toolbarPanelLayout = new javax.swing.GroupLayout(toolbarPanel);
        toolbarPanel.setLayout(toolbarPanelLayout);
        toolbarPanelLayout.setHorizontalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolbarPanelLayout.createSequentialGroup()
                .addContainerGap(645, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        toolbarPanelLayout.setVerticalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        toolBar.add(toolbarPanel);

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        bloqueComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bloqueComboBoxActionPerformed(evt);
            }
        });

        jLabel1.setText("Bloque:");

        yacimientoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yacimientoComboBoxActionPerformed(evt);
            }
        });

        jLabel2.setText("Yacimiento");

        jLabel3.setText("Campo");

        campoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoComboBoxActionPerformed(evt);
            }
        });

        jLabel4.setText("Plan");

        planComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planComboBoxActionPerformed(evt);
            }
        });

        jLabel7.setText("Macollas:");

        jScrollPane3.setViewportView(macollasList);

        jLabel8.setText("Pozos:");

        jScrollPane4.setViewportView(pozosList);

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(551, 551, 551))
                    .addGroup(backPanelLayout.createSequentialGroup()
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
                        .addGap(23, 23, 23))))
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
                .addGap(10, 10, 10)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(198, Short.MAX_VALUE))
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
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);
                generarReporte();
                return null;
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
            }
        };
        worker.execute();
    }//GEN-LAST:event_procesarButtonActionPerformed

    private void onOpen(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onOpen
        progressBar.setVisible(false);
        //setPozoList();
        fillCampoComboBox();
        fillBaseComboBoxes();
    }//GEN-LAST:event_onOpen


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup agrupacionButtonGroup;
    private javax.swing.JPanel backPanel;
    private javax.swing.JComboBox bloqueComboBox;
    private javax.swing.JComboBox campoComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JList macollasList;
    private javax.swing.JComboBox planComboBox;
    private javax.swing.JList pozosList;
    private javax.swing.JButton procesarButton;
    private javax.swing.JProgressBar progressBar;
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
    
    private class BackgroundWorker extends SwingWorker<Void, Void> {

        private JProgressBar pb;
        private JDialog dialog;

        public BackgroundWorker() {
            addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("progress".equalsIgnoreCase(evt.getPropertyName())) {
                        if (dialog == null) {
                            dialog = new JDialog();
                            dialog.setTitle("Recolectando datos");
                            dialog.setLayout(new GridBagLayout());
                            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                            GridBagConstraints gbc = new GridBagConstraints();
                            gbc.insets = new Insets(2, 2, 2, 2);
                            gbc.weightx = 1;
                            gbc.gridy = 0;
                            dialog.add(new JLabel("Recolectando..."), gbc);
                            pb = new JProgressBar();
                            gbc.gridy = 1;
                            dialog.add(pb, gbc);
                            dialog.pack();
                            dialog.setLocationRelativeTo(null);
                            dialog.setVisible(true);
                        }
                        pb.setIndeterminate(true);
                    }
                }

            });
        }

        @Override
        protected void done() {
            if (dialog != null) {
                dialog.dispose();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            pozoList = controller.getPozoList();
            return null;
        }
    }
}
