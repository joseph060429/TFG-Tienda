package com.proyecto.tienda.backend.controllers.Producto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.tienda.backend.DTO.DTOProducto.CrearProductoDTO;
import com.proyecto.tienda.backend.service.ProductoServicio.ProductoServicio;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class ProductoControllers {

    @Autowired
    private ProductoServicio productoServicio;


    //Crear producto
    @PostMapping("/crear-producto")
    public ResponseEntity<?> crearProducto(@Valid @RequestBody CrearProductoDTO crearProductoDTO) {
        try {
            // Validar y crear el producto
            ResponseEntity<?> response = productoServicio.crearProducto(crearProductoDTO);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Manejar cualquier excepción general y devolver una respuesta de error
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

}
