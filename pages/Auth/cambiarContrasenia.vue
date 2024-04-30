<template>
    <div class="container">
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <!-- <div class="q-pa-md d-flex justify-center align-center" style="height: 100vh;"> -->
        <div class="d-flex justify-center align-center" style="max-width: 60%; margin: auto;">
            <h1 class="text-h4 q-mb-md text-center q-mt-lg"
                style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
                Recupera tu cuenta
            </h1>
            <q-form @submit.prevent="cambiarContrasenia" @reset="borrar" class="q-gutter-md">

                <!-- Campo password -->
                <q-input filled v-model="datosCambioPassword.password" :type="mostrarContrasenia ? 'text' : 'password'"
                    label="Contraseña *" lazy-rules :rules="[
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
                <q-input filled v-model="datosCambioPassword.repitaPassword"
                    :type="mostrarContrasenia ? 'text' : 'password'" label="Repita la contraseña *" lazy-rules :rules="[
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
                        <q-icon name="mdi-lock-reset" class="q-ml-md"></q-icon> <!-- Icono para el botón -->
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
import { mostrarAlertaExito, mostrarAlertaError } from '~/utils/alertas';
import { reactive } from "vue";
import { useAuth } from '~/composables/useAuth.js';

// Acceso de la pagina
definePageMeta({
    role: ['PUBLIC']
})

const { cambioContrasenia } = useAuth();
// RUTAS
const router = useRouter()

//USAR QUASAR
const quasar = useQuasar()

// DECLARACION DE VARIABLES
const datosCambioPassword = reactive({
    password: '',
    repitaPassword: '',
})
const mostrarContrasenia = ref(false);


// FUNCIONES

// CUANDO SE ENVÍA EL FORMULARIO, BORRO EL VALOR DE AMBAS CONTRASEÑAS Y MUESTRO UN MENSAJE DE RECUPERACION DE CUENTA EXITOSA.
const cambiarContrasenia = async () => {

    // Traigo el código de recuperación y lo almaceno en un localStorage para que el usuario no vuelva a poner el codigo otra vez
    // solo durará 10 minutos, preguntar si no pasa nada si se almacena el el localStorage durante el tiempo que dura el código
    const code = localStorage.getItem('codigoRecuperacion');

    console.log('Valor almacenado en localStorage:', localStorage.getItem('codigoRecuperacion'));
    // Verifico que las contraseñas sean iguales
    if (datosCambioPassword.password !== datosCambioPassword.repitaPassword) {
        mostrarAlertaError('Las contraseñas no coinciden. Por favor, inténtalo nuevamente.', quasar);
        return; // Salgo de la funcion si las contraseñas no coinciden
    }

    // Recuperar contraseña
    try {
        const response = await cambioContrasenia(datosCambioPassword, code);
        console.log("Response:", response.data)
        if (response.status === 200) {
            console.log("eliminacion del localStorage", localStorage.removeItem('codigoRecuperacion'));
            mostrarAlertaExito('Contraseña cambiada exitosamente, inicie sesión', quasar)
            router.push({ path: '/auth/login' });
            localStorage.removeItem('codigoRecuperacion');
        } else {
            mostrarAlertaError('Ya has utilizado el código de recuperación, o ha expirado, vuelve a pedir uno nuevo', quasar)
        }

    } catch (error) {
        // En el catch capturo las excepciones de que ha pasado mucho tiempo entre uno y otro
        console.error('La fecha de expiración del código ha pasado, vuelva a solicitar uno nuevo', error);
        mostrarAlertaError('La fecha de expiración del código ha pasado, vuelva a solicitar uno nuevo', quasar)
    }
};

// CUANDO SE BORRA EL FORMULARIO, ELIMINO LOS VALORES DEL EMAIL Y LA CONTRASEÑA.
const borrar = () => {
    datosCambioPassword.password = '';
    datosCambioPassword.repitaPassword = '';
};

// CUANDO SE HACE CLIC EN EL BOTÓN DE REGRESAR, REDIRIJO AL USUARIO A LA PÁGINA DE INICIO.
const regresar = () => {
    router.push({ path: '/auth/verificarCodigo' })
};

// CUANDO SE HACE CLIC EN EL BOTÓN PARA MOSTRAR U OCULTAR LA CONTRASEÑA(OJITO), CAMBIO EL ESTADO PARA MOSTRARLA U OCULTARLA.
const cambiarMostrarPassword = () => {
    mostrarContrasenia.value = !mostrarContrasenia.value;
};

</script>

<style scoped>
.full-width {
    /* Ajusto el ancho */
    width: calc(50% - 10px);
}

.container {
  /* Ancho máximo del contenedor */
  max-width: 100%;
  /* Relleno */
  padding: 2vh;
  height: 80vh;
  /* background-color: black; */
}
</style>