package com.proyecto.tienda.backend.DTO.DTOUsuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data //Genera los Getters y Setters
@AllArgsConstructor //Genera el constructor
@NoArgsConstructor //Genera el constructor vacío
public class UsuarioActualizacionDTO {
    
    @Size(min = 2, max = 70, message = "El nombre no puede tener menos de 2 y más de 70 caracteres ACTUALIZACIONDTO")
    private String nombre;

    @Size(min = 2, max = 70, message = "El apellido no puede tener menos de 2 y más de 70 caracteres")
    private String apellido;

    @Email(message = "El formato del correo electrónico no es válido")
    @Indexed(unique = true)
    private String email;

    @Size(min = 8, message = "La contraseña no puede tener menos de 8 caracteres")
    private String password;

    private String fechaModificacion;
    private String direccionEnvio;

    public void setFechaModificacion() {
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.fechaModificacion = fechaActual.format(formatearFecha);

    }

}
