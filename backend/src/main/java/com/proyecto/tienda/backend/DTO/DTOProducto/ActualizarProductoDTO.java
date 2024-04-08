package com.proyecto.tienda.backend.DTO.DTOProducto;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //Genera los Getters y Setters
@AllArgsConstructor //Genera el constructor
@NoArgsConstructor //Genera el constructor vacío
public class ActualizarProductoDTO {


   
    @Pattern(regexp = "^(Portatil|Sobremesa|Componentes)$", message = "La categoría debe ser 'Portatil', 'Sobremesa' o 'Componentes'")
    private String categoriaProducto;

    @Pattern(regexp = "^(?!\\s)(?=\\S)(.{2,70})(?!\\s)$", message = "El nombre del producto debe tener entre 2 y 70 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String nombreProducto;

     @Pattern(regexp = "^\\S.{0,}$", message = "La descripción del producto debe tener como mínimo 2 caracteres y no puede empezar con espacio en blanco")
    private String descripcionProducto;

    
    @Positive(message = "El precio del producto debe ser un número positivo")
    private Double precioProducto;

    // Manejarlo bien que cuando la cantidad este a 0 poner disponiblidad a false
    private boolean disponibilidadProducto = true; // True si queda disponible

    // @NotNull(message = "La cantidad del producto no puede estar en blanco")
    @PositiveOrZero(message = "La cantidad del producto debe ser un número positivo o 0")
    private Integer cantidadProducto;

    
    @Pattern(regexp = "^(?!\\s)(?=\\S)(.{2,50})(?!\\s)$", message = "La marca del producto debe tener entre 2 y 50 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String marcaProducto;

    @Pattern(regexp = "^\\S.{0,}$", message = "Las especificaciones tecnicas del producto debe tener como mínimo 2 caracteres y no puede empezar con espacio en blanco")
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
