package org.ppg.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class DatabaseManager {
    private final Connection connection;
    private static DatabaseManager instance;
    
    //Constructor
    private DatabaseManager() throws PPGSchedulerException {
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
    
    //Singleton
    public static DatabaseManager getInstance() throws PPGSchedulerException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    //Lotes
    public Batch getBatchDB(int nBatch) {
        String query = "SELECT * FROM Lote WHERE N_Lote = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, nBatch);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String planningClass = resultSet.getString("Planning_class");
                    String plant = resultSet.getString("Plant");
                    String item = resultSet.getString("Item");
                    int quantity = resultSet.getInt("Cantidad");
                    LocalDate startDate = resultSet.getDate("Fecha_inicio").toLocalDate();
                    LocalDate needDate = resultSet.getDate("Fecha_necesidad").toLocalDate();
                    Statuses status = Statuses.fromValue(resultSet.getString("Estado"));
                    String description = resultSet.getString("Descripcion");
                    Types type = Types.fromValue(resultSet.getString("Tipo"));
                    int idDilutor = resultSet.getInt("ID_diluidor");
                    
                    Dilutor dilutor = getDilutorDB(idDilutor);
                    return new Batch(id, planningClass, plant, item, quantity, startDate, needDate, status, description, type, dilutor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void getBatchesDB(HashMap<Integer, Dilutor> diluidores) throws PPGSchedulerException {
        String query = "SELECT Fecha_inicio, Fecha_necesidad, ID_diluidor, Tipo, Plant, Cantidad, Planning_class, Estado, Descripcion, N_Lote, Item FROM PPG_scheduler.Lote inner join Diluidores on Diluidores.ID like Lote.ID_diluidor";
        try (PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                LocalDate startDate = resultSet.getDate("Fecha_inicio").toLocalDate();
                LocalDate needDate = resultSet.getDate("Fecha_necesidad").toLocalDate();
                int dilutorID = resultSet.getInt("ID_diluidor");
                Types type = Types.fromValue(resultSet.getString("Tipo"));
                String plant = resultSet.getString("Plant");
                int quantity = resultSet.getInt("Cantidad");
                String planningClass = resultSet.getString("Planning_class");
                Statuses status = Statuses.fromValue(resultSet.getString("Estado"));
                String description = resultSet.getString("Descripcion");
                int nBatch = resultSet.getInt("N_Lote");
                String item = resultSet.getString("Item");
                
                Dilutor dilutor = diluidores.get(dilutorID);
                Batch batch = new Batch(nBatch, planningClass, plant, item, quantity, startDate, needDate, status, description, type, dilutor);
                diluidores.get(dilutorID).addLote(batch);
            }
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
    }
    public void insertBatchDB(Batch batch) throws PPGSchedulerException {
        String query = "INSERT INTO Lote (Fecha_inicio, Fecha_fin, Fecha_necesidad, ID_diluidor, Tipo, Plant, Cantidad, Planning_class, Estado, Descripcion, N_Lote, Item) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, batch.startDate().toString());
            statement.setString(2, batch.needDate().toString());
            statement.setString(3, batch.needDate().toString());
            statement.setInt(4, batch.dilutor().getId());
            statement.setString(5, batch.type().getValue());
            statement.setString(6, batch.plant());
            statement.setInt(7, batch.quantity());
            statement.setString(8, batch.planningClass());
            statement.setString(9, batch.status().getValue());
            statement.setString(10, batch.description());
            statement.setInt(11, batch.nBatch());
            statement.setString(12, batch.item());
            
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
    public void updateBatchDB(Batch batch) throws PPGSchedulerException {
        String query = "UPDATE Lote SET Fecha_inicio = ? WHERE Lote.N_Lote = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, batch.startDate().toString());
            statement.setInt(2, batch.nBatch());
            
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
    
    //Diluidores
    private Dilutor getDilutorDB(int id) {
        String query = "SELECT * FROM Diluidores WHERE ID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("Name");
                    int capacity = resultSet.getInt("Capacity");
                    
                    return new Dilutor(id, name, capacity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void getDilutorsDB(HashMap<Integer, Dilutor> diluidores) throws PPGSchedulerException {
        assert diluidores != null;
        String query = "SELECT ID, Name, Capacity FROM Diluidores";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                int capacidad = resultSet.getInt("Capacity");
                Dilutor dilutor = new Dilutor(id, name, capacidad);
                diluidores.put(id, dilutor);
            }
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
    }
    
    //Funcionalidad
    public ArrayList<Dilutor> getDilutorsAndBatches() throws PPGSchedulerException {
        HashMap<Integer, Dilutor> diluidoresHashMap = new HashMap<>();
        
        getDilutorsDB(diluidoresHashMap);
        getBatchesDB(diluidoresHashMap);
        ArrayList<Dilutor> diluidores = new ArrayList<>();
        
        for (Integer id : diluidoresHashMap.keySet()) {
            diluidores.add(diluidoresHashMap.get(id));
        }
        
        return diluidores;
    }
}
