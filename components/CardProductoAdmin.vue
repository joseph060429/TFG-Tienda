<template>
     <!-- <q-pagination style="padding-top: 1%;" v-model="paginaActual" :max="totalPaginas" direction-links
            boundary-links /> -->
    <div style="padding-bottom: 6%; overflow: auto;" class="contenedor">
        <q-pagination style="padding-top: 1%;" v-model="paginaActual" :max="totalPaginas" direction-links
            boundary-links />

        <!-- Itero sobre cada producto en la página actual -->
        <div id="contenedor-items" class="flex flex-center">
            <div v-for="producto in productosPaginados" class="card-container">

                <q-card class="card q-mx-auto q-sm-w-75 q-md-w-50 q-lg-w-33" flat bordered @click="goTo(producto)">
                    <!-- Imagen del producto -->
                    <!-- {{ producto }} -->
                    <q-img :src="getImagenURL(producto.imagenProducto)" class="q-ma-md centered-image"
                        style="max-width: 45%; height: auto;" @click="goTo(producto)" />

                    <q-card-section>
                        <div>
                            Identificador: <span class="text-title">{{ producto.identificador }}</span>
                        </div>

                        <!-- Nombre del producto -->
                        <div>
                            Nombre: <span class="text-title">{{ producto.nombreProducto}}</span>
                        </div>
                        <!-- Marca del producto -->
                        <div class="text-subtitle1">
                            Marca: {{ producto.marcaProducto}}
                        </div>
                        <!-- Precio del producto -->
                        <div class="text-caption">
                            Precio: {{ producto.precioProducto }} €
                        </div>
                        <div class="text-caption">
                            Cantidad: {{ producto.cantidadProducto }}
                        </div>
                    </q-card-section>

                    <q-separator />
                </q-card>
            </div>
        </div>
    </div>
</template>


<!-- SCRIPTS -->
<script setup>
import { ref, defineProps, onBeforeMount, computed, watch } from 'vue';
import { productoAdminComposable } from '~/composables/productoAdminComposable';
import { getImagenURL } from '~/utils/imagenURL.js';

// El usuario es el de las stores
const { listarProductosAdmin, productos } = productoAdminComposable();

//USAR QUASAR
const quasar = useQuasar()

const router = useRouter()

const emits = defineEmits(['loaded', 'reloaded'])

const props = defineProps({
  reload: Boolean
})


watch(() => props.reload, () => {
  if (props.reload) {
    console.log('reload!!!!')
    emits('reloaded', true)
  }
})

// FUCNIONES

// FUNCION PARA CARGAR LOS PRODUCTOS  ANTES DE QUE SE MONTE EL COMPONENTE
// onBeforeMount(async () => {
//     await cargarProductos();
//     console.log("Productos cargados en el componente card del admin", productos.value);
// })

onBeforeMount(() => {
  cargarProductos().then(() => {
    emits('loaded', true)
  });

  // console.log("Productos cargados en el componente card", productos.value);
})

function goTo(item) {
    console.log("Item: ", item);
    router.push({ path: `/producto/admin/${item._id}` })

}

// FUNCION PARA CARGAR LOS PRODUCTOS
const cargarProductos = async () => {
    try {
        const response = await listarProductosAdmin();
        console.log("RESPONSE: ", response.data);
        // Actualizar productos después de aplicar el filtro de búsqueda
        // productos.value = response.data;
    } catch (error) {
        // Error de red u otro error
        console.error('Error al ver el historial de pedidos', error);
        mostrarAlertaError('Error al ver el historial de pedidos', quasar);
    }
}


// Calculo el número de productos por página
const elementosPorPagina = 16;

// Calculo el total de páginas
const totalPaginas = computed(() => Math.ceil(productos.value.length / elementosPorPagina));


// Calculo los productos a mostrar en la página actual
const paginaActual = ref(1);

const productosPaginados = computed(() => {
    const indiceInicial = (paginaActual.value - 1) * elementosPorPagina;
    const indiceFinal = indiceInicial + elementosPorPagina;
    return productos.value.slice(indiceInicial, indiceFinal);
});
watch(productos, () => {
    totalPaginas.value = Math.ceil(productos.value.length / elementosPorPagina);
});

const regresar = () => {
  window.location.reload();
};

</script>

<style lang="scss" scoped>
.contenedor {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  width: 100%;

  /* Media query para ajustar el ancho en dispositivos móviles */
  @media screen and (max-width: 600px) {
    width: 100%;
    margin: 5px;

  }
}


/* Custom styles */
.text-title {
  font-size: 1em;
  font-weight: bold;
  color: #333;

}

.text-subtitle1 {
  font-size: 1em;
  color: #333;
}

.text-caption {
  font-size: 0.875rem;
  color: #666;
}

.card-container {
  /* Ancho fijo para todas las tarjetas */
  width: 45vh;
  box-sizing: border-box;
  padding-top: 1%;
  cursor: pointer;

  /* Media query para ajustar el ancho en dispositivos móviles */
//     /* Media query para ajustar el ancho en dispositivos móviles */
    @media screen and (max-width: 600px) {
        height: 52vh;
        width: 100%;
        margin-top: 1px;
        padding: auto;

    }
}

.centered-image {
  /* Para centrar la imagen */
  display: block;
  margin: 0 auto;
}



.pagination-container {
  text-align: center;
}
</style>