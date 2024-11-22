package org.ppg.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DatabaseManagerTest {

    // TODO Hay que implementar los test

    private static DatabaseManager app;

    @Before
    public void setup() throws PPGSchedulerException {
        app = DatabaseManager.getInstance();

        Batch initialBatch = new Batch(
                0, "TEMP_CLASS", "TEMP_PLANT", "TEMP_ITEM", 0,
                LocalDate.now(), LocalDate.now().plusDays(1),
                Statuses.EN_DEMORA, "", Types.PISC, null
        );

        try {
            app.insertBatchDB(initialBatch);
        } catch (Exception e) {
            System.out.println("Error inicializando base de datos: " + e.getMessage());
        }
    }


        @Test
        public void testGetBatch() {
            Batch batch = app.getBatchDB(0);
            assertNotNull("El lote debería existir en la base de datos", batch);
            assertEquals("TEMP_CLASS", batch.planningClass());
            assertEquals("TEMP_PLANT", batch.plant());
        }

        @Test
        public void testInsertBatch() throws PPGSchedulerException {
            Batch newBatch = new Batch(
                    1, "NEW_CLASS", "NEW_PLANT", "NEW_ITEM", 10,
                    LocalDate.now(), LocalDate.now().plusDays(7),
                    Statuses.EN_ESPERA, "Prueba de inserción", Types.PIMM, null
            );

            app.insertBatchDB(newBatch);
            Batch retrievedBatch = app.getBatchDB(1);
            assertNotNull("El lote insertado debería existir en la base de datos", retrievedBatch);
            assertEquals("NEW_CLASS", retrievedBatch.planningClass());
            assertEquals("NEW_ITEM", retrievedBatch.item());
        }

        @Test
        public void testUpdateBatch() throws PPGSchedulerException {
            Batch batch = app.getBatchDB(0);
            assertNotNull("El lote 0 debería existir para actualizarlo", batch);

            batch = new Batch(
                    batch.nBatch(), batch.planningClass(), batch.plant(), batch.item(), batch.quantity(),
                    LocalDate.now().plusDays(5), batch.needDate(),
                    batch.status(), batch.description(), batch.type(), batch.dilutor()
            );

            app.updateBatchDB(batch);

            Batch updatedBatch = app.getBatchDB(0);
            assertEquals("La fecha de inicio debería haberse actualizado", LocalDate.now().plusDays(5), updatedBatch.startDate());
        }

        @Test
        public void testGetBatchesList() {
            ArrayList<Batch> batches = app.getBatchesListDB();
            assertNotNull("La lista de lotes no debería ser nula", batches);
            assertFalse("La lista de lotes debería contener al menos un lote", batches.isEmpty());
        }
    }