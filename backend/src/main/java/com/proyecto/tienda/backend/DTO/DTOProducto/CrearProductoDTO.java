package com.proyecto.tienda.backend.DTO.DTOProducto;


import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrearProductoDTO {

    @Id
    private String _id;

    @NotBlank(message = "La categoria del producto no puede estar en blanco")
    private String categoriaProducto;

    @NotBlank(message = "El nombre del producto no puede estar en blanco")
    private String nombreProducto;

    @NotBlank(message = "La descripcion del producto no puede estar en blanco")
    private String descripcionProducto;

    @NotNull(message = "El precio del producto no puede estar en blanco")
    @Positive(message = "El precio del producto debe ser un número positivo")
    private Double precioProducto;

    // Manejarlo bien que cuando la cantidad este a 0 poner disponiblidad a false
    private boolean disponibilidadProducto = true; // True si queda disponible

    @NotNull(message = "La cantidad del producto no puede estar en blanco")
    @Positive(message = "La cantidad del producto debe ser un número positivo")
    private Integer cantidadProducto;

    @NotBlank(message = "La marca del producto no puede estar en blanco")
    private String marcaProducto;

    @NotBlank(message = "Las especificaciones tecnicas del producto no puede estar en blanco")
    private String especificacionesTecnicas;

    @NotBlank(message = "La imagen del producto no puede estar en blanco")
    private String imagenProducto;

    private String identificador;

    public void ajustarDisponibilidad() {
        if (cantidadProducto == 0) {
            disponibilidadProducto = false;
        }
    }

    

}
