package com.proyecto.tienda.backend.service.AuthServicio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.proyecto.tienda.backend.DTO.DTOUsuario.CrearUsuarioDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.RecuperarContraseniaDTO;
import com.proyecto.tienda.backend.UtilEnum.ERol;
import com.proyecto.tienda.backend.models.Roles;
import com.proyecto.tienda.backend.models.UsuarioModelo;
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

    //  @Autowired
    // private JwtUtils jwtUtils;

    // Metodo para crear un nuevo Usuario
    public ResponseEntity<?> crearNuevoUsuario(CrearUsuarioDTO crearUsuarioDTO) {

        String email = crearUsuarioDTO.getEmail().trim();
        String nombre = crearUsuarioDTO.getNombre().trim();
        String apellido = crearUsuarioDTO.getApellido().trim();
        String password = crearUsuarioDTO.getPassword().trim();

        if (usuarioRepositorio.existsByEmail(crearUsuarioDTO.getEmail())) {
            return ResponseEntity.badRequest().body("El email ya existe");
        }

        Set<Roles> roles = obtenerRoles(crearUsuarioDTO);

        // Modificar el DTO antes de construir el usuario
        crearUsuarioDTO.setEmail(email);
        crearUsuarioDTO.setNombre(nombre);
        crearUsuarioDTO.setApellido(apellido);
        crearUsuarioDTO.setPassword(password);

        UsuarioModelo usuario = construirUsuario(crearUsuarioDTO, roles);

        usuarioRepositorio.save(usuario);

        return ResponseEntity.ok("Usuario creado correctamente");
    }

    // Metodo para obtener todos los roles existentes
    private Set<Roles> obtenerRoles(CrearUsuarioDTO crearUsuarioDTO) {
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

    // Metodo para construir un nuevo usuario
    private UsuarioModelo construirUsuario(CrearUsuarioDTO crearUsuarioDTO, Set<Roles> roles) {
        UsuarioModelo usuario = UsuarioModelo.builder()
                .nombre(crearUsuarioDTO.getNombre().trim())
                .apellido(crearUsuarioDTO.getApellido().trim())
                .email(crearUsuarioDTO.getEmail().trim())
                .password(passwordEncoder.encode(crearUsuarioDTO.getPassword()).trim())
                .direccionEnvio("".trim())
                .fechaModificacion("".trim())
                .roles(roles)
                .build();

        if (usuario.get_id() == null) {
            usuario.set_id(UUID.randomUUID().toString());
        }

        CrearUsuarioDTO usuarioDTO = new CrearUsuarioDTO();
        usuarioDTO.setFechaCreacion();
        usuario.setFechaCreacion(usuarioDTO.getFechaCreacion());

        return usuario;
    }

    // Metodo para verificar el codigo de recuperacion, si coincide con el de la
    // base de datos y no esta caducado
    public boolean verificarCodigoYExpiracion(RecuperarContraseniaDTO recuperarContraseniaDTO) {
        // Obtengo el código y la fecha de expiración del DTO, le pongo el trim para lo
        // del los espacios en blanco
        String codigoRecuperacion = recuperarContraseniaDTO.getRecuperarContrasenia().trim();

        // Verifico si el código de recuperación no está vacío
        if (codigoRecuperacion.isEmpty()) {
            System.out.println("Error: El código de recuperación está vacío.");
            return false;
        }

        // Busco el usuario por el código de recuperación
        Optional<UsuarioModelo> optionalUsuario = usuarioRepositorio.findByRecuperarContrasenia(codigoRecuperacion);

        System.out.println("Código proporcionado: " + codigoRecuperacion);
        System.out.println("Usuario optional: " + optionalUsuario);

        if (optionalUsuario.isPresent()) {
            UsuarioModelo usuario = optionalUsuario.get();
            System.out.println("Usuario: " + usuario);
            System.out
                    .println("Fecha de expiración en la base de datos: " + usuario.getExpiracionRecuperarContrasenia());

            // Verifico si la fecha de expiración no ha pasado
            LocalDateTime fechaExpiracionBD = LocalDateTime.parse(
                    usuario.getExpiracionRecuperarContrasenia(),
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

            LocalDateTime fechaActual = LocalDateTime.now();

            if (fechaActual.isBefore(fechaExpiracionBD)) {
                // La fecha de expiración no ha pasado, ahora verifico si los códigos coinciden
                return codigoRecuperacion.equals(usuario.getRecuperarContrasenia());
            } else {
                System.out.println("El código ha expirado");
            }
        } else {
            System.out.println("Usuario no encontrado");
        }

        return false;
    }

    // public boolean verificarCodigoYExpiracionGenerarToken(RecuperarContraseniaDTO recuperarContraseniaDTO) {
    //     try {
    //         String codigoRecuperacion = recuperarContraseniaDTO.getRecuperarContrasenia().trim();

    //         if (codigoRecuperacion.isEmpty()) {
    //             System.out.println("Error: El código de recuperación está vacío.");
    //             return false;
    //         }

    //         Optional<Usuarios> optionalUsuario = usuarioRepositorio.findByRecuperarContrasenia(codigoRecuperacion);

    //         System.out.println("Código proporcionado: " + codigoRecuperacion);
    //         System.out.println("Usuario optional: " + optionalUsuario);

    //         if (optionalUsuario.isPresent()) {
    //             Usuarios usuario = optionalUsuario.get();
    //             System.out.println("Usuario: " + usuario);
    //             System.out.println("Fecha de expiración en la base de datos: " + usuario.getExpiracionRecuperarContrasenia());

    //             LocalDateTime fechaExpiracionBD = LocalDateTime.parse(
    //                     usuario.getExpiracionRecuperarContrasenia(),
    //                     DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

    //             LocalDateTime fechaActual = LocalDateTime.now();

    //             if (fechaActual.isBefore(fechaExpiracionBD)) {
    //                 if (codigoRecuperacion.equals(usuario.getRecuperarContrasenia())) {
    //                     Set<Roles> rolesUsuario = usuario.getRoles();
    //                     usuario.setRoles(rolesUsuario);
    //                     System.out.println("ROLES USUARIO: " + rolesUsuario);

    //                     // Guardar el usuario actualizado en la base de datos
    //                     usuarioRepositorio.save(usuario);

    //                     // Generar y devolver el token
    //                     String token = jwtUtils.generateJwtToken(usuario.getEmail());
    //                     System.out.println("Token generado con éxito: " + token);
    //                     return true;
    //                 } else {
    //                     System.out.println("Los códigos no coinciden");
    //                 }
    //             } else {
    //                 System.out.println("El código ha expirado");
    //             }
    //         } else {
    //             System.out.println("Usuario no encontrado");
    //         }

    //         return false;
    //     } catch (Exception e) {
    //         System.out.println("Error al verificar el código y la expiración");
    //         e.printStackTrace();
    //         return false;
    //     }
    // }
    


    // public boolean cambiarContraseniaSiVerificacionExitosa(RecuperarContraseniaDTO recuperarContraseniaDTO, String nuevaContrasenia) {
    //     if (verificarCodigoYExpiracion(recuperarContraseniaDTO)) {
    //         String codigoRecuperacion = recuperarContraseniaDTO.getRecuperarContrasenia().trim();
    //         Optional<Usuarios> optionalUsuario = usuarioRepositorio.findByRecuperarContrasenia(codigoRecuperacion);
    
    //         if (optionalUsuario.isPresent()) {
    //             Usuarios usuario = optionalUsuario.get();
    
    //             try {
    //                 String contraseniaHasheada = passwordEncoder.encode(nuevaContrasenia);
    
    //                 // Actualizar la contraseña en la base de datos
    //                 usuario.setPassword(contraseniaHasheada);
    //                 usuario.setRecuperarContrasenia(null);
    
    //                 usuarioRepositorio.save(usuario);
    
    //                 return true;
    //             } catch (Exception e) {
    //                 // Manejar excepciones específicas aquí (puede depender del tipo de passwordEncoder)
    //                 return false;
    //             }
    //         }
    //     }
    
    //     return false;
    // }

}
