//USAR QUASAR




export const mostrarAlertaError = (msg, quasar) => {
    // Utilizo Quasar para mostrar una notificación con el mensaje especificado
    quasar.notify({
        message: msg,
        color: 'red-8',
        textColor: 'white',
        icon: 'mdi-alert',
        position: 'top',
        actions: [{ icon: 'mdi-close', color: 'white' }]
    });
};

// ALERTA EXITO
export const mostrarAlertaExito = (msg, quasar) => {
    // Utilizo Quasar para mostrar una notificación con el mensaje especificado
    quasar.notify({
        message: msg,
        color: 'green-7',
        textColor: 'white',
        icon: 'mdi-check-circle',
        position: 'top',
        actions: [{ icon: 'mdi-close', color: 'white' }]
    });
};