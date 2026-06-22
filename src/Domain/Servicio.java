/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import org.jdom.Element;

/**
 *
 * @author emily
 */
public class Servicio implements XMLConvertible{
    private int idServicio;
    private String nombre;
    private String descripcion;
    private double precio;
    private String URL;

    public Servicio(int idServicio, String nombre, String descripcion, double precio, String URL) {
        this.idServicio = idServicio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.URL = URL;
    }

    public Servicio() {
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Override
    public String toString() {
        return "Servicio{" + "idServicio=" + idServicio + ", nombre=" + nombre + ", descripcion=" + descripcion + ", precio=" + precio + ", URL=" + URL + '}';
    }

    @Override
    public void toObject(Element element) {
this.idServicio = Integer.parseInt(element.getChildText("idServicio"));
        this.nombre = element.getChildText("nombre");
        this.descripcion = element.getChildText("descripcion");

        String precioTexto = element.getChildText("precio");
        this.precio = (precioTexto != null && !precioTexto.isEmpty())
                ? Double.parseDouble(precioTexto) : 0.0;

        String urlTexto = element.getChildText("URL");
        if (urlTexto == null) {
            urlTexto = element.getChildText("url");
        }
        this.URL = urlTexto;   
    }

    @Override
    public Element toXMLElement() {
         Element eServicio = new Element("servicio");
        eServicio.addContent(new Element("idServicio").setText(String.valueOf(this.idServicio)));
        eServicio.addContent(new Element("nombre").setText(this.nombre != null ? this.nombre : ""));
        eServicio.addContent(new Element("descripcion").setText(this.descripcion != null ? this.descripcion : ""));
        eServicio.addContent(new Element("precio").setText(String.valueOf(this.precio)));
        eServicio.addContent(new Element("URL").setText(this.URL != null ? this.URL : ""));
        return eServicio;
    }
    
}//sin clase
