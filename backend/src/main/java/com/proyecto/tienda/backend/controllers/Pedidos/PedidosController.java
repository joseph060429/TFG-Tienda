package com.proyecto.tienda.backend.controllers.Pedidos;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.repositorios.ProductoRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.PedidoServicio.PedidoServicio;

import jakarta.validation.Valid;

@RequestMapping("/usuarios/pedidos")
@PreAuthorize("hasAnyRole('USER')")
@RestController
public class PedidosController {

    @Autowired
    PedidoRepositorio pedidoRepositorio;

    @Autowired
    ProductoRepositorio productoRepositorio;

    @Autowired
    private PedidoServicio pedidoServicio;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/crearPedido")
    public ResponseEntity<?> crearPedido(@RequestBody @Valid CrearPedidoDTO crearPedidoDTO, @RequestHeader("Authorization") String token) {
        return pedidoServicio.crearPedido(crearPedidoDTO, token, jwtUtils, crearPedidoDTO.getProductos() );
    }

    @DeleteMapping("/eliminarPedido")
    public ResponseEntity<?> eliminarPedido(@RequestParam Long numeroPedido,
                                            @RequestHeader("Authorization") String token) {
                                                System.out.println("NUM PEDIDO CONTROLLER " + numeroPedido);
        return pedidoServicio.eliminarPedido(numeroPedido, token, jwtUtils);
    }
}
