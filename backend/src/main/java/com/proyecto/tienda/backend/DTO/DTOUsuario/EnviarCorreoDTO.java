package com.proyecto.tienda.backend.DTO.DTOUsuario;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Genera los Getters y Setters
@AllArgsConstructor // Genera el constructor
@NoArgsConstructor // Genera el constructor vacío
public class EnviarCorreoDTO {

    @Email(message = "El formato del correo electrónico no es válido")
    @NotBlank(message = "El email no puede estar en blanco")
    @NotNull(message = "El email no puede estar en blanco")
    private String email;

    





}
