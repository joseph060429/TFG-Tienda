package com.proyecto.tienda.backend.models;

import java.util.List;

import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.proyecto.tienda.backend.util.ProductoPedido;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getters y setters
@AllArgsConstructor // constructor con todos los atributos
@NoArgsConstructor // constructor sin atributos
@Builder
@Document(value = "Pedidos")
public class PedidosModelo {

    @Id
    private String _id;

    @DBRef
    @Field("id_usuario")
    private UsuarioModelo usuario;

    @NotNull(message = "La fecha del pedido no puede estar en blanco")
    private String fechaPedido;

    // @NotNull(message = "La fecha estimada de entrega no puede estar en blanco")
    private String fechaEntregaEstimada="";
    
    private String fechaEnvio = "";

    @NotNull(message = "El numero de pedido no puede estar en blanco")
    @Unique
    private Long numPedido;

    @Field("productos") // Campo como seria almacenado en la base de datos
    private List<ProductoPedido> productos;
    

    @NotNull(message = "El tipo de pago no puede estar en blanco")
    private String tipoPago;

    
    private String  estado;

    private String direccionEnvio;

   

}


