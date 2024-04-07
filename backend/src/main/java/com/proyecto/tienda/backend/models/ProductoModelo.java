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
public class ProductoModelo {

    @Id
    private String _id;

    @NotBlank(message = "La categoria del producto no puede estar en blanco")
    @Pattern(regexp = "^(Portatil|Sobremesa|Componentes)$", message = "La categoría debe ser 'Portatil', 'Sobremesa' o 'Componentes'")
    private String categoriaProducto;

    @NotBlank(message = "El nombre del producto no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)(?=\\S)(.{2,70})(?!\\s)$", message = "El nombre del producto debe tener entre 2 y 70 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String nombreProducto;

    @NotBlank(message = "La descripcion del producto no puede estar en blanco")
    @Pattern(regexp = "^\\S.{0,}$", message = "La descripción del producto debe tener como mínimo 2 caracteres y no puede empezar con espacio en blanco")
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
    @Pattern(regexp = "^(?!\\s)(?=\\S)(.{2,50})(?!\\s)$", message = "La marca del producto debe tener entre 2 y 50 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String marcaProducto;

    @Pattern(regexp = "^\\S.{0,}$", message = "Las especificaciones tecnicas del producto debe tener como mínimo 2 caracteres y no puede empezar con espacio en blanco")
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


// StringBuilder mensajeExcepcion = new StringBuilder("Ha pedido más productos del stock disponible en algunos de los productos: ");
//         for (String nombreProducto : productosNoDisponibles) {
//             mensajeExcepcion.append(nombreProducto).append(", ");
//         }
//         // Eliminar la coma y el espacio extra al final
//         mensajeExcepcion.delete(mensajeExcepcion.length() - 2, mensajeExcepcion.length());
//         throw new RuntimeException(mensajeExcepcion.toString());