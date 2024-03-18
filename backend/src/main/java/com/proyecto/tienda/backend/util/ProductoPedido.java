package com.proyecto.tienda.backend.util;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // getters y setters
@AllArgsConstructor // constructor con todos los atributos
@NoArgsConstructor // constructor sin atributos

public class ProductoPedido {

    private String _idProducto;
    private String nombre;
    private String marca;
    private double precioProducto;
    private String categoria;
    private int cantidadPedida;

}
