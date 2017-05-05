package com.sismon.test;

import com.sismon.model.Escenario;
import com.sismon.vista.controller.ReportesController;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jgcastillo
 */
public class TestFechaMinMax {
    public static void main(String[] args) {
        ReportesController controller = new ReportesController();
        Escenario escenario = new Escenario(1);
        
        List<Object[]> fechas = controller.getFechaMinMax(escenario);
        for(Object[] array : fechas){
            for(Object obj : array){
                System.out.println((Date)obj);
            }
        }
        
    }
    
}
