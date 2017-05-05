package com.sismon.vista.controller;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.FilaHasTaladroManager;
import com.sismon.jpamanager.FilaManager;
import com.sismon.jpamanager.MacollaSecuenciaManager;
import com.sismon.jpamanager.ParidadManager;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.jpamanager.PozoSecuenciaManager;
import com.sismon.jpamanager.RampeoManager;
import com.sismon.jpamanager.TaladroHasFaseManager;
import com.sismon.jpamanager.TaladroManager;
import com.sismon.jpamanager.TaladroMantManager;
import com.sismon.jpamanager.TaladroStatusManager;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.FilaHasTaladro;
import com.sismon.model.Macolla;
import com.sismon.model.MacollaSecuencia;
import com.sismon.model.Paridad;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.model.PozoSecuencia;
import com.sismon.model.Rampeo;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroHasFase;
import com.sismon.model.TaladroMant;
import com.sismon.model.TaladroStatus;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class PerforacionEscenarioController extends SwingWorker<Map<Integer, Object[]>, Void> {

    private final TaladroManager taladroManager;
    private final FilaHasTaladroManager fhtManager;
    private final MacollaSecuenciaManager macollaSecManager;
    private final FilaManager filaManager;
    private final PozoSecuenciaManager pozoSecManager;
    private final TaladroHasFaseManager thfManager;
    private final TaladroStatusManager talStatusManager;
    private final RampeoManager rampeoManager;
    private final TaladroMantManager mantenimientoManager;
    private final ParidadManager paridadManager;
    private final PerforacionManager perforacionManager;

    private final Map<Integer, Object[]> estrategiaPerforacionMap;
    private final Map<Taladro, TaladroStatus> statusTaladroMap;
    private final Map<Taladro, Date> fechaMudadoMap;
    private final LinkedList<Taladro> taladrosUsadosList = new LinkedList<>();
    private final LinkedList<Fila> filaIncompletaList = new LinkedList<>();
    private final Map<Fila, Object[]> taladroUsadosMap = new HashMap<>();
    // Mapa con las filas que no completaron la perforación y en que Pozo 
    // y Fase quedó
    private final Map<Fila, Object[]> filaIncompletaMap = new HashMap();
    private final Escenario escenarioSelected;
    private final JProgressBar progressBar;

    private int indicePerforacion = 1;
    private boolean filaIncompleta = false;

    private static final SismonLog sismonlog = SismonLog.getInstance();

    public PerforacionEscenarioController(Map<Integer, Object[]> estrategiaPerforacionMap,
            Escenario escenarioSelected,
            JProgressBar progressBar) {
        this.taladroManager = new TaladroManager();
        this.fhtManager = new FilaHasTaladroManager();
        this.macollaSecManager = new MacollaSecuenciaManager();
        this.filaManager = new FilaManager();
        this.pozoSecManager = new PozoSecuenciaManager();
        this.thfManager = new TaladroHasFaseManager();
        this.talStatusManager = new TaladroStatusManager();
        this.rampeoManager = new RampeoManager();
        this.mantenimientoManager = new TaladroMantManager();
        this.paridadManager = new ParidadManager();
        this.perforacionManager = new PerforacionManager();

        this.escenarioSelected = escenarioSelected;
        this.estrategiaPerforacionMap = estrategiaPerforacionMap;
        this.statusTaladroMap = new HashMap<>();
        this.fechaMudadoMap = new HashMap<>();
        this.progressBar = progressBar;
    }

    @Override
    protected Map<Integer, Object[]> doInBackground() throws Exception {
        progressBar.setVisible(true);
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);
        List<Taladro> taladroList = taladroManager.findAllByDate();
        Taladro taladroSelected = taladroList.get(0);

        // Se crea un mapa con las filas que tienen taladros asignados
        List<FilaHasTaladro> fhtList = fhtManager.findAll(escenarioSelected);
        Map<Fila, FilaHasTaladro> filaWithTaladroMap = new HashMap<>();
        fhtList.stream().forEach((fht) -> {
            filaWithTaladroMap.put(fht.getFilaId(), fht);
        });

        // Se busca la secuencia de perforación de las macollas restantes
        List<MacollaSecuencia> secuenciaList = macollaSecManager.findAllOrdered(escenarioSelected);
        FilaHasTaladro fhtSelected;
        Macolla macollaSelected;
        boolean mudado = false;

        // Obtengo la lista de filas ordenadas por macolla
        List<MacollaSecuencia> macollasSecuencia = macollaSecManager.findAllOrdered(escenarioSelected);

        List<Fila> filasOrdered = new ArrayList<>();
        macollasSecuencia.stream()
                .map((ms) -> ms.getMacollaId())
                .map((macolla) -> filaManager.findAll(macolla))
                .forEach((filas) -> {
                    filas.stream()
                    .forEach(fl -> filasOrdered.add(fl));
                });

        // se recorre cada fila a perforar
        for (Fila fila : filasOrdered) {
            // se verifica si la fila tiene taladro asignado
            if (filaWithTaladroMap.containsKey(fila)) {
                // con taladro asignado
                fhtSelected = filaWithTaladroMap.get(fila);
                taladroSelected = fhtSelected.getTaladroId();
                if (fechaMudadoMap.containsKey(taladroSelected)) {
                    fhtSelected.setFechaAsignacion(fechaMudadoMap.get(taladroSelected));
                    mudado = true;
                }
            } else {
                // no tiene taladro asignado, lo busca de los liberados
                taladroSelected = taladrosUsadosList.pop();
                fhtSelected = new FilaHasTaladro();
                fhtSelected.setTaladroId(taladroSelected);
                fhtSelected.setEscenarioId(escenarioSelected);
                fhtSelected.setFilaId(fila);
                fhtSelected.setSecuencia(0);
                mudado = true;
            }
            procesarFila(fhtSelected, mudado);
            if (filaIncompleta) {
                Fila filaACompletar = filaIncompletaList.pop();
                if (!fila.equals(filaACompletar)) {
                    fhtSelected = new FilaHasTaladro();
                    fhtSelected.setTaladroId(taladroSelected);
                    fhtSelected.setFilaId(filaACompletar);
                    PozoSecuencia sec = (PozoSecuencia) filaIncompletaMap.get(filaACompletar)[2];
                    if (fila.getMacollaId().equals(filaACompletar.getMacollaId())) {
                        mudado = false;
                    } else {
                        mudado = true;
                    }
                    procesarFila(fhtSelected, mudado, sec);
                } else {
                    filaIncompletaList.push(filaACompletar);
                }
            }

        }

        return estrategiaPerforacionMap;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (ExecutionException | InterruptedException e) {
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        }
        progressBar.setVisible(false);
    }

    private void procesarFila(FilaHasTaladro fhtSelected, boolean mudado) {
        Taladro taladroSelected = fhtSelected.getTaladroId();
        Fila filaSelected = fhtSelected.getFilaId();
        Macolla macollaSelected = filaSelected.getMacollaId();
        boolean descontinuado = false;
        boolean taladroMudado = false;

        if (fhtSelected.getFechaAsignacion() != null) {
            mudado = true;
        }

        Object[] elementos = new Object[11]; // arreglo de elemento a mostrar como resultado
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        List<PozoSecuencia> secuencia = pozoSecManager.findAllOrdered(filaSelected, escenarioSelected);
        if (secuencia == null || secuencia.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(Contexto.getActiveFrame(),
                        "No hay secuencia de perforación registrada para la macolla "
                        + macollaSelected.getNombre()
                        + " (" + macollaSelected.getNumero() + ")"
                        + ", fila " + filaSelected.getNombre(),
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            });
            return;
        }

        if (!statusTaladroMap.containsKey(taladroSelected)) {
            TaladroStatus taladroStatus = talStatusManager.find(taladroSelected,
                    Constantes.TALADRO_STATUS_ACTIVO, Constantes.TALADRO_STATUS_OCUPADO);
            statusTaladroMap.put(taladroSelected, taladroStatus);
        }

        Date fechaIn;
        TaladroStatus taladroStatus;
        if (fechaMudadoMap.containsKey(taladroSelected)) {
            fechaIn = fechaMudadoMap.get(taladroSelected);
            fechaMudadoMap.clear();
        } else {
            taladroStatus = statusTaladroMap.get(taladroSelected);
            fechaIn = taladroStatus.getFechaIn();
        }
        Date fechaOut = null;
        Date fechaFinProductor = null;
        Date fechaDescontinuacion = null;
        Date fechaMudado = null;
        boolean mudanzaEncontrada = false;

        for (PozoSecuencia sec : secuencia) {
            if (sec.getFase().equals(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS)) {
                mudanzaEncontrada = true;
            }
        }

        if (mudado && !mudanzaEncontrada) {
            List<TaladroHasFase> thfList = thfManager.findAll(taladroSelected, escenarioSelected);
            TaladroHasFase thf = null;
            for (TaladroHasFase item : thfList) {
                if (item.getFase().equals(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS)) {
                    thf = item;
                    break;
                }
            }

            long extratime;
            if (thf != null) {
                extratime = (long) (thf.getDias() * 24 * 3600 * 1000);
            } else {
                extratime = (long) (30 * 24 * 3600 * 1000);
            }
            fechaIn = new Date(fechaIn.getTime() + extratime);
        }

        List<TaladroMant> mantenimientos = mantenimientoManager.findAll(taladroSelected);
        List<Paridad> paridadList = paridadManager.findAll();

        // Encuentra que este taladro va a ser descontinuado o no
        TaladroStatus statusDescontinuado = talStatusManager.find(taladroSelected,
                Constantes.TALADRO_STATUS_ACTIVO,
                Constantes.TALADRO_STATUS_DESCONTINUADO);

        if (statusDescontinuado != null) {
            fechaDescontinuacion = statusDescontinuado.getFechaIn();
        }

        // busca para ver si el taladro está marcado para ser mudado
        List<TaladroStatus> statusMudadoList = talStatusManager
                .findAll(taladroSelected, Constantes.TALADRO_STATUS_MUDADO);

        // comienza la perforación
        for (PozoSecuencia sec : secuencia) {
            elementos[0] = taladroSelected; // taladro
            elementos[1] = macollaSelected; //macolla
            elementos[2] = filaSelected; // fila
            Pozo pozo = sec.getPozoId(); // pozo
            elementos[3] = pozo;
            String fase = sec.getFase(); // fase

            // Lista con los diferentes costos y dias a lo largo de la perforación
            List<TaladroHasFase> thFaseList = thfManager.findAll(taladroSelected, escenarioSelected, fase);
            TaladroHasFase thFaseSelected = null;
            Paridad paridad = null;

            // Aqui se determina los cambios de tiempo y costos del taladro
//            if (thFaseList.size() > 1) {
//                for (int i = 0; i < thFaseList.size(); i++) {
//                    if (i == (thFaseList.size() - 1)) {
//                        thFaseSelected = thFaseList.get(i);
//                    } else {
//                        if ((thFaseList.get(i).getFecha().before(fechaIn)
//                                || thFaseList.get(i).getFecha().equals(fechaIn))
//                                && thFaseList.get(i + 1).getFecha().after(fechaIn)) {
//                            thFaseSelected = thFaseList.get(i);
//                            break;
//                        }
//                    }
//                }
//            } else {
//                thFaseSelected = thFaseList.get(0);
//            }
            thFaseSelected = getThfSelected(thFaseList, fechaIn);

            // Aqui se determinan los cambios en el valor de la paridad cambiaria
            paridad = recalculaParidad(paridadList, fechaIn);

            fechaOut = doFase(fechaIn, elementos, fase, thFaseSelected, paridad);
            // Se verifica si hay mantenimiento en esta fase 
            // y se ejecuta a posteriori
            if (!mantenimientos.isEmpty()) {
                for (TaladroMant mant : mantenimientos) {
                    if ((fechaIn.equals(mant.getFecha()) || fechaIn.before(mant.getFecha()))
                            && mant.getFecha().before(fechaOut)) {
                        LocalDate localDateOut = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(fechaOut.getTime()),
                                ZoneId.systemDefault()).toLocalDate();
                        localDateOut = localDateOut.plusDays(mant.getDias());
                        fechaOut = Date.from(localDateOut.atStartOfDay()
                                .atZone(ZoneId.systemDefault()).toInstant());
                        break;
                    }
                }
            }

            // Se verifica que el taladro va a ser descontinuado
            if (fechaDescontinuacion != null
                    && (fechaDescontinuacion.after(fechaIn)
                    || fechaDescontinuacion.equals(fechaIn))
                    && fechaDescontinuacion.before(fechaOut)) {
                Object[] pozoFaseArray = new Object[3];

                pozoFaseArray[0] = pozo;
                switch (fase) {
                    case Constantes.FASE_SUPERFICIAL:
                        pozoFaseArray[1] = Constantes.FASE_INTERMEDIO;
                        break;
                    case Constantes.FASE_INTERMEDIO:
                        pozoFaseArray[1] = Constantes.FASE_PRODUCTOR;
                        break;
                    case Constantes.FASE_PRODUCTOR:
                        pozoFaseArray[1] = "";
                        break;
                    case Constantes.FASE_SLANT:
                        pozoFaseArray[1] = "";
                        break;
                }

                pozoFaseArray[2] = sec;
                filaIncompleta = true;
                filaIncompletaMap.put(filaSelected, pozoFaseArray);
                filaIncompletaList.push(filaSelected);
                descontinuado = true;
                if (!fase.equals(Constantes.FASE_PRODUCTOR)) {
                    break; // rompe la secuencia de perforación
                }
            }

            // Se verifica si el taladro tiene fecha para ser mudado
            if (statusMudadoList.size() > 0) {
                for (TaladroStatus tStatus : statusMudadoList) {
                    Date fechaTemp = tStatus.getFechaIn();
                    if (fechaTemp != null
                            && (fechaTemp.after(fechaIn) || fechaTemp.equals(fechaIn))
                            && fechaTemp.before(fechaOut)) {
                        fechaMudado = fechaOut;
                        fechaMudadoMap.put(taladroSelected, fechaMudado);
                        break;
                    }
                }
            }

            // Si el taladro se muda, hasta aqui se ejecutará la perforación
            if (fechaMudado != null) {
                Object[] pozoFaseArray = new Object[3];
                pozoFaseArray[0] = pozo;
                switch (fase) {
                    case Constantes.FASE_SUPERFICIAL:
                        pozoFaseArray[1] = Constantes.FASE_INTERMEDIO;
                        break;
                    case Constantes.FASE_INTERMEDIO:
                        pozoFaseArray[1] = Constantes.FASE_PRODUCTOR;
                        break;
                    case Constantes.FASE_PRODUCTOR:
                    case Constantes.FASE_SLANT:    
                        pozoFaseArray[1] = "";
                        break;
                }

                pozoFaseArray[2] = sec;
                filaIncompleta = true;
                filaIncompletaList.push(filaSelected);
                filaIncompletaMap.put(filaSelected, pozoFaseArray);
                taladroMudado = true;
                if (!fase.equals(Constantes.FASE_PRODUCTOR)) {
                    break;  // se sale del ciclo de perforación al ser mudado
                }
            }

            // Hace el slant
            if (fase.equals(Constantes.FASE_SLANT)) {
                paridad = recalculaParidad(paridadList, fechaOut);
                thFaseList = thfManager.findAll(taladroSelected, escenarioSelected,
                        Constantes.FASE_SLANT);
                thFaseSelected = getThfSelected(thFaseList, fechaIn);
                fechaOut = doSlant(fechaOut, elementos, thFaseSelected, paridad);
            }

            if (fase.equals(Constantes.FASE_PRODUCTOR)) {
                // Hace la completación
                fechaFinProductor = fechaOut;
                paridad = recalculaParidad(paridadList, fechaOut);
                thFaseList = thfManager.findAll(taladroSelected, escenarioSelected,
                        Constantes.FASE_COMPLETACION);
                thFaseSelected = getThfSelected(thFaseList, fechaIn);
                fechaOut = doCompletacion(fechaOut, elementos, thFaseSelected, paridad);
                // Hace la Conexión
                thFaseList = thfManager.findAll(taladroSelected, escenarioSelected,
                        Constantes.FASE_CONEXION);
                thFaseSelected = getThfSelected(thFaseList, fechaIn);
                paridad = recalculaParidad(paridadList, fechaOut);
                fechaOut = doConexion(fechaOut, elementos, thFaseSelected, paridad);
                fechaOut = doEvaluacion(fechaOut, elementos);
                if (taladroMudado || descontinuado) {
                    filaIncompleta = true;
                    filaIncompletaList.push(filaSelected);
                    Object[] pozoFaseArray = new Object[3];
                    pozoFaseArray[0] = pozo;
                    pozoFaseArray[1] = Constantes.FASE_PRODUCTOR;
                    pozoFaseArray[2] = sec;
                    filaIncompletaMap.put(filaSelected, pozoFaseArray);
                    break;
                }
            }

            fechaIn = fase.equals(Constantes.FASE_PRODUCTOR) ? fechaFinProductor : fechaOut;
            elementos = new Object[10];
        }

        taladroStatus = new TaladroStatus();
        if (!descontinuado && !taladroMudado) {
            taladroStatus.setNombre(Constantes.TALADRO_STATUS_OCUPADO);  //<--- ojo aqui
            taladroStatus.setFechaIn(fechaFinProductor);
            taladroStatus.setStatus(Constantes.TALADRO_STATUS_ACTIVO);
            taladroStatus.setTaladroId(taladroSelected);
            statusTaladroMap.put(taladroSelected, taladroStatus);

            taladrosUsadosList.push(taladroSelected);

            // para saber que taladro usó una fila al final
            Object[] taladroArray = new Object[3];
            taladroArray[0] = taladroSelected;
            taladroArray[1] = fechaFinProductor;
            taladroArray[2] = macollaSelected;
            taladroUsadosMap.put(filaSelected, taladroArray);
            if (taladrosUsadosList.size() > 1) {
                Collections.sort(taladrosUsadosList, (Taladro tal1, Taladro tal2) -> {
                    int result;
                    TaladroStatus talStatus1 = statusTaladroMap.get(tal1);
                    TaladroStatus talStatus2 = statusTaladroMap.get(tal2);
                    result = talStatus1.getFechaIn().compareTo(talStatus2.getFechaIn());
                    return result;
                });
            }
        } else if (descontinuado) {
            taladroStatus.setNombre(Constantes.TALADRO_STATUS_NO_DISPONIBLE);
            taladroStatus.setFechaIn(fechaDescontinuacion);
            taladroStatus.setStatus(Constantes.TALADRO_STATUS_ACTIVO);
            taladroStatus.setTaladroId(taladroSelected);

            if (taladrosUsadosList.contains(taladroSelected)) {
                taladrosUsadosList.remove(taladroSelected);
            }
        } else if (mudado) {
            taladroStatus.setNombre(Constantes.TALADRO_STATUS_OCUPADO);  //<--- ojo aqui
            taladroStatus.setFechaIn(fechaMudado);
            taladroStatus.setStatus(Constantes.TALADRO_STATUS_ACTIVO);
            taladroStatus.setTaladroId(taladroSelected);
            statusTaladroMap.put(taladroSelected, taladroStatus);
        }
    }

    private void procesarFila(FilaHasTaladro fhtSelected, boolean mudado,
            PozoSecuencia secuen) {

        Taladro taladroSelected = fhtSelected.getTaladroId();
        Fila filaSelected = fhtSelected.getFilaId();
        Macolla macollaSelected = filaSelected.getMacollaId();
        boolean descontinuado = false;

        if (fhtSelected.getFechaAsignacion() != null) {
            mudado = true;
        }

        Object[] elementos = new Object[11]; // arreglo de elemento a mostrar como resultado

        if (!statusTaladroMap.containsKey(taladroSelected)) {
            TaladroStatus taladroStatus = talStatusManager.find(taladroSelected,
                    Constantes.TALADRO_STATUS_ACTIVO, Constantes.TALADRO_STATUS_OCUPADO);
            statusTaladroMap.put(taladroSelected, taladroStatus);
        }
        List<PozoSecuencia> secuencia = pozoSecManager.findAllOrdered(filaSelected, escenarioSelected);

        //Date fechaIn = taladroSelected.getFechaInicial();
        TaladroStatus taladroStatus = statusTaladroMap.get(taladroSelected);
        Date fechaIn = taladroStatus.getFechaIn();
        Date fechaOut = null;
        Date fechaFinProductor = null;
        Date fechaDescontinuacion = null;
        boolean mudanzaEncontrada = false;

        if (mudado && !mudanzaEncontrada) {
            List<TaladroHasFase> thfList = thfManager.findAll(taladroSelected, escenarioSelected);
            TaladroHasFase thf = null;
            for (TaladroHasFase item : thfList) {
                if (item.getFase().equals(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS)) {
                    thf = item;
                    break;
                }
            }

            long extratime;
            if (thf != null) {
                extratime = (long) (thf.getDias() * 24 * 3600 * 1000);
            } else {
                extratime = (long) (30 * 24 * 3600 * 1000);
            }
            fechaIn = new Date(fechaIn.getTime() + extratime);
        }

        List<TaladroMant> mantenimientos = mantenimientoManager.findAll(taladroSelected);
        List<Paridad> paridadList = paridadManager.findAll();

        TaladroStatus statusDescontinuado = talStatusManager.find(taladroSelected,
                Constantes.TALADRO_STATUS_ACTIVO,
                Constantes.TALADRO_STATUS_DESCONTINUADO);
        if (statusDescontinuado != null) {
            fechaDescontinuacion = statusDescontinuado.getFechaIn();
        }

        for (PozoSecuencia sec : secuencia) {
            if (sec.getSecuencia() <= secuen.getSecuencia()) {
                continue;
            }

            elementos[0] = taladroSelected; // taladro
            elementos[1] = macollaSelected; //macolla
            elementos[2] = filaSelected; // fila
            Pozo pozo = sec.getPozoId(); // pozo
            elementos[3] = pozo;
            String fase = sec.getFase(); // fase

            List<TaladroHasFase> thFaseList = thfManager.findAll(taladroSelected, escenarioSelected, fase);
            TaladroHasFase thFaseSelected = null;
            Paridad paridad = null;

            // Aqui se determina los cambios de tiempo y costos del taladro
//            if (thFaseList.size() > 1) {
//                for (int i = 0; i < thFaseList.size(); i++) {
//                    if (i == (thFaseList.size() - 1)) {
//                        thFaseSelected = thFaseList.get(i);
//                    } else {
//                        if ((thFaseList.get(i).getFecha().before(fechaIn)
//                                || thFaseList.get(i).getFecha().equals(fechaIn))
//                                && thFaseList.get(i + 1).getFecha().after(fechaIn)) {
//                            thFaseSelected = thFaseList.get(i);
//                            break;
//                        }
//                    }
//                }
//            } else {
//                thFaseSelected = thFaseList.get(0);
//            }
            thFaseSelected = getThfSelected(thFaseList, fechaIn);

            // Aqui se determinan los cambios en el valor de la paridad cambiaria
            paridad = recalculaParidad(paridadList, fechaIn);
            fechaOut = doFase(fechaIn, elementos, fase, thFaseSelected, paridad);

            // Se verifica si hay mantenimiento en esta fase 
            // y se ejecuta a posteriori
            if (!mantenimientos.isEmpty()) {
                for (TaladroMant mant : mantenimientos) {
                    if ((fechaIn.equals(mant.getFecha()) || fechaIn.before(mant.getFecha()))
                            && mant.getFecha().before(fechaOut)) {
                        LocalDate localDateOut = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(fechaOut.getTime()),
                                ZoneId.systemDefault()).toLocalDate();
                        localDateOut = localDateOut.plusDays(mant.getDias());
                        fechaOut = Date.from(localDateOut.atStartOfDay()
                                .atZone(ZoneId.systemDefault()).toInstant());
                        break;
                    }
                }
            }

            // Se verifica que el taladro va a ser descontinuado
            if (fechaDescontinuacion != null
                    && (fechaDescontinuacion.after(fechaIn)
                    || fechaDescontinuacion.equals(fechaIn))
                    && fechaDescontinuacion.before(fechaOut)) {
                Object[] pozoFaseArray = new Object[3];

                pozoFaseArray[0] = pozo;
                switch (fase) {
                    case Constantes.FASE_SUPERFICIAL:
                        pozoFaseArray[1] = Constantes.FASE_INTERMEDIO;
                        break;
                    case Constantes.FASE_INTERMEDIO:
                        pozoFaseArray[1] = Constantes.FASE_PRODUCTOR;
                        break;
                    case Constantes.FASE_PRODUCTOR:
                    case Constantes.FASE_SLANT:
                        pozoFaseArray[1] = "";
                        break;
                }

                pozoFaseArray[2] = sec;
                filaIncompletaMap.put(filaSelected, pozoFaseArray);
                descontinuado = true;
                if (!fase.equals(Constantes.FASE_PRODUCTOR)) {
                    break;
                }
            }
            
            if(fase.equals(Constantes.FASE_SLANT)){
                paridad = recalculaParidad(paridadList, fechaOut);
                thFaseList = thfManager.findAll(taladroSelected, escenarioSelected,
                        Constantes.FASE_SLANT);
                thFaseSelected = getThfSelected(thFaseList, fechaIn);
                fechaOut = doSlant(fechaOut, elementos, thFaseSelected, paridad);
            }

            if (fase.equals(Constantes.FASE_PRODUCTOR)) {
                // hace la completación
                fechaFinProductor = fechaOut;
                paridad = recalculaParidad(paridadList, fechaOut);
                thFaseList = thfManager.findAll(taladroSelected, escenarioSelected,
                        Constantes.FASE_COMPLETACION);
                thFaseSelected = getThfSelected(thFaseList, fechaIn);
                fechaOut = doCompletacion(fechaOut, elementos, thFaseSelected, paridad);

                // hace la conexión
                thFaseList = thfManager.findAll(taladroSelected, escenarioSelected,
                        Constantes.FASE_CONEXION);
                thFaseSelected = getThfSelected(thFaseList, fechaIn);
                paridad = recalculaParidad(paridadList, fechaOut);
                fechaOut = doConexion(fechaOut, elementos, thFaseSelected, paridad);
                fechaOut = doEvaluacion(fechaOut, elementos);
            }

            fechaIn = fase.equals(Constantes.FASE_PRODUCTOR) ? fechaFinProductor : fechaOut;
            elementos = new Object[10];

        }

        filaIncompleta = false;
        taladroStatus = new TaladroStatus();
        if (!descontinuado) {
            taladroStatus.setNombre(Constantes.TALADRO_STATUS_OCUPADO);  //<--- ojo aqui
            taladroStatus.setFechaIn(fechaFinProductor);
            taladroStatus.setStatus(Constantes.TALADRO_STATUS_ACTIVO);
            taladroStatus.setTaladroId(taladroSelected);
            statusTaladroMap.put(taladroSelected, taladroStatus);

            taladrosUsadosList.push(taladroSelected);

            // para saber que taladro usó una fila al final
            Object[] taladroArray = new Object[3];
            taladroArray[0] = taladroSelected;
            taladroArray[1] = fechaFinProductor;
            taladroArray[2] = macollaSelected;
            taladroUsadosMap.put(filaSelected, taladroArray);
            if (taladrosUsadosList.size() > 1) {
                Collections.sort(taladrosUsadosList, (Taladro tal1, Taladro tal2) -> {
                    int result;
                    TaladroStatus talStatus1 = statusTaladroMap.get(tal1);
                    TaladroStatus talStatus2 = statusTaladroMap.get(tal2);
                    result = talStatus1.getFechaIn().compareTo(talStatus2.getFechaIn());
                    return result;
                });
            }
        } else {
            taladroStatus.setNombre(Constantes.TALADRO_STATUS_NO_DISPONIBLE);
            taladroStatus.setFechaIn(fechaDescontinuacion);
            taladroStatus.setStatus(Constantes.TALADRO_STATUS_ACTIVO);
            taladroStatus.setTaladroId(taladroSelected);

            taladrosUsadosList.remove(taladroSelected);
        }
    }

    private TaladroHasFase getThfSelected(List<TaladroHasFase> thFaseList, Date fechaIn) {
        TaladroHasFase thFaseSelected = null;
        if (thFaseList.size() > 1) {
            for (int i = 0; i < thFaseList.size(); i++) {
                if (i == (thFaseList.size() - 1)) {
                    thFaseSelected = thFaseList.get(i);
                } else {
                    if ((thFaseList.get(i).getFecha().before(fechaIn)
                            || thFaseList.get(i).getFecha().equals(fechaIn))
                            && thFaseList.get(i + 1).getFecha().after(fechaIn)) {
                        thFaseSelected = thFaseList.get(i);
                        break;
                    }
                }
            }
        } else {
            thFaseSelected = thFaseList.get(0);
        }
        return thFaseSelected;
    }

    private Paridad recalculaParidad(List<Paridad> paridadList, Date fechaIn) {
        Paridad paridad = null;
        if (paridadList.size() > 1) {
            for (int i = 0; i < paridadList.size(); i++) {
                if (i == (paridadList.size() - 1)) {
                    paridad = paridadList.get(i);
                } else {
                    if ((paridadList.get(i).getFechaIn().before(fechaIn)
                            || paridadList.get(i).getFechaIn().equals(fechaIn))
                            && paridadList.get(i + 1).getFechaIn().after(fechaIn)) {
                        paridad = paridadList.get(i);
                        break;
                    }
                }
            }
        } else {
            paridad = paridadList.get(0);
        }
        return paridad;
    }

    /**
     * Método que completa el arreglo de perforación de cada pozo, con la fase y
     * la fecha de entrada en esa fase, construye el resto del arreglo,
     * agregando la fase, la fechaIn, la fechaOut y el costo de esa fase.
     * Retorna la fecha de salida de esa fase
     */
    private Date doFase(Date fechaIn, Object[] elementos, String fase,
            TaladroHasFase thf, Paridad paridad) {
        Object[] items = new Object[11];
        System.arraycopy(elementos, 0, items, 0, 4);
        Taladro taladro = (Taladro) items[0];
        items[4] = fase;

//        TaladroHasFase thf = thfManager.find(fase, taladro, escenarioSelected);
        double dias = thf.getDias();
        long milisec = (long) (dias * 24 * 3600 * 1000);
        items[5] = fechaIn;
        long fechaInicialMiliSec = fechaIn.getTime();
        Date fechaOut = new Date(fechaInicialMiliSec + milisec);
        items[6] = fechaOut;

        items[7] = thf.getCostoBs(); // costo Bs
        items[8] = thf.getCostoUsd(); // costo US$
        items[9] = thf.getCostoUsd() + thf.getCostoBs() / paridad.getValor(); // costo $Equiv
        items[10] = thf.getEscenarioId(); // escenario

        estrategiaPerforacionMap.put(indicePerforacion++, items);
        return fechaOut;
    }

    /**
     * Método que permite realizar la fase de completación del pozo
     *
     * @param fechaIn
     * @param elementos
     * @return
     */
    private Date doCompletacion(Date fechaIn, Object[] elementos, TaladroHasFase thf, Paridad paridad) {
        return doFase(fechaIn, elementos, Constantes.FASE_COMPLETACION, thf, paridad);
    }

    /**
     * Método que permite realizar la fase slant del pozo
     *
     * @param fechaIn
     * @param elementos
     * @return
     */
    private Date doSlant(Date fechaIn, Object[] elementos, TaladroHasFase thf, Paridad paridad) {
        return doFase(fechaIn, elementos, Constantes.FASE_SLANT, thf, paridad);
    }

    /**
     * Método que permite realizar la fase de conexión del pozo
     *
     * @param fechaIn
     * @param elementos
     * @return
     */
    private Date doConexion(Date fechaIn, Object[] elementos, TaladroHasFase thf, Paridad paridad) {
        return doFase(fechaIn, elementos, Constantes.FASE_CONEXION, thf, paridad);
    }

    /**
     * Método que permite realizar la fase de evaluación del pozo
     *
     */
    private Date doEvaluacion(Date fechaIn, Object[] elementos) {
        Object[] items = new Object[11];
        System.arraycopy(elementos, 0, items, 0, 4);
        Pozo pozo = (Pozo) items[3];
        items[4] = Constantes.FASE_EVALUACION;
        List<Rampeo> rampeos = rampeoManager.findAll(pozo, escenarioSelected);

        double dias = 0;
        for (Rampeo rampa : rampeos) {
            dias += rampa.getDias();
        }
        long milisec = (long) (dias * 24 * 3600 * 1000);
        items[5] = fechaIn;
        long fechaInicialMiliSec = fechaIn.getTime();
        Date fechaOut = new Date(fechaInicialMiliSec + milisec);
        items[6] = fechaOut;

        items[7] = 0.0;
        items[8] = 0.0;
        items[9] = 0.0;
        items[10] = escenarioSelected;

        estrategiaPerforacionMap.put(indicePerforacion++, items);
        return fechaOut;
    }
}
