package org.ppg.model;

public enum Statuses {
    EN_ESPERA("EN ESPERA", "#42aaff", "#0057d9"), EN_PROCESO("EN PROCESO", "#ffa726", "#f57c00"), FINALIZADO("FINALIZADO", "#a8e6a3", "#56c453"), EN_DEMORA("RETARDADO", "#e0e0e0", "#9e9e9e"), EN_ADELANTO("ADELANTADO", "#e0e0e0", "#9e9e9e");
    
    private final String value;
    private final String hex_color_primary;
    private final String hex_color_secondary;
    
    Statuses(String value, String hex_color_primary, String hex_color_secondary) {
        this.value = value;
        this.hex_color_primary = hex_color_primary;
        this.hex_color_secondary = hex_color_secondary;
    }
    
    public static Statuses fromValue(String value) {
        value = value.replace('_', ' ');
        
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
    
    public String getHexColorPrimary() {
        return hex_color_primary;
    }
    
    public String getHexColorSecondary() {
        return hex_color_secondary;
    }
}
