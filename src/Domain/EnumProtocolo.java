/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
//package Domain;
//
//import Business.TareaBusiness;
//import Business.UsuarioBusiness;
//import Utility.GestionXML;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import org.jdom.Element;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author saray
// */
//public enum EnumProtocolo {
//    LOGIN {
//        public void accion(MiCliente mCliente, Element eDatos) {
//
//            try {
//
//                System.out.println("Estoy en la accion LOGIN");
//
//                Usuario usuario = new Usuario();
//                usuario.toObject(eDatos);
//
//                UsuarioBusiness ub = new UsuarioBusiness();
//
//                //validacion de login
//            } catch (SQLException ex) {
//                Logger.getLogger(EnumProtocolo.class.getName())
//                        .log(Level.SEVERE, null, ex);
//            }
//        }
//    },
//    INSERTARTAREA {
//        public void accion(MiCliente mCliente, Element eDatos) {
//
//            try {
//
//                System.out.println("Estoy en la accion INSERTARTAREA");
//
//                Tarea tarea = new Tarea();
//
//                tarea.toObject(eDatos);
//
//                TareaBusiness tb = new TareaBusiness();
//
//                tb.insertar(tarea);
//
//            } catch (SQLException ex) {
//                Logger.getLogger(EnumProtocolo.class.getName())
//                        .log(Level.SEVERE, null, ex);
//            }
//        }
//    },
//    LISTARUSUARIO {
//        public void accion(MiCliente mCliente, Element eDatos) {
//            try {
//                System.out.println("Estoy en la accion LISTARUSUARIO");
//
//                UsuarioBusiness ub = new UsuarioBusiness();
//
//                ArrayList<Usuario> usuarios = ub.leerTodo();
//
//                Element eUsuarios = new Element("usuarios");
//
//                for (Usuario u : usuarios) {
//                    eUsuarios.addContent(u.toXMLElement());
//                }
//
//                DataProtocolo dp = new DataProtocolo(
//                        "LISTARUSUARIO",
//                        eUsuarios
//                );
//
//                mCliente.enviarDatos(
//                        GestionXML.xmlToString(dp.geteAccion())
//                );
//
//            } catch (SQLException ex) {
//                Logger.getLogger(EnumProtocolo.class.getName())
//                        .log(Level.SEVERE, null, ex);
//            }
//        }
//    },
//    ELIMINARUSUARIO {
//        public void accion(MiCliente mCliente, Element eDatos) {
//            try {
//                System.out.println("Estoy en la accion ELIMINARUSUARIO");
//
//                int idUsuario = Integer.parseInt(
//                        eDatos.getChild("idUsuario").getValue()
//                );
//
//                UsuarioBusiness ub = new UsuarioBusiness();
//                ub.eliminar(idUsuario);
//
//            } catch (SQLException ex) {
//                Logger.getLogger(EnumProtocolo.class.getName())
//                        .log(Level.SEVERE, null, ex);
//            }
//        }
//    },
//    LISTARTAREA {
//        public void accion(MiCliente mCliente, Element eDatos) {
//            try {
//                System.out.println("Estoy en la accion LISTARTAREA");
//
//                TareaBusiness tb = new TareaBusiness();
//
//                ArrayList<Tarea> tareas = tb.leerTodo();
//
//                Element eTareas = new Element("tareas");
//
//                for (Tarea t : tareas) {
//                    eTareas.addContent(t.toXMLElement());
//                }
//
//                DataProtocolo dp = new DataProtocolo(
//                        "LISTARTAREA",
//                        eTareas
//                );
//
//                mCliente.enviarDatos(
//                        GestionXML.xmlToString(dp.geteAccion())
//                );
//
//            } catch (SQLException ex) {
//                Logger.getLogger(EnumProtocolo.class.getName())
//                        .log(Level.SEVERE, null, ex);
//            }
//        }
//    },
//    ELIMINARTAREA {
//        public void accion(MiCliente mCliente, Element eDatos) {
//            try {
//                System.out.println("Estoy en la accion ELIMINARTAREA");
//
//                int idTarea = Integer.parseInt(
//                        eDatos.getChild("idTarea").getValue()
//                );
//
//                TareaBusiness tb = new TareaBusiness();
//
//                tb.eliminar(idTarea);
//
//            } catch (SQLException ex) {
//                Logger.getLogger(EnumProtocolo.class.getName())
//                        .log(Level.SEVERE, null, ex);
//            }
//        }
//    },
//    CONSULTARTAREA {
//        public void accion(MiCliente mCliente, Element eDatos) {
//            try {
//                System.out.println("Estoy en la accion CONSULTARTAREA");
//
//                int idTarea = Integer.parseInt(
//                        eDatos.getChild("idTarea").getValue()
//                );
//
//                TareaBusiness tb = new TareaBusiness();
//
//                Tarea tarea = tb.buscarPorId(idTarea);
//
//                DataProtocolo dp = new DataProtocolo(
//                        "CONSULTARTAREA",
//                        tarea.toXMLElement()
//                );
//
//                mCliente.enviarDatos(
//                        GestionXML.xmlToString(dp.geteAccion())
//                );
//
//            } catch (SQLException ex) {
//                Logger.getLogger(EnumProtocolo.class.getName())
//                        .log(Level.SEVERE, null, ex);
//            }
//        }
//    },
//    ANALIZARURL {
//        public void accion(MiCliente mCliente, Element eDatos) {
//
//        }
//    },
//    CONSULTARRESULTADI {
//        public void accion(MiCliente mCliente, Element eDatos) {
//
//        }
//    },
//    LISTARRESULTADOS {
//        public void accion(MiCliente mCliente, Element eDatos) {
//
//        }
//    },
//    EXPORTARPDF {
//        public void accion(MiCliente mCliente, Element eDatos) {
//
//        }    
//    };
//
//    public abstract void accion(MiCliente mCliente, Element eDatos);
//}

package Domain;

import Business.TareaBusiness;
import Business.UsuarioBusiness;
import Utility.GestionXML;
import java.sql.SQLException;
import java.util.ArrayList;
import org.jdom.Element;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Enum del Protocolo del Servidor con respuestas activas hacia el cliente.
 */
public enum EnumProtocolo {
    LOGIN {
    @Override
    public void accion(MiCliente mCliente, Element eDatos) {
        try {
            System.out.println("Estoy en la accion LOGIN");

            Usuario usuarioIncompleto = new Usuario();
            // toObject procesará el eDatos y extraerá <nombre> y <contraseina> perfectamente
            usuarioIncompleto.toObject(eDatos); 

            System.out.println("Intentando verificar en BD a: " + usuarioIncompleto.getNombre());

            UsuarioBusiness ub = new UsuarioBusiness();
            Usuario usuarioReal = ub.verificarLogin(usuarioIncompleto.getNombre(), usuarioIncompleto.getContrasena());

            DataProtocolo dp;
            if (usuarioReal != null && usuarioReal.isEstado()) {
                System.out.println("¡LOGIN EXITOSO! Enviando datos de: " + usuarioReal.getNombre());
                // Construye la respuesta exitosa con las etiquetas sincronizadas
                dp = new DataProtocolo("LOGIN_EXITOSO", usuarioReal.toXMLElement());
            } else {
                System.out.println("LOGIN FALLIDO: Usuario no encontrado o contraseña incorrecta.");
                Element eError = new Element("error").addContent("Usuario o contraseña incorrectos");
                dp = new DataProtocolo("LOGIN_FALLIDO", eError);
            }

            mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(EnumProtocolo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            Element eError = new Element("error").addContent("Error interno del servidor");
            DataProtocolo dp = new DataProtocolo("LOGIN_FALLIDO", eError);
            mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
        }
    }
},
    INSERTARTAREA {
        @Override
        public void accion(MiCliente mCliente, Element eDatos) {
            try {
                System.out.println("Estoy en la accion INSERTARTAREA");

                Tarea tarea = new Tarea();
                tarea.toObject(eDatos);

                TareaBusiness tb = new TareaBusiness();
                tb.insertar(tarea);

                // Notificar al cliente que la tarea se guardó correctamente en la BD
                Element eExito = new Element("resultado").addContent("OK");
                DataProtocolo dp = new DataProtocolo("INSERTARTAREA_RESPUESTA", eExito);
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "INSERTARTAREA", ex.getMessage());
            }
        }
    },
    LISTARUSUARIO {
        @Override
        public void accion(MiCliente mCliente, Element eDatos) {
            try {
                System.out.println("Estoy en la accion LISTARUSUARIO");

                UsuarioBusiness ub = new UsuarioBusiness();
                ArrayList<Usuario> usuarios = ub.leerTodo();

                Element eUsuarios = new Element("usuarios");
                for (Usuario u : usuarios) {
                    eUsuarios.addContent(u.toXMLElement());
                }

                DataProtocolo dp = new DataProtocolo("LISTARUSUARIO", eUsuarios);
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "LISTARUSUARIO", ex.getMessage());
            }
        }
    },
    ELIMINARUSUARIO {
        @Override
        public void accion(MiCliente mCliente, Element eDatos) {
            try {
                System.out.println("Estoy en la accion ELIMINARUSUARIO");

                int idUsuario = Integer.parseInt(eDatos.getChild("idUsuario").getValue());

                UsuarioBusiness ub = new UsuarioBusiness();
                ub.eliminar(idUsuario);

                Element eExito = new Element("resultado").addContent("OK");
                DataProtocolo dp = new DataProtocolo("ELIMINARUSUARIO_RESPUESTA", eExito);
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "ELIMINARUSUARIO", ex.getMessage());
            }
        }
    },
    LISTARTAREA {
        @Override
        public void accion(MiCliente mCliente, Element eDatos) {
            try {
                System.out.println("Estoy en la accion LISTARTAREA");

                TareaBusiness tb = new TareaBusiness();
                ArrayList<Tarea> tareas = tb.leerTodo();

                Element eTareas = new Element("tareas");
                for (Tarea t : tareas) {
                    eTareas.addContent(t.toXMLElement());
                }

                DataProtocolo dp = new DataProtocolo("LISTARTAREA", eTareas);
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "LISTARTAREA", ex.getMessage());
            }
        }
    },
    ELIMINARTAREA {
        @Override
        public void accion(MiCliente mCliente, Element eDatos) {
            try {
                System.out.println("Estoy en la accion ELIMINARTAREA");

                int idTarea = Integer.parseInt(eDatos.getChild("idTarea").getValue());

                TareaBusiness tb = new TareaBusiness();
                tb.eliminar(idTarea);

                Element eExito = new Element("resultado").addContent("OK");
                DataProtocolo dp = new DataProtocolo("ELIMINARTAREA_RESPUESTA", eExito);
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "ELIMINARTAREA", ex.getMessage());
            }
        }
    },
    CONSULTARTAREA {
        @Override
        public void accion(MiCliente mCliente, Element eDatos) {
            try {
                System.out.println("Estoy en la accion CONSULTARTAREA");

                int idTarea = Integer.parseInt(eDatos.getChild("idTarea").getValue());

                TareaBusiness tb = new TareaBusiness();
                Tarea tarea = tb.buscarPorId(idTarea);

                DataProtocolo dp = new DataProtocolo("CONSULTARTAREA", tarea.toXMLElement());
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "CONSULTARTAREA", ex.getMessage());
            }
        }
    },
    ANALIZARURL {
        @Override
        public void accion(MiCliente mCliente, Element eDatos) { /* Pendiente Sprint 3 */ }
    },
    CONSULTARRESULTADI {
        @Override
        public void accion(MiCliente mCliente, Element eDatos) { /* Pendiente Sprint 3 */ }
    },
    LISTARRESULTADOS {
        @Override
        public void accion(MiCliente mCliente, Element eDatos) { /* Pendiente Sprint 3 */ }
    },
    EXPORTARPDF {
        @Override
        public void accion(MiCliente mCliente, Element eDatos) { /* Pendiente Sprint 4 */ }    
    };

    public abstract void accion(MiCliente mCliente, Element eDatos);

    // Método utilitario interno para reportarle fallos de base de datos de manera limpia al cliente
    private static void enviarErrorAlCliente(MiCliente mCliente, String accion, String mensajeError) {
        Element eError = new Element("error").addContent(mensajeError);
        DataProtocolo dp = new DataProtocolo(accion + "_ERROR", eError);
        mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
    }
}