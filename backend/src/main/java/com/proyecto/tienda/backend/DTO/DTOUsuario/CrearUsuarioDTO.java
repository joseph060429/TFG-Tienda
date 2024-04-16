// package com.proyecto.tienda.backend.controllers.request;
package com.proyecto.tienda.backend.DTO.DTOUsuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
// import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrearUsuarioDTO {

    // @Size(min = 2, max = 70, message = "El nombre no puede tener menos de 2 y más de 70 caracteres USUARIODTO")
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,70}(?!\\s)$", message = "El nombre debe tener entre 2 y 70 caracteres y no puede empezar ni terminar con espacios en blanco")
    @NotBlank(message = "El nombre no puede estar en blanco")
    private String nombre;

    
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,70}(?!\\s)$", message = "El apellido debe tener entre 2 y 70 caracteres y no puede empezar ni terminar con espacios en blanco")
    @NotBlank(message = "El apellido no puede estar en blanco")
    private String apellido;

    @Email(message = "El formato del correo electrónico no es válido")
    @Indexed(unique = true)
    @NotBlank(message = "El email no puede estar en blanco")
    private String email;

    
    @NotBlank(message = "La contraseña no puede estar en blanco")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    private Set<String> roles;

    private String fechaCreacion;

    private String fechaModificacion;
    
    // private String direccionEnvio;
    private List<String> direccionesEnvio;

    private List<String> direcionesFacturacion;

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
