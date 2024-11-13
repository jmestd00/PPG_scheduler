package org.ppg.model;

import java.util.ArrayList;

public class Diluidor implements Comparable<Diluidor> {
    private final int id;
    private final String name;
    private final int capacity;
    private final ArrayList<Lote> lotes;
    private Date fechafin;
    
    public Diluidor (int id, String name, int capacity){
        lotes = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }
    
    public void addLote(Lote lote) throws PPGSchedulerException{
        //TODO implementar la funcion add lote en la clase diluidor
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

    public Date getFechaFin() {
        return this.fechafin;
    }

    public void setFechaFin(Date newDate) {
        this.fechafin = newDate;
    }

    public static ArrayList<Diluidor> sortDiluidores(ArrayList<Diluidor> diluidores) {
        diluidores.sort(null);
        return diluidores;
    }

    public ArrayList<Lote> getLotes(){
        return this.lotes;
    }

    @Override
    public String toString() {
        return "id: " + this.id+'\n'+
                "name: " + this.name + '\n'+
                "capacity: " + this.capacity+'\n'+
                "Lotes: {\n" + lotes.toString() + "\n}";
    }
}
