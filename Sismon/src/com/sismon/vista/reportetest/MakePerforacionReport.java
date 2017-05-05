package com.sismon.vista.reportetest;

import com.sismon.controller.Constantes;
import com.sismon.jpamanager.PerforacionManager;
import com.sismon.model.Escenario;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.vista.utilities.SismonLog;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jgcastillo
 */
public class MakePerforacionReport implements MakePerforacion {

    private final PerforacionManager perforacionManager;
    private static final SismonLog LOGGER = SismonLog.getInstance();

    public MakePerforacionReport() {
        this.perforacionManager = new PerforacionManager();
    }

    @Override
    public List<PerforacionReport> makePerforacionData(Escenario escenario) {
        List<Perforacion> perforacionList = perforacionManager
                                                .findAllOrderedByPozo(escenario);
        return prepareDataOperacional(perforacionList);
    }

    @Override
    public void makeExcelReport(List<PerforacionReport> perforaciones) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private List<PerforacionReport> prepareDataOperacional(List<Perforacion> perforacionList) {
        List<PerforacionReport> perforaciones = new LinkedList<>();
        Map<Pozo, Object[]> mapaOperacional = new LinkedHashMap<>();

        Pozo pozo;
        Pozo pozoAnterior = null;
        double costoPerfBs = 0.0;
        double costoPerfUsd = 0.0;
        double costoPerfEquiv = 0.0;

        PerforacionReport pr = null;
        for (Perforacion perf : perforacionList) {
            pozo = perf.getPozoId();
            if (!pozo.equals(pozoAnterior)) {
                pr = new PerforacionReport();
                pozoAnterior = pozo;
                
                pr.setMacolla(perf.getMacollaId().getNombre());
                pr.setLocalizacion(pozo.getUbicacion());
                pr.setNombrePozo(pozo.getNombre());
                pr.setTipoPozo("Horizontal");
                pr.setCampo(perf.getMacollaId().getCampoId().getNombre());
                pr.setPi(pozo.getPi());
                pr.setRgp(pozo.getRgp());
                Double declAnual = pozo.getDeclinacion();
                pr.setDeclAnual(declAnual * 100);
                pr.setDeclMensual(1 - Math.pow(1 - declAnual, 1 / 12));
                pr.setDeclDia(1 - Math.pow(1 - declAnual, 1 / 365));
                
                LocalDate ld = LocalDate.now(ZoneId.systemDefault());
                pr.setActualYear(ld.getYear());

                switch (perf.getFase()) {
                    case Constantes.FASE_MUDANZA_ENTRE_MACOLLAS:
                        pr.setTaladroMudanza(perf.getTaladroId().getNombre());
                        pr.setInicioMdz(perf.getFechaIn());
                        pr.setFinMdz(perf.getFechaOut());
                        pr.setDiasMdz(perf.getDias().intValue());
                        pr.setDiasActMdz(perf.getDiasActivos().intValue());
                        pr.setDiasInactMdz(perf.getDiasInactivos().intValue());

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        break;
                    case Constantes.FASE_SUPERFICIAL:
                        pr.setTaladroSuperficial(perf.getTaladroId().getNombre());
                        pr.setInicioSup(perf.getFechaIn());
                        Date fecha = perf.getFechaOut();
                        ld = LocalDateTime.ofInstant(fecha.toInstant(), 
                                ZoneId.systemDefault()).toLocalDate();
                        int year = ld.getYear();
                        pr.setFinSup(fecha);
                        pr.setYearSup(year);
                        pr.setDiasSup(perf.getDias().intValue());
                        pr.setDiasActSup(perf.getDiasActivos().intValue());
                        pr.setDiasInacSup(perf.getDiasInactivos().intValue());

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        break;
                }
            } else {
                switch (perf.getFase()) {
                    case Constantes.FASE_SUPERFICIAL:
                        pr.setTaladroSuperficial(perf.getTaladroId().getNombre());
                        pr.setInicioSup(perf.getFechaIn());
                        pr.setFinSup(perf.getFechaOut());
                        pr.setDiasSup(perf.getDias().intValue());
                        pr.setDiasActSup(perf.getDiasActivos().intValue());
                        pr.setDiasInacSup(perf.getDiasInactivos().intValue());

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        break;
                    case Constantes.FASE_SLANT:
                        pr.setTaladroSlant(perf.getTaladroId().getNombre());
                        pr.setInicioSlant(perf.getFechaIn());
                        pr.setFinSlant(perf.getFechaOut());
                        pr.setDiasSlant(perf.getDias().intValue());
                        pr.setDiasActSlant(perf.getDiasActivos().intValue());
                        pr.setDiasInacSlant(perf.getDiasInactivos().intValue());

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();

                        pr.setCostoBsPerforacion(costoPerfEquiv);
                        pr.setCostoUsdPerforacion(costoPerfUsd);
                        pr.setCostoEqvPerforacion(costoPerfEquiv);

                        costoPerfBs = 0.0;
                        costoPerfUsd = 0.0;
                        costoPerfEquiv = 0.0;
                        perforaciones.add(pr);
                        break;
                    case Constantes.FASE_VERTICAL:
                        pr.setTaladroVert(perf.getTaladroId().getNombre());
                        pr.setInicioVert(perf.getFechaIn());
                        pr.setFinVert(perf.getFechaOut());
                        pr.setDiasVert(perf.getDias().intValue());
                        pr.setDiasActVert(perf.getDiasActivos().intValue());
                        pr.setDiasInacVert(perf.getDiasInactivos().intValue());

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();

                        pr.setCostoBsPerforacion(costoPerfEquiv);
                        pr.setCostoUsdPerforacion(costoPerfUsd);
                        pr.setCostoEqvPerforacion(costoPerfEquiv);

                        costoPerfBs = 0.0;
                        costoPerfUsd = 0.0;
                        costoPerfEquiv = 0.0;
                        perforaciones.add(pr);
                        break;
                    case Constantes.FASE_PILOTO:
                        pr.setTaladroPilt(perf.getTaladroId().getNombre());
                        pr.setInicioPilt(perf.getFechaIn());
                        pr.setFinPilt(perf.getFechaOut());
                        pr.setDiasPilt(perf.getDias().intValue());
                        pr.setDiasActPilt(perf.getDiasActivos().intValue());
                        pr.setDiasInacPilt(perf.getDiasInactivos().intValue());

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();

                        pr.setCostoBsPerforacion(costoPerfEquiv);
                        pr.setCostoUsdPerforacion(costoPerfUsd);
                        pr.setCostoEqvPerforacion(costoPerfEquiv);

                        costoPerfBs = 0.0;
                        costoPerfUsd = 0.0;
                        costoPerfEquiv = 0.0;
                        perforaciones.add(pr);

                        break;
                    case Constantes.FASE_INTERMEDIO:
                        pr.setTaladroIntrm(perf.getTaladroId().getNombre());
                        pr.setInicioIntrm(perf.getFechaIn());
                        pr.setFinIntrm(perf.getFechaOut());
                        pr.setDiasIntrm(perf.getDias().intValue());
                        pr.setDiasActIntrm(perf.getDiasActivos().intValue());
                        pr.setDiasInacIntrm(perf.getDiasInactivos().intValue());

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();
                        break;
                    case Constantes.FASE_PRODUCTOR:
                        pr.setTaladroProd(perf.getTaladroId().getNombre());
                        pr.setInicioProd(perf.getFechaIn());
                        pr.setFinProd(perf.getFechaOut());
                        pr.setDiasProd(perf.getDias().intValue());
                        pr.setDiasActProd(perf.getDiasActivos().intValue());
                        pr.setDiasInacProd(perf.getDiasInactivos().intValue());

                        costoPerfBs += perf.getBs();
                        costoPerfUsd += perf.getUsd();
                        costoPerfEquiv += perf.getEquiv();

                        pr.setCostoBsPerforacion(costoPerfEquiv);
                        pr.setCostoUsdPerforacion(costoPerfUsd);
                        pr.setCostoEqvPerforacion(costoPerfEquiv);

                        costoPerfBs = 0.0;
                        costoPerfUsd = 0.0;
                        costoPerfEquiv = 0.0;
                        //perforaciones.add(pr);
                        break;
                    case Constantes.FASE_COMPLETACION:
                        pr.setInicioCompl(perf.getFechaIn());
                        pr.setFinCompl(perf.getFechaOut());
                        pr.setDiasCompl(perf.getDias().intValue());
                        pr.setCostoBsCompletacion(perf.getBs());
                        pr.setCostoUsdCompletacion(perf.getUsd());
                        pr.setCostoEqvCompletacion(perf.getEquiv());
                        
                        break;
                    case Constantes.FASE_CONEXION:
                        pr.setInicioConex(perf.getFechaIn());
                        pr.setFinConex(perf.getFechaOut());
                        pr.setDiasConex(perf.getDias().intValue());
                        pr.setCostoBsConexion(perf.getBs());
                        pr.setCostoUsdConexion(perf.getUsd());
                        pr.setCostoEqvConexion(perf.getEquiv());
                        break;
                    case Constantes.FASE_EVALUACION:
                        pr.setInicioEval(perf.getFechaIn());
                        pr.setFinEval(perf.getFechaOut());
                        pr.setDiasEval(perf.getDias().intValue());
                        
                        perforaciones.add(pr);
                        break;
                }
            }
        }

        return perforaciones;
    }

}
