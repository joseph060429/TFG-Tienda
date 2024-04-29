<template>
    <div>
      <h1>Bienvenido, Usuario</h1>
      <div>
        <formularioEditarPerfil v-model="motrarFormuEditarPeril" /> <q-btn @click="editarPerfil" label="Mi Perfil" />
      </div>
      <q-btn @click="eliminarPerfil" label="Darte de baja" />
    </div>
  </template>

<script setup>
import { useRouter } from 'vue-router'
import { mostrarAlertaExito, mostrarAlertaError } from '~/utils/alertas';
import { reactive } from "vue";
import { useAuth } from '~/composables/useAuth.js';

// Prueba subida empresa numero 29



definePageMeta({
    role: ['ROLE_USER']
});

const motrarFormuEditarPeril = ref(false)
const router = useRouter();

const editarPerfil = () => {
  // Abrir el formulario solo si no está ya abierto
  if (!motrarFormuEditarPeril.value) {
    motrarFormuEditarPeril.value = true;
  }
};

const eliminarPerfil = () => {
    router.push({ path: '/usuario/eliminarPerfil' })
};




</script>

<style lang="scss" scoped></style>