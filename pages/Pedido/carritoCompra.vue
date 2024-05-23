<template>
    <div>
        <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
        <eliminar-carrito :idCarrito="productoAEliminar" v-model="mostrarEliminarCarrito" />
        <h2 class="titulo">CARRITO DE COMPRAS</h2>
        <div class="precio-total">PRECIO TOTAL: {{ precioTotal }} €</div>
        <q-table class="tabla" flat bordered :rows="usuario.carrito" row-key="index" virtual-scroll
            :virtual-scroll-item-size="48" :virtual-scroll-sticky-size-start="48" :pagination="{ rowsPerPage: 50 }"
            :rows-per-page-options="[50]" hide-no-data>
            <template v-slot:header="">
                <q-tr v-if="!loading">
                    <q-th>Producto</q-th>
                    <q-th>Marca</q-th>
                    <q-th>Precio</q-th>
                    <q-th>Imagen</q-th>
                    <q-th>Cantidad</q-th>
                    <q-th>Acciones</q-th>
                </q-tr>
                <q-tr v-if="loading">
                    <img src="../../assets/loading.gif"
                        class="loading" />
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
                        <!-- El atributo @blur en Vue.js se utiliza para asociar un manejador de eventos que se ejecutará cuando el elemento pierde el foco,
                    Esto significa que cada vez que el usuario hace clic fuera del campo de entrada (es decir, el campo pierde el foco), se llamará a la función anadirCantidadProducto  -->
                        <q-input class="input-custom" v-model="props.row.cantidadAnadidaAlCarrito" type="text" min="1"
                            max="999" dense
                            style="width: 50px; height: 30px; font-size: 14px; text-align: center; border-radius: 5px;"
                            maxlength="3" :rules="[esNumero]" @blur="anadirCantidadProducto(props.row)"></q-input>
                    </q-td>
                    <q-td class="text-center">
                        <q-btn @click="eliminarProducto(props.row.idCarrito)" class="boton-borrar">
                            <q-icon name="mdi-delete" />
                        </q-btn>
                    </q-td>
                </q-tr>
                <!-- {{ props.row.totalCarrito }}  -->
            </template>
        </q-table>
        <q-btn @click="seguirComprando" label="Comprar" class="boton-seguir-comprando">
            <q-icon name="mdi-cart-plus" />
        </q-btn>
    </div>
</template>



<script setup>

import { usuarioComposable } from '~/composables/usuarioComposable';
import { ref, onMounted } from 'vue';
import { getImagenURL } from '~/utils/imagenURL.js';


definePageMeta({
    role: ['ROLE_USER']
});
//USAR QUASAR
const quasar = useQuasar()

const productoAEliminar = ref(null)

const { verMiCarrito, usuario } = usuarioComposable();

const loading = ref(true)

onBeforeMount(() => {
    obtenerProductosDelCarrito()
})

// RUTAS
const router = useRouter()

const mostrarEliminarCarrito = ref(false);


// FUNCION PARA QUE ME MUESTRE EL PRECIO TOTAL 
const precioTotal = computed(() => {
    console.log("usuario.value", usuario.value);
    console.log("usuario.value.carrito", usuario.value.carrito);
    return usuario.value.carrito.reduce((total, item) => total += item.precioProducto, 0);
})

// FUNCION PARA OBTENER TODOS LOS PRODUCTOS DEL CARRITO
const obtenerProductosDelCarrito = async () => {
    try {
        const response = await verMiCarrito();
        console.log("RESPONSE: ", response.data);
        loading.value = false
    } catch (error) {
        // Error de red u otro error
        console.error('Error al ver el carrito de compra', error);
        mostrarAlertaError('Error al ver el carrito de compra', quasar);
    }
};

// FUNCION PARA AÑADIR CANTIDAD DE PRODUCTOS AL CARRITO
const anadirCantidadProducto = async (item) => {
    try {
        console.log(item.productoId, 'item', item.cantidadAnadidaAlCarrito, 'cantidad');
        const response = await verMiCarrito(item.productoId, item.cantidadAnadidaAlCarrito);
        console.log("RESPONSE: ", response.data);
        if (response.data === 'La cantidad solicitada debe ser mayor que cero') {
            mostrarAlertaError('La cantidad solicitada debe ser mayor que cero', quasar);
        }
        if (response.data === 'La cantidad solicitada supera la cantidad disponible del producto') {
            mostrarAlertaError(' La cantidad solicitada supera la cantidad disponible del producto', quasar);
        }

    } catch (error) {
        // Error de red u otro error
        console.error('Error al ver el carrito de compra', error);
        mostrarAlertaError('Error al ver el carrito de compra', quasar);
    }
};


const eliminarProducto = (id) => {
    productoAEliminar.value = id
    if (!mostrarEliminarCarrito.value) {
        mostrarEliminarCarrito.value = true;
    }
};

const regresar = () => {
    router.push({ path: '/usuario/vistaInicioUsuario' })
};

const seguirComprando = () => {
    router.push({ path: '/pedido/formularioDireccion' });
};



const esNumero = (val) => {
    if (!val || isNaN(val)) {
        // return 'Por favor, introduce solo números.';
        mostrarAlertaError('Por favor, introduce solo números', quasar);
    }
    return true;
};

</script>

<style scoped>
.tabla {
    height: 45vh;
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
        height: 45vh;
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
    /* margin-top: 0em; */
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
        font-size: 12px;
        padding: 3px 6px;
    }
}


.precio-total {
    font-size: 1.2em;
    font-weight: bold;
    margin-bottom: 1vh;
    text-align: right;
    margin-right: 5%;
}


.boton-seguir-comprando {
    margin-top: 1%;
    margin-left: 84.5%;
    background-color: #FF6347;
}

@media only screen and (max-width: 600px) {
    .boton-seguir-comprando {
        font-size: 12px;
        padding: 3px 6px;
        margin-top: 2%;
        margin-left: 62%;
    }
}


.loading {
    display:flex;
    margin: auto;
    width: 30vh;
    padding-top: 2em;
    justify-content: center; 
    align-items: center; 
    height: auto;
}
</style>