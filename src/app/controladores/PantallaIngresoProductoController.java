package app.controladores;

import app.modelo.AppData;
import app.modelo.Producto;
import app.servicios.ProductoService;
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
    @FXML private TextField campoNombrePaquete;
    @FXML private RadioButton radioConvSi;
    @FXML private RadioButton radioConvNo;

    @FXML
    private void initialize() {
        ToggleGroup grupo = new ToggleGroup();
        ToggleGroup grupoConv = new ToggleGroup();
        radioConvSi.setToggleGroup(grupoConv);
        radioConvNo.setToggleGroup(grupoConv);

        radioConvSi.setOnAction(event -> {
            campoUnidadConversion.setDisable(false);
            campoFactor.setDisable(false);
            actualizarVistaFactor();
        });

        radioConvNo.setOnAction(event -> {
            campoUnidadConversion.setDisable(true);
            campoFactor.setDisable(true);
            labelFactorPreview.setText("");
        });

        radioSi.setOnAction(event -> {
            campoUnidadesPorPaquete.setDisable(false);
            campoNombrePaquete.setDisable(false);
        });

        radioNo.setOnAction(event -> {
            campoUnidadesPorPaquete.setDisable(true);
            campoNombrePaquete.setDisable(true);
        });
        radioSi.setToggleGroup(grupo);
        radioNo.setToggleGroup(grupo);
        campoUnidadMinima.textProperty().addListener((obs, oldVal, newVal) -> actualizarVistaFactor());
        campoUnidadConversion.textProperty().addListener((obs, oldVal, newVal) -> actualizarVistaFactor());

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
            String unidadesPorPaqueteStr = campoUnidadesPorPaquete.getText();
            String nombrePaquete = campoNombrePaquete.getText();

            boolean esEmpaquetable = radioSi.isSelected();
            boolean tieneConversion = radioConvSi.isSelected();

            if (nombre.isEmpty() || unidadMinima.isEmpty() || cantidadStr.isEmpty()) {
                mostrarAlerta("Completá los datos básicos del producto.");
                return;
            }

            if (tieneConversion && (unidadConv.isEmpty() || factorStr.isEmpty())) {
                mostrarAlerta("Completá unidad y factor de conversión.");
                return;
            }

            if (esEmpaquetable && (nombrePaquete.isEmpty() || unidadesPorPaqueteStr.isEmpty())) {
                mostrarAlerta("Completá nombre del paquete y unidades por paquete.");
                return;
            }

            try {
                double cantidadMinima = parsearNumero(cantidadStr);
                double factor = esEmpaquetable
                        ? parsearNumero(unidadesPorPaqueteStr)
                        : (tieneConversion ? parsearNumero(factorStr) : 0);

                if (tieneConversion || esEmpaquetable) {
                    if (factor <= 0) {
                        mostrarAlerta("El factor de conversión debe ser mayor a cero.");
                        return;
                    }
                }

                ProductoService productoService = new ProductoService();
                productoService.crearProducto(nombre, unidadMinima, unidadConv, factor, esEmpaquetable, tieneConversion, nombrePaquete, (int) cantidadMinima);

                mostrarAlerta("Producto ingresado correctamente.");
                limpiarCampos();
            } catch (NumberFormatException e) {
                mostrarAlerta("Los campos de cantidad y factor deben ser numéricos.");
            }
        });



    }

    private double parsearNumero(String texto) throws NumberFormatException {
        texto = texto.trim().replace(",", ".");
        return Double.parseDouble(texto);
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
