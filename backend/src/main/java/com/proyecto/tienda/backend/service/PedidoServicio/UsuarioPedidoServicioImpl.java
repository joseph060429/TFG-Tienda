package com.proyecto.tienda.backend.service.PedidoServicio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.proyecto.tienda.backend.DTO.DTOPedido.CrearPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.EmpresaAutonomoDireccionFacturacionDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.ParticularDireccionFacturacionDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.PedidoInfoDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.ProductoPedidoDTO;
import com.proyecto.tienda.backend.UtilEnum.EFactura;
import com.proyecto.tienda.backend.UtilEnum.EPedido;
import com.proyecto.tienda.backend.UtilEnum.EPedidoPago;
import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.models.ProductoModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.repositorios.ProductoRepositorio;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.Paypal.PayPalServicio;
import com.proyecto.tienda.backend.util.ResendUtil;
import jakarta.servlet.http.HttpSession;

@Service
public class UsuarioPedidoServicioImpl implements UsuarioPedidoServicio {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private PayPalServicio paypalServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private ResendUtil resend;

    // IMPLEMENTACION DEL METODO PARA CREAR EL PEDIDO
    @Transactional
    @Override
    public ResponseEntity<?> crearPedido(CrearPedidoDTO crearPedidoDTO, String token, JwtUtils jwtUtils,
            List<ProductoModelo> productosModelo, HttpSession ses) {

        try {
            String emailFromToken = obtenerEmailDelToken(token, jwtUtils);
            Optional<UsuarioModelo> usuarioModelo = buscarUsuarioPorEmail(emailFromToken);

            if (!usuarioModelo.isPresent()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al crear el pedido: Usuario no encontrado");
            }

            UsuarioModelo usuario = usuarioModelo.get();

            System.out.println("NOMBRE USUARIO " + usuario.getNombre());
            System.out.println("APELLIDO DEL USUARIO " + usuario.getApellido());

            PedidosModelo pedido = crearNuevoPedido(crearPedidoDTO, usuario);

            System.out.println("ID PEDIDO " + pedido.get_id());

            // Establezco el numero de telefono, lo hice para evitar que el repartidor vaya
            // a casa y si la persona no esta, tenga un sitio donde dejarlo
            Long numTele = crearPedidoDTO.getNumTelefono();
            pedido.setNumTelefono(numTele);

            // Añado la direccion de envio
            String direccionEnvio = anadirDireccionEnvio(crearPedidoDTO.getCodigoPostal(),
                    crearPedidoDTO.getDireccion(), crearPedidoDTO.getProvincia(), crearPedidoDTO.getNumero(),
                    crearPedidoDTO.getPiso(), crearPedidoDTO.getPuerta(), usuario);
            pedido.setDireccionCompletaEnvio(direccionEnvio);

            ResponseEntity<?> resultadoPagoValidacion = validarTipoPagoYSetearlo(crearPedidoDTO.getTipoPago(), pedido);

            if (resultadoPagoValidacion != null) {
                return resultadoPagoValidacion;
            }

            // Genero una nueva lista con los productos pedidos
            List<ProductoPedidoDTO> listaNueva = generarListaProductosPedido(productosModelo);
            pedido.setProductos(listaNueva);

            // Para imprimir todos los productos pedidos
            for (ProductoPedidoDTO producto : listaNueva) {
                System.out.println("NOMBRE PRODUCTO: " + producto.getNombre());
                System.out.println("MARCA PRODUCTO: " + producto.getMarca());
                System.out.println("PRECIO PRODUCTO: " + producto.getPrecioProducto());
                System.out.println("CANTIDAD PRODUCTO: " + producto.getCantidadPedida());
                System.out.println("--------------------------------------");
            }


            // Calculo el precio total sumando todos los productos de ese pedido
            double total = 0.0;
            for (ProductoPedidoDTO productoPedido : listaNueva) {
                total += productoPedido.getPrecioProducto() * productoPedido.getCantidadPedida();
            }
            System.out.println("TOTAL " + total);

            // Establecezco la fecha del pedido
            crearPedidoDTO.setFechaPedido();
            pedido.setFechaPedido(crearPedidoDTO.getFechaPedido());

            // Veo cual ha sido el resultado de la facturacion, si ha sido particular o
            // autonomo/empresa
            ResponseEntity<?> resultadoDireccionFacturacion = procesarDireccionFacturacion(usuario, crearPedidoDTO,
                    pedido);
            if (resultadoDireccionFacturacion != null) {
                System.out.println("RESULTADO DIRECCION FACTURACION " + resultadoDireccionFacturacion);
                return resultadoDireccionFacturacion;
            }
            ses.setAttribute("pedido", pedido);

            // Mando a la pagina del pago del Pay-Pal antes de crear el pedido
            return paypalServicio.hacerPago(pedido, ses);

        } catch (RuntimeException e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear el pedido: " + e.getMessage());
        }
    }

    private ResponseEntity<?> procesarDireccionFacturacion(UsuarioModelo usuario, CrearPedidoDTO crearPedidoDTO,
            PedidosModelo pedido) {
        try {
            ParticularDireccionFacturacionDTO particular = crearPedidoDTO.getParticularDireccionFacturacionDTO();
            EmpresaAutonomoDireccionFacturacionDTO empresaAutonomo = crearPedidoDTO
                    .getEmpresaAutonomoDireccionFacturacionDTO();
            EFactura tipoFacturacion = EFactura.valueOf(crearPedidoDTO.getTipoFacturacion().trim().toUpperCase());

            // Valido el tipo de factura seleccionado
            if (tipoFacturacion == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error al crear el pedido: Tipo de factura no válido");
            }

            // Establezco la dirección de facturación según el tipo de factura seleccionado
            if (tipoFacturacion == EFactura.PARTICULAR) {
                // Crear instancia del DTO para la dirección de facturación de particulares
                ParticularDireccionFacturacionDTO particularDTO = new ParticularDireccionFacturacionDTO();

                // Asignar los valores del DTO particular
                particularDTO.setNombreFacturacion(particular.getNombreFacturacion());
                particularDTO.setApellidoFacturacion(particular.getApellidoFacturacion());
                particularDTO.setNumTelefonoFacturacion(particular.getNumTelefonoFacturacion());
                particularDTO.setDireccionDeFacturacion(particular.getDireccionDeFacturacion());
                particularDTO.setCodigoPostalDeFacturacion(particular.getCodigoPostalDeFacturacion());
                particularDTO.setProvinciaDeFacturacion(particular.getProvinciaDeFacturacion());
                particularDTO.setNumeroDeFacturacion(particular.getNumeroDeFacturacion());
                particularDTO.setPisoDeFacturacion(particular.getPisoDeFacturacion());
                particularDTO.setPuertaDeFacturacion(particular.getPuertaDeFacturacion());

                // Añado la dirección de facturación particular al pedido
                String direccionFacturacionParticular = anadirDireccionDeFacturacionParticular(
                        particularDTO.getNombreFacturacion(),
                        particularDTO.getApellidoFacturacion(),
                        particularDTO.getNumTelefonoFacturacion(),
                        particularDTO.getDireccionDeFacturacion(),
                        particularDTO.getCodigoPostalDeFacturacion(),
                        particularDTO.getProvinciaDeFacturacion(),
                        particularDTO.getNumeroDeFacturacion(),
                        particularDTO.getPisoDeFacturacion(),
                        particularDTO.getPuertaDeFacturacion(),
                        usuario);

                pedido.setDireccionCompletaFacturacion(direccionFacturacionParticular);

            } else if (tipoFacturacion == EFactura.EMPRESA_AUTONOMO) {
                EmpresaAutonomoDireccionFacturacionDTO empresaAutonomoDireccionFacturacionDTO = new EmpresaAutonomoDireccionFacturacionDTO();

                // Valores del dto empresa/autonomo
                empresaAutonomoDireccionFacturacionDTO.setCifONifFacturacion(empresaAutonomo.getCifONifFacturacion());
                empresaAutonomoDireccionFacturacionDTO
                        .setNumTelefonoFacturacion(empresaAutonomo.getNumTelefonoFacturacion());
                empresaAutonomoDireccionFacturacionDTO
                        .setDireccionDeFacturacion(empresaAutonomo.getDireccionDeFacturacion());
                empresaAutonomoDireccionFacturacionDTO
                        .setCodigoPostalDeFacturacion(empresaAutonomo.getCodigoPostalDeFacturacion());
                empresaAutonomoDireccionFacturacionDTO
                        .setProvinciaDeFacturacion(empresaAutonomo.getProvinciaDeFacturacion());
                empresaAutonomoDireccionFacturacionDTO.setNumeroDeFacturacion(empresaAutonomo.getNumeroDeFacturacion());
                empresaAutonomoDireccionFacturacionDTO.setPisoDeFacturacion(empresaAutonomo.getPisoDeFacturacion());
                empresaAutonomoDireccionFacturacionDTO.setPuertaDeFacturacion(empresaAutonomo.getPuertaDeFacturacion());

                String direccionFacturacionAutoEmpresa = anadirDireccionDeFacturacionAutoEmpresa(
                        empresaAutonomoDireccionFacturacionDTO.getCifONifFacturacion(),
                        empresaAutonomoDireccionFacturacionDTO.getNumTelefonoFacturacion(),
                        empresaAutonomoDireccionFacturacionDTO.getDireccionDeFacturacion(),
                        empresaAutonomoDireccionFacturacionDTO.getCodigoPostalDeFacturacion(),
                        empresaAutonomoDireccionFacturacionDTO.getProvinciaDeFacturacion(),
                        empresaAutonomoDireccionFacturacionDTO.getNumeroDeFacturacion(),
                        empresaAutonomoDireccionFacturacionDTO.getPisoDeFacturacion(),
                        empresaAutonomoDireccionFacturacionDTO.getPuertaDeFacturacion(),
                        usuario);

                pedido.setDireccionCompletaFacturacion(direccionFacturacionAutoEmpresa);

            }

            // Si todo va bien, devuelve null para indicar que no hay errores en la
            // dirección de facturación
            return null;

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear el pedido: Tipo de factura no válido. Los valores permitidos son: "
                            + Arrays.toString(EFactura.values()));
        }
    }

    // IMPLEMENTACION DEL METODO PARA LA DIRECCION DE FACTURACION PARTICULAR
    public String anadirDireccionDeFacturacionParticular(String nombreFacturacion, String apellidoFacturacion,
            Long numTelefonoFacturacion,
            String direccionDeFacturacion, String codigoPostalDeFacturacion, String provinciaDeFacturacion,
            String numeroDeFacturacion, String pisoDeFacturacion, String puertaDeFacturacion, UsuarioModelo usuario) {
        try {
            // Agrego la dirección de envío al usuario
            String direccionFacturacionParticular = usuario.construirDireccionFacturacionParticular(nombreFacturacion,
                    apellidoFacturacion, numTelefonoFacturacion, direccionDeFacturacion, codigoPostalDeFacturacion,
                    provinciaDeFacturacion, numeroDeFacturacion, pisoDeFacturacion, puertaDeFacturacion);

            // Guardo el usuario con la nueva dirección de facturacion
            usuarioRepositorio.save(usuario);

            return direccionFacturacionParticular;
        } catch (IllegalArgumentException e) {
            System.out.println("Error al anadir la dirección de envío: " + e.getMessage());
            return ("Error al anadir la dirección de envío: " + e.getMessage());
        }
    }

    // IMPLEMENTACIO DEL METODO PARA CONTRUIR LA DIRECCION DE FACTURACION AUTONO_EMPRESA
    public String anadirDireccionDeFacturacionAutoEmpresa(String cifONifFacturacion, Long numTelefonoFacturacion,
            String direccionDeFacturacion, String codigoPostalDeFacturacion, String provinciaDeFacturacion,
            String numeroDeFacturacion, String pisoDeFacturacion, String puertaDeFacturacion, UsuarioModelo usuario) {
        try {
            // Agrego la dirección de envío al usuario
            String direccionFacturacionParticular = usuario.construirDireccionFacturacionAutoEmpresa(cifONifFacturacion,
                    numTelefonoFacturacion, direccionDeFacturacion, codigoPostalDeFacturacion,
                    provinciaDeFacturacion, numeroDeFacturacion, pisoDeFacturacion, puertaDeFacturacion);

            // Guardo el usuario con la nueva dirección de facturacion
            usuarioRepositorio.save(usuario);

            return direccionFacturacionParticular;
        } catch (IllegalArgumentException e) {
            System.out.println("Error al anadir la dirección de envío: " + e.getMessage());
            return ("Error al anadir la dirección de envío: " + e.getMessage());
        }
    }

    // IMPLEMENTACION DEL METODO PARA LA DIRECCION DE ENVIO
    public String anadirDireccionEnvio(String codigoPostal, String direccion, String provincia,
            String numero, String piso, String puerta, UsuarioModelo usuario) {
        try {
            // Agrego la dirección de envío al usuario
            String direccionEnvio = usuario.agregarDireccionCompletaATablaUsuario(direccion, provincia, puerta,
                    codigoPostal, piso,
                    numero);

            // Guardo el usuario con la nueva dirección de envío
            usuarioRepositorio.save(usuario);

            return direccionEnvio;
        } catch (IllegalArgumentException e) {
            System.out.println("Error al anadir la dirección de envío: " + e.getMessage());
            return ("Error al anadir la dirección de envío: " + e.getMessage());
        }
    }

    // IMPLEMENTACION DEL METODO PARA OBTENER EL EMAIL DEL TOKEN
    private String obtenerEmailDelToken(String token, JwtUtils jwtUtils) {
        String jwtToken = token.replace("Bearer ", "");
        return jwtUtils.getEmailFromToken(jwtToken);
    }

    // IMPLEMENTACION DEL METODO PARA BUSCAR Y COMPARAR SI EL EMAIL DEL TOKEN EXISTE
    // EN MI BASE DE DATOS
    private Optional<UsuarioModelo> buscarUsuarioPorEmail(String emailFromToken) {
        return usuarioRepositorio.findByEmail(emailFromToken);
    }

    // IMPLEMENTACION DEL METODO PARA CREAR UN NUEVO PEDIDO, ES DECIR ME PINTE ESAS
    // INFORMACIONES EN MI BASE DE DATOS
    private PedidosModelo crearNuevoPedido(CrearPedidoDTO crearPedidoDTO, UsuarioModelo usuario) {
        PedidosModelo pedido = new PedidosModelo();
        pedido.set_id(UUID.randomUUID().toString());
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(crearPedidoDTO.getFechaPedido());
        asignarNumeroPedido(pedido);
        pedido.setEstado(EPedido.PENDIENTE.toString());
        pedido.setTrackingNumber("");
        return pedido;
    }

    // IMPLEMENTACION DEL METODO PARA VALIDAR EL TIPO DE PAGO Y SETEARLO
    private ResponseEntity<?> validarTipoPagoYSetearlo(String tipoPago, PedidosModelo pedido) {
        if (tipoPago == null || tipoPago.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear el pedido: El tipo de pago no puede estar en blanco");
        } else {
            try {
                EPedidoPago enumValue = EPedidoPago.valueOf(tipoPago.toUpperCase().trim());
                pedido.setTipoPago(enumValue.name().trim());
                return null; // Tipo de pago válido
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error al crear el pedido: El tipo de pago no es válido");
            }
        }
    }

    // IMPLEMENTACION DEL METODO PARA ASIGNAR EL NUMERO DE PEDIDO AL PEDIDO
    private void asignarNumeroPedido(PedidosModelo pedido) {
        List<PedidosModelo> listaUltimosPedidos = pedidoRepositorio.findTopByOrderByNumPedidoDesc();
        Long nuevoNumeroPedido = 1L;

        if (listaUltimosPedidos != null && !listaUltimosPedidos.isEmpty()) {
            nuevoNumeroPedido = listaUltimosPedidos.stream()
                    .mapToLong(PedidosModelo::getNumPedido)
                    .max()
                    .orElse(0L) + 1;
        }

        pedido.setNumPedido(nuevoNumeroPedido);
    }

    // IMPLEMENTACION DEL METODO PARA VERIFICAR LOS PRODUCTOS PEDIDOS Y AÑADIRLO A
    // LOS PEDIDOS
    @Transactional
    private List<ProductoPedidoDTO> generarListaProductosPedido(List<ProductoModelo> listaProductosPedidos) {
        List<ProductoPedidoDTO> listaPedidoUsuario = new ArrayList<>(); // Lista para almacenar los productos del pedido

        try {

            // Compruebo que todos los productos existen
            for (ProductoModelo productoModelo : listaProductosPedidos) {

                // Los productos que pide el usuario lo iniciare como null
                ProductoPedidoDTO productoPedido = null; // Crear un objeto ProductoPedido

                // Obtengo el producto por su ID
                ProductoModelo productoEncontradoEnBaseDeDatos = productoRepositorio.findBy_id(productoModelo.get_id());

                // Compruebo que el producto exista
                if (productoEncontradoEnBaseDeDatos == null) {
                    System.out.println("Uno o más productos no existen");
                    // Si el producto NO existe lanzo una exception
                    throw new RuntimeException("Uno o más productos no existen");
                } else {
                    // Si el producto existe verifico que la cantidad que me hayan puesto no sea 0 o
                    // menor que 0
                    // productoModelo = modelo del producto que se está procesando actualmente en el
                    // bucle
                    // productoEncontradoEnBaseDeDatos = producto que se ha encontrado en la base de
                    // datos
                    // listaProductosModelo = lista de productos del pedido
                    if (!verificarCantidadPedidaValida(productoModelo, productoEncontradoEnBaseDeDatos,
                            listaProductosPedidos)) {
                        // Si la cantidad del producto es 0 lanzo una exception
                        throw new RuntimeException("La cantidad del producto "
                                + productoEncontradoEnBaseDeDatos.getNombreProducto().toUpperCase()
                                + " debe ser mayor a 0");

                    } else {
                        // Creo un objeto ProductoPedido con los datos del producto encontrado y la
                        // cantidad pedida
                        productoPedido = new ProductoPedidoDTO(
                                productoEncontradoEnBaseDeDatos.get_id(),
                                productoEncontradoEnBaseDeDatos.getNombreProducto(),
                                productoEncontradoEnBaseDeDatos.getMarcaProducto(),
                                productoEncontradoEnBaseDeDatos.getPrecioProducto(),
                                productoEncontradoEnBaseDeDatos.getCategoria().toString(),
                                productoModelo.getCantidadProducto());

                    }
                    listaPedidoUsuario.add(productoPedido);
                }
            }

            int todosEnStock = 1;
            // Ahora verifico los productos pedidos sobre la lista de productos pedidos
            for (ProductoPedidoDTO productoPedido : listaPedidoUsuario) {
                // Verifico si hay suficiente stock para el producto actual
                ProductoModelo productoEncontrado = productoRepositorio.findBy_id(productoPedido.get_idProducto());
                if (productoEncontrado.getCantidadProducto() >= productoPedido.getCantidadPedida()) {
                    // Hay stock, ==> todosEnStock se mantiene en 1.
                    continue;
                } else {
                    // No hay stock ==> todosEnStock cambia a 0, ver abajo
                    todosEnStock = 0;
                    System.out.println("NO HAY TODOS EN STOCK " + todosEnStock);
                    throw new RuntimeException("La cantidad solicitada para el producto "
                            + productoEncontrado.getNombreProducto().toUpperCase() + " excede de los que hay stock");
                }
            }

            // Verifico que todos los productos esten en stock
            if (todosEnStock == 1) {
                System.out.println("TODOS EN STOCK " + todosEnStock);
                // Significa que todos los productos que se pidieron estan en stock, manejo la
                // resta cuando el pago es exitoso.
                for (ProductoPedidoDTO productoPedido : listaPedidoUsuario) {
                    ProductoModelo encontrado = productoRepositorio.findBy_id(productoPedido.get_idProducto());
                    // restarCantidadProducto(encontrado.get_id(),
                    // productoPedido.getCantidadPedida());
                }
            } else {
                throw new RuntimeException("Todos los productos no tienen stock");
            }
            // Devuelvo la lista de productos pedidos
            return listaPedidoUsuario;

        } catch (RuntimeException e) {
            System.out.println("Error al procesar el pedido: " + e.getMessage());
            throw e; // Lanzo la exception
        }
    }

    // IMPLEMENTACION DEL METODO PARA VERIFICAR LA CANTIDAD PEDIDA DE CADA PRODUCTO
    private boolean verificarCantidadPedidaValida(ProductoModelo productoPedido, ProductoModelo productoEncontrado,
            List<ProductoModelo> productosSolicitados) {
        try {
            if (productoPedido.getCantidadProducto() <= 0) {
                // Si la cantidad es 0 o menor, la cantidad pedida no es válida
                return false;
            }
            return true; // Cantidad pedida válida
        } catch (RuntimeException e) {
            // Manejo de excepciones
            e.getMessage();
            throw new RuntimeException("Error al verificar la cantidad pedida del producto");
        }
    }

    // METODO PARA RESTAR LA CANTIDAD DE PRODUCTOS
    public int restarCantidadProducto(String productoId, int cantidadRestar) {

        try {
            // Buscar el producto por su ID
            Optional<ProductoModelo> productoOptional = productoRepositorio.findById(productoId);

            // Verificar si el producto existe en la base de datos
            if (productoOptional.isPresent()) {
                ProductoModelo productoEncontrado = productoOptional.get();

                // Obtener la cantidad actual del producto
                Integer cantidadActual = productoEncontrado.getCantidadProducto();

                // Verificar si hay suficiente stock para restar
                if (cantidadActual >= cantidadRestar) {
                    // Restar la cantidad especificada del stock
                    productoEncontrado.setCantidadProducto(cantidadActual - cantidadRestar);

                    // Si la cantidad actual es 0, establecer disponibilidad en false
                    if (productoEncontrado.getCantidadProducto() == 0) {
                        productoEncontrado.setDisponibilidadProducto(false);
                    }

                    // Guardar el producto actualizado en la base de datos
                    productoRepositorio.save(productoEncontrado);

                    // Todo bien, la resta se realizó con éxito
                    return 0;
                } else {
                    // No hay suficiente stock para restar la cantidad especificada
                    return 1;
                }
            } else {
                // El producto no fue encontrado
                return 2;
            }
        } catch (Exception e) {
            // Manejo de excepciones
            e.printStackTrace();
            return 3;
        }
    }

    // IMPLEMENTACION DEL METODO PARA ELIMINAR UN PEDIDO
    @Override
    public ResponseEntity<?> eliminarPedido(Long numeroPedido, String token, JwtUtils jwtUtils) {
        try {
            // Comprobar el email del token con el del usuario
            String emailFromToken = obtenerEmailDelToken(token, jwtUtils);
            Optional<UsuarioModelo> usuarioModelo = buscarUsuarioPorEmail(emailFromToken);

            if (!usuarioModelo.isPresent()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al cancelar el pedido: Usuario no encontrado");
            } else {
                UsuarioModelo usuario = usuarioModelo.get();
                // Busco el pedido por su número
                Optional<PedidosModelo> numPedido = pedidoRepositorio.findByNumPedido(numeroPedido);

                // Verifico que el numero de pedido exista
                if (numPedido.isPresent()) {
                    PedidosModelo pedido = numPedido.get();
                    // Verifico si el pedido pertenece al usuario actual
                    if (pedido.getUsuario().equals(usuario)) {
                        // Verifico si el pedido está en estado "PENDIENTE"
                        if (pedido.getEstado().equals(EPedido.PENDIENTE.toString())) {
                            // Obtengo la lista de productos pedidos del pedido
                            List<ProductoPedidoDTO> productosPedidos = pedido.getProductos();

                            // Itero sobre cada producto pedido
                            for (ProductoPedidoDTO productoPedido : productosPedidos) {
                                // Obtenego el ID del producto del pedido
                                String productoId = productoPedido.get_idProducto();

                                // Busco el producto en la base de datos
                                Optional<ProductoModelo> productoOptional = productoRepositorio.findById(productoId);
                                // Si el producto esta en la base de datos
                                if (productoOptional.isPresent()) {
                                    ProductoModelo producto = productoOptional.get();
                                    // Sumo la cantidad pedida al stock del producto, ya que va se va a cancelar el
                                    // pedido
                                    // esa cantidad pedida, esta almacenada dentro de productos pedidos
                                    int cantidadPedida = productoPedido.getCantidadPedida();
                                    // Verifico si la cantidad pedida es mayor que 0
                                    if (cantidadPedida > 0) {
                                        producto.setDisponibilidadProducto(true);
                                    }
                                    producto.setCantidadProducto(producto.getCantidadProducto() + cantidadPedida);

                                    // Guardar el producto actualizado en la base de datos
                                    productoRepositorio.save(producto);
                                } else {
                                    // Cuando el producto no se encuentra en la base de datos
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                            .body("Producto no encontrado en la base de datos con ID: " + productoId);
                                }
                            }
                            // Cambio el estado del pedido y le enviare un correo al usuario diciendole que
                            // su pedido se ha cancelado exitosamente
                            pedido.setEstado(EPedido.CANCELADO.toString());

                            // Obtengo el email del usuario del pedido para enviar el email
                            String email = pedido.getUsuario().getEmail();

                            // Envio el email al usuario
                            resend.enviarEmailPedidoCanceladoUsuario(email);

                            // Guardo el pedido con el nuevo estado
                            pedidoRepositorio.save(pedido);

                            return ResponseEntity.ok("Pedido cancelado exitosamente");
                        } else {

                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Pedido no cancelable: ya ha sido cancelado o tiene otro estado.");
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Error al cancelar el pedido: Solo puedes cancelar tus pedidos");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Error al cancelar el pedido: Pedido no encontrado");
                }
            }
        } catch (RuntimeException e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al cancelar el pedido: " + e.getMessage());
        }
    }

    // METODO PARA VER TODOS LOS PEDIDOS QUE TIENE EL USUARIO
    @Override
    public ResponseEntity<List<PedidoInfoDTO>> historialPedidos(String token, JwtUtils jwtUtils) {
        try {
            String emailFromToken = obtenerEmailDelToken(token, jwtUtils);
            Optional<UsuarioModelo> usuarioModelo = buscarUsuarioPorEmail(emailFromToken);

            if (!usuarioModelo.isPresent()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            UsuarioModelo usuario = usuarioModelo.get();
            List<PedidosModelo> pedidos = pedidoRepositorio.findByUsuario(usuario);

            List<PedidoInfoDTO> pedidosDTO = pedidos.stream()
                    .map(this::mapPedidoToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(pedidosDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    // METODO PARA MAPPEAR LOS PEDIDOS A UN DTO
    private PedidoInfoDTO mapPedidoToDTO(PedidosModelo pedido) {
        PedidoInfoDTO pedidoDTO = new PedidoInfoDTO();
        pedidoDTO.set_id(pedido.get_id());
        pedidoDTO.setFechaPedido(pedido.getFechaPedido());
        pedidoDTO.setFechaEnvio(pedido.getFechaEnvio());
        pedidoDTO.setProductos(mapProductosToDTO(pedido.getProductos()));
        pedidoDTO.setNumPedido(pedido.getNumPedido().intValue());
        pedidoDTO.setTipoPago(pedido.getTipoPago());
        pedidoDTO.setEstado(pedido.getEstado());
        pedidoDTO.setDireccionEnvio(pedido.getDireccionCompletaEnvio());
        return pedidoDTO;
    }

    // METODO PARA MAPPEAR LOS PRODUCTOS A UN DTO
    private List<ProductoPedidoDTO> mapProductosToDTO(List<ProductoPedidoDTO> productos) {
        return productos.stream()
                .map(producto -> {
                    ProductoPedidoDTO productoDTO = new ProductoPedidoDTO();
                    productoDTO.set_idProducto(producto.get_idProducto());
                    productoDTO.setNombre(producto.getNombre());
                    productoDTO.setMarca(producto.getMarca());
                    productoDTO.setPrecioProducto(producto.getPrecioProducto());
                    productoDTO.setCategoria(producto.getCategoria());
                    productoDTO.setCantidadPedida(producto.getCantidadPedida());
                    return productoDTO;
                })
                .collect(Collectors.toList());
    }
}
