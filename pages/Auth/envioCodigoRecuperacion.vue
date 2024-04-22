<template>
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <div class="q-pa-md d-flex justify-center align-center" style="height: 100vh;">
        <div class="d-flex justify-center align-center" style="max-width: 40%; margin: auto;">
            <h1 class="text-h4 q-mb-md text-center"
                style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
                RECUPERAR CONTRASEÑA
            </h1>
            <q-form @submit.prevent="envioCodigoRecuperacion" @reset="borrar" class="q-gutter-md">

                <!-- Campo email -->
                <q-input filled v-model="email" label="Email *" hint="Tu correo electrónico" lazy-rules :rules="[
                    val => val && val.length > 0 || 'Por favor, introduce algo',
                    val => /^\S.*\S$/.test(val) || 'El email no puede empezar ni terminar con espacios en blanco',
                    val => /^\S+@\S+\.\S+$/.test(val) || 'El formato del correo electrónico no es válido',
                ]">
                    <template v-slot:prepend>
                        <q-icon name="mdi-email" />
                    </template>
                </q-input>

                <!-- Botones de enviar y reiniciar -->
                <div class="q-mt-lg d-flex justify-around">

                    <!-- Boton de enviar -->
                    <q-btn label="Recuperar contraseña" type="submit" class="full-width enviar-button">
                        <q-icon name="mdi-login" class="q-ml-md"></q-icon> <!-- Icono para el botón -->
                    </q-btn>

                    <!-- Boton de reiniciar -->
                    <q-btn label="Reiniciar" type="reset" flat class="full-width q-ml-sm custom-button-reiniciar">
                        <q-icon name="mdi-refresh" class="q-ml-md"></q-icon> <!-- Icono para el botón -->
                    </q-btn>

                </div>
            </q-form>
        </div>
    </div>
</template>

<script setup>

// IMPORTACIONES
import { useRouter } from 'vue-router'


// RUTAS
const router = useRouter()

//USAR QUASAR
const quasar = useQuasar()

// AXIOS
const axios = useNuxtApp().$axios


// DECLARACION DE VARIABLES
const email = ref('');

// FUNCIONES

// CUANDO SE ENVÍA EL FORMULARIO, BORRO EL VALOR DEL EMAIL Y MUESTRO UN MENSAJE AVISANDO AL USUARIO 
// QUE SE HA ENVIADO UN CORREO ELECTRONICO.
const envioCodigoRecuperacion = async () => {
    // Login el usuario
    try {
        const response = await axios.post('/envioCodigoRecuperacion', {
            email: email.value
        });
        console.log("Response:", response.data);
        // Muestro un mensaje de exito
        mostrarAlertaExito('Hemos enviado un código de verificación si estás registrado en nuestra aplicación');
        // Redirijo al formulario de verificación del código
        router.push({ path: '/auth/verificarCodigo' })
        borrar();
    } catch (error) {
        console.error('Error al enviar el email de recuperación', error);
        mostrarAlertaError('Erroral enviar el email de recuperación intentelo más tarde');
    }

};

// ALERTAS DE ERROR
const mostrarAlertaError = (msg) => {
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
const mostrarAlertaExito = (msg) => {
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

// CUANDO SE BORRA EL FORMULARIO, ELIMINO LOS VALORES DEL EMAIL Y LA CONTRASEÑA.
const borrar = () => {
    email.value = '';
};

// CUANDO SE HACE CLIC EN EL BOTÓN DE REGRESAR, REDIRIJO AL USUARIO A LA PÁGINA DE INICIO.
const regresar = () => {
    router.push({ path: '/auth/login' })
};

</script>


<!-- ESTILOS -->
<style scoped>
.full-width {
    /* Ajusto el ancho */
    width: calc(50% - 10px);
}

.enviar-h1 {
    color: #333333;
    font-weight: bold;
    text-transform: uppercase;
    letter-spacing: 2px;
}

.enviar-button {
    /* Cambio el color del texto a blanco */
    background-color: #BFC9CA;
    /* Transición suave al color de fondo */
    transition: background-color 0.3s ease;
}

.enviar-button:hover {
    /* Cambio el color de fondo al pasar el mouse sobre el botón */
    background-color: #95A5A6;
}

.custom-button-reiniciar {
    /* Transición suave al color de fondo */
    transition: background-color 0.3s ease;
}

.custom-button-reiniciar:hover {
    /* Cambio el color de fondo al pasar el mouse sobre el botón */
    background-color: #95A5A6;
}

.custom-regresar-button {
    /* Ajusto el margen izquierdo para desplazar el botón hacia la derecha */
    margin-left: 1%;
}
</style>