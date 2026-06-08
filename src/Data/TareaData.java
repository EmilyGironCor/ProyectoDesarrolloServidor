/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import Domain.Tarea;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Clase de acceso a datos para la entidad Tarea. Proporciona métodos para
 * realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre la tabla
 * 'tarea' en la base de datos. La tabla tarea tiene una relación de clave
 * foránea con la tabla 'usuario' a través del campo idUsuarioCreador (el
 * usuario que creó la tarea).
 *
 * @author emily
 */
public class TareaData {

    private ConexionDeBaseDeDatosSingleton cdb;
    private UsuarioData usuarioData;

    /**
     * Constructor de la clase TareaData. Inicializa la conexión a la base de
     * datos y crea una instancia de UsuarioData para poder gestionar la
     * relación entre tarea y usuario creador.
     */
    public TareaData() {
        this.cdb = ConexionDeBaseDeDatosSingleton.getInstancia();
        this.usuarioData = new UsuarioData();
    }

    /**
     * Inicializa la tabla 'tarea' en la base de datos si ésta no existe. La
     * tabla contiene los campos: - idTarea: identificador único autoincremental
     * de la tarea - nombreTarea: nombre o título de la tarea - URL: dirección
     * web asociada a la tarea - estado: estado actual de la tarea (ej:
     * "pendiente", "en progreso", "completada") - idUsuarioCreador: clave
     * foránea que referencia al usuario que creó la tarea - prioridad: nivel de
     * prioridad de la tarea (ej: 1=alta, 2=media, 3=baja) - fechaDeCreacion:
     * fecha en que se creó la tarea - cantidadDeHilos: número de hilos o
     * procesos asignados a la tarea
     *
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
   public void inicializarBD() throws SQLException {
    Connection conn = null;
    Statement stmt = null;
    try {
        conn = this.cdb.conectar();
        stmt = conn.createStatement();
        
        // Crear tabla si no existe (estructura NUEVA)
        String sql = "CREATE TABLE IF NOT EXISTS tarea ("
                + "idTarea INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombreTarea TEXT NOT NULL,"
                + "URL TEXT NOT NULL,"
                + "estado TEXT NOT NULL,"
                + "idUsuarioCreador INTEGER NOT NULL,"
                + "prioridad INTEGER NOT NULL,"
                + "FOREIGN KEY (idUsuarioCreador) REFERENCES usuario(idUsuario)"
                + ")";
        stmt.execute(sql);
//        
//        // Verificar si existe la columna fechaDeCreacion (tabla vieja)
//        try {
//            stmt.execute("SELECT fechaDeCreacion FROM tarea LIMIT 1");
//            // Si llegamos aquí, la columna existe → hay que migrar
//            System.out.println("⚠️ Tabla con estructura antigua detectada. Migrando...");
//            
//            // SQLite no soporta DROP COLUMN directamente, hay que recrear
//            stmt.execute("DROP TABLE IF EXISTS tarea_temp");
//            stmt.execute("CREATE TABLE tarea_temp AS SELECT idTarea, nombreTarea, URL, estado, idUsuarioCreador, prioridad FROM tarea");
//            stmt.execute("DROP TABLE tarea");
//            stmt.execute("ALTER TABLE tarea_temp RENAME TO tarea");
//            
//            System.out.println("✅ Migración completada");
//        } catch (SQLException e) {
//            // La columna no existe, tabla ya está bien
//            System.out.println("✅ Tabla tarea ya tiene estructura correcta");
//        }
        
    } catch (SQLException e) {
        System.err.println("Error al inicializar tabla tarea: " + e.getMessage());
        throw e;
    } finally {
        if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
        if (conn != null) try { conn.close(); } catch (SQLException e) {}
    }
}

  /**
 * Inserta una nueva tarea en la base de datos y retorna el ID generado.
 *
 * @param tarea Objeto Tarea que contiene los datos a insertar
 * @return int ID generado automáticamente
 * @throws SQLException Si ocurre un error durante la ejecución de la sentencia SQL
 */
public int insertar(Tarea tarea) throws SQLException {
    String sql = "INSERT INTO tarea(nombreTarea, URL, estado, idUsuarioCreador, prioridad) VALUES(?,?,?,?,?)";
    try (Connection conn = this.cdb.conectar(); 
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        pstmt.setString(1, tarea.getNombreTarea());
        pstmt.setString(2, tarea.getURL());
        pstmt.setString(3, tarea.getEstado());
        pstmt.setInt(4, tarea.getIdUsuarioCreador());
        pstmt.setInt(5, tarea.getPrioridad());
        pstmt.execute();
        
        // Obtener el ID generado automáticamente
        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                System.out.println(" Tarea insertada con ID: " + idGenerado);
                return idGenerado;
            } else {
                throw new SQLException("No se pudo obtener el ID generado");
            }
        }
    }
}

    /**
     * Obtiene una tarea a partir de su identificador único.
     *
     * @param idTarea Identificador numérico de la tarea a buscar
     * @return Objeto Tarea si se encuentra en la base de datos, null si no
     * existe
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
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
                            rs.getInt("prioridad")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todas las tareas creadas por un usuario específico. Útil para
     * listar el historial o las tareas asignadas a un usuario particular.
     *
     * @param idUsuario Identificador numérico del usuario creador
     * @return ArrayList con todas las tareas asociadas al usuario. Si el
     * usuario no tiene tareas, devuelve una lista vacía (no null)
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public ArrayList<Tarea> obtenerPorUsuario(int idUsuario) throws SQLException {
        ArrayList<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT * FROM tarea WHERE idUsuarioCreador = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tareas.add(new Tarea(
                            rs.getInt("idTarea"),
                            rs.getString("nombreTarea"),
                            rs.getString("URL"),
                            rs.getString("estado"),
                            rs.getInt("idUsuarioCreador"),
                            rs.getInt("prioridad")
                    ));
                }
            }
        }
        return tareas;
    }

    /**
     * Obtiene todas las tareas almacenadas en la base de datos.
     *
     * @return ArrayList con todos los objetos Tarea encontrados. Si no hay
     * tareas, devuelve una lista vacía (no null)
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public ArrayList<Tarea> obtenerTodos() throws SQLException {
        ArrayList<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT * FROM tarea";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tareas.add(new Tarea(
                        rs.getInt("idTarea"),
                        rs.getString("nombreTarea"),
                        rs.getString("URL"),
                        rs.getString("estado"),
                        rs.getInt("idUsuarioCreador"),
                        rs.getInt("prioridad")
                ));
            }
        }
        return tareas;
    }

    /**
     * Actualiza los datos de una tarea existente en la base de datos. La
     * actualización se realiza buscando por el idTarea del objeto
     * proporcionado. Convierte la fecha de creación de java.util.Date a
     * java.sql.Date para poder actualizarla correctamente en la base de datos.
     *
     * @param tarea Objeto Tarea que contiene el ID de la tarea a actualizar
     * junto con los nuevos valores de todos sus campos
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void actualizar(Tarea tarea) throws SQLException {
        String sql = "UPDATE tarea SET nombreTarea = ?, URL = ?, estado = ?, idUsuarioCreador = ?, prioridad = ? WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tarea.getNombreTarea());
            pstmt.setString(2, tarea.getURL());
            pstmt.setString(3, tarea.getEstado());
            pstmt.setInt(4, tarea.getIdUsuarioCreador());
            pstmt.setInt(5, tarea.getPrioridad());
            pstmt.setInt(6, tarea.getIdTarea());
            pstmt.execute();
        }
    }

    /**
     * Elimina una tarea de la base de datos a partir de su identificador.
     *
     * @param idTarea Identificador numérico de la tarea que se desea eliminar
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void eliminar(int idTarea) throws SQLException {
        String sql = "DELETE FROM tarea WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTarea);
            pstmt.execute();
        }
    }
}//fin clase
