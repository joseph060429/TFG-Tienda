<template>
  <q-dialog v-model="dialogVisible" class="q-dialog-max-w-lg" style="background-color: rgba(red, green, blue, 0.1em);">
    <q-card class="q-dialog">
      <q-card-section class="text-center">
        <!-- <q-card-title class="text-h6" style="color: #2196F3;">Confirmación</q-card-title> -->
        <p style="color: #757575;">¿Estás seguro de que quieres eliminar el usuario?</p>
      </q-card-section>
      <q-card-actions align="center" class="q-mt-lg">
        <q-btn label="Cancelar" color="primary" @click="cerrarDialogo" class="q-mr-md" v-close-popup />
        <q-btn label="Eliminar usuario" color="negative" @click="eliminarUsuarioAdmin" class="q-ml-md" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>


<!-- SCRIPT -->
<script setup>
import { ref, defineProps, onBeforeMount } from 'vue';
import { useRouter } from 'vue-router';
import { adminComposable } from '~/composables/adminComposable';


const { eliminarUsuario, usuarios } = adminComposable();

const props = defineProps({
  dialogVisible: Boolean,
  email: String
});

const quasar = useQuasar();
const dialogVisible = ref(props.dialogVisible);

const cerrarDialogo = () => {
  console.log("Cerrando el diálogo");
};


// FUNCION PARA ELIMINAR USUARIO 
const eliminarUsuarioAdmin = async (email) => {

  console.log(props.email, 'email a borrar');
  try {
    console.log("Eliminar usuario con email: ", props.email);
    const response = await eliminarUsuario(props.email);
    if (response.data === "Usuario eliminado correctamente") {
      mostrarAlertaExito('Usuario eliminado correctamente', quasar);

      eliminar(props.email);
    } else {
      mostrarAlertaError('Error al eliminar el usuario intentelo más tarde', quasar);
    }
  } catch (error) {
    console.error("Error al eliminar usuario:", error);
  }
};

// FUNCION PARA ELIMINARLO DE LA VISTA TAMBIEN
// const eliminar = (email) => {
//   let usuario = usuarios.value.email.find((obj) => (obj.email == email));
//   console.log('EMAIL A ELIMINAR de eliminar:', usuarios.value.email.indexOf((obj) => (obj.email === email)), usuario);

//   usuarios.value.email.splice(usuario.value.email.indexOf(usuario), 1);
// };




// const eliminar = (id) => {

// let producto = usuario.value.carrito.find((obj) => (obj.idCarrito == id));
// console.log('ID a eliminar:', usuario.value.carrito.indexOf((obj) => (obj.idCarrito === id)), producto);
// usuario.value.carrito.splice(usuario.value.carrito.indexOf(producto), 1);
// };



</script>


<!-- STYLE -->
<style lang="scss" scoped>
.custom-dialog {
  max-width: 400px;
  margin: auto;
}



</style>