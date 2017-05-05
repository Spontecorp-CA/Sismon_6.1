package com.sismon.jpamanager;

import com.sismon.model.Division;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class DivisionManager extends AbstractFacade<Division>{
    
    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public DivisionManager() {
        super(Division.class);
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

    public Division find(String nombre) {
        Division division = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT d FROM Division d WHERE d.nombre =:nombre");
            q.setParameter("nombre", nombre);
            division = (Division) q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error..no se encontra la Division: {0}, {1}", new Object[]{nombre, e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return division;
    }
}
