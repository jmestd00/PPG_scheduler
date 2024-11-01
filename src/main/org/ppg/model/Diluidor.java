package org.ppg.model;

import java.util.ArrayList;

public class Diluidor {
    private final int id;
    private final String name;
    private final int capacity;
    private final ArrayList<Lote> lotes;
    public Diluidor (int id, String name, int capacity){
        lotes = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }
    public void addLote(Lote lote)throws PPGSchedulerException{
        //TODO implementar la funcion add lote en la clase diluidor
    }
    public int capacity(){
        return this.capacity;
    }
    public int id(){
        return this.id;
    }
    public String name(){
        return this.name;
    }
}
