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

}
