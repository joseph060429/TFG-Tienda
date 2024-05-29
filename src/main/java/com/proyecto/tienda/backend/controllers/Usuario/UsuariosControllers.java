package com.proyecto.tienda.backend.controllers.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.proyecto.tienda.backend.DTO.DTOPedido.EmpresaAutonomoDireccionFacturacionDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.ParticularDireccionFacturacionDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.AnadirDireccionEnvioDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.UsuarioActualizacionDTO;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;
import com.proyecto.tienda.backend.service.UsuarioServicio.UsuarioServicio;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@PreAuthorize("hasAnyRole('USER') || hasAnyRole('ADMIN')")
public class UsuariosControllers {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioServicio usuarioServicio;

    // CONTROLADOR PARA BORRAR UN USUARIO SIENDO USUARIO
    @DeleteMapping("/borrarUsuario")
    public String borrarUsuario(@RequestHeader("Authorization") String token) {
        return usuarioServicio.eliminarUsuarioSiendoUsuario(token, jwtUtils);
    }

    // CONTROLADOR PARA ACTUALIZAR EL USUARIO SIENDO USUARIO
    @PatchMapping("/actualizarUsuario")
    public String actualizarUsuario(
            @RequestBody @Valid UsuarioActualizacionDTO actualizarUsuarioDTO,
            @RequestHeader("Authorization") String token) {

        return usuarioServicio.actualizarUsuario(actualizarUsuarioDTO, token, jwtUtils);
    }

    // CONTROLADOR PARA OBTENER LAS DIRECCIONES DE ENVIO Y FACTURACION DEL USUARIO
    @GetMapping("/direccionesUsuarios")
    public ResponseEntity<?> obtenerDirecciones(@RequestHeader("Authorization") String token) {
        return usuarioServicio.obtenerDireccionesEnvioFacturacion(token, jwtUtils);
    }

    // CONTROLADOR PARA AÑADIR LAS DIRECCIONES DE ENVIO AL USUARIO
    @PostMapping("/anadirDireccionEnvio")
    public ResponseEntity<?> anadirDireccionEnvio(
            @RequestBody @Valid AnadirDireccionEnvioDTO anadirDireccionEnvioDTO,
            @RequestHeader("Authorization") String token) {
        return usuarioServicio.anadirDireccionEnvio(anadirDireccionEnvioDTO, token, jwtUtils);
    }

    //CONTROLADOR PARA ACTUALIZAR LA DIRECCION DE ENVIO
    @PatchMapping("/actualizarDireccionEnvio")
    public ResponseEntity<?> actualizarDireccionEnvio(@RequestBody @Valid AnadirDireccionEnvioDTO anadirDireccionEnvioDTO,
                                                       @RequestHeader("Authorization") String token) {
        return usuarioServicio.actualizarDireccionEnvio(anadirDireccionEnvioDTO, token, jwtUtils);
    }

    // CONTROLADOR PARA AÑADIR LA DIRECCION DE FACTURACION EMPRESA-AUTONOMO AL
    // USUARIO
    @PostMapping("/anadirDireccionFacturacionEmpresaAutonomo")
    public ResponseEntity<?> anadirDireccionFacturacionEmpresaAutonomo(
            @RequestBody @Valid EmpresaAutonomoDireccionFacturacionDTO empresaAutonomoDireccionFacturacionDTO,
            @RequestHeader("Authorization") String token) {
        return usuarioServicio.anadirDireccionFacturacionEmpresaAutonomo(empresaAutonomoDireccionFacturacionDTO, token,
                jwtUtils);
    }

    // CONTROLADOR PARA AÑADIR LA DIRECCION DE FACTURACION PARTICULAR AL
    // USUARIO
    @PostMapping("/anadirDireccionFacturacionParticular")
    public ResponseEntity<?> anadirDireccionFacturacionParticular(
            @RequestBody @Valid ParticularDireccionFacturacionDTO particularDireccionFacturacionDTO,
            @RequestHeader("Authorization") String token) {
        return usuarioServicio.anadirDireccionFacturacionParticular(particularDireccionFacturacionDTO, token,
                jwtUtils);

    }

    // CONTROLADOR PARA ELIMINAR LA DIRECCION DE ENVIO AL USUARIO
    @DeleteMapping("/eliminarDireccionesEnvio")
    public ResponseEntity<?> eliminarDireccionEnvio(@RequestParam Integer index,
            @RequestHeader("Authorization") String token) {
        return usuarioServicio.eliminarDireccionEnvio(index, token, jwtUtils);
    }

    // CONTROLADOR PARA ELIMINAR LA DIRECCION DE FACTURACION PARTICULAR AL USUARIO
    @DeleteMapping("/eliminarDireccionesFacturacion")
    public ResponseEntity<?> eliminarDireccionFacturacion(@RequestParam Integer index,
            @RequestHeader("Authorization") String token) {
        return usuarioServicio.eliminarDireccionFacturacion(index, token, jwtUtils);
    }

}
