package app.modelo;

public class Inventario {
    private int id;
    private Producto producto;
    private String unidad;
    private int cantidad;

    public Inventario(int id, Producto producto, String unidad, int cantidad) {
        this.id = id;
        this.producto = producto;
        this.unidad = unidad;
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }




    public Producto getProducto() {
        return producto;
    }

    public String getUnidad() {
        return unidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void modificarCantidad(int delta) {
        this.cantidad += delta;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
