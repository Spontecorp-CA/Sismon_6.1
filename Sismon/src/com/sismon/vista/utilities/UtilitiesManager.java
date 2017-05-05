 package com.sismon.vista.utilities;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.TaladroStatusManager;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroStatus;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UtilitiesManager {

    /**
     * Cambia el TaladroStatus de un Taladro a uno nuevo TaladroStatus en una fecha 
     * determinada. Si la fecha pasada es null, se asume que la fecha de cambio es la 
     * fechaIn del nuevo TaladroStatus.
     * 
     * @param taladro
     * @param statusAnt
     * @param statusNew
     * @param fechaCambio 
     * @return  
     */
    public synchronized static Date changeTaladroStatus(Taladro taladro, 
            String statusAnt, 
            String statusNew,
            Date fechaCambio){
        TaladroStatusManager tsManager = new TaladroStatusManager();
        
        // Inactiva el status anterior
        TaladroStatus taladroStatus = tsManager.find(
                taladro, 
                Constantes.TALADRO_STATUS_ACTIVO, 
                statusAnt);
        
        
        List<TaladroStatus> taladroStatusList = tsManager.find(taladro);
        Set<TaladroStatus> taladroStatusSet = new HashSet<>(taladroStatusList);
        for(TaladroStatus ts : taladroStatusSet){
            if (ts.equals(taladroStatus)) {
                if (fechaCambio == null) {
                    fechaCambio = ts.getFechaIn();
                }
                ts.setFechaOut(fechaCambio);
                ts.setStatus(Constantes.TALADRO_STATUS_INACTIVO);
                break;
            }
        }
        
        taladroStatus = new TaladroStatus();
        taladroStatus.setFechaIn(fechaCambio);
        taladroStatus.setNombre(statusNew);
        taladroStatus.setTaladroId(taladro);
        taladroStatus.setStatus(Constantes.TALADRO_STATUS_ACTIVO);
        
        taladroStatusSet.add(taladroStatus);
        taladroStatusList.clear();
        taladroStatusList.addAll(taladroStatusSet);
        taladro.setTaladroStatusCollection(taladroStatusList);
        
        return fechaCambio;
    }
    
    /**
     * Cambia el TaladroRealStatus de un TaladroReal a uno nuevo en una fecha
     * determinada. Si la fecha pasada es null, se asume que la fecha de cambio
     * es la fechaIn del status anterior.
     *
     * @param taladro
     * @param statusAnt
     * @param statusNew
     * @param fechaCambio
     * @return fecha del cambio de status
     */
//    public synchronized static Date changeTaladroRealStatus(TaladroReal taladro,
//            String statusAnt,
//            String statusNew,
//            Date fechaCambio) {
//        TaladroRealStatusManager trsManager = TaladroRealStatusManager.getDefault();
//        TaladroRealManager taladroRealManager = new TaladroRealManager();
//
//        // Inactiva el status anterior
//        TaladroRealStatus taladroRealStatus = trsManager.find(
//                taladro,
//                TaladroStatus.ACTIVO,
//                statusAnt);
//
//        List<TaladroRealStatus> taladroRealStatusList = trsManager.find(taladro);
//        Set<TaladroRealStatus> taladroRealStatusSet = new HashSet<>(taladroRealStatusList);
//        for (TaladroRealStatus trs : taladroRealStatusSet) {
//            if (trs.equals(taladroRealStatus)) {
//                if (fechaCambio == null) {
//                    fechaCambio = trs.getFechaIn();
//                }
//                trs.setFechaOut(fechaCambio);
//                trs.setStatus(TaladroStatus.INACTIVO);
//                break;
//            }
//        }
//
//        // crea el nuevo status y lo activa
//        taladroRealStatus = new TaladroRealStatus();
//        taladroRealStatus.setFechaIn(fechaCambio);
//        taladroRealStatus.setNombre(statusNew);
//        taladroRealStatus.setTaladroRealId(taladro);
//        taladroRealStatus.setStatus(TaladroStatus.ACTIVO);
//
//        taladroRealStatusSet.add(taladroRealStatus);
//        taladroRealStatusList.clear();
//        taladroRealStatusList.addAll(taladroRealStatusSet);
//        taladro.setTaladroRealStatusCollection(taladroRealStatusList);
//
//        taladro.setNombreStatus(statusNew);
//        taladro.setFechaStatus(fechaCambio);
//        taladroRealManager.edit(taladro);
//
//        return fechaCambio;
//    }
//    
//    public synchronized static Date setTaladroRealStatus(TaladroReal taladro,
//            String status,
//            Date fechaCambio){
//        TaladroRealStatusManager trsManager = TaladroRealStatusManager.getDefault();
//        TaladroRealManager taladroRealManager = new TaladroRealManager();
//        Set<TaladroRealStatus> taladroRealStatusSet = new HashSet<>();
//        List<TaladroRealStatus> taladroRealStatusList = new ArrayList<>();
//        
//        TaladroRealStatus taladroRealStatus;
//        
//        taladroRealStatus = new TaladroRealStatus();
//        taladroRealStatus.setFechaIn(fechaCambio);
//        taladroRealStatus.setNombre(status);
//        taladroRealStatus.setTaladroRealId(taladro);
//        taladroRealStatus.setStatus(TaladroStatus.ACTIVO);
//
//        taladroRealStatusSet.add(taladroRealStatus);
//        taladroRealStatusList.addAll(taladroRealStatusSet);
//        taladro.setTaladroRealStatusCollection(taladroRealStatusList);
//
//        taladro.setNombreStatus(status);
//        taladro.setFechaStatus(fechaCambio);
//        taladroRealManager.edit(taladro);
//        
//        return fechaCambio;
//    }
}
