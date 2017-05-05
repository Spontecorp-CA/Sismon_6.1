
package com.sismon.jpamanager;

import com.sismon.model.Escenario;
import com.sismon.model.Taladro;
import com.sismon.model.TaladroHasFase;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class TaladroHasFaseManager extends AbstractFacade<TaladroHasFase>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public TaladroHasFaseManager() {
        super(TaladroHasFase.class);
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
    
    public List<TaladroHasFase> findAll(Taladro taladro) {
        List<TaladroHasFase> faseTaladroList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ft FROM TaladroHasFase ft WHERE ft.taladroId = :taladro"
                    + " AND ft.escenarioId IS NULL");
            q.setParameter("taladro", taladro);
            faseTaladroList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.WARNING, "Error: No se encuentran objetos TaladroHasFase para el "
                    + "Taladro {1}", taladro.getNombre());
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return faseTaladroList;
    }
    
    public List<TaladroHasFase> findAll(Taladro taladro, Escenario escenario) {
        List<TaladroHasFase> faseTaladroList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ft FROM TaladroHasFase ft WHERE ft.taladroId = :taladro "
                    + "AND ft.escenarioId = :escenario "
                    + "ORDER BY ft.fecha");
            q.setParameter("taladro", taladro);
            q.setParameter("escenario", escenario);
            faseTaladroList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.WARNING, "Error: No se encuentran objetos TaladroHasFase para el "
                    + "Taladro {1}", taladro.getNombre());
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return faseTaladroList;
    }
    
    public List<TaladroHasFase> findAll(Taladro taladro, Escenario escenario, String fase) {
        List<TaladroHasFase> faseTaladroList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ft FROM TaladroHasFase ft "
                    + "WHERE ft.taladroId = :taladro "
                    + "AND ft.escenarioId = :escenario "
                    + "AND ft.fase = :fase "
                    + "ORDER BY ft.fecha");
            q.setParameter("taladro", taladro);
            q.setParameter("escenario", escenario);
            q.setParameter("fase", fase);
            faseTaladroList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.WARNING, "Error: No se encuentran objetos TaladroHasFase para el "
                    + "Taladro {0}", taladro.getNombre());
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return faseTaladroList;
    }
    
    public List<TaladroHasFase> findAll(Taladro taladro, Escenario escenario, Date fecha) {
        List<TaladroHasFase> faseTaladroList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ft FROM TaladroHasFase ft WHERE ft.taladroId = :taladro"
                    + " AND ft.escenarioId = :escenario AND ft.fecha = :fecha");
            q.setParameter("taladro", taladro);
            q.setParameter("escenario", escenario);
            q.setParameter("fecha", fecha);
            faseTaladroList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.WARNING, "Error: No se encuentran objetos TaladroHasFase para el "
                    + "Taladro {1}", taladro.getNombre());
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return faseTaladroList;
    }
    
    public List<TaladroHasFase> findAll(Escenario escenario) {
        List<TaladroHasFase> faseTaladroList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ft FROM TaladroHasFase ft "
                    + "WHERE ft.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            faseTaladroList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.WARNING, "Error: No se encuentran objetos TaladroHasFase para el "
                    + "Escenario {0}", escenario);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return faseTaladroList;
    }
    
    public List<TaladroHasFase> findAllBase() {
        List<TaladroHasFase> faseTaladroList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT ft FROM TaladroHasFase ft "
                    + "WHERE ft.escenarioId IS NULL");
            faseTaladroList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.WARNING, "Error: No se encuentran objetos TaladroHasFase para el "
                    + "Escenario base");
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return faseTaladroList;
    }
    
    public List<TaladroHasFase> findAllByFaseTaladroEscenario(String fase, 
            Taladro taladro, Escenario escenario){
        List<TaladroHasFase> taladrosFase = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT thf FROM TaladroHasFase thf "
                    + "WHERE thf.taladroId = :taladro "
                    + "AND thf.fase = :fase AND thf.escenarioId = :escenario "
                    + "ORDER BY thf.fecha");
            q.setParameter("taladro", taladro);
            q.setParameter("fase", fase);
            q.setParameter("escenario", escenario);
            taladrosFase = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.WARNING, "Error: No se encuentran FaseTaladro's con nombre: {0} "
                    + "en el Taladro {1}", new Object[]{fase, taladro.getNombre()});
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return taladrosFase;
    }
    
    public List<Date> findAllDates(Taladro taladro, Escenario escenario) {
        List<Date> fechas = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT DISTINCT thf.fecha FROM TaladroHasFase thf "
                    + "WHERE thf.taladroId = :taladro AND thf.escenarioId = :escenario");
            q.setParameter("taladro", taladro);
            q.setParameter("escenario", escenario);
            fechas = q.getResultList();
        } catch (Exception e) {
        } finally {
            if(em != null){
                em.close();
            }
        }
        return fechas;
    }
    
    public TaladroHasFase find(String fase, Taladro taladro) {
        TaladroHasFase taladroFase = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT thf FROM TaladroHasFase thf "
                    + "WHERE thf.taladroId = :taladro "
                    + "AND thf.fase = :fase");
            q.setParameter("taladro", taladro);
            q.setParameter("fase", fase);
            taladroFase = (TaladroHasFase) q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.WARNING, "Error: No se encuentran una FaseTaladro con nombre: {0} "
                    + "en el Taladro {1}", new Object[]{fase, taladro.getNombre()});
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return taladroFase;
    }
    
    public TaladroHasFase find(String fase, Taladro taladro, Escenario escenario) {
        TaladroHasFase taladroFase = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT thf FROM TaladroHasFase thf "
                    + "WHERE thf.taladroId = :taladro "
                    + "AND thf.fase = :fase AND thf.escenarioId = :escenario");
            q.setParameter("taladro", taladro);
            q.setParameter("fase", fase);
            q.setParameter("escenario", escenario);
            taladroFase = (TaladroHasFase) q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.WARNING, "Error: No se encuentran una FaseTaladro con nombre: {0} "
                    + "en el Taladro {1}", new Object[]{fase, taladro.getNombre()});
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return taladroFase;
    }
    
    public TaladroHasFase find(String fase, Taladro taladro, Escenario escenario
                        ,Date fecha) {
        TaladroHasFase taladroFase = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT thf FROM TaladroHasFase thf "
                    + "WHERE thf.taladroId = :taladro "
                    + "AND thf.fase = :fase AND thf.escenarioId = :escenario "
                    + "AND thf.fecha = :fecha");
            q.setParameter("taladro", taladro);
            q.setParameter("fase", fase);
            q.setParameter("escenario", escenario);
            q.setParameter("fecha", fecha);
            taladroFase = (TaladroHasFase) q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.WARNING, "Error: No se encuentran una FaseTaladro con nombre: {0} "
                    + "en el Taladro {1}", new Object[]{fase, taladro.getNombre()});
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return taladroFase;
    }
    
    public void remove(Escenario escenario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM TaladroHasFase p WHERE p.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando la TaladroHasFase del escenario {0}. {1}", 
                    new Object[]{escenario.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    
}
