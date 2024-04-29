<template>
    <div class="container">
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <!-- <div class="q-pa-md d-flex justify-center align-center" style="height: 100vh;"> -->
        <div class="verification-container">
            <h1 class="text-h4 q-mb-md text-center q-mt-lg"
                style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
                Verifica tu código
            </h1>

            <!-- Genero los 10 cuadraditos -->
            <div class="verification-inputs" style="display: flex; flex-wrap: wrap; justify-content: center;">
                <div v-for="index in 10" :key="index" class="verification-input" style="margin-right: 10px;">
                    <input v-model="codeInputs[index - 1]" type="text" maxlength="1" class="verification-character"
                        @input="convertToUppercase(index - 1, $event)" />
                </div>
            </div>
            <div class="q-mt-lg d-flex justify-center">
                <!-- Botón para verificar el código -->
                <q-btn label="Verificar código" @click="verificarCodigo" class="enviar-button" />
            </div>
        </div>
    </div>
</template>

<script setup>
// IMPORTACIONES
import { useRouter } from 'vue-router'
import { mostrarAlertaExito, mostrarAlertaError } from '~/utils/alertas';
import { useAuth } from '~/composables/useAuth.js';

const { verificaCodigo } = useAuth();

// Acceso de la pagina
definePageMeta({
    role: ['PUBLIC']
})

// RUTAS
const router = useRouter()

// VARIABLES
const codeInputs = ref(Array(10).fill(''));

//USAR QUASAR
const quasar = useQuasar()

// FUNCIONES
const convertToUppercase = (index, event) => {
    const newValue = event.target.value.toUpperCase();
    codeInputs.value[index] = newValue;
};

const verificarCodigo = async () => {
    try {
        const code = codeInputs.value.join('');
        localStorage.setItem('codigoRecuperacion', code);

        const response = await verificaCodigo(code);
        console.log("Response:", response.data);

        if (response.data === 'Código válido. Puede proceder con la recuperación de contraseña.') {
            mostrarAlertaExito('Código válido, puede proceder con la recuperación de contraseña', quasar);
            localStorage.removeItem('codigoRecuperacion');
            router.push({ path: '/auth/cambiarContrasenia' });
        } else {
            throw new Error('La respuesta del servidor no es válida');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Código incorrecto o la fecha de expiración ha pasado. Verifique el código o solicite uno nuevo', quasar);
        localStorage.removeItem('codigoRecuperacion');
    }
};

const regresar = () => {
    router.push({ path: '/auth/envioCodigoRecuperacion' })
};
</script>

<style scoped>
/* Estilos para la verificación del código */
.verification-container {
    max-width: 80%;
    margin: auto;
    text-align: center;
}

.verification-inputs {
    display: flex;
    justify-content: center;
    margin-bottom: 20px;
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

.enviar-button {
    max-width: 200px;
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