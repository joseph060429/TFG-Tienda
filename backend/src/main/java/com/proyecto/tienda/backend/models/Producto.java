package com.proyecto.tienda.backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.proyecto.tienda.backend.UtilEnum.EProducto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getters y setters
@AllArgsConstructor // constructor con todos los atributos
@NoArgsConstructor // constructor sin atributos
@Builder
@Document(value = "Producto")

public class Producto {

    @Id
    private String _id;

    @NotBlank(message = "La categoria del producto no puede estar en blanco")
    @Pattern(regexp = "^(Portatil|Sobremesa|Componentes)$", message = "La categoría debe ser 'Portatil', 'Sobremesa' o 'Componentes'")
    private String categoriaProducto;

    @NotBlank(message = "El nombre del producto no puede estar en blanco")
    @Size(min = 2, max = 70, message = "El nombre del producto no puede tener menos de 2 y más de 70 caracteres")
    private String nombreProducto;

    @NotBlank(message = "La descripcion del producto no puede estar en blanco")
    @Size(min = 2, message = "La descripcion del producto no puede tener menos de 2 caracteres")
    private String descripcionProducto;

    @NotNull(message = "El precio del producto no puede estar en blanco")
    @Positive(message = "El precio del producto debe ser un número positivo")
    private Double precioProducto;

    // Manejarlo bien que cuando la cantidad este a 0 poner disponiblidad a false
    private boolean disponibilidadProducto = true; // True si queda disponible

    @NotNull(message = "La cantidad del producto no puede estar en blanco")
    @PositiveOrZero(message = "La cantidad del producto debe ser un número positivo o 0")
    private Integer cantidadProducto;

    @NotBlank(message = "La marca del producto no puede estar en blanco")
    @Size(min = 2, max = 50, message = "La marca del producto no puede tener menos de 2 y más de 50 caracteres")
    private String marcaProducto;

    @Size(min = 2, message = "Las especificaciones tecnicas del producto no puede tener menos de 2 caracteres")
    @NotBlank(message = "Las especificaciones tecnicas del producto no puede estar en blanco")
    private String especificacionesTecnicas;

    @NotBlank(message = "La imagen del producto no puede estar en blanco")
    @Size(min = 2, message = "La imagen del producto no puede tener menos de 2 caracteres")
    private String imagenProducto;

    private String identificador;

    public void setCategoriaProducto(EProducto eProducto) {
        this.categoriaProducto = eProducto.toString();
    }

    public EProducto getCategoria() {
        return EProducto.valueOf(this.categoriaProducto);
    }

   

}
