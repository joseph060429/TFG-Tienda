package com.proyecto.tienda.backend.service.Carrito;

import java.util.ArrayList;
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

        // Busco el carrito o creo uno nuevo si no existe
        Optional<CarritoModelo> carritoOptional = carritoRepositorio.findByUsuario(usuario);
        CarritoModelo carrito;
        if (carritoOptional.isPresent()) {
            carrito = carritoOptional.get();
        } else {
            carrito = new CarritoModelo();
            carrito.setUsuario(usuario);
            carrito.setProductos(new ArrayList<>());
            carrito.setCantidadAnadidaAlCarrito(cantidad);
        }
        
        if (carrito.get_id() == null) {
            carrito.set_id(UUID.randomUUID().toString());
        }

        // Buscar el producto por su ID
        Optional<ProductoModelo> productoOptional = productoRepositorio.findById(productoId);
        if (!productoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }

        ProductoModelo producto = productoOptional.get();

        if (cantidad > producto.getCantidadProducto()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cantidad solicitada mayor que la disponible");
        }


        // Verifico si el producto ya está en el carrito para no añadirlo 2 veces
        boolean productoEnCarrito = false;
        for (ProductoModelo p : carrito.getProductos()) {
            if (p.get_id().equals(productoId)) {
                productoEnCarrito = true;
                break;
            }
        }

        if (productoEnCarrito) {
            return ResponseEntity.badRequest().body("El producto ya está en el carrito");
        } else {
            carrito.getProductos().add(producto);
        }

        // Guardar el carrito actualizado en la base de datos
        carritoRepositorio.save(carrito);

        return ResponseEntity.ok("Producto agregado al carrito");
    }

}
