package org.ppg.model;

/**
 * Excepción del algoritmo en caso de no poder insertar un lote
 */
public class CantAddException extends Exception {
    public CantAddException(String message) {
        super(message);
    }
}
