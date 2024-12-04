package org.ppg.model;


import java.io.File;
import java.util.ArrayList;

public class PPGScheduler {
    private final DatabaseManager connection;
    private final ArrayList<Batch> batchesToInsert = new ArrayList<>();
    
    public PPGScheduler() throws PPGSchedulerException {
        connection = DatabaseManager.getInstance();
    }
    
    public void schedule() throws PPGSchedulerException {
        File file = new File("soluciones.txt");
        if (file.exists()) {
            file.delete();
        }
        if (batchesToInsert.isEmpty()) {
            return;
        }
        SchedulingAlgorithm algorithm = new SchedulingAlgorithm();
        ArrayList<Batch> previewslyScheduledBatches = connection.getAllBatches();
        System.out.println(previewslyScheduledBatches.toString());
        algorithm.schedule(previewslyScheduledBatches, batchesToInsert);
        algorithm.saveSolutionsInFile();
        batchesToInsert.clear();
    }
    
    public void insert(ArrayList<Batch> batches) throws PPGSchedulerException, CantAddException {
        ArrayList<Dilutor> dilutors = connection.getFilledDilutors();
        Scheduler scheduler = new Scheduler(dilutors);
        for (Batch batch : batches) {
            scheduler.add(batch);
        }
    }
}
