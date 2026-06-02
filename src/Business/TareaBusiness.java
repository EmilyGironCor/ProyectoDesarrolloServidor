/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business;

import Data.TareaData;
import Domain.Tarea;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author saray
 */
public class TareaBusiness {

    private TareaData tareaData;

    public TareaBusiness() throws SQLException {
        this.tareaData = new TareaData();
        this.tareaData.inicializarBD();
    }

    public void insertar(Tarea tarea) throws SQLException {
        this.tareaData.insertar(tarea);
    }

    public ArrayList<Tarea> leerTodo() throws SQLException {
        return this.tareaData.obtenerTodos();
    }

    public Tarea buscarPorId(int idTarea) throws SQLException {
        return this.tareaData.obtenerPorId(idTarea);
    }

    public void actualizar(Tarea tarea) throws SQLException {
        this.tareaData.actualizar(tarea);
    }

    public void eliminar(int idTarea) throws SQLException {
        this.tareaData.eliminar(idTarea);
    }
}
