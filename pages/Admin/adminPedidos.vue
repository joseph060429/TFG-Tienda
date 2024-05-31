<template>
    <!-- <EliminarUsuario v-model="mostrarEliminarUsuario" :email="emailUsuarioAeliminar" /> -->

    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <div class="filtrado">
        <filtrado-estado-pedido @estado="(x) => estado = x"></filtrado-estado-pedido>
    </div>
    <div class="tabla-container">
        <!-- :rows-per-page-options="[50]"  Esto hace que me muestre de 50 en 50 pedidos -->
        <h5 class="titulo">PEDIDOS DE LOS USUARIOS</h5>
        <q-table class="tabla" flat :rows="pedidosFiltrados" row-key="index" virtual-scroll
            :virtual-scroll-item-size="48" :pagination="{ rowsPerPage: 50 }" :rows-per-page-options="[50]"
            style="overflow-x: auto;">
            <!-- Cabeceras de la tabla -->
            <template v-slot:header="props">
                <q-tr :props="props">
                    <q-th key="numPedido">Número de pedido</q-th>
                    <!-- <q-th key="fechaPedido">Fecha de envío</q-th> -->
                    <q-th key="numTelefono">Número de teléfono</q-th>
                    <q-th key="direccionEnvio">Dirección de Envío</q-th>
                    <q-th key="email">Email</q-th>
                    <q-th key="productos">Productos</q-th>
                    <q-th key="estado">Estado</q-th>
                </q-tr>
            </template>

            <!-- Cuerpo de la tabla -->
            <template v-slot:body="props">
                <q-tr :props="props">
                    <q-td>{{ props.row.numPedido }}</q-td>
                    <!-- <q-td>{{ props.row.fechaEnvio}}</q-td> -->
                    <q-td>{{ props.row.numTelefono }}</q-td>
                    <q-td>{{ props.row.direccionEnvio }}</q-td>
                    <q-td> {{ props.row.datosUsuarioPedidoDTO.email }}</q-td>
                    <q-td>
                        <div v-for="(producto, index) in props.row.productos" :key="index" style="text-align: left;">
                            &bull; {{ producto.categoria }}
                            {{ producto.nombre }}
                        </div>
                    </q-td>
                    <q-td>

                        <q-select class="select-estado" v-model="props.row.estado" :options="opcionesParaEstado"
                            @update:model-value="seleccionarEstado(props.row, props.row.estado)" />

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

const estado = ref('')
const pedidosFiltrados = computed(() => {
    if (estado.value != '') {
        return pedidos.value.filter(p => p.estado == estado.value)
    }
    return pedidos.value
})

// const estadoPedidoSeleccionado = ref('');

// FUNCION PARA CARGAR LOS pedidos  ANTES DE QUE SE MONTE EL COMPONENTE
onBeforeMount(async () => {
    await listarTodosLosPedidos();
})

// El usuario es el de las stores
const { listarPedidos, pedidos, actualizarEstadoEnviado, actualizarEstadoDireccionErronea, actualizarEstadoEntregado, actualizarEstadoReproParaEntrega, enviarCorreoRetraso } = adminComposable();


//USAR QUASAR
const quasar = useQuasar()

const router = useRouter()


const regresar = () => {
    router.push({ path: '/admin/vistaInicioAdmin' })
};

// FUNCIONES
const opcionesParaEstado = [
    'ENVIADO',
    'PENDIENTE_CONFIRMACION_DIRECCION',
    'ENTREGADO',
    "REPROGRAMADO_PARA_ENTREGA",
    "RETRASO"
];

// FUNCION PARA ACTUALIZAR EL ROL DE UN USUARIO
const seleccionarEstado = async (idPedido, estado) => {
    try {
        switch (estado) {
            case 'ENVIADO':
                const response1 = await actualizarEstadoEnviado(idPedido._id, idPedido.estado);
                console.log("RESPONSE de actualizarEstadoEnviado: ", response1.data);

                switch (response1.data) {
                    case 'Se actualizo correctamente el pedido a enviado':
                        mostrarAlertaExito('Se actualizó correctamente el pedido', quasar);
                        break;
                    case 'El pedido ya ha sido enviado':
                        mostrarAlertaError('El pedido ya ha sido enviado', quasar);
                        break;
                    default:
                        console.warn('Respuesta desconocida de actualizarEstadoEnviado:', response1.data);
                }
                break;
            case 'PENDIENTE_CONFIRMACION_DIRECCION':
                const response2 = await actualizarEstadoDireccionErronea(idPedido._id, idPedido.estado);
                console.log("estado pedido direccion erronea ", response2.data);

                switch (response2.data) {
                    case 'Se actualizó correctamente el pedido a pendiente de confirmación de dirección':
                        mostrarAlertaExito('Se actualizó correctamente el pedido', quasar);
                        break;
                    case 'El pedido ya ha sido marcado como pendiente de confirmación de dirección':
                        mostrarAlertaError('El pedido ya ha sido marcado como pendiente de confirmación de dirección', quasar);
                        break;
                    default:
                        console.warn('Respuesta desconocida de actualizarEstadoDireccionErronea:', response2.data);
                }
                break;

            case 'ENTREGADO':
                const response3 = await actualizarEstadoEntregado(idPedido._id, idPedido.estado);
                console.log("estado pedido entregado ", response3.data);

                switch (response3.data) {
                    case 'Se actualizo correctamente el pedido a entregado':
                        mostrarAlertaExito('Se actualizó correctamente el pedido', quasar);
                        break;
                    case 'El pedido ya ha sido marcado como entregado':
                        mostrarAlertaError('El pedido ya ha sido marcado como entregado', quasar);
                        break;
                    default:
                        console.warn('Respuesta desconocida de actualizarEstadoEntregado:', response3.data);
                }
                break;

            case 'REPROGRAMADO_PARA_ENTREGA':
                const response4 = await actualizarEstadoReproParaEntrega(idPedido._id, idPedido.estado);
                console.log("estado pedido reprogramado para entrega ", response4.data);
                switch (response4.data) {
                    case 'Se actualizó correctamente el pedido a reprogramado para entrega':
                        mostrarAlertaExito('Se actualizó correctamente el pedido', quasar);
                        break;
                    case 'El pedido ya ha sido marcado como reprogramado para entrega':
                        mostrarAlertaError('El pedido ya ha sido marcado como reprogramado para entrega', quasar);
                        break;
                    default:
                        console.warn('Respuesta desconocida de actualizarEstadoReproParaEntrega:', response3.data);
                }

            case 'RETRASO':
                const response5 = await enviarCorreoRetraso(idPedido._id, idPedido.estado);
                console.log("RESPONSE de actualizarEstadoEnviado: ", response5.data);

                switch (response5.data) {
                    case 'Se actualizó correctamente el pedido a retrasado':
                        mostrarAlertaExito('Se actualizó correctamente el pedido', quasar);
                        break;
                    case 'El pedido ya ha sido marcado como retrasado':
                        mostrarAlertaError('El pedido ya ha sido marcado como retrasado', quasar);
                        break;
                    default:
                        console.warn('Respuesta desconocida de actualizarEstadoEnviado:', response5.data);
                }
                break;
            default:
                break;
        }



    } catch (error) {
        console.error("Error al actualizar rol: ", error);
        mostrarAlertaError('Hubo un error al actualizar el pedido', quasar);
    }
}

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
.tabla-container {
    width: 99%;
    overflow-x: auto;
    margin: auto;
}



.tabla {
    height: 45vh;
    background-color: #A9A9A9;
    font-family: Arial, sans-serif;
    border: 3px solid black;
}

@media (max-width: 600px) {
    .tabla {
        width: 98%;
        height: 40vh;
        margin: auto;
        background-color: #A9A9A9;
        font-family: Arial, sans-serif;
    }
}

.tabla th {
    font-weight: bold;
    font-size: 1.1em;
    text-align: center;
}

.tabla td {
    text-align: center;
    height: 20vh;

    &:nth-child(1) {
        width: 5%;
    }

    &:nth-child(2) {
        width: 5%;
    }

    &:nth-child(3) {
        width: 5%;
    }

    &:nth-child(4) {
        width: 25%;
    }

    &:nth-child(5) {
        width: 15%;
        height: 60%;
    }

    &:nth-child(6) {
        width: 10%;
        height: 0%;
    }


}

@media (max-width: 600px) {
    .tabla td {

        text-align: center;
        height: 20vh;

        &:nth-child(1) {
            width: 10%;
        }

        &:nth-child(2) {
            width: 20%;
        }

        &:nth-child(3) {
            width: 5%;
        }

        &:nth-child(4) {
            width: 10%;
        }

        &:nth-child(5) {
            width: 25%;
        }


        &:nth-child(6) {
            width: 20%;
        }

    }
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

.select-estado {
    right: 0;
    top: 0%;
    border: none;
    width: 100%;
    font-size: 1em;
    margin-top: 1em;

}
</style>
