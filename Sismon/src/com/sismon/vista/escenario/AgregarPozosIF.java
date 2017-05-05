package com.sismon.vista.escenario;

import com.sismon.controller.Constantes;
import com.sismon.exceptions.SismonException;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Pozo;
import com.sismon.model.Rampeo;
import com.sismon.vista.Contexto;
import com.sismon.vista.controller.AgregarPozosController;
import com.sismon.vista.utilities.SismonLog;
import java.awt.Color;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class AgregarPozosIF extends javax.swing.JInternalFrame {

    private static AgregarPozosIF instance = null;
    private Escenario escenarioSelected;
    private Macolla macollaSelected;
    private Fila filaSelected;
    private Pozo pozoSelected;

    private final AgregarPozosController controller;
    private static final String SELECCION_INICIAL = "... seleccione ";

    private static final SismonLog SISMONLOG = SismonLog.getInstance();
    private static final NumberFormat NUM_FORMAT = new DecimalFormat("###,###,###,##0.00");
    private static final NumberFormat PORC_FORMAT = new DecimalFormat("##0.00");
    private NumberFormat integerFormat;
    private NumberFormat decimalFormat;
    private NumberFormat porcentFormat;

    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
            .getImage(getClass().getResource("/resources/IconSismon.png")));

    /**
     * Creates new form AgregarPozosIF
     */
    private AgregarPozosIF() {
        init();
        initComponents();

        this.controller = new AgregarPozosController();
    }

    public static AgregarPozosIF getInstance() {
        if (instance == null) {
            instance = new AgregarPozosIF();
        }
        return instance;
    }

    private void init() {
        setFrameIcon(icon);
        setupFormats();
    }

    private void fillEscenariosComboBox() {
        escenarioComboBox.removeAllItems();
        List<Escenario> escenarios = controller.getEscenarios();
        escenarioComboBox.removeAllItems();
        escenarioComboBox.addItem("... seleccione Escenario");
        if (!escenarios.isEmpty()) {
            escenarios.stream().forEach(esc -> {
                escenarioComboBox.addItem(esc);
            });
        }
    }

    private void fillMacollasComboBox() {
        if (escenarioSelected != null) {
            try {
                List<Macolla> macollas = controller.getMacollasEnEscenario(escenarioSelected);
                clearComboBox(macollasComboBox, "Macolla");
                macollas.stream().forEach(mac -> {
                    macollasComboBox.addItem(mac);
                });
            } catch (SismonException ex) {
                Contexto.showMessage(ex.getMessage(), Constantes.MENSAJE_ERROR);
            }
        }
    }

    private void fillFilasComboBox() {
        if (macollaSelected != null) {
            try {
                List<Fila> filas = controller.getFilasEnMacolla(macollaSelected);
                clearComboBox(filasComboBox, "Fila");
                filas.stream().forEach(fil -> {
                    filasComboBox.addItem(fil);
                });
            } catch (Exception ex) {
                Contexto.showMessage(ex.getMessage(), Constantes.MENSAJE_ERROR);
                SISMONLOG.logger.log(Level.SEVERE, "Eroro: {0}", ex);
            }
        }
    }

    private void fillTipoPozoComboBox() {
        Set<String> tipoPozoSet = controller.getTipoPozos();
        clearComboBox(tipoPozoComboBox, "");
        tipoPozoSet.stream().forEach((valor) -> {
            tipoPozoComboBox.addItem(valor);
        });
        tipoPozoComboBox.setEnabled(true);
    }

    private void fillCodigoPozoComboBox(String tipoPozo) {
        if (!tipoPozo.equals(SELECCION_INICIAL)) {
            Set<String> codigoPozoSet = controller.getCodigoPozo(tipoPozo);
            clearComboBox(codigoPozoComboBox, "");
            codigoPozoSet.stream().forEach(cod -> {
                codigoPozoComboBox.addItem(cod);
            });
            codigoPozoComboBox.setEnabled(true);
        } else {
            clearComboBox(codigoPozoComboBox, "");
        }
    }

    private void fillClasePozoComboBox() {
        String[] valores = {Constantes.TIPO_PRODUCTOR, Constantes.TIPO_OBSERVADOR,
            Constantes.TIPO_INYECTOR, Constantes.TIPO_ESTRATIGRAFICO};
        clearComboBox(clasePozoComboBox, "");
        for (String valor : valores) {
            clasePozoComboBox.addItem(valor);
        }
        clasePozoComboBox.setEnabled(true);
    }

    private void clearComboBox(JComboBox combo, String tipo) {
        combo.removeAllItems();
        combo.addItem(SELECCION_INICIAL + tipo);
        combo.setEnabled(false);
    }

    private void clearPozosTable() {
        pozosTable.setModel(new DefaultTableModel());
    }

    private void fillPozosTable() {
        List<Pozo> pozos = controller.getPozosEnFila(macollaSelected, filaSelected, escenarioSelected);
        String[] titles = {"Id", "Localización", "Clase de Pozo", "PI", "RGP", "AyS (%)",
            "Decl. Anual (%)", "T. Abandono", "Resev. Max."};
        Object[][] data = new Object[pozos.size()][titles.length];
        int i = 0;
        for (Pozo well : pozos) {
            data[i][0] = well.getId();
            data[i][1] = well.getUbicacion();
            data[i][2] = well.getClasePozo();
            data[i][3] = NUM_FORMAT.format(well.getPi());
            data[i][4] = NUM_FORMAT.format(well.getRgp());
            data[i][5] = PORC_FORMAT.format(well.getAys() * 100.0);
            data[i][6] = PORC_FORMAT.format(well.getDeclinacion() * 100.0);
            data[i][7] = NUM_FORMAT.format(well.getTasaAbandono());
            data[i++][8] = NUM_FORMAT.format(well.getReservaMax());
        }
        TableModel model = new DefaultTableModel(data, titles);
        pozosTable.setModel(model);
        pozosTable.removeColumn(pozosTable.getColumnModel().getColumn(0));
    }

    private void updateForm() {
        localizacionTextField.setText(pozoSelected.getUbicacion());
        planTextField.setText(pozoSelected.getPlan());
        yacimientoTextField.setText(pozoSelected.getYacimiento());
        bloqueTextField.setText(pozoSelected.getBloque());

        Optional<String> optTipoPozo = Optional.ofNullable(pozoSelected.getTipoPozo());
        if(optTipoPozo.isPresent()){
            tipoPozoComboBox.setSelectedItem(optTipoPozo.get());
        } else {
            tipoPozoComboBox.setSelectedItem(SELECCION_INICIAL);
        }
        
        Optional<String> optCodigoPozo = Optional.ofNullable(pozoSelected.getCodigoPozo());
        if(optCodigoPozo.isPresent()){
            codigoPozoComboBox.setSelectedItem(optCodigoPozo.get());
        } else {
            codigoPozoComboBox.setSelectedItem(SELECCION_INICIAL);
        }
        Optional<String> optClasePozo = Optional.ofNullable(pozoSelected.getClasePozo());
        if (optClasePozo.isPresent()) {
            clasePozoComboBox.setSelectedItem(optClasePozo.get());
        } else {
            clasePozoComboBox.setSelectedItem(pozoSelected.getClasePozo());
        }

        numeroTextField.setBackground(Color.WHITE);
        numeroTextField.setText(String.valueOf(pozoSelected.getNumero()));
        piTextField.setText(NUM_FORMAT.format(pozoSelected.getPi()));
        inicioDeclAnualTextField.setText(NUM_FORMAT.format(pozoSelected.getInicioDecl()));
        rgpTextField.setText(NUM_FORMAT.format(pozoSelected.getRgp()));
        inicioDeclRGPTextField.setText(NUM_FORMAT.format(pozoSelected.getInicioDeclRgp()));
        incrAnualRGPTextField.setText(NUM_FORMAT.format(pozoSelected.getIncremAnualRgp()));
        aysTextField.setText(PORC_FORMAT.format(100 * pozoSelected.getAys()));
        inicioDeclAySTextField.setText(NUM_FORMAT.format(pozoSelected.getInicioDeclAys()));
        incrAnualAySTextField.setText(NUM_FORMAT.format(100 * pozoSelected.getIncremAnualAys()));
        decAnualTextField.setText(PORC_FORMAT.format(100 * pozoSelected.getDeclinacion()));
        expHipTextField.setText(NUM_FORMAT.format(pozoSelected.getExpHiperb()));
        tAbandonoTextField.setText(NUM_FORMAT.format(pozoSelected.getTasaAbandono()));
        reservaMaxTextField.setText(NUM_FORMAT.format(pozoSelected.getReservaMax()));
        apiXpTextField.setText(NUM_FORMAT.format(pozoSelected.getGradoApiXp()));
        apiDlntTextField.setText(NUM_FORMAT.format(pozoSelected.getGradoApiDiluente()));
        apiMexclaTextField.setText(NUM_FORMAT.format(pozoSelected.getGradoApiMezcla()));

        updateRampeos();
    }

    private void updateRampeos() {
        List<Rampeo> rampeos = controller.getRampeos(pozoSelected, escenarioSelected);
        for (Rampeo rampeo : rampeos) {
            double dias = rampeo.getDias();
            double rpm = rampeo.getRpm();
            switch (rampeo.getNumero()) {
                case 1:
                    rampeo1TextField.setText(NUM_FORMAT.format(dias));
                    rpm1TextField.setText(NUM_FORMAT.format(rpm));
                    break;
                case 2:
                    rampeo2TextField.setText(NUM_FORMAT.format(dias));
                    rpm2TextField.setText(NUM_FORMAT.format(rpm));
                    break;
                case 3:
                    rampeo3TextField.setText(NUM_FORMAT.format(dias));
                    rpm3TextField.setText(NUM_FORMAT.format(rpm));
                    break;
                case 4:
                    rampeo4TextField.setText(NUM_FORMAT.format(dias));
                    rpm4TextField.setText(NUM_FORMAT.format(rpm));
                    break;
                case 5:
                    rampeo5TextField.setText(NUM_FORMAT.format(dias));
                    rpm5TextField.setText(NUM_FORMAT.format(rpm));
                    break;
                case 6:
                    rampeo6TextField.setText(NUM_FORMAT.format(dias));
                    rpm6TextField.setText(NUM_FORMAT.format(rpm));
                    break;
            }
        }
    }

    private void addRampeos(Pozo pozo) {
        List<Rampeo> rampeos = new ArrayList<>();
        try {
            Rampeo rampeo;
            for (int num = 1; num < 7; num++) {
                double dias = 0.0;
                double rpm = 0.0;
                switch (num) {
                    case 1:
                        if(rampeo1TextField.getText() != null && !rampeo1TextField.getText().isEmpty()){
                            dias =((Number) NUM_FORMAT.parse(rampeo1TextField.getText())).doubleValue();
                        } 
                        if (rpm1TextField.getText() != null && !rpm1TextField.getText().isEmpty()) {
                            rpm = ((Number) NUM_FORMAT.parse(rpm1TextField.getText())).doubleValue();
                        }
                        break;
                    case 2:
                        if (rampeo2TextField.getText() != null && !rampeo2TextField.getText().isEmpty()) {
                            dias = ((Number) NUM_FORMAT.parse(rampeo2TextField.getText())).doubleValue();
                        }
                        if (rpm2TextField.getText() != null && !rpm2TextField.getText().isEmpty()) {
                            rpm = ((Number) NUM_FORMAT.parse(rpm2TextField.getText())).doubleValue();
                        }
                        break;
                    case 3:
                        if (rampeo3TextField.getText() != null && !rampeo3TextField.getText().isEmpty()) {
                            dias = ((Number) NUM_FORMAT.parse(rampeo3TextField.getText())).doubleValue();
                        }
                        if (rpm3TextField.getText() != null && !rpm3TextField.getText().isEmpty()) {
                            rpm = ((Number) NUM_FORMAT.parse(rpm3TextField.getText())).doubleValue();
                        }
                        break;
                    case 4:
                        if (rampeo4TextField.getText() != null && !rampeo4TextField.getText().isEmpty()) {
                            dias = ((Number) NUM_FORMAT.parse(rampeo4TextField.getText())).doubleValue();
                        }
                        if (rpm4TextField.getText() != null && !rpm4TextField.getText().isEmpty()) {
                            rpm = ((Number) NUM_FORMAT.parse(rpm4TextField.getText())).doubleValue();
                        }
                        break;
                    case 5:
                        if (rampeo5TextField.getText() != null && !rampeo5TextField.getText().isEmpty()) {
                            dias = ((Number) NUM_FORMAT.parse(rampeo5TextField.getText())).doubleValue();
                        }
                        if (rpm5TextField.getText() != null && !rpm5TextField.getText().isEmpty()) {
                            rpm = ((Number) NUM_FORMAT.parse(rpm5TextField.getText())).doubleValue();
                        }
                        break;
                    case 6:
                        if (rampeo6TextField.getText() != null && !rampeo6TextField.getText().isEmpty()) {
                            dias = ((Number) NUM_FORMAT.parse(rampeo6TextField.getText())).doubleValue();
                        }
                        if (rpm6TextField.getText() != null && !rpm6TextField.getText().isEmpty()) {
                            rpm = ((Number) NUM_FORMAT.parse(rpm6TextField.getText())).doubleValue();
                        }
                        break;
                }
                rampeo = new Rampeo();
                rampeo.setDias(dias);
                rampeo.setRpm(rpm);
                rampeo.setPozoId(pozo);
                rampeo.setNumero(num);
                rampeo.setEscenarioId(escenarioSelected);
                rampeos.add(rampeo);
            }
            controller.saveRampeo(rampeos);
        } catch (ParseException e) {
            SISMONLOG.logger.log(Level.SEVERE, "Error guardando valores de rampeo. Error: {0}", e);
            Contexto.showMessage("Ingresó un valor no válido en un campo de rampeo", Constantes.MENSAJE_ERROR);
        }
    }

    private void clearForm() {
        localizacionTextField.setText(null);
        planTextField.setText(null);
        yacimientoTextField.setText(null);
        bloqueTextField.setText(null);

        tipoPozoComboBox.setSelectedItem(SELECCION_INICIAL);
        clasePozoComboBox.setSelectedItem(SELECCION_INICIAL);

        numeroTextField.setText(null);
        piTextField.setText(null);
        inicioDeclAnualTextField.setText(null);
        rgpTextField.setText(null);
        inicioDeclRGPTextField.setText(null);
        incrAnualRGPTextField.setText(null);
        aysTextField.setText(null);
        inicioDeclAySTextField.setText(null);
        incrAnualAySTextField.setText(null);
        decAnualTextField.setText(null);
        expHipTextField.setText(null);
        tAbandonoTextField.setText(null);
        reservaMaxTextField.setText(null);
        apiXpTextField.setText(null);
        apiDlntTextField.setText(null);
        apiMexclaTextField.setText(null);

        rampeo1TextField.setText(null);
        rpm1TextField.setText(null);
        rampeo2TextField.setText(null);
        rpm2TextField.setText(null);
        rampeo3TextField.setText(null);
        rpm3TextField.setText(null);
        rampeo4TextField.setText(null);
        rpm4TextField.setText(null);
        rampeo5TextField.setText(null);
        rpm5TextField.setText(null);
        rampeo6TextField.setText(null);
        rpm6TextField.setText(null);
    }

    private boolean verifyPozoFields() {
        if (!(escenarioComboBox.getSelectedItem() instanceof Escenario)) {
            showErrorMessage("Debe tener seleccionado un Escenario");
            return false;
        }

        if (!(macollasComboBox.getSelectedItem() instanceof Macolla)) {
            showErrorMessage("Debe tener seleccionado una Macolla");
            return false;
        }

        if (!(filasComboBox.getSelectedItem() instanceof Fila)) {
            showErrorMessage("Debe tener seleccionado una Fila");
            return false;
        }

        if (numeroTextField.getText().isEmpty() || numeroTextField.getText() == null) {
            showErrorMessage("Debe asignar un valor para el Número del Pozo");
            return false;
        } else if (!isOkValue(numeroTextField)) {
            showErrorMessage("Debe asignar un valor correcto para el Número del Pozo");
            return false;
        }

        if (planTextField.getText().isEmpty() || planTextField.getText() == null) {
            showErrorMessage("Debe asignar un plan");
            return false;
        }

        if (yacimientoTextField.getText().isEmpty() || yacimientoTextField.getText() == null) {
            showErrorMessage("Debe asignar un yacimiento");
            return false;
        }

        if (bloqueTextField.getText().isEmpty() || bloqueTextField.getText() == null) {
            showErrorMessage("Debe asignar un bloque");
            return false;
        }

        if (piTextField.getText().isEmpty() || piTextField.getText() == null) {
            showErrorMessage("Debe asignar un valor para la P.I.");
            return false;
        } else if (!isOkValue(piTextField)) {
            showErrorMessage("Debe asignar un valor correcto para la P.I.");
            return false;
        }

        if (rgpTextField.getText().isEmpty() || rgpTextField.getText() == null) {
            showErrorMessage("Debe asignar un valor para RGP");
            return false;
        } else if (!isOkValue(rgpTextField)) {
            showErrorMessage("Debe asignar un valor correcto para la RGP");
            return false;
        }

        if (aysTextField.getText().isEmpty() || aysTextField.getText() == null) {
            showErrorMessage("Debe asignar un valor para AyS");
            return false;
        } else if (!isOkValue(aysTextField)) {
            showErrorMessage("Debe asignar un valor correcto para la AyS");
            return false;
        }

        if (decAnualTextField.getText().isEmpty() || decAnualTextField.getText() == null) {
            showErrorMessage("Debe asignar un valor para Declinación Anual");
            return false;
        } else if (!isOkValue(decAnualTextField)) {
            showErrorMessage("Debe asignar un valor correcto para la Declinación Anual");
            return false;
        }

        if (expHipTextField.getText().isEmpty() || expHipTextField.getText() == null) {
            showErrorMessage("Debe asignar un valor para el Exponente Hiperbólico");
            return false;
        } else if (!isOkValue(expHipTextField)) {
            showErrorMessage("Debe asignar un valor correcto para el Exponente Hiperbólico");
            return false;
        }

        if (tAbandonoTextField.getText().isEmpty() || tAbandonoTextField.getText() == null) {
            showErrorMessage("Debe asignar un valor para la Tasa de Abandono");
            return false;
        } else if (!isOkValue(tAbandonoTextField)) {
            showErrorMessage("Debe asignar un valor correcto para la Tasa de Abandono");
            return false;
        }

        if (reservaMaxTextField.getText().isEmpty() || reservaMaxTextField.getText() == null) {
            showErrorMessage("Debe asignar un valor para la Reserva Máxima");
            return false;
        } else if (!isOkValue(reservaMaxTextField)) {
            showErrorMessage("Debe asignar un valor correcto para la Reserva Máxima");
            return false;
        }

        if (tipoPozoComboBox.getSelectedItem().equals(SELECCION_INICIAL)) {
            showErrorMessage("Debe asignar un Tipo de Pozo");
            return false;
        }

        if (codigoPozoComboBox.getSelectedItem().equals(SELECCION_INICIAL)) {
            showErrorMessage("Debe asignar un Código de Pozo");
            return false;
        }

        if (clasePozoComboBox.getSelectedItem().equals(SELECCION_INICIAL)) {
            showErrorMessage("Debe asignar una Clase de Pozo");
            return false;
        }

        if (apiXpTextField.getText().isEmpty() || apiXpTextField.getText() == null) {
            showErrorMessage("Debe asignar el grado API de la producción");
            return false;
        } else if (!isOkValue(apiXpTextField)) {
            showErrorMessage("Debe asignar un valor correcto el grado API de la producción");
            return false;
        }

        if (apiDlntTextField.getText().isEmpty() || apiDlntTextField.getText() == null) {
            showErrorMessage("Debe asignar el grado API del diluente");
            return false;
        } else if (!isOkValue(apiDlntTextField)) {
            showErrorMessage("Debe asignar un valor correcto el grado API del Diluente");
            return false;
        }

        if (apiMexclaTextField.getText().isEmpty() || apiMexclaTextField.getText() == null) {
            showErrorMessage("Debe asignar el grado API de la mezcla");
            return false;
        } else if (!isOkValue(apiMexclaTextField)) {
            showErrorMessage("Debe asignar un valor correcto el grado API de la Mezcla");
            return false;
        }

        return true;
    }

    private boolean isOkValue(JTextField tf) {
        boolean ok = true;
        for (char ch : tf.getText().toCharArray()) {
            if (ch == '\u002C' || ch == '\u002E') {
                continue;
            }
            if (Character.getNumericValue(ch) < 0 || Character.getNumericValue(ch) > 9) {
                ok = false;
                break;
            }
        }
        return ok;
    }

    private boolean updateModel() {
        boolean ok = false;
        if (verifyPozoFields()) {
            ok = true;
            Pozo pozo = guardarPozo();
            addRampeos(pozo);
        }
        return ok;
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setupFormats() {
        integerFormat = NumberFormat.getIntegerInstance();
        decimalFormat = NumberFormat.getNumberInstance(Locale.FRENCH);
        porcentFormat = NumberFormat.getPercentInstance(Locale.FRENCH);
    }

    private Pozo guardarPozo() {
        Pozo pozo = new Pozo();
        try {
            pozo.setUbicacion(localizacionTextField.getText());
            pozo.setNumero(Integer.parseInt(numeroTextField.getText().trim()));
            pozo.setPlan(planTextField.getText());
            pozo.setClasePozo((String) clasePozoComboBox.getSelectedItem());
            pozo.setYacimiento(yacimientoTextField.getText());
            pozo.setBloque(bloqueTextField.getText());
            pozo.setPi(((Number) NUM_FORMAT.parse(piTextField.getText())).doubleValue());
            pozo.setDeclinacion(((Number) NUM_FORMAT.parse(decAnualTextField.getText())).doubleValue() / 100.0);
            pozo.setInicioDecl(((Number) NUM_FORMAT.parse(inicioDeclAnualTextField.getText())).intValue());
            pozo.setRgp(((Number) NUM_FORMAT.parse(rgpTextField.getText())).doubleValue());
            pozo.setInicioDeclRgp(((Number) NUM_FORMAT.parse(inicioDeclRGPTextField.getText())).intValue());
            pozo.setIncremAnualRgp(((Number) NUM_FORMAT.parse(incrAnualRGPTextField.getText())).doubleValue());
            pozo.setAys(((Number) NUM_FORMAT.parse(aysTextField.getText())).doubleValue() / 100.0);
            pozo.setInicioDeclAys(((Number) NUM_FORMAT.parse(inicioDeclAySTextField.getText())).intValue());
            pozo.setIncremAnualAys(((Number) NUM_FORMAT.parse(incrAnualAySTextField.getText())).doubleValue() / 100.0);
            pozo.setExpHiperb(((Number) NUM_FORMAT.parse(expHipTextField.getText())).doubleValue());
            pozo.setTasaAbandono(((Number) NUM_FORMAT.parse(tAbandonoTextField.getText())).doubleValue());
            pozo.setReservaMax(((Number) NUM_FORMAT.parse(reservaMaxTextField.getText())).doubleValue());
            pozo.setTipoPozo((String) tipoPozoComboBox.getSelectedItem());
            pozo.setCodigoPozo((String) codigoPozoComboBox.getSelectedItem());
            pozo.setClasePozo((String) clasePozoComboBox.getSelectedItem());
            pozo.setGradoApiXp(((Number) NUM_FORMAT.parse(apiXpTextField.getText())).doubleValue());
            pozo.setGradoApiDiluente(((Number) NUM_FORMAT.parse(apiDlntTextField.getText())).doubleValue());
            pozo.setGradoApiMezcla(((Number) NUM_FORMAT.parse(apiMexclaTextField.getText())).doubleValue());
            pozo.setMacollaId(macollaSelected);
            pozo.setFilaId(filaSelected);
            pozo.setEscenarioId(escenarioSelected);
            
            StringBuilder sb = new StringBuilder();
            sb.append(macollaSelected.getNombre()).append("-");
            int numero = Integer.parseInt(numeroTextField.getText().trim());
            sb.append(numero);
            pozo.setUbicacion(sb.toString());
            
            controller.savePozo(pozo);
        } catch (ParseException e) {
            SISMONLOG.logger.log(Level.SEVERE, "Error convirtiendo un valor de la forma: {0}", e);
        }
        return pozo;
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
        jLabel1 = new javax.swing.JLabel();
        localizacionTextField = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        tipoPozoComboBox = new javax.swing.JComboBox<>();
        jLabel34 = new javax.swing.JLabel();
        codigoPozoComboBox = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        clasePozoComboBox = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        apiXpTextField = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        apiDlntTextField = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        apiMexclaTextField = new javax.swing.JTextField();
        rampeoPanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        rampeo1TextField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        rpm1TextField = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        rampeo2TextField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        rpm2TextField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        rpm3TextField = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        rampeo3TextField = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        rpm4TextField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        rampeo4TextField = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        rpm5TextField = new javax.swing.JTextField();
        rampeo5TextField = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        rampeo6TextField = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        rpm6TextField = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pozosTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        numeroTextField = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        planTextField = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        yacimientoTextField = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        bloqueTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        piTextField = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        inicioDeclAnualTextField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        decAnualTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        rgpTextField = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        inicioDeclRGPTextField = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        incrAnualRGPTextField = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        aysTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        inicioDeclAySTextField = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        incrAnualAySTextField = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        reservaMaxTextField = new javax.swing.JTextField();
        tAbandonoTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        expHipTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        toolbarPanel = new javax.swing.JPanel();
        escenarioComboBox = new javax.swing.JComboBox();
        macollasComboBox = new javax.swing.JComboBox();
        filasComboBox = new javax.swing.JComboBox();
        guardarButton = new javax.swing.JButton();
        nuevoButton = new javax.swing.JButton();
        eliminarButton = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
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
                onOpened(evt);
            }
        });

        backPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Localización:");

        jLabel33.setText("Tipo Pozo:");

        tipoPozoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipoPozoComboBoxActionPerformed(evt);
            }
        });

        jLabel34.setText("Código Pozo:");

        jLabel35.setText("Clase Pozo:");

        jLabel36.setText("° API Xp:");

        jLabel37.setText("° API Diluente:");

        jLabel38.setText("° API Mexcla:");

        rampeoPanel.setBackground(new java.awt.Color(255, 255, 255));
        rampeoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Rampeos"));

        jLabel12.setText("Rampeo 1:");

        jLabel13.setText("dias");

        jLabel14.setText("RPM 1:");

        jLabel15.setText("Rampeo 2:");

        jLabel16.setText("dias");

        jLabel17.setText("RPM 2:");

        jLabel18.setText("dias");

        jLabel19.setText("RPM 3:");

        jLabel20.setText("Rampeo 3:");

        jLabel21.setText("RPM 4:");

        jLabel22.setText("Rampeo 4:");

        jLabel23.setText("dias");

        jLabel24.setText("Rampeo 5:");

        jLabel25.setText("RPM 5:");

        jLabel26.setText("dias");

        jLabel27.setText("dias");

        jLabel28.setText("Rampeo 6:");

        jLabel29.setText("RPM 6:");

        javax.swing.GroupLayout rampeoPanelLayout = new javax.swing.GroupLayout(rampeoPanel);
        rampeoPanel.setLayout(rampeoPanelLayout);
        rampeoPanelLayout.setHorizontalGroup(
            rampeoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rampeoPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(rampeoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rampeoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rampeo1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rpm1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rampeo2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rpm2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rampeo3TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rpm3TextField, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(rampeoPanelLayout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rampeo4TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rpm4TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rampeo5TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rpm5TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rampeo6TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rpm6TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rampeoPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {rampeo1TextField, rampeo2TextField, rampeo3TextField, rampeo4TextField, rampeo5TextField, rampeo6TextField, rpm1TextField, rpm2TextField, rpm3TextField, rpm4TextField, rpm5TextField, rpm6TextField});

        rampeoPanelLayout.setVerticalGroup(
            rampeoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rampeoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rampeoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(rampeo1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(rpm1TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(rampeo2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(rpm2TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(rampeo3TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(rpm3TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rampeoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rampeoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel28)
                        .addComponent(rampeo6TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27)
                        .addComponent(jLabel29)
                        .addComponent(rpm6TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(rampeoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel24)
                        .addComponent(rampeo5TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26)
                        .addComponent(jLabel25)
                        .addComponent(rpm5TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(rampeoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel22)
                        .addComponent(rampeo4TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel23)
                        .addComponent(jLabel21)
                        .addComponent(rpm4TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pozosTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        pozosTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pozosTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(pozosTable);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel39.setText("Número:");

        jLabel30.setText("Plan:");

        jLabel31.setText("Yacimiento:");

        jLabel32.setText("Bloque:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numeroTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(planTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jLabel31)
                .addGap(5, 5, 5)
                .addComponent(yacimientoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel32)
                .addGap(5, 5, 5)
                .addComponent(bloqueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {bloqueTextField, yacimientoTextField});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(numeroTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39)
                            .addComponent(jLabel30)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(planTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel31))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(yacimientoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel32))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(bloqueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("PI"));

        jLabel2.setText("P.I.");

        jLabel41.setText("Inicio Decl Anual: ");

        jLabel6.setText("Decl. Anual:");

        jLabel7.setText("( % )");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(piTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel41)
                    .addComponent(inicioDeclAnualTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7))
                    .addComponent(decAnualTextField))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {decAnualTextField, inicioDeclAnualTextField, piTextField});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel41)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(piTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inicioDeclAnualTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(decAnualTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("RGP"));

        jLabel3.setText("RGP:");

        jLabel43.setText("Inicio Decl RGP:");

        jLabel42.setText("Incr. Anual RGP:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(rgpTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43)
                    .addComponent(inicioDeclRGPTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42)
                    .addComponent(incrAnualRGPTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {incrAnualRGPTextField, inicioDeclRGPTextField, rgpTextField});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel43)
                    .addComponent(jLabel42))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rgpTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inicioDeclRGPTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(incrAnualRGPTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("A y S"));

        jLabel4.setText("AyS:");

        jLabel5.setText("( % )");

        jLabel45.setText("Inicio Decl AyS:");

        jLabel44.setText("Incr.Anual AyS:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5))
                    .addComponent(aysTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel45)
                    .addComponent(inicioDeclAySTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44)
                    .addComponent(incrAnualAySTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {aysTextField, incrAnualAySTextField, inicioDeclAySTextField});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel45)
                    .addComponent(jLabel44))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aysTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inicioDeclAySTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(incrAnualAySTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Misc"));

        jLabel11.setText("Reserva Max: (b)");

        jLabel9.setText("T. Abandono: (b/d)");

        jLabel8.setText("Exp. Hipr.");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(reservaMaxTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(tAbandonoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(expHipTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {expHipTextField, reservaMaxTextField, tAbandonoTextField});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reservaMaxTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tAbandonoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(expHipTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout backPanelLayout = new javax.swing.GroupLayout(backPanel);
        backPanel.setLayout(backPanelLayout);
        backPanelLayout.setHorizontalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(localizacionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(rampeoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(backPanelLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel36))
                            .addComponent(jLabel33))
                        .addGap(1, 1, 1)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tipoPozoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(apiXpTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(codigoPozoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(apiDlntTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(apiMexclaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(clasePozoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        backPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {clasePozoComboBox, codigoPozoComboBox, tipoPozoComboBox});

        backPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {apiDlntTextField, apiMexclaTextField, apiXpTextField});

        backPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel2, jPanel3, jPanel4, jPanel5});

        backPanelLayout.setVerticalGroup(
            backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backPanelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(localizacionTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tipoPozoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(codigoPozoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clasePozoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33)
                            .addComponent(jLabel34)
                            .addComponent(jLabel35))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(apiXpTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel38)
                        .addComponent(apiMexclaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(apiDlntTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(backPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(backPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel36)
                            .addComponent(jLabel37))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rampeoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addContainerGap())
        );

        backPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel2, jPanel3, jPanel4, jPanel5});

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

        filasComboBox.setEnabled(false);
        filasComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filasComboBoxActionPerformed(evt);
            }
        });

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

        nuevoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconNuevo26.png"))); // NOI18N
        nuevoButton.setText("Nuevo");
        nuevoButton.setEnabled(false);
        nuevoButton.setFocusable(false);
        nuevoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nuevoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nuevoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoButtonActionPerformed(evt);
            }
        });

        eliminarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/iconquitar26.png"))); // NOI18N
        eliminarButton.setText("Eliminar");
        eliminarButton.setEnabled(false);
        eliminarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        eliminarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        eliminarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout toolbarPanelLayout = new javax.swing.GroupLayout(toolbarPanel);
        toolbarPanel.setLayout(toolbarPanelLayout);
        toolbarPanelLayout.setHorizontalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(guardarButton)
                .addComponent(nuevoButton)
                .addComponent(eliminarButton)
                .addGap(58, 58, 58)
                .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(macollasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(filasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        toolbarPanelLayout.setVerticalGroup(
            toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolbarPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guardarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nuevoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eliminarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, toolbarPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(toolbarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(escenarioComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(macollasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filasComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toolbarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {backPanel, toolbarPanel});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbarPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(backPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void guardarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarButtonActionPerformed
        if (updateModel()) {
            StringBuilder sb = new StringBuilder();
            sb.append(macollaSelected.getNombre()).append("-");
            int numero = Integer.parseInt(numeroTextField.getText().trim());
            sb.append(numero);
            JOptionPane.showMessageDialog(this, "Agregado el pozo " + sb.toString());
            localizacionTextField.setText(sb.toString());
            guardarButton.setEnabled(false);
            fillPozosTable();
            clearForm();
            sb = new StringBuilder();
            sb.append("<html>");
            sb.append("Para que el nuevo pozo sea considerado en la perforación,")
                    .append("<br>");
            sb.append("debe modificar la Secuencia de Perforación de la macolla: ")
                    .append("<b>").append(macollaSelected.toString()).append("</b>");
            sb.append("<br>").append("en la fila: ").append("<b>")
                    .append(filaSelected.getNombre()).append("</b>");
            sb.append("</html>");        
            JOptionPane.showMessageDialog(this, sb.toString(), 
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_guardarButtonActionPerformed

    private void escenarioComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escenarioComboBoxActionPerformed
        if (escenarioComboBox.getSelectedItem() instanceof Escenario) {
            escenarioSelected = (Escenario) escenarioComboBox.getSelectedItem();
            fillMacollasComboBox();
            macollasComboBox.setEnabled(true);
        } else {
            clearComboBox(macollasComboBox, "Macolla");
            nuevoButton.setEnabled(false);
            eliminarButton.setEnabled(false);
        }
    }//GEN-LAST:event_escenarioComboBoxActionPerformed

    private void macollasComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_macollasComboBoxActionPerformed
        if (macollasComboBox.getSelectedItem() instanceof Macolla) {
            macollaSelected = (Macolla) macollasComboBox.getSelectedItem();
            fillFilasComboBox();
            filasComboBox.setEnabled(true);
        } else {
            clearComboBox(filasComboBox, "Fila");
            eliminarButton.setEnabled(false);
        }
    }//GEN-LAST:event_macollasComboBoxActionPerformed

    private void filasComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filasComboBoxActionPerformed
        if (filasComboBox.getSelectedItem() instanceof Fila) {
            filaSelected = (Fila) filasComboBox.getSelectedItem();
            fillPozosTable();
            pozosTable.clearSelection();
            clearForm();
            nuevoButton.setEnabled(false);
            Contexto.showMessage("Seleccione un pozo para copiar sus características "
                    + "y haga click en el botón nuevo ó botón eliminar para eliminarlo",
                    Constantes.MENSAJE_INFO);
        } else {
            clearPozosTable();
            clearForm();
            eliminarButton.setEnabled(false);
        }
    }//GEN-LAST:event_filasComboBoxActionPerformed

    private void pozosTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pozosTableMouseClicked
        TableModel model = pozosTable.getModel();
        if (pozosTable.getSelectedRow() >= 0) {
            int rowSelected = pozosTable.getSelectedRow();
            Long pozoId = (Long) model.getValueAt(rowSelected, 0);
            pozoSelected = controller.getSelectedPozo(pozoId);
            updateForm();
            Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
            nuevoButton.setEnabled(true);
            
            // opcion de eliminar
            if(!controller.isPozoInTaladroAsignado(escenarioSelected, pozoSelected)){
                eliminarButton.setEnabled(true);
                Contexto.showMessage(null, Constantes.MENSAJE_CLEAR);
            } else {
                eliminarButton.setEnabled(false);
                Contexto.showMessage("No puede eliminar este pozo por estar en "
                        + "la fase de entrada o salida de un taladro", Constantes.MENSAJE_WARNING);
            }
         
            
        }
    }//GEN-LAST:event_pozosTableMouseClicked

    private void tipoPozoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoPozoComboBoxActionPerformed
        String tipoSelected = (String) tipoPozoComboBox.getSelectedItem();
        if (!tipoSelected.equals(SELECCION_INICIAL)) {
            fillCodigoPozoComboBox(tipoSelected);
        } else {
            codigoPozoComboBox.setSelectedItem(SELECCION_INICIAL);
        }
    }//GEN-LAST:event_tipoPozoComboBoxActionPerformed

    private void nuevoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoButtonActionPerformed
        if (pozoSelected != null) {
            int respuesta = JOptionPane.showInternalConfirmDialog(this,
                    "¿Desea mantener los datos mostrados en pantalla?",
                    "Nuevo pozo en Fila",
                    JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.NO_OPTION) {
                clearForm();
            } else {
                localizacionTextField.setText(null);
                numeroTextField.setText(null);
                numeroTextField.setBackground(Color.getHSBColor(176f, 0.13f, 0.96f));
                numeroTextField.grabFocus();
                nuevoButton.setEnabled(false);
            }
        }
        pozosTable.clearSelection();
        localizacionTextField.setEditable(false);
        guardarButton.setEnabled(true);
    }//GEN-LAST:event_nuevoButtonActionPerformed

    private void onOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onOpened
        Contexto.setActiveFrame(instance);
        fillEscenariosComboBox();
        fillTipoPozoComboBox();
        fillClasePozoComboBox();
    }//GEN-LAST:event_onOpened

    private void onClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClosing
        escenarioComboBox.setSelectedIndex(0);
        clearForm();
    }//GEN-LAST:event_onClosing

    private void eliminarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarButtonActionPerformed
        Fila fila = pozoSelected.getFilaId();
        Macolla macolla = fila.getMacollaId();
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("Al eliminar el pozo ").append("<b>")
                .append(pozoSelected.getUbicacion()).append("</b>");
        sb.append(", se eliminará la Secuencia de Perforación de la fila ");
        sb.append("<b>").append(fila.getNombre()).append("</b>");
        sb.append(" en la macolla ").append("<b>").append(macolla.getNombre())
                .append(".</b>").append("<br>");
        sb.append("Recuerde que debe volver a cargar la Secuencia de Perforación de la fila ");
        sb.append("y volver a ejecutar la perforación. ");
        sb.append("<br>");
        sb.append("¿Desea continuar? ");
        sb.append("</html>");
        int answer = JOptionPane.showConfirmDialog(this, sb.toString()
                , "Advertencia ", JOptionPane.WARNING_MESSAGE);
        if(answer == JOptionPane.YES_OPTION){
            // se elimina la explotación, si tiene
            controller.deleteExplotacion(pozoSelected);
            // se elimina la secuencia de perforacion
            controller.deletePozoSecuencia(escenarioSelected, fila);
            // se elimina la perforación de este pozo
            controller.deletePerforacion(escenarioSelected, pozoSelected);
            // se elimina el rampeo de este pozo
            controller.deleteRampeos(escenarioSelected, pozoSelected);
            // se elimina el pozo
            controller.deletePozo(escenarioSelected, pozoSelected);
            fillPozosTable();
            Contexto.showMessage("Pozo eliminado con éxito", Constantes.MENSAJE_INFO);
            clearForm();
        }
    }//GEN-LAST:event_eliminarButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField apiDlntTextField;
    private javax.swing.JTextField apiMexclaTextField;
    private javax.swing.JTextField apiXpTextField;
    private javax.swing.JTextField aysTextField;
    private javax.swing.JPanel backPanel;
    private javax.swing.JTextField bloqueTextField;
    private javax.swing.JComboBox<String> clasePozoComboBox;
    private javax.swing.JComboBox<String> codigoPozoComboBox;
    private javax.swing.JTextField decAnualTextField;
    private javax.swing.JButton eliminarButton;
    private javax.swing.JComboBox escenarioComboBox;
    private javax.swing.JTextField expHipTextField;
    private javax.swing.JComboBox filasComboBox;
    private javax.swing.JButton guardarButton;
    private javax.swing.JTextField incrAnualAySTextField;
    private javax.swing.JTextField incrAnualRGPTextField;
    private javax.swing.JTextField inicioDeclAnualTextField;
    private javax.swing.JTextField inicioDeclAySTextField;
    private javax.swing.JTextField inicioDeclRGPTextField;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField localizacionTextField;
    private javax.swing.JComboBox macollasComboBox;
    private javax.swing.JButton nuevoButton;
    private javax.swing.JTextField numeroTextField;
    private javax.swing.JTextField piTextField;
    private javax.swing.JTextField planTextField;
    private javax.swing.JTable pozosTable;
    private javax.swing.JTextField rampeo1TextField;
    private javax.swing.JTextField rampeo2TextField;
    private javax.swing.JTextField rampeo3TextField;
    private javax.swing.JTextField rampeo4TextField;
    private javax.swing.JTextField rampeo5TextField;
    private javax.swing.JTextField rampeo6TextField;
    private javax.swing.JPanel rampeoPanel;
    private javax.swing.JTextField reservaMaxTextField;
    private javax.swing.JTextField rgpTextField;
    private javax.swing.JTextField rpm1TextField;
    private javax.swing.JTextField rpm2TextField;
    private javax.swing.JTextField rpm3TextField;
    private javax.swing.JTextField rpm4TextField;
    private javax.swing.JTextField rpm5TextField;
    private javax.swing.JTextField rpm6TextField;
    private javax.swing.JTextField tAbandonoTextField;
    private javax.swing.JComboBox<String> tipoPozoComboBox;
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JTextField yacimientoTextField;
    // End of variables declaration//GEN-END:variables
}
