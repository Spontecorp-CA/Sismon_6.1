package com.sismon.test;

import com.sismon.model.Escenario;
import com.sismon.model.Pozo;
import com.sismon.vista.controller.MakeQueriesToDb;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author jgcastillo
 */
public class MakeQueriesToDbTest {

    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat nf = new DecimalFormat("###,#00.00");
        MakeQueriesToDb mqtdb = new MakeQueriesToDb();
        Escenario escenario = new Escenario(16);
        Map<Pozo, Map<Date, Double>> mapa = mqtdb.generacionPotencialDeclinada(escenario);

        LocalDate ld = LocalDate.of(2016, 12, 31);
        Date lastDayYear = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Pozo pozo = null;
        for (Map.Entry<Pozo, Map<Date, Double>> map : mapa.entrySet()) {
            for (Map.Entry<Date, Double> data : map.getValue().entrySet()) {
                if (data.getKey().before(lastDayYear) || data.getKey().equals(lastDayYear)) {
                    if (!map.getKey().equals(pozo)) {
                        System.out.println("Pozo " + map.getKey().getUbicacion());
                        pozo = map.getKey();
                    }
                    System.out.println(df.format(data.getKey())
                            + " " + nf.format(data.getValue()));
                }
            }
        }
    }
}
