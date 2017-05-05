package com.sismon.test;

import com.sismon.jpamanager.JPAUtilities;
import com.sismon.jpamanager.PerforacionManager;
import javax.persistence.EntityManager;

/**
 *
 * @author jgcastillo
 */
public class QueryEvaluator {
    public static void main(String[] args) {
        
    }
    
    private EntityManager getEntityManager() {
        return JPAUtilities.getEMF().createEntityManager();
    }
    
    private void testQuery(int pozo){
        EntityManager em = getEntityManager();
        try {
            String query = "SELECT perf FROM Perforacion perf WHERE perf.pozoId = pozo";
        } catch (Exception e) {
        }
    }
}
