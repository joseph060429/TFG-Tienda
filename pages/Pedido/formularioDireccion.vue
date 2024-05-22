<template>
    <CardDireccionEnvío />
</template>

<script setup>
import { ref, defineProps, onBeforeMount } from 'vue';
import { useRouter } from 'vue-router';




definePageMeta({
    role: ['ROLE_USER']
});


</script>

<style lang="scss" scoped>
.scrollable-container {
    max-height: 70vh;
    overflow: auto;
}

.container {
    max-width: 80%;
    // background-color: aqua;
    margin: auto;
    text-align: center;
    // padding: 20px;
}
</style>