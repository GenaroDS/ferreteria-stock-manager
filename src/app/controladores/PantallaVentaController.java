package app.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PantallaVentaController {

    @FXML private ComboBox<String> comboProducto;
    @FXML private TextField campoCantidad;
    @FXML private Button btnVender;
    @FXML private Button btnVolver;
    @FXML private TextArea areaResultado;

    @FXML
    private void initialize() {
        comboProducto.getItems().addAll("Tornillos", "Clavos", "Cemento");

        btnVolver.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/PantallaMenuPrincipal.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnVolver.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
