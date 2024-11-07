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

    /**
     * Metodo donde se ejecuta el algoritmo de backtracking necesario para obtener un 
     * calendario de lostes ideal
     * precondicion: los lotes deben estar ordenados por fecha de necesidad
     * Los diluidores deben estar ordenados de menor a mayor Cantidad.
     * @param lotesPrevios Lotes ya existentes obtenidos de la base de datos
     * @param nuevosLotes Lotes nuevos introducidos por el usuario
     */
    private void planificar(ArrayList<Lote> lotesPrevios, ArrayList<Lote> nuevosLotes){
        //planificarRec(null, null, 0);
    }

    public boolean planificarRec(ArrayList<Lote> lotes, int indiceLote){
        //hay que añadir la tolerancia al retraso-----------------------------------
        // Caso base: si hemos asignado todos los lotes, es una solución válida
        if (indiceLote == lotes.size()) {
            return true;//Una solucion guardada -> return false
        }

        Lote loteActual = lotes.get(indiceLote);

        // Intentar agregar el lote actual a cada diluidor
        for (Diluidor diluidor : diluidores) {
            LocalDate fechaDisponible = diluidor.getFechaFin();  // Fecha hasta la cual está ocupado el diluidor
            LocalDate fechaFinTemp = fechaDisponible.plusDays(loteActual.getDuracion());
            LocalDate fechaNecesidad = loteActual.getFechaNecesidad();
            // Verificar si el lote puede agregarse al diluidor sin exceder su fecha de fin
            if ((fechaFinTemp.isBefore(fechaNecesidad) || fechaNecesidad.equals(fechaFinTemp)) && diluidor.getCapacity() >= loteActual.getCantidad()) {
                // Actualizar la fecha de ocupación del diluidor
                diluidor.setFechaFin(fechaFinTemp); // Actualizamos la fecha ocupada con la duración del lote
                //Lote marcar diluidor o el addLote de diluidor
                loteActual.setDiluidor(diluidor.getId());
                loteActual.setFechaFin(fechaFinTemp);
                // Avanzar al siguiente lote
                if (planificarRec(lotes, indiceLote + 1)) {
                    return true; // Si llegamos aquí, hemos encontrado una solución válida
                }

                // Backtrack: si no fue una solución, restauramos la fecha ocupada y quitamos el lote
                diluidor.setFechaFin(fechaDisponible);
                loteActual.setDiluidor(0);//o desmarcar de lote el diluidor
                //estudiar la necesidad de restear la fechafin, ya que la fechaNecesidad no cambia
            }
        }

        // Si no se pudo asignar el lote actual a ningún diluidor, no hay solución
        return false;
    }
}