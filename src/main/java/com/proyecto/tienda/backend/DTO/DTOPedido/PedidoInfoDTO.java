package com.proyecto.tienda.backend.DTO.DTOPedido;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoInfoDTO {

    private String _id;
    private String fechaPedido;
    private String fechaEnvio;
    private int numPedido;
    private List<ProductoPedidoDTO> productos;
    private String tipoPago;
    private String estado;
    private String direccionEnvio;
    private int numTelefono;

    private UsuarioPedidoDTO datosUsuarioPedidoDTO;


    



    
}
