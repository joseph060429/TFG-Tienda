// package com.proyecto.tienda.backend.controllers.request;
package com.proyecto.tienda.backend.DTO.DTOUsuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrearUsuarioDTO {

    @Size(min = 2, max = 70, message = "El nombre no puede tener menos de 2 y más de 70 caracteres USUARIODTO")
    @NotBlank(message = "El nombre no puede estar en blanco")
    private String nombre;

    @Size(min = 2, max = 70, message = "El apellido no puede tener menos de 2 y más de 70 caracteres")
    @NotBlank(message = "El apellido no puede estar en blanco")
    private String apellido;

    @Email(message = "El formato del correo electrónico no es válido")
    @Indexed(unique = true)
    @NotBlank(message = "El email no puede estar en blanco")
    private String email;

    @Size(min = 8, message = "La contraseña no puede tener menos de 8 caracteres")
    @NotBlank(message = "La contraseña no puede estar en blanco")
    private String password;

    private Set<String> roles;

    private String fechaCreacion;

    private String fechaModificacion;
    private String direccionEnvio;

    // METODO PARA CREAR LA FECHA EXACTA EN LA QUE SE REGISTRA EL USUARIO
    public void setFechaCreacion() {
        // Obtengo la fecha actual
        LocalDateTime fechaActual = LocalDateTime.now();

        // Defino el formato para la fecha
        DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Formateo la fecha y la guardo en la propiedad fechaCreacion
        this.fechaCreacion = fechaActual.format(formatearFecha);
    }

    

    
}
