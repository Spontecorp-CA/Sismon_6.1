
package com.sismon.jpamanager;

import com.sismon.model.Campo;
import com.sismon.model.Macolla;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class MacollaManager extends AbstractFacade<Macolla>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public MacollaManager() {
        super(Macolla.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return JPAUtilities.getEMF().createEntityManager();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().removePropertyChangeListener(listener);
    }  
    
    public Macolla find(String nombre) {
        Macolla macolla = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT m FROM Macolla m WHERE m.nombre =:nombre");
            q.setParameter("nombre", nombre);
            macolla = (Macolla) q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error macolla: {0} no encontrada Error: {1}",
                    new Object[]{nombre, e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return macolla;
    }
    
    public List<Macolla> findAll(Campo campo) {
        List<Macolla> macollaList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT m FROM Macolla m WHERE m.campoId = :campo");
            q.setParameter("campo", campo);
            macollaList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No hay macollas configuradas ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return macollaList;
    }
}
