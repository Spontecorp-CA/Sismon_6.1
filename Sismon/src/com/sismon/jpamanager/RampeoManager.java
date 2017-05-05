
package com.sismon.jpamanager;

import com.sismon.model.Escenario;
import com.sismon.model.Pozo;
import com.sismon.model.Rampeo;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RampeoManager extends AbstractFacade<Rampeo>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public RampeoManager() {
        super(Rampeo.class);
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
 
    public List<Rampeo> findAll(Pozo pozo){
        List<Rampeo> lista = null;
        try {
            EntityManager em = getEntityManager();
            Query q = em.createQuery("SELECT r FROM Rampeo r WHERE r.pozoId = :pozo "
                    + "AND r.escenarioId IS NULL");
            q.setParameter("pozo", pozo);
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No se encontraron rampeos configurados para este pozo", e);
        }
        return lista;
    }
    
    public List<Rampeo> findAll(Escenario escenario) {
        List<Rampeo> lista = null;
        try {
            EntityManager em = getEntityManager();
            Query q = em.createQuery("SELECT r FROM Rampeo r WHERE "
                    + "r.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No se encontraron rampeos configurados para este pozo", e);
        }
        return lista;
    }
    
    public List<Rampeo> findAll(Pozo pozo, Escenario escenario) {
        List<Rampeo> lista = null;
        try {
            EntityManager em = getEntityManager();
            Query q = em.createQuery("SELECT r FROM Rampeo r WHERE r.pozoId = :pozo "
                    + "AND r.escenarioId = :escenario ORDER BY r.numero");
            q.setParameter("pozo", pozo);
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No se encontraron rampeos para este pozo", e);
        }
        return lista;
    }
    
    public List<Rampeo> findAllBase() {
        List<Rampeo> lista = null;
        try {
            EntityManager em = getEntityManager();
            Query q = em.createQuery("SELECT r FROM Rampeo r "
                    + "WHERE r.escenarioId IS NULL");
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No se encontraron rampeos para este pozo", e);
        }
        return lista;
    }
    
    public void remove(Escenario escenario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM Rampeo r WHERE r.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando el Rampeo del escenario {0}. {1}",
                    new Object[]{escenario.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void remove(Escenario escenario, Pozo pozo) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM Rampeo r "
                    + "WHERE r.escenarioId = :escenario "
                    + "AND r.pozoId = :pozo");
            q.setParameter("escenario", escenario);
            q.setParameter("pozo", pozo);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando el Rampeo del pozo {0}. {1}",
                    new Object[]{pozo.getUbicacion(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
