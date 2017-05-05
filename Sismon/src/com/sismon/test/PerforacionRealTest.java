package com.sismon.test;

import com.sismon.jpamanager.PerforacionManager;
import com.sismon.model.Escenario;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jgcastillo
 */
public class PerforacionRealTest {

    private static PerforacionManager perforacionManager = new PerforacionManager();
    private static Escenario escenarioSelected = new Escenario(22);
    
    private static Map<Pozo, Map<String, Double>> makePozoDataMap() {
        List<Perforacion> perforaciones = perforacionManager.findAllOrderedByFecha(escenarioSelected);
        
        Map<Pozo, Map<String, Double>> pozoMap = new HashMap<>();
        Map<String, Double> fasesMap = null;
        
        for (Perforacion perf : perforaciones) {
            Pozo pozo = perf.getPozoId();
            if (pozoMap.containsKey(perf.getPozoId())) {
                fasesMap = pozoMap.get(pozo);
                fasesMap.put(perf.getFase(), perf.getDias());
            } else {
                fasesMap = new HashMap<>();
                fasesMap.put(perf.getFase(), perf.getDias());
            }
            pozoMap.put(pozo, fasesMap);
        }

        return pozoMap;
    }
    
    public static void main(String[] args) {
        Map<Pozo, Map<String, Double>> mapa = makePozoDataMap();
        for(Map.Entry<Pozo, Map<String, Double>> mimap : mapa.entrySet()){
            System.out.println("Pozo: " + mimap.getKey().toString());
            Map<String, Double> tumap = mimap.getValue();
            for(Map.Entry<String, Double> m : tumap.entrySet()){
                System.out.println("\t" + m.getKey() + ": " + m.getValue());
            }
            System.out.println("");
        }
    }
}
