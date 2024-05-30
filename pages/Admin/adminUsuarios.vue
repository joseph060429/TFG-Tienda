<template>
    <EliminarUsuario v-model="mostrarEliminarUsuario" :email="emailUsuarioAeliminar" />
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <div class="q-pa-md">
        <!-- :rows-per-page-options="[50]"  Esto hace que me muestre de 50 en 50 usuarios -->
        <h5 class="titulo">PANEL DE ADMINISTRACIÓN</h5>
        <q-table class="tabla" flat :rows="usuarios" row-key="index" virtual-scroll :virtual-scroll-item-size="48"
            :pagination="{ rowsPerPage: 50 }" :rows-per-page-options="[50]">
            <!-- Cabeceras de la tabla -->
            <template v-slot:header="props">
                <q-tr :props="props">
                    <q-th key="nombre">Nombre</q-th>
                    <q-th key="apellido">Apellido</q-th>
                    <q-th key="email">Email</q-th>
                    <q-th key="roles">Rol</q-th>
                    <q-th key="fechaCreacion">Fecha de Creación</q-th>
                    <q-th key="fechaModificacion">Fecha de Modificación</q-th>
                    <q-th key="direccionEnvio">Direccion/es de envío</q-th>
                    <q-th key="direccionEnvio">Acciones</q-th>
                    <!-- <q-th key="direccionFacturacion">Direccion/es de facturación</q-th> -->
                </q-tr>
            </template>

            <!-- Cuerpo de la tabla -->
            <template v-slot:body="props">
                <q-tr :props="props">
                    <q-td>{{ props.row.nombre }}</q-td>
                    <q-td>{{ props.row.apellido }}</q-td>
                    <q-td>{{ props.row.email }}</q-td>


                    <q-td>
                        <q-select class="select-rol" v-model="props.row.roles[0].name" :options="optionesParaRoles"
                            @update:model-value="seleccionarRoles(props.row)" />
                    </q-td>


                    <q-td>{{ props.row.fechaCreacion }}</q-td>
                    <q-td>{{ obtenerFechaModificacion(props.row.fechaModificacion) }}</q-td>


                    <q-td>
                        <span v-html="obtenerDireccionesEnvio(props.row.direccionesEnvio)" style="text-align: left;" ></span>
                    </q-td>


                    <q-td class="text-center">
                        <!-- Botón para eliminar el perfil del usuario-->
                        <q-btn @click="eliminarUsuarioAdmin(props.row.email)" class="boton-borrar">
                            <q-icon name="mdi-delete" />
                        </q-btn>
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

// FUNCION PARA CARGAR LOS USUARIOS  ANTES DE QUE SE MONTE EL COMPONENTE
onBeforeMount(async () => {
    await listarTodosLosUsuarios();
})

// El usuario es el de las stores
const { listarUsuarios, usuarios, actualizarRol } = adminComposable();

//USAR QUASAR
const quasar = useQuasar()

const router = useRouter()


const regresar = () => {
    router.push({ path: '/admin/vistaInicioAdmin' })
};

// FUNCIONES
const optionesParaRoles = [
    'ADMIN',
    'USER'
];

// FUNCION PARA ACTUALIZAR EL ROL DE UN USUARIO
const seleccionarRoles = async (idUsuario) => {


    console.log("idUsuario: ", idUsuario);

    try {
        const response = await actualizarRol(idUsuario._id, idUsuario.roles[0].name);
        console.log("RESPONSE de actualizarRol: ", response.data);

        if (response.data === 'Rol actualizado correctamente') {
            mostrarAlertaExito('Rol actualizado exitosamente', quasar)
        }
        if (response.data === 'El usuario ya tiene ese rol') {
            mostrarAlertaError('El usuario ya tiene ese rol', quasar)
        }

    } catch (error) {
        // Error de red u otro error
        console.error("Error al actualizar rol: ", error);
    }
}

const actualizarVentana = () => {
    window.location.reload();
};


// FUNCION PARA CARGAR LOS USUARIOS
const listarTodosLosUsuarios = async () => {
    try {
        const response = await listarUsuarios();
        adminComposable().usuarios.value = response.data;
        console.log("RESPONSE de listarUsuarios: ", response.data);
    } catch (error) {
        // Error de red u otro error
        console.error('Error al ver todos los usuarios', error);
        mostrarAlertaError('Error al ver todos los usuarios', quasar);
    }
}

// FUNCION PARA OBTENER LOS ROLES DEL USUARIO
const obtenerRoles = (roles) => {
    // Verifico si el usuario tiene roles y si la lista de roles no está vacía
    if (roles && roles.length > 0) {
        // Mapeo los roles para obtener solo los nombres y los uno en una cadena separada por comas
        return roles.map(role => role.name).join(', ');
    } else {
        // Si el usuario no tiene roles, devuelvo un mensaje predeterminado
        return 'Sin roles';
    }
};

// FUNCION PARA OBTENER LA FECHA DE MODIFICACION 
const obtenerFechaModificacion = (fechaModificacion) => {
    // Verifico si hay una fecha de modificación proporcionada
    if (fechaModificacion) {
        // Devuelvo la fecha de modificación tal como está
        return fechaModificacion
    } else {
        // Si no hay fecha de modificación, devuelvo un mensaje predeterminado
        return 'Sin modificaciones';
    }
};

// FUNCION PARA OBTENER LAS DIRECCIONES DE ENVIO formateada
const obtenerDireccionesEnvio = (direccionesEnvio) => {
    // Verifico si hay direcciones de envío y si la lista no está vacía
    if (direccionesEnvio && direccionesEnvio.length > 0) {
        // Consturyo una cadena con las direcciones de envío
        return direccionesEnvio.map(direccion => `• ${direccion}`).join('<br>'); // Agrego un punto delante de cada dirección y une las direcciones con saltos de línea
    } else {
        // Si no hay direcciones de envío, devuelvo un mensaje predeterminado
        return 'Sin comprar';
    }
};

// FUNCION PARA OBTENER LAS DIRECCIONES DE FACTURACION FORMATEADA
const obtenerDireccionesFacturacion = (direcionesFacturacion) => {
    // Verifico si hay direcciones de facturación y si la lista no está vacía
    if (direcionesFacturacion && direcionesFacturacion.length > 0) {
        // Consturyo una cadena con las direcciones de envío
        return direcionesFacturacion.map(direccion => `• ${direccion}`).join('<br>'); // Agrego un punto delante de cada dirección y une las direcciones con saltos de línea
    } else {
        // Si no hay direcciones de facturación, devuelvo un mensaje predeterminado
        return 'Sin comprar';
    }
}


const emailUsuarioAeliminar = ref(null);
const mostrarEliminarUsuario = ref(false);



const eliminarUsuarioAdmin = (email) => {
    // Abro el dialogo si no esta abierto
    if (!mostrarEliminarUsuario.value) {
        mostrarEliminarUsuario.value = true;
    }
    // Guardo el email del usuario que quiero eliminar
    emailUsuarioAeliminar.value = email;
    console.log("email del usuario a eliminar", emailUsuarioAeliminar.value)
};

const mostrarFormularioEditarPerfil = ref(false);
const objetoUsuarioActualizar = ref(null);
const editarPerfil = (id) => {
    // Abro el formulario si no esta abierto
    if (!mostrarFormularioEditarPerfil.value) {
        mostrarFormularioEditarPerfil.value = true;
    }
    objetoUsuarioActualizar.value = id;
    console.log("Objeto usuario a editar", objetoUsuarioActualizar)
};











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



.select-rol {
    right: 0;
    top: 0%;
    border: none;
    width: 100%;
    font-size: 1em;
    text-align: center;
}

@media (max-width: 600px) {
    .select-rol {
        right: 0;
        top: 0%;
        border: none;
        width: 100%;
        font-size: 1em;
        text-align: center;
    }
}
</style>
