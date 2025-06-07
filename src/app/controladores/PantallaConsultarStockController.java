package app.controladores;

import app.modelo.AppData;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;
import app.modelo.Inventario;
import app.servicios.ProductoService;
import app.servicios.InventarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class PantallaConsultarStockController {

    @FXML private ComboBox<Producto> comboProductos;
    @FXML private Button btnConsultar;
    @FXML private Button btnVolver;
    @FXML private TextArea areaResultado;

    @FXML
    private void initialize() {
        if (!AppData.getProductos().isEmpty()) {
            comboProductos.getItems().addAll(AppData.getProductos());
        } else {
            comboProductos.setPromptText("Sin productos cargados");
            comboProductos.setDisable(true);
            btnConsultar.setDisable(true);
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

        btnConsultar.setOnAction(event -> {
            Producto seleccionado = comboProductos.getValue();
            if (seleccionado != null) {
                ProductoService productoService = new ProductoService();
                String informe = productoService.generarInformeStock(seleccionado);
                areaResultado.setText(informe);
            } else {
                areaResultado.setText("Seleccioná un producto.");
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
    private String formatearNumero(double numero) {
        return numero % 1 == 0 ? String.valueOf((int) numero) : String.valueOf(numero);
    }
}
