package com.proyecto.tienda.backend.service.Carrito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.proyecto.tienda.backend.DTO.DTOCarrito.ProductoCarrito;
import com.proyecto.tienda.backend.models.CarritoModelo;
import com.proyecto.tienda.backend.models.ProductoModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.CarritoRepositorio;
import com.proyecto.tienda.backend.repositorios.ProductoRepositorio;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;

@Service
public class CarritoServicioImpl implements CarritoServicio {

    @Autowired
    private CarritoRepositorio carritoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    // METODO PARA AGREGAR UN PRODUCTO AL CARRITO
    @Override
    public ResponseEntity<String> agregarProductoAlCarrito(String token, JwtUtils jwtUtils, String productoId,
            int cantidad) {
        String jwtToken = token.replace("Bearer ", "");
        String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);

        Optional<UsuarioModelo> usuarioOptional = usuarioRepositorio.findByEmail(emailFromToken);

        if (!usuarioOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        UsuarioModelo usuario = usuarioOptional.get();

        // Buscar el producto por su ID
        Optional<ProductoModelo> productoOptional = productoRepositorio.findById(productoId);
        // Si no existe el ID del producto devuelvo un mensaje de producto no encontrado
        if (!productoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }

        ProductoModelo producto = productoOptional.get();

        // Verificar que la cantidad solicitada sea menor o igual a la disponible
        if (cantidad > producto.getCantidadProducto()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cantidad solicitada mayor que la disponible");
        }

        // Busco si el producto ya está en el carrito de ese usuario para no volver a
        // meterlo
        Optional<CarritoModelo> carritoOptional = carritoRepositorio.findByIdUsuarioAndIdProducto(usuario.get_id(),
                productoId);
        if (carritoOptional.isPresent()) {
            return ResponseEntity.badRequest().body("El producto ya está en el carrito");
        }

        // Creo una nueva entrada del carrito
        CarritoModelo carrito = CarritoModelo.builder()
                .idUsuario(usuario.get_id())
                // .email(usuario.getEmail())
                .idProducto(productoId)
                // .nombreProducto(producto.getNombreProducto())
                // .marcaProducto(producto.getMarcaProducto())
                // .precioProducto(producto.getPrecioProducto())
                // .imagenProducto(producto.getImagenProducto())
                .cantidadAnadidaAlCarrito(cantidad)
                .build();

        // Añado el id al usuario
        if (carrito.get_id() == null) {
            carrito.set_id(UUID.randomUUID().toString());
        }

        // Guardo el carrito en la base de datos
        carritoRepositorio.save(carrito);

        return ResponseEntity.ok("Producto agregado al carrito");
    }

    // METODO PARA ELIMINAR PRODUCTOS DEL CARRITO
    @Override
    public ResponseEntity<String> eliminarProductoDelCarrito(String token, JwtUtils jwtUtils, String _idCarrito) {
        String jwtToken = token.replace("Bearer ", "");
        String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);

        // Busco al usuario en el repositorio por el email extraído
        Optional<UsuarioModelo> usuarioOptional = usuarioRepositorio.findByEmail(emailFromToken);

        // Verifico si el usuario existe
        if (usuarioOptional.isPresent()) {
            UsuarioModelo usuario = usuarioOptional.get();
            // Busco el carrito por su ID.
            Optional<CarritoModelo> carritoOptional = carritoRepositorio.findById(_idCarrito);

            // Verifico si el carrito existe
            if (carritoOptional.isPresent()) {
                CarritoModelo carrito = carritoOptional.get();
                if (carrito.getIdUsuario().equals(usuario.get_id())) {
                    // Si el carrito existe y el usuario coincide, elimino el carrito
                    carritoRepositorio.delete(carrito);
                    return ResponseEntity.ok("Producto eliminado del carrito correctamente");
                } else {
                    // Si el carrito existe pero el usuario no coincide, devuelvo un error
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("No estás autorizado para eliminar este producto del carrito");
                }
            } else {
                // Si el carrito no se encuentra devuelvo una respuesta de error de no
                // encontrado.
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado en el carrito");
            }
        } else {
            // Si el usuario no se encuentra devuelvo una respuesta de error de no
            // autorizado
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }
    }

    // METODO PARA TRAERME EL CARRITO DE CADA USUARIO
    @Override
    public ResponseEntity<List<ProductoCarrito>> obtenerCarritoUsuario(String token, JwtUtils jwtUtils, String productoId, int nuevaCantidad) {
        // Elimino el prefijo "Bearer " del token JWT.
        String jwtToken = token.replace("Bearer ", "");
        // Luego, extraigo el email del token usando JwtUtils.
        String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);

        // Busco al usuario en el repositorio por el email extraído.
        Optional<UsuarioModelo> usuarioOptional = usuarioRepositorio.findByEmail(emailFromToken);

        // Verifico si el usuario existe.
        if (usuarioOptional.isPresent()) {
            // Obtengo el usuario de la opción.
            UsuarioModelo usuario = usuarioOptional.get();
            // Busco los productos en el carrito del usuario por su ID.
            List<CarritoModelo> carrito = carritoRepositorio.findByIdUsuario(usuario.get_id());

            // Verifico si el carrito no está vacío.
            if (!carrito.isEmpty()) {
                // Creo una lista para los detalles del producto.
                List<ProductoCarrito> productos = new ArrayList<>();

                // Recorro cada item del carrito.
                for (CarritoModelo item : carrito) {
                    // Obtengo el producto por su ID.
                    Optional<ProductoModelo> productoOptional = productoRepositorio.findById(item.getIdProducto());

                    // Verifico si el producto existe.
                    if (productoOptional.isPresent()) {
                        // Obtengo el producto de la opción.
                        ProductoModelo producto = productoOptional.get();

                        // Si el producto coincide con el ID y se proporciona una nueva cantidad,
                        // actualizo la cantidad.
                        if (producto.get_id().equals(productoId) && nuevaCantidad > 0) {
                            item.setCantidadAnadidaAlCarrito(nuevaCantidad);
                            carritoRepositorio.save(item); // Guardo los cambios en el carrito
                        }
                        // Mapeo los datos del producto a un DTO incluyendo idUsuario y
                        // cantidadAnadidaAlCarrito.
                        ProductoCarrito productoDTO = new ProductoCarrito(
                                producto.get_id(),
                                producto.getNombreProducto(),
                                producto.getMarcaProducto(),
                                producto.getPrecioProducto(),
                                producto.getImagenProducto(),
                                item.getIdUsuario(),
                                item.getCantidadAnadidaAlCarrito());

                        // Añado el producto a la lista.
                        productos.add(productoDTO);
                    }
                }

                // Devuelvo la lista de productos en el carrito.
                return ResponseEntity.ok(productos);
            } else {
                // Si el carrito está vacío, devuelvo una respuesta de no encontrado.
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }
        } else {
            // Si el usuario no se encuentra, devuelvo una respuesta de no autorizado.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }
    }

}