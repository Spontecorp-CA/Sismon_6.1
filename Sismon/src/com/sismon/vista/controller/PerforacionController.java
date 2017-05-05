package com.sismon.vista.controller;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.FilaHasTaladroManager;
import com.sismon.jpamanager.FilaManager;
import com.sismon.jpamanager.MacollaSecuenciaManager;
import com.sismon.jpamanager.PozoSecuenciaManager;
import com.sismon.jpamanager.RampeoManager;
import com.sismon.jpamanager.TaladroHasFaseManager;
import com.sismon.jpamanager.TaladroManager;
import com.sismon.jpamanager.TaladroStatusManager;
import com.sismon.model.Fila;
import com.sismon.model.FilaHasTaladro;
import com.sismon.model.Macolla;
import com.sismon.model.MacollaSecuencia;
import com.sismon.model.Pozo;
import com.sismon.model.PozoSecuencia;
import com.sismon.model.Rampeo;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroHasFase;
import com.sismon.model.TaladroStatus;
import com.sismon.vista.Contexto;
import com.sismon.vista.utilities.SismonLog;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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

public class PerforacionController extends SwingWorker<Map<Integer, Object[]>, Void> {

    private final TaladroManager taladroManager;
    private final FilaHasTaladroManager fhtManager;
    private final MacollaSecuenciaManager macollaSecManager;
    private final FilaManager filaManager;
    private final PozoSecuenciaManager pozoSecManager;
    private final TaladroHasFaseManager thfManager;
    private final TaladroStatusManager talStatusManager;
    private final RampeoManager rampeoManager;

    private final Map<Integer, Object[]> estrategiaPerforacionMap;
    private final Map<Taladro, TaladroStatus> statusTaladroMap;
    private final LinkedList<Taladro> taladrosUsadosList = new LinkedList<>();
    private final JProgressBar progressBar;

    private int indicePerforacion = 1;

    private static final SismonLog sismonlog = SismonLog.getInstance();
    private static final DateFormat DF = new SimpleDateFormat("dd/MM/yyyy");

    public PerforacionController(Map<Integer, Object[]> estrategiaPerforacionMap,
            JProgressBar progressBar) {
        this.taladroManager = new TaladroManager();
        this.fhtManager = new FilaHasTaladroManager();
        this.macollaSecManager = new MacollaSecuenciaManager();
        this.filaManager = new FilaManager();
        this.pozoSecManager = new PozoSecuenciaManager();
        this.thfManager = new TaladroHasFaseManager();
        this.talStatusManager = new TaladroStatusManager();
        this.rampeoManager = new RampeoManager();

        this.estrategiaPerforacionMap = estrategiaPerforacionMap;
        this.statusTaladroMap = new HashMap<>();
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
        List<FilaHasTaladro> fhtList = fhtManager.findAllBase();
        Map<Fila, FilaHasTaladro> filaWithTaladroMap = new HashMap<>();
        for (FilaHasTaladro fht : fhtList) {
            filaWithTaladroMap.put(fht.getFilaId(), fht);
        }

        // Se busca la secuencia de perforación de las macollas
        List<MacollaSecuencia> secuenciaList = macollaSecManager.findAllOrdered();
        FilaHasTaladro fhtSelected;
        Macolla macollaSelected;
        boolean mudado = false;
        for (MacollaSecuencia sec : secuenciaList) {
            macollaSelected = sec.getMacollaId();
            List<Fila> filaList = filaManager.findAll(macollaSelected);
            for (Fila filaSelected : filaList) {
                if (filaWithTaladroMap.containsKey(filaSelected)) {
                    fhtSelected = filaWithTaladroMap.get(filaSelected);
                    taladroSelected = fhtSelected.getTaladroId();
                } else {
                    taladroSelected = taladrosUsadosList.pop();
                    mudado = true;
                    fhtSelected = new FilaHasTaladro();
                    fhtSelected.setTaladroId(taladroSelected);
                    fhtSelected.setFilaId(filaSelected);
                    fhtSelected.setEscenarioId(null);
                }
                procesarFila(fhtSelected, mudado);
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
        
        sismonlog.logger.log(Level.INFO, "Taladro: {0}, Macolla: {1}, Fila: {2}", 
                                new Object[]{taladroSelected.getNombre(),
                                            macollaSelected.getNombre(),
                                            filaSelected.getNombre()});

        Object[] elementos = new Object[13]; // arreglo de elemento a mostrar como resultado
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        List<PozoSecuencia> secuencia = pozoSecManager.findAllOrdered(filaSelected);
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

        sismonlog.logger.log(Level.INFO,"Cargada la secuencia de perferoracion de la fila {0}",
                                filaSelected.getNombre());
        
        if (!statusTaladroMap.containsKey(taladroSelected)) {
            TaladroStatus taladroStatus = talStatusManager.find(taladroSelected,
                    Constantes.TALADRO_STATUS_ACTIVO, Constantes.TALADRO_STATUS_OCUPADO);
            statusTaladroMap.put(taladroSelected, taladroStatus);
        }

        //Date fechaIn = taladroSelected.getFechaInicial();
        TaladroStatus taladroStatus = statusTaladroMap.get(taladroSelected);
        Date fechaIn = taladroStatus.getFechaIn();
        Date fechaOut = null;
        Date fechaFinProductor = null;
        Date fechaFinSlant = null;
        boolean mudanzaEncontrada = false;

        for (PozoSecuencia sec : secuencia) {
            if (sec.getFase().equals(Constantes.FASE_MUDANZA_ENTRE_MACOLLAS)) {
                mudanzaEncontrada = true;
            }
        }

        sismonlog.logger.log(Level.INFO, "Se identifica si la macolla tiene taladro o viene de mudanza");
        if (mudado && !mudanzaEncontrada) {
            List<TaladroHasFase> thfList = thfManager.findAll(taladroSelected);
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

        int index = 0;
        for (PozoSecuencia sec : secuencia) {
            elementos[0] = taladroSelected; // taladro
            elementos[1] = macollaSelected; //macolla
            elementos[2] = filaSelected; // fila
            Pozo pozo = sec.getPozoId(); // pozo
            elementos[3] = pozo;
            String fase = sec.getFase(); // fase

            if (pozo.getClasePozo().equals(Constantes.TIPO_PRODUCTOR)) {
                fechaOut = doFase(fechaIn, elementos, fase);
                if (fase.equals(Constantes.FASE_PRODUCTOR)) {
                    fechaFinProductor = fechaOut;
                    fechaOut = doCompletacion(fechaOut, elementos);
                    fechaOut = doConexion(fechaOut, elementos);
                    fechaOut = doEvaluacion(fechaOut, elementos);
                }
            } else {
                fechaOut = doFase(fechaIn, elementos, fase);
                if(fase.equals(Constantes.FASE_SLANT)){
                    sismonlog.logger.log(Level.INFO, 
                            "Entró en fase SLANT en la fecha {0}", 
                            DF.format(fechaIn));
                    fechaOut = doSlant(fechaOut, elementos);
                    fechaFinSlant = fechaOut;
                    sismonlog.logger.log(Level.INFO,
                            "Salió de la fase SLANT en la fecha {0}",
                            DF.format(fechaOut));
                }
            }
            
            fechaIn = fase.equals(Constantes.FASE_PRODUCTOR) ? fechaFinProductor : fechaOut;
            elementos = new Object[13];

            index++;
        }

        taladroStatus = new TaladroStatus();
        taladroStatus.setNombre(Constantes.TALADRO_STATUS_OCUPADO);
        
        if(null != fechaFinProductor){
            taladroStatus.setFechaIn(fechaFinProductor);
        } else {
            taladroStatus.setFechaIn(fechaFinSlant);
        }
        
        taladroStatus.setStatus(Constantes.TALADRO_STATUS_ACTIVO);
        taladroStatus.setTaladroId(taladroSelected);

        statusTaladroMap.put(taladroSelected, taladroStatus);

        sismonlog.logger.log(Level.INFO, "Cambia el status del taladro {0} a {1}", 
                    new Object[]{taladroSelected.getNombre(), taladroStatus.getStatus()});
        
        taladrosUsadosList.push(taladroSelected);
        
        sismonlog.logger.log(Level.INFO, "Va ordenar la lista de taladros usados: ");
        
        if (taladrosUsadosList.size() > 1) {
            Collections.sort(taladrosUsadosList, (Taladro tal1, Taladro tal2) -> {
                int result;
                TaladroStatus talStatus1 = statusTaladroMap.get(tal1);
                TaladroStatus talStatus2 = statusTaladroMap.get(tal2);
                
                sismonlog.logger.log(Level.INFO, "El taladro {0} tiene fecha de comparación {1}"
                        + " y el taladro {2} tiene fecha de comparación {3}",
                        new Object[]{tal1.getNombre(), DF.format(talStatus1.getFechaIn()),
                                    tal2.getNombre(), DF.format(talStatus2.getFechaIn())});
                
                result = talStatus1.getFechaIn().compareTo(talStatus2.getFechaIn());
                //result = tal1.getFechaStatus().compareTo(tal2.getFechaStatus());
                return result;
            });
        }

    }

    /**
     * Método que completa el arreglo de perforación de cada pozo, con la fase y
     * la fecha de entrada en esa fase, construye el resto del arreglo,
     * agregando la fase, la fechaIn, la fechaOut y el costo de esa fase.
     * Retorna la fecha de salida de esa fase
     */
    private Date doFase(Date fechaIn, Object[] elementos, String fase) {
        Object[] items = new Object[13];
        System.arraycopy(elementos, 0, items, 0, 4);
        Taladro taladro = (Taladro) items[0];
        items[4] = fase;
        
        sismonlog.logger.log(Level.INFO, "Comienza la fase {0} del pozo {1} en fecha {2}",
                new Object[]{fase, ((Pozo)items[3]).getUbicacion(),DF.format(fechaIn)});

        TaladroHasFase thf = thfManager.find(fase, taladro);
        double dias = thf.getDias();
        long milisec = (long) (dias * 24 * 3600 * 1000);
        items[5] = fechaIn;
        long fechaInicialMiliSec = fechaIn.getTime();
        Date fechaOut = new Date(fechaInicialMiliSec + milisec);
        items[6] = fechaOut;

        items[7] = thf.getCostoBs(); // costo Bs
        items[8] = thf.getCostoUsd(); // costo US$
        items[9] = thf.getCostoEquiv(); // costo $Equiv
        
        LocalDate ldIn = LocalDateTime
                .ofInstant(fechaIn.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
        LocalDate ldOut = LocalDateTime
                .ofInstant(fechaOut.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
        
        items[10] = (double) ChronoUnit.DAYS.between(ldIn, ldOut);  // dias activos
        items[11] = 0.0;  // dias inactivos
        items[12] = items[10]; // dias totales

        estrategiaPerforacionMap.put(indicePerforacion++, items);
        
        sismonlog.logger.log(Level.INFO, "Sale de la fase {0} en fecha {1}",
                new Object[]{fase, DF.format(fechaOut)});
        
        return fechaOut;
    }

    /**
     * Método que permite realizar la fase de completación del pozo
     *
     * @param fechaIn
     * @param elementos
     * @return
     */
    private Date doCompletacion(Date fechaIn, Object[] elementos) {
        return doFase(fechaIn, elementos, Constantes.FASE_COMPLETACION);
    }

    /**
     * Método que permite realizar la fase de conexión del pozo
     *
     * @param fechaIn
     * @param elementos
     * @return
     */
    private Date doConexion(Date fechaIn, Object[] elementos) {
        return doFase(fechaIn, elementos, Constantes.FASE_CONEXION);
    }
    
    /**
     * Hace la parte slant
     */
    private Date doSlant(Date fechaIn, Object[] elementos) {
        return doFase(fechaIn, elementos, Constantes.FASE_SLANT);
    }

    /**
     * Método que permite realizar la fase de evaluación del pozo
     *
     */
    private Date doEvaluacion(Date fechaIn, Object[] elementos) {
        Object[] items = new Object[13];
        System.arraycopy(elementos, 0, items, 0, 4);
        Pozo pozo = (Pozo) items[3];
        items[4] = Constantes.FASE_EVALUACION;
        List<Rampeo> rampeos = rampeoManager.findAll(pozo);

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
        
        LocalDate ldIn = LocalDateTime
                .ofInstant(fechaIn.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
        LocalDate ldOut = LocalDateTime
                .ofInstant(fechaOut.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
        items[10] = (double)ChronoUnit.DAYS.between(ldIn, ldOut);
        items[11] = 0.0;
        items[12] = items[10];

        estrategiaPerforacionMap.put(indicePerforacion++, items);
        return fechaOut;
    }
}
