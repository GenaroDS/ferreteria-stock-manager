package app.servicios;

import app.modelo.AppData;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;
import app.modelo.Venta;
import app.servicios.InventarioService;

import java.time.LocalDateTime;

public class VentaService {

    // Registra una venta con fecha y hora actual
    public Venta registrarVenta(Producto producto, String unidadOriginal, double cantidadVendida) {
        Venta venta = new Venta(
                generarId(),
                producto,
                unidadOriginal,
                cantidadVendida,
                LocalDateTime.now()
        );
        AppData.getVentas().add(venta);
        return venta;
    }

    // Genera un ID incremental para la venta
    private int generarId() {
        return AppData.getVentas().size() + 1;
    }
}
