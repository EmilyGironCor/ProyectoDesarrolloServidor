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

public enum EnumProtocolo {

    LOGIN {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("LOGIN recibido");

                Usuario usuarioIncompleto = new Usuario();
                usuarioIncompleto.mapearCredencialesLogin(eDatos);

                System.out.println("Verificando en BD: " + usuarioIncompleto.getNombre());

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
                    Element eError = new Element("error").addContent("Usuario o contraseña incorrectos");
                    dp = new DataProtocolo("LOGIN_FALLIDO", eError);
                }

                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                Element eError = new Element("error").addContent("Error interno del servidor");
                DataProtocolo dp = new DataProtocolo("LOGIN_FALLIDO", eError);
                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));
            }
        }
    },
    
  INSERTARTAREA {
    @Override
    public void accion(Cliente mCliente, Element eDatos) {
        try {
            System.out.println("INSERTARTAREA recibido");

            Tarea tarea = new Tarea();
            tarea.toObject(eDatos);

            // Leer el usuario encargado (puede venir por nombre o por ID)
            Element eTarea = eDatos.getChild("tarea");
            String nombreEncargado = null;
            int idEncargado = 0;

            if (eTarea != null) {
                nombreEncargado = eTarea.getChildText("nombreUsuarioEncargado");
                String idEncargadoStr = eTarea.getChildText("idUsuarioCreador");
                if (idEncargadoStr != null && !idEncargadoStr.isEmpty()) {
                    try {
                        idEncargado = Integer.parseInt(idEncargadoStr);
                    } catch (NumberFormatException e) {
                        idEncargado = 0;
                    }
                }
            }

            UsuarioBusiness ub = new UsuarioBusiness();
            Usuario encargado = null;

            if (nombreEncargado != null && !nombreEncargado.trim().isEmpty()) {
                encargado = ub.buscarPorNombre(nombreEncargado.trim());
                if (encargado == null) {
                    Element eRespuesta = new Element("respuesta");
                    eRespuesta.addContent(new Element("resultado").setText("ERROR"));
                    eRespuesta.addContent(new Element("mensaje").setText("Usuario encargado no encontrado: " + nombreEncargado));
                    DataProtocolo dp = new DataProtocolo("INSERTARTAREA_RESPUESTA", eRespuesta);
                    mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));
                    return;
                }
                tarea.setIdUsuarioCreador(encargado.getId());
            } 
            else if (idEncargado > 0) {
                encargado = ub.buscarPorId(idEncargado);
                if (encargado == null) {
                    Element eRespuesta = new Element("respuesta");
                    eRespuesta.addContent(new Element("resultado").setText("ERROR"));
                    eRespuesta.addContent(new Element("mensaje").setText("Usuario encargado no encontrado con ID: " + idEncargado));
                    DataProtocolo dp = new DataProtocolo("INSERTARTAREA_RESPUESTA", eRespuesta);
                    mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));
                    return;
                }
                tarea.setIdUsuarioCreador(encargado.getId());
            }
            else {
                Element eRespuesta = new Element("respuesta");
                eRespuesta.addContent(new Element("resultado").setText("ERROR"));
                eRespuesta.addContent(new Element("mensaje").setText("Debe indicar un usuario encargado (nombre o ID)"));
                DataProtocolo dp = new DataProtocolo("INSERTARTAREA_RESPUESTA", eRespuesta);
                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));
                return;
            }

            // LEER LISTA DE URLs (si no se cargó en toObject)
            Element eUrls = eTarea.getChild("urls");
            if (eUrls != null && (tarea.getUrls() == null || tarea.getUrls().isEmpty())) {
                ArrayList<String> urls = new ArrayList<>();
                for (Object obj : eUrls.getChildren("url")) {
                    Element eUrl = (Element) obj;
                    String urlValue = eUrl.getValue();
                    if (urlValue != null && !urlValue.isEmpty()) {
                        urls.add(urlValue);
                    }
                }
                tarea.setUrls(urls);
            }
            
            // LEER OPCIONES DE ANÁLISIS (si no se cargaron en toObject)
            Element eOpciones = eTarea.getChild("opcionesAnalisis");
            if (eOpciones != null) {
                String img = eOpciones.getChildText("analizarImagenes");
                if (img != null) tarea.setAnalizarImagenes(Boolean.parseBoolean(img));
                
                String vid = eOpciones.getChildText("analizarVideos");
                if (vid != null) tarea.setAnalizarVideos(Boolean.parseBoolean(vid));
                
                String link = eOpciones.getChildText("analizarLinks");
                if (link != null) tarea.setAnalizarLinks(Boolean.parseBoolean(link));
                
                String prod = eOpciones.getChildText("analizarProductos");
                if (prod != null) tarea.setAnalizarProductos(Boolean.parseBoolean(prod));
                
                String serv = eOpciones.getChildText("analizarServicios");
                if (serv != null) tarea.setAnalizarServicios(Boolean.parseBoolean(serv));
            }

            TareaBusiness tb = new TareaBusiness();
            int idGenerado = tb.insertar(tarea);

            System.out.println("✅ Tarea insertada con ID: " + idGenerado);
            System.out.println("   URLs: " + (tarea.getUrls() != null ? tarea.getUrls().size() : 0));

            // ❌ SE ELIMINÓ EL BLOQUE QUE ENVIABA AUTOMÁTICAMENTE AL WORKER
            // La tarea queda en estado "pendiente" y solo se analiza cuando
            // el usuario lo solicite explícitamente desde JIFVentanaListarTarea

            Element eRespuesta = new Element("respuesta");
            eRespuesta.addContent(new Element("resultado").setText("OK"));
            eRespuesta.addContent(new Element("mensaje").setText("Tarea registrada correctamente con " + 
                    (tarea.getUrls() != null ? tarea.getUrls().size() : 0) + " URL(s). Use 'Analizar tarea' para iniciar el análisis."));
            eRespuesta.addContent(new Element("idTarea").setText(String.valueOf(idGenerado)));

            DataProtocolo dp = new DataProtocolo("INSERTARTAREA_RESPUESTA", eRespuesta);
            mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

        } catch (Exception ex) {
            Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);

            Element eRespuesta = new Element("respuesta");
            eRespuesta.addContent(new Element("resultado").setText("ERROR"));
            eRespuesta.addContent(new Element("mensaje").setText("Error al registrar tarea: " + ex.getMessage()));

            DataProtocolo dp = new DataProtocolo("INSERTARTAREA_RESPUESTA", eRespuesta);
            mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));
        }
    }
},
    
    LISTARUSUARIO {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("LISTARUSUARIO recibido");
                UsuarioBusiness ub = new UsuarioBusiness();
                ArrayList<Usuario> usuarios = ub.leerTodo();

                Element eUsuarios = new Element("usuarios");
                for (Usuario u : usuarios) {
                    eUsuarios.addContent(u.toXMLElement());
                }

                DataProtocolo dp = new DataProtocolo("LISTARUSUARIO", eUsuarios);
                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

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
                System.out.println("ELIMINARUSUARIO recibido");

                String idUsuarioStr = null;
                if (eDatos.getChildText("idUsuario") != null) {
                    idUsuarioStr = eDatos.getChildText("idUsuario");
                } else if (eDatos.getChild("usuario") != null) {
                    idUsuarioStr = eDatos.getChild("usuario").getChildText("idUsuario");
                } else if (eDatos.getValue() != null && !eDatos.getValue().trim().isEmpty()) {
                    idUsuarioStr = eDatos.getValue().trim();
                }

                if (idUsuarioStr == null) {
                    enviarErrorAlCliente(mCliente, "ELIMINARUSUARIO", "ID de usuario no encontrado");
                    return;
                }

                int idUsuario = Integer.parseInt(idUsuarioStr);
                UsuarioBusiness ub = new UsuarioBusiness();
                ub.eliminar(idUsuario);

                Element eExito = new Element("resultado").addContent("OK");
                DataProtocolo dp = new DataProtocolo("ELIMINARUSUARIO_RESPUESTA", eExito);
                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "ELIMINARUSUARIO", ex.getMessage());
            }
        }
    },
    
    LISTARTAREA {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("LISTARTAREA recibido");
                TareaBusiness tb = new TareaBusiness();
                ArrayList<Tarea> tareas = tb.leerTodo();

                Element eTareas = new Element("tareas");
                for (Tarea t : tareas) {
                    eTareas.addContent(t.toXMLElement());
                }

                DataProtocolo dp = new DataProtocolo("LISTARTAREA", eTareas);
                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

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
                System.out.println("ELIMINARTAREA recibido");
                int idTarea = Integer.parseInt(eDatos.getChild("idTarea").getValue());
                TareaBusiness tb = new TareaBusiness();
                tb.eliminar(idTarea);

                Element eRespuesta = new Element("respuesta");
                eRespuesta.addContent(new Element("resultado").setText("OK"));
                eRespuesta.addContent(new Element("mensaje").setText("Tarea eliminada correctamente"));

                DataProtocolo dp = new DataProtocolo("ELIMINARTAREA_RESPUESTA", eRespuesta);
                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "ELIMINARTAREA", ex.getMessage());
            }
        }
    },
    
    CONSULTARTAREA {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("CONSULTARTAREA recibido");
                int idTarea = Integer.parseInt(eDatos.getChild("idTarea").getValue());
                TareaBusiness tb = new TareaBusiness();
                Tarea tarea = tb.buscarPorId(idTarea);

                DataProtocolo dp;
                if (tarea == null) {
                    dp = new DataProtocolo("CONSULTARTAREA", null);
                } else {
                    dp = new DataProtocolo("CONSULTARTAREA", tarea.toXMLElement());
                }
                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

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
                System.out.println("ANALIZARURL recibido del cliente");

                String idTarea = eDatos.getChildText("idTarea");
                if (idTarea == null) {
                    enviarErrorAlCliente(mCliente, "ANALIZARURL", "ID de tarea no proporcionado");
                    return;
                }

                TareaBusiness business = new TareaBusiness();
                Tarea tarea = business.buscarPorId(Integer.parseInt(idTarea));

                if (tarea == null) {
                    enviarErrorAlCliente(mCliente, "ANALIZARURL", "No existe la tarea con ID " + idTarea);
                    return;
                }

                MiClienteTrabajador worker = MiServidor.getTrabajador();
                if (worker == null) {
                    enviarErrorAlCliente(mCliente, "ANALIZARURL", "No hay worker disponible");
                    return;
                }

                System.out.println("Enviando tarea al worker para analizar URL: " + tarea.getURL());

                DataProtocolo dpTrabajador = new DataProtocolo("ANALIZARURL", tarea.toXMLElement());
                worker.enviarDatos(GestionXML.xmlToString(dpTrabajador.geteAccion()));

                System.out.println("Tarea enviada al worker");

                Element eRespuesta = new Element("respuesta");
                eRespuesta.addContent(new Element("resultado").setText("OK"));
                eRespuesta.addContent(new Element("mensaje").setText("Análisis enviado al worker"));

                DataProtocolo dp = new DataProtocolo("ANALIZARURL_RESPUESTA", eRespuesta);
                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "ANALIZARURL", ex.getMessage());
            }
        }
    },
    
    INSERTARUSUARIO {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("INSERTARUSUARIO recibido");

                Element eUsuario = eDatos.getChild("usuario");
                if (eUsuario == null) {
                    Element eError = new Element("exito").setText("false");
                    eError.addContent(new Element("mensaje").setText("Datos de usuario no encontrados"));
                    DataProtocolo dp = new DataProtocolo("INSERTARUSUARIO", eError);
                    mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));
                    return;
                }

                Usuario usuario = new Usuario();
                usuario.toObject(eUsuario);

                String contrasenaEncriptada = encriptarSHA1(usuario.getContrasena());
                usuario.setContrasena(contrasenaEncriptada);

                UsuarioBusiness ub = new UsuarioBusiness();
                ub.insertar(usuario);

                Element eRespuesta = new Element("exito").setText("true");
                eRespuesta.addContent(new Element("mensaje").setText("Usuario creado exitosamente"));
                DataProtocolo dp = new DataProtocolo("INSERTARUSUARIO", eRespuesta);
                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                Element eError = new Element("exito").setText("false");
                if (ex.getMessage() != null && ex.getMessage().contains("UNIQUE")) {
                    eError.addContent(new Element("mensaje").setText("El nombre de usuario ya existe"));
                } else {
                    eError.addContent(new Element("mensaje").setText("Error en base de datos: " + ex.getMessage()));
                }
                DataProtocolo dp = new DataProtocolo("INSERTARUSUARIO", eError);
                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));
            }
        }
    },
    
    BUSCARUSUARIO {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("BUSCARUSUARIO recibido");
                String nombre = eDatos.getChildText("nombre");

                UsuarioBusiness ub = new UsuarioBusiness();
                Usuario usuario = ub.buscarPorNombre(nombre);

                Element eRespuesta = new Element("respuesta");
                if (usuario != null) {
                    eRespuesta.addContent(new Element("encontrado").setText("true"));
                    eRespuesta.addContent(usuario.toXMLElement());
                } else {
                    eRespuesta.addContent(new Element("encontrado").setText("false"));
                    eRespuesta.addContent(new Element("mensaje").setText("Usuario no encontrado"));
                }

                DataProtocolo dp = new DataProtocolo("BUSCARUSUARIO", eRespuesta);
                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "BUSCARUSUARIO", ex.getMessage());
            }
        }
    },
    
    BUSCAR_PRODUCTOS {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("BUSCAR_PRODUCTOS recibido");

                String idTarea = eDatos.getChildText("idTarea");

                if (idTarea == null) {
                    enviarErrorAlCliente(mCliente, "BUSCAR_PRODUCTOS", "Falta el ID de la tarea");
                    return;
                }

                TareaBusiness business = new TareaBusiness();
                Tarea tarea = business.buscarPorId(Integer.parseInt(idTarea));

                if (tarea == null) {
                    enviarErrorAlCliente(mCliente, "BUSCAR_PRODUCTOS", "Tarea no encontrada");
                    return;
                }

                String termino = tarea.getDescripcion();

                if (termino == null || termino.trim().isEmpty()) {
                    enviarErrorAlCliente(mCliente, "BUSCAR_PRODUCTOS", "La tarea no tiene descripción");
                    return;
                }

                MiClienteTrabajador worker = MiServidor.getTrabajador();
                if (worker == null) {
                    enviarErrorAlCliente(mCliente, "BUSCAR_PRODUCTOS", "No hay worker disponible");
                    return;
                }

                Element eBusqueda = new Element("busqueda");
                eBusqueda.addContent(new Element("idTarea").setText(idTarea));
                eBusqueda.addContent(new Element("url").setText(tarea.getURL()));
                eBusqueda.addContent(new Element("termino").setText(termino.trim()));

                DataProtocolo dp = new DataProtocolo("BUSCAR_PRODUCTOS", eBusqueda);
                worker.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

                System.out.println("Búsqueda enviada al worker: " + termino);

                Element eRespuesta = new Element("respuesta");
                eRespuesta.addContent(new Element("resultado").setText("OK"));
                eRespuesta.addContent(new Element("mensaje").setText("Búsqueda iniciada"));

                DataProtocolo dpRespuesta = new DataProtocolo("BUSCAR_PRODUCTOS_RESPUESTA", eRespuesta);
                mCliente.enviarDatos(GestionXML.xmlToString(dpRespuesta.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "BUSCAR_PRODUCTOS", ex.getMessage());
            }
        }
    },
    
    GUARDAR_PRODUCTOS {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("GUARDAR_PRODUCTOS recibido del worker");

                Element eListaProductos = eDatos.getChild("listaProductos");

                if (eListaProductos == null) {
                    System.err.println("No se encontró lista de productos");
                    Element eError = new Element("error").addContent("No se encontró lista de productos");
                    DataProtocolo dpError = new DataProtocolo("GUARDAR_PRODUCTOS_ERROR", eError);
                    mCliente.enviarDatos(GestionXML.xmlToString(dpError.geteAccion()));
                    return;
                }

                String idTarea = eListaProductos.getChildText("idTarea");

                System.out.println("Tarea: " + idTarea);
                System.out.println("Cantidad de productos: " + eListaProductos.getChildren("producto").size());

                List<Element> eProductos = eListaProductos.getChildren("producto");
                ProductoData productoData = new ProductoData();

                for (Element eProducto : eProductos) {
                    Producto p = new Producto();
                    p.toObject(eProducto);
                    productoData.insertar(p);

                    System.out.println("Producto guardado: " + p.getDescripcion() + " | Precio: " + p.getPrecio());
                }

                System.out.println(eProductos.size() + " productos guardados para tarea " + idTarea);

                Element eRespuestaWorker = new Element("respuesta");
                eRespuestaWorker.addContent(new Element("resultado").setText("OK"));
                eRespuestaWorker.addContent(new Element("mensaje").setText("Productos guardados correctamente"));

                DataProtocolo dpWorker = new DataProtocolo("GUARDAR_PRODUCTOS_RESPUESTA", eRespuestaWorker);
                mCliente.enviarDatos(GestionXML.xmlToString(dpWorker.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                Element eError = new Element("error").addContent(ex.getMessage());
                DataProtocolo dpError = new DataProtocolo("GUARDAR_PRODUCTOS_ERROR", eError);
                mCliente.enviarDatos(GestionXML.xmlToString(dpError.geteAccion()));
            } catch (IOException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    },
    
    // ✅ NUEVO MÉTODO AGREGADO AQUÍ
    GUARDAR_RESULTADO_URL {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                Element eResultado = eDatos.getChild("resultado");
                if (eResultado == null) {
                    System.out.println("Error: no llegó elemento <resultado> en GUARDAR_RESULTADO_URL");
                    return;
                }
                
                Resultado resultado = new Resultado();
                resultado.setIdTarea(Integer.parseInt(eResultado.getChildText("idTarea")));
                resultado.setTotalEnlaces(Integer.parseInt(eResultado.getChildText("totalEnlaces")));
                resultado.setTotalImagenes(Integer.parseInt(eResultado.getChildText("totalImagenes")));
                resultado.setTotalProductos(Integer.parseInt(eResultado.getChildText("totalProductos")));
                resultado.setTotalVideos(Integer.parseInt(eResultado.getChildText("totalVideos")));
                resultado.setFecha(new java.util.Date().toString());
                
                String url = eResultado.getChildText("url");
                
                ResultadoData rd = new ResultadoData();
                rd.insertarPorUrl(resultado, url);
                
                System.out.println("✅ Resultado guardado para URL: " + url);
                System.out.println("   Tarea ID: " + resultado.getIdTarea());
                System.out.println("   Imágenes: " + resultado.getTotalImagenes());
                System.out.println("   Enlaces: " + resultado.getTotalEnlaces());
                System.out.println("   Videos: " + resultado.getTotalVideos());
                System.out.println("   Productos: " + resultado.getTotalProductos());
                
            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NumberFormatException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, "Error parseando números en GUARDAR_RESULTADO_URL", ex);
            }
        }
    },
    
    GUARDAR_RESULTADO {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                Element eResultado = eDatos.getChild("resultado");

                if (eResultado == null) {
                    System.out.println("Error: no llegó elemento <resultado>");
                    return;
                }

                Resultado resultado = new Resultado();
                resultado.setIdTarea(Integer.parseInt(eResultado.getChildText("idTarea")));
                resultado.setTotalEnlaces(Integer.parseInt(eResultado.getChildText("totalEnlaces")));
                resultado.setTotalImagenes(Integer.parseInt(eResultado.getChildText("totalImagenes")));
                resultado.setTotalProductos(Integer.parseInt(eResultado.getChildText("totalProductos")));
                resultado.setTotalVideos(Integer.parseInt(eResultado.getChildText("totalVideos")));
                resultado.setFecha(new java.util.Date().toString());

                ResultadoData resultadoData = new ResultadoData();
                resultadoData.insertar(resultado);

                System.out.println("Resultado guardado en BD para tarea: " + resultado.getIdTarea());
                System.out.println("  Imágenes: " + resultado.getTotalImagenes());
                System.out.println("  Enlaces:  " + resultado.getTotalEnlaces());
                System.out.println("  Videos:   " + resultado.getTotalVideos());
                System.out.println("  Productos:" + resultado.getTotalProductos());

            } catch (Exception ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    },     LISTARRESULTADOS {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            try {
                System.out.println("LISTARRESULTADOS recibido");
                ResultadoData rd = new ResultadoData();
                ArrayList<Resultado> resultados = rd.obtenerTodos();
                Element eResultados = new Element("resultados");
                for (Resultado resultado : resultados) {
                    eResultados.addContent(resultado.toXMLElement());
                }
                DataProtocolo dp = new DataProtocolo("LISTARRESULTADOS", eResultados);
                mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

            } catch (SQLException ex) {
                Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
                enviarErrorAlCliente(mCliente, "LISTARRESULTADOS", ex.getMessage());
            }
        }
    },// Agregar en EnumProtocolo.java del servidor
LISTARPRODUCTOS {
    @Override
    public void accion(Cliente mCliente, Element eDatos) {
        try {
            System.out.println("LISTARPRODUCTOS recibido");
            ProductoData pd = new ProductoData();
            ArrayList<Producto> productos = pd.obtenerTodos();

            Element eProductos = new Element("productos");
            for (Producto p : productos) {
                eProductos.addContent(p.toXMLElement());
            }

            DataProtocolo dp = new DataProtocolo("LISTARPRODUCTOS", eProductos);
            mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));

        } catch (Exception ex) {
            Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
            enviarErrorAlCliente(mCliente, "LISTARPRODUCTOS", ex.getMessage());
        }
    }
},
    
  ANALIZAR_TAREA_CON_OPCIONES {
    @Override
    public void accion(Cliente mCliente, Element eDatos) {
        try {
            // Buscar el elemento correcto
            Element eConfig = eDatos.getChild("analisisConfig");
            if (eConfig == null) {
                eConfig = eDatos; // fallback por si viene directo
            }

            // Validar que idTarea no sea null antes de parsear
            String idTareaStr = eConfig.getChildText("idTarea");
            if (idTareaStr == null || idTareaStr.isEmpty()) {
                enviarErrorAlCliente(mCliente, "ANALIZAR_TAREA_CON_OPCIONES", 
                    "ID de tarea no encontrado en el XML");
                return;
            }

            int idTarea = Integer.parseInt(idTareaStr);
            boolean analizarImagenes  = Boolean.parseBoolean(eConfig.getChildText("analizarImagenes"));
            boolean analizarVideos    = Boolean.parseBoolean(eConfig.getChildText("analizarVideos"));
            boolean analizarLinks     = Boolean.parseBoolean(eConfig.getChildText("analizarLinks"));
            boolean analizarProductos = Boolean.parseBoolean(eConfig.getChildText("analizarProductos"));
            boolean analizarServicios = Boolean.parseBoolean(eConfig.getChildText("analizarServicios"));

            TareaBusiness tb = new TareaBusiness();
            Tarea tarea = tb.buscarPorId(idTarea);

            if (tarea == null) {
                enviarErrorAlCliente(mCliente, "ANALIZAR_TAREA_CON_OPCIONES", 
                    "Tarea no encontrada con ID: " + idTarea);
                return;
            }

            // Actualizar opciones
            tarea.setAnalizarImagenes(analizarImagenes);
            tarea.setAnalizarVideos(analizarVideos);
            tarea.setAnalizarLinks(analizarLinks);
            tarea.setAnalizarProductos(analizarProductos);
            tarea.setAnalizarServicios(analizarServicios);

            MiClienteTrabajador worker = MiServidor.getTrabajador();
            if (worker == null) {
                enviarErrorAlCliente(mCliente, "ANALIZAR_TAREA_CON_OPCIONES", 
                    "No hay worker disponible");
                return;
            }

            DataProtocolo dpTrabajador = new DataProtocolo(
                "ANALIZAR_URL_COMPLETO", tarea.toXMLElement()
            );
            worker.enviarDatos(GestionXML.xmlToString(dpTrabajador.geteAccion()));

            System.out.println("Tarea " + idTarea + " enviada al worker con opciones seleccionadas");

        } catch (Exception ex) {
            Logger.getLogger(EnumProtocolo.class.getName()).log(Level.SEVERE, null, ex);
            enviarErrorAlCliente(mCliente, "ANALIZAR_TAREA_CON_OPCIONES", ex.getMessage());
        }
    }
},
    
    CONSULTARRESULTADO {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            System.out.println("CONSULTARRESULTADO - Pendiente de implementar");
            enviarErrorAlCliente(mCliente, "CONSULTARRESULTADO", "No implementado aún");
        }
    },
    
    EXPORTARPDF {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            System.out.println("EXPORTARPDF - Pendiente de implementar");
            enviarErrorAlCliente(mCliente, "EXPORTARPDF", "No implementado aún");
        }
    },
    
    GUARDAR_PRODUCTOS_ERROR {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            String error = eDatos.getChildText("error");
            Logger.getLogger(EnumProtocolo.class.getName())
                    .log(Level.SEVERE, "Worker reportó error en GUARDAR_PRODUCTOS: {0}", error);
        }
    },
    
    BUSCAR_PRODUCTOS_ERROR {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            String error = eDatos.getChildText("error");
            Logger.getLogger(EnumProtocolo.class.getName())
                    .log(Level.SEVERE, "Error en BUSCAR_PRODUCTOS: {0}", error);
        }
    },
    
    ANALIZARURL_ERROR {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            String error = eDatos.getChildText("error");
            Logger.getLogger(EnumProtocolo.class.getName())
                    .log(Level.SEVERE, "Error en ANALIZARURL: {0}", error);
        }
    },
    
    GUARDAR_PRODUCTOS_RESPUESTA {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            String resultado = eDatos.getChildText("resultado");
            String mensaje = eDatos.getChildText("mensaje");

            if ("OK".equals(resultado)) {
                System.out.println("Worker confirmó productos guardados: " + mensaje);
            } else {
                Logger.getLogger(EnumProtocolo.class.getName())
                        .log(Level.WARNING, "Worker reportó problema: {0}", mensaje);
            }
        }
    },
    
    BUSCAR_PRODUCTOS_RESPUESTA {
        @Override
        public void accion(Cliente mCliente, Element eDatos) {
            String resultado = eDatos.getChildText("resultado");
            String mensaje = eDatos.getChildText("mensaje");

            if ("OK".equals(resultado)) {
                System.out.println("Cliente notificado: " + mensaje);
            } else {
                Logger.getLogger(EnumProtocolo.class.getName())
                        .log(Level.WARNING, "Error notificado a cliente: {0}", mensaje);
            }
        }
    };
   
    public abstract void accion(Cliente mCliente, Element eDatos);

    private static void enviarErrorAlCliente(Cliente mCliente, String accion, String mensajeError) {
        Element eError = new Element("error").addContent(mensajeError);
        DataProtocolo dp = new DataProtocolo(accion + "_ERROR", eError);
        mCliente.enviarDatos(GestionXML.xmlToString(dp.geteAccion()));
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
            Logger.getLogger(EnumProtocolo.class.getName())
                    .log(Level.SEVERE, "Error crítico al encriptar contraseña", ex);
            throw new RuntimeException("No se pudo encriptar la contraseña", ex);
        }
    }
}