<template>
    <div class="q-pa-md" style="max-width: 400px">
      <h1>Registrate</h1>
      <q-form @submit="onSubmit" @reset="onReset" class="q-gutter-md">

        <q-input filled v-model="nombre" label="Nombre *" hint="Nombre" lazy-rules
          :rules="[
            val => val && val.length > 0 || 'Por favor, introduce algo',
            val => /^(?!\\s)(?=\\S)([a-zA-Z]+(\\s[a-zA-Z]+)*){2,70}(?!\\s)$/.test(val) || 'El nombre debe tener entre 2 y 70 caracteres y no puede empezar ni terminar con espacios en blanco'
          ]" />
  
        <q-input filled v-model="email" label="Email *" hint="Tu correo electrónico" lazy-rules
          :rules="[
            val => val && val.length > 0 || 'Por favor, introduce algo',
            val => /.+@.+\..+/.test(val) || 'El formato del correo electrónico no es válido'
          ]" />
  
        <q-input filled v-model="password" type="password" label="Contraseña *" lazy-rules
          :rules="[
            val => val && val.length > 0 || 'Por favor, introduce algo',
            val => val.length >= 8 || 'La contraseña debe tener al menos 8 caracteres'
          ]" />
  
        <q-toggle v-model="accept" label="Acepto los términos y condiciones" />
  
        <div>
          <q-btn label="Enviar" type="submit" color="primary" />
          <q-btn label="Reiniciar" type="reset" color="primary" flat class="q-ml-sm" />
        </div>
      </q-form>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const nombre = ref('');
  const email = ref('');
  const password = ref('');
  const accept = ref(false);
  
  const onSubmit = () => {
    // Aquí puedes implementar la lógica para enviar los datos del formulario
  };
  
  const onReset = () => {
    nombre.value = '';
    email.value = '';
    password.value = '';
    accept.value = false;
  };
  </script>
  
  <style scoped></style>
  