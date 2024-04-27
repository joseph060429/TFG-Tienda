<template>
    <div>
        <h1>Bienvenido, Usuario</h1>
        <div>
            <!-- <AppHeader></AppHeader> -->

            <q-btn @click="editarPerfil" label="Editar Perfil" />

        </div>

        <q-btn @click="eliminarPerfil" label="Darte de baja" />
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

const popup = ref(false)
const router = useRouter();

const editarPerfil = () => {
    popup.value = true
};

const eliminarPerfil = () => {
    router.push({ path: '/usuario/eliminarPerfil' })
};




</script>

<style lang="scss" scoped></style>