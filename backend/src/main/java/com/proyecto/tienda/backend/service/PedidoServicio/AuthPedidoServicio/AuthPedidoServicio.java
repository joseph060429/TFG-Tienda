package com.proyecto.tienda.backend.service.PedidoServicio.AuthPedidoServicio;


import org.springframework.http.ResponseEntity;
import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarPedidoDTO;


public interface AuthPedidoServicio {

    ResponseEntity<?> actualizarEstadoPedido(String _id, ActualizarPedidoDTO actualizarPedidoDTO);
}
