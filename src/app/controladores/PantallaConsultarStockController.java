package app.controladores;

import app.modelo.AppData;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;
import app.modelo.Inventario;
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
        if (!AppData.getDatos().getProductos().isEmpty()) {
            comboProductos.getItems().addAll(AppData.getDatos().getProductos());
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
                StringBuilder resultado = new StringBuilder();
                resultado.append("Producto: ").append(seleccionado.getNombre()).append("\n");
                resultado.append("Unidad mínima: ").append(seleccionado.getUnidadMinima()).append("\n");

                int total = AppData.getDatos().consultarStockTotal(seleccionado);
                resultado.append("Stock total (en ").append(seleccionado.getUnidadMinima()).append("): ").append(total).append("\n");

                for (UnidadDeConversion u : seleccionado.getUnidadesAlternativas()) {
                    int cantidadInventario = AppData.getDatos().consultarStock(seleccionado, u.getUnidad());
                    int cantidadConvertida = total * u.getFactorConversion();  // cuántas unidades alternativas se pueden formar
                    int paquetes = AppData.getDatos().consultarStock(seleccionado, u.getUnidad());
                    int disponibleUnidad = AppData.getDatos().consultarStockDisponibleUnidad(seleccionado);


                    resultado.append("Disponibles en unidades: ").append(disponibleUnidad).append("\n");

                    int totalConvertido = paquetes * u.getFactorConversion();
                    resultado.append("Total de ").append(u.getUnidad()).append(" en ").append(seleccionado.getNombre()).append(": ");
                    resultado.append(cantidadConvertida).append(" (");
                    resultado.append(u.getFactorConversion()).append(" ").append(u.getUnidad()).append(" c/u)\n");
                    resultado.append("- Sueltos: ").append(disponibleUnidad).append(" ").append(seleccionado.getUnidadMinima()).append("\n");
                    resultado.append("- Empaquetados: ")
                            .append(paquetes).append(" (").append(totalConvertido).append(") ")
                            .append(seleccionado.getUnidadMinima()).append("\n");
                }
                areaResultado.setText(resultado.toString());
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
}
