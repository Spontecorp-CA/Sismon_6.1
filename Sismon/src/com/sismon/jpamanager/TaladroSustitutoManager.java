package com.sismon.jpamanager;

import com.sismon.model.Taladro;
import com.sismon.model.TaladroSustituto;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class TaladroSustitutoManager extends AbstractFacade<TaladroSustituto>{
    private static final SismonLog sismonlog = SismonLog.getInstance();

    public TaladroSustitutoManager() {
        super(TaladroSustituto.class);
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
    
    public List<TaladroSustituto> findAll(Taladro taladro) {
        List<TaladroSustituto> lista = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT m FROM TaladroSustituto m WHERE m.taladroOriginal = :taladro ");
            q.setParameter("taladro", taladro);
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando el TaladroSustituto del taladro {0}. {1}",
                    new Object[]{taladro.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
}
