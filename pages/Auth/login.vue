<template>
  <div class="container">
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <!-- <div class="q-pa-md d-flex justify-center align-center" style="height: 100vh;"> -->
    <div class="d-flex justify-center align-center" style="max-width: 60%; margin: auto;">
      <h1 class="text-h4 q-mb-md text-center q-mt-lg"
        style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
        Login
      </h1>
      <q-form @submit.prevent="loginUser" @reset="borrar" class="q-gutter-md">

        <!-- Campo email -->
        <q-input filled v-model="datosLogin.email" label="Email *" lazy-rules :rules="[
          val => val && val.length > 0 || 'Por favor, introduce algo',
          val => /^\S.*\S$/.test(val) || 'El email no puede empezar ni terminar con espacios en blanco',
          val => /^\S+@\S+\.\S+$/.test(val) || 'El formato del correo electrónico no es válido',
        ]">
          <template v-slot:prepend>
            <q-icon name="mdi-email" />
          </template>
        </q-input>

        <!-- Campo password -->
        <q-input filled v-model="datosLogin.password" :type="mostrarContrasenia ? 'text' : 'password'"
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
import { reactive } from "vue";
import { useAuth } from '~/composables/useAuth.js';

const { login } = useAuth();

// Acceso de la pagina
definePageMeta({
  role: ['PUBLIC']
})

// RUTAS
const router = useRouter()

//USAR QUASAR
const quasar = useQuasar()

const datosLogin = reactive({
  email: '',
  password: ''
})

// DECLARACION DE VARIABLES
const mostrarContrasenia = ref(false);

// FUNCIONES
// CUANDO SE ENVÍA EL FORMULARIO, BORRO EL VALOR DEL EMAIL Y LA CONTRASEÑA Y MUESTRO UN MENSAJE DE SESION INICIADA CORRECTAMENTE.
const loginUser = async () => {
  // Login el usuario
  try {
    const response = await login(datosLogin);
    console.log("Response:", response.data)
    // Verifico el estado de la respuesta y muestro el mensaje correspondiente
    if (response.status === 200) {
      // Traigo el rol del usuario que se ha registrado y redirecciono a un sitio o a otro
      const roles = localStorage.getItem('roles')

      // Verifico si el usuario tiene el rol de administrador
      // for (const roles of response.data.user.authorities) {
      if (roles === 'ROLE_ADMIN') {
        // console.log('¿Es admin?', isAdmin);
        // isAdmin = true;
        console.log('roles', roles);
        router.push({ path: '/admin/vistaInicioAdmin' })
        // break;
      } else {
        router.push({ path: '/usuario/vistaInicioUsuario' });
      }
    
      console.log('Sesion iniciada correctamente:', response.status);
      // Muestro la alerta de 
      mostrarAlertaExito('Sesion iniciada correctamente', quasar);

      // Borro el formulario
      borrar();
    } else {

      throw new Error(response.data)
    }
  } catch (error) {
    // En el catch capturo las excepciones de credenciales invalidas
    mostrarAlertaError('Credenciales invalidas', quasar);
  }

};

// CUANDO SE BORRA EL FORMULARIO, ELIMINO LOS VALORES DEL EMAIL Y LA CONTRASEÑA.
const borrar = () => {
  datosLogin.email = '';
  datosLogin.password = '';
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

.container {
  /* Ancho máximo del contenedor */
  max-width: 100%;
  /* Relleno */
  padding: 2vh;
  height: 80vh;
  /* background-color: black; */
}
</style>