/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import Domain.Resultado;
import Domain.Tarea;
import java.net.Socket;

/**
 *
 * @author saray
 */
public class MiClienteTrabajador {
   private int idNodo;
   private String ipCliente;
   private int puerto;
   private String esatdo;
   private Socket socket;
   private int cantidadTareasAsugnadas;
   
   public void conectar(){
       
   }
   
   public void desconectar(){
       
   }
   
   public void asignarTarea(Tarea tarea){
       
   }
   
   public Resultado recibirResultado(){
       Resultado resultado =  new Resultado();
       
       return resultado;
   }
   public boolean estaDisponible(){
       return false;
   }

    @Override
    public String toString() {
        return "MiClienteTrabajador{" + "idNodo=" + idNodo + ", ipCliente=" + ipCliente + ", puerto=" + puerto + ", esatdo=" + esatdo + ", socket=" + socket + ", cantidadTareasAsugnadas=" + cantidadTareasAsugnadas + '}';
    }
   
}
