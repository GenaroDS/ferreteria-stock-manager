package app.servicios;

import app.modelo.AppData;
import app.modelo.Producto;
import app.modelo.Inventario;
import app.modelo.UnidadDeConversion;

public class InventarioService {

    public void agregarStock(Producto producto, double cantidad, String unidad) {
        Inventario inv = buscarInventario(producto, unidad);
        if (inv != null) {
            inv.modificarCantidad(cantidad);
        } else {
            int nuevoId = AppData.getInventario().size() + 1;
            AppData.getInventario().add(new Inventario(nuevoId, producto, unidad, cantidad));
        }
    }

    public boolean descontarStock(Producto producto, double cantidad, String unidad) {
        Inventario inv = buscarInventario(producto, unidad);
        if (inv != null && inv.getCantidad() >= cantidad) {
            inv.modificarCantidad(-cantidad);
            return true;
        }
        return false;
    }

    public double consultarStock(Producto producto, String unidad) {
        Inventario inv = buscarInventario(producto, unidad);
        return inv != null ? inv.getCantidad() : 0;
    }

    public boolean armarPaquete(Producto producto, String unidad, int cantidadPaquetes) {
        UnidadDeConversion u = producto.getUnidad(unidad);
        if (u == null || !u.isPaquete()) return false;

        double cantidadNecesaria = u.getFactorConversion() * cantidadPaquetes;
        String unidadMin = producto.getUnidadMinima().getUnidad();

        if (!descontarStock(producto, cantidadNecesaria, unidadMin)) return false;

        agregarStock(producto, cantidadPaquetes, unidad);
        return true;
    }

    public boolean desarmarPaquete(Producto producto, String unidad, int cantidadPaquetes) {
        UnidadDeConversion u = producto.getUnidad(unidad);
        if (u == null || !u.isPaquete()) return false;

        if (!descontarStock(producto, cantidadPaquetes, unidad)) return false;

        double cantidadResultado = cantidadPaquetes * u.getFactorConversion();
        String unidadMin = producto.getUnidadMinima().getUnidad();

        agregarStock(producto, cantidadResultado, unidadMin);
        return true;
    }

    private Inventario buscarInventario(Producto producto, String unidad) {
        for (Inventario inv : AppData.getInventario()) {
            if (inv.getProducto().getId() == producto.getId() && inv.getUnidad().equals(unidad)) {
                return inv;
            }
        }
        return null;
    }
}
