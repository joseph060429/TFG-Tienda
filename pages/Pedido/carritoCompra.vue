<template>
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <h2 class="titulo">Carrito de Compras</h2>
    <q-table class="tabla" flat bordered :rows="carrito" row-key="index" virtual-scroll :virtual-scroll-item-size="48"
        :virtual-scroll-sticky-size-start="48" :pagination="{ rowsPerPage: 50 }" :rows-per-page-options="[50]">

        <template v-slot:header="props">
            <q-tr :props="props">
                <q-th key="nombreProducto">Producto</q-th>
                <q-th key="marcaProducto">Marca</q-th>
                <q-th key="precioProducto">Precio</q-th>
                <q-th key="imagendProducto">Imágen</q-th>
                <q-th key="cantidadProducto">Cantidad</q-th>
                <q-th key="acciones">Aciones</q-th>
            </q-tr>
        </template>
        <template v-slot:body="props">
            <q-tr :props="props">
                <q-td>{{ props.row.nombreProducto }}</q-td>
                <q-td>{{ props.row.marcaProducto }}</q-td>
                <q-td>{{ props.row.precioProducto }} €</q-td>
                <q-td><q-img class="imagen" :src="getImagenURL(props.row.imagenProducto)" />
                </q-td>
                <q-td>
                    <q-input class="input-custom" v-model="props.row.cantidadAnadidaAlCarrito" type="number" min="1"
                        dense
                        style="width: 50px; height: 30px; font-size: 14px; text-align: center; border-radius: 5px;"
                        lazy-rules :rules="[
                            val => val !== null && val !== undefined || 'Por favor, introduce algo',
                            val => !isNaN(val) || 'La cantidad debe ser un número',
                            val => parseInt(val) >= 1 || 'La cantidad debe ser mayor o igual a 1'
                        ]" @blur="probando(props.row)"></q-input>
                </q-td>
                <q-td class="text-center">
                    <q-btn @click="eliminarProducto()" class="boton-borrar">
                        <q-icon name="mdi-delete" />
                    </q-btn>
                </q-td>
            </q-tr>
        </template>
    </q-table>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { getImagenURL } from '~/utils/imagenURL.js';

definePageMeta({
    role: ['ROLE_USER']
});

const { verMiCarrito } = usuarioComposable();


onMounted(() => {
    obtenerProductosDelCarrito()
})

function probando(item) {
    console.log(item.productoId, 'item', item.cantidadProducto, 'cantidad');
    // await store.actualizarCarrito(item.productoId, item.cantidadProducto)
}

// RUTAS
const router = useRouter()

const carrito = ref([]);

const obtenerProductosDelCarrito = async () => {
    try {
        const response = await verMiCarrito();
        console.log("RESPONSE: ", response.data);
        carrito.value = response.data;
    } catch (error) {
        // Error de red u otro error
        console.error('Error al ver el carrito de compra', error);
        mostrarAlertaError('Error al ver el carrito de compra', quasar);
    }
};

const regresar = () => {
    router.push({ path: '/usuario/vistaInicioUsuario' })
};



const procederAlPago = () => {

};

</script>

<style scoped>
.tabla {
    height: 55vh;
    width: 90%;
    background-color: #F5F5F5;
    font-family: Arial, sans-serif;
    border: 3px solid black;
    margin-left: 5%;
    margin-right: 5%;
}

@media (max-width: 600px) {
    .tabla {
        width: 90%;
        margin: 0 auto;
        height: 50vh;
        font-size: 0.8em;
    }
}

@media screen and (max-width: 600px) {
    .imagen {
        max-width: 90%;
        max-height: 70vh;
    }
}

.tabla th {
    font-weight: bold;
}

.imagen {
    max-width: 50%;
    max-height: 50vh;
    height: auto;
}

.tabla td {
    text-align: center;
}

.input-custom {
    text-align: center;
    margin: auto;
}


.titulo {
    font-size: 2em;
    text-align: center;
    margin: 1em;
    color: #546e7a;
    font-weight: bold;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.boton-borrar {
    background-color: #FF4500;
    border: 1px solid;

}

.boton-borrar:hover {
    background-color: rgba(255, 69, 0, 0.8);

}

.boton-borrar {
    font-size: 0.8em;
    padding: 5px 10px;
    margin: 0 5px;
}

@media only screen and (max-width: 600px) {
    .boton-borrar {
        font-size: 14px;
        padding: 3px 6px;
    }
}
</style>