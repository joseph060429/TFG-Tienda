package com.proyecto.tienda.backend.service.PedidoServicio.AuthPedidoServicio;


import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarPedidoDTO;
import com.proyecto.tienda.backend.UtilEnum.EPedido;
import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.util.ResendUtil;

@Service
public class AuthPedidoServicioImpl implements AuthPedidoServicio {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private ResendUtil resend;

    // IMPLEMENTACION DEL METODO PARA ACTUALIZAR EL ESTADO DEL PEDIDO
    @Transactional
    @Override
    public ResponseEntity<?> actualizarEstadoPedido(String _id, ActualizarPedidoDTO actualizarPedidoDTO) {

        // Busco el pedido por el Id
        Optional<PedidosModelo> pedidoOptional = pedidoRepositorio.findBy_id(_id);

        if (pedidoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontró el pedido");
        }

        try {

            // Busco el usuario por el email
            String email = pedidoOptional.get().getUsuario().getEmail();

            // Obtengo el pedido de la base de datos
            PedidosModelo pedido = pedidoOptional.get();


            if (!EPedido.ENVIADO.equals(EPedido.valueOf(actualizarPedidoDTO.getEstado()))) {
                return ResponseEntity.status(400).body("El pedido no tiene un estado válido para esta operación");
            }
            // Actualizo el estado del pedido
            pedido.setEstado(EPedido.ENVIADO.toString());

            // Calculo y seteo la fecha de envío y entrega estimada
            actualizarPedidoDTO.setFechaEnvioPedido();
            actualizarPedidoDTO.setFechaEntregaEstimada();

            // Seteo las fechas en el pedido
            pedido.setFechaEnvio(actualizarPedidoDTO.getFechaEnvio());
            pedido.setFechaEntregaEstimada(actualizarPedidoDTO.getFechaEntregaEstimada());

            // Envio el email al usuario
            resend.enviarEmailEnvioDelPedido(email);
            // Guardo el pedido
            pedidoRepositorio.save(pedido);

            return ResponseEntity.ok("Se actualizo correctamente el pedido");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el pedido: " + e.getMessage());
        }
    }

}
