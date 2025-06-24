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

        colProducto.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getProducto().getNombre())
        );

        colUnidad.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        cellData.getValue().getUnidad())
        );

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

        colCantidadMinima.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createObjectBinding(cellData.getValue()::getCantidadEnUnidadMinima)
        );
        colCantidadMinima.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Double value) {
                return value % 1 == 0 ? String.valueOf(value.intValue()) : String.valueOf(value);
            }

            @Override
            public Double fromString(String string) {
                return Double.valueOf(string);
            }
        }));




        tablaVentas.setItems(FXCollections.observableArrayList(AppData.getVentas()));
    }




}
