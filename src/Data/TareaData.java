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
            
            // Crear tabla con soporte para múltiples URLs y opciones de análisis
            String sql = "CREATE TABLE IF NOT EXISTS tarea ("
                    + "idTarea INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombreTarea TEXT NOT NULL,"
                    + "urls TEXT NOT NULL,"
                    + "estado TEXT NOT NULL,"
                    + "idUsuarioCreador INTEGER NOT NULL,"
                    + "prioridad INTEGER NOT NULL,"
                    + "descripcion TEXT,"
                    + "analizarImagenes INTEGER DEFAULT 0,"
                    + "analizarVideos INTEGER DEFAULT 0,"
                    + "analizarLinks INTEGER DEFAULT 0,"
                    + "analizarProductos INTEGER DEFAULT 0,"
                    + "analizarServicios INTEGER DEFAULT 0,"
                    + "FOREIGN KEY (idUsuarioCreador) REFERENCES usuario(idUsuario)"
                    + ")";
            stmt.execute(sql);
            
            // Migrar columnas si es necesario
            migrarColumnas(stmt);
            
        } catch (SQLException e) {
            System.err.println("Error al inicializar tabla tarea: " + e.getMessage());
            throw e;
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }
    
    private void migrarColumnas(Statement stmt) {
        // Agregar columna analizarImagenes si no existe
        try {
            stmt.execute("ALTER TABLE tarea ADD COLUMN analizarImagenes INTEGER DEFAULT 1");
            System.out.println(" Columna 'analizarImagenes' agregada");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                System.out.println("La columna 'analizarImagenes' ya existe");
            }
        }
        
        // Agregar columna analizarVideos
        try {
            stmt.execute("ALTER TABLE tarea ADD COLUMN analizarVideos INTEGER DEFAULT 1");
            System.out.println(" Columna 'analizarVideos' agregada");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                System.out.println("La columna 'analizarVideos' ya existe");
            }
        }
        
        // Agregar columna analizarLinks
        try {
            stmt.execute("ALTER TABLE tarea ADD COLUMN analizarLinks INTEGER DEFAULT 1");
            System.out.println(" Columna 'analizarLinks' agregada");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                System.out.println("La columna 'analizarLinks' ya existe");
            }
        }
        
        // Agregar columna analizarProductos
        try {
            stmt.execute("ALTER TABLE tarea ADD COLUMN analizarProductos INTEGER DEFAULT 0");
            System.out.println(" Columna 'analizarProductos' agregada");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                System.out.println("La columna 'analizarProductos' ya existe");
            }
        }
        
        // Agregar columna analizarServicios
        try {
            stmt.execute("ALTER TABLE tarea ADD COLUMN analizarServicios INTEGER DEFAULT 0");
            System.out.println(" Columna 'analizarServicios' agregada");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                System.out.println("La columna 'analizarServicios' ya existe");
            }
        }
        
        // Migrar datos antiguos: si existe columna URL, pasar a urls
        try {
            stmt.execute("ALTER TABLE tarea ADD COLUMN urls TEXT");
            System.out.println(" Columna 'urls' agregada");
            
            // Copiar datos de URL a urls
            stmt.execute("UPDATE tarea SET urls = URL WHERE urls IS NULL AND URL IS NOT NULL");
            System.out.println(" Datos migrados de URL a urls");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                System.out.println("La columna 'urls' ya existe");
            }
        }
    }

   public int insertar(Tarea tarea) throws SQLException {
    // Nota: Incluye la columna URL para compatibilidad con estructura antigua
    String sql = "INSERT INTO tarea(nombreTarea, urls, URL, estado, idUsuarioCreador, prioridad, descripcion, "
            + "analizarImagenes, analizarVideos, analizarLinks, analizarProductos, analizarServicios) "
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
    try (Connection conn = this.cdb.conectar(); 
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        // Convertir lista de URLs a string separado por |
        String urlsStr = "";
        String primeraUrl = "";
        if (tarea.getUrls() != null && !tarea.getUrls().isEmpty()) {
            urlsStr = String.join("|", tarea.getUrls());
            primeraUrl = tarea.getUrls().get(0); // Primera URL para la columna URL
        }
        
        pstmt.setString(1, tarea.getNombreTarea());
        pstmt.setString(2, urlsStr);
        pstmt.setString(3, primeraUrl); // ← Columna URL (obligatoria)
        pstmt.setString(4, tarea.getEstado());
        pstmt.setInt(5, tarea.getIdUsuarioCreador());
        pstmt.setInt(6, tarea.getPrioridad());
        pstmt.setString(7, tarea.getDescripcion() != null ? tarea.getDescripcion() : "");
        pstmt.setInt(8, tarea.isAnalizarImagenes() ? 1 : 0);
        pstmt.setInt(9, tarea.isAnalizarVideos() ? 1 : 0);
        pstmt.setInt(10, tarea.isAnalizarLinks() ? 1 : 0);
        pstmt.setInt(11, tarea.isAnalizarProductos() ? 1 : 0);
        pstmt.setInt(12, tarea.isAnalizarServicios() ? 1 : 0);
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
                    Tarea tarea = new Tarea();
                    tarea.setIdTarea(rs.getInt("idTarea"));
                    tarea.setNombreTarea(rs.getString("nombreTarea"));
                    
                    // Convertir URLs de string a ArrayList
                    String urlsStr = rs.getString("urls");
                    ArrayList<String> urls = new ArrayList<>();
                    if (urlsStr != null && !urlsStr.isEmpty()) {
                        for (String url : urlsStr.split("\\|")) {
                            urls.add(url);
                        }
                    }
                    tarea.setUrls(urls);
                    
                    tarea.setEstado(rs.getString("estado"));
                    tarea.setIdUsuarioCreador(rs.getInt("idUsuarioCreador"));
                    tarea.setPrioridad(rs.getInt("prioridad"));
                    tarea.setDescripcion(rs.getString("descripcion"));
                    tarea.setAnalizarImagenes(rs.getInt("analizarImagenes") == 1);
                    tarea.setAnalizarVideos(rs.getInt("analizarVideos") == 1);
                    tarea.setAnalizarLinks(rs.getInt("analizarLinks") == 1);
                    tarea.setAnalizarProductos(rs.getInt("analizarProductos") == 1);
                    tarea.setAnalizarServicios(rs.getInt("analizarServicios") == 1);
                    
                    return tarea;
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
                    Tarea tarea = new Tarea();
                    tarea.setIdTarea(rs.getInt("idTarea"));
                    tarea.setNombreTarea(rs.getString("nombreTarea"));
                    
                    String urlsStr = rs.getString("urls");
                    ArrayList<String> urls = new ArrayList<>();
                    if (urlsStr != null && !urlsStr.isEmpty()) {
                        for (String url : urlsStr.split("\\|")) {
                            urls.add(url);
                        }
                    }
                    tarea.setUrls(urls);
                    
                    tarea.setEstado(rs.getString("estado"));
                    tarea.setIdUsuarioCreador(rs.getInt("idUsuarioCreador"));
                    tarea.setPrioridad(rs.getInt("prioridad"));
                    tarea.setDescripcion(rs.getString("descripcion"));
                    tarea.setAnalizarImagenes(rs.getInt("analizarImagenes") == 1);
                    tarea.setAnalizarVideos(rs.getInt("analizarVideos") == 1);
                    tarea.setAnalizarLinks(rs.getInt("analizarLinks") == 1);
                    tarea.setAnalizarProductos(rs.getInt("analizarProductos") == 1);
                    tarea.setAnalizarServicios(rs.getInt("analizarServicios") == 1);
                    
                    tareas.add(tarea);
                }
            }
        }
        return tareas;
    }

    public ArrayList<Tarea> obtenerTodos() throws SQLException {
        ArrayList<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT * FROM tarea";
        try (Connection conn = this.cdb.conectar(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Tarea tarea = new Tarea();
                tarea.setIdTarea(rs.getInt("idTarea"));
                tarea.setNombreTarea(rs.getString("nombreTarea"));
                
                String urlsStr = rs.getString("urls");
                ArrayList<String> urls = new ArrayList<>();
                if (urlsStr != null && !urlsStr.isEmpty()) {
                    for (String url : urlsStr.split("\\|")) {
                        urls.add(url);
                    }
                }
                tarea.setUrls(urls);
                
                tarea.setEstado(rs.getString("estado"));
                tarea.setIdUsuarioCreador(rs.getInt("idUsuarioCreador"));
                tarea.setPrioridad(rs.getInt("prioridad"));
                tarea.setDescripcion(rs.getString("descripcion"));
                tarea.setAnalizarImagenes(rs.getInt("analizarImagenes") == 1);
                tarea.setAnalizarVideos(rs.getInt("analizarVideos") == 1);
                tarea.setAnalizarLinks(rs.getInt("analizarLinks") == 1);
                tarea.setAnalizarProductos(rs.getInt("analizarProductos") == 1);
                tarea.setAnalizarServicios(rs.getInt("analizarServicios") == 1);
                
                tareas.add(tarea);
            }
        }
        return tareas;
    }

    public void actualizar(Tarea tarea) throws SQLException {
        String sql = "UPDATE tarea SET nombreTarea = ?, urls = ?, estado = ?, idUsuarioCreador = ?, "
                + "prioridad = ?, descripcion = ?, analizarImagenes = ?, analizarVideos = ?, "
                + "analizarLinks = ?, analizarProductos = ?, analizarServicios = ? WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String urlsStr = "";
            if (tarea.getUrls() != null && !tarea.getUrls().isEmpty()) {
                urlsStr = String.join("|", tarea.getUrls());
            }
            
            pstmt.setString(1, tarea.getNombreTarea());
            pstmt.setString(2, urlsStr);
            pstmt.setString(3, tarea.getEstado());
            pstmt.setInt(4, tarea.getIdUsuarioCreador());
            pstmt.setInt(5, tarea.getPrioridad());
            pstmt.setString(6, tarea.getDescripcion() != null ? tarea.getDescripcion() : "");
            pstmt.setInt(7, tarea.isAnalizarImagenes() ? 1 : 0);
            pstmt.setInt(8, tarea.isAnalizarVideos() ? 1 : 0);
            pstmt.setInt(9, tarea.isAnalizarLinks() ? 1 : 0);
            pstmt.setInt(10, tarea.isAnalizarProductos() ? 1 : 0);
            pstmt.setInt(11, tarea.isAnalizarServicios() ? 1 : 0);
            pstmt.setInt(12, tarea.getIdTarea());
            pstmt.execute();
            System.out.println(" Tarea actualizada correctamente");
        }
    }

    public void eliminar(int idTarea) throws SQLException {
        String sql = "DELETE FROM tarea WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTarea);
            pstmt.execute();
            System.out.println(" Tarea eliminada correctamente");
        }
    }
}