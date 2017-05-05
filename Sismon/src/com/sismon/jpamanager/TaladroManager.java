
package com.sismon.jpamanager;

import com.sismon.model.Escenario;
import com.sismon.model.Taladro;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class TaladroManager extends AbstractFacade<Taladro>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public TaladroManager() {
        super(Taladro.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return JPAUtilities.getEMF().createEntityManager();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().addPropertyChangeListener(listener);
    }

    public Taladro find(String nombre) {
        Taladro taladro = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT t FROM Taladro t "
                    + "WHERE t.nombre = :nombre AND t.escenarioId IS NULL");
            q.setParameter("nombre", nombre);
            taladro = (Taladro)q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: No se encuentra el taladro para el escenario base", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return taladro;
    }
    
    public Taladro find(String nombre, Escenario escenario) {
        Taladro taladro = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT t FROM Taladro t "
                    + "WHERE t.nombre = :nombre AND t.escenarioId = :escenario");
            q.setParameter("nombre", nombre);
            q.setParameter("escenario", escenario);
            taladro = (Taladro) q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: No se encuentra el taladro para el escenario base", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return taladro;
    }
    
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getPropertyChangeSupport().removePropertyChangeListener(listener);
    }
    
    public List<Taladro> findAllByDate() {
        List<Taladro> taladros = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT t FROM Taladro t ORDER BY t.fechaInicial");
            taladros = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: Error: No se encuentran Taladros ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return taladros;
    }
    
    public List<Taladro> findAllBase() {
        List<Taladro> taladros = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT t FROM Taladro t WHERE t.escenarioId IS NULL "
                    + "ORDER BY t.fechaInicial");
            taladros = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: No se encuentran taladros para el escenario base", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return taladros;
    }
    
    public List<Taladro> findAll(Escenario escenario) {
        List<Taladro> taladros = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT t FROM Taladro t WHERE t.escenarioId = :escenario "
                    + "ORDER BY t.fechaInicial");
            q.setParameter("escenario", escenario);
            taladros = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: No se encuentran taladros para el escenario {0}, {1} ",
                    new Object[] {escenario.getNombre(),e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return taladros;
    }
    
    public void remove(Escenario escenario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM Taladro p WHERE p.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando el Taladro del escenario {0}. {1}", 
                    new Object[]{escenario.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
