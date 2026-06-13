package Data;

import Domain.Tarea;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class TareaData {

    private ConexionDeBaseDeDatosSingleton cdb;
    private UsuarioData usuarioData;

    public TareaData() {
        this.cdb = ConexionDeBaseDeDatosSingleton.getInstancia();
        this.usuarioData = new UsuarioData();
    }

    public void inicializarBD() throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.cdb.conectar();
            stmt = conn.createStatement();
            
            // Crear tabla con la columna descripcion
            String sql = "CREATE TABLE IF NOT EXISTS tarea ("
                    + "idTarea INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombreTarea TEXT NOT NULL,"
                    + "URL TEXT NOT NULL,"
                    + "estado TEXT NOT NULL,"
                    + "idUsuarioCreador INTEGER NOT NULL,"
                    + "prioridad INTEGER NOT NULL,"
                    + "descripcion TEXT,"
                    + "FOREIGN KEY (idUsuarioCreador) REFERENCES usuario(idUsuario)"
                    + ")";
            stmt.execute(sql);
            
            // Migrar si falta la columna descripcion
            try {
                stmt.execute("ALTER TABLE tarea ADD COLUMN descripcion TEXT");
                System.out.println("✅ Columna 'descripcion' agregada a tabla tarea");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    throw e;
                }
                System.out.println("La columna 'descripcion' ya existe");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al inicializar tabla tarea: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }

    public int insertar(Tarea tarea) throws SQLException {
        String sql = "INSERT INTO tarea(nombreTarea, URL, estado, idUsuarioCreador, prioridad, descripcion) VALUES(?,?,?,?,?,?)";
        try (Connection conn = this.cdb.conectar(); 
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, tarea.getNombreTarea());
            pstmt.setString(2, tarea.getURL());
            pstmt.setString(3, tarea.getEstado());
            pstmt.setInt(4, tarea.getIdUsuarioCreador());
            pstmt.setInt(5, tarea.getPrioridad());
            pstmt.setString(6, tarea.getDescripcion() != null ? tarea.getDescripcion() : "");
            pstmt.execute();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    System.out.println("✅ Tarea insertada con ID: " + idGenerado);
                    return idGenerado;
                }
                throw new SQLException("No se pudo obtener el ID generado");
            }
        }
    }

    public Tarea obtenerPorId(int idTarea) throws SQLException {
        String sql = "SELECT * FROM tarea WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTarea);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Tarea(
                            rs.getInt("idTarea"),
                            rs.getString("nombreTarea"),
                            rs.getString("URL"),
                            rs.getString("estado"),
                            rs.getInt("idUsuarioCreador"),
                            rs.getInt("prioridad"),
                            rs.getString("descripcion")
                    );
                }
            }
        }
        return null;
    }

    public ArrayList<Tarea> obtenerPorUsuario(int idUsuario) throws SQLException {
        ArrayList<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT * FROM tarea WHERE idUsuarioCreador = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tarea tarea = new Tarea(
                            rs.getInt("idTarea"),
                            rs.getString("nombreTarea"),
                            rs.getString("URL"),
                            rs.getString("estado"),
                            rs.getInt("idUsuarioCreador"),
                            rs.getInt("prioridad"),
                            rs.getString("descripcion")
                    );
                    tareas.add(tarea);
                }
            }
        }
        return tareas;
    }

    public ArrayList<Tarea> obtenerTodos() throws SQLException {
        ArrayList<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT * FROM tarea";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Tarea tarea = new Tarea(
                        rs.getInt("idTarea"),
                        rs.getString("nombreTarea"),
                        rs.getString("URL"),
                        rs.getString("estado"),
                        rs.getInt("idUsuarioCreador"),
                        rs.getInt("prioridad"),
                        rs.getString("descripcion")
                );
                tareas.add(tarea);
            }
        }
        return tareas;
    }

    public void actualizar(Tarea tarea) throws SQLException {
        String sql = "UPDATE tarea SET nombreTarea = ?, URL = ?, estado = ?, idUsuarioCreador = ?, prioridad = ?, descripcion = ? WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tarea.getNombreTarea());
            pstmt.setString(2, tarea.getURL());
            pstmt.setString(3, tarea.getEstado());
            pstmt.setInt(4, tarea.getIdUsuarioCreador());
            pstmt.setInt(5, tarea.getPrioridad());
            pstmt.setString(6, tarea.getDescripcion() != null ? tarea.getDescripcion() : "");
            pstmt.setInt(7, tarea.getIdTarea());
            pstmt.execute();
            System.out.println ("Tarea actualizada correctamente");
        }
    }

    public void eliminar(int idTarea) throws SQLException {
        String sql = "DELETE FROM tarea WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTarea);
            pstmt.execute();
            System.out.println("✅ Tarea eliminada correctamente");
        }
    }
}