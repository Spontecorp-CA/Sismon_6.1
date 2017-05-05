package com.sismon.jpamanager;

import com.sismon.model.MacollaExplotada;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class MacollaExplotadaManager extends AbstractFacade<MacollaExplotada> {

    private static final SismonLog sismonlog = SismonLog.getInstance();

    public MacollaExplotadaManager() {
        super(MacollaExplotada.class);
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
    
    public void removeAll() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM MacollaExplotada ex");
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error borrando la tabla de produccion", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
