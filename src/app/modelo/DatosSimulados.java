package app.modelo;

import java.util.ArrayList;
import java.util.List;

public class DatosSimulados {

    private List<Producto> productos = new ArrayList<>();
    private List<Inventario> inventario = new ArrayList<>();

    public DatosSimulados() {
        Producto clavos = new Producto(1, "Clavos", "Clavos");
        UnidadDeConversion gramos = new UnidadDeConversion(1, 1, "gramos", 40, true);
        clavos.agregarUnidadDeConversion(gramos);
        productos.add(clavos);
        inventario.add(new Inventario(1, clavos, "Clavos", 2500));
        inventario.add(new Inventario(2, clavos, "gramos", 0)); // al inicio no hay armados, pero se puede calcular
    }


    public List<Producto> getProductos() {
        return productos;
    }
    public int consultarStockDisponibleUnidad(Producto producto) {
        return consultarStock(producto, producto.getUnidadMinima());
    }
    public boolean armarPaquete(Producto producto, String unidad, int cantidadPaquetes) {
        UnidadDeConversion u = producto.getUnidad(unidad);
        if (u == null || !u.isPaquete()) return false;

        int cantidadNecesaria = u.getFactorConversion() * cantidadPaquetes;
        if (!descontarStock(producto, cantidadNecesaria, producto.getUnidadMinima())) return false;

        agregarStock(producto, cantidadPaquetes, unidad);
        return true;
    }

    public boolean desarmarPaquete(Producto producto, String unidad, int cantidadPaquetes) {
        UnidadDeConversion u = producto.getUnidad(unidad);
        if (u == null || !u.isPaquete()) return false;

        if (!descontarStock(producto, cantidadPaquetes, unidad)) return false;

        agregarStock(producto, cantidadPaquetes * u.getFactorConversion(), producto.getUnidadMinima());
        return true;
    }

    public List<Inventario> getInventario() {
        return inventario;
    }

    public void agregarStock(Producto producto, float cantidad, String unidad) {
        Inventario inv = buscarInventario(producto, unidad);
        if (inv != null) {
            inv.modificarCantidad((int) cantidad);
        } else {
            int nuevoId = inventario.size() + 1;
            inventario.add(new Inventario(nuevoId, producto, unidad, (int) cantidad));
        }
    }

    public boolean descontarStock(Producto producto, float cantidad, String unidad) {
        Inventario inv = buscarInventario(producto, unidad);
        if (inv != null && inv.getCantidad() >= cantidad) {
            inv.modificarCantidad((int) -cantidad);
            return true;
        }
        return false;
    }

    public int consultarStock(Producto producto, String unidad) {
        Inventario inv = buscarInventario(producto, unidad);
        return inv != null ? inv.getCantidad() : 0;
    }

    public int consultarStockTotal(Producto producto) {
        int total = 0;
        for (Inventario inv : inventario) {
            if (inv.getProducto().getId() == producto.getId()) {
                if (inv.getUnidad().equals(producto.getUnidadMinima())) {
                    total += inv.getCantidad();
                } else {
                    UnidadDeConversion u = producto.getUnidad(inv.getUnidad());
                    if (u != null) {
                        total += inv.getCantidad() * u.getFactorConversion();
                    }
                }
            }
        }
        return total;
    }



    private Inventario buscarInventario(Producto producto, String unidad) {
        for (Inventario inv : inventario) {
            if (inv.getProducto().getId() == producto.getId() && inv.getUnidad().equals(unidad)) {
                return inv;
            }
        }
        return null;
    }
}
