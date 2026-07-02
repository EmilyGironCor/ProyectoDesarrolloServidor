/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Main;



import Domain.MiServidor;
import Domain.Usuario;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author emily
 */
public class ProyectoDesarrolloServidor {

    public static void main(String[] args) throws Exception {
 
        try {

           
            System.out.println("=== INICIANDO SERVIDOR EN PUERTO 5025 ===");
            MiServidor miServidor = new MiServidor(5025);
            miServidor.escuchar();

        } catch (IOException ex) {
            System.out.println("Error de red: " + ex.getMessage());
            ex.printStackTrace();

        }
    }


}//fin clase
