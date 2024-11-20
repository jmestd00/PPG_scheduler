package org.ppg.model;

import java.util.ArrayList;
import java.util.List;

public class Solucion {
    private ArrayList<Lote> lotesSolucion;
    public static int idSolucionnext;
    private int idSolucion;


    public Solucion(ArrayList<Lote> lotes) {
        this.lotesSolucion = new ArrayList<>();
        for (Lote lote : lotes) {
            this.lotesSolucion.add(lote.clone()); // Copia profunda
        }
    }

    public ArrayList<Lote> getLotes() {
        return this.lotesSolucion;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Mismo objeto
        if (obj == null || getClass() != obj.getClass()) return false; // Tipos diferentes
        Solucion otra = (Solucion) obj;
    
        // Comparar tamaños de las listas
        if (this.lotesSolucion.size() != otra.lotesSolucion.size()) {
            return false;
        }
    
        // Comparar lotes uno por uno (orden importa)
        for (int i = 0; i < this.lotesSolucion.size(); i++) {
            Lote lote1 = this.lotesSolucion.get(i);
            Lote lote2 = otra.lotesSolucion.get(i);
            if (!lote1.equals(lote2)) { // Asegúrate de que Lote también sobrescriba equals correctamente
                return false;
            }
        }
    
        return true; // Todas las comparaciones pasaron
    }
    /**
     * @Override
    public boolean equals(Object obj) {
        return this.idSolucion==((Solucion)obj).idSolucion;
    }
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
