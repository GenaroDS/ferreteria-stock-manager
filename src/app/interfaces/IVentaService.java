package app.interfaces;

import app.modelo.Producto;
import app.modelo.Venta;

public interface IVentaService {
    Venta registrarVenta(Producto producto, String unidadOriginal, double cantidadVendida);
}