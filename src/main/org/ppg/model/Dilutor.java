package org.ppg.model;

import java.util.ArrayList;

public class Dilutor {
    private final int id;
    private final String name;
    private final int capacity;
    private final ArrayList<Batch> lotes;
    private Date fechafin;
    public Dilutor(int id, String name, int capacity){
        lotes = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }
    public void addLote(Batch lote)throws PPGSchedulerException{
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

    public Date getFechaFin() {
        return this.fechafin;
    }

    public void setFechaFin(Date newDate) {
        this.fechafin = newDate;
    }

    public static ArrayList<Dilutor> sortDiluidores(ArrayList<Dilutor> diluidores) {
        for(int i = 0; i < diluidores.size(); i++) {
            for(int j = 0; j < diluidores.size(); j++) {
              if(diluidores.get(j).getCapacity() < diluidores.get(i).getCapacity()) {
                Dilutor temp = diluidores.get(i);
                diluidores.set(i, diluidores.get(j));
                diluidores.set(j, temp);
              }  
            }
        }
        return diluidores;
    }


    public ArrayList<Batch> getLotes(){
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
