package com.proyecto.tienda.backend.DTO.DTOPedido;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaAutonomoDireccionFacturacionDTO {

    @NotNull(message = "El campo 'C.I.F/N.I.F' es obligatorio")
    @NotBlank(message = "El campo 'C.I.F/N.I.F' no puede estar en blanco")
    @Pattern(regexp = "^\\S{9}$", message = "El campo 'C.I.F/N.I.F' debe tener exactamente 9 caracteres y no puede contener espacios en blanco")
    private String cifONifFacturacion;

    @NotNull(message = "El número de teléfono de facturación, no puede estar en blanco")
    @Min(value = 100000000, message = "El número de teléfono de facturación debe tener al menos 9 dígitos")
    @Max(value = 999999999L, message = "El número de teléfono de facturación debe tener como máximo 9 dígitos")
    private Long numTelefonoFacturacion;

    @NotNull(message = "La direccion de facturación no puede estar en blanco")
    @NotBlank(message = "La direccion de facturación no puede estar en blanco")
    @Pattern(regexp = "^\\S(.*\\S)?$", message = "La dirección de facturación no puede contener espacios en blanco al principio ni al final")
    @Pattern(regexp = "^.{2,100}$", message = "La dirección de facturación debe tener entre 2 y 100 caracteres")
    private String direccionDeFacturacion;

    @NotNull(message = "El número de facturación no puede estar en blanco")
    @NotBlank(message = "El número de facturación no puede estar en blanco")
    @Size(min = 1, max = 10, message = "El número de facturación debe tener entre 1 y 10 caracteres")
    @Pattern(regexp = "^\\S(.*\\S)?$", message = "El número de facturación no puede contener espacios en blanco al principio ni al final")
    private String numeroDeFacturacion;


    @Min(value = 0, message = "El piso de facturación debe ser min 0, maximo 9999999999 y no contener letras")
    @Max(value = 9999999999L, message = "El piso de facturación debe ser min 0, maximo 9999999999 y no contener letras")
    // @Pattern(regexp = "^$|\\d+", message = "El piso de facturación debe ser nulo o contener solo dígitos positivos")
    private String pisoDeFacturacion;

    // @Pattern(regexp = "^(?!\\s)(?=\\S).{1,10}(?!\\s)$", message = "La puerta de facturación debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String puertaDeFacturacion;

    @NotNull(message = "El codigo postal de facturación no puede estar en blanco")
    @NotBlank(message = "El codigo postal de facturación no puede estar en blanco")
    @Pattern(regexp = "\\S{5,10}", message = "El código postal de facturación debe tener entre 5 y 10 caracteres, sin espacios en blanco")
    @Pattern(regexp = "\\d+", message = "El código postal de facturación debe contener solo dígitos")
    private String codigoPostalDeFacturacion;

    @NotNull(message = "La provincia de facturación no puede estar en blanco")
    @NotBlank(message = "La provincia de facturación no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,100}(?!\\s)$", message = "La provincia de facturación debe tener entre 2 y 100 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String provinciaDeFacturacion;

}
