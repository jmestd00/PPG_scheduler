package org.ppg.model;

public class Lote {
    private Estados estados;
    private Date fechaNecesidad;
    private Date fechaInicio;
    private Date fechaFinal;
    private String tipo, planta, planningClass, item;
    private int cantidad;
    private final int id;

    public Lote(int id, String planningClass, String planta, String item, int cantidad, String tipo,
            Date fechaInicio, Date fechaFinal, Date fechaNecesidad, Estados estado) {
        this.id = id;
        this.planningClass = planningClass;
        this.planta = planta;
        this.item = item;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.fechaNecesidad = fechaNecesidad;
        this.estados = estado;
        this.tipo = tipo;
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return this.cantidad;
    }
}
