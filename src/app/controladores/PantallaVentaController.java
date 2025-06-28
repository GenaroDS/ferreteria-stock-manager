package app.controladores;

import app.dao.ProductoDAO;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;
import app.modelo.Venta;
import app.servicios.InventarioService;
import app.servicios.VentaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

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

        try {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = dao.obtenerTodos();
            productos.sort(Comparator.comparing(Producto::getNombre));
            comboProductos.getItems().addAll(productos);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar productos.");
        }

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
                        new VentaService().registrarVenta(producto, unidadSeleccionada, cantidadPaquetes);
                        mostrarAlerta("Venta realizada. Se descontaron " + formatearNumero(cantidadPaquetes) + " " + unidadSeleccionada + ".");
                    } else {
                        mostrarAlerta("No hay suficiente stock de paquetes.");
                    }

                } else {
                    double cantidadConvertida = cantidadIngresada / unidad.getFactorConversion();
                    int cantidadEntera = (int) cantidadConvertida;
                    double sobrante = cantidadConvertida - cantidadEntera;
                    String unidadMin = unidadMinima.getUnidad();

                    if (cantidadEntera == 0) {
                        mostrarAlerta("No se puede realizar la venta porque la cantidad ingresada (" +
                                formatearNumero(cantidadIngresada) + " " + unidad.getUnidad() +
                                ") no alcanza al mínimo necesario (" + formatearNumero(unidad.getFactorConversion()) +
                                " " + unidad.getUnidad() + ") para descontar al menos 1 " + unidadMin + ".");
                        return;
                    }

                    boolean exito = inventarioService.descontarStock(producto, cantidadEntera, unidadMin);
                    if (exito) {
                        double cantidadRealVendida = cantidadEntera * unidad.getFactorConversion();
                        new VentaService().registrarVenta(producto, unidadSeleccionada, cantidadRealVendida);

                        if (sobrante > 0) {
                            double sobranteEnUnidadOriginal = sobrante * unidad.getFactorConversion();
                            mostrarAlerta("Se vendieron " + cantidadEntera + " " + unidadMin +
                                    " porque el valor ingresado (" + formatearNumero(cantidadIngresada) + " " + unidad.getUnidad() +
                                    ") no es múltiplo del factor de conversión (" + formatearNumero(unidad.getFactorConversion()) + ").\n" +
                                    "Sobrante no descontado: " + formatearConDecimales(sobranteEnUnidadOriginal, 2) + " " + unidad.getUnidad() + ".");
                        } else {
                            mostrarAlerta("Venta realizada. Se descontaron " + formatearNumero(cantidadEntera) + " " + unidadMin + ".");
                        }

                        double stockPostVenta = inventarioService.consultarStock(producto, unidadMin);
                        if (stockPostVenta < producto.getStockMinimoUnidadMinima()) {
                            mostrarWarningStockBajo(producto, stockPostVenta);
                        }
                    }
                }

                campoCantidad.clear();

            } catch (NumberFormatException e) {
                mostrarAlerta("La cantidad debe ser un número válido.");
            }
        });
    }

    private void mostrarWarningStockBajo(Producto producto, double stockActual) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Stock Bajo");
        alerta.setHeaderText("Stock bajo detectado.");
        alerta.setContentText("El producto \"" + producto.getNombre() +
                "\" tiene un stock de " + formatearNumero(stockActual) +
                " unidades, por debajo del mínimo configurado de " +
                formatearNumero(producto.getStockMinimoUnidadMinima()));
        alerta.showAndWait();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        DialogPane dialogPane = alerta.getDialogPane();
        dialogPane.setMinWidth(Region.USE_PREF_SIZE);
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);

        Label contentLabel = (Label) dialogPane.lookup(".content.label");
        if (contentLabel != null) {
            contentLabel.setWrapText(true);
            contentLabel.setMaxWidth(Double.MAX_VALUE);
        }

        dialogPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        dialogPane.setPrefHeight(Region.USE_COMPUTED_SIZE);

        alerta.showAndWait();
    }

    private String formatearNumero(double numero) {
        return Math.abs(numero - Math.round(numero)) < 0.00001
                ? String.valueOf((int) Math.round(numero))
                : formatearConDecimales(numero, 2);
    }


    private String formatearConDecimales(double numero, int decimales) {
        return String.format("%." + decimales + "f", numero);
    }

}
