package com.sismon.vista.reportetest;

import com.sismon.model.Escenario;
import com.sismon.model.Perforacion;
import java.util.List;

/**
 *
 * @author jgcastillo
 */
public interface MakePerforacion {

    List<PerforacionReport> makePerforacionData(Escenario escenario);
    void makeExcelReport(List<PerforacionReport> perforaciones);
    
}
