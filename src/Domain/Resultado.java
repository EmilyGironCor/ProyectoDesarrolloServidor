package Domain;

import org.jdom.Element;

/**
 *
 * @author saray
 */
public class Resultado implements XMLConvertible {
    private int idResultado;
    private int idTarea;
    private String fecha;
    private int totalImagenes;
    private int totalEnlaces;
    private int totalProductos;
    private int totalVideos;
    private int totalServicios;

    // Constructor vacío
    public Resultado() {
    }

    public Resultado(int idResultado, int idTarea, String fecha, int totalImagenes, int totalEnlaces, int totalProductos, int totalVideos, int totalServicios) {
        this.idResultado = idResultado;
        this.idTarea = idTarea;
        this.fecha = fecha;
        this.totalImagenes = totalImagenes;
        this.totalEnlaces = totalEnlaces;
        this.totalProductos = totalProductos;
        this.totalVideos = totalVideos;
        this.totalServicios = totalServicios;
    }

  

    // Getters y Setters
    public int getIdResultado() {
        return idResultado;
    }

    public void setIdResultado(int idResultado) {
        this.idResultado = idResultado;
    }

    public int getTotalServicios() {
        return totalServicios;
    }

    public void setTotalServicios(int totalServicios) {
        this.totalServicios = totalServicios;
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

    public int getTotalVideos() {
        return totalVideos;
    }

    public void setTotalVideos(int totalVideos) {
        this.totalVideos = totalVideos;
    }

    // ==================== MÉTODOS XML ====================
    
    @Override
    public void toObject(Element element) {
        // Buscar el nodo resultado (puede estar directamente o dentro de otro nodo)
        Element eResultado = element.getName().equals("resultado") 
                ? element 
                : element.getChild("resultado");
        
        if (eResultado == null) {
            System.out.println("Error: No se encontró el nodo <resultado> en el XML");
            return;
        }
        
        if (eResultado.getChildText("idResultado") != null) {
            this.idResultado = Integer.parseInt(eResultado.getChildText("idResultado"));
        }
        if (eResultado.getChildText("idTarea") != null) {
            this.idTarea = Integer.parseInt(eResultado.getChildText("idTarea"));
        }
        if (eResultado.getChildText("fecha") != null) {
            this.fecha = eResultado.getChildText("fecha");
        }
        if (eResultado.getChildText("totalImagenes") != null) {
            this.totalImagenes = Integer.parseInt(eResultado.getChildText("totalImagenes"));
        }
        if (eResultado.getChildText("totalEnlaces") != null) {
            this.totalEnlaces = Integer.parseInt(eResultado.getChildText("totalEnlaces"));
        }
        if (eResultado.getChildText("totalProductos") != null) {
            this.totalProductos = Integer.parseInt(eResultado.getChildText("totalProductos"));
        }
        if (eResultado.getChildText("totalVideos") != null) {
            this.totalVideos = Integer.parseInt(eResultado.getChildText("totalVideos"));
        }
    }

    @Override
    public Element toXMLElement() {
        Element eResultado = new Element("resultado");
        
        eResultado.addContent(new Element("idResultado").setText(String.valueOf(this.idResultado)));
        eResultado.addContent(new Element("idTarea").setText(String.valueOf(this.idTarea)));
        eResultado.addContent(new Element("fecha").setText(this.fecha != null ? this.fecha : ""));
        eResultado.addContent(new Element("totalImagenes").setText(String.valueOf(this.totalImagenes)));
        eResultado.addContent(new Element("totalEnlaces").setText(String.valueOf(this.totalEnlaces)));
        eResultado.addContent(new Element("totalProductos").setText(String.valueOf(this.totalProductos)));
        eResultado.addContent(new Element("totalVideos").setText(String.valueOf(this.totalVideos)));
        
        return eResultado;
    }

    @Override
    public String toString() {
        return "Resultado{" + "idResultado=" + idResultado 
                + ", idTarea=" + idTarea 
                + ", fecha=" + fecha 
                + ", totalImagenes=" + totalImagenes 
                + ", totalEnlaces=" + totalEnlaces 
                + ", totalProductos=" + totalProductos 
                + ", totalVideos=" + totalVideos + '}';
    }
}