package app.controladores;

import app.dao.ProductoDAO;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;
import app.servicios.InventarioService;
import app.servicios.ProductoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

public class PantallaBolsaController {

    @FXML private ComboBox<Producto> comboTipo;
    @FXML private TextArea areaResumen;
    @FXML private Button btnArmar;
    @FXML private Button btnDesarmar;
    @FXML private Button btnVolver;

    @FXML
    private void initialize() {
        try {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = dao.obtenerTodos();

            productos.stream()
                    .filter(p -> p.getUnidadesAlternativas().stream().anyMatch(UnidadDeConversion::isPaquete))
                    .sorted(Comparator.comparing(Producto::getNombre))
                    .forEach(comboTipo.getItems()::add);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar productos con paquetes.");
        }

        comboTipo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });

        comboTipo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Seleccionar producto" : item.getNombre());
            }
        });

        comboTipo.setOnAction(event -> actualizarResumen());

        btnArmar.setOnAction(event -> {
            Producto p = comboTipo.getValue();
            if (p == null) return;

            InventarioService inventarioService = new InventarioService();
            for (UnidadDeConversion unidad : p.getUnidadesAlternativas()) {
                if (unidad.isPaquete()) {
                    inventarioService.armarPaquete(p, unidad.getUnidad(), 1);
                }
            }
            actualizarResumen();
        });

        btnDesarmar.setOnAction(event -> {
            Producto p = comboTipo.getValue();
            if (p == null) return;

            InventarioService inventarioService = new InventarioService();
            for (UnidadDeConversion unidad : p.getUnidadesAlternativas()) {
                if (unidad.isPaquete()) {
                    inventarioService.desarmarPaquete(p, unidad.getUnidad(), 1);
                }
            }
            actualizarResumen();
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

    private void actualizarResumen() {
        Producto p = comboTipo.getValue();
        if (p == null) {
            areaResumen.setText("");
            return;
        }
        ProductoService productoService = new ProductoService();
        areaResumen.setText(productoService.generarResumenBolsa(p));
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
