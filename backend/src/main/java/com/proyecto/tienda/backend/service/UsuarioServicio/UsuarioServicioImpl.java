package com.proyecto.tienda.backend.service.UsuarioServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.models.*;

import java.text.Normalizer;
import java.util.Optional;

@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // IMPLEMENTACION DEL METODO PARA ELIMINAR EL USUARIO SIENDO USUARIO
    @Override
    public String eliminarUsuarioSiendoUsuario(String token, JwtUtils jwtUtils) {
        String jwtToken = token.replace("Bearer ", "");
        String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);

        Optional<UsuarioModelo> usuarioOptional = usuarioRepositorio.findByEmail(emailFromToken);

        if (usuarioOptional.isPresent()) {
            UsuarioModelo usuario = usuarioOptional.get();
            if (usuario.getEmail().equals(emailFromToken)) {
                usuarioRepositorio.deleteBy_id(usuario.get_id());
                return "Usuario eliminado correctamente";
            } else {
                return "No estás autorizado para eliminar este usuario";
            }
        } else {
            return "Usuario no encontrado";
        }
    }

    // IMPLEMENTACION DEL MÉTODO PARA ACTUALIZAR UN USUARIO SIENDO USUARIO
    @Override
    public String actualizarUsuario(UsuarioActualizacionDTO actualizarUsuarioDTO, String token,
            JwtUtils jwtUtils) {

        String jwtToken = token.replace("Bearer ", "");
        String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);
        System.out.println("EMAIL DEL TOKEN: " + emailFromToken);

        Optional<UsuarioModelo> usuarioOptional = usuarioRepositorio.findByEmail(emailFromToken);

        if (usuarioOptional.isPresent()) {
            UsuarioModelo usuario = usuarioOptional.get();

            // Validar y actualizar los campos que sean diferentes de null
            if (actualizarUsuarioDTO.getNombre() != null && !actualizarUsuarioDTO.getNombre().isEmpty()) {
                // usuario.setNombre(actualizarUsuarioDTO.getNombre().trim());
                usuario.setNombre(normalizeText(actualizarUsuarioDTO.getNombre().trim()));
            }

            if (actualizarUsuarioDTO.getApellido() != null && !actualizarUsuarioDTO.getApellido().isEmpty()) {
                // usuario.setApellido(actualizarUsuarioDTO.getApellido().trim());
                usuario.setApellido(normalizeText(actualizarUsuarioDTO.getApellido().trim()));
            }

            if (actualizarUsuarioDTO.getEmail() != null && !actualizarUsuarioDTO.getEmail().isEmpty()) {
                // Validar que el nuevo email no exista
                Optional<UsuarioModelo> existeEmail = usuarioRepositorio
                        .findByEmail(actualizarUsuarioDTO.getEmail().trim());
                if (existeEmail.isPresent()) {
                    return "El email ya esta en uso";
                }
                usuario.setEmail(actualizarUsuarioDTO.getEmail());
            }

            if (actualizarUsuarioDTO.getPassword() != null && !actualizarUsuarioDTO.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(actualizarUsuarioDTO.getPassword().trim()));
            }

            // Actualizar la fecha de modificación
            actualizarUsuarioDTO.setFechaModificacion();
            usuario.setFechaModificacion(actualizarUsuarioDTO.getFechaModificacion());

            // Guardar los cambios en la base de datos
            usuarioRepositorio.save(usuario);

            return "Usuario actualizado correctamente";
        } else {
            return "No se pudo actualizar el usuario debido a un problema desconocido";
        }
    }

    // METODO PARA INSERTAR CAMPOS SIN TILDE
    private String normalizeText(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

}
