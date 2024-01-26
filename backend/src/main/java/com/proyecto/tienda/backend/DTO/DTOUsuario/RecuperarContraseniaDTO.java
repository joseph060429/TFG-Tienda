package com.proyecto.tienda.backend.DTO.DTOUsuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecuperarContraseniaDTO {

    @Size(min = 8, message = "La contraseña no puede tener menos de 8 caracteres")
    @NotBlank(message = "La contraseña no puede estar en blanco")
    private String password;

    @NotBlank(message = "El codigo no puede estar en blanco")
    private String recuperarContrasenia = "";

    private String expiracionRecuperarContrasenia = "";

    public void setFechaExpiracion() {
        // Obtengo la fecha actual
        LocalDateTime fechaActual = LocalDateTime.now();

        // Añado 3 minutos a la fecha actual para obtener la fecha de expiración
        LocalDateTime fechaExpiracion = fechaActual.plusMinutes(3);

        // Defino el formato para la fecha
        DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Formateo la fecha de expiración y la guardo en la propiedad
        // expiracionRecuperarContrasenia
        this.expiracionRecuperarContrasenia = fechaExpiracion.format(formatearFecha);
    }
}
