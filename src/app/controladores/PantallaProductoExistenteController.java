package app.controladores;

import app.dao.ProductoDAO;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;
import app.servicios.InventarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

public class PantallaProductoExistenteController {

    @FXML private ComboBox<Producto> comboProductos; // ComboBox para seleccionar producto
    @FXML private ComboBox<String> comboUnidades; // ComboBox para seleccionar unidad de medida
    @FXML private TextField campoCantidad; // Campo para ingresar cantidad
    @FXML private Button btnContinuar; // Botón para confirmar ingreso
    @FXML private Button btnVolver; // Botón para volver a pantalla anterior

    private final InventarioService inventarioService = new InventarioService(); // Servicio para gestionar inventario

    @FXML
    private void initialize() {
        comboUnidades.setDisable(true); // Deshabilita comboUnidades inicialmente

        // Carga productos desde DAO y los ordena por nombre
        try {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = dao.obtenerTodos();
            productos.sort(Comparator.comparing(Producto::getNombre));
            comboProductos.getItems().addAll(productos);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar productos.");
        }

        // Configura ComboBox para mostrar solo nombres de productos
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

        // Carga unidades alternativas al seleccionar un producto
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

        // Procesa ingreso de stock al hacer clic en Continuar
        btnContinuar.setOnAction(event -> {
            Producto producto = comboProductos.getValue();
            String unidadSeleccionada = comboUnidades.getValue();
            String cantidadStr = campoCantidad.getText().replace(",", ".");

            // Valida campos obligatorios
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

                // Valida unidades
                if (unidad == null || unidadMinima == null) {
                    mostrarAlerta("Unidad no válida.");
                    return;
                }

                // Maneja ingreso de stock según tipo de unidad
                if (unidad.isPaquete()) {
                    inventarioService.agregarStock(producto, cantidadIngresada, unidad.getUnidad());
                    mostrarAlerta("Se ingresaron " + formatearNumero(cantidadIngresada) + " " + unidad.getUnidad() + ".");
                } else {
                    double cantidadConvertida = cantidadIngresada / unidad.getFactorConversion();
                    int cantidadEntera = (int) cantidadConvertida;
                    double sobrante = cantidadIngresada - (cantidadEntera * unidad.getFactorConversion());

                    inventarioService.agregarStock(producto, cantidadEntera, unidadMinima.getUnidad());

                    // Muestra resultado, incluyendo sobrante si existe
                    if (sobrante > 0) {
                        mostrarAlerta("Se ingresaron " + cantidadEntera + " " + unidadMinima.getUnidad() +
                                " porque el valor ingresado (" + formatearNumero(cantidadIngresada) + " " + unidad.getUnidad() +
                                ") no es múltiplo del factor de conversión (" + formatearNumero(unidad.getFactorConversion()) + ").\n" +
                                "Sobrante no registrado: " + formatearNumero(sobrante) + " " + unidad.getUnidad() + ".");
                    } else {
                        mostrarAlerta("Se ingresaron " + cantidadEntera + " " + unidadMinima.getUnidad() + ".");
                    }
                }

                campoCantidad.clear(); // Limpia campo tras ingreso

            } catch (NumberFormatException e) {
                mostrarAlerta("La cantidad debe ser numérica.");
            }
        });

        // Cambia a pantalla de selección al hacer clic en Volver
        btnVolver.setOnAction(e -> cambiarPantalla("/vistas/PantallaSeleccionIngreso.fxml"));
    }

    // Cambia a la pantalla especificada por la ruta
    private void cambiarPantalla(String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Muestra una alerta con el mensaje proporcionado
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Formatea número como entero o con dos decimales
    private String formatearNumero(double valor) {
        return (valor % 1 == 0) ? String.format("%.0f", valor) : String.format("%.2f", valor);
    }
}