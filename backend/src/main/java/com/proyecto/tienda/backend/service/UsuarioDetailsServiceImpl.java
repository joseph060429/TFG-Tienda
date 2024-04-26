package com.proyecto.tienda.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;

@Service // Esta es una clase de Spring Security//
public class UsuarioDetailsServiceImpl implements UserDetailsService {


    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    // CLASE DE SPRING SECURITY PARA CARGAR LOS DETALLES DEL USUARIO A PARTIR DE SU CORREO ELECTRÓNICO
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busco un usuario por su correo electrónico en el repositorio
        UsuarioModelo usuario = usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email + " me quieres joder? "));
        
        // Construyo un objeto UserDetails personalizado con los detalles del usuario encontrado
        return UsuariosDetailsPersonalizados.build(usuario);
    }
}
