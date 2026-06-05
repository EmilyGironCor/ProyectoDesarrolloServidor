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
public class Tarea implements XMLConvertible {

    private int idTarea;
    private String nombreTarea;
    private String URL;
    private String estado;
    private int idUsuarioEncargado;
    private int prioridad;

    private int cantidadDeHilos;

    public Tarea(String url) {
        this.URL = url;
    }

    public Tarea() {

    }

    public Tarea(int idTarea, String nombreTarea, String URL, String estado, int idUsuarioCreador, int prioridad, int cantidadDeHilos) {
        this.idTarea = idTarea;
        this.nombreTarea = nombreTarea;
        this.URL = URL;
        this.estado = estado;
        this.idUsuarioEncargado = idUsuarioCreador;
        this.prioridad = prioridad;

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
        return idUsuarioEncargado;
    }

    public void setIdUsuarioCreador(int idUsuarioCreador) {
        this.idUsuarioEncargado = idUsuarioCreador;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public int getCantidadDeHilos() {
        return cantidadDeHilos;
    }

    public void setCantidadDeHilos(int cantidadDeHilos) {
        this.cantidadDeHilos = cantidadDeHilos;
    }

    public int getidUsuarioEncargado() {
        return idUsuarioEncargado;
    }

    public void setidUsuarioEncargado(int dUsuarioEncargado) {
        this.idUsuarioEncargado = dUsuarioEncargado;
    }

    @Override
    public String toString() {
        return "Tarea{" + "idTarea=" + idTarea + ", nombreTarea=" + nombreTarea + ", URL=" + URL + ", estado=" + estado + ", idUsuarioCreador=" + idUsuarioEncargado + ", prioridad=" + prioridad + ", cantidadDeHilos=" + cantidadDeHilos + '}';
    }

    public void toObject(Element element) {
        // Detectar si ya somos <tarea> o si hay que buscarlo dentro de <datos>
        Element root = element.getName().equals("tarea")
                ? element
                : element.getChild("tarea");

        if (root == null) {
            System.out.println("Error: No se encontró el nodo <tarea> en el XML recibido.");
            return;
        }

        // idTarea (puede llegar en 0 si es nueva)
        if (root.getChild("idTarea") != null) {
            this.idTarea = Integer.parseInt(root.getChild("idTarea").getValue());
        }

        if (root.getChild("nombreTarea") != null) {
            this.nombreTarea = root.getChild("nombreTarea").getValue();
        }

        if (root.getChild("URL") != null) {
            this.URL = root.getChild("URL").getValue();
        }

        if (root.getChild("estado") != null) {
            this.estado = root.getChild("estado").getValue();
        } else {
            this.estado = "pendiente"; // valor por defecto
        }

        if (root.getChild("idUsuarioCreador") != null) {
            this.idUsuarioEncargado = Integer.parseInt(root.getChild("idUsuarioCreador").getValue());
        }

        if (root.getChild("prioridad") != null) {
            this.prioridad = Integer.parseInt(root.getChild("prioridad").getValue());
        }

        if (root.getChild("cantidadDeHilos") != null) {
            this.cantidadDeHilos = Integer.parseInt(root.getChild("cantidadDeHilos").getValue());
        }
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
        eIdUsuarioCreador.addContent(String.valueOf(this.idUsuarioEncargado));

        Element ePrioridad = new Element("prioridad");
        ePrioridad.addContent(String.valueOf(this.prioridad));

        Element eCantidadHilos = new Element("cantidadDeHilos");
        eCantidadHilos.addContent(String.valueOf(this.cantidadDeHilos));

        eTarea.addContent(eIdTarea);
        eTarea.addContent(eNombreTarea);
        eTarea.addContent(eURL);
        eTarea.addContent(eEstado);
        eTarea.addContent(eIdUsuarioCreador);
        eTarea.addContent(ePrioridad);
        eTarea.addContent(eCantidadHilos);

        return eTarea;
    }

}
