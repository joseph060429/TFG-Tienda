<template>
    <!-- <EliminarUsuario v-model="mostrarEliminarUsuario" :email="emailUsuarioAeliminar" /> -->
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
                    <q-th key="email">Email</q-th>
                    <q-th key="estado">Estado</q-th>
                </q-tr>
            </template>

            <!-- Cuerpo de la tabla -->
            <template v-slot:body="props">
                <q-tr :props="props">
                    <q-td>{{ props.row.numPedido }}</q-td>
                    <q-td>{{ props.row.fechaPedido }}</q-td>
                    <q-td>{{ props.row.numTelefono }}</q-td>
                    <q-td>{{ props.row.direccionEnvio }}</q-td>
                    <q-td> {{ props.row.datosUsuarioPedidoDTO.email }}</q-td>
                    <q-td>
                        <q-select class="select-estado" v-model="props.row.estado" :options="opcionesParaEstado"
                            @update:model-value="seleccionarEstado(props.row)" />
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


// const estadoPedidoSeleccionado = ref('');

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
    'ENVIADO',
    'PENDIENTE_CONFIRMACION_DIRECCION'
];



// FUNCION PARA ACTUALIZAR EL ROL DE UN USUARIO
const seleccionarEstado = async (idPedido) => {
    try {
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

    &:nth-child(1) {
        width: 10%;
    }

    &:nth-child(2) {
        width: 10%;
    }

    &:nth-child(3) {
        width: 10%;
    }

    &:nth-child(4) {
        width: 20%;
    }

    &:nth-child(5) {
        width: 15%;
    }

    &:nth-child(6) {
        width: 20%;
    }

}

@media (max-width: 600px) {
    .tabla td {

        &:nth-child(1) {
            height: 4px;
            // background-color: red;
        }


        &:nth-child(2) {
            width: 10%;
        }

        &:nth-child(3) {
            width: 10%;
        }

        &:nth-child(4) {
            width: 20%;
        }

        &:nth-child(5) {
            width: 15%;
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
    position: absolute;
    right: 0;
    top: 0%;
    border: none;
    width: 100%;
    overflow: hidden;
    font-size: 1em;
    text-align: center;

}
</style>
