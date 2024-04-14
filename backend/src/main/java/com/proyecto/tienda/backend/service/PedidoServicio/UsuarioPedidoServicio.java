package com.proyecto.tienda.backend.service.PedidoServicio;

import java.util.List;
import org.springframework.http.ResponseEntity;
import com.proyecto.tienda.backend.DTO.DTOPedido.CrearPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.PedidoInfoDTO;
import com.proyecto.tienda.backend.models.ProductoModelo;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpSession;

public interface UsuarioPedidoServicio {

    // METODO PARA CREAR UN PEDIDO
    ResponseEntity<?> crearPedido(CrearPedidoDTO crearPedidoDTO, String token, JwtUtils jwtUtils,
            List<ProductoModelo> productosModelo, HttpSession ses) throws IOException;

    // METODO PARA ELIMINAR UN PEDIDO
    ResponseEntity<?> eliminarPedido(Long numeroPedido, String token, JwtUtils jwtUtils);

    // METODO PARA EL HISTORIAL DE PEDIDOS
    ResponseEntity<List<PedidoInfoDTO>> historialPedidos(String token, JwtUtils jwtUtils);

}
