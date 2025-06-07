package app;

import app.modelo.AppData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/vistas/PantallaMenuPrincipal.fxml"));
        Scene scene = new Scene(root); // ajustá el tamaño si querés
        AppData.inicializarDatos();
        stage.setScene(scene);

        stage.setTitle("Ferretería - Menú Principal");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
