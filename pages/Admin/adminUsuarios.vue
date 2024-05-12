<template>
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
                    <q-td>{{ obtenerRoles(props.row.roles) }}</q-td>
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
const { listarUsuarios, usuarios } = adminComposable();

//USAR QUASAR
const quasar = useQuasar()

const router = useRouter()

// FUNCIONES

// FUNCION PARA CARGAR LOS PRODUCTOS  ANTES DE QUE SE MONTE EL COMPONENTE
onBeforeMount(async () => {
    await listarTodosLosUsuarios();
    console.log("Usuarios cargados", usuarios);
})
// FUNCION PARA CARGAR LOS PRODUCTOS
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

const obtenerRoles = (roles) => {
    if (roles && roles.length > 0) {

        return roles.map(role => role.name).join(', ');
    } else {
        return 'Sin roles';
    }
};

const obtenerFechaModificacion = (fechaModificacion) => {
    if (fechaModificacion) {

        return fechaModificacion
    } else {
        return 'Sin modificaciones';
    }
};
const obtenerDireccionesEnvio = (direccionesEnvio) => {
    if (direccionesEnvio && direccionesEnvio.length > 0) {
        // Construir una cadena con las direcciones de envío
        return direccionesEnvio.map(direccion => `• ${direccion}`).join('<br>'); // Agrega un punto delante de cada dirección y une las direcciones con saltos de línea
    } else {
        return 'Sin comprar'; // Devuelve este mensaje si no hay direcciones de envío
    }
};

const obtenerDireccionesFacturacion = (direcionesFacturacion) => {
    if (direcionesFacturacion && direcionesFacturacion.length > 0) {
        // Construir una cadena con las direcciones de envío
        return direcionesFacturacion.map(direccion => `• ${direccion}`).join('<br>'); // Agrega un punto delante de cada dirección y une las direcciones con saltos de línea
    } else {
        return 'Sin comprar'; // Devuelve este mensaje si no hay direcciones de envío
    }
};
</script>



<!-- STYLE -->
<style lang="scss" scoped>
.tabla {
    height: 63vh;
    width: 100%;
    background-color: #A9A9A9;
    font-family: Arial, sans-serif;
    border: 3px solid black;
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
</style>