package com.sismon.vista.configuracion;

import com.sismon.vista.utilities.MacollasTableModel;
import com.sismon.controller.Constantes;
import com.sismon.jpamanager.CampoManager;
import com.sismon.jpamanager.DistritoManager;
import com.sismon.jpamanager.DivisionManager;
import com.sismon.jpamanager.EmpresaManager;
import com.sismon.jpamanager.MacollaManager;
import com.sismon.jpamanager.ParidadManager;
import com.sismon.jpamanager.PozoManager;
import com.sismon.model.Campo;
import com.sismon.model.Distrito;
import com.sismon.model.Division;
import com.sismon.model.Empresa;
import com.sismon.model.Macolla;
import com.sismon.model.Paridad;
import com.sismon.model.Pozo;
import com.sismon.model.Rampeo;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import com.sismon.vista.utilities.Utils;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class ConfiguraCampoIF extends javax.swing.JInternalFrame 
            implements PropertyChangeListener{

    private static ConfiguraCampoIF instance = null;
    
    private int cantPozos;
    
    private File archivo;
    private Division division;
    private Distrito distrito;
    private Empresa empresa;
    private Campo campo;
    private Paridad paridad;
    
    private Set<Macolla> macollas;
    private Set<Pozo> pozos;
    private Map<Macolla, Set<Pozo>> pozosEnMacolla;
    private Map<Pozo, List<Rampeo>> rampeosEnPozoMap;
    
    private final EmpresaManager empresaManager;
    private final DivisionManager divisionManager;
    private final DistritoManager distritoManager;
    private final ParidadManager paridadManager;
    private final MacollaManager macollaManager;
    private final PozoManager pozoManager;
    private final CampoManager campoManager;

    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form ConfiguraCampoIF
     */
    private ConfiguraCampoIF() {
        initComponents();
        setTitle("Configurar Campo");
        setFrameIcon(icon);
        
        empresaManager = new EmpresaManager();
        divisionManager = new DivisionManager();
        distritoManager = new DistritoManager();
        paridadManager = new ParidadManager();
        macollaManager = new MacollaManager();
        pozoManager = new PozoManager();
        campoManager = new CampoManager();
        
        macollas = new TreeSet<>();
        pozosEnMacolla = new TreeMap<>();
        rampeosEnPozoMap = new HashMap<>();
        init();
    }

    public static ConfiguraCampoIF getInstance() {
        if (instance == null) {
            instance = new ConfiguraCampoIF();
        }
        return instance;
    }
    
    private void init(){
        cargarDatosButton.setVisible(false);
        try {
            paridad = paridadManager.find(Constantes.PARIDAD_ACTIVA);
        } catch (Exception ex) {
            sismonlog.logger.log(Level.SEVERE, "Error obteniendo la paridad", ex);
        }
        progressBar.setVisible(false);
    }
    
    private boolean validateEmpresa(String empresaStr){
        boolean valid = false;
        empresa = empresaManager.find(empresaStr);
        if(empresa != null){
            valid = true;
        }
        return valid;
    }
    
    /**
     * Construye la tabla de datos a mostrar al usuario
     *
     * @param data
     */
    private void constructTablesData(List<Object[]> data) {
        int filaInput = 6;
        
        List<Rampeo> rampeos;
       
        for (Object[] array : data) {

            // define las macollas
            String nombreMacolla = array[0].toString().trim();
            int indexLastGuion = nombreMacolla.lastIndexOf("-");
            String numeroMacollaStr = nombreMacolla.substring(indexLastGuion + 1).trim();;

            // Busca los costos de localización
            String costoStr = array[1].toString();
            String moneda = array[2].toString();

            Macolla macollaNew = new Macolla();
            macollaNew.setNombre(nombreMacolla);
            macollaNew.setNumero(Integer.parseInt(numeroMacollaStr));
            macollaNew.setCampoId(campo);

            try {
                Double costoLoc = Double.parseDouble(costoStr);
                if (moneda.equalsIgnoreCase("USD")) {
                    macollaNew.setCostoLocalizacionUsd(costoLoc);
                    macollaNew.setCostoLocalizacionBs(costoLoc * paridad.getValor());
                } else if (moneda.equalsIgnoreCase("BS")) {
                    macollaNew.setCostoLocalizacionUsd(costoLoc / paridad.getValor());
                    macollaNew.setCostoLocalizacionBs(costoLoc);
                }
            } catch (NumberFormatException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Costo de Localización errado en la fila: ");
                sb.append(filaInput + 1);
                Contexto.showMessage(sb.toString(), Constantes.MENSAJE_ERROR);

            }

            macollas.add(macollaNew);

            // define los pozos
            Pozo pozoNew = new Pozo();
            rampeos = new ArrayList<>();
            
            if (macollas.contains(macollaNew)) {
                pozoNew.setMacollaId(macollaNew);
            } else {
                Contexto.showMessage("Ha ocurrido un error, reporte al administrador", Constantes.MENSAJE_ERROR);
            }
            String ubicacionPozo = array[3].toString().trim();
            pozoNew.setUbicacion(ubicacionPozo);

            try {
                indexLastGuion = ubicacionPozo.lastIndexOf("-");
                String numeroPozoStr = ubicacionPozo.substring(indexLastGuion + 1).trim();
                pozoNew.setNumero(Integer.parseInt(numeroPozoStr));
            } catch (NumberFormatException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Número de pozo errado en la fila: ");
                sb.append(filaInput + 1);
                Contexto.showMessage(sb.toString(), Constantes.MENSAJE_ERROR);
                sismonlog.logger.log(Level.SEVERE, " Error: {0}, con exception {1}", 
                        new Object[]{sb.toString(), e});
            }

            // define el plan estratégico al que pertenece el pozo
            String planPozoStr = array[4].toString();

            String yacimientoStr = array[6].toString();
            String bloqueStr = array[7].toString();

            String tipoPozoStr = array[8].toString();
            String codigoPozoStr = array[9].toString();
            //TipoPozo tipoPozoNew = null;

            boolean encontrado = false;

            String clasePozoStr = null;
            try {
                double clasePozoLeida = Double.parseDouble(array[10].toString());
                int clasePozoInt = (int) clasePozoLeida;
                switch (clasePozoInt) {
                    case 1:
                        clasePozoStr = Constantes.TIPO_PRODUCTOR;
                        break;
                    case 2:
                        clasePozoStr = Constantes.TIPO_INYECTOR;
                        break;
                    case 3:
                        clasePozoStr = Constantes.TIPO_ESTRATIGRAFICO;
                        break;
                    case 4:
                        clasePozoStr = Constantes.TIPO_OBSERVADOR;
                        break;
                    default:
                        StringBuilder sb = new StringBuilder();
                        sb.append("Error de datos en la fila: ");
                        sb.append(filaInput).append(", correspondiente a Tipo o Código de Pozo");
                        Contexto.showMessage(sb.toString(), Constantes.MENSAJE_ERROR);
                        sismonlog.logger.log(Level.SEVERE, "Archivo de datos incorrecto");
                }
            } catch (NumberFormatException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Error de datos en la fila: ");
                sb.append(filaInput).append(", correspondiente a Tipo o Código de Pozo");
                Contexto.showMessage(sb.toString(), Constantes.MENSAJE_ERROR);
                sismonlog.logger.log(Level.SEVERE, "Archivo de datos incorrecto");
            }

            String piStr = array[11].toString();
            String rgpStr = array[12].toString();
            String rgpDiasDeclStr = array[13].toString();
            String rgpIncreAnualStr = array[14].toString();
            String porctAyS = array[15].toString();
            String aysDiasDeclStr = array[16].toString();
            String aysIncreAnualStr = array[17].toString();
            String declAnualStr = array[18].toString();
            String expHiperbolicoStr = array[19].toString();
            String inicioDeclStr = array[20].toString();
            String tasaAbandono = array[21].toString();
            String reservaMax = array[22].toString();
            String apiXPStr = array[23].toString();
            String apiDiluenteStr = array[24].toString();
            String apiMexclaStr = array[25].toString();
            
            
            try {
                pozoNew.setPlan(planPozoStr);
                pozoNew.setTipoPozo(tipoPozoStr);
                pozoNew.setCodigoPozo(codigoPozoStr);
                pozoNew.setClasePozo(clasePozoStr);
                pozoNew.setYacimiento(yacimientoStr);
                pozoNew.setBloque(bloqueStr);
                pozoNew.setPi(Utils.parseDouble(piStr));
                pozoNew.setRgp(Utils.parseDouble(rgpStr));
                pozoNew.setInicioDeclRgp((int) Utils.parseDouble(rgpDiasDeclStr));
                pozoNew.setIncremAnualRgp(Utils.parseDouble(rgpIncreAnualStr));
                pozoNew.setAys(Utils.parseDouble(porctAyS) / 100.0);
                pozoNew.setInicioDeclAys((int) Utils.parseDouble(aysDiasDeclStr));
                pozoNew.setIncremAnualAys(Utils.parseDouble(aysIncreAnualStr) / 100.0);
                pozoNew.setDeclinacion(Utils.parseDouble(declAnualStr) / 100.0);
                double inicioDecl = Utils.parseDouble(inicioDeclStr);
                pozoNew.setExpHiperb(Utils.parseDouble(expHiperbolicoStr));
                pozoNew.setInicioDecl((int) inicioDecl);
                pozoNew.setTasaAbandono(Utils.parseDouble(tasaAbandono));
                pozoNew.setReservaMax(Utils.parseDouble(reservaMax) * 1000000.0);
                pozoNew.setGradoApiXp(Utils.parseDouble(apiXPStr));
                pozoNew.setGradoApiDiluente(Utils.parseDouble(apiDiluenteStr));
                pozoNew.setGradoApiMezcla(Utils.parseDouble(apiMexclaStr));
                
                for(int i = 1; i < 7; i++){
                    Rampeo rampeo = new Rampeo();
                    rampeo.setNumero(i);
                    rampeo.setDias(Utils.parseDouble(array[24 + 2*i].toString()));
                    rampeo.setRpm(Utils.parseDouble(array[25 + 2*i].toString()));
                    rampeo.setPozoId(pozoNew);
                    rampeos.add(rampeo);
                }
                
                rampeosEnPozoMap.put(pozoNew, rampeos);

            } catch (NumberFormatException | ParseException e) {
                Contexto.showMessage("Hay un valor incorrecto en el archivo, "
                        + ", en la linea "+ filaInput + ", corriga y vuelva a intentarlo", 
                        Constantes.MENSAJE_ERROR);
                sismonlog.logger.log(Level.SEVERE, "Archivo de datos incorrecto");
            }
            
            if (pozosEnMacolla.containsKey(macollaNew)) {
                pozos = pozosEnMacolla.get(macollaNew);
                pozos.add(pozoNew);
            } else {
                pozos = new LinkedHashSet<>();
                pozos.add(pozoNew);
            }

            pozosEnMacolla.put(macollaNew, pozos);
            filaInput++;
        }

    }
    
    private void buildMacollaTable() {
        NumberFormat nf = new DecimalFormat("###,###,##0.00");
        String[] columns = {"Número", "Nombre de Macolla", "Costo Localización (US$)", "Cant Pozos"};
        Object[][] data = new Object[macollas.size()][columns.length];
        int i = 0;
        for (Macolla item : macollas) {
            if (item.getNumero() == 0) {
                data[i][0] = "";
            } else {
                data[i][0] = item.getNumero();
            }
            data[i][1] = item.getNombre();
            data[i][2] = nf.format(item.getCostoLocalizacionUsd());
            data[i][3] = pozosEnMacolla.get(item).size();
            
            i++;
        }
        MacollasTableModel model = new MacollasTableModel(columns, data);
        macollasTable.setModel(model);

        TableColumnModel tcm = macollasTable.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(10);

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(JLabel.CENTER);

        tcm.getColumn(0).setCellRenderer(centerRender);
        tcm.getColumn(3).setCellRenderer(centerRender);

        macollasTable.getTableHeader().setDefaultRenderer(centerRender);
        cantMacollasLabel.setText(String.valueOf(macollas.size()));
    }
    
    private void buildPozoTable() {
        DecimalFormat df = new DecimalFormat("###,##0.00");
        PozoManager pozoManager = new PozoManager();
        cantPozos = 0;
        for (Map.Entry<Macolla, Set<Pozo>> clave : pozosEnMacolla.entrySet()) {
            Set<Pozo> tempSet = clave.getValue();
            cantPozos += tempSet.size();
        }

        String[] columns = {"Macolla", "Localización", "PI",
            "RGP", "AyS (%)", "Decl.Anual (%)", "Exp. Hipe.",
            "Inicio Decl(dias)", "T. Abandono (b/d)", "Reserva Max"};
        Object[][] data = new Object[cantPozos][columns.length];

        int i = 0;
        for (Map.Entry<Macolla, Set<Pozo>> clave : pozosEnMacolla.entrySet()) {
            pozos = clave.getValue();
            for (Pozo item : pozos) {
                StringBuilder sb = new StringBuilder();
                sb.append(item.getMacollaId().getNombre()).append("-MACOLLA ");
                sb.append(item.getMacollaId().getNumero());
                data[i][0] = sb.toString();
                data[i][1] = item.getUbicacion();
                data[i][2] = df.format(item.getPi());
                data[i][3] = df.format(item.getRgp());
                data[i][4] = df.format((item.getAys() * 100));
                data[i][5] = df.format((item.getDeclinacion()* 100));
                data[i][6] = df.format((item.getExpHiperb()));
                data[i][7] = df.format(item.getInicioDecl());
                data[i][8] = df.format(item.getTasaAbandono());
                data[i][9] = df.format(item.getReservaMax());
                i++;
            }
        }

        TableModel model = new DefaultTableModel(data, columns);
        pozosTable.setModel(model);
        cantPozosLabel.setText(String.valueOf(cantPozos));
    }
    
    private void buildRampeosTable(){
        String[] titles = {"Pozo", "Rampeo 1", "rpm 1", "Rampeo 2", "rpm 2", 
            "Rampeo 3", "rpm 3", "Rampeo 4", "rpm 4", 
            "Rampeo 5", "rpm 5", "Rampeo 6", "rpm 6"};
        
        Object[][] data = new Object[rampeosEnPozoMap.size()][titles.length];
        
        int i = 0;
        for(Map.Entry<Pozo, List<Rampeo>> map : rampeosEnPozoMap.entrySet()){
            data[i][0] = map.getKey().getUbicacion();
            for(Rampeo rampa : map.getValue()){
                data[i][2 * rampa.getNumero() - 1] = rampa.getDias();
                data[i][2 * rampa.getNumero()] = rampa.getRpm();
            }
            i++;
        }
        
        TableModel model = new DefaultTableModel(data, titles);
        rampeosTable.setModel(model);
    }
    
    private void loadInicialInfo() {
        List<Distrito> distritoList = distritoManager.findAll();

        if (!distritoList.isEmpty()) {
            distrito = distritoList.get(0);
            division = distrito.getDivisionId();
        }

        divisionTextField.setText(division.getNombre());
        distritoTextField.setText(distrito.getNombre());

        PozoManager pozoManager = new PozoManager();

        List<Pozo> pozosList = pozoManager.findAll();
        if (!pozosList.isEmpty()) {
            Macolla macollaEnMapa = null;
            pozosEnMacolla = new TreeMap<>();
            macollas = new TreeSet<>();
            rampeosEnPozoMap = new HashMap<>();
            for (Pozo item : pozosList) {
                Macolla macollaPersisted = item.getMacollaId();
                if (macollaPersisted.equals(macollaEnMapa)) {
                    pozos.add(item);
                } else {
                    pozos = new LinkedHashSet<>();
                    pozos.add(item);
                    macollaEnMapa = macollaPersisted;
                    macollas.add(macollaPersisted);
                }
                pozosEnMacolla.put(macollaPersisted, pozos);
                rampeosEnPozoMap.put(item, new ArrayList(item.getRampeoCollection()));
            }
            buildPozoTable();
            buildMacollaTable();
            buildRampeosTable();
            campo = macollaEnMapa.getCampoId();
            empresa = campo.getEmpresaId();
            campoTextField.setText(campo.getNombre());
            empresaTextField.setText(empresa.getNombre());
        }
    }
    
    private void cargaArchivo() {
        String[] encabezado = {"Macolla", "Costo Loc", "Moneda", "Pozo", "Plan", "Campo", "Yacimiento", "Bloque", "Tipo Pozo",
            "Cod Pozo", "Clase Pozo", "PI", "RGP Ini", "Plateau RGP", "Inc RGP", "% Agua Ini",
            "Plateau %Agua", "Inc %Agua", "Decl Anual", "Exp Dec Hip", "Plateau Prod", "Tasa Aband",
            "Reserva Max", "API Xp", "API Diluente", "API mezcla", "Rampeo1", "rpm1", "Rampeo2", "rpm2",
            "Rampeo3", "rpm3", "Rampeo4", "rpm4", "Rampeo5", "rpm5", "Rampeo6", "rpm6"};

        String divisionStr = null;
        String distritoStr = null;
        String empresaStr = null;

        try {
            if (fileTextField.getText().isEmpty()) {
                Contexto.showMessage("Debe seleccionar un archivo válido a cargar", Constantes.MENSAJE_ERROR);
                selectFileButton.grabFocus();
                return;
            }
            BufferedReader input = new BufferedReader(new FileReader(archivo));
            String linea;
            int counter = 0;
            List<Object[]> data = new ArrayList<>();
            while ((linea = input.readLine()) != null) {
                String[] columns = linea.split(";");
                if (columns.length != 0) {
                    switch (counter) {
                        case 0:
                            divisionStr = columns[1];
                            break;
                        case 1:
                            distritoStr = columns[1];
                            break;
                        case 2:
                            empresaStr = columns[1];
                            if (!validateEmpresa(empresaStr.toUpperCase())) {
                                Contexto.showMessage("Empresa en el archivo no es válida", Constantes.MENSAJE_ERROR);
                                return;
                            }
                            break;
                        case 3:
                        case 4:
                            break;
                        default:
                            int columna = 0;
                            String[] array = new String[encabezado.length];
                            for (String dato : columns) {
                                if (columna < 26) {
                                    if (dato == null || dato.trim().isEmpty()) {
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("El archivo de configuración de campo '");
                                        sb.append(archivo.getName()).append("'");
                                        sb.append(",\n en la linea ").append(counter);
                                        sb.append(" columna ").append(columna).append(", esta en blanco o vacia.");
                                        JOptionPane.showMessageDialog(this, sb.toString(),
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                        return;
                                    } else {
                                        array[columna] = dato;
                                    }
                                } else {
                                    if (dato == null || dato.trim().isEmpty()) {
                                        array[columna] = "0";
                                    } else {
                                        if (!dato.equalsIgnoreCase("f")) {
                                            array[columna] = dato;
                                        }
                                    }
                                }
                                columna++;
                            }
                            data.add(array);
                            break;
                    }
                    counter++;
                } else {
                    Contexto.showMessage((counter - 5) + " registros de pozos leidos.", Constantes.MENSAJE_INFO);
                }
            }

            division = divisionManager.find(divisionStr);
            distrito = distritoManager.find(distritoStr.toUpperCase());

            divisionTextField.setText(division.getNombre());
            distritoTextField.setText(distrito.getNombre());
            empresaTextField.setText(empresa.getNombre());

            Object[] firstRow = data.get(0);
            String campoStr = (String) firstRow[5];
            campo = new Campo();
            campo.setNombre(campoStr);
            campo.setEmpresaId(empresa);
            campo.setDistritoId(distrito);

            campoTextField.setText(campoStr);

            constructTablesData(data);
            buildMacollaTable();

            // construcción de los pozos
            buildPozoTable();

            // construcción de los rampeos
            buildRampeosTable();

        } catch (Exception e) {
            Contexto.showMessage("El archivo seleccionado es inválido", Constantes.MENSAJE_ERROR);
            sismonlog.logger.log(Level.SEVERE,"El archivo seleccionado es inválido", e);
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

        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jToolBar1 = new javax.swing.JToolBar();
        guardarButton = new javax.swing.JButton();
        selectFileButton = new javax.swing.JButton();
        cargarDatosButton = new javax.swing.JButton();
        statusBarPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        backPanel = new javax.swing.JPanel();
        filePanel = new javax.swing.JPanel();
        fileTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cantMacollasLabel = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cantPozosLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        infoPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        divisionTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        distritoTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        empresaTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        campoTextField = new javax.swing.JTextField();
        tabbedPane = new javax.swing.JTabbedPane();
        macollasPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        macollasTable = new javax.swing.JTable();
        pozosPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        pozosTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        rampeosTable = new javax.swing.JTable();

        jMenu1.setText("jMenu1");

        jMenu2.setText("jMenu2");

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

        selectFileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconbuscar26.png"))); // NOI18N
        selectFileButton.setText("Buscar");
        selectFileButton.setFocusable(false);
        selectFileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectFileButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectFileButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(selectFileButton);

        cargarDatosButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconloadarchivo26.png"))); // NOI18N
        cargarDatosButton.setText("Cargar");
        cargarDatosButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cargarDatosButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cargarDatosButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarDatosButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(cargarDatosButton);

        progressBar.setStringPainted(true);

        javax.swing.GroupLayout statusBarPanelLayout = new javax.swing.GroupLayout(statusBarPanel);
        statusBarPanel.setLayout(statusBarPanelLayout);
        statusBarPanelLayout.setHorizontalGroup(
            statusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusBarPanelLayout.createSequentialGroup()
                .addContainerGap(769, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        statusBarPanelLayout.setVerticalGroup(
            statusBarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusBarPanelLayout.createSequentialGroup()
                .addGap(0, 34, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jToolBar1.add(statusBarPanel);

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel11.setText("Macollas:");

        cantMacollasLabel.setText(" ");

        jLabel12.setText("Pozos:");

        cantPozosLabel.setText(" ");

        jLabel5.setText("Nombre de archivo:");

        javax.swing.GroupLayout filePanelLayout = new javax.swing.GroupLayout(filePanel);
        filePanel.setLayout(filePanelLayout);
        filePanelLayout.setHorizontalGroup(
            filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 459, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(208, 208, 208)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cantMacollasLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cantPozosLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        filePanelLayout.setVerticalGroup(
            filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(cantMacollasLabel)
                    .addComponent(jLabel12)
                    .addComponent(cantPozosLabel)
                    .addComponent(jLabel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("División:");

        jLabel2.setText("Distrito:");

        jLabel3.setText("Empresa:");

        jLabel4.setText("Campo:");

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(divisionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(distritoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(empresaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(campoTextField)
                .addContainerGap())
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(divisionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(distritoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(empresaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(campoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPane.setBackground(new java.awt.Color(255, 255, 255));

        macollasPanel.setBackground(new java.awt.Color(255, 255, 255));

        macollasTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(macollasTable);

        javax.swing.GroupLayout macollasPanelLayout = new javax.swing.GroupLayout(macollasPanel);
        macollasPanel.setLayout(macollasPanelLayout);
        macollasPanelLayout.setHorizontalGroup(
            macollasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(macollasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 568, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(478, Short.MAX_VALUE))
        );
        macollasPanelLayout.setVerticalGroup(
            macollasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(macollasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab("Macollas", macollasPanel);

        pozosPanel.setBackground(new java.awt.Color(255, 255, 255));

        pozosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(pozosTable);

        javax.swing.GroupLayout pozosPanelLayout = new javax.swing.GroupLayout(pozosPanel);
        pozosPanel.setLayout(pozosPanelLayout);
        pozosPanelLayout.setHorizontalGroup(
            pozosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pozosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1036, Short.MAX_VALUE)
                .addContainerGap())
        );
        pozosPanelLayout.setVerticalGroup(
            pozosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pozosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab("Pozos", pozosPanel);

        rampeosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(rampeosTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1036, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab("Rampeos", jPanel1);

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(infoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabbedPane))
                .addContainerGap())
        );
        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(filePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(backPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selectFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectFileButtonActionPerformed
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
        JFileChooser fileChooser = new JFileChooser();
        FileInputStream fis;
        try {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                archivo = fileChooser.getSelectedFile();
                fis = new FileInputStream(archivo);
                fileTextField.setText(archivo.getCanonicalPath());
            } else {
                Contexto.showMessage("Acción cancelada por el usuario", Color.BLUE);
            }
        } catch (IOException ex) {
            sismonlog.logger.log(Level.SEVERE, null, ex);
        }
        cargaArchivo();
    }//GEN-LAST:event_selectFileButtonActionPerformed
    
    private void onDeactivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onDeactivated
        Contexto.showMessage("", Constantes.MENSAJE_CLEAR);
    }//GEN-LAST:event_onDeactivated

    private void cargarDatosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarDatosButtonActionPerformed
        cargaArchivo();
    }//GEN-LAST:event_cargarDatosButtonActionPerformed

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            boolean salvado = false;
            @Override
            protected Void doInBackground() throws Exception {
                progressBar.setVisible(true);
                int progress = 0;
                setProgress(0);
                try{
                campoManager.edit(campo);
                campo = campoManager.find(campo.getNombre());
                progressBar.setIndeterminate(true);
                //progressBar.setString("Guardando macollas");
                if (!macollas.isEmpty()) {
                    macollas.stream().forEach(makie -> {
                        makie.setCampoId(campo);
                        macollaManager.create(makie);
                    });
                }
                
                progressBar.setIndeterminate(false);
                if (!pozosEnMacolla.isEmpty()) {
                    int count = 0;
                    int maxCount = cantPozos;
                    for (Map.Entry<Macolla, Set<Pozo>> mapa : pozosEnMacolla.entrySet()) {
                        for (Pozo well : mapa.getValue()) {
                            Macolla tempMacolla = macollaManager.find(mapa.getKey().getNombre());
                            well.setMacollaId(tempMacolla);
                            well.setRampeoCollection(rampeosEnPozoMap.get(well));
                            well.setEscenarioId(null);
                            pozoManager.edit(well);
                            count++;
                            progress = 100 * count / maxCount;
                            setProgress(progress);
                        }
                    }
                }
                salvado = true;
                loadInicialInfo();
                } catch (Exception e){
                    sismonlog.logger.log(Level.SEVERE, "Error guargando los datos", e);
                    salvado = false;
                }
                
                return null;
            }
            
            @Override
            protected void done() {
                if (salvado) {
                    Contexto.showMessage("Datos guardados con éxito", Constantes.MENSAJE_INFO);
                    progressBar.setVisible(false);
                } else {
                    Contexto.showMessage("Los Datos no fueron guardados correctamente", Constantes.MENSAJE_ERROR);
                }
            }
        };
        
        worker.addPropertyChangeListener(this);
        worker.execute();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void onActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onActivated
        loadInicialInfo();

        if (division != null) {
            divisionTextField.setText(division.getNombre());
        }

        if (distrito != null) {
           distritoTextField.setText(distrito.getNombre());
        }

        if (empresa != null) {
            empresaTextField.setText(empresa.getNombre());
        }

        if (campo != null) {
            campoTextField.setText(campo.getNombre());
        }
        
        Contexto.setActiveFrame(instance);
    }//GEN-LAST:event_onActivated


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backPanel;
    private javax.swing.JTextField campoTextField;
    private javax.swing.JLabel cantMacollasLabel;
    private javax.swing.JLabel cantPozosLabel;
    private javax.swing.JButton cargarDatosButton;
    private javax.swing.JTextField distritoTextField;
    private javax.swing.JTextField divisionTextField;
    private javax.swing.JTextField empresaTextField;
    private javax.swing.JPanel filePanel;
    private javax.swing.JTextField fileTextField;
    private javax.swing.JButton guardarButton;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel macollasPanel;
    private javax.swing.JTable macollasTable;
    private javax.swing.JPanel pozosPanel;
    private javax.swing.JTable pozosTable;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTable rampeosTable;
    private javax.swing.JButton selectFileButton;
    private javax.swing.JPanel statusBarPanel;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            int progress = (Integer)evt.getNewValue();
            progressBar.setValue(progress);
        }
    }
}
