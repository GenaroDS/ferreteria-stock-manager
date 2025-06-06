package app.modelo;

import java.util.ArrayList;
import java.util.List;

public class Producto {
    private int id;
    private String nombre;
    private String unidadMinima;
    private List<UnidadDeConversion> unidadesAlternativas;

    public Producto(int id, String nombre, String unidadMinima) {
        this.id = id;
        this.nombre = nombre;
        this.unidadMinima = unidadMinima;
        this.unidadesAlternativas = new ArrayList<>();
    }

    public void agregarUnidadDeConversion(UnidadDeConversion unidad) {
        unidadesAlternativas.add(unidad);
    }

    public UnidadDeConversion getUnidad(String nombreUnidad) {
        for (UnidadDeConversion u : unidadesAlternativas) {
            if (u.getUnidad().equals(nombreUnidad)) return u;
        }
        return null;
    }

    public void mostrarInfo() {
        System.out.println("Producto: " + nombre + " | Unidad mínima: " + unidadMinima);
        for (UnidadDeConversion u : unidadesAlternativas) {
            System.out.println(" - " + u.getUnidad() + ": factor " + u.getFactorConversion() + " | Es paquete: " + u.isPaquete());
        }
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUnidadMinima() {
        return unidadMinima;
    }

    public List<UnidadDeConversion> getUnidadesAlternativas() {
        return unidadesAlternativas;
    }
}
