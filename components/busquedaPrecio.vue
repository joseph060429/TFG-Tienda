<template>
<q-dialog v-model="mostrarBuscarPorPrecio" class="q-dialog-max-w-lg">
        <q-card class="q-dialog" style="background-color: #f5f5f5;">
            <q-card-section class="text-center">
                <!-- Input para el precio mínimo -->
                <q-input v-model="precioMinimo" label="Precio mínimo" outlined type="number" color="#ffcccc"
                    class="input-custom" />
                <!-- Input para el precio máximo -->
                <q-input v-model="precioMaximo" label="Precio máximo" outlined type="number" color="#ffcccc"
                    class="input-custom" />
            </q-card-section>
            <q-card-actions align="center" class="q-mt-lg">
                <!-- Botón "Cancelar" -->
                <q-btn label="Cancelar" @click="cerrarDialogoBuscarPrecios" class="button-cancelar" v-close-popup />
                <!-- Botón "Buscar" -->
                <q-btn label="Buscar"@click="busquedaPorPrecio" class="button-buscar"
                    v-close-popup />
            </q-card-actions>
        </q-card>
    </q-dialog> 
</template>
<script setup>
import { mostrarAlertaExito, mostrarAlertaError } from '~/utils/alertas';
import { reactive, ref } from "vue";
import { productoComposable } from '~/composables/productoComposable';

const quasar = useQuasar()

const props = defineProps({
  mostrarBuscarPorPrecio: Boolean
});

const mostrarBuscarPorPrecio = ref(props.mostrarBuscarPorPrecio);

const { buscarProductosPorRangoPrecio, productos } = productoComposable();

const precioMinimo = ref('')
const precioMaximo = ref('')

const busquedaPorPrecio = async () => {
    try {
        const response = await buscarProductosPorRangoPrecio(precioMinimo.value, precioMaximo.value);
        console.log("Response:", response.data)
        if (response.data.length === 0) {
            mostrarAlertaError('No hay productos que coincidan con la búsqueda', quasar)
        } else {
            const productosOrdenados = response.data.sort((a, b) => a.precioProducto - b.precioProducto);
            productos.value = productosOrdenados;
            cerrarDialogoBuscarPrecios();
        }
    } catch (error) {
        console.log("error", error);
    }
};

const cerrarDialogoBuscarPrecios = () => {
    console.log("has presionado cancelar");
    mostrarBuscarPorPrecio.value = false;
};
</script>


<style lang="scss" scoped>
.q-dialog-max-w-md {
    max-width: 500px;
}
.input-custom {
    border-radius: 4px;
}

.button-buscar {
    border-radius: 4px;
    min-width: 120px;
    background-color: #007bff; /* Color de fondo */
    color: #fff; /* Color del texto */
    border-color: #007bff; /* Color del borde */
}

.button-cancelar {
    background-color: #6c757d; /* Color de fondo */
    color: #fff; /* Color del texto */
    border-color: #6c757d; /* Color del borde */
}

</style>
