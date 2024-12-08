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

    public ArrayList<Batch> insert(ArrayList<Batch> newBatches) throws PPGSchedulerException, CantAddException {
        ArrayList<Batch> allBatches = connection.getAllBatchesBasic();
        ArrayList<Dilutor> dilutors = connection.getDilutors();
        Scheduler scheduler = new Scheduler();
        try {
            scheduler.add(dilutors, allBatches, newBatches);
        } catch (CantAddException e) {
            throw new PPGSchedulerException("No se pudieron añadir los lotes");
        }
        for (Batch b : allBatches) {
            connection.updateBatchDates(b);
        }
        for(Batch batch:newBatches){
            connection.insertBatchDB(batch);
        }
        return connection.getUpdatedBatches(newBatches);
    }

    public ArrayList<Batch> changeDate(Batch batch) {
        return null;
    }

    public ArrayList<Batch> remove() {
        return null;
    }
}