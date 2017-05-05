package com.sismon.vista.controller;

import com.sismon.exceptions.SismonException;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.jpamanager.ExplotacionManager;
import com.sismon.jpamanager.FilaManager;
import com.sismon.jpamanager.MacollaSecuenciaManager;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.jpamanager.PozoExplotadoManager;
import com.sismon.jpamanager.PozoManager;
import com.sismon.jpamanager.PozoSecuenciaManager;
import com.sismon.jpamanager.RampeoManager;
import com.sismon.jpamanager.TaladroAsignadoManager;
import com.sismon.jpamanager.TipoPozoManager;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Pozo;
import com.sismon.model.Rampeo;
import com.sismon.model.TaladroAsignado;
import com.sismon.model.TipoPozo;
import com.sismon.vista.utilities.SismonLog;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AgregarPozosController {

    private final EscenarioManager escenarioManager;
    private final MacollaSecuenciaManager macollaSecuenciaManager;
    private final FilaManager filaManager;
    private final PozoManager pozoManager;
    private final RampeoManager rampeoManager;
    private final TipoPozoManager tipoPozoManager;
    private final TaladroAsignadoManager taladroAsignadoManager;
    private final PozoSecuenciaManager pozoSecuenciaManager;
    private final PozoExplotadoManager pozoExplotadoManager;
    private final ExplotacionManager explotacionManager;
    private final PerforacionManager perforacionManager;
    
    private static final SismonLog SISMON_LOG = SismonLog.getInstance();
    
    public AgregarPozosController() {
        this.escenarioManager = new EscenarioManager();
        this.macollaSecuenciaManager = new MacollaSecuenciaManager();
        this.filaManager = new FilaManager();
        this.pozoManager = new PozoManager();
        this.rampeoManager = new RampeoManager();
        this.tipoPozoManager = new TipoPozoManager();
        this.taladroAsignadoManager = new TaladroAsignadoManager();
        this.pozoSecuenciaManager = new PozoSecuenciaManager();
        this.pozoExplotadoManager = new PozoExplotadoManager();
        this.explotacionManager = new ExplotacionManager();
        this.perforacionManager = new PerforacionManager();
    }
    
    public List<Escenario> getEscenarios() {
        return escenarioManager.findAllMV(false);
    }
    
    public List<Macolla> getMacollasEnEscenario(Escenario escenario) throws SismonException {
        return macollaSecuenciaManager.findAll(escenario);
    }
    
    public List<Fila> getFilasEnMacolla(Macolla macolla) {
        return filaManager.findAll(macolla);
    }
    
    public List<Pozo> getPozosEnFila(Macolla macolla, Fila fila, Escenario escenario) {
        return pozoManager.findAll(macolla, fila, escenario);
    }
    
    public Pozo getSelectedPozo(Long id) {
        return pozoManager.find(id);
    }
    
    public List<Rampeo> getRampeos(Pozo pozo, Escenario escenario) {
        return rampeoManager.findAll(pozo, escenario);
    }
    
    public Set<String> getTipoPozos() {
        List<TipoPozo> tipoPozoList = tipoPozoManager.findAllOrdered();
        final Set<String> tipoPozoSet = new LinkedHashSet<>();
        tipoPozoList.stream().forEach((tp) -> {
            tipoPozoSet.add(tp.getTipo());
        });
        return tipoPozoSet;
    }
    
    public Set<String> getCodigoPozo(String tipoPozo) {
        List<TipoPozo> tipoPozoList = tipoPozoManager.findAllOrdered();
        final Set<String> codigoPozoSet = new LinkedHashSet<>();
        tipoPozoList.stream()
                .filter(tp -> tp.getTipo().equals(tipoPozo))
                .forEach(tp -> {
                    codigoPozoSet.add(tp.getCodigo());
                });
        return codigoPozoSet;
    }
    
    public void savePozo(Pozo pozo){
        pozoManager.create(pozo);
    }
    
    public void saveRampeo(List<Rampeo> rampeos){
        rampeoManager.batchSave(rampeos);
    }
    
    public boolean isPozoInTaladroAsignado(Escenario escenario, Pozo pozo){
        List<TaladroAsignado> lista = taladroAsignadoManager.find(escenario, pozo);
        return !(lista == null || lista.isEmpty());
    }
    
    public void deletePozoSecuencia(Escenario escenario, Fila fila){
        pozoSecuenciaManager.remove(fila, escenario);
    }
    
    public void deleteExplotacion(Pozo pozo){
        pozoExplotadoManager.remove(pozo);
        explotacionManager.remove(pozo);
    }
    
    public void deletePerforacion(Escenario escenario, Pozo pozo){
        perforacionManager.remove(escenario, pozo);
    }
    
    public void deleteRampeos(Escenario escenario, Pozo pozo){
        rampeoManager.remove(escenario, pozo);
    }
    
    public void deletePozo(Escenario escenario, Pozo pozo){
        pozoManager.remove(escenario, pozo);
    }
}
