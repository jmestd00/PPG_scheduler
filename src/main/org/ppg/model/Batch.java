package org.ppg.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public record Batch(int nBatch, String planningClass, String plant, String item, int quantity, LocalDate dStart, LocalDate dNeed, Statuses status, String description, String type) {

    public StringProperty[] getProperties(){
        StringProperty[] properties = new StringProperty[8];
        properties[0] = new SimpleStringProperty(Integer.toString(nBatch));
        properties[1] = new SimpleStringProperty(planningClass);
        properties[2] = new SimpleStringProperty(plant);
        properties[3] = new SimpleStringProperty(item);
        properties[4] = new SimpleStringProperty(Integer.toString(quantity));
        properties[5] = new SimpleStringProperty(dStart.toString());
        properties[6] = new SimpleStringProperty(dNeed.toString());
        properties[7] = new SimpleStringProperty(description);
        return properties;
    }
    @Override
    public String description() {
        return description;
    }
    public LocalDate dStart() {
        return dStart;
    }
    @Override
    public String type() {
        return type;
    }
    public LocalDate dNeed() {
        return dNeed;
    }
    public Statuses status() {
        return status;
    }
    @Override
    public int quantity() {
        return quantity;
    }
    @Override
    public String item() {
        return item;
    }
    @Override
    public String planningClass() {
        return planningClass;
    }
    @Override
    public String plant() {
        return plant;
    }
    public int nBatch() {
        return nBatch;
    }
    @Override
    public Batch clone(){
        Batch clone = new Batch(nBatch,planningClass, plant, item,quantity, dStart, dNeed, status, description, type);
        return clone;
    }
}
