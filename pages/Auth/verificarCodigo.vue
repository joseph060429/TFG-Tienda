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
        
        // Almaceno el codigo de recuperacion en el local Storage para que el usuario no escriba otra vez el codigo
        localStorage.setItem('codigoRecuperacion', code);

        const response = await axios.post('/verificarCodigo', { codigoRecuperacion: code });
        console.log("Response:", response.data);
        // Muestro una alerta si el código es válido
        mostrarAlertaExito('Código válido, puede proceder con la recuperación de contraseña'); 
        // Redirecciono a la página de cambiar contrasenia
        router.push({ path: '/auth/cambiarContrasenia' }); // Redirige a otra página si es necesario
    } catch (error) {
        console.error('Codigo', error);
        mostrarAlertaError('Código incorrecto o la fecha de expiración ha pasado. Verifique el código o solicite uno nuevo');
    }
};

// ALERTAS DE ERROR
// const mostrarAlertaError = (msg) => {
//     // Utilizo Quasar para mostrar una notificación con el mensaje especificado
//     quasar.notify({
//         message: msg,
//         color: 'red-8',
//         textColor: 'white',
//         icon: 'mdi-alert',
//         position: 'top',
//         actions: [{ icon: 'mdi-close', color: 'white' }]
//     });
// };

// // ALERTA EXITO
// const mostrarAlertaExito = (msg) => {
//     // Utilizo Quasar para mostrar una notificación con el mensaje especificado
//     quasar.notify({
//         message: msg,
//         color: 'green-7',
//         textColor: 'white',
//         icon: 'mdi-check-circle',
//         position: 'top',
//         actions: [{ icon: 'mdi-close', color: 'white' }]
//     });
// };

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

/* Estilos del boton enviar */
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
</style>