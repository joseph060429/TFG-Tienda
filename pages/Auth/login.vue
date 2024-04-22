<template>
  <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
  <div class="q-pa-md d-flex justify-center align-center" style="height: 100vh;">
    <div class="d-flex justify-center align-center" style="max-width: 40%; margin: auto;">
      <h1 class="text-h4 q-mb-md text-center"
        style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
        Login
      </h1>
      <q-form @submit.prevent="login" @reset="borrar" class="q-gutter-md">

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

        <!-- Botones de enviar y reiniciar -->
        <div class="q-mt-lg d-flex justify-around">

          <!-- Boton de enviar -->
          <q-btn label="Iniciar sesión" type="submit" class="full-width enviar-button">
            <q-icon name="mdi-login" class="q-ml-md"></q-icon> <!-- Icono para el botón -->
          </q-btn>

          <!-- Boton de reiniciar -->
          <q-btn label="Reiniciar" type="reset" flat class="full-width q-ml-sm custom-button-reiniciar">
            <q-icon name="mdi-refresh" class="q-ml-md"></q-icon> <!-- Icono para el botón -->
          </q-btn>

          <!-- Enlace para olvidar contraseña -->
          <q-btn label="¿Has olvidado la contraseña?" flat class="full-width q-ml-sm custom-olvidado-button"
            @click="olvidarContrasenia" />

        </div>
      </q-form>
    </div>
  </div>
</template>

<script setup>

// IMPORTACIONES
import { useRouter } from 'vue-router'
import { mostrarAlertaExito, mostrarAlertaError } from '~/utils/alertas';

// RUTAS
const router = useRouter()

//USAR QUASAR
const quasar = useQuasar()

// AXIOS
const axios = useNuxtApp().$axios


// DECLARACION DE VARIABLES
const email = ref('');
const password = ref('');
const mostrarContrasenia = ref(false);


// FUNCIONES
// CUANDO SE ENVÍA EL FORMULARIO, BORRO EL VALOR DEL EMAIL Y LA CONTRASEÑA Y MUESTRO UN MENSAJE DE SESION INICIADA CORRECTAMENTE.
const login = async () => {
  // Login el usuario
  try {
    const response = await axios.post('/login', {
      email: email.value,
      password: password.value
    });
    console.log("Response:", response.data);
    // Verifico el estado de la respuesta y muestro el mensaje correspondiente
    if (response.data['Message'] === "Autenticacion Correcta") {
      console.log('Sesion iniciada correctamente:', response.data['Message']);
      mostrarAlertaExito('Sesion iniciada correctamente', quasar);
      borrar();
      router.push({ path: '/productos/listarProductos' })

    } else {
      // Si el mensaje no es "Autenticacion Correcta",muestro el error
      console.error('Error de inicio de sesión:', response.data);
      mostrarAlertaError('Error al iniciar sesión', quasar);
    }
  } catch (error) {
    // En el catch capturo las excepciones de credenciales invalidas
    console.error('Credenciales invalidas', error);
    mostrarAlertaError('Credenciales invalidas', quasar);
  }

};

// // ALERTAS DE ERROR
// const mostrarAlertaError = (msg) => {
//   // Utilizo Quasar para mostrar una notificación con el mensaje especificado
//   quasar.notify({
//     message: msg,
//     color: 'red-8',
//     textColor: 'white',
//     icon: 'mdi-alert',
//     position: 'top',
//     actions: [{ icon: 'mdi-close', color: 'white' }]
//   });
// };

// // ALERTA EXITO
// const mostrarAlertaExito = (msg) => {
//   // Utilizo Quasar para mostrar una notificación con el mensaje especificado
//   quasar.notify({
//     message: msg,
//     color: 'green-7',
//     textColor: 'white',
//     icon: 'mdi-check-circle',
//     position: 'top',
//     actions: [{ icon: 'mdi-close', color: 'white' }]
//   });
// };

// CUANDO SE BORRA EL FORMULARIO, ELIMINO LOS VALORES DEL EMAIL Y LA CONTRASEÑA.
const borrar = () => {
  email.value = '';
  password.value = '';
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