<template>
  <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
  <FormularioActualizarProducto v-if="mostrarFormularioEditarProducto" v-model="mostrarFormularioEditarProducto" />
  <q-btn @click="abrirFormulario" label="Actualizar" icon="mdi-update" class="b-tn-actualizar"/>
  <EliminarProducto v-if="mostrarEliminarProducto" v-model="mostrarEliminarProducto" />
  <q-btn @click="eliminarProducto" label="Eliminar"  icon="mdi-delete" class="b-tn-eliminar" />
  <div style="overflow: auto;">
    <div class="q-pa-xs" style="width: 90%; max-height: 100vh; margin: auto">
      <!-- Sección de información del producto -->
      <q-card-section class="info-section">
        <!-- Categoría del producto-->
        <!-- Nombre del producto -->
        <div style="font-size: 1.2rem; font-weight: bold; margin-bottom: 8px;">
          {{ productoAdmin.nombreProducto }}
        </div>
        <br>
        <div class="text-subtitle1" style="margin-bottom: 4px;">
          <strong> CATEGORÍA: </strong> {{ productoAdmin.categoriaProducto }}
        </div>
        <br>
        <!-- Identificador del producto -->
        <div class="text-subtitle1" style="margin-bottom: 4px;">
          <strong> IDENTIFICADOR: </strong> {{ productoAdmin.identificador }}
        </div>
        <br>
        <!-- Disponibilidad del producto -->
        <div class="text-subtitle1" style="margin-bottom: 4px;">
          <strong> DISPONIBILIDAD: </strong> {{ productoAdmin.disponibilidadProducto ? 'SI' : 'No Disponible' }}
        </div>
        <br>
        <!-- Marca del producto-->
        <div class="text-subtitle1" style="margin-bottom: 4px;">
          <strong> MARCA: </strong> {{ productoAdmin.marcaProducto }}
        </div>
        <br>
        <!-- Nombre del producto -->
        <div class="text-subtitle1" style="margin-bottom: 4px;">
          <strong> CANTIDAD: </strong> {{ productoAdmin.cantidadProducto }}
        </div>
        <br>
        <!-- Precio del producto -->
        <div style="margin-bottom: 4px;">
          <strong> PRECIO: </strong> <span class="text-body1">{{ productoAdmin.precioProducto }} &euro;</span>
        </div>
        <br>
        <div style="margin-bottom: 4px;">
          <strong> DESCRIPCIÓN: </strong> <span class="text-body1">{{ productoAdmin.descripcionProducto }}</span>
        </div>
        <br>
        <div><strong>ESPECIFICACIONES TÉCNICAS:</strong></div>
        <br>
        <div v-html="formatearEspecificacionesTecnicas(productoAdmin.especificacionesTecnicas)"></div>
      </q-card-section>

      <!-- Sección de imagen del producto -->
      <q-card-section class="image-section" style="margin-bottom: 20px;">
        <div class="q-col-xs-12 q-col-md-6 d-flex justify-center align-center imagen-producto">
          <q-img class="centered-image" :src="getImagenURL(productoAdmin.imagenProducto)"
            style="max-width: 40%; max-height: 50vh; height: auto;" />
        </div>
      </q-card-section>
    </div>
  </div>
</template>


<script setup>
import { productoAdminComposable } from '~/composables/productoAdminComposable';
import { getImagenURL } from '~/utils/imagenURL.js';

definePageMeta({
  role: ['ADMIN']
})
//USAR QUASAR
const quasar = useQuasar()
const { productoAdmin, listarUnProductoAdmin } = productoAdminComposable();

// RUTAS
const router = useRouter()
const route = useRoute()

// Constante para coger el parametro id
const _id = route.params.detalleProductoAdmin;
// console.log("producto", productoAdmin);

const mostrarFormularioEditarProducto = ref(false);
const mostrarEliminarProducto = ref(false);
const eliminarProducto = () => {
  console.log("has presionado eliminar");
  // Abro el formulario si no esta abierto
  mostrarEliminarProducto.value = true;
};

const abrirFormulario = () => {
  // Cambia el valor de la variable para mostrar el formulario
  mostrarFormularioEditarProducto.value = true;
};


// FUNCIO PARA QUE SIEMPRE SE MANTENGA LA PAGINA DEL PRODUCTO AL INICIAR LA PAGINA
onBeforeMount(async () => {
  await listarProductoAdmin(_id);
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
const listarProductoAdmin = async () => {
  try {

    const response = await listarUnProductoAdmin(_id);
    console.log("RESPONSE: ", response.data);
  } catch (error) {
    // Error de red u otro error
    console.error('Error al ver el producto Admin', error);
    mostrarAlertaError('Error al ver el producto Admin', quasar);
  }
}

// FUNCION PARA REGRESAR, LA FLECHITA
const regresar = () => {
  const token = localStorage.getItem('token');
  // const rol = localStorage.getItem('roles')

  // console.log(rol, "roless");

  if (token) {
    // Si hay un token almacenado,a la vista del usuario
    router.push({ path: '/admin/vistaInicioAdmin' });
  } else {
    // Si no hay token almacenado, al index
    router.push({ path: '/' });
  }
};


</script>

<!-- STYLOS -->
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
.b-tn-eliminar {
  /* Posiciona el botón de forma absoluta dentro de la tarjeta */
  position: fixed;
  top: 15%;
  right: 5%;
  /* Para que esté por encima del contenido */
  z-index: 1;
  padding: 10px 20px;
  border-radius: 20px;
  background-color: #ff4757;
  color: white;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.b-tn-eliminar:hover {
  background-color:red
}

.b-tn-actualizar {
  /* Posiciona el botón de forma absoluta dentro de la tarjeta */
  position: fixed;
  top: 15%;
  right: 15%;
  /* Para que esté por encima del contenido */
  z-index: 1;
  padding: 10px 20px;
  border-radius: 20px;
  background-color: #3b8af9;
  color: white;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.b-tn-actualizar {
  background-color: #1e6fd4;
}

@media (max-width: 600px) {

  /* Estilos responsivos para dispositivos móviles */
  .b-tn-eliminar {
    top: 24.5%;
    bottom: 96%;
    margin-right: 2%;
    position: absolute;
    right: 0px;
    font-size: 13px;
  }

  .b-tn-actualizar{
    top: 20%;
    bottom: 96%;
    margin-right: 2%;
    position: absolute;
    right: 0px;
    font-size: 13px;
  }
}
</style>