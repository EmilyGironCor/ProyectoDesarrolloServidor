package Domain;

import Business.TareaBusiness;
import Business.UsuarioBusiness;
import Data.ProductoData;
import Data.ResultadoData;
import Utility.GestionXML;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Enum del Protocolo del Servidor con respuestas activas hacia el cliente.
 */
public enum EnumProtocolo {
    LOGIN {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("Estoy en la accion LOGIN");

                Usuario usuarioIncompleto = new Usuario();
                // FIX: usar mapearCredencialesLogin en vez de toObject
                // porque el cliente envía <usuario>admin</usuario><contrasena>123</contrasena>
                // no un objeto usuario completo
                usuarioIncompleto.mapearCredencialesLogin(eDatos);

                System.out.println("Intentando verificar en BD a: " + usuarioIncompleto.getNombre());

                UsuarioBusiness ub = new UsuarioBusiness();
                Usuario usuarioReal = ub.verificarLogin(
                        usuarioIncompleto.getNombre(),
                        usuarioIncompleto.getContrasena()
                );

                DataProtocolo dp;
                if (usuarioReal != null && usuarioReal.isEstado()) {
                    System.out.println("LOGIN EXITOSO: " + usuarioReal.getNombre());
                    dp = new DataProtocolo("LOGIN_EXITOSO", usuarioReal.toXMLElement());
                } else {
                    System.out.println("LOGIN FALLIDO");
                    Element eError = new Element("error")
                            .addContent("Usuario o contraseña incorrectos");
                    dp = new DataProtocolo("LOGIN_FALLIDO", eError);
                }

                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                Element eError = new Element("error").addContent("Error interno del servidor");
                DataProtocolo dp = new DataProtocolo("LOGIN_FALLIDO", eError);
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
            }
        }

    },
    INSERTARTAREA {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("Estoy en la accion INSERTARTAREA");

                Tarea tarea = new Tarea();
                tarea.toObject(eDatos);

                Element eTarea = eDatos.getChild("tarea");

                String nombreEncargado = null;

                if (eTarea != null) {
                    nombreEncargado = eTarea.getChildText("nombreUsuarioEncargado");
                }

                if (nombreEncargado == null || nombreEncargado.trim().isEmpty()) {
                    Element eRespuesta = new Element("respuesta");
                    eRespuesta.addContent(new Element("resultado").setText("ERROR"));
                    eRespuesta.addContent(new Element("mensaje").setText("Debe indicar un usuario encargado"));

                    DataProtocolo dp = new DataProtocolo("INSERTARTAREA_RESPUESTA", eRespuesta);
                    mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
                    return;
                }

                UsuarioBusiness ub = new UsuarioBusiness();
                Usuario encargado = ub.buscarPorNombre(nombreEncargado.trim());

                if (encargado == null) {
                    Element eRespuesta = new Element("respuesta");
                    eRespuesta.addContent(new Element("resultado").setText("ERROR"));
                    eRespuesta.addContent(new Element("mensaje").setText("Usuario encargado no encontrado: " + nombreEncargado));

                    DataProtocolo dp = new DataProtocolo("INSERTARTAREA_RESPUESTA", eRespuesta);
                    mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
                    return;
                }

                tarea.setidUsuarioEncargado(encargado.getId());

                TareaBusiness tb = new TareaBusiness();
                int idTarea = tb.insertar(tarea);

                Element eRespuesta = new Element("respuesta");
                eRespuesta.addContent(new Element("resultado").setText("OK"));
                eRespuesta.addContent(new Element("mensaje").setText("Tarea registrada correctamente"));
                eRespuesta.addContent(new Element("idTarea").setText(String.valueOf(idTarea)));
                DataProtocolo dp = new DataProtocolo("INSERTARTAREA_RESPUESTA", eRespuesta);
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "INSERTARTAREA", ex.getMessage());
            }
        }

    },
    LISTARUSUARIO {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
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
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("=== ELIMINARUSUARIO ===");
                System.out.println("XML recibido: " + Utility.GestionXML.xmlToString(eDatos));

                // Buscar el idUsuario en el XML (puede estar directamente o dentro de otro nodo)
                String idUsuarioStr = null;

                // Opción 1: Directamente como hijo de <datos>
                if (eDatos.getChildText("idUsuario") != null) {
                    idUsuarioStr = eDatos.getChildText("idUsuario");
                } // Opción 2: Dentro de un nodo <usuario>
                else if (eDatos.getChild("usuario") != null) {
                    idUsuarioStr = eDatos.getChild("usuario").getChildText("idUsuario");
                } // Opción 3: Como valor directo del elemento
                else if (eDatos.getValue() != null && !eDatos.getValue().trim().isEmpty()) {
                    idUsuarioStr = eDatos.getValue().trim();
                }

                if (idUsuarioStr == null) {
                    System.err.println("ERROR: No se encontró idUsuario en el XML");
                    enviarErrorAlCliente(mCliente, "ELIMINARUSUARIO", "ID de usuario no encontrado");
                    return;
                }

                int idUsuario = Integer.parseInt(idUsuarioStr);
                System.out.println("Eliminando usuario con ID: " + idUsuario);

                UsuarioBusiness ub = new UsuarioBusiness();
                ub.eliminar(idUsuario);

                Element eExito = new Element("resultado").addContent("OK");
                DataProtocolo dp = new DataProtocolo("ELIMINARUSUARIO_RESPUESTA", eExito);
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

                System.out.println("✅ Usuario eliminado correctamente");

            } catch (SQLException ex) {
                System.err.println("Error SQL al eliminar usuario: " + ex.getMessage());
                ex.printStackTrace();
                enviarErrorAlCliente(mCliente, "ELIMINARUSUARIO", ex.getMessage());
            } catch (NumberFormatException ex) {
                System.err.println("Error: ID de usuario inválido: " + ex.getMessage());
                enviarErrorAlCliente(mCliente, "ELIMINARUSUARIO", "ID de usuario inválido");
            } catch (Exception ex) {
                System.err.println("Error general: " + ex.getMessage());
                ex.printStackTrace();
                enviarErrorAlCliente(mCliente, "ELIMINARUSUARIO", ex.getMessage());
            }
        }
    },
    LISTARTAREA {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
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
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                int idTarea = Integer.parseInt(eDatos.getChild("idTarea").getValue());

                TareaBusiness tb = new TareaBusiness();

                Tarea tarea = tb.buscarPorId(idTarea);

                if (tarea == null) {
                    Element eRespuesta = new Element("respuesta");
                    eRespuesta.addContent(new Element("resultado").setText("ERROR"));
                    eRespuesta.addContent(new Element("mensaje").setText("No existe una tarea con el ID " + idTarea));

                    DataProtocolo dp = new DataProtocolo("ELIMINARTAREA_RESPUESTA", eRespuesta);
                    mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
                    return;
                }

                tb.eliminar(idTarea);

                Element eRespuesta = new Element("respuesta");
                eRespuesta.addContent(new Element("resultado").setText("OK"));
                eRespuesta.addContent(new Element("mensaje").setText("Tarea eliminada correctamente"));

                DataProtocolo dp = new DataProtocolo("ELIMINARTAREA_RESPUESTA", eRespuesta);
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                enviarErrorAlCliente(mCliente, "ELIMINARTAREA", ex.getMessage());
            }
        }
    },
    CONSULTARTAREA {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("Estoy en la accion CONSULTARTAREA");

                int idTarea = Integer.parseInt(eDatos.getChild("idTarea").getValue());

                TareaBusiness tb = new TareaBusiness();
                Tarea tarea = tb.buscarPorId(idTarea);

                if (tarea == null) {
                    DataProtocolo dp = new DataProtocolo("CONSULTARTAREA", null);
                    mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
                    return;
                }

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
    public void accion(Cliente mCliente, Element eDatos) {
        try {
            String idTarea = eDatos.getChildText("idTarea");
            if (idTarea == null) {
                Element eTarea = eDatos.getChild("tarea");
                if (eTarea != null) {
                    idTarea = eTarea.getChildText("idTarea");
                }
            }
            
            TareaBusiness business = new TareaBusiness();
            Tarea tarea = business.buscarPorId(Integer.parseInt(idTarea));

            if (tarea == null) {
                enviarErrorAlCliente(mCliente, "ANALIZARURL", "No existe la tarea con ID " + idTarea);
                return;
            }

            // Verificar que el trabajador esté conectado
            MiClienteTrabajador trabajador = MiServidor.getTrabajador();
            if (trabajador == null) {
                enviarErrorAlCliente(mCliente, "ANALIZARURL", "No hay trabajadores disponibles");
                return;
            }

            // Enviar tarea al trabajador
            DataProtocolo dpTrabajador = new DataProtocolo("ANALIZARURL", tarea.toXMLElement());
            String xmlTrabajador = Utility.GestionXML.xmlToString(dpTrabajador.geteAccion());
            trabajador.enviarDatos(xmlTrabajador);

            // ✅ NUEVO: Responder al cliente que el análisis comenzó
            Element eRespuesta = new Element("respuesta");
            eRespuesta.addContent(new Element("nombreTarea").setText(tarea.getNombreTarea()));
            eRespuesta.addContent(new Element("URL").setText(tarea.getURL()));
            eRespuesta.addContent(new Element("estado").setText("En progreso"));
            
            DataProtocolo dpCliente = new DataProtocolo("ANALIZARURL", eRespuesta);
            mCliente.enviarDatos(Utility.GestionXML.xmlToString(dpCliente.geteAccion()));

            System.out.println("✅ Tarea enviada al trabajador y confirmación enviada al cliente");

        } catch (Exception ex) {
            ex.printStackTrace();
            enviarErrorAlCliente(mCliente, "ANALIZARURL", ex.getMessage());
        }
    }
},GUARDAR_RESULTADO {
    @Override
    public void accion(Cliente mCliente, Element eDatos) {
        try {
            System.out.println("=== GUARDAR_RESULTADO ===");
            
            Element eResultado = eDatos.getChild("resultado");
            if (eResultado == null) {
                eResultado = eDatos;
            }
            
            String idTareaStr = eResultado.getChildText("idTarea");
            String totalEnlacesStr = eResultado.getChildText("totalEnlaces");
            String totalImagenesStr = eResultado.getChildText("totalImagenes");
            String totalVideosStr = eResultado.getChildText("totalVideos");  // ← NUEVO
            String totalProductosStr = eResultado.getChildText("totalProductos");
            
            if (idTareaStr == null) {
                System.err.println("Error: No se recibió idTarea");
                return;
            }
            
            int idTarea = Integer.parseInt(idTareaStr);
            int totalEnlaces = Integer.parseInt(totalEnlacesStr != null ? totalEnlacesStr : "0");
            int totalImagenes = Integer.parseInt(totalImagenesStr != null ? totalImagenesStr : "0");
            int totalVideos = Integer.parseInt(totalVideosStr != null ? totalVideosStr : "0");  // ← NUEVO
            int totalProductos = Integer.parseInt(totalProductosStr != null ? totalProductosStr : "0");
            
            // Crear objeto Resultado
            Resultado resultado = new Resultado();
            resultado.setIdTarea(idTarea);
            resultado.setFecha(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
            resultado.setTotalEnlaces(totalEnlaces);
            resultado.setTotalImagenes(totalImagenes);
            resultado.setTotalVideos(totalVideos);  // ← NUEVO
            resultado.setTotalProductos(totalProductos);
            
            // Guardar en BD
            ResultadoData resultadoData = new ResultadoData();
            resultadoData.insertar(resultado);
            
            // Actualizar estado de la tarea
            TareaBusiness tareaBusiness = new TareaBusiness();
            Tarea tarea = tareaBusiness.buscarPorId(idTarea);
            if (tarea != null) {
                tarea.setEstado("completada");
                tareaBusiness.actualizar(tarea);
            }
            
            System.out.println(" Resultado guardado - Videos: " + totalVideos);
            
        } catch (SQLException ex) {
            Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
            enviarErrorAlCliente(mCliente, "GUARDAR_RESULTADO", ex.getMessage());
        }
    }
},
    CONSULTARRESULTADO {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            /* Pendiente Sprint 3 ESTO ES DEL CLIENTE */ }
    },
    LISTARRESULTADOS {
    @Override
    public void accion(Cliente mCliente, Element eDatos) {
        try {
            ResultadoData resultadoData = new ResultadoData();
            ArrayList<Resultado> resultados = resultadoData.obtenerTodos();
            
            Element eResultados = new Element("resultados");
            for (Resultado r : resultados) {
                Element eResultado = new Element("resultado");
                eResultado.addContent(new Element("idResultado").setText(String.valueOf(r.getIdResultado())));
                eResultado.addContent(new Element("idTarea").setText(String.valueOf(r.getIdTarea())));
                eResultado.addContent(new Element("fecha").setText(r.getFecha()));
                eResultado.addContent(new Element("totalEnlaces").setText(String.valueOf(r.getTotalEnlaces())));
                eResultado.addContent(new Element("totalImagenes").setText(String.valueOf(r.getTotalImagenes())));
                eResultado.addContent(new Element("totalProductos").setText(String.valueOf(r.getTotalProductos())));
                eResultado.addContent(new Element("totalVideos").setText(String.valueOf(r.getTotalVideos())));  // ← NUEVO
                eResultados.addContent(eResultado);
            }
            
            DataProtocolo dp = new DataProtocolo("LISTARRESULTADOS", eResultados);
            mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
            
        } catch (SQLException ex) {
            Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
            enviarErrorAlCliente(mCliente, "LISTARRESULTADOS", ex.getMessage());
        }
    }
},
    GUARDAR_PRODUCTOS {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            // 1. Obtener la lista de productos del XML
            List<Element> eProductos = eDatos.getChild("productos").getChildren();
            ProductoData productoData = new ProductoData();
            // 2. Recorrer cada producto y guardarlo en BD
            for (Object obj : eProductos) {
                Element eProducto = (Element) obj;

                // 3. Reconstruir el objeto desde XML
                Producto producto = new Producto();
                producto.toObject(eProducto);

                try {
                    // 4. Insertar en la BD usando ProductoData
                    productoData.insertar(producto);
                } catch (SQLException ex) {
                    Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Producto guardado: " + producto.getDescripcion());
            }
            // 5. Responder al trabajador que todo salió bien
            Element eRespuesta = new Element("respuesta");
            eRespuesta.addContent(new Element("resultado").setText("OK"));
            eRespuesta.addContent(new Element("mensaje").setText("Productos guardados correctamente"));
            DataProtocolo dp = new DataProtocolo("GUARDAR_PRODUCTOS_RESPUESTA", eRespuesta);
            mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));
        }
    },
    EXPORTARPDF {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            /* Pendiente Sprint 4 */ }

    }, INSERTARUSUARIO {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {

                // El cliente envía <usuario>...</usuario> dentro de <datos>
                Element eUsuario = eDatos.getChild("usuario");

                if (eUsuario == null) {
                    System.err.println("ERROR: No se encontró nodo <usuario>");
                    Element eError = new Element("exito").setText("false");
                    eError.addContent(new Element("mensaje").setText("Datos de usuario no encontrados"));
                    DataProtocolo dp = new DataProtocolo("INSERTARUSUARIO", eError);
                    mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
                    return;
                }

                // Crear objeto Usuario desde el XML
                Usuario usuario = new Usuario();
                usuario.toObject(eUsuario);

                System.out.println("Usuario a insertar:");
                System.out.println("  Nombre: " + usuario.getNombre());
                System.out.println("  Rol: " + usuario.getRol());
                System.out.println("  Correo: " + usuario.getCorreo());
                System.out.println("  Contraseña (plana): " + usuario.getContrasena());

                // Encriptar contraseña antes de insertar
                String contrasenaEncriptada = encriptarSHA1(usuario.getContrasena());
                usuario.setContrasena(contrasenaEncriptada);
                System.out.println("  Contraseña (encriptada): " + contrasenaEncriptada);

                // Insertar en BD
                UsuarioBusiness ub = new UsuarioBusiness();
                ub.insertar(usuario);

                // Responder éxito al cliente
                Element eRespuesta = new Element("exito").setText("true");
                eRespuesta.addContent(new Element("mensaje").setText("Usuario creado exitosamente"));
                DataProtocolo dp = new DataProtocolo("INSERTARUSUARIO", eRespuesta);
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

                System.out.println(" Usuario insertado correctamente");

            } catch (SQLException ex) {
                System.err.println("Error SQL al insertar usuario: " + ex.getMessage());
                ex.printStackTrace();

                Element eError = new Element("exito").setText("false");
                if (ex.getMessage().contains("UNIQUE") || ex.getMessage().contains("unique")) {
                    eError.addContent(new Element("mensaje").setText("El nombre de usuario ya existe"));
                } else {
                    eError.addContent(new Element("mensaje").setText("Error en base de datos: " + ex.getMessage()));
                }
                DataProtocolo dp = new DataProtocolo("INSERTARUSUARIO", eError);
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

            } catch (Exception ex) {
                System.err.println("Error general: " + ex.getMessage());
                ex.printStackTrace();

                Element eError = new Element("exito").setText("false");
                eError.addContent(new Element("mensaje").setText("Error: " + ex.getMessage()));
                DataProtocolo dp = new DataProtocolo("INSERTARUSUARIO", eError);
                mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
            }
        }
    }, BUSCARUSUARIO {
        @Override
        public void accion(Cliente mcliente, Element eDatos) {
            try {
                System.out.println("=== BUSCARUSUARIO ===");

                String nombre = eDatos.getChildText("nombre");

                if (nombre == null || nombre.trim().isEmpty()) {
                    Element eRespuesta = new Element("encontrado").setText("false");
                    eRespuesta.addContent(new Element("mensaje").setText("Nombre no proporcionado"));
                    DataProtocolo dp = new DataProtocolo("BUSCARUSUARIO", eRespuesta);
                    mcliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
                    return;
                }

                System.out.println("Buscando usuario: " + nombre);

                UsuarioBusiness ub = new UsuarioBusiness();
                Usuario usuario = ub.buscarPorNombre(nombre);

                Element eRespuesta = new Element("respuesta");

                if (usuario != null) {
                    System.out.println("✅ Usuario encontrado: " + usuario.getNombre());
                    eRespuesta.addContent(new Element("encontrado").setText("true"));
                    eRespuesta.addContent(usuario.toXMLElement());  // <usuario> va aparte
                } else {
                    System.out.println("❌ Usuario no encontrado: " + nombre);
                    eRespuesta.addContent(new Element("encontrado").setText("false"));
                    eRespuesta.addContent(new Element("mensaje").setText("Usuario no encontrado"));
                }

                DataProtocolo dp = new DataProtocolo("BUSCARUSUARIO", eRespuesta);
                mcliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                System.err.println("Error SQL: " + ex.getMessage());
                ex.printStackTrace();
                Element eError = new Element("respuesta");
                eError.addContent(new Element("encontrado").setText("false"));
                eError.addContent(new Element("error").setText(ex.getMessage()));
                DataProtocolo dp = new DataProtocolo("BUSCARUSUARIO", eError);
                mcliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
            }
        }
    };

    public abstract void accion(Cliente mCliente, Element eDatos);

    // Método utilitario interno para reportarle fallos de base de datos de manera limpia al cliente
    private static void enviarErrorAlCliente(Cliente mCliente, String accion, String mensajeError) {
        Element eError = new Element("error").addContent(mensajeError);
        DataProtocolo dp = new DataProtocolo(accion + "_ERROR", eError);
        mCliente.enviarDatos(Utility.GestionXML.xmlToString(dp.geteAccion()));
    }

    private static String encriptarSHA1(String texto) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(texto.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            System.out.println("Error al encriptar: " + ex.getMessage());
            return texto;
        }
    }
}
