/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import org.jdom.Element;

/**
 * Representa un usuario del sistema con su información personal, credenciales y
 * datos necesarios para la autenticación.
 *
 * @author saray
 */
public class Usuario {

    private int id;
    private String nombre;
    private int rol;
    private String contrasena;
    private boolean estado;
    private String correo;

    public Usuario() {
    }

    public Usuario(int id, String nombre, int rol, String contrasena, boolean estado, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
        this.contrasena = contrasena;
        this.estado = estado;
        this.correo = correo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", nombre=" + nombre + ", rol=" + rol + ", contrasena=" + contrasena + ", estado=" + estado + ", correo=" + correo + '}';
    }

    public void toObject(Element element) {
        // 1. Detectar si el elemento recibido ya es directamente <usuario> o si viene envuelto
        Element root = element.getName().equals("usuario") ? element : element.getChild("usuario");

        if (root == null) {
            System.out.println("Error: No se encontró el nodo <usuario> en el XML recibido.");
            return;
        }

        // Leer idUsuario 
        if (root.getChild("idUsuario") != null) {
            this.id = Integer.parseInt(root.getChild("idUsuario").getValue());
        } else if (root.getChild("id") != null) {
            this.id = Integer.parseInt(root.getChild("id").getValue());
        } else {
            this.id = 0;
        }

        //Leer nombre
        if (root.getChild("nombre") != null) {
            this.nombre = root.getChild("nombre").getValue().trim();
        }

        //Leer contraseña 
        if (root.getChild("contraseina") != null) {
            this.contrasena = root.getChild("contraseina").getValue().trim();
        } else if (root.getChild("contrasena") != null) {
            this.contrasena = root.getChild("contrasena").getValue().trim();
        } else if (root.getChild("password") != null) {
            this.contrasena = root.getChild("password").getValue().trim();
        }

        // Leer correo
        if (root.getChild("correo") != null) {
            this.correo = root.getChild("correo").getValue().trim();
        } else {
            this.correo = "";
        }

        // Leer estado
        if (root.getChild("estado") != null) {
            this.estado = Boolean.parseBoolean(root.getChild("estado").getValue());
        } else {
            this.estado = true;
        }

        // Leer rol
        if (root.getChild("rol") != null) {
            this.rol = Integer.parseInt(root.getChild("rol").getValue());
        } else {
            this.rol = 0;
        }
    }

    /**
     * Método exclusivo para mapear las credenciales que el cliente envía
     * durante el inicio de sesión.
     */
    public void mapearCredencialesLogin(Element eDatos) {
        if (eDatos == null) {
            return;
        }

        // Busca el nodo credenciales, si el elemento actual ya es <credenciales>, se usa, 
        // de lo contrario, busca entre sus hijos.
        Element eCredenciales = eDatos.getName().equals("credenciales") ? eDatos : eDatos.getChild("credenciales");

        if (eCredenciales != null) {
            this.nombre = eCredenciales.getChildText("usuario") != null ? eCredenciales.getChildText("usuario").trim() : "";
            this.contrasena = eCredenciales.getChildText("contrasena") != null ? eCredenciales.getChildText("contrasena").trim() : "";
        } else {

            if (eDatos.getChild("usuario") != null) {
                this.nombre = eDatos.getChildText("usuario").trim();
            }
            if (eDatos.getChild("contrasena") != null) {
                this.contrasena = eDatos.getChildText("contrasena").trim();
            }
        }
    }

    public Element toXMLElement() {
        Element eUsuario = new Element("usuario");

        // Usamos exactamente los nombres que espera el Protocolo_Exitoso del cliente
        eUsuario.addContent(new Element("idUsuario").setText(String.valueOf(this.id)));
        eUsuario.addContent(new Element("nombre").setText(this.nombre));
        eUsuario.addContent(new Element("rol").setText(String.valueOf(this.rol)));
        eUsuario.addContent(new Element("correo").setText(this.correo));

        eUsuario.addContent(new Element("contraseina").setText(this.contrasena));

        return eUsuario;
    }

}
