package com.proyecto.tienda.backend.DTO.DTOPedido;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;
import com.proyecto.tienda.backend.models.ProductoModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrearPedidoDTO {

    @Id
    private String _id;

    @DBRef
    @Field("id_usuario")
    private UsuarioModelo usuario;

    private String fechaPedido;

    private String fechaEntregaEstimada = "";

    private String fechaEnvio = "";

    @Unique
    private Long numPedido;

    @NotBlank(message = "El tipo de facturacion no puede estar en blanco")
    @NotNull(message = "El tipo de facturacion no puede estar en blanco")
    private String tipoFacturacion;

    @DBRef
    @Field("productos") // Campo como seria almacenado en la base de datos
    private List<ProductoModelo> productos;

    @NotNull(message = "El tipo de pago no puede estar en blanco")
    @NotBlank(message = "El tipo de pago no puede estar en blanco")
    private String tipoPago;

    private String estado;

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

    // @Pattern(regexp = "^(?!\\s)(?=\\S).{1,10}(?!\\s)$", message = "El piso debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String piso;

    // @Pattern(regexp = "^(?!\\s)(?=\\S).{1,10}(?!\\s)$", message = "La puerta debe tener entre 1 y 10 caracteres y no puede empezar ni terminar con espacios en blanco")
    private String puerta;
    
    private String direccionEnvio;

    @Valid
    private ParticularDireccionFacturacionDTO particularDireccionFacturacionDTO;

    @Valid
    private EmpresaAutonomoDireccionFacturacionDTO empresaAutonomoDireccionFacturacionDTO;

    @NotNull(message = "El número de teléfono, no puede estar en blanco")
    @Min(value = 100000000, message = "El número de teléfono debe tener al menos 9 dígitos")
    @Max(value = 999999999L, message = "El número de teléfono debe tener como máximo 9 dígitos")
    private Long numTelefono;

    // METODO PARA CREAR LA FECHA EXACTA EN LA QUE SE REGISTRA EL PEDIDO
    public void setFechaPedido() {
        // Obtengo la fecha actual
        LocalDateTime fechaActual = LocalDateTime.now();

        // Defino el formato para la fecha
        DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Formateo la fecha y la guardo en la propiedad fechaCreacion
        this.fechaPedido = fechaActual.format(formatearFecha);

    }

}
