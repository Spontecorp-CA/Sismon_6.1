package com.sismon.vista.configuracion;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.FilaManager;
import com.sismon.jpamanager.PozoManager;
import com.sismon.jpamanager.PozoSecuenciaManager;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Pozo;
import com.sismon.model.PozoSecuencia;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import com.sismon.vista.utilities.SpringUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

public class SecPerfoPozoIF extends javax.swing.JInternalFrame {

    private static SecPerfoPozoIF instance = null;
    
    private Macolla macollaSelected;
    private List<Fila> filaList;
    private List<Pozo> pozosEnMacolla;
    private Map<Pozo, JTextField[]>[] secuenciaTxFieldArrayMap;
    private Map<Integer, Object[]>[] ordenDeProceso;
    
    private final FilaManager filaManager;
    private final PozoManager pozoManager;
    private final PozoSecuenciaManager secuenciaPozosManager;
    
    private boolean[][] firstClickArray;    //filas, cantidad de pozos por fila 
    private int[][] contenido;      //filas, cantidad de pozos por fila
    private JTextField lastField;
    private int cuenta[];
    
    private static final int CANT_COLUMNAS = 3;
    private static final int CANT_FASES = 9;
    
    private Map<String, String> faseMap = new HashMap<>();
    
    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));
    
    private SecPerfoPozoIF() {
        initComponents();
        setFrameIcon(icon);
        
        this.filaManager = new FilaManager();
        this.pozoManager = new PozoManager();
        this.secuenciaPozosManager = new PozoSecuenciaManager();
        
        init();
    }

    public static SecPerfoPozoIF getInstance() {
        if(instance == null){
            instance = new SecPerfoPozoIF();
        }
        return instance;
    }
    
    private void init(){
        faseMap.put(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS, Constantes.FASE_MUDANZA_ENTRE_MACOLLAS);
        faseMap.put(Constantes.FASE_MUDANZA_ENTRE_POZOS, Constantes.FASE_MUDANZA_ENTRE_POZOS);
        faseMap.put(Constantes.FASE_SUPERFICIAL, Constantes.FASE_SUPERFICIAL);
        faseMap.put(Constantes.FASE_SLANT, Constantes.FASE_SLANT);
        faseMap.put(Constantes.FASE_PILOTO, Constantes.FASE_PILOTO);
        faseMap.put(Constantes.FASE_VERTICAL, Constantes.FASE_VERTICAL);
        faseMap.put(Constantes.FASE_INTERMEDIO, Constantes.FASE_INTERMEDIO);
        faseMap.put(Constantes.FASE_PRODUCTOR, Constantes.FASE_PRODUCTOR);
        faseMap.put(Constantes.FASE_COMPLETACION, Constantes.FASE_COMPLETACION);
        faseMap.put(Constantes.FASE_CONEXION, Constantes.FASE_CONEXION);
        faseMap.put(Constantes.FASE_EVALUACION, Constantes.FASE_EVALUACION);
        
        fillMacollasComboBox();
    }
    
    private void fillMacollasComboBox() {
        macollasComboBox.removeAllItems();
        List<Macolla> macollaList = filaManager.findAllConfigurated();
        macollasComboBox.addItem("... seleccione una macolla");
        for (Macolla item : macollaList) {
            macollasComboBox.addItem(item);
        }
    }

    private void makeFilasTabbedPane() {
        // son valores de secuencia nuevos
        boolean old = false;

        filasTabbedPane.removeAll();
        String[] encabezadoPerforacion = {"Pozo", "Mudanza/Macolla", "Mudanza/Pozo",
            "Superficial", "Slant", "Piloto", "Vertical", "Intermedio",
            "Productor"};

        int paneles = filaList.size();

        JPanel[] filasPaneles = new JPanel[paneles];
        secuenciaTxFieldArrayMap = new LinkedHashMap[paneles];
        ordenDeProceso = new TreeMap[paneles];
        cuenta = new int[paneles];

        int maxCantPozos = 0;
        for (Fila fila : filaList) {
            List<Pozo> pozos = pozoManager.findAll(macollaSelected, fila);
            if (pozos.size() > maxCantPozos) {
                maxCantPozos = pozos.size();
            }
        }

        firstClickArray = new boolean[paneles][maxCantPozos * CANT_FASES];
        contenido = new int[paneles][maxCantPozos * CANT_FASES];

        for (int j = 0; j < firstClickArray.length; j++) {
            for (int k = 0; k < firstClickArray[j].length; k++) {
                firstClickArray[j][k] = true;
            }
        }

        int i = 0;
        for (Fila fila : filaList) {
            // se crean los repositorios de cada fila
            secuenciaTxFieldArrayMap[i] = new LinkedHashMap<>();

            // se crea la pestaña de cada fila
            filasPaneles[i] = new JPanel();
            filasPaneles[i].setBackground(Color.WHITE);

            // se agregan los pozos que corresponden a cada fila
            List<Pozo> pozos = pozoManager.findAll(macollaSelected, fila);
            //int cantPozosXFila = fila.getPozoCollection().size();
            int cantPozosXFila = pozos.size();

            filasPaneles[i].setLayout(new SpringLayout());
            for (String label : encabezadoPerforacion) {
                filasPaneles[i].add(new JLabel(label));
            }

            JTextField[] secuenciaTxFieldArray;
            int celdas = 0;
            //List<Pozo> pozos = pozoManager.findAll(macollaSelected, fila);

            for (Pozo pozo : pozos) {
                // inicializa el arreglo de textFields por pozo
                secuenciaTxFieldArray = new JTextField[encabezadoPerforacion.length - 1];
                // agrega el nombre del pozo
                filasPaneles[i].add(new JLabel(pozo.getUbicacion()));

                secuenciaTxFieldArray[0] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[0].setToolTipText("Mudanza de Macolla para pozo " + pozo.getUbicacion());
                secuenciaTxFieldArray[0].setName(
                        Constantes.FASE_MUDANZA_ENTRE_MACOLLAS + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[0]);

                secuenciaTxFieldArray[1] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[1].setToolTipText("Mudanza de Pozo para pozo " + pozo.getUbicacion());
                secuenciaTxFieldArray[1].setName(
                        Constantes.FASE_MUDANZA_ENTRE_POZOS + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[1]);

                secuenciaTxFieldArray[2] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[2].setToolTipText("Superficial " + pozo.getUbicacion());
                secuenciaTxFieldArray[2].setName(Constantes.FASE_SUPERFICIAL + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[2]);

                secuenciaTxFieldArray[3] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[3].setToolTipText("Slant." + pozo.getUbicacion());
                secuenciaTxFieldArray[3].setName(Constantes.FASE_SLANT + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[3]);

                secuenciaTxFieldArray[4] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[4].setToolTipText("Piloto." + pozo.getUbicacion());
                secuenciaTxFieldArray[4].setName(Constantes.FASE_PILOTO + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[4]);

                secuenciaTxFieldArray[5] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[5].setToolTipText("Vertical." + pozo.getUbicacion());
                secuenciaTxFieldArray[5].setName(Constantes.FASE_VERTICAL + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[5]);

                secuenciaTxFieldArray[6] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[6].setToolTipText("Intermedio." + pozo.getUbicacion());
                secuenciaTxFieldArray[6].setName(Constantes.FASE_INTERMEDIO + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[6]);

                secuenciaTxFieldArray[7] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[7].setToolTipText("Productor." + pozo.getUbicacion());
                secuenciaTxFieldArray[7].setName(Constantes.FASE_PRODUCTOR + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[7]);

                secuenciaTxFieldArrayMap[i].put(pozo, secuenciaTxFieldArray);
            }

            SpringUtilities.makeCompactGrid(filasPaneles[i], // el contentPane 
                    (cantPozosXFila + 1), // la cantidad de filas
                    encabezadoPerforacion.length, // la cantidad de columnas
                    6,
                    6,
                    6,
                    6);

            // Se agregan los MouseListener a cada celda
            Component[] componentes = filasPaneles[i].getComponents();
            for (int n = 0; n < componentes.length; n++) {
                if (componentes[n] instanceof JTextField) {
                    final JTextField tf = (JTextField) componentes[n];
                    final int panel = i;
                    tf.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            clickAction(tf, panel, old, 0);
                        }

                    });
                }
            }

            filasTabbedPane.add(fila.getNombre(), filasPaneles[i]);
            i++;
        }
    }

    private boolean ordenarPozosXFecha(Fila fila, int indiceFila) {
        //FaseManager faseManager = new FaseManager();
        Set<PozoSecuencia> secuenciaPozosSet = new TreeSet<>();
        Map<Integer, Object[]> inputDeProceso = new LinkedHashMap<>();
        boolean filaVacia = true;
        for (Map.Entry<Pozo, JTextField[]> pozo : secuenciaTxFieldArrayMap[indiceFila].entrySet()) {
            JTextField[] fases = pozo.getValue();
            for (JTextField faseTextField : fases) {
                if (!faseTextField.getText().equals("") || !faseTextField.getText().isEmpty()) {
                    Object[] etapaPozo = new Object[3];
                    etapaPozo[0] = fila;
                    etapaPozo[1] = pozo.getKey();  // pozo
                    String nombreFase = faseTextField.getName().substring(0, faseTextField.getName()
                            .indexOf(".")); // nombre de la fase
                    //Fase fase = faseManager.find(nombreFase);
                    //etapaPozo[2] = fase;    // fase
                    etapaPozo[2] = nombreFase;
                    inputDeProceso.put(Integer.parseInt(faseTextField.getText()), etapaPozo);
                    filaVacia = false;
                }
            }
        }

        if (!filaVacia) {
            ordenDeProceso[indiceFila] = new TreeMap<>(inputDeProceso);

            for (Map.Entry<Integer, Object[]> secuencia : ordenDeProceso[indiceFila].entrySet()) {
                PozoSecuencia sequence = new PozoSecuencia();
                sequence.setSecuencia(secuencia.getKey());
                sequence.setFilaId((Fila) secuencia.getValue()[0]);
                sequence.setPozoId((Pozo) secuencia.getValue()[1]);
                sequence.setFase((String) secuencia.getValue()[2]);
                secuenciaPozosSet.add(sequence);
            }

            secuenciaPozosManager.batchEdit(secuenciaPozosSet);
        }
        return filaVacia;
    }

    private void savePerforacionTemplate(File file) throws IOException {
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

        final int CANT_TEXT_FIELDS = 8;
        int indexFila = 0;
        for (Fila fila : filaList) {
            out.writeInt(secuenciaTxFieldArrayMap[indexFila].size());
            for (Map.Entry<Pozo, JTextField[]> item : secuenciaTxFieldArrayMap[indexFila].entrySet()) {
                JTextField[] casillas = item.getValue();
                if (casillas.length > 0) {
                    for (int counter = 0; counter < CANT_TEXT_FIELDS; counter++) {
                        if (!casillas[counter].getText().isEmpty()) {
                            out.writeInt(Integer.parseInt(casillas[counter].getText()));
                        } else {
                            out.writeInt(0);
                        }
                    }
                }
            }
            indexFila++;
        }

        if (out != null) {
            out.close();
        }
    }

    private void loadPerforacionTemplate(File file) throws IOException {
        final int CANT_TEXT_FIELDS = 8;
        if (file != null) {
            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

            int cantPozos = 0;
            Integer data = null;

            int indexFila = 0;
            for (Fila fila : filaList) {
                boolean firstLine = true;
                for (Map.Entry<Pozo, JTextField[]> item : secuenciaTxFieldArrayMap[indexFila].entrySet()) {
                    JTextField[] casillas = item.getValue();

                    try {
                        if (firstLine) {
                            cantPozos = in.readInt();
                            firstLine = false;
                            if (secuenciaTxFieldArrayMap[indexFila].size() != cantPozos) {
                                JOptionPane.showMessageDialog(this, "La cantidad de pozos "
                                        + "de esta macolla (" + secuenciaTxFieldArrayMap[indexFila].size()
                                        + ")\ndifiere del configurado en "
                                        + "\n la plantilla seleccionada (" + cantPozos
                                        + ")", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            for (int counter = 0; counter < CANT_TEXT_FIELDS; counter++) {
                                data = in.readInt();
                                if (data != 0) {
                                    casillas[counter].setText(String.valueOf(data));
                                } else {
                                    casillas[counter].setText(null);
                                }
                            }

                        } else {
                            for (int counter = 0; counter < CANT_TEXT_FIELDS; counter++) {
                                data = in.readInt();
                                if (data != 0) {
                                    casillas[counter].setText(String.valueOf(data));
                                } else {
                                    casillas[counter].setText(null);
                                }
                            }
                        }
                    } catch (EOFException e) {
                        sismonlog.logger.log(Level.FINER, "Error cargando los datos de la plantilla", e);
                    }
                }
                indexFila++;
            }
            in.close();
        }
    }

    private void loadStoragedSequence(List<Fila> filas) {
        // se define que son valores cargados
        boolean old = true;
        int ultimaSecuencia = 0;

        resetTabbedPane();
        filasTabbedPane.removeAll();
        String[] encabezadoPerforacion = {"Pozo", "Mudanza/Macolla", "Mudanza/Pozo",
            "Superficial", "Slant", "Piloto", "Vertical", "Intermedio",
            "Productor"};

        int paneles = filas.size();
        JPanel[] filasPaneles = new JPanel[paneles];
        secuenciaTxFieldArrayMap = new LinkedHashMap[paneles];
        ordenDeProceso = new TreeMap[paneles];
        cuenta = new int[paneles];

        int maxCantPozos = 0;
        for (Fila fila : filaList) {
            if (fila.getPozoCollection().size() > maxCantPozos) {
                maxCantPozos = fila.getPozoCollection().size();
            }
        }

        firstClickArray = new boolean[paneles][maxCantPozos * CANT_FASES];
        contenido = new int[paneles][maxCantPozos * CANT_FASES];

        for (int j = 0; j < firstClickArray.length; j++) {
            for (int k = 0; k < firstClickArray[j].length; k++) {
                firstClickArray[j][k] = true;
            }
        }

        int i = 0;
        for (Fila fila : filas) {
            // se obtiene la secuencia de esa fila
            List<PozoSecuencia> secuenciaList = secuenciaPozosManager.find(fila);
            ultimaSecuencia = 0;

            Map<Pozo, PozoSecuencia[]> secuenciaMap = new HashMap<>();
            PozoSecuencia[] secuenciaArray = null;
            int j = 0;
            Pozo pozoTemp = null;
            if (!secuenciaList.isEmpty()) {
                for (PozoSecuencia sequence : secuenciaList) {
                    if (!sequence.getPozoId().equals(pozoTemp)) {
                        j = 0;
                        secuenciaArray = new PozoSecuencia[9]; // <- ojo aqui!!!!
                        pozoTemp = sequence.getPozoId();
                    } else {
                        secuenciaArray = secuenciaMap.get(sequence.getPozoId());
                    }
                    secuenciaArray[j] = sequence;
                    secuenciaMap.put(sequence.getPozoId(), secuenciaArray);
                    j++;
                }
                //old = false;
            }

            // se crean los repositorios de cada fila
            secuenciaTxFieldArrayMap[i] = new LinkedHashMap<>();

            // se crea la pestaña de cada fila
            filasPaneles[i] = new JPanel(new BorderLayout());
            filasPaneles[i].setBackground(Color.WHITE);

            // se agregan los pozos que corresponden a cada fila
            List<Pozo> pozos = pozoManager.findAll(macollaSelected, fila);
            int cantPozosXFila = pozos.size();

            filasPaneles[i].setLayout(new SpringLayout());
            for (String label : encabezadoPerforacion) {
                filasPaneles[i].add(new JLabel(label));
            }

            JTextField[] secuenciaTxFieldArray;
            int celdas = 0;

            

            for (Pozo pozo : pozos) {
                secuenciaTxFieldArray = new JTextField[encabezadoPerforacion.length - 1];
                filasPaneles[i].add(new JLabel(pozo.getUbicacion()));

                secuenciaTxFieldArray[0] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[0].setToolTipText("Mudanza de Macolla para pozo " + pozo.getUbicacion());
                secuenciaTxFieldArray[0].setName(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[0]);

                secuenciaTxFieldArray[1] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[1].setToolTipText("Mudanza de Pozo para pozo " + pozo.getUbicacion());
                secuenciaTxFieldArray[1].setName(Constantes.FASE_MUDANZA_ENTRE_POZOS + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[1]);

                secuenciaTxFieldArray[2] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[2].setToolTipText("Superficial " + pozo.getUbicacion());
                secuenciaTxFieldArray[2].setName(Constantes.FASE_SUPERFICIAL + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[2]);

                secuenciaTxFieldArray[3] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[3].setToolTipText("Slant." + pozo.getUbicacion());
                secuenciaTxFieldArray[3].setName(Constantes.FASE_SLANT + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[3]);

                secuenciaTxFieldArray[4] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[4].setToolTipText("Piloto." + pozo.getUbicacion());
                secuenciaTxFieldArray[4].setName(Constantes.FASE_PILOTO + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[4]);

                secuenciaTxFieldArray[5] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[5].setToolTipText("Vertical." + pozo.getUbicacion());
                secuenciaTxFieldArray[5].setName(Constantes.FASE_VERTICAL + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[5]);

                secuenciaTxFieldArray[6] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[6].setToolTipText("Intermedio." + pozo.getUbicacion());
                secuenciaTxFieldArray[6].setName(Constantes.FASE_INTERMEDIO + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[6]);

                secuenciaTxFieldArray[7] = new JTextField(CANT_COLUMNAS);
                secuenciaTxFieldArray[7].setToolTipText("Productor." + pozo.getUbicacion());
                secuenciaTxFieldArray[7].setName(Constantes.FASE_PRODUCTOR + "." + pozo.getUbicacion()
                        + "." + celdas++);
                filasPaneles[i].add(secuenciaTxFieldArray[7]);

                secuenciaTxFieldArrayMap[i].put(pozo, secuenciaTxFieldArray);
            }

            if (!secuenciaList.isEmpty()) {

                for (Map.Entry<Pozo, JTextField[]> mapa : secuenciaTxFieldArrayMap[i].entrySet()) {
                    Pozo well = mapa.getKey();
                    JTextField[] textFields = mapa.getValue();

                    if (secuenciaMap.get(well) != null) {
                        for (PozoSecuencia sp : secuenciaMap.get(well)) {
                            if (sp != null) {
                                ultimaSecuencia = sp.getSecuencia();
                                switch (sp.getFase()) {
                                    case Constantes.FASE_MUDANZA_ENTRE_MACOLLAS:
                                        textFields[0].setText(String.valueOf(sp.getSecuencia()));
                                        contenido[i][getTextFieldValue(textFields[0])] = sp.getSecuencia();
                                        break;
                                    case Constantes.FASE_MUDANZA_ENTRE_POZOS:
                                        textFields[1].setText(String.valueOf(sp.getSecuencia()));
                                        contenido[i][getTextFieldValue(textFields[1])] = sp.getSecuencia();
                                        break;
                                    case Constantes.FASE_SUPERFICIAL:
                                        textFields[2].setText(String.valueOf(sp.getSecuencia()));
                                        contenido[i][getTextFieldValue(textFields[2])] = sp.getSecuencia();
                                        break;
                                    case Constantes.FASE_SLANT:
                                        textFields[3].setText(String.valueOf(sp.getSecuencia()));
                                        contenido[i][getTextFieldValue(textFields[3])] = sp.getSecuencia();
                                        break;
                                    case Constantes.FASE_PILOTO:
                                        textFields[4].setText(String.valueOf(sp.getSecuencia()));
                                        contenido[i][getTextFieldValue(textFields[4])] = sp.getSecuencia();
                                        break;
                                    case Constantes.FASE_VERTICAL:
                                        textFields[5].setText(String.valueOf(sp.getSecuencia()));
                                        contenido[i][getTextFieldValue(textFields[5])] = sp.getSecuencia();
                                        break;
                                    case Constantes.FASE_INTERMEDIO:
                                        textFields[6].setText(String.valueOf(sp.getSecuencia()));
                                        contenido[i][getTextFieldValue(textFields[6])] = sp.getSecuencia();
                                        break;
                                    case Constantes.FASE_PRODUCTOR:
                                        textFields[7].setText(String.valueOf(sp.getSecuencia()));
                                        contenido[i][getTextFieldValue(textFields[7])] = sp.getSecuencia();
                                        break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }

            SpringUtilities.makeCompactGrid(filasPaneles[i], // el contentPane 
                    cantPozosXFila + 1, // la cantidad de pozos en una fila
                    encabezadoPerforacion.length, // la cantidad de columnas
                    6,
                    6,
                    6,
                    6);

            // Se agregan los MouseListener a cada celda
            Component[] componentes = filasPaneles[i].getComponents();
            for (int n = 0; n < componentes.length; n++) {
                if (componentes[n] instanceof JTextField) {
                    final JTextField tf = (JTextField) componentes[n];
                    final int panel = i;
                    final boolean switche = old;
                    final int lastSecuencia = ultimaSecuencia;
                    tf.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            clickAction(tf, panel, switche, lastSecuencia);
                        }

                    });
                }
            }

            filasTabbedPane.add(fila.getNombre(), filasPaneles[i]);
            i++;

        }
    }

    private int getTextFieldValue(JTextField tf) {
        String newStr = tf.getName().substring(tf.getName().lastIndexOf(".") + 1);
        return Integer.parseInt(newStr);
    }

    private void clickAction(Component component, int panel, boolean old, int ultimaSecuencia) {
        String lastValue;
        JTextField tf = (JTextField) component;
        String newStr = tf.getName().substring(tf.getName().lastIndexOf(".") + 1);
        int indice = Integer.parseInt(newStr);
        if (ultimaSecuencia != 0) {
            lastValue = String.valueOf(ultimaSecuencia);    
        } else {
            lastValue = tf.getText();
        }
        
        firstClickArray[panel][indice] = makeSecuence(tf, firstClickArray[panel][indice],
                indice, panel, old, lastValue);
    }

    private boolean makeSecuence(JTextField textField, boolean firstClick,
            int indice, int panel, boolean old, String lastValue) {
        int tab = filasTabbedPane.getSelectedIndex();

        // ha hecho click por vez primera en la celda
        if (!old) {
            if (firstClick) {
                textField.setText(String.valueOf(++cuenta[panel]));
                firstClick = false;
                contenido[panel][indice] = cuenta[panel];
                lastField = textField;
            } else {    // ya se la dado click anteriomente a la celda
                if (lastField.equals(textField)) {      // repitió el click en la misma celda que le dió anteriormente
                    textField.setText(null);
                    cuenta[panel]--;
                    firstClick = true;
                    contenido[panel][indice] = 0;
                } else {    // está haciendo click sobre una celda anterior a la que ya ha recibido el click
                    updateSequence(textField, panel, tab, old);

                    textField.setText(null);
                    cuenta[panel]--;
                    firstClick = true;
                }
            }
        } else {
            if (!lastValue.isEmpty()) {
                cuenta[panel] = Integer.parseInt(lastValue);
                updateSequence(textField, panel, tab, old);
            } else {
                textField.setText(String.valueOf(cuenta[panel]++));
                contenido[panel][indice] = cuenta[panel];
                lastField = textField;
            }
        }

        return firstClick;
    }

    private void updateSequence(JTextField textField, int panel, int tab, boolean old) {
        int valor = Integer.parseInt(textField.getText());

        for (int i = 0; i < contenido[panel].length; i++) {
            if (contenido[panel][i] > valor) {
                if (!old) {
                    contenido[panel][i] -= 1;
                    old = true;  // agregue esto 
                } else {
                    sismonlog.logger.log(Level.INFO, "Entró en updateSequence por data cargada");
                    contenido[panel][i] = 0;
                }
            } else if (contenido[panel][i] == valor) {
                contenido[panel][i] = 0;
            }
        }

        // Obtiene en que panel está trabajando
        JPanel actualPanel = (JPanel) filasTabbedPane.getComponentAt(tab);
        Component[] componentes = actualPanel.getComponents();
        int ind = 0; // usado para hallar la equivalencia del JTextField con su contenio

        // actualiza la posición de todas las celdas
        final int CANTIDAD_COLUMNAS = 10;
        final int CANT_TEXTFIELDS = 9;
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof JTextField) {
                //ind = i - 11 - (i / 10 - 1);  //la equivalencia de componente a JtextField
                ind = i - CANTIDAD_COLUMNAS - (i / CANT_TEXTFIELDS - 1);
                JTextField tf = (JTextField) componentes[i];

                if (contenido[panel][ind] == 0) {
                    tf.setText(null);
                } else {
                    tf.setText(String.valueOf(contenido[panel][ind]));
                }
            }
        }
    }

    private void resetTabbedPane() {
        try {
            macollaSelected = (Macolla) macollasComboBox.getSelectedItem();
            filaList = filaManager.findAll(macollaSelected);
            pozosEnMacolla = pozoManager.findAll(macollaSelected);
        } catch (ClassCastException e) {
            Contexto.showMessage("Debe seleccionar una macolla", Constantes.MENSAJE_ERROR);
        }

        makeFilasTabbedPane();
        this.repaint();
    }

    private void enableButtons(boolean condition) {
        guardaSecuenciaButton.setEnabled(condition);
        guardarPlantillaButton.setEnabled(condition);
        seleccionarPlantillaButton.setEnabled(condition);
        clearSecuenceButton.setEnabled(condition);
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
        guardaSecuenciaButton = new javax.swing.JButton();
        clearSecuenceButton = new javax.swing.JButton();
        macollasComboBox = new javax.swing.JComboBox();
        seleccionarPlantillaButton = new javax.swing.JButton();
        guardarPlantillaButton = new javax.swing.JButton();
        backPanel = new javax.swing.JPanel();
        filasTabbedPane = new javax.swing.JTabbedPane();
        toolBarPanel = new javax.swing.JPanel();

        setClosable(true);
        setIconifiable(true);
        setTitle("Secuencia de Perforación de Pozos");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                onActivate(evt);
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

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        guardaSecuenciaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconguardar26.png"))); // NOI18N
        guardaSecuenciaButton.setText("Guardar");
        guardaSecuenciaButton.setToolTipText("Guardar");
        guardaSecuenciaButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        guardaSecuenciaButton.setDefaultCapable(false);
        guardaSecuenciaButton.setFocusable(false);
        guardaSecuenciaButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        guardaSecuenciaButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        guardaSecuenciaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardaSecuenciaButtonActionPerformed(evt);
            }
        });
        toolBar.add(guardaSecuenciaButton);

        clearSecuenceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconlimpiar26.png"))); // NOI18N
        clearSecuenceButton.setText("Limpiar");
        clearSecuenceButton.setDefaultCapable(false);
        clearSecuenceButton.setFocusable(false);
        clearSecuenceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearSecuenceButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearSecuenceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearSecuenceButtonActionPerformed(evt);
            }
        });
        toolBar.add(clearSecuenceButton);

        macollasComboBox.setMaximumSize(new java.awt.Dimension(220, 20));
        macollasComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                macollasComboBoxActionPerformed(evt);
            }
        });
        toolBar.add(macollasComboBox);

        seleccionarPlantillaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconloadarchivo26.png"))); // NOI18N
        seleccionarPlantillaButton.setText("Seleccionar Plantilla");
        seleccionarPlantillaButton.setDefaultCapable(false);
        seleccionarPlantillaButton.setFocusable(false);
        seleccionarPlantillaButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        seleccionarPlantillaButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        seleccionarPlantillaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarPlantillaButtonActionPerformed(evt);
            }
        });
        toolBar.add(seleccionarPlantillaButton);

        guardarPlantillaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconUnloadarchivo26.png"))); // NOI18N
        guardarPlantillaButton.setText("Guardar Plantilla");
        guardarPlantillaButton.setFocusable(false);
        guardarPlantillaButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        guardarPlantillaButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        guardarPlantillaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarPlantillaButtonActionPerformed(evt);
            }
        });
        toolBar.add(guardarPlantillaButton);

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(filasTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 951, Short.MAX_VALUE)
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addComponent(filasTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        toolBarPanel.setPreferredSize(new java.awt.Dimension(937, 53));

        javax.swing.GroupLayout toolBarPanelLayout = new javax.swing.GroupLayout(toolBarPanel);
        toolBarPanel.setLayout(toolBarPanelLayout);
        toolBarPanelLayout.setHorizontalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        toolBarPanelLayout.setVerticalGroup(
            toolBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 53, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(toolBarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toolBarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void guardaSecuenciaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardaSecuenciaButtonActionPerformed
        int indiceFila = 0;
        for (Fila fila : filaList) {
            boolean filaVacia = ordenarPozosXFecha(fila, indiceFila);
            if (filaVacia) {
                Contexto.showMessage("La fila " + fila.getNombre() + 
                        " se guardó sin secuencia definida", 
                        Constantes.MENSAJE_WARNING);
            }
            indiceFila++;
        }
        Contexto.showMessage("Secuencia almacenada con éxito", Constantes.MENSAJE_INFO);
        enableButtons(false);
        macollasComboBox.grabFocus();
    }//GEN-LAST:event_guardaSecuenciaButtonActionPerformed

    private void onDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onDeactivated
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        resetTabbedPane();
    }//GEN-LAST:event_onDeactivated

    private void clearSecuenceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearSecuenceButtonActionPerformed
        resetTabbedPane();
    }//GEN-LAST:event_clearSecuenceButtonActionPerformed

    private void guardarPlantillaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarPlantillaButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        int answer = fileChooser.showSaveDialog(this);
        File templateFile = null;
        if (answer == JFileChooser.APPROVE_OPTION) {
            templateFile = fileChooser.getSelectedFile();
            if (!templateFile.getAbsolutePath().endsWith(".pmf")) {
                templateFile = new File(templateFile + ".pmf");
            }
        } else {
            Contexto.showMessage("Acción cancelada por el usuario", Constantes.MENSAJE_INFO);
        }
        try {
            savePerforacionTemplate(templateFile);
            Contexto.showMessage("Plantilla guardada con éxito", Constantes.MENSAJE_INFO);
        } catch (IOException e) {
            sismonlog.logger.log(Level.FINER, "Error seleccionando archivo: ", e);
        }
    }//GEN-LAST:event_guardarPlantillaButtonActionPerformed

    private void seleccionarPlantillaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarPlantillaButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new ExtensionFileFilter("Archivos pmf (configuración de secuencia de pozos)", "pmf");
        fileChooser.setFileFilter(filter);
        int answer = fileChooser.showOpenDialog(this);
        File templateFile = null;
        if (answer == JFileChooser.APPROVE_OPTION) {
            templateFile = fileChooser.getSelectedFile();
        } else {
            Contexto.showMessage("Acción cancelada por el usuario", Constantes.MENSAJE_INFO);
        }
        try {
            loadPerforacionTemplate(templateFile);
        } catch (IOException e) {
            sismonlog.logger.log(Level.FINER, "Error seleccionando archivo: ", e);
        }
    }//GEN-LAST:event_seleccionarPlantillaButtonActionPerformed

    private void macollasComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_macollasComboBoxActionPerformed
        if (!(macollasComboBox.getSelectedItem() instanceof String)) {
            List<Macolla> macollaList = secuenciaPozosManager.findMacollaList();
            macollaSelected = (Macolla) macollasComboBox.getSelectedItem();
            if (macollaList.contains(macollaSelected)) {
                // acciones a tomar en caso de querer modificarla
                Contexto.showMessage("La macolla seleccionada ya tiene una secuencia definida"
                        , Constantes.MENSAJE_WARNING);
                int answer = JOptionPane.showConfirmDialog(this, 
                        "La macolla seleccionada ya tiene una secuencia definida "
                                + "¿Desea modificar esta secuencia?", 
                        "Atención", JOptionPane.YES_NO_OPTION);
                if(answer == JOptionPane.YES_OPTION){
                    List<Fila> filas = filaManager.findAll(macollaSelected);
                    loadStoragedSequence(filas);
                }
            } else {
                resetTabbedPane();
            }
            enableButtons(true);
            Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        }
    }//GEN-LAST:event_macollasComboBoxActionPerformed

    private void onActivate(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivate
        init();
        Contexto.setActiveFrame(instance);
    }//GEN-LAST:event_onActivate


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JButton clearSecuenceButton;
    private javax.swing.JTabbedPane filasTabbedPane;
    private javax.swing.JButton guardaSecuenciaButton;
    private javax.swing.JButton guardarPlantillaButton;
    private javax.swing.JComboBox macollasComboBox;
    private javax.swing.JButton seleccionarPlantillaButton;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolBarPanel;
    // End of variables declaration//GEN-END:variables

    private class ExtensionFileFilter extends FileFilter {

        private String description;
        private String[] extensions;

        public ExtensionFileFilter(String description, String extension) {
            this(description, new String[]{extension});
        }

        public ExtensionFileFilter(String description, String[] extensions) {
            if (description == null) {
                this.description = extensions[0];
            } else {
                this.description = description;
            }
            this.extensions = (String[]) extensions.clone();
            toLower(this.extensions);
        }

        private void toLower(String array[]) {
            for (int i = 0, n = array.length; i < n; i++) {
                array[i] = array[i].toLowerCase();
            }
        }

        public String getDescription() {
            return description;
        }

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            } else {
                String path = file.getAbsolutePath().toLowerCase();
                for (int i = 0, n = extensions.length; i < n; i++) {
                    String extension = extensions[i];
                    if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
}
