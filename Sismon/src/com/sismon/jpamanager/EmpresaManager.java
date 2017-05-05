package com.sismon.jpamanager;

import com.sismon.model.Empresa;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author jgcastillo
 */
public class EmpresaManager extends AbstractFacade<Empresa>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public EmpresaManager() {
        super(Empresa.class);
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
    
    public Empresa find(String nombre) {
        Empresa campo = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT e FROM Empresa e WHERE e.nombre =:nombre");
            q.setParameter("nombre", nombre);
            campo = (Empresa) q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. no existe la empresa: {0}, {1}", new Object[]{nombre, e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return campo;
    }

    @SuppressWarnings("unchecked")
    public List<Empresa> findAll(String nombre) {
        List<Empresa> campos = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT e FROM Empresa e WHERE e.nombre =:nombre");
            q.setParameter("nombre", nombre);
            campos = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error encontrando las empresas: {0}, {1}", new Object[]{nombre, e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return campos;
    }
}
