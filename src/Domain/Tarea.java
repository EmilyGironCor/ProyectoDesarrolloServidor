/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import java.util.ArrayList;
import org.jdom.Element;

/**
 * Representa una tarea de análisis con sus URLs, opciones de procesamiento y
 * métodos de conversión entre Java y XML.
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
    private String descripcion;

    private ArrayList<String> urls;
    private boolean analizarImagenes;
    private boolean analizarVideos;
    private boolean analizarLinks;
    private boolean analizarProductos;
    private boolean analizarServicios;

    // Constructores
    public Tarea(String url) {
        this.URL = url;
        this.urls = new ArrayList<>();
        if (url != null && !url.isEmpty()) {
            this.urls.add(url);
        }
        this.analizarImagenes = true;
        this.analizarVideos = true;
        this.analizarLinks = true;
        this.analizarProductos = false;
        this.analizarServicios = false;
    }

    public Tarea() {
        this.urls = new ArrayList<>();
        this.analizarImagenes = true;
        this.analizarVideos = true;
        this.analizarLinks = true;
        this.analizarProductos = false;
        this.analizarServicios = false;
    }

    public Tarea(int idTarea, String nombreTarea, String URL, String estado,
            int idUsuarioEncargado, int prioridad, String descripcion) {
        this.idTarea = idTarea;
        this.nombreTarea = nombreTarea;
        this.URL = URL;
        this.estado = estado;
        this.idUsuarioEncargado = idUsuarioEncargado;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.urls = new ArrayList<>();
        if (URL != null && !URL.isEmpty()) {
            this.urls.add(URL);
        }
        this.analizarImagenes = true;
        this.analizarVideos = true;
        this.analizarLinks = true;
        this.analizarProductos = false;
        this.analizarServicios = false;
    }

    // Constructor completo con todas las opciones
    public Tarea(int idTarea, String nombreTarea, ArrayList<String> urls, String estado,
            int idUsuarioEncargado, int prioridad, String descripcion,
            boolean analizarImagenes, boolean analizarVideos,
            boolean analizarLinks, boolean analizarProductos, boolean analizarServicios) {
        this.idTarea = idTarea;
        this.nombreTarea = nombreTarea;
        this.urls = urls != null ? urls : new ArrayList<>();
        this.estado = estado;
        this.idUsuarioEncargado = idUsuarioEncargado;
        this.prioridad = prioridad;
        this.descripcion = descripcion;
        this.analizarImagenes = analizarImagenes;
        this.analizarVideos = analizarVideos;
        this.analizarLinks = analizarLinks;
        this.analizarProductos = analizarProductos;
        this.analizarServicios = analizarServicios;

        // Mantener URL por compatibilidad (primera URL)
        if (!this.urls.isEmpty()) {
            this.URL = this.urls.get(0);
        }
    }

    // Getters y Setters originales
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
        // Mantener sincronizada la lista de URLs
        if (this.urls == null) {
            this.urls = new ArrayList<>();
        }
        if (!this.urls.contains(URL) && URL != null && !URL.isEmpty()) {
            this.urls.add(0, URL);
        }
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

    public int getidUsuarioEncargado() {
        return idUsuarioEncargado;
    }

    public void setidUsuarioEncargado(int dUsuarioEncargado) {
        this.idUsuarioEncargado = dUsuarioEncargado;
    }

    // NUEVOS GETTERS Y SETTERS
    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
        if (urls != null && !urls.isEmpty()) {
            this.URL = urls.get(0);
        }
    }

    public void addUrl(String url) {
        if (this.urls == null) {
            this.urls = new ArrayList<>();
        }
        this.urls.add(url);
        if (this.URL == null || this.URL.isEmpty()) {
            this.URL = url;
        }
    }

    public boolean isAnalizarImagenes() {
        return analizarImagenes;
    }

    public void setAnalizarImagenes(boolean analizarImagenes) {
        this.analizarImagenes = analizarImagenes;
    }

    public boolean isAnalizarVideos() {
        return analizarVideos;
    }

    public void setAnalizarVideos(boolean analizarVideos) {
        this.analizarVideos = analizarVideos;
    }

    public boolean isAnalizarLinks() {
        return analizarLinks;
    }

    public void setAnalizarLinks(boolean analizarLinks) {
        this.analizarLinks = analizarLinks;
    }

    public boolean isAnalizarProductos() {
        return analizarProductos;
    }

    public void setAnalizarProductos(boolean analizarProductos) {
        this.analizarProductos = analizarProductos;
    }

    public boolean isAnalizarServicios() {
        return analizarServicios;
    }

    public void setAnalizarServicios(boolean analizarServicios) {
        this.analizarServicios = analizarServicios;
    }

    @Override
    public String toString() {
        return "Tarea{" + "idTarea=" + idTarea + ", nombreTarea=" + nombreTarea
                + ", urls=" + (urls != null ? urls.size() : 0)
                + ", estado=" + estado + ", idUsuarioCreador=" + idUsuarioEncargado
                + ", prioridad=" + prioridad + '}';
    }

    @Override
    public void toObject(Element element) {
        // Detectar si ya somos <tarea> o si hay que buscarlo dentro de <datos>
        Element root = element.getName().equals("tarea")
                ? element
                : element.getChild("tarea");

        if (root == null) {
            System.out.println("Error: No se encontró el nodo <tarea> en el XML recibido.");
            return;
        }

        // idTarea
        if (root.getChildText("idTarea") != null && !root.getChildText("idTarea").isEmpty()) {
            try {
                this.idTarea = Integer.parseInt(root.getChildText("idTarea"));
            } catch (NumberFormatException e) {
                this.idTarea = 0;
            }
        }

        // nombreTarea
        if (root.getChildText("nombreTarea") != null) {
            this.nombreTarea = root.getChildText("nombreTarea");
        }

        // URL (compatibilidad)
        if (root.getChildText("URL") != null) {
            this.URL = root.getChildText("URL");
        }

        // LEER LISTA DE URLs 
        Element eUrls = root.getChild("urls");
        if (eUrls != null) {
            this.urls = new ArrayList<>();
            for (Object obj : eUrls.getChildren("url")) {
                Element eUrl = (Element) obj;
                String urlValue = eUrl.getValue();
                if (urlValue != null && !urlValue.isEmpty()) {
                    this.urls.add(urlValue);
                }
            }
            // Si no se encontró la lista pero hay URL individual
        } else if (this.URL != null && !this.URL.isEmpty()) {
            this.urls = new ArrayList<>();
            this.urls.add(this.URL);
        } else {
            this.urls = new ArrayList<>();
        }

        // estado
        if (root.getChildText("estado") != null) {
            this.estado = root.getChildText("estado");
        } else {
            this.estado = "pendiente";
        }

        // idUsuarioCreador
        if (root.getChildText("idUsuarioCreador") != null) {
            try {
                this.idUsuarioEncargado = Integer.parseInt(root.getChildText("idUsuarioCreador"));
            } catch (NumberFormatException e) {
                this.idUsuarioEncargado = 0;
            }
        }

        // prioridad
        if (root.getChildText("prioridad") != null) {
            try {
                this.prioridad = Integer.parseInt(root.getChildText("prioridad"));
            } catch (NumberFormatException e) {
                this.prioridad = 5;
            }
        }

        // descripcion
        if (root.getChildText("descripcion") != null) {
            this.descripcion = root.getChildText("descripcion");
        }

        // LEER OPCIONES DE ANÁLISIS
        Element eOpciones = root.getChild("opcionesAnalisis");
        if (eOpciones != null) {
            String img = eOpciones.getChildText("analizarImagenes");
            if (img != null) {
                this.analizarImagenes = Boolean.parseBoolean(img);
            }

            String vid = eOpciones.getChildText("analizarVideos");
            if (vid != null) {
                this.analizarVideos = Boolean.parseBoolean(vid);
            }

            String link = eOpciones.getChildText("analizarLinks");
            if (link != null) {
                this.analizarLinks = Boolean.parseBoolean(link);
            }

            String prod = eOpciones.getChildText("analizarProductos");
            if (prod != null) {
                this.analizarProductos = Boolean.parseBoolean(prod);
            }

            String serv = eOpciones.getChildText("analizarServicios");
            if (serv != null) {
                this.analizarServicios = Boolean.parseBoolean(serv);
            }
        }
    }

    @Override
    public Element toXMLElement() {
        Element eTarea = new Element("tarea");

        // Campos básicos
        eTarea.addContent(new Element("idTarea").setText(String.valueOf(this.idTarea)));
        eTarea.addContent(new Element("nombreTarea").setText(this.nombreTarea != null ? this.nombreTarea : ""));
        eTarea.addContent(new Element("estado").setText(this.estado != null ? this.estado : "pendiente"));
        eTarea.addContent(new Element("idUsuarioCreador").setText(String.valueOf(this.idUsuarioEncargado)));
        eTarea.addContent(new Element("prioridad").setText(String.valueOf(this.prioridad)));
        eTarea.addContent(new Element("descripcion").setText(this.descripcion != null ? this.descripcion : ""));

        // ENVIAR LISTA DE URLs
        Element eUrls = new Element("urls");
        if (this.urls != null) {
            for (String url : this.urls) {
                if (url != null && !url.isEmpty()) {
                    eUrls.addContent(new Element("url").setText(url));
                }
            }
        } else if (this.URL != null && !this.URL.isEmpty()) {
            eUrls.addContent(new Element("url").setText(this.URL));
        }
        eTarea.addContent(eUrls);

        // ENVIAR OPCIONES DE ANÁLISIS
        Element eOpciones = new Element("opcionesAnalisis");
        eOpciones.addContent(new Element("analizarImagenes").setText(String.valueOf(analizarImagenes)));
        eOpciones.addContent(new Element("analizarVideos").setText(String.valueOf(analizarVideos)));
        eOpciones.addContent(new Element("analizarLinks").setText(String.valueOf(analizarLinks)));
        eOpciones.addContent(new Element("analizarProductos").setText(String.valueOf(analizarProductos)));
        eOpciones.addContent(new Element("analizarServicios").setText(String.valueOf(analizarServicios)));
        eTarea.addContent(eOpciones);

        return eTarea;
    }
}
