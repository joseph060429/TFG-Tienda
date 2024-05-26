package com.proyecto.tienda.backend.controllers.Paypal;

import java.util.Enumeration;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.proyecto.tienda.backend.DTO.DTOPedido.FacturaDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.ProductoPedidoDTO;
import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.CarritoRepositorio;
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.service.Paypal.PayPalServicio;
import com.proyecto.tienda.backend.service.PedidoServicio.UsuarioPedidoServicioImpl;
import com.proyecto.tienda.backend.util.ResendUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaypalController {

    @Autowired
    private PayPalServicio paypalServicio;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private UsuarioPedidoServicioImpl usuarioPedidoServicioImpl;

    @Autowired
    private CarritoRepositorio carritoRepositorio;

    @Autowired
    private ResendUtil resend;

    // CONTROLADOR DEL PAYPAL PARA CUANDO SE CREA UN NUEVO PEDIDO
    // @GetMapping("/payment/success")
    // public ResponseEntity<String> paymentSuccess(@RequestParam("paymentId")
    // String paymentId,
    // @RequestParam("PayerID") String payerId, HttpSession ses) {

    // try {

    // FacturaDTO facturaDTO = (FacturaDTO) ses.getAttribute("factura");

    // Payment payment = paypalServicio.executePayment(paymentId, payerId);

    // // Traigo el pedido que se va a pagar
    // PedidosModelo pedido = (PedidosModelo) ses.getAttribute("pedido");

    // System.out.println("pedido desde payment succes " + pedido);

    // // Obtengo el usuario asociado al pedido
    // UsuarioModelo usuario = pedido.getUsuario();

    // // Traigo la lista de los productosPedido para restar los productos antes de
    // // guardar el pedido en la base de datos
    // List<ProductoPedidoDTO> productosPedido = pedido.getProductos();

    // // Resto la cantidad de cada producto del stock
    // for (ProductoPedidoDTO productoPedido : productosPedido) {
    // String productoId = productoPedido.get_idProducto();
    // int cantidadRestar = productoPedido.getCantidadPedida();
    // int resultadoResta =
    // usuarioPedidoServicioImpl.restarCantidadProducto(productoId, cantidadRestar);

    // // Verifico el resultado de la resta
    // if (resultadoResta != 0) {
    // // Siempre me va a restar porque ya controla que la cantidad sea mas que 0 y
    // que
    // // no sean mas de los que ya hay en stock en el metodo
    // // generarListaProductosPedido de UsuarioPedidoServicioImpl
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    // .body("No se pudo restar la cantidad del producto del stock.");
    // }
    // }

    // // Envio un email al usuario con su factura cuando la compra ha sido exitosa
    // resend.enviarEmailFacturaPdf(pedido.getUsuario().getEmail(), facturaDTO);

    // // Elimino todos los productos del carrito de ese usuario
    // carritoRepositorio.deleteByIdUsuario(usuario.get_id());

    // // Guardo el pedido en la base de datos
    // pedidoRepositorio.save(pedido);

    // return ResponseEntity.ok().body("Pedido creado existosamente");
    // } catch (PayPalRESTException e) {
    // log.error("Error ocurrido", e);
    // }

    // return ResponseEntity.ok().body("paymentSuccess");
    // }

    @GetMapping("/payment/success")
    public ResponseEntity<String> paymentSuccess(@RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId, HttpSession ses) {
    
        try {
            // Recupera el ID de sesión de la sesión
            String sessionId = (String) ses.getAttribute("sessionId");
    
            // Imprime el ID de sesión en la consola
            System.out.println("ID de sesión en paymentSuccess: " + sessionId);
    
            // Utiliza el ID de sesión como sea necesario
    
            return ResponseEntity.ok().body("Pedido creado exitosamente");
    
        } catch (Exception e) {
            log.error("Error ocurrido", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error durante el pago.");
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    // CONTROLADOR PARA CANCELAR EL PAGO DEL PEDIDO, POR ENDE NO SE PAGARA Y NO SE
    // ALMACENARA EN MI BASE DE DATOS
    @GetMapping("/payment/cancel")
    public ResponseEntity<String> paymentCancel() {
        return ResponseEntity.status(400).body("Pedido cancelado, vuelva hacer el pedido");
    }

    // CONTROLADOR POR SI HUBO UN ERROR EN EL PAGO
    @GetMapping("/payment/error")
    public ResponseEntity<String> paymentError() {
        return ResponseEntity.status(500).body("Error en el pago, vuelve a intentarlo");
    }

}