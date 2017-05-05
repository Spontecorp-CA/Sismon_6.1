package com.sismon.test;

import com.sismon.jpamanager.PerforacionManager;
import com.sismon.model.Escenario;
import com.sismon.model.Perforacion;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 *
 * @author jgcastillo
 */
public class ConteoTest {

    public static void main(String[] args) {
        PerforacionManager perforacionManager = new PerforacionManager();
        Escenario escenario = new Escenario(1);

        List<Perforacion> perfList = perforacionManager.findAll(escenario);

        Optional<Date> fechaOpMin = perfList.stream()
                .map(p -> p.getFechaIn())
                .max((p1, p2) -> p2.compareTo(p1));

        Optional<Date> fechaOpMax = perfList.stream()
                .map(p -> p.getFechaIn())
                .max((p1, p2) -> p1.compareTo(p2));

        Date fechaMax = fechaOpMax.get();
        Date fechaMin = fechaOpMin.get();

        System.out.println("La fecha min es " + fechaMin);
        System.out.println("La fecha max es " + fechaMax);

        long cuenta = perfList.stream()
                .filter(p -> p.getFase().equals("Completación"))
                .count();
        System.out.println("El valor de cuenta es " + cuenta);

        LocalDate ldInicio = LocalDate.of(2014, Month.JANUARY, 1);
        LocalDate ldFin = LocalDate.of(2014, Month.DECEMBER, 31);

        Instant instant = ldInicio.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Date fechaInicio = Date.from(instant);
        instant = ldFin.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Date fechaFin = Date.from(instant);

        System.out.println("Va a recorrer : " + ldInicio.getYear());
        for (LocalDate ld = ldInicio; (ld.isBefore(ldFin) || ld.isEqual(ldFin)); ld = ld.plusDays(1)) {
            Instant inst = ld.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Date fecha = Date.from(inst);
            cuenta = perfList.stream()
                    .filter(p -> p.getFase().equals("Completación"))
                    .filter(p -> fecha.after(p.getFechaIn()) 
                            && (fecha.equals(p.getFechaOut()) || fecha.before(p.getFechaOut())))
                    .count();
            if(cuenta != 0){
            System.out.println(" El dia " + ld.format(DateTimeFormatter.ISO_DATE) 
                    + " tiene " + cuenta + " pozos");
            }
        }

    }
}
