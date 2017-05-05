package com.sismon.jpamanager;

import com.sismon.model.Paridad;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

/**
 *
 * @author jgcastillo
 */
public class ParidadManager extends AbstractFacade<Paridad> {

    private static final SismonLog sismonlog = SismonLog.getInstance();

    public ParidadManager() {
        super(Paridad.class);
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
    
    public Paridad find(double valor, Date fechaIn) throws Exception {
        Paridad paridad = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Paridad p WHERE p.valor =:valor AND p.fechaIn =:fechaIn");
            q.setParameter("valor", valor);
            q.setParameter("fechaIn", fechaIn);
            paridad = (Paridad) q.getSingleResult();
        } catch (NonUniqueResultException e) {
            paridad = null;
            throw new Exception("Error.. Ya existe una paridad definida con este valor y fecha", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return paridad;
    }
    
    public Paridad find(int status) throws Exception {
        Paridad paridad = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Paridad p WHERE p.status =:status");
            q.setParameter("status", status);
            paridad = (Paridad) q.getSingleResult();
        } catch (NoResultException e) {
            //Exceptions.printStackTrace(e);
            throw new NoResultException("No ha conseguido un objecto paridad");
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return paridad;
    }
    
    public Paridad find(Date fecha){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Paridad paridad = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT p FROM Paridad p WHERE p.fechaIn < :fecha");
            q.setParameter("fecha", fecha);
            List<Paridad> paridades = q.getResultList();
            
            if(paridades.size() == 1){
                paridad = paridades.get(0);
            } else {
                long min = 999999999999999L;
                long fechaLong = fecha.getTime();
                for(Paridad par : paridades){
                    long fechaInLong = par.getFechaIn().getTime();
                    long dif = fechaLong - fechaInLong;
                    if(dif < min){
                        min = dif;
                        paridad = par;
                    }
                }
            }
            
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error, no encontró paridad para la fecha {0}, {1}", 
                    new Object[]{sdf.format(fecha), e});
        }
        return paridad;
    }
}
