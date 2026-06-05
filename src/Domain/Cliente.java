/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author emily
 */
public abstract class Cliente extends Thread {

    protected Socket socket;//esta clase se compone de un socket, porque a fuerza lo tiene que componer 
    protected static PrintStream enviar;
    protected BufferedReader recibir;

    public Cliente(Socket socket) throws IOException {
        this.socket = socket;
        this.recibir = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));//modifica el recibir y modifica el inputStream soket
        this.enviar = new PrintStream(this.socket.getOutputStream());//modifica enviiar y modifica el outputAtream soket

    }

    public abstract void run();

    public static void enviarDatos(String dato) {
        enviar.println(dato);
    }//enviarDato

    public String leerDatos() throws IOException {
        return this.recibir.readLine();
    }//leerdatos

}
