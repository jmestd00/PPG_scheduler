package org.ppg.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

public class Scheduler {
    private static final int MAX_DELAY_VALUE = 2;
    private final ArrayList<Dilutor> dilutors;
    
    //Constructor
    public Scheduler(ArrayList<Dilutor> dilutors) {
        this.dilutors = dilutors;
    }
    
    //Getter
    public int getDWithMinimumCapacity(Batch batch) {
        int indexToDTarget = -1;
        for (int i = 0; i < dilutors.size(); i++) {
            int currentDifference = dilutors.get(i).getCapacity() - batch.getQuantity();
            if (currentDifference >= 0) {
                indexToDTarget = i;
                break;
            }
        }
        return indexToDTarget;
    }
    
    //Methods
    public void add(Batch batch) throws CantAddException {
        dilutors.sort(Comparator.comparingInt(Dilutor::getCapacity));
        boolean added = addRecursively(dilutors, batch, MAX_DELAY_VALUE);
        if (!added) {
            throw new CantAddException("No se ha podido añadir el lote");
        }
    }
    
    private boolean addRecursively(ArrayList<Dilutor> dilutors, Batch batch, int maxDelay) throws CantAddException {
        boolean added = false;
        int minDilutor = getDWithMinimumCapacity(batch);
        //Try adding without delays
        try {
            added = normalAdd(dilutors, batch, 0);
            System.out.println("+El lote " + batch + " se añadió por normalAdd()");
            if (added) {
                return true;
            }
        } catch (CantAddException e) {
            System.out.println("-El lote " + batch + " no se pudo añadir por normalAdd()");
        }
        try {
            added = deepAdd(dilutors, batch, 0);
            System.out.println("+El lote " + batch + " se añadió por deepAdd()");
            if (added) {
                return true;
            }
        } catch (CantAddException e) {
            System.out.println("-El lote " + batch + " no se pudo añadir por deepAdd()");
        }
        //Start adjusting delays
        try {
            added = normalAdd(dilutors, batch, maxDelay);
            System.out.println("+El lote " + batch + " se añadió por normalAdd()*");
            if (added) {
                return true;
            }
        } catch (CantAddException e) {
            System.out.println("-El lote " + batch + " no se pudo añadir por normalAdd()*");
        }
        try {
            added = deepAdd(dilutors, batch, maxDelay);
            System.out.println("+El lote " + batch + " se añadió por deepAdd()*");
            if (added) {
                return true;
            }
        } catch (CantAddException e) {
            System.out.println("-El lote " + batch + " no se pudo añadir por deepAdd()*");
        }
        throw new CantAddException("No se ha podido añadir el lote");
    }
    
    private boolean deepAddRec(ArrayList<Dilutor> dilutors, Batch batch, int delay, int targetDilutor, int skip) {
        if (targetDilutor == skip) {
            targetDilutor++;
        }
        if (targetDilutor >= dilutors.size()) {
            return false;
        }
        dilutors.get(targetDilutor).add(batch);
        int[] solution = new int[dilutors.get(targetDilutor).getNumberOfBatches()];
        boolean hasCollisions = dilutors.get(targetDilutor).hasCollisionsAdjustingDelays(delay, solution);
        if (!hasCollisions) {
            dilutors.get(targetDilutor).setSolution(solution);
            return true;
        }
        boolean added = false;
        LinkedList<Collision> collisions = dilutors.get(targetDilutor).collisions();
        while (!collisions.isEmpty()) {
            Batch collidedBatch = collisions.getFirst().getCollidedBatch();
            dilutors.get(targetDilutor).remove(collidedBatch);
            boolean stillHasCollisions = dilutors.get(targetDilutor).hasCollisionsAdjustingDelays(delay, solution);
            if (stillHasCollisions) {
                dilutors.get(targetDilutor).add(collidedBatch);
                collisions.removeFirst();
            } else {
                boolean addedInOtherDilutors;
                if (collidedBatch.equals(batch)) {
                    addedInOtherDilutors = deepAddRec(dilutors, collidedBatch, delay, targetDilutor + 1, targetDilutor);
                } else {
                    addedInOtherDilutors = deepAddRec(dilutors, collidedBatch, delay, getDWithMinimumCapacity(collidedBatch), targetDilutor);
                }
                if (addedInOtherDilutors) {
                    dilutors.get(targetDilutor).setSolution(solution);
                    added = true;
                    break;
                } else {
                    dilutors.get(targetDilutor).add(collidedBatch);
                    collisions.removeFirst();
                }
            }
        }
        if (!added) {
            dilutors.get(targetDilutor).remove(batch);
            return false;
        }
        return true;
        
    }
    
    private boolean deepAdd(ArrayList<Dilutor> dilutors, Batch batch, int delay) throws CantAddException {
        boolean added = deepAddRec(dilutors, batch, delay, getDWithMinimumCapacity(batch), -1);
        if (added) {
            return true;
        }
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
                int[] solution = new int[dilutor.getNumberOfBatches()];
                boolean hasCollisions = dilutor.hasCollisionsAdjustingDelays(delay, solution);
                if (!hasCollisions) {
                    added = true;
                    dilutor.setSolution(solution);
                    break;
                } else {
                    dilutor.remove(batch);
                }
            }
        }
        if (added) {
            return true;
        }
        throw new CantAddException("No se ha podido añadir el lote");
    }
    
    //Override
    @Override
    public String toString() {
        return this.dilutors.toString();
    }
}
