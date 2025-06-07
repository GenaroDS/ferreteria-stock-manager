package app.servicios;

import app.modelo.AppData;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;
import app.modelo.Inventario;

public class ProductoService {

    public void crearProducto(String nombre, String unidadMinima, String unidadConv, double factor, boolean esPaquete, int cantidadMinima) {
        int id = AppData.getProductos().size() + 1;
        Producto nuevo = new Producto(id, nombre);

        UnidadDeConversion unidadMin = new UnidadDeConversion(0, id, unidadMinima, 1, false, true);
        UnidadDeConversion unidad = new UnidadDeConversion(1, id, unidadConv, factor, esPaquete, false);

        nuevo.agregarUnidadDeConversion(unidadMin);
        nuevo.agregarUnidadDeConversion(unidad);

        AppData.getProductos().add(nuevo);

        int idInv = AppData.getInventario().size() + 1;
        AppData.getInventario().add(new Inventario(idInv, nuevo, unidadMinima, cantidadMinima));
    }

    public int consultarStockTotal(Producto producto) {
        int total = 0;
        for (Inventario inv : AppData.getInventario()) {
            if (inv.getProducto().getId() == producto.getId()) {
                UnidadDeConversion u = producto.getUnidad(inv.getUnidad());
                if (u != null) {
                    total += inv.getCantidad() * u.getFactorConversion();
                }
            }
        }
        return total;
    }


    public String generarResumenBolsa(Producto producto) {
        InventarioService inventarioService = new InventarioService();
        StringBuilder sb = new StringBuilder();

        sb.append("Producto: ").append(producto.getNombre()).append("\n");
        sb.append("Unidad mínima: ").append(producto.getUnidadMinima().getUnidad()).append("\n");

        double stockUnidad = inventarioService.consultarStock(producto, producto.getUnidadMinima().getUnidad());
        int stockEntero = (int) stockUnidad;
        sb.append("Unidades disponibles: ").append(stockEntero).append("\n");

        for (UnidadDeConversion unidad : producto.getUnidadesAlternativas()) {
            if (unidad.isPaquete()) {
                int armables = (int)(stockUnidad / unidad.getFactorConversion());
                int armadas = (int) inventarioService.consultarStock(producto, unidad.getUnidad());
                sb.append("Bolsas disponibles para armar: ").append(armables).append("\n");
                sb.append("Bolsas armadas: ").append(armadas).append("\n");
            }
        }

        return sb.toString();
    }

    public double consultarStockDisponibleUnidad(Producto producto) {
        return new InventarioService().consultarStock(producto, producto.getUnidadMinima().getUnidad());
    }

    public String generarInformeStock(Producto producto) {
        InventarioService inventarioService = new InventarioService();
        StringBuilder resultado = new StringBuilder();

        UnidadDeConversion unidadMinima = producto.getUnidadMinima();
        if (unidadMinima == null) return "Error: el producto no tiene unidad mínima definida.";

        String nombreUnidadMinima = unidadMinima.getUnidad();
        double stockTotalMinima = consultarStockTotal(producto);
        double stockSueltos = consultarStockDisponibleUnidad(producto);

        resultado.append("Producto: ").append(producto.getNombre()).append("\n");
        resultado.append("Unidad mínima: ").append(nombreUnidadMinima).append("\n");
        resultado.append("Stock total (en ").append(nombreUnidadMinima).append("): ")
                .append(formatearNumero(stockTotalMinima)).append("\n");

        for (UnidadDeConversion unidad : producto.getUnidadesAlternativas()) {
            if (!unidad.isUnidadMinima() && !unidad.isPaquete()) {
                double totalConvertido = stockTotalMinima * unidad.getFactorConversion();
                resultado.append("Total en ").append(unidad.getUnidad()).append(": ")
                        .append(formatearNumero(totalConvertido)).append("\n");
            }
        }

        for (UnidadDeConversion unidad : producto.getUnidadesAlternativas()) {
            if (unidad.isPaquete()) {
                String nombreUnidad = unidad.getUnidad();
                double factor = unidad.getFactorConversion();
                double paquetes = inventarioService.consultarStock(producto, nombreUnidad);
                double totalEnMinima = paquetes * factor;

                resultado.append("- En unidades: ").append(formatearNumero(stockSueltos)).append("\n");
                resultado.append("- En paquetes: ").append(formatearNumero(paquetes))
                        .append(" (").append(formatearNumero(totalEnMinima)).append(" ")
                        .append(nombreUnidadMinima).append(")\n");
            }
        }

        return resultado.toString();
    }




    private String formatearNumero(double numero) {
        return (numero == Math.floor(numero)) ? String.valueOf((long) numero) : String.format("%.2f", numero);
    }
}
