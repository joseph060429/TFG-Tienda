package com.proyecto.tienda.backend.service.PedidoServicio.AdminPedidoServicio;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarDireccionEnvioDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.PedidoInfoDTO;
import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
// import com.proyecto.tienda.backend.repositorios.PedidoRepositorio.UsuarioProjection;

public interface AdminPedidoServicio {

        // METODO PARA LISTAR TODOS LOS USUARIOS
        List<PedidoInfoDTO> listarPedidos();

        // METODO PARA BUSCAR EL PEDIDO POR ESTADOS
        ResponseEntity<List<PedidosModelo>> obtenerPedidosPorEstado(String estado, int page, int size);

        // METODO PARA ACTUALIZAR EL ESTADO DEL PEDIDO A ENVIADO
        ResponseEntity<?> actualizarEstadoPedidoEnviado(String _id, String estado,
                        ActualizarPedidoDTO actualizarPedidoDTO);

        // METODO PARA ACTUALIZAR EL ESTADO DEL PEDIDO A ENTREGADO
        ResponseEntity<?> actualizarEstadoPedidoEntregado(String _id, String estado, ActualizarPedidoDTO actualizarPedidoDTO);

        // METODO PARA BUSCAR EL PEDIDO POR ID
        PedidosModelo listarUnPedido(String _id);

        // METODO PARA ACTUALIZAR EL ESTADO DEL PEDIDO A PENDIENTE CONFIRMACION DE ENVIO
        ResponseEntity<?> actualizarEstadoPedidoPendienteConfirmacionDireccion(String _id, String estado,
                        ActualizarPedidoDTO actualizarPedidoDTO);

        // METODO PARA ACTUALIZARLE LA DIRECCION DE ENVIO A UN USUARIO CUANDO SU
        // DIRECCION ES ERRONEA
        ResponseEntity<?> actualizarDireccionEnvioAdmin(String _idPedido,
                        ActualizarDireccionEnvioDTO actualizarDireccionEnvioDTO, UsuarioModelo usuario);

        // METDODO PARA ACTUALIZAR EL PEDIDO A REPROGRAMADO_PARA_ENTREGA CUANDO NO HAY
        // NADIE EN CASA O CUANDO NO RESPONDEN LA LLAMADA AL REPARTIDOR
        ResponseEntity<?> actualizarEstadoReprogramadoParaEntrega(String _id, ActualizarPedidoDTO actualizarPedidoDTO);

}
