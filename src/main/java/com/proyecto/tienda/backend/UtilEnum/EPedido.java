package com.proyecto.tienda.backend.UtilEnum;

public enum EPedido {
    PENDIENTE_PAGO, // Cuando se crea un pedido, ya esta hecho en el front, cuando el usuario crea el pedido
    PENDIENTE_ENVÍO,// Cuando el pedido ha sido cancelado, ya esta hecho en el front, cuando el usuario paga el pedido
    CANCELADO, // Por si el usuario elimina su pedido antes de enviarlo, ya esta hecho en el front
    ENVIADO,  // Cuando yo como admin envio el pedido, ya esta hecho en el front
    PENDIENTE_CONFIRMACION_DIRECCION, // Cuando el repartidor llega a casa y la direccion no existe, ya esta hecho en el front
    DIRECCION_ACTUALIZADA, 
    REPROGRAMADO_PARA_ENTREGA, // Cuando el repartidor ya fue a la direccion y el repartidor no entrego el pedido porque no hay nadie en casa o no responde al tlfono
    ENTREGADO, // Cuando el repartidor me venga con todos los pedidos que han sido entregados, o no se pudieron entregar, ya esta hecho en el front
    RETRASADO,
    DEVUELTO // Cuando el usuario me devuelve el pedido
}
