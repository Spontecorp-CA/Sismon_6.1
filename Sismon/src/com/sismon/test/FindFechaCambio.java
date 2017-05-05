package com.sismon.test;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.TaladroHasFaseManager;
import com.sismon.model.Escenario;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroHasFase;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FindFechaCambio {

    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    public static void main(String[] args) {
        Taladro taladro = new Taladro(118L);
        Escenario escenario = new Escenario(17);
        
         // fecha inicial del taladro
        LocalDate ld = LocalDate.of(2013, Month.DECEMBER, 6);
        Date fechaInicial = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        // fecha del primer cambio
        ld = LocalDate.of(2014, Month.DECEMBER, 6);
        Date fechaCambio = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        

        String fase = Constantes.FASE_INTERMEDIO;
        Set<Date> fechas = new HashSet<>();

        Date fechaAplicar = fechaInicial;

        TaladroHasFaseManager thfManager = new TaladroHasFaseManager();
        List<TaladroHasFase> thfListTotal = thfManager.findAll(taladro, escenario);
        List<TaladroHasFase> thfList = thfManager.findAll(taladro, escenario, fechaAplicar);
        
        System.out.println("");

        Comparator<Date> porFecha = (Date d1, Date d2) -> d1.compareTo(d2);
        if(thfList.size() > 1){
            fechas = thfListTotal.stream()
                            .map(thf -> thf.getFecha())
                            .collect(Collectors.toSet());
        }
        List<Date> fechaList = new ArrayList<>(fechas);
        
        fechaList.sort(porFecha);
        fechaList.stream().forEach(System.out::println);
        
        // fecha de la fase
        ld = LocalDate.of(2017, Month.JULY, 11);
        Date fechaIn = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());

        System.out.println("");
        System.out.println("EL proceso: ");
        for(Date fechaCmb : fechaList){
            System.out.println("FechaIn " + df.format(fechaIn));
            System.out.println("Fecha Cambio " + df.format(fechaCmb));
            if(fechaIn.after(fechaCmb)){
                System.out.println("se perfora con la fht de fecha: " + df.format(fechaCmb));
            } else {
                System.out.println("Se cambio la fecha a " + df.format(fechaCmb));
                
            }
        
        }

        System.out.println("");
        System.out.println("");
        TaladroHasFase thf = thfManager.find(fase, taladro, escenario, fechaAplicar);
        System.out.println(thf.getId() + "\t" + thf.getFase() + "\t" + df.format(thf.getFecha()) + "\t" + thf.getDias());
    }

    
}
