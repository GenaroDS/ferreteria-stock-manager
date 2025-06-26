package app.controladores;

import app.modelo.AppData;
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

public class PantallaBolsaController {

    @FXML private ComboBox<Producto> comboTipo;
    @FXML private TextArea areaResumen;
    @FXML private Button btnArmar;
    @FXML private Button btnDesarmar;
    @FXML private Button btnVolver;

    @FXML
    private void initialize() {
        // Cargar productos que tienen unidad tipo paquete en el combo, ordenados por nombre
        AppData.getProductos().stream()
                .filter(p -> p.getUnidadesAlternativas().stream().anyMatch(UnidadDeConversion::isPaquete))
                .sorted(Comparator.comparing(Producto::getNombre))
                .forEach(comboTipo.getItems()::add);

        // Mostrar nombre del producto en la lista desplegable
        comboTipo.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });

        // Mostrar nombre del producto seleccionado (o texto por defecto)
        comboTipo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Seleccionar producto" : item.getNombre());
            }
        });

        // Al seleccionar un producto, actualizar el resumen
        comboTipo.setOnAction(event -> actualizarResumen());

        // Al hacer clic en "Armar", crear un paquete del producto seleccionado
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

        // Al hacer clic en "Desarmar", deshacer un paquete del producto seleccionado
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

        // Volver a la pantalla principal
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

    // Generar y mostrar el resumen del producto seleccionado
    private void actualizarResumen() {
        Producto p = comboTipo.getValue();
        if (p == null) {
            areaResumen.setText("");
            return;
        }
        ProductoService productoService = new ProductoService();
        areaResumen.setText(productoService.generarResumenBolsa(p));
    }
}
