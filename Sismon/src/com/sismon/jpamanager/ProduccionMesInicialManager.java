package com.sismon.jpamanager;

import com.sismon.model.Escenario;
import com.sismon.model.ProduccionMesInicial;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class ProduccionMesInicialManager extends AbstractFacade<ProduccionMesInicial>{

    private static final SismonLog sismonlog = SismonLog.getInstance();
    
    public ProduccionMesInicialManager() {
        super(ProduccionMesInicial.class);
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
    
    public void remove(Escenario escenario) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            String query = "DELETE FROM ProduccionMesInicial pmi "
                    + "WHERE pmi.escenarioId = :escenario";
            Query q = em.createQuery(query);
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
    
    public List<ProduccionMesInicial> findAll(Escenario escenario){
        List<ProduccionMesInicial> lista = null;
        try {
            EntityManager em = getEntityManager();
            Query q = em.createQuery("SELECT pmi FROM ProduccionMesInicial pmi"
                    + " WHERE pmi.escenarioId = :escenario");
            q.setParameter("escenario", escenario);
            lista = q.getResultList();
        } catch (Exception e) {
        }
        return lista;
    }
}
