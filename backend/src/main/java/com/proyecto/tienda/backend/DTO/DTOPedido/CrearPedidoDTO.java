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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @DBRef
    @Field("productos") // Campo como seria almacenado en la base de datos
    private List<ProductoModelo> productos;

    @NotNull(message = "El tipo de pago no puede estar en blanco")
    @NotBlank(message = "El tipo de pago no puede estar en blanco")
    private String tipoPago;

    private String estado;

    @NotNull(message = "El codigo postal no puede estar en blanco")
    @NotBlank(message = "El codigo postal no puede estar en blanco")
    private String codigoPostal;

    @NotNull(message = "La direccion no puede estar en blanco")
    @NotBlank(message = "La direccion no puede estar en blanco")
    private String direccion;

    @NotNull(message = "La provincia no puede estar en blanco")
    @NotBlank(message = "La provincia no puede estar en blanco")
    private String provincia;

    @NotNull(message = "El numero no puede estar en blanco")
    @NotBlank(message = "El numero no puede estar en blanco")
    private String numero;

    private String piso;
    private String puerta;

    private String direccionEnvio;

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
