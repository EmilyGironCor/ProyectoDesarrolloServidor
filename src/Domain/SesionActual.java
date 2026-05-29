/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Domain;

import Domain.Usuario;
import Domain.Rol;

/**
 *
 * @author saray
 */
public class SesionActual {
    private SesionActual instancia;
    private Usuario usuarioActivo;
    private Rol rolActivo;

    public SesionActual getInstancia() {
        return instancia;
    }

    public void setInstancia(SesionActual instancia) {
        this.instancia = instancia;
    }

    public Usuario getUsuarioActivo() {
        return usuarioActivo;
    }

    public void setUsuarioActivo(Usuario usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
    }

    public Rol getRolActivo() {
        return rolActivo;
    }

    public void setRolActivo(Rol rolActivo) {
        this.rolActivo = rolActivo;
    }
    
    public void cerrarSesion(){
        
    }
}
