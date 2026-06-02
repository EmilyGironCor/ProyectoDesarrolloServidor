/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Main;

import Data.UsuarioData;
import Domain.MiServidor;
import Domain.Usuario;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author emily
 */
public class ProyectoDesarrolloServidor {

    public static void main(String[] args) throws Exception {
        try {
            MiServidor miServidor=new MiServidor(5025);
            miServidor.escuchar();
        } catch (IOException ex) {
            System.out.println("error");
        }
        
    }
}
