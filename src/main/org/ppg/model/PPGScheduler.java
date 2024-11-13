package org.ppg.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PPGScheduler {

    private final String databaseHostname = "josedm64rpi.ddns.net";
    private final String databaseUsername = "PPG";
    private final String databasePassword = "PPG.";
    private final int databasePort = 3306;
    private final String databaseName = "PPG_scheduler";
    private Connection connection;
    private ArrayList<Diluidor> diluidores = new ArrayList<>();

    public PPGScheduler() throws PPGSchedulerException {
        String url = "jdbc:mysql://"+databaseHostname+":"+databasePort+"/"+databaseName;
        try{
            connection = DriverManager.getConnection(url, databaseUsername, databasePassword);
        }catch(SQLException e){
            throw new PPGSchedulerException("No se ha podido realizar la conexion a la base de datos");
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

     /**
      * Metodo para obtener los diluidores y lotes que hay en la base de datos
     * @return Devuelve diluidores ya en calendario.
     * @throws PPGSchedulerException
     */
     public ArrayList<Diluidor> obtenerDiluidoresDeLaBaseDeDatos() throws PPGSchedulerException {
         HashMap<Integer, Diluidor> diluidoresHashMap = new HashMap<>();
         String query = "select ID, Name, Capacity from Diluidores";
         try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
             while (resultSet.next()) {
                 int id = resultSet.getInt("ID");
                 String name = resultSet.getString("Name");
                 int capacidad = resultSet.getInt("Capacity");
                 Diluidor diluidor = new Diluidor(id, name, capacidad);
                 diluidoresHashMap.put(id, diluidor);
             }
         } catch (SQLException e) {
             throw new PPGSchedulerException(e.getMessage());
         }

         query = "select ID, Fecha_inicio,Fecha_fin,Fecha_necesidad,Tipo, Plant,Cantidad, ID_diluidor, Planning_class, Estado from PPG_scheduler.Lote inner join Diluidores on Diluidores.ID like Lote.ID_diluidor = Diluidores.ID";

         try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
             while (resultSet.next()) {
                 int id = resultSet.getInt("ID");
                 int idDiluidor = resultSet.getInt("ID_diluidor");
                 String planningClass = resultSet.getString("Planning_class");
                 String planta = resultSet.getString("Planning_class");
                 String item = resultSet.getString("Item");
                 int cantidad = resultSet.getInt("Cantidad");
                 String tipo = resultSet.getString("Tipo");
                 Date f_inicio = new Date(resultSet.getDate("Fecha_inicio"));
                 Date f_fin  = new Date(resultSet.getDate("Fecha_fin"));
                 Date f_necesidad = new Date(resultSet.getDate("Fecha_necesidad"));
                 Estados estado = Estados.fromValue(resultSet.getString("ESTADO"));
                 Lote lote = new Lote(id, planningClass, planta, item, cantidad, tipo, f_inicio, f_fin,f_necesidad,estado);
                 diluidoresHashMap.get(idDiluidor).addLote(lote);
             }
         } catch (SQLException e) {
             throw new PPGSchedulerException(e.getMessage());
         }
         ArrayList<Diluidor> diluidores = new ArrayList<>();
         for(Integer id:diluidoresHashMap.keySet()){
             diluidores.add(diluidoresHashMap.get(id));
         }
         return diluidores;
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