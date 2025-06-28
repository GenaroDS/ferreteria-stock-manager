package app.dao;

import app.db.ConexionDB;
import app.modelo.Inventario;
import app.modelo.Producto;
import app.dao.ProductoDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    public List<Inventario> obtenerTodos() throws Exception {
        List<Inventario> lista = new ArrayList<>();
        ProductoDAO productoDAO = new ProductoDAO();

        String sql = "SELECT * FROM inventario";
        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int idProd = rs.getInt("id_producto");
                Producto producto = productoDAO.obtenerPorId(idProd);
                lista.add(new Inventario(
                        rs.getInt("id"),
                        producto,
                        rs.getString("unidad"),
                        rs.getDouble("cantidad")
                ));
            }
        }

        return lista;
    }

    public void actualizarCantidad(Inventario inventario) throws Exception {
        String sql = "UPDATE inventario SET cantidad = ? WHERE id = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, inventario.getCantidad());
            stmt.setInt(2, inventario.getId());
            stmt.executeUpdate();
        }
    }

    public void insertar(Inventario inventario) throws Exception {
        String sql = "INSERT INTO inventario (id_producto, unidad, cantidad) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inventario.getProducto().getId());
            stmt.setString(2, inventario.getUnidad());
            stmt.setDouble(3, inventario.getCantidad());
            stmt.executeUpdate();
        }
    }

    public Inventario buscarPorProductoYUnidad(int idProducto, String unidad) throws Exception {
        ProductoDAO productoDAO = new ProductoDAO();
        String sql = "SELECT * FROM inventario WHERE id_producto = ? AND unidad = ?";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            stmt.setString(2, unidad);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Producto p = productoDAO.obtenerPorId(idProducto);
                return new Inventario(
                        rs.getInt("id"),
                        p,
                        unidad,
                        rs.getDouble("cantidad")
                );
            }
        }
        return null;
    }
}
