package com.proyecto.tienda.backend.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.tienda.backend.controllers.request.UsuarioDTO;
import com.proyecto.tienda.backend.models.ERol;
import com.proyecto.tienda.backend.models.Roles;
import com.proyecto.tienda.backend.models.Usuarios;
import com.proyecto.tienda.backend.repositorios.RolesRepositorio;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;

@Service
public class AuthServicio {

    @Autowired
    private RolesRepositorio rolesRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;


    //Metodo para crear un nuevo Usuario
    public ResponseEntity<?> crearNuevoUsuario(UsuarioDTO crearUsuarioDTO) {
        if (usuarioRepositorio.existsByEmail(crearUsuarioDTO.getEmail())) {
            return ResponseEntity.badRequest().body("El email ya existe");
        }

        Set<Roles> roles = obtenerRoles(crearUsuarioDTO);

        Usuarios usuario = construirUsuario(crearUsuarioDTO, roles);

        usuarioRepositorio.save(usuario);

        return ResponseEntity.ok(usuario);
    }

    //Metodo para obtener todos los roles existentes
    private Set<Roles> obtenerRoles(UsuarioDTO crearUsuarioDTO) {
        Set<Roles> roles = new HashSet<>();

        if (crearUsuarioDTO.getRoles() == null || crearUsuarioDTO.getRoles().isEmpty()) {
            Optional<Roles> userRole = rolesRepositorio.findByName(ERol.USER);
            Roles defaultRole;
            if (userRole.isPresent()) {
                defaultRole = userRole.get();
            } else {
                defaultRole = new Roles();
                defaultRole.setName(ERol.USER);
                defaultRole.setId(UUID.randomUUID().toString());
                rolesRepositorio.save(defaultRole);
            }
            roles.add(defaultRole);
        } else {
            roles = crearUsuarioDTO.getRoles().stream()
                    .map(rol -> rolesRepositorio.findByName(rol))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
        }
        return roles;
    }

    //Metodo para construir un nuevo usuario
    private Usuarios construirUsuario(UsuarioDTO crearUsuarioDTO, Set<Roles> roles) {
        Usuarios usuario = Usuarios.builder()
                .nombre(crearUsuarioDTO.getNombre())
                .apellido(crearUsuarioDTO.getApellido())
                .email(crearUsuarioDTO.getEmail())
                .password(passwordEncoder.encode(crearUsuarioDTO.getPassword()))
                .direccionEnvio("")
                .fechaModificacion("")
                .roles(roles)
                .build();

        if (usuario.get_id() == null) {
            usuario.set_id(UUID.randomUUID().toString());
        }

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setFechaCreacion();
        usuario.setFechaCreacion(usuarioDTO.getFechaCreacion());

        return usuario;
    }

}
