package org.ppg.model;

import java.time.LocalDate;
import java.util.*;

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
        batches.add(batch);
    }
    
    public LinkedList<Collision> collisions() {
        LinkedList<Collision> collisions = new LinkedList<>();
        ArrayList<Batch> list = new ArrayList<>(batches.size());
        list.addAll(batches);
        
        // Iterar sobre los lotes para encontrar colisiones consecutivas
        for (int i = 0; i < list.size() - 1; i++) {
            LocalDate endDateCurrent = list.get(i).endDate();
            LocalDate startDateNext = list.get(i + 1).startDate();
            
            if (!endDateCurrent.isBefore(startDateNext)) {
                // Calcular el factor de colisión como diferencia en días
                long collisionFactor = endDateCurrent.toEpochDay() - startDateNext.toEpochDay();
                collisions.add(new Collision((int) collisionFactor, list.get(i)));
            }
        }
        
        // Ordenar las colisiones de mayor a menor factor de colisión
        collisions.sort((o1, o2) -> {
            if (o1.factor() < o2.factor()) {
                return 1;
            } else if (o1.factor() > o2.factor()) {
                return -1;
            }
            return -o1.getCollidedBatch().compareTo(o2.getCollidedBatch());
        });
        
        return collisions;
    }
    
    public boolean hasCollisionsAdjustingDelays(int maxDelay, int[] solution) {
        if (batches.isEmpty()) {
            return false;
        }
        ArrayList<Batch> list = new ArrayList<>(batches.size());
        
        for (Batch batch : batches) {
            list.add(batch.clone());
        }
        int[][] xs = new int[1][];
        boolean hasAtLeastOneSolution = hasAtLeastOneSolution(maxDelay, xs);
        if (hasAtLeastOneSolution) {
            boolean hasCollisions = hasCollisions(list);
            if (hasCollisions) {
                if (maxDelay > 0) {
                    boolean hasCollisionsAdjustingDelays = hasCollisionsAdjustingDelays(list, maxDelay, xs[0]);
                    if (hasCollisionsAdjustingDelays) {
                        return true;
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            solution[i] = list.get(i).getDelay();
                        }
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    
    /**
     * Verifica si existe al menos una solución válida ajustando retrasos dentro de un máximo permitido.
     *
     * @param maxDelay Máximo retraso permitido.
     * @param delays   Matriz para almacenar los retrasos encontrados.
     * @return true si existe al menos una solución; false en caso contrario.
     */
    public boolean hasAtLeastOneSolution(int maxDelay, int[][] delays) {
        ArrayList<Batch> copy = new ArrayList<>(batches.size());
        for (Batch batch : batches) {
            copy.add(batch.clone());
        }
        boolean hasSolution = true;
        for (int i = 0; i < copy.size() && hasSolution; i++) {
            if (i - 1 >= 0) {
                LocalDate prevEndDate = copy.get(i - 1).endDate();
                LocalDate currentStartDate = copy.get(i).startDate();
                if (!prevEndDate.isBefore(currentStartDate)) {
                    long offset = prevEndDate.toEpochDay() - currentStartDate.toEpochDay();
                    if (offset > maxDelay) {
                        hasSolution = false;
                    } else {
                        copy.get(i).setDelay((int) offset); // setDelay sigue usando un int.
                    }
                } else {
                    copy.get(i).setDelay(-maxDelay);
                    prevEndDate = copy.get(i - 1).endDate(); // Actualiza después del retraso.
                    currentStartDate = copy.get(i).startDate(); // Actualiza la fecha actual.
                    if (!prevEndDate.isBefore(currentStartDate)) {
                        long offset = prevEndDate.toEpochDay() - currentStartDate.toEpochDay();
                        if (offset > maxDelay) {
                            hasSolution = false;
                        } else {
                            copy.get(i).setDelay((int) -offset);
                        }
                    }
                }
            } else {
                copy.get(i).setDelay(-maxDelay);
            }
        }
        delays[0] = new int[copy.size()];
        for (int i = 0; i < delays[0].length; i++) {
            delays[0][i] = copy.get(i).getDelay();
        }
        return hasSolution;
    }
    
    
    /**
     * Verifica si hay colisiones ajustando los retrasos en base a las muestras de retraso.
     *
     * @param list     Lista de lotes.
     * @param maxDelay Máximo retraso permitido.
     * @param solution Solución propuesta de retrasos.
     * @return true si hay colisiones; false en caso contrario.
     */
    private boolean hasCollisionsAdjustingDelays(ArrayList<Batch> list, int maxDelay, int[] solution) {
        int[] delaySamples = new int[(maxDelay * 2) + 1];
        int[] cache = new int[list.size()];
        int[] depthPosition = new int[list.size()];
        int[] delayInDepthPosition = new int[list.size()];
        boolean hasCollisions = true;
        boolean shouldUseSolution = false;
        HashMap<String, Boolean> alreadyTested = new HashMap<>();
        int delaysSum = 0;
        for (int value : solution) {
            int abs = value >= 0 ? value : -value;
            delaysSum += abs;
        }
        for (int i = 1, x = 1; i <= maxDelay; i++, x += 2) {
            delaySamples[x] = i;
            delaySamples[x + 1] = -i;
        }
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
                int currentDelaysSum = 0;
                for (Batch batch : list) {
                    int abs = batch.getDelay() >= 0 ? batch.getDelay() : -batch.getDelay();
                    currentDelaysSum += abs;
                }
                if (currentDelaysSum >= delaysSum) {
                    shouldUseSolution = true;
                    break;
                }
                if (count >= 1000) {
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
            boolean isLast = true;
            for (int i = 1; i < cache.length; i++) {
                isLast = isLast && cache[i] == delaySamples[delaySamples.length - 1];
            }
            if (isLast) {
                break;
            }
        }
        if (shouldUseSolution) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setDelay(solution[i]);
            }
            hasCollisions = false;
        }
        return hasCollisions;
    }
    
    /**
     * Verifica si hay colisiones en la lista de lotes.
     *
     * @param list Lista de lotes.
     * @return true si hay colisiones; false en caso contrario.
     */
    public boolean hasCollisions(ArrayList<Batch> list) {
        // Ordenar la lista basada en las fechas de finalización (endDate()()) y duración
        list.sort((o1, o2) -> {
            if (o1.endDate().isAfter(o2.endDate())) {
                return 1;
            } else if (o1.endDate().isBefore(o2.endDate())) {
                return -1;
            }
            if (o1.getDuration() > o2.getDuration()) {
                return 1;
            }
            return -1;
        });
        
        boolean hasCollisions = false;
        
        // Verificar colisiones entre lotes consecutivos
        for (int i = 0; i < list.size() - 1; i++) {
            LocalDate endDateCurrent = list.get(i).endDate();
            LocalDate startDateNext = list.get(i + 1).startDate();
            
            if (!endDateCurrent.isBefore(startDateNext)) {
                hasCollisions = true;
                break;
            }
        }
        
        return hasCollisions;
    }
    
    /**
     * Elimina un lote del conjunto.
     *
     * @param batch Lote a eliminar.
     */
    public void remove(Batch batch) {
        this.batches.remove(batch);
    }
    
    /**
     * Representación en forma de cadena del sistema.
     *
     * @return Cadena que describe los lotes actuales.
     */
    @Override
    public String toString() {
        return "\nD:" + batches + "\n";
    }
    
    /**
     * Configura la solución de retrasos en los lotes.
     *
     * @param solution Solución propuesta de retrasos.
     */
    public void setSolution(int[] solution) {
        int i = 0;
        for (Batch batch : batches) {
            batch.setDelay(solution[i]);
            i++;
        }
    }
}
