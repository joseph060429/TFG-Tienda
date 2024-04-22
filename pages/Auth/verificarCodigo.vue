<template>
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <div class="q-pa-md d-flex justify-center align-center" style="height: 100vh;">
        <div class="verification-container">
            <h1 class="text-h4 q-mb-md text-center"
                style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
                VERIFICAR CÓDIGO
            </h1>

            <!-- Genero los 10 cuadraditos -->
            <div class="verification-inputs">
                <div v-for="index in 10" :key="index" class="verification-input">
                    <input v-model="codeInputs[index - 1]" type="text" maxlength="1" class="verification-character"
                        @input="convertToUppercase(index - 1, $event)" />
                </div>
            </div>
            <div class="q-mt-lg d-flex justify-around">
                <!-- Botón para verificar el código -->
                <q-btn label="Verificar código" @click="verificarCodigo" class="full-width enviar-button" />
            </div>
        </div>
    </div>
</template>

<script setup>
// IMPORTACIONES
import { useRouter } from 'vue-router'
import { mostrarAlertaExito, mostrarAlertaError } from '~/utils/alertas';
import { reactive } from "vue";
import { useAuth } from '~/composables/useAuth.js';

const { verificaCodigo } = useAuth();


// RUTAS
const router = useRouter()

//USAR QUASAR
const quasar = useQuasar()

// AXIOS
const axios = useNuxtApp().$axios

// Variables
const codeInputs = ref(Array(10).fill(''));

// FUNCION PARA CONVERTIR A MAYUSCULAS
const convertToUppercase = (index, event) => {
    const newValue = event.target.value.toUpperCase();
    // Actualizar el valor en codeInputs
    codeInputs.value[index] = newValue;
};

//  FUNCION PARA VERIFICAR EL CODIGO
const verificarCodigo = async () => {
    try {
        const code = codeInputs.value.join('');

        // Almaceno el código de recuperación en el localStorage para que el usuario no tenga que escribirlo nuevamente
        localStorage.setItem('codigoRecuperacion', code);

        const response = await verificaCodigo(code);
        console.log("Response:", response.data);

        // Verificar si la respuesta es válida
        if (response.data === 'Código válido. Puede proceder con la recuperación de contraseña.') {
            // Mostrar una alerta si el código es válido
            mostrarAlertaExito('Código válido, puede proceder con la recuperación de contraseña', quasar);
            // Redireccionar a la página de cambiar contraseña
            router.push({ path: '/auth/cambiarContrasenia' });
        } else {
            // Mostrar una alerta si la respuesta no es válida
            throw new Error('La respuesta del servidor no es válida');
        }
    } catch (error) {
        console.error('Error:', error);
        // Mostrar una alerta si ocurre un error
        mostrarAlertaError('Código incorrecto o la fecha de expiración ha pasado. Verifique el código o solicite uno nuevo', quasar);
    }

};

const regresar = () => {
    router.push({ path: '/auth/envioCodigoRecuperacion' })
};
</script>

<style scoped>
/* Estilos para la verificación del código */
.verification-container {
    max-width: 60%;
    margin: auto;
    text-align: center;
}

.verification-inputs {
    display: flex;
    justify-content: center;
}

.verification-input {
    margin-right: 10px;
}

.verification-character {
    width: 40px;
    height: 40px;
    border: 1px solid #ccc;
    text-align: center;
}
</style>