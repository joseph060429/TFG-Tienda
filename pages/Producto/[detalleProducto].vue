<template>
  <div class="q-pa-md producto-container">
    <div class="q-gutter-md q-col-gutter-sm q-row">
      <div class="q-col-xs-12 q-col-md-6">
        <div class="producto-info">
          <h2 class="producto-nombre">{{ producto.nombre }}</h2>
          <div><strong>Marca:</strong> {{ producto.marca }}</div>
          <div><strong>Precio:</strong> {{ producto.precio }} €</div>
          <div><strong>Descripción:</strong> {{ producto.descripcion }}</div>
          <div><strong>Categoría:</strong> {{ producto.categoria }}</div>
          <div><strong>Especificaciones Técnicas:</strong></div>
          <div v-html="formatearEspecificacionesTecnicas(producto.especificacionesTecnicas)"></div>
        </div>
      </div>
      <div class="q-col-xs-12 q-col-md-6">
        <div class="contenedor-imagen">
          <q-img class="imagen-producto" :src="getImagenURL(producto.imagenProducto)" />
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { productoComposable } from '~/composables/productoComposable';
import { getImagenURL } from '~/utils/imagenURL.js';

//USAR QUASAR
const quasar = useQuasar()
const { producto, listarUnProducto } = productoComposable();

const route = useRoute()

// Constante para coger el parametro id
const _id = route.params.detalleProducto;

// FUNCIO PARA QUE SIEMPRE SE MANTENGA LA PAGINA DEL PRODUCTO AL INICIAR LA PAGINA
onBeforeMount(async () => {
  await listarProducto(_id);
});

const formatearEspecificacionesTecnicas = (especificaciones) => {
  // Verifico si especificaciones está vacía
  if (typeof especificaciones === 'string' && especificaciones.trim() !== '') {
    // Reemplazo todos los '\\\\' con '<br>'
    return especificaciones.replace(/\\\\/g, '<br>');
  } else {
    // En caso de que especificaciones no sea una cadena o esté vacía, devuelvo que está cargando
    return "Cargando...";
  }
};

definePageMeta({
  role: ['PUBLIC']
})



// FUNCION QUE CARGA EL PRODUCTO
const listarProducto = async () => {
  try {
    const response = await listarUnProducto(_id);
    console.log("RESPONSE: ", response.data);
  } catch (error) {
    // Error de red u otro error
    console.error('Error al ver el producto', error);
    mostrarAlertaError('Error al ver el producto', quasar);
  }
}

</script>

<style lang="scss" scoped>
// .producto-container {
//   display: flex;
//   flex-wrap: wrap;
//   width: 70%;
//   margin: 2em auto;
//   border: 1px solid #ccc;
  
// }

// .producto-info {
//   display: flex;
//   flex-direction: column;
//   border: 1px solid #ccc;
//   width: 50%;
//   padding: 2em;
// }

// .contenedor-imagen{
//   display: flex;
//   // margin-left: auto;
//   width: 50%;
//   text-align: center;
//   // margin-bottom: 10%;
//   border: 3px solid black;
//   padding: 2em;
// }

// .producto-nombre {
//   font-size: 24px;
//   margin-bottom: 10px;
// }

// .producto-detalles {
//   margin-bottom: 20px;
// }


// .q-pa-md {
//   padding: 20px;
// }

// /* Estilo para la imagen */
// .imagen-producto {
//   max-width: 100%;
//   height: auto;
// }

.imagen-producto {
  max-width: 50%;
  height: auto;
  margin: 0 auto; /* Centra la imagen horizontalmente */
}

/* Ajuste para pantallas grandes */
@media screen and (min-width: 768px) {
  .producto-container {
    max-width: 1200px;
    margin: auto;
  }
}


</style>
