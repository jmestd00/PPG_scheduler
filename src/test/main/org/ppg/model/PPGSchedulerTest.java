package org.ppg.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class PPGSchedulerTest {

    // TODO Hay que implementar los test

    @Before
    public void setUp() {

    }

    @Test /* (expected = PPGSchedulerException.class) */
    public void testTemplate() /* throws PPGSchedulerException */ {
        /*
         * assertTrue();
         * assertFalse();
         * assertEquals();
         * ...
         */
    }

    @Test
    public void testGetDiluidoresFromDatabase() throws PPGSchedulerException{
        PPGScheduler app = new PPGScheduler();
        ArrayList<Diluidor> diluidores = app.obtenerDiluidoresDeLaBaseDeDatos();
        System.out.println(diluidores);
    }

    @Test
    public void testUpdateLoteDB() throws PPGSchedulerException {
        PPGScheduler app = new PPGScheduler();
        Lote lote = new Lote(3, "VD-APA", "VDM", "A-RXX3359-DD", 650, "PISC", new Date(8, 11, 2025), new Date(15, 11, 2025), new Date(15, 11, 2025), Estados.EN_DEMORA, 1, 0);
        app.actualizarLoteDB(lote);
    }

    @Test
    public void testInsertLoteDB() throws PPGSchedulerException {
        PPGScheduler app = new PPGScheduler();
        Lote lote = new Lote(3, "VD-APA", "VDM", "A-RXX3359-DD", 650, "PISC", new Date(8, 11, 2025), new Date(15, 11, 2025), new Date(15, 11, 2025), Estados.EN_DEMORA, 1, 0);
        app.insertarLoteDB(lote);
    }
}
