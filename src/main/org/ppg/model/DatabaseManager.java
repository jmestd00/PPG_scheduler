package org.ppg.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

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

    public Batch getBatchDB(int nBatch) throws PPGSchedulerException {
        Batch batch;
        String query = "SELECT Planning_class, plant, Item.Item , Cantidad, Fecha_inicio, Fecha_fin, Fecha_necesidad, Estado , Descripcion ,Tipo , ID_diluidor, Item.Duración as 'duration' FROM Lote INNER JOIN Item ON Lote.Item LIKE Item.Item WHERE N_Lote = ?";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, nBatch);
        } catch (SQLException e) {
            throw new PPGSchedulerException("Ha ocurrido un error en la conexión a la base de datos");
        }
        ResultSet resultSet;
        try {
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String planningClass = resultSet.getString("Planning_class");
                String plant = resultSet.getString("plant");
                String item = resultSet.getString("Item");
                int quantity = resultSet.getInt("Cantidad");
                LocalDate startDate = resultSet.getDate("Fecha_inicio").toLocalDate();
                LocalDate endDate = resultSet.getDate("Fecha_fin").toLocalDate();
                LocalDate needDate = resultSet.getDate("Fecha_necesidad").toLocalDate();
                Statuses status = Statuses.valueOf(resultSet.getString("Estado"));
                String description = resultSet.getString("Descripcion");
                Types type = Types.valueOf(resultSet.getString("Tipo"));
                int duration = resultSet.getInt("duration");
                int dilutorId = resultSet.getInt("ID_diluidor");
                batch = new Batch(nBatch, planningClass, plant, item, quantity, startDate, endDate, needDate, status, description, type, dilutorId, duration);
            } else {
                throw new PPGSchedulerException("El lote solicitado no existe en la base de datos");
            }
        } catch (SQLException e) {
            throw new PPGSchedulerException("Ha ocurrido un error en la conexión a la base de datos");
        }
        return batch;
    }

    public void insertBatchDB(Batch batch) throws PPGSchedulerException {
        String query = "INSERT INTO Lote (Fecha_inicio, Fecha_fin, Fecha_necesidad, ID_diluidor, Tipo, Plant, Cantidad, Planning_class, Estado, Descripcion, N_Lote, Item) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, batch.getStartDate().toString());
            statement.setString(2, batch.getNeedDate().toString());
            statement.setString(3, batch.getNeedDate().toString());
            statement.setInt(4, batch.getDilutorId());
            statement.setString(5, batch.getType().getValue());
            statement.setString(6, batch.getPlant());
            statement.setInt(7, batch.getQuantity());
            statement.setString(8, batch.getPlannigClass());
            statement.setString(9, batch.getStatus().getValue());
            statement.setString(10, batch.getDescription());
            statement.setInt(11, batch.getnBatch());
            statement.setString(12, batch.getItem());
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
        String query = "UPDATE Lote SET Fecha_inicio = ?, Fecha_fin=?, Fecha_necesidad=?, ID_diluidor=?, Tipo=?, Plant=?, Cantidad=?, Planning_class=?, Estado=?, Descripcion=?, Item=? WHERE Lote.N_Lote = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, batch.getStartDate().toString());
            statement.setString(2, batch.getEndDate().toString());
            statement.setString(3, batch.getNeedDate().toString());
            statement.setInt(4, batch.getDilutorId());
            statement.setString(5, batch.getType().getValue());
            statement.setString(6, batch.getPlant());
            statement.setInt(7, batch.getQuantity());
            statement.setString(8, batch.getPlannigClass());
            statement.setString(9, batch.getStatus().getValue());
            statement.setString(10, batch.getDescription());
            statement.setString(11, batch.getItem());
            statement.setInt(12, batch.getnBatch());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new PPGSchedulerException("Error. No se ha modificado ningun registro en la base de datos");
            }
        } catch (SQLException e) {
            throw new PPGSchedulerException("No se ha podido actualizar la base de datos debido a un error en la conexión");
        }
    }


    public ArrayList<Batch> getAllBatches() throws PPGSchedulerException {
        ArrayList<Batch> batches = new ArrayList<>();
        String query = "SELECT Fecha_inicio, Fecha_fin, Fecha_necesidad, ID_diluidor, Tipo, Plant, Cantidad, Planning_class, Estado, Descripcion, N_Lote, Lote.Item, Item.Duración as 'duration' FROM PPG_scheduler.Lote inner join Item on Item.Item like Lote.Item";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
        }catch (SQLException e){
            throw new PPGSchedulerException(e.getMessage());
        }
        try{
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                LocalDate startDate = resultSet.getDate("Fecha_inicio").toLocalDate();
                LocalDate needDate = resultSet.getDate("Fecha_necesidad").toLocalDate();
                LocalDate endDate = resultSet.getDate("Fecha_fin").toLocalDate();
                int dilutorID = resultSet.getInt("ID_diluidor");
                Types type = Types.fromValue(resultSet.getString("Tipo"));
                String plant = resultSet.getString("Plant");
                int quantity = resultSet.getInt("Cantidad");
                String planningClass = resultSet.getString("Planning_class");
                Statuses status = Statuses.fromValue(resultSet.getString("Estado"));
                String description = resultSet.getString("Descripcion");
                int nBatch = resultSet.getInt("N_Lote");
                String item = resultSet.getString("Item");
                int duration = resultSet.getInt("duration");
                Batch batch = new Batch(nBatch, planningClass, plant,item, quantity, startDate, endDate, needDate, status, description, type,dilutorID, duration);
                batches.add(batch);
            }
        }catch (SQLException e){
            throw new PPGSchedulerException(e.getMessage());
        }
        return batches;
    }

    public ArrayList<Batch> getBatches(int start, int offset) throws PPGSchedulerException {
        ArrayList<Batch> batches = new ArrayList<>();
        String query = "SELECT Fecha_inicio, Fecha_fin, Fecha_necesidad, ID_diluidor, Tipo, Plant, Cantidad, Planning_class, Estado, Descripcion, N_Lote, Lote.Item, Item.Duración as 'duration' FROM PPG_scheduler.Lote INNER JOIN Item ON Item.Item = Lote.Item LIMIT ?, ?";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, start);
            statement.setInt(2, offset);
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                LocalDate startDate = resultSet.getDate("Fecha_inicio").toLocalDate();
                LocalDate needDate = resultSet.getDate("Fecha_necesidad").toLocalDate();
                LocalDate endDate = resultSet.getDate("Fecha_fin").toLocalDate();
                int dilutorID = resultSet.getInt("ID_diluidor");
                Types type = Types.fromValue(resultSet.getString("Tipo"));
                String plant = resultSet.getString("Plant");
                int quantity = resultSet.getInt("Cantidad");
                String planningClass = resultSet.getString("Planning_class");
                Statuses status = Statuses.fromValue(resultSet.getString("Estado"));
                String description = resultSet.getString("Descripcion");
                int nBatch = resultSet.getInt("N_Lote");
                String item = resultSet.getString("Item");
                int duration = resultSet.getInt("duration");
                Batch batch = new Batch(nBatch, planningClass, plant, item, quantity, startDate, endDate, needDate, status, description, type, dilutorID, duration);
                batches.add(batch);
            }
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
        return batches;
    }

    public Dilutor getDilutorDB(int id) throws PPGSchedulerException {
        String query = "SELECT * FROM Diluidores WHERE ID = ?";
        PreparedStatement statement;
        Dilutor dilutor;
        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
        try{
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("Name");
                int capacity = resultSet.getInt("Capacity");
                dilutor = new Dilutor(id, name, capacity);
            }else{
                throw new PPGSchedulerException("");
            }
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
        return dilutor;
    }


    public HashMap<Integer, Dilutor> getDilutorsDB() throws PPGSchedulerException {
        HashMap<Integer, Dilutor> dilutors = new HashMap<>();
        String query = "SELECT ID, Name, Capacity FROM Diluidores";
        Statement statement;
        try{
            statement = connection.createStatement();
        }catch (SQLException e){
            throw new PPGSchedulerException(e.getMessage());
        }
        try{
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                int capacidad = resultSet.getInt("Capacity");
                Dilutor dilutor = new Dilutor(id, name, capacidad);
                dilutors.put(id, dilutor);
            }
        }catch (SQLException e){
            throw new PPGSchedulerException(e.getMessage());
        }
        return dilutors;
    }


    public LocalDate getFreeDilutorDate(int idDilutor) throws PPGSchedulerException {
        String query = "SELECT MAX(Fecha_fin) as 'Fecha_fin' FROM Lote WHERE ID_diluidor = ? AND Estado like 'EN_PROCESO'";
        LocalDate freeDilutorDate;
        PreparedStatement statement;
        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, idDilutor);
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
        try {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                freeDilutorDate = resultSet.getDate("Fecha_fin").toLocalDate();
            }else{
                throw new PPGSchedulerException("");
            }
        }catch (SQLException e){
            throw new PPGSchedulerException(e.getMessage());
        }
        return freeDilutorDate;
    }


    public ArrayList<Dilutor> getFilledDilutors() throws PPGSchedulerException {
        HashMap<Integer, Dilutor> dilutors = getDilutorsDB();
        ArrayList<Batch> batches = getAllBatches();
        for (Batch b : batches) {
            if(dilutors.get(b.getDilutorId()) != null){
                dilutors.get(b.getDilutorId()).addLote(b);
            }
        }
        ArrayList<Dilutor> dilutorArrayList = new ArrayList<>();
        for (Dilutor dilutor : dilutors.values()) {
            if (!dilutor.getLotes().isEmpty()) { // Filtrar solo diluidores con lotes asignados
                dilutorArrayList.add(dilutor);
            }
        }
        return dilutorArrayList;
    }


    public ArrayList<Batch> getBatchesWeekly() throws PPGSchedulerException {
        ArrayList<Batch> batches = new ArrayList<>();
        String query = "SELECT Fecha_inicio, Fecha_fin, Fecha_necesidad, ID_diluidor, Tipo, Plant, Cantidad, Planning_class, Estado, Descripcion, N_Lote, Lote.Item, Item.Duración FROM PPG_scheduler.Lote inner join Item on Item.Item like Lote.Item WHERE Fecha_inicio >= CURDATE() - INTERVAL WEEKDAY(CURDATE()) DAY AND Fecha_inicio < CURDATE() - INTERVAL (WEEKDAY(CURDATE()) - 6) DAY ORDER BY Fecha_inicio";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query);
        }catch (SQLException e){
            throw new PPGSchedulerException(e.getMessage());
        }
        try{
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                LocalDate startDate = resultSet.getDate("Fecha_inicio").toLocalDate();
                LocalDate needDate = resultSet.getDate("Fecha_necesidad").toLocalDate();
                LocalDate endDate = resultSet.getDate("Fecha_fin").toLocalDate();
                int dilutorID = resultSet.getInt("ID_diluidor");
                Types type = Types.fromValue(resultSet.getString("Tipo"));
                String plant = resultSet.getString("Plant");
                int quantity = resultSet.getInt("Cantidad");
                String planningClass = resultSet.getString("Planning_class");
                Statuses status = Statuses.fromValue(resultSet.getString("Estado"));
                String description = resultSet.getString("Descripcion");
                int nBatch = resultSet.getInt("N_Lote");
                String item = resultSet.getString("Item");
                int duration = resultSet.getInt("Duración");
                Batch batch = new Batch(nBatch, planningClass, plant,item, quantity, startDate, endDate, needDate, status, description, type,dilutorID, duration);
                batches.add(batch);
            }
        }catch (SQLException e){
            throw new PPGSchedulerException(e.getMessage());
        }
        return batches;
    }
}