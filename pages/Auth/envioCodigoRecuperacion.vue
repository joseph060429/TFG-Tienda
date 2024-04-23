<template>
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <div class="q-pa-md d-flex justify-center align-center" style="height: 100vh;">
        <div class="d-flex justify-center align-center" style="max-width: 70%; margin: auto;">
            <h1 class="text-h4 q-mb-md text-center q-mt-lg"
                style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
                Recupera tu contraseña
            </h1>
            <q-form @submit.prevent="envioCodigoRecuperacion" @reset="borrar" class="q-gutter-md">

                <!-- Campo email -->
                <q-input filled v-model="emailUsuario.email" label="Email *" hint="Tu correo electrónico" lazy-rules
                    :rules="[
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

const { enviarCodigoRecuperacion } = useAuth();


// RUTAS
const router = useRouter()

//USAR QUASAR
const quasar = useQuasar()

// DECLARACION DE VARIABLES
const emailUsuario = reactive({
    email: '',
})

// FUNCIONES
// CUANDO SE ENVÍA EL FORMULARIO, BORRO EL VALOR DEL EMAIL Y MUESTRO UN MENSAJE AVISANDO AL USUARIO 
// QUE SE HA ENVIADO UN CORREO ELECTRONICO.
const envioCodigoRecuperacion = async () => {
    // Login el usuario
    try {
        const response = await enviarCodigoRecuperacion(emailUsuario);
        console.log("Response:", response.data);
        // Muestro un mensaje de exito
        mostrarAlertaExito('Hemos enviado un código de verificación si estás registrado en nuestra aplicación', quasar);
        // Redirijo al formulario de verificación del código
        router.push({ path: '/auth/verificarCodigo' })
        borrar();
    } catch (error) {
        console.error('Error al enviar el email de recuperación', error);
        mostrarAlertaError('Error al enviar el email de recuperación intentelo más tarde', quasar);
    }

};
// CUANDO SE BORRA EL FORMULARIO, ELIMINO LOS VALORES DEL EMAIL Y LA CONTRASEÑA.
const borrar = () => {
    emailUsuario.email = '';
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
</style>