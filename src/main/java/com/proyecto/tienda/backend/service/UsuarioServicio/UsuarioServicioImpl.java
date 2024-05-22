package com.proyecto.tienda.backend.service.UsuarioServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.DTO.DTOCarrito.ProductoCarrito;
import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.models.*;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
                usuario.setNombre(normalizeText(actualizarUsuarioDTO.getNombre().trim().toUpperCase()));
            }

            if (actualizarUsuarioDTO.getApellido() != null && !actualizarUsuarioDTO.getApellido().isEmpty()) {
                // usuario.setApellido(actualizarUsuarioDTO.getApellido().trim());
                usuario.setApellido(normalizeText(actualizarUsuarioDTO.getApellido().trim().toUpperCase()));
            }

            // Para que me actualice el email si coincide con el que ya tiene

            if (actualizarUsuarioDTO.getEmail() != null && !actualizarUsuarioDTO.getEmail().isEmpty()) {
                // Validar que el nuevo email no exista
                Optional<UsuarioModelo> existeEmail = usuarioRepositorio
                        .findByEmail(actualizarUsuarioDTO.getEmail().trim());
                if (existeEmail.isPresent()) {

                    if (emailFromToken.equals(actualizarUsuarioDTO.getEmail())) {
                        System.out.println("email toke: " + emailFromToken);
                        System.out.println("email nuevo: " + actualizarUsuarioDTO.getEmail());
                        usuario.setEmail(actualizarUsuarioDTO.getEmail());
                    } else {
                        return "El email ya esta en uso";
                    }
                }
                usuario.setEmail(actualizarUsuarioDTO.getEmail());
            }

            if (actualizarUsuarioDTO.getPassword() != null && !actualizarUsuarioDTO.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(actualizarUsuarioDTO.getPassword()));
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

    @Override
    public ResponseEntity<?> obtenerDireccionesEnvioFacturacion(String token, JwtUtils jwtUtils) {
        try {
            // Elimino el prefijo "Bearer " del token JWT.
            String jwtToken = token.replace("Bearer ", "");

            // Extraigo el email del token usando JwtUtils.
            String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);

            // Busco al usuario en el repositorio por el email extraído.
            Optional<UsuarioModelo> usuarioOptional = usuarioRepositorio.findByEmail(emailFromToken);

            // Verifico si el usuario existe.
            if (usuarioOptional.isPresent()) {
                // Obtengo el usuario de la opción.
                UsuarioModelo usuario = usuarioOptional.get();

                 // Obtengo las direcciones de envío y facturación del usuario.
                List<String> direccionesEnvio = usuario.getDireccionesEnvio();
                List<String> direccionesFacturacion = usuario.getDirecionesFacturacion();



                // Creao una respuesta que contenga ambas listas de direcciones.
                Map<String, Object> response = new HashMap<>();
                response.put("direccionesEnvio", direccionesEnvio);
                response.put("direccionesFacturacion", direccionesFacturacion);

                // Devuelvo la respuesta con las direcciones obtenidas.
                return ResponseEntity.ok(response);
            } else {
                // Si el usuario no se encuentra, devolver una respuesta de error.
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
        } catch (Exception e) {
            // Manejar cualquier excepción y devolver una respuesta de error.
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }
    }


 

}