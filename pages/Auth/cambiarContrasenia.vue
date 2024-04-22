<template>
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <div class="q-pa-md d-flex justify-center align-center" style="height: 100vh;">
        <div class="d-flex justify-center align-center" style="max-width: 40%; margin: auto;">
            <h1 class="text-h4 q-mb-md text-center"
                style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
                Recuperar cuenta
            </h1>
            <q-form @submit.prevent="cambiarContrasenia" @reset="borrar" class="q-gutter-md">

                <!-- Campo password -->
                <q-input filled v-model="password" :type="mostrarContrasenia ? 'text' : 'password'" label="Contraseña *"
                    lazy-rules :rules="[
                        val => val && val.length > 0 || 'Por favor, introduce algo',
                        val => val.length >= 8 || 'La contraseña debe tener al menos 8 caracteres'
                    ]">
                    <template v-slot:prepend>
                        <q-icon name="mdi-lock" />
                    </template>
                    <!-- Icono de ojito -->
                    <template v-slot:append>
                        <q-icon :name="mostrarContrasenia ? 'mdi-eye' : 'mdi-eye-off'" @click="cambiarMostrarPassword"
                            class="cursor-pointer" />
                    </template>
                </q-input>

                <!-- Campo repitaPassword -->
                <q-input filled v-model="repitaPassword" :type="mostrarContrasenia ? 'text' : 'password'"
                    label="Repita la contraseña *" lazy-rules :rules="[
                        val => val && val.length > 0 || 'Por favor, introduce algo',
                        val => val.length >= 8 || 'La contraseña debe tener al menos 8 caracteres'
                    ]">
                    <template v-slot:prepend>
                        <q-icon name="mdi-lock" />
                    </template>
                    <!-- Icono de ojito -->
                    <template v-slot:append>
                        <q-icon :name="mostrarContrasenia ? 'mdi-eye' : 'mdi-eye-off'" @click="cambiarMostrarPassword"
                            class="cursor-pointer" />
                    </template>
                </q-input>

                <!-- Botones de enviar y reiniciar -->
                <div class="q-mt-lg d-flex justify-around">

                    <!-- Boton de enviar -->
                    <q-btn label="Cambiar contraseña" type="submit" class="full-width enviar-button">
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
const password = ref('');
const repitaPassword = ref('');
const mostrarContrasenia = ref(false);


// FUNCIONES

// CUANDO SE ENVÍA EL FORMULARIO, BORRO EL VALOR DE AMBAS CONTRASEÑAS Y MUESTRO UN MENSAJE DE RECUPERACION DE CUENTA EXITOSA.
const cambiarContrasenia = async () => {

    // Traigo el código de recuperación y lo almaceno en un localStorage para que el usuario no vuelva a poner el codigo otra vez
    // solo durará 10 minutos, preguntar si no pasa nada si se almacena el el localStorage durante el tiempo que dura el código
    const code = localStorage.getItem('codigoRecuperacion');

    console.log(localStorage.getItem('codigoRecuperacion'));

    // Verifico que las contraseñas sean iguales
    if (password.value !== repitaPassword.value) {
        mostrarAlertaError('Las contraseñas no coinciden. Por favor, inténtalo nuevamente.');
        return; // Salgo de la funcion si las contraseñas no coinciden
    }

    // Recuperar contraseña
    try {
        const response = await axios.post('/cambiarContrasenia', {
            password: password.value,
            repitaPassword: repitaPassword.value,
            recuperarContrasenia: code // AgregO el código de recuperación al cuerpo de la solicitud
        });
        console.log("Response:", response.data);
        mostrarAlertaExito('Contraseña cambiada exitosamente, inicie sesión')
        router.push({ path: '/auth/login' });

    } catch (error) {
        // En el catch capturo las excepciones de que ha pasado mucho tiempo entre uno y otro
        console.error('La fecha de expiración del código ha pasado, vuelva a solicitar uno nuevo', error);
        mostrarAlertaError('La fecha de expiración del código ha pasado, vuelva a solicitar uno nuevo');
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
    password.value = '';
    repitaPassword.value = '';
};

// CUANDO SE HACE CLIC EN EL BOTÓN DE REGRESAR, REDIRIJO AL USUARIO A LA PÁGINA DE INICIO.
const regresar = () => {
    router.push({ path: '/' })
};

// CUANDO SE HACE CLIC EN EL BOTÓN PARA MOSTRAR U OCULTAR LA CONTRASEÑA(OJITO), CAMBIO EL ESTADO PARA MOSTRARLA U OCULTARLA.
const cambiarMostrarPassword = () => {
    mostrarContrasenia.value = !mostrarContrasenia.value;
};

const olvidarContrasenia = () => {
    router.push({ path: '/auth/envioCodigoRecuperacion' })
};

</script>

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

/* Boton de olvidar la contraseña */
.custom-olvidado-button {
    /* Color del boton */
    background-color: white;
    /* Subraya el texto para indicar que es un enlace */
    text-decoration: underline;
    /* Cambia el cursor al pasar sobre el enlace */
    cursor: pointer;
    /* Tamaño de fuente */
    font-size: 14px;
    /* Negrita */
    font-weight: bold;
}

.custom-olvidado-button:hover {
    /* Cambio el color del texto al pasar el mouse */
    color: #0056b3;

}
</style>