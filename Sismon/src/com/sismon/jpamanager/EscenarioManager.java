package com.sismon.jpamanager;

import com.sismon.controller.Constantes;
import com.sismon.model.Escenario;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class EscenarioManager extends AbstractFacade<Escenario>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public EscenarioManager() {
        super(Escenario.class);
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
    
    public Escenario find(String nombre){
        EntityManager em = null;
        Escenario escenario = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT es FROM Escenario es "
                    + "WHERE es.nombre = :nombre", Escenario.class);
            q.setParameter("nombre", nombre);
            escenario = (Escenario)q.getSingleResult();
        } catch (Exception e) {
            sismonlog.logger.log(Level.INFO, "Información.. no hay un escenario con el nombre {0}",
                    new Object[]{nombre, e});
        } finally {
            if(em != null){
                em.close();
            }
        }
        return escenario;
    }
    
    public List<Escenario> findAllMV(boolean isMV) {
        EntityManager em = null;
        List<Escenario> lista = null;
        int tipo = isMV ? Constantes.ESCENARIO_MEJOR_VISION : Constantes.ESCENARIO_PRUEBA;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT es FROM Escenario es "
                    + "WHERE es.tipo = :tipo");
            q.setParameter("tipo", tipo);
            lista = q.getResultList();
        } catch (Exception e) {
            sismonlog.logger.log(Level.SEVERE, "Error.. no hay un escenario tipo {0}",
                    new Object[]{tipo, e});
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return lista;
    }
    
}
