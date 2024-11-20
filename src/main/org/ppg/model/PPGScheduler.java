package org.ppg.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class PPGScheduler {


    private Connection connection;
    private ArrayList<Dilutor> diluidores = new ArrayList<>();

    public PPGScheduler() throws PPGSchedulerException {
        String hostname = "josedm64rpi.ddns.net";
        String username = "PPG";
        String password = "PPG.";
        int port = 3306;
        String database = "PPG_scheduler";
        String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;
        try {
            connection = getConnection(url, username, password);
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
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

    public void insertarLoteDB(Batch lote)throws PPGSchedulerException{
        String query = "INSERT INTO Lote (Fecha_inicio, Fecha_fin, Fecha_necesidad, Tipo, Plant, Cantidad, ID_diluidor, Planning_class, Estado, Item, Stock) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters for the placeholders in the query
            statement.setString(1, lote.dStart().toString());
            statement.setString(2, lote.dNeed().toString());
            statement.setString(3, lote.dNeed().toString());
            statement.setString(4, lote.type());
            statement.setString(5, lote.plant());
            statement.setInt(6, lote.quantity());
            statement.setInt(7, lote.dil);
            statement.setString(8, lote.getPlannigClass());
            statement.setString(9, lote.getEstado());
            statement.setString(10, lote.getItem());
            statement.setInt(11, lote.getStock());

            // Execute the insert
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Insert successful. Rows affected: " + rowsAffected);
            } else {
                System.out.println("No rows were inserted.");
            }
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
    }

    public void actualizarLoteDB(Batch lote)throws PPGSchedulerException{
        String query = "UPDATE Lote SET Fecha_inicio = ?, Fecha_fin = ?, Fecha_necesidad = ?, Tipo = ?, Plant = ?, Cantidad = ?, ID_diluidor = ?, Planning_class = ?, Estado = ?, Item = ? WHERE Lote.ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, lote.getFechaInicio().toString());
            statement.setString(2, lote.getFechaFinal().toString());
            statement.setString(3, lote.getFechaNecesidad().toString());
            statement.setString(4, lote.getTipo());
            statement.setString(5, lote.getPlant());
            statement.setInt(6, lote.getCantidad());
            statement.setInt(7, lote.getIdDiluidor());
            statement.setString(8, lote.getPlannigClass());
            statement.setString(9, lote.getEstado());
            statement.setString(10, lote.getItem());
            statement.setInt(11, lote.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Update successful. Rows affected: " + rowsAffected);
            } else {
                System.out.println("No rows were updated.");
            }
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }

    }

    private void obtenerDiluidoresDB(HashMap<Integer, Dilutor> diluidores) throws PPGSchedulerException {
        assert diluidores != null;
        String query = "select ID, Name, Capacity from Diluidores";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                int capacidad = resultSet.getInt("Capacity");
                Dilutor diluidor = new Dilutor(id, name, capacidad);
                diluidores.put(id, diluidor);
            }
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
    }

    private void obtenerLotesDB(HashMap<Integer, Dilutor> diluidores) throws PPGSchedulerException {
        String query = "select Lote.ID, Fecha_inicio,Fecha_fin,Fecha_necesidad,Tipo, Plant,Cantidad, ID_diluidor, Planning_class, Estado, Item, Stock from PPG_scheduler.Lote inner join Diluidores on Diluidores.ID like Lote.ID_diluidor";
        try (PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int idDiluidor = resultSet.getInt("ID_diluidor");
                String planningClass = resultSet.getString("Planning_class");
                String planta = resultSet.getString("Planning_class");
                String item = resultSet.getString("Item");
                int cantidad = resultSet.getInt("Cantidad");
                String tipo = resultSet.getString("Tipo");
                Date f_inicio = new Date(resultSet.getDate("Fecha_inicio"));
                Date f_fin = new Date(resultSet.getDate("Fecha_fin"));
                Date f_necesidad = new Date(resultSet.getDate("Fecha_necesidad"));
                Statuses estado = Statuses.fromValue(resultSet.getString("ESTADO"));
                int stock = resultSet.getInt("Stock");
                Batch lote = new Batch(id, planningClass, planta, item, cantidad, tipo, f_inicio, f_fin, f_necesidad, estado, idDiluidor, stock);
                diluidores.get(idDiluidor).addLote(lote);
            }
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
    }

    public ArrayList<Dilutor> obtenerDiluidoresDeLaBaseDeDatos() throws PPGSchedulerException {
        HashMap<Integer, Dilutor> diluidoresHashMap = new HashMap<>();
        obtenerDiluidoresDB(diluidoresHashMap);
        obtenerLotesDB(diluidoresHashMap);
        ArrayList<Dilutor> diluidores = new ArrayList<>();
        for (Integer id : diluidoresHashMap.keySet()) {
            diluidores.add(diluidoresHashMap.get(id));
        }
        return diluidores;
    }

    /**
     * Metodo para calcular el Item
     *
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
     *
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
     *
     * @param lotesPrevios Lotes ya existentes obtenidos de la base de datos
     * @param nuevosLotes  Lotes nuevos introducidos por el usuario
     */
    private void planificar(ArrayList<Batch> lotesPrevios, ArrayList<Batch> nuevosLotes) {
        //planificarRec(null, null, 0);
    }
    /*
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
    */
}