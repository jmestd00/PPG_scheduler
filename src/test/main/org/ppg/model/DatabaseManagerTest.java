package org.ppg.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class DatabaseManagerTest {

    // TODO Hay que implementar los test

    private static DatabaseManager dbManager;


    @Before
    public void setup() throws PPGSchedulerException {
        dbManager = DatabaseManager.getInstance();
    }

    @Test
    public void testGetBatch() throws PPGSchedulerException {
        Batch batch = dbManager.getBatchDB(2);
        System.out.println(batch);
    }

    @Test
    public void testInsertBatch() throws PPGSchedulerException {
        Batch newBatch = new Batch(12, "VDWBBC", "VDW", "A-RXX3359-DD", 12, LocalDate.now(), LocalDate.now(), LocalDate.now(), Statuses.FINALIZADO, "", Types.PISC, 6, 45);
        dbManager.insertBatchDB(newBatch);
    }


    @Test
    public void testUpdateBatch() throws PPGSchedulerException {
        Batch batchToUpdate = new Batch(12, "VDWBBC", "VDW", "A-RXX3359-DD", 500, LocalDate.now(), LocalDate.now(), LocalDate.now(), Statuses.FINALIZADO, "KXP BCW   *ZZZ Bulk 225589 @2 / \n05/05/23 - RetiraW previsioWes coW 150 kg  de stock", Types.PISC, 6, 4);
        dbManager.updateBatchDB(batchToUpdate);
    }

    @Test
    public void testGetAllBatches() throws PPGSchedulerException {
        LinkedList<Batch> batches = (LinkedList<Batch>) dbManager.getAllBatches();
        System.out.println(batches.toString());
    }

    @Test
    public void testGetBatches() throws PPGSchedulerException {
        ArrayList<Batch> batches = (ArrayList<Batch>) dbManager.getBatches(1, 1);
        System.out.println(batches.toString());
    }

    @Test
    public void testGetDilutorDB() throws PPGSchedulerException {
        Dilutor dilutor = dbManager.getDilutorDB(2);
        System.out.println(dilutor);
    }

    @Test
    public void testGetDilutorsDB() throws PPGSchedulerException {
        HashMap<Integer, Dilutor> dilutor = dbManager.getDilutorsDB();
        System.out.println(dilutor);
    }


    @Test
    public void getFreeDilutorDate() throws PPGSchedulerException {
        LocalDate date = dbManager.getFreeDilutorDate(2);
        System.out.println(date);
    }

    @Test
    public void getFilledDilutors() throws PPGSchedulerException {
        ArrayList<Dilutor> dilutor = dbManager.getFilledDilutors();
        System.out.println(dilutor);
    }

    @Test
    public void testGetBatchFromDatabase() throws PPGSchedulerException {
        dbManager = DatabaseManager.getInstance();
        Batch b = dbManager.getBatchDB(2);
        System.out.println(b);
    }
}