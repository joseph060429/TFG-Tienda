<template>
    <div>
        <h1> Soy la vista admin</h1>
    </div>
</template>

<script setup>
// Importo el middleware que has creado
import authMiddleware from '~/middleware/authMiddleware.global.js';

// Agrega el middleware a la configuración de la página
definePageMeta({
    role: ['ROLE_ADMIN']
});

</script>

<style lang="scss" scoped></style>
