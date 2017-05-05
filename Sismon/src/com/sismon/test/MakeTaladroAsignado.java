package com.sismon.test;

import com.sismon.jpamanager.FilaHasTaladroManager;
import com.sismon.jpamanager.PozoSecuenciaManager;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.FilaHasTaladro;
import com.sismon.model.PozoSecuencia;
import com.sismon.model.Taladro;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author jgcastillo
 */
public class MakeTaladroAsignado {

    private static final FilaHasTaladroManager fhtManager = new FilaHasTaladroManager();
    private static final PozoSecuenciaManager psManager = new PozoSecuenciaManager();
    
    public static void main(String[] args) {
        List<FilaHasTaladro> fhtList = fhtManager.findAllBase();
        List<PozoSecuencia> psList = psManager.findAllBase();
        Escenario escenario = new Escenario(29);
        
        makeTaladroAsignado(fhtList, psList, escenario);
    }
    
    private static void makeTaladroAsignado(List<FilaHasTaladro> fhtList, 
            List<PozoSecuencia> psList, Escenario escenario){
        for(FilaHasTaladro fht : fhtList){
            Fila fila = fht.getFilaId();
            Taladro taladro = fht.getTaladroId();
            
            List<PozoSecuencia> secuenciaDeFila = psList.stream()
                    .filter(pzsec -> pzsec.getFilaId().equals(fila))
                    .collect(Collectors.toList());
            
            Optional<PozoSecuencia> opMin = secuenciaDeFila.stream()
                    .min((ps1, ps2) -> 
                            ps1.getSecuencia().compareTo(ps2.getSecuencia()));
            
            Optional<PozoSecuencia> opMax = secuenciaDeFila.stream()
                    .max((ps1, ps2)
                            -> ps1.getSecuencia().compareTo(ps2.getSecuencia()));
            
            PozoSecuencia psMin = opMin.get();
            PozoSecuencia psMax = opMax.get();
            
            System.out.print("Para la fila " + fila + "( macolla: "
                    + fila.getMacollaId() + "): ");
            System.out.println("La sec de inicio es: " + psMin.getSecuencia() 
                    + " del pozo " + psMin.getPozoId().getUbicacion());
            System.out.println("La sec de salida es: " + psMax.getSecuencia()
                    + " del pozo " + psMax.getPozoId().getUbicacion());
            
        }
    }
}
