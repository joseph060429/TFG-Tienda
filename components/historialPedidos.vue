<template>
  <q-dialog v-model="verPedidos">
    <q-table style="height: 70vh; width: 100%; background-color: #808080;  font-family: Arial, sans-serif; border: 3px solid black;"
      title="Historial de pedidos" title-tag="h2" flat bordered class="my-sticky-virtscroll-table" :rows="detallePedidosFlat"
      row-key="index"
      virtual-scroll :virtual-scroll-item-size="48" :virtual-scroll-sticky-size-start="48"
      :rows-per-page-options="[0]" />
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
const detalleColumns = [
  { name: 'direccionEnvio', label: 'Dirección de Envío', align: 'left', sortable: true },
  { name: 'tipoPago', label: 'Tipo de Pago', align: 'left', sortable: true },
  { name: 'estado', label: 'Estado del Pedido', align: 'left', sortable: true },
  { name: 'fecha', label: 'Fecha del Pedido', align: 'left', sortable: true },
  { name: 'numPedido', label: 'Número del Pedido', align: 'left', sortable: true },
  { name: 'productos', label: 'Productos Pedidos', align: 'left' }
];

const detallePedidos = computed(() => {
  if (usuario.value && usuario.value.pedidos) {
    return usuario.value.pedidos.map(pedido => ({
      direccionEnvio: pedido.direccionEnvio,
      tipoPago: pedido.tipoPago,
      estado: pedido.estado,
      fecha: pedido.fecha,
      numPedido: pedido.numPedido,
      productos: pedido.productos.map(producto => ({
        nombre: producto.nombre,
        marca: producto.marca,
        precio: producto.precio,
        cantidadPedida: producto.cantidadPedida
      }))
    }));
  } else {
    return [];
  }
});

const detallePedidosFlat = computed(() => {
  return detallePedidos.value.flatMap(pedido => ({
    ...pedido,
    productos: pedido.productos.map(producto => ` PRODUCTO: (NOMBRE: ${producto.nombre} - MARCA: ${producto.marca} - CANTIDAD: ${producto.cantidadPedida} - PRECIO: ${producto.precio}) `).join('')
  }));
});



</script>






<!-- STYLE -->
<style lang="scss" scoped></style>


<!-- // const detalleColumns = [
//   { name: 'label', label: 'PEDIDOS', field: 'label', align: 'left', sortable: true },
//   { name: 'value', label: 'DETALLES', field: 'value', sortable: true }
// ];
// const pedidos = ref(await historialDePedidos())

// Computed sirve para traerme datos calculados de la base de datos
// const detallePedidos = computed(() => {
//   if (usuario.value && usuario.value.pedidos) {
//     return usuario.value.pedidos.map(pedido => {
//       const detalles = [
//         { label: 'DIRECCIÓN DE ENVÍO: ', value: pedido.direccionEnvio },
//         { label: 'TIPO DE PAGO: ', value: pedido.tipoPago },
//         { label: 'ESTADO DEL PEDIDO: ', value: pedido.estado },
//         { label: 'FECHA DEL PEDIDO: ', value: pedido.fecha },
//         { label: 'NUMERO DEL PEDIDO: ', value: pedido.numPedido },
//         { label: 'PRODUCTOS PEDIDOS: ' }
//       ];
//       return detalles.concat(pedido.productos.map(producto => ({
//         label: 'PRODUCTO',
//         value: `NOMBRE: ${producto.nombre}, MARCA: ${producto.marca}, PRECIO: ${producto.precio}, CANTIDAD PEDIDA: ${producto.cantidadPedida}`
//       })));
//     });
//   } else {
//     return [];
//   }
// }); -->



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