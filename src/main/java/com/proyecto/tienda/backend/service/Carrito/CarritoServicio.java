package com.proyecto.tienda.backend.service.Carrito;

import org.springframework.http.ResponseEntity;

import com.proyecto.tienda.backend.models.CarritoModelo;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;


public interface CarritoServicio {
    
    ResponseEntity<CarritoModelo>  agregarProductoAlCarrito(String token, JwtUtils jwtUtils, String productoId, int cantidad);

    ResponseEntity <String> eliminarProductoDelCarrito(String token, JwtUtils jwtUtils, String _idCarrito);

    public ResponseEntity<?> obtenerCarritoUsuario(String token, JwtUtils jwtUtils, String productoId, int nuevaCantidad);
    
}
