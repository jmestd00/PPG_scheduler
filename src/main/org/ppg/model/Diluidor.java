package org.ppg.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Diluidor implements Comparable<Diluidor> {
    private final int id;
    private final String name;
    private final int capacity;
    private final ArrayList<Lote> lotes;
    private LocalDate fechafin;
    public Diluidor (int id, String name, int capacity){
        lotes = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }
    public void addLote(Lote lote)throws PPGSchedulerException{
        lotes.add(lote);
    }
    public int getCapacity(){
        return this.capacity;
    }
    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }

    public LocalDate getFechaFin() {
        return this.fechafin;
    }

    public void setFechaFin(LocalDate newDate) {
        this.fechafin = newDate;
    }

    @Override
    public int compareTo(Diluidor o) {
        return Integer.compare(this.id, o.id);
    }


    public ArrayList<Lote> getLotes(){
        return this.lotes;
    }

    @Override
    public String toString() {
        return "id: " + this.id+'\n'+
                "name: " + this.name + '\n'+
                "capacity: " + this.capacity+'\n'+
                "Lotes: {\n" + lotes + "\n}";
    }
}
