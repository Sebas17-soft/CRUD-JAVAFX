package com.crud.javafx;

/**
 * Clase intermediaria de arranque (Bootstrap Launcher).
 * Esta clase no extiende de {@link javafx.application.Application}, lo que permite
 * ejecutar la aplicación y empaquetar un JAR ejecutable (fat-jar) sin que la JVM
 * arroje el error de "JavaFX runtime components are missing".
 */
public class Launcher {

    /**
     * Método principal que delega el arranque a {@link MainApp}.
     *
     * @param args Argumentos de consola.
     */
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
