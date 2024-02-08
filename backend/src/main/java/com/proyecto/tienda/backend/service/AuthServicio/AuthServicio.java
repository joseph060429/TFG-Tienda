package com.proyecto.tienda.backend.service.AuthServicio;

import org.springframework.http.ResponseEntity;
import com.proyecto.tienda.backend.DTO.DTOUsuario.CrearUsuarioDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.RecuperarContraseniaDTO;

public interface AuthServicio {

    ResponseEntity<?> crearNuevoUsuario(CrearUsuarioDTO crearUsuarioDTO);

    ResponseEntity<String> cambiarContrasenia(RecuperarContraseniaDTO recuperarContraseniaDTO);

    boolean verificarCodigoYExpiracion(RecuperarContraseniaDTO recuperarContraseniaDTO);
    
}
