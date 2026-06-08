/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

/**
 *
 * @author saray
 */
public class Resultado {
    private int idResultado;
    private int idTarea;
    private  String fecha;
    private int totalImagenes;
    private int totalEnlaces;
    private int totalProductos;
    private int totalVideos;

    public int getIdResultado() {
        return idResultado;
    }

    public int getTotalVideos() {
        return totalVideos;
    }

    public void setTotalVideos(int totalVideos) {
        this.totalVideos = totalVideos;
    }

    public void setIdResultado(int idResultado) {
        this.idResultado = idResultado;
    }

    public int getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getTotalImagenes() {
        return totalImagenes;
    }

    public void setTotalImagenes(int totalImagenes) {
        this.totalImagenes = totalImagenes;
    }

    public int getTotalEnlaces() {
        return totalEnlaces;
    }

    public void setTotalEnlaces(int totalEnlaces) {
        this.totalEnlaces = totalEnlaces;
    }

    public int getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(int totalProductos) {
        this.totalProductos = totalProductos;
    }

    @Override
    public String toString() {
        return "Resultado{" + "idResultado=" + idResultado + ", idTarea=" + idTarea + ", fecha=" + fecha + ", totalImagenes=" + totalImagenes + ", totalEnlaces=" + totalEnlaces + ", totalProductos=" + totalProductos + '}';
    }
    
    
}
