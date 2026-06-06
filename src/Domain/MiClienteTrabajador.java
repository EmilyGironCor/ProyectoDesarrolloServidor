/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import Domain.DataProtocolo;
import Domain.Resultado;
import Domain.Tarea;
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
 * @author saray
 */
public class MiClienteTrabajador extends Cliente {

    private Socket socket;
    private BufferedReader recibir;
    private PrintStream enviar;
    private Tarea analisis;

    public MiClienteTrabajador(Socket socket) throws IOException {
        super(socket);
        this.analisis = null;
    }

    public void run() {
        try {

        

            do {
                String xmlString = this.leerDatos();

                if (xmlString == null) {
                    System.out.println("Cliente desconectado");
                    break;
                }

                System.out.println(xmlString);

                Element eAccion = GestionXML.stringTOXML(xmlString);
                String accion = eAccion.getAttributeValue("metodo");

                EnumProtocolo enumProtocolo = EnumProtocolo.valueOf(accion);
                enumProtocolo.accion(this, eAccion.getChild("datos"));

            } while (true);

        } catch (IOException ex) {
            Logger.getLogger(MiClienteTrabajador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(MiClienteTrabajador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Tarea getAnalisis() {
        return analisis;
    }

    public void setAnalisis(Tarea analisis) {
        this.analisis = analisis;
    }

}//fin clases
