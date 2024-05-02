<template>

  <div>
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

</style>