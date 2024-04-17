<template>
  <div class="q-pa-md d-flex justify-center align-center" style="height: 100vh;">
    <div class="d-flex justify-center align-center" style="max-width: 40%; margin: auto;">
      <h1 class="text-h4 q-mb-md text-center"
        style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
        Login
      </h1>
      <q-form @submit.prevent="Enviar" @reset="Borrar" class="q-gutter-md">

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


const email = ref('');
const password = ref('');


const Enviar = () => {

  console.log("ENVIADO CORRECTAMENTE");
};

const Borrar = () => {
  email.value = '';
  password.value = '';
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