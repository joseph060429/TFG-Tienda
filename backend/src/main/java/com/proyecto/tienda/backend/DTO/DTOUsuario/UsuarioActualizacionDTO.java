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
    
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,70}(?!\\s)$", message = "El nombre debe tener entre 2 y 70 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String nombre;

    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,70}(?!\\s)$", message = "El apellido debe tener entre 2 y 70 caracteres y no puede empezar ni terminar con espacios en blanco")
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
