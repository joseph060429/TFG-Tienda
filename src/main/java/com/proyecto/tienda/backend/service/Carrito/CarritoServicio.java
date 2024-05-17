package com.proyecto.tienda.backend.service.Carrito;


import java.util.List;

import org.springframework.http.ResponseEntity;

import com.proyecto.tienda.backend.DTO.DTOCarrito.ProductoCarrito;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;


public interface CarritoServicio {
    
    ResponseEntity<String> agregarProductoAlCarrito(String token, JwtUtils jwtUtils, String productoId, int cantidad);

    ResponseEntity <String> eliminarProductoDelCarrito(String token, JwtUtils jwtUtils, String _idCarrito);

    ResponseEntity<List<ProductoCarrito>> obtenerCarritoUsuario(String token, JwtUtils jwtUtils, String productoId, int nuevaCantidad);
    
}
