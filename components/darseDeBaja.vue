<template>
  <q-dialog v-model="dialogVisible" class="q-dialog-max-w-lg">
    <q-card class="q-dialog">
      <q-card-section class="text-center">
        <q-card-title class="text-h6">Confirmación</q-card-title>
        <p>¿Estás seguro de que quieres darte de baja?</p>
      </q-card-section>
      <q-card-actions align="center" class="q-mt-lg">
        <q-btn label="Cancelar" color="primary" @click="cerrarDialogo" class="q-mr-md" v-close-popup />
        <q-btn label="Darte de baja" color="negative" @click="confirmarDarseDeBaja" class="q-ml-md" v-close-popup />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>


<!-- SCRIPT -->
<script setup>
import { ref, defineProps } from 'vue';
import { usuarioComposable } from '~/composables/usuarioComposable';


let authStore = useAuthStore();
const router = useRouter();
const { borrarUsuario } = usuarioComposable();

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
const confirmarDarseDeBaja = async () => {
  try {
    const response = await borrarUsuario();
    console.log("Response", response);
    // Verifico el estado de la respuesta y muestro el mensaje correspondiente
      if (response.data === "Usuario eliminado correctamente") {
        mostrarAlertaExito('Te has dado de baja exitosamente, esperamos verte pronto por aquí', quasar);
        authStore.loggedIn = false;
        borrar();
      } else if (response.data === "No estás autorizado para eliminar este usuario") {
        console.log('No estás autorizado para eliminar este usuario:', response.data);
        mostrarAlertaError('No estas autorizado para eliminar este usuario', quasar);
        authStore.loggedIn = false;
        router.push({ path: '/auth/login' });
      } else {
        mostrarAlertaError('Usuario no encontrado', quasar)
      }
  } catch (error) {
    // Error de red o algo parecido
    console.error('Error al eliminar el usuario:', error);
    mostrarAlertaError('Error al eliminar el usuario intentelo más tarde', quasar);
  }
};

// FUNCION BORRAR, PARA QUE CUANDO SE DE DEBAJA, EL USUARIO SE BORRE DE LA BASE DE DATOS Y DE LOCALSTORAGE Y LO REDIRIJA A LA VISTA DE INICIO
const borrar = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('roles');
  localStorage.removeItem('email');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('tiempoExpiracion');
  localStorage.removeItem('nombre');
  localStorage.removeItem('apellido');
  authStore.loggedIn = false;
  router.push({ path: '/' });
};


</script>

<!-- STYLE -->
<style lang="scss" scoped>



</style>