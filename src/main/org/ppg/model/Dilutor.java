package org.ppg.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Dilutor implements Comparable<Dilutor> {
    private final int id;
    private final String name;
    private final int capacity;
    private final ArrayList<Batch> batches;
    private LocalDate endDate;
    
    //Constructor
    public Dilutor(int id, String name, int capacity) {
        this.batches = new ArrayList<>();
        this.id = id;
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
        return this.batches;
    }
    
    //Setters
    public void setEndDate(LocalDate newDate) {
        this.endDate = newDate;
    }
    
    //Funcionalidad
    public void addLote(Batch batch) throws PPGSchedulerException {
        batches.add(batch);
    }
    
    //Override
    @Override
    public int compareTo(Dilutor d) {
        return Integer.compare(this.id, d.id);
    }
    
    @Override
    public String toString() {
        return "id: " + this.id + '\n' +
                "Name: " + this.name + '\n' +
                "Capacity: " + this.capacity + '\n' +
                "Batches: {\n" + batches + "\n}";
    }
}
