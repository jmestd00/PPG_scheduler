package org.ppg.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class DilutorTest {
    Dilutor dilutor;

    @Before
    public void setUp() {
        dilutor = new Dilutor(1, "Dilutor1", 10000);
    }

    @Test
    public void testGetCapacity() {
        assertEquals(10000, dilutor.getCapacity());
    }

    @Test
    public void testGetId() {
        assertEquals(1, dilutor.getId());
    }

    @Test
    public void testGetName() {
        assertEquals("Dilutor1", dilutor.getName());
    }


    @Test
    public void testDynamicAdd() throws CantAddException {

        ArrayList<Dilutor> dilutors = new ArrayList<>();
        dilutors.add(new Dilutor(1, "A", 50));
        dilutors.add(new Dilutor(2, "B", 100));
        dilutors.add(new Dilutor(3, "C", 150));
        dilutors.add(new Dilutor(4, "D", 200));


        dilutors.get(0).add(new Batch(1, 2, LocalDate.of(2024, 1, 2), 30, false));
        dilutors.get(0).add(new Batch(2, 2, LocalDate.of(2024, 1, 2), 45, false));
        dilutors.get(0).add(new Batch(3, 3, LocalDate.of(2024, 1, 18), 50, false));
        dilutors.get(0).add(new Batch(4, 3, LocalDate.of(2024, 1, 6), 20, false));
        dilutors.get(0).add(new Batch(5, 6, LocalDate.of(2024, 1, 8), 25, false));

        dilutors.get(1).add(new Batch(6, 3, LocalDate.of(2024, 1, 3), 90, false));
        dilutors.get(1).add(new Batch(7, 2, LocalDate.of(2024, 1, 5), 40, false));
        dilutors.get(1).add(new Batch(8, 5, LocalDate.of(2024, 1, 8), 60, false));
        dilutors.get(1).add(new Batch(9, 3, LocalDate.of(2024, 1, 13), 85, false));
        dilutors.get(1).add(new Batch(10, 1, LocalDate.of(2024, 1, 15), 21, false));

        dilutors.get(2).add(new Batch(11, 1, LocalDate.of(2024, 1, 2), 100, false));
        dilutors.get(2).add(new Batch(12, 4, LocalDate.of(2024, 1, 4), 140, false));
        dilutors.get(2).add(new Batch(13, 6, LocalDate.of(2024, 1, 12), 120, false));
        dilutors.get(2).add(new Batch(14, 1, LocalDate.of(2024, 1, 30), 150, false));
        dilutors.get(2).add(new Batch(15, 3, LocalDate.of(2024, 1, 31), 90, false));

        dilutors.get(3).add(new Batch(16, 1, LocalDate.of(2024, 1, 2), 190, false));
        dilutors.get(3).add(new Batch(17, 2, LocalDate.of(2024, 1, 4), 140, false));
        dilutors.get(3).add(new Batch(18, 6, LocalDate.of(2024, 1, 12), 160, false));
        dilutors.get(3).add(new Batch(19, 1, LocalDate.of(2024, 1, 13), 200, false));
        dilutors.get(3).add(new Batch(20, 3, LocalDate.of(2024, 1, 31), 20, false));


        Scheduler scheduler = new Scheduler(dilutors);

        scheduler.dynamicAdd(new Batch(21, 1, LocalDate.of(2024, 1, 2), 190, false), 2);
        System.out.println(scheduler);

    }

    /*
    @Test
    public void testGetFechaFin(){
        dilutor.setEndDate(LocalDate.of(2024, 11, 25));
        assertEquals(LocalDate.of(2024,11,25),dilutor.getFechaFin());
    }

    @Test
    public void testGetBatches() throws PPGSchedulerException {
        dilutor.addLote(new BatchTemp(1, "1", "colores", "rojo",
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
                "DilutorTemp " + "Dilutor1"), dilutor.getBatches().getFirst().toString());
    }

    @Test
    public void testCompareTo(){
        DilutorTemp dilutor2 = new DilutorTemp(2, "Dilutor2", 5000);
        assertEquals(-1, dilutor.compareTo(dilutor2));
        assertEquals(1, dilutor2.compareTo(dilutor));
        dilutor2 = new DilutorTemp(1, "Dilutor2", 5000);
        assertEquals(0, dilutor.compareTo(dilutor2));
    }

     */

    @Test
    public void testToString() {
        assertEquals("id: " + 1 + '\n' + "Name: " + "Dilutor1" + '\n' + "Capacity: " + 10000 + '\n' + "Batches: {\n" + "[]" + "\n}", dilutor.toString());
    }
}
