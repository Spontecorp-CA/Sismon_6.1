package com.sismon.vista.controller;

import com.sismon.model.Escenario;
import com.sismon.model.Pozo;
import com.sismon.model.ProduccionMesInicial;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
public class MakeQueriesToDbTest {
    
    private Escenario escenario;
    private MakeQueriesToDb instance;
    
    public MakeQueriesToDbTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        escenario = new Escenario(16);
        instance = new MakeQueriesToDb();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of generacionPotencialDeclinada method, of class MakeQueriesToDb.
     */
    @Test
    public void testGeneracionPotencialDeclinada() {
        System.out.println("generacionPotencialDeclinada");
        Map<Pozo, Map<Date, Double>> expResult = null;
        Map<Pozo, Map<Date, Double>> result = instance.generacionPotencialDeclinada(escenario);
        assertNotNull(result);
        
    }
    
}
