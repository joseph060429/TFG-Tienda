package com.proyecto.tienda.backend.UtilEnum;

public enum EPedido {
    PENDIENTE_PAGO, // Cuando se crea un pedido bien
    PENDIENTE_ENVIO, // Cuando el pedido ha sido cancelado bien
    CANCELADO, // Por si el usuario elimina su pedido antes de enviarlo bien
    ENVIADO,  // Cuando yo como admin envio el pedido bien
    PENDIENTE_CONFIRMACION_DIRECCION, // Cuando el repartidor llega a casa y la direccion no existe bien
    DIRECCION_ACTUALIZADA, 
    REPROGRAMADO_PARA_ENTREGA, // Cuando el repartidor ya fue a la direccion y el repartidor no entrego el pedido porque no hay nadie en casa o no responde al tlfono
    ENTREGADO, // Cuando el repartidor me venga con todos los pedidos que han sido entregados, o no se pudieron entregar
    DEVUELTO // Cuando el usuario me devuelve el pedido
}
