package com.proyecto.tienda.backend.UtilEnum;

public enum EPedido {

    PENDIENTE, // Por defeceto cuando se envia el pedido
    CANCELADO, // Por si el usuario elimina su pedido antes de enviarlo
    ENVIADO,  // Cuando yo como admin envio el pedido
    PENDIENTE_CONFIRMACION_DIRECCION, // Cuando el repartidor llega a casa y la direccion no existe
    REPROGRAMADO_PARA_ENTREGA, // Cuando el repartidor ya fue a la direccion y el repartidor no entrego el pedido porque no hay nadie en casa o no responde al tlfono
    ENTREGADO, // Cuando el repartidor ya entrego el pedido lo pondre por defecto a 15 dias despues de que se envie el producto
    DEVUELTO // Cuando el usuario me devuelve el pedido

}
