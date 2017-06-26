package com.sismon.jpamanager;

import com.sismon.exceptions.SismonException;
import com.sismon.model.Escenario;
import com.sismon.model.Fila;
import com.sismon.model.Macolla;
import com.sismon.model.Perforacion;
import com.sismon.model.Pozo;
import com.sismon.model.Taladro;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class PerforacionManager extends AbstractFacade<Perforacion> {

    private static final SismonLog sismonlog = SismonLog.getInstance();

    public PerforacionManager() {
        super(Perforacion.class);
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
    
    public Date find(Pozo pozo, String fase, Escenario escenario) {
        EntityManager em = null;
        Date fechaIn = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p.fechaIn FROM Perforacion p "
                    + "WHERE p.pozoId = :pozo AND p.fase = :fase "
                    + "AND p.escenarioId = :escenario");
            q.setParameter("pozo", pozo);
            q.setParameter("fase", fase);
            q.setParameter("escenario", escenario);
            fechaIn = (Date) q.getSingleResult();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return fechaIn;
    }
    
    public Date findFaseEnd(Pozo pozo, String fase, Escenario escenario) {
        EntityManager em = null;
        Date fechaOut = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p.fechaOut FROM Perforacion p "
                    + "WHERE p.pozoId = :pozo AND p.fase = :fase "
                    + "AND p.escenarioId = :escenario");
            q.setParameter("pozo", pozo);
            q.setParameter("fase", fase);
            q.setParameter("escenario", escenario);
            fechaOut = (Date) q.getSingleResult();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return fechaOut;
    }
    
    public Taladro find(Fila fila, Escenario escenario) throws SismonException{
        EntityManager em = null;
        Taladro taladro = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT DISTINCT p.taladroId FROM Perforacion p "
                    + "WHERE p.filaId = :fila AND p.escenarioId = :escenario");
            q.setParameter("fila", fila);
            q.setParameter("escenario", escenario);
            taladro = (Taladro) q.getSingleResult();
        } catch (Exception e) {
            throw new SismonException("No se encontró taladro asiganado a esta fila");
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return taladro;
    }

    public List<Perforacion> findAllBase() {
        EntityManager em = null;
        List<Perforacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Perforacion p WHERE p.escenarioId IS NULL "
                    + "ORDER BY p.taladroId, p.fechaIn");
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Perforacion> findAll(Escenario escenario) {
        EntityManager em = null;
        List<Perforacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Perforacion p "
                    + "WHERE p.escenarioId = :escenario "
                    + "ORDER BY p.taladroId, p.fechaIn");
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Perforacion> findAll(Escenario escenario, Taladro taladro) {
        EntityManager em = null;
        List<Perforacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Perforacion p "
                    + "WHERE p.escenarioId = :escenario "
                    + "AND p.taladroId = :taladro "
                    + "ORDER BY p.taladroId, p.fechaIn");
            q.setParameter("escenario", escenario);
            q.setParameter("taladro", taladro);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }

    public List<Perforacion> findAll(String query, String[] paramNames , Object[] params) {
        EntityManager em = null;
        List<Perforacion> lista = null;
        
        try {
            em = getEntityManager();
            Query q = em.createQuery(query);
            q.setParameter(paramNames[0], params[0]);
            if(paramNames.length > 1){
                for(int i = 1; i < paramNames.length; i++){
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
    
    public List<Perforacion> findAll(Escenario escenario, Fila fila){
        EntityManager em = null;
        List<Perforacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT perf FROM Perforacion perf "
                    + "WHERE perf.escenarioId = :escenario AND perf.filaId = :fila "
                    + "ORDER BY perf.pozoId, perf.fechaIn");
            q.setParameter("escenario", escenario);
            q.setParameter("fila", fila);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;    
    }
    
    public List<Perforacion> findAll(Escenario escenario, Fila fila, 
            Taladro taladro) {
        EntityManager em = null;
        List<Perforacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT perf FROM Perforacion perf "
                    + "WHERE perf.escenarioId = :escenario "
                    + "AND perf.filaId = :fila "
                    + "AND perf.taladroId = :taladro "
                    + "ORDER BY perf.pozoId, perf.fechaIn");
            q.setParameter("escenario", escenario);
            q.setParameter("fila", fila);
            q.setParameter("taladro", taladro);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Perforacion> findAll(Escenario escenario, Macolla macolla, 
            Taladro taladro) {
        EntityManager em = null;
        List<Perforacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT perf FROM Perforacion perf "
                    + "WHERE perf.escenarioId = :escenario "
                    + "AND perf.macollaId = :macolla "
                    + "AND perf.taladroId = :taladro "
                    + "ORDER BY perf.pozoId, perf.fechaIn");
            q.setParameter("escenario", escenario);
            q.setParameter("macolla", macolla);
            q.setParameter("taladro", taladro);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Perforacion> findAll(Escenario escenario, Pozo pozo) {
        EntityManager em = null;
        List<Perforacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT perf FROM Perforacion perf "
                    + "WHERE perf.escenarioId = :escenario "
                    + "AND perf.pozoId = :pozo "
                    + "ORDER BY perf.pozoId, perf.fechaIn");
            q.setParameter("escenario", escenario);
            q.setParameter("pozo", pozo);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Perforacion> findAllByDate(Escenario escenario, Fila fila) {
        EntityManager em = null;
        List<Perforacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT perf FROM Perforacion perf "
                    + "WHERE perf.escenarioId = :escenario AND perf.filaId = :fila "
                    + "ORDER BY perf.fechaIn");
            q.setParameter("escenario", escenario);
            q.setParameter("fila", fila);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Perforacion> findAllByDate(Escenario escenario, Taladro taladro) {
        EntityManager em = null;
        List<Perforacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT perf FROM Perforacion perf "
                    + "WHERE perf.escenarioId = :escenario AND perf.taladroId = :taladro "
                    + "ORDER BY perf.fechaIn");
            q.setParameter("escenario", escenario);
            q.setParameter("taladro", taladro);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Perforacion> findAllOrderedByFila(Escenario escenario) {
        EntityManager em = null;
        List<Perforacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Perforacion p "
                    + "WHERE p.escenarioId = :escenario "
                    + "ORDER BY p.filaId");
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Perforacion> findAllOrderedByPozo(Escenario escenario) {
        EntityManager em = null;
        List<Perforacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Perforacion p "
                    + "WHERE p.escenarioId = :escenario "
                    + "ORDER BY p.filaId, p.pozoId, p.fechaIn");
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Perforacion> findAllOrderedByFecha(Escenario escenario) {
        EntityManager em = null;
        List<Perforacion> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Perforacion p "
                    + "WHERE p.escenarioId = :escenario "
                    + "ORDER BY p.taladroId, p.fechaIn");
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Fila> findAllFilas(Macolla macolla, Escenario escenario){
        EntityManager em = null;
        List<Fila> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT DISTINCT p.filaId FROM Perforacion p "
                    + "WHERE p.macollaId = :macolla AND p.escenarioId = :escenario "
                    + "ORDER BY p.filaId");
            q.setParameter("macolla", macolla);
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<Pozo> findAllPozos(Fila fila, Escenario escenario){
        EntityManager em = null;
        List<Pozo> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT DISTINCT p.pozoId FROM Perforacion p "
                    + "WHERE p.filaId = :fila AND p.escenarioId = :escenario "
                    + "ORDER BY p.pozoId.numero");
            q.setParameter("fila", fila);
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public List<String> findAllFases(Pozo pozo, Escenario escenario) {
        EntityManager em = null;
        List<String> lista = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p.fase FROM Perforacion p "
                    + "WHERE p.pozoId = :pozo AND p.escenarioId = :escenario "
                    + "ORDER BY p.fechaIn");
            q.setParameter("pozo", pozo);
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
    public void removeBase(){
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM Perforacion p WHERE p.escenarioId IS NULL");
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando la perforacion base. {0}",e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void remove(Escenario escenario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM Perforacion p WHERE p.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando la perforacion del escenario {0}. {1}", 
                    new Object[]{escenario.getNombre(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public void remove(Taladro taladro) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery("DELETE FROM Perforacion p WHERE p.taladroId = :taladro");
            q.setParameter("taladro", taladro);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando la perforacion "
                    + "del taladro {0}. {1}",
                    new Object[]{taladro.getNombre(), e});
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
            Query q = em.createQuery("DELETE FROM Perforacion p "
                    + "WHERE p.escenarioId = :escenario "
                    + "AND p.pozoId = :pozo");
            q.setParameter("escenario", escenario);
            q.setParameter("pozo", pozo);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error eliminando la perforacion del pozo {0}. {1}",
                    new Object[]{pozo.getUbicacion(), e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public List<Object[]> getFechasMinMax(Escenario escenario) {
        List<Object[]> fechas = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT MIN(p.fechaIn), MAX(p.fechaOut) FROM Perforacion p "
                    + "WHERE p.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            fechas = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No se puedo obtener las fechas", e);
        }
        return fechas;
    }
    
    public Perforacion getPerforacion(Pozo pozo, String fase){
        Perforacion perforacion = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            String query = "FROM Perforacion p WHERE p.pozoId = :pozo AND p.fase = :fase";
            TypedQuery<Perforacion> q = em.createQuery(query, Perforacion.class);
            q.setParameter("pozo", pozo);
            q.setParameter("fase", fase);
            perforacion = q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "No se obtuvo una perforación para "
                    + "el pozo {0} y la fase {1}, dió error {2}", 
                    new Object[]{pozo.getUbicacion(), fase, e});
        }
        return perforacion;
    }
}
