package com.sismon.jpamanager;

import com.sismon.model.Explotacion;
import com.sismon.model.Pozo;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class ExplotacionManager extends AbstractFacade<Explotacion> {

    private static final SismonLog sismonlog = SismonLog.getInstance();

    public ExplotacionManager() {
        super(Explotacion.class);
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
    
    public void batchSave(List<Explotacion> col) {
        EntityManager em = null;
        try {
            int count = 0;
            em = getEntityManager();
            em.getTransaction().begin();
            for (Explotacion element : col) {
                em.persist(element);
                if (count % 500 == 0) {
                    em.flush();
                    em.clear();
                }
                count++;
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error guardando la explotación : ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void removeAll(){
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM Explotacion ex");
            q.executeUpdate();
            em.getTransaction().commit();
        } catch(Exception e){
            sismonlog.logger.log(Level.SEVERE,"Error borrando la tabla de produccion", e);
        } finally {
            if(em != null){
                em.close();
            }
        }
    }
    
    public void remove(Pozo pozo) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM Explotacion ex "
                    + "WHERE ex.pozoId = :pozo");
            q.setParameter("pozo", pozo);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error borrando la tabla de produccion", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    @Override
    public List<Explotacion> findAll(){
        EntityManager em = null;
        List<Explotacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ex FROM Explotacion ex "
                    + "JOIN ex.pozoId p "
                    + "ORDER BY p.macollaId, p.filaId, p.numero, ex.fecha");
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error buscando la data de explotacion", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Explotacion> findAll(String query){
        EntityManager em = null;
        List<Explotacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery(query);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Explotacion> findAll(String query, String[] paramNames, Object[] params) {
        EntityManager em = null;
        List<Explotacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery(query);
            q.setParameter(paramNames[0], params[0]);
            if (paramNames.length > 1) {
                for (int i = 1; i < paramNames.length; i++) {
                    q.setParameter(paramNames[i], params[i]);
                }
            }
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Pozo> findAllPozos(){
        EntityManager em = null;
        List<Pozo> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT DISTINCT ex.pozoId FROM Explotacion ex "
                    + "JOIN ex.pozoId p "
                    + "ORDER BY p.macollaId, p.filaId, p.numero, ex.fecha");
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error buscando la data de explotacion", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
}
