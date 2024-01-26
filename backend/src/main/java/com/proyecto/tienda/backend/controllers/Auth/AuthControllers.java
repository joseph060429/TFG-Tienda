package com.proyecto.tienda.backend.controllers.Auth;

import org.springframework.web.bind.annotation.RestController;
// import com.proyecto.tienda.backend.controllers.request.CrearUsuarioDTO;
// import com.proyecto.tienda.backend.controllers.request.EnviarCorreoDTO;
// import com.proyecto.tienda.backend.controllers.request.RecuperarContraseniaDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.CrearUsuarioDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.EnviarCorreoDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.RecuperarContraseniaDTO;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.service.AuthServicio.AuthServicio;
import com.proyecto.tienda.backend.util.Gmailer;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class AuthControllers {

    @Autowired
    private AuthServicio authServicio;

    @Autowired
    private Gmailer gmailer;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World SIN SEGURIDAD";
    }

    @GetMapping("/helloSeguridad")
    @PreAuthorize("#userId == authentication.principal.id")
    public String helloSeguridad() {
        return "Hello World CON SEGURIDAD";
    }

    // Controlador para crear
    @PostMapping("/crearUsuario")
    public ResponseEntity<?> crearUsuario(@RequestBody @Valid CrearUsuarioDTO crearUsuarioDTO) {
        // return authServicio.crearNuevoUsuario(crearUsuarioDTO);
        try {
            // Validar y crear el producto
            ResponseEntity<?> response = authServicio.crearNuevoUsuario(crearUsuarioDTO);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Manejar cualquier excepción general y devolver una respuesta de error
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // Controlador para enviar codigo de recuperacion, viene de util, Gmailer
    @PostMapping("/envio-codigo-recuperacion")
    public ResponseEntity<String> enviarEmail(@RequestBody @Valid EnviarCorreoDTO correoUsuarioSolicitud) {
        try {
            // Verificar si el usuario existe
            Optional<UsuarioModelo> usuarioOptional = usuarioRepositorio.findByEmail(correoUsuarioSolicitud.getEmail());

            if (usuarioOptional.isPresent()) {
                // Usuario encontrado, enviar correo
                gmailer.enviarEmaildeRecuperacion(correoUsuarioSolicitud.getEmail(), "Asunto del correo",
                        "Cuerpo del mensaje");
                return ResponseEntity.ok("Un email ha sido enviado a tu correo si estas registrado en la aplicación");
            } else {
                // Usuario no encontrado, devolver respuesta adecuada
                return ResponseEntity.status(HttpStatus.OK)
                        .body("Un email ha sido enviado a tu correo si estas registrado en la aplicación");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar el correo");
        }
    }

    // Controlador para verificar si el codigo existe en la base de datos y a si no
    // esta caducado
    @PostMapping("/verificar-codigo")
    public ResponseEntity<String> verificarCodigo(@RequestBody Map<String, String> requestBody) {
        // Creo un nuevo objeto RecuperarContraseniaDTO y le asigno el código
        // proporcionado
        RecuperarContraseniaDTO recuperarContraseniaDTO = new RecuperarContraseniaDTO();
        recuperarContraseniaDTO.setRecuperarContrasenia(requestBody.get("codigoRecuperacion"));

        // Llamas al servicio para verificar el código y la expiración
        boolean exitoso = authServicio.verificarCodigoYExpiracion(recuperarContraseniaDTO);

        if (exitoso) {
            return ResponseEntity.ok("Código válido. Puede proceder con la recuperación de contraseña.");
        } else {
            return ResponseEntity.status(401).body(
                    "Error: Código incorrecto o la fecha de expiración ha pasado. Verifique el código o solicite uno nuevo.");
        }
    }

    // @PostMapping("/verificar-codigo")
    // public ResponseEntity<String> verificarCodigo(@RequestBody Map<String,
    // String> requestBody) {
    // RecuperarContraseniaDTO recuperarContraseniaDTO = new
    // RecuperarContraseniaDTO();
    // recuperarContraseniaDTO.setRecuperarContrasenia(requestBody.get("codigoRecuperacion"));

    // boolean exitoso =
    // authServicio.verificarCodigoYExpiracion(recuperarContraseniaDTO);

    // if (exitoso) {
    // // Genera un token o marca que indique una verificación exitosa
    // String tokenVerificacion = generarTokenVerificacion();

    // // Puedes adjuntar el token a la respuesta, aquí se utiliza un encabezado
    // personalizado
    // HttpHeaders headers = new HttpHeaders();
    // headers.add("X-Verificacion-Token", tokenVerificacion);
    // System.out.println("Token de verificación: " + tokenVerificacion);

    // // Redirige a la ruta de cambio de contraseña
    // headers.setLocation(URI.create("/cambiar-contrasenia"));

    // return new ResponseEntity<>(headers, HttpStatus.FOUND);
    // } else {
    // return ResponseEntity.status(401).body("Error: Código incorrecto o la fecha
    // de expiración ha pasado. Verifique el código o solicite uno nuevo.");
    // }
    // }

    // private String generarTokenVerificacion() {
    // // Genera un token único utilizando UUID
    // return UUID.randomUUID().toString();
    // }

    // @PostMapping("/verificar-codigo")
    // public ResponseEntity<String> verificarCodigo(@RequestBody Map<String,
    // String> requestBody) {
    // String codigoRecuperacion = requestBody.get("codigoRecuperacion");

    // if (codigoRecuperacion == null || codigoRecuperacion.trim().isEmpty()) {
    // return ResponseEntity.badRequest().body("Error: El código de recuperación
    // está vacío.");
    // }

    // RecuperarContraseniaDTO recuperarContraseniaDTO = new
    // RecuperarContraseniaDTO();
    // recuperarContraseniaDTO.setRecuperarContrasenia(codigoRecuperacion);

    // boolean exitoso =
    // authServicio.verificarCodigoYExpiracion(recuperarContraseniaDTO);

    // if (exitoso) {
    // return ResponseEntity.ok("Código válido. Puede proceder con la recuperación
    // de contraseña.");
    // } else {
    // return ResponseEntity.status(401)
    // .body("Error: Código incorrecto o la fecha de expiración ha pasado. Verifique
    // el código o solicite uno nuevo.");
    // }
    // }

    // @PostMapping("/verificar-codigo")
    // public ResponseEntity<String> verificarCodigo(@RequestBody Map<String,
    // String> requestBody) {
    // String codigoRecuperacion = requestBody.get("codigoRecuperacion");

    // if (codigoRecuperacion == null || codigoRecuperacion.trim().isEmpty()) {
    // return ResponseEntity.badRequest().body("Error: El código de recuperación
    // está vacío.");
    // }

    // RecuperarContraseniaDTO recuperarContraseniaDTO = new
    // RecuperarContraseniaDTO();
    // recuperarContraseniaDTO.setRecuperarContrasenia(codigoRecuperacion);

    // boolean exitoso =
    // authServicio.verificarCodigoYExpiracionGenerarToken(recuperarContraseniaDTO);

    // if (exitoso) {
    // // Devuelve la URL de redirección como texto si la verificación es exitosa
    // // Devolver la URL del controlador de cambio de contraseña
    // String urlCambioContrasenia = "/cambiar-contrasenia";
    // String successMessage = "¡Código verificado con éxito! Por favor, visite " +
    // urlCambioContrasenia + " para cambiar su contraseña.";
    // return ResponseEntity.ok().body(successMessage);
    // // return ResponseEntity.ok().body("/cambiar-contrasenia");
    // } else {
    // return ResponseEntity.status(401)
    // .body("Error: Código incorrecto o la fecha de expiración ha pasado. Verifique
    // el código o solicite uno nuevo.");
    // }
    // }

    // @PreAuthorize("hasRole('ROLE_USER')")
    // @PostMapping("/cambiar-contrasenia")
    // public ResponseEntity<String> cambiarContrasenia(@RequestBody Map<String,
    // String> requestBody) {
    // String nuevaContrasenia = requestBody.get("nuevaContrasenia");
    // String confirmarContrasenia = requestBody.get("confirmarContrasenia");

    // // Verificar si ambas contraseñas coinciden
    // if (nuevaContrasenia == null || confirmarContrasenia == null ||
    // !nuevaContrasenia.equals(confirmarContrasenia)) {
    // return ResponseEntity.badRequest().body("Las contraseñas no coinciden");
    // }

    // RecuperarContraseniaDTO recuperarContraseniaDTO = new
    // RecuperarContraseniaDTO();
    // recuperarContraseniaDTO.setRecuperarContrasenia(requestBody.get("codigoRecuperacion"));

    // // Llamar al servicio para cambiar la contraseña
    // boolean cambioExitoso =
    // authServicio.cambiarContraseniaSiVerificacionExitosa(recuperarContraseniaDTO,
    // nuevaContrasenia);

    // // Verificar el resultado del cambio
    // if (cambioExitoso) {
    // // Contraseña cambiada con éxito
    // String successMessage = "¡Contraseña cambiada con éxito!";
    // return ResponseEntity.ok().body(successMessage);
    // } else {
    // // Contraseña no pudo ser cambiada debido a contraseñas que no coinciden o
    // verificación fallida
    // return ResponseEntity.badRequest().body("No se pudo cambiar la contraseña.
    // Verifica el código de recuperación y vuelve a intentarlo.");
    // }
    // }

}

// @PostMapping("/actualizar-contrasenia")
// public ResponseEntity<String> actualizarContrasenia(
// @RequestBody @Valid RecuperarContraseniaDTO recuperarContraseniaDTO) {
// String codigoRecuperacion =
// recuperarContraseniaDTO.getRecuperarContrasenia();
// String nuevaPassword = recuperarContraseniaDTO.getPassword();

// // Lógica para verificar el código de recuperación y actualizar la contraseña
// String resultado = authServicio.recuperarContrasenia(recuperarContraseniaDTO,
// codigoRecuperacion, nuevaPassword);

// // Devuelve el resultado de la verificación y actualización
// return ResponseEntity.ok(resultado);
// }

// Este metodo es para crear un nuevo rol por si me cargo por casualidad el
// ADMIN xd

// Set<Roles> roles = crearUsuarioDTO.getRoles().stream()
// .map(rol -> {
// Roles nuevoRol = Roles.builder()
// .name(ERol.valueOf(rol))
// .build();

// //Asigno un UUID a mi ID de roles
// nuevoRol.setId(UUID.randomUUID().toString());

// //Guardo el rol con el nuevo ID
// nuevoRol = rolesRepositorio.save(nuevoRol);
// return nuevoRol;
// })
// .collect(Collectors.toSet());
