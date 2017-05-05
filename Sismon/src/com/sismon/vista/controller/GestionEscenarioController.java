package com.sismon.vista.controller;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.jpamanager.ExplotacionManager;
import com.sismon.jpamanager.FilaHasTaladroManager;
import com.sismon.jpamanager.MacollaExplotadaManager;
import com.sismon.jpamanager.MacollaSecuenciaManager;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.jpamanager.PozoExplotadoManager;
import com.sismon.jpamanager.PozoManager;
import com.sismon.jpamanager.PozoSecuenciaManager;
import com.sismon.jpamanager.RampeoManager;
import com.sismon.jpamanager.TaladroAsignadoManager;
import com.sismon.jpamanager.TaladroHasFaseManager;
import com.sismon.jpamanager.TaladroManager;
import com.sismon.jpamanager.TaladroMantManager;
import com.sismon.jpamanager.TaladroStatusManager;
import com.sismon.jpamanager.TaladroSustitutoManager;
import com.sismon.jpamanager.TaladroTrazaManager;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.FilaHasTaladro;
import com.sismon.model.MacollaSecuencia;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.model.PozoSecuencia;
import com.sismon.model.Rampeo;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroAsignado;
import com.sismon.model.TaladroHasFase;
import com.sismon.model.TaladroMant;
import com.sismon.model.TaladroStatus;
import com.sismon.model.TaladroSustituto;
import com.sismon.model.TaladroTraza;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeEvent;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class GestionEscenarioController extends SwingWorker<Boolean, Void> {

    private final EscenarioManager escenarioManager;
    private final TaladroManager taladroManager;
    private final TaladroStatusManager taladroStatusManager;
    private final TaladroHasFaseManager thFaseManager;
    private final TaladroSustitutoManager sustitutoManager;
    private final TaladroMantManager mantenimientoManager;
    private final TaladroAsignadoManager taladroAsignadoManager;
    private final MacollaSecuenciaManager macollaSecManager;
    private final PozoSecuenciaManager pozoSecManager;
    private final FilaHasTaladroManager fhTaladroManager;
    private final PozoManager pozoManager;
    private final RampeoManager rampeoManager;
    private final PerforacionManager perfManager;
    private final ExplotacionManager explotacionManager;
    private final PozoExplotadoManager pozoExplotadoManager;
    private final MacollaExplotadaManager macollaExplotadaManager;
    private final TaladroTrazaManager taladroTrazaManager;

    private String nombre;
    private String comentario;
    private String archivoPremisas;
    private Date fecha;
    private Escenario escenarioActual;
    private Escenario escenarioOrigen;
    private int tipoEscenario;
    private int registros = 0;
    private boolean finalizado;
    private boolean isCreating;
    private JProgressBar progressBar;

    private static final SismonLog sismonlog = SismonLog.getInstance();

    private GestionEscenarioController() {
        this.escenarioManager = new EscenarioManager();
        this.taladroManager = new TaladroManager();
        this.taladroStatusManager = new TaladroStatusManager();
        this.taladroAsignadoManager = new TaladroAsignadoManager();
        this.thFaseManager = new TaladroHasFaseManager();
        this.macollaSecManager = new MacollaSecuenciaManager();
        this.pozoSecManager = new PozoSecuenciaManager();
        this.fhTaladroManager = new FilaHasTaladroManager();
        this.pozoManager = new PozoManager();
        this.rampeoManager = new RampeoManager();
        this.perfManager = new PerforacionManager();
        this.mantenimientoManager = new TaladroMantManager();
        this.sustitutoManager = new TaladroSustitutoManager();
        this.explotacionManager = new ExplotacionManager();
        this.pozoExplotadoManager = new PozoExplotadoManager();
        this.macollaExplotadaManager = new MacollaExplotadaManager();
        this.taladroTrazaManager = new TaladroTrazaManager();
    }

    public GestionEscenarioController(String nombre, Date fecha, String comentario,
            JProgressBar progressBar, String archivoPremisas, int tipoEscenario,
            Escenario escenarioOrigen) {
        this();
        this.progressBar = progressBar;
        this.nombre = nombre;
        this.fecha = fecha;
        this.comentario = comentario;
        this.archivoPremisas = archivoPremisas;
        this.tipoEscenario = tipoEscenario;
        this.escenarioOrigen = escenarioOrigen;

        isCreating = true;
        init();
    }

    public GestionEscenarioController(Escenario escenario, JProgressBar progressBar) {
        this();
        this.escenarioActual = escenario;
        this.progressBar = progressBar;
        isCreating = false;
    }

    private void init() {
        setProgress(0);

        this.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if ("progress".equals(evt.getPropertyName())) {
                int progress1 = (Integer) evt.getNewValue();
                progressBar.setValue(progress1);
            }
        });
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        finalizado = false;
        if (isCreating) {
            crearEscenario();
        } else {
            eliminarEscenario();
        }
        return finalizado;
    }

    @Override
    protected void done() {
        try {
            get(10, TimeUnit.SECONDS);
            progressBar.setIndeterminate(true);
            progressBar.setStringPainted(false);
            progressBar.setVisible(false);
            setProgress(100);
            if (isCreating) {
                Contexto.showMessage("Escenario creado...", Constantes.MENSAJE_INFO);
            } else {
                Contexto.showMessage("Escenario eliminado...", Constantes.MENSAJE_INFO);
            }
        } catch (InterruptedException | ExecutionException e) {
            if (isCreating) {
                Contexto.showMessage("Error generando el escenario", Constantes.MENSAJE_ERROR);
                sismonlog.logger.log(Level.SEVERE, "Ocurrió un error generando escenario", e);
            } else {
                Contexto.showMessage("Error eliminando el escenario", Constantes.MENSAJE_ERROR);
                sismonlog.logger.log(Level.SEVERE, "Ocurrió un error eliminando escenario", e);
            }
        } catch (Exception e) {
            Contexto.showMessage("No puede repertir el nombre del Escenario", Constantes.MENSAJE_ERROR);
            sismonlog.logger.log(Level.SEVERE, "Error. Nombre de Escenario ya existe", e);
        }
    }

    private List<Taladro> taladrosList() {
        List<Taladro> lista = null;
        switch (tipoEscenario) {
            case Constantes.ESCENARIO_BASE:
                lista = taladroManager.findAllBase();
                break;
            case Constantes.ESCENARIO_PRUEBA:
            case Constantes.ESCENARIO_MEJOR_VISION:
                lista = taladroManager.findAll(escenarioOrigen);
                break;
        }
        registros += lista.size();
        return lista;
    }

    private List<TaladroHasFase> thFaseList() {
        List<TaladroHasFase> lista = null;
        switch (tipoEscenario) {
            case Constantes.ESCENARIO_BASE:
                lista = thFaseManager.findAllBase();
                break;
            case Constantes.ESCENARIO_PRUEBA:
            case Constantes.ESCENARIO_MEJOR_VISION:
                lista = thFaseManager.findAll(escenarioOrigen);
                break;
        }
        registros += lista.size();
        return lista;
    }

    private List<MacollaSecuencia> macollaSecList() {
        List<MacollaSecuencia> lista = null;
        switch (tipoEscenario) {
            case Constantes.ESCENARIO_BASE:
                lista = macollaSecManager.findAllBase();
                break;
            case Constantes.ESCENARIO_PRUEBA:
            case Constantes.ESCENARIO_MEJOR_VISION:
                lista = macollaSecManager.findAllOrdered(escenarioOrigen);
                break;
        }
        registros += lista.size();
        return lista;
    }

    private List<PozoSecuencia> pozoSecList() {
        List<PozoSecuencia> lista = null;
        switch (tipoEscenario) {
            case Constantes.ESCENARIO_BASE:
                lista = pozoSecManager.findAllBase();
                break;
            case Constantes.ESCENARIO_PRUEBA:
            case Constantes.ESCENARIO_MEJOR_VISION:
                lista = pozoSecManager.findAll(escenarioOrigen);
                break;
        }
        registros += lista.size();
        return lista;
    }

    private List<FilaHasTaladro> fhTaladroList() {
        List<FilaHasTaladro> lista = null;
        switch (tipoEscenario) {
            case Constantes.ESCENARIO_BASE:
                lista = fhTaladroManager.findAllBase();
                break;
            case Constantes.ESCENARIO_PRUEBA:
            case Constantes.ESCENARIO_MEJOR_VISION:
                lista = fhTaladroManager.findAll(escenarioOrigen);
                break;
        }
        registros += lista.size();
        return lista;
    }

    private List<Rampeo> rampeoList() {
        List<Rampeo> lista = null;
        switch (tipoEscenario) {
            case Constantes.ESCENARIO_BASE:
                lista = rampeoManager.findAllBase();
                break;
            case Constantes.ESCENARIO_PRUEBA:
            case Constantes.ESCENARIO_MEJOR_VISION:
                lista = rampeoManager.findAll(escenarioOrigen);
                break;
        }
        registros += lista.size();
        return lista;
    }

    private List<Pozo> pozoList() {
        List<Pozo> lista = null;
        switch (tipoEscenario) {
            case Constantes.ESCENARIO_BASE:
                lista = pozoManager.findAllBase();
                break;
            case Constantes.ESCENARIO_PRUEBA:
            case Constantes.ESCENARIO_MEJOR_VISION:
                lista = pozoManager.findAll(escenarioOrigen);
                break;
        }
        registros += lista.size();
        return lista;
    }

    private List<Perforacion> perfList() {
        List<Perforacion> lista = null;
        switch (tipoEscenario) {
            case Constantes.ESCENARIO_BASE:
                lista = perfManager.findAllBase();
                break;
            case Constantes.ESCENARIO_PRUEBA:
            case Constantes.ESCENARIO_MEJOR_VISION:
                lista = perfManager.findAll(escenarioOrigen);
                break;
        }
        registros += lista.size();
        return lista;
    }

    private List<TaladroStatus> tsList() {
        List<TaladroStatus> lista = taladroStatusManager.findAll();
        registros += lista.size();
        return lista;
    }

    private List<TaladroAsignado> talAsigList() {
        List<TaladroAsignado> talAsigList = new ArrayList<>();
        switch (tipoEscenario) {
            case Constantes.ESCENARIO_BASE:
                List<FilaHasTaladro> fhtList = fhTaladroManager.findAllBase();
                List<PozoSecuencia> psList = pozoSecManager.findAllBase();
                for (FilaHasTaladro fht : fhtList) {
                    Fila fila = fht.getFilaId();
                    Taladro taladro = fht.getTaladroId();
                    List<PozoSecuencia> secuenciaDeFila = psList.stream()
                            .filter(pzsec -> pzsec.getFilaId().equals(fila))
                            .collect(Collectors.toList());

                    Optional<PozoSecuencia> opIn = secuenciaDeFila.stream()
                            .min((ps1, ps2)
                                    -> ps1.getSecuencia().compareTo(ps2.getSecuencia()));

                    Optional<PozoSecuencia> opOut = secuenciaDeFila.stream()
                            .max((ps1, ps2)
                                    -> ps1.getSecuencia().compareTo(ps2.getSecuencia()));

                    TaladroAsignado tasg = new TaladroAsignado();
                    tasg.setEscenarioId(escenarioActual);
                    tasg.setOrden(1); // debido a que es la primera asignación
                    tasg.setFaseIn(opIn.get().getFase());
                    tasg.setFaseOut(opOut.get().getFase());
                    tasg.setFilaId(fila);
                    Pozo pozoOrigen = opIn.get().getPozoId();
                    Pozo pozoIn = pozoManager.find(pozoOrigen.getUbicacion(), escenarioActual);
                    tasg.setPozoInId(pozoIn);
                    pozoOrigen = opOut.get().getPozoId();
                    Pozo pozoOut = pozoManager.find(pozoOrigen.getUbicacion(), escenarioActual);
                    tasg.setPozoOutId(pozoOut);
                    PozoSecuencia ps = pozoSecManager.find(escenarioActual, pozoIn, opIn.get().getFase());
                    tasg.setPozoSecuenciaInId(ps);
                    ps = pozoSecManager.find(escenarioActual, pozoOut, opOut.get().getFase());
                    tasg.setPozoSecuenciaOutId(ps);
                    Taladro tal = taladroManager.find(taladro.getNombre(), escenarioActual);
                    tasg.setTaladroId(tal);
                    talAsigList.add(tasg);
                }
                break;
            case Constantes.ESCENARIO_PRUEBA:
            case Constantes.ESCENARIO_MEJOR_VISION:
                List<Taladro> talList = taladroManager.findAll(escenarioActual);
                Map<String, Taladro> taladroMap = new HashMap<>();
                talList.stream().forEach((tld) -> {
                    taladroMap.put(tld.getNombre(), tld);
                });
                List<TaladroAsignado> taTempList = taladroAsignadoManager.findAll(escenarioOrigen);
                
                for (TaladroAsignado taOrigen : taTempList) {
                    TaladroAsignado ta = new TaladroAsignado();
                    ta.setEscenarioId(escenarioActual);
                    ta.setOrden(taOrigen.getOrden());
                    ta.setFaseIn(taOrigen.getFaseIn());
                    ta.setFaseOut(taOrigen.getFaseOut());
                    ta.setFilaId(taOrigen.getFilaId());
                    // se copian pozoIn y pozoOut
                    Pozo pozoOrigenIn = pozoManager.find(taOrigen.getPozoInId().getId());
                    Pozo pozoDestino = pozoManager.find(pozoOrigenIn.getFilaId(), pozoOrigenIn.getUbicacion(), escenarioActual);
                    ta.setPozoInId(pozoDestino);
                    Pozo pozoOrigenOut = pozoManager.find(taOrigen.getPozoOutId().getId());
                    pozoDestino = pozoManager.find(pozoOrigenOut.getFilaId(), pozoOrigenOut.getUbicacion(), escenarioActual);
                    ta.setPozoOutId(pozoDestino);
                    // se copian las pozoSecuenciaIn y pozoSecuenciaOut
                    PozoSecuencia psecDestino = pozoSecManager.find(escenarioActual, ta.getPozoInId(), ta.getFaseIn());
                    ta.setPozoSecuenciaInId(psecDestino);
                    psecDestino = pozoSecManager.find(escenarioActual, ta.getPozoOutId(), ta.getFaseOut());
                    ta.setPozoSecuenciaOutId(psecDestino);
                    ta.setTaladroId(taladroMap.get(taOrigen.getTaladroId().getNombre()));
                    talAsigList.add(ta);
                }
                break;
        }
        
        return talAsigList;
    }

    private List<TaladroTraza> taladroTrazaList() {
        List<TaladroTraza> ttList = new ArrayList<>();
        switch (tipoEscenario) {
            case Constantes.ESCENARIO_BASE:
                List<TaladroAsignado> talAsgList = taladroAsignadoManager.findAll(escenarioActual);
                // Se crea la traza de movimiento / descontinuación de cada taladro
                for (TaladroAsignado ta : talAsgList) {
                    TaladroTraza tt = new TaladroTraza();
                    tt.setTaladroId(ta.getTaladroId().getId());
                    tt.setOrden(1);
                    tt.setTaladroAsignadoOrigenId(ta.getId());
                    tt.setPozoOutOrigenId(ta.getPozoOutId().getId());
                    tt.setFaseOutOrigen(ta.getFaseOut());
                    tt.setPozoSecuenciaOrigenId(ta.getPozoSecuenciaOutId().getId());
                    TaladroStatus ts = taladroStatusManager.find(ta.getTaladroId(), Constantes.TALADRO_STATUS_ACTIVO);
                    tt.setTaladroStatusInicialId(ts.getId());
                    tt.setEscenarioId(escenarioActual.getId());
                    ttList.add(tt);
                }
                break;
            case Constantes.ESCENARIO_PRUEBA:
            case Constantes.ESCENARIO_MEJOR_VISION:
                List<TaladroTraza> ttOrigenList = taladroTrazaManager.findAll(escenarioOrigen);
                for(TaladroTraza tt : ttOrigenList){
                    TaladroTraza ttActual = new TaladroTraza();
                    
                    Taladro talOrigen = taladroManager.find(tt.getTaladroId());
                    Taladro taladro = taladroManager.find(talOrigen.getNombre(), escenarioActual);
                    ttActual.setTaladroId(taladro.getId());
                    
                    ttActual.setOrden(tt.getOrden());
                    
                    Pozo pzOrigen = pozoManager.find(tt.getPozoOutOrigenId());
                    Pozo pozo = pozoManager.find(pzOrigen.getUbicacion(), escenarioActual);
                    ttActual.setPozoOutOrigenId(pozo.getId());
                    
                    ttActual.setFaseOutOrigen(tt.getFaseOutOrigen());
                    
                    PozoSecuencia secOrigen = pozoSecManager.find(escenarioActual, 
                            pozo, tt.getFaseOutOrigen());
                    ttActual.setPozoSecuenciaOrigenId(secOrigen.getId());
                    
                    if(tt.getTaladroAsignadoDestinoId() != null){
                        TaladroAsignado talAsigDestOrigen = taladroAsignadoManager
                                .find(tt.getTaladroAsignadoDestinoId());
                       
                        Pozo pozoInOrigen = talAsigDestOrigen.getPozoInId();
                        Pozo pozoOutOrigen = talAsigDestOrigen.getPozoOutId();
                        Taladro taladroOrigen = talAsigDestOrigen.getTaladroId();
                        
                        Pozo pozoIn = pozoManager.find(pozoInOrigen.getUbicacion(), escenarioActual);
                        Pozo pozoOut = pozoManager.find(pozoOutOrigen.getUbicacion(), escenarioActual);
                        Taladro taladroAsg = taladroManager.find(taladroOrigen.getNombre(), escenarioActual);
                        TaladroAsignado taOrigen = taladroAsignadoManager
                                .find(pozoIn, pozoOut, taladroAsg, escenarioActual);
                        ttActual.setTaladroAsignadoDestinoId(taOrigen.getId());
                    }
                    
                    if(tt.getTaladroStatusInicialId() != null){
                        TaladroStatus tsOrigen = taladroStatusManager
                                .find(taladro, Constantes.TALADRO_STATUS_OCUPADO);
                        ttActual.setTaladroStatusInicialId(tsOrigen.getId());
                    }
                    
                    ttActual.setEscenarioId(escenarioActual.getId());
                    
                    ttList.add(ttActual);
                }
        }
        return ttList;
    }

    private void crearEscenario() {
        int progress = 0;
        setProgress(progress);

        // se guarda el escenario
        escenarioActual = new Escenario();
        escenarioActual.setNombre(nombre);
        escenarioActual.setFecha(fecha);

        if (escenarioOrigen != null) {
            if (escenarioOrigen.getFechaCierre() != null) {
                escenarioActual.setFechaCierre(escenarioOrigen.getFechaCierre());
            }
        }

        escenarioActual.setComentario(comentario);
        escenarioActual.setStatus(Constantes.ESCENARIO_STATUS_ABIERTO);
        escenarioActual.setTipo(Constantes.ESCENARIO_PRUEBA);
        escenarioActual.setArchivo(archivoPremisas);
        escenarioManager.create(escenarioActual);

        escenarioActual = escenarioManager.find(nombre);

        // guarda lista de taladros para el escenario
        List<Taladro> taladroList = new ArrayList<>();
        Map<String, Taladro> taladrosMap = new HashMap<>();
        for (Taladro tal : taladrosList()) {
            Taladro taladro = new Taladro();
            taladro.setNombre(tal.getNombre());
            taladro.setFechaInicial(tal.getFechaInicial());
            taladro.setEscenarioId(escenarioActual);
            taladroList.add(taladro);

        }
        taladroManager.batchSave(taladroList);
        taladroList.clear();
        taladroList = taladroManager.findAll(escenarioActual);
        taladroList.stream().forEach(tal -> {
            taladrosMap.put(tal.getNombre(), tal);
        });
//        progress += taladroList.size();
//        setProgress(progress * 100 / registros);

//        List<TaladroStatus> tsTalList = tsList();
//        tsTalList.clear();
        

        // guarda list de objetos TaladroHasFase para el escenario
        List<TaladroHasFase> thfList = new ArrayList<>();
        for (TaladroHasFase thf : thFaseList()) {
            TaladroHasFase thFase = new TaladroHasFase();
            thFase.setDias(thf.getDias());
            thFase.setCostoBs(thf.getCostoBs());
            thFase.setCostoUsd(thf.getCostoUsd());
            thFase.setCostoEquiv(thf.getCostoEquiv());
            thFase.setFase(thf.getFase());
            Taladro taladro = taladrosMap.get(thf.getTaladroId().getNombre());
            thFase.setTaladroId(taladro);
            if (thf.getFecha() == null) {
                thFase.setFecha(taladro.getFechaInicial());
            } else {
                thFase.setFecha(thf.getFecha());
            }
            thFase.setEscenarioId(escenarioActual);
            thfList.add(thFase);
        }
        thFaseManager.batchSave(thfList);
//        progress += thfList.size();
//        setProgress(progress * 100 / registros);

        // guarda la lista de MacollaSecuencia para este Escenario
        List<MacollaSecuencia> msecList = new ArrayList<>();
        for (MacollaSecuencia ms : macollaSecList()) {
            MacollaSecuencia masec = new MacollaSecuencia();
            masec.setSecuencia(ms.getSecuencia());
            masec.setMacollaId(ms.getMacollaId());
            masec.setEscenarioId(escenarioActual);
            msecList.add(masec);
        }
        macollaSecManager.batchSave(msecList);
//        progress += msecList.size();
//        setProgress(progress * 100 / registros);

        // guarda las filas con taladros asignados
        List<FilaHasTaladro> fhtList = new ArrayList<>();
        for (FilaHasTaladro fht : fhTaladroList()) {
            FilaHasTaladro fhTal = new FilaHasTaladro();
            fhTal.setFilaId(fht.getFilaId());
            fhTal.setTaladroId(taladrosMap.get(fht.getTaladroId().getNombre()));
            fhTal.setEscenarioId(escenarioActual);
            fhtList.add(fhTal);
        }
        fhTaladroManager.batchSave(fhtList);
//        progress += fhtList.size();
//        setProgress(progress * 100 / registros);

        // guarda los pozos de este escenario
        List<Pozo> pozoList = new ArrayList<>();
        for (Pozo p : pozoList()) {
            Pozo pozo = new Pozo();
            pozo.setAys(p.getAys());
            pozo.setBloque(p.getBloque());
            pozo.setClasePozo(p.getClasePozo());
            pozo.setDeclinacion(p.getDeclinacion());
            pozo.setEscenarioId(escenarioActual);
            pozo.setExpHiperb(p.getExpHiperb());
            pozo.setFilaId(p.getFilaId());
            pozo.setGradoApiDiluente(p.getGradoApiDiluente());
            pozo.setGradoApiMezcla(p.getGradoApiMezcla());
            pozo.setGradoApiXp(p.getGradoApiXp());
            pozo.setIncremAnualAys(p.getIncremAnualAys());
            pozo.setIncremAnualRgp(p.getIncremAnualRgp());
            pozo.setInicioDecl(p.getInicioDecl());
            pozo.setInicioDeclAys(p.getInicioDeclAys());
            pozo.setInicioDeclRgp(p.getInicioDeclRgp());
            pozo.setMacollaId(p.getMacollaId());
            pozo.setNombre(p.getNombre());
            pozo.setNumero(p.getNumero());
            pozo.setPi(p.getPi());
            pozo.setPlan(p.getPlan());
            pozo.setReservaMax(p.getReservaMax());
            pozo.setRgp(p.getRgp());
            pozo.setTasaAbandono(p.getTasaAbandono());
            pozo.setUbicacion(p.getUbicacion());
            pozo.setYacimiento(p.getYacimiento());
            pozoList.add(pozo);
        }
        pozoManager.batchSave(pozoList);
        Map<String, Pozo> pozoMap = new HashMap<>();
        pozoManager.findAll(escenarioActual).stream()
                .forEach(pz -> pozoMap.put(pz.getUbicacion(), pz));
        
        // se crean los taladros -status
        for (Taladro tal : taladroList) {
            Taladro talTemp = null;
            switch(tipoEscenario){
                case Constantes.ESCENARIO_BASE:
                    talTemp = taladroManager.find(tal.getNombre());
                    break;
                case Constantes.ESCENARIO_PRUEBA:
                case Constantes.ESCENARIO_MEJOR_VISION:
                    talTemp = taladroManager.find(tal.getNombre(), escenarioOrigen);
                    break;
            }
            
            List<TaladroStatus> tsTalList = taladroStatusManager.find(talTemp);

            for (TaladroStatus tsTal : tsTalList) {
                TaladroStatus ts = new TaladroStatus();
                ts.setTaladroId(tal);
                ts.setFechaIn(tsTal.getFechaIn());
                ts.setFechaOut(tsTal.getFechaOut());
                ts.setNombre(tsTal.getNombre());
                ts.setStatus(tsTal.getStatus());
                ts.setFilaId(tsTal.getFilaId());
                BigInteger id = tsTal.getPozoId();
                if(id != null){
                    Pozo pozoOriginal = pozoManager.find(id.longValue());
                    Pozo pozoActual = pozoMap.get(pozoOriginal.getUbicacion());

                    BigInteger idBi = BigInteger.valueOf(pozoActual.getId());
                    ts.setPozoId(idBi);
                }
                ts.setFase(tsTal.getFase());
                taladroStatusManager.create(ts);
            }
//            progress += tsTalList.size();
//            setProgress(progress * 100 / registros);
        }

//        progress += fhtList.size();
//        setProgress(progress * 100 / registros);
        // guarda la secuencia de pozos para este escenario
        List<PozoSecuencia> psList = new ArrayList<>();
        for (PozoSecuencia ps : pozoSecList()) {
            PozoSecuencia psec = new PozoSecuencia();
            psec.setSecuencia(ps.getSecuencia());
            psec.setFase(ps.getFase());
            //Pozo pozo = pozoManager.find(ps.getFilaId(), ps.getPozoId().getUbicacion(), escenarioActual);
            psec.setPozoId(pozoMap.get(ps.getPozoId().getUbicacion()));
            psec.setFilaId(ps.getFilaId());
            psec.setEscenarioId(escenarioActual);
            psList.add(psec);
        }
        pozoSecManager.batchSave(psList);
//        progress += psList.size();
//        setProgress(progress * 100 / registros);

        // guarda los rampeos de este escenario
        List<Rampeo> rampList = new ArrayList<>();
        for (Rampeo r : rampeoList()) {
            Rampeo rampeo = new Rampeo();
            rampeo.setNumero(r.getNumero());
            rampeo.setDias(r.getDias());
            rampeo.setRpm(r.getRpm());
            Pozo pozo = pozoManager.find(r.getPozoId().getUbicacion(), escenarioActual);
            rampeo.setPozoId(pozo);
            rampeo.setEscenarioId(escenarioActual);
            rampList.add(rampeo);
        }
        rampeoManager.batchSave(rampList);
//        progress += rampList.size();
//        setProgress(progress * 100 / registros);

        // se copia la data para la tabla TaladroAsignado
        List<TaladroAsignado> tasgList = talAsigList();
        tasgList.stream().forEach(ta -> {
            taladroAsignadoManager.create(ta);
        });
        //taladroAsignadoManager.batchSave(tasgList);

        List<TaladroTraza> ttList = taladroTrazaList();
        taladroTrazaManager.batchSave(ttList);

        //guarda la data de perforación para este escenario
        List<Perforacion> pfList = new ArrayList<>();
        for (Perforacion p : perfList()) {
            Perforacion perf = new Perforacion();
            perf.setTaladroId(taladrosMap.get(p.getTaladroId().getNombre()));
            perf.setMacollaId(p.getMacollaId());
            perf.setFilaId(p.getFilaId());
            perf.setPozoId(pozoMap.get(p.getPozoId().getUbicacion()));
            perf.setFase(p.getFase());
            perf.setFechaIn(p.getFechaIn());
            perf.setDiasActivos(p.getDiasActivos());
            perf.setDiasInactivos(p.getDiasInactivos());
            perf.setDias(p.getDias());
            perf.setFechaOut(p.getFechaOut());
            perf.setBs(p.getBs());
            perf.setUsd(p.getUsd());
            perf.setEquiv(p.getEquiv());
            perf.setEscenarioId(escenarioActual);
            perf.setStatus(p.getStatus());
            pfList.add(perf);
        }
        perfManager.batchSave(pfList);
//        progress += pfList.size();
//        progressBar.setValue(progress * 100 / registros);
        finalizado = true;
    }

    private void eliminarEscenario() {
        perfManager.remove(escenarioActual);
        setProgress(10);
        taladroAsignadoManager.remove(escenarioActual);
        rampeoManager.remove(escenarioActual);
        perfManager.remove(escenarioActual);
        explotacionManager.removeAll();
        macollaExplotadaManager.removeAll();
        pozoExplotadoManager.removeAll();
        pozoSecManager.remove(escenarioActual);
        perfManager.remove(escenarioActual);
        pozoManager.remove(escenarioActual);
        fhTaladroManager.remove(escenarioActual);
        macollaSecManager.remove(escenarioActual);
        thFaseManager.remove(escenarioActual);
        setProgress(95);

        List<Taladro> taladros = taladroManager.findAll(escenarioActual);
        for (Taladro taladro : taladros) {
            List<TaladroStatus> tsList = taladroStatusManager.find(taladro);
            if (!tsList.isEmpty()) {
                for (TaladroStatus ts : tsList) {
                    taladroStatusManager.remove(ts);
                }
            }
        }

        for (Taladro taladro : taladros) {
            List<TaladroMant> mantList = mantenimientoManager.findAll(taladro);
            if (!mantList.isEmpty()) {
                for (TaladroMant mant : mantList) {
                    mantenimientoManager.remove(mant);
                }
            }
        }

        for (Taladro taladro : taladros) {
            List<TaladroSustituto> sustList = sustitutoManager.findAll(taladro);
            if (!sustList.isEmpty()) {
                for (TaladroSustituto sus : sustList) {
                    sustitutoManager.remove(sus);
                }
            }
        }

        taladroManager.remove(escenarioActual);
        escenarioManager.remove(escenarioActual);
        taladroTrazaManager.remove(escenarioActual);
        finalizado = true;
    }

}
