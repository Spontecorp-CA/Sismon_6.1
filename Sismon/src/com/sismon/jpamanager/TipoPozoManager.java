package com.sismon.jpamanager;

import com.sismon.model.TipoPozo;
import com.sismon.vista.utilities.SismonLog;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class TipoPozoManager extends AbstractFacade<TipoPozo> {

    private static final SismonLog SISMOLOG = SismonLog.getInstance();

    public TipoPozoManager() {
        super(TipoPozo.class);
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
    
    public List<TipoPozo> findAllOrdered(){
        List<TipoPozo> tipoPozoList = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query q = em.createQuery("SELECT tp FROM TipoPozo tp "
                    + "ORDER BY tp.tipo ASC");
            tipoPozoList = q.getResultList();
        } catch (Exception e) {
            SISMOLOG.logger.log(Level.SEVERE, "Error.. No encontró pozos para esta macolla y fila");
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return tipoPozoList;
    }
}
