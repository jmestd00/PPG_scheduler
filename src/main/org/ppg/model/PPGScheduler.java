package org.ppg.model;

import java.util.ArrayList;

public class PPGScheduler {
    private ArrayList<Diluidor> diluidores = new ArrayList<>();

    public PPGScheduler() throws PPGSchedulerException {
        // TODO implementar constructor PPGScheduler
        //Asumiendo que los diluidores y su capacidad se nos pasan como parametro
        diluidores.add(new Diluidor(1,"",50));//En este ejemplo asumimos que el id es 1 y que su capacidad es 50
    }

    //Llama al resto de metodos de la clase para realizar la accion de añadir lotes al horario
    public void insertarLote(String planningClass, String planta, String tipo, String cantidad, String fechaNecesidad)
            throws PPGSchedulerException {
        // TODO implementar metodo insertar de la clase PPGScheduler
    }
    
    public void modificarFechaNecesidad(int idLote, String fechaNecesidad) throws PPGSchedulerException {
        // TODO implementar metodo modifyDate de la clase PPGScheduler
    }

    public void empezarLote(int idLote) throws PPGSchedulerException {
        // TODO implementar metodo empezarLote de la clase PPGScheduler
    }

    public void retrasarLote(int idLote) throws PPGSchedulerException {
        // TODO implementar metodo retrasarLote de la clase PPGScheduler
    }

     /**
     * Metodo para obtener los lotes previos si existen para añadir los nuevos lotes y recalcular 
     * el calendario de lotes para acomodarlos
     * @return Devuelve los lotes ya en calendario.
     * @throws PPGSchedulerException
     */
    private ArrayList<Lote> obtenerLotesDeLaBaseDeDatos() throws PPGSchedulerException {
        // TODO implementar metodo obtenerLotesDeLaBaseDeDatos de la clase PPGScheduler
        return null;
    }

    /**
     * Metodo para calcular el Item 
     * @throws PPGSchedulerException
     */
    private void calcularItem() throws PPGSchedulerException {
        // TODO implementar metodo calcularItem de la clase PPGScheduler
        // Necesitamos el algoritmo para calcular el item a partir de los datos, pedir a
        // ppg
    }

    /**
     * Metodo para calcular las fechas de inicio de los lotes a partir de la fecha de fin 
     * que ha calculado el algoritmo
     * @return
     * @throws PPGSchedulerException
     */
    private Date calcularFechaInicio() throws PPGSchedulerException {
        // TODO implementar metodo obtenerFechaInicio de la clase PPGScheduler
        return null;
    }

    private Date calcularFechaFin() throws PPGSchedulerException {
        // TODO implementar metodo obtenerFechaFin de la clase PPGScheduler
        return null;
    }
}