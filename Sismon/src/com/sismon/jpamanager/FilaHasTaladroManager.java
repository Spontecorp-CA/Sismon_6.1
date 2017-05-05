
package com.sismon.jpamanager;

import com.sismon.exceptions.SismonException;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.FilaHasTaladro;
import com.sismon.model.Taladro;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class FilaHasTaladroManager extends AbstractFacade<FilaHasTaladro>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public FilaHasTaladroManager() {
        super(FilaHasTaladro.class);
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
    
    public List<FilaHasTaladro> findAllBase() {
        List<FilaHasTaladro> mhtList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery(
                    "SELECT fht FROM FilaHasTaladro fht WHERE fht.escenarioId IS NULL");
            mhtList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. no hay Taladros Asociados a Filas del escenario base", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return mhtList;
    }
    
    public List<FilaHasTaladro> findAll(Escenario escenario) {
        List<FilaHasTaladro> mhtList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery(
                    "SELECT fht FROM FilaHasTaladro fht WHERE fht.escenarioId = :escenario "
                            + "ORDER BY fht.taladroId");
            q.setParameter("escenario", escenario);
            mhtList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. no hay Taladros Asociados a Filas de este escenario", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return mhtList;
    }
    
    public List<FilaHasTaladro> findAllAsigned(Escenario escenario) {
        List<FilaHasTaladro> mhtList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery(
                    "SELECT fht FROM FilaHasTaladro fht "
                            + "WHERE fht.escenarioId = :escenario "
                            + "AND fht.fechaAsignacion IS NOT NULL");
            q.setParameter("escenario", escenario);
            mhtList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. no hay Taladros Asociados a Filas de este escenario", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return mhtList;
    }
    
    public FilaHasTaladro find(Fila fila, Escenario escenario) throws SismonException {
        FilaHasTaladro fht = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery(
                    "SELECT fht FROM FilaHasTaladro fht WHERE "
                            + " fht.filaId = :fila AND fht.escenarioId = :escenario");
            q.setParameter("fila", fila);
            q.setParameter("escenario", escenario);
            fht = (FilaHasTaladro) q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.WARNING, "Error.. no hay Taladros Asociados a esta Fila de este escenario", e);
            throw new SismonException(String.format("No hay taladros asociados a"
                    + " la fila %s de la macolla %s en este escenario", fila, fila.getMacollaId()));
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return fht;
    }
    
    public List<Fila> findAll(Taladro taladro){
        List<Fila> filas = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            // SELECT fht.fila_id FROM Fila_Has_Taladro fht WHERE fht.taladro_id = 315;
            Query q = em.createQuery("SELECT fht.filaId FROM FilaHasTaladro fht "
                    + "WHERE fht.taladroId = :taladro");
            q.setParameter("taladro", taladro);
            filas = q.getResultList();
        } catch (Exception e) {
        } finally {
            if(em != null){
                em.close();
            }
        }
        return filas;
    }
    
    public void remove(Escenario escenario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM FilaHasTaladro p WHERE p.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. al eliminar la FilaHasTaladro del escenario {0}. {1}",
                    new Object[]{escenario.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
