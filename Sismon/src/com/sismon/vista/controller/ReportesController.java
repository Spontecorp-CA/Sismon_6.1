package com.sismon.vista.controller;

import com.sismon.controller.Constantes;
import com.sismon.exceptions.SismonException;
import com.sismon.jpamanager.CampoManager;
import com.sismon.jpamanager.EscenarioManager;
import com.sismon.jpamanager.ExplotacionManager;
import com.sismon.jpamanager.MacollaExplotadaManager;
import com.sismon.jpamanager.MacollaManager;
import com.sismon.jpamanager.ParidadManager;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.jpamanager.PozoExplotadoManager;
import com.sismon.jpamanager.PozoManager;
import com.sismon.jpamanager.TaladroManager;
import com.sismon.model.Campo;
import com.sismon.model.Escenario;
import com.sismon.model.Explotacion;
import com.sismon.model.Macolla;
import com.sismon.model.MacollaExplotada;
import com.sismon.model.Paridad;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.model.PozoExplotado;
import com.sismon.model.Taladro;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ReportesController {

    private final EscenarioManager escenarioManager;
    private final PozoManager pozoManager;
    private final CampoManager campoManager;
    private final MacollaManager macollaManager;
    private final TaladroManager taladroManager;
    private final PerforacionManager perforacionManager;
    private final ExplotacionManager explotacionManager;
    private final MacollaExplotadaManager macollaExplotadaManager;
    private final PozoExplotadoManager pozoExplotadoManager;
    private final ParidadManager paridadManager;

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public ReportesController() {
        this.escenarioManager = new EscenarioManager();
        this.pozoManager = new PozoManager();
        this.campoManager = new CampoManager();
        this.macollaManager = new MacollaManager();
        this.taladroManager = new TaladroManager();
        this.perforacionManager = new PerforacionManager();
        this.explotacionManager = new ExplotacionManager();
        this.macollaExplotadaManager = new MacollaExplotadaManager();
        this.pozoExplotadoManager = new PozoExplotadoManager();
        this.paridadManager = new ParidadManager();
    }

    public List<Escenario> getEscenarioList() {
        return escenarioManager.findAll();
    }

    public List<Pozo> getPozoList(Escenario escenario) {
        return pozoManager.findAll(escenario);
    }

    public int getPozosCount() {
        return explotacionManager.count();
    }

    public List<Pozo> getPozoList() {
        return explotacionManager.findAllPozos();
    }

    public List<Pozo> getPozoList(Escenario escenario, Macolla macolla) throws SismonException {
        return pozoManager.findAll(macolla, escenario);
    }

    public List<Campo> getCampoList() {
        return campoManager.findAll();
    }

    public List<Macolla> getMacollaList(Campo campo) {
        return macollaManager.findAll(campo);
    }

    public List<Taladro> getTaladroList(Escenario escenario) {
        return taladroManager.findAll(escenario);
    }

    public List<Perforacion> getPerforacionList(String query, String[] paramNames,
            Object[] params) {
        return perforacionManager.findAll(query, paramNames, params);
    }

    public List<Explotacion> getExplotacionList(String query, String[] paramNames,
            Object[] params) {
        List<Explotacion> lista = explotacionManager.findAll(query, paramNames, params);
        return lista;
    }

    public List<Explotacion> getExplotacionList(String query) {
        List<Explotacion> lista = explotacionManager.findAll(query);
        return lista;
    }

    public List<Macolla> getMacollasExplotadas() {
        List<MacollaExplotada> macollasExplotadasList = macollaExplotadaManager.findAll();
        List<Macolla> macollas = new ArrayList<>();
        macollasExplotadasList.stream()
                .forEach(me -> {
                    macollas.add(me.getMacollaId());
                });
        return macollas;
    }

    public List<Pozo> getPozosExplotados(Macolla macolla) {
        List<PozoExplotado> pozosExplotadosList = pozoExplotadoManager.findAll();
        pozosExplotadosList.sort((p1, p2)
                -> p1.getPozoId().getUbicacion().compareTo(p2.getPozoId().getUbicacion()));
        List<Pozo> pozos = new ArrayList<>();
        pozosExplotadosList.stream()
                .filter(pe -> pe.getPozoId().getMacollaId().equals(macolla))
                .forEach(pe -> {
                    pozos.add(pe.getPozoId());
                });
        return pozos;
    }

    public Map<Pozo, Object[]> prepareDataPerforacionXExcel2(List<Perforacion> lista) {
        Map<Pozo, Object[]> mapa = new LinkedHashMap<>();

        List<Perforacion> listaSorted = new ArrayList<>();
        // se ordena la lista por taladro, macolla y pozo
//        Comparator<Perforacion> byTaladro = (t1, t2)
//                -> t1.getTaladroId().getId().compareTo(t2.getTaladroId().getId());
        Comparator<Perforacion> byMacolla = (m1, m2)
                -> m1.getMacollaId().getId().compareTo(m2.getMacollaId().getId());
        Comparator<Perforacion> byPozo = (p1, p2)
                -> p1.getPozoId().compareTo(p2.getPozoId());

        lista.stream()
                .sorted(byMacolla.thenComparing(byPozo))
                .forEach(p -> listaSorted.add(p));

        List<Paridad> paridadList = paridadManager.findAll();

        Pozo pozoActual = null;
        Pozo pozoAnterior = null;
        double costoPerfBs = 0.0;
        double costoPerfUsd = 0.0;
        double costoPerfEquiv = 0.0;
        double costo_total = 0.0;
        long diasPerf = 0L;
        long dias_totales = 0L;
        LocalDate ldIn;
        LocalDate ldOut;
        Paridad paridad;
        Object[] pozoData = null;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        for (Perforacion perf : listaSorted) {
            pozoActual = perf.getPozoId();
            if (!pozoActual.equals(pozoAnterior)) {
                pozoData = new Object[93];
                costoPerfBs = 0.0;
                costoPerfUsd = 0.0;
                costoPerfEquiv = 0.0;
                double locEquv = 0.0;
                costo_total = 0.0;
                dias_totales = 0L;
                pozoAnterior = pozoActual;

                pozoData[0] = perf.getMacollaId().getNombre(); // macolla
                pozoData[1] = pozoActual.getUbicacion(); // localización
                pozoData[2] = pozoActual.getNombre() != null
                        ? pozoActual.getNombre() : "";  // Nombre del pozo

                pozoData[4] = perf.getMacollaId().getCampoId().getNombre(); // campo
                pozoData[5] = pozoActual.getPi();
                pozoData[6] = pozoActual.getRgp();
                pozoData[7] = pozoActual.getDeclinacion();

                switch (perf.getFase()) {
                    case Constantes.FASE_MUDANZA_ENTRE_MACOLLAS:
                        pozoData[8] = perf.getTaladroId().getNombre(); // taladro Mdz
                        pozoData[9] = perf.getFase();   // mdz
                        pozoData[10] = perf.getFechaIn(); // fechaIN Mdz
                        pozoData[11] = perf.getFechaOut(); // fechaOut Mdz
                        pozoData[12] = perf.getDiasActivos().intValue(); // dias Act mdz
                        pozoData[13] = perf.getDiasInactivos().intValue(); // dias inaAct mdz
                        pozoData[14] = perf.getDias().intValue(); // dias total mdz

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        break;
                    case Constantes.FASE_SUPERFICIAL:
                        pozoData[15] = perf.getTaladroId().getNombre();
                        pozoData[16] = perf.getFase();
                        pozoData[17] = perf.getFechaIn();
                        pozoData[18] = perf.getFechaOut();
                        pozoData[19] = perf.getDiasActivos().intValue();
                        pozoData[20] = perf.getDiasInactivos().intValue();
                        pozoData[21] = perf.getDias().intValue();

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        break;
                }
            } else {
                switch (perf.getFase()) {
                    case Constantes.FASE_SUPERFICIAL:
                        pozoData[15] = perf.getTaladroId().getNombre();
                        pozoData[16] = perf.getFase();
                        pozoData[17] = perf.getFechaIn();
                        pozoData[18] = perf.getFechaOut();
                        pozoData[19] = perf.getDiasActivos().intValue();
                        pozoData[20] = perf.getDiasInactivos().intValue();
                        pozoData[21] = perf.getDias().intValue();

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        break;
                    case Constantes.FASE_SLANT:
                        pozoData[3] = "Slant"; // tipo de pozo

                        pozoData[22] = perf.getTaladroId().getNombre();
                        pozoData[23] = perf.getFase();
                        pozoData[24] = perf.getFechaIn();
                        pozoData[25] = perf.getFechaOut();
                        pozoData[26] = perf.getDiasActivos().intValue();
                        pozoData[27] = perf.getDiasInactivos().intValue();
                        pozoData[28] = perf.getDias().intValue();

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();

                        pozoData[57] = costoPerfBs; // costo total de perforacion
                        pozoData[58] = costoPerfUsd; // costo total de perforacion
                        pozoData[59] = costoPerfEquiv; // costo total de perforacion

                        costoPerfBs = 0.0;
                        costoPerfUsd = 0.0;
                        costoPerfEquiv = 0.0;

                        pozoData[90] = perf.getMacollaId().getCostoLocalizacionBs();
                        pozoData[91] = perf.getMacollaId().getCostoLocalizacionUsd();
                        double locEquv = perf.getMacollaId().getCostoLocalizacionBs()
                                / recalculaParidad(paridadList, (Date) pozoData[17]).getValor()
                                + perf.getMacollaId().getCostoLocalizacionUsd();
                        pozoData[92] = locEquv;

                        mapa.put(pozoActual, pozoData);
                        break;
                    case Constantes.FASE_VERTICAL:
                        pozoData[3] = "Vertical"; // tipo de pozo

                        pozoData[29] = perf.getTaladroId().getNombre();
                        pozoData[30] = perf.getFase();
                        pozoData[31] = perf.getFechaIn();
                        pozoData[32] = perf.getFechaOut();
                        pozoData[33] = perf.getDiasActivos().intValue();
                        pozoData[34] = perf.getDiasInactivos().intValue();
                        pozoData[35] = perf.getDias().intValue();

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();

                        pozoData[57] = costoPerfBs; // costo total de perforacion
                        pozoData[58] = costoPerfUsd; // costo total de perforacion
                        pozoData[59] = costoPerfEquiv; // costo total de perforacion

                        costoPerfBs = 0.0;
                        costoPerfUsd = 0.0;
                        costoPerfEquiv = 0.0;

                        pozoData[90] = perf.getMacollaId().getCostoLocalizacionBs();
                        pozoData[91] = perf.getMacollaId().getCostoLocalizacionUsd();
                        locEquv = perf.getMacollaId().getCostoLocalizacionBs()
                                / recalculaParidad(paridadList, (Date) pozoData[17]).getValor()
                                + perf.getMacollaId().getCostoLocalizacionUsd();
                        pozoData[92] = locEquv;

                        mapa.put(pozoActual, pozoData);
                        break;
                    case Constantes.FASE_PILOTO:
                        pozoData[3] = "Piloto"; // tipo de pozo

                        pozoData[36] = perf.getTaladroId().getNombre();
                        pozoData[37] = perf.getFase();
                        pozoData[38] = perf.getFechaIn();
                        pozoData[39] = perf.getFechaOut();
                        pozoData[40] = perf.getDiasActivos().intValue();
                        pozoData[41] = perf.getDiasInactivos().intValue();
                        pozoData[42] = perf.getDias().intValue();

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();

                        pozoData[57] = costoPerfBs; // costo total de perforacion
                        pozoData[58] = costoPerfUsd; // costo total de perforacion
                        pozoData[59] = costoPerfEquiv; // costo total de perforacion

                        costoPerfBs = 0.0;
                        costoPerfUsd = 0.0;
                        costoPerfEquiv = 0.0;

                        pozoData[90] = perf.getMacollaId().getCostoLocalizacionBs();
                        pozoData[91] = perf.getMacollaId().getCostoLocalizacionUsd();
                        locEquv = perf.getMacollaId().getCostoLocalizacionBs()
                                / recalculaParidad(paridadList, (Date) pozoData[17]).getValor()
                                + perf.getMacollaId().getCostoLocalizacionUsd();
                        pozoData[92] = locEquv;

                        mapa.put(pozoActual, pozoData);
                        break;
                    case Constantes.FASE_INTERMEDIO:
                        pozoData[3] = "Horizontal"; // tipo de pozo

                        pozoData[43] = perf.getTaladroId().getNombre();
                        pozoData[44] = perf.getFase();
                        pozoData[45] = perf.getFechaIn();
                        pozoData[46] = perf.getFechaOut();
                        pozoData[47] = perf.getDiasActivos().intValue();
                        pozoData[48] = perf.getDiasInactivos().intValue();
                        pozoData[49] = perf.getDias().intValue();

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                    case Constantes.FASE_PRODUCTOR:
                        pozoData[50] = perf.getTaladroId().getNombre();
                        pozoData[51] = perf.getFase();
                        pozoData[52] = perf.getFechaIn();
                        pozoData[53] = perf.getFechaOut();
                        pozoData[54] = perf.getDiasActivos().intValue();
                        pozoData[55] = perf.getDiasInactivos().intValue();
                        pozoData[56] = perf.getDias().intValue();

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();

                        pozoData[57] = costoPerfBs; // costo total de perforacion
                        pozoData[58] = costoPerfUsd; // costo total de perforacion
                        pozoData[59] = costoPerfEquiv; // costo total de perforacion

                        costoPerfBs = 0.0;
                        costoPerfUsd = 0.0;
                        costoPerfEquiv = 0.0;
                    case Constantes.FASE_COMPLETACION:
                        pozoData[60] = perf.getTaladroId().getNombre();
                        pozoData[61] = perf.getFase();
                        pozoData[62] = perf.getFechaIn();
                        pozoData[63] = perf.getFechaOut();
                        pozoData[64] = perf.getDiasActivos().intValue();
                        pozoData[65] = perf.getDiasInactivos().intValue();
                        pozoData[66] = perf.getDias().intValue();

                        pozoData[67] = perf.getBs();
                        pozoData[68] = perf.getUsd();
                        pozoData[69] = perf.getEquiv();

                        break;
                    case Constantes.FASE_CONEXION:
                        pozoData[70] = perf.getTaladroId().getNombre();
                        pozoData[71] = perf.getFase();
                        pozoData[72] = perf.getFechaIn();
                        pozoData[73] = perf.getFechaOut();
                        pozoData[74] = perf.getDiasActivos().intValue();
                        pozoData[75] = perf.getDiasInactivos().intValue();
                        pozoData[76] = perf.getDias().intValue();

                        pozoData[77] = perf.getBs();
                        pozoData[78] = perf.getUsd();
                        pozoData[79] = perf.getEquiv();

                        break;
                    case Constantes.FASE_EVALUACION:
                        pozoData[80] = perf.getTaladroId().getNombre();
                        pozoData[81] = perf.getFase();
                        pozoData[82] = perf.getFechaIn();
                        pozoData[83] = perf.getFechaOut();
                        pozoData[84] = perf.getDiasActivos().intValue();
                        pozoData[85] = perf.getDiasInactivos().intValue();
                        pozoData[86] = perf.getDias().intValue();

                        pozoData[87] = perf.getBs();
                        pozoData[88] = perf.getUsd();
                        pozoData[89] = perf.getEquiv();

                        pozoData[90] = perf.getMacollaId().getCostoLocalizacionBs();
                        pozoData[91] = perf.getMacollaId().getCostoLocalizacionUsd();
                        locEquv = perf.getMacollaId().getCostoLocalizacionBs()
                                / recalculaParidad(paridadList, (Date) pozoData[17]).getValor()
                                + perf.getMacollaId().getCostoLocalizacionUsd();
                        pozoData[92] = locEquv;

                        mapa.put(pozoActual, pozoData);
                        break;
                }

            }
        }
        return mapa;
    }

    public Map<Pozo, Object[]> prepareDataPerforacionXExcel(List<Perforacion> lista, String moneda) {
        Map<Pozo, Object[]> mapa = new LinkedHashMap<>();
        List<Perforacion> listaSorted = new ArrayList<>();
        // se ordena la lista por taladro, macolla y pozo
//        Comparator<Perforacion> byTaladro = (t1, t2)
//                -> t1.getTaladroId().getId().compareTo(t2.getTaladroId().getId());
        Comparator<Perforacion> byMacolla = (m1, m2)
                -> m1.getMacollaId().getId().compareTo(m2.getMacollaId().getId());
        Comparator<Perforacion> byPozo = (p1, p2)
                -> p1.getPozoId().compareTo(p2.getPozoId());

        lista.stream()
                .sorted(byMacolla.thenComparing(byPozo))
                .forEach(p -> listaSorted.add(p));

        List<Paridad> paridadList = paridadManager.findAll();

        Pozo pozoActual = null;
        Pozo pozoAnterior = null;
        double costo_perforacion = 0.0;
        double costo_total = 0.0;
        long diasPerf = 0L;
        long dias_totales = 0L;
        LocalDate ldIn;
        LocalDate ldOut;
        Paridad paridad;
        Object[] pozoData = null;
        for (Perforacion perf : listaSorted) {
            pozoActual = perf.getPozoId();
            if (!pozoActual.equals(pozoAnterior)) {
                pozoData = new Object[39];
                costo_perforacion = 0.0;
                costo_total = 0.0;
                dias_totales = 0L;
                pozoAnterior = pozoActual;
                pozoData[0] = perf.getTaladroId();
                pozoData[1] = perf.getPozoId().getUbicacion();
                pozoData[2] = perf.getMacollaId();
                pozoData[3] = perf.getPozoId();
                //pozoData[4] = "Horizontal"; // Aqui debe ir el tipo de pozo, pero no se guarda
                switch(perf.getFase()){
                    case Constantes.FASE_PILOTO:
                    case Constantes.FASE_SLANT:    
                    case Constantes.FASE_VERTICAL:
                        pozoData[4] = "Vertical";
                    default:
                        pozoData[4] = "Horizontal";
                }
                pozoData[5] = perf.getMacollaId().getCampoId();
                pozoData[6] = perf.getPozoId().getPi();
                pozoData[7] = perf.getPozoId().getRgp();
                pozoData[8] = perf.getPozoId().getDeclinacion();
                pozoData[9] = 1 - Math.pow(1 - perf.getPozoId().getDeclinacion(), 1.0 / 12); // Declinación mensual
                pozoData[10] = 1 - Math.pow(1 - perf.getPozoId().getDeclinacion(), 1.0 / 365); // Declinacion diaria
            }

            if (perf.getFase().equals(Constantes.FASE_SUPERFICIAL)) {
                pozoData[16] = perf.getFechaIn(); // Aqui la fecha de inicio superficial
                pozoData[18] = perf.getFechaOut(); // Aqui la fecha de inicio superficial
                paridad = recalculaParidad(paridadList, perf.getFechaIn());
                if(moneda.equals(Constantes.BOLIVARES)){
                    costo_perforacion += perf.getBs();
                } else {
                    costo_perforacion += perf.getUsd();
                }
                //costo_perforacion += perf.getBs() + perf.getUsd() * paridad.getValor();
                if (moneda.equals(Constantes.BOLIVARES)) {
                    pozoData[33] = perf.getBs();
                } else {
                    pozoData[33] = perf.getUsd();
                }
                //pozoData[33] = perf.getBs() + perf.getUsd() * paridad.getValor();
                // calculo de los dias en esta etapa
                ldIn = LocalDateTime.ofInstant(perf.getFechaIn().toInstant(), ZoneId.systemDefault()).toLocalDate();
                ldOut = LocalDateTime.ofInstant(perf.getFechaOut().toInstant(), ZoneId.systemDefault()).toLocalDate();
                pozoData[17] = ChronoUnit.DAYS.between(ldIn, ldOut);  // Calculo de los días
                dias_totales += (Long) pozoData[17];
                // Aqui se calcula costo de localización
                pozoData[12] = perf.getMacollaId().getCostoLocalizacionBs()
                        + perf.getMacollaId().getCostoLocalizacionUsd() * paridad.getValor();
                costo_total += (Double) pozoData[12];
            }
            if (perf.getFase().equals(Constantes.FASE_INTERMEDIO)) {
                pozoData[19] = perf.getFechaIn(); // Aqui la fecha de inicio intermedio
                pozoData[21] = perf.getFechaOut(); // Aqui la fecha de inicio intermedio
                // calculo de los dias en esta etapa
                ldIn = LocalDateTime.ofInstant(perf.getFechaIn().toInstant(), ZoneId.systemDefault()).toLocalDate();
                ldOut = LocalDateTime.ofInstant(perf.getFechaOut().toInstant(), ZoneId.systemDefault()).toLocalDate();
                pozoData[20] = ChronoUnit.DAYS.between(ldIn, ldOut);  // Calculo de los días
                dias_totales += (Long) pozoData[20];
                // Costos
                paridad = recalculaParidad(paridadList, perf.getFechaIn());
                if (moneda.equals(Constantes.BOLIVARES)) {
                    costo_perforacion += perf.getBs();
                } else {
                    costo_perforacion += perf.getUsd();
                }
                //costo_perforacion += perf.getBs() + perf.getUsd() * paridad.getValor();
                if (moneda.equals(Constantes.BOLIVARES)) {
                    pozoData[34] = perf.getBs();
                } else {
                    pozoData[34] = perf.getUsd();
                }
                //pozoData[34] = perf.getBs() + perf.getUsd() * paridad.getValor();
            }
            if (perf.getFase().equals(Constantes.FASE_PRODUCTOR)) {
                pozoData[22] = perf.getFechaIn(); // Aqui la fecha de inicio productor
                pozoData[24] = perf.getFechaOut(); // Aqui la fecha de inicio productor
                ldIn = LocalDateTime.ofInstant(perf.getFechaIn().toInstant(), ZoneId.systemDefault()).toLocalDate();
                ldOut = LocalDateTime.ofInstant(perf.getFechaOut().toInstant(), ZoneId.systemDefault()).toLocalDate();
                pozoData[23] = ChronoUnit.DAYS.between(ldIn, ldOut);  // Calculo de los días
                dias_totales += (Long) pozoData[23];
                // Costos
                paridad = recalculaParidad(paridadList, perf.getFechaIn());
                if (moneda.equals(Constantes.BOLIVARES)) {
                    costo_perforacion += perf.getBs();
                } else {
                    costo_perforacion += perf.getUsd();
                }
                //costo_perforacion += perf.getBs() + perf.getUsd() * paridad.getValor();
                if (moneda.equals(Constantes.BOLIVARES)) {
                    pozoData[35] = perf.getBs();
                } else {
                    pozoData[35] = perf.getUsd();
                }
                //pozoData[35] = perf.getBs() + perf.getUsd() * paridad.getValor();
                costo_total += costo_perforacion;
                pozoData[25] = dias_totales; // aqui va el calculo de los dias totales
            }

            pozoData[11] = costo_perforacion; //costo de perforación total

            if (perf.getFase().equals(Constantes.FASE_COMPLETACION)) {
                paridad = recalculaParidad(paridadList, perf.getFechaIn());
                if (moneda.equals(Constantes.BOLIVARES)) {
                    pozoData[13] = perf.getBs();
                } else {
                    pozoData[13] = perf.getUsd();
                }
                //pozoData[13] = perf.getBs() + perf.getUsd() * paridad.getValor(); // Costo de completación
                costo_total += (Double) pozoData[13];
                pozoData[26] = perf.getFechaIn(); // Aqui debe ir la fecha de inicio completación

                pozoData[28] = perf.getFechaOut(); // Aqui debe ir la fecha de inicio completación
                // calculo de los dias en esta etapa
                ldIn = LocalDateTime.ofInstant(perf.getFechaIn().toInstant(), ZoneId.systemDefault()).toLocalDate();
                ldOut = LocalDateTime.ofInstant(perf.getFechaOut().toInstant(), ZoneId.systemDefault()).toLocalDate();
                pozoData[27] = ChronoUnit.DAYS.between(ldIn, ldOut); // dias para completación
            }
            if (perf.getFase().equals(Constantes.FASE_CONEXION)) {
                paridad = recalculaParidad(paridadList, perf.getFechaIn());
                if (moneda.equals(Constantes.BOLIVARES)) {
                    pozoData[14] = perf.getBs();
                } else {
                    pozoData[14] = perf.getUsd();
                }
                //pozoData[14] = perf.getBs() + perf.getUsd() * paridad.getValor(); // Costo de conexion
                costo_total += (Double) pozoData[14];
                pozoData[29] = perf.getFechaIn(); // Aqui debe ir la fecha de inicio conexión
                pozoData[36] = perf.getFechaOut(); // Fin de conexion
                ldIn = LocalDateTime.ofInstant(perf.getFechaIn().toInstant(), ZoneId.systemDefault()).toLocalDate();
                ldOut = LocalDateTime.ofInstant(perf.getFechaOut().toInstant(), ZoneId.systemDefault()).toLocalDate();
                pozoData[30] = ChronoUnit.DAYS.between(ldIn, ldOut); // dias de conexión
            }
            if (perf.getFase().equals(Constantes.FASE_EVALUACION)) {
                ldIn = LocalDateTime.ofInstant(perf.getFechaIn().toInstant(), ZoneId.systemDefault()).toLocalDate();
                ldOut = LocalDateTime.ofInstant(perf.getFechaOut().toInstant(), ZoneId.systemDefault()).toLocalDate();
                pozoData[37] = perf.getFechaIn(); // inicio de evaluación
                pozoData[38] = perf.getFechaOut(); // fin de evaluación
                pozoData[31] = ChronoUnit.DAYS.between(ldIn, ldOut); // dias de evaluación
                pozoData[32] = perf.getFechaOut(); // ¿Esta es la fecha de aceptación?

                pozoData[15] = costo_total; // costo total
                mapa.put(pozoActual, pozoData);

            }
        }
        return mapa;
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
    
    public Taladro getProduccionTaladro(Pozo pozo, String fase){
        Perforacion perforacion = perforacionManager.getPerforacion(pozo, fase);
        return perforacion.getTaladroId();
    }

    public List<Object[]> getFechaMinMax(Escenario escenario) {
        return perforacionManager.getFechasMinMax(escenario);
        
    }
    
    public SXSSFWorkbook makeExcelPerforacionFile(Map<Pozo, Object[]> mapa){
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);

        String[] titulos1 = {"MACOLLA", "NOMBRE", "POZO", "TIPO DE", "CAMPO",
            "Pi", "RGP", "Decl.", "Decl.", "Decl.",
            "COSTO PERFORACION", "COSTO PERFORACION", "COSTO PERFORACION",
            "LOC.", "LOC.", "LOC.",
            "COMPLETACION", "COMPLETACION", "COMPLETACION",
            "CONEXION", "CONEXION", "CONEXION",
            "COSTO TOTAL", "COSTO TOTAL", "COSTO TOTAL",
            "PERFORACION", "",
            "", "", "", "", "", "", "", "", "", "", "",
            "FECHA INICIO", "DIAS DE", "FECHA FIN", "",
            "", "DIAS", "FECHA"};
        String[] titulos2 = {"", "LOC.", "", "POZO", "", "(BPPD)", "(PCN/BN)",
            "(1/anual)", "(1/mes)", "(1/dia)", "(MBs.)", "(MUS$)", "(MUS$ Eqv.)",
            "(MBs.)", "(MUS$)", "(MUS$ Eqv.)",
            "(MBs.)", "(MUS$)", "(MUS$ Eqv.)",
            "(MBs.)", "(MUS$)", "(MUS$ Eqv.)",
            "(MBs.)", "(MUS$)", "(MUS$ Eqv.)",
            "TAL. SUP.", "INICIO SUP.", "DIAS SUP.", "FIN SUP.",
            "TAL. INTER (o SLANT)", "INICIO. INTER (o SLANT)", "DIAS. INTER (o SLANT)",
            "FIN. INTER (o SLANT)",
            "TAL. PROD.", "INICIO PROD.", "DIAS PROD.", "FIN PROD.",
            "DIAS TOTAL PERF.",
            "COMP.", "COMP.", "COMP.",
            "CONEXION", "DIAS CONEXION",
            "EVAL.", "ACEPTACION"};

        LocalDate currentLd = LocalDate.now(ZoneId.systemDefault());

        SXSSFSheet sheetResumen = (SXSSFSheet) workbook.createSheet("Resumen");
        SXSSFSheet sheetArrastre = (SXSSFSheet) workbook.createSheet("PERFORACION ARRASTRE");
        SXSSFSheet sheetActual = (SXSSFSheet) workbook.createSheet("PERFORACION ACTUAL");

        int rownum = 0;
        int i = 0;
        Row headerRowArrastre = sheetArrastre.createRow(rownum);
        Row headerRowActual = sheetActual.createRow(rownum++);
        for (String title : titulos1) {
            Cell cellAr = headerRowArrastre.createCell(i);
            Cell cellAc = headerRowActual.createCell(i);
            cellAr.setCellValue(titulos1[i]);
            cellAc.setCellValue(titulos1[i]);
            i++;
        }

        i = 0;
        headerRowArrastre = sheetArrastre.createRow(rownum);
        headerRowActual = sheetActual.createRow(rownum++);
        for (String title : titulos2) {
            Cell cellAr = headerRowArrastre.createCell(i);
            Cell cell = headerRowActual.createCell(i);
            cellAr.setCellValue(titulos2[i]);
            cell.setCellValue(titulos2[i]);
            i++;
        }

        int rowActualNumber = rownum;
        int rowArrastreNumber = rownum;
        Row dataArrastreRow;
        Row dataActualRow;
        Cell cell;

        double costoTotalBs = 0.0;
        double costoTotalUsd = 0.0;
        double costoTotalEqv = 0.0;

        for (Map.Entry<Pozo, Object[]> dataMap : mapa.entrySet()) {
            Object[] datos = dataMap.getValue();
            if (datos[17] == null || datos[45] == null) {
                continue;
            }
            Date fechaInicio = (Date) datos[17];
            Instant instant = fechaInicio.toInstant();
            LocalDate inicioLd = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();

            if (currentLd.getYear() > inicioLd.getYear()) {
                // pozos arrastre
                dataArrastreRow = sheetArrastre.createRow(rowArrastreNumber++);
                int cellNum = 0;

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((String) datos[0]); // Macolla

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((String) datos[1]); // localización

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((String) datos[2]); // nombre pozo

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((String) datos[3]); // Tipo pozo

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((String) datos[4]); // Campo

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[5]); // Pi

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[6]); // rgp

                double declAnual = (Double) datos[7] * 100;
                double declMensual = 1 - Math.pow(1 - declAnual, 1 / 12);
                double declDiaria = 1 - Math.pow(1 - declAnual, 1 / 365);
                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue(declAnual); // decl anual

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue(declMensual); // decl mensual

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue(declDiaria); // decl diaria

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[57] / 1000); // Costo Perf Bs

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[58] / 1000); // Costo Perf USd

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[59] / 1000); // Costo Perf Eqv

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[90] / 1000); // Costo Loc Bs

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[91] / 1000); // Costo Loc USd

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[92] / 1000); // Costo Loc Eqv

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[67] / 1000); // Costo Compl Bs

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[68] / 1000); // Costo Compl USd

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[69] / 1000); // Costo Comp Eqv

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[77] / 1000); // Costo Conx Bs

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[78] / 1000); // Costo Conx USd

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[79] / 1000); // Costo Conx Eqv

                costoTotalBs = (Double) datos[57] / 1000
                        + (Double) datos[90] / 1000
                        + (Double) datos[67] / 1000
                        + (Double) datos[77] / 1000;

                costoTotalUsd = (Double) datos[58] / 1000
                        + (Double) datos[91] / 1000
                        + (Double) datos[68] / 1000
                        + (Double) datos[78] / 1000;

                costoTotalEqv = (Double) datos[59] / 1000
                        + (Double) datos[92] / 1000
                        + (Double) datos[69] / 1000
                        + (Double) datos[79] / 1000;

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue(costoTotalBs); // Costo total Bs

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue(costoTotalUsd); // Costo total USd

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue(costoTotalEqv); // Costo total Eqv

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((String) datos[15]); // Tal Supf

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((String) dateFormat.format(datos[17])); // inicio Supf

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((Integer) datos[21]); // dias Supf

                cell = dataArrastreRow.createCell(cellNum++);
                cell.setCellValue((String) dateFormat.format(datos[18])); // fin Supf

                if (datos[22] == null) {
                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) datos[43]); // Tal int

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[45])); // inicio int

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((Integer) datos[49]); // dias int

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[46])); // fin int

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) datos[50]); // Tal prd

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[52])); // inicio prd

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((Integer) datos[56]); // dias prd

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[53])); // fin prd

                    Integer diasPerf = (Integer) datos[21] + (Integer) datos[49]
                            + (Integer) datos[56];
                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue(diasPerf); // dias total perf

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[62])); // ini complt

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((Integer) datos[66]); // dias complt

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[63])); // fin complt

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[72])); // ini conx

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((Integer) datos[76]); // dias conx

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((Integer) datos[86]); // dias eval

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[83])); // aceptacion

                } else {
                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) datos[22]); // Tal slant

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[24])); // inicio slant

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((Integer) datos[28]); // dias slant

                    cell = dataArrastreRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[25])); // fin slant

                    Integer diasPerf = (Integer) datos[21] + (Integer) datos[28];
                    cell = dataArrastreRow.createCell(37);
                    cell.setCellValue(diasPerf); // dias total perf
                }

            } else {
                dataActualRow = sheetActual.createRow(rowActualNumber++);
                int cellNum = 0;

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((String) datos[0]); // Macolla

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((String) datos[1]); // localizacion

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((String) datos[2]); // Nombre Pozo

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((String) datos[3]); // Tipo pozo

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((String) datos[4]); // Campo

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[5]); // Pi

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[6]); // rgp

                double declAnual = (Double) datos[7] * 100;
                double declMensual = 1 - Math.pow(1 - declAnual, 1 / 12);
                double declDiaria = 1 - Math.pow(1 - declAnual, 1 / 365);
                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue(declAnual); // decl anual

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue(declMensual); // decl mensual

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue(declDiaria); // decl diaria

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[57] / 1000); // Costo Perf Bs

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[58] / 1000); // Costo Perf USd

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[59] / 1000); // Costo Perf Eqv

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[90] / 1000); // Costo Loc Bs

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[91] / 1000); // Costo Loc USd

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[92] / 1000); // Costo Loc Eqv

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[67] / 1000); // Costo Compl Bs

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[68] / 1000); // Costo Compl USd

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[69] / 1000); // Costo Comp Eqv

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[77] / 1000); // Costo Conx Bs

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[78] / 1000); // Costo Conx USd

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Double) datos[79] / 1000); // Costo Conx Eqv

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue(costoTotalBs); // Costo total Bs

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue(costoTotalUsd); // Costo total USd

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue(costoTotalEqv); // Costo total Eqv

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((String) datos[15]); // Tal Supf

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((String) dateFormat.format(datos[17])); // inicio Supf

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((Integer) datos[21]); // dias Supf

                cell = dataActualRow.createCell(cellNum++);
                cell.setCellValue((String) dateFormat.format(datos[18])); // fin Supf

                if (datos[22] == null) {
                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) datos[43]); // Tal int

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[45])); // inicio int

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((Integer) datos[49]); // dias int

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[46])); // fin int

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) datos[50]); // Tal prd

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[52])); // inicio prd

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((Integer) datos[56]); // dias prd

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[53])); // fin prd

                    Integer diasPerf = (Integer) datos[21] + (Integer) datos[49]
                            + (Integer) datos[56];
                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue(diasPerf); // dias total perf 

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[62])); // ini complt

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((Integer) datos[66]); // dias complt

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[63])); // fin complt

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[72])); // ini conx

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((Integer) datos[76]); // dias conx

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((Integer) datos[86]); // dias eval

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[83])); // aceptacion

                } else {
                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) datos[22]); // Tal slant

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[24])); // inicio slant

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((Integer) datos[28]); // dias slant

                    cell = dataActualRow.createCell(cellNum++);
                    cell.setCellValue((String) dateFormat.format(datos[25])); // fin slant

                    Integer diasPerf = (Integer) datos[21] + (Integer) datos[28];
                    cell = dataActualRow.createCell(37);
                    cell.setCellValue(diasPerf); // dias total perf
                }
            }
        }

        return workbook;
    }
}
