package com.sismon.jpamanager;

import com.sismon.model.Escenario;
import com.sismon.model.ProduccionMesInicial;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author jgcastillo
 */
public class ProduccionMesInicialManagerTest {
    
    public ProduccionMesInicialManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getEntityManager method, of class ProduccionMesInicialManager.
     */
    @Test
    public void testGetEntityManager() {
        System.out.println("getEntityManager");
        ProduccionMesInicialManager instance = new ProduccionMesInicialManager();
        EntityManager expResult = null;
        EntityManager result = instance.getEntityManager();
        assertNotNull(result);
    }

    /**
     * Test of addPropertyChangeListener method, of class ProduccionMesInicialManager.
     */
    @Test
    @Ignore
    public void testAddPropertyChangeListener() {
        System.out.println("addPropertyChangeListener");
        PropertyChangeListener listener = null;
        ProduccionMesInicialManager instance = new ProduccionMesInicialManager();
        instance.addPropertyChangeListener(listener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removePropertyChangeListener method, of class ProduccionMesInicialManager.
     */
    @Test
    @Ignore
    public void testRemovePropertyChangeListener() {
        System.out.println("removePropertyChangeListener");
        PropertyChangeListener listener = null;
        ProduccionMesInicialManager instance = new ProduccionMesInicialManager();
        instance.removePropertyChangeListener(listener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class ProduccionMesInicialManager.
     */
    @Test
    @Ignore
    public void testRemove() {
        System.out.println("remove");
        Escenario escenario = null;
        ProduccionMesInicialManager instance = new ProduccionMesInicialManager();
        instance.remove(escenario);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAll method, of class ProduccionMesInicialManager.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        Escenario escenario = new Escenario(16);
        ProduccionMesInicialManager instance = new ProduccionMesInicialManager();
        List<ProduccionMesInicial> expResult = null;
        List<ProduccionMesInicial> result = instance.findAll(escenario);
        assertNotNull(result);
        
    }
    
}
