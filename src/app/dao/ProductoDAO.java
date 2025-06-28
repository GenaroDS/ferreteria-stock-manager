package app.dao;

import app.db.ConexionDB;
import app.modelo.Producto;
import app.modelo.UnidadDeConversion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    public List<Producto> obtenerTodos() throws Exception {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto";

        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("stock_minimo")
                );
                p.getUnidadesAlternativas().addAll(obtenerUnidades(p.getId()));
                lista.add(p);
            }
        }

        return lista;
    }

    public List<UnidadDeConversion> obtenerUnidades(int idProducto) throws Exception {
        List<UnidadDeConversion> unidades = new ArrayList<>();
        String sql = "SELECT * FROM unidad_de_conversion WHERE id_producto = ?";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                unidades.add(new UnidadDeConversion(
                        rs.getInt("id"),
                        idProducto,
                        rs.getString("unidad"),
                        rs.getDouble("factor_conversion"),
                        rs.getBoolean("es_paquete"),
                        rs.getBoolean("es_unidad_minima")
                ));
            }
        }

        return unidades;
    }
    public Producto obtenerPorId(int id) throws Exception {
        String sql = "SELECT * FROM producto WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Producto p = new Producto(id, rs.getString("nombre"), rs.getInt("stock_minimo"));
                p.getUnidadesAlternativas().addAll(obtenerUnidades(id));
                return p;
            }
        }
        throw new RuntimeException("Producto no encontrado con ID: " + id);
    }

    // Inserta un producto y devuelve el ID generado
    public int insertarProducto(Producto producto) throws Exception {
        String sql = "INSERT INTO producto (nombre, stock_minimo) VALUES (?, ?)";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, producto.getNombre());
            stmt.setDouble(2, producto.getStockMinimoUnidadMinima());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        throw new RuntimeException("No se pudo insertar el producto.");
    }
    public void actualizarStockMinimo(int idProducto, double nuevoStockMinimo) throws Exception {
        String sql = "UPDATE producto SET stock_minimo = ? WHERE id = ?";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, nuevoStockMinimo);
            stmt.setInt(2, idProducto);
            stmt.executeUpdate();
        }
    }


}
