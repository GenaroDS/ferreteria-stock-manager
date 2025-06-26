package app.controladores;

import app.modelo.AppData;
import app.modelo.Producto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModificarAlertasController {

    // ComboBox para seleccionar un producto
    @FXML private ComboBox<Producto> comboProductos;

    // Campo de texto para mostrar y editar el stock mínimo
    @FXML private TextField txtStockMinimo;

    // Botón para guardar los cambios
    @FXML private Button btnGuardar;

    // Botón para volver a la pantalla anterior
    @FXML private Button btnVolver;

    @FXML
    private void initialize() {
        // Si hay productos cargados, los ordena alfabéticamente y los carga en el combo
        if (!AppData.getProductos().isEmpty()) {
            List<Producto> productosOrdenados = new ArrayList<>(AppData.getProductos());
            productosOrdenados.sort(Comparator.comparing(Producto::getNombre));
            comboProductos.getItems().addAll(productosOrdenados);
        } else {
            // Si no hay productos, se deshabilita el combo y el botón de guardar
            comboProductos.setPromptText("Sin productos cargados");
            comboProductos.setDisable(true);
            btnGuardar.setDisable(true);
        }

        // Muestra el nombre del producto seleccionado en la parte cerrada del combo
        comboProductos.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Seleccionar producto" : item.getNombre());
            }
        });

        // Muestra el nombre de los productos en la lista desplegable
        comboProductos.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });

        // Al seleccionar un producto, se carga su stock mínimo en el campo de texto
        comboProductos.setOnAction(event -> {
            Producto seleccionado = comboProductos.getValue();
            if (seleccionado != null) {
                txtStockMinimo.setText(String.valueOf((int) seleccionado.getStockMinimoUnidadMinima()));
            }
        });

        // Al hacer clic en "Guardar", actualiza el stock mínimo del producto seleccionado
        btnGuardar.setOnAction(event -> {
            Producto seleccionado = comboProductos.getValue();
            if (seleccionado == null) return;

            try {
                // Intenta parsear el nuevo valor ingresado
                double nuevoStock = Double.parseDouble(txtStockMinimo.getText());
                // Actualiza el valor en el producto
                seleccionado.setStockMinimoUnidadMinima(nuevoStock);
                // Muestra mensaje de éxito
                mostrarAlerta("Éxito", "Stock mínimo actualizado.");
            } catch (NumberFormatException e) {
                // Si el texto ingresado no es numérico, muestra error
                mostrarAlerta("Error", "Ingresá un número válido.");
            }
        });

        // Vuelve a la pantalla del menú principal
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

    // Método auxiliar para mostrar una alerta modal
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
