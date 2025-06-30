package app.servicios;

import app.dao.InventarioDAO;
import app.dao.ProductoDAO;
import app.dao.UnidadDAO;
import app.modelo.Inventario;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;

import java.util.List;

public class ProductoService {

    // Método para crear un producto con unidad mínima, conversión opcional y paquete opcional
    // Crea un producto con unidad mínima, conversión opcional y paquete opcional
    public void crearProducto(String nombre, int stockUnidadMinima, String unidadMinima, String unidadConv,
                              double factorConversion, boolean esPaquete, boolean tieneConversion,
                              String nombrePaquete, int cantidadMinima, double factorPaquete) {
        try {
            // Instanciar DAOs
            ProductoDAO productoDAO = new ProductoDAO();
            UnidadDAO unidadDAO = new UnidadDAO();
            InventarioDAO inventarioDAO = new InventarioDAO();

            // Crear e insertar producto
            Producto nuevo = new Producto(0, nombre, stockUnidadMinima);
            int idProducto = productoDAO.insertarProducto(nuevo);
            nuevo.setId(idProducto);

            // Insertar unidad mínima
            UnidadDeConversion unidadMin = new UnidadDeConversion(0, idProducto, unidadMinima, 1, false, true);
            unidadDAO.insertarUnidad(unidadMin);
            nuevo.agregarUnidadDeConversion(unidadMin);

            int unidadId = 1;

            // Insertar unidad de conversión si aplica
            if (tieneConversion) {
                UnidadDeConversion unidadConvAlt = new UnidadDeConversion(unidadId++, idProducto, unidadConv,
                        factorConversion, false, false);
                unidadDAO.insertarUnidad(unidadConvAlt);
                nuevo.agregarUnidadDeConversion(unidadConvAlt);
            }

            // Insertar unidad de paquete si aplica
            if (esPaquete) {
                UnidadDeConversion unidadPaquete = new UnidadDeConversion(unidadId, idProducto, nombrePaquete,
                        factorPaquete, true, false);
                unidadDAO.insertarUnidad(unidadPaquete);
                nuevo.agregarUnidadDeConversion(unidadPaquete);
            }

            // Insertar inventario
            Inventario inventario = new Inventario(0, nuevo, unidadMinima, cantidadMinima);
            inventarioDAO.insertar(inventario);

        } catch (Exception e) {
            // Imprimir error
            e.printStackTrace();
        }
    }



    // Devuelve el stock total de un producto en unidades mínimas
    public int consultarStockTotal(Producto producto) {
        try {
            InventarioDAO inventarioDAO = new InventarioDAO();
            List<Inventario> lista = inventarioDAO.obtenerTodos();

            int total = 0;
            for (Inventario inv : lista) {
                if (inv.getProducto().getId() == producto.getId()) {
                    UnidadDeConversion u = producto.getUnidad(inv.getUnidad());
                    if (u != null) {
                        total += inv.getCantidad() * u.getFactorConversion();
                    }
                }
            }
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Genera resumen de paquete para pantalla de bolsa
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

    // Consulta stock disponible en unidad mínima
    public double consultarStockDisponibleUnidad(Producto producto) {
        return new InventarioService().consultarStock(producto, producto.getUnidadMinima().getUnidad());
    }

    // Genera un informe de stock para la pantalla de consulta
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

        // Mostrar stock en unidades convertidas (no paquetes)
        for (UnidadDeConversion unidad : producto.getUnidadesAlternativas()) {
            if (!unidad.isUnidadMinima() && !unidad.isPaquete()) {
                double totalConvertido = stockTotalMinima * unidad.getFactorConversion();
                resultado.append("Total en ").append(unidad.getUnidad()).append(": ")
                        .append(formatearNumero(totalConvertido)).append("\n");
            }
        }


        // Mostrar detalle de paquetes (si los hay)
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

    // Formatea el número (entero si es exacto, 2 decimales si no)
    private String formatearNumero(double numero) {
        return (numero == Math.floor(numero)) ? String.valueOf((long) numero) : String.format("%.2f", numero);
    }
}
