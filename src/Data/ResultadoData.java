/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import Domain.Resultado;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Clase de acceso a datos para la entidad Resultado. Proporciona métodos para
 * realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre la tabla
 * 'resultado' en la base de datos. La tabla resultado tiene una relación de
 * clave foránea con la tabla 'tarea', almacenando estadísticas y métricas del
 * procesamiento de cada tarea.
 *
 * @author emily
 */
public class ResultadoData {

    private ConexionDeBaseDeDatosSingleton cdb;

    /**
     * Constructor de la clase ResultadoData. Inicializa la conexión a la base
     * de datos obteniendo la instancia única del singleton de conexión.
     */
    public ResultadoData() {
        this.cdb = ConexionDeBaseDeDatosSingleton.getInstancia();
    }

    /**
     * Inicializa la tabla 'resultado' en la base de datos si ésta no existe. La
     * tabla contiene los campos: - idResultado: identificador único
     * autoincremental del resultado - idTarea: clave foránea que referencia a
     * la tarea procesada - fecha: fecha y hora en que se generó el resultado
     * (formato texto) - totalImagenes: cantidad total de imágenes encontradas
     * en la tarea - totalEnlaces: cantidad total de enlaces encontrados en la
     * tarea - totalProductos: cantidad total de productos identificados en la
     * tarea
     *
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void inicializarBD() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS resultado ("
                + "idResultado INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "idTarea INTEGER NOT NULL,"
                + "fecha TEXT NOT NULL,"
                + "totalImagenes INTEGER NOT NULL,"
                + "totalEnlaces INTEGER NOT NULL,"
                + "totalProductos INTEGER NOT NULL,"
                + "totalVideos INTEGER NOT NULL, "
                + "FOREIGN KEY (idTarea) REFERENCES tarea(idTarea)"
                + ")";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement();) {
            stmt.execute(sql);
        }
    }

    /**
     * Inserta un nuevo resultado en la base de datos. Cada resultado está
     * asociado a una tarea específica mediante su idTarea.
     *
     * @param resultado Objeto Resultado que contiene los datos a insertar
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
  public void insertar(Resultado resultado) throws SQLException {
   
    String sql = "INSERT INTO resultado(idTarea, fecha, totalImagenes, totalEnlaces, totalProductos, totalVideos) VALUES(?,?,?,?,?,?)";
    try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, resultado.getIdTarea());
        pstmt.setString(2, resultado.getFecha());
        pstmt.setInt(3, resultado.getTotalImagenes());
        pstmt.setInt(4, resultado.getTotalEnlaces());
        pstmt.setInt(5, resultado.getTotalProductos());
        pstmt.setInt(6, resultado.getTotalVideos());  
        pstmt.execute();
    }
}

    /**
     * Obtiene un resultado a partir de su identificador único.
     *
     * @param idResultado Identificador numérico del resultado a buscar
     * @return Objeto Resultado si se encuentra en la base de datos, null si no
     * existe
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public Resultado obtenerPorId(int idResultado) throws SQLException {
        String sql = "SELECT * FROM resultado WHERE idResultado = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idResultado);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Resultado resultado = new Resultado();
                    resultado.setIdResultado(rs.getInt("idResultado"));
                    resultado.setIdTarea(rs.getInt("idTarea"));
                    resultado.setFecha(rs.getString("fecha"));
                    resultado.setTotalImagenes(rs.getInt("totalImagenes"));
                    resultado.setTotalEnlaces(rs.getInt("totalEnlaces"));
                    resultado.setTotalProductos(rs.getInt("totalProductos"));
                    resultado.setTotalVideos(rs.getInt("totalVideos"));
                    return resultado;
                }
            }
        }
        return null;
    }

    /**
     * Obtiene el resultado asociado a una tarea específica. Como cada tarea
     * probablemente tiene un único resultado, este método devuelve el resultado
     * correspondiente al ID de tarea proporcionado.
     *
     * @param idTarea Identificador numérico de la tarea cuyo resultado se busca
     * @return Objeto Resultado asociado a la tarea si existe, null si la tarea
     * aún no tiene resultados o no se encuentra
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public Resultado obtenerPorIdTarea(int idTarea) throws SQLException {
        String sql = "SELECT * FROM resultado WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTarea);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Resultado resultado = new Resultado();
                    resultado.setIdResultado(rs.getInt("idResultado"));
                    resultado.setIdTarea(rs.getInt("idTarea"));
                    resultado.setFecha(rs.getString("fecha"));
                    resultado.setTotalImagenes(rs.getInt("totalImagenes"));
                    resultado.setTotalEnlaces(rs.getInt("totalEnlaces"));
                    resultado.setTotalProductos(rs.getInt("totalProductos"));
                    resultado.setTotalVideos(rs.getInt("totalVideos"));
                    return resultado;
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todos los resultados almacenados en la base de datos,
     * independientemente de la tarea a la que pertenezcan.
     *
     * @return ArrayList con todos los objetos Resultado encontrados. Si no hay
     * resultados, devuelve una lista vacía (no null)
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public ArrayList<Resultado> obtenerTodos() throws SQLException {
        ArrayList<Resultado> resultados = new ArrayList<>();
        String sql = "SELECT * FROM resultado";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Resultado resultado = new Resultado();
                resultado.setIdResultado(rs.getInt("idResultado"));
                resultado.setIdTarea(rs.getInt("idTarea"));
                resultado.setFecha(rs.getString("fecha"));
                resultado.setTotalImagenes(rs.getInt("totalImagenes"));
                resultado.setTotalEnlaces(rs.getInt("totalEnlaces"));
                resultado.setTotalProductos(rs.getInt("totalProductos"));
                resultado.setTotalVideos(rs.getInt("totalVideos"));
                resultados.add(resultado);
            }
        }
        return resultados;
    }

    /**
     * Actualiza los datos de un resultado existente en la base de datos. La
     * actualización se realiza buscando por el idResultado del objeto
     * proporcionado.
     *
     * @param resultado Objeto Resultado que contiene el ID del resultado a
     * actualizar junto con los nuevos valores de todos sus campos
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
  public void actualizar(Resultado resultado) throws SQLException {
  
    String sql = "UPDATE resultado SET idTarea = ?, fecha = ?, totalImagenes = ?, totalEnlaces = ?, totalProductos = ?, totalVideos = ? WHERE idResultado = ?";
    try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, resultado.getIdTarea());
        pstmt.setString(2, resultado.getFecha());
        pstmt.setInt(3, resultado.getTotalImagenes());
        pstmt.setInt(4, resultado.getTotalEnlaces());
        pstmt.setInt(5, resultado.getTotalProductos());
        pstmt.setInt(6, resultado.getTotalVideos());
        pstmt.setInt(7, resultado.getIdResultado());
        pstmt.execute();
    }
}
    /**
     * Elimina un resultado de la base de datos a partir de su identificador.
     *
     * @param idResultado Identificador numérico del resultado que se desea
     * eliminar
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void eliminar(int idResultado) throws SQLException {
        String sql = "DELETE FROM resultado WHERE idResultado = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idResultado);
            pstmt.execute();
        }
    }

    /**
     * Elimina el resultado asociado a una tarea específica. Útil cuando se
     * elimina una tarea y se desea eliminar también su resultado asociado, o
     * para limpiar resultados antiguos de una tarea antes de generar uno nuevo.
     *
     * @param idTarea Identificador numérico de la tarea cuyo resultado se desea
     * eliminar
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void eliminarPorIdTarea(int idTarea) throws SQLException {
        String sql = "DELETE FROM resultado WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTarea);
            pstmt.execute();
        }
    }

    public void migrarAgregarColumnaVideos() throws SQLException {
        String sql = "ALTER TABLE resultado ADD COLUMN totalVideos INTEGER DEFAULT 0";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Columna 'totalVideos' agregada a la tabla resultado");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                throw e;
            }
            System.out.println("La columna 'totalVideos' ya existe");
        }
    }
}//fin clase
