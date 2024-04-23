<template>
    <div>
        <h1> Soy la vista usuario, preguntar si hay una mejor manera de hacer las rutas</h1>
    </div>
</template>

<script setup>
// Importo el middleware que he creado
// import authMiddleware from '~/middleware/authMiddleware.global.js';

// Agrego el middleware a la configuración de la página
definePageMeta({
    role: ['ROLE_USER']
});

</script>

<style lang="scss" scoped></style>
