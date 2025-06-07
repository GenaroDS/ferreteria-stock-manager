package app.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class PantallaSeleccionIngresoController {

    @FXML private Button btnNuevoProducto;
    @FXML private Button btnExistente;
    @FXML private Button btnVolver;

    @FXML
    private void initialize() {
        btnNuevoProducto.setOnAction(e -> cambiarPantalla("/vistas/PantallaIngresoProducto.fxml"));
        btnExistente.setOnAction(e -> cambiarPantalla("/vistas/PantallaProductoExistente.fxml"));
        btnVolver.setOnAction(e -> cambiarPantalla("/vistas/PantallaMenuPrincipal.fxml"));
    }

    private void cambiarPantalla(String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) btnNuevoProducto.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
