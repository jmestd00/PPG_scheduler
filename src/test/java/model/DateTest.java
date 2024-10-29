package model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DateTest {

    // TODO Hay que implementar los test

    private Date date1;
    private Date date2;

    @Before
    public void setUp() {
        date1 = new Date(23, 10, 2024);
        date2 = new Date(3, 11, 2024);
    }

    @Test
    public void testJUnitConfig() {
        assertTrue("JUnit configurado correctamente", true);
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
    public void testToString() {
        assertEquals("23/10/2024", date1.toString());
        assertEquals("3/11/2024", date2.toString());
    }

    @Test
    public void testDateDayDifference() {
        int difference = date1.DateDayDifference(date2);
        assertEquals(11, difference);

        difference = date2.DateDayDifference(date1);
        assertEquals(-11, difference);
    }

    @Test
    public void testAddOrSubstractDates() {
        Date newDate = date1.AddOrSubstractDays(8);
        assertEquals("31/10/2024", newDate.toString());

        newDate = date1.AddOrSubstractDays(70);
        assertEquals("1/1/2025", newDate.toString());

        newDate = date2.AddOrSubstractDays(-365);
        assertEquals("3/11/2023", newDate.toString());
    }
}
