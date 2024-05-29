<template>
    <CardDireccionEnvío @error="showError($event)" />
</template>

<script setup>
import { ref, defineProps, onBeforeMount } from 'vue';
import { useRouter } from 'vue-router';

const quasar = useQuasar()
definePageMeta({
    role: ['ROLE_USER']
});

function showError(error) {
    console.log(error)
    mostrarAlertaError(error, quasar)
}

</script>

<style lang="scss" scoped>
.scrollable-container {
    max-height: 70vh;
    overflow: auto;
}

.container {
    max-width: 80%;
    margin: auto;
    text-align: center;
}
</style>