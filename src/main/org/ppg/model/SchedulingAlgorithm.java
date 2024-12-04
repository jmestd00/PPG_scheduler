package org.ppg.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.abs;

public class SchedulingAlgorithm {

    private ArrayList<Dilutor> diluidores = new ArrayList<>();
    private HashMap<String, Boolean> mapSoluciones = new HashMap<>();
    private final int toleranciaRetraso = 2;
    // Nuevos atributos de clase para evitar pasar como parámetros
    private ArrayList<Solution> soluciones = new ArrayList<>();
    private ArrayList<Batch> lotes = new ArrayList<>();
    private int indiceLote = 0;
    private final String estadoFile = "estado.txt";

    public ArrayList<Dilutor> sortDilutors() {
        this.diluidores.sort(null);
        return this.diluidores;
    }


    private Solution getBestSolution(ArrayList<Solution> soluciones) {
        int min = Integer.MAX_VALUE;
        Solution best = null;
        for (Solution solucion : soluciones) {
            int sum = 0;
            for (Dilutor diluidor : diluidores) {
                for (Batch lote : diluidor.getLotes()) {
                    sum += abs(lote.getEndDate().getDayOfYear() - lote.getNeedDate().getDayOfYear());
                }
            }
            if (sum < min) {
                min = sum;
                best = solucion;
            }
        }
        return best;
    }


    /**
     * Metodo donde se ejecuta el algoritmo de backtracking necesario para obtener
     * un
     * calendario de lostes ideal
     * precondicion: los lotes deben estar ordenados por fecha de necesidad
     * Los diluidores deben estar ordenados de menor a mayor Cantidad.
     *
     * @param lotesPrevios Lotes ya existentes obtenidos de la base de datos
     * @param nuevosLotes  Lotes nuevos introducidos por el usuario
     */

    public void schedule(ArrayList<Batch> lotesPrevios, ArrayList<Batch> nuevosLotes) {

        System.out.println(lotesPrevios);



        lotes.clear();
        lotes.addAll(lotesPrevios);
        lotes.addAll(nuevosLotes);
        actualizarLotes(lotes);
        soluciones.clear();
        indiceLote = 0;
        // Limpiar el archivo de estado
        try {
            Files.deleteIfExists(Paths.get(estadoFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Iniciar el algoritmo
        // if()
        scheduleRecursively();
    }


    /**
     * Método recursivo para el algoritmo de backtracking.
     *
     * @return true si encuentra una solución válida, false en caso contrario
     */

    public boolean scheduleRecursively() {

        if (indiceLote == lotes.size()) {
            System.out.println("Caso base alcanzado. Generando solución...");
            Solution sol = new Solution(new ArrayList<>(lotes)); // Crear copia de los lotes
            try {
                calcularFechasInicio(sol);
                if (ajustarSolucionesConHashMap(sol)) {
                    if (!mapSoluciones.containsKey(sol.toString())) {
                        mapSoluciones.put(sol.toString(), true);
                        soluciones.add(sol);
                    }
                }
            } catch (PPGSchedulerException e) {
                System.err.println("Error al calcular fechas de inicio: " + e.getMessage());
            }
            return false;
        }
        Batch loteActual = lotes.get(indiceLote);
        System.out.println("Procesando Lote ID: " + loteActual.getId());
        for (int i = 0; i <= toleranciaRetraso; i++) {
            for (Dilutor diluidor : diluidores) {
                LocalDate fechaDisponible = diluidor.getFechaFin();
                LocalDate fechaFinTemp = fechaDisponible.plusDays(loteActual.getDuration());
                LocalDate fechaNecesidad = loteActual.getNeedDate();
                if ((fechaFinTemp.isBefore(fechaNecesidad.plusDays(i)) || fechaNecesidad.equals(fechaFinTemp)) &&
                        diluidor.getCapacity() >= loteActual.getQuantity()) {
                    if (fechaDisponible == null) {
                        System.out.println("El Diluidor ID: " + diluidor.getId() + " tiene fecha disponible nula. Saltando.");
                        continue;
                    }
                    // Guardar estado actual en archivo
                    guardarEstado(diluidores, lotes, indiceLote);
                    // Actualizar estado para esta iteración
                    diluidor.setFechaFin(fechaFinTemp);
                    loteActual.setDilutor(diluidor.getId());
                    loteActual.setEndDate(fechaFinTemp);
                    indiceLote++;
                    // Llamada recursiva
                    scheduleRecursively();
                    // Retroceso: restaurar estado desde archivo
                    restaurarEstado();
                    System.out.println("Retroceso: Estado restaurado tras intentar asignar Lote ID: " + loteActual.getId());
                }
            }
        }
        return false;
    }


    private void guardarEstado(ArrayList<Dilutor> diluidores, ArrayList<Batch> lotes, int indiceLote) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(estadoFile, true))) {
            StringBuilder sb = new StringBuilder();
            for (Dilutor diluidor : diluidores) {
                sb.append(diluidor.getId()).append(".")
                        .append(diluidor.getFechaFin()).append(","); // Separar atributos con punto
            }
            sb.append(";"); // Separar objetos con punto y coma

            for (Batch lote : lotes) {
                sb.append(lote.getId()).append(".")
                        .append(lote.getEndDate()).append(".")
                        .append(lote.getDilutorId()).append(","); // Separar atributos con punto
            }
            sb.append(";").append(indiceLote); // Agregar índice

            writer.write(sb.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void restaurarEstado() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(estadoFile));
            if (!lines.isEmpty()) {
                String lastLine = lines.get(lines.size() - 1);
                String[] parts = lastLine.split(";");
                String[] diluidoresData = parts[0].split(",");
                String[] lotesData = parts[1].split(",");
                indiceLote = Integer.parseInt(parts[2]);

                for (int i = 0; i < diluidores.size(); i++) {
                    String[] diluidorParts = diluidoresData[i].split("\\.");
                    String fecha = diluidorParts[1];
                    if (fecha != null && !fecha.isEmpty()) {
                        diluidores.get(i).setFechaFin(LocalDate.parse(fecha));
                    } else {
                        diluidores.get(i).setFechaFin(LocalDate.of(2023, 1, 1)); // Fecha inicial predeterminada
                    }
                }

                for (int i = 0; i < lotes.size(); i++) {
                    String[] loteParts = lotesData[i].split("\\.");
                    String fecha = loteParts[1];
                    if (fecha != null && !fecha.isEmpty()) {
                        lotes.get(i).setEndDate(LocalDate.parse(fecha));
                    } else {
                        lotes.get(i).setEndDate(null); // Manejar fechas nulas
                    }
                    lotes.get(i).setDilutor(Integer.parseInt(loteParts[2]));
                }
                // Eliminar la última línea del archivo
                Files.write(Paths.get(estadoFile), lines.subList(0, lines.size() - 1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DateTimeParseException e) {
            System.err.println("Error al parsear una fecha del archivo de estado.");
            e.printStackTrace();
        }
    }


    public void actualizarLotes(ArrayList<Batch> nuevosLotes) {
        this.lotes = nuevosLotes;
    }


    /**
     * Calcula las fechas de inicio para todos los lotes en una solución.
     *
     * @param solucion La solución que contiene la lista de lotes.
     * @throws PPGSchedulerException Si algún lote no tiene suficiente información
     *                               para calcular su fecha de inicio.
     */


    private void calcularFechasInicio(Solution solucion) throws PPGSchedulerException {
        for (Batch lote : solucion.getLotes()) {
            if (lote.getEndDate() == null || lote.getDuration() <= 0) {
                throw new PPGSchedulerException(
                        "Información insuficiente para calcular la fecha de inicio del lote ID: " + lote.getId());
            }
            // Calcular la fecha de inicio restando la duración a la fecha de fin
            LocalDate fechaInicio = lote.getEndDate().minusDays(lote.getDuration());
            lote.setStartDate(fechaInicio); // Actualizar la fecha de inicio del lote
        }
    }


    /**
     * Recorre todas las soluciones y ajusta las fechas de los lotes, si es posible,
     * para que terminen justo en su fecha de necesidad.
     * Organiza los lotes por diluidor para facilitar la verificación de colisiones.
     */

     public boolean ajustarSolucionesConHashMap(Solucion solucion) {
        System.out.println("Ajustando solución...");
        // Agrupar lotes por diluidor en un HashMap
        HashMap<Integer, ArrayList<Lote>> lotesPorDiluidor = agruparLotesPorDiluidor(solucion);

        for (HashMap.Entry<Integer, ArrayList<Lote>> entry : lotesPorDiluidor.entrySet()) {
            int diluidorId = entry.getKey();
            ArrayList<Lote> lotesDelDiluidor = entry.getValue();
            if(!ajustarDiluidor(lotesDelDiluidor)) {
                return false;
            }

        }
        return true;
    }


    /**
     * Agrupa los lotes de una solución por diluidor.
     *
     * @param solucion La solución cuyos lotes se van a agrupar.
     * @return Un HashMap donde la clave es el ID del diluidor y el valor es la
     * lista de lotes asignados a ese diluidor.
     */

    private HashMap<Integer, ArrayList<Batch>> agruparLotesPorDiluidor(Solution solucion) {
        HashMap<Integer, ArrayList<Batch>> lotesPorDiluidor = new HashMap<>();
        System.out.println("Agrupando lotes por diluidor...");
        for (Batch lote : solucion.getLotes()) {
            int diluidorId = lote.getDilutorId();
            lotesPorDiluidor.putIfAbsent(diluidorId, new ArrayList<>());
            lotesPorDiluidor.get(diluidorId).add(lote);
            System.out.println("Lote ID: " + lote.getId() + " asignado a Diluidor ID: " + diluidorId);
        }
        System.out.println("Lotes agrupados por diluidor: " + lotesPorDiluidor);
        return lotesPorDiluidor;
    }


    public boolean ajustarDiluidor(ArrayList<Lote> lotesPorDiluidor) {
        // Llamada inicial al método recursivo, comenzando desde el último lote
        return ajustarDiluidorRec(lotesPorDiluidor, lotesPorDiluidor.size() - 1, 0); // Estado inicial: 0 (en fecha de necesidad)
    }
    
    private boolean ajustarDiluidorRec(ArrayList<Lote> lotesPorDiluidor, int posicion, int estadoPrevio) {
        // Si hemos procesado todos los lotes, la recursión termina exitosamente
        if (posicion < 0) {
            return true;
        }
    
        // Obtener el lote actual
        Lote loteActual = lotesPorDiluidor.get(posicion);
        LocalDate fechaFin = loteActual.getFechaFin();
        LocalDate fechaNecesidad = loteActual.getFechaNecesidad();
    
        // Comprobar si el lote está en su fecha de necesidad o retrasado
        if (!fechaFin.isBefore(fechaNecesidad)) {
            // Está en fecha o retrasado, pasar al siguiente lote
            long dias = ChronoUnit.DAYS.between(fechaFin, fechaNecesidad);
            int n = (int) dias;
            return ajustarDiluidorRec(lotesPorDiluidor, posicion - 1, n); // Estado actual: 0
        }
    
        // Si no está en fecha, moverlo a su fecha de necesidad
        loteActual.setFechaFin(fechaNecesidad);
        loteActual.setFechaInicio(loteActual.getFechaFin().minusDays(loteActual.getDuracion() - 1));
    
        if (posicion + 1 >= lotesPorDiluidor.size()) {
            return ajustarDiluidorRec(lotesPorDiluidor, posicion - 1, 0);
        }
    
        // Comprobar si hay solapamiento con el lote previo
        Lote lotePrevio = lotesPorDiluidor.get(posicion + 1);
        LocalDate fechaFinActual = loteActual.getFechaFin();
        LocalDate fechaInicioPrevio = lotePrevio.getFechaInicio();
    
        // Bucle para ajustar solapamientos mediante retrasos
        while (estadoPrevio <= toleranciaRetraso) {
            if (!fechaFinActual.isBefore(fechaInicioPrevio)&&(estadoPrevio<toleranciaRetraso)) {
                boolean sePudoRetrasarPrevio = retrasarPrevio(lotesPorDiluidor, posicion + 1, estadoPrevio);
                if (!sePudoRetrasarPrevio) {
                    break;
                }
                fechaInicioPrevio = lotePrevio.getFechaInicio();
                estadoPrevio++;
            } else if (fechaFinActual.isBefore(fechaInicioPrevio)){
                return ajustarDiluidorRec(lotesPorDiluidor, posicion - 1, 0);
            } else {estadoPrevio++;}
        }
    
        // Intentar ajustes mediante adelantos
        for (int i = 1; i <= toleranciaRetraso; i++) {
            loteActual.setFechaFin(fechaNecesidad.minusDays(i));
            loteActual.setFechaInicio(loteActual.getFechaFin().minusDays(loteActual.getDuracion() - 1));
            fechaFinActual = loteActual.getFechaFin();
            fechaInicioPrevio = lotePrevio.getFechaInicio();
    
            if (!fechaFinActual.isBefore(fechaInicioPrevio)) {
                continue;
            } else {
                return ajustarDiluidorRec(lotesPorDiluidor, posicion - 1, toleranciaRetraso);
            }
        }
    
        return false;
    }
    
    private boolean retrasarPrevio(ArrayList<Lote> lotesPorDiluidor, int posicion, int estadoRetraso) {
        if (posicion >= lotesPorDiluidor.size()) {
            return false;
        }
    
        Lote lotePrevio = lotesPorDiluidor.get(posicion);
    
        if (estadoRetraso < toleranciaRetraso) {
            lotePrevio.setFechaInicio(lotePrevio.getFechaInicio().plusDays(1));
            lotePrevio.setFechaFin(lotePrevio.getFechaFin().plusDays(1));
    
            if (posicion + 1 < lotesPorDiluidor.size()) {
                Lote loteSiguiente = lotesPorDiluidor.get(posicion + 1);
                if (!lotePrevio.getFechaFin().isBefore(loteSiguiente.getFechaInicio())) {
                    boolean sePuedeRetrasarSiguiente = retrasarPrevio(lotesPorDiluidor, posicion + 1, estadoRetraso + 1);
                    if (!sePuedeRetrasarSiguiente) {
                        lotePrevio.setFechaInicio(lotePrevio.getFechaInicio().minusDays(1));
                        lotePrevio.setFechaFin(lotePrevio.getFechaFin().minusDays(1));
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Metodo temporal para pruebas de imprimir lotes
     */


    private void imprimirLotes(ArrayList<Batch> lotes) {
        System.out.println("Lotes:");
        for (int i = 0; i < lotes.size(); i++) {
            Batch lote = lotes.get(i);
            String informacionLote = "l" + (i + 1) +
                    " (Cantidad: " + lote.getQuantity() + ")" +
                    " (Duración: " + lote.getDuration() + ")" +
                    " (Fecha fin: " + lote.getEndDate().toString() + ")" +
                    " (Diluidor: " + lote.getDilutorId() + ")";
            System.out.println(informacionLote);
        }
    }


    public void guardarSolucionesEnArchivo() {
        String nombreArchivo = "soluciones.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            if (soluciones.isEmpty()) {
                writer.write("No se encontraron soluciones.");
                return;
            }

            int solucionNumero = 1;
            for (Solution solucion : soluciones) {
                writer.write("Solución #" + solucionNumero + ":\n");
                for (Batch lote : solucion.getLotes()) {
                    writer.write(" - Lote ID: " + lote.getId() +
                            ", Cantidad: " + lote.getQuantity() +
                            ", Duración: " + lote.getDuration() +
                            ", Fecha Fin: " + (lote.getEndDate() != null ? lote.getEndDate() : "No asignada") +
                            ", Fecha Necesidad : " + lote.getNeedDate() +
                            ", Diluidor: " + (lote.getDilutorId() > 0 ? "Diluidor " + lote.getDilutorId() : "No asignado")
                            + "\n");
                }
                writer.write("\n"); // Espacio entre soluciones
                solucionNumero++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void imprimirEstadoDiluidores() {
        StringBuilder estado = new StringBuilder();
        for (Dilutor diluidor : diluidores) {
            estado.append("d")
                    .append(diluidor.getId())
                    .append("(")
                    .append(diluidor.getCapacity())
                    .append(")(")
                    .append(diluidor.getFechaFin() != null ? diluidor.getFechaFin().toString() : "Sin fecha")
                    .append(")");

            // Añadir los lotes asignados al diluidor
            for (Batch lote : lotes) {
                if (lote.getDilutorId() == diluidor.getId()) {
                    estado.append("->(l")
                            .append(lote.getId())
                            .append(" ")
                            .append(lote.getEndDate() != null ? lote.getEndDate().toString() : "Sin fecha")
                            .append(")");
                }
            }
            estado.append("\n");
        }
        System.out.println(estado);
    }
}
