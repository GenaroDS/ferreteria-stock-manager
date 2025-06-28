package app.controladores;

import app.dao.ProductoDAO;
import app.modelo.Producto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

public class ModificarAlertasController {

    @FXML private ComboBox<Producto> comboProductos;
    @FXML private TextField txtStockMinimo;
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    @FXML
    private void initialize() {
        try {
            // Obtener productos desde la base de datos
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = dao.obtenerTodos();

            if (!productos.isEmpty()) {
                productos.sort(Comparator.comparing(Producto::getNombre));
                comboProductos.getItems().addAll(productos);
            } else {
                comboProductos.setPromptText("Sin productos cargados");
                comboProductos.setDisable(true);
                btnGuardar.setDisable(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            comboProductos.setPromptText("Error al cargar productos");
            comboProductos.setDisable(true);
            btnGuardar.setDisable(true);
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
            Producto seleccionado = comboProductos.getValue();
            if (seleccionado != null) {
                txtStockMinimo.setText(String.valueOf((int) seleccionado.getStockMinimoUnidadMinima()));
            }
        });

        btnGuardar.setOnAction(event -> {
            Producto seleccionado = comboProductos.getValue();
            if (seleccionado == null) return;

            try {
                double nuevoStock = Double.parseDouble(txtStockMinimo.getText());
                seleccionado.setStockMinimoUnidadMinima(nuevoStock);

                // Actualizar stock mínimo en base de datos
                ProductoDAO dao = new ProductoDAO();
                dao.actualizarStockMinimo(seleccionado.getId(), nuevoStock);

                mostrarAlerta("Éxito", "Stock mínimo actualizado.");
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Ingresá un número válido.");
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "Falló la actualización en la base de datos.");
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
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
