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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author saray
 */
public class MiServidor {

    private ServerSocket serverSocket;
    //private static MiClienteTrabajador trabajador;
    
    private static final List<MiClienteTrabajador> trabajadores = new ArrayList<>();

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
                MiClienteTrabajador nuevoTrabajador = new MiClienteTrabajador(socket);
                registrarTrabajador(nuevoTrabajador);
                nuevoTrabajador.start();
                System.out.println("Trabajador conectado. Total activos: " + trabajadores.size());
                
                
//                this.trabajador = new MiClienteTrabajador(socket);
//                this.trabajador.start();
            } else {
                System.out.println("Cliente conectado");
                MiCliente miCliente = new MiCliente(socket);
                miCliente.start();
            }
        }
    }

//    public static MiClienteTrabajador getTrabajador() {
//        return trabajador;
//    }
    
    
     private static synchronized void registrarTrabajador(MiClienteTrabajador trabajador) {
        trabajadores.add(trabajador);
    }
     
     public static synchronized void desregistrarTrabajador(MiClienteTrabajador trabajador) {
        trabajadores.remove(trabajador);
        System.out.println("Trabajador desconectado. Activos restantes: " + trabajadores.size());
    }
     
     public static synchronized MiClienteTrabajador getTrabajador() {
        limpiarDesconectados();
        if (trabajadores.isEmpty()) return null;
        return trabajadores.get(0);
    }

      public static synchronized List<MiClienteTrabajador> getTrabajadores() {
        limpiarDesconectados();
        return new ArrayList<>(trabajadores);
    }
     
      private static void limpiarDesconectados() {
        trabajadores.removeIf(w -> w == null || w.getSocket() == null || w.getSocket().isClosed());
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
      
      
    

} // fin clase
