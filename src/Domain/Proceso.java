/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

/**
 * Representa un proceso de análisis con su estado, cantidad de hilos utilizados
 * y el resultado obtenido.
 *
 * @author saray
 */
public class Proceso {

    private int idProceso;
    private int idUsario;
    private int cantidadDeHilos;
    private String estado;
    private Resultado resultado;

    public Proceso(int idProceso, int idUsario, int cantidadDeHilos, String estado, Resultado resultado) {
        this.idProceso = idProceso;
        this.idUsario = idUsario;
        this.cantidadDeHilos = cantidadDeHilos;
        this.estado = estado;
        this.resultado = resultado;
    }

    public void run() {

    }

    public void incializarAnalisis() {

    }

    public void detener() {

    }

    public int getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(int idProceso) {
        this.idProceso = idProceso;
    }

    public int getIdUsario() {
        return idUsario;
    }

    public int getCantidadDeHilos() {
        return cantidadDeHilos;
    }

    public void setCantidadDeHilos(int cantidadDeHilos) {
        this.cantidadDeHilos = cantidadDeHilos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public void setResultado(Resultado resultado) {
        this.resultado = resultado;
    }

}
