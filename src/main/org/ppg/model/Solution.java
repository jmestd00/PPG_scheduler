package org.ppg.model;

import java.util.ArrayList;

public class Solution {
    public static int idSolucionnext;
    private ArrayList<Batch> lotesSolucion;
    private int idSolucion;
    
    public Solution(ArrayList<Batch> lotes) {
        this.lotesSolucion = new ArrayList<>();
        for (Batch lote : lotes) {
            this.lotesSolucion.add(lote.clone()); // Copia profunda
        }
    }
    
    public ArrayList<Batch> getLotes() {
        return this.lotesSolucion;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Mismo objeto
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // Tipos diferentes
        }
        Solution otra = (Solution) obj;
        
        // Comparar tamaños de las listas
        if (this.lotesSolucion.size() != otra.lotesSolucion.size()) {
            return false;
        }
        
        // Comparar lotes uno por uno (orden importa)
        for (int i = 0; i < this.lotesSolucion.size(); i++) {
            Batch lote1 = this.lotesSolucion.get(i);
            Batch lote2 = otra.lotesSolucion.get(i);
            if (!lote1.equals(lote2)) { // Asegúrate de que Lote también sobrescriba equals correctamente
                return false;
            }
        }
        
        return true; // Todas las comparaciones pasaron
    }
    
    /**
     * @Override public boolean equals(Object obj) {
     * return this.idSolucion==((Solucion)obj).idSolucion;
     * }
     */
    @Override
    public int hashCode() {
        return lotesSolucion.hashCode(); // Usa el hashCode de la lista de lotes
    }
    
    @Override
    public String toString() {
        return "Solucion{lotes=" + lotesSolucion + "}";
    }
}
