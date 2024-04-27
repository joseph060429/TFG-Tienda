package com.proyecto.tienda.backend.DTO.DTOUsuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data //Genera los Getters y Setters
@AllArgsConstructor //Genera el constructor
@NoArgsConstructor //Genera el constructor vacío
public class UsuarioActualizacionDTO {
    
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-ZáéíóúÁÉÍÓÚ]+(\\s[a-zA-ZáéíóúÁÉÍÓÚ]+)*){2,70}(?!\\s)$", message = "El nombre/s solo puede contener letras y no puede empezar ni terminar con espacios en blanco. No se permiten números ni caracteres especiales, excepto la tilde.")
    private String nombre;

    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-ZáéíóúÁÉÍÓÚ]+(\\s[a-zA-ZáéíóúÁÉÍÓÚ]+)*){2,70}(?!\\s)$", message = "Los apellidos solo pueden contener letras y no puede empezar ni terminar con espacios en blanco. No se permiten números ni caracteres especiales, excepto la tilde.")
    private String apellido;

    @Email(message = "El formato del correo electrónico no es válido")
    @Indexed(unique = true)
    private String email;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    private String fechaModificacion;
    // private String direccionEnvio;

    public void setFechaModificacion() {
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.fechaModificacion = fechaActual.format(formatearFecha);

    }

}
