package org.ppg.model;

import javafx.beans.property.BooleanProperty;

import java.io.File;
import java.util.ArrayList;

public class PPGScheduler {
    private final DatabaseManager connection;
    private BooleanProperty operationCompleted;

    public PPGScheduler(BooleanProperty operationCompleted) throws PPGSchedulerException {
        connection = DatabaseManager.getInstance();
        operationCompleted.set(false);
        this.operationCompleted = operationCompleted;
    }

    public ArrayList<Batch> insert(ArrayList<Batch> newBatches) throws PPGSchedulerException, CantAddException {
        ArrayList<Batch> allBatches = connection.getAllBatches();
        ArrayList<Dilutor> dilutors = connection.getDilutors();
        Scheduler scheduler = new Scheduler();
        try {
            scheduler.add(dilutors, allBatches, newBatches);
        } catch (CantAddException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
        allBatches = scheduler.getAllBatches();
        ArrayList<Batch> batchesToInsert = new ArrayList<>();
        int count = newBatches.size();
        for (Batch updatedBatch : allBatches) {
            for (Batch insertBatch : newBatches) {
                if (insertBatch.getnBatch() == updatedBatch.getnBatch()) {
                    System.out.println(updatedBatch);
                    batchesToInsert.add(updatedBatch);
                    count--;
                    break;
                }
            }
            if (count == 0) {
                break;
            }
        }

        for (Batch batch : batchesToInsert) {
            connection.insertBatchDB(batch);
        }
        for (Batch batch : allBatches) {
            connection.updateBatchDates(batch);
        }
        operationCompleted.set(true);
        return batchesToInsert;
    }
}