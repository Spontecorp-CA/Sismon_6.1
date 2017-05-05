/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sismon.test;

import com.sismon.jpamanager.FilaHasTaladroManager;
import com.sismon.jpamanager.TaladroManager;
import com.sismon.model.Fila;
import com.sismon.model.Taladro;
import java.util.List;

/**
 *
 * @author jgcastillo
 */
public class TestFilaCollection {

    public static void main(String[] args) {
        FilaHasTaladroManager fhtManager = new FilaHasTaladroManager();
        TaladroManager taladroManager = new TaladroManager();
        
        long id = 315L;
        Taladro taladro = taladroManager.find(id);
        List<Fila> filas = fhtManager.findAll(taladro);
        
        for(Fila fila : filas){
            System.out.println("trajo " + fila.getNombre() + " de la macolla " 
                    + fila.getMacollaId().getNombre());
        }
    }
}
