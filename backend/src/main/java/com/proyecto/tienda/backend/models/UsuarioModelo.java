package com.proyecto.tienda.backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
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
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,70}(?!\\s)$", message = "El nombre debe tener entre 2 y 70 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,70}(?!\\s)$", message = "El apellido debe tener entre 2 y 70 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String apellido;

    @Email(message = "El formato del correo electrónico no es válido")
    @NotBlank(message = "El email no puede estar en blanco")
    private String email;

    @NotBlank(message = "La contraseña no puede estar en blanco")
    private String password;

    @DBRef
    private Set<RolesModelo> roles;

    private String fechaCreacion;

    private String fechaModificacion;

    private List<String> direccionesEnvio;

    private List<String> direcionesFacturacion;

    @Builder.Default // Lo puse porque ya tenia los otros campos creados
    private String recuperarContrasenia = "";

    @Builder.Default // Lo puse porque ya tenia los otros campos creados
    private String expiracionRecuperarContrasenia = "";

    // El campo @Transient se utiliza para indicar a Spring Data que ignore este
    // campo durante la persistencia en la base de datos, ya que la expiración se
    // gestionará en la lógica de la aplicación y no necesariamente necesita
    // almacenarse permanentemente.

    // METODO PARA AÑADIR LAS DIRECCIONES DE ENVIO AL USUARIO, SI SE REPITEN NO SE
    // AGREGAN AL ARRAY
    public String agregarDireccionCompleta(String direccion, String provincia, String puerta,
            String codigoPostal, String piso, String numero) {

        if (codigoPostal == null || codigoPostal.isEmpty()
                || direccion == null || direccion.isEmpty()
                || provincia == null || provincia.isEmpty()
                || numero == null || numero.isEmpty()) {
            throw new IllegalArgumentException(
                    "El código postal, la dirección, la provincia y el número son obligatorios.");
        }

        StringBuilder direccionCompleta = new StringBuilder();
        direccionCompleta.append("Código Postal: ").append(codigoPostal.trim()).append(", ");
        direccionCompleta.append("Dirección: ").append(direccion.trim()).append(", ");
        direccionCompleta.append("Provincia: ").append(provincia.trim()).append(", ");
        direccionCompleta.append("Número: ").append(numero.trim()).append(", ");

        if (puerta != null && !puerta.isEmpty()) {
            direccionCompleta.append("Puerta: ").append(puerta.trim()).append(", ");
        }
        if (piso != null && !piso.isEmpty()) {
            direccionCompleta.append("Piso: ").append(piso.trim()).append(", ");
        }

        // Eliminar la coma al final
        if (direccionCompleta.length() > 0) {
            direccionCompleta.delete(direccionCompleta.length() - 2, direccionCompleta.length());
        }

        // Si existe no lo añado al array
        if (direccionesEnvio != null && direccionesEnvio.contains(direccionCompleta.toString())) {
            return direccionCompleta.toString();
        }

        // Agregar la nueva dirección a la lista
        if (direccionesEnvio == null) {
            direccionesEnvio = new ArrayList<>();
        }
        direccionesEnvio.add(direccionCompleta.toString());
        return direccionCompleta.toString();
    }

    // METODO PARA AGREGAR LAS DIRECCIONES DE FACTURACION:
    public String agregarDireccionFacturacion(String direccion, String provincia, String puerta,
            String codigoPostal, String piso, String numero) {

        if (codigoPostal == null || codigoPostal.isEmpty()
                || direccion == null || direccion.isEmpty()
                || provincia == null || provincia.isEmpty()
                || numero == null || numero.isEmpty()) {
            throw new IllegalArgumentException(
                    "El código postal, la dirección, la provincia y el número son obligatorios.");
        }

        StringBuilder direccionCompleta = new StringBuilder();
        direccionCompleta.append("Código Postal: ").append(codigoPostal.trim()).append(", ");
        direccionCompleta.append("Dirección: ").append(direccion.trim()).append(", ");
        direccionCompleta.append("Provincia: ").append(provincia.trim()).append(", ");
        direccionCompleta.append("Número: ").append(numero.trim()).append(", ");

        if (puerta != null && !puerta.isEmpty()) {
            direccionCompleta.append("Puerta: ").append(puerta.trim()).append(", ");
        }
        if (piso != null && !piso.isEmpty()) {
            direccionCompleta.append("Piso: ").append(piso.trim()).append(", ");
        }

        // Eliminar la coma al final
        if (direccionCompleta.length() > 0) {
            direccionCompleta.delete(direccionCompleta.length() - 2, direccionCompleta.length());
        }

        // Si existe no lo añado al array
        if (direcionesFacturacion != null && direcionesFacturacion.contains(direccionCompleta.toString())) {
            return direccionCompleta.toString();
        }

        // Agregar la nueva dirección a la lista
        if (direcionesFacturacion == null) {
            direcionesFacturacion = new ArrayList<>();
        }
        direcionesFacturacion.add(direccionCompleta.toString());
        return direccionCompleta.toString();
    }

}
