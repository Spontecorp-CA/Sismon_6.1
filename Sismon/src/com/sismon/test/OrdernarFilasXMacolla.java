package com.sismon.test;

import com.sismon.jpamanager.FilaManager;
import com.sismon.jpamanager.MacollaSecuenciaManager;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.MacollaSecuencia;
import java.util.ArrayList;
import java.util.List;

public class OrdernarFilasXMacolla {

    public static void main(String[] args) {
        Escenario escenario = new Escenario(20);
        MacollaSecuenciaManager macSecManager = new MacollaSecuenciaManager();
        FilaManager filaManager = new FilaManager();
        
        List<MacollaSecuencia> macollasSecuencia = macSecManager.findAllOrdered(escenario);
        
        List<Fila> filasOrdered = new ArrayList<>();
        macollasSecuencia.stream()
                .map((ms) -> ms.getMacollaId())
                .map((macolla) -> filaManager.findAll(macolla))
                .forEach((filas) -> {
                    filas.stream()
                         .forEach(fl -> filasOrdered.add(fl));
        });
        
        filasOrdered.stream().forEach(fila -> {
            System.out.println("Macolla: "+ fila.getMacollaId() + " - Fila " + fila);
        });
    }
}
