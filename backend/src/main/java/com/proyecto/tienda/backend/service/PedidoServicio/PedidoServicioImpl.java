package com.proyecto.tienda.backend.service.PedidoServicio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.proyecto.tienda.backend.DTO.DTOPedido.CrearPedidoDTO;
import com.proyecto.tienda.backend.ErroresGlobales.ExcepcionesPersonalizadas;
import com.proyecto.tienda.backend.UtilEnum.EPedido;
import com.proyecto.tienda.backend.UtilEnum.EPedidoPago;
import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.models.ProductoModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.repositorios.ProductoRepositorio;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.util.ProductoPedido;

@Service
public class PedidoServicioImpl implements PedidoServicio {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    // IMPLEMENTACION DEL METODO PARA CREAR EL PEDIDO
    @Transactional
    @Override
    public ResponseEntity<?> crearPedido(CrearPedidoDTO crearPedidoDTO, String token, JwtUtils jwtUtils,
            List<ProductoModelo> productosModelo) {

        try {
            String emailFromToken = obtenerEmailDelToken(token, jwtUtils);
            Optional<UsuarioModelo> usuarioOptional = buscarUsuarioPorEmail(emailFromToken);

            if (!usuarioOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al crear el pedido: Usuario no encontrado");
            }

            UsuarioModelo usuario = usuarioOptional.get();
            PedidosModelo pedido = crearNuevoPedido(crearPedidoDTO, usuario);
            ResponseEntity<?> resultadoPagoValidacion = validarTipoPagoYSetearlo(crearPedidoDTO.getTipoPago(), pedido);

            if (resultadoPagoValidacion != null) {
                return resultadoPagoValidacion;
            }

            List<ProductoPedido> listaNueva = generarListaProductosPedido(productosModelo);
            pedido.setProductos(listaNueva);

            // Establecezco la fecha del pedido
            crearPedidoDTO.setFechaPedido();
            pedido.setFechaPedido(crearPedidoDTO.getFechaPedido());

            guardarPedido(pedido);

            return ResponseEntity.ok("Pedido creado exitosamente");
        } catch (RuntimeException e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear el pedido: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el pedido");
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
        return pedido;
    }

    // IMPLEMENTACION DEL METODO PARA GUARDAR EL PEDIDO
    private void guardarPedido(PedidosModelo pedido) {
        pedidoRepositorio.save(pedido);
    }

    // IMPLEMENTACION DEL METODO PARA VALIDAR EL TIPO DE PAGO Y SETEARLO
    private ResponseEntity<?> validarTipoPagoYSetearlo(String tipoPago, PedidosModelo pedido) {
        if (tipoPago == null || tipoPago.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear el pedido: El tipo de pago no puede estar en blanco");
        } else {
            try {
                EPedidoPago enumValue = EPedidoPago.valueOf(tipoPago.toUpperCase());
                pedido.setTipoPago(enumValue.name());
                return null; // Tipo de pago válido
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error al crear el pedido: El tipo de pago no es válido");
            }
        }
    }

    //
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

    @Transactional
    private List<ProductoPedido> generarListaProductosPedido(List<ProductoModelo> productosModelo) {
        List<ProductoPedido> listaPedidoUsuario = new ArrayList<>(); // Lista para almacenar los productos del pedido
        List<String> productosNoDisponibles = new ArrayList<>(); // Lista para almacenar los nombres de los productos no
                                                                 // disponibles

        try {

            for (ProductoModelo productoModelo : productosModelo) {

                ProductoPedido productoPedido = null; // Crear un objeto ProductoPedido

                // Obtengo el producto por su ID
                ProductoModelo productoEncontrado = productoRepositorio.findBy_id(productoModelo.get_id());
                System.out.println("PRODUCTO ENCONTRADO: " + productoEncontrado);

                if (productoEncontrado == null) {
                    System.out.println("Uno o más productos no existen");
                    // todosDisponibles = false;
                    throw new RuntimeException("Uno o más productos no existen");
                } else {

                    if (!verificarCantidadPedidaValida(productoModelo, productoEncontrado, productosModelo)) {
                        // todosDisponibles = false;
                        // System.out.println("PRODUCTO MODELO " + productoEncontrado.get_id());
                        productosNoDisponibles.add(productoEncontrado.getNombreProducto());
                        throw new RuntimeException("La cantidad del producto "
                                + productoEncontrado.getNombreProducto().toUpperCase() + " debe ser mayor a 0");
                    } else {

                        productoPedido = new ProductoPedido(
                                productoEncontrado.get_id(),
                                productoEncontrado.getNombreProducto(),
                                productoEncontrado.getMarcaProducto(),
                                productoEncontrado.getPrecioProducto(),
                                productoEncontrado.getCategoria().toString(),
                                productoModelo.getCantidadProducto());
                        System.out.println("PRODUCTOS PEDIDOS " + productoPedido);

                    }
                    listaPedidoUsuario.add(productoPedido);
                }
            }

            int todosEnStock = 1;
            // Itero sobre la lista de productos pedidos
            for (ProductoPedido productoPedido : listaPedidoUsuario) {
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
                // Significa que es posible restar a todos los productos pedidos. quiere decir, hay stock para todo lo que se pide.
                for (ProductoPedido productoPedido : listaPedidoUsuario) {
                    ProductoModelo encontrado = productoRepositorio.findBy_id(productoPedido.get_idProducto());
                    restarCantidadProducto(encontrado.get_id(), productoPedido.getCantidadPedida());
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
    private boolean verificarCantidadPedidaValida(ProductoModelo productoModelo, ProductoModelo productoEncontrado,
            List<ProductoModelo> productosSolicitados) {
        try {
            if (productoModelo.getCantidadProducto() <= 0) {
                // Sumar la cantidad del producto al inventario de otros productos solicitados
                for (ProductoModelo productoSolicitado : productosSolicitados) {
                    if (productoSolicitado.get_id().equals(productoModelo.get_id())) {
                        // Si el producto se encuentra en la lista de productos solicitados, se suma la
                        // cantidad
                        sumarCantidadProducto(productoModelo.get_id(), productoModelo.getCantidadProducto());
                        continue; // No es necesario continuar buscando, ya se encontró y sumó la cantidad
                    }
                }
                return false;
            }
            return true; // La cantidad pedida es válida
        } catch (RuntimeException e) {
            // Manejo de excepciones
            e.getMessage();
            throw new RuntimeException("Error al verificar la cantidad pedida del producto");
        }
    }

    // METODO PARA SUMAR LA CANTIDAD DE PRODUCTOS
    private void sumarCantidadProducto(String productoId, int cantidadSumar) {

        try {
            // Busco el producto por su ID
            Optional<ProductoModelo> productoEncontradoOptional = productoRepositorio.findById(productoId);

            if (productoEncontradoOptional.isPresent()) {
                ProductoModelo productoEncontrado = productoEncontradoOptional.get();
                Integer cantidadActual = productoEncontrado.getCantidadProducto();

                // Sumo la cantidad suministrada a la cantidad actual del producto
                productoEncontrado.setCantidadProducto(cantidadActual + cantidadSumar);

                // Mantengo la disponibilidad si la cantidad actual es mayor que cero
                if (cantidadActual + cantidadSumar > 0) {
                    productoEncontrado.setDisponibilidadProducto(true);
                }

                // Guardo el producto actualizado en el repositorio
                productoRepositorio.save(productoEncontrado);
            } else {
                // Producto no fue encontrado
                System.out.println("Producto no encontrado");
            }
        } catch (Exception e) {
            // Manejo las excepciones
            e.printStackTrace();
            System.out.println("Error al sumar la cantidad del producto: " + e.getMessage());
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
        }catch (Exception e) {
            // Manejo de excepciones
            e.printStackTrace();
            return 3;
           
        }
    }

}
