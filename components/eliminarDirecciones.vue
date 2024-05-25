<template>
    <q-dialog v-model="dialogVisible" class="q-dialog-max-w-lg"
        style="background-color: rgba(red, green, blue, 0.1em);">
        <q-card class="q-dialog">
            <q-card-section class="text-center">
                <p style="color: #757575;">¿Estás seguro de que quieres eliminar la direccion?</p>
            </q-card-section>
            <q-card-actions align="center" class="q-mt-lg">
                <q-btn label="Cancelar" color="primary" class="q-mr-md" v-close-popup />
                <q-btn label="Eliminar Dirección" color="negative" @click="loggg()" class="q-ml-md" v-close-popup />
            </q-card-actions>
        </q-card>
    </q-dialog>
</template>


<!-- SCRIPT -->
<script setup>
import { ref, defineProps, onBeforeMount } from 'vue';
import { useRouter } from 'vue-router';
import { usuarioComposable } from '~/composables/usuarioComposable';


const { eliminarDireccionEnvio, eliminarDireccionFacturacion, usuarios } = usuarioComposable();

const props = defineProps({
    dialogVisible: Boolean,
    indice: Number,
    modo: String,
});

function loggg() {
    console.log(props)
    switch (props.modo) {
        case 'envio':
            eliminarDireccionEnvio(props.indice)
            break;
        case 'facturacion':
            eliminarDireccionFacturacion(props.indice)
            break;
        default:
            break;
    }
}

const quasar = useQuasar();
const dialogVisible = ref(props.dialogVisible);

const cerrarDialogo = () => {
    console.log("Cerrando el diálogo");
};


// FUNCION PARA ELIMINAR USUARIO 


</script>


<!-- STYLE -->
<style lang="scss" scoped>
.custom-dialog {
    max-width: 400px;
    margin: auto;
}
</style>