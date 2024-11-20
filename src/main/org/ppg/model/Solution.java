package org.ppg.model;
import java.util.ArrayList;

public class Solution {
    private ArrayList<Batch> lotesSolucion;


    public Solution(ArrayList<Batch> lotes) {
        this.lotesSolucion = new ArrayList<>();
        for (Batch lote : lotes) {
            this.lotesSolucion.add(lote.clone()); // Copia profunda
        }
    }

    public ArrayList getLotes() {
        return this.lotesSolucion;
    }

    @Override
    public String toString() {
        return "Solucion{lotes=" + lotesSolucion + "}";
    }
}
