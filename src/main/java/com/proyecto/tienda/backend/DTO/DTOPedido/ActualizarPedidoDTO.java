package com.proyecto.tienda.backend.DTO.DTOPedido;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarPedidoDTO {

    @NotBlank(message = "El id del pedido no puede estar en blanco")
    @NotNull(message = "El id del pedido no puede estar en blanco")
    private String pedidoId;

    // Actualizar estado del pedido
    @NotNull(message = "El campo 'estado' es obligatorio")
    private String estado;

    // Actualizar fecha de entrega estimada
    private String fechaEntregaEstimada = "";

    // Actualizar fecha de envio
    private String fechaEnvio = "";

    // Ponerle un numero de envio aleatoriamente
    private String trackingNumber = "";

    // METODO PARA CREAR LA FECHA EXACTA EN LA QUE SE ENVIA EL PEDIDO
    public void setFechaEnvioPedido() {

        ZoneId zoneId = ZoneId.of("Europe/Madrid");

        // Obtengo la fecha actual
        LocalDateTime fechaActual = LocalDateTime.now();

        // Defino el formato para la fecha
        DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Formateo la fecha y la guardo en la propiedad fechaCreacion
        this.fechaEnvio = fechaActual.format(formatearFecha);

    }

    // METODO PARA CREAR LA FECHA EXACTA EN LA QUE SE ESTIMA LA ENTREGA DEL PEDIDO
    public void setFechaEntregaEstimada() {
        // Especifico la zona horaria
        ZoneId zoneId = ZoneId.of("Europe/Madrid");

        // Obtengo la fecha y hora actual en la zona especificada
        ZonedDateTime fechaActual = ZonedDateTime.now(zoneId);

        // Añado 4 días a la fecha y hora actual para obtener la fecha de entrega
        // estimada
        ZonedDateTime fechaEntrega = fechaActual.plusDays(4);

        // Defino el formato para la fecha
        DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Formateo la fecha de entrega y la guardo en la propiedad fechaEntregaEstimada
        this.fechaEntregaEstimada = fechaEntrega.format(formatearFecha);
    }

}
