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
        // Ir a pantalla de ingreso de nuevo producto
        btnNuevoProducto.setOnAction(e -> cambiarPantalla("/vistas/PantallaIngresoProducto.fxml"));

        // Ir a pantalla de ingreso para producto existente
        btnExistente.setOnAction(e -> cambiarPantalla("/vistas/PantallaProductoExistente.fxml"));

        // Volver al menú principal
        btnVolver.setOnAction(e -> cambiarPantalla("/vistas/PantallaMenuPrincipal.fxml"));
    }

    // Cambia de pantalla cargando el FXML indicado
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
