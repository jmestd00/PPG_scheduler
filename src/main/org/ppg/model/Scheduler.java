package org.ppg.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public class Scheduler {
    private static final int MAX_DELAY_VALUE = 2;
    private ArrayList<Dilutor> dilutors;

    //Constructor
    public Scheduler(ArrayList<Dilutor> dilutors) {
        this.dilutors = dilutors;
    }

    public void add(Batch batch) throws CantAddException {
        if (batch.getQuantity() > dilutors.getLast().getCapacity())
            throw new CantAddException("No se ha podido añadir el lote debido a que la cantidad del lote excede a la de los diluidores disponibles");
        boolean added = add(dilutors, batch, MAX_DELAY_VALUE);
        if (!added) throw new CantAddException("No se ha podido añadir el lote");
    }

    private boolean add(ArrayList<Dilutor> dilutors, Batch batch, int maxDelay) throws CantAddException {
        boolean added = false;

        /*
        try {
            added = simpleAdd(dilutors, batch, 0, null);
            //System.out.println("+El lote " + batch + " se añadió por simpleAdd()");
            if (added) return true;
        } catch (CantAddException e) {
            //System.out.println("-El lote " + batch + " no se pudo añadir por simpleAdd()");
        }
        try {
            added = swappingAdd(dilutors, batch, 0);
            //System.out.println("+El lote " + batch + " se añadió por swappingAdd()");
            if (added) return true;
        } catch (CantAddException e) {
            //System.out.println("-El lote " + batch + " no se pudo añadir por swappingAdd()");
        }
        //Start adjusting delays
        try {
            added = simpleAdd(dilutors, batch, maxDelay, null);
            //System.out.println("+El lote " + batch + " se añadió por simpleAdd()*");
            if (added) return true;
        } catch (CantAddException e) {
            //System.out.println("-El lote " + batch + " no se pudo añadir por simpleAdd()*");
        }
        try {
            added = swappingAdd(dilutors, batch, maxDelay);
            //System.out.println("+El lote " + batch + " se añadió por swappingAdd()*");
            if (added) return true;
        } catch (CantAddException e) {
            //System.out.println("-El lote " + batch + " no se pudo añadir por swappingAdd()*");
        }
        */
        try {
            added = dynamicAdd(batch, maxDelay);
            System.out.println("+El lote " + batch + " se añadió por dynamicAdd()*");
            if (added) return true;
        } catch (CantAddException e) {
            System.out.println("-El lote " + batch + " no se pudo añadir por dynamicAdd()*");
        }
        throw new CantAddException("No se ha podido añadir el lote");
    }

    public boolean dynamicAdd(Batch batch, int delay) throws CantAddException {
        LinkedList<Batch> allBatches = new LinkedList<>();
        for (Dilutor d : dilutors) {
            for (Batch b : d.getBatches()) {
                allBatches.add(b.clone());
            }
        }
        allBatches.add(batch);
        allBatches.sort((o1, o2) -> {
            if (o1.getQuantity() > o2.getQuantity()) return 1;
            else if (o1.getQuantity() < o2.getQuantity()) return -1;
            return 0;
        });
        ArrayList<ArrayList<Batch>> batchesSeparatedByCapacity = new ArrayList<>(dilutors.size());
        for (Dilutor d : dilutors) {
            ArrayList<Batch> batches = new ArrayList<>();
            while (!allBatches.isEmpty()) {
                if (allBatches.getFirst().getQuantity() <= d.getCapacity()) {
                    batches.add(allBatches.getFirst());
                    allBatches.removeFirst();
                } else {
                    break;
                }
            }
            batchesSeparatedByCapacity.add(batches);
        }
        ArrayList<Dilutor> dilutorsCopy = new ArrayList<>(dilutors.size());
        for (Dilutor d : dilutors) {
            dilutorsCopy.add(new Dilutor(d.getId(), d.getName(), d.getCapacity()));
        }
        ArrayList<Batch> notAddedBatches = new ArrayList<>();
        for (int i = 0; i < batchesSeparatedByCapacity.size(); i++) {
            ArrayList<Batch> batches = batchesSeparatedByCapacity.get(i);
            for (Batch b : batches) {
                dilutorsCopy.get(i).add(b);
                if (!dilutorsCopy.get(i).hasAtLeastOneSolution(delay)) {
                    dilutorsCopy.get(i).remove(b);
                    notAddedBatches.add(b);
                }
            }
        }
        boolean hasSolution = true;
        while (!notAddedBatches.isEmpty() && hasSolution) {
            for (Dilutor dilutor : dilutorsCopy) {
                if (notAddedBatches.getLast().getQuantity() <= dilutor.getCapacity()) {
                    dilutor.add(notAddedBatches.getLast());
                    if (dilutor.hasAtLeastOneSolution(delay)) {
                        notAddedBatches.removeLast();
                        break;
                    } else {
                        if (dilutor == dilutorsCopy.getLast()) {
                            hasSolution = false;
                            break;
                        } else {
                            dilutor.remove(notAddedBatches.getLast());
                        }
                    }
                }
            }
        }
        if (!notAddedBatches.isEmpty() || !hasSolution) {
            throw new CantAddException("No se ha podido añadir el lote");
        }
        for (Dilutor d : dilutorsCopy) {
            try {
                int[] adjustments = d.findDelaysAdjustment(delay);
                d.setSolution(adjustments);
            } catch (NoAdjustmentFoundException ignored) {
                System.out.println("Alerta!!!!");
            }
        }
        this.dilutors = dilutorsCopy;
        return true;
    }

    private boolean swappingAdd(ArrayList<Dilutor> dilutors, Batch batch, int delay) throws CantAddException {
        /*
        boolean added = false;
        for (Dilutor dilutor : dilutors) {
            if (batch.getQuantity() > dilutor.getCapacity()) continue;
            if (dilutor.isEmpty()) {
                dilutor.add(batch);
                added = true;
                break;
            }
            dilutor.add(batch);
            LinkedList<Collision> collisions = dilutor.collisions();
            if (collisions.isEmpty()) {
                added = true;
                break;
            }
            for (Collision c : collisions) {
                Batch collidedBatch = c.getCollidedBatch();
                if (collidedBatch == batch) continue;
                dilutor.remove(collidedBatch);
                if (dilutor.hasAtLeastOneSolution(delay)) {
                    try {
                        simpleAdd(dilutors, collidedBatch, delay, dilutor);
                        try {
                            int[] delaysAdjustments = dilutor.findDelaysAdjustment(delay);
                            dilutor.setSolution(delaysAdjustments);
                            added = true;
                            break;
                        } catch (NoAdjustmentFoundException e) {
                            dilutor.add(collidedBatch);
                        }
                    } catch (CantAddException e) {
                        dilutor.add(collidedBatch);
                    }
                } else {
                    dilutor.add(collidedBatch);
                }
            }
            if (!added) {
                dilutor.remove(batch);
            } else {
                break;
            }
        }
        if (added) return true;

         */
        throw new CantAddException("No se ha podido añadir el lote");
    }

    private boolean simpleAdd(ArrayList<Dilutor> dilutors, Batch batch, int delay, Dilutor skip) throws CantAddException {
        /*
        boolean added = false;
        for (Dilutor dilutor : dilutors) {
            if (dilutor == skip) continue;
            if (dilutor.getCapacity() >= batch.getQuantity()) {
                dilutor.add(batch);
                try {
                    int[] delaysAdjustments = dilutor.findDelaysAdjustment(delay);
                    dilutor.setSolution(delaysAdjustments);
                    added = true;
                    break;
                } catch (NoAdjustmentFoundException e) {
                    dilutor.remove(batch);
                }
            }
        }
        if (added) return true;

         */
        throw new CantAddException("No se ha podido añadir el lote");
    }

    public int total() {
        int total = 0;
        for (Dilutor d : dilutors) {
            total += d.getNumberOfBatches();
        }
        return total;
    }

    public ArrayList<Batch> getAllBatches(){

        ArrayList<Batch> output = new ArrayList<>(total());
        for(Dilutor d:dilutors){
            output.addAll(d.getBatches());
        }
        output.sort(null);
        return output;
    }

    //Override
    @Override
    public String toString() {
        return this.dilutors.toString();
    }

}
