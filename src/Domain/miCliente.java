/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import Utility.GestionXML;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author Saray
 */
public class MiCliente extends Thread {
//comit
    private Socket socket;
    private BufferedReader recibir;
    private PrintStream enviar;

    public MiCliente(Socket socket) throws IOException {
        this.socket = socket;
        this.recibir = new BufferedReader(
                new InputStreamReader(this.socket.getInputStream())
        );
        this.enviar = new PrintStream(this.socket.getOutputStream());
    } // constructor

    public void enviarDatos(String dato) {
        this.enviar.println(dato);
    }

    public String leerDatos() throws IOException {
        return this.recibir.readLine();
    }

    public void run() {
        try {

            do {
                String xmlString = this.leerDatos();
                System.out.println(xmlString);
                Element eAccion = GestionXML.stringTOXML(xmlString);
                String accion = eAccion.getAttributeValue("metodo");
                EnumProtocolo enumProtocolo
                        = EnumProtocolo.valueOf(accion);
                enumProtocolo.accion(this, eAccion.getChild("datos"));

            } while (true);

        } catch (IOException ex) {
            Logger.getLogger(MiCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(MiCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // run

} // fin clase

