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
        for (Batch batch : newBatches) {
            connection.insertBatchDB(batch);
        }
        operationCompleted.set(true);
        return connection.getUpdatedBatches(newBatches);
    }

    public ArrayList<Batch> changeDate(Batch batch) throws PPGSchedulerException {
        try {
            connection.updateBatchDB(batch);
            ArrayList<Batch> allBatches = connection.getAllBatchesBasic();
            ArrayList<Dilutor> dilutors = connection.getDilutors();
            Scheduler scheduler = new Scheduler();
            try {
                scheduler.add(dilutors, allBatches, null);
                for (Batch b : allBatches) {
                    connection.updateBatchDates(b);
                }
                return connection.getAllBatches();
            } catch (CantAddException e) {
                throw new PPGSchedulerException("No se ha podido actualzar el lote");
            }


        } catch (PPGSchedulerException e) {
            throw new PPGSchedulerException("No se ha podido actualzar el lote");
        }
    }

    public ArrayList<Batch> remove(Batch batch) throws PPGSchedulerException {
        try {
            connection.deleteBatch(batch.getnBatch());
            ArrayList<Batch> allBatches = connection.getAllBatchesBasic();
            ArrayList<Dilutor> dilutors = connection.getDilutors();
            Scheduler scheduler = new Scheduler();
            try {
                scheduler.add(dilutors, allBatches, null);
                for (Batch b : allBatches) {
                    connection.updateBatchDates(b);
                }
                return connection.getAllBatches();
            } catch (CantAddException e) {
                throw new PPGSchedulerException("No se ha podido actualzar el lote");
            }
        } catch (PPGSchedulerException e) {
            throw new PPGSchedulerException("No se ha podido actualzar el lote");
        }
    }
}