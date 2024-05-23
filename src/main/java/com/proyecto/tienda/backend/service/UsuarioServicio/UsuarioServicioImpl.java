package com.proyecto.tienda.backend.service.UsuarioServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.DTO.DTOPedido.EmpresaAutonomoDireccionFacturacionDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.ParticularDireccionFacturacionDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.AnadirDireccionEnvioDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.models.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // METODO PARA OBTENER LAS DIRECCIONES DE ENVIO Y FACTURACION
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

    // METODO PARA AÑADIR UNA DIRECCION DE ENVIO
    @Override
    public ResponseEntity<?> anadirDireccionEnvio(AnadirDireccionEnvioDTO anadirDireccionEnvioDTO, String token,
            JwtUtils jwtUtils) {
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

                // Construyo la dirección completa.
                if (anadirDireccionEnvioDTO.getCodigoPostal() == null
                        || anadirDireccionEnvioDTO.getCodigoPostal().isEmpty()
                        || anadirDireccionEnvioDTO.getDireccion() == null
                        || anadirDireccionEnvioDTO.getDireccion().isEmpty()
                        || anadirDireccionEnvioDTO.getProvincia() == null
                        || anadirDireccionEnvioDTO.getProvincia().isEmpty()
                        || anadirDireccionEnvioDTO.getNumero() == null
                        || String.valueOf(anadirDireccionEnvioDTO.getNumero()).trim().isEmpty()) {
                    return ResponseEntity.status(400)
                            .body("El código postal, la dirección, la provincia y el número son obligatorios.");
                }

                StringBuilder direccionCompleta = new StringBuilder();
                direccionCompleta.append(anadirDireccionEnvioDTO.getDireccion().trim()).append(", ");
                direccionCompleta.append("Nº ").append(anadirDireccionEnvioDTO.getNumero()).append(", ");

                if (anadirDireccionEnvioDTO.getPiso() != null
                        && !String.valueOf(anadirDireccionEnvioDTO.getPiso()).trim().isEmpty()
                        && anadirDireccionEnvioDTO.getPiso() >= 0) {
                    direccionCompleta.append("Piso ").append(anadirDireccionEnvioDTO.getPiso()).append(", ");
                }

                if (anadirDireccionEnvioDTO.getPuerta() != null && !anadirDireccionEnvioDTO.getPuerta().isEmpty()) {
                    if (!anadirDireccionEnvioDTO.getPuerta().matches("^(?!\\s)(?=\\S).{1,10}(?!\\s)$")) {
                        return ResponseEntity.status(400).body(
                                "La puerta debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco");
                    }
                    direccionCompleta.append("Puerta ").append(anadirDireccionEnvioDTO.getPuerta().trim()).append(", ");
                }

                direccionCompleta.append(anadirDireccionEnvioDTO.getCodigoPostal()).append(", ");
                direccionCompleta.append(anadirDireccionEnvioDTO.getProvincia().trim()).append(", ");

                // Eliminar la coma al final
                if (direccionCompleta.length() > 0) {
                    direccionCompleta.delete(direccionCompleta.length() - 2, direccionCompleta.length());
                }

                // Verifico si la dirección ya existe.
                if (usuario.getDireccionesEnvio() != null
                        && usuario.getDireccionesEnvio()
                                .contains(usuario.convertirEstiloTitulo(direccionCompleta.toString()))) {
                    return ResponseEntity.status(409).body("La dirección ya existe");
                }

                // Agregar la nueva dirección a la lista del usuario
                if (usuario.getDireccionesEnvio() == null) {
                    usuario.setDireccionesEnvio(new ArrayList<>());
                }
                usuario.getDireccionesEnvio().add(usuario.convertirEstiloTitulo(direccionCompleta.toString()));

                // Guardar el usuario actualizado en el repositorio
                usuarioRepositorio.save(usuario);
                System.out.println("direccion anadida" + direccionCompleta.toString());
                return ResponseEntity.ok("Dirección añadida exitosamente");
            } else {
                // Si el usuario no se encuentra, devolver una respuesta de error.
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
        } catch (Exception e) {
            // Manejar cualquier excepción y devolver una respuesta de error.
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }
    }

    // METODO PARA AÑADIR UNA DIRECCION DE FACTURACION EMPRESA-AUTONOMO
    @Override
    public ResponseEntity<?> anadirDireccionFacturacionEmpresaAutonomo(
            EmpresaAutonomoDireccionFacturacionDTO empresaAutonomoDireccionFacturacionDTO, String token,
            JwtUtils jwtUtils) {
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

                // Valido primero los datos
                if (empresaAutonomoDireccionFacturacionDTO.getCifONifFacturacion() == null
                        || empresaAutonomoDireccionFacturacionDTO.getCifONifFacturacion().isEmpty()
                        || empresaAutonomoDireccionFacturacionDTO.getNumTelefonoFacturacion() == null
                        || empresaAutonomoDireccionFacturacionDTO.getDireccionDeFacturacion() == null
                        || empresaAutonomoDireccionFacturacionDTO.getDireccionDeFacturacion().isEmpty()
                        || empresaAutonomoDireccionFacturacionDTO.getCodigoPostalDeFacturacion() == null
                        || empresaAutonomoDireccionFacturacionDTO.getCodigoPostalDeFacturacion().isEmpty()
                        || empresaAutonomoDireccionFacturacionDTO.getProvinciaDeFacturacion() == null
                        || empresaAutonomoDireccionFacturacionDTO.getProvinciaDeFacturacion().isEmpty()
                        || empresaAutonomoDireccionFacturacionDTO.getNumeroDeFacturacion() == null) {
                    throw new IllegalArgumentException(
                            "El Cif/Nif, el número de telefono, la dirección, el código postal, la provincia y el número son obligatorios.");
                }

                // Convierto el Long a String
                String numTelefonoStr = String
                        .valueOf(empresaAutonomoDireccionFacturacionDTO.getNumTelefonoFacturacion());

                // Valido la longitud del número de teléfono
                if (numTelefonoStr.length() != 9) {
                    throw new IllegalArgumentException("El número de teléfono debe tener 9 dígitos.");
                }

                StringBuilder direccionCompletaFacturacion = new StringBuilder();
                direccionCompletaFacturacion.append("Facturacion Empresa/Autonomo: ");
                direccionCompletaFacturacion.append("Cif/Nif: ")
                        .append(empresaAutonomoDireccionFacturacionDTO.getCifONifFacturacion().trim()).append(", ");
                direccionCompletaFacturacion.append("Numero de telefono: ")
                        .append(empresaAutonomoDireccionFacturacionDTO.getNumTelefonoFacturacion()).append(", ");

                direccionCompletaFacturacion
                        .append(empresaAutonomoDireccionFacturacionDTO.getDireccionDeFacturacion().trim()).append(", ");

                direccionCompletaFacturacion.append("Nº ")
                        .append(empresaAutonomoDireccionFacturacionDTO.getNumeroDeFacturacion()).append(", ");

                if (empresaAutonomoDireccionFacturacionDTO.getPisoDeFacturacion() != null
                        && !empresaAutonomoDireccionFacturacionDTO.getPisoDeFacturacion().isEmpty()) {
                    direccionCompletaFacturacion.append("Piso ")
                            .append(empresaAutonomoDireccionFacturacionDTO.getPisoDeFacturacion().trim()).append(",");
                }

                // PUERTA FACTURACION
                if (empresaAutonomoDireccionFacturacionDTO.getPuertaDeFacturacion() != null
                        && !empresaAutonomoDireccionFacturacionDTO.getPuertaDeFacturacion().isEmpty()) {
                    if (!empresaAutonomoDireccionFacturacionDTO.getPuertaDeFacturacion()
                            .matches("^(?!\\s)(?=\\S).{1,10}(?!\\s)$")) {
                        return ResponseEntity.status(400).body(
                                "La puerta debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco");
                    }
                    direccionCompletaFacturacion.append("Puerta ")
                            .append(empresaAutonomoDireccionFacturacionDTO.getPuertaDeFacturacion().trim())
                            .append(", ");
                }

                direccionCompletaFacturacion
                        .append(empresaAutonomoDireccionFacturacionDTO.getCodigoPostalDeFacturacion().trim())
                        .append(", ");
                direccionCompletaFacturacion
                        .append(empresaAutonomoDireccionFacturacionDTO.getProvinciaDeFacturacion().trim()).append(", ");

                // Elimino la coma al final
                if (direccionCompletaFacturacion.length() > 0) {
                    direccionCompletaFacturacion.delete(direccionCompletaFacturacion.length() - 2,
                            direccionCompletaFacturacion.length());
                }

                // Verifico si la dirección ya existe.
                if (usuario.getDirecionesFacturacion() != null
                        && usuario.getDirecionesFacturacion()
                                .contains(usuario.convertirEstiloTitulo(direccionCompletaFacturacion.toString()))) {
                    return ResponseEntity.status(409).body("La dirección de facturación ya existe");
                }

                // Agregar la nueva dirección a la lista del usuario
                if (usuario.getDirecionesFacturacion() == null) {
                    usuario.setDirecionesFacturacion(new ArrayList<>());
                }
                usuario.getDirecionesFacturacion()
                        .add(usuario.convertirEstiloTitulo(direccionCompletaFacturacion.toString()));

                // Guardao el usuario actualizado
                usuarioRepositorio.save(usuario);
                System.out.println("direccion anadida" + direccionCompletaFacturacion.toString());
                return ResponseEntity.ok("Dirección de facturación añadida exitosamente");
            } else {
                // Si el usuario no se encuentra devuelvo una respuesta de error.
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

        } catch (Exception e) {
            // Manejo cualquier excepción y devolver una respuesta de error.
            return ResponseEntity.status(401).body("Token inválido o expirado");

        }

    }

    // METODO PARA AÑADIR UNA DIRECCION DE FACTURACION PARTICULAR
    @Override
    public ResponseEntity<?> anadirDireccionFacturacionParticular(
            ParticularDireccionFacturacionDTO particularDireccionFacturacionDTO, String token,
            JwtUtils jwtUtils) {
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

                // Valido primero los datos
                if (particularDireccionFacturacionDTO.getNombreFacturacion() == null
                        || particularDireccionFacturacionDTO.getNombreFacturacion().isEmpty()
                        || particularDireccionFacturacionDTO.getApellidoFacturacion() == null
                        || particularDireccionFacturacionDTO.getApellidoFacturacion().isEmpty()
                        || particularDireccionFacturacionDTO.getNumTelefonoFacturacion() == null
                        || particularDireccionFacturacionDTO.getDireccionDeFacturacion() == null
                        || particularDireccionFacturacionDTO.getDireccionDeFacturacion().isEmpty()
                        || particularDireccionFacturacionDTO.getCodigoPostalDeFacturacion() == null
                        || particularDireccionFacturacionDTO.getCodigoPostalDeFacturacion().isEmpty()
                        || particularDireccionFacturacionDTO.getProvinciaDeFacturacion() == null
                        || particularDireccionFacturacionDTO.getProvinciaDeFacturacion().isEmpty()
                        || particularDireccionFacturacionDTO.getNumeroDeFacturacion() == null
                        || particularDireccionFacturacionDTO.getNumeroDeFacturacion().isEmpty()) {
                    throw new IllegalArgumentException(
                            "El nombre, el apellido, el número de telefono, la dirección, el código postal, la provincia y el número son obligatorios.");
                }

                // Convierto el Long a String
                String numTelefonoStr = String
                        .valueOf(particularDireccionFacturacionDTO.getNumTelefonoFacturacion());

                // Valido la longitud del número de teléfono
                if (numTelefonoStr.length() != 9) {
                    throw new IllegalArgumentException("El número de teléfono debe tener 9 dígitos.");
                }

                StringBuilder direccionCompletaFacturacion = new StringBuilder();
                direccionCompletaFacturacion.append("Facturacion Particular: ");
                direccionCompletaFacturacion.append("Nombre: ")
                        .append(particularDireccionFacturacionDTO.getNombreFacturacion().trim()).append(", ");
                direccionCompletaFacturacion.append("Apellido: ")
                        .append(particularDireccionFacturacionDTO.getApellidoFacturacion().trim()).append(", ");
                direccionCompletaFacturacion.append("Numero de telefono: ")
                        .append(particularDireccionFacturacionDTO.getNumTelefonoFacturacion()).append(", ");
                direccionCompletaFacturacion.append("Dirección: ")
                        .append(particularDireccionFacturacionDTO.getDireccionDeFacturacion().trim()).append(", ");
                direccionCompletaFacturacion.append("Nº ")
                        .append(particularDireccionFacturacionDTO.getNumeroDeFacturacion().trim()).append(", ");

                if (particularDireccionFacturacionDTO.getPisoDeFacturacion() != null
                        && !particularDireccionFacturacionDTO.getPisoDeFacturacion().isEmpty()) {
                    direccionCompletaFacturacion.append("Piso ")
                            .append(particularDireccionFacturacionDTO.getPisoDeFacturacion().trim()).append(", ");
                }

                if (particularDireccionFacturacionDTO.getPuertaDeFacturacion() != null
                        && !particularDireccionFacturacionDTO.getPuertaDeFacturacion().isEmpty()) {
                    direccionCompletaFacturacion.append("Puerta ")
                            .append(particularDireccionFacturacionDTO.getPuertaDeFacturacion().trim())
                            .append(", ");
                }

                direccionCompletaFacturacion
                        .append(particularDireccionFacturacionDTO.getCodigoPostalDeFacturacion().trim()).append(", ");
                direccionCompletaFacturacion
                        .append(particularDireccionFacturacionDTO.getProvinciaDeFacturacion().trim()).append(", ");

                // Elimino la coma al final
                if (direccionCompletaFacturacion.length() > 0) {
                    direccionCompletaFacturacion.delete(direccionCompletaFacturacion.length() - 2,
                            direccionCompletaFacturacion.length());
                }

                // Verifico si la dirección ya existe.
                if (usuario.getDirecionesFacturacion() != null
                        && usuario.getDirecionesFacturacion()
                                .contains(usuario.convertirEstiloTitulo(direccionCompletaFacturacion.toString()))) {
                    return ResponseEntity.status(409).body("La dirección de facturación ya existe");
                }

                // Agregar la nueva dirección a la lista del usuario
                if (usuario.getDirecionesFacturacion() == null) {
                    usuario.setDirecionesFacturacion(new ArrayList<>());
                }
                usuario.getDirecionesFacturacion()
                        .add(usuario.convertirEstiloTitulo(direccionCompletaFacturacion.toString()));

                // Guardao el usuario actualizado
                usuarioRepositorio.save(usuario);
                System.out.println("direccion anadida" + direccionCompletaFacturacion.toString());
                return ResponseEntity.ok("Dirección de facturación añadida exitosamente");
            } else {
                // Si el usuario no se encuentra devuelvo una respuesta de error.
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

        } catch (Exception e) {
            // Manejo cualquier excepción y devolver una respuesta de error.
            return ResponseEntity.status(401).body("Token inválido o expirado");

        }

    }

    // METODO PARA ELIMINAR UNA DIRECCION DE ENVIO
    @Override
    public ResponseEntity<?> eliminarDireccionEnvio(Integer index, String token, JwtUtils jwtUtils) {
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

                System.out.println("Usuario" + usuario);

                // Obtengo la lista de direcciones de envío del usuario
                List<String> direccionesEnvio = usuario.getDireccionesEnvio();

                // Verifico si el índice está dentro de los límites
                if (index >= 0 && index < direccionesEnvio.size()) {
                    // Elimino la dirección de envío en el índice especificado
                    // Elimino la dirección de envío en el índice especificado
                    direccionesEnvio.remove(direccionesEnvio.get(index));

                    // Guardo el usuario actualizado en el repositorio
                    usuarioRepositorio.save(usuario);

                    return ResponseEntity.ok("Dirección de envío eliminada exitosamente");
                } else {
                    // Si el índice está fuera de los límites, devuelvo una respuesta de error
                    return ResponseEntity.status(400).body("Índice de dirección de envío inválido");
                }
            } else {
                // Si el usuario no se encuentra, devuelvo una respuesta de error.
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
        } catch (Exception e) {
            // Manejo cualquier excepción y devuelvo una respuesta de error.
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }
    }

    // METODO PARA ELIMINAR UNA DIRECCION DE FACTURACION
    @Override
    public ResponseEntity<?> eliminarDireccionFacturacion(Integer index, String token, JwtUtils jwtUtils) {
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

                System.out.println("Usuario" + usuario);

                // Obtengo la lista de direcciones de envío del usuario
                List<String> direccionesFacturacion = usuario.getDirecionesFacturacion();

                // Verifico si el índice está dentro de los límites
                if (index >= 0 && index < direccionesFacturacion.size()) {
                    // Elimino la dirección de envío en el índice especificado
                    // Elimino la dirección de envío en el índice especificado
                    direccionesFacturacion.remove(direccionesFacturacion.get(index));

                    // Guardo el usuario actualizado en el repositorio
                    usuarioRepositorio.save(usuario);

                    return ResponseEntity.ok("Dirección de facturación eliminada exitosamente");
                } else {
                    // Si el índice está fuera de los límites, devuelvo una respuesta de error
                    return ResponseEntity.status(400).body("Índice de dirección de envío inválido");
                }
            } else {
                // Si el usuario no se encuentra, devuelvo una respuesta de error.
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
        } catch (Exception e) {
            // Manejo cualquier excepción y devuelvo una respuesta de error.
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }
    }

}
