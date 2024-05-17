package com.proyecto.tienda.backend.controllers.Carrito;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.tienda.backend.DTO.DTOCarrito.ProductoCarrito;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.Carrito.CarritoServicio;

@RestController
@RequestMapping("/carrito")
@PreAuthorize("hasAnyRole('USER') || hasAnyRole('ADMIN')")
public class CarritoController {

    @Autowired
    private CarritoServicio carritoServicio;

    @Autowired
    private JwtUtils jwtUtils;

    // CONTROLADOR PARA AÑADIR PRODUCTOS AL CARRITO
    @PostMapping("/anadirAlCarrito")
    public ResponseEntity<String> agregarProductoAlCarrito(
            @RequestHeader("Authorization") String token,
            @RequestParam String productoId,
            @RequestParam int cantidad) {

        return carritoServicio.agregarProductoAlCarrito(token, jwtUtils, productoId, cantidad);
    }

    // CONTROLADOR PARA ELIMINAR PRODUCTOS DEL CARRITO
    @DeleteMapping("/eliminarDelCarrito")
    public ResponseEntity<String> eliminarProductoDelCarrito(
            @RequestHeader("Authorization") String token,
            @RequestParam String _idCarrito) {
        return carritoServicio.eliminarProductoDelCarrito(token, jwtUtils, _idCarrito);
    }

    @GetMapping("/verCarrito")
    public ResponseEntity<List<ProductoCarrito>> obtenerCarritoUsuario(@RequestHeader("Authorization") String token) {
        return carritoServicio.obtenerCarritoUsuario(token, jwtUtils);
    }

}
