<template>
<q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
<div>
        <h1>Eliminar Perfil</h1>
    </div>
</template>

<script setup>


// RUTAS
const router = useRouter()
const regresar = () => {
    router.push({ path: '/usuario/vistaInicioUsuario' })
};

</script>

<style lang="scss" scoped>

</style>