package com.proyecto.tienda.backend.DTO.DTOUsuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecuperarContraseniaDTO {

    @Pattern(regexp = "^(?!\\s)(?=\\S)(.{8,})(?!\\s)$", message = "La contraseña debe tener al menos 8 caracteres y no puede empezar ni terminar con espacios en blanco")
    @NotBlank(message = "La contraseña no puede estar en blanco")
    private String password;

    @NotBlank(message = "El codigo no puede estar en blanco")
    private String recuperarContrasenia = "";

    private String expiracionRecuperarContrasenia = "";

    private String repitaPassword = "";

    // METODO PARA CREAR LA FECHA EXACTA EN LA QUE SE TERMINA LA VALIDEZ DEL CODIGO QUE SE LE ENVIA AL USUARIO PARA RECUPERAR CONTRASEÑA
    public void setFechaExpiracion() {
        // Obtengo la fecha actual
        LocalDateTime fechaActual = LocalDateTime.now();

        // Añado 10 minutos a la fecha actual para obtener la fecha de expiración
        LocalDateTime fechaExpiracion = fechaActual.plusMinutes(10);

        // Defino el formato para la fecha
        DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Formateo la fecha de expiración y la guardo en la propiedad
        // expiracionRecuperarContrasenia
        this.expiracionRecuperarContrasenia = fechaExpiracion.format(formatearFecha);
    }
}
