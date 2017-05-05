package com.sismon.jpamanager;

import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Pozo;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroAsignado;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class TaladroAsignadoManager extends AbstractFacade<TaladroAsignado> {

    private static final SismonLog SISMON_LOG = SismonLog.getInstance();

    public TaladroAsignadoManager() {
        super(TaladroAsignado.class);
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
    
    public List<TaladroAsignado> findAll(Escenario escenario){
        List<TaladroAsignado> lista = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ta FROM TaladroAsignado ta WHERE "
                    + "ta.escenarioId = :escenario ORDER BY ta.id");
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
            SISMON_LOG.logger.log(Level.SEVERE, "No pudo encontrar registros de "
                    + "TaladroAsignado. Error: {0}", e);
        } finally {
            if(em != null){
                em.close();
            }
        }
        return lista;
    }
    
    public TaladroAsignado find(Escenario escenario, Taladro taladro, Fila fila){
        TaladroAsignado ta = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ta FROM TaladroAsignado ta WHERE "
                    + "ta.escenarioId = :escenario AND ta.taladroId = :taladro "
                    + "AND ta.filaId = :fila");
            q.setParameter("escenario", escenario);
            q.setParameter("taladro", taladro);
            q.setParameter("fila", fila);
            ta = (TaladroAsignado) q.getSingleResult();
        } catch (Exception e) {
            SISMON_LOG.logger.log(Level.SEVERE, "No pudo encontrar registros de "
                    + "TaladroAsignado. Error: {0}", e);
        } finally {
            if(em != null){
                em.close();
            }
        }
        return ta;
    }
    
    public List<TaladroAsignado> find(Escenario escenario, Pozo pozo) {
        List<TaladroAsignado> lista = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ta FROM TaladroAsignado ta WHERE "
                    + "ta.escenarioId = :escenario AND (ta.pozoInId = :pozo "
                    + "OR ta.pozoOutId = :pozo)");
            q.setParameter("escenario", escenario);
            q.setParameter("pozo", pozo);
            lista =  q.getResultList();
        } catch (Exception e) {
            SISMON_LOG.logger.log(Level.SEVERE, "No pudo encontrar registros de "
                    + "TaladroAsignado. Error: {0}", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<TaladroAsignado> findRutaTaladro(Escenario escenario, Taladro taladro){
        List<TaladroAsignado> lista = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ta FROM TaladroAsignado ta WHERE "
                    + "ta.escenarioId = :escenario AND ta.taladroId = :taladro "
                    + "ORDER BY ta.orden");
            q.setParameter("escenario", escenario);
            q.setParameter("taladro", taladro);
            lista = q.getResultList();
        } catch (Exception e) {
            SISMON_LOG.logger.log(Level.SEVERE, "No pudo encontrar registros de "
                    + "TaladroAsignado. Error: {0}", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<TaladroAsignado> findAll(Escenario escenario, Fila fila){
        List<TaladroAsignado> lista = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ta FROM TaladroAsignado ta WHERE "
                    + "ta.escenarioId = :escenario AND ta.filaId = :fila ");
            q.setParameter("escenario", escenario);
            q.setParameter("fila", fila);
            lista = q.getResultList();
        } catch (Exception e) {
            SISMON_LOG.logger.log(Level.SEVERE, "No pudo encontrar registros de "
                    + "TaladroAsignado. Error: {0}", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public void remove(Escenario escenario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM TaladroAsignado ta "
                    + "WHERE ta.escenarioId = :escenario ");
            q.setParameter("escenario", escenario);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            SISMON_LOG.logger.log(Level.SEVERE, "No pudo eliminar registros de "
                    + "TaladroAsignado. Error: {0}", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void remove(Escenario escenario, Fila fila){
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM TaladroAsignado ta WHERE ta.escenarioId = :escenario "
                    + "AND ta.filaId = :fila");
            q.setParameter("escenario", escenario);
            q.setParameter("fila", fila);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            SISMON_LOG.logger.log(Level.SEVERE, "No pudo eliminar registros de "
                    + "TaladroAsignado. Error: {0}", e);
        } finally{
            if(em != null){
                em.close();
            }
        }
    }
    
    public TaladroAsignado find(Pozo pozoIn, Pozo pozoOut, Taladro taladro, Escenario escenario){
        EntityManager em = null;
        TaladroAsignado ta = null;
        try {
            em= getEntityManager();
            Query q = em.createQuery("SELECT ta FROM TaladroAsignado ta WHERE "
                    + "ta.pozoInId = :pozoIn AND ta.pozoOutId = :pozoOut "
                    + "AND ta.taladroId = :taladro AND ta.escenarioId = :escenario");
            q.setParameter("pozoIn", pozoIn);
            q.setParameter("pozoOut", pozoOut);
            q.setParameter("taladro", taladro);
            q.setParameter("escenario", escenario);
            ta = (TaladroAsignado)q.getSingleResult();
        } catch (Exception e) {
            SISMON_LOG.logger.log(Level.SEVERE, "No pudo encontrar registros de "
                    + "TaladroAsignado. Error: {0}", e);
        } finally {
            if(em != null){
                em.close();
            }
        }
        return ta;
    }
}
