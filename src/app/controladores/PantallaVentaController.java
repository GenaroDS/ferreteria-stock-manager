package app.controladores;
import app.servicios.InventarioService;
import app.modelo.AppData;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class PantallaVentaController {

    @FXML private ComboBox<Producto> comboProductos;
    @FXML private TextField campoCantidad;
    @FXML private Button btnVender;
    @FXML private Button btnVolver;
    @FXML private TextArea areaResultado;
    @FXML private ComboBox<String> comboUnidades;

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

        btnVender.setOnAction(event -> {
            Producto producto = comboProductos.getValue();
            String unidadSeleccionada = comboUnidades.getValue();
            String cantidadStr = campoCantidad.getText().trim().replace(",", ".");

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
                InventarioService inventarioService = new InventarioService();

                if (unidad == null || unidadMinima == null) {
                    mostrarAlerta("Unidad no válida.");
                    return;
                }

                if (unidad.isPaquete()) {
                    int cantidadPaquetes = (int) cantidadIngresada;
                    boolean exito = inventarioService.descontarStock(producto, cantidadPaquetes, unidadSeleccionada);
                    if (exito) {
                        mostrarAlerta("Venta realizada. Se descontaron " + formatearNumero(cantidadPaquetes) + " " + unidadSeleccionada + ".");
                    } else {
                        mostrarAlerta("No hay suficiente stock de paquetes.");
                    }

                } else {
                    double cantidadConvertida = cantidadIngresada / unidad.getFactorConversion();
                    int cantidadEntera = (int) cantidadConvertida;
                    double sobrante = cantidadConvertida - cantidadEntera;

                    String unidadMin = unidadMinima.getUnidad();

                    boolean exito = inventarioService.descontarStock(producto, cantidadEntera, unidadMin);
                    if (exito) {
                        if (sobrante > 0) {
                            mostrarAlerta("Se vendieron " + cantidadEntera + " " + unidadMin +
                                    " porque el valor ingresado (" + formatearNumero(cantidadIngresada) + " " + unidad.getUnidad() +
                                    ") no es múltiplo del factor de conversión (" + formatearNumero(unidad.getFactorConversion()) + ").\n" +
                                    "Sobrante no descontado: " + formatearNumero(sobrante) + " " + unidadMin + ".");
                        } else {
                            mostrarAlerta("Venta realizada. Se descontaron " + formatearNumero(cantidadEntera) + " " + unidadMin + ".");
                        }
                    } else {
                        mostrarAlerta("No hay suficiente stock disponible en " + unidadMin + ".");
                    }
                }

                campoCantidad.clear();

            } catch (NumberFormatException e) {
                mostrarAlerta("La cantidad debe ser un número válido.");
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
    private String formatearNumero(double numero) {
        return numero % 1 == 0 ? String.valueOf((int) numero) : String.valueOf(numero);
    }


}
