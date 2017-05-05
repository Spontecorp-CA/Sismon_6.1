package com.sismon.vista.escenario;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.jpamanager.ProduccionMesInicialManager;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.model.ProduccionMesInicial;
import com.sismon.model.Taladro;
import com.sismon.vista.Contexto;
import com.sismon.vista.controller.PerforacionEscenarioController3;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class PerforacionEscenariolIF extends javax.swing.JInternalFrame {

    private static PerforacionEscenariolIF instance = null;

    private final PerforacionManager perforacionManager;
    private final ProduccionMesInicialManager pmiManager;
    private final EscenarioManager escenarioManager;

    private Escenario escenarioSelected;

    // Mapa para el almacenamiento temporal de los resultados
    private List<Perforacion> perforaciones;
    private Map<Integer, Object[]> estrategiaPerforacionMap;
    private Date fechaCierre;
    private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private final NumberFormat decimalFormat = new DecimalFormat("###,###,##0.00");

    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form GenPerfBaseIF
     */
    private PerforacionEscenariolIF() {
        initComponents();
        setTitle("Perforación de Escenario");
        setFrameIcon(icon);

        this.perforacionManager = new PerforacionManager();
        this.pmiManager = new ProduccionMesInicialManager();
        this.escenarioManager = new EscenarioManager();
        this.estrategiaPerforacionMap = new TreeMap<>();
        this.progressBar.setVisible(false);
    }

    public static PerforacionEscenariolIF getInstance() {
        if (instance == null) {
            instance = new PerforacionEscenariolIF();
        }
        return instance;
    }

    private void init() {
        fillEscenarioComboBox();
    }

    private void configureListener() {
        fechaCierreDateChooser.getDateEditor().addPropertyChangeListener(dateListener);
    }

    private void removeListener() {
        fechaCierreDateChooser.getDateEditor().removePropertyChangeListener(dateListener);
    }

    private void fillEscenarioComboBox() {
        escenarioComboBox.removeAllItems();
        List<Escenario> escenarios = escenarioManager.findAll();
        escenarioComboBox.addItem("... seleccione escenario");
        for (Escenario escenario : escenarios) {
            escenarioComboBox.addItem(escenario);
        }
    }

    private void fillPerforacionMap() {
        perforaciones = perforacionManager.findAll(escenarioSelected);
        if (perforaciones != null || !perforaciones.isEmpty()) {
            estrategiaPerforacionMap = new TreeMap<>();
            int i = 1;
            for (Perforacion perf : perforaciones) {
                Object[] items = new Object[15];
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
                items[10] = perf.getEscenarioId();
                items[11] = perf.getDias();
                items[12] = perf.getDiasActivos();
                items[13] = perf.getDiasInactivos();
                items[14] = perf.getStatus();
                estrategiaPerforacionMap.put(i++, items);
            }
        } else {
            dataTable.setModel(new DefaultTableModel());
        }
    }

    private void fillHuellaTaladroTable() {
        List<Perforacion> perforacioList = new ArrayList<>();
        estrategiaPerforacionMap.entrySet().stream().map((mapi) -> {
            Perforacion perf = new Perforacion();
            perf.setTaladroId((Taladro) mapi.getValue()[0]);
            perf.setMacollaId((Macolla) mapi.getValue()[1]);
            perf.setFilaId((Fila) mapi.getValue()[2]);
            perf.setPozoId((Pozo) mapi.getValue()[3]);
            perf.setFase((String) mapi.getValue()[4]);
            perf.setFechaIn((Date) mapi.getValue()[5]);
            perf.setFechaOut((Date) mapi.getValue()[6]);
            return perf;
        }).forEach((perf) -> {
            perforacioList.add(perf);
        });

        Comparator comparator = Comparator.comparing((Perforacion p) -> p.getTaladroId().getNombre())
                .thenComparing((Perforacion p) -> p.getTaladroId().getNombre())
                .thenComparing((Perforacion p) -> p.getFilaId().getId())
                .thenComparing((Perforacion p) -> p.getFechaIn());
        List<Perforacion> perfListSorted = (List<Perforacion>) perforacioList.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        int consecutivo = 0;
        List<Perforacion> perforacionList = null;
        Fila fila = new Fila();
        Taladro taladro = new Taladro();
        Map<Integer, List<Perforacion>> conseMap = new HashMap<>();
        for (Perforacion perf : perfListSorted) {
            if (fila.equals(perf.getFilaId()) && taladro.equals(perf.getTaladroId())) {
                perforacionList = conseMap.get(consecutivo);
            } else {
                fila = perf.getFilaId();
                taladro = perf.getTaladroId();
                perforacionList = new ArrayList<>();
                consecutivo++;
            }
            perforacionList.add(perf);
            conseMap.put(consecutivo, perforacionList);
        }

        Object[][] data = new Object[conseMap.size()][6];
        List<Perforacion> temp = new ArrayList<>();
        for (Map.Entry<Integer, List<Perforacion>> mapi : conseMap.entrySet()) {
            List<Perforacion> lista = mapi.getValue();

            Perforacion minPerf = lista
                    .stream()
                    .min(Comparator.comparing(Perforacion::getFechaIn))
                    .get();

            boolean noProductor = true;
            for (Perforacion perf : lista) {
                if (perf.getFase().equals(Constantes.FASE_PRODUCTOR)) {
                    noProductor = false;
                    break;
                } else {
                    noProductor = true;
                }
            }

            Perforacion maxPerf;
            if (noProductor) {
                maxPerf = lista
                        .stream()
                        .max(Comparator.comparing(Perforacion::getFechaOut))
                        .get();
            } else {
                maxPerf = lista
                        .stream()
                        .filter(perf -> perf.getFase().equals(Constantes.FASE_PRODUCTOR))
                        .max(Comparator.comparing(Perforacion::getFechaOut))
                        .get();
            }

            Perforacion p = minPerf;
            p.setFechaOut(maxPerf.getFechaOut());
            p.setFase(maxPerf.getFase());
            temp.add(p);
        }

        List<Perforacion> temp1 = temp.stream()
                .sorted(Comparator.comparing((Perforacion p) -> p.getTaladroId().getNombre())
                        .thenComparing((Perforacion p) -> p.getFechaIn()))
                .collect(Collectors.toList());

        int j = 0;
        for (Perforacion p : temp1) {
            data[j][0] = p.getTaladroId();
            data[j][1] = p.getMacollaId();
            data[j][2] = p.getFilaId();
            data[j][3] = p.getFase();
            data[j][4] = df.format(p.getFechaIn());
            data[j][5] = df.format(p.getFechaOut());
            j++;
        }

        String[] titles = {"Taladro", "Macolla", "Fila", "Fase de salida", "Fecha In", "Fecha Out"};
        DefaultTableModel model = new DefaultTableModel(data, titles);
        huellaTaladroTable.setModel(model);
    }

    private void fillDataTable() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                String[] header = {"Taladro", "Macolla", "Fila", "Pozo", "Fase", "Dias", "Fecha In",
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
                    Date fechaIn = (Date) elementos[5];
                    Date fechaOut = (Date) elementos[6];
                    //long dif = (fechaOut.getTime() - fechaIn.getTime()) / (1000 * 3600 * 24);
                    //data[i][5] = String.valueOf(dif);
                    data[i][5] = decimalFormat.format((double) elementos[11]);
                    data[i][6] = df.format(fechaIn);
                    data[i][7] = df.format(fechaOut);
                    data[i][8] = decimalFormat.format((double) elementos[7]);
                    data[i][9] = decimalFormat.format((double) elementos[8]);
                    data[i][10] = decimalFormat.format((double) elementos[9]);
                    i++;
                }

                TableModel model = new DefaultTableModel(data, header);
                dataTable.setModel(model);
                messageLabel.setText("Procesados " + dataTable.getModel().getRowCount() + " registros");
            }
        });
    }

    private void clearTables() {
        dataTable.setModel(new DefaultTableModel());
        huellaTaladroTable.setModel(new DefaultTableModel());
    }

    private void guardarEscenario() {
        guardarButton.setEnabled(false);
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
                List<ProduccionMesInicial> pmiList = new ArrayList<>();
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
                    perforacion.setBs((Double) items[7]);
                    perforacion.setUsd((Double) items[8]);
                    perforacion.setEquiv((Double) items[9]);
                    perforacion.setEscenarioId(escenarioSelected);
                    perforacion.setDias((Double) items[11]);
                    perforacion.setDiasActivos((Double) items[12]);
                    perforacion.setDiasInactivos((Double) items[13]);
                    perforacion.setStatus((Double) items[14]);
                    perforacionList.add(perforacion);
                    
                    if(Constantes.FASE_EVALUACION.equals((String) items[4])){
                        Pozo pozo = (Pozo) items[3];
                        Date fechaAceptacion = (Date)(Date) items[6];
                        
                        ProduccionMesInicial pmi = new ProduccionMesInicial();
                        pmi.setFechaAceptacion((Date) items[6]);
                        pmi.setPozoId((Pozo) items[3]);
                        pmi.setEscenarioId(escenarioSelected);
                        pmi.setProduccion(produccionMesInicial(pozo, fechaAceptacion));
                        pmiList.add(pmi);
                    }
                }

                if (!perforacionManager.findAll(escenarioSelected).isEmpty()) {
                    perforacionManager.remove(escenarioSelected);
                    pmiManager.remove(escenarioSelected);
                }

                perforacionManager.batchSave(perforacionList);
                pmiManager.batchSave(pmiList);

                escenarioSelected.setFechaCierre(fechaCierre);
                escenarioManager.edit(escenarioSelected);
                salvado = true;
                return null;
            }
            
            private double produccionMesInicial(Pozo pozo, Date fechaAceptacion){
                double declMensual = calculaDeclinacionMensual(pozo);
                
                LocalDate ld = LocalDateTime.ofInstant(Instant
                        .ofEpochMilli(fechaAceptacion.getTime()), 
                        ZoneId.systemDefault()).toLocalDate();
                int dia = ld.getDayOfMonth();
                Month month = ld.getMonth();
                Year year = Year.of(ld.getYear());
                int lastDayOfMonth = month.length(year.isLeap());
                int diasToEndofMonth = lastDayOfMonth - dia;
                
                double pmi = (pozo.getPi() * diasToEndofMonth * (2 - declMensual))
                                / (2 * lastDayOfMonth);
                return pmi;
            }
            
            private double calculaDeclinacionMensual(Pozo pozo){
                return 1 - Math.pow(1 - pozo.getDeclinacion(), 1/12);
            }

            @Override
            protected void done() {
                try {
                    if (salvado) {
                        progressBar.setVisible(false);
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
        tabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        messageLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        huellaTaladroTable = new javax.swing.JTable();
        toolBar = new javax.swing.JToolBar();
        guardarButton = new javax.swing.JButton();
        procesarButton = new javax.swing.JButton();
        toolbarPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        escenarioComboBox = new javax.swing.JComboBox();
        fechaCierreDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        lastPerforacionLabel = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                onActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                onClosing(evt);
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
                "Taladro", "Macolla", "Fila", "Pozo", "Fase", "Días", "Fecha in", "Fecha out", "Bs.", "US$", "US$ Equiv."
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(dataTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 626, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(311, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Perforación", jPanel1);

        huellaTaladroTable.setAutoCreateRowSorter(true);
        huellaTaladroTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Taladro", "Macolla", "Fila", "Fase de salida", "Fecha In", "Fecha Out"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(huellaTaladroTable);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 927, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Huella de Taladro", jPanel2);

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane)
                .addContainerGap())
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane)
                .addContainerGap())
        );

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        guardarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconguardar26.png"))); // NOI18N
        guardarButton.setText("Guardar");
        guardarButton.setEnabled(false);
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
        procesarButton.setEnabled(false);
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

        jLabel1.setText("Fecha cierre:");

        lastPerforacionLabel.setText(" ");

        javax.swing.GroupLayout toolbarPanelLayout = new javax.swing.GroupLayout(toolbarPanel);
        toolbarPanel.setLayout(toolbarPanelLayout);
        toolbarPanelLayout.setHorizontalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lastPerforacionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(escenarioComboBox, 0, 287, Short.MAX_VALUE))
                .addGap(42, 42, 42)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fechaCierreDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        toolbarPanelLayout.setVerticalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarPanelLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fechaCierreDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lastPerforacionLabel))
        );

        toolBar.add(toolbarPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        guardarEscenario();
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void procesarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_procesarButtonActionPerformed
        try {
            PerforacionEscenarioController3 perfController
                    = new PerforacionEscenarioController3(escenarioSelected, fechaCierre);
            perfController.execute();
            estrategiaPerforacionMap = perfController.get();
            fillDataTable();
            fillHuellaTaladroTable();
            guardarButton.setEnabled(true);
        } catch (InterruptedException | ExecutionException ex) {
            sismonlog.logger.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_procesarButtonActionPerformed

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        Contexto.setActiveFrame(instance);
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        Contexto.setActiveFrame(instance);
        messageLabel.setText(null);
        fechaCierreDateChooser.setDate(null);
        configureListener();
        init();
    }//GEN-LAST:event_onActivated

    private void escenarioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escenarioComboBoxActionPerformed
        if (escenarioComboBox.getSelectedItem() instanceof Escenario) {
            escenarioSelected = (Escenario) escenarioComboBox.getSelectedItem();
            fillPerforacionMap();
            fillDataTable();
            fillHuellaTaladroTable();
            fechaCierreDateChooser.setDate(null);
            if (escenarioSelected.getFechaCierre() != null) {
                String mensaje = String.format("Última perforación de este escenario: %s",
                        df.format(escenarioSelected.getFechaCierre()));
                lastPerforacionLabel.setText(mensaje);
            }
        } else {
            clearTables();
            lastPerforacionLabel.setText(null);
        }
    }//GEN-LAST:event_escenarioComboBoxActionPerformed

    private void onClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClosing
        removeListener();
        Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
        clearTables();
        escenarioComboBox.setSelectedItem("... seleccione escenario");
    }//GEN-LAST:event_onClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JTable dataTable;
    private javax.swing.JComboBox escenarioComboBox;
    private com.toedter.calendar.JDateChooser fechaCierreDateChooser;
    private javax.swing.JButton guardarButton;
    private javax.swing.JTable huellaTaladroTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lastPerforacionLabel;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton procesarButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolbarPanel;
    // End of variables declaration//GEN-END:variables

    private final PropertyChangeListener dateListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("date".equals(evt.getPropertyName())) {
                if (fechaCierreDateChooser.getDate() != null) {
                    fechaCierre = fechaCierreDateChooser.getDate();
                    procesarButton.setEnabled(true);
                }
            } else {
                procesarButton.setEnabled(false);
            }
        }

    };
}
