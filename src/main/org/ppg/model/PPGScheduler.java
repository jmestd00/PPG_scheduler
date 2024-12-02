package org.ppg.model;

import java.io.File;
import java.util.ArrayList;

public class PPGScheduler {
    private final DatabaseManager connection;
    public PPGScheduler() throws PPGSchedulerException {
        connection = DatabaseManager.getInstance();
    }
    private final ArrayList<Batch> batchesToInsert = new ArrayList<>();

    public void schedule() throws PPGSchedulerException {




        File file = new File("soluciones.txt");
        if(file.exists()){
            file.delete();
        }
        if (batchesToInsert.isEmpty()) return;
        SchedulingAlgorithm algorithm = new SchedulingAlgorithm();
        ArrayList<Batch> previewslyScheduledBatches = connection.getAllBatches();
        System.out.println(previewslyScheduledBatches.toString());
        algorithm.schedule(previewslyScheduledBatches, batchesToInsert);
        algorithm.guardarSolucionesEnArchivo();
        batchesToInsert.clear();
    }
    public void insert(ArrayList<Batch> batches) {
        batchesToInsert.addAll(batches);
    }
    public void undo() {
        //TODO lo haremos?
    }
    public void redo() {
        //TODO lo haremos?
    }
    public void remove() {
        //TODO lo haremos?
    }
    public void changeDate() {
        //TODO lo haremos?
    }
    public void filter() {
        //TODO lo haremos?
    }

    public void search() {
        //TODO lo haremos?
    }
}