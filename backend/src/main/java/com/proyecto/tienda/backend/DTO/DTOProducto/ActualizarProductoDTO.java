package com.proyecto.tienda.backend.DTO.DTOProducto;

// import com.proyecto.tienda.backend.UtilEnum.EProducto;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //Genera los Getters y Setters
@AllArgsConstructor //Genera el constructor
@NoArgsConstructor //Genera el constructor vacío
public class ActualizarProductoDTO {


   
    @Pattern(regexp = "^(Portatil|Sobremesa|Componentes)$", message = "La categoría debe ser 'Portatil', 'Sobremesa' o 'Componentes'")
    private String categoriaProducto;

    @Size(min = 2, max = 70, message = "El nombre del producto no puede tener menos de 2 y más de 70 caracteresesss")
    private String nombreProducto;

    @Size(min = 2, message = "La descripcion del producto no puede tener menos de 2 caracteres")
    private String descripcionProducto;

    
    @Positive(message = "El precio del producto debe ser un número positivo")
    private Double precioProducto;

    // Manejarlo bien que cuando la cantidad este a 0 poner disponiblidad a false
    private boolean disponibilidadProducto = true; // True si queda disponible

    // @NotNull(message = "La cantidad del producto no puede estar en blanco")
    @PositiveOrZero(message = "La cantidad del producto debe ser un número positivo o 0")
    private Integer cantidadProducto;

    
    @Size(min = 2, max = 50, message = "La marca del producto no puede tener menos de 2 y más de 50 caracteres")
    private String marcaProducto;

    @Size(min = 2, message = "Las especificaciones tecnicas del producto no puede tener menos de 2 caracteres")
    private String especificacionesTecnicas;

    // @NotBlank(message = "La imagen del producto no puede estar en blanco")
    // @Size(min = 2, message = "La imagen del producto no puede tener menos de 2 caracteres")
    private String imagenProducto;

    private String identificador;

    // public void setCategoriaProducto(EProducto eProducto) {
    //     this.categoriaProducto = eProducto.toString();
    // }

    // public EProducto getCategoria() {
    //     return EProducto.valueOf(this.categoriaProducto);
    // }
}
