package com.proyecto.tienda.backend.DTO.DTOPedido;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarPedidoDTO {

    // Actualizar estado del pedido
     @NotNull(message = "El campo 'estado' es obligatorio")
    private String estado;

    // Actualizar fecha de entrega estimada
    private String fechaEntregaEstimada = "";

    // Actualizar fecha de envio
    private String fechaEnvio = "";

    // METODO PARA CREAR LA FECHA EXACTA EN LA QUE SE ENVIA EL PEDIDO
    public void setFechaEnvioPedido() {
        // Obtengo la fecha actual
        LocalDateTime fechaActual = LocalDateTime.now();

        // Defino el formato para la fecha
        DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Formateo la fecha y la guardo en la propiedad fechaCreacion
        this.fechaEnvio = fechaActual.format(formatearFecha);
    }

    // METODO PARA CREAR LA FECHA EXACTA EN LA QUE SE ESTIMA LA ENTREGA DEL PEDIDO
    public void setFechaEntregaEstimada() {
        // Obtengo la fecha actual
        LocalDateTime fechaActual = LocalDateTime.now();

        // Añado 4 dias a la fecha actual para obtener la fecha de entregaEstimada
        LocalDateTime fechaEntrega = fechaActual.plusDays(4);

        // Defino el formato para la fecha
        DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Formateo la fecha de entrega y la guardo en la propiedad
        // fechaEntregaEstimada
        this.fechaEntregaEstimada = fechaEntrega.format(formatearFecha);
    }

}
