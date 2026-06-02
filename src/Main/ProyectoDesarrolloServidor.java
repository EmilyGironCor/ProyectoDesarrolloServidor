/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Main;

import Business.TareaBusiness;
import Business.UsuarioBusiness;
import Data.UsuarioData;
import Domain.MiServidor;
import Domain.Tarea;
import Domain.Usuario;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author emily
 */
public class ProyectoDesarrolloServidor {

    public static void main(String[] args) throws Exception {

        try {
//            // ==========================================
//            // PASO 1: Inicializar base de datos (crea tablas si no existen)
//            // ==========================================
//            System.out.println("=== VERIFICANDO BASE DE DATOS ===");
//            UsuarioData usuarioData = new UsuarioData();
//            usuarioData.inicializarBD();  // Solo crea la tabla si no existe y agrega admin si no hay usuarios
//            System.out.println("✅ Base de datos lista");

            // ==========================================
            // PASO 2: Iniciar servidor
            // ==========================================
            listarTodosLosUsuarios();
            System.out.println("=== INICIANDO SERVIDOR EN PUERTO 5025 ===");
            MiServidor miServidor = new MiServidor(5025);
            miServidor.escuchar();

        } catch (IOException ex) {
            System.out.println("Error de red: " + ex.getMessage());
            ex.printStackTrace();

        }
    }
    
public static void listarTodosLosUsuarios() {
        try {
            System.out.println("\n=== LISTADO DE USUARIOS EN BASE DE DATOS ===");
            
            UsuarioBusiness usuarioBusiness = new UsuarioBusiness();
            ArrayList<Usuario> usuarios = usuarioBusiness.leerTodo();
            
            if (usuarios.isEmpty()) {
                System.out.println("⚠️ No hay usuarios registrados en la base de datos.");
                return;
            }
            
            System.out.println("Total de usuarios: " + usuarios.size());
            System.out.println("--------------------------------------------------");
            
            for (Usuario u : usuarios) {
                System.out.println("ID: " + u.getId());
                System.out.println("  Nombre: " + u.getNombre());
                System.out.println("  Rol: " + (u.getRol() == 1 ? "Administrador" : "Examinador"));
                System.out.println("  Correo: " + u.getCorreo());
                System.out.println("  Estado: " + (u.isEstado() ? "Activo" : "Inactivo"));
                System.out.println("  Contraseña (hash): " + u.getContrasena().substring(0, Math.min(20, u.getContrasena().length())) + "...");
                System.out.println("--------------------------------------------------");
            }
            
        } catch (SQLException ex) {
            System.err.println("Error al listar usuarios: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
