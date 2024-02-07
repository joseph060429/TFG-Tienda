package com.proyecto.tienda.backend.service.AuthServicio;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    // @Autowired
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

    // nuevoProducto.setNombreProducto(normalizeText(crearProductoDTO.getNombreProducto().trim()));

    private UsuarioModelo construirUsuario(CrearUsuarioDTO crearUsuarioDTO, Set<Roles> roles) {
        UsuarioModelo usuario = UsuarioModelo.builder()
                .nombre(normalizeText(crearUsuarioDTO.getNombre().trim()))
                .apellido(normalizeText(crearUsuarioDTO.getApellido().trim()))
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
        
        if (optionalUsuario.isPresent()) {
            UsuarioModelo usuario = optionalUsuario.get();
            // System.out.println("Usuario: " + usuario);
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
            // System.out.println("Usuario no encontrado");
        }

        return false;
    }

    // Metodo para cambiar la contraseña, verifica el codigo de envio, si este es
    // exitoso procedo a cambiar la contraseña con el metodo
    // procesarCambioContrasenia
    // Solo me cambiara la contraseña si y solo si el codigo es exitoso
    public ResponseEntity<String> cambiarContrasenia(RecuperarContraseniaDTO recuperarContraseniaDTO) {
        try {
            // Verificar que las contraseñas coincidan
            if (!recuperarContraseniaDTO.getPassword().equals(recuperarContraseniaDTO.getRepitaPassword())) {
                return ResponseEntity.badRequest()
                        .body("Las contraseñas no coinciden. Por favor, inténtalo nuevamente.");
            }

            // Verifico el código y la expiración
            boolean exitoso = verificarCodigoYExpiracion(recuperarContraseniaDTO);

            if (exitoso) {
                return procesarCambioContrasenia(recuperarContraseniaDTO);
            } else {
                return ResponseEntity.badRequest()
                        .body("Error: Código incorrecto o la fecha de expiración ha pasado. Verifique el código o solicite uno nuevo.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // Metodo para procesar todos los datos del cambio de contraseña
    private ResponseEntity<String> procesarCambioContrasenia(RecuperarContraseniaDTO recuperarContraseniaDTO) {
        // Busco el usuario por el código de recuperación
        Optional<UsuarioModelo> usuarioOptional = usuarioRepositorio
                .findByRecuperarContrasenia(recuperarContraseniaDTO.getRecuperarContrasenia().trim());

        // Verifico si el usuario existe a traves del codigo de recuperación
        if (usuarioOptional.isPresent()) {
            // Codifico la nueva contraseña
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode(recuperarContraseniaDTO.getPassword());

            // Actualizo la contraseña del usuario
            UsuarioModelo usuario = usuarioOptional.get();
            usuario.setPassword(encodedPassword);
            usuarioRepositorio.save(usuario);

            return ResponseEntity.ok("Contraseña cambiada correctamente");
        } else {
            return ResponseEntity.badRequest()
                    .body("No se encontró un usuario con el código de recuperación proporcionado.");
        }
    }

      // Metodo para normalizar los textos que ponga el usuario y me busque sin tilde
    // los campos
    private String normalizeText(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
