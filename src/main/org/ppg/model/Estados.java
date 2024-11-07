package org.ppg.model;

public enum Estados {
    EN_ESPERA("EN_ESPERA"),
    EN_PROCESSO("EN_PROCESSO"),
    FINALIZADO("FINALIZADO"),
    EN_DEMORA("EN_DEMORA");
    private final String value;
    private Estados(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public static Estados fromValue(String value) {
        for (Estados estado : Estados.values()) {
            if (estado.getValue().equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}
