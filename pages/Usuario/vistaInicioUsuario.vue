<template>
  <div class="container">
    <!-- Título principal -->
    <h1 class="title">Bienvenido/a, {{ nombre }}</h1>
    <!-- Sección del perfil -->
    <div class="botones">
      <div class="perfil">
        <formulario-editar-perfil v-model="mostrarFormularioEditarPerfil" />
        <!-- Botón para editar el perfil -->
        <q-btn @click="editarPerfil" class="boton-mi-perfil" label="Mi Perfil">
          <q-icon name="mdi-account" /> <!-- Icono de perfil -->
        </q-btn>
      </div>
      <!-- Botón para eliminar el perfil -->
      <div class="eliminar-perfil">
        <darse-de-baja v-model="mostrarDarseDeBaja" />
        <!-- Botón para editar el perfil -->
        <q-btn @click="eliminarPerfil" class="boton-borrar" label="Darte de baja">
          <q-icon name="mdi-delete" /> <!-- Icono de eliminar perfil -->
        </q-btn>
      </div>
      <!-- Botón para ver el historial de los pedidos -->
      <div class="ver-pedidos">
        <historial-pedidos v-model="verPedidos" />
        <!-- Botón para ver pedidos -->
        <q-btn @click="verMisPedidos" class="boton-ver-pedidos" label="Ver mis pedidos">
          <q-icon name="mdi-cart-outline" /> <!-- Icono de eliminar perfil -->
        </q-btn>
      </div>
    </div>
    <div>
      <template v-if="productos.length > 0">
        <BarraDeBusquedaUsuario />
      </template>
    </div>
    <q-btn @click="regresar" flat dense class="custom-regresar-button">
      Volver <q-icon name="mdi-refresh" />
    </q-btn>

    <div>
      <CardProducto />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { mostrarAlertaExito, mostrarAlertaError } from '~/utils/alertas';
import { ref } from "vue";
import { useAuth } from '~/composables/useAuth.js';
import { productoComposable } from '~/composables/productoComposable';

definePageMeta({
  role: ['ROLE_USER', 'ROLE_ADMIN']
});

let authStore = useAuthStore();
const { productos } = productoComposable();

const nombre = authStore.auth.nombre


const mostrarFormularioEditarPerfil = ref(false);
const mostrarDarseDeBaja = ref(false);
const verPedidos = ref(false);
const router = useRouter();

const editarPerfil = () => {
  // Abro el formulario si no esta abierto
  if (!mostrarFormularioEditarPerfil.value) {
    mostrarFormularioEditarPerfil.value = true;
  }
};

const eliminarPerfil = () => {
  // Abro el formulario si no esta abierto
  if (!mostrarDarseDeBaja.value) {
    mostrarDarseDeBaja.value = true;
  }
};

const verMisPedidos = () => {
  // Abro el formulario si no esta abierto
  if (!verPedidos.value) {
    verPedidos.value = true;

  }
};

const regresar = () => {
  window.location.reload();
};
</script>

<style lang="scss" scoped>
.container {
  /* Ancho máximo del contenedor */
  max-width: 100%;
  // height: 20vh;
  /* Centrar horizontalmente */
  margin: 0 auto;
  /* Relleno */
  /* 1% de la altura de la ventana */
  // padding: 2vh;
  height: 80vh;
  // background-color: black;
}

/* TITULO */
.title {
  /* Tamaño de fuente */
  padding: 0;
  margin: 0.5em;
  font-size: 2.5em;
  /* Margen inferior */
  margin-bottom: 20px;
  /* Color rojo */
  color: #F44336;
  /* Negrita */
  font-weight: bold;
  /* Sombra de texto */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.4);
}

// CONTENEDOR BOTONES
.botones {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  width: 50%;
  margin: 1em;


}

// BOTON DE MI PERFIL
.boton-mi-perfil {
  background-color: #FF5722;
  color: #fff;
  transition: background-color 0.3s;
  min-width: 200px;
  margin-bottom: 2.5%;
}

.boton-mi-perfi:hover {
  background-color: #F44336;

}

// BOTON DE ELIMINAR PERFIL
.boton-borrar {
  // margin-top: 20px;
  /* Color naranja */
  background-color: #F44336;
  // background-color: #F44336;
  /* Texto blanco */
  color: #fff;
  transition: background-color 0.3s;
  min-width: 200px;
  margin-bottom: 2.5%;
}

.boton-borrar:hover {
  background-color: #D32F2F;
}

// BOTON DE VER PEDIDOS
.boton-ver-pedidos {
  // margin-top: 20px;
  /* Color naranja */
  background-color: #F44336;
  // background-color: #F44336;
  /* Texto blanco */
  color: #fff;
  transition: background-color 0.3s;
  min-width: 200px;
  margin-bottom: 2.5%;
}

.boton-ver-pedidos:hover {
  background-color: #D32F2F;
}
</style>
