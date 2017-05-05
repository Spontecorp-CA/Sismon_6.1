package com.sismon.jpamanager;

import com.sismon.model.Escenario;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroAsignado;
import com.sismon.model.TaladroTraza;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class TaladroTrazaManager extends AbstractFacade<TaladroTraza> {

    private static final SismonLog SISMOLOG = SismonLog.getInstance();

    public TaladroTrazaManager() {
        super(TaladroTraza.class);
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
    
    public List<TaladroTraza> findAll(Escenario escenario) {
        EntityManager em = null;
        List<TaladroTraza> ttList = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT tt FROM TaladroTraza tt "
                    + "WHERE tt.escenarioId = :escenario ");
            q.setParameter("escenario", escenario.getId());
            ttList = q.getResultList();
        } catch (Exception e) {
            SISMOLOG.logger.log(Level.SEVERE, "Error obteniendo la traza para el escenario. {0}",
                    new Object[]{escenario.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return ttList;
    }
    
    public List<TaladroTraza> find(Taladro taladro, Escenario escenario) {
        EntityManager em = null;
        List<TaladroTraza> ttList = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT tt FROM TaladroTraza tt "
                    + "WHERE tt.taladroId = :taladro "
                    + "AND tt.escenarioId = :escenario "
                    + "ORDER BY tt.orden");
            q.setParameter("taladro", taladro.getId());
            q.setParameter("escenario", escenario.getId());
            ttList = q.getResultList();
        } catch (Exception e) {
            SISMOLOG.logger.log(Level.SEVERE, "Error obteniendo la traza del taladro. {0}",
                    new Object[]{taladro.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return ttList;
    }
    
    public TaladroTraza find(TaladroAsignado talAsig, Escenario escenario){
        EntityManager em = null;
        TaladroTraza tt = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT tt FROM TaladroTraza tt "
                    + "WHERE tt.taladroAsignadoDestinoId = :talAsig "
                    + "AND tt.escenarioId = :escenario ");
            q.setParameter("talAsig", talAsig.getId());
            q.setParameter("escenario", escenario.getId());
            tt = (TaladroTraza)q.getSingleResult();
        } catch (Exception e) {
            SISMOLOG.logger.log(Level.SEVERE, "Error obteniendo la traza del taladro. {0}",
                    new Object[]{talAsig.getTaladroId().getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return tt;
    }
    
    public void remove(Escenario escenario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM TaladroTraza tt WHERE tt.escenarioId = :escenarioId");
            q.setParameter("escenarioId", escenario.getId());
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            SISMOLOG.logger.log(Level.SEVERE, "Error eliminando el Taladro del escenario {0}. {1}",
                    new Object[]{escenario.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
