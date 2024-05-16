package com.proyecto.tienda.backend.service.Carrito;


import org.springframework.http.ResponseEntity;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;


public interface CarritoServicio {
    
    ResponseEntity<String> agregarProductoAlCarrito(String token, JwtUtils jwtUtils, String productoId, int cantidad);
    
}
