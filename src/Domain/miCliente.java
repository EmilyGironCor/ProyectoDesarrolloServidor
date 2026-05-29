/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import Domain.Usuario;
import java.io.ObjectInputStream;
import java.net.Socket;
import javax.management.ObjectInstance;
import org.w3c.dom.Element;

/**
 *
 * @author saray
 */
public class miCliente {
    private int idCliente;
    private Socket socket;
    private Usuario usuario;
    private ObjectInputStream entrada;
    private ObjectInputStream salida;
    private boolean conectado;

    public miCliente(Socket socket) {
        this.socket = socket;
    }
    
    public void enviarMensaje(Element element){
        
    }
    
    public Element recibirMensaje(){
        return null;
    }
    
    public void cerrarSesion(){
        
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    @Override
    public String toString() {
        return "miCliente{" + "idCliente=" + idCliente + ", socket=" + socket + ", usuario=" + usuario + ", entrada=" + entrada + ", salida=" + salida + ", conectado=" + conectado + '}';
    }
    
    
}
