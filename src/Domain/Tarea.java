/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import java.util.Date;
import org.jdom.Element;

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

    public Tarea() {
    }

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

    public int getIdTarea() {
        return idTarea;
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

    public void toObject(Element element) {

        this.idTarea = Integer.parseInt(
                element.getChild("tarea")
                        .getChild("idTarea")
                        .getValue());

        this.nombreTarea = element.getChild("tarea")
                .getChild("nombreTarea")
                .getValue();

        this.URL = element.getChild("tarea")
                .getChild("URL")
                .getValue();

        this.estado = element.getChild("tarea")
                .getChild("estado")
                .getValue();

        this.idUsuarioCreador = Integer.parseInt(
                element.getChild("tarea")
                        .getChild("idUsuarioCreador")
                        .getValue());

        this.prioridad = Integer.parseInt(
                element.getChild("tarea")
                        .getChild("prioridad")
                        .getValue());
    }

    public Element toXMLElement() {

        Element eTarea = new Element("tarea");

        Element eIdTarea = new Element("idTarea");
        eIdTarea.addContent(String.valueOf(this.idTarea));

        Element eNombreTarea = new Element("nombreTarea");
        eNombreTarea.addContent(this.nombreTarea);

        Element eURL = new Element("URL");
        eURL.addContent(this.URL);

        Element eEstado = new Element("estado");
        eEstado.addContent(this.estado);

        Element eIdUsuarioCreador = new Element("idUsuarioCreador");
        eIdUsuarioCreador.addContent(String.valueOf(this.idUsuarioCreador));

        Element ePrioridad = new Element("prioridad");
        ePrioridad.addContent(String.valueOf(this.prioridad));

        Element eFechaCreacion = new Element("fechaDeCreacion");
        eFechaCreacion.addContent(String.valueOf(this.fechaDeCreacion));

        Element eCantidadHilos = new Element("cantidadDeHilos");
        eCantidadHilos.addContent(String.valueOf(this.cantidadDeHilos));

        eTarea.addContent(eIdTarea);
        eTarea.addContent(eNombreTarea);
        eTarea.addContent(eURL);
        eTarea.addContent(eEstado);
        eTarea.addContent(eIdUsuarioCreador);
        eTarea.addContent(ePrioridad);
        eTarea.addContent(eFechaCreacion);
        eTarea.addContent(eCantidadHilos);

        return eTarea;
    }

}
