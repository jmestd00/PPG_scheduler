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
    private final int tolerance = 2;
    private final String statusFile = "status.txt";
    private final ArrayList<Dilutor> dilutors = new ArrayList<>();
    private final HashMap<String, Boolean> mapSolutions = new HashMap<>();
    private final ArrayList<Solution> solutions = new ArrayList<>();
    private ArrayList<Batch> batches = new ArrayList<>();
    private int batchIndex = 0;
    
    //Constructor
    private Solution getBestSolution(ArrayList<Solution> soluciones) {
        int min = Integer.MAX_VALUE;
        Solution best = null;
        for (Solution solucion : soluciones) {
            int sum = 0;
            for (Dilutor diluidor : dilutors) {
                for (Batch lote : diluidor.getBatches()) {
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
     * @param previousBatches Lotes ya existentes obtenidos de la base de datos
     * @param newBatches  Lotes nuevos introducidos por el usuario
     */
    public void schedule(ArrayList<Batch> previousBatches, ArrayList<Batch> newBatches) {
        System.out.println(previousBatches);  //TODO que es esto
        
        batches.clear();
        batches.addAll(previousBatches);
        batches.addAll(newBatches);
        updateBatches(batches);
        solutions.clear();
        batchIndex = 0;
        
        // Limpiar el archivo de estado
        try {
            Files.deleteIfExists(Paths.get(statusFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scheduleRecursively();
    }
    
    /**
     * Método recursivo para el algoritmo de backtracking.
     *
     * @return true si encuentra una solución válida, false en caso contrario
     */
    public boolean scheduleRecursively() {
        if (batchIndex == batches.size()) {
            System.out.println("Caso base alcanzado. Generando solución...");
            Solution sol = new Solution(new ArrayList<>(batches)); // Crear copia de los lotes
            try {
                calculateStartDate(sol);
                if (adjustSolutionsWithHashMap(sol)) {
                    if (!mapSolutions.containsKey(sol.toString())) {
                        mapSolutions.put(sol.toString(), true);
                        solutions.add(sol);
                    }
                }
            } catch (PPGSchedulerException e) {
                System.err.println("Error al calcular fechas de inicio: " + e.getMessage());
            }
            return false;
        }
        Batch actualBatch = batches.get(batchIndex);
        System.out.println("Procesando Lote ID: " + actualBatch.getId());
        for (int i = 0; i <= tolerance; i++) {
            for (Dilutor dilutor : dilutors) {
                LocalDate endDate = dilutor.getFechaFin();
                LocalDate tempEndDate = endDate.plusDays(actualBatch.getDuration());
                LocalDate needDate = actualBatch.getNeedDate();
                if ((tempEndDate.isBefore(needDate.plusDays(i)) || needDate.equals(tempEndDate)) && dilutor.getCapacity() >= actualBatch.getQuantity()) {
                    if (endDate == null) {
                        System.out.println("El Diluidor ID: " + dilutor.getId() + " tiene fecha disponible nula. Saltando.");
                        continue;
                    }
                    // Guardar estado actual en archivo
                    saveStatus(dilutors, batches, batchIndex);
                    // Actualizar estado para esta iteración
                    dilutor.setEndDate(tempEndDate);
                    actualBatch.setDilutor(dilutor.getId());
                    actualBatch.setEndDate(tempEndDate);
                    batchIndex++;
                    // Llamada recursiva
                    scheduleRecursively();
                    // Retroceso: restaurar estado desde archivo
                    resetStatus();
                    System.out.println("Retroceso: Estado restaurado tras intentar asignar Lote ID: " + actualBatch.getId());
                }
            }
        }
        return false;
    }
    
    private void saveStatus(ArrayList<Dilutor> dilutors, ArrayList<Batch> batches, int batchIndex) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(statusFile, true))) {
            StringBuilder builder = new StringBuilder();
            for (Dilutor dilutor : dilutors) {
                builder.append(dilutor.getId()).append(".").append(dilutor.getFechaFin()).append(","); // Separar atributos con punto
            }
            builder.append(";"); // Separar objetos con punto y coma
            
            for (Batch batch : batches) {
                builder.append(batch.getId()).append(".").append(batch.getEndDate()).append(".").append(batch.getDilutorId()).append(","); // Separar atributos con punto
            }
            builder.append(";").append(batchIndex); // Agregar índice
            
            writer.write(builder.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void resetStatus() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(statusFile));
            if (!lines.isEmpty()) {
                String lastLine = lines.getLast();
                String[] parts = lastLine.split(";");
                String[] dilutorsData = parts[0].split(",");
                String[] batchesData = parts[1].split(",");
                batchIndex = Integer.parseInt(parts[2]);
                
                for (int i = 0; i < dilutors.size(); i++) {
                    String[] dilutorParts = dilutorsData[i].split("\\.");
                    String date = dilutorParts[1];
                    if (date != null && !date.isEmpty()) {
                        dilutors.get(i).setEndDate(LocalDate.parse(date));
                    } else {
                        dilutors.get(i).setEndDate(LocalDate.of(2023, 1, 1)); // Fecha inicial predeterminada
                    }
                }
                
                for (int i = 0; i < batches.size(); i++) {
                    String[] batchParts = batchesData[i].split("\\.");
                    String date = batchParts[1];
                    if (date != null && !date.isEmpty()) {
                        batches.get(i).setEndDate(LocalDate.parse(date));
                    } else {
                        batches.get(i).setEndDate(null); // Manejar fechas nulas
                    }
                    batches.get(i).setDilutor(Integer.parseInt(batchParts[2]));
                }
                // Eliminar la última línea del archivo
                Files.write(Paths.get(statusFile), lines.subList(0, lines.size() - 1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DateTimeParseException e) {
            System.err.println("Error al parsear una fecha del archivo de estado.");
            e.printStackTrace();
        }
    }
    
    public void updateBatches(ArrayList<Batch> newBatches) {
        this.batches = newBatches;
    }
    
    /**
     * Calcula las fechas de inicio para todos los lotes en una solución.
     *
     * @param solution La solución que contiene la lista de lotes.
     * @throws PPGSchedulerException Si algún lote no tiene suficiente información
     *                               para calcular su fecha de inicio.
     */
    private void calculateStartDate(Solution solution) throws PPGSchedulerException {
        for (Batch batch : solution.getLotes()) {
            if (batch.getEndDate() == null || batch.getDuration() <= 0) {
                throw new PPGSchedulerException("Información insuficiente para calcular la fecha de inicio del batch ID: " + batch.getId());
            }
            // Calcular la fecha de inicio restando la duración a la fecha de fin
            LocalDate startDate = batch.getEndDate().minusDays(batch.getDuration());
            batch.setStartDate(startDate); // Actualizar la fecha de inicio del batch
        }
    }
    
    /**
     * Recorre todas las soluciones y ajusta las fechas de los lotes, si es posible,
     * para que terminen justo en su fecha de necesidad.
     * Organiza los lotes por diluidor para facilitar la verificación de colisiones.
     */
    public boolean adjustSolutionsWithHashMap(Solution solucion) {
        System.out.println("Ajustando solución...");
        // Agrupar lotes por diluidor en un HashMap
        HashMap<Integer, ArrayList<Batch>> batchInDilutor = groupBatchInDilutor(solucion);
        System.out.println("Lotes agrupados por diluidor: " + batchInDilutor.keySet());
        for (HashMap.Entry<Integer, ArrayList<Batch>> entry : batchInDilutor.entrySet()) {
            int dilutorId = entry.getKey();
            ArrayList<Batch> batchOfDilutor = entry.getValue();
            System.out.println("Ajustando lotes del Diluidor ID: " + dilutorId);
            for (Batch batch : batchOfDilutor) {
                LocalDate endDate = batch.getEndDate();
                LocalDate needDate = batch.getNeedDate();
                System.out.println("Lote ID: " + batch.getId() + ", Fecha Fin: " + endDate + ", Fecha Necesidad: " + needDate);
                // Si ya cumple, continuar con el siguiente batch
                if (endDate.equals(needDate)) {
                    System.out.println("Lote ID: " + batch.getId() + " ya cumple con la fecha de necesidad.");
                    continue;
                }
                boolean adjusted = false;
                for (int i = 0; i <= tolerance; i++) {
                    if (canMoveBatch(batch, -i)) { // Intentar adelantar el batch
                        System.out.println("Lote ID: " + batch.getId() + " ajustado con adelanto de " + i + " días.");
                        adjusted = true;
                        break;
                    } else if (canMoveBatch(batch, i)) { // Intentar retrasar el batch
                        System.out.println("Lote ID: " + batch.getId() + " ajustado con retraso de " + i + " días.");
                        adjusted = true;
                        break;
                    }
                }
                if (!adjusted) {
                    System.out.println("No se pudo ajustar el Lote ID: " + batch.getId() + " dentro de la tolerancia.");
                    return false; // Si no se pudo ajustar un batch, retorna falso
                }
            }
        }
        System.out.println("Ajuste completado para la solución.");
        return true;
    }
    
    /**
     * Agrupa los lotes de una solución por diluidor.
     *
     * @param solucion La solución cuyos lotes se van a agrupar.
     * @return Un HashMap donde la clave es el ID del diluidor y el valor es la
     * lista de lotes asignados a ese diluidor.
     */
    private HashMap<Integer, ArrayList<Batch>> groupBatchInDilutor(Solution solucion) {
        HashMap<Integer, ArrayList<Batch>> batchInDilutor = new HashMap<>();
        System.out.println("Agrupando lotes por diluidor...");
        for (Batch batch : solucion.getLotes()) {
            int dilutorId = batch.getDilutorId();
            batchInDilutor.putIfAbsent(dilutorId, new ArrayList<>());
            batchInDilutor.get(dilutorId).add(batch);
            System.out.println("Lote ID: " + batch.getId() + " asignado a Diluidor ID: " + dilutorId);
        }
        System.out.println("Lotes agrupados por diluidor: " + batchInDilutor);
        return batchInDilutor;
    }
    
    /**
     * Verifica si un ajuste de fecha es válido dentro de los lotes asignados a un
     * diluidor.
     *
     * @param batchInDilutor La lista de lotes asignados al mismo diluidor.
     * @param batch             El lote que se intenta ajustar.
     * @param newStartDate La nueva fecha de inicio propuesta.
     * @return true si el ajuste es válido, false en caso contrario.
     */
    private boolean isValidAdjust(ArrayList<Batch> batchInDilutor, Batch batch, LocalDate newStartDate) {
        System.out.println("Verificando si el ajuste es válido para Lote ID: " + batch.getId() + ", Nueva Fecha Inicio: " + newStartDate);
        for (Batch anotherBatch : batchInDilutor) {
            if (!anotherBatch.equals(batch)) {
                LocalDate anotherStart = anotherBatch.getStartDate();
                LocalDate anotherEnd = anotherBatch.getEndDate();
                if (anotherStart == null || anotherEnd == null) {
                    System.out.println("El Lote ID: " + anotherBatch.getId() + " tiene fechas nulas. Ignorando.");
                    continue;
                }
                if ((newStartDate.isBefore(anotherEnd) && newStartDate.isAfter(anotherStart)) || newStartDate.equals(anotherStart) || newStartDate.equals(anotherEnd)) {
                    System.out.println("Ajuste no válido: solapamiento con Lote ID: " + anotherBatch.getId());
                    return false; // Hay solapamiento
                }
            }
        }
        System.out.println("Ajuste válido para Lote ID: " + batch.getId());
        return true;
    }
    
    private boolean canMoveBatch(Batch batch, int days) {
        LocalDate newStartDate = batch.getStartDate();
        LocalDate newEndDate = batch.getEndDate();
        if (newStartDate == null || newEndDate == null) {
            System.out.println("El Lote ID: " + batch.getId() + " tiene fechas nulas. No se puede mover.");
            return false; // No se puede mover si las fechas son nulas
        }
        newStartDate = newStartDate.plusDays(days);
        newEndDate = newEndDate.plusDays(days);
        for (Batch anotherBatch : batches) {
            if (!anotherBatch.equals(batch)) {
                LocalDate anotherStart = anotherBatch.getStartDate();
                LocalDate anotherEnd = anotherBatch.getEndDate();
                if (anotherStart == null || anotherEnd == null) {
                    System.out.println("El Lote ID: " + anotherBatch.getId() + " tiene fechas nulas. Ignorando.");
                    continue;
                }
                if ((newStartDate.isBefore(anotherEnd) && newStartDate.isAfter(anotherStart)) || newStartDate.equals(anotherStart) || newStartDate.equals(anotherEnd)) {
                    return false; // Hay solapamiento
                }
            }
        }
        // Si no hay conflictos, actualiza las fechas del lote
        batch.setStartDate(newStartDate);
        batch.setEndDate(newEndDate);
        return true;
    }
    
    /**
     * Metodo temporal para pruebas de imprimir lotes
     */
    private void printBatches(ArrayList<Batch> lotes) {
        System.out.println("Lotes:");
        for (int i = 0; i < lotes.size(); i++) {
            Batch batch = lotes.get(i);
            String batchInfo = "getCollidedBatch" + (i + 1) + " (Cantidad: " + batch.getQuantity() + ")" + " (Duración: " + batch.getDuration() + ")" + " (Fecha fin: " + batch.getEndDate().toString() + ")" + " (Diluidor: " + batch.getDilutorId() + ")";
            System.out.println(batchInfo);
        }
    }
    
    public void saveSolutionsInFile() {
        String fileName = "soluciones.txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            if (solutions.isEmpty()) {
                writer.write("No se encontraron soluciones.");
                return;
            }
            
            int nSolution = 1;
            for (Solution solution : solutions) {
                writer.write("Solución #" + nSolution + ":\n");
                for (Batch batch : solution.getLotes()) {
                    writer.write(" - Lote ID: " + batch.getId() + ", Cantidad: " + batch.getQuantity() + ", Duración: " + batch.getDuration() + ", Fecha Fin: " + (batch.getEndDate() != null ? batch.getEndDate() : "No asignada") + ", Fecha Necesidad : " + batch.getNeedDate() + ", Diluidor: " + (batch.getDilutorId() > 0 ? "Diluidor " + batch.getDilutorId() : "No asignado") + "\n");
                }
                writer.write("\n"); // Espacio entre soluciones
                nSolution++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private void printDilutorStatus() {
        StringBuilder status = new StringBuilder();
        for (Dilutor dilutor : dilutors) {
            status.append("d").append(dilutor.getId()).append("(").append(dilutor.getCapacity()).append(")(").append(dilutor.getFechaFin() != null ? dilutor.getFechaFin().toString() : "Sin fecha").append(")");
            
            // Añadir los lotes asignados al diluidor
            for (Batch batch : batches) {
                if (batch.getDilutorId() == dilutor.getId()) {
                    status.append("->(getCollidedBatch").append(batch.getId()).append(" ").append(batch.getEndDate() != null ? batch.getEndDate().toString() : "Sin fecha").append(")");
                }
            }
            status.append("\n");
        }
        System.out.println(status);
    }
    
    public ArrayList<Dilutor> sortDilutors() {
        this.dilutors.sort(null);
        return this.dilutors;
    }
}
