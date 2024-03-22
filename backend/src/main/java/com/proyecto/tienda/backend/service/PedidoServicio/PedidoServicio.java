package com.proyecto.tienda.backend.service.PedidoServicio;

import java.util.List;

import org.springframework.http.ResponseEntity;
import com.proyecto.tienda.backend.DTO.DTOPedido.CrearPedidoDTO;
import com.proyecto.tienda.backend.models.ProductoModelo;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;

public interface PedidoServicio {

    // METODO PARA CREAR UN PRODUCTO
    ResponseEntity<?> crearPedido(CrearPedidoDTO crearPedidoDTO, String token, JwtUtils jwtUtils,
            List<ProductoModelo> productosModelo);

    // ResponseEntity<?> validarTipoPagoYSetearlo(String tipoDePago, PedidosModelo pedido);
   

}
