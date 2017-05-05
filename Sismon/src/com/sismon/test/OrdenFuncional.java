package com.sismon.test;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Perforacion;
import com.sismon.model.Taladro;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdenFuncional {

    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    
    public static void main(String[] args) {
        PerforacionManager perforacionManager = new PerforacionManager();
        Escenario escenario = new Escenario(20);

        List<Perforacion> perforaciones = perforacionManager.findAllOrderedByFecha(escenario);
        Map<Fila, List<Perforacion>> filasMap = new HashMap<>();
        
        Fila fila = new Fila();
        List<Perforacion> perforacionList = null;
        for(Perforacion perf : perforaciones){
            if(!fila.equals(perf.getFilaId())){
                fila = perf.getFilaId();
                perforacionList = new ArrayList<>();
                perforacionList.add(perf);
                filasMap.put(fila, perforacionList);
            } else {
                fila = perf.getFilaId();
                perforacionList = filasMap.get(fila);
                perforacionList.add(perf);
                filasMap.put(fila, perforacionList);
            }
        }
        
        int i = 1;
        for(Map.Entry<Fila, List<Perforacion>> mapi : filasMap.entrySet()){
            
            List<Perforacion> lista = mapi.getValue();
            Perforacion minPerf = lista
                    .stream()
                    .min(Comparator.comparing(Perforacion::getFechaIn))
                    .get();
            
            Perforacion maxPerf = lista
                    .stream()
                    .filter(perf -> perf.getFase().equals(Constantes.FASE_PRODUCTOR))
                    .max(Comparator.comparing(Perforacion::getFechaOut))
                    .get();
            
            System.out.println((i++)+ " " + minPerf.getTaladroId() 
                        + "\t" + minPerf.getMacollaId() 
                        + "\t" + mapi.getKey() 
                        + "\t" + df.format(minPerf.getFechaIn()) 
                        + "\t" + df.format(maxPerf.getFechaOut()));
                    
        }
        
        System.out.println("");
        System.out.println("");
        
        int consecutivo = 0;
        fila = new Fila();
        Taladro taladro = new Taladro();
        Map<Integer, List<Perforacion>> conseMap = new HashMap<>();
        for(Perforacion perf : perforaciones){
            if(fila.equals(perf.getFilaId()) && taladro.equals(perf.getTaladroId())){
                perforacionList = conseMap.get(consecutivo);
            } else {
                fila = perf.getFilaId();
                taladro = perf.getTaladroId();
                perforacionList = new ArrayList<>();
                consecutivo++;
            }
            perforacionList.add(perf);
            conseMap.put(consecutivo, perforacionList);
        }
        
        i = 0;
        Object[][] data = new Object[conseMap.size()][5];
        for (Map.Entry<Integer, List<Perforacion>> mapi : conseMap.entrySet()) {

            List<Perforacion> lista = mapi.getValue();
            Perforacion minPerf = lista
                    .stream()
                    .min(Comparator.comparing(Perforacion::getFechaIn))
                    .get();

            Perforacion maxPerf = lista
                    .stream()
                    .filter(perf -> perf.getFase().equals(Constantes.FASE_PRODUCTOR))
                    .max(Comparator.comparing(Perforacion::getFechaOut))
                    .get();

            data[i][0] = minPerf.getTaladroId();
            data[i][1] = minPerf.getMacollaId();
            data[i][2] = minPerf.getFilaId();
            data[i][3] = minPerf.getFechaIn();
            data[i][4] = maxPerf.getFechaOut();
            i++;            
        }
        
        for(Object[] dat : data ){
        System.out.println(((Taladro)dat[0])
                + "\t" + ((Macolla) dat[1])
                + "\t" + ((Fila) dat[2])
                + "\t" + df.format(((Date) dat[3]))
                + "\t" + df.format(((Date) dat[4])));
        }
    }
    
    
    
    
}
