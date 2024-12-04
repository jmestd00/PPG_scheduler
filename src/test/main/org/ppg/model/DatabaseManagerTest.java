package org.ppg.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

public class DatabaseManagerTest {
    private static DatabaseManager dbManager;
    
    @Before
    public void setup() throws PPGSchedulerException {
        dbManager = DatabaseManager.getInstance();
    }
    
    @Test
    public void testGetBatch() throws PPGSchedulerException {
        dbManager.getBatchDB(2);
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
        dbManager.getAllBatches();
    }
    
    @Test
    public void testGetBatches() throws PPGSchedulerException {
        dbManager.getBatches(1, 1);
    }
    
    @Test
    public void testGetDilutorDB() throws PPGSchedulerException {
        dbManager.getDilutorDB(2);
    }
    
    @Test
    public void testGetDilutorsDB() throws PPGSchedulerException {
        dbManager.getDilutorsDB();
    }
    
    
    @Test
    public void getFreeDilutorDate() throws PPGSchedulerException {
        dbManager.getFreeDilutorDate(2);
    }
    
    @Test
    public void getFilledDilutors() throws PPGSchedulerException {
        dbManager.getFilledDilutors();
    }
    
    @Test
    public void testGetBatchFromDatabase() throws PPGSchedulerException {
        dbManager = DatabaseManager.getInstance();
        dbManager.getBatchDB(2);
    }
    
    @Test
    public void testGetBatchesWeekly() throws PPGSchedulerException {
        dbManager.getBatchesWeekly();
    }
    
    @Test
    public void testDeleteBatch() throws PPGSchedulerException {
        dbManager.deleteBatch(12);
    }
}
