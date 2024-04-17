<template>
  <div class="q-pa-md d-flex justify-center align-center" style="height: 100vh;">
    <div class="d-flex justify-center align-center" style="max-width: 40%; margin: auto;">
      <h1 class="text-h4 q-mb-md text-center"
        style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
        Regístrate
      </h1>

      <q-form @submit.prevent="Enviar" @reset="Borrar" class="q-gutter-md">

        <!-- Campo nombre -->
        <q-input filled v-model="nombre" label="Nombre *" hint="Nombre" lazy-rules :rules="[
          val => val && val.length > 0 || 'Por favor, introduce algo',
          val => /^.{2,70}$/.test(val) || 'El nombre debe tener entre 2 y 70 caracteres',
          val => /^\S.*\S$/.test(val) || 'El nombre no puede empezar ni terminar con espacios en blanco',
        ]">
          <template v-slot:prepend>
            <q-icon name="mdi-account" />
          </template>
        </q-input>

        <!-- Campo apellido -->
        <q-input filled v-model="apellidos" label="Apellidos *" hint="Apellidos" lazy-rules :rules="[
          val => val && val.length > 0 || 'Por favor, introduce algo',
          val => /^.{2,70}$/.test(val) || 'Los apellidos debe tener entre 2 y 70 caracteres',
          val => /^\S.*\S$/.test(val) || 'Los apellidos no pueden empezar ni terminar con espacios en blanco',
        ]">
          <template v-slot:prepend>
            <q-icon name="mdi-account" />
          </template>
        </q-input>

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
        <q-input filled v-model="password" type="password" label="Contraseña *" lazy-rules :rules="[
          val => val && val.length > 0 || 'Por favor, introduce algo',
          val => val.length >= 8 || 'La contraseña debe tener al menos 8 caracteres'
        ]">
          <template v-slot:prepend>
            <q-icon name="mdi-lock" />
          </template>
        </q-input>

        <q-toggle v-model="accept" label="Acepto los términos y condiciones" class="q-mt-lg" />

        <!-- Botones de enviar y reiniciar -->
        <div class="q-mt-lg d-flex justify-around">

          <!-- Boton de enviar -->
          <q-btn label="Enviar" type="submit" class="full-width custom-button">
            <q-icon name="mdi-send" class="q-ml-md"></q-icon> <!-- Icono para el botón -->
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
import { onBeforeMount } from "vue";
import { useNuxtApp } from '#app'

const quasar = useQuasar()
onBeforeMount(() => {
  console.log('Esto es quasar ==> ', quasar);
})

const nombre = ref('');
const apellidos = ref('');
const email = ref('');
const password = ref('');
const accept = ref(false);


const Enviar = () => {
  if (accept.value === false) {
    console.log('Hola');
    // nuxtApp.$q.notify({
    //   color: 'green',
    //   textColor: 'white',
    //   icon: 'info',
    //   message: 'Hello from Quasar!'
    // })
    alert('Debes aceptar los términos y condiciones para enviar el formulario.')

    return;
  }
  console.log("ENVIADO CORRECTAMENTE");
};

const mostrarAlerta = (message) => {
  Alert.create({
    html: message,
    color: 'negative',
    position: 'top'
  });

};

const Borrar = () => {
  nombre.value = '';
  apellidos.value = '';
  email.value = '';
  password.value = '';
  accept.value = false;
};
</script>

<style scoped>
.full-width {
  width: calc(50% - 10px);
  /* Ajusta el ancho según tus preferencias */
}

.custom-h1 {
  color: #333333;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.custom-button {
  background-color: #BFC9CA;

  /* Cambia el color del texto a blanco */
  transition: background-color 0.3s ease;
  /* Agrega una transición suave al color de fondo */
}

.custom-button:hover {
  background-color: #95A5A6;
  /* Cambia el color de fondo al pasar el mouse sobre el botón */
}

.custom-button-reiniciar {
  transition: background-color 0.3s ease;
  /* Agrega una transición suave al color de fondo */
}

.custom-button-reiniciar:hover {
  background-color: #95A5A6;
  /* Cambia el color de fondo al pasar el mouse sobre el botón */
}
</style>
