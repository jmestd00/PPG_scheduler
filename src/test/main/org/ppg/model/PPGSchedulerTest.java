package org.ppg.model;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.sql.DriverManager.getConnection;
import static org.junit.Assert.assertTrue;

public class PPGSchedulerTest {

    // TODO Hay que implementar los test

    @Before
    public void setUp() {

    }

    @Test /* (expected = PPGSchedulerException.class) */
    public void testTemplate() /* throws PPGSchedulerException */ {
        /*
         * assertTrue();
         * assertFalse();
         * assertEquals();
         * ...
         */
    }

    @Test
    public void testGetDiluidoresFromDatabase() throws PPGSchedulerException{
        PPGScheduler app = new PPGScheduler();
        ArrayList<Diluidor> diluidores = app.obtenerDiluidoresDeLaBaseDeDatos();
        System.out.println(diluidores);
    }

    @Test
    public void testUpdateLoteDB() throws PPGSchedulerException {
        PPGScheduler app = new PPGScheduler();
        Lote lote = new Lote(3, "VD-APA", "VDM", "A-RXX3359-DD", 650, "PISC", new Date(8, 11, 2025), new Date(15, 11, 2025), new Date(15, 11, 2025), Estados.EN_DEMORA, 1, 0);
        app.actualizarLoteDB(lote);
    }

    @Test
    public void testInsertLoteDB() throws PPGSchedulerException {
        PPGScheduler app = new PPGScheduler();
        Lote lote = new Lote(3, "VD-APA", "VDM", "A-RXX3359-DD", 650, "PISC", new Date(8, 11, 2025), new Date(15, 11, 2025), new Date(15, 11, 2025), Estados.EN_DEMORA, 1, 0);
        app.insertarLoteDB(lote);
    }



    @Test
    public void loadCSV() throws PPGSchedulerException {
        File file = new File("./src/main/resources/csv/schedule_template.csv");
        List<String[]> csv = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] columns = line.split(",", -1);
                csv.add(columns);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        csv.removeFirst();
        String hostname = "josedm64rpi.ddns.net";
        String username = "PPG";
        String password = "PPG.";
        int port = 3306;
        String database = "PPG_scheduler";
        String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;
        Connection connection;
        try {
            connection = getConnection(url, username, password);
        } catch (SQLException e) {
            throw new PPGSchedulerException(e.getMessage());
        }
        int diluidor = 1;
        HashMap<String, Boolean> map = new HashMap<>(300);
        for (String[] row: csv) {
            String query = "INSERT INTO Lote (Fecha_inicio, Fecha_fin, Fecha_necesidad, ID_diluidor, Tipo, Plant, Cantidad, Planning_class, Estado, Descripcion, N_Lote, Item) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, formatDate(row[15])); // Fecha_inicio - Columna "Bx_Start"
                statement.setString(2, formatDate(row[16])); // Fecha_fin - Columna "Bx_End"
                statement.setString(3, formatDate(row[17])); // Fecha_necesidad - Columna "Required_Completion_Date"
                statement.setInt(4, (diluidor%6)+1);
                diluidor++;
                // ID_diluidor - Columna "Routing_Code"
                if(row[7].length()>=4){
                    String type = row[7].substring(row[7].length()-5, row[7].length()-1);
                    if(!type.equals("PISC") && !type.equals("PIMM"))type = "PISC";
                    statement.setString(5, type);
                }
                statement.setString(6, row[4]);  // Plant - Columna "Plant"
                statement.setString(7, row[12]); // Cantidad - Columna "Planned_Quantity_KG"
                statement.setString(8, row[0]);  // Planning_class - Columna "PPG_Planning_Class"
                String estado = row[6].equals("Pending")?"EN_ESPERA":"EN_PROCESO";
                statement.setString(9, estado); // Estado - Columna "Batch_Status"
                statement.setString(10,row[19]+'\n'+row[20]);
                statement.setInt(11, diluidor);
                statement.setString(12, row[1]);
                int rowsAffected = statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public String formatDate(String date) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return targetFormat.format(originalFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
