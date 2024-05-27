<template>
    <EliminarUsuario v-model="mostrarEliminarUsuario" :email="emailUsuarioAeliminar" />
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <div class="q-pa-md">
        <!-- :rows-per-page-options="[50]"  Esto hace que me muestre de 50 en 50 pedidos -->
        <h5 class="titulo">PEDIDOS DE LOS USUARIOS</h5>
        <q-table class="tabla" flat :rows="pedidos" row-key="index" virtual-scroll :virtual-scroll-item-size="48"
            :pagination="{ rowsPerPage: 50 }" :rows-per-page-options="[50]">
            <!-- Cabeceras de la tabla -->
            <template v-slot:header="props">
                <q-tr :props="props">
                    <q-th key="numPedido">Número de pedido</q-th>
                    <q-th key="fechaPedido">Fecha del pedido</q-th>
                    <q-th key="numTelefono">Número de teléfono</q-th>
                    <q-th key="direccionEnvio">Dirección de Envío</q-th>
                    <q-th key="estado">Estado</q-th>
                    <!-- <q-th key="fechaCreacion">Fecha de Creación</q-th>
                    <q-th key="fechaModificacion">Fecha de Modificación</q-th>
                    <q-th key="direccionEnvio">Direccion/es de envío</q-th> -->
                    <!-- <q-th key="direccionFacturacion">Direccion/es de facturación</q-th> -->
                </q-tr>
            </template>

            <!-- Cuerpo de la tabla -->
            <template v-slot:body="props">
                <q-tr :props="props">
                    <q-td>{{ props.row.numPedido }}</q-td>
                    <q-td>{{ props.row.fechaPedido }}</q-td>
                    <q-td>{{ props.row.numTelefono }}</q-td>
                    <q-td>{{ props.row.direccionEnvio }}</q-td>



                    <q-td>
                        {{ (props.row.estado) }}
                        <q-select v-model="estadoPedidoSeleccionado" :options="opcionesParaEstado"
                            @update:model-value="seleccionarEstado(props.row._id)"
                            style="position: absolute; right: 0; top: 30%; border: none;" append-to-body />
                    </q-td>

                </q-tr>
            </template>
        </q-table>
    </div>
</template>
<!-- SCRIPT -->
<script setup>
import { ref, defineProps, onBeforeMount, computed } from 'vue';
import { adminComposable } from '~/composables/adminComposable';



definePageMeta({
    role: ['ROLE_ADMIN']
});

// FUNCION PARA CARGAR LOS pedidos  ANTES DE QUE SE MONTE EL COMPONENTE
onBeforeMount(async () => {
    await listarTodosLosPedidos();
})

// El usuario es el de las stores
const { listarPedidos, pedidos, actualizarEstadoEnviado, actualizarEstadoDireccionErronea } = adminComposable();

//USAR QUASAR
const quasar = useQuasar()

const router = useRouter()


const regresar = () => {
    router.push({ path: '/admin/vistaInicioAdmin' })
};

// FUNCIONES
const opcionesParaEstado = [
    // { label: 'PENDIENTE_PAGO', value: 'PENDIENTE_PAGO' }, // CUANDO SE HACE EL PEDIDO PERO AUN NO HA PAGADO
    // { label: 'PENDIENTE_ENVÍO', value: 'PENDIENTE_ENVÍO' }, // CUANDO YA HA PAGADO
    { label: 'ENVIADO', value: 'ENVIADO' }, // CUANDO YA HA SIDO ENVIADO, hecho
    { label: 'PENDIENTE_CONFIRMACION_DIRECCION', value: 'PENDIENTE_CONFIRMACION_DIRECCION' }, // CUANDO EL REPARTIDOR A IDO A CASA Y LA DIRECCIÓN NO EXISTE
    // { label: 'REPROGRAMADO_PARA_ENTREGA', value: 'REPROGRAMADO_PARA_ENTREGA' }, // CUANDO EL REPARTIDOR HA IDO Y NO HUBO NADIE EN CASA
    // { label: 'ENTREGADO', value: 'ENTREGADO' },
    // { label: 'PENDIENTE_ENVÍO', value: 'PENDIENTE_ENVÍO' },
];

const estadoPedidoSeleccionado = ref('');

// FUNCION PARA ACTUALIZAR EL ROL DE UN USUARIO
// const seleccionarEstado = async (idPedido) => {
//     try {
//         const response1 = await actualizarEstadoEnviado(idPedido, estadoPedidoSeleccionado.value.value);
//         console.log("estado pedido ", estadoPedidoSeleccionado.value.value);
//         console.log("RESPONSE de actualizarEstadoEnviado: ", response1.data);

//         switch (response1.data) {
//             case 'Se actualizo correctamente el pedido a enviado':
//                 mostrarAlertaExito('Se actualizó correctamente el pedido', quasar);
//                 // setTimeout(actualizarVentana, 1600);
//                 break;
//             // case 'El pedido no tiene un estado válido para esta operación':
//                 // mostrarAlertaError('El pedido no tiene un estado válido para esta operación', quasar);
//                 // setTimeout(actualizarVentana, 1600);
//                 break;
//             case 'El pedido ya ha sido enviado':
//                 mostrarAlertaError('El pedido ya ha sido enviado', quasar);
//                 // setTimeout(actualizarVentana, 1600);
//                 break;
//             default:
//                 // Manejar otros casos si es necesario
//                 break;
//         }

//         // const response2 = await actualizarEstadoDireccionErronea(idPedido, estadoPedidoSeleccionado.value.value);
//         // console.log("estado pedido direccion erronea ", response2);

//         // switch (response2.data) {
//         //     // case 'Se actualizó correctamente el pedido a reprogramado para entrega':
//         //     //     mostrarAlertaExito('Se actualizó correctamente el pedido', quasar);
//         //         // setTimeout(actualizarVentana, 1600);
//         //         break;
//         //     // case 'El pedido no tiene un estado válido para esta operación':
//         //         // mostrarAlertaError('El pedido no tiene un estado válido para esta operaciónNNNN', quasar);
//         //         // setTimeout(actualizarVentana, 1600);
//         //         break;
//         //     case 'El pedido ya ha sido marcado como pendiente de confirmación de dirección':
//         //          mostrarAlertaError('El pedido ya ha sido marcado como pendiente de confirmación de dirección', quasar);
//         //         // setTimeout(actualizarVentana, 1600);
//         //         break;
//         //     default:
//         //         // Manejar otros casos si es necesario
//         //         break;
//         // }

//     } catch (error) {
//         // Error de red u otro error
//         console.error("Error al actualizar rol: ", error);
//     }

// }
const seleccionarEstado = async (idPedido) => {
    try {
        // const response1 = await actualizarEstadoEnviado(idPedido, estadoPedidoSeleccionado.value.value);
        // console.log("estado pedido ", estadoPedidoSeleccionado.value.value);
        // console.log("RESPONSE de actualizarEstadoEnviado: ", response1.data);

        // if (response1.data === 'Se actualizo correctamente el pedido a enviado') {
        //     mostrarAlertaExito('Se actualizó correctamente el pedido', quasar);
        //     // setTimeout(actualizarVentana, 1600);
        // } else if (response1.data === 'El pedido ya ha sido enviado') {
        //     mostrarAlertaError('El pedido ya ha sido enviado', quasar);
        //     // setTimeout(actualizarVentana, 1600);
        // } else {
        //     // Manejar otros casos si es necesario
        // }

        const response2 = await actualizarEstadoDireccionErronea(idPedido, estadoPedidoSeleccionado.value.value);
        console.log("estado pedido direccion erronea ", response2);

        if (response2.data === 'Se actualizó correctamente el pedido a pendiente de confirmación de dirección') {
            mostrarAlertaExito('Se actualizó correctamente el pedido', quasar);
            // setTimeout(actualizarVentana, 1600);
        } else if (response2.data === 'El pedido ya ha sido marcado como pendiente de confirmación de dirección') {
            mostrarAlertaError('El pedido ya ha sido marcado como pendiente de confirmación de dirección', quasar);
            // setTimeout(actualizarVentana, 1600);
        } else {
            // Manejar otros casos si es necesario
        }

    } catch (error) {
        // Error de red u otro error
        console.error("Error al actualizar rol: ", error);
    }
}

const actualizarVentana = () => {
    window.location.reload();
};


// FUNCION PARA CARGAR LOS pedidos
const listarTodosLosPedidos = async () => {
    try {
        const response = await listarPedidos();
        console.log("RESPONSE de listarPedidos ", response.data);
    } catch (error) {
        // Error de red u otro error
        console.error('Error al ver todos los pedidos', error);
        mostrarAlertaError('Error al ver todos los pedidos', quasar);
    }
}
















</script>



<!-- STYLE -->
<style lang="scss" scoped>
.tabla {
    height: 55vh;
    width: 100%;
    background-color: #A9A9A9;
    font-family: Arial, sans-serif;
    border: 3px solid black;
    border-collapse: collapse;
}

@media (max-width: 600px) {
    .tabla {
        width: 100%;
        margin: 0 auto;
        height: 50vh;
        font-size: 0.8em;
    }
}

.tabla th {
    font-weight: bold;
    font-size: 1.1em;
    text-align: center;
}

.tabla td {
    text-align: center;
}

.titulo {
    font-size: 2em;
    text-align: center;
    margin: 1em;
    color: #546e7a;
    font-weight: bold;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.custom-regresar-button {
    margin-top: 0.1em;
}

.boton-perfil {
    background-color: #4169E1;
    margin-right: 8px;
    border: 1px solid;
}

.boton-borrar {
    background-color: #FF4500;
    border: 1px solid;

}

.boton-perfil:hover {
    background-color: rgba(65, 105, 225, 0.8);

}

.boton-borrar:hover {
    background-color: rgba(255, 69, 0, 0.8);

}



.boton-perfil,
.boton-borrar {
    font-size: 0.8em;
    padding: 5px 10px;
    margin: 0 5px;
}



@media only screen and (max-width: 600px) {

    .boton-perfil,
    .boton-borrar {
        font-size: 14px;
        padding: 3px 6px;
    }
}
</style>
