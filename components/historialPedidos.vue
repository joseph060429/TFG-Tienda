<template>
  <q-dialog v-model="verPedidos">
    {{ usuario.pedidos }}
    <q-table style="height: 70vh; width: 100%; font-family: Arial, sans-serif; border: 3px solid black;" 
      title="Historial de pedidos" flat bordered
      class="my-sticky-virtscroll-table"
      :rows="detallePedidos" 
      :columns="detalleColumns"
      :loading="loading"
      row-key="index"
      virtual-scroll
      :virtual-scroll-item-size="48"
      :virtual-scroll-sticky-size-start="48"
      :rows-per-page-options="[0]"
     />
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
  // console.log('Usuarios ==> ', usuario.value);
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

const detalleColumns = [
  { name: 'label', label: 'PEDIDOS', field: 'label', align: 'left', sortable: true },
  { name: 'value', label: 'DETALLES', field: 'value', sortable: true }
];

// Computed sirve para traerme datos calculados de la base de datos
const detallePedidos = computed(() => {
  if (usuario.value && usuario.value.pedidos) {
    return usuario.value.pedidos.map(pedido => {
      const detalles = [
        { label: 'DIRECCIÓN DE ENVÍO: ', value: pedido.direccionEnvio },
        { label: 'TIPO DE PAGO: ', value: pedido.tipoPago },
        { label: 'ESTADO DEL PEDIDO: ', value: pedido.estado },
        { label: 'FECHA DEL PEDIDO: ', value: pedido.fecha },
        { label: 'NUMERO DEL PEDIDO: ', value: pedido.numPedido },
        { label: 'PRODUCTOS PEDIDOS: ' }
      ];
      return detalles.concat(pedido.productos.map(producto => ({
        label: 'PRODUCTO',
        value: `NOMBRE: ${producto.nombre}, MARCA: ${producto.marca}, PRECIO: ${producto.precio}, CANTIDAD PEDIDA: ${producto.cantidadPedida}`
      })));
    });
  } else {
    return [];
  }
});



</script>






<!-- STYLE -->
<style lang="scss" scoped>


</style>


<!-- const detallePedido = computed(() => [
  { label: 'DIRECCIÓN DE ENVÍO: ', value: usuario.value ? usuario.value : 'Cargando...' },
  { label: 'TIPO DE PAGO: ', value: usuario.value ? usuario.value.tipoPago : 'Cargando...' },
  { label: 'ESTADO DEL PEDIDO: ', value: usuario.value ? usuario.value.estado : 'Cargando...' },
  { label: 'FECHA DEL PEDIDO: ', value: usuario.value ? usuario.value.fecha : 'Cargando...' },
  { label: 'NUMERO DEL PEDIDO: ', value: usuario.value ? usuario.value.numPedido : 'Cargando...' },
  { label: 'PRODUCTOS PEDIDOS: ' },
]);

// PARA FORMATEAR EL NOMBRE DE LOS PRODUCTOS
const detallesProductosFromateados = computed(() => {
  if (usuario.value && usuario.value.productos) {
    return usuario.value.productos.map(producto => {
      // Formatear los detalles del producto
      const formatearProducto = {
        label: 'PRODUCTO',
        value: `NOMBRE: ${producto.nombre}, MARCA: ${producto.marca}, PRECIO: ${producto.precio}, CANTIDAD PEDIDA: ${producto.cantidadPedida}`
      };
      return formatearProducto;
    });
  } else {
    return [];
  }
}); -->