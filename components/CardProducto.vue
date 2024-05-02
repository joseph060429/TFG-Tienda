<template>
  <div class="q-pa-md row items-start q-gutter-md">
    <!-- Itero sobre cada producto -->
    <div v-for="producto in productos">
      <q-card class="card q-mx-auto q-sm-w-75 q-md-w-50 q-lg-w-33" flat bordered>
        <!-- Imagen del producto -->
        <q-img :src="getImagenURL(producto.imagenProducto)" class="q-ma-md" style="max-width: 150px;" />

        <q-card-section>
          <!-- Nombre del producto -->
          <div class="row no-wrap items-center">
            <div class="col text-h6 ellipsis">
              {{ producto.nombre }}
            </div>
          </div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <!-- Marca del producto -->
          <div class="text-subtitle1">
            {{ producto.marca }}
          </div>
          <!-- Precio del producto -->
          <div class="text-caption">
            {{ producto.precio }}
          </div>
        </q-card-section>

        <q-separator />
      </q-card>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, onBeforeMount, computed } from 'vue';
import { productoComposable } from '~/composables/productoComposable';

// El usuario es el de las stores
const { listarProductos, productos } = productoComposable();

//USAR QUASAR
const quasar = useQuasar()

const router = useRouter()


// Props
const nombreProducto = 'Nombre del producto';
const marcaProducto = 'Marca del producto';
const precioProducto = 'Precio';

onBeforeMount(async () => {
  await cargarProductos();
  console.log("Productos cargados en el compornente card", productos.value);
})

const cargarProductos = async () => {
  try {
    const response = await  listarProductos();
    console.log("RESPONSE: ", response.data);
  } catch (error) {
    // Error de red u otro error
    console.error('Error al ver el historial de pedidos', error);
    mostrarAlertaError('Error al ver el historial de pedidos', quasar);
  }
}


const getImagenURL = (imagenProducto) => {
  return `https://firebasestorage.googleapis.com/v0/b/proyecto-ionic-tienda.appspot.com/o/Imagenes-Productos%2F${imagenProducto}?alt=media`;
};










</script>

<style lang="scss" scoped>
/* Custom styles */
.text-h6 {
  font-size: 1.25rem; /* Tamaño de fuente más grande para el nombre del producto */
}

.text-subtitle1 {
  font-size: 1rem; /* Tamaño de fuente para la marca del producto */
  color: #333; /* Color del texto más oscuro */
}

.text-caption {
  font-size: 0.875rem; /* Tamaño de fuente para el precio del producto */
  color: #666; /* Color de texto gris */
}
</style>
