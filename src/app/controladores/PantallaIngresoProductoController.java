package app.controladores;

import app.modelo.AppData;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;
import app.modelo.Inventario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PantallaIngresoProductoController {

    @FXML private TextField campoProducto;
    @FXML private TextField campoUnidadMinima;
    @FXML private TextField campoCantidadMinima;
    @FXML private TextField campoUnidadConversion;
    @FXML private TextField campoFactor;
    @FXML private RadioButton radioSi;
    @FXML private RadioButton radioNo;
    @FXML private TextField campoUnidadesPorPaquete;
    @FXML private Button btnGuardar;
    @FXML private Label labelFactorPreview;
    @FXML private Button btnVolver;

    @FXML
    private void initialize() {
        ToggleGroup grupo = new ToggleGroup();
        radioSi.setToggleGroup(grupo);
        radioNo.setToggleGroup(grupo);
        campoUnidadMinima.textProperty().addListener((obs, oldVal, newVal) -> actualizarVistaFactor());
        campoUnidadConversion.textProperty().addListener((obs, oldVal, newVal) -> actualizarVistaFactor());

        radioSi.setOnAction(event -> campoUnidadesPorPaquete.setDisable(false));
        radioNo.setOnAction(event -> campoUnidadesPorPaquete.setDisable(true));

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
        btnGuardar.setOnAction(event -> {
            String nombre = campoProducto.getText();
            String unidadMinima = campoUnidadMinima.getText();
            String cantidadStr = campoCantidadMinima.getText();
            String unidadConv = campoUnidadConversion.getText();
            String factorStr = campoFactor.getText();
            boolean esEmpaquetable = radioSi.isSelected();
            String unidadesPorPaqueteStr = campoUnidadesPorPaquete.getText();

            if (nombre.isEmpty() || unidadMinima.isEmpty() || cantidadStr.isEmpty() ||
                    unidadConv.isEmpty() || factorStr.isEmpty() || (esEmpaquetable && unidadesPorPaqueteStr.isEmpty())) {
                mostrarAlerta("Todos los campos son obligatorios.");
                return;
            }

            try {
                int cantidadMinima = Integer.parseInt(cantidadStr);
                int factor = Integer.parseInt(factorStr);
                int unidadesPorPaquete = esEmpaquetable ? Integer.parseInt(unidadesPorPaqueteStr) : 0;

                int nuevoId = AppData.getDatos().getProductos().size() + 1;
                Producto nuevo = new Producto(nuevoId, nombre, unidadMinima);

                UnidadDeConversion conversor = new UnidadDeConversion(
                        AppData.getDatos().getProductos().size() + 1,
                        nuevoId,
                        unidadConv,
                        factor,
                        esEmpaquetable
                );

                nuevo.agregarUnidadDeConversion(conversor);
                AppData.getDatos().getProductos().add(nuevo);

                int nuevoInvId = AppData.getDatos().getInventario().size() + 1;
                Inventario inv = new Inventario(nuevoInvId, nuevo, unidadMinima, cantidadMinima);
                AppData.getDatos().getInventario().add(inv);

                mostrarAlerta("Producto ingresado correctamente.");
                limpiarCampos();
            } catch (NumberFormatException e) {
                mostrarAlerta("Los campos de cantidad, factor y unidades por paquete deben ser numéricos.");
            }
        });
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    private void actualizarVistaFactor() {
        String uMin = campoUnidadMinima.getText();
        String uConv = campoUnidadConversion.getText();
        if (!uMin.isEmpty() && !uConv.isEmpty()) {
            labelFactorPreview.setText( uConv + " x " + uMin);
        } else {
            labelFactorPreview.setText("");
        }
    }

    private void limpiarCampos() {
        campoProducto.clear();
        campoUnidadMinima.clear();
        campoCantidadMinima.clear();
        campoUnidadConversion.clear();
        campoFactor.clear();
        campoUnidadesPorPaquete.clear();
        radioSi.setSelected(false);
        radioNo.setSelected(true);
        campoUnidadesPorPaquete.setDisable(true);
    }
}
