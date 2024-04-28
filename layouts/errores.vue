<template>
  <div class="not-found-page">
    <h1>¡Página no encontrada!</h1>
    <p>Lo sentimos, la página que estás buscando no existe.</p>
    <button @click="goBackToHome">Volver al inicio</button>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'

const error = ref(null)
const router = useRouter()

const goBackToHome = () => {
  router.push('/')
}

onMounted(() => {
  error.value = useRoute().query.error
})
</script>

<style scoped>
.not-found-page {
  text-align: center;
  margin: 50px auto;
}
</style>