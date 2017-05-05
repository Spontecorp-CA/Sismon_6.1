package com.sismon.jpamanager;


import com.sismon.model.Distrito;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Casper
 */
public class DistritoManager extends AbstractFacade<Distrito>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public DistritoManager() {
        super(Distrito.class);
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
    
    public Distrito find(String nombre) {
        Distrito distritoEm = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT dem FROM Distrito dem WHERE dem.nombre =:nombre");
            q.setParameter("nombre", nombre);
            distritoEm = (Distrito) q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. no existe el distrito: {0}, {1}", new Object[]{nombre, e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return distritoEm;
    }
}
