package org.ppg.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.assertEquals;


public class DiluidorTest {

    // TODO Hay que implementar los test

    private ArrayList<Dilutor> diluidores;
    private Dilutor d1;
    private Dilutor d2;
    private Dilutor d3;
    @Before
    public void setUp() {
        d1 = new Dilutor(1, "d1", 10);
        d2 = new Dilutor(2, "d2", 20);
        d3 = new Dilutor(3, "d3", 30);

        diluidores.add(d2);
        diluidores.add(d3);
        diluidores.add(d1);
    }

    @Test 
    public void testSortDiluidores()  throws PPGSchedulerException {
        Dilutor.sortDiluidores(diluidores);
        assertEquals(diluidores.get(0).getId(), 1);
        assertEquals(diluidores.get(1).getId(), 2);
        assertEquals(diluidores.get(2).getId(), 3);
    }

}
