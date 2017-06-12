package com.sismon.vista.controller;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.FilaManager;
import com.sismon.jpamanager.MacollaSecuenciaManager;
import com.sismon.jpamanager.ParidadManager;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.jpamanager.PozoSecuenciaManager;
import com.sismon.jpamanager.RampeoManager;
import com.sismon.jpamanager.TaladroAsignadoManager;
import com.sismon.jpamanager.TaladroHasFaseManager;
import com.sismon.jpamanager.TaladroManager;
import com.sismon.jpamanager.TaladroMantManager;
import com.sismon.jpamanager.TaladroStatusManager;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.MacollaSecuencia;
import com.sismon.model.Paridad;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.model.PozoSecuencia;
import com.sismon.model.Rampeo;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroAsignado;
import com.sismon.model.TaladroHasFase;
import com.sismon.model.TaladroMant;
import com.sismon.model.TaladroStatus;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import org.apache.poi.sl.usermodel.FreeformShape;

public class PerforacionEscenarioController3 extends SwingWorker<Map<Integer, Object[]>, Void> {

    private final Map<Integer, Object[]> estrategiaPerforacionMap = new LinkedHashMap<>();
    private final Escenario escenario;
    private final Date fechaCierre;
    private Map<Fila, Integer> filasPerforadasMap;
    //private LinkedList<Taladro> taladrosDisponibles;

    private final MacollaSecuenciaManager macollaSecuenciaManager;
    private final FilaManager filaManager;
    private final TaladroAsignadoManager taladroAsignadoManager;
    private final TaladroManager taladroManager;
    private final PozoSecuenciaManager pozoSecuenciaManager;
    private final ParidadManager paridadManager;
    private final TaladroHasFaseManager thfManager;
    private final TaladroStatusManager taladroStatusManager;
    private final PerforacionManager perforacionManager;
    private final RampeoManager rampeoManager;
    private final TaladroMantManager mantenimientoManager;

    private int indicePerforacion = 1;
    private static final Integer FILA_NO_PERFORADA = 0;
    private static final Integer FILA_PERFORADA = 1;
    private static final DateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final SismonLog SISMON_LOG = SismonLog.getInstance();

    public PerforacionEscenarioController3(Escenario escenario, Date fechaCierre) {
        this.macollaSecuenciaManager = new MacollaSecuenciaManager();
        this.taladroAsignadoManager = new TaladroAsignadoManager();
        this.filaManager = new FilaManager();
        this.taladroManager = new TaladroManager();
        this.pozoSecuenciaManager = new PozoSecuenciaManager();
        this.paridadManager = new ParidadManager();
        this.thfManager = new TaladroHasFaseManager();
        this.taladroStatusManager = new TaladroStatusManager();
        this.perforacionManager = new PerforacionManager();
        this.rampeoManager = new RampeoManager();
        this.mantenimientoManager = new TaladroMantManager();

        this.fechaCierre = fechaCierre;
        this.escenario = escenario;
    }

    @Override
    protected Map<Integer, Object[]> doInBackground() throws Exception {
        // Se buscan los tiempos de perforación de cada pozo;
        Map<Pozo, Map<String, Double[]>> tiemposPerforacionMap = makePozoDataMap(escenario);
        // obtiene el orden en que se asignan los taladros
        List<Taladro> taladroList = getTaladroOrdered(escenario);
        List<Taladro> taladrosAsignadosList = getTaladrosAsignados(escenario);

        // Un mapa con el status de cada taladro
        Map<Taladro, TaladroStatus> taladroStatusMap = new HashMap<>();
        // se hace un mapa de las filas que han sido perforadas o nó
        filasPerforadasMap = makeFilasPerforadasMap();
        // Se inicializa el Arreglo con los datos que se van a devolver y la 
        // secuencia de perforación
        Object[] elementos = new Object[14];

        // Se busca los valores de paridad cambiaria en el tiempo
        List<Paridad> paridadList = paridadManager.findAll();

        // Se busca el conjunto de taladros descontinuados
        Set<Taladro> taladrosDescontinuados = getTaladroDescotinuados();
        // Se crea la lista de taladros disponibles para seguir perforando
        LinkedList<Taladro> taladrosDisponibles = new LinkedList<>();
        // Se declara el objeto fechaOut y taladroAsignado, para evitar el nullPointerException
        Date fechaOut = null;
        Taladro taladroAsignado = null;
        // Se busca la lista de mantenimiento de cada taladro y se deja en un mapa
        Map<Taladro, List<TaladroMant>> mantenimientosMap = new HashMap<>();
        for (Taladro taladro : taladroList) {
            List<TaladroMant> talMantList = mantenimientoManager.findAll(taladro);
            mantenimientosMap.put(taladro, talMantList);
        }
        double diasMant = 0.0;
        // Se van a perforar primero las filas con taladros asignados, segun la
        // fecha de asignación del taladro y de acuerdo a como el taladro aparece
        // asignado en la tabla TaladroAsignado

        for (Taladro taladro : taladrosAsignadosList) {
            // Se busca la ruta de perforación de este taladro
            List<TaladroAsignado> talAsigList = getRutaTaladroAsignado(escenario,
                    taladro);

            // Se realizan primero las perforaciones de taladros asignado
            Date fechaIn = taladro.getFechaInicial();
            boolean primerPasoTaladro = true;
            Macolla macollaEnPerforacion = null;
            for (TaladroAsignado ta : talAsigList) {
                taladroAsignado = ta.getTaladroId();

                // Se busca la secuencia de perforación de este taladro en cada fila
                // que está asignado y se verifica que hay mudanzo o no del taladro
                Fila fila = ta.getFilaId();

                List<PozoSecuencia> pozoSecuenciaList = getPozoSecuencia(fila, escenario);
                // Se hace la perforación de esta fila según su secuencia
                boolean primeraSecuenciaPerforada = false;
                int saltar = 0; // usado para identificar la primera secuencia de perforación
                // Se ejecuta la secuencia de perforación de la fila
                for (PozoSecuencia sec : pozoSecuenciaList) {

                    // se chequea que va a arrancaren la primera secuencia asignada
                    if (sec.getFilaId().equals(ta.getFilaId())
                            && sec.getPozoId().equals(ta.getPozoInId())
                            && sec.getFase().equals(ta.getFaseIn())) {
                        if (saltar == 0) {
                            saltar++;
                        }
                    } else {
                        if (saltar == 0) {
                            continue;
                        } else {
                            saltar++;
                        }
                    }

                    // Se verifica si hay mudanza de macolla
                    if (primerPasoTaladro) {
                        macollaEnPerforacion = fila.getMacollaId();
                        primerPasoTaladro = false;
                    } else if (!macollaEnPerforacion.equals(fila.getMacollaId())
                            && !primeraSecuenciaPerforada) {
                        double diasMudanza = getDiasMudanza(taladroAsignado, fechaIn);
                        fechaIn = agregarDiasAFecha(fechaIn, diasMudanza);
                    }
                    // Se verifica que está en la primera secuencia de la fila
                    if (sec.getFase().equals(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS)) {
                        fechaIn = agregarDiasAFecha(fechaIn, getDiasMudanza(taladroAsignado, fechaIn));
                        primeraSecuenciaPerforada = true;
                        continue;
                    }

                    if (ta.getPozoSecuenciaInId().equals(sec)) {
                        primeraSecuenciaPerforada = true;
                        fechaOut = ejecutaFase(sec, taladroAsignado, fechaIn, mantenimientosMap,
                                tiemposPerforacionMap, paridadList);
                        fechaIn = fechaOut;

                        // Aqui se debe verificar el status del taladro
                        verificaStatusTaladro(taladroAsignado, fechaOut,
                                taladrosDescontinuados, taladrosDisponibles,
                                taladroStatusMap, sec.getFilaId(), sec.getPozoId(),
                                sec.getFase());

                    } else if (primeraSecuenciaPerforada) {
                        if (ta.getPozoSecuenciaOutId().equals(sec)) {
                            fechaOut = ejecutaFase(sec, taladroAsignado, fechaIn, mantenimientosMap,
                                    tiemposPerforacionMap, paridadList);
                            if (Objects.equals(sec.getFase(), Constantes.FASE_PRODUCTOR)) {
                                makeCompletacion(sec, taladroAsignado, fechaOut, mantenimientosMap,
                                        tiemposPerforacionMap, paridadList);
                            }
                            fechaIn = fechaOut;

                            // Aqui se debe verificar el status del taladro
                            verificaStatusTaladro(taladroAsignado, fechaOut,
                                    taladrosDescontinuados, taladrosDisponibles,
                                    taladroStatusMap, sec.getFilaId(), sec.getPozoId(),
                                    sec.getFase());

                            break; //llegó al final de perforación
                        } else {
                            fechaOut = ejecutaFase(sec, taladroAsignado, fechaIn, mantenimientosMap,
                                    tiemposPerforacionMap, paridadList);
                            fechaIn = fechaOut;
                            if (Objects.equals(sec.getFase(), Constantes.FASE_PRODUCTOR)) {
                                makeCompletacion(sec, taladroAsignado, fechaOut, mantenimientosMap,
                                        tiemposPerforacionMap, paridadList);
                                fechaIn = fechaOut;
                                continue;
                            }
                        }
                        if (Objects.equals(sec.getFase(), Constantes.FASE_PRODUCTOR)) {
                            makeCompletacion(sec, taladroAsignado, fechaOut, mantenimientosMap,
                                    tiemposPerforacionMap, paridadList);
                            fechaIn = fechaOut;
                            continue;
                        }
                    }
                }
            }
        }

        // Restan por perforar
        Macolla macollaEnPerforacion = null;
        for (Map.Entry<Fila, Integer> filasMap : filasPerforadasMap.entrySet()) {
            if (Objects.equals(filasMap.getValue(), FILA_NO_PERFORADA)) {
                Fila fila = filasMap.getKey();

                Taladro taladro = taladrosDisponibles.pop();
                List<PozoSecuencia> secuencia = getPozoSecuencia(fila, escenario);
                TaladroStatus talStatus = taladroStatusMap.get(taladro);
                Date fechaIn = talStatus.getFechaIn();

                for (PozoSecuencia sec : secuencia) {
                    // Se verifica si hay mudanza de macolla
                    if (!Objects.equals(macollaEnPerforacion, fila.getMacollaId())) {
                        macollaEnPerforacion = fila.getMacollaId();
                        double diasMudanza = getDiasMudanza(taladro, fechaIn);
                        fechaIn = agregarDiasAFecha(fechaIn, diasMudanza);
                    }

                    fechaOut = ejecutaFase(sec, taladro, fechaIn, mantenimientosMap,
                            tiemposPerforacionMap, paridadList);
                    if (Objects.equals(sec.getFase(), Constantes.FASE_PRODUCTOR)) {
                        makeCompletacion(sec, taladro, fechaOut, mantenimientosMap,
                                tiemposPerforacionMap, paridadList);
                    }
                    fechaIn = fechaOut;

                    // verifica el status del taladro
                    verificaStatusTaladro(taladro, fechaOut, taladrosDescontinuados,
                            taladrosDisponibles, taladroStatusMap, sec.getFilaId(), sec.getPozoId(),
                            sec.getFase());
                }
            }
        }

        return estrategiaPerforacionMap;
    }

    @Override
    protected void done() {
        // sólo para retornar el resultado
        SISMON_LOG.logger.log(Level.INFO, "Perforado con éxito el escenario {0}",
                escenario.getNombre());
        Contexto.showMessage("Perforado con éxito el escenario "
                + escenario.getNombre(), Constantes.MENSAJE_INFO);
    }

    private Date ejecutaFase(PozoSecuencia sec, Taladro taladro, Date fechaIn,
            Map<Taladro, List<TaladroMant>> mantenimientos,
            Map<Pozo, Map<String, Double[]>> tiemposPerforacionMap,
            List<Paridad> paridadList) {

        List<TaladroHasFase> thfList = thfManager
                .findAllByFaseTaladroEscenario(sec.getFase(), taladro, escenario);
        TaladroHasFase thf = null;
        for (TaladroHasFase ent : thfList) {
            if (ent.getFecha().before(fechaIn) || ent.getFecha().equals(fechaIn)) {
                thf = ent;
            }
        }

        // Aqui se determinan los cambios en el valor de la paridad cambiaria
        Paridad paridad = recalculaParidad(paridadList, fechaIn);
        double dias;
        double diasActivos;
        double diasInactivos;
        double bs;
        double usd;
        double equiv;
        
        Map<String, Double[]> tiemposMap = tiemposPerforacionMap.get(sec.getPozoId());

        Double status = null;
        if (tiemposMap != null && tiemposMap.get(sec.getFase()) != null) {

            Optional<Double> statusOpt = Optional.ofNullable(tiemposMap.get(sec.getFase())[6]);
            
            if (statusOpt.isPresent()) {
                status = statusOpt.get();
            } else {
                SismonLog.getInstance().logger.log(Level.SEVERE,
                        "Fall\u00f3 en {0} en la fila {1} con el taladro {2} en la fase: {3}",
                        new Object[]{sec.getPozoId(), sec.getFilaId(),
                            taladro.getNombre(), sec.getFase()});
            }

            if (status != Constantes.PERF_MODIFICADA) {
                // aqui toma los valores que viene de modificar las fases del taladro
                dias = thf.getDias();
                diasActivos = dias;
                diasInactivos = 0.0;
                bs = thf.getCostoBs();
                usd = thf.getCostoUsd();
                equiv = (paridad.getValor() * bs) + usd;
            } else {
                // aqui toma los valores de la tabla de perforación
                dias = tiemposMap.get(sec.getFase())[0];
                diasActivos = tiemposMap.get(sec.getFase())[1];
                diasInactivos = tiemposMap.get(sec.getFase())[2];
                bs = tiemposMap.get(sec.getFase())[3];
                usd = tiemposMap.get(sec.getFase())[4];
                equiv = tiemposMap.get(sec.getFase())[5];
            }
        } else {
            dias = thf.getDias();
            diasActivos = dias;
            diasInactivos = 0.0;
            bs = thf.getCostoBs();
            usd = thf.getCostoUsd();
            equiv = (paridad.getValor() * bs) + usd;
            status = Constantes.PERF_ORIGINAL;
        }

        LocalDate ldIn = LocalDateTime.ofInstant(fechaIn.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
        LocalDate ldOut = ldIn.plusDays((long) dias);
        Date fechaOut = Date.from(ldOut.atStartOfDay(ZoneId.systemDefault()).toInstant());

        double diasMant = verificaMantenimiento(mantenimientos, taladro, fechaIn, fechaOut);
        if (diasMant != 0.0) {
            fechaOut = agregarDiasAFecha(fechaOut, diasMant);
        }

        Object[] elementos = new Object[15];
        elementos[0] = taladro;
        elementos[1] = sec.getFilaId().getMacollaId();
        elementos[2] = sec.getFilaId();
        elementos[3] = sec.getPozoId();
        elementos[4] = sec.getFase();
        elementos[5] = fechaIn;
        elementos[6] = fechaOut;
        elementos[7] = bs;
        elementos[8] = usd;
        elementos[9] = equiv;
        elementos[10] = escenario;
        elementos[11] = dias;
        elementos[12] = diasActivos;
        elementos[13] = diasInactivos;
        elementos[14] = status;

        estrategiaPerforacionMap.put(indicePerforacion++, elementos);
        filasPerforadasMap.put(sec.getFilaId(), FILA_PERFORADA);
        return fechaOut;
    }

    private void makeCompletacion(PozoSecuencia sec, Taladro taladro, Date fechaIn,
            Map<Taladro, List<TaladroMant>> mantenimientos,
            Map<Pozo, Map<String, Double[]>> tiemposPerforacionMap,
            List<Paridad> paridadList) {
        // ejecuta la completación
        sec.setFase(Constantes.FASE_COMPLETACION);
        Date fechaOut = ejecutaFase(sec, taladro, fechaIn, mantenimientos,
                tiemposPerforacionMap, paridadList);
        // ejecuta la conexión
        sec.setFase(Constantes.FASE_CONEXION);
        fechaOut = ejecutaFase(sec, taladro, fechaOut, mantenimientos,
                tiemposPerforacionMap, paridadList);
        // ejecuta la evaluación
        makeEvaluacion(sec, fechaOut, taladro, tiemposPerforacionMap);
    }

    private void makeEvaluacion(PozoSecuencia sec, Date fechaIn, Taladro taladro,
            Map<Pozo, Map<String, Double[]>> tiemposPerforacionMap) {

        List<Rampeo> rampeos = rampeoManager.findAll(sec.getPozoId(), escenario);
        double dias = 0.0;

        double diasActivos;
        double diasInactivos;
        Map<String, Double[]> tiemposMap = tiemposPerforacionMap.get(sec.getPozoId());
        if (tiemposMap != null && tiemposMap.get(Constantes.FASE_EVALUACION) != null) {
            dias = tiemposMap.get(Constantes.FASE_EVALUACION)[0];
            diasActivos = tiemposMap.get(Constantes.FASE_EVALUACION)[1];
            diasInactivos = tiemposMap.get(Constantes.FASE_EVALUACION)[2];
        } else {
            for (Rampeo rampa : rampeos) {
                dias += rampa.getDias();
            }
            diasActivos = dias;
            diasInactivos = 0;
        }

        Date fechaOut = agregarDiasAFecha(fechaIn, dias);

        Object[] elementos = new Object[15];
        elementos[0] = taladro;
        elementos[1] = sec.getFilaId().getMacollaId();
        elementos[2] = sec.getFilaId();
        elementos[3] = sec.getPozoId();
        elementos[4] = Constantes.FASE_EVALUACION;
        elementos[5] = fechaIn;
        elementos[6] = fechaOut;
        elementos[7] = 0.0;
        elementos[8] = 0.0;
        elementos[9] = 0.0;
        elementos[10] = escenario;
        elementos[11] = dias;
        elementos[12] = diasActivos;
        elementos[13] = diasInactivos;
        elementos[14] = Constantes.PERF_ORIGINAL;

        estrategiaPerforacionMap.put(indicePerforacion++, elementos);
    }

    private double getDiasMudanza(Taladro taladro, Date fechaIn) {
        //TaladroHasFase thf = thfManager.find(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS, taladro, escenario);
        List<TaladroHasFase> thfList = thfManager
                .findAllByFaseTaladroEscenario(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS, taladro, escenario);
        TaladroHasFase thf = null;
        for (TaladroHasFase ent : thfList) {
            if (ent.getFecha().before(fechaIn) || ent.getFecha().equals(fechaIn)) {
                thf = ent;
            }
        }

        return thf.getDias();
    }

    private double verificaMantenimiento(Map<Taladro, List<TaladroMant>> mantenimientoMap,
            Taladro taladro, Date fechaIn, Date fechaOut) {

        double dias = 0.0;
        List<TaladroMant> mantenimientos = mantenimientoMap.get(taladro);
        if (!mantenimientos.isEmpty()) {
            for (TaladroMant mant : mantenimientos) {
                if ((fechaIn.equals(mant.getFecha()) || fechaIn.before(mant.getFecha()))
                        && mant.getFecha().before(fechaOut)) {
                    dias = mant.getDias();
                    break;
                }
            }
        }
        return dias;
    }

    private Map<Fila, Integer> makeFilasPerforadasMap() {
        // Obtengo la lista de filas ordenadas por macolla
        List<MacollaSecuencia> macollasSecuencia = macollaSecuenciaManager
                .findAllOrdered(escenario);

        List<Fila> filasOrdered = new ArrayList<>();
        macollasSecuencia.stream()
                .map((ms) -> ms.getMacollaId())
                .map((macolla) -> filaManager.findAll(macolla))
                .forEach((filas) -> {
                    filas.stream()
                            .forEach(fl -> filasOrdered.add(fl));
                });
        filasPerforadasMap = new LinkedHashMap<>();
        filasOrdered.stream().forEach(fi -> {
            filasPerforadasMap.put(fi, FILA_NO_PERFORADA);
        });

        return filasPerforadasMap;
    }

    private Set<Taladro> getTaladroDescotinuados() {
        List<TaladroStatus> talStatusList = taladroStatusManager.findAll();
        Set<Taladro> lista = new HashSet<>();
        talStatusList.stream()
                .filter(ts -> Objects.equals(ts.getNombre(),
                Constantes.TALADRO_STATUS_DESCONTINUADO))
                .forEach(ts -> {
                    lista.add(ts.getTaladroId());
                });
        return lista;
    }

    private boolean verificaStatusTaladro(Taladro taladro, Date fechaOut,
            Set<Taladro> taladrosDescontinuados,
            LinkedList<Taladro> taladrosDisponibles,
            Map<Taladro, TaladroStatus> taladroStatusMap,
            Fila fila, Pozo pozo, String fase) {

        boolean descontinuado = false;

        // verifica el status del taladro si continua o queda descontinuado
        if (!taladrosDescontinuados.contains(taladro)) {
            // Se coloca el Taladro como disponible
            taladrosDisponibles.add(taladro);

            // Se pone su nuevo status 
            TaladroStatus ts = new TaladroStatus();
            ts.setNombre(Constantes.TALADRO_STATUS_OCUPADO);
            ts.setFechaIn(fechaOut); // <- aqui va la fecha de disponibilidad
            ts.setStatus(Constantes.TALADRO_STATUS_ACTIVO);
            ts.setTaladroId(taladro);
            taladroStatusMap.put(taladro, ts);

            // reordena la disponibilidad de taladro
            if (taladrosDisponibles.size() > 1) {
                Collections.sort(taladrosDisponibles, (Taladro t1, Taladro t2) -> {
                    int result;
                    TaladroStatus ts1 = taladroStatusMap.get(t1);
                    TaladroStatus ts2 = taladroStatusMap.get(t2);
                    result = ts1.getFechaIn().compareTo(ts2.getFechaIn());
                    return result;
                });
            }
        } else {
            // se procede a la descontinuación del taladro
            descontinuado = true;

            TaladroStatus ts = getTaladroStatus(taladro, Constantes.TALADRO_STATUS_ACTIVO);
            int filaId = ts.getFilaId();
            BigInteger pozoId = ts.getPozoId();
            String faseDesc = ts.getFase();

            updateDescontinuacionTaladro(taladro, fechaOut, ts, pozo);
            
            if (fila.getId() == filaId) {
                long pozoIdLong = pozoId.longValue();
                if (pozo.getId() == pozoIdLong) {
                    if (faseDesc.equals(fase)) {
                        taladrosDisponibles.remove(taladro);
                        if (taladrosDisponibles.size() > 1) {
                            Collections.sort(taladrosDisponibles, (Taladro t1, Taladro t2) -> {
                                int result;
                                TaladroStatus ts1 = taladroStatusMap.get(t1);
                                TaladroStatus ts2 = taladroStatusMap.get(t2);
                                result = ts1.getFechaIn().compareTo(ts2.getFechaIn());
                                return result;
                            });
                        }
                    }
                }
            }
        }

        return descontinuado;
    }

    private void updateDescontinuacionTaladro(Taladro taladro, Date fechaOut,
            TaladroStatus ts, Pozo pozo) {

        if(ts.getPozoId().longValue() == pozo.getId()){
            List<TaladroStatus> tsList = taladroStatusManager.find(taladro);
            for(TaladroStatus tsEnDb : tsList){
                if(tsEnDb.getFechaOut() != null 
                        && tsEnDb.getNombre().equals(Constantes.TALADRO_STATUS_OCUPADO)){
                    tsEnDb.setFechaOut(fechaOut);
                    taladroStatusManager.edit(tsEnDb);
                }
                if(tsEnDb.getFechaOut() == null
                        && tsEnDb.getNombre().equals(Constantes.TALADRO_STATUS_DESCONTINUADO)){
                    tsEnDb.setFechaIn(fechaOut);
                    taladroStatusManager.edit(tsEnDb);
                }
            }
        }
    }

    private Date agregarDiasAFecha(Date fecha, double dias) {
        LocalDate fechaLd = LocalDateTime
                .ofInstant(fecha.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
        fechaLd = fechaLd.plusDays((long) dias);
        return Date.from(fechaLd.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Map<Pozo, Map<String, Double[]>> makePozoDataMap(Escenario escenario) {
        List<Perforacion> perforaciones = perforacionManager.findAllOrderedByFecha(escenario);

        Map<Pozo, Map<String, Double[]>> pozoMap = new HashMap<>();
        // este mapa contiene el nombre de la fase y un arreglo con los dias
        // totales de perforacion, los dias activos y los dias inactivos
        Map<String, Double[]> fasesMap = null;

        for (Perforacion perf : perforaciones) {
            Pozo pozo = perf.getPozoId();
            if (pozoMap.containsKey(perf.getPozoId())) {
                fasesMap = pozoMap.get(pozo);
            } else {
                fasesMap = new HashMap<>();
            }
            Double[] data = {perf.getDias(), perf.getDiasActivos(), perf.getDiasInactivos(),
                perf.getBs(), perf.getUsd(), perf.getEquiv(), perf.getStatus()};
            fasesMap.put(perf.getFase(), data);
            pozoMap.put(pozo, fasesMap);
        }

        return pozoMap;
    }

    private List<Taladro> getTaladrosAsignados(Escenario escenario) {
        List<Taladro> tasigList = new ArrayList<>();
        List<TaladroAsignado> taList = taladroAsignadoManager.findAll(escenario);
        for (TaladroAsignado ta : taList) {
            if (!tasigList.contains(ta.getTaladroId())) {
                tasigList.add(ta.getTaladroId());
            }
        }
        Collections.sort(tasigList, (Taladro t1, Taladro t2) -> {
            return t1.getFechaInicial().compareTo(t2.getFechaInicial());
        });
        return tasigList;
    }

    private List<TaladroAsignado> getRutaTaladroAsignado(Escenario escenario,
            Taladro taladro) {
        List<TaladroAsignado> taList = taladroAsignadoManager
                .findRutaTaladro(escenario, taladro);

        return taList;
    }

    private List<Taladro> getTaladroOrdered(Escenario escenario) {
        return taladroManager.findAll(escenario);
    }

    private List<PozoSecuencia> getPozoSecuencia(Fila fila, Escenario escenario) {
        return pozoSecuenciaManager.findBySecuencia(fila, escenario);
    }

    private Paridad recalculaParidad(List<Paridad> paridadList, Date fechaIn) {
        Paridad paridad = null;
        if (paridadList.size() > 1) {
            for (int i = 0; i < paridadList.size(); i++) {
                if (i == (paridadList.size() - 1)) {
                    paridad = paridadList.get(i);
                } else if ((paridadList.get(i).getFechaIn().before(fechaIn)
                        || paridadList.get(i).getFechaIn().equals(fechaIn))
                        && paridadList.get(i + 1).getFechaIn().after(fechaIn)) {
                    paridad = paridadList.get(i);
                    break;
                }
            }
        } else {
            paridad = paridadList.get(0);
        }
        return paridad;
    }

    private List<TaladroStatus> getTaladroStatus(Escenario escenario) {
        return taladroStatusManager.findAll(escenario);
    }

    private TaladroStatus getTaladroStatus(Taladro taladro, int status) {
        return taladroStatusManager.find(taladro, status);
    }
}
