package app.modelo;

public class Inventario {
    private int id;
    private Producto producto;
    private String unidad;
    private double cantidad;

    public Inventario(int id, Producto producto, String unidad, double cantidad) {
        this.id = id;
        this.producto = producto;
        this.unidad = unidad;
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }

    public double getCantidadEnUnidadMinima() {
        UnidadDeConversion unidadActual = producto.getUnidad(unidad);
        if (unidadActual == null) {
            throw new IllegalStateException("Unidad no registrada");
        }
        return cantidad * unidadActual.getFactorConversion();
    }



    public Producto getProducto() {
        return producto;
    }

    public String getUnidad() {
        return unidad;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void modificarCantidad(double delta) {
        this.cantidad += delta;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
}
