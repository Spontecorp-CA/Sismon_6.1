
package com.sismon.jpamanager;

import com.sismon.exceptions.SismonException;
import com.sismon.model.Escenario;
import com.sismon.model.Macolla;
import com.sismon.model.MacollaSecuencia;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class MacollaSecuenciaManager extends AbstractFacade<MacollaSecuencia>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public MacollaSecuenciaManager() {
        super(MacollaSecuencia.class);
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
    
    public List<MacollaSecuencia> findAll(Macolla macolla, Escenario escenario){
        EntityManager em;
        List<MacollaSecuencia> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sec FROM MacollaSecuencia sec WHERE "
                    + "sec.macollaId = :macolla AND sec.escenarioId = :escenario", 
                    MacollaSecuencia.class);
            q.setParameter("macolla", macolla);
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No hay secuencia configurada para esta macolla", e);
        }
        return lista;
    }
    
    public List<MacollaSecuencia> findAll(Macolla macolla) {
        EntityManager em;
        List<MacollaSecuencia> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sec FROM MacollaSecuencia sec WHERE "
                    + "sec.macollaId = :macolla AND sec.escenarioId IS NULL",
                    MacollaSecuencia.class);
            q.setParameter("macolla", macolla);
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No hay secuencia configurada para esta macolla", e);
        }
        return lista;
    }
    
    public List<MacollaSecuencia> findAllOrdered(){
        EntityManager em;
        List<MacollaSecuencia> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sec FROM MacollaSecuencia sec WHERE "
                    + "sec.escenarioId IS NULL ORDER BY sec.secuencia",
                    MacollaSecuencia.class);
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No hay secuencia configurada para el escenario base", e);
        }
        return lista;
    }
    
    public List<MacollaSecuencia> findAllOrdered(Escenario escenario) {
        EntityManager em;
        List<MacollaSecuencia> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sec FROM MacollaSecuencia sec WHERE "
                    + "sec.escenarioId = :escenario ORDER BY sec.secuencia",
                    MacollaSecuencia.class);
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No hay secuencia de macollas configurada para el escenario: ", e);
        }
        return lista;
    }
    
    public List<Macolla> findAll(Escenario escenario) throws SismonException{
        EntityManager em;
        List<Macolla> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sec.macollaId FROM MacollaSecuencia sec "
                    + "WHERE sec.escenarioId = :escenario ORDER BY sec.secuencia",
                    MacollaSecuencia.class);
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No hay secuencia de macollas configurada para el escenario: ", e);
            throw new SismonException("No hay secuencia de macollas configurada para el escenario: " +
                    escenario.getNombre());
        }
        return lista;
    }
    
    public List<MacollaSecuencia> findAllBase() {
        EntityManager em;
        List<MacollaSecuencia> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sec FROM MacollaSecuencia sec WHERE "
                    + "sec.escenarioId IS NULL",
                    MacollaSecuencia.class);
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No encontró secuencia para el escenario base", e);
        }
        return lista;
    }
    
    public void remove(Escenario escenario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM MacollaSecuencia p WHERE p.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando la MacollaSecuencia del escenario {0}. {1}", 
                    new Object[]{escenario.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
