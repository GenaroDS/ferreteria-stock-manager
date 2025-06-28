package app.controladores;

import app.servicios.ProductoService;
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
    @FXML private TextField campoStockMinimo;

    @FXML
    private void initialize() {
        // Agrupar radio buttons
        ToggleGroup grupo = new ToggleGroup();
        ToggleGroup grupoConv = new ToggleGroup();
        radioConvSi.setToggleGroup(grupoConv);
        radioConvNo.setToggleGroup(grupoConv);

        // Habilitar campos de conversión si se selecciona "Sí"
        radioConvSi.setOnAction(event -> {
            campoUnidadConversion.setDisable(false);
            campoFactor.setDisable(false);
            actualizarVistaFactor();
        });

        // Deshabilitar campos de conversión si se selecciona "No"
        radioConvNo.setOnAction(event -> {
            campoUnidadConversion.setDisable(true);
            campoFactor.setDisable(true);
            labelFactorPreview.setText("");
        });

        // Habilitar campos de paquete si se selecciona "Sí"
        radioSi.setOnAction(event -> {
            campoUnidadesPorPaquete.setDisable(false);
            campoNombrePaquete.setDisable(false);
        });

        // Deshabilitar campos de paquete si se selecciona "No"
        radioNo.setOnAction(event -> {
            campoUnidadesPorPaquete.setDisable(true);
            campoNombrePaquete.setDisable(true);
        });
        campoUnidadConversion.setDisable(true);
        campoFactor.setDisable(true);
        radioSi.setToggleGroup(grupo);
        radioNo.setToggleGroup(grupo);

        // Actualizar vista previa del factor de conversión cuando cambian los textos
        campoUnidadMinima.textProperty().addListener((obs, oldVal, newVal) -> actualizarVistaFactor());
        campoUnidadConversion.textProperty().addListener((obs, oldVal, newVal) -> actualizarVistaFactor());

        // Acción del botón "Volver"
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

        // Acción del botón "Guardar"
        btnGuardar.setOnAction(event -> {
            String nombre = campoProducto.getText();
            String unidadMinima = campoUnidadMinima.getText();
            String cantidadStr = campoCantidadMinima.getText();
            String unidadConv = campoUnidadConversion.getText();
            String factorStr = campoFactor.getText();
            String unidadesPorPaqueteStr = campoUnidadesPorPaquete.getText();
            String nombrePaquete = campoNombrePaquete.getText();
            String stockMinimoStr = campoStockMinimo.getText();

            boolean esEmpaquetable = radioSi.isSelected();
            boolean tieneConversion = radioConvSi.isSelected();

            if (nombre.isEmpty() || unidadMinima.isEmpty() || cantidadStr.isEmpty()) {
                mostrarAlerta("Completá los datos básicos del producto.");
                return;
            }

            if (stockMinimoStr.isEmpty()) {
                mostrarAlerta("Completá el stock mínimo de alerta.");
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
                int stockMinimo = Integer.parseInt(stockMinimoStr);

                if (stockMinimo >= cantidadMinima) {
                    mostrarAlerta("El stock mínimo debe ser menor a la cantidad ingresada.");
                    return;
                }

                double factorConversion = tieneConversion ? parsearNumero(factorStr) : 0;
                double factorPaquete = esEmpaquetable ? parsearNumero(unidadesPorPaqueteStr) : 0;

                if ((tieneConversion && factorConversion <= 0) || (esEmpaquetable && factorPaquete <= 0)) {
                    mostrarAlerta("Los factores de conversión deben ser mayores a cero.");
                    return;
                }

                ProductoService productoService = new ProductoService();
                productoService.crearProducto(
                        nombre,
                        stockMinimo,
                        unidadMinima,
                        unidadConv,
                        factorConversion,
                        esEmpaquetable,
                        tieneConversion,
                        nombrePaquete,
                        (int) cantidadMinima,
                        factorPaquete
                );

                mostrarAlerta("Producto ingresado correctamente.");
                limpiarCampos();
            } catch (NumberFormatException e) {
                mostrarAlerta("Los campos de cantidad y factor deben ser numéricos.");
            }
        });

    }

    // Parsea un número desde texto (permite "," como decimal)
    private double parsearNumero(String texto) throws NumberFormatException {
        texto = texto.trim().replace(",", ".");
        return Double.parseDouble(texto);
    }

    // Muestra una alerta informativa
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Muestra vista previa de cómo quedaría el factor de conversión
    private void actualizarVistaFactor() {
        String uMin = campoUnidadMinima.getText();
        String uConv = campoUnidadConversion.getText();
        if (!uMin.isEmpty() && !uConv.isEmpty()) {
            labelFactorPreview.setText(uConv + " x " + uMin);
        } else {
            labelFactorPreview.setText("");
        }
    }

    // Limpia los campos del formulario
    private void limpiarCampos() {
        campoProducto.clear();
        campoUnidadMinima.clear();
        campoCantidadMinima.clear();
        campoUnidadConversion.clear();
        campoFactor.clear();
        campoUnidadesPorPaquete.clear();
        campoNombrePaquete.clear();
        radioSi.setSelected(false);
        radioNo.setSelected(true);
        campoUnidadesPorPaquete.setDisable(true);
        campoNombrePaquete.setDisable(true);
        campoStockMinimo.clear();
        labelFactorPreview.setText("");
    }

}
