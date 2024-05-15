package com.proyecto.tienda.backend.service.AdminServicio;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.UtilEnum.ERol;
import com.proyecto.tienda.backend.models.RolesModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.RolesRepositorio;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;

@Service
public class AdminServicioImpl implements AdminServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolesRepositorio rolesRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // IMPLEMENTADCION DEL METODO PARA LISTAR TODOS LOS USUARIOS
    @Override
    public List<UsuarioModelo> listarUsuarios() {
        return usuarioRepositorio.findAllWithoutPassword();
    }

    // IMPLEMENTACION DEL METODO PARA LISTAR UN USUARIO
    @Override
    public UsuarioModelo listarUnUsuario(String _id) {

        Optional<UsuarioModelo> optionalUsuario = usuarioRepositorio.findById(_id);

        // Verifica si el usuario está presente en la base de datos
        if (optionalUsuario.isPresent()) {
            // Devuelve el usuario si ha sido encontrado
            return optionalUsuario.get();
        } else {
            // Devuelve null si el usuario no ha sido encontrado
            return null;
        }
    }

    // IMPLEMENTACION DEL METODO PARA ELIMINAR UN USUARIO POR EMAIL
    @Override
    public String eliminarUsuarioSiendoAdmin(String email) {

        Optional<UsuarioModelo> usuarioOptional = usuarioRepositorio.findByEmail(email);

        if (usuarioOptional.isPresent()) {
            UsuarioModelo usuario = usuarioOptional.get();
            usuarioRepositorio.deleteBy_id(usuario.get_id());
            return "Usuario eliminado correctamente";
        } else {
            return "Usuario no encontrado";
        }
    }

    // IMPLEMENTACION DEL METODO PARA ACTUALIZAR EL ROL DE UN USUARIO
    @Override
    public ResponseEntity<?> actualizarRolUsuario(String usuarioId, Set<String> nuevosRoles) {
        
        if (nuevosRoles.isEmpty()) {
            return ResponseEntity.badRequest().body("Debes proporcionar al menos un rol");
        }

        Set<RolesModelo> roles = obtenerRolesPorNombresExistentes(nuevosRoles);

        if (roles.isEmpty()) {
            return ResponseEntity.badRequest().body("Los roles proporcionados no son  válidos");
        }

        Optional<UsuarioModelo> optionalUsuario = usuarioRepositorio.findById(usuarioId);

        // if (optionalUsuario.isPresent()) {
        //     UsuarioModelo usuario = optionalUsuario.get();

        //     usuario.setRoles(roles);
        //     usuarioRepositorio.save(usuario);

        //     return ResponseEntity.ok("Rol actualizado correctamente");

        if (optionalUsuario.isPresent()) {
            UsuarioModelo usuario = optionalUsuario.get();
    
            // Verificar si el usuario ya tiene alguno de los roles nuevos
            for (RolesModelo nuevoRol : roles) {
                if (usuario.getRoles().contains(nuevoRol)) {
                    return ResponseEntity.badRequest().body("El usuario ya tiene ese rol");
                }
            }
    
            usuario.setRoles(roles);
            usuarioRepositorio.save(usuario);
    
            return ResponseEntity.ok("Rol actualizado correctamente");
        } else {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }
    }

    // IMPLEMENTYCION DEL METODO PARA ACTUALIZAR EL PERFIL DE UN USUARIO
    @Override
    public String actualizarUsuarioSiendoAdmin(String userId, UsuarioActualizacionDTO actualizarUsuarioDTO,
            String token, JwtUtils jwtUtils) {
        String jwtToken = token.replace("Bearer ", "");
        String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);

        Optional<UsuarioModelo> usuarioOptional = usuarioRepositorio.findByEmail(emailFromToken);

        if (usuarioOptional.isPresent()
                && usuarioOptional.get().getRoles().stream().anyMatch(role -> ERol.ADMIN.equals(role.getName()))) {
            Optional<UsuarioModelo> usuarioAActualizarOptional = usuarioRepositorio.findById(userId);

            if (usuarioAActualizarOptional.isPresent()) {
                UsuarioModelo usuario = usuarioAActualizarOptional.get();

                // Validar y actualizar los campos que sean diferentes de null
                if (actualizarUsuarioDTO.getNombre() != null &&
                        !actualizarUsuarioDTO.getNombre().isEmpty()) {
                    usuario.setNombre(normalizeText(actualizarUsuarioDTO.getNombre().trim()));
                }

                if (actualizarUsuarioDTO.getApellido() != null &&
                        !actualizarUsuarioDTO.getApellido().isEmpty()) {
                    usuario.setApellido(normalizeText(actualizarUsuarioDTO.getApellido().trim()));
                }

                if (actualizarUsuarioDTO.getEmail() != null &&
                        !actualizarUsuarioDTO.getEmail().isEmpty()) {
                    // Validar que el nuevo email no exista
                    Optional<UsuarioModelo> existeEmail = usuarioRepositorio
                            .findByEmail(actualizarUsuarioDTO.getEmail());
                    if (existeEmail.isPresent()) {
                        return "El email ya está en uso";
                    }
                    usuario.setEmail(actualizarUsuarioDTO.getEmail());
                }

                if (actualizarUsuarioDTO.getPassword() != null &&
                        !actualizarUsuarioDTO.getPassword().isEmpty()) {
                    usuario.setPassword(passwordEncoder.encode(actualizarUsuarioDTO.getPassword()));
                }

                // Actualizar la fecha de modificación
                actualizarUsuarioDTO.setFechaModificacion();
                usuario.setFechaModificacion(actualizarUsuarioDTO.getFechaModificacion());

                // Guardar los cambios en la base de datos
                usuarioRepositorio.save(usuario);

                return "Usuario actualizado correctamente POR EL ADMINISTRADOR";
            } else {
                return "Usuario no encontrado";
            }
        } else {
            return "No tienes permisos para realizar esta acción";
        }
    }

    // IMPLEMENTACION DEL METODO PARA OBTENER LOS NOMBRES DE LOS ROLES EXISTENTES
    @Override
    public Set<RolesModelo> obtenerRolesPorNombresExistentes(Set<String> nombresRoles) {
        Set<RolesModelo> rolesExistentes = new HashSet<>();

        for (String nombreRol : nombresRoles) {
            try {
                ERol rolEnum = ERol.valueOf(nombreRol);
                RolesModelo rol = rolesRepositorio.findByName(rolEnum.name()).orElse(null);

                if (rol != null) {
                    rolesExistentes.add(rol);
                }
            } catch (IllegalArgumentException ignored) {
                // Esta excecption como es de un for y no quiero que coja un rol malo y salga
                // del programa, le
                // pongo un continue para que siga su flujo normal.
                continue;
            }
        }
        return rolesExistentes;
    }

    // IMPLEMENTACION DEL METODO PARA NORMALIZAR TEXTO, SIN TILDES Y CARACTERES ESPECIALES
    private String normalizeText(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

}
