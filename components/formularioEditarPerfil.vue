<template>
    <q-dialog v-model="mostrarFormuEditarPerfil"> <q-card>
            <div class="q-pa-md d-flex justify-center align-center" style="height: 100vh;">
                <div class="d-flex justify-center align-center" style="max-width: 60%; margin: auto;">
                    <h1 class="text-h4 q-mb-md text-center q-mt-lg"
                        style="color: #333333; font-weight: bold; text-transform: uppercase; letter-spacing: 2px;">
                        Actualiza tu perfil
                    </h1>
                    <q-form @submit.prevent="manejoEnvioFormulario" @reset="borrar" class="q-gutter-md">

                        <!-- Campo nombre -->
                        <q-input filled v-model="datosActualizar.nombre" label="Nombre *" hint="Nombre" lazy-rules
                            :rules="[
                                val => val && val.length > 0 || 'Por favor, introduce algo',
                                val => /^.{2,70}$/.test(val) || 'El nombre/s debe tener entre 2 y 70 caracteres',
                                val => /^\S.*\S$/.test(val) || 'El nombre/s no puede empezar ni terminar con espacios en blanco',
                                val => !/\d/.test(val) || 'El nombre/s no puede contener números',
                                val => /^[a-zA-ZáéíóúÁÉÍÓÚ\s]+$/.test(val) || 'El nombre/s solo puede contener letras y espacios, incluyendo la tilde'
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-account" />
                            </template>
                        </q-input>

                        <!-- Campo apellido -->
                        <q-input filled v-model="datosActualizar.apellido" label="Apellidos *" hint="Apellidos"
                            lazy-rules :rules="[
                                val => val && val.length > 0 || 'Por favor, introduce algo',
                                val => /^.{2,70}$/.test(val) || 'Los apellidos debe tener entre 2 y 70 caracteres',
                                val => /^\S.*\S$/.test(val) || 'Los apellidos no pueden empezar ni terminar con espacios en blanco',
                                val => !/\d/.test(val) || 'Los apellidos no puede contener números',
                                val => /^[a-zA-ZáéíóúÁÉÍÓÚ\s]+$/.test(val) || 'Los apellidos solo puede contener letras y espacios, incluyendo la tilde'
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-account" />
                            </template>
                        </q-input>

                        <!-- Campo email -->
                        <q-input filled v-model="datosActualizar.email" label="Email *" hint="Tu correo electrónico"
                            lazy-rules :rules="[
                                val => val && val.length > 0 || 'Por favor, introduce algo',
                                val => /^\S.*\S$/.test(val) || 'El email no puede empezar ni terminar con espacios en blanco',
                                val => /^\S+@\S+\.\S+$/.test(val) || 'El formato del correo electrónico no es válido',
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-email" />
                            </template>
                        </q-input>

                        <!-- Campo password -->
                        <q-input filled v-model="datosActualizar.password"
                            :type="mostrarContrasenia ? 'text' : 'password'" label="Contraseña *" lazy-rules :rules="[
                                val => val && val.length > 0 || 'Por favor, vuelve a introducir tu contraseña, o escoge una nueva',
                                val => val.length >= 8 || 'La contraseña debe tener al menos 8 caracteres'
                            ]">
                            <template v-slot:prepend>
                                <q-icon name="mdi-lock" />
                            </template>
                            <!-- Icono de ojito -->
                            <template v-slot:append>
                                <q-icon :name="mostrarContrasenia ? 'mdi-eye' : 'mdi-eye-off'"
                                    @click="cambiarMostrarPassword" class="cursor-pointer" />
                            </template>
                        </q-input>
                        <!-- Botones de enviar y reiniciar -->
                        <div class="q-mt-lg d-flex justify-around">
                            <!-- Boton de enviar -->
                            <q-btn label="Actualizar cuenta" type="submit"
                                class="full-width enviar-button col-xs-12 col-sm-6">
                                <q-icon name="mdi-account-edit" class="q-ml-md inline"></q-icon>
                                <!-- Icono para el botón -->
                            </q-btn>

                            <!-- Boton de reiniciar -->
                            <q-btn label="Reiniciar" type="reset" flat
                                class="full-width q-ml-sm custom-button-reiniciar col-xs-12 col-sm-6">
                                <q-icon name="mdi-refresh" class="q-ml-md inline"></q-icon>
                                <!-- Icono para el botón -->
                            </q-btn>
                        </div>
                    </q-form>
                </div>
            </div>
        </q-card>
    </q-dialog>
</template>

<script setup>

// IMPORTACIONES
import { useRouter } from 'vue-router'
import { mostrarAlertaExito, mostrarAlertaError } from '~/utils/alertas';
import { reactive } from "vue";
import { usuarioComposable } from '~/composables/usuarioComposable';
import { adminComposable } from '~/composables/adminComposable';

const { actualizacionUsuario } = usuarioComposable();

const { actualizacionUsuarioAdmin, usuarios } = adminComposable();

const props = defineProps({
    formularioEditarPerfil: Boolean,
    // id: String
    id: Object
})
const mostrarFormuEditarPerfil = ref(props.formularioEditarPerfil)



let authStore = useAuthStore();

// RUTAS
const router = useRouter()

//USAR QUASAR
const quasar = useQuasar()

// DECLARACION DE VARIABLES
const datosActualizar = reactive({
    nombre: authStore.auth.nombre,
    apellido: authStore.auth.apellido,
    email: authStore.auth.email,
    password: ''
})

// const datosActualizarAdmin = reactive({
//     nombre: props.id.nombre,
//     apellido: props.id.apellido,
//     email: props.id.email,
//     password: ''
// })


onBeforeMount(async () => {
    
})


const mostrarContrasenia = ref(false);


// FUNCIONES
const roles = localStorage.getItem('roles');
const isAdmin = () => {
    // Verificar si el rol de administrador está presente en los roles del usuario
    return roles && roles.includes('ROLE_ADMIN');
};


const manejoEnvioFormulario = () => {
    if (isAdmin()) {
        actualizarDatosUsuarioAdmin();
    } else {
        actualizarDatosUsuario();
    }
};

const actualizarDatosUsuarioAdmin = async () => {

    console.log(props.id._id, 'id a actualizar');
    console.log(props.id.nombre, 'nombre usuario');
    console.log(props.id.apellido, 'apellido usuario');
    console.log(props.id.email, 'email usuario');
    // console.log(props.id.password, 'password usuario');

// Actualizo el usuario
try {
    const response = await actualizacionUsuarioAdmin(props.id._id, datosActualizarAdmin);
    console.log("Response", response);
    // Verifico el estado de la respuesta y muestro el mensaje correspondiente
    if (response.data === "El email ya esta en uso") {
        console.log('Email ya está en uso:', response.data);
        mostrarAlertaError('Prueba con otro correo electrónico', quasar);
    } else {
        // Si todo va bien actualizo el usuario y redirecciono al login
        console.log('Usuario actualizado correctamente:', response.data);
        mostrarAlertaExito('Usuario actualizado correctamente, vuelve a iniciar sesión para guardar los cambios', quasar);
        borrar();
        authStore.loggedIn = false;
        router.push({ path: '/auth/login' });
    }
} catch (error) {
    // Error de red o algo parecido
    console.error('Error al crear el usuario:', error);
    mostrarAlertaError('Error al actualizar el usuario intentelo más tarde', quasar);
}

};

// FUNCION PARA ENVIAR EL FORMULARIO
const actualizarDatosUsuario = async () => {

    // Actualizo el usuario
    try {
        const response = await actualizacionUsuario(datosActualizar);
        console.log("Response", response);
        // Verifico el estado de la respuesta y muestro el mensaje correspondiente
        if (response.data === "El email ya esta en uso") {
            console.log('Email ya está en uso:', response.data);
            mostrarAlertaError('Prueba con otro correo electrónico', quasar);
        } else {
            // Si todo va bien actualizo el usuario y redirecciono al login
            console.log('Usuario actualizado correctamente:', response.data);
            mostrarAlertaExito('Usuario actualizado correctamente, vuelve a iniciar sesión para guardar los cambios', quasar);
            borrar();
            authStore.loggedIn = false;
            router.push({ path: '/auth/login' });
        }
    } catch (error) {
        // Error de red o algo parecido
        console.error('Error al crear el usuario:', error);
        mostrarAlertaError('Error al actualizar el usuario intentelo más tarde', quasar);
    }

};

// CUANDO SE BORRA EL FORMULARIO, ELIMINO LOS VALORES DEL EMAIL Y LA CONTRASEÑA.
const borrar = () => {
    datosActualizar.nombre = ''
    datosActualizar.apellido = ''
    datosActualizar.email = ''
    datosActualizar.password = ''
};

// CUANDO SE HACE CLIC EN EL BOTÓN PARA MOSTRAR U OCULTAR LA CONTRASEÑA(OJITO), CAMBIO EL ESTADO PARA MOSTRARLA U OCULTARLA.
const cambiarMostrarPassword = () => {
    mostrarContrasenia.value = !mostrarContrasenia.value;
};

</script>


<!--  ESTILOS -->
<style lang="scss" scoped>
.full-width {
    /* Ajusto el ancho  */
    width: calc(50% - 10px);
}
</style>