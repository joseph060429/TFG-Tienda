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
            <!-- Botón para crear productos  -->
            <div class="productos">
                <!-- Botón para editar el perfil -->
                <q-btn @click="todoProductos" class="boton-producto" label="Crear Producto">
                    <q-icon name="mdi-package-variant-closed" />
                </q-btn>
            </div>
            <!-- Botón para ver el historial de los pedidos -->
            <div class="usuarios">
                <!-- Botón para ver pedidos -->
                <q-btn @click="perfilesDeUsuario" class="boton-usuarios" label="Usuarios">
                    <q-icon name="mdi-account-group" />
                </q-btn>
            </div>
            <!-- Botón para ver todo lo de pedidos -->
            <div class="pedidos">
                <!-- Botón para ver pedidos -->
                <q-btn @click="todoPedidos" class="boton-pedidos" label="Pedidos">
                    <q-icon name="mdi-cart-outline" />
                </q-btn>
            </div>
        </div>
        <div>
            <template v-if="productos.length > 0">
                <BarraDeBusquedaAdmin />
            </template>
        </div>
        <q-btn @click="regresar" flat dense class="custom-regresar-button">
            Volver <q-icon name="mdi-refresh" />
        </q-btn>
        <div>
            <CardProductoAdmin />
        </div>
    </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from 'vue-router'
import { productoAdminComposable } from '~/composables/productoAdminComposable';

definePageMeta({
    role: ['ROLE_ADMIN']
});

// RUTAS
const router = useRouter()

let authStore = useAuthStore();
const nombre = authStore.auth.nombre

const { productos } = productoAdminComposable();
const mostrarFormularioEditarPerfil = ref(false);

const editarPerfil = () => {
    // Abro el formulario si no esta abierto
    if (!mostrarFormularioEditarPerfil.value) {
        mostrarFormularioEditarPerfil.value = true;
    }
};

const todoProductos = () => {
    router.push({ path: '/producto/crearProducto' })
};

const perfilesDeUsuario = () => {
    router.push({ path: '/admin/adminUsuarios' })
};
const todoPedidos = () => {
    router.push({ path: '/admin/adminPedidos' })
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
    color: #BF360C;
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
    width: 70%;
    margin: 1em;
}

// BOTON DE MI PERFIL
.boton-mi-perfil {
    background-color: gray;
    color: black;
    transition: background-color 0.3s;
    min-width: 200px;
    margin-bottom: 2.5%;
}

.boton-mi-perfil:hover {
    background-color:  #464646;
}

// BOTON DE PRODUCTO
.boton-producto {
    // margin-top: 20px;
    /* Color naranja */
    background-color:gray;
    /* Texto blanco */
    color: black;
    transition: background-color 0.3s;
    min-width: 200px;
    margin-bottom: 2.5%;
}

.boton-producto:hover {
    background-color:  #464646;
}

// BOTON DE USUARIOS
.boton-usuarios {
    // margin-top: 20px;
    /* Color naranja */
    background-color: gray;
    /* Texto blanco */
    color: black;
    transition: background-color 0.3s;
    min-width: 200px;
    margin-bottom: 2.5%;
}

.boton-usuarios:hover {
    background-color:  #464646;;
}

// BOTON DE USUARIOS
.boton-pedidos {
    // margin-top: 20px;
    /* Color naranja */
    background-color: gray;
    /* Texto blanco */
    color: black;
    transition: background-color 0.3s;
    min-width: 200px;
    margin-bottom: 2.5%;
}

.boton-pedidos:hover {
    background-color:  #464646;;
}
</style>