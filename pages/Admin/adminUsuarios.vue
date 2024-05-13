<template>
    <q-btn @click="regresar" flat dense icon="mdi-arrow-left" class="custom-regresar-button" />
    <div class="q-pa-md">
        <!-- :rows-per-page-options="[50]"  Esto hace que me muestre de 50 en 50 usuarios -->
        <h5 class="titulo">PANEL DE ADMINISTRACIÓN</h5>
        <q-table class="tabla" flat :rows="usuarios" row-key="index" virtual-scroll :virtual-scroll-item-size="48"
            :pagination="true" :rows-per-page-options="[50]">
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
                    <q-th key="direccionFacturacion">Direccion/es de facturación</q-th>
                </q-tr>
            </template>

            <!-- Cuerpo de la tabla -->
            <template v-slot:body="props">
                <q-tr :props="props">
                    <q-td>{{ props.row.nombre }}</q-td>
                    <q-td>{{ props.row.apellido }}</q-td>
                    <q-td>{{ props.row.email }}</q-td>


                    <q-td>
                        {{ obtenerRoles(props.row.roles) }}
                        <q-select v-model="rolSeleccionado" :options="optionesParaRoles"
                            @update:model-value="seleccionarRoles(props.row._id)"
                            style="position: absolute; right: 0; top: 0;" />
                    </q-td>


                    <q-td>{{ props.row.fechaCreacion }}</q-td>
                    <q-td>{{ obtenerFechaModificacion(props.row.fechaModificacion) }}</q-td>


                    <q-td> <!-- No especificamos alineación para estas dos celdas -->
                        <span v-html="obtenerDireccionesEnvio(props.row.direccionesEnvio)"></span>
                    </q-td>
                    <q-td> <!-- No especificamos alineación para estas dos celdas -->
                        <span v-html="obtenerDireccionesFacturacion(props.row.direcionesFacturacion)"></span>
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
    { label: 'ADMIN', value: 'ADMIN' },
    { label: 'USUARIO', value: 'USER' },
];



const rolSeleccionado = ref('');
const seleccionarRoles = async (idUsuario) => {
    try {

        // if(rol_seleccionado.value === 'ADMIN'){
        //     rol_seleccionado.value = 'USER';
        // }else if(rol_seleccionado.value === 'USER'){
        //     rol_seleccionado.value = 'ADMIN';
        // }

        const id = idUsuario._id;
        console.log("idUsuario: ", id);

        console.log('rol seleccionado ', rolSeleccionado.value.value);

        console.log('opciones para roles ', optionesParaRoles);

        const response = await actualizarRol(id, rolSeleccionado.value.value);

        console.log("RESPONSE de actualizarRol: ", response.data);

    } catch (error) {
        console.error("Error en seleccionarRol: ", error);
    }


}



// FUNCION PARA CARGAR LOS USUARIOS  ANTES DE QUE SE MONTE EL COMPONENTE
onBeforeMount(async () => {
    await listarTodosLosUsuarios();
    const usuarioId = usuarios.value._id;

    console.log("usuarioId: ", usuarioId);

})
// FUNCION PARA CARGAR LOS USUARIOS
const listarTodosLosUsuarios = async () => {
    try {
        const response = await listarUsuarios();
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











</script>



<!-- STYLE -->
<style lang="scss" scoped>
.tabla {
    height: 63vh;
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
</style>