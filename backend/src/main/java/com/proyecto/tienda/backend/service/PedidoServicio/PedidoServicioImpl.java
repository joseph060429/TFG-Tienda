package com.proyecto.tienda.backend.service.PedidoServicio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonPrimitive;
import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.CrearPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.CrearUsuarioDTO;
import com.proyecto.tienda.backend.UtilEnum.EPedido;
import com.proyecto.tienda.backend.UtilEnum.EPedidoPago;
import com.proyecto.tienda.backend.UtilEnum.ERol;
import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.models.ProductoModelo;
import com.proyecto.tienda.backend.models.RolesModelo;
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

            // Compruebo que el email del token coincida con el del usuario que quiera hacer
            // el pedido para seguir adelante
            String jwtToken = token.replace("Bearer ", "");

            // Obtengo el correo electrónico del token JWT utilizando JwtUtils
            String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);

            // Verifico si el correo electrónico del token coincide con algún usuario
            // registrado
            Optional<UsuarioModelo> usuarioOptional = usuarioRepositorio.findByEmail(emailFromToken);

            // Si existe el email sigo para adelante
            if (usuarioOptional.isPresent()) {
                // Traigo el objeto usuario
                UsuarioModelo usuario = usuarioOptional.get();

                // Creo un nuevo pedido
                PedidosModelo pedido = new PedidosModelo();

                // Seteo el id del pedido
                if (pedido.get_id() == null) {
                    pedido.set_id(UUID.randomUUID().toString());
                }

                // Seteo el usuario en el pedido, ya que me pintara el id del usuario
                pedido.setUsuario(usuario);

                // Seteo la fecha de pedido usando el método que tengo en CrearPedidoDTO
                crearPedidoDTO.setFechaPedido();
                pedido.setFechaPedido(crearPedidoDTO.getFechaPedido());

                // Establezco el numero de pedido
                List<PedidosModelo> listaUltimosPedidos = pedidoRepositorio.findTopByOrderByNumPedidoDesc();
                Long nuevoNumeroPedido = 1L; // Lo pongo por defecto si no hay numeros anteriores

                if (listaUltimosPedidos != null && !listaUltimosPedidos.isEmpty()) {
                    nuevoNumeroPedido = listaUltimosPedidos.stream()
                            .mapToLong(PedidosModelo::getNumPedido)
                            .max()
                            .orElse(0L) + 1;
                }

                // Almaceno el nuevo número de pedido en mi pedidoDTO y en el modelo pedido
                crearPedidoDTO.setNumPedido(nuevoNumeroPedido);
                pedido.setNumPedido(crearPedidoDTO.getNumPedido());

                // Hago el estado del pedido por defecto en pendiente
                pedido.setEstado(EPedido.PENDIENTE.toString());

                // Escogere el metodo de pago que ha seleccionado el usuario en el body, solo
                // será lo que tengo definido en el enum
                String tipoPago = crearPedidoDTO.getTipoPago();
                if (tipoPago == null || tipoPago.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Error al crear el pedido: El tipo de pago no puede estar en blanco");
                } else {
                    try {
                        EPedidoPago enumValue = EPedidoPago.valueOf(tipoPago.toUpperCase());
                        pedido.setTipoPago(enumValue.name()); // Seteo el tipo de pago
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Error al crear el pedido: El tipo de pago no es válido");
                    }
                }

                // Creo una nueva lista para almacenar los productos del pedido
                List<ProductoPedido> listaNueva = new ArrayList<ProductoPedido>();

                // Itero a través de la lista original de productos
                for (ProductoModelo productoModelo : productosModelo) {
                    // Busco el producto correspondiente en la base de datos utilizando su ID
                    ProductoModelo productoEncontrado = productoRepositorio.findBy_id(productoModelo.get_id());

                    // Disminuyo la cantidad de productos depende la cantidad que haya seleccionado
                    // el usuario
                    int restarResultado = restarCantidadProducto(productoModelo.get_id(),
                            productoModelo.getCantidadProducto());
                    if (restarResultado == 1) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Ha pedido más productos del stock disponible en algunos de los productos: " + productoEncontrado.getNombreProducto());
                    } else if (restarResultado == 2) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontro el producto");
                    }

                    // Agrego el nuevo producto a la nueva lista, y le meto los campos que creo que
                    // son importantes
                    listaNueva.add(new ProductoPedido(productoEncontrado.get_id(),
                            productoEncontrado.getNombreProducto(), productoEncontrado.getMarcaProducto(),
                            productoEncontrado.getPrecioProducto(),
                            productoEncontrado.getCategoria().toString(), productoModelo.getCantidadProducto()));

                }

                // Seteo la nueva lista de productos en el campo productos del pedido
                pedido.setProductos(listaNueva);

                // Guardo el pedido
                pedidoRepositorio.save(pedido);

                return ResponseEntity.ok("Pedido creado exitosamente");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al crear el pedido: Usuario no encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el pedido");
        }

    }

    // METODO PARA RESTAR LA CANTIDAD DE PRODUCTOS
    public int restarCantidadProducto(String productoId, int cantidadRestar) {

        // Busco el producto por el Id
        ProductoModelo productoEncontrado = productoRepositorio.findBy_id(productoId);

        // Si el producto existe
        if (productoEncontrado != null) {
            // Cojo la cantidad actual de ese producto
            Integer cantidadActual = productoEncontrado.getCantidadProducto();

            // Verifico si la cantidad actual es suficiente a la cantidad que quiere pedir
            // el usuario
            if (cantidadActual >= cantidadRestar) {
                // Si la cantidad actual es mayor o igual, resta la cantidad proporcionada por
                // el usuario
                productoEncontrado.setCantidadProducto(cantidadActual - cantidadRestar);

                // Si la cantidad actual es 0, cambia el estado de disponibilidad a false
                if (productoEncontrado.getCantidadProducto() == 0) {
                    productoEncontrado.setDisponibilidadProducto(false);
                }

                // Guarda el producto actualizado en la base de datos
                productoRepositorio.save(productoEncontrado);
                // Todo bien
                return 0;
            } else {
                // Cantidad solicitada mayor que la de stock
                return 1;
            }
        }
        // No encuentra producto
        return 2;
    }

}
