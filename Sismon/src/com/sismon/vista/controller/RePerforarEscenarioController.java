package com.sismon.vista.controller;

import com.sismon.jpamanager.PerforacionManager;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingWorker;

public class RePerforarEscenarioController extends SwingWorker<Map<Integer, Object[]>, Void> {

    private List<Perforacion> perforaciones;
    
    private final Escenario escenario;
    private final PerforacionManager perforacionManager;
    
    private final Map<Integer, Object[]> estrategiaPerforacionMap;
    
    public RePerforarEscenarioController(Escenario escenario) {
        this.escenario = escenario;
        this.estrategiaPerforacionMap = new LinkedHashMap<>();
        this.perforacionManager = new PerforacionManager();
    }

    @Override
    protected Map<Integer, Object[]> doInBackground() throws Exception {
        Map<Pozo, Map<String, Double>> pozoMap = makePozoDataMap();
        
        
        
        return estrategiaPerforacionMap;
    }

    @Override
    protected void done() {
        super.done(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    private Map<Pozo, Map<String, Double>> makePozoDataMap(){
        List<Perforacion> perforaciones = perforacionManager.findAllOrderedByFecha(escenario);
        Map<Pozo, Map<String, Double>> pozoMap = new HashMap<>();
        Map<String, Double> fasesMap = new HashMap<>();
        for(Perforacion perf : perforaciones){
            Pozo pozo = perf.getPozoId();
            if (pozoMap.containsKey(perf.getPozoId())) {
                fasesMap.put(perf.getFase(), perf.getDias());
            } else {
                fasesMap = new HashMap<>();
                fasesMap.put(perf.getFase(), perf.getDias());
            }
            pozoMap.put(pozo, fasesMap);
        }
        
        return pozoMap;
    }
    
    private List<Fila> getFilasPerforadas(){
        return null;
    }
    
    private List<Perforacion> getPerforaciones(){
        perforaciones = perforacionManager.findAllOrderedByFecha(escenario);
        return perforaciones;
    }
}
