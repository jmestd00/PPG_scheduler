package org.ppg.model;

import java.util.ArrayList;
import java.util.List;

public class Solucion {
    private ArrayList<Lote> lotesSolucion;


    public Solucion(ArrayList<Lote> lotes) {
        this.lotesSolucion = new ArrayList<>();
        for (Lote lote : lotes) {
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
