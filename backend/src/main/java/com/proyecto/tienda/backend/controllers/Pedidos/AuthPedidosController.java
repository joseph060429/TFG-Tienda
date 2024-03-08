package com.proyecto.tienda.backend.controllers.Pedidos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.EnviarCorreoDTO;
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.repositorios.ProductoRepositorio;
import com.proyecto.tienda.backend.service.PedidoServicio.AuthPedidoServicio.AuthPedidoServicio;
import com.proyecto.tienda.backend.util.ResendUtil;

import jakarta.validation.Valid;

@RequestMapping("/admin/pedidos")
@PreAuthorize("hasAnyRole('ADMIN')")
@RestController
public class AuthPedidosController {

    @Autowired
    PedidoRepositorio pedidoRepositorio;

    @Autowired
    ProductoRepositorio productoRepositorio;

    @Autowired
    AuthPedidoServicio authPedidoServicio;

    @Autowired
    private ResendUtil resend;

    @PatchMapping("/actualizarEstadoPedido")
    public ResponseEntity<?> actualizarEstadoPedido(@RequestParam("id") String pedidoId,
            @RequestBody @Valid ActualizarPedidoDTO actualizarPedidoDTO) {
        return authPedidoServicio.actualizarEstadoPedido(pedidoId,
                actualizarPedidoDTO);

    }

    // @PostMapping("/actualizarEstadoPedido")
    // public ResponseEntity<?> actualizarEstadoPedido(@RequestParam("id") String
    // pedidoId,
    // @RequestBody @Valid ActualizarPedidoDTO actualizarPedidoDTO, EnviarCorreoDTO
    // correoDTO) {

    // ResponseEntity<?> respuesta =
    // authPedidoServicio.actualizarEstadoPedido(pedidoId, actualizarPedidoDTO);

    // if (respuesta.getStatusCode().is2xxSuccessful()) {
    // String destinatario = correoDTO.getEmail();
    // resend.enviarEmailEnvioDelPedido(destinatario);
    // }

    // return respuesta;
    // }

    @PostMapping("/envioEmailRetraso")
    public String enviarCorreo(@RequestBody EnviarCorreoDTO correoDTO) {
        resend.enviarEmailDeRetrasoDelPedido(correoDTO.getEmail());
        return "Correo de retraso enviado con éxito.";
    }

}
