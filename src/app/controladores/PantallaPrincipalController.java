package app.controladores;

import app.modelo.AppData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.application.Platform;


public class PantallaPrincipalController {

    @FXML private Button btnVolver;
    @FXML private Button btnVenta;
    @FXML private Button btnConsultarStock;
    @FXML private Button btnIngreso;
    @FXML private Button btnBolsa;
    @FXML private Button btnHistorialVentas;


    @FXML
    private void initialize() {
        btnVolver.setOnAction(event -> Platform.exit());

        btnConsultarStock.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/PantallaConsultarStock.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnConsultarStock.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        btnBolsa.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/PantallaBolsa.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnBolsa.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnVenta.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/PantallaVenta.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnVenta.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnIngreso.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/PantallaSeleccionIngreso.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnIngreso.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnHistorialVentas.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/PantallaHistorialVentas.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnHistorialVentas.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}
