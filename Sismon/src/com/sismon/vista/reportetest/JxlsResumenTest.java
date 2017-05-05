package com.sismon.vista.reportetest;

import com.sismon.jpamanager.PerforacionManager;
import com.sismon.model.Escenario;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;

import com.sismon.vista.controller.ReportesController;
import com.sismon.vista.utilities.SismonLog;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.SwingWorker;

/**
 *
 * @author jgcastillo
 */
public class JxlsResumenTest {
    
    private static final SismonLog LOGGER = SismonLog.getInstance();
    private final ReportesController reportesConstroller;
    private final PerforacionManager perforacionManager;
    private final MakePerforacionReport makePerforacionReport;
    
    public JxlsResumenTest() {
        this.reportesConstroller = new ReportesController();
        this.perforacionManager = new PerforacionManager();
        this.makePerforacionReport = new MakePerforacionReport();
    }

//    private Map<Pozo, Object[]> makePerforacionMap(Escenario escenario){
//        
//        SwingWorker<Map<Pozo, Object[]>, Void> worker = new SwingWorker<Map<Pozo, Object[]>, Void>() {
//            @Override
//            protected Map<Pozo, Object[]> doInBackground() throws Exception {
//                List<Perforacion> perforaciones = perforacionManager.findAll(escenario);
//                Map<Pozo, Object[]> perforacionMap = reportesConstroller
//                        .prepareDataPerforacionXExcel(perforaciones);                
//                return perforacionMap;
//            }
//        };
//        
//        Map<Pozo, Object[]> perforacionMap = null;
//        try {
//            worker.execute();
//            perforacionMap = worker.get();
//        } catch (InterruptedException | ExecutionException e) {
//            LOGGER.logger.log(Level.SEVERE, "Error: ", e);
//        }
//        
//        return perforacionMap;
//    }
    
    private void makeExcelFile(Escenario escenario){
//        List<PerforacionReport> perforaciones = makePerforacionReport.makePerforacionData(escenario);
//
//        try(InputStream is = getClass().getResourceAsStream("/resources/files/PerforacionTotal_template.xls")){
//            try (OutputStream os = new FileOutputStream("Hoja_Operacional.xls")) {
//                Context context = new Context();
//                context.putVar("perforaciones", perforaciones);
//                JxlsHelper.getInstance().processTemplate(is, os, context);
//            }
//        } catch(IOException e){}
    }
    
    public static void main(String[] args) {
        JxlsResumenTest resumen = new JxlsResumenTest();
        Escenario escenario = new Escenario(16);
        resumen.makeExcelFile(escenario);
    }
}
