package org.ppg.model;

import java.util.ArrayList;

public class Diluidor {
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
    public void addLote(Lote lote)throws PPGSchedulerException{
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

    public ArrayList<Diluidor> sortDiluidores(ArrayList<Diluidor> diluidores) {
        for(int i = 0; i < diluidores.size(); i++) {
            for(int j = 0; j < diluidores.size(); j++) {
              if(diluidores.get(j).getCapacity() < diluidores.get(i).getCapacity()) {
                Diluidor temp = diluidores.get(i);
                diluidores.set(i, diluidores.get(j));
                diluidores.set(j, temp);
              }  
            }
        }
        return diluidores;
    }
}
