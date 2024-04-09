package com.proyecto.tienda.backend.controllers.Paypal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.proyecto.tienda.backend.service.Paypal.PayPalServicio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaypalController {

    @Autowired
    private PayPalServicio paypalServicio;

    @GetMapping("/payment/success")
    public ResponseEntity<String> paymentSuccess(@RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalServicio.executePayment(paymentId, payerId);
            // if (payment.getState().equals("approved")) {
            //     return ResponseEntity.ok().body("paymentSuccess");
            // }

            return ResponseEntity.ok().body(payment.toString());
        } catch (PayPalRESTException e) {
            log.error("Error ocurrido", e);
        }

        return ResponseEntity.ok().body("paymentSuccess");
    }

    @GetMapping("/payment/cancel")
    public ResponseEntity<String> paymentCancel() {
        return ResponseEntity.status(400).body("paymentCancel");
    }

    @GetMapping("/payment/error")
    public ResponseEntity<String> paymentError() {
        return ResponseEntity.ok().body("paymentError");
    }

    @PostMapping("/payment/verificar")
    public ResponseEntity<String> payment(@RequestParam("paymentId") String paymentId,
    @RequestParam("PayerID") String payerId) {

        try {
            Payment payment = paypalServicio.executePayment(paymentId, payerId);
            // if (payment.getState().equals("approved")) {
            //     return ResponseEntity.ok().body("paymentSuccess");
            // }

            return ResponseEntity.ok().body(payment.toString());
        } catch (PayPalRESTException e) {
            log.error("Error ocurrido", e);
        }

        return ResponseEntity.ok().body("payment");
    }

}
