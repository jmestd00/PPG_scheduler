package org.ppg.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public class Scheduler {
    private static final int MAX_DELAY_VALUE = 2;
    private final ArrayList<Dilutor> dilutors;

    //Constructor
    public Scheduler(ArrayList<Dilutor> dilutors) {
        this.dilutors = dilutors;
    }

    public void add(Batch batch) throws CantAddException {
        if (batch.getQuantity() > dilutors.getLast().getCapacity())
            throw new CantAddException("No se ha podido añadir el lote debido a que la cantidad del lote excede a la de los diluidores disponibles");
        boolean added = addRecursively(dilutors, batch, MAX_DELAY_VALUE);
        if (!added) throw new CantAddException("No se ha podido añadir el lote");
    }

    private boolean isNormalized() {
        double total = 0;
        double[] percentages = new double[dilutors.size()];
        boolean isNormalized = true;
        for (Dilutor d : dilutors) {
            total += d.getNumberOfBatches();
        }
        for (int i = 0; i < percentages.length; i++) {
            percentages[i] = dilutors.get(i).getNumberOfBatches() / total;
            //System.out.println("Dilutor " + i + " percentage:" + percentages[i]);
        }
        for (double p : percentages) {
            if (p >= 0.8d) {
                isNormalized = false;
                break;
            }
        }
        return isNormalized;
    }
    
    private boolean addRecursively(ArrayList<Dilutor> dilutors, Batch batch, int maxDelay) throws CantAddException {
        boolean added = false;
        boolean isNormalized = isNormalized();
        //Try adding without delays
        if (isNormalized) {
            try {
                added = simpleAdd(dilutors, batch, 0, null);
                System.out.println("+El lote " + batch + " se añadió por simpleAdd()");
                if (added) return true;
            } catch (CantAddException e) {
                System.out.println("-El lote " + batch + " no se pudo añadir por simpleAdd()");
            }
        } else {
            try {
                added = normalAdd(dilutors, batch, 0);
                System.out.println("+El lote " + batch + " se añadió por normalAdd()");
                if (added) return true;
            } catch (CantAddException e) {
                System.out.println("-El lote " + batch + " no se pudo añadir por normalAdd()");
            }
        }
        try {
            added = swappingAdd(dilutors, batch, 0);
            System.out.println("+El lote " + batch + " se añadió por swappingAdd()");
            if (added) return true;
        } catch (CantAddException e) {
            System.out.println("-El lote " + batch + " no se pudo añadir por swappingAdd()");
        }
        try {
            added = deepAdd(dilutors, batch, 0, new HashMap<>());
            System.out.println("+El lote " + batch + " se añadió por deepAdd()");
            if (added) return true;
        } catch (CantAddException e) {
            System.out.println("-El lote " + batch + " no se pudo añadir por deepAdd()");
        }
        //Start adjusting delays
        if (isNormalized) {
            try {
                added = simpleAdd(dilutors, batch, maxDelay, null);
                System.out.println("+El lote " + batch + " se añadió por simpleAdd()*");
                if (added) return true;
            } catch (CantAddException e) {
                System.out.println("-El lote " + batch + " no se pudo añadir por simpleAdd()*");
            }
        } else {
            try {
                added = normalAdd(dilutors, batch, maxDelay);
                System.out.println("+El lote " + batch + " se añadió por normalAdd()*");
                if (added) return true;
            } catch (CantAddException e) {
                System.out.println("-El lote " + batch + " no se pudo añadir por normalAdd()*");
            }
        }
        try {
            added = swappingAdd(dilutors, batch, maxDelay);
            System.out.println("+El lote " + batch + " se añadió por swappingAdd()*");
            if (added) return true;
        } catch (CantAddException e) {
            System.out.println("-El lote " + batch + " no se pudo añadir por swappingAdd()*");
        }
        try {
            added = deepAdd(dilutors, batch, maxDelay, new HashMap<>());
            System.out.println("+El lote " + batch + " se añadió por deepAdd()*");
            if (added) return true;
        } catch (CantAddException e) {
            System.out.println("-El lote " + batch + " no se pudo añadir por deepAdd()*");
        }
        throw new CantAddException("No se ha podido añadir el lote");
    }


    private boolean deepAdd(ArrayList<Dilutor> dilutors, Batch batch, int delay, HashMap<Dilutor, Boolean> skips) throws CantAddException {
        boolean added = false;
        for (Dilutor dilutor : dilutors) {
            if (batch.getQuantity() > dilutor.getCapacity()) continue;
            if (skips.containsKey(dilutor)) continue;
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
                    skips.put(dilutor, true);
                    try {
                        deepAdd(dilutors, collidedBatch, delay, skips);
                        try {
                            int[] adjustments = dilutor.findDelaysAdjustment(delay);
                            dilutor.setSolution(adjustments);
                            added = true;
                            skips.remove(dilutor);
                            break;
                        } catch (NoAdjustmentFoundException | InterruptedException e) {
                            dilutor.add(collidedBatch);
                            skips.remove(dilutor);
                        }
                    } catch (CantAddException e) {
                        dilutor.add(collidedBatch);
                        skips.remove(dilutor);
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
        throw new CantAddException("No se ha podido añadir el lote");
    }


    private boolean swappingAdd(ArrayList<Dilutor> dilutors, Batch batch, int delay) throws CantAddException {
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
                        } catch (NoAdjustmentFoundException | InterruptedException e) {
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
        throw new CantAddException("No se ha podido añadir el lote");
    }

    private boolean normalAdd(ArrayList<Dilutor> dilutors, Batch batch, int delay) throws CantAddException {
        ArrayList<Dilutor> normalizedDilutors = new ArrayList<>(dilutors.size());
        normalizedDilutors.addAll(dilutors);
        normalizedDilutors.sort(Comparator.comparingInt(Dilutor::getNumberOfBatches));
        boolean added = false;
        for (Dilutor dilutor : normalizedDilutors) {
            if (dilutor.getCapacity() >= batch.getQuantity()) {
                dilutor.add(batch);
                try {
                    int[] delaysAdjustments = dilutor.findDelaysAdjustment(delay);
                    dilutor.setSolution(delaysAdjustments);
                    added = true;
                    break;
                } catch (NoAdjustmentFoundException | InterruptedException e) {
                    dilutor.remove(batch);
                }
            }
        }
        if (added) return true;
        throw new CantAddException("No se ha podido añadir el lote");
    }

    private boolean simpleAdd(ArrayList<Dilutor> dilutors, Batch batch, int delay, Dilutor skip) throws CantAddException {
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
                } catch (NoAdjustmentFoundException | InterruptedException e) {
                    dilutor.remove(batch);
                }
            }
        }
        if (added) return true;
        throw new CantAddException("No se ha podido añadir el lote");
    }

    //Override
    @Override
    public String toString() {
        return this.dilutors.toString();
    }

    public int total() {
        int total = 0;
        for(Dilutor d:dilutors){
            total+=d.getNumberOfBatches();
        }
        return total;
    }
}
