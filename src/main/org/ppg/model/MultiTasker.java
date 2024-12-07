package org.ppg.model;

/**
 * Clase algo.MultiTasker permite ejecutar tareas en paralelo utilizando un número determinado de hilos.
 * Proporciona métodos para iniciar y gestionar el trabajo paralelo a través de la interfaz ParallelRegion.
 *
 * @author Jose Benito Edu Ngomo Medja
 * @version 1.0
 * @date 2024-11-14
 */
public class MultiTasker {
    /**
     * Interfaz ParallelRegion define una región de código que puede ejecutarse en paralelo.
     * Esta interfaz se utiliza en conjunto con la clase algo.MultiTasker para ejecutar tareas
     * en múltiples hilos, permitiendo que cada hilo ejecute una parte del trabajo total.
     */
    public interface ParallelRegion {
        /**
         * Método que define la lógica de ejecución de cada hilo en una región paralela.
         * Este método será invocado en paralelo por múltiples hilos, proporcionando a cada
         * hilo su número de identificación y el número total de hilos que participan.
         *
         * @param threadNumber    El identificador del hilo actual, que varía entre 0 y numberOfThreads - 1.
         * @param numberOfThreads El número total de hilos que están ejecutando esta región paralela.
         */
        void perform(int threadNumber, int numberOfThreads);
    }

    /**
     * Ejecuta una región paralela utilizando un número específico de hilos.
     * Crea y arranca cada hilo, los cuales ejecutarán el método perform de la interfaz ParallelRegion.
     * Una vez que todos los hilos están en ejecución, espera a que cada uno termine.
     *
     * @param numberOfThreads El número de hilos a utilizar para ejecutar la región paralela.
     * @param region          La implementación de ParallelRegion que define la lógica a ejecutar en paralelo.
     */
    public static void perform(final int numberOfThreads, ParallelRegion region) throws InterruptedException {
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < threads.length; i++) {
            final int threadNumber = i;
            threads[i] = new Thread(() -> region.perform(threadNumber, numberOfThreads));
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

    /**
     * Ejecuta una región paralela utilizando el número de hilos disponible en el sistema.
     * El número de hilos se determina utilizando el número de procesadores disponibles.
     * Este método llama al método perform con el número de hilos calculado.
     *
     * @param region La implementación de ParallelRegion que define la lógica a ejecutar en paralelo.
     */
    public static void perform(ParallelRegion region) throws InterruptedException {
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        perform(numberOfThreads, region);
    }
}