package org.ppg.model;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Dilutor {
    private final int id;
    private final String name;
    private final int capacity;
    private final TreeSet<Batch> batches;
    private LocalDate endDate;

    //Constructor
    public Dilutor(int id, String name, int capacity) {
        this.id = id;
        this.batches = new TreeSet<>();
        this.name = name;
        this.capacity = capacity;
    }

    //Getters
    public int getCapacity() {
        return this.capacity;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public LocalDate getFechaFin() {
        return this.endDate;
    }

    public ArrayList<Batch> getBatches() {
        return new ArrayList<>(batches);
    }

    public int getNumberOfBatches() {
        return batches.size();
    }

    //Setter
    public void setEndDate(LocalDate newDate) {
        this.endDate = newDate;
    }

    //Methods
    public void add(Batch batch) {
        batch.setDilutor(this.getId());
        batches.add(batch);
    }

    public LinkedList<Collision> collisions() {
        LinkedList<Collision> collisions = new LinkedList<>();
        ArrayList<Batch> list = new ArrayList<>(this.batches.size());
        list.addAll(this.batches);
        for (int i = 0; i < list.size() - 1; i++) {
            LocalDate endA = list.get(i).endDate();
            LocalDate endB = list.get(i + 1).endDate();
            LocalDate startB = list.get(i + 1).startDate();
            if (endA.isAfter(startB) || endA.equals(endB)) {
                long collisionFactor = endA.toEpochDay() - startB.toEpochDay();
                collisions.add(new Collision((int) collisionFactor, list.get(i)));
            }
        }
        // Ordenar las colisiones de mayor a menor factor de colisión
        collisions.sort((o1, o2) -> {
            if (o1.factor() < o2.factor()) return 1;
            else if (o1.factor() > o2.factor()) return -1;
            else if (o1.getCollidedBatch().getDuration() < o2.getCollidedBatch().getDuration()) return 1;
            else if (o1.getCollidedBatch().getDuration() > o2.getCollidedBatch().getDuration()) return -1;
            return 0;
        });
        return collisions;
    }

    public boolean hasAtLeastOneSolution(int maxDelay) {
        ArrayList<Batch> list = new ArrayList<>(this.batches.size());
        list.addAll(this.batches);
        try {
            findDefaultDelaysAdjustment(list, maxDelay);
            return true;
        } catch (NoAdjustmentFoundException e) {
            return false;
        }
    }

    public int[] findDelaysAdjustment(int maxDelay) throws NoAdjustmentFoundException {
        ArrayList<Batch> list = new ArrayList<>(this.batches.size());
        list.addAll(this.batches);
        int[] defaultDelaysAdjustment = findDefaultDelaysAdjustment(list, maxDelay);
        return findBestDelaysAdjustment(list, maxDelay, defaultDelaysAdjustment);
    }

    private int[] findDefaultDelaysAdjustment(ArrayList<Batch> list, int maxDelay) throws NoAdjustmentFoundException {
        boolean hasCollisions = false;
        int[] currentAdjustment = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            currentAdjustment[i] = list.get(i).getDelay();
        }
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                list.get(i).setDelay(0);
                LocalDate endA = list.get(i - 1).endDate();
                LocalDate startB = list.get(i).startDate();
                int offset = (int) (startB.toEpochDay() - endA.toEpochDay());
                if (offset > 0) {
                    if (offset >= maxDelay) {
                        list.get(i).setDelay(-maxDelay);
                    } else {
                        list.get(i).setDelay(-offset);
                    }
                } else if (offset < 0) {
                    if (offset > -maxDelay) {
                        list.get(i).setDelay(-offset);
                    } else if (offset == -maxDelay) {
                        list.get(i).setDelay(maxDelay);
                    } else {
                        hasCollisions = true;
                        break;
                    }
                }
            } else {
                list.get(i).setDelay(-maxDelay);
            }
        }

        if (hasCollisions) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setDelay(currentAdjustment[i]);
            }
            throw new NoAdjustmentFoundException("Dilutor with batches " + this.batches + " doesnt have any adjustment for max delay: " + maxDelay);
        } else {
            int[] adjustment = new int[list.size()];
            for (int i = 0; i < adjustment.length; i++) {
                adjustment[i] = list.get(i).getDelay();
            }
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setDelay(currentAdjustment[i]);
            }
            return adjustment;
        }
    }

    private int[] findBestDelaysAdjustment(ArrayList<Batch> list, int maxDelay, int[] defaultDelaysAdjustment) {
        int[] delaySamples = delaySamples(maxDelay);
        int[] cache = new int[list.size()];
        int[] depthPosition = new int[list.size()];
        int[] delayInDepthPosition = new int[list.size()];
        int[] currentAdjustment = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            currentAdjustment[i] = list.get(i).getDelay();
        }
        boolean hasCollisions = true;
        boolean shouldUseSolution = false;
        HashMap<String, Boolean> alreadyTested = new HashMap<>();
        int defaultSolutionSum = delaySum(defaultDelaysAdjustment);
        int count = 0;
        while (true) {
            if (alreadyTested.get(Arrays.toString(cache)) == null) {
                for (int i = 1; i < delaySamples.length && hasCollisions; i++) {
                    for (int j = 0; j < list.size() && hasCollisions; j++) {
                        for (int k = 0; k < list.size(); k++) {
                            if (k == j) {
                                list.get(k).setDelay(delaySamples[i]);
                            } else {
                                list.get(k).setDelay(cache[k]);
                            }
                        }
                        hasCollisions = hasCollisions(list);
                    }
                }
                count++;
            }
            if (hasCollisions) {
                int currentDelaysSum = delaySum(list);
                if (currentDelaysSum >= defaultSolutionSum) {
                    shouldUseSolution = true;
                    break;
                }

                if (count >= 10000) {
                    shouldUseSolution = true;
                    break;
                }


            } else {
                break;
            }
            alreadyTested.put(Arrays.toString(cache), true);
            depthPosition[0] = list.size();
            delayInDepthPosition[0] = delaySamples.length - 1;
            for (int i = 0; i < depthPosition.length; i++) {
                if (depthPosition[i] == list.size()) {
                    depthPosition[i] = 0;
                    delayInDepthPosition[i]++;
                    if (delayInDepthPosition[i] == delaySamples.length) {
                        delayInDepthPosition[i] = 0;
                        if (i < depthPosition.length - 1) {
                            depthPosition[i + 1]++;
                        }
                    }
                } else {
                    break;
                }
            }
            Arrays.fill(cache, 0);
            for (int i = depthPosition.length - 1; i > 0; i--) {
                cache[depthPosition[i]] = delaySamples[delayInDepthPosition[i]];
            }
        }
        if (shouldUseSolution) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setDelay(currentAdjustment[i]);
            }
            return defaultDelaysAdjustment;
        } else {
            int[] bestAdjustment = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                bestAdjustment[i] = list.get(i).getDelay();
            }
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setDelay(currentAdjustment[i]);
            }
            return bestAdjustment;
        }
    }

    /**
     * Verifica si hay colisiones en la lista de lotes.
     *
     * @param list Lista de lotes.
     * @return true si hay colisiones; false en caso contrario.
     */

    public boolean hasCollisions(ArrayList<Batch> list) {
        ArrayList<Batch> clone = new ArrayList<>();
        for (Batch b : list) {
            clone.add(b.clone());
        }
        clone.sort((batch1, batch2) -> {
            if (batch1.endDate().isAfter(batch2.endDate())) return 1;
            if (batch2.endDate().isAfter(batch1.endDate())) return -1;
            return 0;
        });
        boolean hasCollisions = false;
        for (int i = 0; i < clone.size() - 1; i++) {
            LocalDate endA = clone.get(i).endDate();
            LocalDate endB = clone.get(i + 1).endDate();
            LocalDate startA = clone.get(i + 1).startDate();
            if (endA.isEqual(endB) || endA.isAfter(startA)) {
                hasCollisions = true;
                break;
            }
        }
        return hasCollisions;
    }

    /*
    public boolean hasCollisions(ArrayList<Batch> list) throws InterruptedException {
        ArrayList<Batch> clone = new ArrayList<>();
        for (Batch b : list) {
            clone.add(b.clone());
        }
        // Ordenar la lista de clones
        clone.sort((batch1, batch2) -> {
            if (batch1.endDate().isAfter(batch2.endDate())) return 1;
            if (batch2.endDate().isAfter(batch1.endDate())) return -1;
            return 0;
        });
        AtomicBoolean hasCollisions = new AtomicBoolean(false); // Variable compartida para el resultado
        // Paralelizar la búsqueda de colisiones
        MultiTasker.perform((threadNumber, numberOfThreads) -> {
            // Dividir el trabajo entre los hilos
            int chunkSize = clone.size() / numberOfThreads;
            int start = threadNumber * chunkSize;
            int end = (threadNumber == numberOfThreads - 1) ? clone.size() - 1 : (start + chunkSize);
            for (int i = start; i < end && !hasCollisions.get(); i++) {
                LocalDate endA = clone.get(i).endDate();
                LocalDate endB = clone.get(i + 1).endDate();
                LocalDate startA = clone.get(i + 1).startDate();
                if (endA.isEqual(endB) || endA.isAfter(startA)) {
                    hasCollisions.set(true); // Actualizar el resultado de forma atómica
                    break;
                }
            }
        });
        return hasCollisions.get();
    }
    */


    private int[] delaySamples(int maxDelay) {
        int[] delaySamples = new int[(maxDelay * 2) + 1];
        for (int i = 1, x = 1; i <= maxDelay; i++, x += 2) {
            delaySamples[x] = i;
            delaySamples[x + 1] = -i;
        }
        return delaySamples;
    }


    private int delaySum(int[] adjustment) {
        int sum = 0;
        for (int value : adjustment) {
            int abs = value >= 0 ? value : -value;
            sum += abs;
        }
        return sum;
    }

    /*
    private int delaySum(int[] adjustment) throws InterruptedException {
        AtomicInteger sum = new AtomicInteger(0); // Variable compartida para acumular la suma
        MultiTasker.perform((threadNumber, numberOfThreads) -> {
            // Dividir el trabajo entre los hilos
            int chunkSize = adjustment.length / numberOfThreads;
            int start = threadNumber * chunkSize;
            int end = (threadNumber == numberOfThreads - 1) ? adjustment.length : (start + chunkSize);
            // Calcular la suma parcial para este hilo
            int localSum = 0;
            for (int i = start; i < end; i++) {
                localSum += Math.abs(adjustment[i]);
            }
            // Agregar la suma parcial a la suma total de forma atómica
            sum.addAndGet(localSum);
        });
        return sum.get();
    }
    */


    private int delaySum(ArrayList<Batch> list) {
        int sum = 0;
        for (Batch l : list) {
            int abs = l.getDelay() >= 0 ? l.getDelay() : -l.getDelay();
            sum += abs;
        }
        return sum;
    }


/*
    private int delaySum(ArrayList<Batch> list) throws InterruptedException {
        AtomicInteger sum = new AtomicInteger(0); // Variable compartida para acumular la suma
        MultiTasker.perform((threadNumber, numberOfThreads) -> {
            // Dividir el trabajo entre los hilos
            int chunkSize = list.size() / numberOfThreads;
            int start = threadNumber * chunkSize;
            int end = (threadNumber == numberOfThreads - 1) ? list.size() : (start + chunkSize);
            // Calcular la suma parcial para este hilo
            int localSum = 0;
            for (int i = start; i < end; i++) {
                int abs = Math.abs(list.get(i).getDelay());
                localSum += abs;
            }
            // Agregar la suma parcial a la suma total de forma atómica
            sum.addAndGet(localSum);
        });
        return sum.get();
    }

*/

    /**
     * Elimina un lote del conjunto.
     *
     * @param batch Lote a eliminar.
     */
    public void remove(Batch batch) {
        this.batches.remove(batch);
    }


    /**
     * Configura la solución de retrasos en los lotes.
     *
     * @param delayAdjustment Solución propuesta de retrasos.
     */
    public void setSolution(int[] delayAdjustment) {
        ArrayList<Batch> list = new ArrayList<>(batches.size());
        list.addAll(batches);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setDelay(delayAdjustment[i]);
        }
        batches.clear();
        batches.addAll(list);
    }

    public boolean isEmpty() {
        return batches.isEmpty();
    }

    /**
     * Representación en forma de cadena del sistema.
     *
     * @return Cadena que describe los lotes actuales.
     */
    @Override
    public String toString() {
        return "\nD" + this.getId() + "\tcapacity:" + this.getCapacity() + "\ttotal batches:" + this.getNumberOfBatches() + "\n" + batches + "\n";
    }
}
