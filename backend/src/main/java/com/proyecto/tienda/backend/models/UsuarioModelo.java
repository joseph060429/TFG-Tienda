package com.proyecto.tienda.backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data // getters y setters
@AllArgsConstructor // constructor con todos los atributos
@NoArgsConstructor // constructor sin atributos
@Builder
@Document(value = "Usuarios")
public class UsuarioModelo {

        @Id
        private String _id;

        @NotBlank(message = "El nombre no puede estar en blanco")
        @Size(max = 70, message = "El nombre no puede tener más de 70 caracteres")
        private String nombre;

        @NotBlank(message = "El apellido no puede estar en blanco")
        @Size(max = 70, message = "El apellido no puede tener más de 70 caracteres")
        private String apellido;

        @Email(message = "El formato del correo electrónico no es válido")
        @NotBlank(message = "El email no puede estar en blanco")
        private String email;

        @NotBlank(message = "La contraseña no puede estar en blanco")
        private String password;

        @DBRef
        private Set<Roles> roles;

        private String fechaCreacion;
        private String fechaModificacion;
        private String direccionEnvio;

        @Builder.Default // Lo puse porque ya tenia los otros campos creados
        private String recuperarContrasenia = "";

        
        // El campo @Transient se utiliza para indicar a Spring Data que ignore este
        // campo durante la persistencia en la base de datos, ya que la expiración se
        // gestionará en la lógica de la aplicación y no necesariamente necesita
        // almacenarse permanentemente.
        @Builder.Default
        private String expiracionRecuperarContrasenia = "";

}
