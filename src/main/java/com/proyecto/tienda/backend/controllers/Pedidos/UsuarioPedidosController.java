package com.proyecto.tienda.backend.controllers.Pedidos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.tienda.backend.DTO.DTOPedido.CrearPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.PedidoInfoDTO;
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.repositorios.ProductoRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.PedidoServicio.UsuarioPedidoServicio;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@RequestMapping("/usuarios/pedidos")
@PreAuthorize("hasAnyRole('USER')")
@RestController
public class UsuarioPedidosController {

    @Autowired
    PedidoRepositorio pedidoRepositorio;

    @Autowired
    ProductoRepositorio productoRepositorio;

    @Autowired
    private UsuarioPedidoServicio usuarioPedidoServicio;

    @Autowired
    private JwtUtils jwtUtils;

    // CONTROLADOR PARA CREAR UN PEDIDO

    /*
     * Cuando en postman tenga la peticion lista, en Code asegurarme de estar
     * copiando HTTP, y no otra cosa
     */

    // CONTROLADOR PARA CREAR UN PEDIDO
    @PostMapping("/crearPedido")
    public ResponseEntity<?> crearPedido(@RequestBody @Valid CrearPedidoDTO crearPedidoDTO,
            @RequestHeader("Authorization") String token, HttpSession ses) {
        return usuarioPedidoServicio.crearPedido(crearPedidoDTO, token, jwtUtils,
                crearPedidoDTO.getProductos(), ses);
    }

    // CONTROLADOR PARA ELIMINAR UN PEDIDO
    @DeleteMapping("/eliminarPedido")
    public ResponseEntity<?> eliminarPedido(@RequestParam Long numeroPedido,
            @RequestHeader("Authorization") String token) {
        System.out.println("NUM PEDIDO CONTROLLER " + numeroPedido);
        return usuarioPedidoServicio.eliminarPedido(numeroPedido, token, jwtUtils);
    }

    //CONTROLADOR PARA VER EL HISTORIAL DE LOS PEDIDOS
    @GetMapping("/historialPedidos")
    public ResponseEntity<List<PedidoInfoDTO>> historialPedidos(@RequestHeader("Authorization") String token) {
        ResponseEntity<List<PedidoInfoDTO>> responseEntity = usuarioPedidoServicio.historialPedidos(token, jwtUtils);
        List<PedidoInfoDTO> historialPedidos = responseEntity.getBody();

        if (historialPedidos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(historialPedidos);
        }
        return ResponseEntity.ok(historialPedidos);
    }
}
