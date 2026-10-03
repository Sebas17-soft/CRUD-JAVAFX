package com.crud.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Clase principal que inicializa el ciclo de vida de la aplicación JavaFX.
 * Carga la vista FXML, configura la escena, aplica dimensiones y despliega la ventana principal.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Localización del archivo FXML en la ruta de recursos
            URL fxmlLocation = getClass().getResource("/com/crud/javafx/view/producto-view.fxml");
            if (fxmlLocation == null) {
                throw new IllegalStateException("No se pudo localizar el archivo FXML en: /com/crud/javafx/view/producto-view.fxml");
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Pane root = loader.load();

            // Configuración de la Escena (Scene)
            Scene scene = new Scene(root, 1100, 700);

            // Configuración de la Ventana Principal (Stage)
            primaryStage.setTitle("Sistema CRUD de Inventario - JavaFX (En Memoria / ArrayList)");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(650);

            // Centrar ventana en la pantalla del usuario
            primaryStage.centerOnScreen();

            // Desplegar la aplicación
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Error crítico al inicializar la interfaz gráfica de usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Punto de entrada de la aplicación Java.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
