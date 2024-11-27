package org.ppg.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Batch {

    private final int nBatch;
    private final String planningClass;
    private final String plant;
    private final String item;
    private final int quantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate needDate;
    private Statuses status;
    private final String description;
    private final Types type;
    private int dilutor;
    private int duration;

    public Batch(int nBatch, String planningClass, String plant, String item, int quantity, LocalDate startDate, LocalDate endDate, LocalDate needDate, Statuses status, String description, Types type, int dilutor, int duration) {
        this.nBatch = nBatch;
        this.planningClass = planningClass;
        this.plant = plant;
        this.item = item;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.needDate = needDate;
        this.status = status;
        this.description = description;
        this.type = type;
        this.dilutor = dilutor;
        this.duration = duration;
    }

    public Batch(int nBatch, String planningClass, String plant, String item, int quantity, String description, Types type, LocalDate needDate) {
        this.nBatch = nBatch;
        this.planningClass = planningClass;
        this.plant = plant;
        this.item = item;
        this.quantity = quantity;
        this.description = description;
        this.type = type;
        this.needDate = needDate;
    }

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
    public Batch(int nBatch, String planningClass, String plant, String item, int quantity, LocalDate needDate, Types type, String description) {
        this.nBatch = nBatch;
        this.planningClass = planningClass;
        this.plant = plant;
        this.item = item;
        this.quantity = quantity;
        this.needDate = needDate;
        this.type = type;
        this.description = description;
    }

    public int getnBatch() {
        return nBatch;
    }
    public int getQuantity() {
        return quantity;
    }
    public Types getType() {
        return this.type;
    }
    public String getPlant() {
        return this.plant;
    }
    public String getPlannigClass() {
        return this.planningClass;
    }
    public Statuses getStatus() {
        return status;
    }
    public String getItem() {
        return this.item;
    }
    public int getId() {
        return this.nBatch;
    }
    public LocalDate getStartDate() {
        return this.startDate;
    }
    public LocalDate getEndDate() {
        return this.endDate;
    }
    public LocalDate getNeedDate() {
        return this.needDate;
    }
    public String getDescription() {
        return description;
    }
    public long getDuration() {
        return this.duration;
    }
    public void setDilutor(int dilutor) {
        this.dilutor = dilutor;
    }

    public void setEndDate(LocalDate fechaFin) {
        this.endDate = fechaFin;
    }

    public int getDilutorId() {
        return dilutor;
    }

    public void setStartDate(LocalDate fechaInicio) {
        this.startDate = fechaInicio;
    }

    //Override
    @Override
    public Batch clone() {
        return null;
        //return new Batch(nBatch, planningClass, plant, item, quantity, startDate, needDate, status, description, type, dilutor);
    }
}
