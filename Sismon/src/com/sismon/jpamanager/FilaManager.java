package com.sismon.jpamanager;

import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class FilaManager extends AbstractFacade<Fila>{
    
    private static final SismonLog sismonlog = SismonLog.getInstance();

    public FilaManager() {
        super(Fila.class);
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
    
    public Fila find(String nombre, Macolla macolla) {
        Fila fila = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery(
                    "SELECT f FROM Fila f WHERE f.nombre = :nombre "
                            + "AND f.macollaId = :macolla");
            q.setParameter("nombre", nombre);
            q.setParameter("macolla", macolla);
            fila = (Fila) q.getSingleResult();
        } catch (NoResultException e) {
            sismonlog.logger.log(Level.SEVERE, "No hay fila {0} para la macolla: {1}, Error: {2}", 
                    new Object[]{nombre, macolla.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return fila;
    }
    
    public List<Fila> findAll(Macolla macolla) {
        List<Fila> filas = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery(
                    "SELECT f FROM Fila f WHERE f.macollaId = :macolla");
            q.setParameter("macolla", macolla);
            filas = q.getResultList();
        } catch (NoResultException e) {
            sismonlog.logger.log(Level.SEVERE, "No hay filas para la macolla: {0}, Error: {1}", 
                    new Object[]{macolla.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return filas;
    }
    
    public List<Macolla> findAllConfigurated() {
        List<Macolla> macollaList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT DISTINCT pf.macollaId FROM Fila pf",
                    Fila.class);
            macollaList = (List<Macolla>) q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.FINER, "No consigió objetos Macolla configurados", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return macollaList;
    }
}
