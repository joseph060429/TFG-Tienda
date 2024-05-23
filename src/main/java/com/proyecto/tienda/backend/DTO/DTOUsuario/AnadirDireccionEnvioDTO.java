package com.proyecto.tienda.backend.DTO.DTOUsuario;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnadirDireccionEnvioDTO {

    @NotNull(message = "La direccion no puede estar en blanco")
    @NotBlank(message = "La direccion no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,100}(?!\\s)$", message = "La dirección debe tener entre 2 y 100 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String direccion;

    @NotNull(message = "El número no puede estar en blanco")
    @NotBlank(message = "El número no puede estar en blanco")
    @Size(min = 1, max = 10, message = "El número  debe tener entre 1 y 10 caracteres")
    @Pattern(regexp = "^\\S.*\\S$", message = "El número no puede contener espacios en blanco al principio ni al final")
    private String numero;

    @Min(value = 0, message = "El piso debe ser al menos 0")
    @Max(value = 9999999999L, message = "El número debe ser menor o igual a 9999999999")
    private Integer piso;

    @Pattern(regexp = "^(?!\\s)(?=\\S).{1,10}(?!\\s)$", message = "La puerta debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String puerta;

    @NotNull(message = "El codigo postal no puede estar en blanco")
    @NotBlank(message = "El codigo postal no puede estar en blanco")
    @Pattern(regexp = "\\S{5,10}", message = "El código postal debe tener entre 5 y 10 caracteres, sin espacios en blanco")
    private String codigoPostal;

    @NotNull(message = "La provincia no puede estar en blanco")
    @NotBlank(message = "La provincia no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,100}(?!\\s)$", message = "La provincia debe tener entre 2 y 20 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String provincia;
}
