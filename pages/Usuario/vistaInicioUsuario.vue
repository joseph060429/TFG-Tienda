<template>
    <div>
    <h1>Bienvenido, Usuario</h1>
    <q-btn @click="editarPerfil" label="Editar Perfil" />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { mostrarAlertaExito, mostrarAlertaError } from '~/utils/alertas';
import { reactive } from "vue";
import { useAuth } from '~/composables/useAuth.js';

definePageMeta({
    role: ['ROLE_USER']
});

const router = useRouter();

const editarPerfil = () => {
    router.push({ path: '/usuario/editarPerfil' })
};




</script>

<style lang="scss" scoped>

</style>