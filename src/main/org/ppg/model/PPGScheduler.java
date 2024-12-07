package org.ppg.model;

import javafx.beans.property.BooleanProperty;

import java.io.File;
import java.util.ArrayList;

public class PPGScheduler {
    private final DatabaseManager connection;
    //private BooleanProperty operationCompleted;

    public PPGScheduler(/*BooleanProperty operationCompleted*/) throws PPGSchedulerException {
        connection = DatabaseManager.getInstance();
        //operationCompleted.set(false);
        //this.operationCompleted = operationCompleted;
    }

    public ArrayList<Batch> insert(ArrayList<Batch> batches) throws PPGSchedulerException, CantAddException {
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
        //this.operationCompleted.set(true);
        return scheduler.getAllBatches();
    }

    public ArrayList<Batch> changeDate(Batch batch) {
        return null;
    }

    public ArrayList<Batch> remove() {
        return null;
    }
}
