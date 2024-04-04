package com.proyecto.tienda.backend.DTO.DTOPedido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getters y setters
@AllArgsConstructor // constructor con todos los atributos
@NoArgsConstructor // constructor sin atributos
public class UsuarioPedidoDTO {

    private String _id;
    private String nombre;
    private String apellido;
    private String email;

}
