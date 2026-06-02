/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author saray
 */
public class MiServidor {

    private ServerSocket serverSocket;

    public MiServidor(int puerto) throws IOException {
        this.serverSocket=new ServerSocket(puerto);
    } // constructor
    
    public void escuchar() throws IOException{
        System.out.println("Servidor run");
        while (true) {            
            Socket socket=this.serverSocket.accept();
            System.out.println("Cliente accept");
            MiCliente miCliente=new MiCliente(socket);
            miCliente.start();
        } // while
    } // escuchar
    
    
    
} // fin clase
