package com.sismon.vista.escenario;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.jpamanager.ParidadManager;
import com.sismon.jpamanager.TaladroHasFaseManager;
import com.sismon.jpamanager.TaladroManager;
import com.sismon.model.Escenario;
import com.sismon.model.Paridad;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroHasFase;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import com.sismon.vista.utilities.VistaUtilities;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class GestionFaseTaladrosIF extends javax.swing.JInternalFrame
        implements PropertyChangeListener {

    private static GestionFaseTaladrosIF instance = null;

    private Paridad paridad;
    private Escenario escenarioSelected;
    private List<String[]> data;
    private List<Taladro> taladros;
    private Map<String, Taladro> taladrosMap;
    private boolean changeOk = false;
    private boolean isEditing = false;
    private Taladro taladroSelected;
    private Date fechaSelected;

    private static final DateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final NumberFormat numFormat = new DecimalFormat("###,###,###,##0.00");

    private final ParidadManager paridadManager;
    private final TaladroManager taladroManager;
    private final EscenarioManager escenarioManager;
    private final TaladroHasFaseManager faseManager;

    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form ConfiguraTaladrosIF
     */
    private GestionFaseTaladrosIF() {
        initComponents();
        setTitle("Gestión de Fases de Taladro");
        setFrameIcon(icon);
        paridadManager = new ParidadManager();
        taladroManager = new TaladroManager();
        escenarioManager = new EscenarioManager();
        faseManager = new TaladroHasFaseManager();

        taladrosMap = new HashMap<>();

        init();
    }

    public static GestionFaseTaladrosIF getInstance() {
        if (instance == null) {
            instance = new GestionFaseTaladrosIF();
        }
        return instance;
    }

    private void init() {
        progressBar.setVisible(false);
        try {
            paridad = paridadManager.find(Constantes.PARIDAD_ACTIVA);
        } catch (Exception ex) {
            sismonlog.logger.log(Level.SEVERE, null, ex);
        }
    }

    private void configureListeners() {
        mudanzaMacollaDiasTextField.getDocument().addDocumentListener(docListener);
        mudanzaMacollaBsTextField.getDocument().addDocumentListener(docListener);
        mudanzaMacollaUsdTextField.getDocument().addDocumentListener(docListener);

        mudanzaPozoDiasTextField.getDocument().addDocumentListener(docListener);
        mudanzaPozoBsTextField.getDocument().addDocumentListener(docListener);
        mudanzaPozoUsdTextField.getDocument().addDocumentListener(docListener);

        superficialDiasTextField.getDocument().addDocumentListener(docListener);
        superficialBsTextField.getDocument().addDocumentListener(docListener);
        superficialUsdTextField.getDocument().addDocumentListener(docListener);

        pilotoDiasTextField.getDocument().addDocumentListener(docListener);
        pilotoBsTextField.getDocument().addDocumentListener(docListener);
        pilotoUsdTextField.getDocument().addDocumentListener(docListener);

        slantDiasTextField.getDocument().addDocumentListener(docListener);
        slantBsTextField.getDocument().addDocumentListener(docListener);
        slantUsdTextField.getDocument().addDocumentListener(docListener);

        verticalDiasTextField.getDocument().addDocumentListener(docListener);
        verticalBsTextField.getDocument().addDocumentListener(docListener);
        verticalUsdTextField.getDocument().addDocumentListener(docListener);

        intermedioDiasTextField.getDocument().addDocumentListener(docListener);
        intermedioBsTextField.getDocument().addDocumentListener(docListener);
        intermedioUsdTextField.getDocument().addDocumentListener(docListener);

        productorDiasTextField.getDocument().addDocumentListener(docListener);
        productorBsTextField.getDocument().addDocumentListener(docListener);
        productorUsdTextField.getDocument().addDocumentListener(docListener);

        completacionDiasTextField.getDocument().addDocumentListener(docListener);
        completacionBsTextField.getDocument().addDocumentListener(docListener);
        completacionUsdTextField.getDocument().addDocumentListener(docListener);

        conexionDiasTextField.getDocument().addDocumentListener(docListener);
        conexionBsTextField.getDocument().addDocumentListener(docListener);
        conexionUsdTextField.getDocument().addDocumentListener(docListener);

        fechaDateChooser.getDateEditor().addPropertyChangeListener(dateListener);
    }

    private void removeListeners() {
        mudanzaMacollaDiasTextField.getDocument().removeDocumentListener(docListener);
        mudanzaMacollaBsTextField.getDocument().removeDocumentListener(docListener);
        mudanzaMacollaUsdTextField.getDocument().removeDocumentListener(docListener);

        mudanzaPozoDiasTextField.getDocument().removeDocumentListener(docListener);
        mudanzaPozoBsTextField.getDocument().removeDocumentListener(docListener);
        mudanzaPozoUsdTextField.getDocument().removeDocumentListener(docListener);

        superficialDiasTextField.getDocument().removeDocumentListener(docListener);
        superficialBsTextField.getDocument().removeDocumentListener(docListener);
        superficialUsdTextField.getDocument().removeDocumentListener(docListener);

        pilotoDiasTextField.getDocument().removeDocumentListener(docListener);
        pilotoBsTextField.getDocument().removeDocumentListener(docListener);
        pilotoUsdTextField.getDocument().removeDocumentListener(docListener);

        slantDiasTextField.getDocument().removeDocumentListener(docListener);
        slantBsTextField.getDocument().removeDocumentListener(docListener);
        slantUsdTextField.getDocument().removeDocumentListener(docListener);

        verticalDiasTextField.getDocument().removeDocumentListener(docListener);
        verticalBsTextField.getDocument().removeDocumentListener(docListener);
        verticalUsdTextField.getDocument().removeDocumentListener(docListener);

        intermedioDiasTextField.getDocument().removeDocumentListener(docListener);
        intermedioBsTextField.getDocument().removeDocumentListener(docListener);
        intermedioUsdTextField.getDocument().removeDocumentListener(docListener);

        productorDiasTextField.getDocument().removeDocumentListener(docListener);
        productorBsTextField.getDocument().removeDocumentListener(docListener);
        productorUsdTextField.getDocument().removeDocumentListener(docListener);

        completacionDiasTextField.getDocument().removeDocumentListener(docListener);
        completacionBsTextField.getDocument().removeDocumentListener(docListener);
        completacionUsdTextField.getDocument().removeDocumentListener(docListener);

        conexionDiasTextField.getDocument().removeDocumentListener(docListener);
        conexionBsTextField.getDocument().removeDocumentListener(docListener);
        conexionUsdTextField.getDocument().removeDocumentListener(docListener);

        fechaDateChooser.getDateEditor().removePropertyChangeListener(dateListener);
    }

    private void fillTaladrosComboBox() {
        List<Taladro> taladroList = taladroManager.findAll(escenarioSelected);
        taladrosComboBox.removeAllItems();
        taladrosComboBox.addItem("... seleccione Taladro");
        if (!taladroList.isEmpty()) {
            taladroList.stream().forEach(tal -> {
                taladrosComboBox.addItem(tal);
            });
        } else {
            clearForm();
        }
    }

    private void fillEscenarioComboBox() {
        escenarioComboBox.removeAllItems();
        escenarioComboBox.addItem("... seleccione Escenario");
        List<Escenario> escenarios = escenarioManager.findAllMV(false);
        if (!escenarios.isEmpty()) {
            escenarios.stream().forEach(esc -> {
                escenarioComboBox.addItem(esc);
            });
        }
    }

    private void clearForm() {
        mudanzaMacollaDiasTextField.setText(null);
        mudanzaMacollaBsTextField.setText(null);
        mudanzaMacollaUsdTextField.setText(null);

        mudanzaPozoDiasTextField.setText(null);
        mudanzaPozoBsTextField.setText(null);
        mudanzaPozoUsdTextField.setText(null);

        superficialDiasTextField.setText(null);
        superficialBsTextField.setText(null);
        superficialUsdTextField.setText(null);

        slantDiasTextField.setText(null);
        slantBsTextField.setText(null);
        slantUsdTextField.setText(null);

        pilotoDiasTextField.setText(null);
        pilotoBsTextField.setText(null);
        pilotoUsdTextField.setText(null);

        verticalDiasTextField.setText(null);
        verticalBsTextField.setText(null);
        verticalUsdTextField.setText(null);

        intermedioDiasTextField.setText(null);
        intermedioBsTextField.setText(null);
        intermedioUsdTextField.setText(null);

        productorDiasTextField.setText(null);
        productorBsTextField.setText(null);
        productorUsdTextField.setText(null);

        completacionDiasTextField.setText(null);
        completacionBsTextField.setText(null);
        completacionUsdTextField.setText(null);

        conexionDiasTextField.setText(null);
        conexionBsTextField.setText(null);
        conexionUsdTextField.setText(null);

        changeOk = true;
        saveButton.setEnabled(false);
        fechaDateChooser.setDate(null);
    }

    private void fillFechaTable() {
        fechasTable.setModel(new DefaultTableModel());
        
        List<Date> fechas = faseManager.findAllDates(taladroSelected, escenarioSelected);
        String[] titles = {"Fecha"};
        Object[][] datos = new Object[fechas.size()][titles.length];
        int i = 0;
        for (Date fecha : fechas) {
            datos[i][0] = dFormat.format(fecha);
            i++;
        }

        DefaultTableModel model = new DefaultTableModel(datos, titles);
        fechasTable.setModel(model);
    }

    private Date parseFecha(String fechaStr) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            date = formatter.parse(fechaStr);
        } catch (ParseException e) {
            sismonlog.logger.log(Level.SEVERE, "Error convirtiendo fecha del archivo", e);
        }
        return date;
    }

    private void updateForm(Date fecha) {
        changeOk = false;
        saveButton.setEnabled(false);
        List<TaladroHasFase> fases = faseManager.findAll(taladroSelected, escenarioSelected, fecha);
        for (TaladroHasFase fase : fases) {
            switch (fase.getFase()) {
                case Constantes.FASE_MUDANZA_ENTRE_MACOLLAS:
                    mudanzaMacollaDiasTextField.setText(numFormat.format(fase.getDias()));
                    mudanzaMacollaBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    mudanzaMacollaUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_MUDANZA_ENTRE_POZOS:
                    mudanzaPozoDiasTextField.setText(numFormat.format(fase.getDias()));
                    mudanzaPozoBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    mudanzaPozoUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_SUPERFICIAL:
                    superficialDiasTextField.setText(numFormat.format(fase.getDias()));
                    superficialBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    superficialUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_SLANT:
                    slantDiasTextField.setText(numFormat.format(fase.getDias()));
                    slantBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    slantUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_PILOTO:
                    pilotoDiasTextField.setText(numFormat.format(fase.getDias()));
                    pilotoBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    pilotoUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_VERTICAL:
                    verticalDiasTextField.setText(numFormat.format(fase.getDias()));
                    verticalBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    verticalUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_INTERMEDIO:
                    intermedioDiasTextField.setText(numFormat.format(fase.getDias()));
                    intermedioBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    intermedioUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_PRODUCTOR:
                    productorDiasTextField.setText(numFormat.format(fase.getDias()));
                    productorBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    productorUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_COMPLETACION:
                    completacionDiasTextField.setText(numFormat.format(fase.getDias()));
                    completacionBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    completacionUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_CONEXION:
                    conexionDiasTextField.setText(numFormat.format(fase.getDias()));
                    conexionBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    conexionUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
            }
        }
        changeOk = true;
    }

    private void updateModel() {
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        if (!changeOk) {
            return;
        }
        saveButton.setEnabled(false);
        List<TaladroHasFase> fases = faseManager.findAll(taladroSelected, escenarioSelected, fechaSelected);
        try {
            if (isEditing) {
                for (TaladroHasFase fase : fases) {
                    switch (fase.getFase()) {
                        case Constantes.FASE_MUDANZA_ENTRE_MACOLLAS:
                            fase.setDias(VistaUtilities.parseDouble(mudanzaMacollaDiasTextField.getText()));
                            fase.setCostoBs(VistaUtilities.parseDouble(mudanzaMacollaBsTextField.getText()));
                            fase.setCostoUsd(VistaUtilities.parseDouble(mudanzaMacollaUsdTextField.getText()));
                            fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                            break;
                        case Constantes.FASE_MUDANZA_ENTRE_POZOS:
                            fase.setDias(VistaUtilities.parseDouble(mudanzaPozoDiasTextField.getText()));
                            fase.setCostoBs(VistaUtilities.parseDouble(mudanzaPozoBsTextField.getText()));
                            fase.setCostoUsd(VistaUtilities.parseDouble(mudanzaPozoUsdTextField.getText()));
                            fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                            break;
                        case Constantes.FASE_SUPERFICIAL:
                            fase.setDias(VistaUtilities.parseDouble(superficialDiasTextField.getText()));
                            fase.setCostoBs(VistaUtilities.parseDouble(superficialBsTextField.getText()));
                            fase.setCostoUsd(VistaUtilities.parseDouble(superficialUsdTextField.getText()));
                            fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                            break;
                        case Constantes.FASE_SLANT:
                            fase.setDias(VistaUtilities.parseDouble(slantDiasTextField.getText()));
                            fase.setCostoBs(VistaUtilities.parseDouble(slantBsTextField.getText()));
                            fase.setCostoUsd(VistaUtilities.parseDouble(slantUsdTextField.getText()));
                            fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                            break;
                        case Constantes.FASE_PILOTO:
                            fase.setDias(VistaUtilities.parseDouble(pilotoDiasTextField.getText()));
                            fase.setCostoBs(VistaUtilities.parseDouble(pilotoBsTextField.getText()));
                            fase.setCostoUsd(VistaUtilities.parseDouble(pilotoUsdTextField.getText()));
                            fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                            break;
                        case Constantes.FASE_VERTICAL:
                            fase.setDias(VistaUtilities.parseDouble(verticalDiasTextField.getText()));
                            fase.setCostoBs(VistaUtilities.parseDouble(verticalBsTextField.getText()));
                            fase.setCostoUsd(VistaUtilities.parseDouble(verticalUsdTextField.getText()));
                            fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                            break;
                        case Constantes.FASE_INTERMEDIO:
                            fase.setDias(VistaUtilities.parseDouble(intermedioDiasTextField.getText()));
                            fase.setCostoBs(VistaUtilities.parseDouble(intermedioBsTextField.getText()));
                            fase.setCostoUsd(VistaUtilities.parseDouble(intermedioUsdTextField.getText()));
                            fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                            break;
                        case Constantes.FASE_PRODUCTOR:
                            fase.setDias(VistaUtilities.parseDouble(productorDiasTextField.getText()));
                            fase.setCostoBs(VistaUtilities.parseDouble(productorBsTextField.getText()));
                            fase.setCostoUsd(VistaUtilities.parseDouble(productorUsdTextField.getText()));
                            fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                            break;
                        case Constantes.FASE_COMPLETACION:
                            fase.setDias(VistaUtilities.parseDouble(completacionDiasTextField.getText()));
                            fase.setCostoBs(VistaUtilities.parseDouble(completacionBsTextField.getText()));
                            fase.setCostoUsd(VistaUtilities.parseDouble(completacionUsdTextField.getText()));
                            fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                            break;
                        case Constantes.FASE_CONEXION:
                            fase.setDias(VistaUtilities.parseDouble(conexionDiasTextField.getText()));
                            fase.setCostoBs(VistaUtilities.parseDouble(conexionBsTextField.getText()));
                            fase.setCostoUsd(VistaUtilities.parseDouble(conexionUsdTextField.getText()));
                            fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                            break;
                    }
                    faseManager.batchEdit(fases);
                    Contexto.showMessage("Fase de taladro actualizada con éxito", Constantes.MENSAJE_INFO);
                }
            } else {
                if (isValidDateModification(fechaSelected)) {
                
                    List<TaladroHasFase> faseList = new ArrayList();
                    // fase mudanza entre macollas
                    TaladroHasFase fase = new TaladroHasFase();
                    fase.setFase(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS);
                    fase.setEscenarioId(escenarioSelected);
                    fase.setTaladroId(taladroSelected);
                    fase.setFecha(fechaSelected);
                    if(!mudanzaMacollaDiasTextField.getText().isEmpty()){
                        fase.setDias(VistaUtilities.parseDouble(mudanzaMacollaDiasTextField.getText()));
                    } else {
                        fase.setDias(0.0);
                    }

                    if (!mudanzaMacollaBsTextField.getText().isEmpty()) {
                        fase.setCostoBs(VistaUtilities.parseDouble(mudanzaMacollaBsTextField.getText()));
                    } else {
                        fase.setCostoBs(0.0);
                    }

                    if (!mudanzaMacollaUsdTextField.getText().isEmpty()) {
                        fase.setCostoUsd(VistaUtilities.parseDouble(mudanzaMacollaUsdTextField.getText()));
                    } else {
                        fase.setCostoUsd(0.0);
                    }
                    fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                    faseList.add(fase);

                    // fase mudanze entre pozos
                    fase = new TaladroHasFase();
                    fase.setFase(Constantes.FASE_MUDANZA_ENTRE_POZOS);
                    fase.setEscenarioId(escenarioSelected);
                    fase.setTaladroId(taladroSelected);
                    fase.setFecha(fechaSelected);
                    if (!mudanzaPozoDiasTextField.getText().isEmpty()) {
                        fase.setDias(VistaUtilities.parseDouble(mudanzaPozoDiasTextField.getText()));
                    } else {
                        fase.setDias(0.0);
                    }

                    if (!mudanzaPozoBsTextField.getText().isEmpty()) {
                        fase.setCostoBs(VistaUtilities.parseDouble(mudanzaPozoBsTextField.getText()));
                    } else {
                        fase.setCostoBs(0.0);
                    }

                    if (!mudanzaPozoUsdTextField.getText().isEmpty()) {
                        fase.setCostoUsd(VistaUtilities.parseDouble(mudanzaPozoUsdTextField.getText()));
                    } else {
                        fase.setCostoUsd(0.0);
                    }
                    fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                    faseList.add(fase);

                    // fase superficial
                    fase = new TaladroHasFase();
                    fase.setFase(Constantes.FASE_SUPERFICIAL);
                    fase.setEscenarioId(escenarioSelected);
                    fase.setTaladroId(taladroSelected);
                    fase.setFecha(fechaSelected);
                    if (!superficialDiasTextField.getText().isEmpty()) {
                        fase.setDias(VistaUtilities.parseDouble(superficialDiasTextField.getText()));
                    } else {
                        fase.setDias(0.0);
                    }

                    if (!superficialBsTextField.getText().isEmpty()) {
                        fase.setCostoBs(VistaUtilities.parseDouble(superficialBsTextField.getText()));
                    } else {
                        fase.setCostoBs(0.0);
                    }

                    if (!superficialUsdTextField.getText().isEmpty()) {
                        fase.setCostoUsd(VistaUtilities.parseDouble(superficialUsdTextField.getText()));
                    } else {
                        fase.setCostoUsd(0.0);
                    }
                    fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                    faseList.add(fase);

                    // fase slant
                    fase = new TaladroHasFase();
                    fase.setFase(Constantes.FASE_SLANT);
                    fase.setEscenarioId(escenarioSelected);
                    fase.setTaladroId(taladroSelected);
                    fase.setFecha(fechaSelected);
                    if (!slantDiasTextField.getText().isEmpty()) {
                        fase.setDias(VistaUtilities.parseDouble(slantDiasTextField.getText()));
                    } else {
                        fase.setDias(0.0);
                    }

                    if (!slantBsTextField.getText().isEmpty()) {
                        fase.setCostoBs(VistaUtilities.parseDouble(slantBsTextField.getText()));
                    } else {
                        fase.setCostoBs(0.0);
                    }

                    if (!slantUsdTextField.getText().isEmpty()) {
                        fase.setCostoUsd(VistaUtilities.parseDouble(slantUsdTextField.getText()));
                    } else {
                        fase.setCostoUsd(0.0);
                    }
                    fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                    faseList.add(fase);

                    // fase piloto
                    fase = new TaladroHasFase();
                    fase.setFase(Constantes.FASE_PILOTO);
                    fase.setEscenarioId(escenarioSelected);
                    fase.setTaladroId(taladroSelected);
                    fase.setFecha(fechaSelected);
                    if (!pilotoDiasTextField.getText().isEmpty()) {
                        fase.setDias(VistaUtilities.parseDouble(pilotoDiasTextField.getText()));
                    } else {
                        fase.setDias(0.0);
                    }

                    if (!pilotoBsTextField.getText().isEmpty()) {
                        fase.setCostoBs(VistaUtilities.parseDouble(pilotoBsTextField.getText()));
                    } else {
                        fase.setCostoBs(0.0);
                    }

                    if (!pilotoUsdTextField.getText().isEmpty()) {
                        fase.setCostoUsd(VistaUtilities.parseDouble(pilotoUsdTextField.getText()));
                    } else {
                        fase.setCostoUsd(0.0);
                    }
                    fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                    faseList.add(fase);

                    // fase vertical
                    fase = new TaladroHasFase();
                    fase.setFase(Constantes.FASE_VERTICAL);
                    fase.setEscenarioId(escenarioSelected);
                    fase.setTaladroId(taladroSelected);
                    fase.setFecha(fechaSelected);
                    if (!verticalDiasTextField.getText().isEmpty()) {
                        fase.setDias(VistaUtilities.parseDouble(verticalDiasTextField.getText()));
                    } else {
                        fase.setDias(0.0);
                    }

                    if (!verticalBsTextField.getText().isEmpty()) {
                        fase.setCostoBs(VistaUtilities.parseDouble(verticalBsTextField.getText()));
                    } else {
                        fase.setCostoBs(0.0);
                    }

                    if (!verticalUsdTextField.getText().isEmpty()) {
                        fase.setCostoUsd(VistaUtilities.parseDouble(verticalUsdTextField.getText()));
                    } else {
                        fase.setCostoUsd(0.0);
                    }
                    fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                    faseList.add(fase);

                    // fase intermedio
                    fase = new TaladroHasFase();
                    fase.setFase(Constantes.FASE_INTERMEDIO);
                    fase.setEscenarioId(escenarioSelected);
                    fase.setTaladroId(taladroSelected);
                    fase.setFecha(fechaSelected);
                    if (!intermedioDiasTextField.getText().isEmpty()) {
                        fase.setDias(VistaUtilities.parseDouble(intermedioDiasTextField.getText()));
                    } else {
                        fase.setDias(0.0);
                    }

                    if (!intermedioBsTextField.getText().isEmpty()) {
                        fase.setCostoBs(VistaUtilities.parseDouble(intermedioBsTextField.getText()));
                    } else {
                        fase.setCostoBs(0.0);
                    }

                    if (!intermedioUsdTextField.getText().isEmpty()) {
                        fase.setCostoUsd(VistaUtilities.parseDouble(intermedioUsdTextField.getText()));
                    } else {
                        fase.setCostoUsd(0.0);
                    }
                    fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                    faseList.add(fase);

                    // fase productor
                    fase = new TaladroHasFase();
                    fase.setFase(Constantes.FASE_PRODUCTOR);
                    fase.setEscenarioId(escenarioSelected);
                    fase.setTaladroId(taladroSelected);
                    fase.setFecha(fechaSelected);
                    if (!productorDiasTextField.getText().isEmpty()) {
                        fase.setDias(VistaUtilities.parseDouble(productorDiasTextField.getText()));
                    } else {
                        fase.setDias(0.0);
                    }

                    if (!productorBsTextField.getText().isEmpty()) {
                        fase.setCostoBs(VistaUtilities.parseDouble(productorBsTextField.getText()));
                    } else {
                        fase.setCostoBs(0.0);
                    }

                    if (!productorUsdTextField.getText().isEmpty()) {
                        fase.setCostoUsd(VistaUtilities.parseDouble(productorUsdTextField.getText()));
                    } else {
                        fase.setCostoUsd(0.0);
                    }
                    fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                    faseList.add(fase);

                    // fase completación
                    fase = new TaladroHasFase();
                    fase.setFase(Constantes.FASE_COMPLETACION);
                    fase.setEscenarioId(escenarioSelected);
                    fase.setTaladroId(taladroSelected);
                    fase.setFecha(fechaSelected);
                    if (!completacionDiasTextField.getText().isEmpty()) {
                        fase.setDias(VistaUtilities.parseDouble(completacionDiasTextField.getText()));
                    } else {
                        fase.setDias(0.0);
                    }

                    if (!completacionBsTextField.getText().isEmpty()) {
                        fase.setCostoBs(VistaUtilities.parseDouble(completacionBsTextField.getText()));
                    } else {
                        fase.setCostoBs(0.0);
                    }

                    if (!completacionUsdTextField.getText().isEmpty()) {
                        fase.setCostoUsd(VistaUtilities.parseDouble(completacionUsdTextField.getText()));
                    } else {
                        fase.setCostoUsd(0.0);
                    }
                    fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                    faseList.add(fase);

                    // fase conexion
                    fase = new TaladroHasFase();
                    fase.setFase(Constantes.FASE_CONEXION);
                    fase.setEscenarioId(escenarioSelected);
                    fase.setTaladroId(taladroSelected);
                    fase.setFecha(fechaSelected);
                    if (!conexionDiasTextField.getText().isEmpty()) {
                        fase.setDias(VistaUtilities.parseDouble(conexionDiasTextField.getText()));
                    } else {
                        fase.setDias(0.0);
                    }

                    if (!conexionBsTextField.getText().isEmpty()) {
                        fase.setCostoBs(VistaUtilities.parseDouble(conexionBsTextField.getText()));
                    } else {
                        fase.setCostoBs(0.0);
                    }

                    if (!conexionUsdTextField.getText().isEmpty()) {
                        fase.setCostoUsd(VistaUtilities.parseDouble(conexionUsdTextField.getText()));
                    } else {
                        fase.setCostoUsd(0.0);
                    }
                    fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridad.getValor());
                    faseList.add(fase);

                    faseManager.batchSave(faseList);
                    Contexto.showMessage("Fase de taladro creada con éxito", Constantes.MENSAJE_INFO);
                    fillFechaTable();
                    clearForm();
                } else {
                    showCierreWarning();
                }
            }
        } catch (ParseException e) {
            Contexto.showMessage("Los campos sólo aceptan valores numéricos", Constantes.MENSAJE_ERROR);
        }
    }

    private void modify() {
        saveButton.setEnabled(true);
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
        fechasTable = new javax.swing.JTable();
        tabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        mudanzaMacollaDiasTextField = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        mudanzaPozoDiasTextField = new javax.swing.JTextField();
        verticalDiasTextField = new javax.swing.JTextField();
        conexionDiasTextField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        pilotoDiasTextField = new javax.swing.JTextField();
        completacionDiasTextField = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        productorDiasTextField = new javax.swing.JTextField();
        slantDiasTextField = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        intermedioDiasTextField = new javax.swing.JTextField();
        superficialDiasTextField = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        mudanzaMacollaBsTextField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        mudanzaPozoBsTextField = new javax.swing.JTextField();
        verticalBsTextField = new javax.swing.JTextField();
        conexionBsTextField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        pilotoBsTextField = new javax.swing.JTextField();
        completacionBsTextField = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        productorBsTextField = new javax.swing.JTextField();
        slantBsTextField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        intermedioBsTextField = new javax.swing.JTextField();
        superficialBsTextField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        mudanzaMacollaUsdTextField = new javax.swing.JTextField();
        mudanzaPozoUsdTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        superficialUsdTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        slantUsdTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        pilotoUsdTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        verticalUsdTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        intermedioUsdTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        productorUsdTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        completacionUsdTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        conexionUsdTextField = new javax.swing.JTextField();
        taladrosComboBox = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel32 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        fechaDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel33 = new javax.swing.JLabel();
        toolBar = new javax.swing.JToolBar();
        saveButton = new javax.swing.JButton();
        statusBarPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
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

        fechasTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        fechasTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fechasTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(fechasTable);

        jLabel21.setText("Mudanza entre Macollas:");

        mudanzaMacollaDiasTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel22.setText("Mudanza entre Pozos:");

        mudanzaPozoDiasTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        verticalDiasTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        conexionDiasTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel23.setText("Conexión:");

        jLabel24.setText("Vertical:");

        pilotoDiasTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        completacionDiasTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel25.setText("Completación:");

        jLabel26.setText("Piloto:");

        productorDiasTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        slantDiasTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel27.setText("Productor:");

        jLabel28.setText("Slant:");

        intermedioDiasTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        superficialDiasTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel29.setText("Intermedio:");

        jLabel30.setText("Superficial:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaMacollaDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaPozoDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel29)
                            .addComponent(jLabel30))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(superficialDiasTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                            .addComponent(intermedioDiasTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(slantDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addComponent(jLabel26))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(productorDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel25)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(pilotoDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(jLabel24))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(completacionDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel23)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(verticalDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(conexionDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(252, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {completacionDiasTextField, conexionDiasTextField, intermedioDiasTextField, mudanzaMacollaDiasTextField, mudanzaPozoDiasTextField, pilotoDiasTextField, productorDiasTextField, slantDiasTextField, superficialDiasTextField, verticalDiasTextField});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(mudanzaMacollaDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mudanzaPozoDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(superficialDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(slantDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(pilotoDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(verticalDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(intermedioDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(jLabel27)
                    .addComponent(productorDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(completacionDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(conexionDiasTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Nro Días", jPanel1);

        jLabel11.setText("Mudanza entre Macollas:");

        mudanzaMacollaBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel12.setText("Mudanza entre Pozos:");

        mudanzaPozoBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        verticalBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        conexionBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel13.setText("Conexión:");

        jLabel14.setText("Vertical:");

        pilotoBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        completacionBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel15.setText("Completación:");

        jLabel16.setText("Piloto:");

        productorBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        slantBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel17.setText("Productor:");

        jLabel18.setText("Slant:");

        intermedioBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        superficialBsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel19.setText("Intermedio:");

        jLabel20.setText("Superficial:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaMacollaBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaPozoBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(intermedioBsTextField))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(superficialBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(productorBsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(completacionBsTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(conexionBsTextField))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(slantBsTextField)
                                .addGap(42, 42, 42)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pilotoBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14)
                                .addGap(6, 6, 6)
                                .addComponent(verticalBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {completacionBsTextField, conexionBsTextField, intermedioBsTextField, pilotoBsTextField, productorBsTextField, slantBsTextField, superficialBsTextField, verticalBsTextField});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(mudanzaMacollaBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mudanzaPozoBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(superficialBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(slantBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(pilotoBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(verticalBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(intermedioBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel17)
                    .addComponent(productorBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(completacionBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(conexionBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Costo Bs.", jPanel2);

        jLabel1.setText("Mudanza entre Macollas:");

        jLabel2.setText("Mudanza entre Pozos:");

        mudanzaMacollaUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        mudanzaPozoUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setText("Superficial:");

        superficialUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setText("Slant:");

        slantUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setText("Piloto:");

        pilotoUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel6.setText("Vertical:");

        verticalUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setText("Intermedio:");

        intermedioUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel8.setText("Productor:");

        productorUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel9.setText("Completación:");

        completacionUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel10.setText("Conexión:");

        conexionUsdTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaMacollaUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaPozoUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(superficialUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(intermedioUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(slantUsdTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(productorUsdTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pilotoUsdTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(completacionUsdTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel6))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel10)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(verticalUsdTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(conexionUsdTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(72, Short.MAX_VALUE))))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {completacionUsdTextField, conexionUsdTextField, intermedioUsdTextField, pilotoUsdTextField, productorUsdTextField, slantUsdTextField, superficialUsdTextField, verticalUsdTextField});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(mudanzaMacollaUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mudanzaPozoUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(superficialUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(slantUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(pilotoUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(verticalUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(intermedioUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(productorUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(completacionUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(conexionUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Costo Us$", jPanel3);

        taladrosComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                taladrosComboBoxActionPerformed(evt);
            }
        });

        jLabel32.setText("Nombre del Taladro:");

        jPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel31.setText("Fecha de nueva pauta:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fechaDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 363, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fechaDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addGap(27, 27, 27))
        );

        jLabel33.setText("Pautas del taladro registradas:");

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tabbedPane)
                            .addGroup(backPanelLayout.createSequentialGroup()
                                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, backPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel32)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(taladrosComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backPanelLayout.createSequentialGroup()
                                .addComponent(jSeparator1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(taladrosComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconguardar26.png"))); // NOI18N
        saveButton.setText("Guardar");
        saveButton.setToolTipText("Guardar");
        saveButton.setEnabled(false);
        saveButton.setFocusable(false);
        saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        toolBar.add(saveButton);
        saveButton.getAccessibleContext().setAccessibleDescription("");

        escenarioComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escenarioComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout statusBarPanelLayout = new javax.swing.GroupLayout(statusBarPanel);
        statusBarPanel.setLayout(statusBarPanelLayout);
        statusBarPanelLayout.setHorizontalGroup(
            statusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusBarPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 250, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        statusBarPanelLayout.setVerticalGroup(
            statusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusBarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        toolBar.add(statusBarPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        //saveTaladrosData();
        updateModel();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        fillEscenarioComboBox();
        Contexto.setActiveFrame(instance);
        configureListeners();
    }//GEN-LAST:event_onActivated

    private void escenarioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escenarioComboBoxActionPerformed
        if (escenarioComboBox.getSelectedItem() instanceof Escenario) {
            escenarioSelected = (Escenario) escenarioComboBox.getSelectedItem();
            fillTaladrosComboBox();
        } else {
            clearForm();
            fechasTable.setModel(new DefaultTableModel());
            taladrosComboBox.removeAllItems();
            taladrosComboBox.addItem("... seleccione Taladro");
        }
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
    }//GEN-LAST:event_escenarioComboBoxActionPerformed

    private void onDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onDeactivated
        removeListeners();
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
    }//GEN-LAST:event_onDeactivated

    private void fechasTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fechasTableMouseClicked
        try {
            String fechaSt = (String) fechasTable.getValueAt(fechasTable.getSelectedRow(), 0);
            fechaSelected = dFormat.parse(fechaSt);
            updateForm(fechaSelected);
            Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
            changeOk = true;
            isEditing = true;
        } catch (ParseException ex) {
            sismonlog.logger.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_fechasTableMouseClicked

    private void taladrosComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_taladrosComboBoxActionPerformed
        if (taladrosComboBox.getSelectedItem() instanceof Taladro) {
            taladroSelected = (Taladro) taladrosComboBox.getSelectedItem();
            clearForm();
            fillFechaTable();
        }
    }//GEN-LAST:event_taladrosComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JTextField completacionBsTextField;
    private javax.swing.JTextField completacionDiasTextField;
    private javax.swing.JTextField completacionUsdTextField;
    private javax.swing.JTextField conexionBsTextField;
    private javax.swing.JTextField conexionDiasTextField;
    private javax.swing.JTextField conexionUsdTextField;
    private javax.swing.JComboBox escenarioComboBox;
    private com.toedter.calendar.JDateChooser fechaDateChooser;
    private javax.swing.JTable fechasTable;
    private javax.swing.JTextField intermedioBsTextField;
    private javax.swing.JTextField intermedioDiasTextField;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField mudanzaMacollaBsTextField;
    private javax.swing.JTextField mudanzaMacollaDiasTextField;
    private javax.swing.JTextField mudanzaMacollaUsdTextField;
    private javax.swing.JTextField mudanzaPozoBsTextField;
    private javax.swing.JTextField mudanzaPozoDiasTextField;
    private javax.swing.JTextField mudanzaPozoUsdTextField;
    private javax.swing.JTextField pilotoBsTextField;
    private javax.swing.JTextField pilotoDiasTextField;
    private javax.swing.JTextField pilotoUsdTextField;
    private javax.swing.JTextField productorBsTextField;
    private javax.swing.JTextField productorDiasTextField;
    private javax.swing.JTextField productorUsdTextField;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField slantBsTextField;
    private javax.swing.JTextField slantDiasTextField;
    private javax.swing.JTextField slantUsdTextField;
    private javax.swing.JPanel statusBarPanel;
    private javax.swing.JTextField superficialBsTextField;
    private javax.swing.JTextField superficialDiasTextField;
    private javax.swing.JTextField superficialUsdTextField;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JComboBox taladrosComboBox;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JTextField verticalBsTextField;
    private javax.swing.JTextField verticalDiasTextField;
    private javax.swing.JTextField verticalUsdTextField;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }
    }

    private final PropertyChangeListener dateListener = (PropertyChangeEvent evt) -> {
        if (evt.getPropertyName().equals("date")) {
            changeOk = true;
            isEditing = false;
            fechaSelected = fechaDateChooser.getDate();
            modify();
        }
    };

    private DocumentListener docListener = new DocumentListener() {

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
}
