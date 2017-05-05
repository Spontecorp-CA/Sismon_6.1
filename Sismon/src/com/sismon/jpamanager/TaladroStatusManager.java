package com.sismon.jpamanager;

import com.sismon.model.Escenario;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroStatus;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;


public class TaladroStatusManager extends AbstractFacade<TaladroStatus> {

    private static final SismonLog sismonlog = SismonLog.getInstance();

    public TaladroStatusManager() {
        super(TaladroStatus.class);
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
    
    /**
     * Devuelve el TaladroStatus del Taladro pasado com parámetro cuyo status de
     * TaladroStatus es pasado como parámetro. Este último puede ser INACTIVO
     * (0) o ACTIVO (1) y además coincida con el nombre del StatusTaladro
     *
     * @param taladro
     * @param status
     * @param nombreStatus
     * @return
     */
    public TaladroStatus find(Taladro taladro, int status, String nombreStatus) {
        TaladroStatus taladroStatus = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ts FROM TaladroStatus ts "
                    + "WHERE ts.taladroId = :taladro "
                    + "AND ts.status = :status "
                    + "AND ts.nombre = :nombreStatus");
            q.setParameter("taladro", taladro);
            q.setParameter("status", status);
            q.setParameter("nombreStatus", nombreStatus);
            taladroStatus = (TaladroStatus) q.getSingleResult();
        } catch (NoResultException e) {
            sismonlog.logger.log(Level.FINE, "No se encuentra el status {0} para este Taladro: {1}",
                    new Object[]{nombreStatus, taladro.getNombre(), e.getMessage()});
        }
        return taladroStatus;
    }
    
    /**
     * Devuelve el TaladroStatus del Taladro pasado como parámetro y con el status de
     * TaladroStatus es pasado como parámetro. Este último puede ser INACTIVO
     * (0) o ACTIVO (1)
     *
     * @param taladro
     * @param status
     * @param nombreStatus
     * @return
     */
    public TaladroStatus find(Taladro taladro, int status) {
        TaladroStatus taladroStatus = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ts FROM TaladroStatus ts "
                    + "WHERE ts.taladroId = :taladro "
                    + "AND ts.status = :status ");
            q.setParameter("taladro", taladro);
            q.setParameter("status", status);
            taladroStatus = (TaladroStatus) q.getSingleResult();
        } catch (NoResultException e) {
            sismonlog.logger.log(Level.FINE, "No se encuentra el taladro {0} con status {1}. Error: {2}",
                    new Object[]{taladro.getNombre(), status, e.getMessage()});
        }
        return taladroStatus;
    }
    
    /**
     * Devuelve la lista de TaladroStatus del taladro usado como parámetro
     *
     * @param taladro
     * @return
     */
    public List<TaladroStatus> find(Taladro taladro) {
        List<TaladroStatus> taladroStatusList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ts FROM TaladroStatus ts "
                    + "WHERE ts.taladroId = :taladro ORDER By ts.fechaIn");
            q.setParameter("taladro", taladro);
            taladroStatusList = q.getResultList();
        } catch (NoResultException e) {
            sismonlog.logger.log(Level.FINE, "No hay status configurados para este Taladro: {0}", 
                    new String[]{taladro.getNombre(), e.getMessage()});
        }
        return taladroStatusList;
    }
    
    public TaladroStatus find(Taladro taladro, String nombreStatus) {
        TaladroStatus taladroStatus = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ts FROM TaladroStatus ts "
                    + "WHERE ts.taladroId = :taladro "
                    + "AND ts.nombre = :nombreStatus");
            q.setParameter("taladro", taladro);
            q.setParameter("nombreStatus", nombreStatus);
            taladroStatus = (TaladroStatus)q.getSingleResult();
        } catch (NoResultException e) {
            sismonlog.logger.log(Level.FINE, "No hay status: {0} configurados para este Taladro: {1}. Error: {2}",
                    new String[]{nombreStatus, taladro.getNombre(), e.getMessage()});
        }
        return taladroStatus;
    }
    
    public List<TaladroStatus> findAll(Taladro taladro, String nombreStatus) {
        List<TaladroStatus> taladroStatusList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ts FROM TaladroStatus ts "
                    + "WHERE ts.taladroId = :taladro "
                    + "AND ts.nombre = :nombreStatus");
            q.setParameter("taladro", taladro);
            q.setParameter("nombreStatus", nombreStatus);
            taladroStatusList = q.getResultList();
        } catch (NoResultException e) {
            sismonlog.logger.log(Level.FINE, "No hay status: {0} configurados para este Taladro: {1}. Error: {2}",
                    new String[]{nombreStatus, taladro.getNombre(), e.getMessage()});
        }
        return taladroStatusList;
    }
    
    public List<TaladroStatus> findAll(Escenario escenario) {
        List<TaladroStatus> taladroStatusList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ts FROM TaladroStatus ts "
                    + "JOIN ts.taladroId t "
                    + "WHERE t.escenarioId.id = :escenario");
            q.setParameter("escenario", escenario);
            taladroStatusList = q.getResultList();
        } catch (NoResultException e) {
            sismonlog.logger.log(Level.FINE, "No hay status: configurados para este Escenario: {0}. Error: {1}",
                    new String[]{escenario.getNombre(), e.getMessage()});
        }
        return taladroStatusList;
    }
    
    public void remove(Taladro taladro) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM TaladroStatus p WHERE p.taladroId = :taladro");
            q.setParameter("taladro", taladro);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando el TaladroStatus del taladro {0}. {1}",
                    new Object[]{taladro.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
