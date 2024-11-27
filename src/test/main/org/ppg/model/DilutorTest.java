package org.ppg.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;


public class DilutorTest {

    // TODO Hay que implementar los test


    Dilutor dilutor;

    @Before
    public void setUp() {
        dilutor = new Dilutor(1, "Dilutor1", 10000);
    }

    @Test
    public void testGetCapacity(){
        assertEquals(10000, dilutor.getCapacity());
    }

    @Test
    public void testGetId(){
        assertEquals(1, dilutor.getId());
    }

    @Test
    public void testGetName(){
        assertEquals("Dilutor1", dilutor.getName());
    }

    /*
    @Test
    public void testGetFechaFin(){
        dilutor.setEndDate(LocalDate.of(2024, 11, 25));
        assertEquals(LocalDate.of(2024,11,25),dilutor.getFechaFin());
    }

    @Test
    public void testGetBatches() throws PPGSchedulerException {
        dilutor.addLote(new Batch(1, "1", "colores", "rojo",
                1000, LocalDate.of(2024,11,21),
                LocalDate.of(2024,11,25), Statuses.EN_ESPERA,
                "Este lote crea el color rojo", Types.PIMM, dilutor));
        assertEquals(("Número de lote " + 1 + "\n" +
                "Clase de planificación " + "1" + "\n" +
                "Planta " + "colores" + "\n" + "Artículo " + "rojo" + "\n" +
                "Cantidad " + 1000 + "\n" +
                "Fecha de inicio " + "21/11/2024" + "\n" +
                "Fecha de necesidad " + "25/11/2024" + "\n" +
                "Estado " + "EN ESPERA" + "\n" +
                "Descripción " + "Este lote crea el color rojo" + "\n" +
                "Tipo " + "PIMM" + "\n" +
                "Dilutor " + "Dilutor1"), dilutor.getBatches().getFirst().toString());
    }

    @Test
    public void testCompareTo(){
        Dilutor dilutor2 = new Dilutor(2, "Dilutor2", 5000);
        assertEquals(-1, dilutor.compareTo(dilutor2));
        assertEquals(1, dilutor2.compareTo(dilutor));
        dilutor2 = new Dilutor(1, "Dilutor2", 5000);
        assertEquals(0, dilutor.compareTo(dilutor2));
    }

     */

    @Test
    public void testToString(){
        assertEquals("id: " + 1 + '\n' +
                "Name: " + "Dilutor1" + '\n' +
                "Capacity: " + 10000 + '\n' +
                "Batches: {\n" + "[]" + "\n}", dilutor.toString());
    }
}
