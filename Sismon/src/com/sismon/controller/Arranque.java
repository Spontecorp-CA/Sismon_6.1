package com.sismon.controller;

import com.sismon.jpamanager.DivisionManager;
import com.sismon.jpamanager.TipoPozoManager;
import com.sismon.model.Division;
import com.sismon.model.TipoPozo;
import com.sismon.vista.utilities.SismonLog;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 * La clase Arranque permite caragar los datos iniciales de la base de datos
 * en caso de estar vacía. Lo que hace es crear un hilo de ejecución que corre
 * en paralelo a la pantalla de carga.
 * 
 * @author jgcastillo
 */
public class Arranque implements Runnable {

    private static final String[] divisionNames = {"Ayacucho", "Boyacá", "Carabobo",
        "Junin",};
    private static final String[] faseNames = {
        Constantes.FASE_MUDANZA_ENTRE_MACOLLAS, Constantes.FASE_MUDANZA_ENTRE_POZOS,
        Constantes.FASE_SUPERFICIAL, Constantes.FASE_SLANT, Constantes.FASE_PILOTO,
        Constantes.FASE_VERTICAL, Constantes.FASE_INTERMEDIO, Constantes.FASE_PRODUCTOR,
        Constantes.FASE_CONEXION, Constantes.FASE_EVALUACION, Constantes.FASE_COMPLETACION};

    private List<Division> divisionList;
    private final DivisionManager divisionManager;

    private static final SismonLog SISMONLOG = SismonLog.getInstance();

    /**
     * Constructor de la clase usado para crear inicializar el DivisionManager
     * 
     */
    public Arranque() {
        this.divisionManager = new DivisionManager();
    }

    /**
     * Método principal del hilo de ejecución, usado para crear los accesos
     * iniciales a la base de datos y generar los tipos de pozos
     * 
     */
    @Override
    public void run() {

        // verifica los valores iniciales y si no están los carga
        if (divisionManager == null) {
            SISMONLOG.logger.log(Level.SEVERE, "No se pudo obtener un objeto DivisionManager");
            JOptionPane.showMessageDialog(null, "Error al acceder a la base de datos",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            divisionList = divisionManager.findAll();
            if (divisionList == null || divisionList.isEmpty()) {
                divisionList = new ArrayList<>();
                for (String divisionName : divisionNames) {
                    Division division = new Division();
                    division.setNombre(divisionName);
                    divisionManager.create(division);
                }
            }
            SISMONLOG.logger.log(Level.FINER, "Creadas las divisiones");
        }

        loadTipoPozos();
    }

    private void loadTipoPozos() {
        TipoPozoManager tipoPozoManager = new TipoPozoManager();
        List<TipoPozo> tipoPozoList = tipoPozoManager.findAll();
        if (tipoPozoList.isEmpty()) {
            TipoPozoAPI api = new TipoPozoAPI();
//                fasePozoManager.batchSave(tempSet);
        }
        SISMONLOG.logger.log(Level.INFO, "Se inicializan los tipos de pozos");
    }
}
