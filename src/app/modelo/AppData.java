package app.modelo;

public class AppData {

    private static final DatosSimulados instanciaDatos = new DatosSimulados();

    public static DatosSimulados getDatos() {
        return instanciaDatos;
    }
}
