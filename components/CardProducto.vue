<template>
  <div style="padding-bottom: 6%; overflow: auto;" class="contenedor">
    <q-pagination v-model="paginaActual" :max="totalPaginas" direction-links boundary-links />
    <!-- Itero sobre cada producto en la página actual -->
    <div id="contenedor-items" class="flex flex-center">
      <div v-for="producto in productosPaginados" class="card-container w-100">

        <q-card class="card q-mx-auto q-sm-w-50 q-md-w-50 q-lg-w-33" flat bordered @click="goTo(producto)">
          <!-- Imagen del producto -->
          <q-img :src="getImagenURL(producto.imagenProducto)" class="q-ma-md centered-image"
            style="max-width: 45%; height: auto;" @click="goTo(producto)" />

          <q-card-section>
            <!-- Nombre del producto -->
            <div>
              Nombre: <span class="text-title">{{ producto.nombreProducto }}</span>
            </div>
            <!-- Marca del producto -->
            <div class="text-subtitle1">
              Marca: {{ producto.marcaProducto }}
            </div>
            <!-- Precio del producto -->
            <div class="text-caption">
              Precio: {{ producto.precioProducto }} €
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
import { ref, defineProps, onBeforeMount, computed } from 'vue';
import { productoComposable } from '~/composables/productoComposable';
import { getImagenURL } from '~/utils/imagenURL.js';


// El usuario es el de las stores
const { listarProductos, productos, buscarProductoPorEspecificacion } = productoComposable();

//USAR QUASAR
const quasar = useQuasar()
const emits = defineEmits(['loaded', 'reloaded'])
const router = useRouter()

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
onBeforeMount(() => {
  cargarProductos().then(() => {
    emits('loaded', true)
  });

  // console.log("Productos cargados en el componente card", productos.value);
})

function goTo(item) {
  router.push({ path: `/producto/${item._id}` })
}


// FUNCION PARA CARGAR LOS PRODUCTOS
const cargarProductos = async () => {
  try {
    const response = await listarProductos();
    console.log("RESPONSE: ", response.data);
  } catch (error) {
    // Error de red u otro error
    console.error('Error al ver el historial de pedidos', error);
    mostrarAlertaError('Error al ver el historial de pedidos', quasar);
  }
}

// // Calculo el número de productos por página
const elementosPorPagina = 16;
// const elementosPorPagina = 8;

// Calculo el total de páginas
const totalPaginas = computed(() => Math.ceil(productos.value.length / elementosPorPagina));

// Calculo los productos a mostrar en la página actual
const paginaActual = ref(1);
const productosPaginados = computed(() => {
  const indiceInicial = (paginaActual.value - 1) * elementosPorPagina;
  const indiceFinal = indiceInicial + elementosPorPagina;
  return productos.value.slice(indiceInicial, indiceFinal);
});

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
    margin: 4px;
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
  @media screen and (max-width: 600px) {
    height: 44vh;
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
