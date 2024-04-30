<template>
    <q-dialog v-model="verPedidos">
        <q-card class="q-dialog">
            {{ usuario.direccionEnvio }}
            <q-card-section class="text-center">
                <q-table :rows="detallePedido" :columns="detalleColumns" row-key="key" />
            </q-card-section>
            <q-card-section class="text-center">
                <!-- Aquí puedes agregar cualquier otro contenido adicional -->
            </q-card-section>
        </q-card>
    </q-dialog>
</template>

<!-- SCRIPT -->
<script setup>
import { ref, defineProps, onBeforeMount, computed } from 'vue';
import { usuarioComposable } from '~/composables/usuarioComposable';

const { historialPedidos, usuario } = usuarioComposable();

//USAR QUASAR
const quasar = useQuasar()


const props = defineProps({
    mostrarLosPedidos: Boolean
})
const verPedidos = ref(props.mostrarLosPedidos)

const test = ref('')

// FUNCIONES
// FUNCION PARA TRAER LOS PEDIDOS DEL USUARIO 
const historialDePedidos = async () => {
    try {
        const response = await historialPedidos();

        if (response.status === 404) {
            // Mostrar una alerta al usuario indicando que no tiene pedidos
            mostrarAlertaError("No tienes pedidos en tu historial.", quasar);
        } else {
            // Si hay pedidos, ocultar la vista de pedidos
            verPedidos.value = false;

            console.log("DIRECCIÓN DE ENVÍO: ", response.data[0].direccionEnvio);
            console.log("TIPO DE PAGO: ", response.data[0].tipoPago);
            console.log("ESTADO DEL PEDIDO: ", response.data[0].estado);
            console.log("FECHA DEL PEDIDO: ", response.data[0].fechaPedido);








            console.log("PRODUCTOS: ");

            for (const producto of response.data[0].productos) {
                console.log("CANTIDAD PEDIDA ", producto.cantidadPedida);
                console.log("NOMBRE DEL PRODUCTO ", producto.nombre);
                console.log("MARCA DEL PRODUCTO ", producto.marca);
                console.log("PRECIO DEL PRODUCTO ", producto.precioProducto);
            }
        }
    } catch (error) {
        // Error de red u otro error
        console.error('Error al ver el historial de pedidos', error);
        mostrarAlertaError('Error al ver el historial de pedidos', quasar);
    }
}

onBeforeMount(async () => {
    await historialDePedidos();

    console.log('Usuarios ==> ', usuario.value);
    test.value = usuario.value;
})

const detalleColumns = [
    { name: 'label', label: 'Información', field: 'label', align: 'left', sortable: true },
    { name: 'value', label: 'Valor', field: 'value', sortable: true }
];

// Computed sirve para traerme datos calculados de la base de datos
const detallePedido = computed(() => [{ label: 'Dirección de Envío', value: usuario.value ? usuario.value.direccionEnvio : 'Cargando...' }, 
{ label: 'Tipo de Pago', value: usuario.value ? usuario.value.tipoPago : 'Cargando...' }, { label: 'Estado del Pedido', value: usuario.value ? usuario.value.estado : 'Cargando...' }, { label: 'Fecha del Pedido', value: usuario.value ? usuario.value.fechaPedido : 'Cargando...' }]);






</script>

<!-- STYLE -->
<style lang="scss" scoped></style>