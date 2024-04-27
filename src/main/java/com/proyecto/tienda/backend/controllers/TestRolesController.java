package com.proyecto.tienda.backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRolesController {

    @GetMapping("/accesAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public String accesAdmin() {
        return "Acceso Admin";
    }
    
    @GetMapping("/accesUsuario")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String accesUsuario() {
        return "Acceso Usuario";
    }
    
    @GetMapping("/accesInvitado")
    @PreAuthorize("hasAnyRole('ADMIN', 'INVITED')")
    public String accesInvitado() {
        return "Acceso Invitado";
    }
}
