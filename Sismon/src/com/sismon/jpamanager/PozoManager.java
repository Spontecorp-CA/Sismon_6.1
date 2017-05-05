
package com.sismon.jpamanager;

import com.sismon.exceptions.SismonException;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Pozo;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class PozoManager extends AbstractFacade<Pozo>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public PozoManager() {
        super(Pozo.class);
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
    
    public Pozo find(Fila fila, String ubicacion, Escenario escenario) {
        Pozo pozo = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Pozo p WHERE p.filaId = :fila "
                    + "AND p.ubicacion = :ubicacion AND p.escenarioId = :escenario ");
            q.setParameter("fila", fila);
            q.setParameter("ubicacion", ubicacion);
            q.setParameter("escenario", escenario);
            pozo = (Pozo)q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. No encontró el pozo {0}, para esta fila: "
                    + "{1} y escenario: {2}"
                    ,new Object[]{ubicacion, fila.getNombre(), escenario.getNombre()});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return pozo;
    }
    
    public Pozo find(String ubicacion, Escenario escenario) {
        Pozo pozo = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Pozo p "
                    + "WHERE p.ubicacion = :ubicacion AND p.escenarioId = :escenario ");
            q.setParameter("ubicacion", ubicacion);
            q.setParameter("escenario", escenario);
            pozo = (Pozo) q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. No encontró el pozo {0}, para esta fila: "
                    + "escenario: {1}"
                    ,new Object[]{ubicacion, escenario.getNombre()});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return pozo;
    }
    
    public List<Pozo> findAllBase() {
        List<Pozo> pozoList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Pozo p WHERE p.escenarioId IS NULL"
                    + " ORDER BY p.numero DESC");
            pozoList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. No encontró pozos para la perforacion base", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return pozoList;
    }
    
    public List<Pozo> findAll(Macolla macolla) {
        List<Pozo> pozoList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Pozo p WHERE p.macollaId = :macolla"
                    + " ORDER BY p.numero DESC");
            q.setParameter("macolla", macolla);
            pozoList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. No encontró pozos para esta macolla", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return pozoList;
    }
    
    public List<Pozo> findAll(Escenario escenario) {
        List<Pozo> pozoList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Pozo p "
                    + "WHERE p.escenarioId = :escenario "
                    + "ORDER BY p.numero DESC");
            q.setParameter("escenario", escenario);
            pozoList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. No encontró pozos para esta macolla", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return pozoList;
    }
    
    public List<Pozo> findAll(Macolla macolla, Fila fila) {
        List<Pozo> pozoList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Pozo p WHERE p.macollaId = :macolla"
                    + " AND p.filaId = :fila AND p.escenarioId IS NULL "
                    + "ORDER BY p.numero DESC");
            q.setParameter("macolla", macolla);
            q.setParameter("fila", fila);
            pozoList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. No encontró pozos para esta macolla y fila");
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return pozoList;
    }
    
    public List<Pozo> findAllByEscenario(Macolla macolla, Fila fila, Escenario escenario) {
        List<Pozo> pozoList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Pozo p WHERE p.macollaId = :macolla"
                    + " AND p.filaId = :fila AND p.escenarioId = :escenario"
                    + " ORDER BY p.numero DESC");
            q.setParameter("macolla", macolla);
            q.setParameter("fila", fila);
            q.setParameter("escenario", escenario);
            pozoList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. No encontró pozos para esta macolla y fila");
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return pozoList;
    }
    
    public List<Pozo> findAll(Macolla macolla, Escenario escenario) throws SismonException{
        List<Pozo> pozoList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Pozo p WHERE p.macollaId = :macolla"
                    + " AND p.escenarioId = :escenario ORDER BY p.numero ASC");
            q.setParameter("macolla", macolla);
            q.setParameter("escenario", escenario);
            pozoList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No encontró pozos para esta macolla y escenario. Error: {0}", e);
            throw new SismonException("Error.. No se encontró pozos para esta macolla y escenario");
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return pozoList;
    }
    
    public List<Pozo> findAll(Fila fila, Escenario escenario) {
        List<Pozo> pozoList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Pozo p "
                    + "WHERE p.filaId = :fila "
                    + "AND p.escenarioId = :escenario "
                    + "ORDER BY p.numero DESC");
            q.setParameter("fila", fila);
            q.setParameter("escenario", escenario);
            pozoList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. No encontró pozos para esta fila");
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return pozoList;
    }
    
    public List<Pozo> findAll(Macolla macolla, Fila fila, Escenario escenario) {
        List<Pozo> pozoList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Pozo p WHERE p.macollaId = :macolla"
                    + " AND p.filaId = :fila AND p.escenarioId = :escenario "
                    + "ORDER BY p.numero DESC");
            q.setParameter("macolla", macolla);
            q.setParameter("fila", fila);
            q.setParameter("escenario", escenario);
            pozoList = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. No encontró pozos para esta macolla y fila");
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return pozoList;
    }
    
    public void remove(Escenario escenario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM Pozo p WHERE p.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando el Pozo del escenario {0}. {1}",
                    new Object[]{escenario.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void remove(Escenario escenario, Pozo pozo) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM Pozo p "
                    + "WHERE p.escenarioId = :escenario "
                    + "AND p.id = :pozo");
            q.setParameter("escenario", escenario);
            q.setParameter("pozo", pozo.getId());
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando el Pozo del escenario {0}. {1}",
                    new Object[]{escenario.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
