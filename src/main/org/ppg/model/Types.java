package org.ppg.model;

public enum Types {
    PIMM("PIMM"), PISC("PISC");
    
    private final String value;
    
    Types(String value) {
        this.value = value;
    }
    
    public static Types fromValue(String value) {
        for (Types type : Types.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
    
    public String getValue() {
        return value;
    }
}
