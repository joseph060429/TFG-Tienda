package com.proyecto.tienda.backend.service.Paypal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.proyecto.tienda.backend.DTO.DTOPedido.ProductoPedidoDTO;
import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayPalServicio {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Value("${paypal.cancelUrl}")
    private String cancelUrl;

    @Value("${paypal.successUrl}")
    private String successUrl;

    private final APIContext apiContext;

    // METODO PARA CREAR EL PAGO
    public Payment createPayment(
            Double total,
            String currency,
            String method,
            String intent,
            String description,
            String cancelUrl,
            String successUrl) throws PayPalRESTException {

        // Configuracion del monto de pago
        Amount montoPago = new Amount();
        montoPago.setCurrency(currency);
        montoPago.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", total));

        // Configuracion de la transaccion
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(montoPago);

        // Configuracion de la lista de transacciones
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        // Configuracion del pagador
        Payer pagador = new Payer();
        pagador.setPaymentMethod(method);

        // Configuracion del objeto pago
        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(pagador);
        payment.setTransactions(transactions);

        // Configuracion de las URLS de redireccionamiento
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        // Creo el pago y lo devuelvo
        return payment.create(apiContext);

    }

    // METODO PARA EJECUTAR UN PAGO
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {

        // Configuracion del objeto pago
        Payment payment = new Payment();
        payment.setId(paymentId);

        // Configuración de la ejecución del pago
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        // Ejecuto el pago y lo devuelvo
        return payment.execute(apiContext, paymentExecute);
    }

    // METODO PARA HACER EL PAGO
    public ResponseEntity<String> hacerPago(String id, String token, JwtUtils jwtUtils) {
        try {

            String emailFromToken = obtenerEmailDelToken(token, jwtUtils);
            Optional<UsuarioModelo> usuarioModelo = buscarUsuarioPorEmail(emailFromToken);

            System.out.println("Email DEL TOKEN: " + emailFromToken);

            if (!usuarioModelo.isPresent()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error usuario no encontrado");
            } else {

                // Busco el producto por id
                Optional<PedidosModelo> pedidoOptional = pedidoRepositorio.findById(id);

                if (pedidoOptional.isPresent()) {
                    PedidosModelo pedido = pedidoOptional.get();
                    if (!pedido.getUsuario().getEmail().equals(emailFromToken)) {
                        return ResponseEntity.status(404).body("No eres el usuario del pedido");
                    }

                    // Traigo todos los productos que ha pedido ese usuario
                    List<ProductoPedidoDTO> productos = pedido.getProductos();

                    // Calculo el precio total sumando todos los productos de ese pedido
                    double total = 0.0;
                    for (ProductoPedidoDTO producto : productos) {
                        total += producto.getPrecioProducto() * producto.getCantidadPedida();
                    }

                    // Total es el precio total del pedido
                    System.out.println("Precio total del pedido: " + total);

                    // Uso el total para el pago
                    Payment payment = createPayment(total, "EUR", "paypal", "sale", "Pedido Pagado",
                            cancelUrl, successUrl);

                    for (Links links : payment.getLinks()) {
                        if (links.getRel().equals("approval_url")) {
                            String href = links.getHref();
                            System.out.println("Location: " + links.getHref());
                            return ResponseEntity.ok().body(href);
                        }
                    }
                } else {
                    return ResponseEntity.status(404).body("El pedido con el ID especificado no fue encontrado.");
                }
            }
        } catch (PayPalRESTException e) {
            System.out.println("Error ocurrido:: " + e);
        }
        return ResponseEntity.status(500).body("Algo ha fallado");
    }

    private String obtenerEmailDelToken(String token, JwtUtils jwtUtils) {
        String jwtToken = token.replace("Bearer ", "");
        return jwtUtils.getEmailFromToken(jwtToken);
    }

    private Optional<UsuarioModelo> buscarUsuarioPorEmail(String emailFromToken) {
        return usuarioRepositorio.findByEmail(emailFromToken);
    }

}
