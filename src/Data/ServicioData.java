/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import Domain.Servicio;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author emily
 */
public class ServicioData {

    private ConexionDeBaseDeDatosSingleton cdb;

    public ServicioData() {
        this.cdb = ConexionDeBaseDeDatosSingleton.getInstancia();
    }

    public void inicializarBD() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS servicio ("
                + "idServicio INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre TEXT NOT NULL,"
                + "descripcion TEXT,"
                + "precio REAL NOT NULL,"
                + "URL TEXT NOT NULL"
                + ")";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void insertar(Servicio servicio) throws SQLException {
        String sql = "INSERT INTO servicio(nombre, descripcion, precio, URL) VALUES(?,?,?,?)";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, servicio.getNombre());
            pstmt.setString(2, servicio.getDescripcion());
            pstmt.setDouble(3, servicio.getPrecio());
            pstmt.setString(4, servicio.getURL());
            pstmt.execute();
        } catch (SQLException e) {
            throw new SQLException("Error al guardar el servicio: " + e.getMessage());
        }
    }

    public Servicio obtenerPorId(int idServicio) throws SQLException {
        String sql = "SELECT * FROM servicio WHERE idServicio = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idServicio);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Servicio(
                            rs.getInt("idServicio"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getDouble("precio"),
                            rs.getString("URL")
                    );
                }
            }
        }
        return null;
    }

    public ArrayList<Servicio> obtenerTodos() throws SQLException {
        ArrayList<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicio";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                servicios.add(new Servicio(
                        rs.getInt("idServicio"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getString("URL")
                ));
            }
        }
        return servicios;
    }

    public void eliminar(int idServicio) throws SQLException {
        String sql = "DELETE FROM servicio WHERE idServicio = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idServicio);
            pstmt.execute();
        }
    }
}//fin clase
