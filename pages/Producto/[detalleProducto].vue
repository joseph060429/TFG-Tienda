<template>
  <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
  <div style="overflow: auto;">
    <div class="q-pa-xs" style="width: 90%; max-height: 100vh; margin: auto; border: none !important;">

      <!-- Sección de información del producto -->
      <q-card-section class="info-section">
        <!-- Nombre del producto -->
        <div style="font-size: 1.2rem; font-weight: bold; margin-bottom: 8px;">
          {{ producto.nombre }}
        </div>
        <br>
        <!-- Marca del producto -->
        <div class="text-subtitle1" style="margin-bottom: 4px;">
          <strong> MARCA: </strong> {{ producto.marca }}
        </div>
        <br>
        <!-- Precio del producto -->
        <div style="margin-bottom: 4px;">
          <strong> PRECIO: </strong> <span class="text-body1">{{ producto.precio }} &euro;</span>
        </div>
        <br>
        <div style="margin-bottom: 4px;">
          <strong> DESCRIPCIÓN: </strong> <span class="text-body1">{{ producto.descripcion }}</span>
        </div>
        <br>
        <div><strong>ESPECIFICACIONES TÉCNICAS:</strong></div>
        <br>
        <div v-html="formatearEspecificacionesTecnicas(producto.especificacionesTecnicas)"></div>
      </q-card-section>

      <!-- Sección de imagen del producto -->
      <q-card-section class="image-section" style="margin-bottom: 20px;">
        <div class="q-col-xs-12 q-col-md-6 d-flex justify-center align-center imagen-producto">
          <q-img class="centered-image" :src="getImagenURL(producto.imagenProducto)"
            style="max-width: 40%; max-height: 50vh; height: auto;" />
        </div>
      </q-card-section>

    </div>
  </div>
</template>


<script setup>
import { productoComposable } from '~/composables/productoComposable';
import { getImagenURL } from '~/utils/imagenURL.js';

definePageMeta({
  role: ['PUBLIC']
})
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
  // Verificar si especificaciones está vacía
  if (typeof especificaciones === 'string' && especificaciones.trim() !== '') {
    // Dividir las especificaciones en líneas y agregar puntos al principio de cada línea
    return especificaciones.trim().split('\n').map(linea => {
      // Eliminar todos los caracteres '\' al final de la línea
      linea = linea.replace(/\\+$/, '');
      return `<div style="text-align: left;"><span style="display: inline-block; width: 1em; text-align: center;">•</span>${linea}</div>`;
    }).join('<br>');
  } else {
    // Devolver un mensaje de carga si las especificaciones están vacías
    return "Cargando...";
  }
};





// RUTAS
const router = useRouter()

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

const regresar = () => {
  const token = localStorage.getItem('token');

  if (token) {
    // Si hay un token almacenado,a la vista del usuario
    router.push({ path: '/usuario/vistaInicioUsuario' });
  } else {
    // Si no hay token almacenado, al index
    router.push({ path: '/' });
  }
};

</script>

<style lang="scss" scoped>
.imagen-producto {
  max-width: 50%;
  height: auto;
  margin: 0 auto;
  border: 100px;
  /* Centra la imagen horizontalmente */
}


.image-section {
  padding-bottom: 15%;
  /* Ajusta el padding según sea necesario */
}

.centered-image {
  /* Para centrar la imagen */
  display: block;
  margin: 0 auto;
}


@media only screen and (max-width: 600px) {
  .info-section {
    padding: 5px;
    /* Ajusta el padding según sea necesario para móviles */
  }

  .image-section {
    padding-bottom: 30%;
    /* Ajusta el padding según sea necesario para móviles */
  }

  .imagen-producto {
    max-width: 100%;
    /* Ajusta el ancho máximo de la imagen */
  }
}
</style>
