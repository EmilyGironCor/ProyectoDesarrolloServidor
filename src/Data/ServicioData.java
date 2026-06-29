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
        try {
            inicializarBD();
        } catch (SQLException e) {
            System.err.println("Error al inicializar tabla servicio: " + e.getMessage());
        }
    }

    public void inicializarBD() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS servicio ("
                + "idServicio INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "idTarea INTEGER NOT NULL,"
                + "nombre TEXT NOT NULL,"
                + "descripcion TEXT,"
                + "precio REAL NOT NULL,"
                + "URL TEXT NOT NULL,"
                + "FOREIGN KEY (idTarea) REFERENCES tarea(idTarea)"
                + ")";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println(" Tabla 'servicio' creada/verificada correctamente");
            
            //  Ejecutar migraciones automáticamente
            migrarColumnas(stmt);
            
        } catch (SQLException e) {
            System.err.println("Error al crear tabla servicio: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     *  MÉTODO DE MIGRACIÓN: Agrega columnas faltantes si la tabla ya existe
     */
    private void migrarColumnas(Statement stmt) {
        // 1. Agregar columna idTarea si no existe
        try {
            stmt.execute("ALTER TABLE servicio ADD COLUMN idTarea INTEGER DEFAULT 0");
            System.out.println(" Columna 'idTarea' agregada a tabla servicio");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                System.out.println("ℹ️ Columna 'idTarea' ya existe o error: " + e.getMessage());
            } else {
                System.out.println("ℹ️ Columna 'idTarea' ya existe");
            }
        }
        
        // 2. Agregar columna descripcion si no existe
        try {
            stmt.execute("ALTER TABLE servicio ADD COLUMN descripcion TEXT");
            System.out.println(" Columna 'descripcion' agregada a tabla servicio");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                System.out.println("ℹ️ Columna 'descripcion' ya existe o error: " + e.getMessage());
            } else {
                System.out.println("ℹ️ Columna 'descripcion' ya existe");
            }
        }
        
        // 3. Agregar columna precio si no existe
        try {
            stmt.execute("ALTER TABLE servicio ADD COLUMN precio REAL DEFAULT 0.0");
            System.out.println(" Columna 'precio' agregada a tabla servicio");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                System.out.println("ℹ️ Columna 'precio' ya existe o error: " + e.getMessage());
            } else {
                System.out.println("ℹ️ Columna 'precio' ya existe");
            }
        }
        
        // 4. Agregar columna URL si no existe
        try {
            stmt.execute("ALTER TABLE servicio ADD COLUMN URL TEXT");
            System.out.println(" Columna 'URL' agregada a tabla servicio");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                System.out.println("ℹ️ Columna 'URL' ya existe o error: " + e.getMessage());
            } else {
                System.out.println("ℹ️ Columna 'URL' ya existe");
            }
        }
    }

    public void insertar(Servicio servicio) throws SQLException {
        String sql = "INSERT INTO servicio(idTarea, nombre, descripcion, precio, URL) VALUES(?,?,?,?,?)";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, servicio.getIdTarea());
            pstmt.setString(2, servicio.getNombre());
            pstmt.setString(3, servicio.getDescripcion());
            pstmt.setDouble(4, servicio.getPrecio());
            pstmt.setString(5, servicio.getURL());
            pstmt.execute();
            System.out.println(" Servicio guardado: " + servicio.getNombre() + " (Tarea ID: " + servicio.getIdTarea() + ")");
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
                    Servicio servicio = new Servicio(
                            rs.getInt("idServicio"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getDouble("precio"),
                            rs.getString("URL")
                    );
                    servicio.setIdTarea(rs.getInt("idTarea"));
                    return servicio;
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
                Servicio servicio = new Servicio(
                        rs.getInt("idServicio"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getString("URL")
                );
                servicio.setIdTarea(rs.getInt("idTarea"));
                servicios.add(servicio);
            }
        }
        return servicios;
    }
    
    /**
     * Obtiene todos los servicios asociados a una tarea específica.
     *
     * @param idTarea Identificador de la tarea
     * @return ArrayList de servicios de esa tarea
     * @throws SQLException Si ocurre un error en la consulta
     */
    public ArrayList<Servicio> obtenerPorTarea(int idTarea) throws SQLException {
        ArrayList<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicio WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTarea);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Servicio servicio = new Servicio(
                            rs.getInt("idServicio"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getDouble("precio"),
                            rs.getString("URL")
                    );
                    servicio.setIdTarea(rs.getInt("idTarea"));
                    servicios.add(servicio);
                }
            }
        }
        return servicios;
    }

    public void eliminar(int idServicio) throws SQLException {
        String sql = "DELETE FROM servicio WHERE idServicio = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idServicio);
            pstmt.execute();
            System.out.println(" Servicio eliminado: ID " + idServicio);
        }
    }
    
    /**
     * Elimina todos los servicios asociados a una tarea.
     *
     * @param idTarea Identificador de la tarea
     * @throws SQLException Si ocurre un error en la consulta
     */
    public void eliminarPorTarea(int idTarea) throws SQLException {
        String sql = "DELETE FROM servicio WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTarea);
            pstmt.execute();
            System.out.println(" Servicios eliminados para tarea: " + idTarea);
        }
    }
}