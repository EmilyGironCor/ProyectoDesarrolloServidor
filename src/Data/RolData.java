/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import Domain.Rol;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Clase de acceso a datos para la entidad Rol. Proporciona métodos para
 * realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre la tabla
 * 'rol' en la base de datos.
 *
 * @author emily
 */
public class RolData {

    private ConexionDeBaseDeDatosSingleton cdb;

    /**
     * Constructor de la clase RolData. Inicializa el objeto de conexión a la
     * base de datos obteniendo la instancia única del singleton de conexión.
     */
    public RolData() {
        this.cdb = ConexionDeBaseDeDatosSingleton.getInstancia();
    }

    /**
     * Inicializa la tabla 'rol' en la base de datos si ésta no existe. La tabla
     * contiene los campos: - idRol: identificador único autoincremental -
     * nombre: nombre del rol (ej: ADMIN, USUARIO) - descripcion: descripción
     * detallada del rol
     *
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void inicializarBD() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS rol ("
                + "idRol INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "descripcion TEXT NOT NULL,"
                + "nombre TEXT NOT NULL" + ")";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement();) {
            stmt.execute(sql);
        }
    }

    /**
     * Inserta un nuevo rol en la base de datos.
     *
     * @param rol Objeto Rol que contiene los datos a insertar (nombre y
     * descripción)
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void insertar(Rol rol) throws SQLException {
        String sql = "INSERT INTO rol(descripcion, nombre) VALUES(?,?)";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rol.getDescripcion());
            pstmt.setString(2, rol.getNombre());
            pstmt.execute();
        }
    }

    /**
     * Obtiene un rol a partir de su identificador único.
     *
     * @param idRol Identificador numérico del rol a buscar
     * @return Objeto Rol si se encuentra en la base de datos, null si no existe
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public Rol obtenerPorId(int idRol) throws SQLException {
        String sql = "SELECT * FROM rol WHERE idRol = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idRol);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Rol(
                            rs.getInt("idRol"),
                            rs.getString("nombre"),
                            rs.getString("descripcion")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todos los roles almacenados en la base de datos.
     *
     * @return ArrayList con todos los objetos Rol encontrados. Si no hay roles,
     * devuelve una lista vacía (no null).
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public ArrayList<Rol> obtenerTodos() throws SQLException {
        ArrayList<Rol> roles = new ArrayList<>();
        String sql = "SELECT * FROM rol";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(new Rol(
                        rs.getInt("idRol"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                ));
            }
        }
        return roles;
    }

    /**
     * Actualiza los datos de un rol existente en la base de datos. La
     * actualización se realiza buscando por el idRol del objeto proporcionado.
     *
     * @param rol Objeto Rol que contiene el ID del rol a actualizar junto con
     * los nuevos valores de nombre y descripción
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void actualizar(Rol rol) throws SQLException {
        String sql = "UPDATE rol SET descripcion = ?, nombre = ? WHERE idRol = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rol.getDescripcion());
            pstmt.setString(2, rol.getNombre());
            pstmt.setInt(3, rol.getIdRol());
            pstmt.execute();
        }
    }

    /**
     * Elimina un rol de la base de datos a partir de su identificador.
     *
     * @param idRol Identificador numérico del rol que se desea eliminar
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void eliminar(int idRol) throws SQLException {
        String sql = "DELETE FROM rol WHERE idRol = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idRol);
            pstmt.execute();
        }
    }
}//fin clase
