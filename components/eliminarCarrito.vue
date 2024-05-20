<template>
    <q-dialog v-model="dialogVisible" class="q-dialog-max-w-lg">
        <q-card class="q-dialog">
            <q-card-section class="text-center">
                <!-- <q-card-title class="text-h6" style="color: #2196F3;">Confirmación</q-card-title> -->
                <p style="color: #757575;">¿Estás seguro de que quieres eliminar el producto del carrito?</p>
            </q-card-section>
            <q-card-actions align="center" class="q-mt-lg">
                <q-btn label="Cancelar" color="primary" @click="cerrarDialogo" class="q-mr-md" v-close-popup />
                <q-btn label="Eliminar Producto" color="negative" @click="eliminarProducto" class="q-ml-md"
                    v-close-popup />
            </q-card-actions>
        </q-card>
    </q-dialog>

</template>

<script setup>

import { usuarioComposable } from '~/composables/usuarioComposable';


const { eliminarProductoCarrito, usuario } = usuarioComposable();
const quasar = useQuasar();

const props = defineProps({
    dialogVisible: Boolean,
    idCarrito: String
});
const dialogVisible = ref(props.dialogVisible);


const cerrarDialogo = () => {
    console.log("Cerrando el diálogo");
};


const eliminarProducto = async () => {
    try {
        console.log('Carrito ID a eliminar:', props.idCarrito);

        const response = await eliminarProductoCarrito(props.idCarrito);
        console.log("RESPONSE: ", response.data);
        if (response.status === 200) {
            mostrarAlertaExito('Producto eliminado del carrito correctamente', quasar);
            eliminar(props.idCarrito);

        }
    } catch (error) {
        // Error de red u otro error
        console.error('Error al ver el carrito de compras', error);
        mostrarAlertaError('Error al ver el eliminar producto del carrito de compras', quasar);
    }
};

const eliminar = (id) => {
  usuario.value.carrito.splice(usuario.value.carrito.indexOf(id), 1);
};


const refrescar = () => {
    window.location.reload();
};



</script>

<style lang="scss" scoped></style>