<template>
  
  <div class="container">
    <template v-if="productos.length > 0" >
      <BarraDeBusquedaUsuario />
    </template>
    <CardProducto />
  </div>
</template>

<script setup>
// Importaciones
import { useRouter } from 'vue-router'
import { productoComposable } from '~/composables/productoComposable';

const { productos } = productoComposable();

definePageMeta({
  role: ['PUBLIC']
})

// Rutas
const router = useRouter()

// Prueba axios
const axios = useNuxtApp().$axios

</script>

<style lang="scss" scoped>
.container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  height: 70vh;
}

.card-producto {
  max-height: 70vh;
  /* Establecer la altura máxima de la tarjeta */
}
</style>