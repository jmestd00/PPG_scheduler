package org.ppg.model;

import javafx.beans.property.BooleanProperty;

import java.io.File;
import java.util.ArrayList;

public class PPGScheduler {
    private final DatabaseManager connection;
    private final ArrayList<Batch> batchesToInsert = new ArrayList<>();
    //private BooleanProperty operationCompleted;

    public PPGScheduler(/*BooleanProperty operationCompleted*/) throws PPGSchedulerException {
        connection = DatabaseManager.getInstance();
        //operationCompleted.set(false);
        //this.operationCompleted = operationCompleted;
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
        ArrayList<Dilutor> dilutors = connection.getDilutors();
        ArrayList<Batch> batchesDB = connection.getAllBatches();
        Scheduler scheduler = new Scheduler(dilutors);
        ArrayList<Batch> notAdded = new ArrayList<>();
        for (Batch b : batchesDB) {
            try {
                scheduler.add(b);
            } catch (CantAddException e) {
                notAdded.add(b);
            }
        }

        for (Batch b : batches) {
            try {
                scheduler.add(b);
            } catch (CantAddException e) {
                notAdded.add(b);
            }
        }

        System.out.println("Total added: " + scheduler.total());
        System.out.println("Not added: " + notAdded.size());
        System.out.println("-----Added----");
        System.out.println(scheduler);
        System.out.println("-----Not added----");
        System.out.println(notAdded);

        //this.operationCompleted.set(true);
    }
}
