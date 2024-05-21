package com.proyecto.tienda.backend.DTO.DTOCarrito;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
    private String idCarrito;
    private Double totalCarrito;

    @NotNull(message = "La cantidad del producto no puede estar en blanco")
    @PositiveOrZero(message = "La cantidad del producto debe ser un número positivo o 0")
    private int cantidadAnadidaAlCarrito;

}
