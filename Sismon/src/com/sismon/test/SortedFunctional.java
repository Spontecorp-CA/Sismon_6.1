package com.sismon.test;

import com.sismon.jpamanager.PerforacionManager;
import com.sismon.model.Escenario;
import com.sismon.model.Perforacion;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

public class SortedFunctional {

    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    public static void main(String[] args) {
    
    PerforacionManager perforacionManager = new PerforacionManager();
    Escenario escenarioSelected = new Escenario(17);
    
    List<Perforacion> perforacioList = perforacionManager.findAll(escenarioSelected);
    
    Comparator comparator = Comparator.comparing((Perforacion p) -> p.getTaladroId().getNombre())
                                       .thenComparing((Perforacion p) -> p.getFechaIn());
    perforacioList.stream().sorted(comparator);
    
    perforacioList.stream().forEach(p -> {
        System.out.println(p.getTaladroId() + "\t" + df.format(p.getFechaIn()));
    });
    
    }
    
}
