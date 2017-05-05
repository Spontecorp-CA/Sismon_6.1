package com.sismon.vista.escenario;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.jpamanager.FilaHasTaladroManager;
import com.sismon.jpamanager.FilaManager;
import com.sismon.jpamanager.ParidadManager;
import com.sismon.jpamanager.TaladroHasFaseManager;
import com.sismon.jpamanager.TaladroManager;
import com.sismon.jpamanager.TaladroMantManager;
import com.sismon.jpamanager.TaladroStatusManager;
import com.sismon.jpamanager.TaladroSustitutoManager;
import com.sismon.jpamanager.MacollaManager;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.jpamanager.PozoManager;
import com.sismon.jpamanager.PozoSecuenciaManager;
import com.sismon.jpamanager.TaladroAsignadoManager;
import com.sismon.jpamanager.TaladroTrazaManager;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Paridad;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.model.PozoSecuencia;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroAsignado;
import com.sismon.model.TaladroHasFase;
import com.sismon.model.TaladroMant;
import com.sismon.model.TaladroStatus;
import com.sismon.model.TaladroSustituto;
import com.sismon.model.TaladroTraza;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import com.sismon.vista.utilities.Utils;
import com.sismon.vista.utilities.VistaUtilities;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author jgcastillo
 */
public class GestionTaladros2IF extends javax.swing.JInternalFrame {

    private static GestionTaladros2IF instance = null;

    private final ParidadManager paridadManager;
    private final TaladroManager taladroManager;
    private final EscenarioManager escenarioManager;
    private final TaladroHasFaseManager thFaseManager;
    private final TaladroMantManager mantManager;
    private final TaladroStatusManager talStatusManager;
    private final TaladroSustitutoManager talSustitutoManager;
    private final MacollaManager macollaManager;
    private final FilaManager filaManager;
    private final FilaHasTaladroManager fhTaladroManager;
    private final PerforacionManager perforacionManager;
    private final PozoSecuenciaManager pozoSecuenciaManager;
    private final PozoManager pozoManager;
    private final TaladroAsignadoManager taladroAsignadoManager;
    private final TaladroTrazaManager taladroTrazaManager;

    private File archivo;
    private Paridad paridad;
    private Escenario escenarioSelected;
    private TaladroMant mantenimientoSelected;
    private Taladro taladroSelected;
    private List<Perforacion> perforacionList;
    private Pozo pozoOriginal;
    private Pozo pozoDestino;
//    private FilaHasTaladro fhtEliminar;
    private TaladroAsignado taEliminar;
    private Map<String, Taladro> taladrosMap = new LinkedHashMap<>();
    private Map<String, Taladro> newTaladrosMap = new HashMap<>();
    private Map<Taladro, List<TaladroHasFase>> fasesTaladroMap = new HashMap<>();
    private Map<Taladro, List<TaladroStatus>> statusTaladroMap = new HashMap<>();
    private boolean isnew = false;
    private boolean newTaladro = false;
    private boolean newMantenimiento = false;
    private boolean updateMantenimiento = false;
    private boolean newDescontinuacion = false;
    private boolean deleteMovimientoTaladro = false;
    private boolean descontinuarTaladro = false;
    private boolean isEditable = false;
    private boolean isEditingMantenimiento = false;

    private static final String OPCION_SELECIONE = "... seleccione";
    private static final DateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final NumberFormat nFormat = new DecimalFormat("###,###,###,###,##0.00");
    private static final SismonLog SISMONLOG = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form GestionTaladrosIF
     */
    private GestionTaladros2IF() {
        super("Gestión de Taladros");
        initComponents();

        this.paridadManager = new ParidadManager();
        this.taladroManager = new TaladroManager();
        this.escenarioManager = new EscenarioManager();
        this.thFaseManager = new TaladroHasFaseManager();
        this.mantManager = new TaladroMantManager();
        this.talStatusManager = new TaladroStatusManager();
        this.talSustitutoManager = new TaladroSustitutoManager();
        this.macollaManager = new MacollaManager();
        this.filaManager = new FilaManager();
        this.fhTaladroManager = new FilaHasTaladroManager();
        this.perforacionManager = new PerforacionManager();
        this.pozoSecuenciaManager = new PozoSecuenciaManager();
        this.pozoManager = new PozoManager();
        this.taladroAsignadoManager = new TaladroAsignadoManager();
        this.taladroTrazaManager = new TaladroTrazaManager();

        init();
    }

    public static GestionTaladros2IF getInstance() {
        if (instance == null) {
            instance = new GestionTaladros2IF();
        }
        return instance;
    }

    private void init() {
        try {
            this.setFrameIcon(icon);
            paridad = paridadManager.find(Constantes.PARIDAD_ACTIVA);

            // relativos a los mantenimientos
            periodicoPanel.setVisible(false);

            // ocultar estos componentes:
            jLabel38.setVisible(false);
            jLabel39.setVisible(false);
            descontinuacionDateChooser.setVisible(false);
            sustitutoComboBox.setVisible(false);
            quitarTaladroButton.setVisible(false);
        } catch (Exception ex) {
            SISMONLOG.logger.log(Level.SEVERE, null, ex);
        }
    }

    private void configureListener() {
        taladroNombreTextField.getDocument().addDocumentListener(docListener);
        fechaDispInicialDateChooser.getDateEditor().addPropertyChangeListener(dateListener);

        // relativo a los mantenimientos
        mantenimientoDateChooser.getDateEditor().addPropertyChangeListener(dateListener);
        diasMantTextField.getDocument().addDocumentListener(docListener);

        // relativo a la descontinuación
        descontinuacionDateChooser.getDateEditor().addPropertyChangeListener(dateListener);
    }

    private void removeListener() {
        taladroNombreTextField.getDocument().removeDocumentListener(docListener);
        fechaDispInicialDateChooser.getDateEditor().removePropertyChangeListener(dateListener);

        // relativo a los mantenimientos
        mantenimientoDateChooser.getDateEditor().removePropertyChangeListener(dateListener);
        diasMantTextField.getDocument().removeDocumentListener(docListener);

        // relativo a la descontinuación
        descontinuacionDateChooser.getDateEditor().removePropertyChangeListener(dateListener);
    }

    private void configureTaladrosListener() {
        mudanzaMacollaTextField.getDocument().addDocumentListener(taladroDocListener);
        mudanzaPozoTextField.getDocument().addDocumentListener(taladroDocListener);
        superficialTextField.getDocument().addDocumentListener(taladroDocListener);
        slantTextField.getDocument().addDocumentListener(taladroDocListener);
        pilotoTextField.getDocument().addDocumentListener(taladroDocListener);
        verticalTextField.getDocument().addDocumentListener(taladroDocListener);
        intermedioTextField.getDocument().addDocumentListener(taladroDocListener);
        productorTextField.getDocument().addDocumentListener(taladroDocListener);
        completacionTextField.getDocument().addDocumentListener(taladroDocListener);
        conexionTextField.getDocument().addDocumentListener(taladroDocListener);

        mudanzaMacollaBsTextField.getDocument().addDocumentListener(taladroDocListener);
        mudanzaPozoBsTextField.getDocument().addDocumentListener(taladroDocListener);
        superficialBsTextField.getDocument().addDocumentListener(taladroDocListener);
        slantBsTextField.getDocument().addDocumentListener(taladroDocListener);
        pilotoBsTextField.getDocument().addDocumentListener(taladroDocListener);
        verticalBsTextField.getDocument().addDocumentListener(taladroDocListener);
        intermedioBsTextField.getDocument().addDocumentListener(taladroDocListener);
        productorBsTextField.getDocument().addDocumentListener(taladroDocListener);
        completacionBsTextField.getDocument().addDocumentListener(taladroDocListener);
        conexionBsTextField.getDocument().addDocumentListener(taladroDocListener);

        mudanzaMacollaUsdTextField.getDocument().addDocumentListener(taladroDocListener);
        mudanzaPozoUsdTextField.getDocument().addDocumentListener(taladroDocListener);
        superficialUsdTextField.getDocument().addDocumentListener(taladroDocListener);
        slantUsdTextField.getDocument().addDocumentListener(taladroDocListener);
        pilotoUsdTextField.getDocument().addDocumentListener(taladroDocListener);
        verticalUsdTextField.getDocument().addDocumentListener(taladroDocListener);
        intermedioUsdTextField.getDocument().addDocumentListener(taladroDocListener);
        productorUsdTextField.getDocument().addDocumentListener(taladroDocListener);
        completacionUsdTextField.getDocument().addDocumentListener(taladroDocListener);
        conexionUsdTextField.getDocument().addDocumentListener(taladroDocListener);
    }

    private void removeTaladrosListener() {
        mudanzaMacollaTextField.getDocument().removeDocumentListener(taladroDocListener);
        mudanzaPozoTextField.getDocument().removeDocumentListener(taladroDocListener);
        superficialTextField.getDocument().removeDocumentListener(taladroDocListener);
        slantTextField.getDocument().removeDocumentListener(taladroDocListener);
        pilotoTextField.getDocument().removeDocumentListener(taladroDocListener);
        verticalTextField.getDocument().removeDocumentListener(taladroDocListener);
        intermedioTextField.getDocument().removeDocumentListener(taladroDocListener);
        productorTextField.getDocument().removeDocumentListener(taladroDocListener);
        completacionTextField.getDocument().removeDocumentListener(taladroDocListener);
        conexionTextField.getDocument().removeDocumentListener(taladroDocListener);

        mudanzaMacollaBsTextField.getDocument().removeDocumentListener(taladroDocListener);
        mudanzaPozoBsTextField.getDocument().removeDocumentListener(taladroDocListener);
        superficialBsTextField.getDocument().removeDocumentListener(taladroDocListener);
        slantBsTextField.getDocument().removeDocumentListener(taladroDocListener);
        pilotoBsTextField.getDocument().removeDocumentListener(taladroDocListener);
        verticalBsTextField.getDocument().removeDocumentListener(taladroDocListener);
        intermedioBsTextField.getDocument().removeDocumentListener(taladroDocListener);
        productorBsTextField.getDocument().removeDocumentListener(taladroDocListener);
        completacionBsTextField.getDocument().removeDocumentListener(taladroDocListener);
        conexionBsTextField.getDocument().removeDocumentListener(taladroDocListener);

        mudanzaMacollaUsdTextField.getDocument().removeDocumentListener(taladroDocListener);
        mudanzaPozoUsdTextField.getDocument().removeDocumentListener(taladroDocListener);
        superficialUsdTextField.getDocument().removeDocumentListener(taladroDocListener);
        slantUsdTextField.getDocument().removeDocumentListener(taladroDocListener);
        pilotoUsdTextField.getDocument().removeDocumentListener(taladroDocListener);
        verticalUsdTextField.getDocument().removeDocumentListener(taladroDocListener);
        intermedioUsdTextField.getDocument().removeDocumentListener(taladroDocListener);
        productorUsdTextField.getDocument().removeDocumentListener(taladroDocListener);
        completacionUsdTextField.getDocument().removeDocumentListener(taladroDocListener);
        conexionUsdTextField.getDocument().removeDocumentListener(taladroDocListener);
    }

    private void updateAgregarForm() {
        List<TaladroHasFase> fasesTaladroList = fasesTaladroMap.get(taladroSelected);
        for (TaladroHasFase thf : fasesTaladroList) {
            switch (thf.getFase()) {
                case Constantes.FASE_MUDANZA_ENTRE_MACOLLAS:
                    mudanzaMacollaTextField.setText(nFormat.format(thf.getDias()));
                    mudanzaMacollaBsTextField.setText(nFormat.format(thf.getCostoBs()));
                    mudanzaMacollaUsdTextField.setText(nFormat.format(thf.getCostoUsd()));
                    break;
                case Constantes.FASE_MUDANZA_ENTRE_POZOS:
                    mudanzaPozoTextField.setText(nFormat.format(thf.getDias()));
                    mudanzaPozoBsTextField.setText(nFormat.format(thf.getCostoBs()));
                    mudanzaPozoUsdTextField.setText(nFormat.format(thf.getCostoUsd()));
                    break;
                case Constantes.FASE_SUPERFICIAL:
                    superficialTextField.setText(nFormat.format(thf.getDias()));
                    superficialBsTextField.setText(nFormat.format(thf.getCostoBs()));
                    superficialUsdTextField.setText(nFormat.format(thf.getCostoUsd()));
                    break;
                case Constantes.FASE_SLANT:
                    slantTextField.setText(nFormat.format(thf.getDias()));
                    slantBsTextField.setText(nFormat.format(thf.getCostoBs()));
                    slantUsdTextField.setText(nFormat.format(thf.getCostoUsd()));
                    break;
                case Constantes.FASE_PILOTO:
                    pilotoTextField.setText(nFormat.format(thf.getDias()));
                    pilotoBsTextField.setText(nFormat.format(thf.getCostoBs()));
                    pilotoUsdTextField.setText(nFormat.format(thf.getCostoUsd()));
                    break;
                case Constantes.FASE_VERTICAL:
                    verticalTextField.setText(nFormat.format(thf.getDias()));
                    verticalBsTextField.setText(nFormat.format(thf.getCostoBs()));
                    verticalUsdTextField.setText(nFormat.format(thf.getCostoUsd()));
                    break;
                case Constantes.FASE_INTERMEDIO:
                    intermedioTextField.setText(nFormat.format(thf.getDias()));
                    intermedioBsTextField.setText(nFormat.format(thf.getCostoBs()));
                    intermedioUsdTextField.setText(nFormat.format(thf.getCostoUsd()));
                    break;
                case Constantes.FASE_PRODUCTOR:
                    productorTextField.setText(nFormat.format(thf.getDias()));
                    productorBsTextField.setText(nFormat.format(thf.getCostoBs()));
                    productorUsdTextField.setText(nFormat.format(thf.getCostoUsd()));
                    break;
                case Constantes.FASE_COMPLETACION:
                    completacionTextField.setText(nFormat.format(thf.getDias()));
                    completacionBsTextField.setText(nFormat.format(thf.getCostoBs()));
                    completacionUsdTextField.setText(nFormat.format(thf.getCostoUsd()));
                    break;
                case Constantes.FASE_CONEXION:
                    conexionTextField.setText(nFormat.format(thf.getDias()));
                    conexionBsTextField.setText(nFormat.format(thf.getCostoBs()));
                    conexionUsdTextField.setText(nFormat.format(thf.getCostoUsd()));
                    break;
            }

        }
    }

    private void fillEscenarioComboBox() {
        escenarioComboBox.removeAllItems();
        List<Escenario> escenarios = escenarioManager.findAllMV(false);
        escenarioComboBox.addItem("... seleccione escenario");
        for (Escenario escenario : escenarios) {
            escenarioComboBox.addItem(escenario);
        }

        clearDescComboBoxes();
    }

    private List<TaladroHasFase> fillFaseData(List<TaladroHasFase> faseList) {
        Paridad paridadActiva = null;
        try {
            paridadActiva = paridadManager.find(Constantes.PARIDAD_ACTIVA);
        } catch (Exception ex) {
            SISMONLOG.logger.log(Level.SEVERE, "No pudo cargar la paridad activa", ex);
            Contexto.showMessage("No pudo cargar la paridad activa", Constantes.MENSAJE_ERROR);
        }
        try {
            for (TaladroHasFase fase : faseList) {
                switch (fase.getFase()) {
                    case Constantes.FASE_MUDANZA_ENTRE_MACOLLAS:
                        fase.setDias(Utils.parseDouble(mudanzaMacollaTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(mudanzaMacollaBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(mudanzaMacollaUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_MUDANZA_ENTRE_POZOS:
                        fase.setDias(Utils.parseDouble(mudanzaPozoTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(mudanzaPozoBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(mudanzaPozoUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_SUPERFICIAL:
                        fase.setDias(Utils.parseDouble(superficialTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(superficialBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(superficialUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_SLANT:
                        fase.setDias(Utils.parseDouble(slantTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(slantBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(slantUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_PILOTO:
                        fase.setDias(Utils.parseDouble(pilotoTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(pilotoBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(pilotoUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_VERTICAL:
                        fase.setDias(Utils.parseDouble(verticalTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(verticalBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(verticalUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_INTERMEDIO:
                        fase.setDias(Utils.parseDouble(intermedioTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(intermedioBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(intermedioUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_PRODUCTOR:
                        fase.setDias(Utils.parseDouble(productorTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(productorBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(productorUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_COMPLETACION:
                        fase.setDias(Utils.parseDouble(completacionTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(completacionBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(completacionUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_CONEXION:
                        fase.setDias(Utils.parseDouble(conexionTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(conexionBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(conexionUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                }
            }
            guardarButton.setEnabled(true);
        } catch (ParseException ex) {
            SISMONLOG.logger.log(Level.SEVERE, "Error: dato introducido no correcto", ex);
            Contexto.showMessage("Introdujo un valor incorrecto", Constantes.MENSAJE_ERROR);
        }
        return faseList;
    }

    private void fillTaladrosTable() {
        String[] titles = {"Nombre", "Fecha Disp Inicial", "Status", "Fecha del Status"};
        Object[][] datos = new Object[taladrosMap.size()][titles.length];
        if (!taladrosMap.isEmpty()) {
            int i = 0;
            for (Map.Entry<String, Taladro> mapa : taladrosMap.entrySet()) {
                datos[i][0] = mapa.getKey();
                datos[i][1] = dFormat.format(mapa.getValue().getFechaInicial());
                List<TaladroStatus> statusList = statusTaladroMap.get(mapa.getValue());
                if (statusList != null && !statusList.isEmpty()) {
                    for (TaladroStatus ts : statusList) {
                        if (ts.getStatus() == Constantes.TALADRO_STATUS_ACTIVO) {
                            datos[i][2] = ts.getNombre();
                            datos[i][3] = dFormat.format(ts.getFechaIn());
                            break;
                        }
                    }
                } else {
                    datos[i][2] = "";
                    datos[i][3] = "";
                }
                i++;
            }
        }
        TableModel model = new DefaultTableModel(datos, titles);
        taladrosTable.setModel(model);
    }

    private void makeNewTaladros(List<String[]> data) {
        try {
            for (String[] array : data) {
                Taladro taladro = new Taladro();
                taladro.setNombre(array[0]);
                taladro.setFechaInicial(dFormat.parse(array[1]));
                taladro.setEscenarioId(escenarioSelected);

                List<TaladroHasFase> faseList = new ArrayList<>();

                TaladroHasFase thf = new TaladroHasFase();
                thf.setTaladroId(taladro);
                thf.setFecha(taladro.getFechaInicial());
                thf.setEscenarioId(escenarioSelected);
                thf.setFase(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS);
                thf.setDias(VistaUtilities.parseDouble(array[2]));
                thf.setCostoBs(VistaUtilities.parseDouble(array[12]));
                thf.setCostoUsd(VistaUtilities.parseDouble(array[22]));
                thf.setCostoEquiv(thf.getCostoUsd() + thf.getCostoBs() / paridad.getValor());
                faseList.add(thf);

                thf = new TaladroHasFase();
                thf.setTaladroId(taladro);
                thf.setFecha(taladro.getFechaInicial());
                thf.setEscenarioId(escenarioSelected);
                thf.setFase(Constantes.FASE_MUDANZA_ENTRE_POZOS);
                thf.setDias(VistaUtilities.parseDouble(array[3]));
                thf.setCostoBs(VistaUtilities.parseDouble(array[13]));
                thf.setCostoUsd(VistaUtilities.parseDouble(array[23]));
                thf.setCostoEquiv(thf.getCostoUsd() + thf.getCostoBs() / paridad.getValor());
                faseList.add(thf);

                thf = new TaladroHasFase();
                thf.setTaladroId(taladro);
                thf.setFecha(taladro.getFechaInicial());
                thf.setEscenarioId(escenarioSelected);
                thf.setFase(Constantes.FASE_SUPERFICIAL);
                thf.setDias(VistaUtilities.parseDouble(array[4]));
                thf.setCostoBs(VistaUtilities.parseDouble(array[14]));
                thf.setCostoUsd(VistaUtilities.parseDouble(array[24]));
                thf.setCostoEquiv(thf.getCostoUsd() + thf.getCostoBs() / paridad.getValor());
                faseList.add(thf);

                thf = new TaladroHasFase();
                thf.setTaladroId(taladro);
                thf.setFecha(taladro.getFechaInicial());
                thf.setEscenarioId(escenarioSelected);
                thf.setFase(Constantes.FASE_SLANT);
                thf.setDias(VistaUtilities.parseDouble(array[5]));
                thf.setCostoBs(VistaUtilities.parseDouble(array[15]));
                thf.setCostoUsd(VistaUtilities.parseDouble(array[25]));
                thf.setCostoEquiv(thf.getCostoUsd() + thf.getCostoBs() / paridad.getValor());
                faseList.add(thf);

                thf = new TaladroHasFase();
                thf.setTaladroId(taladro);
                thf.setFecha(taladro.getFechaInicial());
                thf.setEscenarioId(escenarioSelected);
                thf.setFase(Constantes.FASE_PILOTO);
                thf.setDias(VistaUtilities.parseDouble(array[6]));
                thf.setCostoBs(VistaUtilities.parseDouble(array[16]));
                thf.setCostoUsd(VistaUtilities.parseDouble(array[26]));
                thf.setCostoEquiv(thf.getCostoUsd() + thf.getCostoBs() / paridad.getValor());
                faseList.add(thf);

                thf = new TaladroHasFase();
                thf.setTaladroId(taladro);
                thf.setFecha(taladro.getFechaInicial());
                thf.setEscenarioId(escenarioSelected);
                thf.setFase(Constantes.FASE_VERTICAL);
                thf.setDias(VistaUtilities.parseDouble(array[7]));
                thf.setCostoBs(VistaUtilities.parseDouble(array[17]));
                thf.setCostoUsd(VistaUtilities.parseDouble(array[27]));
                thf.setCostoEquiv(thf.getCostoUsd() + thf.getCostoBs() / paridad.getValor());
                faseList.add(thf);

                thf = new TaladroHasFase();
                thf.setTaladroId(taladro);
                thf.setFecha(taladro.getFechaInicial());
                thf.setEscenarioId(escenarioSelected);
                thf.setFase(Constantes.FASE_INTERMEDIO);
                thf.setDias(VistaUtilities.parseDouble(array[8]));
                thf.setCostoBs(VistaUtilities.parseDouble(array[18]));
                thf.setCostoUsd(VistaUtilities.parseDouble(array[28]));
                thf.setCostoEquiv(thf.getCostoUsd() + thf.getCostoBs() / paridad.getValor());
                faseList.add(thf);

                thf = new TaladroHasFase();
                thf.setTaladroId(taladro);
                thf.setFecha(taladro.getFechaInicial());
                thf.setEscenarioId(escenarioSelected);
                thf.setFase(Constantes.FASE_PRODUCTOR);
                thf.setDias(VistaUtilities.parseDouble(array[9]));
                thf.setCostoBs(VistaUtilities.parseDouble(array[19]));
                thf.setCostoUsd(VistaUtilities.parseDouble(array[29]));
                thf.setCostoEquiv(thf.getCostoUsd() + thf.getCostoBs() / paridad.getValor());
                faseList.add(thf);

                thf = new TaladroHasFase();
                thf.setTaladroId(taladro);
                thf.setEscenarioId(escenarioSelected);
                thf.setFecha(taladro.getFechaInicial());
                thf.setFase(Constantes.FASE_COMPLETACION);
                thf.setDias(VistaUtilities.parseDouble(array[10]));
                thf.setCostoBs(VistaUtilities.parseDouble(array[20]));
                thf.setCostoUsd(VistaUtilities.parseDouble(array[30]));
                thf.setCostoEquiv(thf.getCostoUsd() + thf.getCostoBs() / paridad.getValor());
                faseList.add(thf);

                thf = new TaladroHasFase();
                thf.setTaladroId(taladro);
                thf.setFecha(taladro.getFechaInicial());
                thf.setEscenarioId(escenarioSelected);
                thf.setFase(Constantes.FASE_CONEXION);
                thf.setDias(VistaUtilities.parseDouble(array[11]));
                thf.setCostoBs(VistaUtilities.parseDouble(array[21]));
                thf.setCostoUsd(VistaUtilities.parseDouble(array[31]));
                thf.setCostoEquiv(thf.getCostoUsd() + thf.getCostoBs() / paridad.getValor());
                faseList.add(thf);

                taladro.setTaladroHasFaseCollection(faseList);

                taladrosMap.put(taladro.getNombre(), taladro);
                fasesTaladroMap.put(taladro, faseList);

                newTaladrosMap.put(taladro.getNombre(), taladro);
            }

            fillTaladrosTable();
            archivoButton.setEnabled(false);
            guardarButton.setEnabled(true);
        } catch (ParseException ex) {
            SISMONLOG.logger.log(Level.SEVERE, "Error convirtiendo un dato", ex);
        }
    }

    private void loadTaladrosMap() {
        clearTaladrosMap();
        List<Taladro> taladros = taladroManager.findAll(escenarioSelected);
        if (!taladros.isEmpty()) {
            //  int i = 0;
            Collections.sort(taladros, (Taladro t1, Taladro t2) -> {
                return t1.getNombre().compareTo(t2.getNombre());
            });
            for (Taladro taladro : taladros) {
                taladrosMap.put(taladro.getNombre(), taladro);
                List<TaladroHasFase> fasesTaladroList = thFaseManager.findAll(taladro, escenarioSelected);
                fasesTaladroMap.put(taladro, fasesTaladroList);
                List<TaladroStatus> statusTaladroList = talStatusManager.find(taladro);
                statusTaladroMap.put(taladro, statusTaladroList);
            }
        }
    }

    private void clearTaladrosMap() {
        taladrosMap.clear();
        fasesTaladroMap.clear();
    }

    private void updateTaladroModel() {
        List<TaladroHasFase> faseTaladroList = thFaseManager.findAll(taladroSelected, escenarioSelected);
        try {
            for (TaladroHasFase fht : faseTaladroList) {
                switch (fht.getFase()) {
                    case Constantes.FASE_MUDANZA_ENTRE_MACOLLAS:
                        fht.setDias(VistaUtilities.parseDouble(mudanzaMacollaTextField.getText()));
                        fht.setCostoBs(VistaUtilities.parseDouble(mudanzaMacollaBsTextField.getText()));
                        fht.setCostoUsd(VistaUtilities.parseDouble(mudanzaMacollaUsdTextField.getText()));
                        fht.setCostoEquiv(fht.getCostoUsd() + fht.getCostoBs() / paridad.getValor());
                        break;
                    case Constantes.FASE_MUDANZA_ENTRE_POZOS:
                        fht.setDias(VistaUtilities.parseDouble(mudanzaPozoTextField.getText()));
                        fht.setCostoBs(VistaUtilities.parseDouble(mudanzaPozoBsTextField.getText()));
                        fht.setCostoUsd(VistaUtilities.parseDouble(mudanzaPozoUsdTextField.getText()));
                        fht.setCostoEquiv(fht.getCostoUsd() + fht.getCostoBs() / paridad.getValor());
                        break;
                    case Constantes.FASE_SUPERFICIAL:
                        fht.setDias(VistaUtilities.parseDouble(superficialTextField.getText()));
                        fht.setCostoBs(VistaUtilities.parseDouble(superficialBsTextField.getText()));
                        fht.setCostoUsd(VistaUtilities.parseDouble(superficialUsdTextField.getText()));
                        fht.setCostoEquiv(fht.getCostoUsd() + fht.getCostoBs() / paridad.getValor());
                        break;
                    case Constantes.FASE_SLANT:
                        fht.setDias(VistaUtilities.parseDouble(slantTextField.getText()));
                        fht.setCostoBs(VistaUtilities.parseDouble(slantBsTextField.getText()));
                        fht.setCostoUsd(VistaUtilities.parseDouble(slantUsdTextField.getText()));
                        fht.setCostoEquiv(fht.getCostoUsd() + fht.getCostoBs() / paridad.getValor());
                        break;
                    case Constantes.FASE_PILOTO:
                        fht.setDias(VistaUtilities.parseDouble(pilotoTextField.getText()));
                        fht.setCostoBs(VistaUtilities.parseDouble(pilotoBsTextField.getText()));
                        fht.setCostoUsd(VistaUtilities.parseDouble(pilotoUsdTextField.getText()));
                        fht.setCostoEquiv(fht.getCostoUsd() + fht.getCostoBs() / paridad.getValor());
                        break;
                    case Constantes.FASE_VERTICAL:
                        fht.setDias(VistaUtilities.parseDouble(verticalTextField.getText()));
                        fht.setCostoBs(VistaUtilities.parseDouble(verticalBsTextField.getText()));
                        fht.setCostoUsd(VistaUtilities.parseDouble(verticalUsdTextField.getText()));
                        fht.setCostoEquiv(fht.getCostoUsd() + fht.getCostoBs() / paridad.getValor());
                        break;
                    case Constantes.FASE_INTERMEDIO:
                        fht.setDias(VistaUtilities.parseDouble(intermedioTextField.getText()));
                        fht.setCostoBs(VistaUtilities.parseDouble(intermedioBsTextField.getText()));
                        fht.setCostoUsd(VistaUtilities.parseDouble(intermedioUsdTextField.getText()));
                        fht.setCostoEquiv(fht.getCostoUsd() + fht.getCostoBs() / paridad.getValor());
                        break;
                    case Constantes.FASE_PRODUCTOR:
                        fht.setDias(VistaUtilities.parseDouble(productorTextField.getText()));
                        fht.setCostoBs(VistaUtilities.parseDouble(productorBsTextField.getText()));
                        fht.setCostoUsd(VistaUtilities.parseDouble(productorUsdTextField.getText()));
                        fht.setCostoEquiv(fht.getCostoUsd() + fht.getCostoBs() / paridad.getValor());
                        break;
                    case Constantes.FASE_COMPLETACION:
                        fht.setDias(VistaUtilities.parseDouble(completacionTextField.getText()));
                        fht.setCostoBs(VistaUtilities.parseDouble(completacionBsTextField.getText()));
                        fht.setCostoUsd(VistaUtilities.parseDouble(completacionUsdTextField.getText()));
                        fht.setCostoEquiv(fht.getCostoUsd() + fht.getCostoBs() / paridad.getValor());
                        break;
                    case Constantes.FASE_CONEXION:
                        fht.setDias(VistaUtilities.parseDouble(conexionTextField.getText()));
                        fht.setCostoBs(VistaUtilities.parseDouble(conexionBsTextField.getText()));
                        fht.setCostoUsd(VistaUtilities.parseDouble(conexionUsdTextField.getText()));
                        fht.setCostoEquiv(fht.getCostoUsd() + fht.getCostoBs() / paridad.getValor());
                        break;
                }
            }
            taladroSelected.setTaladroHasFaseCollection(faseTaladroList);
        } catch (ParseException ex) {
            SISMONLOG.logger.log(Level.SEVERE, "Error convirtiendo un dato", ex);
        }
    }

    private List<String[]> addNewTaladro() {
        List<String[]> data = new ArrayList<>();
        String[] campo = new String[32];
        if (!taladroNombreTextField.getText().isEmpty()) {
            campo[0] = taladroNombreTextField.getText();
        } else {
            Contexto.showMessage("Debe completar el campo 'Nombre de Taladro'", Constantes.MENSAJE_ERROR);
        }

        if (fechaDispInicialDateChooser.getDate() != null) {
            campo[1] = dFormat.format(fechaDispInicialDateChooser.getDate());
        } else {
            Contexto.showMessage("Debe completar el campo 'Fecha Disponibilidad'", Constantes.MENSAJE_ERROR);
        }
        if (!mudanzaMacollaTextField.getText().isEmpty()) {
            campo[2] = mudanzaMacollaTextField.getText();
        } else {
            campo[2] = String.valueOf(0.0);
        }
        if (!mudanzaMacollaBsTextField.getText().isEmpty()) {
            campo[12] = mudanzaMacollaBsTextField.getText();
        } else {
            campo[12] = String.valueOf(0.0);
        }
        if (!mudanzaMacollaUsdTextField.getText().isEmpty()) {
            campo[22] = mudanzaMacollaUsdTextField.getText();
        } else {
            campo[22] = String.valueOf(0.0);
        }

        if (!mudanzaPozoTextField.getText().isEmpty()) {
            campo[3] = mudanzaPozoTextField.getText();
        } else {
            campo[3] = String.valueOf(0.0);
        }
        if (!mudanzaPozoBsTextField.getText().isEmpty()) {
            campo[13] = mudanzaPozoBsTextField.getText();
        } else {
            campo[13] = String.valueOf(0.0);
        }
        if (!mudanzaPozoUsdTextField.getText().isEmpty()) {
            campo[23] = mudanzaPozoUsdTextField.getText();
        } else {
            campo[23] = String.valueOf(0.0);
        }

        if (!superficialTextField.getText().isEmpty()) {
            campo[4] = superficialTextField.getText();
        } else {
            campo[4] = String.valueOf(0.0);
        }
        if (!superficialBsTextField.getText().isEmpty()) {
            campo[14] = superficialBsTextField.getText();
        } else {
            campo[14] = String.valueOf(0.0);
        }
        if (!superficialUsdTextField.getText().isEmpty()) {
            campo[24] = superficialUsdTextField.getText();
        } else {
            campo[24] = String.valueOf(0.0);
        }

        if (!slantTextField.getText().isEmpty()) {
            campo[5] = slantTextField.getText();
        } else {
            campo[5] = String.valueOf(0.0);
        }
        if (!slantBsTextField.getText().isEmpty()) {
            campo[15] = slantBsTextField.getText();
        } else {
            campo[15] = String.valueOf(0.0);
        }
        if (!slantUsdTextField.getText().isEmpty()) {
            campo[25] = slantUsdTextField.getText();
        } else {
            campo[25] = String.valueOf(0.0);
        }

        if (!pilotoTextField.getText().isEmpty()) {
            campo[6] = pilotoTextField.getText();
        } else {
            campo[6] = String.valueOf(0.0);
        }
        if (!pilotoBsTextField.getText().isEmpty()) {
            campo[16] = pilotoBsTextField.getText();
        } else {
            campo[16] = String.valueOf(0.0);
        }
        if (!pilotoUsdTextField.getText().isEmpty()) {
            campo[26] = pilotoUsdTextField.getText();
        } else {
            campo[26] = String.valueOf(0.0);
        }

        if (!verticalTextField.getText().isEmpty()) {
            campo[7] = verticalTextField.getText();
        } else {
            campo[7] = String.valueOf(0.0);
        }
        if (!verticalBsTextField.getText().isEmpty()) {
            campo[17] = verticalBsTextField.getText();
        } else {
            campo[17] = String.valueOf(0.0);
        }
        if (!verticalUsdTextField.getText().isEmpty()) {
            campo[27] = verticalUsdTextField.getText();
        } else {
            campo[27] = String.valueOf(0.0);
        }

        if (!intermedioTextField.getText().isEmpty()) {
            campo[8] = intermedioTextField.getText();
        } else {
            campo[8] = String.valueOf(0.0);
        }
        if (!intermedioBsTextField.getText().isEmpty()) {
            campo[18] = intermedioBsTextField.getText();
        } else {
            campo[18] = String.valueOf(0.0);
        }
        if (!intermedioUsdTextField.getText().isEmpty()) {
            campo[28] = intermedioUsdTextField.getText();
        } else {
            campo[28] = String.valueOf(0.0);
        }

        if (!productorTextField.getText().isEmpty()) {
            campo[9] = productorTextField.getText();
        } else {
            campo[9] = String.valueOf(0.0);
        }
        if (!productorBsTextField.getText().isEmpty()) {
            campo[19] = productorBsTextField.getText();
        } else {
            campo[19] = String.valueOf(0.0);
        }
        if (!productorUsdTextField.getText().isEmpty()) {
            campo[29] = productorUsdTextField.getText();
        } else {
            campo[29] = String.valueOf(0.0);
        }

        if (!completacionTextField.getText().isEmpty()) {
            campo[10] = completacionTextField.getText();
        } else {
            campo[10] = String.valueOf(0.0);
        }
        if (!completacionBsTextField.getText().isEmpty()) {
            campo[20] = completacionBsTextField.getText();
        } else {
            campo[20] = String.valueOf(0.0);
        }
        if (!completacionUsdTextField.getText().isEmpty()) {
            campo[30] = completacionUsdTextField.getText();
        } else {
            campo[30] = String.valueOf(0.0);
        }

        if (!conexionTextField.getText().isEmpty()) {
            campo[11] = conexionTextField.getText();
        } else {
            campo[11] = String.valueOf(0.0);
        }
        if (!conexionBsTextField.getText().isEmpty()) {
            campo[21] = conexionBsTextField.getText();
        } else {
            campo[21] = String.valueOf(0.0);
        }
        if (!conexionUsdTextField.getText().isEmpty()) {
            campo[31] = conexionUsdTextField.getText();
        } else {
            campo[31] = String.valueOf(0.0);
        }

        data.add(campo);
        newTaladro = true;
        return data;
    }

    private void modify() {
        if (!taladroNombreTextField.getText().isEmpty() && (fechaDispInicialDateChooser.getDate() != null)) {
            newTaladro = true;
            guardarButton.setEnabled(true);
        }

        if (mainTabbedPane.getSelectedIndex() == 0 && mantenimientoDateChooser.getDate() != null
                && !diasMantTextField.getText().isEmpty() && taladroSelected != null) {
            try {
                Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
                Integer.parseInt(diasMantTextField.getText());
                newMantenimiento = true;
                if (!isEditingMantenimiento) {
                    agregarButton.setEnabled(true);
                }
                //guardarButton.setEnabled(true);
            } catch (NumberFormatException e) {
                Contexto.showMessage("'Duración en Dias' deber ser un valor entero", Constantes.MENSAJE_ERROR);
                diasMantTextField.grabFocus();
                agregarButton.setEnabled(false);
            }
        }

        if (mainTabbedPane.getSelectedIndex() == 1 && descontinuacionDateChooser.getDate() != null) {
            newDescontinuacion = true;
            guardarButton.setEnabled(false);
            eliminarButton.setText("Descontinuar");
            eliminarButton.setEnabled(true);
        }

        if ((macollasOriginalComboBox.getSelectedItem() instanceof Macolla)
                && (filasOriginalComboBox.getSelectedItem() instanceof Fila)
                && (macollasDestComboBox.getSelectedItem() instanceof Macolla)
                && (filasDestComboBox.getSelectedItem() instanceof Fila)) {
            moverButton.setEnabled(true);
        }
    }

    private void clearForm() {
        taladrosTable.clearSelection();
        taladroSelected = null;
//        escenarioComboBox.setSelectedIndex(0);
        archivoTextField.setText(null);
        taladroNombreTextField.setText(null);
        fechaDispInicialDateChooser.setDate(null);
        mudanzaMacollaTextField.setText(null);
        mudanzaMacollaBsTextField.setText(null);
        mudanzaMacollaUsdTextField.setText(null);
        mudanzaPozoTextField.setText(null);
        mudanzaPozoBsTextField.setText(null);
        mudanzaPozoUsdTextField.setText(null);
        superficialTextField.setText(null);
        superficialBsTextField.setText(null);
        superficialUsdTextField.setText(null);
        slantTextField.setText(null);
        slantBsTextField.setText(null);
        slantUsdTextField.setText(null);
        pilotoTextField.setText(null);
        pilotoBsTextField.setText(null);
        pilotoUsdTextField.setText(null);
        verticalTextField.setText(null);
        verticalBsTextField.setText(null);
        verticalUsdTextField.setText(null);
        intermedioTextField.setText(null);
        intermedioBsTextField.setText(null);
        intermedioUsdTextField.setText(null);
        productorTextField.setText(null);
        productorBsTextField.setText(null);
        productorUsdTextField.setText(null);
        completacionTextField.setText(null);
        completacionBsTextField.setText(null);
        completacionUsdTextField.setText(null);
        conexionTextField.setText(null);
        conexionBsTextField.setText(null);
        conexionUsdTextField.setText(null);
        guardarButton.setEnabled(false);
        archivoButton.setEnabled(false);
        agregarButton.setEnabled(false);
        mainTabbedPane.setSelectedIndex(0);
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);

        // relativo a los mantenimientos
        clearMantForm();

        // relativo a la descontinuación
        clearDescForm();

        // relativo al movimiento de taladros
        clearMoverForm();
    }

    private void fillMantenimientoTable() {
        String[] title = {"Id", "Fecha Mantenimiento", "Días"};
        List<TaladroMant> mantenimientos = mantManager.findAll(taladroSelected);
        Object[][] data = new Object[mantenimientos.size()][title.length];
        int i = 0;
        for (TaladroMant mant : mantenimientos) {
            data[i][0] = mant.getId();
            data[i][1] = dFormat.format(mant.getFecha());
            data[i++][2] = mant.getDias();
        }
        TableModel model = new DefaultTableModel(data, title);

        mantenimientoTable.setModel(model);
        mantenimientoTable.removeColumn(mantenimientoTable.getColumnModel().getColumn(0));
    }

    private void clearMantForm() {
        mantenimientoDateChooser.setDate(null);
        taladroSelectedLabel.setText(null);
        taladroSelectedLabel1.setText(null);
        diasMantTextField.setText(null);
        guardarButton.setEnabled(false);
        agregarButton.setEnabled(false);
        eliminarButton.setEnabled(false);
        newMantenimiento = false;
        updateMantenimiento = false;
        periodicoCheckBox.setSelected(false);
        vecesTextField.setText(null);
        periodicoPanel.setVisible(false);
        mantenimientoTable.setModel(new DefaultTableModel());
    }

    private void clearDescForm() {
        newDescontinuacion = false;
        descontinuacionDateChooser.setDate(null);
    }

    private void clearMoverForm() {
        taladroSelected = null;
        clearComboBox(macollasOriginalComboBox);
        clearComboBox(filasOriginalComboBox);
        clearComboBox(pozosOriginalComboBox);
        clearComboBox(fasesOriginalComboBox);

        clearComboBox(macollasDestComboBox);
        clearComboBox(filasDestComboBox);
        clearComboBox(pozosDestComboBox);
        clearComboBox(sustitutoComboBox);
        clearComboBox(fasesDestComboBox);

    }

    private void fillSustitutoComboBox() {
        sustitutoComboBox.removeAllItems();
        sustitutoComboBox.addItem("Seleccione...");
        List<Taladro> taladros = taladroManager.findAll(escenarioSelected);
        taladros.stream().filter(tal -> (!tal.equals(taladroSelected)))
                .forEach(tal -> {
                    sustitutoComboBox.addItem(tal);
                });
    }

    // Sección de MOVER de taladros
    private void fillMacollaOriginalComboBox() {
        if (escenarioSelected != null) {
            List<Macolla> macollaSet = fillMacollasSet();
            macollasOriginalComboBox.removeAllItems();
            macollasOriginalComboBox.addItem(OPCION_SELECIONE);
            macollaSet.stream().forEach(mac -> {
                macollasOriginalComboBox.addItem(mac);
            });
        }
    }

    private void fillFilasOriginalComboBox() {
        Macolla macolla = (Macolla) macollasOriginalComboBox.getSelectedItem();
        Set<Fila> filaSet = fillFilasSet(macolla);
        filasOriginalComboBox.removeAllItems();
        filasOriginalComboBox.addItem(OPCION_SELECIONE);
        filaSet.stream().forEach(fil -> {
            filasOriginalComboBox.addItem(fil);
        });
    }

    private void fillPozosOriginalComboBox() {
        Fila fila = (Fila) filasOriginalComboBox.getSelectedItem();
        LinkedHashSet<Pozo> pozoSet = fillPozosSet(fila);

        pozosOriginalComboBox.removeAllItems();
        pozosOriginalComboBox.addItem(OPCION_SELECIONE);
        pozoSet.stream().forEach(pz -> {
            pozosOriginalComboBox.addItem(pz);
        });

    }

    private void fillFaseOriginalComboBox() {
        Pozo pozo = (Pozo) pozosOriginalComboBox.getSelectedItem();
        List<Perforacion> perforaciones = perforacionManager
                .findAll(escenarioSelected, pozo);
        fasesOriginalComboBox.removeAllItems();
        fasesOriginalComboBox.addItem(OPCION_SELECIONE);
        Set<String> perfFase = new HashSet<>();
        perforaciones.stream()
                .filter(perf -> (!perf.getFase().equals(Constantes.FASE_COMPLETACION)
                && !perf.getFase().equals(Constantes.FASE_CONEXION)
                && !perf.getFase().equals(Constantes.FASE_EVALUACION)))
                .forEach(perf -> {
                    perfFase.add(perf.getFase());
                });
        perfFase.stream()
                .forEach((String fa) -> {
                    fasesOriginalComboBox.addItem(fa);
                });
    }

    private void fillMacollaDestinoComboBox() {
        if (escenarioSelected != null) {
            Set<Macolla> macollaSet = perforacionList.stream()
                    .map(per -> per.getMacollaId())
                    .collect(Collectors.toSet());
            macollasDestComboBox.removeAllItems();
            macollasDestComboBox.addItem(OPCION_SELECIONE);
            macollaSet.stream().forEach(mac -> {
                macollasDestComboBox.addItem(mac);
            });
        }
    }

    private void fillFilaDestinoComboBox() {
        Macolla macolla = (Macolla) macollasDestComboBox.getSelectedItem();
        List<Fila> filas = filaManager.findAll(macolla);
        Set<Fila> filaSet = new HashSet(filas);
        filasDestComboBox.removeAllItems();
        filasDestComboBox.addItem(OPCION_SELECIONE);
        filaSet.stream().forEach(fil -> {
            filasDestComboBox.addItem(fil);
        });
    }

    private void fillPozoDestinoComboBox() {
        Fila fila = (Fila) filasDestComboBox.getSelectedItem();
        List<Pozo> pozos = pozoManager.findAll(fila, escenarioSelected);
        List<Pozo> pozosSorted = pozos.stream()
                .sorted((p1, p2) -> p1.getNumero().compareTo(p2.getNumero()))
                .collect(Collectors.toList());
        pozosDestComboBox.removeAllItems();
        pozosDestComboBox.addItem(OPCION_SELECIONE);
        pozosSorted.forEach(pz -> {
            pozosDestComboBox.addItem(pz);
        });
    }

    private void fillFaceDestinoComboBox() {
        fasesDestComboBox.removeAllItems();
        fasesDestComboBox.addItem(OPCION_SELECIONE);
        fasesDestComboBox.addItem(Constantes.FASE_SUPERFICIAL);
        fasesDestComboBox.addItem(Constantes.FASE_SLANT);
        fasesDestComboBox.addItem(Constantes.FASE_INTERMEDIO);
        fasesDestComboBox.addItem(Constantes.FASE_PRODUCTOR);
    }

    private void clearComboBox(JComboBox combo) {
        combo.removeAllItems();
        combo.addItem("... seleccione");
    }

    private void fillAsignaTaladrosTable() {
        asignaTaladrosTable.setModel(new DefaultTableModel());
        String[] titles = {"Id", "Nombre", "Macolla", "Fila", "Pozo Inicial", "Fase Inicial",
            "Pozo Salida", "Fase Salida"};
        List<TaladroAsignado> taList = taladroAsignadoManager.findAll(escenarioSelected);
        taList.sort((TaladroAsignado o1, TaladroAsignado o2)
                -> o1.getTaladroId().getId().compareTo(o2.getTaladroId().getId()));
        Object[][] datos = new Object[taList.size()][titles.length];
        int i = 0;
        for (TaladroAsignado ta : taList) {
            datos[i][0] = ta.getId();
            datos[i][1] = ta.getTaladroId();
            datos[i][2] = ta.getFilaId().getMacollaId().getNombre();
            datos[i][3] = ta.getFilaId().getNombre();
            datos[i][4] = ta.getPozoInId().getUbicacion();
            datos[i][5] = ta.getFaseIn();
            datos[i][6] = ta.getPozoOutId();
            datos[i][7] = ta.getFaseOut();
            i++;
        }
        TableModel model = new DefaultTableModel(datos, titles);
        asignaTaladrosTable.setModel(model);
        asignaTaladrosTable.removeColumn(asignaTaladrosTable.getColumnModel().getColumn(0));
    }

    // con este método se elimina el botón "cargar"
    private void cargaArchivo() {
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        List<Paridad> paridades = paridadManager.findAll();
        if (paridades.isEmpty()) {
            Contexto.showMessage("No puede configurar los taladros,"
                    + "sin tener previamente \n la paridad cambiaria configurada", Constantes.MENSAJE_ERROR);
            return;
        }

        try {
            List<String[]> data = new ArrayList<>();

            String[] encabezado = {"Taladro", "F Disponible", "Mdz Macolla", "Mdz Pozo", "Superficial", "Slant", "Piloto",
                "Vertical", "Intermedi", "Productor", "Completación", "Conexión",
                "Bs Mdz Macolla", "Bs Mdz Pozo", "Bs Superficial", "Bs Slant", "Bs Piloto",
                "BsVertical", "Bs Intermedio", "Bs Productor", "Bs Completación", "Bs Conexión", "Bs Evaluacion",
                "US$ Mdz Macolla", "US$ Mdz Pozo", "US$ Superficial", "US$ Slant", "US$ Piloto",
                "US$Vertical", "US$ Intermedi", "US$ Productor", "US$ Completación", "US$ Conexión", "US$ Evaluacion"};

            BufferedReader input = new BufferedReader(new FileReader(archivo));
            String linea;
            int counter = 0;
            try {

                while ((linea = input.readLine()) != null) {
                    String[] columns = linea.split(";");
                    if (columns.length != 0) {
                        switch (counter) {
                            case 0:
                            case 1:
                                break;
                            default:
                                int columna = 0;
                                String[] dataRead = new String[encabezado.length];
                                for (String dato : columns) {
                                    switch (columna) {
                                        case 0:
                                        case 1:
                                            if (dato == null || dato.trim().isEmpty()) {
                                                dataRead[columna] = "";
                                            } else {
                                                dataRead[columna] = dato;
                                            }
                                            break;
                                        default:
                                            if (dato == null || dato.trim().isEmpty()) {
                                                dataRead[columna] = "0";
                                            } else if (!dato.equalsIgnoreCase("f")) {
                                                dataRead[columna] = dato;
                                            }
                                            break;
                                    }
                                    columna++;
                                }

                                data.add(dataRead);
                                break;
                        }
                        counter++;
                    } else {
                        Contexto.showMessage((counter - 2) + " registros de taladros leidos.", Constantes.MENSAJE_INFO);
                    }
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                SISMONLOG.logger.log(Level.SEVERE, "Intenta cargar un archivo incorrecto", e);
                Contexto.showMessage("Intenta cargar un archivo incorrecto", Constantes.MENSAJE_ERROR);
            }
            // pasa a rellenar la tabla de macollas y pozos

            if (!data.isEmpty()) {
                makeNewTaladros(data);
                isnew = true;
            }
        } catch (Exception e) {
            SISMONLOG.logger.log(Level.SEVERE, "Error convirtiendo datos", e);
        }
    }

    private void checkJTValue() {
        isEditable = true;
        guardarButton.setEnabled(true);
    }

    private boolean isValidDateModification(Date fecha) {
        if (escenarioSelected.getFechaCierre() != null) {
            return fecha.after(escenarioSelected.getFechaCierre());
        } else {
            return true;
        }
    }

    private void showCierreWarning() {
        String fecha = dFormat.format(escenarioSelected.getFechaCierre());
        JOptionPane.showMessageDialog(this,
                "No puede hacer cambios en este escenario antes de esta fecha: " + fecha,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void fillMacollaDescComboBox() {
        if (escenarioSelected != null) {
            List<Macolla> macollaSet = fillMacollasSet();
            macollaDescComboBox.removeAllItems();
            macollaDescComboBox.addItem(OPCION_SELECIONE);
            macollaSet.stream().forEach(mac -> {
                macollaDescComboBox.addItem(mac);
            });
        }
    }

    private void fillFilasDescComboBox() {
        Macolla macolla = (Macolla) macollaDescComboBox.getSelectedItem();
        Set<Fila> filaSet = fillFilasSet(macolla);
        filaDescComboBox.removeAllItems();
        filaDescComboBox.addItem(OPCION_SELECIONE);
        filaSet.stream().forEach(fil -> {
            filaDescComboBox.addItem(fil);
        });
    }

    private void fillPozoDescComboBox() {
        Fila fila = (Fila) filaDescComboBox.getSelectedItem();
        LinkedHashSet pozoSortedSet = fillPozosSet(fila);

        pozoDescComboBox.removeAllItems();
        pozoDescComboBox.addItem(OPCION_SELECIONE);
        pozoSortedSet.stream().forEach(pz -> {
            pozoDescComboBox.addItem(pz);
        });
    }

    private void fillFaseDescComboBox() {
        Pozo pozo = (Pozo) pozoDescComboBox.getSelectedItem();
        List<Perforacion> perforaciones = perforacionManager
                .findAll(escenarioSelected, pozo);
        faseDescComboBox.removeAllItems();
        faseDescComboBox.addItem(OPCION_SELECIONE);
        perforaciones.stream()
                .filter(perf -> (!perf.getFase().equals(Constantes.FASE_COMPLETACION)
                && !perf.getFase().equals(Constantes.FASE_CONEXION)
                && !perf.getFase().equals(Constantes.FASE_EVALUACION)))
                .forEach(perf -> {
                    faseDescComboBox.addItem(perf.getFase());
                });
    }

    private void clearDescComboBoxes() {
        macollaDescComboBox.removeAllItems();
        filaDescComboBox.removeAllItems();
        pozoDescComboBox.removeAllItems();
        faseDescComboBox.removeAllItems();
    }

    private List<Macolla> fillMacollasSet() {
        List<Perforacion> perforaciones = perforacionManager
                .findAll(escenarioSelected, taladroSelected);
        List<Macolla> macollas = new ArrayList();
        for (Perforacion perf : perforaciones) {
            if (!macollas.contains(perf.getMacollaId())) {
                macollas.add(perf.getMacollaId());
            }
        }

        Collections.sort(macollas, (Macolla m1, Macolla m2) -> {
            return m1.getNumero().compareTo(m2.getNumero());
        });

        return macollas;
    }

    private Set<Fila> fillFilasSet(Macolla macolla) {
        List<Perforacion> perforaciones = perforacionManager
                .findAll(escenarioSelected, macolla, taladroSelected);
        return perforaciones.stream()
                .map(per -> per.getFilaId())
                .collect(Collectors.toSet());
    }

    private LinkedHashSet fillPozosSet(Fila fila) {
        List<Perforacion> perforaciones = perforacionManager
                .findAll(escenarioSelected, fila, taladroSelected);
        List<Pozo> pozoList = perforaciones.stream()
                .map(per -> per.getPozoId())
                .sorted((Pozo o1, Pozo o2) -> o1.getNumero().compareTo(o2.getNumero()))
                .collect(Collectors.toList());

        return new LinkedHashSet(pozoList);
    }

    private void checkCambioTaladro() {
        if (fasesOriginalComboBox.getSelectedItem() != null
                && !fasesOriginalComboBox.getSelectedItem().equals(OPCION_SELECIONE)
                && fasesDestComboBox.getSelectedItem() != null
                && !fasesDestComboBox.getSelectedItem().equals(OPCION_SELECIONE)) {
            moverButton.setEnabled(true);
        } else {
            moverButton.setEnabled(false);
        }
    }

    private void fillQuitarTable(List<TaladroAsignado> talAsigList) {
        quitarTable.setModel(new DefaultTableModel());
        String[] titles = {"Taladro", "Macolla", "Fila"};
        Object[][] datos = new Object[talAsigList.size()][titles.length];
        int i = 0;
        for (TaladroAsignado ta : talAsigList) {
            datos[i][0] = ta.getTaladroId().getNombre();
            datos[i][1] = ta.getFilaId().getMacollaId().getNombre();
            datos[i][2] = ta.getFilaId().getNombre();
            i++;
        }
        DefaultTableModel model = new DefaultTableModel(datos, titles);
        quitarTable.setModel(model);
    }

    private void eliminarMovimientoTaladro() {
        // busca la traza del taladro al que se le va a eliminar el movimiento
        List<TaladroTraza> ttList = taladroTrazaManager
                .find(taEliminar.getTaladroId(), escenarioSelected);
        // identifica la traza particular de esa eliminación        
        TaladroTraza tt = taladroTrazaManager.find(taEliminar, escenarioSelected);
        // verificar que sea la última traza de ese movimiento
        if (tt != null && Objects.equals(tt, ttList.get(ttList.size() - 1))) {
            // se busca la traza anterior
            TaladroTraza ttAnterior = ttList.get(ttList.size() - 2);
            // se verifica que es la última secuencia en la fila
            Long secId = ttAnterior.getPozoSecuenciaOrigenId();
            PozoSecuencia sec = pozoSecuenciaManager.find(secId);
            Pozo pozo = sec.getPozoId();
            Fila fila = pozo.getFilaId();
            List<PozoSecuencia> secList = pozoSecuenciaManager.find(fila, escenarioSelected);
            // ordena la busqueda en orden decreciente
            secList.sort((PozoSecuencia ps1, PozoSecuencia ps2)
                    -> ps2.getSecuencia().compareTo(ps1.getSecuencia()));
            if (!sec.equals(secList.get(0))) {
                // si no es la última hay que actualizar el taladroAsignado anterior
                // con los cambios realizados anteriormente
                TaladroAsignado taAnterior = taladroAsignadoManager.find(tt.getTaladroAsignadoDestinoId());
                PozoSecuencia psecOrigen = pozoSecuenciaManager.find(tt.getPozoSecuenciaOrigenId());
                Pozo pozoOrigen = pozoManager.find(tt.getPozoOutOrigenId());
                String faseOrigen = tt.getFaseOutOrigen();

                taAnterior.setPozoOutId(pozoOrigen);
                taAnterior.setPozoSecuenciaOutId(psecOrigen);
                taAnterior.setFaseOut(faseOrigen);

                taladroAsignadoManager.edit(taAnterior);
            }
            // se elimina la traza de ese movimiento
            taladroTrazaManager.remove(tt);
            // se elimina el movimiento
            taladroAsignadoManager.remove(taEliminar);
            SISMONLOG.logger.log(Level.INFO, "Movimiento de Taladro {0}"
                    + " eliminado con éxito",
                    taEliminar.getTaladroId().getNombre());
            Contexto.showMessage("Movimiento de Taladro " + taEliminar.getTaladroId().getNombre()
                    + " eliminado con éxito",
                    Constantes.MENSAJE_INFO);
            fillAsignaTaladrosTable();
        } else {
            // se emite un mensaje que indica que no puede ser eliminada esa traza ya 
            // que hay movimientos intermedios
            SISMONLOG.logger.log(Level.WARNING, "Este movimiento no es el último realizado, "
                    + "del taladro {0}, debe eliminar los movimientos posteriores primero.",
                    taEliminar.getTaladroId());
            Contexto.showMessage("Este movimiento no es el último realizado, "
                    + "debe eliminar los movimientos posteriores primero",
                    Constantes.MENSAJE_ERROR);
            eliminarButton.setEnabled(false);
        }

    }

    private void fillTaladrosNuevosTable() {
        List<Taladro> taladros = taladroManager.findAll(escenarioSelected);
        List<TaladroStatus> talStatusList = talStatusManager.findAll();
        Map<Taladro, List<TaladroStatus>> talStatusMap = new HashMap<>();
        List<Taladro> taladrosNoAsignados = new ArrayList<>();
        for (TaladroStatus ts : talStatusList) {
            List<TaladroStatus> tsList = talStatusMap.get(ts.getTaladroId());
            if (tsList == null) {
                tsList = new ArrayList<>();
            }
            tsList.add(ts);
            talStatusMap.put(ts.getTaladroId(), tsList);
        }

        // se buscan los taladros nuevos que no tienen status asignado
        for (Taladro taladro : taladros) {
            if (!talStatusMap.containsKey(taladro)) {
                taladrosNoAsignados.add(taladro);
            }
        }

        String[] titles = {"Id", "Taladro", "Fecha Disponibilidad"};
        Object[][] datos = new Object[taladrosNoAsignados.size()][titles.length];

        int i = 0;
        for (Taladro taladro : taladrosNoAsignados) {
            datos[i][0] = taladro.getId();
            datos[i][1] = taladro.getNombre();
            datos[i][2] = dFormat.format(taladro.getFechaInicial());
            i++;
        }

        TableModel model = new DefaultTableModel(datos, titles);
        taladrosNuevosTable.setModel(model);
        taladrosNuevosTable.getColumnModel().removeColumn(taladrosNuevosTable.getColumn("Id"));
    }

    private void fillAsignaMacollaComboBox() {
        asignaMacollaComboBox.removeAllItems();
        asignaMacollaComboBox.addItem(OPCION_SELECIONE);
        List<Macolla> macollas = macollaManager.findAll();
        macollas.stream()
                .forEach(mac -> {
                    asignaMacollaComboBox.addItem(mac);
                });
    }

    private void fillAsignaFilaComboBox(Macolla macolla) {
        asignaFilaComboBox.removeAllItems();
        asignaFilaComboBox.addItem(OPCION_SELECIONE);
        List<Fila> filas = filaManager.findAll(macolla);
        filas.stream()
                .forEach(fi -> {
                    asignaFilaComboBox.addItem(fi);
                });
    }
    
    private void fillAsignaPozoComboBox(Fila fila) {
        asignaPozoComboBox.removeAllItems();
        asignaPozoComboBox.addItem(OPCION_SELECIONE);
        List<Pozo> pozos = pozoManager.findAll(fila, escenarioSelected);
        pozos.stream()
                .forEach(po -> {
                    asignaPozoComboBox.addItem(po);
                });
    }
    
    private void crearAsignacionTaladroNuevo(Taladro taladroNuevo) {
        TaladroStatus ts = new TaladroStatus();
        ts.setTaladroId(taladroNuevo);
        ts.setFechaIn(taladroNuevo.getFechaInicial());
        ts.setNombre(Constantes.TALADRO_STATUS_OCUPADO);
        ts.setStatus(Constantes.TALADRO_STATUS_ACTIVO);

        talStatusManager.create(ts);

        Fila filaIn = (Fila) asignaFilaComboBox.getSelectedItem();
        Pozo pozoIn = (Pozo) asignaPozoComboBox.getSelectedItem();
        List<PozoSecuencia> secList = pozoSecuenciaManager
                .find(filaIn, escenarioSelected);
        // ordena la busqueda en orden decreciente
        secList.sort((PozoSecuencia ps1, PozoSecuencia ps2)
                -> ps2.getSecuencia().compareTo(ps1.getSecuencia()));
        // calcular la fase de salida automática de esta fila
        PozoSecuencia ultimaSec = secList.get(0);

        // se busca la secuencia de entrada de este taladro
        PozoSecuencia secIn = pozoSecuenciaManager
                .find(escenarioSelected, pozoIn, asignarFaseTextField.getText());

        // se crea el objecto TaladroAsignado para la tabla
        TaladroAsignado ta = new TaladroAsignado();
        ta.setEscenarioId(escenarioSelected);
        ta.setFilaId(secIn.getFilaId());
        ta.setTaladroId(taladroNuevo);
        
        // la entrada del taladro
        ta.setFaseIn(secIn.getFase());
        ta.setPozoInId(secIn.getPozoId());
        ta.setPozoSecuenciaInId(secIn);
        // la salida del taladro
        ta.setFaseOut(ultimaSec.getFase());
        ta.setPozoOutId(ultimaSec.getPozoId());
        ta.setPozoSecuenciaOutId(ultimaSec);

        // aqui se almacena el taladro_Asignado en la BD
        taladroAsignadoManager.create(ta);

        // Se crea la Traza del taladro
        TaladroTraza tt = new TaladroTraza();
        tt.setEscenarioId(escenarioSelected.getId());
        tt.setOrden(1); // por ser la primera entrada del taladro en la traza
        tt.setTaladroId(taladroNuevo.getId());
        tt.setTaladroAsignadoOrigenId(ta.getId());
        tt.setPozoOutOrigenId(ultimaSec.getPozoId().getId());
        tt.setFaseOutOrigen(ultimaSec.getFase());
        tt.setPozoSecuenciaOrigenId(ultimaSec.getId());
        tt.setTaladroStatusFinalId(ts.getId());

        //aqui se almacena la traza del taladro
        taladroTrazaManager.create(tt);

        Contexto.showMessage("Asignación de nuevo taladro realizado con éxito",
                Constantes.MENSAJE_INFO);
        taladrosNuevosTable.clearSelection();
        fillTaladrosNuevosTable();
        asignaMacollaComboBox.removeAllItems();
        asignaMacollaComboBox.setEnabled(false);
        loadTaladrosMap();
        fillTaladrosTable();
    }

    private void cleanTaladrosLabel() {
        taladroSelectedLabel.setText(null);
        taladroSelectedLabel1.setText(null);
        taladroSelectedMoverLabel.setText(null);
        quitarTaladroLabel.setText(null);
    }

    private void fillTaladrosDescontinuadosTable() {
        List<Taladro> taladros = taladroManager.findAll(escenarioSelected);
        List<TaladroStatus> descontinuados = new ArrayList<>();
        for (Taladro tal : taladros) {
            List<TaladroStatus> tsList = talStatusManager.find(tal);
            if (tsList.size() > 1) {
                for (TaladroStatus ts : tsList) {
                    if (ts.getStatus() == Constantes.TALADRO_STATUS_ACTIVO
                            && ts.getNombre().equals(Constantes.TALADRO_STATUS_DESCONTINUADO)) {
                        descontinuados.add(ts);
                        break;
                    }
                }
            }

        }
        if (descontinuados.size() > 0) {
            String[] titles = {"TStatus", "Taladro", "Fecha Descontinuación"};
            Object[][] datos = new Object[descontinuados.size()][titles.length];
            int j = 0;
            for (TaladroStatus ts : descontinuados) {
                datos[j][0] = ts;
                datos[j][1] = ts.getTaladroId().getNombre();
                datos[j][2] = dFormat.format(ts.getFechaIn());
                j++;
            }
            TableModel model = new DefaultTableModel(datos, titles);
            taladrosDescontinuadosTable.setModel(model);
            taladrosDescontinuadosTable.removeColumn(
                    taladrosDescontinuadosTable.getColumnModel().getColumn(0));
        }
    }

    private void clearTaladrosDescontinuadosTable() {
        taladrosDescontinuadosTable.setModel(new DefaultTableModel());
    }

    private void reincorporarTaladro(TaladroStatus ts) {
        String mensaje = "¿Realmente desea reincorporar el taladro "
                + ts.getTaladroId().getNombre() + "?";
        int respuesta = JOptionPane.showConfirmDialog(this, mensaje,
                "Advertencia", JOptionPane.WARNING_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            TaladroStatus tsActual = talStatusManager.find(ts.getTaladroId(),
                    Constantes.TALADRO_STATUS_DESCONTINUADO);
            Taladro taladro = ts.getTaladroId();
            // se busca el TaladroAsignado de este taladro
            List<TaladroAsignado> talAsigList = taladroAsignadoManager
                    .findRutaTaladro(escenarioSelected, taladro);
            Fila fila = null;
            TaladroAsignado ta = null;
            if (talAsigList.size() > 0) {
                if (talAsigList.size() == 1) {
                    ta = talAsigList.get(0);
                } else {
                    // hay mas de un movimiento de este taladro, hay que localizar
                    // el último, ya viene ordenado, de manera que el último
                    // movimiento será el ultimo registro de la lista
                    ta = talAsigList.get(talAsigList.size() - 1);
                }
            }
            // se busca otros taladros en la fila asignada
            if (ta != null) {
                fila = ta.getFilaId();
            }

            // si se encuentran taladro en la fila se adviete
            if (fila != null) {
                List<TaladroAsignado> talAsigXFilaList = taladroAsignadoManager
                        .findAll(escenarioSelected, fila);
                if (talAsigXFilaList.size() > 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html>");
                    sb.append("Existe(n) taladro(s) asignado(s) en la fila en que fue descontinuado");
                    sb.append("<br>");
                    sb.append("que deben ser removidos, para poder reasignar el taladro.").append("<br>");
                    sb.append("Proceda a eliminar ese(esos) movimiento(s) antes");
                    sb.append("</html>");
                    JOptionPane.showMessageDialog(this, sb.toString(), "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                    taladrosDescontinuadosTable.clearSelection();
                    reincorporarButton.setEnabled(false);
                    return;
                }
            }

            TaladroStatus tsAnterior = talStatusManager.find(ts.getTaladroId(),
                    Constantes.TALADRO_STATUS_OCUPADO);
            tsAnterior.setFechaOut(null);
            tsAnterior.setStatus(Constantes.TALADRO_STATUS_ACTIVO);
            talStatusManager.remove(tsActual);
            talStatusManager.edit(tsAnterior);

            // se modifica el taladro asignado para que el taladro reasignado
            // permanezca en la fila
            if (fila != null) {
                List<PozoSecuencia> secList = pozoSecuenciaManager
                        .find(fila, escenarioSelected);
                int last = 0;
                PozoSecuencia secLast = null;
                for (PozoSecuencia ps : secList) {
                    if (ps.getSecuencia() > last) {
                        last = ps.getSecuencia();
                        secLast = ps;
                    }
                }

                if (secLast != null) {
                    ta.setFaseOut(secLast.getFase());
                    ta.setPozoOutId(secLast.getPozoId());
                    ta.setPozoSecuenciaOutId(secLast);
                    taladroAsignadoManager.edit(ta);

                    Contexto.showMessage("Taladro reasignado con éxito, recuerde "
                            + "ejecutar la perforación nuevamente", Constantes.MENSAJE_INFO);
                    fillTaladrosDescontinuadosTable();
                    fillTaladrosTable();
                } else {
                    Contexto.showMessage("No se encontró un secuencia de perforación "
                            + "para reestablecer", Constantes.MENSAJE_ERROR);
                    reincorporarButton.setEnabled(false);
                    return;
                }
            } else {
                Contexto.showMessage("No se pudo determinar la fila en que debe "
                        + "reincorporar este taladro", Constantes.MENSAJE_ERROR);
                return;
            }

        } else {
            taladrosDescontinuadosTable.clearSelection();
        }
        reincorporarButton.setEnabled(false);
        taladrosDescontinuadosTable.clearSelection();
    }
    
    private void findNextSequence(){
        List<String> fasesPerforadas = new ArrayList<>();
        // se busca el pozo seleccionado en el DropBox
        Pozo pozoSelected = (Pozo) asignaPozoComboBox.getSelectedItem();
        // se obtiene las secuencias programadas de este pozo
        List<PozoSecuencia> secuenciaList = pozoSecuenciaManager.findAllByPozo(pozoSelected);
        secuenciaList.sort((ps1, ps2) -> ps1.getSecuencia().compareTo(ps2.getSecuencia()));
        
        // se obtiene las secuencias perforadas de este pozo
        List<Perforacion> perfList = perforacionManager.findAll(escenarioSelected, pozoSelected);
        perfList.forEach(perf -> {
            fasesPerforadas.add(perf.getFase());
        });
        
        PozoSecuencia nextSecuence = null;
        
        for(PozoSecuencia ps : secuenciaList){
            if(fasesPerforadas.contains(ps.getFase())){
                continue;
            } else {
                nextSecuence = ps;
                break;
            }
        }
        
        asignarFaseTextField.setText(nextSecuence.getFase());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        frecButtonGroup = new javax.swing.ButtonGroup();
        backPanel = new javax.swing.JPanel();
        mainTabbedPane = new javax.swing.JTabbedPane();
        mantenimientoPanel = new javax.swing.JPanel();
        taladroSelectedLabel = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        mantenimientoDateChooser = new com.toedter.calendar.JDateChooser();
        diasMantTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        mantenimientoTable = new javax.swing.JTable();
        periodicoCheckBox = new javax.swing.JCheckBox();
        periodicoPanel = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        mensualRadioButton = new javax.swing.JRadioButton();
        trimestralRadioButton = new javax.swing.JRadioButton();
        semestralRadioButton = new javax.swing.JRadioButton();
        anualRadioButton = new javax.swing.JRadioButton();
        jLabel36 = new javax.swing.JLabel();
        vecesTextField = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        decontinuarPanel = new javax.swing.JPanel();
        taladroSelectedLabel1 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        descontinuacionDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel39 = new javax.swing.JLabel();
        sustitutoComboBox = new javax.swing.JComboBox();
        jLabel54 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        macollaDescComboBox = new javax.swing.JComboBox<Object>();
        jLabel55 = new javax.swing.JLabel();
        filaDescComboBox = new javax.swing.JComboBox<>();
        jLabel56 = new javax.swing.JLabel();
        pozoDescComboBox = new javax.swing.JComboBox<>();
        jLabel57 = new javax.swing.JLabel();
        faseDescComboBox = new javax.swing.JComboBox<>();
        jLabel58 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        taladrosDescontinuadosTable = new javax.swing.JTable();
        jLabel65 = new javax.swing.JLabel();
        reincorporarButton = new javax.swing.JButton();
        moverPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        asignaTaladrosTable = new javax.swing.JTable();
        taladroSelectedMoverLabel = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        macollasOriginalComboBox = new javax.swing.JComboBox<>();
        jLabel59 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        filasOriginalComboBox = new javax.swing.JComboBox<>();
        jLabel43 = new javax.swing.JLabel();
        pozosOriginalComboBox = new javax.swing.JComboBox<>();
        jLabel44 = new javax.swing.JLabel();
        fasesOriginalComboBox = new javax.swing.JComboBox<>();
        jLabel45 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        macollasDestComboBox = new javax.swing.JComboBox<>();
        jLabel50 = new javax.swing.JLabel();
        filasDestComboBox = new javax.swing.JComboBox<>();
        jLabel51 = new javax.swing.JLabel();
        pozosDestComboBox = new javax.swing.JComboBox<>();
        jLabel52 = new javax.swing.JLabel();
        fasesDestComboBox = new javax.swing.JComboBox<>();
        AgregarPanel = new javax.swing.JPanel();
        archivoPanel = new javax.swing.JPanel();
        archivoTextField = new javax.swing.JTextField();
        manualPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        taladroNombreTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        fechaDispInicialDateChooser = new com.toedter.calendar.JDateChooser();
        taladroTabbedPane = new javax.swing.JTabbedPane();
        diasPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        mudanzaMacollaTextField = new javax.swing.JTextField();
        mudanzaPozoTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        superficialTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        slantTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        pilotoTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        verticalTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        intermedioTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        productorTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        completacionTextField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        conexionTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        mudanzaMacollaBsTextField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        mudanzaPozoBsTextField = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        superficialBsTextField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        slantBsTextField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        pilotoBsTextField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        verticalBsTextField = new javax.swing.JTextField();
        conexionBsTextField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        completacionBsTextField = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        productorBsTextField = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        intermedioBsTextField = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        mudanzaMacollaUsdTextField = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        mudanzaPozoUsdTextField = new javax.swing.JTextField();
        verticalUsdTextField = new javax.swing.JTextField();
        conexionUsdTextField = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        completacionUsdTextField = new javax.swing.JTextField();
        pilotoUsdTextField = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        productorUsdTextField = new javax.swing.JTextField();
        slantUsdTextField = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        superficialUsdTextField = new javax.swing.JTextField();
        intermedioUsdTextField = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        quitarPanel = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        quitarTaladroLabel = new javax.swing.JLabel();
        quitarMsgLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        quitarTable = new javax.swing.JTable();
        quitarTaladroButton = new javax.swing.JButton();
        asignaciónNuevosPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        taladrosNuevosTable = new javax.swing.JTable();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        asignaMacollaComboBox = new javax.swing.JComboBox<>();
        jLabel62 = new javax.swing.JLabel();
        asignaFilaComboBox = new javax.swing.JComboBox<>();
        jLabel63 = new javax.swing.JLabel();
        asignaPozoComboBox = new javax.swing.JComboBox<>();
        jLabel64 = new javax.swing.JLabel();
        asignarNuevoButton = new javax.swing.JButton();
        jLabel66 = new javax.swing.JLabel();
        asignarFaseTextField = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        taladrosTable = new javax.swing.JTable();
        toolBar = new javax.swing.JToolBar();
        guardarButton = new javax.swing.JButton();
        archivoButton = new javax.swing.JButton();
        agregarButton = new javax.swing.JButton();
        eliminarButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        moverButton = new javax.swing.JButton();
        toolBarPanel = new javax.swing.JPanel();
        escenarioComboBox = new javax.swing.JComboBox();

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

        mainTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mainTabbedPaneStateChanged(evt);
            }
        });

        mantenimientoPanel.setBackground(new java.awt.Color(255, 255, 255));

        taladroSelectedLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel33.setText("Fecha de Mantenimiento:");

        jLabel34.setText("Número de días:");

        mantenimientoTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha Mantenimiiento", "Días"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        mantenimientoTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mantenimientoTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(mantenimientoTable);

        periodicoCheckBox.setText("Repetir Mantenimiento");
        periodicoCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        periodicoCheckBox.setOpaque(false);
        periodicoCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                periodicoCheckBoxActionPerformed(evt);
            }
        });

        jLabel35.setText("Frecuencia:");

        frecButtonGroup.add(mensualRadioButton);
        mensualRadioButton.setText("mensual");

        frecButtonGroup.add(trimestralRadioButton);
        trimestralRadioButton.setText("trimestral");

        frecButtonGroup.add(semestralRadioButton);
        semestralRadioButton.setText("semestral");

        frecButtonGroup.add(anualRadioButton);
        anualRadioButton.setText("anual");

        jLabel36.setText("Repetir por:");

        jLabel37.setText("veces");

        javax.swing.GroupLayout periodicoPanelLayout = new javax.swing.GroupLayout(periodicoPanel);
        periodicoPanel.setLayout(periodicoPanelLayout);
        periodicoPanelLayout.setHorizontalGroup(
            periodicoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(periodicoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35)
                .addGap(18, 18, 18)
                .addGroup(periodicoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(periodicoPanelLayout.createSequentialGroup()
                        .addGroup(periodicoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(trimestralRadioButton)
                            .addComponent(mensualRadioButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vecesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel37))
                    .addGroup(periodicoPanelLayout.createSequentialGroup()
                        .addGroup(periodicoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(anualRadioButton)
                            .addComponent(semestralRadioButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        periodicoPanelLayout.setVerticalGroup(
            periodicoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(periodicoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(periodicoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(periodicoPanelLayout.createSequentialGroup()
                        .addGroup(periodicoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel35)
                            .addComponent(mensualRadioButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trimestralRadioButton))
                    .addGroup(periodicoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel36)
                        .addComponent(vecesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel37)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(semestralRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(anualRadioButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel47.setText("Taladro seleccionado:");

        jLabel46.setText("Mantenimientos programados");

        javax.swing.GroupLayout mantenimientoPanelLayout = new javax.swing.GroupLayout(mantenimientoPanel);
        mantenimientoPanel.setLayout(mantenimientoPanelLayout);
        mantenimientoPanelLayout.setHorizontalGroup(
            mantenimientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mantenimientoPanelLayout.createSequentialGroup()
                .addGroup(mantenimientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mantenimientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(mantenimientoPanelLayout.createSequentialGroup()
                            .addGap(26, 26, 26)
                            .addGroup(mantenimientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel47)
                                .addComponent(jLabel34)
                                .addComponent(jLabel33))
                            .addGap(18, 18, 18)
                            .addGroup(mantenimientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(taladroSelectedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(mantenimientoDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(diasMantTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(41, 41, 41)
                            .addComponent(periodicoCheckBox)
                            .addGap(29, 29, 29)
                            .addComponent(periodicoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(mantenimientoPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jSeparator4)))
                    .addGroup(mantenimientoPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mantenimientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(mantenimientoPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(mantenimientoPanelLayout.createSequentialGroup()
                                .addComponent(jLabel46)
                                .addGap(171, 171, 171)))))
                .addContainerGap(194, Short.MAX_VALUE))
        );
        mantenimientoPanelLayout.setVerticalGroup(
            mantenimientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mantenimientoPanelLayout.createSequentialGroup()
                .addGroup(mantenimientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mantenimientoPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(mantenimientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(taladroSelectedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel47))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mantenimientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel33)
                            .addComponent(mantenimientoDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mantenimientoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34)
                            .addComponent(diasMantTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(periodicoCheckBox)))
                    .addComponent(periodicoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );

        mainTabbedPane.addTab("Mantenimiento", mantenimientoPanel);

        decontinuarPanel.setBackground(new java.awt.Color(255, 255, 255));

        taladroSelectedLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel38.setText("A partir de:");

        descontinuacionDateChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                descontinuacionDateChooserPropertyChange(evt);
            }
        });

        jLabel39.setText("Taladro sustituto:");

        jLabel54.setText("Taladro seleccionado:");

        jLabel48.setText("Macolla:");

        macollaDescComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                macollaDescComboBoxActionPerformed(evt);
            }
        });

        jLabel55.setText("Fila:");

        filaDescComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filaDescComboBoxActionPerformed(evt);
            }
        });

        jLabel56.setText("Pozo:");

        pozoDescComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pozoDescComboBoxActionPerformed(evt);
            }
        });

        jLabel57.setText("Fase:");

        faseDescComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                faseDescComboBoxActionPerformed(evt);
            }
        });

        jLabel58.setText("Descontinuar al final de la ejecución de esta fase:");

        taladrosDescontinuadosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        taladrosDescontinuadosTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                taladrosDescontinuadosTableMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(taladrosDescontinuadosTable);

        jLabel65.setText("Taladros descontinuados:");

        reincorporarButton.setText("Reincorporar");
        reincorporarButton.setEnabled(false);
        reincorporarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reincorporarButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout decontinuarPanelLayout = new javax.swing.GroupLayout(decontinuarPanel);
        decontinuarPanel.setLayout(decontinuarPanelLayout);
        decontinuarPanelLayout.setHorizontalGroup(
            decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, decontinuarPanelLayout.createSequentialGroup()
                .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(decontinuarPanelLayout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(sustitutoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(decontinuarPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel39))
                    .addGroup(decontinuarPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel58))
                    .addGroup(decontinuarPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel54)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(taladroSelectedLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(decontinuarPanelLayout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(decontinuarPanelLayout.createSequentialGroup()
                                    .addGap(59, 59, 59)
                                    .addComponent(descontinuacionDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel38))
                            .addGroup(decontinuarPanelLayout.createSequentialGroup()
                                .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel48)
                                    .addGroup(decontinuarPanelLayout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addComponent(jLabel56)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(macollaDescComboBox, 0, 140, Short.MAX_VALUE)
                                    .addComponent(pozoDescComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(18, 18, 18)
                        .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel55)
                            .addComponent(jLabel57))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filaDescComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(faseDescComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel65)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reincorporarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(278, 278, 278))
        );
        decontinuarPanelLayout.setVerticalGroup(
            decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(decontinuarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(taladroSelectedLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54)
                    .addComponent(jLabel65))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(decontinuarPanelLayout.createSequentialGroup()
                        .addComponent(jLabel58)
                        .addGap(11, 11, 11)
                        .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel48)
                            .addComponent(macollaDescComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel55)
                            .addComponent(filaDescComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel56)
                            .addComponent(pozoDescComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel57)
                            .addComponent(faseDescComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38)
                            .addComponent(descontinuacionDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(decontinuarPanelLayout.createSequentialGroup()
                        .addGap(0, 15, Short.MAX_VALUE)
                        .addGroup(decontinuarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel39)
                            .addComponent(sustitutoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(56, 56, 56))
                    .addGroup(decontinuarPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reincorporarButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        mainTabbedPane.addTab("Descontinuar", decontinuarPanel);

        moverPanel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Movimiento de Taladros", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        asignaTaladrosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        asignaTaladrosTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                asignaTaladrosTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(asignaTaladrosTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 996, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addContainerGap())
        );

        taladroSelectedMoverLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel40.setText("Taladro seleccionado:");

        macollasOriginalComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                macollasOriginalComboBoxActionPerformed(evt);
            }
        });

        jLabel59.setText("Mover al final de la ejecución de esta fase:");

        jLabel41.setText("Macolla: ");

        jLabel42.setText("Fila:");

        filasOriginalComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filasOriginalComboBoxActionPerformed(evt);
            }
        });

        jLabel43.setText("Pozo:");

        pozosOriginalComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pozosOriginalComboBoxActionPerformed(evt);
            }
        });

        jLabel44.setText("Fase:");

        fasesOriginalComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fasesOriginalComboBoxActionPerformed(evt);
            }
        });

        jLabel45.setText("a esta localización:");

        jLabel49.setText("Macolla: ");

        macollasDestComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                macollasDestComboBoxActionPerformed(evt);
            }
        });

        jLabel50.setText("Fila:");

        filasDestComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filasDestComboBoxActionPerformed(evt);
            }
        });

        jLabel51.setText("Pozo:");

        pozosDestComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pozosDestComboBoxActionPerformed(evt);
            }
        });

        jLabel52.setText("Fase:");

        fasesDestComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fasesDestComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout moverPanelLayout = new javax.swing.GroupLayout(moverPanel);
        moverPanel.setLayout(moverPanelLayout);
        moverPanelLayout.setHorizontalGroup(
            moverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(moverPanelLayout.createSequentialGroup()
                .addGroup(moverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(moverPanelLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(moverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41)
                            .addComponent(jLabel49))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(moverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(macollasDestComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(macollasOriginalComboBox, 0, 153, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(moverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(moverPanelLayout.createSequentialGroup()
                                .addComponent(jLabel42)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filasOriginalComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(moverPanelLayout.createSequentialGroup()
                                .addComponent(jLabel50)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(filasDestComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(moverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(moverPanelLayout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pozosOriginalComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fasesOriginalComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(moverPanelLayout.createSequentialGroup()
                                .addComponent(jLabel51)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pozosDestComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel52)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fasesDestComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(moverPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel45))
                    .addGroup(moverPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(moverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(moverPanelLayout.createSequentialGroup()
                                .addComponent(jLabel40)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(taladroSelectedMoverLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel59))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        moverPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fasesOriginalComboBox, filasOriginalComboBox, macollasOriginalComboBox, pozosOriginalComboBox});

        moverPanelLayout.setVerticalGroup(
            moverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(moverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(moverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(taladroSelectedMoverLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel59)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(moverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(macollasOriginalComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42)
                    .addComponent(filasOriginalComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43)
                    .addComponent(pozosOriginalComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44)
                    .addComponent(fasesOriginalComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(moverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(macollasDestComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50)
                    .addComponent(filasDestComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51)
                    .addComponent(pozosDestComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel52)
                    .addComponent(fasesDestComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Mover", moverPanel);

        AgregarPanel.setBackground(new java.awt.Color(255, 255, 255));

        archivoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Carga desde Archivo")));

        javax.swing.GroupLayout archivoPanelLayout = new javax.swing.GroupLayout(archivoPanel);
        archivoPanel.setLayout(archivoPanelLayout);
        archivoPanelLayout.setHorizontalGroup(
            archivoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(archivoPanelLayout.createSequentialGroup()
                .addComponent(archivoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 671, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        archivoPanelLayout.setVerticalGroup(
            archivoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(archivoTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        manualPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Carga Manual"));

        jLabel1.setText("Nombre del Taladro:");

        jLabel2.setText("Fecha Disponibilidad:");

        javax.swing.GroupLayout manualPanelLayout = new javax.swing.GroupLayout(manualPanel);
        manualPanel.setLayout(manualPanelLayout);
        manualPanelLayout.setHorizontalGroup(
            manualPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(manualPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(taladroNombreTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fechaDispInicialDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        manualPanelLayout.setVerticalGroup(
            manualPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(manualPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(taladroNombreTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2))
            .addComponent(fechaDispInicialDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jLabel3.setText("Mudanza entre macollas:");

        jLabel4.setText("Mudanza entre pozos:");

        mudanzaMacollaTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        mudanzaPozoTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setText("Superficial:");

        superficialTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel6.setText("Slant:");

        slantTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setText("Piloto:");

        pilotoTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel8.setText("Vertical:");

        verticalTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel9.setText("Intermedio:");

        intermedioTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel10.setText("Productor:");

        productorTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel11.setText("Completación:");

        completacionTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel12.setText("Conexión:");

        conexionTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout diasPanelLayout = new javax.swing.GroupLayout(diasPanel);
        diasPanel.setLayout(diasPanelLayout);
        diasPanelLayout.setHorizontalGroup(
            diasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(diasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(diasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(diasPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaMacollaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaPozoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 784, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(diasPanelLayout.createSequentialGroup()
                        .addGroup(diasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(diasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(intermedioTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(superficialTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(diasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, diasPanelLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(slantTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(diasPanelLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(productorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(21, 21, 21)
                        .addGroup(diasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(diasPanelLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(completacionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(diasPanelLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pilotoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(diasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(diasPanelLayout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(conexionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, diasPanelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(verticalTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(210, Short.MAX_VALUE))
        );
        diasPanelLayout.setVerticalGroup(
            diasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(diasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(diasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(mudanzaMacollaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mudanzaPozoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(diasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(superficialTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(slantTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(verticalTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pilotoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(diasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(intermedioTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(productorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(conexionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(completacionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        taladroTabbedPane.addTab("Días", diasPanel);

        jLabel13.setText("Mudanza entre macollas:");

        mudanzaMacollaBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel14.setText("Mudanza entre pozos:");

        mudanzaPozoBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel15.setText("Superficial:");

        superficialBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel16.setText("Slant:");

        slantBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel17.setText("Piloto:");

        pilotoBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel18.setText("Vertical:");

        verticalBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        conexionBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel19.setText("Conexión:");

        completacionBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel20.setText("Completación:");

        productorBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel21.setText("Productor:");

        intermedioBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel22.setText("Intermedio:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaMacollaBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaPozoBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 784, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(intermedioBsTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(superficialBsTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(slantBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(productorBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(completacionBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pilotoBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(conexionBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(verticalBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(210, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {completacionBsTextField, conexionBsTextField, intermedioBsTextField, mudanzaMacollaBsTextField, mudanzaPozoBsTextField, pilotoBsTextField, productorBsTextField, slantBsTextField, superficialBsTextField, verticalBsTextField});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(mudanzaMacollaBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mudanzaPozoBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(superficialBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(slantBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(verticalBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pilotoBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(intermedioBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(productorBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel19)
                    .addComponent(conexionBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(completacionBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        taladroTabbedPane.addTab("Costo Bs.", jPanel2);

        jLabel23.setText("Mudanza entre macollas:");

        mudanzaMacollaUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel24.setText("Mudanza entre pozos:");

        mudanzaPozoUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        verticalUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        conexionUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel25.setText("Conexión:");

        jLabel26.setText("Vertical:");

        completacionUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        pilotoUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel27.setText("Piloto:");

        jLabel28.setText("Completación:");

        jLabel29.setText("Productor:");

        productorUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        slantUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel30.setText("Slant:");

        superficialUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        intermedioUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel31.setText("Intermedio:");

        jLabel32.setText("Superficial:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaMacollaUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaPozoUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 784, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32)
                            .addComponent(jLabel31))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(intermedioUsdTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(superficialUsdTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(slantUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel29)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(productorUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(21, 21, 21)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(completacionUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pilotoUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(conexionUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(verticalUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(210, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24)
                    .addComponent(mudanzaMacollaUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mudanzaPozoUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(superficialUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(slantUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(jLabel26)
                    .addComponent(verticalUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pilotoUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(intermedioUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(productorUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(jLabel25)
                    .addComponent(conexionUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(completacionUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        taladroTabbedPane.addTab("Costo US$", jPanel3);

        javax.swing.GroupLayout AgregarPanelLayout = new javax.swing.GroupLayout(AgregarPanel);
        AgregarPanel.setLayout(AgregarPanelLayout);
        AgregarPanelLayout.setHorizontalGroup(
            AgregarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AgregarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(AgregarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(archivoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(manualPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(taladroTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1008, Short.MAX_VALUE))
                .addContainerGap())
        );
        AgregarPanelLayout.setVerticalGroup(
            AgregarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AgregarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(archivoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(manualPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(taladroTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Agregar ", AgregarPanel);

        quitarPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel53.setText("Taladro:");

        quitarTaladroLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        quitarTaladroLabel.setText(" ");

        quitarMsgLabel.setText(" ");

        quitarTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Taladro", "Macolla", "Fila"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(quitarTable);

        quitarTaladroButton.setText("Quitar taladro");
        quitarTaladroButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitarTaladroButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout quitarPanelLayout = new javax.swing.GroupLayout(quitarPanel);
        quitarPanel.setLayout(quitarPanelLayout);
        quitarPanelLayout.setHorizontalGroup(
            quitarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(quitarPanelLayout.createSequentialGroup()
                .addGroup(quitarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(quitarPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(quitarTaladroLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(quitarPanelLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(quitarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(quitarTaladroButton)
                            .addGroup(quitarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(quitarMsgLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)))))
                .addContainerGap(571, Short.MAX_VALUE))
        );
        quitarPanelLayout.setVerticalGroup(
            quitarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(quitarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(quitarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(quitarTaladroLabel))
                .addGap(18, 18, 18)
                .addComponent(quitarMsgLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(quitarTaladroButton)
                .addContainerGap(81, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Quitar", quitarPanel);

        asignaciónNuevosPanel.setBackground(new java.awt.Color(255, 255, 255));

        taladrosNuevosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        taladrosNuevosTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                taladrosNuevosTableMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(taladrosNuevosTable);

        jLabel60.setText("Localización a asignar:");

        jLabel61.setText("Macolla:");

        asignaMacollaComboBox.setEnabled(false);
        asignaMacollaComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asignaMacollaComboBoxActionPerformed(evt);
            }
        });

        jLabel62.setText("Fila:");

        asignaFilaComboBox.setEnabled(false);
        asignaFilaComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asignaFilaComboBoxActionPerformed(evt);
            }
        });

        jLabel63.setText("Pozo:");

        asignaPozoComboBox.setEnabled(false);
        asignaPozoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asignaPozoComboBoxActionPerformed(evt);
            }
        });

        jLabel64.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel64.setText("Taladro no asignados:");

        asignarNuevoButton.setText("Asignar");
        asignarNuevoButton.setEnabled(false);
        asignarNuevoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asignarNuevoButtonActionPerformed(evt);
            }
        });

        jLabel66.setText("Fase:");

        asignarFaseTextField.setEnabled(false);

        javax.swing.GroupLayout asignaciónNuevosPanelLayout = new javax.swing.GroupLayout(asignaciónNuevosPanel);
        asignaciónNuevosPanel.setLayout(asignaciónNuevosPanelLayout);
        asignaciónNuevosPanelLayout.setHorizontalGroup(
            asignaciónNuevosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(asignaciónNuevosPanelLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(asignaciónNuevosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel64)
                    .addComponent(jLabel60)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(asignaciónNuevosPanelLayout.createSequentialGroup()
                        .addGroup(asignaciónNuevosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(asignaciónNuevosPanelLayout.createSequentialGroup()
                                .addComponent(jLabel61)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(asignaMacollaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(asignaciónNuevosPanelLayout.createSequentialGroup()
                                .addComponent(jLabel63)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(asignaciónNuevosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(asignarNuevoButton)
                                    .addComponent(asignaPozoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(asignaciónNuevosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel62)
                            .addComponent(jLabel66))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(asignaciónNuevosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(asignaFilaComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(asignarFaseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(628, Short.MAX_VALUE))
        );
        asignaciónNuevosPanelLayout.setVerticalGroup(
            asignaciónNuevosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(asignaciónNuevosPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel64)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel60)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(asignaciónNuevosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61)
                    .addComponent(asignaMacollaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel62)
                    .addComponent(asignaFilaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(asignaciónNuevosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(asignaPozoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66)
                    .addComponent(asignarFaseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(asignarNuevoButton)
                .addContainerGap())
        );

        mainTabbedPane.addTab("Asignación Nuevos", asignaciónNuevosPanel);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lista de Taladros", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        taladrosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Taladro", "Fecha", "Status", "Fecha Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        taladrosTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                taladrosTableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(taladrosTable);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addComponent(mainTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        agregarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconagregar.png"))); // NOI18N
        agregarButton.setText("Agregar");
        agregarButton.setEnabled(false);
        agregarButton.setFocusable(false);
        agregarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        agregarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        agregarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarButtonActionPerformed(evt);
            }
        });
        toolBar.add(agregarButton);

        eliminarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconquitar26.png"))); // NOI18N
        eliminarButton.setText("Eliminar");
        eliminarButton.setEnabled(false);
        eliminarButton.setFocusable(false);
        eliminarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        eliminarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        eliminarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarButtonActionPerformed(evt);
            }
        });
        toolBar.add(eliminarButton);

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
        toolBar.add(clearButton);

        moverButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconrefresh26.png"))); // NOI18N
        moverButton.setText("Mover");
        moverButton.setEnabled(false);
        moverButton.setFocusable(false);
        moverButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moverButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        moverButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moverButtonActionPerformed(evt);
            }
        });
        toolBar.add(moverButton);

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
                .addGap(27, 27, 27)
                .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(499, Short.MAX_VALUE))
        );
        toolBarPanelLayout.setVerticalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolBarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        toolBar.add(toolBarPanel);

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
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void archivoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_archivoButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        try {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                archivo = fileChooser.getSelectedFile();
                archivoTextField.setText(archivo.getCanonicalPath());
                cargaArchivo();

            } else {
                Contexto.showMessage("Acción cancelada por el usuario", Constantes.MENSAJE_INFO);
            }
        } catch (IOException ex) {
            SISMONLOG.logger.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_archivoButtonActionPerformed

    private void taladrosTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_taladrosTableMouseClicked
        Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
        asignaTaladrosTable.clearSelection();
        TableModel model = taladrosTable.getModel();
        if (taladrosTable.getSelectedRow() >= 0) {
            String nombreTaladro = (String) model.getValueAt(taladrosTable.getSelectedRow(), 0);
            taladroSelected = taladrosMap.get(nombreTaladro);
            updateAgregarForm();
            isnew = false;
            guardarButton.setEnabled(false);
            isEditable = true;
            configureTaladrosListener();

            // relativo a los mantenimientos
            taladroSelectedLabel.setText(taladroSelected.getNombre());
            taladroSelectedLabel1.setText(taladroSelected.getNombre());
            taladroSelectedMoverLabel.setText(taladroSelected.getNombre());

            fillMantenimientoTable();
            mantenimientoDateChooser.setDate(null);
            diasMantTextField.setText(null);

            // relativo a la descontinuación
            fillMacollaDescComboBox();
            fillSustitutoComboBox();

            // relativo al movimiento de taladros
            fillMacollaOriginalComboBox();
            fillMacollaDestinoComboBox();

            // relativo a quitar taladros:
            quitarTaladroLabel.setText(taladroSelected.getNombre());
            List<TaladroAsignado> talAsigList = taladroAsignadoManager
                    .findRutaTaladro(escenarioSelected, taladroSelected);
            String msg;
            if (talAsigList == null || talAsigList.isEmpty()) {
                msg = "Este taladro puede ser quitado del escenario sin problemas";
                quitarTable.setVisible(false);
            } else {
                msg = "Este taladro tiene las siguientes asignaciones recuerde ejecutar la perforación luego de eliminarlo";
                quitarTaladroButton.setVisible(false);
                quitarTable.setVisible(true);
                fillQuitarTable(talAsigList);
            }
            quitarTaladroButton.setVisible(true);
            quitarMsgLabel.setText(msg);
        }
    }//GEN-LAST:event_taladrosTableMouseClicked

    private void escenarioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escenarioComboBoxActionPerformed
        if (escenarioComboBox.getSelectedItem() instanceof Escenario) {
            Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
            escenarioSelected = (Escenario) escenarioComboBox.getSelectedItem();
            perforacionList = perforacionManager.findAll(escenarioSelected);
            loadTaladrosMap();
            fillTaladrosTable();
            archivoButton.setEnabled(true);
            fillMacollaOriginalComboBox();
            fillMacollaDestinoComboBox();
            fillAsignaTaladrosTable();
            clearMantForm();
            clearDescComboBoxes();
            clearForm();
            fillTaladrosNuevosTable();
            fillTaladrosDescontinuadosTable();
        } else {
            taladrosTable.setModel(new DefaultTableModel());
            mantenimientoTable.setModel(new DefaultTableModel());
            asignaTaladrosTable.setModel(new DefaultTableModel());
            quitarTable.setModel(new DefaultTableModel());
            escenarioSelected = null;
            clearForm();
            clearDescComboBoxes();
            clearTaladrosDescontinuadosTable();
        }
    }//GEN-LAST:event_escenarioComboBoxActionPerformed

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        Contexto.setActiveFrame(instance);
        configureListener();
        fillEscenarioComboBox();
    }//GEN-LAST:event_onActivated

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
        if (escenarioComboBox.getSelectedItem() instanceof Escenario) {
            // nuevos taladros
            if (isnew && !newTaladro && !updateMantenimiento) {
                for (Map.Entry<String, Taladro> mapa : newTaladrosMap.entrySet()) {
                    taladroManager.create(mapa.getValue());
                    loadTaladrosMap();
                    fillTaladrosTable();
                    fillTaladrosNuevosTable();
                }
                guardarButton.setEnabled(false);
                clearForm();
                Contexto.showMessage("Taladros agregados con éxito a este escenario", Constantes.MENSAJE_INFO);
                newTaladrosMap.clear();
                isnew = false;
            } else if (!isnew && !newTaladro && !updateMantenimiento) {
                updateTaladroModel();
                thFaseManager.batchEdit(taladroSelected.getTaladroHasFaseCollection());
                guardarButton.setEnabled(false);
                clearForm();
                Contexto.showMessage("Taladro actualizado con éxito en este escenario", Constantes.MENSAJE_INFO);
            } else if (newTaladro) {
                makeNewTaladros(addNewTaladro());
                for (Map.Entry<String, Taladro> mapa : newTaladrosMap.entrySet()) {
                    taladroManager.create(mapa.getValue());
                    loadTaladrosMap();
                    fillTaladrosTable();
                    fillTaladrosNuevosTable();
                }
                guardarButton.setEnabled(false);
                taladroNombreTextField.setText(null);
                fechaDispInicialDateChooser.setDate(null);
                clearForm();
                Contexto.showMessage("Taladros agregados con éxito a este escenario", Constantes.MENSAJE_INFO);
                newTaladrosMap.clear();
                newTaladro = false;
            }

            // relativo a los mantenimientos
            if (updateMantenimiento) {
                if (isValidDateModification(mantenimientoDateChooser.getDate())) {
                    mantenimientoSelected.setFecha(mantenimientoDateChooser.getDate());
                    try {
                        int dias = Integer.parseInt(diasMantTextField.getText());
                        mantenimientoSelected.setDias(dias);
                        mantManager.edit(mantenimientoSelected);
                        fillMantenimientoTable();
                        Contexto.showMessage("Mantenimiento actualizado con exito", Constantes.MENSAJE_INFO);
                        clearMantForm();
                    } catch (NumberFormatException e) {
                        Contexto.showMessage("'Duración en Dias' deber ser un valor entero", Constantes.MENSAJE_ERROR);
                    }
                } else {
                    showCierreWarning();
                }
                updateMantenimiento = false;
            }

            // relativo a la descontinuación
            if (descontinuarTaladro) {
                if (isValidDateModification(descontinuacionDateChooser.getDate())) {
                    boolean encontrado = false;
                    List<TaladroStatus> talStatusList = talStatusManager.find(taladroSelected);
                    for (TaladroStatus ts : talStatusList) {
                        if (ts.getNombre().equals(Constantes.TALADRO_STATUS_DESCONTINUADO)) {
                            encontrado = true;
                            break;
                        }
                    }

                    if (!encontrado) {
                        TaladroStatus taladroStatus = talStatusManager
                                .find(taladroSelected, Constantes.TALADRO_STATUS_ACTIVO);
                        taladroStatus.setFechaOut(descontinuacionDateChooser.getDate());
                        taladroStatus.setStatus(Constantes.TALADRO_STATUS_INACTIVO);
                        talStatusManager.edit(taladroStatus);

                        taladroStatus = new TaladroStatus();
                        taladroStatus.setTaladroId(taladroSelected);
                        taladroStatus.setFechaIn(descontinuacionDateChooser.getDate());
                        taladroStatus.setNombre(Constantes.TALADRO_STATUS_DESCONTINUADO);
                        taladroStatus.setStatus(Constantes.TALADRO_STATUS_ACTIVO);
                        talStatusManager.create(taladroStatus);

                        if (sustitutoComboBox.getSelectedItem() instanceof Taladro) {
                            TaladroSustituto sustituto = new TaladroSustituto();
                            sustituto.setTaladroOriginal(taladroSelected);
                            sustituto.setTaladroSutituto((Taladro) sustitutoComboBox.getSelectedItem());
                            talSustitutoManager.create(sustituto);
                        }
                        clearDescForm();

                        Contexto.showMessage("Taladro " + taladroSelected + " descontinuado con éxito",
                                Constantes.MENSAJE_INFO);
                        taladroSelected = null;
                        loadTaladrosMap();
                        fillTaladrosTable();

                    } else {
                        clearDescForm();
                        Contexto.showMessage("El taladro " + taladroSelected
                                + " ha sido descontinuado previamente", Constantes.MENSAJE_ERROR);
                    }
                } else {
                    showCierreWarning();
                }
                descontinuarTaladro = false;
            }

            // relativo a las caracteristicas del taladro
//            if (isEditable) {
//                List<TaladroHasFase> faseList = (List<TaladroHasFase>) taladroSelected.getTaladroHasFaseCollection();
//                faseList = fillFaseData(faseList);
//                taladroSelected.setTaladroHasFaseCollection(faseList);
//                taladroManager.edit(taladroSelected);
//                isEditable = false;
//                removeTaladrosListener();
//                loadTaladrosMap();
//                fillTaladrosTable();
//                guardarButton.setEnabled(false);
//                Contexto.showMessage("Taladro Editado con éxito", Constantes.MENSAJE_INFO);
//            }
        } else {
            Contexto.showMessage("Debe tener un escenario seleccionado", Constantes.MENSAJE_ERROR);
        }
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void onDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onDeactivated
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        removeListener();
        clearMantForm();
        clearForm();
    }//GEN-LAST:event_onDeactivated

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clearForm();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void periodicoCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_periodicoCheckBoxActionPerformed
        if (periodicoCheckBox.isSelected()) {
            periodicoPanel.setVisible(true);
        } else {
            periodicoPanel.setVisible(false);
        }
    }//GEN-LAST:event_periodicoCheckBoxActionPerformed

    private void agregarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarButtonActionPerformed
        if (newMantenimiento) {
            TaladroMant mant;
            if (!periodicoCheckBox.isSelected()) {
                if (isValidDateModification(mantenimientoDateChooser.getDate())) {
                    mant = new TaladroMant();
                    mant.setTaladroId(taladroSelected);
                    mant.setFecha(mantenimientoDateChooser.getDate());
                    mant.setDias(Integer.parseInt(diasMantTextField.getText()));
                    mantManager.create(mant);
                } else {
                    showCierreWarning();
                }
                fillMantenimientoTable();
            } else {
                Date fechaInicial = mantenimientoDateChooser.getDate();
                if (isValidDateModification(fechaInicial)) {
                    LocalDate ldate = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(fechaInicial.getTime()),
                            ZoneId.systemDefault()).toLocalDate();
                    List<TaladroMant> mantList = new ArrayList<>();
                    mant = new TaladroMant();
                    mant.setTaladroId(taladroSelected);
                    mant.setFecha(fechaInicial);
                    mant.setDias(Integer.parseInt(diasMantTextField.getText()));
                    mantList.add(mant);
                    try {
                        int veces = Integer.parseInt(vecesTextField.getText());
                        for (int i = 0; i < (veces - 1); i++) {
                            if (mensualRadioButton.isSelected()) {
                                ldate = ldate.plusMonths(1);
                            } else if (trimestralRadioButton.isSelected()) {
                                ldate = ldate.plusMonths(3);
                            } else if (semestralRadioButton.isSelected()) {
                                ldate = ldate.plusMonths(6);
                            } else if (anualRadioButton.isSelected()) {
                                ldate = ldate.plusYears(1);
                            }
                            Date fecha = Date.from(ldate.atStartOfDay()
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant());
                            mant = new TaladroMant();
                            mant.setTaladroId(taladroSelected);
                            mant.setFecha(fecha);
                            mant.setDias(Integer.parseInt(diasMantTextField.getText()));
                            mantList.add(mant);
                        }
                        mantManager.batchSave(mantList);
                        fillMantenimientoTable();
                        clearMantForm();
                    } catch (NumberFormatException e) {
                        Contexto.showMessage("'Repetir por' deber ser un valor entero", Constantes.MENSAJE_ERROR);
                    }
                } else {
                    showCierreWarning();
                }
            }
            clearMantForm();
        }
    }//GEN-LAST:event_agregarButtonActionPerformed

    private void mantenimientoTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mantenimientoTableMouseClicked
        int id = (int) mantenimientoTable.getModel().getValueAt(mantenimientoTable.getSelectedRow(), 0);
        mantenimientoSelected = mantManager.find(id);
        isEditingMantenimiento = true;
        mantenimientoDateChooser.setDate(mantenimientoSelected.getFecha());
        diasMantTextField.setText(String.valueOf(mantenimientoSelected.getDias()));
        eliminarButton.setEnabled(true);
        updateMantenimiento = true;
        isEditingMantenimiento = false;
    }//GEN-LAST:event_mantenimientoTableMouseClicked

    private void eliminarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarButtonActionPerformed
        if (updateMantenimiento) {
            mantManager.remove(mantenimientoSelected);
            fillMantenimientoTable();
            Contexto.showMessage("Mantenimiento eliminado con éxito", Constantes.MENSAJE_INFO);
            clearMantForm();
            eliminarButton.setEnabled(false);
        }

        if (newDescontinuacion && mainTabbedPane.getSelectedIndex() == 1) {
            if (isValidDateModification(descontinuacionDateChooser.getDate())) {
                
                List<TaladroStatus> statusList = talStatusManager.find(taladroSelected);
                TaladroStatus status = null;
                for (TaladroStatus stat : statusList) {
                    if (stat.getNombre().equals(Constantes.TALADRO_STATUS_OCUPADO)
                            && stat.getStatus() == Constantes.TALADRO_STATUS_ACTIVO) {
                        status = stat;
                        break;
                    }
                }

                if (status != null) {
                    status.setFechaOut(descontinuacionDateChooser.getDate());
                    status.setStatus(Constantes.TALADRO_STATUS_INACTIVO);
                    talStatusManager.edit(status);

                    status = new TaladroStatus();
                    status.setFechaIn(descontinuacionDateChooser.getDate());
                    status.setNombre(Constantes.TALADRO_STATUS_DESCONTINUADO);
                    status.setStatus(Constantes.TALADRO_STATUS_ACTIVO);
                    status.setTaladroId(taladroSelected);
                    status.setFilaId(((Fila) filaDescComboBox.getSelectedItem()).getId());
                    Long pozoId = ((Pozo) pozoDescComboBox.getSelectedItem()).getId();
                    status.setPozoId(BigInteger.valueOf(pozoId));
                    status.setFase((String) faseDescComboBox.getSelectedItem());
                    talStatusManager.create(status);

                    // cambio de asignacion
                    PozoSecuencia psOut = pozoSecuenciaManager.find(escenarioSelected,
                            (Pozo) pozoDescComboBox.getSelectedItem(),
                            (String) faseDescComboBox.getSelectedItem());

                    TaladroAsignado ta = taladroAsignadoManager.find(escenarioSelected, taladroSelected,
                            (Fila) filaDescComboBox.getSelectedItem());
                    ta.setPozoOutId((Pozo) pozoDescComboBox.getSelectedItem());
                    ta.setFaseOut((String) faseDescComboBox.getSelectedItem());
                    ta.setPozoSecuenciaOutId(psOut);
                    taladroAsignadoManager.edit(ta);

                    Contexto.showMessage("Taladro " + taladroSelected.getNombre()
                            + " descontinuado con éxito", Constantes.MENSAJE_INFO);
                    fillTaladrosDescontinuadosTable();
                } else {
                    String mensaje = "<html>Este taladro no está asignado a ninguna fila."
                            + "<br>¿Desea eliminarlo de este escenario?</html>";
                    int answer = JOptionPane.showConfirmDialog(this, mensaje,
                            "Advertencia", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {
                        taladroManager.remove(taladroSelected);
                        Contexto.showMessage("Taladro " + taladroSelected.getNombre()
                                + " eliminado con éxito", Constantes.MENSAJE_INFO);
                    }
                }
                taladroSelected = null;
                loadTaladrosMap();
                fillTaladrosTable();
                clearDescForm();
                eliminarButton.setText("Eliminar");
                eliminarButton.setEnabled(false);
            } else {
                showCierreWarning();
            }
        }

        if (deleteMovimientoTaladro && mainTabbedPane.getSelectedIndex() == 2) {
            int answer = JOptionPane.showConfirmDialog(this,
                    "¿Desea eliminar el cambio del taladro "
                    + taEliminar.getTaladroId() + "?",
                    "Eliminación de Cambio de Taladro",
                    JOptionPane.WARNING_MESSAGE,
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                eliminarMovimientoTaladro();
            }
            eliminarButton.setEnabled(false);
            asignaTaladrosTable.clearSelection();
        }
    }//GEN-LAST:event_eliminarButtonActionPerformed

    private void moverButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moverButtonActionPerformed
        // para actualizar de que fila, pozo y fase salió el taladro y a cual será 
        // la de destino
        int answer = JOptionPane.showConfirmDialog(this, "¿Está seguro de este movimiento?. ",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            SISMONLOG.logger.log(Level.INFO, "Va a mover el taladro {0}",
                    taladroSelected.getNombre());
            // de donde va a salir el taladro
            Fila filaOut = (Fila) filasOriginalComboBox.getSelectedItem();
            Pozo pozoOut = (Pozo) pozosOriginalComboBox.getSelectedItem();
            String faseOut = (String) fasesOriginalComboBox.getSelectedItem();

            PozoSecuencia secuenciaOut = pozoSecuenciaManager
                    .find(escenarioSelected, pozoOut, faseOut);

            TaladroAsignado taIni = taladroAsignadoManager.find(escenarioSelected,
                    taladroSelected, filaOut);

            // se crea la nueva localización del taladro
            Fila filaIn = (Fila) filasDestComboBox.getSelectedItem();
            Pozo pozoIn = (Pozo) pozosDestComboBox.getSelectedItem();
            String faseIn = (String) fasesDestComboBox.getSelectedItem();
            PozoSecuencia secuenciaIn = pozoSecuenciaManager.find(escenarioSelected, pozoIn, faseIn);
            // y se va colocando el orden del movimiento
            int ordenIni = taIni.getOrden();

            List<PozoSecuencia> pzoSecList = pozoSecuenciaManager
                    .findAllOrdered(filaIn, escenarioSelected);
            // se obtiene la secuencia máxima, para ver hasta donde va a llegar el movimiento 
            PozoSecuencia psMax = pzoSecList.stream()
                    .max((ps1, ps2) -> ps1.getSecuencia() - ps2.getSecuencia()).get();

            // se actualiza el taladroAsignado inicial
            taIni.setPozoOutId(pozoOut);
            taIni.setFaseOut(faseOut);
            taIni.setPozoSecuenciaOutId(secuenciaOut);
            taladroAsignadoManager.edit(taIni);

            // se crea el nuevo taladroAsignado
            TaladroAsignado taEnd = new TaladroAsignado();
            taEnd.setTaladroId(taladroSelected);
            taEnd.setOrden(ordenIni + 1);
            taEnd.setFilaId(filaIn);
            taEnd.setPozoInId(pozoIn);
            taEnd.setFaseIn(faseIn);
            taEnd.setPozoSecuenciaInId(secuenciaIn);
            taEnd.setPozoOutId(psMax.getPozoId());
            taEnd.setFaseOut(psMax.getFase());
            taEnd.setPozoSecuenciaOutId(psMax);
            taEnd.setEscenarioId(escenarioSelected);
            taladroAsignadoManager.create(taEnd);

            // se crea la traza de movimiento de este taladro
            List<TaladroTraza> ttList = taladroTrazaManager.find(taladroSelected, escenarioSelected);
            // se obtiene el ultimo movimiento realizado
            TaladroTraza ttOld = ttList.get(ttList.size() - 1);
            // busca el nuevo orden 
            int orden = ttOld.getOrden() + 1;
            // se crea la nueva traza de este movimiento de taladro
            TaladroTraza ttNew = new TaladroTraza();
            ttNew.setTaladroId(taladroSelected.getId());
            ttNew.setPozoOutOrigenId(taIni.getPozoOutId().getId());
            ttNew.setFaseOutOrigen(taIni.getFaseOut());
            ttNew.setPozoSecuenciaOrigenId(taIni.getPozoSecuenciaOutId().getId());
            ttNew.setOrden(orden);
            ttNew.setTaladroAsignadoOrigenId(taIni.getId());
            ttNew.setTaladroAsignadoDestinoId(taEnd.getId());
            ttNew.setEscenarioId(escenarioSelected.getId());
            taladroTrazaManager.create(ttNew);

//            taIni.setPozoOutId(pozoOut);
//            taIni.setFaseOut(faseOut);
//            taIni.setPozoSecuenciaOutId(secuenciaOut);
//            taladroAsignadoManager.edit(taIni);
            Contexto.showMessage("Movimiento de taladro " + taladroSelected.getNombre()
                    + " realizado con éxito",
                    Constantes.MENSAJE_INFO);
            SISMONLOG.logger.log(Level.INFO, "Taladro {0} movido con éxito",
                    taladroSelected);

            fillAsignaTaladrosTable();
            moverButton.setEnabled(false);
            clearMoverForm();

            taladrosTable.clearSelection();
            taladroSelected = null;
            cleanTaladrosLabel();
        } else {
            moverButton.setEnabled(false);
        }
    }//GEN-LAST:event_moverButtonActionPerformed

    private void asignaTaladrosTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_asignaTaladrosTableMouseClicked
        Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
        eliminarButton.setEnabled(true);
        Integer id = (Integer) asignaTaladrosTable.getModel()
                .getValueAt(asignaTaladrosTable.getSelectedRow(), 0);
        taEliminar = taladroAsignadoManager.find(id);
        deleteMovimientoTaladro = true;

    }//GEN-LAST:event_asignaTaladrosTableMouseClicked

    private void descontinuacionDateChooserPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_descontinuacionDateChooserPropertyChange
        if (evt.getPropertyName().equals("date") && evt.getNewValue() != null) {
            descontinuarTaladro = true;
            //guardarButton.setEnabled(true);
        }
    }//GEN-LAST:event_descontinuacionDateChooserPropertyChange

    private void macollaDescComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_macollaDescComboBoxActionPerformed
        if (macollaDescComboBox.getSelectedItem() instanceof Macolla) {
            fillFilasDescComboBox();
        } else {
            filaDescComboBox.removeAllItems();
        }
    }//GEN-LAST:event_macollaDescComboBoxActionPerformed

    private void filaDescComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filaDescComboBoxActionPerformed
        if (filaDescComboBox.getSelectedItem() instanceof Fila) {
            fillPozoDescComboBox();
        } else {
            pozoDescComboBox.removeAllItems();
        }
    }//GEN-LAST:event_filaDescComboBoxActionPerformed

    private void pozoDescComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pozoDescComboBoxActionPerformed
        if (pozoDescComboBox.getSelectedItem() instanceof Pozo) {
            fillFaseDescComboBox();
        } else {
            faseDescComboBox.removeAllItems();
        }
    }//GEN-LAST:event_pozoDescComboBoxActionPerformed

    private void faseDescComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_faseDescComboBoxActionPerformed
        if (faseDescComboBox.getSelectedItem() != null
                && !faseDescComboBox.getSelectedItem().equals(OPCION_SELECIONE)) {
            Date fecha = perforacionManager.findFaseEnd((Pozo) pozoDescComboBox.getSelectedItem(),
                    (String) faseDescComboBox.getSelectedItem(),
                    escenarioSelected);
           descontinuacionDateChooser.setDate(fecha);
        }
    }//GEN-LAST:event_faseDescComboBoxActionPerformed

    private void macollasOriginalComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_macollasOriginalComboBoxActionPerformed
        if (macollasOriginalComboBox.getSelectedItem() instanceof Macolla) {
            fillFilasOriginalComboBox();
        } else {
            filasOriginalComboBox.removeAllItems();
        }
    }//GEN-LAST:event_macollasOriginalComboBoxActionPerformed

    private void filasOriginalComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filasOriginalComboBoxActionPerformed
        if (filasOriginalComboBox.getSelectedItem() instanceof Fila) {
            fillPozosOriginalComboBox();
        } else {
            pozosOriginalComboBox.removeAllItems();
        }
    }//GEN-LAST:event_filasOriginalComboBoxActionPerformed

    private void pozosOriginalComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pozosOriginalComboBoxActionPerformed
        if (pozosOriginalComboBox.getSelectedItem() instanceof Pozo) {
            fillFaseOriginalComboBox();
        } else {
            fasesOriginalComboBox.removeAllItems();
        }
    }//GEN-LAST:event_pozosOriginalComboBoxActionPerformed

    private void macollasDestComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_macollasDestComboBoxActionPerformed
        if (macollasDestComboBox.getSelectedItem() instanceof Macolla) {
            fillFilaDestinoComboBox();
        } else {
            filasDestComboBox.removeAllItems();
        }
    }//GEN-LAST:event_macollasDestComboBoxActionPerformed

    private void filasDestComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filasDestComboBoxActionPerformed
        if (filasDestComboBox.getSelectedItem() instanceof Fila) {
            fillPozoDestinoComboBox();
        } else {
            pozosDestComboBox.removeAllItems();
        }
    }//GEN-LAST:event_filasDestComboBoxActionPerformed

    private void pozosDestComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pozosDestComboBoxActionPerformed
        if (pozosDestComboBox.getSelectedItem() instanceof Pozo) {
            fillFaceDestinoComboBox();
        } else {
            fasesDestComboBox.removeAllItems();
        }
    }//GEN-LAST:event_pozosDestComboBoxActionPerformed

    private void fasesOriginalComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fasesOriginalComboBoxActionPerformed
        checkCambioTaladro();
    }//GEN-LAST:event_fasesOriginalComboBoxActionPerformed

    private void fasesDestComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fasesDestComboBoxActionPerformed
        checkCambioTaladro();
    }//GEN-LAST:event_fasesDestComboBoxActionPerformed

    private void quitarTaladroButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitarTaladroButtonActionPerformed
        int answer = JOptionPane.showConfirmDialog(this,
                "Va a eliminar el taladro " + taladroSelected.getNombre() + " del "
                + "presente escenario, ¿Desea continuar?",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            Date fechaCierre = escenarioSelected.getFechaCierre();
            if (fechaCierre != null) {
                if (taladroSelected.getFechaInicial().after(fechaCierre)) {
                    perforacionManager.remove(taladroSelected);
                    talStatusManager.remove(taladroSelected);
                    taladroManager.remove(taladroSelected);
                    Contexto.showMessage("Taladro eliminado del escenario con éxito", Constantes.MENSAJE_INFO);
                    taladroSelected = null;
                    taladrosTable.clearSelection();
                    loadTaladrosMap();
                    fillTaladrosTable();
                    quitarTaladroButton.setVisible(false);
                } else {
                    Contexto.showMessage("Este taladro no puede ser eliminado, "
                            + "debido a que tiene actividad anterior a la fecha de cierre de este escenario", Constantes.MENSAJE_ERROR);
                }
            } else {
                taladroManager.remove(taladroSelected);
                Contexto.showMessage("Taladro eliminado del escenario con éxito", Constantes.MENSAJE_INFO);
                taladroSelected = null;
                taladrosTable.clearSelection();
                loadTaladrosMap();
                fillTaladrosTable();
                quitarTaladroButton.setVisible(false);
            }
        }
    }//GEN-LAST:event_quitarTaladroButtonActionPerformed

    private void taladrosNuevosTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_taladrosNuevosTableMouseClicked

        fillAsignaMacollaComboBox();
        asignaMacollaComboBox.setEnabled(true);
    }//GEN-LAST:event_taladrosNuevosTableMouseClicked

    private void asignaMacollaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_asignaMacollaComboBoxActionPerformed
        if (asignaMacollaComboBox.getSelectedItem() instanceof Macolla) {
            fillAsignaFilaComboBox((Macolla) asignaMacollaComboBox.getSelectedItem());
            asignaFilaComboBox.setEnabled(true);
        } else {
            asignaFilaComboBox.removeAllItems();
            asignaFilaComboBox.setEnabled(false);
        }
    }//GEN-LAST:event_asignaMacollaComboBoxActionPerformed

    private void asignaFilaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_asignaFilaComboBoxActionPerformed
        if (asignaFilaComboBox.getSelectedItem() instanceof Fila) {
            fillAsignaPozoComboBox((Fila) asignaFilaComboBox.getSelectedItem());
            asignaPozoComboBox.setEnabled(true);
        } else {
            asignaPozoComboBox.removeAllItems();
            asignaPozoComboBox.setEnabled(false);
        }
    }//GEN-LAST:event_asignaFilaComboBoxActionPerformed

    private void asignaPozoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_asignaPozoComboBoxActionPerformed
        if(asignaPozoComboBox.getSelectedItem() instanceof Pozo){
            findNextSequence();
            asignarNuevoButton.setEnabled(true);
        } else {
            asignarNuevoButton.setEnabled(false);
        }
    }//GEN-LAST:event_asignaPozoComboBoxActionPerformed

    private void asignarNuevoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_asignarNuevoButtonActionPerformed
        Long id = (Long) taladrosNuevosTable.getModel()
                .getValueAt(taladrosNuevosTable.getSelectedRow(), 0);
        Taladro taladroNuevo = taladroManager.find(id);
        crearAsignacionTaladroNuevo(taladroNuevo);
    }//GEN-LAST:event_asignarNuevoButtonActionPerformed

    private void taladrosDescontinuadosTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_taladrosDescontinuadosTableMouseClicked
        TaladroStatus ts = (TaladroStatus) taladrosDescontinuadosTable.getModel()
                .getValueAt(taladrosDescontinuadosTable.getSelectedRow(), 0);
        Pozo pozo = pozoManager.find(ts.getPozoId().longValue());
        Fila fila = filaManager.find(ts.getFilaId());
        Macolla macolla = fila.getMacollaId();
        String fase = ts.getFase();

        macollaDescComboBox.setSelectedItem(macolla);

        reincorporarButton.setEnabled(true);
    }//GEN-LAST:event_taladrosDescontinuadosTableMouseClicked

    private void reincorporarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reincorporarButtonActionPerformed
        TaladroStatus ts = (TaladroStatus) taladrosDescontinuadosTable.getModel()
                .getValueAt(taladrosDescontinuadosTable.getSelectedRow(), 0);

        reincorporarTaladro(ts);
    }//GEN-LAST:event_reincorporarButtonActionPerformed

    private void mainTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mainTabbedPaneStateChanged
        archivoButton.setEnabled(mainTabbedPane.getSelectedIndex() == Constantes.TAB_AGREGAR);
    }//GEN-LAST:event_mainTabbedPaneStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AgregarPanel;
    private javax.swing.JButton agregarButton;
    private javax.swing.JRadioButton anualRadioButton;
    private javax.swing.JButton archivoButton;
    private javax.swing.JPanel archivoPanel;
    private javax.swing.JTextField archivoTextField;
    private javax.swing.JComboBox<Object> asignaFilaComboBox;
    private javax.swing.JComboBox<Object> asignaMacollaComboBox;
    private javax.swing.JComboBox<Object> asignaPozoComboBox;
    private javax.swing.JTable asignaTaladrosTable;
    private javax.swing.JPanel asignaciónNuevosPanel;
    private javax.swing.JTextField asignarFaseTextField;
    private javax.swing.JButton asignarNuevoButton;
    private javax.swing.JPanel backPanel;
    private javax.swing.JButton clearButton;
    private javax.swing.JTextField completacionBsTextField;
    private javax.swing.JTextField completacionTextField;
    private javax.swing.JTextField completacionUsdTextField;
    private javax.swing.JTextField conexionBsTextField;
    private javax.swing.JTextField conexionTextField;
    private javax.swing.JTextField conexionUsdTextField;
    private javax.swing.JPanel decontinuarPanel;
    private com.toedter.calendar.JDateChooser descontinuacionDateChooser;
    private javax.swing.JTextField diasMantTextField;
    private javax.swing.JPanel diasPanel;
    private javax.swing.JButton eliminarButton;
    private javax.swing.JComboBox escenarioComboBox;
    private javax.swing.JComboBox<String> faseDescComboBox;
    private javax.swing.JComboBox<Object> fasesDestComboBox;
    private javax.swing.JComboBox<Object> fasesOriginalComboBox;
    private com.toedter.calendar.JDateChooser fechaDispInicialDateChooser;
    private javax.swing.JComboBox<Object> filaDescComboBox;
    private javax.swing.JComboBox<Object> filasDestComboBox;
    private javax.swing.JComboBox<Object> filasOriginalComboBox;
    private javax.swing.ButtonGroup frecButtonGroup;
    private javax.swing.JButton guardarButton;
    private javax.swing.JTextField intermedioBsTextField;
    private javax.swing.JTextField intermedioTextField;
    private javax.swing.JTextField intermedioUsdTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JComboBox<Object> macollaDescComboBox;
    private javax.swing.JComboBox<Object> macollasDestComboBox;
    private javax.swing.JComboBox<Object> macollasOriginalComboBox;
    private javax.swing.JTabbedPane mainTabbedPane;
    private com.toedter.calendar.JDateChooser mantenimientoDateChooser;
    private javax.swing.JPanel mantenimientoPanel;
    private javax.swing.JTable mantenimientoTable;
    private javax.swing.JPanel manualPanel;
    private javax.swing.JRadioButton mensualRadioButton;
    private javax.swing.JButton moverButton;
    private javax.swing.JPanel moverPanel;
    private javax.swing.JTextField mudanzaMacollaBsTextField;
    private javax.swing.JTextField mudanzaMacollaTextField;
    private javax.swing.JTextField mudanzaMacollaUsdTextField;
    private javax.swing.JTextField mudanzaPozoBsTextField;
    private javax.swing.JTextField mudanzaPozoTextField;
    private javax.swing.JTextField mudanzaPozoUsdTextField;
    private javax.swing.JCheckBox periodicoCheckBox;
    private javax.swing.JPanel periodicoPanel;
    private javax.swing.JTextField pilotoBsTextField;
    private javax.swing.JTextField pilotoTextField;
    private javax.swing.JTextField pilotoUsdTextField;
    private javax.swing.JComboBox<Object> pozoDescComboBox;
    private javax.swing.JComboBox<Object> pozosDestComboBox;
    private javax.swing.JComboBox<Object> pozosOriginalComboBox;
    private javax.swing.JTextField productorBsTextField;
    private javax.swing.JTextField productorTextField;
    private javax.swing.JTextField productorUsdTextField;
    private javax.swing.JLabel quitarMsgLabel;
    private javax.swing.JPanel quitarPanel;
    private javax.swing.JTable quitarTable;
    private javax.swing.JButton quitarTaladroButton;
    private javax.swing.JLabel quitarTaladroLabel;
    private javax.swing.JButton reincorporarButton;
    private javax.swing.JRadioButton semestralRadioButton;
    private javax.swing.JTextField slantBsTextField;
    private javax.swing.JTextField slantTextField;
    private javax.swing.JTextField slantUsdTextField;
    private javax.swing.JTextField superficialBsTextField;
    private javax.swing.JTextField superficialTextField;
    private javax.swing.JTextField superficialUsdTextField;
    private javax.swing.JComboBox sustitutoComboBox;
    private javax.swing.JTextField taladroNombreTextField;
    private javax.swing.JLabel taladroSelectedLabel;
    private javax.swing.JLabel taladroSelectedLabel1;
    private javax.swing.JLabel taladroSelectedMoverLabel;
    private javax.swing.JTabbedPane taladroTabbedPane;
    private javax.swing.JTable taladrosDescontinuadosTable;
    private javax.swing.JTable taladrosNuevosTable;
    private javax.swing.JTable taladrosTable;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolBarPanel;
    private javax.swing.JRadioButton trimestralRadioButton;
    private javax.swing.JTextField vecesTextField;
    private javax.swing.JTextField verticalBsTextField;
    private javax.swing.JTextField verticalTextField;
    private javax.swing.JTextField verticalUsdTextField;
    // End of variables declaration//GEN-END:variables

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

    private DocumentListener taladroDocListener = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            checkJTValue();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkJTValue();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            checkJTValue();
        }
    };

    private final PropertyChangeListener dateListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("date".equals(evt.getPropertyName())) {
                modify();
            }
        }

    };
}
