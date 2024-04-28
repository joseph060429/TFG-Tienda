<template>
    <div>
      <h1>Bienvenido, Usuario</h1>
      <div>
        <FormularioEditarPerfil v-if="popup" /> <q-btn @click="editarPerfil" label="Mi Perfil" />
      </div>
      <q-btn @click="eliminarPerfil" label="Darte de baja" />
    </div>
  </template>

<script setup>
import { useRouter } from 'vue-router'
import { mostrarAlertaExito, mostrarAlertaError } from '~/utils/alertas';
import { reactive } from "vue";
import { useAuth } from '~/composables/useAuth.js';
// import {FormularioEditarPerfil} from '#components';



definePageMeta({
    role: ['ROLE_USER']
});

const popup = ref(false)
const router = useRouter();

const editarPerfil = () => {
  // Abrir el formulario solo si no está ya abierto
  if (!popup.value) {
    popup.value = true;
  }
};

const eliminarPerfil = () => {
    router.push({ path: '/usuario/eliminarPerfil' })
};




</script>

<style lang="scss" scoped></style>