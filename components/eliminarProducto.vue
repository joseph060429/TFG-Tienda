<template>
  <q-dialog v-model="dialogVisible" class="q-dialog-max-w-lg">
    <q-card class="q-dialog">
      <q-card-section class="text-center">
        <q-card-title class="text-h6" style="color: #2196F3;">Confirmación</q-card-title>
        <p style="color: #757575;">¿Estás seguro de que quieres eliminar el producto?</p>
      </q-card-section>
      <q-card-actions align="center" class="q-mt-lg">
        <q-btn label="Cancelar" color="primary" @click="cerrarDialogo" class="q-mr-md" style="color: #fff; background-color: #2196F3;" v-close-popup />
        <q-btn label="Eliminar Producto" color="negative" @click="confirmarEliminarProducto" class="q-ml-md" style="color: #fff; background-color: #f44336;" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>



<!-- SCRIPT -->
<script setup>
import { ref, defineProps } from 'vue';
import { productoAdminComposable } from '~/composables/productoAdminComposable';


const router = useRouter();
const { eliminarProducto, productoAdmin, limpiarProducto } = productoAdminComposable();

//USAR QUASAR
const quasar = useQuasar()

const props = defineProps({
  dialogVisible: Boolean
});

const dialogVisible = ref(props.dialogVisible);

console.log("dialogVisible: " + dialogVisible.value);

const cerrarDialogo = () => {
  console.log("Cerrando el diálogo");
};

// FUNCIONES
// FUNCION PARA ELIMINAR USUARIO 
const confirmarEliminarProducto = async () => {
  const _id = productoAdmin.value._id;
  try {
    const response = await eliminarProducto(_id);
    console.log("Response", response);
    if (response.data === "Producto eliminado correctamente") {
      // Muestro la alerta de éxito después de un breve retraso
      setTimeout(() => {
        mostrarAlertaExito('Producto eliminado correctamente', quasar);
        router.push({ path: '/admin/vistaInicioAdmin' });
        limpiarProducto();
      }, 100); // Espero 300 milisegundos antes de mostrar la alerta de éxito
    } else if (response.data === "El producto no existe o no se pudo eliminar") {
      console.log('El producto no existe o no se pudo eliminar', response.data);
      mostrarAlertaError('El producto no existe o no se pudo eliminar', quasar);
    }
  } catch (error) {
    console.error('Error al eliminar el producto:', error);
    mostrarAlertaError('Error al eliminar el producto intentelo más tarde', quasar);
  }
};




</script>

<!-- STYLE -->
<style lang="scss" scoped></style>