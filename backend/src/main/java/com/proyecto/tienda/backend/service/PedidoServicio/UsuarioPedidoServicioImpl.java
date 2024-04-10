package com.proyecto.tienda.backend.service.PedidoServicio;

import java.util.ArrayList;
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
import com.proyecto.tienda.backend.DTO.DTOPedido.PedidoInfoDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.ProductoPedidoDTO;
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

            PedidosModelo pedido = crearNuevoPedido(crearPedidoDTO, usuario);

            // Establezco el numero de telefono, lo hice para evitar que el repartidor vaya a casa y si la persona no esta, tenga un sitio donde dejarlo
            Long numTele = crearPedidoDTO.getNumTelefono();
            pedido.setNumTelefono(numTele);

            // Añado la direccion de envio
            String direccionEnvio = anadirDireccionEnvio(crearPedidoDTO.getCodigoPostal(),
                    crearPedidoDTO.getDireccion(), crearPedidoDTO.getProvincia(), crearPedidoDTO.getNumero(),
                    crearPedidoDTO.getPiso(), crearPedidoDTO.getPuerta(), usuario);

            ResponseEntity<?> resultadoPagoValidacion = validarTipoPagoYSetearlo(crearPedidoDTO.getTipoPago(), pedido);

            if (resultadoPagoValidacion != null) {
                return resultadoPagoValidacion;
            }
            
            // Genero una nueva lista con los productos pedidos
            List<ProductoPedidoDTO> listaNueva = generarListaProductosPedido(productosModelo);
            pedido.setProductos(listaNueva);

            // Establecezco la fecha del pedido
            crearPedidoDTO.setFechaPedido();
            pedido.setFechaPedido(crearPedidoDTO.getFechaPedido());

            // Establezco la direccion del pedido
            pedido.setDireccionEnvio(direccionEnvio);

            // 
            ses.setAttribute("pedido", pedido);

            return paypalServicio.hacerPago(pedido, ses);

        } catch (RuntimeException e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear el pedido: " + e.getMessage());
        }
    }

    // IMPLEMENTACION DEL METODO PARA LA DIRECCION DE ENVIO
    public String anadirDireccionEnvio(String codigoPostal, String direccion, String provincia,
            String numero, String piso, String puerta, UsuarioModelo usuario) {
        try {
            // Agrego la dirección de envío al usuario
            String direccionEnvio = usuario.agregarDireccionCompleta(direccion, provincia, puerta, codigoPostal, piso,
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
                // Significa que todos los productos que se pidieron estan en stock, manejo la resta cuando el pago es exitoso.
                for (ProductoPedidoDTO productoPedido : listaPedidoUsuario) {
                    ProductoModelo encontrado = productoRepositorio.findBy_id(productoPedido.get_idProducto());
                    // restarCantidadProducto(encontrado.get_id(), productoPedido.getCantidadPedida());
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
                        .body("Error al eliminar el pedido: Usuario no encontrado");
            } else {
                UsuarioModelo usuario = usuarioModelo.get();
                // BuscO el pedido por su número
                Optional<PedidosModelo> numPedido = pedidoRepositorio.findByNumPedido(numeroPedido);

                // Verifico que el numero de pedido exista
                if (numPedido.isPresent()) {
                    PedidosModelo pedido = numPedido.get();
                    // Verifico si el pedido pertenece al usuario actual
                    if (pedido.getUsuario().equals(usuario)) {
                        // Verifico si el pedido está en estado "PENDIENTE"
                        if (pedido.getEstado().equals(EPedido.PENDIENTE.toString())) {
                            // Obtener la lista de productos pedidos del pedido
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
                            // Elimino el pedido
                            pedidoRepositorio.delete(pedido);

                            return ResponseEntity.ok("Pedido eliminado exitosamente");
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Error al eliminar el pedido: El pedido ya ha sido ENVIADO");
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Error al eliminar el pedido: Solo puedes eliminar tus pedidos");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Error al eliminar el pedido: Pedido no encontrado");
                }
            }
        } catch (RuntimeException e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al eliminar el pedido: " + e.getMessage());
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
        pedidoDTO.setDireccionEnvio(pedido.getDireccionEnvio());
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
