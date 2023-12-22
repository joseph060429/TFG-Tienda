package com.proyecto.tienda.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.proyecto.tienda.backend.models.Usuarios;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;

@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuarios usuario = usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return UsuariosDetailsPersonalizados.build(usuario);
    }

}

// @Override
// public UserDetails loadUserByUsername(String email) throws
// UsernameNotFoundException {

// Usuarios modeloUsuario = usuarioRepositorio.findByEmail(email)
// .orElseThrow(() -> new UsernameNotFoundException("El usuario " + email + " no
// existe."));

// Collection<? extends GrantedAuthority> authorities = modeloUsuario.getRoles()
// .stream()
// .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
// .collect(Collectors.toSet());

// return new User(modeloUsuario.getEmail(), modeloUsuario.getPassword(),
// true,
// true,
// true,
// true,
// authorities
// );
// }

// Prueba 1
// @Override
// public UserDetails loadUserByUsername(String email) throws
// UsernameNotFoundException {

// Usuarios modeloUsuario = usuarioRepositorio.findByEmail(email)
// .orElseThrow(() -> new UsernameNotFoundException("El usuario " + email + " no
// existe."));

// Collection<? extends GrantedAuthority> authorities = modeloUsuario.getRoles()
// .stream()
// .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
// .collect(Collectors.toSet());

// return new User(modeloUsuario.get_id(), modeloUsuario.getPassword(),
// true,
// true,
// true,
// true,
// authorities
// );
// }
