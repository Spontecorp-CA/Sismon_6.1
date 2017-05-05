package com.sismon.test;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.model.Escenario;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import java.util.List;
import java.util.stream.Collectors;

public class PowTest {
    public static void main(String[] args) {
        PerforacionManager perforacionManager = new PerforacionManager();
        Escenario escenarioSelected = new Escenario(28);
        Pozo pozo = new Pozo(9692L);
        List<Perforacion> perforaciones = perforacionManager
                .findAll(escenarioSelected, pozo);
        List<Perforacion> perfFiltrado = perforaciones.stream()
                .filter(perf -> (!perf.getFase().equals(Constantes.FASE_COMPLETACION)
                        && !perf.getFase().equals(Constantes.FASE_CONEXION)
                        && !perf.getFase().equals(Constantes.FASE_EVALUACION)))
                .collect(Collectors.toList());
        perfFiltrado.stream().forEach( perf -> {
            System.out.println(perf.getFase());
        });
               
    }
}
