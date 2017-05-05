package com.sismon.vista.controller;

import com.sismon.jpamanager.ProduccionMesInicialManager;
import com.sismon.model.Escenario;
import com.sismon.model.Pozo;
import com.sismon.model.ProduccionMesInicial;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MakeQueriesToDb {
    
    public Map<Pozo, Map<Date, Double>> generacionPotencialDeclinada(Escenario escenario){
        /*
        if( el año de aceptación != del año actual)
            retorna cero
        else if (mes de aceptación == mes en proceso)
            Pi * Potencia(1 - decl_dia, diasAlFinDeMes)
        else if(mes de aceptacion < mes en proceso)
            ProducionMesAnterior * (1 - decl_mes)
        else
            0
        */
        List<ProduccionMesInicial> pmiList = getPmiList(escenario);
        
        Year year = Year.now();
        int currentYear = year.getValue();
        LocalDate ldFin = LocalDate.of(currentYear, 12, 31);
        Map<Pozo, Map<Date, Double>> produccionPozoMap = new HashMap<>();
        for (ProduccionMesInicial pmi : pmiList) {    
            Map<Date, Double> produccionMap = new LinkedHashMap<>();
            
            Pozo pozo = pmi.getPozoId();
            Date fechaPozo = pmi.getFechaAceptacion();
            LocalDate ldPozo = LocalDateTime.ofInstant(Instant.
                    ofEpochMilli(fechaPozo.getTime()),
                    ZoneId.systemDefault()).toLocalDate();
            // variables necesarias para el calculo
            double declMes = 1 - Math.pow((1 - pozo.getDeclinacion() * 100), 1 / 12.0);
            double declDia = 1 - Math.pow((1 - pozo.getDeclinacion() * 100), 1 / 365.0);
            
            int finMes = ldPozo.getMonth().length(ldPozo.isLeapYear());
            int diasAlFinMes = finMes - ldPozo.getDayOfMonth();
            double prod = pozo.getPi() * Math.pow((1 - declDia), diasAlFinMes);
            
            ldPozo = getNextMonth(ldPozo, 0);
            fechaPozo = convertLocalDateToDate(ldPozo);
            produccionMap.put(fechaPozo, prod);
            do {
                prod = prod * (1 - declMes);
                ldPozo = getNextMonth(ldPozo, 1);
                fechaPozo = convertLocalDateToDate(ldPozo);
                produccionMap.put(fechaPozo, prod);
            } while (ldPozo.isBefore(ldFin));
            produccionPozoMap.put(pozo, produccionMap);
        }
        return produccionPozoMap;
    }
    
    private List<ProduccionMesInicial> getPmiList(Escenario escenario){
        ProduccionMesInicialManager pmiManager = new ProduccionMesInicialManager();
        return pmiManager.findAll(escenario);
    }
    
    private Date convertLocalDateToDate(LocalDate ld){
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    private LocalDate getNextMonth(LocalDate ld, int monthAmount){
        ld = ld.plusMonths(monthAmount);
        int finMes = ld.getMonth().length(ld.isLeapYear());
        ld = ld.withDayOfMonth(finMes);
        return ld;
    }
    
}
