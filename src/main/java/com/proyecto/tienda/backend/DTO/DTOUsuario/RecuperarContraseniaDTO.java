package com.proyecto.tienda.backend.DTO.DTOUsuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecuperarContraseniaDTO {

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @NotBlank(message = "La contraseña no puede estar en blanco")
    private String password;

    @NotBlank(message = "El codigo no puede estar en blanco")
    private String recuperarContrasenia = "";

    private String expiracionRecuperarContrasenia = "";

    private String repitaPassword = "";

    // METODO PARA CREAR LA FECHA EXACTA EN LA QUE SE TERMINA LA VALIDEZ DEL CODIGO
    // QUE SE LE ENVIA AL USUARIO PARA RECUPERAR CONTRASEÑA
    // public void setFechaExpiracion() {
    // // Obtengo la fecha actual
    // LocalDateTime fechaActual = LocalDateTime.now();

    // // Añado 10 minutos a la fecha actual para obtener la fecha de expiración
    // LocalDateTime fechaExpiracion = fechaActual.plusMinutes(10);

    // // Defino el formato para la fecha
    // DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy
    // HH:mm:ss");

    // // Formateo la fecha de expiración y la guardo en la propiedad
    // // expiracionRecuperarContrasenia
    // this.expiracionRecuperarContrasenia = fechaExpiracion.format(formatearFecha);
    // }

    public void setFechaExpiracion() {
        // Especifico la zona horaria
        ZoneId zoneId = ZoneId.of("Europe/Madrid");

        // Obtengo la fecha y hora actual en la zona especificada
        ZonedDateTime fechaActual = ZonedDateTime.now(zoneId);

        // Añado 10 minutos a la fecha y hora actual para obtener la fecha de expiración
        ZonedDateTime fechaExpiracion = fechaActual.plusMinutes(10);

        // Defino el formato para la fecha
        DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Formateo la fecha de expiración y la guardo en la propiedad
        // expiracionRecuperarContrasenia
        this.expiracionRecuperarContrasenia = fechaExpiracion.format(formatearFecha);
    }
}
