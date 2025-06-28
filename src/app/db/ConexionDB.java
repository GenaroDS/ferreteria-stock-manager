package app.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/ferreteria_stock_manager";
    private static final String USUARIO = "root";
    private static final String CLAVE = "admin";

    public static Connection obtenerConexion() throws Exception {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
