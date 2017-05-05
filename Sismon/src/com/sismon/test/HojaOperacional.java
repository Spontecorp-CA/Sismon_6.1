package com.sismon.test;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.jpamanager.PozoSecuenciaManager;
import com.sismon.model.Escenario;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.model.PozoSecuencia;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jgcastillo
 */
public class HojaOperacional {

    public static void main(String[] args) {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        Escenario escenario = new Escenario(27);
        PerforacionManager perforacionManager = new PerforacionManager();
        PozoSecuenciaManager secuenciaManager = new PozoSecuenciaManager();
        List<Perforacion> perfList = perforacionManager.findAllOrderedByPozo(escenario);

//        perfList.stream().forEach(perf -> {
//            System.out.println(perf.getMacollaId().getNombre() + "\t"
//                    + perf.getTaladroId().getNombre() + "\t"
//                    + perf.getPozoId() + "\t"
//                    + perf.getFase() + "\t"
//                    + df.format(perf.getFechaIn()) + "\t"
//                    + df.format(perf.getFechaOut()));
//        });
        
        Map<Pozo, Object[]> dataMap = new LinkedHashMap<>();
        Pozo pozoTemp = null;
        Object[] data = null;
        int i = 0;
        for (Perforacion perf : perfList) {
            Pozo pozo = perf.getPozoId();
            if (!pozo.equals(pozoTemp)) {
                data = new Object[42];
                pozoTemp = pozo;
                
                data[0] = perf.getMacollaId(); // macolla
                data[1] = pozo; // pozo
                switch (perf.getFase()) {
                    case Constantes.FASE_MUDANZA_ENTRE_MACOLLAS:
                        data[2] = perf.getTaladroId(); // taladro
                        data[3] = perf.getFase(); // fase
                        data[4] = perf.getFechaIn(); // fechaIn
                        data[5] = perf.getFechaOut(); // fechaOut 
                        break;
                    case Constantes.FASE_SUPERFICIAL:
                        data[6] = perf.getTaladroId(); // taladro
                        data[7] = perf.getFase(); // fase
                        data[8] = perf.getFechaIn(); // fechaIn
                        data[9] = perf.getFechaOut(); // fechaOut 
                        break;
                }
            } else {
                switch (perf.getFase()) {
                    case Constantes.FASE_SUPERFICIAL:
                        data[6] = perf.getTaladroId(); // taladro
                        data[7] = perf.getFase(); // fase
                        data[8] = perf.getFechaIn(); // fechaIn
                        data[9] = perf.getFechaOut(); // fechaOut 
                        break;
                    case Constantes.FASE_SLANT:
                        data[10] = perf.getTaladroId(); // taladro
                        data[11] = perf.getFase(); // fase
                        data[12] = perf.getFechaIn(); // fechaIn
                        data[13] = perf.getFechaOut(); // fechaOut 
                        break;
                    case Constantes.FASE_VERTICAL:
                        data[14] = perf.getTaladroId(); // taladro
                        data[15] = perf.getFase(); // fase
                        data[16] = perf.getFechaIn(); // fechaIn
                        data[17] = perf.getFechaOut(); // fechaOut 
                        break;
                    case Constantes.FASE_PILOTO:
                        data[18] = perf.getTaladroId(); // taladro
                        data[19] = perf.getFase(); // fase
                        data[20] = perf.getFechaIn(); // fechaIn
                        data[21] = perf.getFechaOut(); // fechaOut 
                        break;
                    case Constantes.FASE_INTERMEDIO:
                        data[22] = perf.getTaladroId(); // taladro
                        data[23] = perf.getFase(); // fase
                        data[24] = perf.getFechaIn(); // fechaIn
                        data[25] = perf.getFechaOut(); // fechaOut 
                        break;
                    case Constantes.FASE_PRODUCTOR:
                        data[26] = perf.getTaladroId(); // taladro
                        data[27] = perf.getFase(); // fase
                        data[28] = perf.getFechaIn(); // fechaIn
                        data[29] = perf.getFechaOut(); // fechaOut 
                        break;
                    case Constantes.FASE_COMPLETACION:
                        data[30] = perf.getTaladroId(); // taladro
                        data[31] = perf.getFase(); // fase
                        data[32] = perf.getFechaIn(); // fechaIn
                        data[33] = perf.getFechaOut(); // fechaOut 
                        break;
                    case Constantes.FASE_CONEXION:
                        data[34] = perf.getTaladroId(); // taladro
                        data[35] = perf.getFase(); // fase
                        data[36] = perf.getFechaIn(); // fechaIn
                        data[37] = perf.getFechaOut(); // fechaOut 
                        break;
                    case Constantes.FASE_EVALUACION:
                        data[38] = perf.getTaladroId(); // taladro
                        data[39] = perf.getFase(); // fase
                        data[40] = perf.getFechaIn(); // fechaIn
                        data[41] = perf.getFechaOut(); // fechaOut 
                        break;
                }
            }
            i++;
            if (i < perfList.size() && perfList.get(i).getPozoId().equals(pozo)) {
                dataMap.put(pozo, data);
            }
        }
        
        for (Map.Entry<Pozo, Object[]> mapa : dataMap.entrySet()) {
            Object[] datos = mapa.getValue();
            System.out.print(datos[0] + "\t"
                    + mapa.getKey().getUbicacion() + "\t");
            for(int k = 2; k < datos.length; k += 4){
                if(datos[k] == null){
                    continue;
                }
                System.out.print(datos[k] + "\t"
                        + datos[k + 1] + "\t"
                        + df.format((Date) datos[k + 2]) + "\t"
                        + df.format((Date) datos[k + 3]) + "\t");
                if(((String)datos[k + 1]).equals(Constantes.FASE_SLANT)){
                    System.out.println("");
                }
                if(k >= 38){
                    System.out.println("");
                }
            }
        }

//        List<PozoSecuencia> secList = secuenciaManager.findAllOrderedByFila(escenario);
//        Map<Pozo, Object[]> dataSortedMap = new LinkedHashMap<>();
//        secList.stream().forEach((ps) -> {
//            Pozo pozo = ps.getPozoId();
//            if (ps.getFase().equals(Constantes.FASE_SUPERFICIAL)) {
//                Object[] datos = dataMap.get(pozo);
//                dataSortedMap.put(pozo, datos);
//            }
//        });

//        for (Map.Entry<Pozo, Object[]> mapa : dataSortedMap.entrySet()) {
//            Object[] datos = mapa.getValue();
//            System.out.print(datos[0] + "\t"
//                    + datos[1] + "\t"
//                    + mapa.getKey().getUbicacion() + "\t");
//            for (int k = 2; k < datos.length; k += 3) {
//                if (datos[k] == null) {
//                    continue;
//                }
//                System.out.print(datos[k] + "\t"
//                        + df.format((Date) datos[k + 1]) + "\t"
//                        + df.format((Date) datos[k + 2]) + "\t");
//                if (k >= 29) {
//                    System.out.println("");
//                }
//            }
//        }
    }
    
    private static boolean checkPerforacion(String fase){
        switch(fase){
            case Constantes.FASE_COMPLETACION:
            case Constantes.FASE_CONEXION:
            case Constantes.FASE_EVALUACION:    
                return false;
            default:
                return true;
        } 
    }
}
