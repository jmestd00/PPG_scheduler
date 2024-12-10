package org.ppg.model;

import java.util.ArrayList;

public class Scheduler {
    private static final int MAX_DELAY_VALUE = 2;
    private ArrayList<Dilutor> dilutors;

    //Constructor
    public Scheduler() {
        this.dilutors = new ArrayList<>();
    }

    public void add(ArrayList<Dilutor> dilutors, ArrayList<Batch> allBatches, ArrayList<Batch> batch) throws CantAddException {
        if (dynamicAdd(dilutors, allBatches, batch, MAX_DELAY_VALUE)) {
            System.out.println("+El lote " + batch + " se añadió correctamente");
        } else {
            System.out.println("-El lote " + batch + " no se pudo añadir");
        }
    }

    public boolean dynamicAdd(ArrayList<Dilutor> dilutors, ArrayList<Batch> allBatches, ArrayList<Batch> newBatch, int delay) throws CantAddException {
        allBatches.addAll(newBatch);
        allBatches.sort((o1, o2) -> {
            if (o1.getQuantity() > o2.getQuantity()) return 1;
            else if (o1.getQuantity() < o2.getQuantity()) return -1;
            return 0;
        });
        class PseudoDilutor {
            int capacity = 0;
            ArrayList<Batch> batches = new ArrayList<>();

            PseudoDilutor(int capacity) {
                this.capacity = capacity;
            }

            @Override
            public String toString() {
                return "\ncapacity:" + capacity + "\n" + batches;
            }
        }
        ArrayList<PseudoDilutor> pseudoDilutors = new ArrayList<>();
        for (Dilutor dilutor : dilutors) {
            pseudoDilutors.add(new PseudoDilutor(dilutor.getCapacity()));
        }

        for (PseudoDilutor dilutor : pseudoDilutors) {
            while (true) {
                if (allBatches.getFirst().getQuantity() <= dilutor.capacity) {
                    dilutor.batches.add(allBatches.getFirst());
                    allBatches.removeFirst();
                    if (allBatches.isEmpty()) {
                        break;
                    }
                } else {
                    break;
                }
            }
            if (allBatches.isEmpty()) {
                break;
            }
        }

        for (PseudoDilutor dilutor : pseudoDilutors) {
            dilutor.batches.sort((batch1, batch2) -> {
                if (batch1.endDate().isAfter(batch2.endDate())) return 1;
                if (batch2.endDate().isAfter(batch1.endDate())) return -1;
                return 0;
            });
        }

        ArrayList<Batch> notAdded = new ArrayList<>();
        for (int i = 0; i < pseudoDilutors.size(); i++) {
            for (Batch batch : pseudoDilutors.get(i).batches) {
                dilutors.get(i).add(batch);
                if (!dilutors.get(i).hasAtLeastOneSolution(delay)) {
                    dilutors.get(i).remove(batch);
                    notAdded.add(batch);
                }
            }
        }
        notAdded.sort((batch1, batch2) -> {
            if (batch1.endDate().isAfter(batch2.endDate())) return 1;
            if (batch2.endDate().isAfter(batch1.endDate())) return -1;
            return 0;
        });
        ArrayList<Batch> finallyNotAdded = new ArrayList<>();
        for (Batch batch : notAdded) {
            for (Dilutor dilutor : dilutors) {
                if (batch.getQuantity() <= dilutor.getCapacity()) {
                    dilutor.add(batch);
                    if (!dilutor.hasAtLeastOneSolution(delay)) {
                        dilutor.remove(batch);
                        if (dilutor == dilutors.getLast()) {
                            finallyNotAdded.add(batch);
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        if (!finallyNotAdded.isEmpty()) {
            throw new CantAddException("No se ha podido añadir el lote. Quedan fuera: "+finallyNotAdded);
        }
        for (Dilutor d : dilutors) {
            try {
                int[] adjustments = d.findDelaysAdjustment(delay);
                d.setSolution(adjustments);
            } catch (NoAdjustmentFoundException ignored) {
                System.out.println("Alerta!!!!");
                System.out.println(d);
            }
        }
        this.dilutors = dilutors;
        return true;
    }

    public int total() {
        int total = 0;
        for (Dilutor d : dilutors) {
            total += d.getNumberOfBatches();
        }
        return total;
    }

    public ArrayList<Batch> getAllBatches() {
        ArrayList<Batch> output = new ArrayList<>(total());
        for (Dilutor d : dilutors) {
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
