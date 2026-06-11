/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author saray
 */
public class MiServidor {

    private ServerSocket serverSocket;
    private static MiClienteTrabajador trabajador;

    public MiServidor(int puerto) throws IOException {
        this.serverSocket = new ServerSocket(puerto);
    }

    public void escuchar() throws IOException {
        System.out.println("Servidor corriendo...");

        while (true) {
            Socket socket = this.serverSocket.accept();

            // Leer el mensaje de identificación sin cerrar el stream
            BufferedReader identificador = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );
            
            String tipo = identificador.readLine(); 

            if ("TRABAJADOR".equalsIgnoreCase(tipo)) {
                System.out.println("Trabajador conectado");
                this.trabajador = new MiClienteTrabajador(socket);
                this.trabajador.start();
            } else {
                System.out.println("Cliente conectado");
                miCliente miCliente = new miCliente(socket);
                miCliente.start();
            }
        }
    }

    public static MiClienteTrabajador getTrabajador() {
        return trabajador;
    }

} // fin clase
