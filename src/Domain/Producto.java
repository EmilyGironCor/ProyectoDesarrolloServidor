/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jdom.Element;

/**
 *
 * @author saray
 */
public class Producto implements XMLConvertible {

    private int idProducto;
    private double precio;
    private String descripcion;
    private BufferedImage imagen;
    private String URL;

    public Producto(int idProducto, double precio, String descripcion, BufferedImage imagen, String URL) {
        this.idProducto = idProducto;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.URL = URL;
    }

    public Producto() {
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BufferedImage getImagen() {
        return imagen;
    }

    public void setImagen(BufferedImage imagen) {
        this.imagen = imagen;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Override
    public String toString() {
        return "Producto{" + "idProducto=" + idProducto + ", precio=" + precio + ", descripcion=" + descripcion + ", imagen=" + imagen + ", URL=" + URL + '}';
    }

    @Override
    public void toObject(Element element) {
        this.idProducto = Integer.parseInt(element.getChildText("idProducto"));
        this.precio = Double.parseDouble(element.getChildText("precio"));
        this.descripcion = element.getChildText("descripcion");
        this.URL = element.getChildText("URL");
        
        String urlTexto = element.getChildText("URL");

if (urlTexto == null) {
    urlTexto = element.getChildText("url");
}

this.URL = urlTexto;

        
        String imagenBase64 = element.getChildText("imagen");
        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
            byte[] bytes = Base64.getDecoder().decode(imagenBase64);
            try {
                this.imagen = ImageIO.read(new ByteArrayInputStream(bytes));
            } catch (IOException ex) {
                Logger.getLogger(Producto.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            this.imagen = null;
        }
    }

    @Override
    public Element toXMLElement() {
        Element eProducto = new Element("producto");

        eProducto.addContent(new Element("idProducto")
                .setText(String.valueOf(this.idProducto)));
        eProducto.addContent(new Element("precio")
                .setText(String.valueOf(this.precio)));
        eProducto.addContent(new Element("descripcion")
                .setText(this.descripcion));
        eProducto.addContent(new Element("URL")
                .setText(this.URL));

        // Imagen a Base64 para viajar en XML
        if (this.imagen != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(this.imagen, "png", baos);
                String imagenBase64 = Base64.getEncoder()
                        .encodeToString(baos.toByteArray());
                eProducto.addContent(new Element("imagen")
                        .setText(imagenBase64));
            } catch (IOException ex) {
                Logger.getLogger(Producto.class.getName()).log(Level.SEVERE, null, ex);
                eProducto.addContent(new Element("imagen").setText(""));
            }
        } else {
            eProducto.addContent(new Element("imagen").setText(""));
        }

        return eProducto;
    }
}//fin clase
