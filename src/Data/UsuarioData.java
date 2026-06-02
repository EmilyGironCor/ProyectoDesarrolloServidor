/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import Domain.Usuario;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Clase de acceso a datos para la entidad Usuario. Proporciona métodos para
 * realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre la tabla
 * 'usuario' en la base de datos. La tabla usuario tiene una relación de clave
 * foránea con la tabla 'rol'.
 *
 * @author emily
 */
public class UsuarioData {

    private ConexionDeBaseDeDatosSingleton cdb;

    /**
     * Constructor de la clase UsuarioData. Inicializa la conexión a la base de
     * datos y crea una instancia de RolData para poder gestionar la relación
     * entre usuario y rol.
     */
    public UsuarioData() {
        this.cdb = ConexionDeBaseDeDatosSingleton.getInstancia();
    }

    /**
     * Inicializa la tabla 'usuario' en la base de datos si ésta no existe. La
     * tabla contiene los campos: - idUsuario: identificador único
     * autoincremental - nombre: nombre del usuario - contrasena: contraseña del
     * usuario - estado: indica si el usuario está activo (1) o inactivo (0) -
     * correo: correo electrónico del usuario - idRol: clave foránea que
     * referencia al rol del usuario
     *
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
public void inicializarBD() throws SQLException {
    String sql = "CREATE TABLE IF NOT EXISTS usuario ("
            + "idUsuario INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "nombre TEXT NOT NULL UNIQUE,"  // ← UNIQUE evita duplicados
            + "contrasena TEXT NOT NULL,"
            + "estado INTEGER NOT NULL,"
            + "correo TEXT NOT NULL,"
            + "idRol INTEGER NOT NULL,"
            + "FOREIGN KEY (idRol) REFERENCES rol(idRol)"
            + ")";
    
    try (Connection conn = this.cdb.conectar(); 
         Statement stmt = conn.createStatement()) {
        
        stmt.execute(sql);
        
        // Verificar si ya existe un administrador
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuario");
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        
        if (count == 0) {
            // Hash SHA-1 de "admin"
            String contrasenaEncriptada = encriptarSHA1("admin");
            
            String sqlAdmin = "INSERT INTO usuario (nombre, contrasena, estado, correo, idRol) "
                    + "VALUES ('admin', '" + contrasenaEncriptada + "', 1, 'admin@ejemplo.com', 1)";
            stmt.execute(sqlAdmin);
            
            System.out.println("✅ Usuario 'admin' creado con contraseña 'admin'");
            System.out.println("   Hash almacenado: " + contrasenaEncriptada);
        } else {
            System.out.println("La tabla usuario ya existe y tiene datos");
        }
    } catch (SQLException e) {
        System.err.println("Error al inicializar BD: " + e.getMessage());
        throw e;
    }
}

    /**
     * Inserta un nuevo usuario en la base de datos. Convierte el estado
     * booleano del usuario a un valor entero (1 para true, 0 para false) antes
     * de guardarlo en la tabla.
     *
     * @param usuario Objeto Usuario que contiene los datos a insertar
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void insertar(Usuario usuario) throws SQLException {
        // Al especificar los campos entre paréntesis, nos aseguramos de que no haya cruces de datos
        String sql = "INSERT INTO usuario (nombre, contrasena, estado, correo, idRol) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setInt(3, usuario.isEstado() ? 1 : 0); // Estado va en la posición 3
            pstmt.setString(4, usuario.getCorreo());     // Correo va en la posición 4
            pstmt.setInt(5, usuario.getRol());           // idRol va en la posición 5
            pstmt.execute();
        }
    }

    /**
     * Obtiene un usuario a partir de su identificador único. Además, consulta
     * automáticamente el objeto Rol asociado al usuario mediante la instancia
     * de RolData.
     *
     * @param idUsuario Identificador numérico del usuario a buscar
     * @return Objeto Usuario completo (con su Rol incluido) si se encuentra,
     * null si no existe
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public Usuario obtenerPorId(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE idUsuario = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    return new Usuario(
                            rs.getInt("idUsuario"),
                            rs.getString("nombre"),
                            rs.getInt("idRol"),
                            rs.getString("contrasena"),
                            rs.getInt("estado") == 1,
                            rs.getString("correo")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todos los usuarios almacenados en la base de datos. Para cada
     * usuario, consulta automáticamente su objeto Rol asociado.
     *
     * @return ArrayList con todos los objetos Usuario encontrados. Si no hay
     * usuarios, devuelve una lista vacía (no null)
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public ArrayList<Usuario> obtenerTodos() throws SQLException {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                
                usuarios.add(new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getInt("idRol"),
                        rs.getString("contrasena"),
                        rs.getInt("estado") == 1,
                        rs.getString("correo")
                ));
            }
        }
        return usuarios;
    }

    /**
     * Actualiza los datos de un usuario existente en la base de datos. La
     * actualización se realiza buscando por el id del objeto usuario
     * proporcionado. Convierte el estado booleano a entero (1/0) para
     * almacenarlo en la tabla.
     *
     * @param usuario Objeto Usuario que contiene el ID del usuario a actualizar
     * junto con los nuevos valores de nombre, contraseña, estado, correo y rol
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET nombre = ?, contrasena = ?, estado = ?, correo = ?, idRol = ? WHERE idUsuario = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setInt(3, usuario.isEstado() ? 1 : 0);
            pstmt.setString(4, usuario.getCorreo());
            pstmt.setInt(5, usuario.getRol());
            pstmt.setInt(6, usuario.getId());
            pstmt.execute();
        }
    }

    /**
     * Elimina un usuario de la base de datos a partir de su identificador.
     *
     * @param idUsuario Identificador numérico del usuario que se desea eliminar
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuario WHERE idUsuario = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            pstmt.execute();
        }
    }

    /**
     * Encripta un texto usando SHA-1. Mismo algoritmo que debe usar el Business
     * al verificar login.
     */
    private String encriptarSHA1(String texto) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(texto.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            System.out.println("Error al encriptar: " + ex.getMessage());
            return texto; // fallback: devolver sin encriptar
        }
    }
    public Usuario obtenerPorNombre(String nombre) throws SQLException {
    String sql = "SELECT * FROM usuario WHERE nombre = ?";
    try (Connection conn = this.cdb.conectar(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, nombre);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("idUsuario"),
                    rs.getString("nombre"),
                    rs.getInt("idRol"),
                    rs.getString("contrasena"),
                    rs.getInt("estado") == 1,
                    rs.getString("correo")
                );
            }
        }
    }
    return null;
}
}//fin clase
