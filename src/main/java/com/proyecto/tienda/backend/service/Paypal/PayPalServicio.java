package com.proyecto.tienda.backend.service.Paypal;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class PayPalServicio {

    // Urls de redireccionamiento para PayPal
    @Value("${paypal.cancelUrl}")
    private String cancelUrl;

    // Urls de redireccionamiento para PayPal
    @Value("${paypal.successUrl}")
    private String successUrl;

    private final APIContext apiContext;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Value("${ruta.logo}")
    private String rutaLogo;

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
    public ResponseEntity<String> hacerPago(PedidosModelo pedido, HttpSession ses) {
        try {
            // Traigo todos los productos que ha pedido ese usuario
            List<ProductoPedidoDTO> productos = pedido.getProductos();

            // Calculo el precio total sumando todos los productos de ese pedido
            double total = 0.0;
            for (ProductoPedidoDTO producto : productos) {
                total += producto.getPrecioProducto() * producto.getCantidadPedida();
            }

            pedido = (PedidosModelo) ses.getAttribute("pedido");

            System.out.println("id pedido " + pedido.get_id());

            System.out.println("id usuario en pedido " + pedido.getUsuario().get_id());

            // FacturaDTO facturaDTO = (FacturaDTO) ses.getAttribute("factura");
            // byte[] facturaPdfBytes = facturaDTO.generarFacturaPDF(facturaDTO, rutaLogo);

            // // Asignar el PDF de la factura al pedido
            // pedido.setFacturaPdf(facturaPdfBytes);

            // Uso el total para el pago
            Payment pago = createPayment(total, "EUR", "paypal", "sale", pedido.get_id(),
                    cancelUrl+"/"+pedido.get_id(), successUrl);

            // Recorro las URLs de redireccionamiento
            for (Links links : pago.getLinks()) {
                // Verificar si es la URL de redireccionamiento para PayPal
                if (links.getRel().equals("approval_url")) {
                    // Generar una clave única para el pedido y almacenarlo en la sesión
                    String clavePedido = "pedido_" + pago.getId();
                    ses.setAttribute(clavePedido, pedido);

                    // Obtener el ID de sesión
                    // String sessionId = ses.getId();

                    // Construir la URL de redireccionamiento con el ID de sesión como parámetro
                    // String redirectUrl = links.getHref() + "&id=" + pedido.get_id();

                    String redirectUrl = links.getHref();

                    // System.out.println("Sesión ID en servicio hacer pago: " + pedido.get_id());
                    // System.out.println("pedido desde hacer pago " + ses.getAttribute(clavePedido));
                    // System.out.println("Factura almacenada en sesión de hacer pago: " + ses.getAttribute("factura"));
                    System.out.println("URL de redireccionamiento " + redirectUrl);

                    // Guardo el pedido en la base de datos

                    pedidoRepositorio.save(pedido);            

                    // Devolver la URL de redireccionamiento al cliente
                    return ResponseEntity.ok().body(redirectUrl);
                }
            }

        } catch (PayPalRESTException e) {
            System.out.println("Error ocurrido:: " + e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Algo ha fallado");
    }

}