package com.sismon.jpamanager;

import com.sismon.model.Campo;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Casper
 */
public class CampoManager extends AbstractFacade<Campo>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public CampoManager() {
        super(Campo.class);
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
    
    public Campo find(String nombre) {
        Campo campo = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT c FROM Campo c WHERE c.nombre =:nombre");
            q.setParameter("nombre", nombre);
            campo = (Campo) q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. no existe el campo: {0}, {1}",new Object[]{nombre, e} );
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return campo;
    }
    
    @SuppressWarnings("unchecked")
    public List<Campo> findAll(String nombre){
        List<Campo> campos = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT c FROM Campo c WHERE c.nombre =:nombre");
            q.setParameter("nombre", nombre);
            campos = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. no existen los campos: {0}, {1}", new Object[]{nombre, e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return campos;
    }
}
