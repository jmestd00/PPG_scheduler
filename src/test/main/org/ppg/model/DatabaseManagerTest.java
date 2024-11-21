package org.ppg.model;

import org.junit.Before;
import org.junit.Test;

import static java.sql.DriverManager.getConnection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DatabaseManagerTest {

    // TODO Hay que implementar los test

    private DatabaseManager app;

    @Before
    public void setUp() throws PPGSchedulerException{
        app = DatabaseManager.getInstance();
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
}
