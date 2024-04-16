package com.proyecto.tienda.backend.controllers.Auth;

import org.springframework.web.bind.annotation.RestController;
import com.proyecto.tienda.backend.DTO.DTOUsuario.CrearUsuarioDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.EnviarCorreoDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.RecuperarContraseniaDTO;
import com.proyecto.tienda.backend.service.AuthServicio.AuthServicio;
import com.proyecto.tienda.backend.util.ResendUtil;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ResendUtil resend;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World SIN SEGURIDAD";
    }

    @GetMapping("/helloSeguridad")
    @PreAuthorize("#userId == authentication.principal.id")
    public String helloSeguridad() {
        return "Hello World CON SEGURIDAD";
    }

    // CONTROLADOR PARA CREAR UN USUARIO
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

    //CONTROLADOR PARA ENVIAR CORREO CON CODIGO DE RECUPERACION
    @PostMapping("/envioCodigoRecuperacion")
    public String enviarCorreo(@RequestBody EnviarCorreoDTO correoDTO) {
        resend.enviarEmailDeRecuperacion(correoDTO.getEmail().trim());
        return "Correo de recuperación enviado con éxito.";
    }

    // CONTROLADOR PARA VERIFICAR SI EL CÓDIGO EXISTE EN LA BASE DE DATOS Y SI NO ESTÁ CADUCADO
    @PostMapping("/verificarCodigo")
    public ResponseEntity<String> verificarCodigo(@RequestBody Map<String, String> requestBody) {
        // Creo un nuevo objeto RecuperarContraseniaDTO y le asigno el código
        // proporcionado
        RecuperarContraseniaDTO recuperarContraseniaDTO = new RecuperarContraseniaDTO();
        recuperarContraseniaDTO.setRecuperarContrasenia(requestBody.get("codigoRecuperacion"));

        // Llamo al servicio para verificar el código y la expiración
        boolean exitoso = authServicio.verificarCodigoYExpiracion(recuperarContraseniaDTO);

        if (exitoso) {
            return ResponseEntity.ok("Código válido. Puede proceder con la recuperación de contraseña.");
        } else {
            return ResponseEntity.status(401).body(
                    "Error: Código incorrecto o la fecha de expiración ha pasado. Verifique el código o solicite uno nuevo.");
        }
    }

    // CONTROLADOR PARA CAMBIAR LA CONTRASEÑA
    @PostMapping("/cambiarContrasenia")
    public ResponseEntity<String> cambiarContrasenia(@RequestBody @Valid
    RecuperarContraseniaDTO recuperarContraseniaDTO) {
    return authServicio.cambiarContrasenia(recuperarContraseniaDTO);
    }    

}
