/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import Domain.Producto;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * Clase de acceso a datos para la entidad Producto. Proporciona métodos para
 * realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre la tabla
 * 'producto' en la base de datos. Esta clase maneja la conversión de imágenes
 * entre el formato BufferedImage (Java) y el tipo BLOB (Base de Datos) para
 * almacenar imágenes directamente en la BD.
 *
 * @author emily
 */
public class ProductoData {

    private ConexionDeBaseDeDatosSingleton cdb;

    /**
     * Constructor de la clase ProductoData. Inicializa la conexión a la base de
     * datos obteniendo la instancia única del singleton de conexión.
     */
    public ProductoData() {
        this.cdb = ConexionDeBaseDeDatosSingleton.getInstancia();
        try {
            inicializarBD();
        } catch (SQLException e) {
            System.err.println("Error al inicializar tabla producto: " + e.getMessage());
        }
    }

    /**
     * Inicializa la tabla 'producto' en la base de datos si ésta no existe.
     * Ahora incluye la columna idTarea para asociar productos a una tarea.
     *
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void inicializarBD() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS producto ("
                + "idProducto INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "idTarea INTEGER NOT NULL,"
                + "precio REAL NOT NULL,"
                + "descripcion TEXT NOT NULL,"
                + "imagen BLOB,"
                + "URL TEXT NOT NULL,"
                + "FOREIGN KEY (idTarea) REFERENCES tarea(idTarea)"
                + ")";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Tabla 'producto' creada/verificada correctamente");
            
            // ✅ Ejecutar migraciones automáticamente
            migrarColumnas(stmt);
            
        } catch (SQLException e) {
            System.err.println("Error al crear tabla producto: " + e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ MÉTODO DE MIGRACIÓN: Agrega columnas faltantes si la tabla ya existe
     */
    private void migrarColumnas(Statement stmt) {
        // 1. Agregar columna idTarea si no existe
        try {
            stmt.execute("ALTER TABLE producto ADD COLUMN idTarea INTEGER DEFAULT 0");
            System.out.println("✅ Columna 'idTarea' agregada a tabla producto");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                System.out.println("ℹ️ Columna 'idTarea' ya existe o error: " + e.getMessage());
            } else {
                System.out.println("ℹ️ Columna 'idTarea' ya existe");
            }
        }
    }

    /**
     * Inserta un nuevo producto en la base de datos. Convierte la imagen de
     * BufferedImage a un arreglo de bytes (byte[]) para almacenarla como BLOB
     * en la base de datos. Si la imagen es null, guarda un valor nulo en la
     * columna imagen.
     *
     * @param producto Objeto Producto que contiene los datos a insertar
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     * @throws IOException Si ocurre un error al procesar la imagen
     * (escritura/conversión)
     */
    public void insertar(Producto producto) throws SQLException, IOException {
        String sql = "INSERT INTO producto(idTarea, precio, descripcion, imagen, URL) VALUES(?,?,?,?,?)";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, producto.getIdTarea());
            pstmt.setDouble(2, producto.getPrecio());
            pstmt.setString(3, producto.getDescripcion());

            // Convertir la imagen a bytes si existe
            if (producto.getImagen() != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(producto.getImagen(), "png", baos);
                pstmt.setBytes(4, baos.toByteArray());
            } else {
                pstmt.setBytes(4, null);
            }

            pstmt.setString(5, producto.getURL());
            pstmt.execute();
            System.out.println("✅ Producto guardado: " + producto.getDescripcion() + " (Tarea ID: " + producto.getIdTarea() + ")");

        } catch (SQLException e) {
            throw new SQLException("Error al guardar la información: " + e.getMessage());
        }
    }

    /**
     * Obtiene un producto a partir de su identificador único. Convierte los
     * bytes de la imagen almacenada en la base de datos (BLOB) de vuelta a un
     * objeto BufferedImage para ser utilizado en la aplicación.
     *
     * @param idProducto Identificador numérico del producto a buscar
     * @return Objeto Producto completo (con imagen incluida) si se encuentra,
     * null si no existe
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     * @throws IOException Si ocurre un error al procesar la imagen
     * (lectura/conversión)
     */
    public Producto obtenerPorId(int idProducto) throws SQLException, IOException {
        String sql = "SELECT * FROM producto WHERE idProducto = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BufferedImage imagen = null;
                    byte[] imagenBytes = rs.getBytes("imagen");
                    if (imagenBytes != null) {
                        imagen = ImageIO.read(new ByteArrayInputStream(imagenBytes));
                    }
                    Producto producto = new Producto(
                            rs.getInt("idProducto"),
                            rs.getDouble("precio"),
                            rs.getString("descripcion"),
                            imagen,
                            rs.getString("URL")
                    );
                    producto.setIdTarea(rs.getInt("idTarea"));
                    return producto;
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todos los productos almacenados en la base de datos. Para cada
     * producto, convierte los bytes de la imagen (si existe) de vuelta a un
     * objeto BufferedImage.
     *
     * @return ArrayList con todos los objetos Producto encontrados. Si no hay
     * productos, devuelve una lista vacía (no null)
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     * @throws IOException Si ocurre un error al procesar alguna imagen
     * (lectura/conversión)
     */
    public ArrayList<Producto> obtenerTodos() throws SQLException, IOException {
        ArrayList<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM producto";
        try (Connection conn = this.cdb.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                BufferedImage imagen = null;
                byte[] imagenBytes = rs.getBytes("imagen");
                if (imagenBytes != null) {
                    imagen = ImageIO.read(new ByteArrayInputStream(imagenBytes));
                }
                Producto producto = new Producto(
                        rs.getInt("idProducto"),
                        rs.getDouble("precio"),
                        rs.getString("descripcion"),
                        imagen,
                        rs.getString("URL")
                );
                producto.setIdTarea(rs.getInt("idTarea"));
                productos.add(producto);
            }
        }
        return productos;
    }

    /**
     * Obtiene todos los productos asociados a una tarea específica.
     *
     * @param idTarea Identificador de la tarea
     * @return ArrayList de productos de esa tarea
     * @throws SQLException Si ocurre un error en la consulta
     * @throws IOException Si ocurre un error al procesar alguna imagen
     */
    public ArrayList<Producto> obtenerPorTarea(int idTarea) throws SQLException, IOException {
        ArrayList<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTarea);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BufferedImage imagen = null;
                    byte[] imagenBytes = rs.getBytes("imagen");
                    if (imagenBytes != null) {
                        imagen = ImageIO.read(new ByteArrayInputStream(imagenBytes));
                    }
                    Producto producto = new Producto(
                            rs.getInt("idProducto"),
                            rs.getDouble("precio"),
                            rs.getString("descripcion"),
                            imagen,
                            rs.getString("URL")
                    );
                    producto.setIdTarea(rs.getInt("idTarea"));
                    productos.add(producto);
                }
            }
        }
        return productos;
    }

    /**
     * Actualiza los datos de un producto existente en la base de datos.
     * Convierte la imagen actualizada de BufferedImage a bytes (BLOB) para
     * almacenarla. Si la nueva imagen es null, guarda un valor nulo. La
     * actualización se realiza buscando por el idProducto del objeto
     * proporcionado.
     *
     * @param producto Objeto Producto que contiene el ID del producto a
     * actualizar junto con los nuevos valores de precio, descripción, imagen y
     * URL
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     * @throws IOException Si ocurre un error al procesar la imagen
     * (escritura/conversión)
     */
    public void actualizar(Producto producto) throws SQLException, IOException {
        String sql = "UPDATE producto SET idTarea = ?, precio = ?, descripcion = ?, imagen = ?, URL = ? WHERE idProducto = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, producto.getIdTarea());
            pstmt.setDouble(2, producto.getPrecio());
            pstmt.setString(3, producto.getDescripcion());

            // Convertir la nueva imagen a bytes si existe
            if (producto.getImagen() != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(producto.getImagen(), "png", baos);
                pstmt.setBytes(4, baos.toByteArray());
            } else {
                pstmt.setBytes(4, null);
            }

            pstmt.setString(5, producto.getURL());
            pstmt.setInt(6, producto.getIdProducto());
            pstmt.execute();
            System.out.println("✅ Producto actualizado: ID " + producto.getIdProducto());
        }
    }

    /**
     * Elimina un producto de la base de datos a partir de su identificador.
     *
     * @param idProducto Identificador numérico del producto que se desea
     * eliminar
     * @throws SQLException Si ocurre un error durante la ejecución de la
     * sentencia SQL
     */
    public void eliminar(int idProducto) throws SQLException {
        String sql = "DELETE FROM producto WHERE idProducto = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProducto);
            pstmt.execute();
            System.out.println("✅ Producto eliminado: ID " + idProducto);
        }
    }

    /**
     * Elimina todos los productos asociados a una tarea.
     *
     * @param idTarea Identificador de la tarea
     * @throws SQLException Si ocurre un error en la consulta
     */
    public void eliminarPorTarea(int idTarea) throws SQLException {
        String sql = "DELETE FROM producto WHERE idTarea = ?";
        try (Connection conn = this.cdb.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTarea);
            pstmt.execute();
            System.out.println("✅ Productos eliminados para tarea: " + idTarea);
        }
    }
}