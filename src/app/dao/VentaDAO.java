package app.dao;

import app.db.ConexionDB;
import app.modelo.Producto;
import app.modelo.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public void insertarVenta(Venta venta) throws Exception {
        String sql = "INSERT INTO venta (id_producto, unidad, cantidad, fecha_hora) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, venta.getProducto().getId());
            stmt.setString(2, venta.getUnidad());
            stmt.setDouble(3, venta.getCantidad());
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(venta.getFechaHora()));

            stmt.executeUpdate();
        }
    }


    public List<Venta> obtenerTodas() throws Exception {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT * FROM venta ORDER BY fecha_hora DESC";

        try (Connection conn = ConexionDB.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ProductoDAO productoDAO = new ProductoDAO();

            while (rs.next()) {
                int idProducto = rs.getInt("id_producto");
                Producto producto = productoDAO.obtenerPorId(idProducto);

                Venta venta = new Venta(
                        rs.getInt("id"),
                        producto,
                        rs.getString("unidad"),
                        rs.getDouble("cantidad"),
                        rs.getTimestamp("fecha_hora").toLocalDateTime()
                );

                lista.add(venta);
            }
        }

        return lista;
    }
}
