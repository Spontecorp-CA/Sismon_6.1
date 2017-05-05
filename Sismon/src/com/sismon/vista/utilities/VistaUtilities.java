package com.sismon.vista.utilities;

import com.sismon.jpamanager.ParidadManager;
import com.sismon.model.Paridad;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;

public class VistaUtilities {

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public static boolean OnStart(){
        boolean checked = false;
        try {
            ParidadManager paridadManager = new ParidadManager();
            List<Paridad> paridades = paridadManager.findAll();
            if(paridades != null){
                checked = true;
            }
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "La paridad no ha sido inicializada", e);
            return checked;
        }
        return checked;
    }
    
    public static double parseDouble(String numero) throws ParseException {
        String convertido = numero.replaceAll("[^\\d,\\.]++", "");
        if (convertido.matches(".+\\.\\d+,\\d+$")) {
            return Double.parseDouble(convertido.replaceAll("\\.", "").replaceAll(",", "."));
        }

        if (convertido.matches(".+,\\d+\\.\\d+$")) {
            return Double.parseDouble(convertido.replaceAll(",", ""));
        }
        return Double.parseDouble(convertido.replaceAll(",", "."));
    }
    
}
