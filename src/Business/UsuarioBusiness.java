package Business;

import Data.UsuarioData;
import Domain.Usuario;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author saray
 */
public class UsuarioBusiness {

    private UsuarioData usuarioData;

    public UsuarioBusiness() throws SQLException {
        this.usuarioData = new UsuarioData();
        this.usuarioData.inicializarBD();
    }

    public Usuario buscarPorNombre(String nombre) throws SQLException {
        System.out.println("UsuarioBusiness.buscarPorNombre() - Nombre: " + nombre);
        return this.usuarioData.obtenerPorNombre(nombre);
    }

    public void insertar(Usuario usuario) throws SQLException {
        this.usuarioData.insertar(usuario);
    }

    public ArrayList<Usuario> leerTodo() throws SQLException {
        return this.usuarioData.obtenerTodos();
    }

    public void actualizar(Usuario usuario) throws SQLException {
        this.usuarioData.actualizar(usuario);
    }

    public void eliminar(int idUsuario) throws SQLException {
        this.usuarioData.eliminar(idUsuario);
    }

    public Usuario buscarPorId(int idUsuario) throws SQLException {
        return this.usuarioData.obtenerPorId(idUsuario);
    }

    public Usuario verificarLogin(String username, String password) throws SQLException {
        String sql = "SELECT idUsuario, nombre, contrasena, correo, idRol, estado FROM usuario WHERE nombre = ? AND contrasena = ?";

        // 1. Encriptamos la contraseña recibida en texto plano utilizando el método de UsuarioData
        // Si tu método en UsuarioData es privado, puedes copiar la lógica SHA-1 aquí,
        // o asegurarte de usar el string procesado.
        String contrasenaEncriptada = "";
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            contrasenaEncriptada = sb.toString();
        } catch (Exception ex) {
            System.out.println("Error al encriptar en Business: " + ex.getMessage());
            contrasenaEncriptada = password; // fallback por si falla
        }

        // El try-with-resources abre y cierra automáticamente las conexiones
        try (Connection conn = Data.ConexionDeBaseDeDatosSingleton.getInstancia().conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, contrasenaEncriptada); // Comparamos HASH contra HASH

            try (ResultSet rs = ps.executeQuery()) {
                Usuario u = null;
                if (rs.next()) {
                    u = new Usuario();

                    // CORRECCIÓN: Nombres de columnas idénticos a los de tu SQLite
                    u.setId(rs.getInt("idUsuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setContrasena(rs.getString("contrasena"));
                    u.setCorreo(rs.getString("correo"));
                    u.setRol(rs.getInt("idRol"));

                    // SQLite maneja booleanos como enteros (1 para true, 0 para false)
                    u.setEstado(rs.getInt("estado") == 1);
                }
                return u;
            }
        }

    }
}
