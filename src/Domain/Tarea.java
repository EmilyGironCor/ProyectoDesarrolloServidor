/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import java.util.Date;

/**
 *
 * @author saray
 */
public class Tarea {
    private int idTarea;
    private String nombreTarea;
    private String URL;
    private String estado;
    private int idUsuarioCreador;
    private int prioridad;
    private Date fechaDeCreacion;
    private int cantidadDeHilos;

    public Tarea(int idTarea, String nombreTarea, String URL, String estado, int idUsuarioCreador, int prioridad, Date fechaDeCreacion, int cantidadDeHilos) {
        this.idTarea = idTarea;
        this.nombreTarea = nombreTarea;
        this.URL = URL;
        this.estado = estado;
        this.idUsuarioCreador = idUsuarioCreador;
        this.prioridad = prioridad;
        this.fechaDeCreacion = fechaDeCreacion;
        this.cantidadDeHilos = cantidadDeHilos;
    }

    public int getIdTarea() { return idTarea;
    }

    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }

    public String getNombreTarea() {
        return nombreTarea;
    }

    public void setNombreTarea(String nombreTarea) {
        this.nombreTarea = nombreTarea;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdUsuarioCreador() {
        return idUsuarioCreador;
    }

    public void setIdUsuarioCreador(int idUsuarioCreador) {
        this.idUsuarioCreador = idUsuarioCreador;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public Date getFechaDeCreacion() {
        return fechaDeCreacion;
    }

    public void setFechaDeCreacion(Date fechaDeCreacion) {
        this.fechaDeCreacion = fechaDeCreacion;
    }

    public int getCantidadDeHilos() {
        return cantidadDeHilos;
    }

    public void setCantidadDeHilos(int cantidadDeHilos) {
        this.cantidadDeHilos = cantidadDeHilos;
    }

    @Override
    public String toString() {
        return "Tarea{" + "idTarea=" + idTarea + ", nombreTarea=" + nombreTarea + ", URL=" + URL + ", estado=" + estado + ", idUsuarioCreador=" + idUsuarioCreador + ", prioridad=" + prioridad + ", fechaDeCreacion=" + fechaDeCreacion + ", cantidadDeHilos=" + cantidadDeHilos + '}';
    }
    
    
}
