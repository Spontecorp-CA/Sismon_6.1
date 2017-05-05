package com.sismon.test;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.TaladroStatusManager;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroStatus;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author jgcastillo
 */
public class TaladrosDescontinuadosTest {

    public static void main(String[] args) {
        TaladroStatusManager taladroStatusManager = new TaladroStatusManager();
        
        List<TaladroStatus> talStatusList = taladroStatusManager.findAll();
        Set<Taladro> lista = new HashSet<>();
        talStatusList.stream()
                .filter(ts -> Objects.equals(ts.getNombre(),
                        Constantes.TALADRO_STATUS_DESCONTINUADO))
                .forEach(ts -> {
                    lista.add(ts.getTaladroId());
                });
        
        lista.stream().forEach(ta ->{
            System.out.println("El taladro descontinuado es " 
                    + ta.getId() + " - " + ta.getNombre());
        });
        
    }
    
}
