
package com.sismon.jpamanager;

import com.sismon.model.Taladro;
import com.sismon.model.TaladroMant;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class TaladroMantManager extends AbstractFacade<TaladroMant>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public TaladroMantManager() {
        super(TaladroMant.class);
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
    
    public List<TaladroMant> findAll(Taladro taladro){
        List<TaladroMant> lista = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT m FROM TaladroMant m WHERE m.taladroId = :taladro "
                    + "ORDER BY m.fecha");
            q.setParameter("taladro", taladro);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if(em != null){
                em.close();
            }
        }
        return lista;
    }
    
    public void remove(Taladro taladro) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM TaladroMant p WHERE p.taladroId = :taladro");
            q.setParameter("taladro", taladro);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando el TaladroMant del taladro {0}. {1}",
                    new Object[]{taladro.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
