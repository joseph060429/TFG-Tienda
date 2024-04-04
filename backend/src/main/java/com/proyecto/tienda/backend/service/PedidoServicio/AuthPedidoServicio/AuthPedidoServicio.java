package com.proyecto.tienda.backend.service.PedidoServicio.AuthPedidoServicio;


import java.util.List;
import org.springframework.http.ResponseEntity;
import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarPedidoDTO;
import com.proyecto.tienda.backend.models.PedidosModelo;



public interface AuthPedidoServicio {

    // METODO PARA ACTUALIZAR EL ESTADO DEL PEDIDO A ENVIADO
    ResponseEntity<?> actualizarEstadoPedidoEnviado(String _id, ActualizarPedidoDTO actualizarPedidoDTO);

    // METODO PARA BUSCAR EL PEDIDO POR ESTADOS
    ResponseEntity<List<PedidosModelo>> obtenerPedidosPorEstado(String estado, int page, int size);

    // METODO PARA ACTUALIZAR EL ESTADO DEL PEDIDO A ENTREGADO
    ResponseEntity<?> actualizarEstadoPedidoEntregado(String _id, ActualizarPedidoDTO actualizarPedidoDTO);
    

}
