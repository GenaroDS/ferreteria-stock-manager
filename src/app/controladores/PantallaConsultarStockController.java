package app.controladores;

import app.dao.ProductoDAO;
import app.modelo.Producto;
import app.servicios.ProductoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.util.Comparator;
import java.util.List;

public class PantallaConsultarStockController {

    @FXML private ComboBox<Producto> comboProductos;
    @FXML private Button btnConsultar;
    @FXML private Button btnVolver;
    @FXML private TextArea areaResultado;

    @FXML
    private void initialize() {
        try {
            // Cargar productos desde la base de datos
            ProductoDAO dao = new ProductoDAO();
            List<Producto> productos = dao.obtenerTodos();

            if (!productos.isEmpty()) {
                productos.sort(Comparator.comparing(Producto::getNombre));
                comboProductos.getItems().addAll(productos);
            } else {
                comboProductos.setPromptText("Sin productos cargados");
                comboProductos.setDisable(true);
                btnConsultar.setDisable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            comboProductos.setPromptText("Error al cargar");
            comboProductos.setDisable(true);
            btnConsultar.setDisable(true);
        }

        // Mostrar nombre del producto seleccionado o texto por defecto
        comboProductos.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Seleccionar producto" : item.getNombre());
            }
        });

        // Mostrar nombre de los productos en la lista desplegable
        comboProductos.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });

        // Al hacer clic en "Consultar", mostrar informe de stock del producto seleccionado
        btnConsultar.setOnAction(event -> {
            Producto seleccionado = comboProductos.getValue();
            if (seleccionado != null) {
                ProductoService productoService = new ProductoService();
                String informe = productoService.generarInformeStock(seleccionado);
                String stockMinimo = "Stock mínimo de alerta: " + formatearNumero(seleccionado.getStockMinimoUnidadMinima()) + " " +
                        seleccionado.getUnidadMinima().getUnidad();
                areaResultado.setText(informe + stockMinimo);
            } else {
                areaResultado.setText("Seleccioná un producto.");
            }
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

    // Devuelve el número como entero si no tiene decimales, si no lo deja como está
    private String formatearNumero(double numero) {
        return numero % 1 == 0 ? String.valueOf((int) numero) : String.valueOf(numero);
    }
}
