package app.modelo;

import java.util.ArrayList;
import java.util.List;

public class AppData {

    private static final List<Producto> productos = new ArrayList<>();
    private static final List<Inventario> inventario = new ArrayList<>();

    public static List<Producto> getProductos() {
        return productos;
    }

    public static List<Inventario> getInventario() {
        return inventario;
    }

    public static void inicializarDatos() {
        Producto clavos = new Producto(1, "Clavos");

        UnidadDeConversion unidadMinima = new UnidadDeConversion(0, 1, "Clavos", 1, false, true);
        UnidadDeConversion gramos = new UnidadDeConversion(1, 1, "gramos", 40, false, false);
        UnidadDeConversion bolsa = new UnidadDeConversion(2, 1, "bolsa", 100, true, false);

        clavos.agregarUnidadDeConversion(unidadMinima);
        clavos.agregarUnidadDeConversion(gramos);
        clavos.agregarUnidadDeConversion(bolsa);

        productos.add(clavos);

        inventario.add(new Inventario(1, clavos, "Clavos", 2500));
    }
}
