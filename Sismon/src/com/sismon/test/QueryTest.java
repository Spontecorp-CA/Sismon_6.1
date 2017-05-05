package com.sismon.test;

import com.sismon.jpamanager.ExplotacionManager;
import com.sismon.model.Explotacion;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class QueryTest {

    public static void main(String[] args) {
        ExplotacionManager explotacionManager = new ExplotacionManager();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat decF = new DecimalFormat("###,###,###,##0.00");
        List<Explotacion> lista = explotacionManager.findAll();

        int i = 0;
        for (Explotacion explt : lista) {
            System.out.printf("Macolla %s Filas %s Pozo %s fecha %s prodAcm %s%n",
                    explt.getPozoId().getMacollaId(), explt.getPozoId().getFilaId(),
                    explt.getPozoId(), df.format(explt.getFecha()),
                    explt.getProdAcum());
            i++;
            if(i > 2500){
                break;
            }
        }
        
    }
}
