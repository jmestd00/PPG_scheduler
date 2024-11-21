package org.ppg.model;

public enum Statuses {
    EN_ESPERA("EN ESPERA", "#009BFF"), EN_PROCESO("EN PROCESO", "#FF8C00"), FINALIZADO("FINALIZADO", "#28A745"), EN_DEMORA("EN DEMORA", "#c30b00");
    
    private final String value;
    private final String hex_color;
    
    Statuses(String value, String hex_color) {
        this.value = value;
        this.hex_color = hex_color;
    }
    
    public static Statuses fromValue(String value) {
        for (Statuses status : Statuses.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
    
    public String getValue() {
        return value;
    }
    
    public String getHexColor() {
        return hex_color;
    }
}
