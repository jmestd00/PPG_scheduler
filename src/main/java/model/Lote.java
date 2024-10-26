package model;

public class Lote {
    private Estados estados;
    private Date fechaNecesidad;
    private Date fechaInicio;
    private Date fechaFinal;
    private String tipo, planta, planningClass, cantidad, item;
    private final int id;

    public Lote(int id, String planningClass, String planta, String item, String cantidad, String tipo, Date fechaInicio, Date fechaFinal, Date fechaNecesidad, Estados estado) {
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
}
