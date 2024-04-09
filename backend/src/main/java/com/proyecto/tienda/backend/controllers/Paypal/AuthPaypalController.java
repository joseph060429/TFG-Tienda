package com.proyecto.tienda.backend.controllers.Paypal;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.Paypal.PayPalServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usuarios/pedidos/paypal")
@PreAuthorize("hasAnyRole('USER')")
public class AuthPaypalController {

    @Autowired
    private PayPalServicio paypalServicio;

    
    @Autowired
    private JwtUtils jwtUtils;

    // @Value("${paypal.cancelUrl}")
    // private String cancelUrl;

    // @Value("${paypal.successUrl}")
    // private String successUrl;

    @PostMapping("/hacerPago")
    public ResponseEntity<?> createPayment(@RequestParam("id") String id, @RequestHeader("Authorization") String token) {
        return paypalServicio.hacerPago(id, token, jwtUtils);
    }


}
