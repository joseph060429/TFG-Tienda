<template>
    <div>
        <q-select v-model="estado" :options="options" label="Filtrar por estado" style="width: 300px" />
        <q-btn label="X" @click="estado = ''"></q-btn>
    </div>
</template>

<script setup>


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
]

const emit = defineEmits(['estado'])

watch(estado, (x) => {
    emit('estado', x)
})

</script>

<style lang="scss" scoped></style>