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
    // Invoca la única instancia de la base de datos SQLite
    Connection conn = Data.ConexionDeBaseDeDatosSingleton.getInstancia().conectar();
    
    String sql = "SELECT * FROM usuario WHERE nombre = ? AND contrasena = ?";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setString(1, username);
    ps.setString(2, password);
    ResultSet rs = ps.executeQuery();
    
    Usuario u = null;
    if (rs.next()) {
        u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombre(rs.getString("nombre"));
        u.setContrasena(rs.getString("contrasena"));
        u.setCorreo(rs.getString("correo"));
        u.setRol(rs.getInt("rol"));
        u.setEstado(rs.getBoolean("estado"));
    }
    
    rs.close();
    ps.close();
    conn.close(); // Siempre cerrar la conexión al terminar la consulta
    return u;
}
}
