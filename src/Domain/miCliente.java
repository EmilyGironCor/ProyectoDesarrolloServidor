/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import Utility.GestionXML;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Representa un cliente conectado al servidor y procesa las
 * solicitudes recibidas mediante el protocolo de comunicación.
 * @author Saray
 */
public class MiCliente extends Cliente {

    public MiCliente(Socket socket) throws IOException {
        super(socket);
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
                EnumProtocolo enumProtocolo
                        = EnumProtocolo.valueOf(accion);
                enumProtocolo.accion(this, eAccion.getChild("datos"));

            } while (true);

        } catch (IOException ex) {
            Logger.getLogger(MiCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(MiCliente.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (recibir != null) {
                try {
                    recibir.close();
                } catch (IOException ex) {
                    Logger.getLogger(MiCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (enviar != null) {
                enviar.close();
            }
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(MiCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    } // run

} // fin clase

