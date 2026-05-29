/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import Domain.Proceso;
import Domain.Resultado;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Clase de acceso a datos para la entidad Proceso. Proporciona métodos para
 * realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre la tabla
 * 'proceso' en la base de datos. La tabla proceso tiene relaciones de clave
 * foránea con: - usuario: el usuario que ejecuta o posee el proceso -
 * resultado: el resultado asociado al proceso (opcional, puede ser null)
 *
 * @author emily
 */
public class ProcesoData {

    private ConexionDeBaseDeDatosSingleton cdb;
    private ResultadoData resultadoData;

    /**
     * Constructor de la clase ProcesoData. Inicializa la conexión a la base de
     * datos y crea una instancia de ResultadoData para poder gestionar la
     * relación entre proceso y su resultado asociado.
     */
    public ProcesoData() {
        this.cdb = ConexionDeBaseDeDatosSingleton.getInstancia();
        this.resultadoData = new ResultadoData();
    }

    /**
     * Inicializa la tabla 'proceso' en la base de datos si ésta no existe. La
     * tabla contiene los campos: - idProceso: identificador único
     * autoincremental del proceso - idUsuario: clave foránea que referencia al
     * usuario dueño del proceso - cantidadDeHilos: número de hilos o threads
     * asignados al proceso - estado: estado actual del proceso (ej: "en
     * ejecución", "completado", "fallido") - idResultado: clave foránea
     * opcional que referencia al resultado generado
     *
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void inicializarBD() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS proceso ("
                + "idProceso INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "idUsuario INTEGER NOT NULL,"
                + "cantidadDeHilos INTEGER NOT NULL,"
                + "estado TEXT NOT NULL,"
                + "idResultado INTEGER,"
                + "FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario),"
                + "FOREIGN KEY (idResultado) REFERENCES resultado(idResultado)"
                + ")";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement();) {
            stmt.execute(sql);
        }
    }

    /**
     * Inserta un nuevo proceso en la base de datos. Si el proceso tiene un
     * resultado asociado, guarda su ID; de lo contrario, guarda un valor NULL
     * en la columna idResultado.
     *
     * @param proceso Objeto Proceso que contiene los datos a insertar
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void insertar(Proceso proceso) throws SQLException {
        String sql = "INSERT INTO proceso(idUsuario, cantidadDeHilos, estado, idResultado) VALUES(?,?,?,?)";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, proceso.getIdUsario());
            pstmt.setInt(2, proceso.getCantidadDeHilos());
            pstmt.setString(3, proceso.getEstado());

            // Manejar resultado opcional (puede ser null si el proceso aún no ha terminado)
            if (proceso.getResultado() != null) {
                pstmt.setInt(4, proceso.getResultado().getIdResultado());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            pstmt.execute();
        }
    }

    /**
     * Obtiene un proceso a partir de su identificador único. Además, consulta
     * automáticamente el objeto Resultado asociado al proceso (si existe)
     * mediante la instancia de ResultadoData.
     *
     * @param idProceso Identificador numérico del proceso a buscar
     * @return Objeto Proceso completo (con su Resultado incluido si existe) si
     * se encuentra, null si no existe
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public Proceso obtenerPorId(int idProceso) throws SQLException {
        String sql = "SELECT * FROM proceso WHERE idProceso = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProceso);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Resultado resultado = null;
                    int idResultado = rs.getInt("idResultado");
                    // Verificar si el campo idResultado no es NULL antes de intentar obtener el resultado
                    if (!rs.wasNull()) {
                        resultado = resultadoData.obtenerPorId(idResultado);
                    }
                    return new Proceso(
                            rs.getInt("idProceso"),
                            rs.getInt("idUsuario"),
                            rs.getInt("cantidadDeHilos"),
                            rs.getString("estado"),
                            resultado
                    );
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todos los procesos asociados a un usuario específico. Útil para
     * listar el historial de procesos ejecutados por un usuario particular.
     * Cada proceso incluye su resultado asociado si existe.
     *
     * @param idUsuario Identificador numérico del usuario dueño de los procesos
     * @return ArrayList con todos los procesos asociados al usuario. Si el
     * usuario no tiene procesos, devuelve una lista vacía (no null)
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public ArrayList<Proceso> obtenerPorUsuario(int idUsuario) throws SQLException {
        ArrayList<Proceso> procesos = new ArrayList<>();
        String sql = "SELECT * FROM proceso WHERE idUsuario = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Resultado resultado = null;
                    int idResultado = rs.getInt("idResultado");
                    if (!rs.wasNull()) {
                        resultado = resultadoData.obtenerPorId(idResultado);
                    }
                    procesos.add(new Proceso(
                            rs.getInt("idProceso"),
                            rs.getInt("idUsuario"),
                            rs.getInt("cantidadDeHilos"),
                            rs.getString("estado"),
                            resultado
                    ));
                }
            }
        }
        return procesos;
    }

    /**
     * Obtiene todos los procesos almacenados en la base de datos,
     * independientemente del usuario al que pertenezcan.
     *
     * @return ArrayList con todos los objetos Proceso encontrados. Si no hay
     * procesos, devuelve una lista vacía (no null)
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public ArrayList<Proceso> obtenerTodos() throws SQLException {
        ArrayList<Proceso> procesos = new ArrayList<>();
        String sql = "SELECT * FROM proceso";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Resultado resultado = null;
                int idResultado = rs.getInt("idResultado");
                if (!rs.wasNull()) {
                    resultado = resultadoData.obtenerPorId(idResultado);
                }
                procesos.add(new Proceso(
                        rs.getInt("idProceso"),
                        rs.getInt("idUsuario"),
                        rs.getInt("cantidadDeHilos"),
                        rs.getString("estado"),
                        resultado
                ));
            }
        }
        return procesos;
    }

    /**
     * Actualiza los datos de un proceso existente en la base de datos. La
     * actualización se realiza buscando por el idProceso del objeto
     * proporcionado. Si el proceso tiene un nuevo resultado asociado, actualiza
     * su ID; si no, guarda NULL en la columna idResultado.
     *
     * @param proceso Objeto Proceso que contiene el ID del proceso a actualizar
     * junto con los nuevos valores de usuario, cantidad de hilos, estado y
     * resultado
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void actualizar(Proceso proceso) throws SQLException {
        String sql = "UPDATE proceso SET idUsuario = ?, cantidadDeHilos = ?, estado = ?, idResultado = ? WHERE idProceso = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, proceso.getIdUsario());
            pstmt.setInt(2, proceso.getCantidadDeHilos());
            pstmt.setString(3, proceso.getEstado());

            // Manejar resultado opcional (puede ser null si el proceso aún no tiene resultado)
            if (proceso.getResultado() != null) {
                pstmt.setInt(4, proceso.getResultado().getIdResultado());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            pstmt.setInt(5, proceso.getIdProceso());
            pstmt.execute();
        }
    }

    /**
     * Elimina un proceso de la base de datos a partir de su identificador.
     * Nota: Esta operación solo elimina el proceso, no elimina el resultado
     * asociado (si existe), ya que el resultado podría estar referenciado por
     * otras entidades.
     *
     * @param idProceso Identificador numérico del proceso que se desea eliminar
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void eliminar(int idProceso) throws SQLException {
        String sql = "DELETE FROM proceso WHERE idProceso = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProceso);
            pstmt.execute();
        }
    }
}//fin clase
