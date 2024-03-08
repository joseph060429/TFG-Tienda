package com.proyecto.tienda.backend.service.AuthServicio;

import org.springframework.http.ResponseEntity;
import com.proyecto.tienda.backend.DTO.DTOUsuario.CrearUsuarioDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.RecuperarContraseniaDTO;

public interface AuthServicio {

    // METODO PARA CREAR UN NUEVO USUARIO
    ResponseEntity<?> crearNuevoUsuario(CrearUsuarioDTO crearUsuarioDTO);

    // METODO PARA CAMBIAR LA CONTRASEÑA
    ResponseEntity<String> cambiarContrasenia(RecuperarContraseniaDTO recuperarContraseniaDTO);

    // METODO PARA VERIFICAR EL CODIGO DE RECUPERACION, SI COINCIDE CON EL CAMPO RECUPERARCONTRASENIA Y NO ESTÁ CADUCADO,
    boolean verificarCodigoYExpiracion(RecuperarContraseniaDTO recuperarContraseniaDTO);

}
