package com.proyecto.tienda.backend.DTO.DTOPedido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getters y setters
@AllArgsConstructor // constructor con todos los atributos
@NoArgsConstructor // constructor sin atributos

public class ProductoPedidoDTO {

    private String _idProducto;
    private String nombre;
    private String marca;
    private double precioProducto;
    private String categoria;
    private int cantidadPedida;

}
