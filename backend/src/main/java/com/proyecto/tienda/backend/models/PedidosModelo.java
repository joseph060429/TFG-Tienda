package com.proyecto.tienda.backend.models;

import java.util.List;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proyecto.tienda.backend.DTO.DTOPedido.ProductoPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.UsuarioPedidoDTO;

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
    @JsonIgnore
    private UsuarioModelo usuario;
    
    private UsuarioPedidoDTO usuarioPedido;

    @NotNull(message = "La fecha del pedido no puede estar en blanco")
    private String fechaPedido;

    // @NotNull(message = "La fecha estimada de entrega no puede estar en blanco")
    private String fechaEntregaEstimada = "";

    private String fechaEnvio = "";

    // @NotNull(message = "El tracking number no puede estar en blanco")
    private String trackingNumber = "";

    @NotNull(message = "El numero de pedido no puede estar en blanco")
    @Unique
    private Long numPedido;

    @Field("productos") // Campo como seria almacenado en la base de datos
    private List<ProductoPedidoDTO> productos;

    @NotNull(message = "El tipo de pago no puede estar en blanco")
    private String tipoPago;

    private String estado;

    private String direccionEnvio;

    

    // Método para establecer el usuario a partir de un UsuarioPedidoDTO
    public void setUsuarioFromModelo(UsuarioModelo usuarioModelo) {
        this.usuarioPedido = convertirAUsuarioDTO(usuarioModelo);
    }

    // Método para convertir un UsuarioPedidoDTO a UsuarioModelo
    private UsuarioPedidoDTO convertirAUsuarioDTO(UsuarioModelo usuarioModelo) {
        UsuarioPedidoDTO usuarioPedidoDTO = new UsuarioPedidoDTO();
        usuarioPedidoDTO.set_id(usuarioModelo.get_id());
        usuarioPedidoDTO.setNombre(usuarioModelo.getNombre());
        usuarioPedidoDTO.setApellido(usuarioModelo.getApellido());
        usuarioPedidoDTO.setEmail(usuarioModelo.getEmail());
        return usuarioPedidoDTO;
    }

}
