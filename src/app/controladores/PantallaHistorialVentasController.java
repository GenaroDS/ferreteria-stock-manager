package app.controladores;

import app.modelo.AppData;
import app.modelo.Venta;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;

import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public class PantallaHistorialVentasController {

    @FXML private Button btnVolver;
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, String> colProducto;
    @FXML private TableColumn<Venta, String> colUnidad;
    @FXML private TableColumn<Venta, Double> colCantidad;
    @FXML private TableColumn<Venta, Double> colCantidadMinima;

    @FXML
    private void initialize() {
        // Volver al menú principal
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Configurar columna de fecha con formato personalizado
        colFecha.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }));
        colFecha.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getFechaHora().format(formatter))
        );

        // Mostrar nombre del producto en la tabla
        colProducto.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getProducto().getNombre())
        );

        // Mostrar unidad utilizada en la venta
        colUnidad.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getUnidad())
        );

        // Mostrar cantidad vendida con formato
        colCantidad.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createObjectBinding(cellData.getValue()::getCantidad)
        );
        colCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Double value) {
                return value % 1 == 0 ? String.valueOf(value.intValue()) : String.valueOf(value);
            }

            @Override
            public Double fromString(String string) {
                return Double.valueOf(string);
            }
        }));

        // Cargar las ventas en la tabla
        tablaVentas.setItems(FXCollections.observableArrayList(AppData.getVentas()));
    }
}
