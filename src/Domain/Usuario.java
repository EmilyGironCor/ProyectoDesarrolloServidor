/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import Domain.Rol;

/**
 *
 * @author saray
 */
public class Usuario {
    private int id;
    private String nombre;
    private Rol rol;
    private String contrasena;
    private boolean estado;
    private String correo;

    public Usuario(int id, String nombre, Rol rol, String contrasena, boolean estado, String correo) {
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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
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

    
    
    
}
