
package com.sismon.jpamanager;

import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Pozo;
import com.sismon.model.PozoSecuencia;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class PozoSecuenciaManager extends AbstractFacade<PozoSecuencia>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public PozoSecuenciaManager() {
        super(PozoSecuencia.class);
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
    
    public List<PozoSecuencia> find(Fila fila) {
        List<PozoSecuencia> secuencias = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sec FROM PozoSecuencia sec "
                    + "WHERE sec.filaId = :fila AND sec.escenarioId IS NULL "
                    + "ORDER BY sec.pozoId");
            q.setParameter("fila", fila);
            secuencias = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.FINER, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return secuencias;
    }
    
    public List<PozoSecuencia> find(Fila fila, Escenario escenario) {
        List<PozoSecuencia> secuencias = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sec FROM PozoSecuencia sec "
                    + "WHERE sec.filaId = :fila AND sec.escenarioId = :escenario "
                    + "ORDER BY sec.pozoId");
            q.setParameter("fila", fila);
            q.setParameter("escenario", escenario);
            secuencias = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.FINER, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return secuencias;
    }
    
    public List<PozoSecuencia> findBySecuencia(Fila fila, Escenario escenario) {
        List<PozoSecuencia> secuencias = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sec FROM PozoSecuencia sec "
                    + "WHERE sec.filaId = :fila AND sec.escenarioId = :escenario "
                    + "ORDER BY sec.secuencia");
            q.setParameter("fila", fila);
            q.setParameter("escenario", escenario);
            secuencias = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.FINER, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return secuencias;
    }
    
    public PozoSecuencia find(Escenario escenario, Pozo pozo, String fase) {
        PozoSecuencia secuencia = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sec FROM PozoSecuencia sec "
                    + "WHERE sec.escenarioId = :escenario AND sec.pozoId = :pozo "
                    + "AND sec.fase = :fase");
            q.setParameter("escenario", escenario);
            q.setParameter("pozo", pozo);
            q.setParameter("fase", fase);
            secuencia = (PozoSecuencia)q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.FINER, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return secuencia;
    }
    
    public List<Macolla> findMacollaList() {
        List<Macolla> macollas = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT DISTINCT p.macollaId FROM "
                    + "PozoSecuencia sp JOIN sp.pozoId p "
                    + "WHERE sp.escenarioId IS NULL");
            macollas = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return macollas;
    }
    
    public List<Macolla> findMacollaList(Escenario escenario) {
        List<Macolla> macollas = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT DISTINCT p.macollaId FROM "
                    + "PozoSecuencia sp JOIN sp.pozoId p "
                    + "WHERE sp.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            macollas = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return macollas;
    }
    
    public List<PozoSecuencia> findAllOrdered(Fila fila) {
        List<PozoSecuencia> secuencias = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sp FROM PozoSecuencia sp "
                    + "WHERE sp.filaId = :fila "
                    + "ORDER BY sp.secuencia");
            q.setParameter("fila", fila);
            secuencias = q.getResultList();
            //secuencias.addAll(secList);
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return secuencias;
    }
    
    public List<PozoSecuencia> findAllOrdered(Fila fila, Escenario escenario) {
        List<PozoSecuencia> secuencias = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sp FROM PozoSecuencia sp "
                    + "WHERE sp.filaId = :fila AND sp.escenarioId = :escenario "
                    + "ORDER BY sp.secuencia");
            q.setParameter("fila", fila);
            q.setParameter("escenario", escenario);
            secuencias = q.getResultList();
            //secuencias.addAll(secList);
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return secuencias;
    }
    
    public List<PozoSecuencia> findAll(Escenario escenario) {
        List<PozoSecuencia> secuencias = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sp FROM PozoSecuencia sp "
                    + "WHERE sp.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            secuencias = q.getResultList();
            //secuencias.addAll(secList);
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return secuencias;
    }
    
    public List<PozoSecuencia> findAllOrderedByFila(Escenario escenario) {
        List<PozoSecuencia> secuencias = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sp FROM PozoSecuencia sp "
                    + "WHERE sp.escenarioId = :escenario "
                    + "ORDER BY sp.filaId, sp.secuencia");
            q.setParameter("escenario", escenario);
            secuencias = q.getResultList();
            //secuencias.addAll(secList);
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return secuencias;
    }
    
    public List<PozoSecuencia> findAllByPozo(Pozo pozo){
        List<PozoSecuencia> secuencias = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sp FROM PozoSecuencia sp "
                    + "WHERE sp.pozoId = :pozo "
                    + "ORDER BY sp.secuencia");
            q.setParameter("pozo", pozo);
            secuencias = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return secuencias;
    }
    
    public List<PozoSecuencia> findAllBase() {
        List<PozoSecuencia> secuencias = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT sp FROM PozoSecuencia sp "
                    + "WHERE sp.escenarioId IS NULL");
            secuencias = q.getResultList();
            //secuencias.addAll(secList);
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error: ", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return secuencias;
    }
    
    public void remove(Escenario escenario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM PozoSecuencia p WHERE p.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando la PozoSecuencia del escenario {0}. {1}", 
                    new Object[]{escenario.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void remove(Fila fila, Escenario escenario){
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM PozoSecuencia p WHERE p.escenarioId = :escenario "
                    + "AND p.filaId = :fila");
            q.setParameter("fila", fila);
            q.setParameter("escenario", escenario);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando la PozoSecuencia del escenario {0}. {1}",
                    new Object[]{escenario.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    
}
