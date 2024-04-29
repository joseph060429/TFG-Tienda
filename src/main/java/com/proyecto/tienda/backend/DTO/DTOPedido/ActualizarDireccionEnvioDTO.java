package com.proyecto.tienda.backend.DTO.DTOPedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarDireccionEnvioDTO {
    

    @NotBlank(message = "El id del pedido no puede estar en blanco")
    @NotNull(message = "El id del pedido no puede estar en blanco")
    private String pedidoId;

    @NotNull(message = "El codigo postal no puede estar en blanco")
    @NotBlank(message = "El codigo postal no puede estar en blanco")
    @Pattern(regexp = "\\S{5,10}", message = "El código postal debe tener entre 5 y 10 caracteres, sin espacios en blanco")
    private String codigoPostal;

    @NotNull(message = "La direccion no puede estar en blanco")
    @NotBlank(message = "La direccion no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,100}(?!\\s)$", message = "La dirección debe tener entre 2 y 100 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String direccion;

    @NotNull(message = "La provincia no puede estar en blanco")
    @NotBlank(message = "La provincia no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,100}(?!\\s)$", message = "La provincia debe tener entre 2 y 100 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String provincia;

    @NotNull(message = "El numero no puede estar en blanco")
    @NotBlank(message = "El numero no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)(?=\\S).{1,10}(?!\\s)$", message = "El numero debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String numero;

    @Pattern(regexp = "^(?!\\s)(?=\\S).{1,10}(?!\\s)$", message = "El piso debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String piso;

    @Pattern(regexp = "^(?!\\s)(?=\\S).{1,10}(?!\\s)$", message = "La puerta debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String puerta;

    private String direccionEnvio;

    // Prueba subida backend
}
