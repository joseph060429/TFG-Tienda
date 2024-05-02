<template>
  <div class="container">
    <CardProducto />
  </div>
</template>

<script setup>
// Importaciones
import { useRouter } from 'vue-router'
import CardProducto from '~/components/CardProducto.vue';
definePageMeta({
  role: ['PUBLIC']
})

// Rutas
const router = useRouter()

// Prueba axios
const axios = useNuxtApp().$axios

</script>

<style lang="scss" scoped>
.container{
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    height: 70vh;
}
.card-producto {
  max-height: 70vh; /* Establecer la altura máxima de la tarjeta */
}
</style>