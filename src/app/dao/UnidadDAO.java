package app.dao;

import app.db.ConexionDB;
import app.modelo.UnidadDeConversion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnidadDAO {

    // Inserta una unidad de conversión asociada a un producto
    public void insertarUnidad(UnidadDeConversion unidad) throws Exception {
        String sql = "INSERT INTO unidad_de_conversion (id_producto, unidad, factor_conversion, es_paquete, es_unidad_minima) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, unidad.getIdProducto());
            stmt.setString(2, unidad.getUnidad());
            stmt.setDouble(3, unidad.getFactorConversion());
            stmt.setBoolean(4, unidad.isPaquete());
            stmt.setBoolean(5, unidad.isUnidadMinima());

            stmt.executeUpdate();
        }
    }

    // Devuelve todas las unidades asociadas a un producto
    public List<UnidadDeConversion> obtenerPorProducto(int idProducto) throws Exception {
        List<UnidadDeConversion> lista = new ArrayList<>();
        String sql = "SELECT * FROM unidad_de_conversion WHERE id_producto = ?";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UnidadDeConversion unidad = new UnidadDeConversion(
                        rs.getInt("id"),
                        idProducto,
                        rs.getString("unidad"),
                        rs.getDouble("factor_conversion"),
                        rs.getBoolean("es_paquete"),
                        rs.getBoolean("es_unidad_minima")
                );
                lista.add(unidad);
            }
        }

        return lista;
    }
}
