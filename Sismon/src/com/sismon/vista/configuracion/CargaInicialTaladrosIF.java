package com.sismon.vista.configuracion;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.ParidadManager;
import com.sismon.jpamanager.TaladroManager;
import com.sismon.model.Paridad;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroHasFase;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import com.sismon.vista.utilities.Utils;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
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
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class CargaInicialTaladrosIF extends javax.swing.JInternalFrame
        implements PropertyChangeListener {

    private static CargaInicialTaladrosIF instance = null;

    private Paridad paridad;
    private File archivo;
    private List<String[]> data;
    private List<Taladro> taladros;
    private Map<String, Taladro> taladrosMap;
    private boolean isEditable = false;
    private Taladro taladroSelected;

    private static final DateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final NumberFormat numFormat = new DecimalFormat("###,###,###,##0.00");

    private final ParidadManager paridadManager;
    private final TaladroManager taladroManager;

    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form ConfiguraTaladrosIF
     */
    private CargaInicialTaladrosIF() {
        initComponents();
        setTitle("Carga Inicial de Taladros");
        setFrameIcon(icon);
        paridadManager = new ParidadManager();
        taladroManager = new TaladroManager();
        taladrosMap = new HashMap<>();

        init();
    }

    public static CargaInicialTaladrosIF getInstance() {
        if (instance == null) {
            instance = new CargaInicialTaladrosIF();
        }
        return instance;
    }

    private void init() {
        progressBar.setVisible(false);
        loadInicialInfo();
    }

    private void configureListeners() {
        mudanzaMacollaDiasTextField.getDocument().addDocumentListener(docListener);
        mudanzaPozoDiasTextField.getDocument().addDocumentListener(docListener);
        superficialDiasTextField.getDocument().addDocumentListener(docListener);
        slantDiasTextField.getDocument().addDocumentListener(docListener);
        pilotoDiasTextField.getDocument().addDocumentListener(docListener);
        verticalDiasTextField.getDocument().addDocumentListener(docListener);
        intermedioDiasTextField.getDocument().addDocumentListener(docListener);
        productorDiasTextField.getDocument().addDocumentListener(docListener);
        completacionDiasTextField.getDocument().addDocumentListener(docListener);
        conexionDiasTextField.getDocument().addDocumentListener(docListener);

        mudanzaMacollaBsTextField.getDocument().addDocumentListener(docListener);
        mudanzaPozoBsTextField.getDocument().addDocumentListener(docListener);
        superficialBsTextField.getDocument().addDocumentListener(docListener);
        slantBsTextField.getDocument().addDocumentListener(docListener);
        pilotoBsTextField.getDocument().addDocumentListener(docListener);
        verticalBsTextField.getDocument().addDocumentListener(docListener);
        intermedioBsTextField.getDocument().addDocumentListener(docListener);
        productorBsTextField.getDocument().addDocumentListener(docListener);
        completacionBsTextField.getDocument().addDocumentListener(docListener);
        conexionBsTextField.getDocument().addDocumentListener(docListener);

        mudanzaMacollaUsdTextField.getDocument().addDocumentListener(docListener);
        mudanzaPozoUsdTextField.getDocument().addDocumentListener(docListener);
        superficialUsdTextField.getDocument().addDocumentListener(docListener);
        slantUsdTextField.getDocument().addDocumentListener(docListener);
        pilotoUsdTextField.getDocument().addDocumentListener(docListener);
        verticalUsdTextField.getDocument().addDocumentListener(docListener);
        intermedioUsdTextField.getDocument().addDocumentListener(docListener);
        productorUsdTextField.getDocument().addDocumentListener(docListener);
        completacionUsdTextField.getDocument().addDocumentListener(docListener);
        conexionUsdTextField.getDocument().addDocumentListener(docListener);
    }

    private void removeListeners() {
        mudanzaMacollaDiasTextField.getDocument().removeDocumentListener(docListener);
        mudanzaPozoDiasTextField.getDocument().removeDocumentListener(docListener);
        superficialDiasTextField.getDocument().removeDocumentListener(docListener);
        slantDiasTextField.getDocument().removeDocumentListener(docListener);
        pilotoDiasTextField.getDocument().removeDocumentListener(docListener);
        verticalDiasTextField.getDocument().removeDocumentListener(docListener);
        intermedioDiasTextField.getDocument().removeDocumentListener(docListener);
        productorDiasTextField.getDocument().removeDocumentListener(docListener);
        completacionDiasTextField.getDocument().removeDocumentListener(docListener);
        conexionDiasTextField.getDocument().removeDocumentListener(docListener);

        mudanzaMacollaBsTextField.getDocument().removeDocumentListener(docListener);
        mudanzaPozoBsTextField.getDocument().removeDocumentListener(docListener);
        superficialBsTextField.getDocument().removeDocumentListener(docListener);
        slantBsTextField.getDocument().removeDocumentListener(docListener);
        pilotoBsTextField.getDocument().removeDocumentListener(docListener);
        verticalBsTextField.getDocument().removeDocumentListener(docListener);
        intermedioBsTextField.getDocument().removeDocumentListener(docListener);
        productorBsTextField.getDocument().removeDocumentListener(docListener);
        completacionBsTextField.getDocument().removeDocumentListener(docListener);
        conexionBsTextField.getDocument().removeDocumentListener(docListener);

        mudanzaMacollaUsdTextField.getDocument().removeDocumentListener(docListener);
        mudanzaPozoUsdTextField.getDocument().removeDocumentListener(docListener);
        superficialUsdTextField.getDocument().removeDocumentListener(docListener);
        slantUsdTextField.getDocument().removeDocumentListener(docListener);
        pilotoUsdTextField.getDocument().removeDocumentListener(docListener);
        verticalUsdTextField.getDocument().removeDocumentListener(docListener);
        intermedioUsdTextField.getDocument().removeDocumentListener(docListener);
        productorUsdTextField.getDocument().removeDocumentListener(docListener);
        completacionUsdTextField.getDocument().removeDocumentListener(docListener);
        conexionUsdTextField.getDocument().removeDocumentListener(docListener);
    }

    private void loadInicialInfo() {
        List<Taladro> taladroList = taladroManager.findAllBase();
        cargarButton.setVisible(false);
        if (!taladroList.isEmpty()) {
            fillDataTable(taladroList);
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        taladrosTable.setModel(new DefaultTableModel());
        fileTextField.setText(null);
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
    }

    private void saveTaladrosData() {
        if (!isEditable) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                boolean salvado = false;

                @Override
                protected Void doInBackground() throws Exception {
                    progressBar.setVisible(true);
                    int progress = 0;
                    int maxCount = taladros.size();
                    int count = 0;
                    setProgress(0);
                    try {
                        progressBar.setIndeterminate(false);
                        for (Taladro tal : taladros) {
                            List<TaladroHasFase> fasesTal
                                    = (List<TaladroHasFase>) tal.getTaladroHasFaseCollection();
                            for (TaladroHasFase thf : fasesTal) {
                                thf.setTaladroId(tal);
                            }
                            taladroManager.create(tal);
                            count++;
                            progress = 100 * count / maxCount;
                            setProgress(progress);
                        }
                        salvado = true;
                    } catch (Exception e) {
                        sismonlog.logger.log(Level.SEVERE, "Error guardadndo archivos", e);
                    }

                    return null;
                }

                @Override
                protected void done() {
                    if (salvado) {
                        loadInicialInfo();
                        Contexto.showMessage("Datos guardados con éxito", Constantes.MENSAJE_INFO);
                        progressBar.setVisible(false);
                        saveButton.setEnabled(false);
                    } else {
                        Contexto.showMessage("Error guardando los Datos ...", Constantes.MENSAJE_ERROR);
                    }
                }

            };

            worker.addPropertyChangeListener(this);
            worker.execute();
        } else {
            List<TaladroHasFase> faseList = (List<TaladroHasFase>) taladroSelected.getTaladroHasFaseCollection();
            fillFaseData(faseList);
            taladroManager.edit(taladroSelected);
            Contexto.showMessage("Datos guardados con éxito", Constantes.MENSAJE_INFO);
            isEditable = false;
            saveButton.setEnabled(false);
            removeListeners();
        }
    }

    private void fillFaseData(List<TaladroHasFase> faseList) {
        Paridad paridadActiva = null;
        try {
            paridadActiva = paridadManager.find(Constantes.PARIDAD_ACTIVA);
        } catch (Exception ex) {
            sismonlog.logger.log(Level.SEVERE, "No pudo cargar la paridad activa", ex);
            Contexto.showMessage("No pudo cargar la paridad activa", Constantes.MENSAJE_ERROR);
        }
        try {
            for (TaladroHasFase fase : faseList) {
                switch (fase.getFase()) {
                    case Constantes.FASE_MUDANZA_ENTRE_MACOLLAS:
                        fase.setDias(Utils.parseDouble(mudanzaMacollaDiasTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(mudanzaMacollaBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(mudanzaMacollaUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_MUDANZA_ENTRE_POZOS:
                        fase.setDias(Utils.parseDouble(mudanzaPozoDiasTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(mudanzaPozoBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(mudanzaPozoUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_SUPERFICIAL:
                        fase.setDias(Utils.parseDouble(superficialDiasTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(superficialBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(superficialUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_SLANT:
                        fase.setDias(Utils.parseDouble(slantDiasTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(slantBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(slantUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_PILOTO:
                        fase.setDias(Utils.parseDouble(pilotoDiasTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(pilotoBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(pilotoUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;  
                    case Constantes.FASE_VERTICAL:
                        fase.setDias(Utils.parseDouble(verticalDiasTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(verticalBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(verticalUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;   
                    case Constantes.FASE_INTERMEDIO:
                        fase.setDias(Utils.parseDouble(intermedioDiasTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(intermedioBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(intermedioUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;
                    case Constantes.FASE_PRODUCTOR:
                        fase.setDias(Utils.parseDouble(productorDiasTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(productorBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(productorUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break; 
                    case Constantes.FASE_COMPLETACION :
                        fase.setDias(Utils.parseDouble(completacionDiasTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(completacionBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(completacionUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;  
                    case Constantes.FASE_CONEXION:
                        fase.setDias(Utils.parseDouble(conexionDiasTextField.getText()));
                        fase.setCostoBs(Utils.parseDouble(conexionBsTextField.getText()));
                        fase.setCostoUsd(Utils.parseDouble(conexionUsdTextField.getText()));
                        fase.setCostoEquiv(fase.getCostoUsd() + fase.getCostoBs() / paridadActiva.getValor());
                        break;    
                }
            }
            saveButton.setEnabled(true);
        } catch (ParseException ex) {
            sismonlog.logger.log(Level.SEVERE, "Error: dato introducido no correcto", ex);
            Contexto.showMessage("Introdujo un valor incorrecto", Constantes.MENSAJE_ERROR);
        }
    }

    private void fillDataTable() {
        String[] titles = {"Nombre", "Fecha Disp Inicial"};
        Object[][] datos = new Object[data.size()][titles.length];
        taladros = new ArrayList<>();
        if (!data.isEmpty()) {
            int i = 0;
            for (String[] array : data) {
                Taladro taladro = makeTaladro(array);
                taladros.add(taladro);
                datos[i][0] = taladro.getNombre();
                datos[i][1] = dFormat.format(taladro.getFechaInicial());
                taladrosMap.put(taladro.getNombre(), taladro);
                i++;
            }
        }
        TableModel model = new DefaultTableModel(datos, titles);
        taladrosTable.setModel(model);
        saveButton.setEnabled(true);
    }

    private void fillDataTable(List<Taladro> taladroList) {
        String[] titles = {"Nombre", "Fecha Disp Inicial"};
        Object[][] datos = new Object[taladroList.size()][titles.length];
        int i = 0;
        for (Taladro taladro : taladroList) {
            datos[i][0] = taladro.getNombre();
            datos[i][1] = dFormat.format(taladro.getFechaInicial());
            taladrosMap.put(taladro.getNombre(), taladro);
            i++;
        }

        TableModel model = new DefaultTableModel(datos, titles);
        taladrosTable.setModel(model);
    }

    private Taladro makeTaladro(String[] array) {
        Taladro taladro = new Taladro();
        taladro.setNombre(array[0]);

        Date fecha = parseFecha(array[1]);
        taladro.setFechaInicial(fecha);
        taladro.setTaladroHasFaseCollection(makeFaseList(array, fecha));
        taladro.setEscenarioId(null);

        return taladro;
    }

    private List<TaladroHasFase> makeFaseList(String[] array, Date fecha) {
        TaladroHasFase fase;
        List<TaladroHasFase> fases = new ArrayList<>();
        double bs;
        double usd;

        if (array[2] != null && !array[2].isEmpty()) {
            fase = new TaladroHasFase();
            fase.setFase(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS);
            fase.setDias(parseDouble(array[2]));
            bs = parseDouble(array[12]);
            usd = parseDouble(array[22]);
            fase.setCostoBs(bs);
            fase.setCostoUsd(usd);
            fase.setCostoEquiv(calculateCostoEqui(bs, usd, fecha));
            fases.add(fase);
        }

        if (array[3] != null && !array[3].isEmpty()) {
            fase = new TaladroHasFase();
            fase.setFase(Constantes.FASE_MUDANZA_ENTRE_POZOS);
            fase.setDias(parseDouble(array[3]));
            bs = parseDouble(array[13]);
            usd = parseDouble(array[23]);
            fase.setCostoBs(bs);
            fase.setCostoUsd(usd);
            fase.setCostoEquiv(calculateCostoEqui(bs, usd, fecha));
            fases.add(fase);
        }

        if (array[4] != null && !array[4].isEmpty()) {
            fase = new TaladroHasFase();
            fase.setFase(Constantes.FASE_SUPERFICIAL);
            fase.setDias(parseDouble(array[4]));
            bs = parseDouble(array[14]);
            usd = parseDouble(array[24]);
            fase.setCostoBs(bs);
            fase.setCostoUsd(usd);
            fase.setCostoEquiv(calculateCostoEqui(bs, usd, fecha));
            fases.add(fase);
        }

        if (array[5] != null && !array[5].isEmpty()) {
            fase = new TaladroHasFase();
            fase.setFase(Constantes.FASE_SLANT);
            fase.setDias(parseDouble(array[5]));
            bs = parseDouble(array[15]);
            usd = parseDouble(array[25]);
            fase.setCostoBs(bs);
            fase.setCostoUsd(usd);
            fase.setCostoEquiv(calculateCostoEqui(bs, usd, fecha));
            fases.add(fase);
        }

        if (array[6] != null && !array[6].isEmpty()) {
            fase = new TaladroHasFase();
            fase.setFase(Constantes.FASE_PILOTO);
            fase.setDias(parseDouble(array[6]));
            bs = parseDouble(array[16]);
            usd = parseDouble(array[26]);
            fase.setCostoBs(bs);
            fase.setCostoUsd(usd);
            fase.setCostoEquiv(calculateCostoEqui(bs, usd, fecha));
            fases.add(fase);
        }

        if (array[7] != null && !array[7].isEmpty()) {
            fase = new TaladroHasFase();
            fase.setFase(Constantes.FASE_VERTICAL);
            fase.setDias(parseDouble(array[7]));
            bs = parseDouble(array[17]);
            usd = parseDouble(array[27]);
            fase.setCostoBs(bs);
            fase.setCostoUsd(usd);
            fase.setCostoEquiv(calculateCostoEqui(bs, usd, fecha));
            fases.add(fase);
        }

        if (array[8] != null && !array[8].isEmpty()) {
            fase = new TaladroHasFase();
            fase.setFase(Constantes.FASE_INTERMEDIO);
            fase.setDias(parseDouble(array[8]));
            bs = parseDouble(array[18]);
            usd = parseDouble(array[28]);
            fase.setCostoBs(bs);
            fase.setCostoUsd(usd);
            fase.setCostoEquiv(calculateCostoEqui(bs, usd, fecha));
            fases.add(fase);
        }

        if (array[9] != null && !array[9].isEmpty()) {
            fase = new TaladroHasFase();
            fase.setFase(Constantes.FASE_PRODUCTOR);
            fase.setDias(parseDouble(array[9]));
            bs = parseDouble(array[19]);
            usd = parseDouble(array[29]);
            fase.setCostoBs(bs);
            fase.setCostoUsd(usd);
            fase.setCostoEquiv(calculateCostoEqui(bs, usd, fecha));
            fases.add(fase);
        }

        if (array[10] != null && !array[10].isEmpty()) {
            fase = new TaladroHasFase();
            fase.setFase(Constantes.FASE_COMPLETACION);
            fase.setDias(parseDouble(array[10]));
            bs = parseDouble(array[20]);
            usd = parseDouble(array[30]);
            fase.setCostoBs(bs);
            fase.setCostoUsd(usd);
            fase.setCostoEquiv(calculateCostoEqui(bs, usd, fecha));
            fases.add(fase);
        }

        if (array[11] != null && !array[11].isEmpty()) {
            fase = new TaladroHasFase();
            fase.setFase(Constantes.FASE_CONEXION);
            fase.setDias(parseDouble(array[11]));
            bs = parseDouble(array[21]);
            usd = parseDouble(array[31]);
            fase.setCostoBs(bs);
            fase.setCostoUsd(usd);
            fase.setCostoEquiv(calculateCostoEqui(bs, usd, fecha));
            fases.add(fase);
        }

        return fases;
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

    private Double calculateCostoEqui(Double bs, Double usd, Date fecha) {
        Paridad paridad = paridadManager.find(fecha);
        return bs / paridad.getValor() + usd;
    }

    private double parseDouble(String numero) {
        Double doble;
        String convertido = numero.replaceAll("[^\\d,\\.]++", "");
        if (convertido.matches(".+\\.\\d+,\\d+$")) {
            return Double.parseDouble(convertido.replaceAll("\\.", "").replaceAll(",", "."));
        }
        if (convertido.matches(".+,\\d+\\.\\d+$")) {
            return Double.parseDouble(convertido.replaceAll(",", ""));
        }
        doble = Double.parseDouble(convertido.replaceAll(",", "."));

        return doble;
    }

    private void fillDataFields(Taladro taladroSelected) {
        List<TaladroHasFase> fases = (List<TaladroHasFase>) taladroSelected.getTaladroHasFaseCollection();
        for (TaladroHasFase fase : fases) {
            switch (fase.getFase()) {
                case Constantes.FASE_MUDANZA_ENTRE_MACOLLAS:
                    mudanzaMacollaDiasTextField.setText(String.valueOf(fase.getDias()));
                    mudanzaMacollaBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    mudanzaMacollaUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_MUDANZA_ENTRE_POZOS:
                    mudanzaPozoDiasTextField.setText(String.valueOf(fase.getDias()));
                    mudanzaPozoBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    mudanzaPozoUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_SUPERFICIAL:
                    superficialDiasTextField.setText(String.valueOf(fase.getDias()));
                    superficialBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    superficialUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_SLANT:
                    slantDiasTextField.setText(String.valueOf(fase.getDias()));
                    slantBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    slantUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_PILOTO:
                    pilotoDiasTextField.setText(String.valueOf(fase.getDias()));
                    pilotoBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    pilotoUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_VERTICAL:
                    verticalDiasTextField.setText(String.valueOf(fase.getDias()));
                    verticalBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    verticalUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_INTERMEDIO:
                    intermedioDiasTextField.setText(String.valueOf(fase.getDias()));
                    intermedioBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    intermedioUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_PRODUCTOR:
                    productorDiasTextField.setText(String.valueOf(fase.getDias()));
                    productorBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    productorUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_COMPLETACION:
                    completacionDiasTextField.setText(String.valueOf(fase.getDias()));
                    completacionBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    completacionUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
                case Constantes.FASE_CONEXION:
                    conexionDiasTextField.setText(String.valueOf(fase.getDias()));
                    conexionBsTextField.setText(numFormat.format(fase.getCostoBs()));
                    conexionUsdTextField.setText(numFormat.format(fase.getCostoUsd()));
                    break;
            }
        }
    }

    private void cargaArchivo() {
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);

        List<Paridad> paridades = paridadManager.findAll();
        if (paridades.isEmpty()) {
            Contexto.showMessage("No puede configurar los taladros sin tener "
                    + "previamente la paridad cambiaria configurada", Constantes.MENSAJE_ERROR);
            return;
        }

        try {
            data = new ArrayList<>();

            String[] encabezado = {"Taladro", "F Disponible", "Mdz Macolla", "Mdz Pozo", "Superficial", "Slant", "Piloto",
                "Vertical", "Intermedio", "Productor", "Completación", "Conexión",
                "Bs Mdz Macolla", "Bs Mdz Pozo", "Bs Superficial", "Bs Slant", "Bs Piloto",
                "BsVertical", "Bs Intermedi", "Bs Productor", "Bs Completación", "Bs Conexión", "Bs Evaluacion",
                "US$ Mdz Macolla", "US$ Mdz Pozo", "US$ Superficial", "US$ Slant", "US$ Piloto",
                "US$Vertical", "US$ Intermedio", "US$ Productor", "US$ Completación", "US$ Conexión", "US$ Evaluacion"};

            BufferedReader input = new BufferedReader(new FileReader(archivo));
            String linea;
            int counter = 0;
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
                                        } else {
                                            if (!dato.equalsIgnoreCase("f")) {
                                                dataRead[columna] = dato;
                                            }
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
                    mensajeLabel.setText((counter - 2) + " registros de taladros leidos.");
                }
            }
            fillDataTable();
            isEditable = false;
        } catch (Exception e) {
            Contexto.showMessage("Archivo seleccionado es inválido", Constantes.MENSAJE_ERROR);
        }
    }

    private void checkJTValue() {
        isEditable = true;
        saveButton.setEnabled(true);
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
        filePanel = new javax.swing.JPanel();
        fileTextField = new javax.swing.JTextField();
        mensajeLabel = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taladrosTable = new javax.swing.JTable();
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
        jLabel31 = new javax.swing.JLabel();
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
        jLabel32 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        toolBar = new javax.swing.JToolBar();
        saveButton = new javax.swing.JButton();
        selectFileButton = new javax.swing.JButton();
        cargarButton = new javax.swing.JButton();
        statusBarPanel = new javax.swing.JPanel();
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

        mensajeLabel.setText(" ");

        jLabel33.setText("Nombre de archivo:");

        javax.swing.GroupLayout filePanelLayout = new javax.swing.GroupLayout(filePanel);
        filePanel.setLayout(filePanelLayout);
        filePanelLayout.setHorizontalGroup(
            filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filePanelLayout.createSequentialGroup()
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(filePanelLayout.createSequentialGroup()
                        .addGap(145, 145, 145)
                        .addComponent(mensajeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(filePanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        filePanelLayout.setVerticalGroup(
            filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addGap(18, 18, 18)
                .addComponent(mensajeLabel))
        );

        taladrosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        taladrosTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                taladrosTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(taladrosTable);

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
                        .addGap(82, 82, 82)
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
                .addContainerGap(495, Short.MAX_VALUE))
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
                .addContainerGap(24, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Nro. de Días", jPanel1);

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

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel31.setText("Bs.");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaMacollaBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(intermedioBsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(superficialBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(productorBsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(completacionBsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(conexionBsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(mudanzaPozoBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(slantBsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                        .addGap(42, 42, 42)
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(pilotoBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14)
                                .addGap(6, 6, 6)
                                .addComponent(verticalBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(46, 46, 46)
                        .addComponent(jLabel31)))
                .addContainerGap(124, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {completacionBsTextField, conexionBsTextField, intermedioBsTextField, pilotoBsTextField, productorBsTextField, slantBsTextField, superficialBsTextField, verticalBsTextField});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(mudanzaMacollaBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(mudanzaPozoBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                            .addComponent(conexionBsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel31)))
                .addContainerGap(24, Short.MAX_VALUE))
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

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel32.setText("US$");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel7)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(superficialUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(slantUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(intermedioUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(productorUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(completacionUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pilotoUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(mudanzaMacollaUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mudanzaPozoUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(conexionUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(verticalUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(45, 45, 45)
                .addComponent(jLabel32)
                .addContainerGap(106, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {completacionUsdTextField, conexionUsdTextField, intermedioUsdTextField, mudanzaPozoUsdTextField, pilotoUsdTextField, productorUsdTextField, slantUsdTextField, superficialUsdTextField, verticalUsdTextField});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(mudanzaMacollaUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mudanzaPozoUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(superficialUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(slantUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(pilotoUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(verticalUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(intermedioUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(productorUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(completacionUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(conexionUsdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel32)))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Costo Us$", jPanel3);

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel34.setText("Lista de Taladros en el Campo");

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        selectFileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconbuscar26.png"))); // NOI18N
        selectFileButton.setText("Buscar");
        selectFileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectFileButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectFileButtonActionPerformed(evt);
            }
        });
        toolBar.add(selectFileButton);

        cargarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconUnloadarchivo26.png"))); // NOI18N
        cargarButton.setText("Cargar");
        cargarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cargarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cargarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarButtonActionPerformed(evt);
            }
        });
        toolBar.add(cargarButton);

        javax.swing.GroupLayout statusBarPanelLayout = new javax.swing.GroupLayout(statusBarPanel);
        statusBarPanel.setLayout(statusBarPanelLayout);
        statusBarPanelLayout.setHorizontalGroup(
            statusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusBarPanelLayout.createSequentialGroup()
                .addContainerGap(669, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        statusBarPanelLayout.setVerticalGroup(
            statusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusBarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        toolBar.add(statusBarPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(backPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selectFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectFileButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileInputStream fis;
        try {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                archivo = fileChooser.getSelectedFile();
                fis = new FileInputStream(archivo);
                fileTextField.setText(archivo.getCanonicalPath());
            } else {
                Contexto.showMessage("Acción cancelada por el usuario",
                        Constantes.MENSAJE_INFO);
            }
        } catch (IOException ex) {
            Contexto.showMessage("Error leyendo el archivo",
                    Constantes.MENSAJE_ERROR);
            sismonlog.logger.log(Level.SEVERE, "Error leyendo el archivo", ex);
        }
        cargaArchivo();
    }//GEN-LAST:event_selectFileButtonActionPerformed

    private void cargarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarButtonActionPerformed
        cargaArchivo();
    }//GEN-LAST:event_cargarButtonActionPerformed

    private void taladrosTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_taladrosTableMouseClicked
        String nombreTaladro = (String) taladrosTable.getValueAt(taladrosTable.getSelectedRow(), 0);
        taladroSelected = taladrosMap.get(nombreTaladro);
        fillDataFields(taladroSelected);
        configureListeners();
    }//GEN-LAST:event_taladrosTableMouseClicked

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        saveTaladrosData();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        Contexto.setActiveFrame(instance);
    }//GEN-LAST:event_onActivated

    private void onDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onDeactivated
        // no hace nada
    }//GEN-LAST:event_onDeactivated


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JButton cargarButton;
    private javax.swing.JTextField completacionBsTextField;
    private javax.swing.JTextField completacionDiasTextField;
    private javax.swing.JTextField completacionUsdTextField;
    private javax.swing.JTextField conexionBsTextField;
    private javax.swing.JTextField conexionDiasTextField;
    private javax.swing.JTextField conexionUsdTextField;
    private javax.swing.JPanel filePanel;
    private javax.swing.JTextField fileTextField;
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
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel mensajeLabel;
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
    private javax.swing.JButton selectFileButton;
    private javax.swing.JTextField slantBsTextField;
    private javax.swing.JTextField slantDiasTextField;
    private javax.swing.JTextField slantUsdTextField;
    private javax.swing.JPanel statusBarPanel;
    private javax.swing.JTextField superficialBsTextField;
    private javax.swing.JTextField superficialDiasTextField;
    private javax.swing.JTextField superficialUsdTextField;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable taladrosTable;
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

    private DocumentListener docListener = new DocumentListener() {

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
}
