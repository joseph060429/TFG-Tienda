package com.proyecto.tienda.backend.models;

import org.springframework.data.annotation.Id;
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

    @Field("id_usuario")
    private String idUsuario;
   
    @Field("id_producto")
    private String idProducto;

    private int cantidadAnadidaAlCarrito;

}
