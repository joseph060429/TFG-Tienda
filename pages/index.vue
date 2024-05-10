<template>
  <template v-if="productos.length > 0">
    <div class="barra-busqueda">
      <BarraDeBusquedaUsuario />
    </div>
    <q-btn @click="regresar" flat dense class="custom-regresar-button">
    Volver <q-icon name="mdi-refresh" />
  </q-btn>
  </template>
  <!-- <q-btn @click="regresar" flat dense class="custom-regresar-button">
    Volver <q-icon name="mdi-refresh" />
  </q-btn> -->
  <div class="container">
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

const regresar = () => {
  window.location.reload();
};

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

.custom-regresar-button {
  position: absolute;
  /* Posiciona el botón de manera absoluta */
  left: 0;
  /* Alinea el botón a la izquierda del contenedor */
  top: 29vh;
  /* Centra verticalmente el botón */
  transform: translateY(-50%);
  /* Ajusta la posición vertical */
}


@media screen and (max-width: 600px) {
  .custom-regresar-button {
    top: 27vh;
    /* Ajusta la posición vertical para dispositivos móviles */
  }
}
</style>