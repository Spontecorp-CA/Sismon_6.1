package com.sismon.jpamanager;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Casper
 */
public class JPAUtilities {
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("SismonPU");

    public static EntityManagerFactory getEMF() {
        return EMF;
    }

    protected static void closeEMF() {
        if (EMF != null) {
            EMF.close();
        }
    }
}
