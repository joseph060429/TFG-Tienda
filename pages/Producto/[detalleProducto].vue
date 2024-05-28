<template>
  <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
  <!-- Botón de añadir al carrito -->
  <q-btn @click="agregarAlCarrito" label="Añadir al carrito" icon="mdi-cart-plus" class="btn-anadir-carrito" />
  <div style="overflow: auto;">
    <div class="q-pa-xs" style="width: 90%; max-height: 100vh; margin: auto">

      <!-- Sección de información del producto -->
      <q-card-section class="info-section">
        <!-- Nombre del producto -->
        <div style="font-size: 1.2rem; font-weight: bold; margin-bottom: 8px;">
          {{ producto.nombreProducto }}
        </div>
        <br>
        <!-- Marca del producto -->
        <div class="text-subtitle1" style="margin-bottom: 4px;">
          <strong> MARCA: </strong> {{ producto.marcaProducto }}
        </div>
        <br>
        <!-- Precio del producto -->
        <div style="margin-bottom: 4px;">
          <strong> PRECIO: </strong> <span class="text-body1">{{ producto.precioProducto }} &euro;</span>
        </div>
        <br>
        <div style="margin-bottom: 4px;">
          <strong> DESCRIPCIÓN: </strong> <span class="text-body1">{{ producto.descripcionProducto }}</span>
        </div>
        <br>
        <!-- Disponibilidad del producto -->
        <div class="text-subtitle1" style="margin-bottom: 4px;">
          <strong> DISPONIBILIDAD: </strong> {{ producto.disponibilidadProducto ? 'SI' : 'No Disponible' }}
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
import { usuarioComposable } from '~/composables/usuarioComposable';
import { getImagenURL } from '~/utils/imagenURL.js';

definePageMeta({
  role: ['PUBLIC']
})
//USAR QUASAR
const quasar = useQuasar()
const { producto, listarUnProducto } = productoComposable();

const { anadirAlCarrito } = usuarioComposable();



// RUTAS
const router = useRouter()
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
    // Divido las especificaciones en líneas y agrego puntos al principio de cada línea
    return especificaciones.trim().split('\n').map(linea => {
      // Elimino todos los caracteres '\' al final de la línea
      linea = linea.replace(/\\+$/, '');
      return `<div style="text-align: left;"><span style="display: inline-block; width: 1em; text-align: center;">•</span>${linea}</div>`;
    }).join('<br>');
  } else {
    // Devuelvo un mensaje de carga si las especificaciones están vacías
    return "Cargando...";
  }
};

// FUNCION QUE CARGA EL PRODUCTO
const listarProducto = async () => {
  try {
    const response = await listarUnProducto(_id);
    // console.log("_id producto", _id);
    console.log("RESPONSE: ", response.data);


  } catch (error) {
    // Error de red u otro error
    console.error('Error al ver el producto', error);
    mostrarAlertaError('Error al ver el producto', quasar);
  }
}

// FUNCION PARA REGRESAR, LA FLECHITA
const regresar = () => {
  const token = localStorage.getItem('token');
  const rol = localStorage.getItem('roles')

  if (token && rol === 'ROLE_USER') {
    // Si hay un token almacenado,a la vista del usuario
    router.push({ path: '/usuario/vistaInicioUsuario' });
  } else {
    // Si no hay token almacenado, al index
    router.push({ path: '/' });
  }
};

const cantidad = ref(1);
// FUNCIÓN PARA AÑADIR PPRODUCTOS AL CARRITO
const agregarAlCarrito = async () => {

  const response = await anadirAlCarrito(_id, cantidad.value);

  console.log("response", response);

  if (response.status === 200) {
    mostrarAlertaExito('Producto agregado al carrito', quasar);
  }
  if (response.status === 400) {
    mostrarAlertaError('El producto ya está en el carrito', quasar);
  }

  if (response.status === 403) {
    mostrarAlertaError('No tienes permiso para realizar esta operación, por favor inicia sesión o regístrate', quasar);
    router.push({ path: '/' })
  }

}


</script>

<!-- STYLOS -->
<style lang="scss" scoped>
.imagen-producto {
  max-width: 50%;
  height: auto;
  margin: 0 auto;
  border: 100px;
}


.image-section {
  padding-bottom: 15%;
}

.centered-image {
  /* Para centrar la imagen */
  display: block;
  margin: 0 auto;
}

// // El boton del carrito se pone abajo del todo
@media only screen and (max-width: 600px) {
  .info-section {
    padding: 5px;

  }

  .image-section {
    padding-bottom: 30%;
    /* Ajusta el padding según sea necesario para móviles */
  }

  .imagen-producto {
    /* Ajusta el ancho máximo de la imagen */
    max-width: 100%;
  }
}

/* Asegura que el botón esté posicionado correctamente */
.q-card {
  position: relative;
}

/* Estilos para el botón "Añadir al carrito" */
.btn-anadir-carrito {
  /* Posiciona el botón de forma absoluta dentro de la tarjeta */
  position: fixed;
  top: 15%;
  right: 5%;
  /* Para que esté por encima del contenido */
  z-index: 1;
  padding: 10px 20px;
  border-radius: 20px;
  background-color: #6c757d;
  color: white;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-anadir-carrito:hover {
  background-color: #3b8af9;
}

@media (max-width: 600px) {

  /* Estilos responsivos para dispositivos móviles */
  .btn-anadir-carrito {
    top: 11%;
    bottom: 96%;
    margin-right: 2%;
    position: absolute;
    right: 0px;
    font-size: 13px;
  }
}
</style>
