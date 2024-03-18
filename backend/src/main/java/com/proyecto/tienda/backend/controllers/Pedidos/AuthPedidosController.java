package com.proyecto.tienda.backend.controllers.Pedidos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.EnviarCorreoDTO;
import com.proyecto.tienda.backend.UtilEnum.EPedido;
import com.proyecto.tienda.backend.models.PedidosModelo;
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

    // CONTROLADOR PARA ACTUALIZAR EL ESTADO DEL PEDIDO
    @PatchMapping("/actualizarEstadoPedido")
    public ResponseEntity<?> actualizarEstadoPedido(@RequestParam("id") String pedidoId,
            @RequestBody @Valid ActualizarPedidoDTO actualizarPedidoDTO) {
        return authPedidoServicio.actualizarEstadoPedido(pedidoId,
                actualizarPedidoDTO);

    }

    // CONTROLADOR PARA NVIAR UN EMAIL CUANDO HAY UN RETRASO
    @PostMapping("/envioEmailRetraso")
    public String enviarCorreo(@RequestBody EnviarCorreoDTO correoDTO) {
        resend.enviarEmailDeRetrasoDelPedido(correoDTO.getEmail());
        System.out.println("EMAIL ENVIADO A: " + correoDTO.getEmail());
        return "Correo de retraso enviado con éxito.";
    }

    // CONTROLADOR PARA BUSCAR LOS PEDIDOS POR SU ESTADO
    @GetMapping("/buscarPedidoPorEstado")
    public ResponseEntity<List<PedidosModelo>> buscarPedidosPorEstado(@RequestParam("estado") String estado) {
        ResponseEntity<List<PedidosModelo>> estadoPedido = authPedidoServicio.obtenerPedidosPorEstado(estado);
        System.out.println("ESTADO PUESTO POR MI " + estado);
        return estadoPedido;
    }
    

}