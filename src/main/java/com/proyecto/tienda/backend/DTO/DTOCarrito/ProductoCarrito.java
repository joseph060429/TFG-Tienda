package com.proyecto.tienda.backend.DTO.DTOCarrito;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoCarrito {

    private String productoId;
    private String nombreProducto;
    private String marcaProducto;
    private Double precioProducto;
    private String imagenProducto;
    private String idUsuario;
    private int cantidadAnadidaAlCarrito;
    
}
