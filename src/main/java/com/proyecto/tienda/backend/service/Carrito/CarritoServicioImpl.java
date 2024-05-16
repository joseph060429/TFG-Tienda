package com.proyecto.tienda.backend.service.Carrito;


import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

        // Busco si el producto ya está en el carrito de ese usuario para no volver a meterlo
        Optional<CarritoModelo> carritoOptional = carritoRepositorio.findByIdUsuarioAndIdProducto(usuario.get_id(),
                productoId);
        if (carritoOptional.isPresent()) {
            return ResponseEntity.badRequest().body("El producto ya está en el carrito");
        }

        // Creo una nueva entrada del carrito
        CarritoModelo carrito = CarritoModelo.builder()
                .idUsuario(usuario.get_id())
                .email(usuario.getEmail())
                .idProducto(productoId)
                .nombreProducto(producto.getNombreProducto())
                .marcaProducto(producto.getMarcaProducto())
                .precioProducto(producto.getPrecioProducto())
                .imagenProducto(producto.getImagenProducto())
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

}
