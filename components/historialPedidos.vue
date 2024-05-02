<template>
  <q-dialog v-model="verPedidos">
    <div class="q-pa-md">
      <q-table class="my-sticky-header-table" flat bordered title="Historial de pedidos" title-tag="h2"
        :rows="detallePedidosFlat" row-key="index" virtual-scroll :virtual-scroll-item-size="48"
        :virtual-scroll-sticky-size-start="48" :rows-per-page-options="[0]">
        <template v-slot:top-right>
          <q-btn color="negative" icon="mdi-cancel" label="Cancelar pedidos" class="q-mb-md" @click="cancelarPedidos" />
        </template>
      </q-table>
    </div>
  </q-dialog>
</template>

<!-- SCRIPT -->
<script setup>
import { ref, defineProps, onBeforeMount, computed } from 'vue';
import { usuarioComposable } from '~/composables/usuarioComposable';

// El usuario es el de las stores
const { historialPedidos, usuario } = usuarioComposable();

//USAR QUASAR
const quasar = useQuasar()

const router = useRouter()

const props = defineProps({
  mostrarLosPedidos: Boolean
})

const verPedidos = ref(props.mostrarLosPedidos)


// FUNCIONES
// FUNCION PARA TRAER LOS PEDIDOS DEL USUARIO 
onBeforeMount(async () => {
  await historialDePedidos();
  console.log("detalles pedidos", detallePedidos.value);
})
const historialDePedidos = async () => {
  try {
    const response = await historialPedidos();
    console.log("RESPONSE: ", response.data);
  } catch (error) {
    // Error de red u otro error
    console.error('Error al ver el historial de pedidos', error);
    mostrarAlertaError('Error al ver el historial de pedidos', quasar);
  }
}

// FUNCION PARA VER LOS DETALLES DE LOS PEDIDOS
const detallePedidos = computed(() => {
  // Verifico si soy un usuario y si hay pedidos
  if (usuario.value && usuario.value.pedidos) {
    // Recorro cada pedido que tengo y creo un nuevo objeto con sus detalles
    return usuario.value.pedidos.map(pedido => ({
      // Asigno la dirección de envío del pedido.
      direccionEnvio: pedido.direccionEnvio,
      // Asigno el tipo de pago del pedido
      tipoPago: pedido.tipoPago,
      // Asigno el estado del pedido
      estado: pedido.estado,
      // Asigno la fecha del pedido
      fecha: pedido.fecha,
      // Asigno el número de pedido
      numPedido: pedido.numPedido,
      // Recorro cada producto en el pedido y creo un nuevo objeto con sus detalles
      productos: pedido.productos.map(producto => ({
        // Asigno el nombre del producto
        nombre: producto.nombre,
        // Asigno la marca del producto
        marca: producto.marca,
        // Asigno el precio del producto
        precio: producto.precio,
        // Asigno la cantidad pedida del producto
        cantidadPedida: producto.cantidadPedida
      }))
    }));
  } else {
    return [];
  }
});

// FUNCION PARA JUNTAR LOS PEDIDOS EN FORMATO DE TEXTO PLANO
const detallePedidosFlat = computed(() => {
  // Utilizo la función flatMap para recorrer cada pedido y sus productos, y luego poner en plano el resultado
  return detallePedidos.value.flatMap(pedido => ({
    // Creo un nuevo objeto que contiene los detalles del pedido
    ...pedido,
    // Modifico la propiedad 'productos' del pedido para que sea una cadena plana que contenga detalles de cada producto
    productos: pedido.productos.map(producto => ` PRODUCTO: (NOMBRE: ${producto.nombre} - MARCA: ${producto.marca} - CANTIDAD: ${producto.cantidadPedida} - PRECIO: ${producto.precio}) `).join('')
  }));
});



</script>



<!-- STYLE -->
<style lang="scss" scoped>
.my-sticky-header-table {
  height: 50vh;
  width: 100%;
  background-color: #A9A9A9;
  font-family: Arial, sans-serif;
  border: 3px solid black;
}
</style>
