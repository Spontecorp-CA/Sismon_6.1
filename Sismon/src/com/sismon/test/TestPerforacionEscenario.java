package com.sismon.test;

import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Pozo;
import com.sismon.model.Taladro;
import com.sismon.vista.controller.PerforacionEscenarioController4;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPerforacionEscenario {
    
    public static void main(String[] args) {
        
        System.out.println("Va a correr el proceso");
        doPerforacion();
    }
    
    private static void doPerforacion(){
        Escenario escenario = new Escenario(1);
        LocalDate ld = LocalDate.of(2014, Month.DECEMBER, 31);
        Date fechaCierre = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        PerforacionEscenarioController4 controller = new PerforacionEscenarioController4(escenario, fechaCierre);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat numberFormat = new DecimalFormat("###,###,###,###,#00.00");
        
        System.out.println("Se va a ejecutar");
        //controller.execute();
        
        try {
//            worker.execute();
//            worker.get();
            controller.execute();
            Map<Integer, Object[]> mapa = controller.get();
            
//            System.out.println("");
//            System.out.println("La data a presentar sera: ");
//            System.out.println("");
//            for(Map.Entry<Integer, Object[]> data : mapa.entrySet()){
//                Object[] datos = data.getValue();
//                System.out.print(data.getKey());
//                System.out.print("\t" + ((Taladro)datos[0]).getNombre());
//                System.out.print("\t" + ((Macolla)datos[1]).getNombre());
//                System.out.print("\t" + ((Fila)datos[2]).getNombre());
//                System.out.print("\t" + ((Pozo) datos[3]).getUbicacion());
//                System.out.print("\t" + datos[4].toString());
//                System.out.print("\t" + dateFormat.format((Date)datos[5]));
//                System.out.print("\t" + dateFormat.format((Date) datos[6]));
//                System.out.print("\t" + numberFormat.format((Double)datos[7]));
//                System.out.print("\t" + numberFormat.format((Double) datos[8]));
//                System.out.print("\t" + numberFormat.format((Double) datos[9]));
//                System.out.print("\t" + datos[10].toString());
//                System.out.print("\t" + numberFormat.format((Double) datos[11]));
//                System.out.print("\t" + numberFormat.format((Double) datos[12]));
//                System.out.println("\t" + numberFormat.format((Double) datos[13]));
//            }
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(TestPerforacionEscenario.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Listo");
    }
    
}

