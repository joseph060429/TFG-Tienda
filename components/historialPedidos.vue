<template>
  <!-- :rows-per-page-options="[10]">  PARA QUE ME MUESTRE EL HISTORIAL DE PEDIDOS DE 10 EN 10-->
  <q-dialog v-model="verPedidos">
    <div class="q-pa-md">
      <q-table class="tabla" flat bordered title="Historial de pedidos" title-tag="h2" :rows="pedidos"
        row-key="index" virtual-scroll :virtual-scroll-item-size="48" :virtual-scroll-sticky-size-start="48"
        :pagination="{ rowsPerPage: 50 }" :rows-per-page-options="[50]">
        <template v-slot:top-right>
          <!-- Botones cancelar y devolver  pedidos -->
          <div class="botones-container">
            <!-- Boton para abirl el dialogo de eliminar pedido -->
            <!-- Botón para cancelar pedidos -->
            <q-btn color="negative" icon="mdi-cancel" label="Cancelar pedidos" class="q-mb-md boton-cancelar"
              @click="abrirDialogoEliminar" />

            <!-- Botón para abrir el diálogo de devolución de pedidos -->
            <!-- <q-btn color="negative" icon="mdi-undo" label="Devolución de pedidos" class="q-mb-md boton-devolucion"
              @click="abrirFormularioDevolverPedido" /> -->
          </div>
        </template>

        <template v-slot:header="props">
          <q-tr :props="props">
            <q-th key="fechaPedido">Fecha del pedido</q-th>
            <q-th key="fechaEnvio">Fecha de envío</q-th>
            <q-th key="numPedido">Numero de pedido</q-th>
            <q-th key="productos">Productos pedidos</q-th>
            <q-th key="estado">Estado del pedido</q-th>
            <q-th key="direccionEnvio">Direccion/es de envío</q-th>

          </q-tr>
        </template>
        <template v-slot:body="props">
          <q-tr :props="props">
            <q-td>{{ props.row.fechaPedido }}</q-td>
            <q-td>{{ fechaEnvio(props.row.fechaEnvio) }}</q-td>
            <q-td>{{ props.row.numPedido }}</q-td>
            <q-td>
              <span v-html="productosFormateados(props.row.productos)"></span>
            </q-td>
            <q-td>{{ props.row.estado }}</q-td>
            <q-td>{{ props.row.direccionEnvio }}</q-td>

          </q-tr>
        </template>

      </q-table>
    </div>

    <!-- Diálogo para ingresar número de pedido -->
    <q-dialog v-model="mostrarDialogoEliminar">
      <div class="q-pa-md">
        <q-input v-model="numeroPedidoCancelar" label="Número de pedido" outlined type="number" color="#ffcccc"
          class="input-custom" />
        <q-card-actions align="right">
          <!-- Botón "Cancelar" en el diálogo -->
          <q-btn label="Cancelar" color="primary" @click="cerrarDialogoEliminar" />
          <!-- Botón "Eliminar pedido" en el diálogo -->
          <q-btn label="Eliminar pedido" color="negative" @click="cancelarPedido" />
        </q-card-actions>
      </div>
    </q-dialog>
  </q-dialog>
</template>

<!-- SCRIPT -->
<script setup>
import { ref, defineProps, onBeforeMount, computed } from 'vue';
import { usuarioComposable } from '~/composables/usuarioComposable';

// El usuario es el de las stores
const { historialPedidos, usuario, cancelarPedidos } = usuarioComposable();

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
  console.log("PEDIDOS", pedidos);
})

// FUNCION PARA VER EL HISTORIAL DE PEDIDOS
const pedidos = ref([]);

const historialDePedidos = async () => {
  try {
    const response = await historialPedidos();
    console.log("RESPONSE: ", response.data);
    pedidos.value = response.data;
  } catch (error) {
    // Error de red u otro error
    console.error('Error al ver el historial de pedidos', error);
    mostrarAlertaError('Error al ver el historial de pedidos', quasar);
  }
}

// Referencia para controlar la visibilidad del diálogo
const mostrarDialogoEliminar = ref(false);

// Referencia para almacenar el número de pedido a cancelar
const numeroPedidoCancelar = ref('');

// FUNCION PARA ABRIR EL DIALOGO DE CANCELAR PEDIDO
const abrirDialogoEliminar = () => {
  mostrarDialogoEliminar.value = true;
};

// FUNCION PARA CERRAR EL DIALOGO DE CANCELAR PEDIDOS
const cerrarDialogoEliminar = () => {
  mostrarDialogoEliminar.value = false;
};

// FUNCION PARA CANCELAR LOS PEDIDOS
const cancelarPedido = async () => {
  try {
    // Llamo a la función para cancelar el pedido, pasando el número de pedido
    const response = await cancelarPedidos(numeroPedidoCancelar.value);
    // Reviso el código de estado de la respuesta para determinar el mensaje a mostrar al usuario
    if (response.status === 404) {
      // Si el pedido no se encontró, muestro un mensaje de error
      mostrarAlertaError('Error al cancelar el pedido: Pedido no encontrado', quasar);
    } else if (response.status === 401) {
      // Si el usuario no tiene permiso para cancelar el pedido, muestro un mensaje de error
      mostrarAlertaError('Error al cancelar el pedido: Solo puedes cancelar tus pedidos', quasar);
    } else if (response.status === 400) {
      // Si el pedido no es cancelable por algún motivo, muestro un mensaje de error
      mostrarAlertaError('Pedido no cancelable: ya ha sido cancelado o tiene otro estado.', quasar);
    } else if (response.status === 200) {
      // Si el pedido se canceló exitosamente, muestro un mensaje de éxito
      mostrarAlertaExito('Pedido cancelado. Se ha enviado un correo electrónico y los cambios se reflejarán al iniciar sesión nuevamente.', quasar)

    }
    console.log("RESPONSE: ", response.data);
    console.log("RESPONSE: ", response.status);
  } catch (error) {
    // Error de red u otro error
    console.error('Error al cancelar pedido', error);
    // Si ocurre un error durante la solicitud, muestro un mensaje de error genérico
    mostrarAlertaError('Error al cancelar el pedido', quasar);
  }
}

const fechaEnvio = (fechaEnvio) => {
  return fechaEnvio ? fechaEnvio : 'Disponible solo cuando el estado del pedido es ENVIADO';
};


const productosFormateados = (productos) => {
  if (productos && productos.length > 0) {
    // Mapear cada producto y formatear sus propiedades
    const formattedProducts = productos.map(producto => {
      return `•Nombre: ${producto.nombre}, Marca: ${producto.marca}, Cantidad: ${producto.cantidadPedida}, Precio Unitario: ${producto.precioProducto} € <br>`;
    });
    // Unir los productos formateados con saltos de línea
    return formattedProducts.join('');
  } 
};








</script>



<!-- STYLE -->
<style lang="scss" scoped>
.tabla{
  height: 50vh;
  width: 100%;
  background-color: #A9A9A9;
  font-family: Arial, sans-serif;
  border: 3px solid black;
}

.botones-container {
  display: flex;
}


.tabla th {
  font-weight: bold;
}


.tabla td {
    text-align: center;
}


.boton-cancelar {
  margin-right: 10px;
  /* Ajusta el margen entre los botones según sea necesario */
}

@media (max-width: 600px) {

  .boton-cancelar,
  .boton-devolucion {
    font-size: 10px;
    /* Tamaño de fuente aún más pequeño en dispositivos móviles */
    padding: 3px 6px;
    /* Padding más pequeño en dispositivos móviles */
  }
}

.input-custom {
  width: 100%;
  /* Ancho completo del input */
  margin-bottom: 20px;
  /* Margen inferior */
  background-color: rgba(255, 255, 255, 0.8);
  /* Color de fondo */
}
</style>
