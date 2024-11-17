package org.ppg.model;

import java.time.LocalDate;

public class Lote {
    private Estados estado;
    private LocalDate fechaNecesidad;
    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
    private String tipo, planta, planningClass, item;
    private int cantidad;
    private final int id;
    private int idDiluidor;
    private int stock;

    public Lote(int id, String planningClass, String planta, String item, int cantidad, String tipo,
            LocalDate fechaInicio, LocalDate fechaFinal, LocalDate fechaNecesidad, Estados estado,  int idDiluidor, int stock) {
        this.id = id;
        this.planningClass = planningClass;
        this.planta = planta;
        this.item = item;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.fechaNecesidad = fechaNecesidad;
        this.estado = estado;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.idDiluidor = idDiluidor;
        this.stock = stock;
    }

    public int getCantidad() {
        return this.cantidad;
    }


    @Override
    public String toString() {
        return "Lote{" +
                "id=" + id +
                ", estados=" + estado +
                ", fechaNecesidad=" + fechaNecesidad +
                ", fechaInicio=" + fechaInicio +
                ", fechaFinal=" + fechaFinal +
                ", tipo='" + tipo + '\'' +
                ", planta='" + planta + '\'' +
                ", planningClass='" + planningClass + '\'' +
                ", item='" + item + '\'' +
                ", cantidad=" + cantidad +
                '}';
    }

    public String getTipo() {
        return this.tipo;
    }

    public String getPlant() {
        return this.planta;
    }

    public int getIdDiluidor() {
        return this.idDiluidor;
    }

    public String getPlannigClass() {
        return this.planningClass;
    }

    public String getEstado() {
        return this.estado.getValue();
    }

    public String getItem() {
        return this.item;
    }

    public int getId() {
        return this.id;
    }

    public LocalDate getFechaInicio() {
        return this.fechaInicio;
    }

    public LocalDate getFechaFinal() {
        return this.fechaFinal;
    }

    public LocalDate getFechaNecesidad() {
        return this.fechaNecesidad;
    }

    public int getStock() {
        return this.stock;
    }
}
