<template>
    <div>
        <h1> Soy la vista usuario, preguntar si hay una mejor manera de hacer las rutas</h1>
    </div>
</template>

<script setup>
// Importo el middleware que he creado
import authMiddleware from '~/middleware/authMiddleware.js';

// Agrego el middleware a la configuración de la página
definePageMeta({
    middleware: [authMiddleware]
});
</script>

<style lang="scss" scoped></style>
