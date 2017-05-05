package com.sismon.test;

import com.sismon.exceptions.SismonException;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.jpamanager.MacollaManager;
import com.sismon.jpamanager.PozoManager;
import com.sismon.jpamanager.RampeoManager;
import com.sismon.model.Escenario;
import com.sismon.model.Macolla;
import com.sismon.model.Pozo;
import com.sismon.model.Rampeo;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jgcastillo
 */
public class BusquedaFunctional {

    public static void main(String[] args) {
        PozoManager pozoManager = new PozoManager();
        MacollaManager macollaManager = new MacollaManager();
        RampeoManager rampeoManager = new RampeoManager();
        EscenarioManager escenarioManager = new EscenarioManager();
        
        Escenario escenarioSelected = escenarioManager.find(49);
        Macolla macollaSelected = macollaManager.find(1);
        
        try {
            List<Pozo> pozos = pozoManager.findAll(macollaSelected, escenarioSelected);
//            pozos.stream()
//                    .forEach(p -> {
//                        System.out.println("Pozo " + p.getUbicacion() + ", id: " + p.getId());
//                    });
            
            Pozo primerPozo = pozos.stream()
                    .filter(p -> p.getNumero() == 1)
                    .findFirst().get();
            
            System.out.println(String.format("El primer pozo es %s y tiene id: %s", primerPozo, primerPozo.getId()));
            
            List<Rampeo> rampeos = rampeoManager.findAll(primerPozo, escenarioSelected);
            System.out.println("el pozo " + primerPozo + " tiene " + rampeos.size() + " rampeos");
            
            rampeos.stream()
                    .forEach(r -> System.out.println(String.format("Rampeo %s ", r.getNumero())));
            
            Optional<Rampeo> rampOp = rampeos.stream()
                                            .filter(rampeoByNumber(1))
                                            .findFirst();
               
            System.out.println("El rampeo encontrado es " + rampOp.get().getNumero() 
                    + " con id: " + rampOp.get().getId());
        } catch (SismonException ex) {
            Logger.getLogger(BusquedaFunctional.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static Predicate<Rampeo> rampeoByNumber(Integer i) {
        return r -> r.getNumero() == i;
    }
}
