package app.servicios;

import app.dao.InventarioDAO;
import app.modelo.Inventario;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;

public class InventarioService {

    // Agrega stock a un producto en la unidad especificada
    public void agregarStock(Producto producto, double cantidad, String unidad) {
        try {
            InventarioDAO dao = new InventarioDAO();
            Inventario existente = dao.buscarPorProductoYUnidad(producto.getId(), unidad);
            if (existente != null) {
                // Actualiza cantidad si ya existe
                existente.modificarCantidad(cantidad);
                dao.actualizarCantidad(existente);
            } else {
                // Crea nuevo registro de inventario si no existe
                Inventario nuevo = new Inventario(0, producto, unidad, cantidad);
                dao.insertar(nuevo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Descuenta stock de un producto en la unidad especificada
    public boolean descontarStock(Producto producto, double cantidad, String unidad) {
        try {
            InventarioDAO dao = new InventarioDAO();
            Inventario inv = dao.buscarPorProductoYUnidad(producto.getId(), unidad);
            // Verifica si hay suficiente stock y descuenta
            if (inv != null && inv.getCantidad() >= cantidad) {
                inv.modificarCantidad(-cantidad);
                dao.actualizarCantidad(inv);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Consulta el stock disponible de un producto en la unidad especificada
    public double consultarStock(Producto producto, String unidad) {
        try {
            InventarioDAO dao = new InventarioDAO();
            Inventario inv = dao.buscarPorProductoYUnidad(producto.getId(), unidad);
            return inv != null ? inv.getCantidad() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Crea paquetes a partir de unidades mínimas
    public boolean armarPaquete(Producto producto, String unidad, int cantidadPaquetes) {
        UnidadDeConversion u = producto.getUnidad(unidad);
        // Verifica si la unidad es un paquete válido
        if (u == null || !u.isPaquete()) return false;
        // Calcula cantidad necesaria en unidad mínima
        double necesario = u.getFactorConversion() * cantidadPaquetes;
        String unidadMin = producto.getUnidadMinima().getUnidad();
        // Descuenta unidad mínima y agrega paquetes
        if (!descontarStock(producto, necesario, unidadMin)) return false;
        agregarStock(producto, cantidadPaquetes, unidad);
        return true;
    }

    // Desarma paquetes en unidades mínimas
    public boolean desarmarPaquete(Producto producto, String unidad, int cantidadPaquetes) {
        UnidadDeConversion u = producto.getUnidad(unidad);
        // Verifica si la unidad es un paquete válido
        if (u == null || !u.isPaquete()) return false;
        // Descuenta paquetes y agrega unidades mínimas
        if (!descontarStock(producto, cantidadPaquetes, unidad)) return false;
        double cantidadResultado = cantidadPaquetes * u.getFactorConversion();
        String unidadMin = producto.getUnidadMinima().getUnidad();
        agregarStock(producto, cantidadResultado, unidadMin);
        return true;
    }
}