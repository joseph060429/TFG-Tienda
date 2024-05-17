package com.proyecto.tienda.backend.models;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getters y setters
@AllArgsConstructor // constructor con todos los atributos
@NoArgsConstructor // constructor sin atributos
@Builder
@Document(value = "Carrito")
public class CarritoModelo {

    @Id
    private String _id;

    // @DBRef
    @Field("id_usuario")
    // private UsuarioModelo usuario;

    private String idUsuario;
    // private String email;

    // @DBRef
    // private List<ProductoModelo> productos;

    @Field("id_producto")
    private String idProducto;

    // private String nombreProducto;

    // private String marcaProducto;

    // private Double precioProducto;

    // private String imagenProducto;

    private int cantidadAnadidaAlCarrito;

}
