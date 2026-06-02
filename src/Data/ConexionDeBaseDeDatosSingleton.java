/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar la conexión a la base de datos SQLite.
 * Implementa el patrón Singleton para garantizar una única instancia.
 *
 * @author emily
 */
public class ConexionDeBaseDeDatosSingleton {

    //URL de conexión con la base de datos
    private final String URL = "jdbc:sqlite:awos.db";
    //instancia de la clase
    private static ConexionDeBaseDeDatosSingleton cdb;

    /**
     * Constructor privado para evitar instancias externas
     */
    private ConexionDeBaseDeDatosSingleton() {
    }

    /**
     * Retorna la única instancia de ConexionBD (Singleton).
     *
     * @return instancia de ConexionBD
     */
    public static synchronized ConexionDeBaseDeDatosSingleton getInstancia() {
    if (cdb == null) {
        cdb = new ConexionDeBaseDeDatosSingleton();
    }
    return cdb;
}

    /**
     * Establece y retorna una conexion activa con la base de datos
     *
     * @return objeto Connection activo, null si falla
     */
    public Connection conectar() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e);
        }
        return conn;
    }//conectar

}//fin clase
