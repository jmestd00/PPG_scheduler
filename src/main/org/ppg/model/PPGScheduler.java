package org.ppg.model;

import javafx.beans.property.BooleanProperty;

import java.io.File;
import java.util.ArrayList;

public class PPGScheduler {
    private final DatabaseManager connection;
    private final ArrayList<Batch> batchesToInsert = new ArrayList<>();
    //private BooleanProperty operationCompleted;

    public PPGScheduler(/*BooleanProperty operationCompleted*/) throws PPGSchedulerException {
        connection = org.ppg.model.DatabaseManager.getInstance();
        //operationCompleted.set(false);
        //this.operationCompleted = operationCompleted;
    }

    public void schedule() throws org.ppg.model.PPGSchedulerException {
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
        ArrayList<Dilutor> dilutors = connection.getDilutors();
        ArrayList<Batch> batchesDB = connection.getAllBatches();
        Scheduler scheduler = new Scheduler(dilutors);
        for (Batch b : batchesDB) {
            try {
                scheduler.add(b);
            } catch (CantAddException e) {
                break;
            }
        }
        System.out.println(scheduler);
        /*
        for (Batch batch : batches) {
            scheduler.add(batch);
        }
         */
        //this.operationCompleted.set(true);
    }
}
