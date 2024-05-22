<template>
    <div v-if="!loading">
        <!-- <h2 class="titulo">DIRECCIONES DE ENVÍO</h2> -->
        <div v-if="usuario.direccionesEnvioFacturacion.direccionesEnvio.length > 0 && usuario.direccionesEnvioFacturacion.direccionesFacturacion.length > 0">
            <h6>direcciones facturacion</h6>
            <q-card v-for="(direccion, index) in usuario.direccionesEnvioFacturacion.direccionesFacturacion"  :key="index" class="q-ma-md">
                <q-card-section>
                    <div class="text-h6">{{ direccion }}</div>
                </q-card-section>
            </q-card>
            <q-separator />
            <h6>direcciones envio</h6>
            <q-card v-for="(direccion, index) in usuario.direccionesEnvioFacturacion.direccionesEnvio"  :key="index" class="q-ma-md">
                <q-card-section>
                    <div class="text-h6">{{ direccion }}</div>
                </q-card-section>
            </q-card>
        </div>
        <div v-else>
            <q-card class="q-ma-md">
                <q-card-section>
                    <div class="text-h6">No hay direcciones de envío disponibles</div>
                </q-card-section>
            </q-card>
        </div>
    </div>
</template>

<script setup>
import { ref, defineProps, onBeforeMount, computed } from 'vue';
import { usuarioComposable } from '~/composables/usuarioComposable';




//USAR QUASAR
const quasar = useQuasar()


const { direccionesUsuario, usuario } = usuarioComposable();


const loading = ref(true)

// FUNCION PARA TRAER LOS PEDIDOS DEL USUARIO 
onBeforeMount(async () => {
    await direccionesEnvioFacturacion();
    console.log("Direcciones envio", usuario.value.direccionesEnvioFacturacion);
    loading.value = false
})


const direccionesEnvioFacturacion = async () => {
    try {
        const response = await direccionesUsuario();
        console.log("RESPONSE DIRECCION FACTURACION: ", response.data);
        // pedidos.value = response.data;
    } catch (error) {
        // // Error de red u otro error
        // console.error('Error al ver el historial de pedidos', error);
        // mostrarAlertaError('Error al ver el historial de pedidos', quasar);
    }
}




</script>

<style lang="scss" scoped></style>
