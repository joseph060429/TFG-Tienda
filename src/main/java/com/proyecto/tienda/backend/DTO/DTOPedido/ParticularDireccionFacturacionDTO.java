package com.proyecto.tienda.backend.DTO.DTOPedido;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticularDireccionFacturacionDTO {

    @NotBlank(message = "El nombre no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)([a-zA-ZáéíóúÁÉÍÓÚ]+\\s?)*[a-zA-ZáéíóúÁÉÍÓÚ]+(?!\\s)$", message = "El nombre solo puede contener letras y no puede empezar ni terminar con espacios en blanco. No se permiten números ni caracteres especiales, excepto la tilde.")
    @Pattern(regexp = "^.{2,70}$", message = "El nombre debe tener entre 2 y 70 caracteres.")
    private String nombreFacturacion;

    @NotBlank(message = "El apellido no puede estar en blanco")
    @Pattern(regexp = "^(?!\\s)([a-zA-ZáéíóúÁÉÍÓÚ]+\\s?)*[a-zA-ZáéíóúÁÉÍÓÚ]+(?!\\s)$", message = "El nombre solo puede contener letras y no puede empezar ni terminar con espacios en blanco. No se permiten números ni caracteres especiales, excepto la tilde.")
    @Pattern(regexp = "^.{2,70}$", message = "El apellido debe tener entre 2 y 70 caracteres.")
    // @Pattern(regexp = "^.{2,70}$", message = "El nombre debe tener entre 2 y 70 caracteres.")
    private String apellidoFacturacion;

    @NotNull(message = "El número de teléfono, no puede estar en blanco")
    @Min(value = 100000000, message = "El número de teléfono debe tener al menos 9 dígitos")
    @Max(value = 999999999L, message = "El número de teléfono debe tener como máximo 9 dígitos")
    private Long numTelefonoFacturacion;

   
    @NotNull(message = "La direccion de facturación no puede estar en blanco")
    @NotBlank(message = "La direccion de facturación no puede estar en blanco")
    @Pattern(regexp = "^\\S(.*\\S)?$", message = "La direccion de facturación no puede contener espacios en blanco al principio ni al final")
    @Pattern(regexp = "^.{2,100}$", message = "La direccion de facturacióndebe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^\\D*$", message = "La direccion de facturación no puede contener números")
    private String direccionDeFacturacion;

    @NotNull(message = "El número de facturación no puede estar en blanco")
    @NotBlank(message = "El número de facturación no puede estar en blanco")
    @Size(min = 1, max = 10, message = "El número de facturación debe tener entre 1 y 10 caracteres")
    @Pattern(regexp = "^\\S(.*\\S)?$", message = "El número de facturación no puede contener espacios en blanco al principio ni al final")
    private String numeroDeFacturacion;

    
    @Size(max = 10, message = "El piso de facturacion puede tener un máximo de 10 dígitos")
    private String pisoDeFacturacion;

    private String puertaDeFacturacion;

    @NotNull(message = "El codigo postal de facturación no puede estar en blanco")
    @NotBlank(message = "El codigo postal de facturación no puede estar en blanco")
    @Pattern(regexp = "\\S{5,10}", message = "El código postal de facturación debe tener entre 5 y 10 caracteres, sin espacios en blanco")
    @Pattern(regexp = "\\d+", message = "El código postal de facturación debe contener solo dígitos")
    private String codigoPostalDeFacturacion;

    @NotNull(message = "La provincia de facturación no puede estar en blanco")
    @NotBlank(message = "La provincia de facturación no puede estar en blanco")
    @Pattern(regexp = "^\\S(.*\\S)?$", message = "La provincia de facturación no puede contener espacios en blanco al principio ni al final")
    @Pattern(regexp = "^.{2,100}$", message = "La provincia de facturación debe tener entre 2 y 100 caracteres")
    private String provinciaDeFacturacion;

    // private String direccionCompletaFacturacion;

}
