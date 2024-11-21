package org.ppg.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

//Clase y constructor de lote
public record Batch(int nBatch, String planningClass, String plant, String item, int quantity, LocalDate startDate,
                    LocalDate needDate, Statuses status, String description, Types type, Dilutor dilutor) {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    //Getters
    public StringProperty[] getProperties() {
        StringProperty[] properties = new StringProperty[9];
        properties[0] = new SimpleStringProperty(Integer.toString(nBatch));
        properties[1] = new SimpleStringProperty(planningClass);
        properties[2] = new SimpleStringProperty(plant);
        properties[3] = new SimpleStringProperty(item);
        properties[4] = new SimpleStringProperty(Integer.toString(quantity));
        properties[5] = new SimpleStringProperty(startDate.format(formatter));
        properties[6] = new SimpleStringProperty(needDate.format(formatter));
        properties[7] = new SimpleStringProperty(status.getValue());
        properties[8] = new SimpleStringProperty(description);
        return properties;
    }
    
    //Override
    @Override
    public Batch clone() {
        return new Batch(nBatch, planningClass, plant, item, quantity, startDate, needDate, status, description, type, dilutor);
    }
    
    @Override
    public String toString() {
        return "Número de lote " + nBatch + "\n" +
                "Clase de planificación " + planningClass + "\n" +
                "Planta " + plant + "\n" + "Artículo " + item + "\n" +
                "Cantidad " + quantity + "\n" +
                "Fecha de inicio " + startDate.format(formatter) + "\n" +
                "Fecha de necesidad " + needDate.format(formatter) + "\n" +
                "Estado " + status.getValue() + "\n" +
                "Descripción " + description + "\n" +
                "Tipo " + type.getValue() + "\n" +
                "Dilutor " + dilutor.getName();
    }
}
