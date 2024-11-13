package org.ppg.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public record Lote(
        int nLote, String planningClass, String plant, String item, int quantity, Date fStart, Date fNeed,
        Estados status, String description, Types type
) {
   
    public StringProperty[] getProperties() {
        StringProperty[] properties = new StringProperty[9];
        properties[0] = new SimpleStringProperty(Integer.toString(nLote));
        properties[1] = new SimpleStringProperty(planningClass);
        properties[2] = new SimpleStringProperty(plant);
        properties[3] = new SimpleStringProperty(item);
        properties[4] = new SimpleStringProperty(Integer.toString(quantity));
        properties[5] = new SimpleStringProperty(fStart.toString());
        properties[6] = new SimpleStringProperty(fNeed.toString());
        properties[7] = new SimpleStringProperty(status.toString());
        properties[8] = new SimpleStringProperty(description);
        return properties;
    }
}
