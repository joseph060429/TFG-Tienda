package com.proyecto.tienda.backend.controllers.Pedidos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarDireccionEnvioDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.EnviarCorreoDTO;
import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.repositorios.ProductoRepositorio;
import com.proyecto.tienda.backend.service.PedidoServicio.AdminPedidoServicio.AdminPedidoServicio;
import com.proyecto.tienda.backend.util.ResendUtil;
import jakarta.validation.Valid;

@RequestMapping("/admin/pedidos")
@PreAuthorize("hasAnyRole('ADMIN')")
@RestController
public class AdminPedidosController {

    @Autowired
    PedidoRepositorio pedidoRepositorio;

    @Autowired
    ProductoRepositorio productoRepositorio;

    @Autowired
    AdminPedidoServicio adminPedidoServicio;

    @Autowired
    private ResendUtil resend;

    // CONTROLADOR PARA ACTUALIZAR EL ESTADO DEL PEDIDO
    @PatchMapping("/actualizarEstadoPedidoEnviado")
    public ResponseEntity<?> actualizarEstadoPedidoEnviado(
            @Valid @RequestBody ActualizarPedidoDTO actualizarPedidoDTO) {

        // Extraer el ID del pedido y el estado del DTO
        String pedidoId = actualizarPedidoDTO.getPedidoId().trim();
        String estado = actualizarPedidoDTO.getEstado();

        // Llamar al servicio para actualizar el estado del pedido
        return adminPedidoServicio.actualizarEstadoPedidoEnviado(pedidoId, estado, actualizarPedidoDTO);
    }

    // CONTROLADOR PARA NVIAR UN EMAIL CUANDO HAY UN RETRASO
    @PostMapping("/envioEmailRetraso")
    public String enviarCorreoRetraso(@RequestBody @Valid EnviarCorreoDTO correoDTO) {
        resend.enviarEmailDeRetrasoDelPedido(correoDTO.getEmail());
        System.out.println("EMAIL ENVIADO A: " + correoDTO.getEmail());
        return "Correo de retraso enviado con éxito.";
    }

    // CONTROLADOR PARA BUSCAR LOS PEDIDOS POR SU ESTADO, PAGINADO DE 10 EN 10
    @GetMapping("/buscarPedidoPorEstado")
    public ResponseEntity<List<PedidosModelo>> buscarPedidosPorEstado(@RequestParam("estado") String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ResponseEntity<List<PedidosModelo>> estadoPedido = adminPedidoServicio.obtenerPedidosPorEstado(estado, page,
                size);
        System.out.println("ESTADO PUESTO POR MI " + estado);
        return estadoPedido;
    }

    // CONTROLADOR PARA ACTUALIZAR EL ESTADO DEL PEDIDO A ENTREGADO
    @PatchMapping("/actualizarEstadoPedidoEntregado")
    public ResponseEntity<?> actualizarEstadoPedidoEntrEGADO(@RequestParam("id") String pedidoId,
            @RequestBody @Valid ActualizarPedidoDTO actualizarPedidoDTO) {
        return adminPedidoServicio.actualizarEstadoPedidoEntregado(pedidoId,
                actualizarPedidoDTO);

    }

    // CONTROLADOR PARA BUSCAR UN PEDIDO POR ID
    @GetMapping("/listarUnPedido")
    public ResponseEntity<?> listarUnPedido(@RequestParam("id") String usuarioId) {

        PedidosModelo pedidos = adminPedidoServicio.listarUnPedido(usuarioId);

        if (pedidos != null) {
            return ResponseEntity.ok(pedidos); // Devuelve el pedidos encontrado
        } else {
            String mensaje = "Usuario no encontrado para el ID: " + usuarioId;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje);
        }
    }

    // CONTROLADOR PARA ENVIAR UN EMAIL CUANDO LA DIRECCION NO EXISTE
    @PatchMapping("/envioEmailDireccionErronea")
    public ResponseEntity<?> actualizarEstadoPedidoPendienteConfirmacionDireccion(
            @Valid @RequestBody ActualizarPedidoDTO actualizarPedidoDTO) {

        // Extraer el ID del pedido y el estado del DTO
        String pedidoId = actualizarPedidoDTO.getPedidoId().trim();
        String estado = actualizarPedidoDTO.getEstado().trim();

        // Llamar al servicio para actualizar el estado del pedido
        return adminPedidoServicio.actualizarEstadoPedidoPendienteConfirmacionDireccion(pedidoId, estado,
                actualizarPedidoDTO);
    }

    // CONTROLADOR PARA ACTUALIZARLE LA DIRECCION DE ENVIO AL USUARIO
    @PatchMapping("/actualizarDireccionEnvioAdmin")
    public ResponseEntity<?> actualizarDireccionEnvioAdmin(
            @Valid @RequestBody ActualizarDireccionEnvioDTO actualizarDireccionEnvioDTO, UsuarioModelo usuario) {
        String pedidoId = actualizarDireccionEnvioDTO.getPedidoId().trim();
        return adminPedidoServicio.actualizarDireccionEnvioAdmin(pedidoId, actualizarDireccionEnvioDTO, usuario);
    }


    // CONTRLADOR PARA ACTUALIZAR EL ESTADO DEL PEDIDO A REPROGRAMADO_PARA_ENTREGA CUANDO EL REPARTIDOR LLEGA Y NO HAY NADIE EN CASA O NO RESPONDEN AL MÓVIL
    @PatchMapping("/envioEmailReprogramadoEntrega")
    public ResponseEntity<?> actualizarEstadoReprogramadoParaEntrega(
            @Valid @RequestBody ActualizarPedidoDTO actualizarPedidoDTO) {

        // Extraigo el ID del pedido y el estado del DTO
        String pedidoId = actualizarPedidoDTO.getPedidoId().trim();

        // Llamo al servicio para actualizar el estado del pedido
        return adminPedidoServicio.actualizarEstadoReprogramadoParaEntrega(pedidoId, actualizarPedidoDTO);
    }

}
