package app.modelo;

import java.time.LocalDateTime;

public class Venta {
    private int id;
    private Producto producto;
    private String unidad;
    private double cantidad;
    private LocalDateTime fechaHora;

    // Constructor con todos los campos
    public Venta(int id, Producto producto, String unidad, double cantidad, LocalDateTime fechaHora) {
        this.id = id;
        this.producto = producto;
        this.unidad = unidad;
        this.cantidad = cantidad;
        this.fechaHora = fechaHora;
    }

    // Devuelve la cantidad convertida a unidades mínimas
    public double getCantidadEnUnidadMinima() {
        UnidadDeConversion u = producto.getUnidad(unidad);
        return cantidad / u.getFactorConversion();
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getUnidad() {
        return unidad;
    }

    public double getCantidad() {
        return cantidad;
    }

    public Producto getProducto() {
        return producto;
    }
}
