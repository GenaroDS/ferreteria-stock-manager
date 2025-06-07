package app.controladores;

import app.modelo.AppData;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;
import app.servicios.InventarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PantallaProductoExistenteController {

    @FXML private ComboBox<Producto> comboProductos;
    @FXML private ComboBox<String> comboUnidades;
    @FXML private TextField campoCantidad;
    @FXML private Button btnContinuar;
    @FXML private Button btnVolver;

    private final InventarioService inventarioService = new InventarioService();

    @FXML
    private void initialize() {
        comboUnidades.setDisable(true);
        comboProductos.getItems().addAll(AppData.getProductos());

        comboProductos.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Seleccionar producto" : item.getNombre());
            }
        });

        comboProductos.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });

        comboProductos.setOnAction(event -> {
            Producto producto = comboProductos.getValue();
            comboUnidades.getItems().clear();
            campoCantidad.clear();

            if (producto != null && !producto.getUnidadesAlternativas().isEmpty()) {
                producto.getUnidadesAlternativas().forEach(u -> comboUnidades.getItems().add(u.getUnidad()));
                comboUnidades.setDisable(false);
                comboUnidades.getSelectionModel().selectFirst();
            } else {
                comboUnidades.setDisable(true);
            }
        });

        btnContinuar.setOnAction(event -> {
            Producto producto = comboProductos.getValue();
            String unidadSeleccionada = comboUnidades.getValue();
            String cantidadStr = campoCantidad.getText().replace(",", ".");

            if (producto == null || unidadSeleccionada == null || cantidadStr.isEmpty()) {
                mostrarAlerta("Completá todos los campos.");
                return;
            }

            try {
                double cantidadIngresada = Double.parseDouble(cantidadStr);
                if (cantidadIngresada <= 0) {
                    mostrarAlerta("La cantidad debe ser mayor a cero.");
                    return;
                }

                UnidadDeConversion unidad = producto.getUnidad(unidadSeleccionada);
                UnidadDeConversion unidadMinima = producto.getUnidadMinima();

                if (unidad == null || unidadMinima == null) {
                    mostrarAlerta("Unidad no válida.");
                    return;
                }

                if (unidad.isPaquete()) {
                    double cantidadConvertida = cantidadIngresada * unidad.getFactorConversion();
                    inventarioService.agregarStock(producto, cantidadConvertida, unidadMinima.getUnidad());
                    mostrarAlerta("Se ingresaron " + formatearNumero(cantidadConvertida) + " " + unidadMinima.getUnidad() +
                            " (" + formatearNumero(cantidadIngresada) + " " + unidad.getUnidad() + ").");

                } else {
                    double cantidadConvertida = cantidadIngresada / unidad.getFactorConversion();
                    int cantidadEntera = (int) cantidadConvertida;
                    double sobrante = cantidadIngresada - (cantidadEntera * unidad.getFactorConversion());

                    inventarioService.agregarStock(producto, cantidadEntera, unidadMinima.getUnidad());

                    if (sobrante > 0) {
                        mostrarAlerta("Se ingresaron " + cantidadEntera + " " + unidadMinima.getUnidad() +
                                " porque el valor ingresado (" + formatearNumero(cantidadIngresada) + " " + unidad.getUnidad() +
                                ") no es múltiplo del factor de conversión (" + formatearNumero(unidad.getFactorConversion()) + ").\n" +
                                "Sobrante no registrado: " + formatearNumero(sobrante) + " " + unidad.getUnidad() + ".");
                    } else {
                        mostrarAlerta("Se ingresaron " + cantidadEntera + " " + unidadMinima.getUnidad() + ".");
                    }
                }

                campoCantidad.clear();

            } catch (NumberFormatException e) {
                mostrarAlerta("La cantidad debe ser numérica.");
            }
        });

        btnVolver.setOnAction(e -> cambiarPantalla("/vistas/PantallaSeleccionIngreso.fxml"));
    }

    private void cambiarPantalla(String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private String formatearNumero(double valor) {
        return (valor % 1 == 0) ? String.format("%.0f", valor) : String.format("%.2f", valor);
    }
}
