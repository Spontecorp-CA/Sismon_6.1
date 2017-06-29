package com.sismon.vista.controller;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.ExplotacionManager;
import com.sismon.jpamanager.MacollaExplotadaManager;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.jpamanager.PozoExplotadoManager;
import com.sismon.jpamanager.RampeoManager;
import com.sismon.model.Escenario;
import com.sismon.model.Explotacion;
import com.sismon.model.Macolla;
import com.sismon.model.MacollaExplotada;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.model.PozoExplotado;
import com.sismon.model.Rampeo;
import com.sismon.vista.utilities.SismonLog;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

public class ExplotacionEscenarioController {

    private Escenario escenarioSelected;
    private int indiceProduccion = 1;

    private List<Explotacion> produccionList;
    private final PerforacionManager perforacionManager;
    private final RampeoManager rampeoManager;
    private final ExplotacionManager explotacionManager;
    private final MacollaExplotadaManager macollaExplotadaManager;
    private final PozoExplotadoManager pozoExplotadoManager;
    private static final SismonLog sismonlog = SismonLog.getInstance();

    public ExplotacionEscenarioController(Escenario escenarioSelected,
            Map<Integer, Object[]> produccionMap) {

        this.escenarioSelected = escenarioSelected;

        this.perforacionManager = new PerforacionManager();
        this.explotacionManager = new ExplotacionManager();
        this.rampeoManager = new RampeoManager();
        this.macollaExplotadaManager = new MacollaExplotadaManager();
        this.pozoExplotadoManager = new PozoExplotadoManager();
        this.produccionList = new ArrayList<>();
    }

    private Map<Integer, Object[]> makeEstrategiaPerforacionMap() {
        List<Perforacion> perforacionList = perforacionManager.findAll(escenarioSelected);
        Map<Integer, Object[]> estrategiaPerforacionMap = new TreeMap<>();

        int i = 1;
        for (Perforacion perf : perforacionList) {
            Object[] data = new Object[11];
            data[0] = perf.getTaladroId();
            data[1] = perf.getMacollaId();
            data[2] = perf.getFilaId();
            data[3] = perf.getPozoId();
            data[4] = perf.getFase();
            data[5] = perf.getFechaIn();
            data[6] = perf.getFechaOut();
            data[7] = perf.getBs();
            data[8] = perf.getUsd();
            data[9] = perf.getEquiv();
            data[10] = perf.getEscenarioId();
            estrategiaPerforacionMap.put(i++, data);
        }
        return estrategiaPerforacionMap;
    }

    /**
     * Método que prepara la generación de la producción de los pozos en cada
     * fila recibe el parámetro year que indica los años que se va a producir.
     * El parámetro todos, indica si se va a estudiar el primer pozo de la
     * lista(false) o todos (true) los contenidos en estudio. El parámetro
     * hiperbóloco indica si se hará el estudio con la formula
     * exponencial(false) o la hiperbólica (true). El valor double de b, indica
     * el factor del exponente hiperbólico a usar.
     *
     * @param years
     * @param todos
     * @param hiperbolico
     * @param fechaArranque
     * @return
     */
    public void generarProduccion(double years, boolean todos,
            boolean hiperbolico, Date fechaArranque) {
        Map<Integer, Object[]> estrategiaPerforacionMap = makeEstrategiaPerforacionMap();
        try {
            Map<Integer, Object[]> auxiliarMap = new TreeMap<>();
            int secuencia = 1;

            // crea un mapa del comienzo de la fase de evaluación de cada pozo
            for (Map.Entry<Integer, Object[]> mapa : estrategiaPerforacionMap.entrySet()) {
                Object[] elementos = mapa.getValue();
                for (Object item : elementos) {
                    if (item instanceof String) {
                        String fase = (String) item;
                        if (fase.equals(Constantes.FASE_EVALUACION)) {
                            auxiliarMap.put(secuencia++, elementos);
                        }
                    }
                }
            }

            // obtiene los pozos productores ordenados por fecha
            Map<Integer, Object[]> produccionSortedMap = sortByDate(auxiliarMap);

            // Inicio de explotación
            Object[] datos = produccionSortedMap.get(1);
            Date fechaInicio = (Date) datos[5];//null;
            Object[] data = null;

            // Esto va dentro del loop cuando se procese todos los pozos
            LocalDate ldArranque = LocalDateTime.ofInstant(fechaInicio.toInstant(), ZoneId.systemDefault()).toLocalDate();
            int newYear = ldArranque.getYear() + (int) years;
            LocalDate ldFin = LocalDate.of(newYear, Month.DECEMBER, 31);

            // se limpia la tabla de explotación
            macollaExplotadaManager.removeAll();
            pozoExplotadoManager.removeAll();
            explotacionManager.removeAll();
            Set<PozoExplotado> pozoExpSet = new HashSet<>();
            Set<MacollaExplotada> macollaExpSet = new HashSet<>();

            for (Map.Entry<Integer, Object[]> secuenciaMap : produccionSortedMap.entrySet()) {
                data = secuenciaMap.getValue();

                LocalDate ldEvaluacion = LocalDateTime
                        .ofInstant(((Date) data[5]).toInstant(), ZoneId.systemDefault())
                        .toLocalDate();
                if (ldEvaluacion.isBefore(ldFin)) {
                    if (fechaArranque == null) {
                        explotaPozo2(data, ldFin, hiperbolico, (long) years);
                    } else {
                        explotaPozo(data, hiperbolico, fechaArranque, (long) years);
                    }

                    MacollaExplotada mcExp = new MacollaExplotada();
                    PozoExplotado pzExp = new PozoExplotado();
                    mcExp.setMacollaId((Macolla) data[1]);
                    pzExp.setPozoId((Pozo) data[3]);
                    macollaExpSet.add(mcExp);
                    pozoExpSet.add(pzExp);
                }
            }

            macollaExplotadaManager.batchSave(macollaExpSet);
            pozoExplotadoManager.batchSave(pozoExpSet);
            explotacionManager.batchSave(produccionList);
            produccionList = null;
        } catch (Exception e) {
            sismonlog.logger.log(Level.WARNING, null, e);
        }
    }

    /**
     * Método para ordenar el mapa de explotación por fecha de pozo después de
     * la fase de aceptación. Basado en:
     * http://stackoverflow.com/questions/2864840/treemap-sort-by-value y
     * http://www.mkyong.com/java/how-to-sort-a-map-in-java/ adaptado por
     * JGCastillo
     *
     * @param <K>
     * @param <V>
     * @param map
     * @return
     */
    private static Map<Integer, Object[]> sortByDate(Map<Integer, Object[]> unsortedMap) {
        // Se convierte el mapa en una List
        List<Map.Entry<Integer, Object[]>> list = new LinkedList<>(unsortedMap.entrySet());

        // Se ordena la List con un comparator, basado en los valores del mapa
        Collections.sort(list,
                (Map.Entry<Integer, Object[]> o1, Map.Entry<Integer, Object[]> o2)
                -> ((Date) o1.getValue()[5]).compareTo((Date) o2.getValue()[5]));

        // Se convierte la lista ordenada en un mapa para retornarlo
        Map<Integer, Object[]> sortedMap = new LinkedHashMap<>();
        int i = 1;
        for (Map.Entry<Integer, Object[]> entry : list) {
            //sortedMap.put(entry.getKey(), entry.getValue());
            sortedMap.put(i++, entry.getValue());
        }
        return sortedMap;
    }

    /**
     * realiza la explotación, día a día, de un pozo que es pasado como
     * parámetro
     *
     * @param datos
     * @param arranque
     * @param fin
     * @param hiperbolico
     * @param b
     */
    private void explotaPozo(Object[] datos, LocalDate ldFin, boolean hiperbolico,
            long years) {

        try {
            Pozo pozo = (Pozo) datos[3];
            Date fechaInRampeo = (Date) datos[5];
            Date fechaAceptacion = (Date) datos[6];

            // Fase de Rampeo
            List<Rampeo> rampTemp = rampeoManager.findAll(pozo, escenarioSelected);
            List<Rampeo> rampeos = new ArrayList<>();

            rampTemp.stream().filter((ramp) -> (ramp.getDias() != 0)).forEach((ramp) -> {
                rampeos.add(ramp);
            });

            Collections.sort(rampeos, (Rampeo o1, Rampeo o2)
                    -> o1.getNumero().compareTo(o2.getNumero()));

            double maxRpm = (rampeos.get(rampeos.size() - 1)).getRpm();

            double pi = pozo.getPi();
            double decl = pozo.getDeclinacion();
            int diasDecl = pozo.getInicioDecl();
            double b = pozo.getExpHiperb();
            double rgp = pozo.getRgp();
            int rgpDecl = pozo.getInicioDeclRgp();
            double rgpIncrAnual = pozo.getIncremAnualRgp();
            double ays = pozo.getAys();
            int aysDecl = pozo.getInicioDeclAys();
            double aysIncrAnual = pozo.getIncremAnualAys();

            double gradoApiXP = pozo.getGradoApiXp();
            double gradoApiDiluente = pozo.getGradoApiDiluente();
            double gradoApiMezcla = pozo.getGradoApiMezcla();

            double prodDiariaRampeo = 0.0;
            double prodAcumuladaRampeo = 0.0;
            double prodPromedioRampeo = 0.0;

            double prodDiariaGasRampeo = 0.0;
            double prodAcumuladaGasRampeo = 0.0;
            double prodPromedioGasRampeo = 0.0;

            double prodDiariaAySRampeo = 0.0;
            double prodAcumuladaAySRampeo = 0.0;
            double prodPromedioAySRampeo = 0.0;

            double factorDiluente = ((141.5 / (131.5 + gradoApiXP)) - (141.5 / (131.5 + gradoApiMezcla)))
                    / ((141.5 / (131.5 + gradoApiMezcla)) - (141.5 / (131.5 + gradoApiDiluente)));
            double prodDiariaDiluenteRampeo = 0.0;
            double prodAcumuladaDiluenteRampeo = 0.0;
            double prodPromedioDiluenteRampeo = 0.0;

            int numDias = 1;
            Instant instProduccion = fechaInRampeo.toInstant();
            LocalDate ldProduccion = LocalDateTime
                    .ofInstant(fechaInRampeo.toInstant(),
                            ZoneId.systemDefault()).toLocalDate();
            //LocalDate.ofEpochDay(fechaInRampeo.getTime() / (1000 * 3600 * 24));

            prodDiariaRampeo = pi;
            for (Rampeo rampa : rampeos) {
                double rpm = rampa.getRpm();
                double dias = rampa.getDias();
                for (int i = 0; i < dias; i++) {

                    // Petróleo
                    if (diasDecl == 0) {
                        if (!hiperbolico) {
                            prodDiariaRampeo = prodDiariaRampeo * Math.exp(-decl / 365) * rpm / maxRpm;
                        } else {
                            prodDiariaRampeo = prodDiariaRampeo * Math.pow((1 + (b * decl / 365)), (-1 / b)) * rpm / maxRpm;
                        }
                    } else {
                        prodDiariaRampeo = pi * rpm / maxRpm;
                    }
                    prodAcumuladaRampeo += prodDiariaRampeo;

                    // Gas
                    if (rgpDecl == 0) {
                        prodDiariaGasRampeo = (prodDiariaRampeo * rgp / 1000) * (1 + (rgpIncrAnual / 365));
                    } else {
                        prodDiariaGasRampeo = prodDiariaRampeo * rgp / 1000;
                    }
                    prodAcumuladaGasRampeo += prodDiariaGasRampeo;

                    // Agua y Sedimento
                    if (aysDecl == 0) {
                        prodDiariaAySRampeo = (prodDiariaRampeo / (1 - (ays + aysIncrAnual / 365)));
                    } else {
                        prodDiariaAySRampeo = prodDiariaRampeo / (1 - ays);
                    }
                    prodAcumuladaAySRampeo += prodDiariaAySRampeo;

                    // Diluente
                    prodDiariaDiluenteRampeo = prodDiariaRampeo * factorDiluente;
                    prodAcumuladaDiluenteRampeo += prodDiariaDiluenteRampeo;

                    if (years <= 5L) {
                        Explotacion explt = new Explotacion();
                        Date fecha = Date.from(ldProduccion.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        explt.setFecha(fecha);
                        explt.setPozoId(pozo);
                        explt.setProdDiaria(prodDiariaRampeo);
                        explt.setProdAcum(prodAcumuladaRampeo);
                        explt.setProdGas(prodDiariaGasRampeo);
                        explt.setProdGasAcum(prodAcumuladaGasRampeo);
                        explt.setProdAyS(prodDiariaAySRampeo);
                        explt.setProdAySAcum(prodAcumuladaAySRampeo);
                        explt.setProdDlnt(prodDiariaDiluenteRampeo);
                        explt.setProdDlntAcum(prodAcumuladaDiluenteRampeo);
                        produccionList.add(explt);
                    }
//                    instProduccion = instProduccion.plusSeconds(3600 * 24);
                    ldProduccion = ldProduccion.plusDays(1);
                    numDias++;
                }
            }

            if (years > 5L) {
                prodPromedioRampeo = prodAcumuladaRampeo / numDias;
                prodPromedioGasRampeo = prodAcumuladaGasRampeo / numDias;
                prodPromedioAySRampeo = prodAcumuladaAySRampeo / numDias;
                prodPromedioDiluenteRampeo = prodAcumuladaDiluenteRampeo / numDias;

                Explotacion explt = new Explotacion();
                Date fecha = Date.from(ldProduccion.atStartOfDay(ZoneId.systemDefault()).toInstant());
                explt.setFecha(fecha);
                explt.setPozoId(pozo);
                explt.setProdDiaria(prodPromedioRampeo);
                explt.setProdAcum(prodAcumuladaRampeo);
                explt.setProdGas(prodPromedioGasRampeo);
                explt.setProdGasAcum(prodAcumuladaGasRampeo);
                explt.setProdAyS(prodPromedioAySRampeo);
                explt.setProdAySAcum(prodAcumuladaAySRampeo);
                explt.setProdDlnt(prodPromedioDiluenteRampeo);
                explt.setProdDlntAcum(prodAcumuladaDiluenteRampeo);
                produccionList.add(explt);
            }

            // Arranque de producción después de la aceptación
            double prodDiaria = prodDiariaRampeo;
            double prodAcumuladaTotal = prodAcumuladaRampeo;
            double prodAcumulada = 0.0;

            double prodDiariaGas = prodDiariaGasRampeo;
            double prodAcumuladaGasTotal = prodAcumuladaGasRampeo;
            double prodAcumuladaGas = 0.0;

            double prodDiariaAyS = prodDiariaAySRampeo;
            double prodAcumuladaAySTotal = prodAcumuladaAySRampeo;
            double prodAcumuladaAyS = 0.0;

            double prodDiariaDlnt = prodDiariaDiluenteRampeo;
            double prodAcumuladaDlntTotal = prodAcumuladaDiluenteRampeo;
            double prodAcumuladaDiluente = 0.0;

            int dias;

            LocalDateTime arranqueProd = LocalDateTime.ofInstant(
                    fechaInRampeo.toInstant(), ZoneId.systemDefault());

            LocalDateTime ldtProd = LocalDateTime.ofInstant(
                    instProduccion, ZoneId.systemDefault());

            if (arranqueProd.getMonthValue() == ldtProd.getMonthValue()) {
                dias = numDias;
                prodAcumulada = prodAcumuladaRampeo;
            } else {
                dias = 1;
            }

//            LocalDate ldProd = null;
            Month mes;// = null;
            int finDeMes = 0;

            while (ldProduccion.isBefore(ldFin) || ldProduccion.isEqual(ldFin)) {
                // calcula producción diaria y acumula
                // Petróleo
                if (numDias >= pozo.getInicioDecl()) {
                    if (!hiperbolico) {
                        prodDiaria = prodDiaria * Math.exp(-decl / 365);
                    } else {
                        prodDiaria = prodDiaria * Math.pow((1 + (b * decl / 365)), (-1 / b));
                    }
                }
                // Gas
                if (numDias >= pozo.getInicioDeclRgp()) {
                    prodDiariaGas = (prodDiaria * rgp / 1000) * (1 + (rgpIncrAnual / 365));
                }
                // AyS
                if (numDias >= pozo.getInicioDeclAys()) {
                    prodDiariaAyS = (prodDiaria / (1 - (ays + aysIncrAnual / 365)));
                }
                // Diluente
                prodDiariaDlnt = prodDiaria * factorDiluente;

                // Petróleo
                prodAcumulada += prodDiaria;
                prodAcumuladaTotal += prodDiaria;
                // Gas
                prodAcumuladaGas += prodDiariaGas;
                prodAcumuladaGasTotal += prodDiariaGas;
                // A y S
                prodAcumuladaAyS += prodDiariaAyS;
                prodAcumuladaAySTotal += prodDiariaAyS;
                // Diluente
                prodAcumuladaDiluente += prodDiariaDlnt;
                prodAcumuladaDlntTotal += prodDiariaDlnt;

                if (years <= 5L) {
                    Explotacion explt = new Explotacion();
                    Date fecha = Date.from(ldProduccion.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    explt.setFecha(fecha);
                    explt.setPozoId(pozo);
                    explt.setProdDiaria(prodDiaria);
                    explt.setProdAcum(prodAcumuladaTotal);
                    explt.setProdGas(prodDiariaGas);
                    explt.setProdGasAcum(prodAcumuladaGasTotal);
                    explt.setProdAyS(prodDiariaAyS);
                    explt.setProdAySAcum(prodAcumuladaAySTotal);
                    explt.setProdDlnt(prodDiariaDlnt);
                    explt.setProdDlntAcum(prodAcumuladaDlntTotal);
                    produccionList.add(explt);
                }

                // verifica fin de mes
                mes = ldProduccion.getMonth();//ldProd.getMonth();
                finDeMes = mes.length(ldProduccion.isLeapYear());

                if (ldProduccion.getDayOfMonth() == finDeMes) {
                    if (years > 5L) {
                        Explotacion explt = new Explotacion();
                        Date fecha = Date.from(ldProduccion.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        explt.setFecha(fecha);
                        explt.setPozoId(pozo);
                        explt.setProdDiaria(prodAcumulada / mes.maxLength());
                        explt.setProdAcum(prodAcumuladaTotal);
                        explt.setProdGas(prodAcumuladaGas / mes.maxLength());
                        explt.setProdGasAcum(prodAcumuladaGasTotal);
                        explt.setProdAyS(prodAcumuladaAyS / mes.maxLength());
                        explt.setProdAySAcum(prodAcumuladaAySTotal);
                        explt.setProdDlnt(prodAcumuladaDiluente / mes.maxLength());
                        explt.setProdDlntAcum(prodAcumuladaDlntTotal);
                        produccionList.add(explt);
                    }
                    prodAcumulada = 0.0;
                    prodAcumuladaGas = 0.0;
                    prodAcumuladaAyS = 0.0;
                    prodAcumuladaDiluente = 0.0;
                    dias = 0;
                }

                if (pozo.getTasaAbandono() > 0 && prodDiaria <= pozo.getTasaAbandono()) {
                    break;
                }

                if (pozo.getReservaMax() > 0 && prodAcumuladaTotal >= pozo.getReservaMax()) {
                    break;
                }

                ldProduccion = ldProduccion.plusDays(1);
                dias++;
                numDias++;
            }

        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        }
    }

    private void explotaPozo(Object[] datos, boolean hiperbolico, Date fechaInicio, long years) {
        Pozo pozo = (Pozo) datos[3];
        Date fechaInRampeo = (Date) datos[5];

        // Fase de Rampeo
        List<Rampeo> rampTemp = rampeoManager.findAll(pozo, escenarioSelected);
        List<Rampeo> rampeos = new ArrayList<>();

        rampTemp.stream().filter((ramp) -> (ramp.getDias() != 0)).forEach((ramp) -> {
            rampeos.add(ramp);
        });

        Collections.sort(rampeos, (Rampeo o1, Rampeo o2)
                -> o1.getNumero().compareTo(o2.getNumero()));

        double maxRpm = (rampeos.get(rampeos.size() - 1)).getRpm();

        double pi = pozo.getPi();
        double decl = pozo.getDeclinacion();
        int diasDecl = pozo.getInicioDecl();
        double b = pozo.getExpHiperb();
        double rgp = pozo.getRgp();
        int rgpDecl = pozo.getInicioDeclRgp();
        double rgpIncrAnual = pozo.getIncremAnualRgp();
        double ays = pozo.getAys();
        int aysDecl = pozo.getInicioDeclAys();
        double aysIncrAnual = pozo.getIncremAnualAys();

        double gradoApiXP = pozo.getGradoApiXp();
        double gradoApiDiluente = pozo.getGradoApiDiluente();
        double gradoApiMezcla = pozo.getGradoApiMezcla();

        double prodDiariaRampeo = 0.0;
        double prodAcumuladaRampeo = 0.0;
        double prodPromedioRampeo = 0.0;

        double prodDiariaGasRampeo = 0.0;
        double prodAcumuladaGasRampeo = 0.0;
        double prodPromedioGasRampeo = 0.0;

        double prodDiariaAySRampeo = 0.0;
        double prodAcumuladaAySRampeo = 0.0;
        double prodPromedioAySRampeo = 0.0;

        double factorDiluente = ((141.5 / (131.5 + gradoApiXP)) - (141.5 / (131.5 + gradoApiMezcla)))
                / ((141.5 / (131.5 + gradoApiMezcla)) - (141.5 / (131.5 + gradoApiDiluente)));
        double prodDiariaDiluenteRampeo = 0.0;
        double prodAcumuladaDiluenteRampeo = 0.0;
        double prodPromedioDiluenteRampeo = 0.0;

        // Se arranca a registrar desde el primer día de rampeo
        Instant instProduccion = fechaInRampeo.toInstant();
        LocalDate ldProduccion = LocalDateTime
                .ofInstant(fechaInRampeo.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
        LocalDate ldInicio = LocalDateTime
                .ofInstant(fechaInicio.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
        int finalYear = ldInicio.getYear() + (int) years;
        LocalDate ldFin = LocalDate.of(finalYear, Month.DECEMBER, 31);

        // Se explota el rampeo
        int numDias = 1;
        prodDiariaRampeo = pi;

        // Hace el rampeo
        for (Rampeo rampa : rampeos) {
            double dias = rampa.getDias();
            double rpm = rampa.getRpm();

            for (int d = 0; d < dias; d++) {
                // Petróleo
                if (diasDecl == 0) {
                    if (!hiperbolico) {
                        prodDiariaRampeo = prodDiariaRampeo * Math.exp(-decl / 365) * rpm / maxRpm;
                    } else {
                        prodDiariaRampeo = prodDiariaRampeo * Math.pow((1 + (b * decl / 365)), (-1 / b)) * rpm / maxRpm;
                    }
                } else {
                    prodDiariaRampeo = pi * rpm / maxRpm;
                }
                prodAcumuladaRampeo += prodDiariaRampeo;

                // Gas
                if (rgpDecl == 0) {
                    prodDiariaGasRampeo = (prodDiariaRampeo * rgp / 1000) * (1 + (rgpIncrAnual / 365));
                } else {
                    prodDiariaGasRampeo = prodDiariaRampeo * rgp / 1000;
                }
                prodAcumuladaGasRampeo += prodDiariaGasRampeo;

                // Agua y Sedimento
                if (aysDecl == 0) {
                    prodDiariaAySRampeo = (prodDiariaRampeo / (1 - (ays + aysIncrAnual / 365)));
                } else {
                    prodDiariaAySRampeo = prodDiariaRampeo / (1 - ays);
                }
                prodAcumuladaAySRampeo += prodDiariaAySRampeo;

                // Diluente
                prodDiariaDiluenteRampeo = prodDiariaRampeo * factorDiluente;
                prodAcumuladaDiluenteRampeo += prodDiariaDiluenteRampeo;

                ldProduccion.plusDays(1);
                numDias++;
            }
        }
        LocalDate ldExplotacion = ldProduccion.plusDays(1);

        // Arranque de producción después de la aceptación
        double prodDiaria = prodDiariaRampeo;
        double prodAcumuladaTotal = prodAcumuladaRampeo;
        double prodAcumulada = 0.0;

        double prodDiariaGas = prodDiariaGasRampeo;
        double prodAcumuladaGasTotal = prodAcumuladaGasRampeo;
        double prodAcumuladaGas = 0.0;

        double prodDiariaAyS = prodDiariaAySRampeo;
        double prodAcumuladaAySTotal = prodAcumuladaAySRampeo;
        double prodAcumuladaAyS = 0.0;

        double prodDiariaDlnt = prodDiariaDiluenteRampeo;
        double prodAcumuladaDlntTotal = prodAcumuladaDiluenteRampeo;
        double prodAcumuladaDiluente = 0.0;

        Month mes;// = null;
        int finDeMes = 0;
        int dias;

        LocalDateTime arranqueProd = LocalDateTime.ofInstant(
                fechaInRampeo.toInstant(), ZoneId.systemDefault());

        LocalDateTime ldtProd = LocalDateTime.ofInstant(
                instProduccion, ZoneId.systemDefault());

        if (arranqueProd.getMonthValue() == ldtProd.getMonthValue()) {
            dias = numDias;
            prodAcumulada = prodAcumuladaRampeo;
        } else {
            dias = 1;
        }

        while (ldProduccion.isBefore(ldFin) || ldProduccion.isEqual(ldFin)) {
            // calcula producción diaria y acumula
            // Petróleo
            if (numDias >= pozo.getInicioDecl()) {
                if (!hiperbolico) {
                    prodDiaria = prodDiaria * Math.exp(-decl / 365);
                } else {
                    prodDiaria = prodDiaria * Math.pow((1 + (b * decl / 365)), (-1 / b));
                }
            }
            // Gas
            if (numDias >= pozo.getInicioDeclRgp()) {
                prodDiariaGas = (prodDiaria * rgp / 1000) * (1 + (rgpIncrAnual / 365));
            }
            // AyS
            if (numDias >= pozo.getInicioDeclAys()) {
                prodDiariaAyS = (prodDiaria / (1 - (ays + aysIncrAnual / 365)));
            }
            // Diluente
            prodDiariaDlnt = prodDiaria * factorDiluente;

            // Petróleo
            prodAcumulada += prodDiaria;
            prodAcumuladaTotal += prodDiaria;
            // Gas
            prodAcumuladaGas += prodDiariaGas;
            prodAcumuladaGasTotal += prodDiariaGas;
            // A y S
            prodAcumuladaAyS += prodDiariaAyS;
            prodAcumuladaAySTotal += prodDiariaAyS;
            // Diluente
            prodAcumuladaDiluente += prodDiariaDlnt;
            prodAcumuladaDlntTotal += prodDiariaDlnt;

            // verifica fin de mes
            mes = ldProduccion.getMonth();//ldProd.getMonth();
            finDeMes = mes.length(ldProduccion.isLeapYear());

            if (ldProduccion.isEqual(ldInicio) || ldProduccion.isAfter(ldInicio)) {
                if (years <= 5L) {
                    Explotacion explt = new Explotacion();
                    Date fecha = Date.from(ldProduccion.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    explt.setFecha(fecha);
                    explt.setPozoId(pozo);
                    explt.setProdDiaria(prodDiaria);
                    explt.setProdAcum(prodAcumuladaTotal);
                    explt.setProdGas(prodDiariaGas);
                    explt.setProdGasAcum(prodAcumuladaGasTotal);
                    explt.setProdAyS(prodDiariaAyS);
                    explt.setProdAySAcum(prodAcumuladaAySTotal);
                    explt.setProdDlnt(prodDiariaDlnt);
                    explt.setProdDlntAcum(prodAcumuladaDlntTotal);
                    produccionList.add(explt);
                } else {
                    if (ldProduccion.getDayOfMonth() == finDeMes) {
                        Explotacion explt = new Explotacion();
                        Date fecha = Date.from(ldProduccion.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        explt.setFecha(fecha);
                        explt.setPozoId(pozo);
                        explt.setProdDiaria(prodAcumulada / mes.maxLength());
                        explt.setProdAcum(prodAcumuladaTotal);
                        explt.setProdGas(prodAcumuladaGas / mes.maxLength());
                        explt.setProdGasAcum(prodAcumuladaGasTotal);
                        explt.setProdAyS(prodAcumuladaAyS / mes.maxLength());
                        explt.setProdAySAcum(prodAcumuladaAySTotal);
                        explt.setProdDlnt(prodAcumuladaDiluente / mes.maxLength());
                        explt.setProdDlntAcum(prodAcumuladaDlntTotal);
                        produccionList.add(explt);
                        prodAcumulada = 0.0;
                        prodAcumuladaGas = 0.0;
                        prodAcumuladaAyS = 0.0;
                        prodAcumuladaDiluente = 0.0;
                        dias = 0;
                    }
                }
            }

//            if (ldProduccion.isEqual(ldInicio) || ldProduccion.isAfter(ldInicio)) {
//                if (ldProduccion.getDayOfMonth() == finDeMes) {
//                    if (years > 5L) {
//                        Explotacion explt = new Explotacion();
//                        Date fecha = Date.from(ldProduccion.atStartOfDay(ZoneId.systemDefault()).toInstant());
//                        explt.setFecha(fecha);
//                        explt.setPozoId(pozo);
//                        explt.setProdDiaria(prodAcumulada / mes.maxLength());
//                        explt.setProdAcum(prodAcumuladaTotal);
//                        explt.setProdGas(prodAcumuladaGas / mes.maxLength());
//                        explt.setProdGasAcum(prodAcumuladaGasTotal);
//                        explt.setProdAyS(prodAcumuladaAyS / mes.maxLength());
//                        explt.setProdAySAcum(prodAcumuladaAySTotal);
//                        explt.setProdDlnt(prodAcumuladaDiluente / mes.maxLength());
//                        explt.setProdDlntAcum(prodAcumuladaDlntTotal);
//                        produccionList.add(explt);
//                    }
//                    prodAcumulada = 0.0;
//                    prodAcumuladaGas = 0.0;
//                    prodAcumuladaAyS = 0.0;
//                    prodAcumuladaDiluente = 0.0;
//                    dias = 0;
//                }
//            }
            if (pozo.getTasaAbandono() > 0 && prodDiaria <= pozo.getTasaAbandono()) {
                break;
            }

            if (pozo.getReservaMax() > 0 && prodAcumuladaTotal >= pozo.getReservaMax()) {
                break;
            }

            ldProduccion = ldProduccion.plusDays(1);
            dias++;
            numDias++;
        }
    }

    /**
     *
     * Definición de explota pozo sin indicar la fecha de inicio de la
     * explotación
     */
    private void explotaPozo2(Object[] datos, LocalDate ldFin, boolean hiperbolico,
            long years) {

        try {
            Pozo pozo = (Pozo) datos[3];

            Date fechaInRampeo = (Date) datos[5];

            // Fase de Rampeo
            List<Rampeo> rampTemp = rampeoManager.findAll(pozo, escenarioSelected);
            List<Rampeo> rampeos = new ArrayList<>();

            rampTemp.stream().filter((ramp) -> (ramp.getDias() != 0)).forEach((ramp) -> {
                rampeos.add(ramp);
            });

            Collections.sort(rampeos, (Rampeo o1, Rampeo o2)
                    -> o1.getNumero().compareTo(o2.getNumero()));

            double maxRpm = (rampeos.get(rampeos.size() - 1)).getRpm();

            double pi = pozo.getPi();
            double decl = pozo.getDeclinacion();
            int diasDecl = pozo.getInicioDecl();
            double b = pozo.getExpHiperb();
            double rgp = pozo.getRgp();
            int rgpDecl = pozo.getInicioDeclRgp();
            double rgpIncrAnual = pozo.getIncremAnualRgp();
            double ays = pozo.getAys();
            int aysDecl = pozo.getInicioDeclAys();
            double aysIncrAnual = pozo.getIncremAnualAys();

            double gradoApiXP = pozo.getGradoApiXp();
            double gradoApiDiluente = pozo.getGradoApiDiluente();
            double gradoApiMezcla = pozo.getGradoApiMezcla();

            double prodDiariaRampeo = 0.0;
            double prodAcumuladaRampeo = 0.0;
            double prodPromedioRampeo = 0.0;

            double prodDiariaGasRampeo = 0.0;
            double prodAcumuladaGasRampeo = 0.0;
            double prodPromedioGasRampeo = 0.0;

            double prodDiariaAySRampeo = 0.0;
            double prodAcumuladaAySRampeo = 0.0;
            double prodPromedioAySRampeo = 0.0;

            double factorDiluente = ((141.5 / (131.5 + gradoApiXP)) - (141.5 / (131.5 + gradoApiMezcla)))
                    / ((141.5 / (131.5 + gradoApiMezcla)) - (141.5 / (131.5 + gradoApiDiluente)));
            double prodDiariaDiluenteRampeo = 0.0;
            double prodAcumuladaDiluenteRampeo = 0.0;
            double prodPromedioDiluenteRampeo = 0.0;

            int numDias = 1;
            Instant instProduccion = fechaInRampeo.toInstant();
            LocalDate ldProduccion = LocalDateTime
                    .ofInstant(fechaInRampeo.toInstant(),
                            ZoneId.systemDefault()).toLocalDate();

            // etapa de rampeo
            prodDiariaRampeo = pi;
            for (Rampeo rampa : rampeos) {
                double rpm = rampa.getRpm();
                double dias = rampa.getDias();
                for (int i = 0; i < dias; i++) {

                    // Petróleo
                    if (diasDecl == 0) {
                        if (!hiperbolico) {
                            prodDiariaRampeo = prodDiariaRampeo * Math.exp(-decl / 365) * rpm / maxRpm;
                        } else {
                            prodDiariaRampeo = prodDiariaRampeo * Math.pow((1 + (b * decl / 365)), (-1 / b)) * rpm / maxRpm;
                        }
                    } else {
                        prodDiariaRampeo = pi * rpm / maxRpm;
                    }
                    prodAcumuladaRampeo += prodDiariaRampeo;

                    // Gas
                    if (rgpDecl == 0) {
                        prodDiariaGasRampeo = (prodDiariaRampeo * rgp / 1000) * (1 + (rgpIncrAnual / 365));
                    } else {
                        prodDiariaGasRampeo = prodDiariaRampeo * rgp / 1000;
                    }
                    prodAcumuladaGasRampeo += prodDiariaGasRampeo;

                    // Agua y Sedimento
                    if (aysDecl == 0) {
                        prodDiariaAySRampeo = (prodDiariaRampeo / (1 - (ays + aysIncrAnual / 365)));
                    } else {
                        prodDiariaAySRampeo = prodDiariaRampeo / (1 - ays);
                    }
                    prodAcumuladaAySRampeo += prodDiariaAySRampeo;

                    // Diluente
                    prodDiariaDiluenteRampeo = prodDiariaRampeo * factorDiluente;
                    prodAcumuladaDiluenteRampeo += prodDiariaDiluenteRampeo;

                    if (years <= 1L) {
                        saveProduccionData(ldProduccion, pozo, prodDiariaRampeo, prodAcumuladaRampeo,
                                prodDiariaGasRampeo, prodAcumuladaGasRampeo,
                                prodDiariaAySRampeo, prodAcumuladaAySRampeo,
                                prodDiariaDiluenteRampeo, prodAcumuladaDiluenteRampeo);
                    } else {
                        if(isEndOfMonth(ldProduccion)){
                            prodPromedioRampeo = prodAcumuladaRampeo;
                            prodPromedioGasRampeo = prodAcumuladaGasRampeo;
                            prodPromedioAySRampeo = prodAcumuladaAySRampeo;
                            prodPromedioDiluenteRampeo = prodAcumuladaDiluenteRampeo;

                            if (isEndOfMonth(ldProduccion)) {
                                saveProduccionData(ldProduccion, pozo, prodPromedioRampeo, prodAcumuladaRampeo,
                                        prodPromedioGasRampeo, prodAcumuladaGasRampeo,
                                        prodPromedioAySRampeo, prodAcumuladaAySRampeo,
                                        prodPromedioDiluenteRampeo, prodAcumuladaDiluenteRampeo);
                                prodAcumuladaRampeo = 0.0;
                                prodAcumuladaGasRampeo = 0.0;
                                prodAcumuladaAySRampeo = 0.0;
                                prodAcumuladaDiluenteRampeo = 0.0;
                            }
                        }
                    }

                    ldProduccion = ldProduccion.plusDays(1);
                    numDias++;
                }
            }

            // Arranque de producción después de finalizado el rampeo
            double prodDiaria = prodDiariaRampeo;
            double prodAcumuladaTotal = prodAcumuladaRampeo;
            double prodAcumulada = 0.0;

            double prodDiariaGas = prodDiariaGasRampeo;
            double prodAcumuladaGasTotal = prodAcumuladaGasRampeo;
            double prodAcumuladaGas = 0.0;

            double prodDiariaAyS = prodDiariaAySRampeo;
            double prodAcumuladaAySTotal = prodAcumuladaAySRampeo;
            double prodAcumuladaAyS = 0.0;

            double prodDiariaDlnt = prodDiariaDiluenteRampeo;
            double prodAcumuladaDlntTotal = prodAcumuladaDiluenteRampeo;
            double prodAcumuladaDiluente = 0.0;

            int dias;
            LocalDateTime arranqueProd = LocalDateTime.ofInstant(
                    fechaInRampeo.toInstant(), ZoneId.systemDefault());

            LocalDateTime ldtProd = LocalDateTime.ofInstant(
                    instProduccion, ZoneId.systemDefault());

            // si no ha terminado el mes se lleva el acumulado de cada parámetro
            if (arranqueProd.getMonthValue() == ldtProd.getMonthValue()) {
                dias = numDias;
                prodAcumulada = prodAcumuladaRampeo;
                prodAcumuladaGas = prodAcumuladaGasRampeo;
                prodAcumuladaAyS = prodAcumuladaAySRampeo;
                prodAcumuladaDiluente = prodAcumuladaDiluenteRampeo;
            } else {
                dias = 1;
            }

            Month mes;// = null;
            int finDeMes = 0;

            while (ldProduccion.isBefore(ldFin) || ldProduccion.isEqual(ldFin)) {
                // calcula producción diaria y acumula
                // Petróleo
                
                if (numDias >= pozo.getInicioDecl()) {
                    if (!hiperbolico) {
                        prodDiaria = prodDiaria * Math.exp(-decl / 365);
                    } else {
                        prodDiaria = prodDiaria * Math.pow((1 + (b * decl / 365)), (-1 / b));
                    }
                }
                // Gas
                if (numDias >= pozo.getInicioDeclRgp()) {
                    prodDiariaGas = (prodDiaria * rgp / 1000) * (1 + (rgpIncrAnual / 365));
                }
                // AyS
                if (numDias >= pozo.getInicioDeclAys()) {
                    prodDiariaAyS = (prodDiaria / (1 - (ays + aysIncrAnual / 365)));
                }
                // Diluente
                prodDiariaDlnt = prodDiaria * factorDiluente;

                // Petróleo
                prodAcumulada += prodDiaria;
                prodAcumuladaTotal += prodDiaria;
                // Gas
                prodAcumuladaGas += prodDiariaGas;
                prodAcumuladaGasTotal += prodDiariaGas;
                // A y S
                prodAcumuladaAyS += prodDiariaAyS;
                prodAcumuladaAySTotal += prodDiariaAyS;
                // Diluente
                prodAcumuladaDiluente += prodDiariaDlnt;
                prodAcumuladaDlntTotal += prodDiariaDlnt;

                if (years <= 1L) {
                    saveProduccionData(ldProduccion, pozo, prodDiaria, prodAcumuladaTotal,
                            prodDiariaGas, prodAcumuladaGasTotal, prodDiariaAyS,
                            prodAcumuladaAySTotal, prodDiariaDlnt, prodAcumuladaDlntTotal);

                }

                // verifica fin de mes
                mes = ldProduccion.getMonth();//ldProd.getMonth();
                finDeMes = mes.length(ldProduccion.isLeapYear());

                if (ldProduccion.getDayOfMonth() == finDeMes) {
                //if(isEndOfMonth(ldProduccion)){
                    if (years > 1L) {
                        saveProduccionData(ldProduccion, pozo,
                                prodAcumulada, prodAcumuladaTotal,
                                prodAcumuladaGas, prodAcumuladaGasTotal,
                                prodAcumuladaAyS, prodAcumuladaAySTotal,
                                prodAcumuladaDiluente, prodAcumuladaDlntTotal);

                    }
                    prodAcumulada = 0.0;
                    prodAcumuladaGas = 0.0;
                    prodAcumuladaAyS = 0.0;
                    prodAcumuladaDiluente = 0.0;
                    dias = 1;
                }

                if (pozo.getTasaAbandono() > 0 && prodDiaria <= pozo.getTasaAbandono()) {
                    break;
                }

                if (pozo.getReservaMax() > 0 && prodAcumuladaTotal >= pozo.getReservaMax()) {
                    break;
                }

                ldProduccion = ldProduccion.plusDays(1);
                dias++;
                numDias++;
            }

        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        }
    }

    private boolean isEndOfMonth(LocalDate ld) {
        boolean result = false;
        Month mes = ld.getMonth();//ldProd.getMonth();
        int finDeMes = mes.length(ld.isLeapYear());
        if (finDeMes == ld.getDayOfMonth()) {
            result = true;
        }
        return result;
    }

    private void saveProduccionData(LocalDate ldProduccion, Pozo pozo, double prodDiaria,
            double prodAcumuladaTotal, double prodDiariaGas, double prodAcumuladaGasTotal,
            double prodDiariaAyS, double prodAcumuladaAySTotal, double prodDiariaDlnt,
            double prodAcumuladaDlntTotal) {
        Explotacion explt = new Explotacion();
        Date fecha = Date.from(ldProduccion.atStartOfDay(ZoneId.systemDefault()).toInstant());
        explt.setFecha(fecha);
        explt.setPozoId(pozo);
        explt.setProdDiaria(prodDiaria);
        explt.setProdAcum(prodAcumuladaTotal);
        explt.setProdGas(prodDiariaGas);
        explt.setProdGasAcum(prodAcumuladaGasTotal);
        explt.setProdAyS(prodDiariaAyS);
        explt.setProdAySAcum(prodAcumuladaAySTotal);
        explt.setProdDlnt(prodDiariaDlnt);
        explt.setProdDlntAcum(prodAcumuladaDlntTotal);
        produccionList.add(explt);
    }

}
