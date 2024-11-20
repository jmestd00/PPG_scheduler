package org.ppg.model;

public enum Statuses {
    EN_ESPERA("EN_ESPERA"),
    EN_PROCESSO("EN_PROCESSO"),
    FINALIZADO("FINALIZADO"),
    EN_DEMORA("EN_DEMORA");
    private final String value;
    private Statuses(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public static Statuses fromValue(String value) {
        for (Statuses estado : Statuses.values()) {
            if (estado.getValue().equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}
