<template>
  <div class="q-pa-md">
    <q-layout view="lHh lpr lFf" container style="height: 10vh; width: 100%">
      <q-header class="header" style="background-color: #666666; height: 10vh; width: 100%">
        <q-toolbar class="text-primary">
          <div class="logo-container">
            <img
              src="https://firebasestorage.googleapis.com/v0/b/proyecto-ionic-tienda.appspot.com/o/Logo-Imagenes%2FLogo-Letra.png?alt=media&token=04198112-d45c-4a6c-b014-accbeecbbd4d"
              alt="Logo" class="logo">
          </div>
          <q-space></q-space> <!-- Espacio flexible para empujar los elementos hacia la derecha -->
          <h1 class="header-text">Explora el poder de la tecnología, crea tu futuro.</h1>
          <q-space></q-space> <!-- Espacio flexible para empujar los elementos hacia la derecha -->
          <q-btn v-if="!isLogged" color="dark" dense flat @click="registro" class="custom-btn">
            <q-icon name="mdi-account-plus"></q-icon> <!-- Icono para el botón de registro -->
            Registro
          </q-btn>
          <q-btn v-if="!isLogged" color="dark" dense flat @click="login" class="custom-btn">
            <q-icon name="mdi-login"></q-icon> <!-- Icono para el botón de login -->
            Login
          </q-btn>
          <q-btn v-if="isLogged" color="dark" dense flat @click="cerrarSesion" class="custom-btn">
            <q-icon name="mdi-logout"></q-icon>
            Cerrar sesion
          </q-btn>
        </q-toolbar>
      </q-header>
    </q-layout>
  </div>
</template>


<script setup>
// Importaciones
import { useRouter } from 'vue-router'
import useAuthStore from '~/stores/authStore.js'
// import
// let authStore = useAuthStore()

// Rutas
const router = useRouter()

// PRUEBA
// const token = localStorage.getItem('token')

// Función para verificar si el usuario está autenticado

// Función para determinar si mostrar los botones de login y registro
const showRegisterAndLogin = () => {
  const route = useRoute(); // Obtener la ruta actual
  console.log('Rol de la ruta actl:', route.meta.role);
  return route.meta.role === 'PUBLIC'; // Solo muestra los botones si la ruta es pública
};

let isLogged = false;

// onMounted(() => {
//   if (authStore.checkLogin() == true) {
//     if(token)
//     isLogged = true
//   } else {
//     isLogged = false
//   }
// })




const login = () => {
  router.push({ path: '/auth/login' })

};

const registro = () => {
  router.push({ path: '/auth/registro' })
  console.log("Registro");
};

const cerrarSesion = () => {
  localStorage.removeItem('token')
  router.push({ path: '/' })
}


</script>


<style scoped>
.header {
  display: flex;
  align-items: center;
}

.logo-container {
  margin-left: 20px;
}

.logo {
  max-height: 80%;
  max-width: 200px;
}

.header-text {
  color: white;
  font-size: 18px;
  margin-right: 20px;
}

.custom-btn {
  color: white;
  transition: background-color 0.3s ease;
}

.custom-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.header-text {
  color: white;
  font-size: 20px;
  /* Tamaño de fuente más grande */
  font-weight: bold;
  /* Texto en negrita */
  font-family: 'Arial', sans-serif;
  /* Tipo de fuente personalizado */
  text-transform: uppercase;
  /* Convertir el texto a mayúsculas */
  letter-spacing: 2px;
  /* Espaciado entre letras */
  margin-right: 20px;
}

.text-h6 {
  font-size: 1.25rem;
  /* Tamaño de fuente más grande para dispositivos grandes */
  font-weight: bold;
  font-family: 'Arial', sans-serif;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin: 0;
}

/* Estilos adicionales para dispositivos pequeños */
@media (max-width: 600px) {
  .logo-container {
    margin-left: 10px;
  }

  .header-text {
    display: none;
    /* Ocultar el texto en dispositivos pequeños */
  }

  .custom-btn {
    font-size: 0.75rem;
    /* Tamaño de fuente más pequeño para botones en dispositivos pequeños */
  }
}
</style>