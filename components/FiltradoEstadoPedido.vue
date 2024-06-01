<template>
    <div class="search-bar">
        <q-select
            v-model="estado"
            :options="options"
            label="Filtrar por estado"
            outlined
            dense
            style="width: 300px"
            class="search-select"
        />
        <q-btn label="X" @click="estado = ''" flat round class="clear-btn" />
    </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const estado = ref('');

const options = [
    'PENDIENTE_PAGO',
    'PENDIENTE_ENVÍO',
    'CANCELADO',
    'ENVIADO',
    'PENDIENTE_CONFIRMACION_DIRECCION',
    'DIRECCION_ACTUALIZADA',
    'REPROGRAMADO_PARA_ENTREGA',
    'ENTREGADO',
    'RETRASADO',
    'DEVUELTO'
];

const emit = defineEmits(['estado']);

watch(estado, (newEstado) => {
    emit('estado', newEstado);
});
</script>

<style lang="scss" scoped>
.search-bar {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px;
    background-color: #f8f9fa;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.search-select {
    flex-grow: 1;
    .q-field__control {
        border-radius: 8px;
        border: 1px solid #ced4da;
    }
    .q-field__native {
        padding: 10px;
    }
    .q-input__bottom {
        display: none;
    }
}

.clear-btn {
    color: #ff6f61;
    border: 1px solid #ff6f61;
    &:hover {
        background-color: #ff6f61;
        color: white;
    }
}
</style>
