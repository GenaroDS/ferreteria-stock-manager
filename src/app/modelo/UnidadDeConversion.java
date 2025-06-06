package app.modelo;

public class UnidadDeConversion {
    private int id;
    private int idProducto;
    private String unidad;
    private int factorConversion;
    private boolean esPaquete;

    public UnidadDeConversion(int id, int idProducto, String unidad, int factorConversion, boolean esPaquete) {
        this.id = id;
        this.idProducto = idProducto;
        this.unidad = unidad;
        this.factorConversion = factorConversion;
        this.esPaquete = esPaquete;
    }

    public String getUnidad() {
        return unidad;
    }

    public int getFactorConversion() {
        return factorConversion;
    }

    public boolean isPaquete() {
        return esPaquete;
    }

    public int getIdProducto() {
        return idProducto;
    }
}
