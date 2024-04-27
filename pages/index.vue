<template>
  <div class="home-container">
    <div class="banner">
      <h1>Bienvenido a nuestra tienda de ordenadores y componentes</h1>
      <p>Descubre nuestra amplia selección de productos de alta calidad</p>
      <q-btn label="Explorar" color="primary" @click="listarProductos" class="custom-btn" />
    </div>
  </div>
</template>

<script setup>
// Importaciones
import { useRouter } from 'vue-router'
definePageMeta({
  role: ['PUBLIC']
})

// Rutas
const router = useRouter()



const listarProductos = () => {
  router.push({ path: '/productos/listarProductos' })
};

// Prueba axios
const axios = useNuxtApp().$axios




</script>

<style scoped>
.home-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.banner {
  text-align: center;
  max-width: 600px;
  padding: 20px;
}

.banner h1 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 20px;
}

.banner p {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 30px;
}

.custom-btn {
  padding: 12px 40px;
  font-size: 1.4rem;
  border-radius: 25px;
}
</style>