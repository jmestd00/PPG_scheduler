package org.ppg.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public record Lote(int nLote, String planningClass, String plant, String item, int quantity, LocalDate fStart, LocalDate fNeed, Estados estado, String descriptions, String type) {

    public StringProperty[] getProperties(){
        StringProperty[] properties = new StringProperty[8];
        properties[0] = new SimpleStringProperty(Integer.toString(nLote));
        properties[1] = new SimpleStringProperty(planningClass);
        properties[2] = new SimpleStringProperty(plant);
        properties[3] = new SimpleStringProperty(item);
        properties[4] = new SimpleStringProperty(Integer.toString(quantity));
        properties[5] = new SimpleStringProperty(fStart.toString());
        properties[6] = new SimpleStringProperty(fNeed.toString());
        properties[7] = new SimpleStringProperty(descriptions);
        return properties;
    }

    @Override
    public Lote clone(){
        Lote clone = new Lote(nLote,planningClass, plant, item,quantity, fStart, fNeed, estado, descriptions, type);
        return clone;
    }

}
