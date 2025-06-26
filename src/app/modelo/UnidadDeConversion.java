package app.modelo;

public class UnidadDeConversion {
    private int id;
    private int idProducto;
    private String unidad;
    private double factorConversion;
    private boolean esPaquete;
    private boolean esUnidadMinima;

    // Constructor con todos los campos
    public UnidadDeConversion(int id, int idProducto, String unidad, double factorConversion, boolean esPaquete, boolean esUnidadMinima) {
        this.id = id;
        this.idProducto = idProducto;
        this.unidad = unidad;
        this.factorConversion = factorConversion;
        this.esPaquete = esPaquete;
        this.esUnidadMinima = esUnidadMinima;
    }

    public String getUnidad() {
        return unidad;
    }

    public double getFactorConversion() {
        return factorConversion;
    }

    // Indica si es una unidad tipo paquete
    public boolean isPaquete() {
        return esPaquete;
    }

    // Indica si es la unidad mínima del producto
    public boolean isUnidadMinima() {
        return esUnidadMinima;
    }

    public int getIdProducto() {
        return idProducto;
    }
}
