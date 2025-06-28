package app.servicios;

import app.dao.VentaDAO;
import app.modelo.Producto;
import app.modelo.Venta;

import java.time.LocalDateTime;

public class VentaService {

    // Registra una venta con fecha y hora actual
    public Venta registrarVenta(Producto producto, String unidadOriginal, double cantidadVendida) {
        Venta venta = new Venta(
                0, // el ID real lo asigna la base de datos
                producto,
                unidadOriginal,
                cantidadVendida,
                LocalDateTime.now()
        );

        try {
            new VentaDAO().insertarVenta(venta);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return venta;
    }
}
