package org.ppg.model;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.sql.DriverManager.getConnection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PPGSchedulerTest {

    // TODO Hay que implementar los test

    private PPGScheduler app;

    @Before
    public void setUp() throws PPGSchedulerException{
        app = new PPGScheduler();
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
