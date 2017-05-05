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
import java.util.Set;
import java.util.logging.Level;
import javax.swing.SwingWorker;

public class PerforacionEscenarioController4 extends SwingWorker<Map<Integer, Object[]>, Void> {

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

    public PerforacionEscenarioController4(Escenario escenario, Date fechaCierre) {
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
                // Se ejecuta la secuencia de perforación de la fila
                for (PozoSecuencia sec : pozoSecuenciaList) {
                    
                    // Se verifica si hay mudanza de macolla
                    if (primerPasoTaladro) {
                        macollaEnPerforacion = fila.getMacollaId();
                        primerPasoTaladro = false;
                    } else if (!macollaEnPerforacion.equals(fila.getMacollaId())
                            && !primeraSecuenciaPerforada) {
                        double diasMudanza = getDiasMudanza(taladroAsignado);
                        fechaIn = agregarDiasAFecha(fechaIn, diasMudanza);
                    }
                    // Se verifica que está en la primera secuencia de la fila
                    if (sec.getFase().equals(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS)) {
                        fechaIn = agregarDiasAFecha(fechaIn, getDiasMudanza(taladroAsignado));
                        primeraSecuenciaPerforada = true;
                        continue;
                    }
                    if (ta.getPozoSecuenciaInId().equals(sec)) {
                        primeraSecuenciaPerforada = true;
                        fechaOut = ejecutaFase(sec, taladroAsignado, fechaIn, mantenimientosMap,
                                tiemposPerforacionMap, paridadList);

                    } else if (primeraSecuenciaPerforada) {
                        if (ta.getPozoSecuenciaOutId().equals(sec)) {
                            fechaOut = ejecutaFase(sec, taladroAsignado, fechaIn, mantenimientosMap,
                                    tiemposPerforacionMap, paridadList);
                            if (Objects.equals(sec.getFase(), Constantes.FASE_PRODUCTOR)) {
                                madeCompletacion(sec, taladroAsignado, fechaOut, mantenimientosMap,
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
                            if (Objects.equals(sec.getFase(), Constantes.FASE_PRODUCTOR)) {
                                madeCompletacion(sec, taladroAsignado, fechaOut, mantenimientosMap,
                                        tiemposPerforacionMap, paridadList);
                                fechaIn = fechaOut;
                                continue;
                            }
                        }
                        if (Objects.equals(sec.getFase(), Constantes.FASE_PRODUCTOR)) {
                            madeCompletacion(sec, taladroAsignado, fechaOut, mantenimientosMap,
                                    tiemposPerforacionMap, paridadList);
                            fechaIn = fechaOut;
                            continue;
                        }
                    }
                    fechaIn = fechaOut;
                    
                    // verifica el status del taladro si continua o queda descontinuado
                }
                //verificaStatusTaladro(taladroAsignado, fechaOut, taladrosDescontinuados,
                //        taladrosDisponibles, taladroStatusMap);
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
                        double diasMudanza = getDiasMudanza(taladro);
                        fechaIn = agregarDiasAFecha(fechaIn, diasMudanza);
                    }

                    fechaOut = ejecutaFase(sec, taladro, fechaIn, mantenimientosMap,
                            tiemposPerforacionMap, paridadList);
                    if (Objects.equals(sec.getFase(), Constantes.FASE_PRODUCTOR)) {
                        madeCompletacion(sec, taladro, fechaOut, mantenimientosMap,
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
        TaladroHasFase thf = thfManager.find(sec.getFase(), taladro, escenario);

        // Aqui se determinan los cambios en el valor de la paridad cambiaria
        Paridad paridad = recalculaParidad(paridadList, fechaIn);
        double dias;
        double diasActivos;
        double diasInactivos;
        double bs;
        double usd;
        double equiv;
        if (fechaIn.before(fechaCierre) || fechaIn.equals(fechaCierre)) {
            Map<String, Double[]> tiemposMap = tiemposPerforacionMap.get(sec.getPozoId());
            dias = tiemposMap.get(sec.getFase())[0];
            diasActivos = tiemposMap.get(sec.getFase())[1];
            diasInactivos = tiemposMap.get(sec.getFase())[2];
            bs = tiemposMap.get(sec.getFase())[3];
            usd = tiemposMap.get(sec.getFase())[4];
            equiv = tiemposMap.get(sec.getFase())[5];
        } else {
            dias = thf.getDias();
            diasActivos = dias;
            diasInactivos = 0.0;
            bs = thf.getCostoBs();
            usd = thf.getCostoUsd();
            equiv = (paridad.getValor() * bs) + usd;
        }

        LocalDate ldIn = LocalDateTime.ofInstant(fechaIn.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
        LocalDate ldOut = ldIn.plusDays((long) dias);
        Date fechaOut = Date.from(ldOut.atStartOfDay(ZoneId.systemDefault()).toInstant());

        double diasMant = verificaMantenimiento(mantenimientos, taladro, fechaIn, fechaOut);
        if (diasMant != 0.0) {
            fechaOut = agregarDiasAFecha(fechaOut, diasMant);
        }

        Object[] elementos = new Object[14];
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

        System.out.print(indicePerforacion + "\t" );
        System.out.print(taladro.getNombre() + "\t");
        System.out.print(sec.getFilaId().getMacollaId().getNombre() + "\t");
        System.out.print(sec.getPozoId().getUbicacion() + "\t");
        System.out.print(sec.getFase() + "\t");
        System.out.print(ldIn.format(DateTimeFormatter.ISO_DATE) + "\t");
        System.out.println(ldOut.format(DateTimeFormatter.ISO_DATE) + "\t");
        
        estrategiaPerforacionMap.put(indicePerforacion++, elementos);
        filasPerforadasMap.put(sec.getFilaId(), FILA_PERFORADA);
        return fechaOut;
    }

    private void madeCompletacion(PozoSecuencia sec, Taladro taladro, Date fechaIn,
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
        madeEvaluacion(sec, fechaOut, taladro, tiemposPerforacionMap);
    }

    private void madeEvaluacion(PozoSecuencia sec, Date fechaIn, Taladro taladro,
            Map<Pozo, Map<String, Double[]>> tiemposPerforacionMap) {
        List<Rampeo> rampeos = rampeoManager.findAll(sec.getPozoId(), escenario);
        LocalDate ldIn = LocalDateTime
                .ofInstant(fechaIn.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
        double dias = 0.0;

        double diasActivos;
        double diasInactivos;
        if (fechaIn.before(fechaCierre) || fechaIn.equals(fechaCierre)) {
            Map<String, Double[]> tiemposMap = tiemposPerforacionMap.get(sec.getPozoId());
            dias = tiemposMap.get(sec.getFase())[0];
            diasActivos = tiemposMap.get(sec.getFase())[1];
            diasInactivos = tiemposMap.get(sec.getFase())[2];
        } else {
            for (Rampeo rampa : rampeos) {
                dias += rampa.getDias();
            }
            diasActivos = dias;
            diasInactivos = 0.0;
        }
        Date fechaOut = agregarDiasAFecha(fechaIn, dias);

        Object[] elementos = new Object[14];
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

        estrategiaPerforacionMap.put(indicePerforacion++, elementos);
    }

    private boolean checkTaladroDescontinuado(Taladro taladro, Fila fila,
            Pozo pozo, String fase) {
        boolean descontinuado = false;
        Set<Taladro> taladrosDescontinuados = getTaladroDescotinuados();
        if (taladrosDescontinuados.contains(taladro)) {
            TaladroStatus ts = taladroStatusManager
                    .find(taladro, Constantes.TALADRO_STATUS_DESCONTINUADO);
            int filaId = ts.getFilaId();
            if (filaId == fila.getId()) {
                BigInteger pozoBigId = ts.getPozoId();
                long pozoId = pozoBigId.longValue();
                if (pozoId == pozo.getId()) {
                    System.out.println("Llego al pozo " + pozoId + " para descontinuar");
                    String faseDes = ts.getFase();
                    if (faseDes.equals(fase)) {
                        System.out.println("El taladro " + taladro
                                + "está descontinuado en esta fase");
                        descontinuado = true;
                    }
                }
            }
        }
        return descontinuado;
    }

    private boolean checkTaladroMudado(Taladro taladro, PozoSecuencia sec) {
        boolean mudado = false;
        List<TaladroAsignado> taList = taladroAsignadoManager.findRutaTaladro(escenario, taladro);
        if (!taList.isEmpty()) {
            for (TaladroAsignado ta : taList) {
                PozoSecuencia pz = ta.getPozoSecuenciaOutId();
                if (pz.equals(sec)) {
                    mudado = true;
                    break;
                }
            }
        }
        return mudado;
    }

    private double getDiasMudanza(Taladro taladro) {
        TaladroHasFase thf = thfManager.find(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS, 
                taladro, escenario);
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

    private void verificaStatusTaladro(Taladro taladro, Date fechaOut,
            Set<Taladro> taladrosDescontinuados,
            LinkedList<Taladro> taladrosDisponibles,
            Map<Taladro, TaladroStatus> taladroStatusMap,
            Fila fila, Pozo pozo, String fase) {
        // verifica el status del taladro si continua o queda descontinuado

        System.out.println("Entró a ver si el taladro " + taladro.getNombre()
                + " continua o esta descontinuado");

        if (!taladrosDescontinuados.contains(taladro)) {

            System.out.println(taladro.getNombre() + " no está en la lista "
                    + "de descontinuados, continua ");

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
            TaladroStatus ts = getTaladroStatus(taladro, Constantes.TALADRO_STATUS_ACTIVO);
            int filaId = ts.getFilaId();
            BigInteger pozoId = ts.getPozoId();
            String faseDesc = ts.getFase();
            
            if(fila.getId() == filaId){
                long pozoIdLong = pozoId.longValue();
                if(pozo.getId() == pozoIdLong){
                    if(faseDesc.equals(fase)){
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
                        System.out.println(taladro.getNombre() + " removido");
                    }
                }
            } else {
                System.out.println(taladro.getNombre() + " continua");
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
        // totales de perforacio, los dias activos y los dias inactivos
        Map<String, Double[]> fasesMap = null;

        for (Perforacion perf : perforaciones) {
            Pozo pozo = perf.getPozoId();
            if (pozoMap.containsKey(perf.getPozoId())) {
                fasesMap = pozoMap.get(pozo);
            } else {
                fasesMap = new HashMap<>();
            }
            Double[] data = {perf.getDias(), perf.getDiasActivos(), perf.getDiasInactivos(),
                perf.getBs(), perf.getUsd(), perf.getEquiv()};
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
    
    private List<TaladroStatus> getTaladroStatus(Escenario escenario){
        return taladroStatusManager.findAll(escenario);
    }
    
    private TaladroStatus getTaladroStatus(Taladro taladro, int status){
        return taladroStatusManager.find(taladro, status);
    }
}
