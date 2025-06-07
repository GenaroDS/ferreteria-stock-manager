package app.modelo;

import java.util.ArrayList;
import java.util.List;

public class Producto {
    private int id;
    private String nombre;
    private List<UnidadDeConversion> unidadesAlternativas;

    public Producto(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    public UnidadDeConversion getUnidadMinima() {
        for (UnidadDeConversion u : unidadesAlternativas) {
            if (u.isUnidadMinima()) return u;
        }
        return null;
    }

    public void mostrarInfo() {
        System.out.println("Producto: " + nombre);
        for (UnidadDeConversion u : unidadesAlternativas) {
            String extra = u.isUnidadMinima() ? " (unidad mínima)" : "";
            System.out.println(" - " + u.getUnidad() + ": factor " + u.getFactorConversion() + " | Es paquete: " + u.isPaquete() + extra);
        }
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public List<UnidadDeConversion> getUnidadesAlternativas() {
        return unidadesAlternativas;
    }
}
