package com.proyecto.tienda.backend.controllers.Paypal;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.paypal.api.payments.Payment;
import org.springframework.beans.factory.annotation.Value;

import com.proyecto.tienda.backend.DTO.DTOPedido.FacturaDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.ProductoPedidoDTO;
import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.repositorios.CarritoRepositorio;
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.service.Paypal.PayPalServicio;
import com.proyecto.tienda.backend.service.PedidoServicio.UsuarioPedidoServicioImpl;
import com.proyecto.tienda.backend.util.ResendUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Value("${paypal.success.redirect}")
    private String paypalSucces;

    // CONTROLADOR DEL PAYPAL PARA CUANDO HA IDO BIEN LO DEL PAYPAL
    @GetMapping("/payment/success")
    public void paymentSuccess(@RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId, HttpSession ses, HttpServletRequest request,
            HttpServletResponse res) {

        try {

            Payment payment = paypalServicio.executePayment(paymentId, payerId);

            Optional<PedidosModelo> pedido = pedidoRepositorio
                    .findBy_id(payment.getTransactions().get(0).getDescription());

            // Actualizo el estado del pedido
            PedidosModelo pedidoActualizado = pedido.get();
            pedidoActualizado.setEstado("PENDIENTE_ENVÍO");
            pedidoRepositorio.save(pedidoActualizado);

            // Resto la cantidad de cada producto del stock
            List<ProductoPedidoDTO> productosPedido = pedido.get().getProductos();
            for (ProductoPedidoDTO productoPedido : productosPedido) {
                String productoId = productoPedido.get_idProducto();
                int cantidadRestar = productoPedido.getCantidadPedida();
                int resultadoResta = usuarioPedidoServicioImpl.restarCantidadProducto(productoId, cantidadRestar);

                // Verifico el resultado de la resta
                if (resultadoResta != 0) {
                    res.setStatus(400);
                    // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    // .body("No se pudo restar la cantidad del producto del stock.");
                }
            }

            FacturaDTO facturaDTO = new FacturaDTO();
            facturaDTO.setUsuarioModelo(pedido.get().getUsuario());
            facturaDTO.setPedidosModelo(pedido.get());
            facturaDTO.setProductosPedidos(pedido.get().getProductos());

            resend.enviarEmailFacturaPdf(pedido.get().getUsuario().getEmail(), facturaDTO);

            // Elimino todos los productos del carrito del usuario
            carritoRepositorio.deleteByIdUsuario(pedido.get().getUsuario().get_id());
            res.setHeader("Location", paypalSucces);
            res.setStatus(302);
            return;

        } catch (Exception e) {
            log.error("Error ocurrido", e);
            res.setStatus(400);

        }
    }

    // CONTROLADOR PARA CANCELAR EL PAGO DEL PEDIDO, POR ENDE NO SE PAGARA Y NO SE
    // ALMACENARA EN MI BASE DE DATOS
    @GetMapping("/payment/cancel/{id}")
    public ResponseEntity<String> paymentCancel(@PathVariable("id") String id) {

        Optional<PedidosModelo> pedidoOptional = pedidoRepositorio
                .findBy_id(id);

        if (pedidoOptional.isPresent()) {
            pedidoRepositorio.deleteById(id);
        }

        return ResponseEntity.status(400).body("El pago ha sido cancelado");
    }

    // CONTROLADOR POR SI HUBO UN ERROR EN EL PAGO
    @GetMapping("/payment/error")
    public ResponseEntity<String> paymentError() {
        return ResponseEntity.status(500).body("Error en el pago, vuelve a intentarlo");
    }

}