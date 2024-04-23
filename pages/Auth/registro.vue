<template>
  <!-- Boton de volver atras -->
  <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />

  <div class="q-pa-md d-flex justify-center align-center" style="height: 100vh;">

    <div class="d-flex justify-center align-center" style="max-width: 60%; margin: auto;">
      <h1 class="text-h4 q-mb-md text-center q-mt-lg"
        style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
        Regístrate
      </h1>



      <q-form @submit.prevent="registroUsuario" @reset="borrar" class="q-gutter-md">

        <!-- Campo nombre -->
        <q-input filled v-model="datosRegistro.nombre" label="Nombre *" hint="Nombre" lazy-rules :rules="[
          val => val && val.length > 0 || 'Por favor, introduce algo',
          val => /^.{2,70}$/.test(val) || 'El nombre/s debe tener entre 2 y 70 caracteres',
          val => /^\S.*\S$/.test(val) || 'El nombre/s no puede empezar ni terminar con espacios en blanco',
          val => !/\d/.test(val) || 'El nombre/s no puede contener números',
          val => /^[a-zA-ZáéíóúÁÉÍÓÚ\s]+$/.test(val) || 'El nombre/s solo puede contener letras y espacios, incluyendo la tilde'
        ]">
          <template v-slot:prepend>
            <q-icon name="mdi-account" />
          </template>
        </q-input>

        <!-- Campo apellido -->
        <q-input filled v-model="datosRegistro.apellido" label="Apellidos *" hint="Apellidos" lazy-rules :rules="[
          val => val && val.length > 0 || 'Por favor, introduce algo',
          val => /^.{2,70}$/.test(val) || 'Los apellidos debe tener entre 2 y 70 caracteres',
          val => /^\S.*\S$/.test(val) || 'Los apellidos no pueden empezar ni terminar con espacios en blanco',
          val => !/\d/.test(val) || 'Los apellidos no puede contener números',
          val => /^[a-zA-ZáéíóúÁÉÍÓÚ\s]+$/.test(val) || 'Los apellidos solo puede contener letras y espacios, incluyendo la tilde'
        ]">
          <template v-slot:prepend>
            <q-icon name="mdi-account" />
          </template>
        </q-input>

        <!-- Campo email -->
        <q-input filled v-model="datosRegistro.email" label="Email *" hint="Tu correo electrónico" lazy-rules :rules="[
          val => val && val.length > 0 || 'Por favor, introduce algo',
          val => /^\S.*\S$/.test(val) || 'El email no puede empezar ni terminar con espacios en blanco',
          val => /^\S+@\S+\.\S+$/.test(val) || 'El formato del correo electrónico no es válido',
        ]">
          <template v-slot:prepend>
            <q-icon name="mdi-email" />
          </template>
        </q-input>

        <!-- Campo password -->
        <q-input filled v-model="datosRegistro.password" :type="mostrarContrasenia ? 'text' : 'password'"
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

        <q-toggle v-model="accept" label="Acepto los términos y condiciones" class="q-mt-lg" />

        <!-- Botones de enviar y reiniciar -->
        <div class="q-mt-lg d-flex justify-around">
          <!-- Boton de enviar -->
          <q-btn label="Crear cuenta" type="submit" class="full-width enviar-button col-xs-12 col-sm-6">
            <q-icon name="mdi-account-plus" class="q-ml-md inline"></q-icon> <!-- Icono para el botón -->
          </q-btn>

          <!-- Boton de reiniciar -->
          <q-btn label="Reiniciar" type="reset" flat
            class="full-width q-ml-sm custom-button-reiniciar col-xs-12 col-sm-6">
            <q-icon name="mdi-refresh" class="q-ml-md inline"></q-icon> <!-- Icono para el botón -->
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

const { registro } = useAuth();

// Acceso de la pagina
definePageMeta({
  role: ['PUBLIC']
})


// RUTAS
const router = useRouter()

//USAR QUASAR
const quasar = useQuasar()

// DECLARACION DE VARIABLES
const datosRegistro = reactive({
  nombre: '',
  apellido: '',
  email: '',
  password: ''
})
const mostrarContrasenia = ref(false);
const accept = ref(false);


// FUNCIONES

// FUNCION PARA ENVIAR EL FORMULARIO
const registroUsuario = async () => {
  if (accept.value === false) {
    mostrarAlertaError('Debes aceptar los términos y condiciones para registrarte en la aplicación', quasar);
  } else {
    // Creo el usuario
    try {
      const response = await registro(datosRegistro);
      console.log("Response", response);
      // Verifico el estado de la respuesta y muestro el mensaje correspondiente
      if (response.data === "Prueba con otro email") {
        console.log('Email ya está en uso:', response.data);
        mostrarAlertaError('Prueba con otro correo electrónico', quasar);
      } else {
        // Si todo va bien creo el usuario y redirecciono al login
        console.log('Usuario creado correctamente:', response.data);
        mostrarAlertaExito('Usuario creado correctamente, por favor inicia sesión', quasar);
        borrar();
        router.push({ path: '/auth/login' });
      }
    } catch (error) {
      // Error de red o algo parecido
      console.error('Error al crear el usuario:', error);
      mostrarAlertaError('Error al crear el usuario intentelo más tarde', quasar);
    }
  }
};

// CUANDO SE BORRA EL FORMULARIO, ELIMINO LOS VALORES DEL EMAIL Y LA CONTRASEÑA.
const borrar = () => {
  datosRegistro.nombre = ''
  datosRegistro.apellido = ''
  datosRegistro.email = ''
  datosRegistro.password = ''
  accept.value = false;
};

// CUANDO SE HACE CLIC EN EL BOTÓN DE REGRESAR, REDIRIJO AL USUARIO A LA PÁGINA DE INICIO.
const regresar = () => {
  router.push({ path: '/' })
};

// CUANDO SE HACE CLIC EN EL BOTÓN PARA MOSTRAR U OCULTAR LA CONTRASEÑA(OJITO), CAMBIO EL ESTADO PARA MOSTRARLA U OCULTARLA.
const cambiarMostrarPassword = () => {
  mostrarContrasenia.value = !mostrarContrasenia.value;

};



</script>


<!--  ESTILOS -->
<style scoped>
.full-width {
  /* Ajusto el ancho según tus preferencias */
  width: calc(50% - 10px);
}
</style>
