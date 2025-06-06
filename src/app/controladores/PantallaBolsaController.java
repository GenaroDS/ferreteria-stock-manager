package app.controladores;

import app.modelo.AppData;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PantallaBolsaController {

    @FXML private ComboBox<Producto> comboTipo;
    @FXML private TextArea areaResumen;
    @FXML private Button btnArmar;
    @FXML private Button btnDesarmar;
    @FXML private Button btnVolver;

    @FXML
    private void initialize() {
        comboTipo.getItems().addAll(AppData.getDatos().getProductos());

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
            for (UnidadDeConversion unidad : p.getUnidadesAlternativas()) {
                if (unidad.isPaquete()) {
                    AppData.getDatos().armarPaquete(p, unidad.getUnidad(), 1);
                }
            }
            actualizarResumen();
        });

        btnDesarmar.setOnAction(event -> {
            Producto p = comboTipo.getValue();
            if (p == null) return;
            for (UnidadDeConversion unidad : p.getUnidadesAlternativas()) {
                if (unidad.isPaquete()) {
                    AppData.getDatos().desarmarPaquete(p, unidad.getUnidad(), 1);
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
        StringBuilder sb = new StringBuilder();
        sb.append("Producto: ").append(p.getNombre()).append("\n");
        sb.append("Unidad mínima: ").append(p.getUnidadMinima()).append("\n");

        int stockUnidad = AppData.getDatos().consultarStock(p, p.getUnidadMinima());
        sb.append("Unidades disponibles: ").append(stockUnidad).append("\n");

        for (UnidadDeConversion unidad : p.getUnidadesAlternativas()) {
            if (unidad.isPaquete()) {
                int armables = stockUnidad / unidad.getFactorConversion();
                int armadas = AppData.getDatos().consultarStock(p, unidad.getUnidad());
                sb.append("Bolsas disponibles para armar: ").append(armables).append("\n");
                sb.append("Bolsas armadas: ").append(armadas).append("\n");
            }
        }

        areaResumen.setText(sb.toString());
    }
}
