package org.ppg.model;

import java.util.ArrayList;

public class Solution {
    public static int idSolucionnext;
    private ArrayList<Batch> solutionBatches;
    private int idSolucion;
    
    public Solution(ArrayList<Batch> lotes) {
        this.solutionBatches = new ArrayList<>();
        for (Batch batch : lotes) {
            this.solutionBatches.add(batch.clone()); // Copia profunda
        }
    }
    
    public ArrayList<Batch> getLotes() {
        return this.solutionBatches;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Mismo objeto
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // Tipos diferentes
        }
        Solution another = (Solution) obj;
        
        // Comparar tamaños de las listas
        if (this.solutionBatches.size() != another.solutionBatches.size()) {
            return false;
        }
        
        // Comparar lotes uno por uno (orden importa)
        for (int i = 0; i < this.solutionBatches.size(); i++) {
            Batch batch1 = this.solutionBatches.get(i);
            Batch batch2 = another.solutionBatches.get(i);
            if (!batch1.equals(batch2)) { // Asegúrate de que Lote también sobrescriba equals correctamente
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
        return solutionBatches.hashCode(); // Usa el hashCode de la lista de lotes
    }
    
    @Override
    public String toString() {
        return "Solucion{lotes=" + solutionBatches + "}";
    }
}
