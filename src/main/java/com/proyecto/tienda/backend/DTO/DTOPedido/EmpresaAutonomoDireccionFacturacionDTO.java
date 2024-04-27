package com.proyecto.tienda.backend.DTO.DTOPedido;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaAutonomoDireccionFacturacionDTO {

    @NotNull(message = "El campo 'C.I.F/N.I.F' es obligatorio")
    @NotBlank(message = "El campo 'C.I.F/N.I.F' no puede estar en blanco")
    @Pattern(regexp = "^\\S{9}$", message = "El campo 'C.I.F/N.I.F' debe tener exactamente 9 caracteres y no puede contener espacios en blanco")
    private String cifONifFacturacion;

    @NotNull(message = "El número de teléfono, no puede estar en blanco")
    @Min(value = 100000000, message = "El número de teléfono debe tener al menos 9 dígitos")
    @Max(value = 999999999L, message = "El número de teléfono debe tener como máximo 9 dígitos")
    private Long numTelefonoFacturacion;

    @NotNull(message = "La direccion no puede estar en blanco")
    @NotBlank(message = "La direccion no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,100}(?!\\s)$", message = "La dirección debe tener entre 2 y 100 caracteres y no puede empezar,terminar o contener mas de un espacio en blanco")
    private String direccionDeFacturacion;

    @NotNull(message = "El codigo postal no puede estar en blanco")
    @NotBlank(message = "El codigo postal no puede estar en blanco")
    @Pattern(regexp = "\\S{5,10}", message = "El código postal debe tener entre 5 y 10 caracteres, sin espacios en blanco")
    private String codigoPostalDeFacturacion;

    @NotNull(message = "La provincia no puede estar en blanco")
    @NotBlank(message = "La provincia no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,100}(?!\\s)$", message = "La provincia debe tener entre 2 y 100 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String provinciaDeFacturacion;

    @NotNull(message = "El numero no puede estar en blanco")
    @NotBlank(message = "El numero no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)(?=\\S).{1,10}(?!\\s)$", message = "El numero debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String numeroDeFacturacion;

    @Pattern(regexp = "^(?!\\s)(?=\\S).{1,10}(?!\\s)$", message = "El piso debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String pisoDeFacturacion;

    @Pattern(regexp = "^(?!\\s)(?=\\S).{1,10}(?!\\s)$", message = "La puerta debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String puertaDeFacturacion;


}
