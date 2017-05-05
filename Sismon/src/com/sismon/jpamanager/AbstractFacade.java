package com.sismon.jpamanager;

import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 *
 * @author Casper
 * @param <T>
 */
public abstract class AbstractFacade<T> {
    
    public static final String PROP_ENTITY_ELIMINADA = "EntidadEliminada";
    public static final String PROP_ENTITY_AGREGADA = "EntidadAgregada";
    public static final String PROP_ENTITY_ACTUALIZADA = "EntidadActualizada";
    
    private final Class<T> entityClass;
    private SwingPropertyChangeSupport propChangeSupport = null;
    
    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    protected abstract EntityManager getEntityManager();
    
    public abstract void addPropertyChangeListener(PropertyChangeListener listener);
    
    public abstract void removePropertyChangeListener(PropertyChangeListener listener);
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        if (propChangeSupport == null) {
            propChangeSupport = new SwingPropertyChangeSupport(this, true);
        }
        return propChangeSupport;
    }
    
    private void fireChangeOnEDT(final String propname, final Object oldValue, final Object newValue) {
        //logger.log(Level.FINER, "Disparando cambio para {0}", propname);
        getPropertyChangeSupport().firePropertyChange(propname, oldValue, newValue);
    }
    
    public void create(T entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            fireChangeOnEDT(PROP_ENTITY_AGREGADA, null, entity);
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error creando en Abstract Facade : ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void batchSave(Collection<T> col){
        EntityManager em = null;
        try {
            int count = 0;
            em = getEntityManager();
            em.getTransaction().begin();
            for(T element : col){
                em.persist(element);
                if(count % 50 == 0){
                    em.flush();
                    em.clear();    
                }
                count++;
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error creando en Abstract Facade : ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void batchEdit(Collection<T> col) {
        EntityManager em = null;
        try {
            int count = 0;
            em = getEntityManager();
            em.getTransaction().begin();
            for (T element : col) {
                em.merge(element);
                if (count % 50 == 0) {
                    em.flush();
                    em.clear();
                }
                count++;
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error editando en Abstract Facade : ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void edit(T entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
            fireChangeOnEDT(PROP_ENTITY_ACTUALIZADA, null, entity);
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error editando en Abstract Facade : ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void remove(T entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.remove(em.merge(entity));
            em.getTransaction().commit();
            fireChangeOnEDT(PROP_ENTITY_ELIMINADA, entity, null);
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando en Abstract Facade : ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<T> findAll() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<T> findRange(int[] range) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public int count() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }
}
