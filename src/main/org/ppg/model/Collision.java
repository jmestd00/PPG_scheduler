package org.ppg.model;

/**
 * Representa una colisión detectada entre dos lotes.
 *
 * <p>El lote asociado es aquel que tiene la menor fecha de necesidad, y el
 * factor de colisión representa el número de días en los que ambos lotes coinciden.</p>
 *
 * <p>Autor: Jose Benito Edu Ngomo Medja</p>
 * <p>Versión: 1.5</p>
 * <p>Fecha: 19 de noviembre de 2024</p>
 */
public class Collision {
    private final int collisionFactor;
    private final Batch collidedBatch;

    //Constructor

    /**
     * Crea una nueva instancia de colisión entre dos lotes.
     *
     * @param collisionFactor Número de días en los que los lotes coinciden.
     * @param collidedBatch   El lote asociado, con la menor fecha de necesidad.
     */
    public Collision(int collisionFactor, Batch collidedBatch) {
        this.collisionFactor = collisionFactor;
        this.collidedBatch = collidedBatch;
    }

    //Getter

    /**
     * Obtiene el lote asociado en la colisión.
     *
     * @return El lote con la menor fecha de necesidad.
     */
    public Batch getCollidedBatch() {
        return collidedBatch;
    }

    //Method

    /**
     * Obtiene el factor de colisión.
     *
     * @return Número de días en los que los lotes coinciden.
     */
    public int factor() {
        return collisionFactor;
    }

    //Override

    /**
     * Devuelve una representación en texto de la colisión en el formato `(f=factor, getCollidedBatch=lote)`.
     *
     * @return Una cadena que representa la colisión.
     */
    @Override
    public String toString() {
        return "(factor:" + collisionFactor +
                ", batch:" + collidedBatch +
                ')';
    }
}
